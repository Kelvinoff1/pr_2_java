package finance;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class Transaction {
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private String date;
    private double amount;
    private String description;

    public LocalDate getLocalDate() {
        return LocalDate.parse(date, DATE_FORMAT);
    }
}
