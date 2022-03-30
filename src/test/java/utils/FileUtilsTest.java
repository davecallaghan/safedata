package utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FileUtilsTest {

    @Test
    public void givenNull_whenFileUtilsIsCreated_thenIllegalArgumentExceptionIsThrownWithBadDirectoryPathInMessage() {
        String directory = "abc/main/resources/";

        NullPointerException thrown = Assertions.assertThrows(NullPointerException.class, () -> {
            FileUtils fileUtils = new FileUtils(null);
        });
    }

    @Test
    public void givenDirectoryPathIsValidAndPopulated_whenFileUtilsIsCreated_thenSetOfFileNamesIsNotEmptyAndEqualToCount() {
        String directory = "src/main/resources/";
        FileUtils fileUtils = new FileUtils(FileUtils.FileParameters.builder().directory(directory).build());

        assertFalse(fileUtils.getFileNames().isEmpty());
        assertEquals(fileUtils.getFileNames().size(), fileUtils.getCount());
    }

    @Test
    public void givenDirectoryPathIsInvalid_whenFileUtilsIsCreated_thenIllegalArgumentExceptionIsThrownWithBadDirectoryPathInMessage() {
        String directory = "abc/main/resources/";

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            FileUtils fileUtils = new FileUtils(FileUtils.FileParameters.builder().directory(directory).build());
        });

        assertTrue(thrown.getMessage().contains(directory));
    }

    @Test
    public void givenDirectoryPathIsEmpty_whenFileUtilsIsCreated_thenIllegalArgumentExceptionIsThrownWithBadDirectoryPathInMessage() {
        String directory = "     ";

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            FileUtils fileUtils = new FileUtils(FileUtils.FileParameters.builder().directory(directory).build());
        });

        assertTrue(thrown.getMessage().contains(directory));
    }

    @Test
    public void givenDirectoryPathParameterIsNull_whenFileUtilsIsCreated_thenIllegalArgumentExceptionIsThrownWithNullInMessage() {
        String directory = null;

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            FileUtils fileUtils = new FileUtils(FileUtils.FileParameters.builder().directory(directory).build());
        });

        assertTrue(thrown.getMessage().contains("null"));
    }


    @Test
    public void testDirectoryParamNull() {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            FileUtils fileUtils = new FileUtils(FileUtils.FileParameters.builder().directory(null).build());
        });

       assertEquals("The directory [null] has not been found", thrown.getMessage());
    }

    @Test
    public void givenDirectoryPathIsValidAndNameContainsIsValid_whenFileUtilsIsCreated_thenSetOfFileNamesIsNotEmptyAndOnlyHasItemsThatMatchNameContains() {
        String directory = "src/main/resources/";
        String nameContains = "yob";

        FileUtils fileUtils = new FileUtils(FileUtils.FileParameters.builder().directory(directory).nameContains(nameContains).build());

        assertFalse(fileUtils.getFileNames().isEmpty());
        assertTrue(fileUtils.getFileNames().stream().allMatch(r -> r.contains(nameContains)));
        assertTrue(fileUtils.getCount() >= 1);
    }

    @Test
    public void givenDirectoryPathIsValidAndNameContainsIsInvalid_whenFileUtilsIsCreated_thenSetOfFileNamesIsEmpty() {
        String directory = "src/main/resources/";
        String nameContains = "xyz";

        FileUtils fileUtils = new FileUtils(FileUtils.FileParameters.builder().directory(directory).nameContains(nameContains).build());

        assertTrue(fileUtils.getFileNames().isEmpty());
        assertTrue(fileUtils.getCount() == 0);
    }

    @Test
    public void givenDirectoryPathIsValidAndNameContainsIsNull_whenFileUtilsIsCreated_thenNameContaimsIsIgnored() {
        String directory = "src/main/resources/";

        FileUtils fileUtilsA = new FileUtils(FileUtils.FileParameters.builder().directory(directory).nameContains(null).build());
        FileUtils fileUtilsB = new FileUtils(FileUtils.FileParameters.builder().directory(directory).nameContains("").build());

        assertEquals(fileUtilsA.getFileNames(), fileUtilsB.getFileNames());
        assertEquals(fileUtilsA.getCount(), fileUtilsB.getCount());
        assertFalse(fileUtilsA.getFileNames().isEmpty());
        assertFalse(fileUtilsB.getFileNames().isEmpty());
        assertTrue(fileUtilsA.getCount() > 0);
        assertTrue(fileUtilsB.getCount() > 0);
        assertEquals(fileUtilsA.getCount(), fileUtilsB.getCount());
    }

    @Test
    public void givenDirectoryPathIsValidAndExtensionIsValid_whenFileUtilsIsCreated_thenSetOfFileNamesIsNotEmptyAndOnlyHasItemsThatMatchExtension() {
        String directory = "src/main/resources/";
        String extension = ".txt";

        FileUtils fileUtils = new FileUtils(FileUtils.FileParameters.builder().directory(directory).extension(extension).build());

        assertFalse(fileUtils.getFileNames().isEmpty());
        assertTrue(fileUtils.getFileNames().stream().allMatch(r -> r.contains(extension)));
        assertTrue(fileUtils.getCount() >= 1);
    }

    @Test
    public void givenDirectoryPathIsValidAndExtensionDoesNotStartWithPeriod_whenFileUtilsIsCreated_thenAPeriodWillBeAddedToTheExtension() {
        String directory = "src/main/resources/";

        FileUtils fileUtilsA = new FileUtils(FileUtils.FileParameters.builder().directory(directory).extension(".txt").build());
        FileUtils fileUtilsB = new FileUtils(FileUtils.FileParameters.builder().directory(directory).extension("txt").build());

        assertFalse(fileUtilsA.getFileNames().isEmpty());
        assertFalse(fileUtilsB.getFileNames().isEmpty());
        assertTrue(fileUtilsA.getFileNames().stream().allMatch(r -> r.contains(".txt")));
        assertTrue(fileUtilsA.getFileNames().stream().allMatch(r -> r.contains(".txt")));
        assertEquals(fileUtilsA.getFileNames(), fileUtilsB.getFileNames());
        assertTrue(fileUtilsA.getCount() > 0);
        assertTrue(fileUtilsB.getCount() > 0);
        assertEquals(fileUtilsA.getCount(), fileUtilsB.getCount());
        assertFalse(fileUtilsB.getFileNames().contains("txt.foo"));
    }

    @Test
    public void givenDirectoryPathIsValidAndExtensionIsInvalid_whenFileUtilsIsCreated_thenSetOfFileNamesIsEmpty() {
        String directory = "src/main/resources/";
        String extension = ".xyz";

        FileUtils fileUtils = new FileUtils(FileUtils.FileParameters.builder().directory(directory).extension(extension).build());

        assertTrue(fileUtils.getFileNames().isEmpty());
        assertTrue(fileUtils.getCount() == 0);
    }

    @Test
    public void givenDirectoryPathIsValidAndExtensionIsNull_whenFileUtilsIsCreated_thenExtensionIsIgnored() {
        String directory = "src/main/resources/";

        FileUtils fileUtilsA = new FileUtils(FileUtils.FileParameters.builder().directory(directory).extension(null).build());
        FileUtils fileUtilsB = new FileUtils(FileUtils.FileParameters.builder().directory(directory).extension("").build());

        assertFalse(fileUtilsA.getFileNames().isEmpty());
        assertFalse(fileUtilsB.getFileNames().isEmpty());
        assertEquals(fileUtilsA.getFileNames(), fileUtilsB.getFileNames());
        assertTrue(fileUtilsA.getCount() > 0);
        assertTrue(fileUtilsB.getCount() > 0);
        assertEquals(fileUtilsA.getCount(), fileUtilsB.getCount());
    }

}
