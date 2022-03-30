package utils;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * Sanitize First Names
 *
 * First names can be a candidate for :
 *  redaction: just replace the existing characters with A-Za-z either fixed or randomized
 *  perturbation: replace part of the name or reverse the name
 *  substitution: replace a valid first name with another first name. There can also be an option
 *      to maintain gender and/or choose a name with a similar frequency. Its even possible to make the
 *      frequency bounded by time (replace the 7th most popular female name in 1976 with the 8th most popular name).
 *      There will always be a weakness in this dataset since it only uses baby names registered in the US Social
 *      Security Administration. Still, this can be a strong solution for a synthetic dataset
 *
 *  The following process is used to create a synthetic dataset using substitution. The algorithm is such that
 *  it can provide a high probability of reversability without storing the original and the replacement in a dataset
 *  assuming you know some key variable in the substitution
 *
 *  1) read in the fields from the US SSA. They are comma delimited files created by year with first name, gender initial (F, M)
 *  and the count of babies registered with that name that year.
 */
@Slf4j
public class ProcessFirstNames {

    @Value
    @Builder
    static class key_column {
        String name;
        String gender;
    }

    @Value
    @Builder
    static class value_column {
        Integer count;
        Integer frequency;
    }

    // Constants to be used as defaults in case there is some issue reading application.yaml
    private static final String FIRSTNAME_FILE_PREFIX = "yob";
    private static final String FIRSTNAME_FILE_SUFFIX = ".txt";
    private static final String FIRSTNAME_FILE_LOCATION = "/src/main/resources";
    private static final String FIRSTNAME_FIELD_SEPARATOR = ",";
    private static final Integer FIRSTNAME_START_YEAR = 1920;
    private static final Integer FIRSTNAME_END_YEAR = 2020;
    private static final Integer FIRSTNAME_INCREMENT = 10;

    private FileUtils.FileParameters fileParameters;

    /*
    A note on threadsafe collections
    Synchronized collections use intrinsic locking to achieve thread safety, so the entire collection is locked.
    Concurrent collections divide data into segments to achieve thread safety, which allows for concurrent access.
    Synchronized collections are less performant that concurrent collections since only one thread can access the
    collection at a time. However, we only want one thread to process a file request at a time while it would actually
    be desirable for multiple threads to access the HashMaps
     */

    private Set<String> requestedFiles = Collections.synchronizedSet(new HashSet<>());

    //private ConcurrentHashMap<String, value_columns> names = new ConcurrentHashMap<String, value_columns>();
    private final ConcurrentHashMap<key_column, value_column> names = new ConcurrentHashMap<key_column, value_column>();
    private final ConcurrentHashMap<key_column, value_column> weighted_male_names = new ConcurrentHashMap<key_column, value_column>();
    private final ConcurrentHashMap<key_column, value_column> weighted_female_names = new ConcurrentHashMap<key_column, value_column>();


    private final Integer upper = 0;
    private final Integer duplicateUpper = 0;

    private final Integer totalNumberOfFiles = 0;
    private Integer maleWeight = 0;
    private Integer femaleWeight = 0;

    @Getter
    @Builder(builderClassName = "Builder", buildMethodName = "build")
    static class RequestParameters {

        private final Integer startYear;

        private final Integer endYear;

        private final Integer increment;

        static class Builder {
            private Integer startYearDefault;
            private Integer endYearDefault;
            private Integer incrementDefault;

            RequestParameters build() {
                readDefaultValues();

                if (startYear == null || startYear < startYearDefault || startYear > endYearDefault || startYear > endYear) {
                    log.info("Start year was not provided or is invalid so the default value will be used.");
                    startYear = startYearDefault;
                }

                if (endYear == null || endYear > endYearDefault || endYear < startYearDefault || endYear < startYear) {
                    log.info("End year was not provided or is invalid so the default will be used.");
                    endYear = endYearDefault;
                }

                if (increment == null || increment < 1 || increment > (endYear - startYear)) {
                    log.info("Increment was not provided or is invalid so the default will be used.");
                    increment = incrementDefault;
                }

                return new RequestParameters(startYear, endYear, increment);
            }

