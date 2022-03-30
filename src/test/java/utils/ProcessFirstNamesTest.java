package utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProcessFirstNamesTest {

    @Test
    public void testCountFiles() {
        Set<String> fileList = new HashSet<>();
        String dir = "src/main/resources/";
        Set files = listFilesUsingFilesList(dir);
        System.out.println(files.size());
        files.forEach(file -> System.out.println(file));


    }

    private Set listFilesUsingFilesList(String dir)  {
        try (Stream<Path> stream = Files.walk(Paths.get(dir), 1)) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .filter(file -> file.getFileName().toString().contains("yob"))
                    .filter(file -> file.getFileName().toString().contains(".txt"))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            System.out.println("Didn't work");
        }
        return null;
    }

    @Test
    public void testReqestParametersClass() {

        ProcessFirstNames processFirstNames = new ProcessFirstNames(ProcessFirstNames.RequestParameters.builder().build());


        //ProcessFirstNames.builder().startYear(1920).endYear(2020).increment(1);

    }

    //@Test
    public void testRead() throws URISyntaxException {
        //URL url = ClassLoader.getResource("src/main/resources/yob1960.txt");
        //System.out.println(url.toString());
        //System.out.println(Paths.get(url.toURI()));
        //System.out.println(Paths.get(ClassLoader.getSystemResource("yob1920.txt").toURI()));
    }

    //@Test
    public void testParallel() {
        // this idea is to read all 11 files in parallel and populate a map object
        // if the key already exists (name), then add to the value (count)
        Map<String, Integer> names = new HashMap<String, Integer>();
        names.put("a", 1);
        names.put("b", 1);
        names.put("c", 1);
        names.put("d", 1);
        names.put("a", 1);
        System.out.println(names.get("a"));

        ConcurrentHashMap<String, Integer> names2 = new ConcurrentHashMap<String, Integer>();
        names2.compute("a", (key, value) -> value == null ? 1 : value + 10);
        names2.compute("b", (key, value) -> value == null ? 2 : value + 10);
        names2.compute("c", (key, value) -> value == null ? 3 : value + 10);
        names2.compute("a", (key, value) -> value == null ? 1 : value + 10);
        System.out.println(names2.get("a"));
        System.out.println(names2.get("b"));
        System.out.println(names2.get("c"));


        ConcurrentHashMap<String, Integer> names3 = new ConcurrentHashMap<String, Integer>();
        names3.computeIfPresent("a", (key, value) -> value + 10);
        names3.computeIfPresent("b", (key, value) -> value + 20);
        names3.computeIfPresent("c", (key, value) -> value + 30);
        names3.computeIfPresent("a", (key, value) -> value + 10);
        System.out.println(names3.get("a"));
        System.out.println(names3.get("b"));
        System.out.println(names3.get("c"));


    }
}
