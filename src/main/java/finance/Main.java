package finance;

import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.util.List;

public class Main {
    private static final String DEFAULT_SOURCE = "https://informer.com.ua/dut/java/pr2.csv";

    public static void main(String[] args) {
        // Джерело можна передати аргументом: URL або шлях до локального файлу
        String source = args.length > 0 ? args[0] : DEFAULT_SOURCE;

        List<Transaction> transactions;
        try {
            transactions = TransactionCSVReader.readTransactions(source);
        } catch (UncheckedIOException e) {
            System.err.println(e.getMessage() + " (" + e.getCause() + ")");
            return;
        }
        if (transactions.isEmpty()) {
            System.out.println("Транзакцій не знайдено.");
            return;
        }

        // Завдання 1: вміст файлу
        System.out.println("Прочитано транзакцій: " + transactions.size());
        transactions.forEach(System.out::println);

        // Завдання 2-4: баланс і кількість за місяць
        System.out.println();
        TransactionReportGenerator.printBalanceReport(TransactionAnalyzer.calculateTotalBalance(transactions));
        String monthYear = "01-2024";
        TransactionReportGenerator.printTransactionsCountByMonth(
                monthYear, TransactionAnalyzer.countTransactionsByMonth(transactions, monthYear));

        // Завдання 5: 10 найбільших витрат
        System.out.println();
        TransactionReportGenerator.printTopExpensesReport(TransactionAnalyzer.findTopExpenses(transactions));

        // Самостійна: найбільша і найменша витрата за період
        System.out.println();
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);
        TransactionReportGenerator.printExpenseRangeReport(from, to,
                TransactionAnalyzer.findLargestExpense(transactions, from, to),
                TransactionAnalyzer.findSmallestExpense(transactions, from, to));

        // Самостійна: текстові діаграми
        System.out.println();
        TransactionReportGenerator.printCategoryChart(
                TransactionAnalyzer.sumExpensesByCategory(transactions), TransactionReportGenerator.DEFAULT_UNIT);
        System.out.println();
        TransactionReportGenerator.printMonthlyChart(
                TransactionAnalyzer.sumExpensesByMonth(transactions), TransactionReportGenerator.DEFAULT_UNIT);
    }
}