            private void readDefaultValues() {
                YAMLConfiguration yamlConfiguration = new YAMLConfiguration();
                URL url =  getClass().getClassLoader().getResource("application.yaml");
                try (InputStream inputStream = url.openStream()) {
                    yamlConfiguration.read(inputStream);
                    startYearDefault = yamlConfiguration.get(Integer.class, "substitution.firstNameStart", FIRSTNAME_START_YEAR);
                    endYearDefault = yamlConfiguration.get(Integer.class, "substitution.firstNameEnd", FIRSTNAME_END_YEAR);
                    incrementDefault = yamlConfiguration.get(Integer.class, "substitution.firstNameIncrement", FIRSTNAME_INCREMENT);
                } catch (ConfigurationException | IOException e) {
                    log.info("Unable to read the configuration file for substitution values. Using hardcoded defaults.");
                }
            }
        }
    }


    public ProcessFirstNames() throws IllegalArgumentException {
        RequestParameters requestParameters = RequestParameters.builder()
                .startYear(FIRSTNAME_START_YEAR)
                .endYear(FIRSTNAME_END_YEAR)
                .increment(FIRSTNAME_INCREMENT)
                .build();
        new ProcessFirstNames(requestParameters);
    }

    public ProcessFirstNames(@NotNull final RequestParameters requestParameters) throws IllegalArgumentException {
        if (Objects.equals(requestParameters, null)) {
            String error = "RequestParameters must not be null";
            log.warn(error);
            throw new IllegalArgumentException(error);
        }

        // get the parameters that will be used to retrieve data files
        this.fileParameters = populateFileParameters();
        FileUtils fileUtils = new FileUtils(this.fileParameters);

        // validate the requested file list actually exists in the target file system
        AbstractMap.SimpleImmutableEntry<Boolean, String> validation = validateRequest(requestParameters, fileUtils);
        if (!validation.getKey()) {
            String error = validation.getValue();
            log.warn(error);
            throw new IllegalArgumentException(error);
        }

        // process the requested files
    }

    /**
     * Ensure the file names requested in the requestParameters actually exist
     *
     * @param requestParameters is used to build up the specific file names that would be requested
     *                          based on the data's years (ex 1980-2000 incrementing every 2 years)
     * @param fileUtils uses the values in the application.yaml to identify the location and file patterns
     *                  that make up the first names file set
     * @return an abstract map with boolean and a string. If there was an error (or more than one) the boolean is false
     * and the string is populated with error messages(s). Otherwise, the result is true and the messages is empty. The
     * calling function can decide if this is an exception or an error
     */
    private AbstractMap.SimpleImmutableEntry<Boolean, String> validateRequest(final RequestParameters requestParameters, final FileUtils fileUtils) {
        boolean result = true;
        StringBuilder messages = new StringBuilder();

        // extract requestParameters and fileUtils for logic checks
        Integer startYear = requestParameters.getStartYear();
        Integer endYear = requestParameters.getEndYear();
        Integer increment = requestParameters.getIncrement();
        Integer requestFileCount = (startYear - endYear) / increment;
        Integer availableFileCount = fileUtils.getCount();

        // if more files have been requested than are available, log the error but continue
        if (availableFileCount < requestFileCount) {
            result = false;
            String error = String.format("You have requested %d files but only %d are available for use", requestFileCount, availableFileCount);
            log.warn(error);
            messages.append(error);
        }

        // Build the file names that are requested and compare the result to the actual available files
        Set<String> availableFiles = fileUtils.getFileNames();
        Set<String> requestedFiles = new HashSet<>();
        for (int i = 0; i <= requestFileCount; i++) {
            requestedFiles.add(buildFileName(startYear, i * increment));
        }

        // Log an error if there are any files that have been requested that are not in the available list
        if (!availableFiles.containsAll(requestedFiles)) {
            result = false;

            Set<String> invalidFiles = requestedFiles.stream()
                    .filter(f -> !availableFiles.contains(f))
                    .collect(Collectors.toSet());

            StringBuffer sb = new StringBuffer();
            sb.append("The following files were requested but do not exist in the target directory :");
            invalidFiles.forEach(f -> sb.append(f).append(" "));
            String error = sb.toString();
            log.warn(error);
            messages.append(error);
        } else {
            this.requestedFiles = requestedFiles;
        }

        return new AbstractMap.SimpleImmutableEntry(result, messages.toString);
    }

