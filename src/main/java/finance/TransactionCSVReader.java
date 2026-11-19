package finance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Читання транзакцій з CSV.
 * Процес розділено на дві частини (завдання 1.2):
 *  1) {@link #openReader(String)} - отримання даних із джерела (URL або локальний файл);
 *  2) {@link #parseTransactions(Reader)} - розбір CSV, не залежить від джерела.
 */
public abstract class TransactionCSVReader {

    private static final String DELIMITER = ",";

    /** Читає транзакції з URL (http/https) або з локального файлу. */
    public static List<Transaction> readTransactions(String source) {
        try (BufferedReader reader = openReader(source)) {
            return parseTransactions(reader);
        } catch (IOException e) {
            throw new UncheckedIOException("Не вдалося прочитати джерело: " + source, e);
        }
    }

    /** Частина 1: відкриває потік даних. Щоб додати нове джерело, змінюємо лише цей метод. */
    static BufferedReader openReader(String source) throws IOException {
        if (source.startsWith("http://") || source.startsWith("https://")) {
            return new BufferedReader(new InputStreamReader(
                    URI.create(source).toURL().openStream(), StandardCharsets.UTF_8));
        }
        return Files.newBufferedReader(Path.of(source), StandardCharsets.UTF_8);
    }

    /** Частина 2: перетворює CSV-рядки на об'єкти Transaction. Некоректні рядки пропускаються. */
    public static List<Transaction> parseTransactions(Reader source) throws IOException {
        BufferedReader reader = (source instanceof BufferedReader br) ? br : new BufferedReader(source);
        List<Transaction> transactions = new ArrayList<>();
        String line;
        int lineNumber = 0;
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            if (lineNumber == 1 && line.startsWith("\uFEFF")) {
                line = line.substring(1); // прибираємо BOM
            }
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            // limit 3: кома в описі не ламає розбір
            String[] values = line.split(DELIMITER, 3);
            if (values.length < 3) {
                System.err.println("Пропущено рядок " + lineNumber + " (мало колонок): " + line);
                continue;
            }
            try {
                Transaction transaction = new Transaction(
                        values[0].trim(), Double.parseDouble(values[1].trim()), values[2].trim());
                transaction.getLocalDate(); // перевірка формату дати
                transactions.add(transaction);
            } catch (NumberFormatException | DateTimeParseException e) {
                System.err.println("Пропущено рядок " + lineNumber + " (некоректні дані): " + line);
            }
        }
        return transactions;
    }
}
