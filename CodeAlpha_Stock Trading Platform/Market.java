import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Manages the collection of stocks and simulates market fluctuations.
 */
public class Market {
    private final Map<String, Stock> stocks;
    private final Random random;

    public Market() {
        this.stocks = new HashMap<>();
        this.random = new Random();
        initializeMarket();
    }

    private void initializeMarket() {
        // Prices in Indian Rupees (INR) at approx. ₹84 per USD
        addStock(new Stock("AAPL", "Apple Inc.", 14742.00));
        addStock(new Stock("GOOGL", "Alphabet Inc.", 12793.20));
        addStock(new Stock("MSFT", "Microsoft Corp.", 34876.80));
        addStock(new Stock("AMZN", "Amazon.com Inc.", 14985.60));
        addStock(new Stock("TSLA", "Tesla Inc.", 14368.20));
        addStock(new Stock("NVDA", "NVIDIA Corp.", 73510.08));
        addStock(new Stock("NFLX", "Netflix Inc.", 51282.00));
    }

    private void addStock(Stock stock) {
        stocks.put(stock.getSymbol(), stock);
    }

    public Map<String, Stock> getStocks() {
        return stocks;
    }

    public Stock getStock(String symbol) {
        return stocks.get(symbol.toUpperCase());
    }

    /**
     * Simulates a single market tick where stock prices fluctuate.
     * Price changes are usually standard (-3% to +3%), but occasional market news
     * can cause a larger move (-10% to +10%).
     */
    public void fluctuatePrices() {
        for (Stock stock : stocks.values()) {
            double changePercent;
            int eventChance = random.nextInt(100);

            if (eventChance < 5) {
                // Positive news event (5% chance)
                changePercent = 4.0 + (random.nextDouble() * 6.0); // +4% to +10%
            } else if (eventChance < 10) {
                // Negative news event (5% chance)
                changePercent = -10.0 + (random.nextDouble() * 6.0); // -10% to -4%
            } else {
                // Standard volatility (90% chance)
                changePercent = -3.0 + (random.nextDouble() * 6.0); // -3% to +3%
            }

            stock.updatePrice(changePercent);
        }
    }
}
