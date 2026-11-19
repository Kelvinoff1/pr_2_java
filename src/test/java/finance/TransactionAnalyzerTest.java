package finance;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionAnalyzerTest {

    @Test
    void testCalculateTotalBalance() {
        List<Transaction> transactions = Arrays.asList(
                new Transaction("01-01-2023", 100.0, "Дохід"),
                new Transaction("02-01-2023", -50.0, "Витрата"),
                new Transaction("03-01-2023", 150.0, "Дохід"));

        assertEquals(200.0, TransactionAnalyzer.calculateTotalBalance(transactions),
                "Розрахунок загального балансу неправильний");
    }

    @Test
    void testCountTransactionsByMonth() {
        List<Transaction> transactions = Arrays.asList(
                new Transaction("01-02-2023", 50.0, "Дохід"),
                new Transaction("15-02-2023", -20.0, "Витрата"),
                new Transaction("05-03-2023", 100.0, "Дохід"));

        assertEquals(2, TransactionAnalyzer.countTransactionsByMonth(transactions, "02-2023"),
                "Кількість транзакцій за лютий неправильна");
        assertEquals(1, TransactionAnalyzer.countTransactionsByMonth(transactions, "03-2023"),
                "Кількість транзакцій за березень неправильна");
        assertEquals(0, TransactionAnalyzer.countTransactionsByMonth(transactions, "04-2023"));
    }

    @Test
    void testFindTopExpensesReturnsTenLargestSorted() {
        // 12 витрат: -100, -200, ... -1200, плюс один дохід
        List<Transaction> transactions = IntStream.rangeClosed(1, 12)
                .mapToObj(i -> new Transaction("01-01-2024", -100.0 * i, "Витрата " + i))
                .collect(Collectors.toList());
        transactions.add(new Transaction("02-01-2024", 99999.0, "Зарплата"));

        List<Transaction> top = TransactionAnalyzer.findTopExpenses(transactions);

        assertEquals(10, top.size());
        assertEquals(-1200.0, top.get(0).getAmount());
        assertEquals(-300.0, top.get(9).getAmount());
        assertTrue(top.stream().allMatch(t -> t.getAmount() < 0), "Доходи не мають потрапляти у витрати");
    }

    @Test
    void testFindLargestAndSmallestExpenseInPeriod() {
        List<Transaction> transactions = Arrays.asList(
                new Transaction("05-01-2024", -500.0, "Сільпо"),
                new Transaction("10-01-2024", -50.0, "Кава"),
                new Transaction("20-01-2024", 3000.0, "Зарплата"),
                new Transaction("05-02-2024", -9000.0, "Подарунки")); // поза періодом

        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        assertEquals(-500.0, TransactionAnalyzer.findLargestExpense(transactions, from, to).orElseThrow().getAmount());
        assertEquals(-50.0, TransactionAnalyzer.findSmallestExpense(transactions, from, to).orElseThrow().getAmount());
    }

    @Test
    void testExpenseInEmptyPeriodIsEmpty() {
        List<Transaction> transactions = List.of(new Transaction("05-01-2024", -500.0, "Сільпо"));
        Optional<Transaction> result = TransactionAnalyzer.findLargestExpense(
                transactions, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));
        assertTrue(result.isEmpty());
    }

    @Test
    void testSumExpensesByCategoryIsSortedDescending() {
        List<Transaction> transactions = Arrays.asList(
                new Transaction("01-01-2024", -100.0, "Кава"),
                new Transaction("02-01-2024", -400.0, "Сільпо"),
                new Transaction("03-01-2024", -200.0, "Кава"),
                new Transaction("04-01-2024", 5000.0, "Зарплата"));

        Map<String, Double> result = TransactionAnalyzer.sumExpensesByCategory(transactions);

        assertEquals(List.of("Сільпо", "Кава"), List.copyOf(result.keySet()));
        assertEquals(300.0, result.get("Кава"));
        assertEquals(2, result.size());
    }

    @Test
    void testSumExpensesByMonth() {
        List<Transaction> transactions = Arrays.asList(
                new Transaction("01-12-2023", -100.0, "Кава"),
                new Transaction("15-12-2023", -50.0, "Кава"),
                new Transaction("01-01-2024", -70.0, "Кава"));

        Map<YearMonth, Double> result = TransactionAnalyzer.sumExpensesByMonth(transactions);

        assertEquals(150.0, result.get(YearMonth.of(2023, 12)));
        assertEquals(70.0, result.get(YearMonth.of(2024, 1)));
        assertEquals(YearMonth.of(2023, 12), result.keySet().iterator().next(), "Місяці мають іти хронологічно");
    }
}
