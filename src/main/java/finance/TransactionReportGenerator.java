package finance;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** Виведення звітів у консоль. Усі методи статичні. */
public abstract class TransactionReportGenerator {

    /** Одна зірочка на діаграмі = 1000 грн. */
    public static final double DEFAULT_UNIT = 1000.0;
    private static final DateTimeFormatter MONTH_YEAR = DateTimeFormatter.ofPattern("MM-yyyy");

    public static void printBalanceReport(double totalBalance) {
        System.out.println("Загальний баланс: " + money(totalBalance));
    }

    public static void printTransactionsCountByMonth(String monthYear, int count) {
        System.out.println("Кількість транзакцій за " + monthYear + ": " + count);
    }

    public static void printTopExpensesReport(List<Transaction> topExpenses) {
        System.out.println(TransactionAnalyzer.TOP_EXPENSES_LIMIT + " найбільших витрат:");
        for (Transaction expense : topExpenses) {
            System.out.println(expense.getDescription() + ": " + money(expense.getAmount()));
        }
    }

    public static void printExpenseRangeReport(LocalDate from, LocalDate to,
                                               Optional<Transaction> largest,
                                               Optional<Transaction> smallest) {
        System.out.println("Витрати за період " + from.format(Transaction.DATE_FORMAT)
                + " - " + to.format(Transaction.DATE_FORMAT) + ":");
        System.out.println("  Найбільша: " + describe(largest));
        System.out.println("  Найменша:  " + describe(smallest));
    }

    public static void printCategoryChart(Map<String, Double> totals, double unit) {
        System.out.println("Витрати по категоріях (* = " + (long) unit + " грн):");
        totals.forEach((category, sum) ->
                System.out.printf("%-22s %10s | %s%n", category, money(sum), bar(sum, unit)));
    }

    public static void printMonthlyChart(Map<YearMonth, Double> totals, double unit) {
        System.out.println("Витрати по місяцях (* = " + (long) unit + " грн):");
        totals.forEach((month, sum) ->
                System.out.printf("%-22s %10s | %s%n", month.format(MONTH_YEAR), money(sum), bar(sum, unit)));
    }

    /** Кількість зірочок = сума / unit, але не менше однієї для ненульової суми. */
    static String bar(double amount, double unit) {
        if (amount <= 0) {
            return "";
        }
        return "*".repeat(Math.max(1, (int) Math.round(amount / unit)));
    }

    private static String money(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    private static String describe(Optional<Transaction> transaction) {
        return transaction
                .map(t -> t.getDescription() + " (" + t.getDate() + "): " + money(t.getAmount()))
                .orElse("витрат за цей період немає");
    }
}