    /**
     * Get information about the directory and files containing first name information
     *
     * @return FileParameters object with values for file location, prefix and suffix extracted from
     *          yaml file or from class-level defaults
     */
    private FileUtils.FileParameters populateFileParameters() {
        //TODO extenralize this
        YAMLConfiguration yamlConfiguration = new YAMLConfiguration();
        URL url =  getClass().getClassLoader().getResource("application.yaml");

        try (InputStream inputStream = Objects.requireNonNull(url).openStream()) {
            yamlConfiguration.read(inputStream);
            log.info("Populating fileparameters object with values stored in yaml");
            return FileUtils.FileParameters.builder()
                    .extension(yamlConfiguration.get(String.class, "substitution.firstNameFileSuffix"))
                    .nameContains(yamlConfiguration.get(String.class, "substitution.firstNameFilePrefix"))
                    .directory(yamlConfiguration.get(String.class, "substitution.firstNameFileDirectory"))
                    .fieldSeparator(yamlConfiguration.get(String.class, "substitution.firstNameFieldSeparator"))
                    .build();
        } catch (ConfigurationException | IOException e) {
            log.warn("Unable to read the configuration file for first name file parameters values. Using hardcoded defaults.");
            return FileUtils.FileParameters.builder()
                    .extension(FIRSTNAME_FILE_SUFFIX)
                    .nameContains(FIRSTNAME_FILE_PREFIX)
                    .directory(FIRSTNAME_FILE_LOCATION)
                    .directory(FIRSTNAME_FIELD_SEPARATOR)
                    .build();
        }
    }

