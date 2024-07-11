package com.sarkesa.palindrome.persistence;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.sarkesa.palindrome.model.PalindromeCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CsvPalindromeRepository implements PalindromeRepository {
    private final Path csvFilePath;

    public CsvPalindromeRepository(@Value("${csvFilePath}") final String csvFilePath) {
        this.csvFilePath = Path.of(csvFilePath);
    }

    @Override
    public void save(final PalindromeCheck palindromeCheck) {
        final CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            saveToFile(palindromeCheck);
            return "Success";
        }).exceptionally(ex -> {
            log.error("Error in task: {}", ex.getMessage());
            return "Error occurred";
        }).completeOnTimeout("Timed out!", 2, TimeUnit.SECONDS);

        future.thenAccept(result -> log.info("Saving Palindrome to file: {}", result));
    }

    private void saveToFile(final PalindromeCheck palindromeCheck) {
        try (final Writer writer = new FileWriter(csvFilePath.toString(), StandardCharsets.UTF_8, true)) {
            final StatefulBeanToCsv<PalindromeCheck> beanToCsv = new StatefulBeanToCsvBuilder<PalindromeCheck>(writer).build();
            beanToCsv.write(palindromeCheck);
        } catch (CsvRequiredFieldEmptyException | CsvDataTypeMismatchException | IOException e) {
            throw new RuntimeException(String.format("Caught excpetion of type %s with message %s", e.getClass(), e.getMessage()), e);
        }
    }

    @Override
    public List<PalindromeCheck> findAll() {
        return readAllRecordsFromCsvFile();
    }

    private List<PalindromeCheck> readAllRecordsFromCsvFile() {
        try (Reader reader = Files.newBufferedReader(csvFilePath)) {
            final CsvToBean<PalindromeCheck> csvToBean = new CsvToBeanBuilder<PalindromeCheck>(reader)
                    .withType(PalindromeCheck.class)
                    .build();
            return csvToBean.parse();
        } catch (final IOException ex) {
            log.error("Could not open CSV file at [{}]", csvFilePath);
        }
        return List.of();
    }
}
