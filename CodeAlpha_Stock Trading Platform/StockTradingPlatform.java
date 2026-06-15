import java.util.Map;
import java.util.Scanner;

/**
 * Main application class providing the interactive command-line interface (CLI)
 * for the Stock Trading Platform.
 */
public class StockTradingPlatform {
    // ANSI Color Escape Codes
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";

    private static boolean useColors = true; // Easily toggled if terminal doesn't support ANSI

    private final Market market;
    private final User user;
    private final Scanner scanner;

    public StockTradingPlatform() {
        this.market = new Market();
        // Load existing portfolio data for default user 'Guest' with starting cash of ₹8,40,000.00
        this.user = DataManager.loadState("Guest", 840000.00);
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        // Simple command-line flag check for disabling colors
        for (String arg : args) {
            if (arg.equalsIgnoreCase("--no-color")) {
                useColors = false;
                break;
            }
        }

        StockTradingPlatform platform = new StockTradingPlatform();
        platform.run();
    }

    public void run() {
        printHeader();
        System.out.printf("Welcome back, %s!\n", colorize(user.getUsername(), BOLD + CYAN));
        System.out.printf("Current Net Worth: %s\n", formatCurrency(user.getTotalNetWorth(market.getStocks())));

        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = getNextString("Select an option (1-7): ");
            System.out.println();

            switch (choice) {
                case "1":
                    displayMarket();
                    break;
                case "2":
                    executeBuy();
                    break;
                case "3":
                    executeSell();
                    break;
                case "4":
                    displayPortfolio();
                    break;
                case "5":
                    displayTransactionHistory();
                    break;
                case "6":
                    advanceMarketTick();
                    break;
                case "7":
                    saveAndExit();
                    running = false;
                    break;
                default:
                    printError("Invalid option. Please choose between 1 and 7.");
            }
            if (running) {
                System.out.println();
                getNextString("Press [Enter] to return to the main menu...");
            }
        }
    }

    private void printHeader() {
        System.out.println(colorize("===============================================", BLUE));
        System.out.println(colorize("       CODEALPHA STOCK TRADING SIMULATOR       ", BOLD + BLUE));
        System.out.println(colorize("===============================================", BLUE));
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println(colorize("--- MAIN MENU ---", BOLD));
        System.out.println("1. View Stock Market Data");
        System.out.println("2. Buy Stock");
        System.out.println("3. Sell Stock");
        System.out.println("4. View Portfolio & Performance");
        System.out.println("5. View Transaction History");
        System.out.println("6. Advance Market (Simulate Price Fluctuations)");
        System.out.println(colorize("7. Save State & Exit", YELLOW));
    }

    private void displayMarket() {
        System.out.println(colorize("----------------------------------------------------------------", BOLD + BLUE));
        System.out.printf(colorize("%-8s %-22s %-12s %-12s %-8s\n", BOLD + BLUE), "SYMBOL", "COMPANY NAME", "PRICE", "PREV PRICE", "DAILY CHANGE");
        System.out.println(colorize("----------------------------------------------------------------", BOLD + BLUE));

        for (Stock s : market.getStocks().values()) {
            double changePercent = s.getChangePercentage();
            String changeStr;
            if (changePercent > 0) {
                changeStr = colorize(String.format("+%.2f%% ▲", changePercent), GREEN);
            } else if (changePercent < 0) {
                changeStr = colorize(String.format("%.2f%% ▼", changePercent), RED);
            } else {
                changeStr = colorize("0.00% =", RESET);
            }

            System.out.printf("%-8s %-22s ₹%-11.2f ₹%-11.2f %s\n",
                    colorize(s.getSymbol(), BOLD),
                    s.getName(),
                    s.getCurrentPrice(),
                    s.getPreviousPrice(),
                    changeStr
            );
        }
        System.out.println(colorize("----------------------------------------------------------------", BOLD + BLUE));
    }

    private void executeBuy() {
        displayMarket();
        String symbol = getNextString("Enter stock symbol to BUY: ").toUpperCase();
        Stock stock = market.getStock(symbol);

        if (stock == null) {
            printError("Stock symbol '" + symbol + "' not found in the market.");
            return;
        }

        System.out.printf("Stock selected: %s (%s) - Current Price: %s\n",
                stock.getName(), stock.getSymbol(), formatCurrency(stock.getCurrentPrice()));
        System.out.printf("Available balance: %s\n", formatCurrency(user.getBalance()));

        int quantity = getNextInt("Enter quantity to buy: ");
        if (quantity <= 0) {
            printError("Quantity must be a positive integer.");
            return;
        }

        try {
            double cost = stock.getCurrentPrice() * quantity;
            user.buyStock(stock, quantity);
            DataManager.saveState(user); // Auto-save on successful transaction
            printSuccess(String.format("Successfully bought %d shares of %s for a total of %s!",
                    quantity, symbol, formatCurrency(cost)));
            System.out.printf("New remaining cash balance: %s\n", formatCurrency(user.getBalance()));

            // Small immediate market reaction simulation
            market.fluctuatePrices();
            System.out.println(colorize("[Market Note] Market updated slightly following your trade.", YELLOW));
        } catch (IllegalArgumentException e) {
            printError(e.getMessage());
        }
    }

    private void executeSell() {
        Map<String, PortfolioHolding> holdings = user.getHoldings();
        if (holdings.isEmpty()) {
            printError("You do not own any stocks to sell.");
            return;
        }

        System.out.println(colorize("--- YOUR CURRENT HOLDINGS ---", BOLD));
        for (PortfolioHolding holding : holdings.values()) {
            Stock s = market.getStock(holding.getSymbol());
            double currPrice = (s != null) ? s.getCurrentPrice() : 0.0;
            System.out.printf("%s: %d shares (Average Cost: %s | Current Price: %s)\n",
                    colorize(holding.getSymbol(), BOLD),
                    holding.getQuantity(),
                    formatCurrency(holding.getAveragePurchasePrice()),
                    formatCurrency(currPrice)
            );
        }
        System.out.println();

        String symbol = getNextString("Enter stock symbol to SELL: ").toUpperCase();
        Stock stock = market.getStock(symbol);
        if (stock == null) {
            printError("Stock symbol '" + symbol + "' not found in the market.");
            return;
        }

        PortfolioHolding holding = holdings.get(symbol);
        if (holding == null) {
            printError("You do not own any shares of " + symbol);
            return;
        }

        int quantity = getNextInt("Enter quantity to sell (Max: " + holding.getQuantity() + "): ");
        if (quantity <= 0) {
            printError("Quantity must be a positive integer.");
            return;
        }

        try {
            double revenue = stock.getCurrentPrice() * quantity;
            user.sellStock(stock, quantity);
            DataManager.saveState(user); // Auto-save on successful transaction
            printSuccess(String.format("Successfully sold %d shares of %s for a total of %s!",
                    quantity, symbol, formatCurrency(revenue)));
            System.out.printf("New cash balance: %s\n", formatCurrency(user.getBalance()));

            // Small immediate market reaction simulation
            market.fluctuatePrices();
            System.out.println(colorize("[Market Note] Market updated slightly following your trade.", YELLOW));
        } catch (IllegalArgumentException e) {
            printError(e.getMessage());
        }
    }

    private void displayPortfolio() {
        double cash = user.getBalance();
        double holdingsValue = user.getPortfolioValue(market.getStocks());
        double netWorth = user.getTotalNetWorth(market.getStocks());
        double startingFunds = 840000.00; // Reference starting funds (INR)
        double overallReturn = ((netWorth - startingFunds) / startingFunds) * 100.0;

        System.out.println(colorize("=======================================================================", BOLD + BLUE));
        System.out.println(colorize("                           PORTFOLIO PERFORMANCE                       ", BOLD + BLUE));
        System.out.println(colorize("=======================================================================", BOLD + BLUE));
        System.out.printf(" Cash Balance:       %-25s\n", formatCurrency(cash));
        System.out.printf(" Holdings Value:     %-25s\n", formatCurrency(holdingsValue));
        System.out.printf(" Total Net Worth:    %-25s (Initial Funds: ₹8,40,000.00)\n", formatCurrency(netWorth));

        String returnStr;
        if (overallReturn > 0) {
            returnStr = colorize(String.format("+%.2f%% ▲", overallReturn), GREEN);
        } else if (overallReturn < 0) {
            returnStr = colorize(String.format("%.2f%% ▼", overallReturn), RED);
        } else {
            returnStr = "0.00% =";
        }
        System.out.printf(" Overall Return:     %-25s\n", returnStr);
        System.out.println(colorize("-----------------------------------------------------------------------", BLUE));

        Map<String, PortfolioHolding> holdings = user.getHoldings();
        if (holdings.isEmpty()) {
            System.out.println(" You do not currently hold any shares.");
        } else {
            System.out.printf(colorize(" %-8s %-6s %-12s %-12s %-12s %-10s\n", BOLD + BLUE),
                    "SYMBOL", "QTY", "AVG COST", "CURR PRICE", "TOTAL VALUE", "RETURN (%)");
            System.out.println(colorize(" ---------------------------------------------------------------------", BLUE));

            for (PortfolioHolding holding : holdings.values()) {
                Stock s = market.getStock(holding.getSymbol());
                double currPrice = (s != null) ? s.getCurrentPrice() : 0.0;
                double avgCost = holding.getAveragePurchasePrice();
                double totalValue = holding.getQuantity() * currPrice;
                double stockReturn = 0.0;
                if (avgCost > 0) {
                    stockReturn = ((currPrice - avgCost) / avgCost) * 100.0;
                }

                String stockReturnStr;
                if (stockReturn > 0) {
                    stockReturnStr = colorize(String.format("+%.2f%% ▲", stockReturn), GREEN);
                } else if (stockReturn < 0) {
                    stockReturnStr = colorize(String.format("%.2f%% ▼", stockReturn), RED);
                } else {
                    stockReturnStr = "0.00% =";
                }

                System.out.printf(" %-8s %-6d ₹%-11.2f ₹%-11.2f ₹%-11.2f %s\n",
                        colorize(holding.getSymbol(), BOLD),
                        holding.getQuantity(),
                        avgCost,
                        currPrice,
                        totalValue,
                        stockReturnStr
                );
            }
        }
        System.out.println(colorize("=======================================================================", BOLD + BLUE));
    }

    private void displayTransactionHistory() {
        System.out.println(colorize("=======================================================================", BOLD + BLUE));
        System.out.println(colorize("                          TRANSACTION LOG HISTORY                      ", BOLD + BLUE));
        System.out.println(colorize("=======================================================================", BOLD + BLUE));

        if (user.getTransactions().isEmpty()) {
            System.out.println(" No transactions recorded yet.");
        } else {
            for (Transaction t : user.getTransactions()) {
                // Color-code BUY vs SELL
                String typeStr = t.getType().equalsIgnoreCase("BUY") ? colorize("BUY", GREEN) : colorize("SELL", RED);
                String record = t.toString().replace("BUY ", typeStr).replace("SELL", typeStr);
                System.out.println(" " + record);
            }
        }
        System.out.println(colorize("=======================================================================", BOLD + BLUE));
    }

    private void advanceMarketTick() {
        System.out.println("Simulating market price updates...");
        market.fluctuatePrices();
        System.out.println(colorize("Success: Market updated! Selected stock prices have fluctuated.", GREEN));
        displayMarket();
    }

    private void saveAndExit() {
        System.out.println("Saving portfolio and transaction data...");
        DataManager.saveState(user);
        System.out.println(colorize("Save complete! Thank you for playing. Goodbye!", BOLD + GREEN));
    }

    // --- Helper Utilities ---

    private String colorize(String text, String colorCode) {
        if (!useColors) return text;
        return colorCode + text + RESET;
    }

    private String formatCurrency(double val) {
        return colorize(String.format("₹%.2f", val), BOLD);
    }

    private void printError(String errorMsg) {
        System.out.println(colorize("Error: " + errorMsg, BOLD + RED));
    }

    private void printSuccess(String successMsg) {
        System.out.println(colorize("Success: " + successMsg, BOLD + GREEN));
    }

    private String getNextString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int getNextInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                printError("Please enter a valid integer.");
            }
        }
    }
}