    /**
     *
     * @param fileName
     */
    private void readFile(final String fileName) {

        try (BufferedReader br = Files.newBufferedReader(Paths.get(ClassLoader.getSystemResource(fileName).toURI()))) {
            String line;
            while ((line = br.readLine()) != null) {

                String[] columns = line.split(FIRSTNAME_FIELD_SEPARATOR);
                key_column keyColumn = buildKeyColumn(fileName, columns);

                int count = Integer.parseInt(columns[2]);
                //names.compute(name, (key, value) -> value == null ?
                //        value_column.builder().count(count).frequency(1).build() :
                //        value_column.builder().count(value.getCount() + count).frequency(value.getFrequency() + 1).build());

                names.compute(keyColumn, (key, value) -> {
                    if (value != null ) {
                        return value_column.builder().count(value.getCount() + count).frequency(value.getFrequency() + 1).build();
                    } else {
                        return value_column.builder().count(count).frequency(1).build();
                    }
                });
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private key_column buildKeyColumn(final String fileName, final String[] columns) {
        return key_column.builder()
                //.year(fileName.replaceAll(FIRSTNAME_FILE_PREFIX, "").replaceAll(FIRSTNAME_FILE_SUFFIX, ""))
                .name(columns[0])
                .gender(columns[1])
                .build();
    }

    /**
     * Read US SSA baby name files
     */
    private void read(final RequestParameters requestParameters) {
        LocalDate a = LocalDate.now();
        /*
        readFiles(FileUtils.FileParameters.builder()
                .directory(firstNameFileParameters.directory)
                .nameContains(firstNameFileParameters.nameContains)
                .extension(firstNameFileParameters.extension)
                .build());
*/

        System.out.println(names.size());  // 68165
        /*
        names.forEach((key, value) -> {
            if (value.getCount() < THRESHOLD) {
                names.remove(key);
            }
        });
        System.out.println(names.size()); //12492
        //createDataSet();
        */
        calculateGenderedWeights();
        System.out.println(femaleWeight); //17699977
        System.out.println(maleWeight);   //18527741
    }

    private void calculateGenderedWeights() {
        names.forEach((key, value) -> {
            if (key.getGender().contains("F")) {
                this.femaleWeight = this.femaleWeight + value.getCount();
            } else {
                this.maleWeight = this.maleWeight + value.getCount();
            }
        });
    }

    private void createDataset() {

    }


    private void readFiles(final FileUtils.FileParameters fileParameters)  {
        int j = 10;

        if (j < 1) {
            log.error("The number of files to be read must be 1 or more. You passed " + j + ". No files will be read.");
            j = 0;
        }

        if (j > totalNumberOfFiles) {
            log.error("The number of files to be read must less than the total number of files:  " + totalNumberOfFiles + ". All files will be read.");
            j = totalNumberOfFiles;
        }

        for (int i = 0; i < j; i++) {
            readFile(buildFileName(i * INCREMENT_YEAR));
        }
    }

    /**
     * Get a valid US SSA year of birth file
     *
     * Pass in a distinct integer that represents a number between the first year of the
     * dataset and the last year. (ex if the start year is 1920 and you want to create a file
     * name that is 10 years later, pass in a 10 and you will create yob2030.yxy.
     *
     * @param startYear the file name is made distinct by yyyy, so provide a single base year for the increment
     * @param increment the file name is made distinct by yyyy
     * @return file name as yob + yyyy+ .txt
     */
    private String buildFileName(final Integer startYear, final Integer increment) {
        StringBuilder sb = new StringBuilder();
        sb.append(FIRSTNAME_FILE_PREFIX);
        sb.append(startYear + increment);
        sb.append(FIRSTNAME_FILE_SUFFIX);
        return sb.toString();
    }



    /*
            String[] files = new String[11];
        files[0] = "yob1920.txt";
        files[1] = "yob1930.txt";
        files[2] = "yob1940.txt";
        files[3] = "yob1950.txt";
        files[4] = "yob1960.txt";
        files[5] = "yob1970.txt";
        files[6] = "yob1980.txt";
        files[7] = "yob1990.txt";
        files[8] = "yob2000.txt";
        files[9] = "yob2010.txt";
        files[10] = "yob2020.txt";

        //TODO if I just get rid of the gender marker and use an approximation, I can use an immutable map
        readFile(files[0]);
        readFile(files[1]);
        readFile(files[2]);
        readFile(files[3]);
        readFile(files[4]);
        readFile(files[5]);
        readFile(files[6]);
        readFile(files[7]);
        readFile(files[8]);
        readFile(files[9]);
        readFile(files[10]);

        for (int i = 0; i < 11; i++) {
            readFile(files[i]);
        }

     */

        /*
    public void read() {
        names.clear();

        readFiles();

        ConcurrentHashMap<String, Integer> results = new ConcurrentHashMap<String, Integer>();

        names.forEach((key, value) -> results.put(key, value.getCount() / value.getFrequency()));

        names.forEach((key, value) -> System.out.println(key + " - " + value.getCount() + " " + value.getFrequency()));
        //results.forEach((key, value) -> System.out.println(key + " " + value));

        Map<Integer, Integer> duplicates = new HashMap<Integer, Integer>();

        results.forEach((key, value) -> {
            if (value > upper){
                upper = value;
            }

            if (duplicates.containsKey(value)) {
                int increment = duplicates.get(value) + 1;
                duplicates.put(value, increment);
                if (increment > duplicateUpper){
                    duplicateUpper = increment;
                }
            } else {
                duplicates.put(value, 1);
            }
        });
        //System.out.println("Upper " + upper); //23059
        //System.out.println("Duplicate Upper " + duplicateUpper); //11316
        //duplicates.forEach((key, value) -> System.out.println(key + " - " + value));


        results.forEach((key, value) -> {
            if (value > duplicateUpper) {
                duplicateUpper = value;
            }
        });
    }
*/
}
