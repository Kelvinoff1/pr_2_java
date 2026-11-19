package finance;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Аналіз списку транзакцій. Усі методи статичні, список передається параметром. */
public abstract class TransactionAnalyzer {

    public static final int TOP_EXPENSES_LIMIT = 10;
    private static final DateTimeFormatter MONTH_YEAR = DateTimeFormatter.ofPattern("MM-yyyy");

    /** Загальний баланс: сума всіх доходів і витрат. */
    public static double calculateTotalBalance(List<Transaction> transactions) {
        return transactions.stream().mapToDouble(Transaction::getAmount).sum();
    }

    /** Кількість транзакцій за місяць і рік у форматі "MM-yyyy". */
    public static int countTransactionsByMonth(List<Transaction> transactions, String monthYear) {
        YearMonth target = YearMonth.parse(monthYear, MONTH_YEAR);
        return (int) transactions.stream()
                .filter(t -> YearMonth.from(t.getLocalDate()).equals(target))
                .count();
    }

    /** 10 найбільших витрат (від найбільшої до меншої). */
    public static List<Transaction> findTopExpenses(List<Transaction> transactions) {
        return findTopExpenses(transactions, TOP_EXPENSES_LIMIT);
    }

    public static List<Transaction> findTopExpenses(List<Transaction> transactions, int limit) {
        return transactions.stream()
                .filter(t -> t.getAmount() < 0)                              // лише витрати
                .sorted(Comparator.comparingDouble(Transaction::getAmount))  // від найбільш від'ємної
                .limit(limit)
                .collect(Collectors.toList());
    }

    /** Найбільша витрата за період [from; to] включно. */
    public static Optional<Transaction> findLargestExpense(List<Transaction> transactions,
                                                           LocalDate from, LocalDate to) {
        return expensesBetween(transactions, from, to)
                .min(Comparator.comparingDouble(Transaction::getAmount));
    }

    /** Найменша витрата за період [from; to] включно. */
    public static Optional<Transaction> findSmallestExpense(List<Transaction> transactions,
                                                            LocalDate from, LocalDate to) {
        return expensesBetween(transactions, from, to)
                .max(Comparator.comparingDouble(Transaction::getAmount));
    }

    /** Сума витрат по категоріях (додатні числа), від найбільшої до найменшої. */
    public static Map<String, Double> sumExpensesByCategory(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> t.getAmount() < 0)
                .collect(Collectors.groupingBy(Transaction::getDescription,
                        Collectors.summingDouble(t -> -t.getAmount())))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));
    }

    /** Сума витрат по місяцях (додатні числа), у хронологічному порядку. */
    public static Map<YearMonth, Double> sumExpensesByMonth(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> t.getAmount() < 0)
                .collect(Collectors.groupingBy(t -> YearMonth.from(t.getLocalDate()),
                        TreeMap::new,
                        Collectors.summingDouble(t -> -t.getAmount())));
    }

    private static Stream<Transaction> expensesBetween(List<Transaction> transactions,
                                                       LocalDate from, LocalDate to) {
        return transactions.stream()
                .filter(t -> t.getAmount() < 0)
                .filter(t -> {
                    LocalDate d = t.getLocalDate();
                    return !d.isBefore(from) && !d.isAfter(to);
                });
    }
}
