# 🤖 AI Chatbot — Java NLP Chatbot

> **CodeAlpha Internship — Task 3: Artificial Intelligence Chatbot**

A fully-featured, Java-based AI chatbot with Natural Language Processing (NLP), rule-based ML classification, and a modern dark-themed GUI built with Java Swing.

---

## 📸 Features

| Feature | Description |
|---|---|
| 🧠 NLP Engine | Tokenization, stop-word removal, suffix stemming, bigram analysis |
| 🎯 Intent Classification | Weighted scoring via regex + keyword + bigram overlap |
| 💬 20+ Topic Domains | Java, Python, AI, Science, History, Health, Space & more |
| 🧩 Conversation Memory | Tracks user name, last intent, and chat history |
| 🎨 Modern GUI | Dark-themed Swing UI with gradient bubbles, typing animation, timestamps |
| 😂 Jokes & Fun Facts | Built-in humor and trivia responses |
| ⚡ Zero Dependencies | Pure Java — no external libraries needed |

---

## 🗂️ Project Structure

```
CodeAlpha_ Artificial Intelligence Chatbot/
│
├── src/main/java/com/chatbot/
│   │
│   ├── Main.java                        ← Application entry point
│   │
│   ├── core/
│   │   └── ChatbotEngine.java           ← Brain: intent loading, response generation, memory
│   │
│   ├── nlp/
│   │   ├── Preprocessor.java            ← Tokenizer, stop-word remover, stemmer, bigrams
│   │   ├── Intent.java                  ← Intent data model (keywords, patterns, responses)
│   │   └── IntentClassifier.java        ← Scoring engine (regex + keyword + bigram)
│   │
│   └── gui/
│       └── ChatbotGUI.java              ← Swing dark-theme chat interface
│
├── bin/                                 ← Compiled .class files (auto-generated)
├── dist/
│   └── chatbot.jar                      ← Executable JAR (auto-generated)
│
├── run.bat                              ← One-click build & run (Windows)
├── run.sh                               ← One-click build & run (Linux/macOS)
└── README.md                            ← You are here!
```

---

## ⚙️ Prerequisites

