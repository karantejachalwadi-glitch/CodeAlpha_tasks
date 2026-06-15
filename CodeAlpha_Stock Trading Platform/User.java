import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages user balance, stock holdings, and buy/sell operations.
 */
public class User {
    private final String username;
    private double balance;
    private final Map<String, PortfolioHolding> holdings;
    private final List<Transaction> transactions;

    public User(String username, double initialBalance) {
        this.username = username;
        this.balance = Math.round(initialBalance * 100.0) / 100.0;
        this.holdings = new HashMap<>();
        this.transactions = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = Math.round(balance * 100.0) / 100.0;
    }

    public Map<String, PortfolioHolding> getHoldings() {
        return holdings;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Executes buying a stock if user has enough balance.
     *
     * @param stock the Stock to buy
     * @param quantity number of shares to buy
     * @throws IllegalArgumentException if quantity is negative, or balance is insufficient
     */
    public void buyStock(Stock stock, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        double price = stock.getCurrentPrice();
        double totalCost = price * quantity;
        if (totalCost > balance) {
            throw new IllegalArgumentException(String.format("Insufficient funds! Required: ₹%.2f, Available: ₹%.2f", totalCost, balance));
        }

        // Deduct balance
        balance = Math.round((balance - totalCost) * 100.0) / 100.0;

        // Update holdings
        PortfolioHolding holding = holdings.get(stock.getSymbol());
        if (holding == null) {
            holding = new PortfolioHolding(stock.getSymbol(), quantity, price);
            holdings.put(stock.getSymbol(), holding);
        } else {
            holding.buy(quantity, price);
        }

        // Log transaction
        Transaction t = new Transaction(LocalDateTime.now(), stock.getSymbol(), "BUY", quantity, price);
        transactions.add(t);
    }

    /**
     * Executes selling a stock if user owns sufficient shares.
     *
     * @param stock the Stock to sell
     * @param quantity number of shares to sell
     * @throws IllegalArgumentException if quantity is invalid or greater than owned quantity
     */
    public void sellStock(Stock stock, int quantity) {
        String symbol = stock.getSymbol();
        PortfolioHolding holding = holdings.get(symbol);

        if (holding == null || holding.getQuantity() < quantity) {
            int owned = (holding != null) ? holding.getQuantity() : 0;
            throw new IllegalArgumentException(String.format("Insufficient shares of %s! Owned: %d, Requested to sell: %d", symbol, owned, quantity));
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        double price = stock.getCurrentPrice();
        double revenue = price * quantity;

        // Add to balance
        balance = Math.round((balance + revenue) * 100.0) / 100.0;

        // Update or remove holdings
        holding.sell(quantity);
        if (holding.getQuantity() == 0) {
            holdings.remove(symbol);
        }

        // Log transaction
        Transaction t = new Transaction(LocalDateTime.now(), symbol, "SELL", quantity, price);
        transactions.add(t);
    }

    /**
     * Calculates the total value of all stocks in the portfolio based on current market prices.
     */
    public double getPortfolioValue(Map<String, Stock> marketStocks) {
        double totalValue = 0.0;
        for (PortfolioHolding holding : holdings.values()) {
            Stock stock = marketStocks.get(holding.getSymbol());
            if (stock != null) {
                totalValue += holding.getQuantity() * stock.getCurrentPrice();
            }
        }
        return Math.round(totalValue * 100.0) / 100.0;
    }

    /**
     * Calculates the overall net worth (cash balance + market value of holdings).
     */
    public double getTotalNetWorth(Map<String, Stock> marketStocks) {
        return Math.round((balance + getPortfolioValue(marketStocks)) * 100.0) / 100.0;
    }
}
