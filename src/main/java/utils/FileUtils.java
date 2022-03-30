package utils;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class FileUtils {

    @Builder
    @Getter
    static class FileParameters{
        private final String directory;
        @Builder.Default
        private final String nameContains = "";
        @Builder.Default
        private final String extension = "";
        @Builder.Default
        private final String fieldSeparator = ",";
    }

    @Getter
    private Integer count;

    @Getter
    private Set<String> fileNames;

    /**
     * Provide a directory location and (optionally) file name patterns and/or extensions to be uploaded
     *
     * If a non-null FileParameter object is passed, the method will return either an empty or non-empty distinct
     * set of file names and a count of those filenames for convenience
     *
     * @param fileParameters directory is mandatory, nameContains is an optional way of specifying a pattern in the file name
     *                       (like a distinct prefix), extension is an optional way of specifying a file extension (if no
     *                       period is provided, one will be added)
     * @throws IllegalArgumentException is thrown if the directory is null, empty or invalid
     */
    FileUtils(@NonNull final FileParameters fileParameters) throws IllegalArgumentException {
        final String directory = validDirectory(fileParameters.getDirectory());
        final String nameContains = validNameContains(fileParameters.getNameContains());
        final String extension = validExtension(fileParameters.getExtension());
        final String fieldSeparator = validFieldSeparator(fileParameters.getFieldSeparator());

        fileNames = buildList(directory, nameContains, extension);
        count = fileNames.size();

        assert count >= 0;
        assert count == fileNames.size();
    }

    private String validDirectory(final String directory) throws IllegalArgumentException {
        if (directory == null || directory.isBlank() || !Files.exists(Path.of(directory)) || !Files.isDirectory(Path.of(directory))) {
            String message = String.format("The directory [%s] has not been found", directory);
            throw new IllegalArgumentException(message);
        } else {
            return directory;
        }
    }

    private String validNameContains(final String nameContains) {
        if (nameContains == null) {
            return "";
        } else {
            return nameContains;
        }
    }

    private String validExtension(final String extension) {
        if (extension == null) {
            return "";
        } else if (!extension.startsWith(".")) {
            return "." + extension;
        } else {
            return extension;
        }
    }

    private String validFieldSeparator(final String fieldSeparator) {
        if (fieldSeparator == null) {
            return ",";
        } else {
            return fieldSeparator;
        }
    }

    private Set<String> buildList(final String directory, final String prefix, final String extension) throws IllegalArgumentException {
        try (Stream<Path> stream = Files.walk(Paths.get(directory), 1)) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .filter(file -> file.getFileName().toString().contains(prefix))
                    .filter(file -> file.getFileName().toString().contains(extension))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            String message = String.format("The directory [%s] threw an IOException", directory);
            throw new IllegalArgumentException(message);
        }
    }
}
