import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a historical stock transaction (BUY or SELL).
 */
public class Transaction {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final LocalDateTime timestamp;
    private final String symbol;
    private final String type; // "BUY" or "SELL"
    private final int quantity;
    private final double price;
    private final double totalValue;

    public Transaction(LocalDateTime timestamp, String symbol, String type, int quantity, double price) {
        this.timestamp = timestamp;
        this.symbol = symbol.toUpperCase();
        this.type = type.toUpperCase();
        this.quantity = quantity;
        this.price = price;
        this.totalValue = Math.round((quantity * price) * 100.0) / 100.0;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getTotalValue() {
        return totalValue;
    }

    /**
     * Converts the transaction details to a CSV format line for file storage.
     */
    public String toCSV() {
        return String.format("%s,%s,%s,%d,%.2f",
                timestamp.format(FORMATTER),
                symbol,
                type,
                quantity,
                price
        );
    }

    /**
     * Parses a CSV format line back into a Transaction object.
     */
    public static Transaction fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length < 5) {
            throw new IllegalArgumentException("Invalid transaction CSV line format");
        }
        LocalDateTime time = LocalDateTime.parse(parts[0], FORMATTER);
        String symbol = parts[1];
        String type = parts[2];
        int qty = Integer.parseInt(parts[3]);
        double price = Double.parseDouble(parts[4]);
        return new Transaction(time, symbol, type, qty, price);
    }

    @Override
    public String toString() {
        return String.format("[%s] %-4s %-6s | Quantity: %-4d | Price: ₹%-7.2f | Total: ₹%-9.2f",
                timestamp.format(DISPLAY_FORMATTER),
                type,
                symbol,
                quantity,
                price,
                totalValue
        );
    }
}
