import java.io.*;
import java.util.ArrayList;
import java.util.List;




/**
 * Handles the saving and loading of user portfolios and transactions using text files.
 */
public class DataManager {
    private static final String PORTFOLIO_FILE = "portfolio.txt";
    private static final String TRANSACTIONS_FILE = "transactions.csv";

    /**
     * Saves user state (balance and holdings) and transaction log.
     *
     * @param user the User object to save
     */
    public static void saveState(User user) {
        // Save portfolio
        try (PrintWriter writer = new PrintWriter(new FileWriter(PORTFOLIO_FILE))) {
            writer.println("username:" + user.getUsername());
            writer.println("balance:" + user.getBalance());
            writer.println("HOLDINGS");
            for (PortfolioHolding holding : user.getHoldings().values()) {
                writer.printf("%s,%d,%.2f\n",
                        holding.getSymbol(),
                        holding.getQuantity(),
                        holding.getAveragePurchasePrice());
            }
        } catch (IOException e) {
            System.err.println("Error saving portfolio data: " + e.getMessage());
        }

        // Save transactions
        try (PrintWriter writer = new PrintWriter(new FileWriter(TRANSACTIONS_FILE))) {
            for (Transaction t : user.getTransactions()) {
                writer.println(t.toCSV());
            }
        } catch (IOException e) {
            System.err.println("Error saving transaction data: " + e.getMessage());
        }
    }

    /**
     * Loads user state from file, or returns a new User with default settings if files aren't found.
     *
     * @param defaultUsername username to create if file doesn't exist
     * @param defaultBalance balance to start with if file doesn't exist
     * @return User object reconstructed from file or a fresh default User
     */
    public static User loadState(String defaultUsername, double defaultBalance) {
        File portfolioFile = new File(PORTFOLIO_FILE);
        if (!portfolioFile.exists()) {
            return new User(defaultUsername, defaultBalance);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(portfolioFile))) {
            String line;
            String username = defaultUsername;
            double balance = defaultBalance;
            boolean readingHoldings = false;
            List<PortfolioHolding> loadedHoldings = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("username:")) {
                    username = line.substring(9).trim();
                } else if (line.startsWith("balance:")) {
                    balance = Double.parseDouble(line.substring(8).trim());
                } else if (line.equals("HOLDINGS")) {
                    readingHoldings = true;
                } else if (readingHoldings) {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        String symbol = parts[0];
                        int qty = Integer.parseInt(parts[1]);
                        double avgPrice = Double.parseDouble(parts[2]);
                        loadedHoldings.add(new PortfolioHolding(symbol, qty, avgPrice));
                    }
                }
            }

            User user = new User(username, balance);
            for (PortfolioHolding holding : loadedHoldings) {
                user.getHoldings().put(holding.getSymbol(), holding);
            }

            // Load transactions
            loadTransactions(user);

            return user;

        } catch (Exception e) {
            System.out.println("Warning: Could not load existing portfolio. Starting fresh. Details: " + e.getMessage());
            return new User(defaultUsername, defaultBalance);
        }
    }

    /**
     * Loads transactions from transactions.csv and adds them to the user.
     */
    private static void loadTransactions(User user) {
        File txFile = new File(TRANSACTIONS_FILE);
        if (!txFile.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(txFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                try {
                    Transaction t = Transaction.fromCSV(line);
                    user.getTransactions().add(t);
                } catch (Exception parseException) {
                    // Ignore malformed transactions lines and continue
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading transactions file: " + e.getMessage());
        }
    }
}
