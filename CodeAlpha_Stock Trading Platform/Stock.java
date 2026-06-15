/**
 * Represents a stock in the trading simulator.
 * Tracks current price, previous price, and calculates percentage changes.
 */
public class Stock {
    private final String symbol;
    private final String name;
    private double currentPrice;
    private double previousPrice;

    public Stock(String symbol, String name, double initialPrice) {
        this.symbol = symbol.toUpperCase();
        this.name = name;
        this.currentPrice = initialPrice;
        this.previousPrice = initialPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public double getPreviousPrice() {
        return previousPrice;
    }

    /**
     * Updates the stock's current price based on a percentage change.
     * Prevents the price from falling below 0.01.
     *
     * @param percentChange percentage change (e.g. 2.5 for +2.5%, -1.2 for -1.2%)
     */
    public void updatePrice(double percentChange) {
        this.previousPrice = this.currentPrice;
        double newPrice = this.currentPrice * (1 + (percentChange / 100.0));
        // Keep a realistic minimum stock price
        this.currentPrice = Math.max(0.01, Math.round(newPrice * 100.0) / 100.0);
    }

    /**
     * Calculates the percentage change from the previous price to the current price.
     *
     * @return percentage change as a double
     */
    public double getChangePercentage() {
        if (previousPrice == 0) return 0.0;
        double change = ((currentPrice - previousPrice) / previousPrice) * 100.0;
        return Math.round(change * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - ₹%.2f", symbol, name, currentPrice);
    }
}
