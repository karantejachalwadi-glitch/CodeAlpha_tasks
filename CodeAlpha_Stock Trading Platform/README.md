# Stock Trading Platform

A lightweight, terminal-based stock market simulation platform built in Java. This application simulates a stock exchange with price fluctuations, tracks user portfolios, processes buy/sell trades, calculates performance analytics, and persists data between executions using file I/O. All prices are displayed in **Indian Rupees (₹)**.

---

## Features

1. **Interactive Console UI**: Displays data tables with terminal color coding (green ▲ for gains, red ▼ for losses) using ANSI codes.
2. **Market Price Simulator**: Generates price movements at every tick. Standard fluctuations average -3% to +3%, with a 10% chance of high-volatility news events (-10% to +10%).
3. **Portfolio Analytics**: Tracks total cash balance, assets value, cost basis, and calculated returns (both overall and stock-specific profit/loss).
4. **Transaction Log Audit**: Automatically logs details of every transaction (timestamp, action, ticker, quantity, price, and total value).
5. **State Persistence**: Saves cash balances and holdings to `portfolio.txt` and logs to `transactions.csv`, auto-saving transactions so your portfolio retains data across sessions.

---

## OOP Design Architecture

The project adheres to Object-Oriented Programming (OOP) principles by dividing components into distinct classes:

*   **`Stock`**: Manages ticker details, current and historical prices, and fluctuation updates.
*   **`PortfolioHolding`**: Manages shares owned, average cost basis, and recalculates average price per share on additional purchases.
*   **`Transaction`**: Models single trade orders (BUY/SELL) and formats them for display and CSV file serialization.
*   **`User`**: Controls the user profile, remaining cash balances, stock buying/selling validation rules, and holds the holdings registry.
*   **`Market`**: Registers all active tradeable tickers and drives market volatility.
*   **`DataManager`**: Manages reading and writing data to local flat files (`portfolio.txt` and `transactions.csv`).
*   **`StockTradingPlatform`**: Serves as the application entry point, containing CLI menu loops, text coloring helpers, and input handlers.

---

## Setup & Running the Project

### Prerequisites

*   **Java Development Kit (JDK)**: Version 8 or higher (this project was successfully tested with JDK 25 LTS).

### Compilation

Open your command prompt or terminal in the project directory (`CodeAlpha_Stock Trading Platform`) and run:

```bash
javac *.java
```

### Execution

Run the compiled platform:

```bash
java StockTradingPlatform
```

#### Running Without ANSI Colors

If your terminal window does not support standard ANSI colors (resulting in scrambled characters like `[31m` or `[32m`), disable them by passing the `--no-color` argument:

```bash
java StockTradingPlatform --no-color
```

---

## Simulated Tickers Available

*   **AAPL** (Apple Inc.) - Starting at ₹14,742.00
*   **GOOGL** (Alphabet Inc.) - Starting at ₹12,793.20
*   **MSFT** (Microsoft Corp.) - Starting at ₹34,876.80
*   **AMZN** (Amazon.com Inc.) - Starting at ₹14,985.60
*   **TSLA** (Tesla Inc.) - Starting at ₹14,368.20
*   **NVDA** (NVIDIA Corp.) - Starting at ₹73,510.08
*   **NFLX** (Netflix Inc.) - Starting at ₹51,282.00

> Prices are denominated in Indian Rupees (INR) at an approximate exchange rate of ₹84 per USD.
