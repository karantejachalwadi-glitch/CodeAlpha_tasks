/**
 * Represents a stock holding in a user's portfolio.
 * Tracks quantity owned and the average purchase price (cost basis).
 */
public class PortfolioHolding {
    private final String symbol;
    private int quantity;
    private double averagePurchasePrice;

    public PortfolioHolding(String symbol, int quantity, double averagePurchasePrice) {
        this.symbol = symbol.toUpperCase();
        this.quantity = quantity;
        this.averagePurchasePrice = Math.round(averagePurchasePrice * 100.0) / 100.0;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getAveragePurchasePrice() {
        return averagePurchasePrice;
    }

    /**
     * Updates the holding details when more shares are bought.
     * Re-calculates the average purchase price (cost basis).
     *
     * @param qty quantity bought
     * @param price purchase price per share
     */
    public void buy(int qty, double price) {
        if (qty <= 0) return;
        double totalCost = (this.quantity * this.averagePurchasePrice) + (qty * price);
        this.quantity += qty;
        this.averagePurchasePrice = Math.round((totalCost / this.quantity) * 100.0) / 100.0;
    }

    /**
     * Decreases the holding quantity when shares are sold.
     * Cost basis remains unchanged.
     *
     * @param qty quantity sold
     */
    public void sell(int qty) {
        if (qty <= 0 || qty > this.quantity) {
            throw new IllegalArgumentException("Invalid sell quantity");
        }
        this.quantity -= qty;
    }
}
