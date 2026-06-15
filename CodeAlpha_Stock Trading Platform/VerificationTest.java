import java.io.File;
import java.time.LocalDateTime;

/**
 * Automates the validation of the Stock Trading Platform OOP engine,
 * verifying that market mechanics, buy/sell rules, cost-basis logic,
 * and file serialization function perfectly.
 */
public class VerificationTest {

    public static void main(String[] args) {
        System.out.println("=== STARTING CODEALPHA STOCK TRADING PLATFORM VERIFICATION ===");
        
        try {
            testStockModel();
            testUserAndPortfolioHolding();
            testTransactionModel();
            testDataManagerPersistence();
            
            System.out.println("\n[SUCCESS] All core OOP classes and behaviors verified successfully!");
        } catch (Exception e) {
            System.err.println("\n[FAILURE] Verification failed with exception:");
            e.printStackTrace();
            System.exit(1);
        } finally {
            // Clean up files generated during tests
            new File("portfolio.txt").delete();
            new File("transactions.csv").delete();
        }
    }

    private static void testStockModel() {
        System.out.print("Testing Stock Model... ");
        Stock stock = new Stock("AAPL", "Apple Inc.", 100.0);
        
        if (!stock.getSymbol().equals("AAPL")) throw new AssertionError("Stock symbol mismatch");
        if (!stock.getName().equals("Apple Inc.")) throw new AssertionError("Stock name mismatch");
        if (stock.getCurrentPrice() != 100.0) throw new AssertionError("Initial price mismatch");
        
        // Test price increase
        stock.updatePrice(5.0); // +5%
        if (stock.getCurrentPrice() != 105.0) throw new AssertionError("Price update failed on increase");
        if (stock.getChangePercentage() != 5.0) throw new AssertionError("Percentage calculation failed on increase");
        
        // Test price decrease
        stock.updatePrice(-10.0); // -10% from 105.0 = 94.5
        if (stock.getCurrentPrice() != 94.5) throw new AssertionError("Price update failed on decrease");
        if (stock.getChangePercentage() != -10.0) throw new AssertionError("Percentage calculation failed on decrease");
        
        System.out.println("PASSED");
    }

    private static void testUserAndPortfolioHolding() {
        System.out.print("Testing User and PortfolioHoldings... ");
        User user = new User("TestUser", 1000.00);
        Stock aapl = new Stock("AAPL", "Apple Inc.", 150.0);
        
        // Buy 2 shares of AAPL (₹300 total)
        user.buyStock(aapl, 2);
        if (user.getBalance() != 700.0) throw new AssertionError("Balance not deducted correctly");
        
        PortfolioHolding holding = user.getHoldings().get("AAPL");
        if (holding == null || holding.getQuantity() != 2) throw new AssertionError("Holdings quantity mismatch");
        if (holding.getAveragePurchasePrice() != 150.0) throw new AssertionError("Average cost basis mismatch");

        // Buy 2 more shares of AAPL at a different price (₹160.0)
        aapl.updatePrice(6.6667); // price goes to ~160.0
        user.buyStock(aapl, 2); // new avg price = (2*150 + 2*160)/4 = 155.0
        
        if (user.getBalance() != 380.0) throw new AssertionError("Balance not deducted correctly on second buy");
        if (holding.getQuantity() != 4) throw new AssertionError("Holding quantity did not aggregate");
        if (holding.getAveragePurchasePrice() != 155.0) throw new AssertionError("Average cost basis calculation incorrect");

        // Attempt buy beyond balance
        try {
            user.buyStock(aapl, 10); // costs ₹1600, user has ₹380
            throw new AssertionError("Allowed buy transaction with insufficient balance");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Sell 3 shares
        user.sellStock(aapl, 3); // cash returns 3 * 160.0 = 480.0. New balance = 860.0
        if (user.getBalance() != 860.0) throw new AssertionError("Balance did not increase correctly on sell");
        if (holding.getQuantity() != 1) throw new AssertionError("Holding quantity did not decrease correctly on sell");
        
        // Attempt sell beyond owned qty
        try {
            user.sellStock(aapl, 5);
            throw new AssertionError("Allowed sell transaction exceeding owned quantity");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        System.out.println("PASSED");
    }

    private static void testTransactionModel() {
        System.out.print("Testing Transaction CSV Serialization... ");
        LocalDateTime now = LocalDateTime.now();
        Transaction t = new Transaction(now, "MSFT", "BUY", 10, 350.50);
        
        String csv = t.toCSV();
        Transaction loaded = Transaction.fromCSV(csv);
        
        if (!loaded.getSymbol().equals("MSFT")) throw new AssertionError("Deserialized symbol mismatch");
        if (!loaded.getType().equals("BUY")) throw new AssertionError("Deserialized type mismatch");
        if (loaded.getQuantity() != 10) throw new AssertionError("Deserialized quantity mismatch");
        if (loaded.getPrice() != 350.50) throw new AssertionError("Deserialized price mismatch");
        if (loaded.getTotalValue() != 3505.0) throw new AssertionError("Deserialized total value mismatch");
        
        System.out.println("PASSED");
    }

    private static void testDataManagerPersistence() {
        System.out.print("Testing DataManager Persistence (File I/O)... ");
        User user = new User("PersistUser", 5000.00);
        Stock tsla = new Stock("TSLA", "Tesla Inc.", 200.0);
        
        user.buyStock(tsla, 5); // Cash = 4000. Holdings = 5 TSLA @ 200.0
        
        // Save current user state to files
        DataManager.saveState(user);
        
        // Load the saved state back
        User loadedUser = DataManager.loadState("PersistUser", 10000.00);
        
        if (!loadedUser.getUsername().equals("PersistUser")) throw new AssertionError("Loaded username mismatch");
        if (loadedUser.getBalance() != 4000.00) throw new AssertionError("Loaded balance mismatch");
        
        PortfolioHolding loadedHolding = loadedUser.getHoldings().get("TSLA");
        if (loadedHolding == null) throw new AssertionError("Loaded holdings list is empty");
        if (loadedHolding.getQuantity() != 5) throw new AssertionError("Loaded holding quantity mismatch");
        if (loadedHolding.getAveragePurchasePrice() != 200.0) throw new AssertionError("Loaded holding average price mismatch");
        if (loadedUser.getTransactions().size() != 1) throw new AssertionError("Loaded transaction count mismatch");
        
        System.out.println("PASSED");
    }
}
