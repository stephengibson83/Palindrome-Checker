package com.sarkesa.palindrome.persistence;

import com.sarkesa.palindrome.model.PalindromeCheck;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvPalindromeRepositoryTest {
    private CsvPalindromeRepository csvPalindromeRepository;

    private static final String CSV_FILE_PATH = "/tmp/palindrome.csv";
    private static final String PRE_CANNED_FILE_PATH = "src/test/resources/validSavedResults.csv";

    @BeforeEach
    void setUp() {
        deleteFile(CSV_FILE_PATH);
        csvPalindromeRepository = new CsvPalindromeRepository(CSV_FILE_PATH);
    }

    @Test
    void save_shouldSaveSuccessfully() throws IOException, InterruptedException {
        String inputTest = UUID.randomUUID().toString();

        PalindromeCheck palindromeCheck = PalindromeCheck.builder().text(inputTest).isPalindrome(false).build();
        csvPalindromeRepository.save(palindromeCheck);

        sleep(500); // Need the file to be written before proceeding

        Path palindromePath = Paths.get(CSV_FILE_PATH);
        assertTrue(palindromePath.toFile().exists());

        Optional<String> lineInCsvFile = Files.lines(palindromePath).filter(lineContent -> lineContent.contains(inputTest)).findAny();
        assertTrue(lineInCsvFile.isPresent());
    }

    @Test
    void save_shouldNotSaveWhenInputNull() throws IOException, InterruptedException {
        PalindromeCheck palindromeCheck = PalindromeCheck.builder().text(null).isPalindrome(false).build();
        csvPalindromeRepository.save(palindromeCheck);

        sleep(500); // Need the file to be written before proceeding

        Path palindromePath = Paths.get(CSV_FILE_PATH);
        assertTrue(palindromePath.toFile().exists());

        assertEquals(Files.lines(palindromePath).count(), 0);
    }

    @Test
    void findAll_shouldSuccessfullyRetrieveAllThatAreNewlyWritten() throws InterruptedException {
        String randomString = UUID.randomUUID().toString();
        PalindromeCheck palindromeCheck1 = PalindromeCheck.builder().text("test1").isPalindrome(true).build();
        PalindromeCheck palindromeCheck2 = PalindromeCheck.builder().text("test2").isPalindrome(false).build();
        PalindromeCheck palindromeCheck3 = PalindromeCheck.builder().text(randomString).isPalindrome(false).build();

        csvPalindromeRepository.save(palindromeCheck1);
        csvPalindromeRepository.save(palindromeCheck2);
        csvPalindromeRepository.save(palindromeCheck3);

        sleep(1000); // Need the file to be written before proceeding

        List<PalindromeCheck> results = csvPalindromeRepository.findAll();
        assertEquals(3, results.size());
        assertTrue(results.contains(palindromeCheck1));
        assertTrue(results.contains(palindromeCheck2));
        assertTrue(results.contains(palindromeCheck3));
    }

    @Test
    void findAll_shouldSuccessfullyRetrieveAllFromPreCannedFile() {
        csvPalindromeRepository = new CsvPalindromeRepository(PRE_CANNED_FILE_PATH);

        List<PalindromeCheck> results = csvPalindromeRepository.findAll();
        assertEquals(3, results.size());

        assertTrue(results.contains(PalindromeCheck.builder().text("kayak").isPalindrome(true).build()));
        assertTrue(results.contains(PalindromeCheck.builder().text("hannah").isPalindrome(true).build()));
        assertTrue(results.contains(PalindromeCheck.builder().text("help").isPalindrome(false).build()));
    }

    @Test
    void findAll_shouldReturEmptyListWhenFileNotPopulatedYet() {
        List<PalindromeCheck> results = csvPalindromeRepository.findAll();
        assertTrue(results.isEmpty());
    }

    @SneakyThrows
    private void deleteFile(String filePath) {
        Path path = Paths.get(filePath);
        Files.deleteIfExists(path);
        System.out.println("File deleted successfully!");
    }
}