| Requirement | Version | Download |
|---|---|---|
| Java JDK | 11 or higher | [adoptium.net](https://adoptium.net/) |

> **Note:** No Maven, Gradle, or external libraries required. Pure Java!

### Verify Java Installation

```bash
java -version
javac -version
```

You should see output like:
```
java version "17.0.x" ...
javac 17.0.x
```

---

## 🚀 How to Run — Step-by-Step

### ✅ Method 1: One-Click Script (Easiest)

#### Windows
1. Open the project folder in File Explorer
2. Double-click **`run.bat`**
3. The script will automatically:
   - ✅ Check Java installation
   - ✅ Compile all source files
   - ✅ Create `dist/chatbot.jar`
   - ✅ Launch the chatbot window

#### Linux / macOS
```bash
chmod +x run.sh
./run.sh
```

---

### 🛠️ Method 2: Manual Command Line (Step by Step)

> **Windows note:** If your project path contains non-ASCII characters (e.g., Korean/Japanese folder names), use the short path. To find it, run:
> ```cmd
> for %I in ("C:\path\to\your\project") do echo %~sI
> ```

#### Step 1 — Create output folders
```cmd
mkdir bin
mkdir dist
```

#### Step 2 — Compile all Java source files
```cmd
javac -encoding UTF-8 -d bin ^
  src\main\java\com\chatbot\Main.java ^
  src\main\java\com\chatbot\core\ChatbotEngine.java ^
  src\main\java\com\chatbot\nlp\Preprocessor.java ^
  src\main\java\com\chatbot\nlp\Intent.java ^
  src\main\java\com\chatbot\nlp\IntentClassifier.java ^
  src\main\java\com\chatbot\gui\ChatbotGUI.java
```

On Linux/macOS:
```bash
javac -encoding UTF-8 -d bin \
  src/main/java/com/chatbot/Main.java \
  src/main/java/com/chatbot/core/ChatbotEngine.java \
  src/main/java/com/chatbot/nlp/Preprocessor.java \
  src/main/java/com/chatbot/nlp/Intent.java \
  src/main/java/com/chatbot/nlp/IntentClassifier.java \
  src/main/java/com/chatbot/gui/ChatbotGUI.java
```

#### Step 3 — Create the executable JAR

```cmd
echo Main-Class: com.chatbot.Main > manifest.txt
jar cfm dist\chatbot.jar manifest.txt -C bin .
del manifest.txt
```

#### Step 4 — Run the chatbot

```cmd
java -jar dist\chatbot.jar
```

---

### ⚡ Method 3: Run Without JAR (Directly from class files)

After Step 2 above:
```cmd
java -cp bin com.chatbot.Main
```

---

## 💬 What the Chatbot Knows

The chatbot is trained on **20+ topic domains** with **hundreds of responses**:

| Domain | Example Questions |
|---|---|
| 💻 **Java** | "What is Java?", "How does JVM work?", "Java OOP concepts" |
| 🐍 **Python** | "What is Python?", "Python libraries", "What is Django?" |
| 🤖 **AI & ML** | "What is machine learning?", "Explain neural networks", "What is NLP?" |
| 🌐 **Web Dev** | "What is HTML?", "Frontend vs Backend", "What is REST API?" |
| 🧮 **Math** | "What is calculus?", "Fibonacci sequence", "What are prime numbers?" |
| ⚛️ **Physics** | "Newton's laws", "What is E=mc²?", "Explain quantum mechanics" |
| 🧪 **Chemistry** | "What is pH?", "Types of chemical bonds", "Periodic table" |
| 🧬 **Biology** | "What is DNA?", "How does photosynthesis work?", "Explain evolution" |
| 📜 **History** | "What was World War II?", "Ancient civilizations", "Industrial Revolution" |
| 🌍 **Geography** | "What are the continents?", "Largest country?", "Longest river?" |
| 🏥 **Health** | "How to sleep better?", "What is a balanced diet?", "Mental health tips" |
| 🛰️ **Space** | "How many planets?", "What is a black hole?", "How old is the universe?" |
| 🌿 **Environment** | "What is climate change?", "Renewable energy types", "Greenhouse effect" |
| 📈 **Economics** | "What is GDP?", "Explain inflation", "What is a recession?" |
| 💭 **Philosophy** | "What is consciousness?", "Who was Socrates?", "Meaning of life?" |
| 😂 **Jokes** | "Tell me a joke", "Make me laugh", "Say something funny" |
| 🌟 **Fun Facts** | "Fun fact", "Did you know?", "Tell me something interesting" |
| 🤝 **Greetings** | "Hello", "Hi", "Good morning", "How are you?" |

---

## 🧠 How the NLP Works

```
User Input
    │
    ▼
┌─────────────────┐
│  Preprocessor   │  → normalize → tokenize → remove stop words → stem
└─────────────────┘
    │
    ▼
┌──────────────────────┐
│  IntentClassifier    │  Score each intent:
│                      │   • Regex match  → +3.0 pts per hit
│                      │   • Keyword hit  → +1.0 pts per word
│                      │   • Bigram bonus → +0.5 pts per pair
│                      │   × Intent weight multiplier
└──────────────────────┘
    │
    ▼
Best scoring intent (if score ≥ 1.0 threshold)
    │
    ▼
Random response from that intent's response pool
    │
    ▼
Personalization (inject user name occasionally)
    │
    ▼
Bot Reply ✅
```

---

## 🎨 GUI Overview

| Element | Description |
|---|---|
| 🎨 Header | Gradient purple-blue header with bot name and online status |
| 💬 Chat Area | Scrollable dark panel with distinct user (purple) and bot (dark) bubbles |
| 📍 Timestamps | Each message shows HH:mm time |
| ⌨️ Typing Indicator | Animated "AI is typing..." dots while processing |
| ✉️ Input Bar | Multi-line text field; **Enter** sends, **Shift+Enter** = new line |
| 🖱️ Send Button | Gradient button with hover effect |

---

## 🐛 Troubleshooting

| Problem | Solution |
|---|---|
| `javac not found` | Install JDK (not just JRE) and add `bin` folder to PATH |
| `java not found` | Add Java's `bin` directory to system PATH |
| Compilation errors | Make sure all 6 `.java` files are present in `src/main/java/com/chatbot/` |
| Path with special chars | Use short-path method (see Method 2 note above) or move to a simple path like `C:\Chatbot\` |
| GUI doesn't open | Ensure your system has a display. On headless servers use `-Djava.awt.headless=false` |
| JAR launch fails | Re-compile and re-package; check that `bin/` contains `.class` files |

---

## 📌 Running on PATH with Special Characters (Korean/Japanese/etc.)

If your project is in a folder with non-ASCII characters:

1. Open **Command Prompt** (cmd, not PowerShell)
2. Run: `for %I in ("C:\full\path\to\project") do echo %~sI`
3. Use the short path (e.g., `C:\Users\Asus\ONEDRI~1\5744~1\CODEAL~4`) in all `javac` and `java` commands

---

## 👨‍💻 Author & Credits

- **Project:** CodeAlpha Internship — Task 3
- **Technology:** Java SE 11+, Java Swing
- **NLP Approach:** Rule-based with weighted intent scoring (regex + keyword + bigram)
- **No external dependencies** — pure Java standard library only

---

## 📄 License

This project was created as part of the **CodeAlpha Internship Program**.
Free to use for educational purposes.
