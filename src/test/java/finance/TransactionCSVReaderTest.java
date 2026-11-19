package finance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionCSVReaderTest {

    @Test
    void shouldParseCorrectLines() throws IOException {
        String csv = "05-12-2023,-450,Сільпо\n10-12-2023,8000,Зарплата\n";

        List<Transaction> result = TransactionCSVReader.parseTransactions(new StringReader(csv));

        assertEquals(2, result.size());
        assertEquals(new Transaction("05-12-2023", -450.0, "Сільпо"), result.get(0));
        assertEquals(new Transaction("10-12-2023", 8000.0, "Зарплата"), result.get(1));
    }

    @Test
    void shouldSkipHeaderEmptyAndBrokenLines() throws IOException {
        String csv = String.join("\n",
                "Дата,Сума,Категорія",       // заголовок
                "",                          // порожній рядок
                "05-12-2023,-450,Сільпо",
                "тільки,дві",                // мало колонок
                "32-13-2023,-1,Помилкова дата",
                "07-12-2023,abc,Погана сума");

        List<Transaction> result = TransactionCSVReader.parseTransactions(new StringReader(csv));

        assertEquals(1, result.size());
        assertEquals("Сільпо", result.get(0).getDescription());
    }

    @Test
    void shouldKeepCommasInDescription() throws IOException {
        List<Transaction> result = TransactionCSVReader.parseTransactions(
                new StringReader("01-01-2024,-10,Кава, цукор\n"));

        assertEquals("Кава, цукор", result.get(0).getDescription());
    }

    @Test
    void shouldReadFromLocalFile(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("test.csv");
        Files.writeString(file, "05-12-2023,-450,Сільпо\n", StandardCharsets.UTF_8);

        List<Transaction> result = TransactionCSVReader.readTransactions(file.toString());

        assertEquals(1, result.size());
        assertEquals(-450.0, result.get(0).getAmount());
    }
}
