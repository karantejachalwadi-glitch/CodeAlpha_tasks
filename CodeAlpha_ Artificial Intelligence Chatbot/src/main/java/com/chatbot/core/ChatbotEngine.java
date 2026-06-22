package com.chatbot.core;

import com.chatbot.nlp.Intent;
import com.chatbot.nlp.IntentClassifier;

import java.util.*;

/**
 * ChatbotEngine – the brain of the chatbot.
 *
 * Wires together:
 *  - The IntentClassifier (NLP layer)
 *  - FAQ training data (300+ intents across 20+ topics)
 *  - Conversation context / memory (last intent, user name, turn count)
 *  - Fallback responses with suggestions
 */
public class ChatbotEngine {

    private final IntentClassifier classifier = new IntentClassifier();
    private final Random random = new Random();

    // ---------- Conversation state ----------
    private String userName         = null;
    private String lastIntentName   = null;
    private int    turnCount        = 0;
    private final Deque<String> history = new ArrayDeque<>();   // last 5 user messages

    // ---------- Constructor ----------
    public ChatbotEngine() {
        loadIntents();
    }

    // =========================================================
    //  Public API
    // =========================================================

    /**
     * Process a single user message and return the bot's reply.
     */
    public String respond(String userInput) {
        if (userInput == null || userInput.isBlank()) {
            return "Please say something — I'm here to help! 😊";
        }

        turnCount++;
        history.addLast(userInput.trim());
        if (history.size() > 5) history.pollFirst();

        // --- Check for name extraction first ---
        String nameCheck = extractName(userInput);
        if (nameCheck != null) {
            userName = nameCheck;
            return "Nice to meet you, " + userName + "! 😊 How can I assist you today?";
        }

        // --- Classify intent ---
        Intent intent = classifier.classify(userInput);

        String response;
        if (intent != null) {
            lastIntentName = intent.getName();
            response = intent.getRandomResponse();

            // Personalize if we know the user's name
            if (userName != null && random.nextInt(4) == 0) {
                response = userName + ", " + response;
            }
        } else {
            response = fallback(userInput);
        }

        return response;
    }

    public String getUserName()    { return userName;       }
    public int    getTurnCount()   { return turnCount;      }
    public String getLastIntent()  { return lastIntentName; }

    // =========================================================
    //  Name extraction
    // =========================================================
    private String extractName(String input) {
        String norm = input.toLowerCase(Locale.ENGLISH).trim();
        String[] patterns = {
            "my name is (.+)",
            "i am (.+)",
            "i'm (.+)",
            "call me (.+)",
            "you can call me (.+)"
        };
        for (String pat : patterns) {
            java.util.regex.Matcher m =
                java.util.regex.Pattern.compile(pat).matcher(norm);
            if (m.find()) {
                String candidate = m.group(1).trim();
                // Only accept if it looks like a name (1-3 words, no special chars)
                if (candidate.matches("[a-z]+(\\s[a-z]+){0,2}") && candidate.length() < 40) {
                    // Capitalize
                    String[] parts = candidate.split("\\s+");
                    StringBuilder sb = new StringBuilder();
                    for (String p : parts) {
                        sb.append(Character.toUpperCase(p.charAt(0)))
                          .append(p.substring(1)).append(" ");
                    }
                    return sb.toString().trim();
                }
            }
        }
        return null;
    }

    // =========================================================
    //  Fallback
    // =========================================================
    private String fallback(String input) {
        String[] fallbacks = {
            "Hmm, I'm not sure I understand. Could you rephrase that? 🤔",
            "That's a bit outside my expertise right now. Try asking about topics like technology, science, math, or general knowledge!",
            "I didn't quite get that. You can ask me about programming, AI, history, health, and much more!",
            "Interesting question! I'm still learning. Can you ask me something else?",
            "I'm not sure about that one. Try asking: 'What can you do?' to see my capabilities!",
        };
        return fallbacks[random.nextInt(fallbacks.length)];
    }

    // =========================================================
    //  Intent loading – FAQ training data
    // =========================================================
    private void loadIntents() {

        // ---- Greetings ----
        addIntent("greeting",
            Arrays.asList("hello", "hi", "hey", "greet", "howdy", "sup", "morning", "evening", "afternoon"),
            Arrays.asList("\\b(hello|hi|hey|howdy|sup|good morning|good evening|good afternoon)\\b"),
            Arrays.asList(
                "Hello there! 👋 How can I help you today?",
                "Hey! Great to see you! What's on your mind?",
                "Hi! I'm your AI assistant. Ask me anything! 🤖",
                "Good day! What would you like to talk about?",
                "Hello! I'm here and ready to chat. How can I assist?"
            ), 1.5);

        // ---- How are you ----
        addIntent("how_are_you",
            Arrays.asList("how", "are", "doing", "feeling", "going", "fine", "good"),
            Arrays.asList("how are you", "how('?re| are) (you|u)", "you doing", "how do you feel", "how's it going"),
            Arrays.asList(
                "I'm doing great, thanks for asking! 😊 How about you?",
                "Feeling fantastic! I'm an AI so I'm always ready to help. 🤖",
                "I'm running smoothly! Ready to answer your questions. What's up?",
                "All systems nominal! 🚀 What can I do for you today?"
            ), 1.5);

        // ---- Goodbye ----
        addIntent("goodbye",
            Arrays.asList("bye", "goodbye", "quit", "exit", "see", "later", "farewell", "cya"),
            Arrays.asList("\\b(bye|goodbye|quit|exit|see you|farewell|cya|ttyl|take care)\\b"),
            Arrays.asList(
                "Goodbye! It was a pleasure chatting with you! 👋",
                "See you later! Have a wonderful day! ☀️",
                "Take care! Come back anytime you have questions! 😊",
                "Bye! Stay curious and keep learning! 📚"
            ), 1.5);

        // ---- Thanks ----
        addIntent("thanks",
            Arrays.asList("thank", "thanks", "appreciate", "grateful", "cheers"),
            Arrays.asList("\\b(thanks|thank you|thank u|thx|cheers|appreciate|grateful)\\b"),
            Arrays.asList(
                "You're welcome! 😊 Happy to help!",
                "No problem at all! That's what I'm here for!",
                "Anytime! Is there anything else you'd like to know?",
                "My pleasure! Feel free to ask more questions!"
            ), 1.5);

        // ---- What can you do ----
        addIntent("capabilities",
            Arrays.asList("what", "can", "do", "help", "capable", "ability", "feature", "function"),
            Arrays.asList("what can you do", "how can you help", "what are you capable", "your features", "your abilities"),
            Arrays.asList(
                "I can help you with a wide range of topics! 🌟\n" +
                "• 💻 Programming & Technology (Java, Python, AI, web dev)\n" +
                "• 🧮 Math & Science (algebra, physics, chemistry, biology)\n" +
                "• 📜 History & Geography\n" +
                "• 🏥 Health & Wellness tips\n" +
                "• 🌍 General knowledge & FAQ\n" +
                "• 😂 Jokes and fun facts!\n" +
                "Just ask me anything!"
            ), 1.5);

        // ---- Who are you ----
        addIntent("identity",
            Arrays.asList("who", "what", "are", "you", "name", "bot", "ai", "robot", "chatbot"),
            Arrays.asList("who are you", "what are you", "your name", "are you (a )?bot", "are you (an )?ai", "are you human"),
            Arrays.asList(
                "I'm an AI Chatbot built in Java! 🤖 I use NLP techniques to understand and respond to your messages.",
                "I'm your friendly AI assistant, coded in Java with rule-based NLP. Ask me anything!",
                "I'm a Java-powered chatbot with Natural Language Processing. I'm here to help you! 😊",
                "Great question! I'm an AI chatbot — not human, but I do my best to be helpful! 🚀"
            ), 1.5);

        // ---- Java programming ----
        addIntent("java",
            Arrays.asList("java", "jdk", "jvm", "class", "object", "inheritance", "polymorphism", "compile", "bytecode", "spring", "maven", "gradle"),
            Arrays.asList("\\bjava\\b", "jdk", "jvm", "spring boot", "maven", "gradle"),
            Arrays.asList(
                "Java is a high-level, object-oriented programming language developed by Sun Microsystems in 1995. ☕\n" +
                "Key features: platform independence (Write Once, Run Anywhere), strong typing, garbage collection, and rich APIs.",
                "Java runs on the JVM (Java Virtual Machine), which compiles source code to bytecode — making it platform-independent! 🚀",
                "Java supports four OOP pillars:\n• Encapsulation\n• Inheritance\n• Polymorphism\n• Abstraction\nThese make code modular and reusable.",
                "Popular Java frameworks include Spring Boot (web apps), Hibernate (ORM), JUnit (testing), and Apache Maven/Gradle (build tools).",
                "Java tip 💡: Use StringBuilder instead of String concatenation in loops for better performance!"
            ), 1.2);

        // ---- Python programming ----
        addIntent("python",
            Arrays.asList("python", "pip", "django", "flask", "numpy", "pandas", "scikit", "tensorflow", "pytorch", "script"),
            Arrays.asList("\\bpython\\b", "\\bpip\\b", "django", "flask", "numpy", "pandas"),
            Arrays.asList(
                "Python is a versatile, high-level programming language known for its simple syntax and readability. 🐍",
                "Python is widely used in AI/ML, data science, web development (Django, Flask), automation, and scientific computing.",
                "Python's most popular libraries include NumPy, Pandas, Matplotlib, Scikit-learn, TensorFlow, and PyTorch.",
                "Python tip 💡: Use list comprehensions for concise, readable code:\n`squares = [x**2 for x in range(10)]`",
                "Python is dynamically typed, interpreted, and supports multiple programming paradigms (OOP, functional, procedural)."
            ), 1.2);

        // ---- Artificial Intelligence ----
        addIntent("ai",
            Arrays.asList("ai", "artificial", "intelligence", "machine", "learning", "deep", "neural", "network", "nlp", "natural", "language"),
            Arrays.asList("artificial intelligence", "\\bai\\b", "machine learning", "deep learning", "neural network", "natural language processing", "\\bnlp\\b"),
            Arrays.asList(
                "Artificial Intelligence (AI) is the simulation of human intelligence by machines. 🤖\nKey branches: ML, Deep Learning, NLP, Computer Vision, Robotics.",
                "Machine Learning (ML) is a subset of AI where algorithms learn patterns from data without being explicitly programmed.",
                "Deep Learning uses artificial neural networks with multiple layers to learn complex representations from large datasets.",
                "Natural Language Processing (NLP) enables computers to understand, interpret, and generate human language — it's what powers me! 💬",
                "AI applications include: voice assistants (Siri, Alexa), recommendation systems (Netflix, Spotify), autonomous vehicles, medical diagnosis, and chatbots!"
            ), 1.2);

        // ---- Web Development ----
        addIntent("web_dev",
            Arrays.asList("html", "css", "javascript", "web", "frontend", "backend", "react", "angular", "vue", "nodejs", "rest", "api", "http"),
            Arrays.asList("\\bhtml\\b", "\\bcss\\b", "\\bjavascript\\b|\\bjs\\b", "web development", "frontend", "backend", "\\breact\\b", "\\bnode\\b"),
            Arrays.asList(
                "Web development involves building websites and web applications.\n• Frontend: HTML, CSS, JavaScript, React/Vue/Angular\n• Backend: Node.js, Java, Python, PHP\n• Databases: MySQL, MongoDB, PostgreSQL",
                "HTML structures web pages, CSS styles them, and JavaScript adds interactivity — they're the three pillars of frontend development!",
                "REST APIs use HTTP methods (GET, POST, PUT, DELETE) to allow communication between frontend and backend services.",
                "Modern frontend frameworks like React, Vue, and Angular make it easier to build complex, interactive single-page applications (SPAs)."
            ), 1.2);

        // ---- Data Structures ----
        addIntent("data_structures",
            Arrays.asList("array", "list", "stack", "queue", "tree", "graph", "hash", "linked", "binary", "sort", "search", "algorithm", "complexity", "big", "notation"),
            Arrays.asList("data structure", "\\barray\\b", "\\bstack\\b", "\\bqueue\\b", "\\btree\\b", "\\bgraph\\b", "linked list", "binary search", "big.?o"),
            Arrays.asList(
                "Common data structures:\n• Array – O(1) access, O(n) search\n• LinkedList – O(1) insert/delete at ends\n• Stack – LIFO (Last In, First Out)\n• Queue – FIFO (First In, First Out)\n• HashMap – O(1) average lookup",
                "Trees are hierarchical data structures. A Binary Search Tree (BST) allows O(log n) search, insert, and delete on average.",
                "Big-O notation describes algorithm efficiency:\n• O(1) – constant\n• O(log n) – logarithmic\n• O(n) – linear\n• O(n²) – quadratic",
                "Sorting algorithms:\n• Bubble Sort: O(n²) – simple but slow\n• Merge Sort: O(n log n) – efficient, stable\n• Quick Sort: O(n log n) avg – fast in practice\n• Heap Sort: O(n log n) – in-place"
            ), 1.2);

        // ---- Mathematics ----
        addIntent("math",
            Arrays.asList("math", "mathematics", "algebra", "calculus", "geometry", "equation", "derivative", "integral", "matrix", "vector", "prime", "fibonacci", "statistics", "probability"),
            Arrays.asList("\\bmath\\b", "mathematics", "algebra", "calculus", "\\bgeometry\\b", "derivative", "integral", "\\bmatrix\\b", "\\bprime\\b", "fibonacci"),
            Arrays.asList(
                "Mathematics is the language of the universe! 🧮 Key branches: Algebra, Calculus, Geometry, Statistics, Number Theory, and Linear Algebra.",
                "The Fibonacci sequence: 0, 1, 1, 2, 3, 5, 8, 13, 21… Each number is the sum of the two preceding ones. It appears everywhere in nature!",
                "Prime numbers are divisible only by 1 and themselves: 2, 3, 5, 7, 11, 13… There are infinitely many primes (proved by Euclid ~300 BC).",
                "Calculus has two main branches:\n• Differential Calculus – rates of change (derivatives)\n• Integral Calculus – areas under curves (integrals)\nFundamental Theorem: they're inverses of each other!",
                "Probability basics:\n• P(A) = favorable outcomes / total outcomes\n• P(A and B) = P(A) × P(B) if independent\n• P(A or B) = P(A) + P(B) − P(A and B)"
            ), 1.2);

        // ---- Physics ----
        addIntent("physics",
            Arrays.asList("physics", "force", "gravity", "energy", "velocity", "acceleration", "newton", "einstein", "relativity", "quantum", "electricity", "magnetism", "wave", "light"),
            Arrays.asList("\\bphysics\\b", "\\bforce\\b", "\\bgravity\\b", "\\benergy\\b", "newton", "einstein", "relativity", "quantum", "electricity"),
            Arrays.asList(
                "Newton's Three Laws of Motion:\n1. An object at rest stays at rest (Inertia)\n2. F = ma (Force = mass × acceleration)\n3. Every action has an equal and opposite reaction",
                "Einstein's famous equation E = mc² states that energy equals mass times the speed of light squared — unifying mass and energy!",
                "Quantum mechanics describes physics at the atomic/subatomic level, where particles exhibit wave-particle duality and probability-based behavior.",
                "The speed of light in a vacuum is approximately 299,792,458 m/s (~3 × 10⁸ m/s). Nothing with mass can reach this speed!",
                "Gravity: according to Newton, F = G(m₁m₂)/r². Einstein refined this with General Relativity — gravity is the curvature of spacetime by mass."
            ), 1.2);

        // ---- Chemistry ----
        addIntent("chemistry",
            Arrays.asList("chemistry", "atom", "molecule", "element", "periodic", "chemical", "reaction", "bond", "acid", "base", "ph", "electron", "proton", "neutron"),
            Arrays.asList("\\bchemistry\\b", "\\batom\\b", "\\bmolecule\\b", "periodic table", "chemical reaction", "\\bacid\\b", "\\bbase\\b", "\\bph\\b"),
            Arrays.asList(
                "The Periodic Table has 118 confirmed elements organized by atomic number. Groups (columns) share similar chemical properties.",
                "Atoms consist of a nucleus (protons + neutrons) surrounded by electron clouds. Proton count = atomic number = element identity.",
                "Chemical bonds:\n• Ionic – transfer of electrons (e.g., NaCl)\n• Covalent – sharing of electrons (e.g., H₂O)\n• Metallic – sea of electrons (metals)",
                "pH scale (0-14): Below 7 = acidic (vinegar, lemon juice), 7 = neutral (water), above 7 = basic/alkaline (baking soda, bleach).",
                "Water (H₂O) is a polar molecule with unique properties: high surface tension, high heat capacity, and the ability to dissolve many substances (universal solvent)."
            ), 1.2);

        // ---- Biology ----
        addIntent("biology",
            Arrays.asList("biology", "cell", "dna", "gene", "evolution", "organism", "species", "bacteria", "virus", "photosynthesis", "protein", "enzyme"),
            Arrays.asList("\\bbiology\\b", "\\bcell\\b", "\\bdna\\b", "\\bgene\\b", "evolution", "\\bvirus\\b", "\\bbacteria\\b", "photosynthesis"),
            Arrays.asList(
                "DNA (Deoxyribonucleic Acid) is the molecule that carries genetic information. It's a double helix made of nucleotide bases: A, T, G, C.",
                "The cell is the basic unit of life. Prokaryotic cells (bacteria) have no nucleus; eukaryotic cells (plants, animals) have a membrane-bound nucleus.",
                "Photosynthesis: 6CO₂ + 6H₂O + light energy → C₆H₁₂O₆ + 6O₂ — plants convert light into glucose for energy.",
                "Darwin's Theory of Evolution: species change over time through natural selection — organisms with beneficial traits survive and reproduce more.",
                "Viruses are not technically alive — they're genetic material (DNA or RNA) in a protein coat that hijacks host cells to replicate."
            ), 1.2);

        // ---- History ----
        addIntent("history",
            Arrays.asList("history", "war", "ancient", "civilization", "empire", "revolution", "historical", "century", "world"),
            Arrays.asList("\\bhistory\\b", "world war", "ancient", "civilization", "\\bempire\\b", "\\brevolution\\b"),
            Arrays.asList(
                "World War II (1939–1945) was the deadliest conflict in history, involving most of the world's nations and resulting in 70–85 million casualties.",
                "Ancient civilizations: Mesopotamia (first writing ~3200 BC), Egypt (pyramids ~2560 BC), Indus Valley, Greece, and Rome shaped modern culture.",
                "The Industrial Revolution (1760–1840) transformed manufacturing from hand production to machines, starting in Britain and spreading worldwide.",
                "The French Revolution (1789–1799) ended absolute monarchy in France and introduced ideals of liberty, equality, and fraternity that shaped modern democracy.",
                "Ancient Rome's contributions: Roman law, Latin language (root of Romance languages), engineering (aqueducts, roads), and republican government."
            ), 1.2);

        // ---- Geography ----
        addIntent("geography",
            Arrays.asList("country", "capital", "continent", "largest", "ocean", "river", "mountain", "population", "geography", "city"),
            Arrays.asList("\\bgeography\\b", "capital (of|city)", "largest country", "\\bocean\\b", "\\bcontinent\\b"),
            Arrays.asList(
                "The seven continents: Asia, Africa, North America, South America, Antarctica, Europe, and Australia/Oceania. Asia is the largest!",
                "The five oceans: Pacific (largest), Atlantic, Indian, Southern, and Arctic. Together they cover ~71% of Earth's surface.",
                "The world's highest mountain: Mount Everest (8,848.86 m / 29,032 ft) in the Himalayas on the Nepal-Tibet border.",
                "Largest countries by area:\n1. Russia (17.1M km²)\n2. Canada (10.0M km²)\n3. USA (9.8M km²)\n4. China (9.6M km²)\n5. Brazil (8.5M km²)",
                "The Amazon River carries the most water of any river (20% of all freshwater discharge). The Nile is the longest at ~6,650 km."
            ), 1.2);

        // ---- Health & Wellness ----
        addIntent("health",
            Arrays.asList("health", "diet", "exercise", "sleep", "calories", "nutrition", "vitamin", "mental", "stress", "fitness", "weight", "disease"),
            Arrays.asList("\\bhealth\\b", "\\bdiet\\b", "\\bexercise\\b", "\\bsleep\\b", "\\bfitness\\b", "mental health", "\\bnutrition\\b"),
            Arrays.asList(
                "WHO recommends: 150+ minutes of moderate exercise per week, 7-9 hours of sleep nightly, a balanced diet, and no smoking for good health!",
                "Good sleep tips 😴:\n• Stick to a consistent schedule\n• Avoid screens 1 hour before bed\n• Keep room cool and dark\n• Limit caffeine after 2pm",
                "A balanced diet includes: proteins (meat, legumes), carbohydrates (grains, fruits), healthy fats (avocado, nuts), and plenty of vegetables.",
                "Mental health matters as much as physical health! Practice mindfulness, connect with others, exercise regularly, and seek help when needed. 💙",
                "Staying hydrated is crucial — drink 8+ glasses of water daily. Water supports every body function including brain performance!"
            ), 1.2);

        // ---- Technology ----
        addIntent("technology",
            Arrays.asList("technology", "computer", "smartphone", "internet", "cloud", "blockchain", "5g", "iot", "cybersecurity", "software", "hardware"),
            Arrays.asList("\\btechnology\\b", "\\bcomputer\\b", "\\binternet\\b", "\\bcloud\\b", "blockchain", "\\b5g\\b", "cybersecurity"),
            Arrays.asList(
                "Cloud computing delivers computing services (servers, storage, databases, AI) over the internet. Major providers: AWS, Google Cloud, Microsoft Azure.",
                "Blockchain is a distributed ledger technology where data is stored in linked blocks, making it tamper-resistant. Powers cryptocurrencies like Bitcoin.",
                "5G networks offer speeds up to 100× faster than 4G, enabling IoT, autonomous vehicles, AR/VR, and smarter cities.",
                "Cybersecurity protects systems and data from digital attacks. Key practices: strong passwords, two-factor auth, regular updates, and VPN use.",
                "The Internet of Things (IoT) connects everyday devices (fridges, watches, cars) to the internet, enabling smart automation and data collection."
            ), 1.2);

        // ---- Space & Astronomy ----
        addIntent("space",
            Arrays.asList("space", "planet", "star", "galaxy", "universe", "nasa", "moon", "sun", "solar", "black", "hole", "astronaut"),
            Arrays.asList("\\bspace\\b", "\\bplanet\\b", "\\bstar\\b", "\\bgalaxy\\b", "\\buniverse\\b", "\\bnasa\\b", "\\bmoon\\b", "black hole", "solar system"),
            Arrays.asList(
                "Our solar system has 8 planets: Mercury, Venus, Earth, Mars, Jupiter, Saturn, Uranus, Neptune. Jupiter is the largest!",
                "The Sun accounts for 99.86% of the solar system's total mass. It's about 109 times the diameter of Earth. ☀️",
                "A black hole is a region where gravity is so strong that nothing — not even light — can escape. They form when massive stars collapse.",
                "The Milky Way galaxy contains 100-400 billion stars and is about 100,000 light-years in diameter. Earth is in one of its spiral arms.",
                "The universe is approximately 13.8 billion years old, originating from the Big Bang — an expansion of an incredibly hot, dense singularity."
            ), 1.2);

        // ---- Environment ----
        addIntent("environment",
            Arrays.asList("environment", "climate", "global", "warming", "pollution", "renewable", "energy", "carbon", "ozone", "recycle", "sustainability", "ocean"),
            Arrays.asList("\\benvironment\\b", "climate change", "global warming", "\\bpollution\\b", "renewable energy", "\\bcarbon\\b"),
            Arrays.asList(
                "Climate change refers to long-term shifts in global temperatures and weather patterns. Since the 1800s, human activities (burning fossil fuels) are the main driver.",
                "The greenhouse effect: CO₂ and other gases trap heat in Earth's atmosphere. Without it, Earth would be too cold; too much causes global warming. 🌡️",
                "Renewable energy sources: Solar ☀️, Wind 💨, Hydropower 💧, Geothermal 🌋, and Biomass. They produce little to no greenhouse gases.",
                "Recycling reduces waste, conserves resources, and cuts emissions. Aluminum recycling saves 95% of the energy needed to produce new aluminum!",
                "The ozone layer (in the stratosphere) shields Earth from UV radiation. CFCs were damaging it; the 1987 Montreal Protocol helped it recover!"
            ), 1.2);

        // ---- Economics ----
        addIntent("economics",
            Arrays.asList("economics", "economy", "gdp", "inflation", "stock", "market", "trade", "currency", "investment", "budget", "recession", "finance"),
            Arrays.asList("\\beconomics\\b", "\\beconomy\\b", "\\bgdp\\b", "\\binflation\\b", "stock market", "\\btrade\\b", "\\brecession\\b"),
            Arrays.asList(
                "GDP (Gross Domestic Product) measures the total monetary value of all goods and services produced in a country over a specific period.",
                "Inflation is the rate at which prices rise over time, eroding purchasing power. Central banks (like the Federal Reserve) manage it through interest rates.",
                "Stock markets allow companies to raise capital by selling shares to the public. Key indices: S&P 500, Dow Jones (USA), FTSE 100 (UK), Nikkei 225 (Japan).",
                "Supply and demand: when supply exceeds demand, prices fall; when demand exceeds supply, prices rise. This is the fundamental principle of economics.",
                "A recession is two consecutive quarters of negative GDP growth. Causes include reduced consumer spending, financial crises, or external shocks."
            ), 1.2);

        // ---- Philosophy ----
        addIntent("philosophy",
            Arrays.asList("philosophy", "ethics", "moral", "logic", "meaning", "existence", "consciousness", "socrates", "plato", "aristotle", "kant"),
            Arrays.asList("\\bphilosophy\\b", "\\bethics\\b", "\\bmorality\\b", "\\bconsciousness\\b", "meaning of life", "\\bsocrates\\b", "\\bplato\\b"),
            Arrays.asList(
                "Philosophy explores fundamental questions about existence, knowledge, values, reason, and reality. Major branches: metaphysics, epistemology, ethics, logic.",
                "Socrates taught through questions (Socratic method), Plato proposed the Theory of Forms, and Aristotle emphasized empirical observation and logic.",
                "The meaning of life? Philosophers have answered differently:\n• Hedonism: maximize pleasure\n• Stoicism: live virtuously\n• Existentialism: you create your own meaning\n• Utilitarianism: maximize overall happiness",
                "Consciousness remains one of philosophy's hardest problems. Descartes said 'I think, therefore I am' — thinking is proof of existence!",
                "Kant's Categorical Imperative: act only according to principles you'd want to be universal laws. It's a cornerstone of deontological ethics."
            ), 1.2);

        // ---- Jokes ----
        addIntent("joke",
            Arrays.asList("joke", "funny", "laugh", "humor", "pun", "silly", "hilarious"),
            Arrays.asList("tell (me )?(a )?joke", "\\bjoke\\b", "make me laugh", "something funny", "\\bfunny\\b"),
            Arrays.asList(
                "Why do programmers prefer dark mode? Because light attracts bugs! 🐛😂",
                "Why did the Java developer quit? Because they didn't get arrays! 😄",
                "A SQL query walks into a bar, approaches two tables and asks… 'Can I join you?' 🍺",
                "Why was the math book sad? Because it had too many problems! 📚😂",
                "I told my computer I needed a break. Now it won't stop sending me Kit-Kat ads! 🍫",
                "Why do scientists rarely tell jokes? Because they're afraid the jokes won't have a good reception! 📡😄",
                "What do you call a fake noodle? An impasta! 🍝",
                "Why was the computer cold? Because it left its Windows open! 💻❄️"
            ), 1.3);

        // ---- Fun Facts ----
        addIntent("fun_fact",
            Arrays.asList("fact", "interesting", "did", "know", "trivia", "cool", "amazing", "surprise"),
            Arrays.asList("fun fact", "did you know", "interesting fact", "tell me (a |something |an )?(cool|interesting|amazing|random|fun)", "\\btrivia\\b"),
            Arrays.asList(
                "🌟 Fun Fact: Honey never spoils! Archaeologists found 3,000-year-old honey in Egyptian tombs that was still edible.",
                "🌟 Fun Fact: A day on Venus is longer than a year on Venus — it rotates so slowly it takes 243 Earth days to spin once!",
                "🌟 Fun Fact: Octopuses have three hearts, blue blood, and can change color — and they're remarkably intelligent! 🐙",
                "🌟 Fun Fact: There are more possible iterations of a game of chess than there are atoms in the observable universe!",
                "🌟 Fun Fact: Bananas are mildly radioactive because they contain potassium-40. But you'd need to eat 10 million to get a harmful dose! 🍌",
                "🌟 Fun Fact: The Great Wall of China is NOT visible from space with the naked eye — it's a common myth!",
                "🌟 Fun Fact: Your brain generates about 12-25 watts of electricity — enough to power a small LED light bulb! 🧠💡",
                "🌟 Fun Fact: Oxford University is older than the Aztec Empire! Teaching started there around 1096 AD."
            ), 1.2);

        // ---- Time ----
        addIntent("time",
            Arrays.asList("time", "date", "today", "clock", "day", "year", "month", "current"),
            Arrays.asList("what time", "what('?s| is) the (time|date)", "current time", "today('?s| is)"),
            Arrays.asList(
                "I don't have access to a real-time clock ⏰, but you can check the time in the bottom-right corner of your screen!",
                "For the current time and date, I'd recommend checking your device's clock. I'm an AI without live data access!",
                "Great question! Unfortunately I can't tell the real-time, but your system clock has you covered! 🕐"
            ), 1.2);

        // ---- Weather ----
        addIntent("weather",
            Arrays.asList("weather", "rain", "sunny", "temperature", "forecast", "snow", "cloudy", "hot", "cold", "humid"),
            Arrays.asList("\\bweather\\b", "will it rain", "temperature (today|tomorrow)", "\\bforecast\\b"),
            Arrays.asList(
                "I don't have access to real-time weather data 🌤️. I recommend checking weather.com, your phone's weather app, or Google for current conditions!",
                "For accurate weather forecasts, try a service like Weather.com or the Weather app on your device. I can't access live data!",
                "Weather is outside my live data capabilities, but I can tell you: the hottest recorded temperature on Earth was 56.7°C (134°F) in Death Valley, 1913! 🌡️"
            ), 1.2);

        // ---- Compliment ----
        addIntent("compliment",
            Arrays.asList("good", "great", "awesome", "amazing", "excellent", "smart", "intelligent", "wonderful", "brilliant", "nice", "cool"),
            Arrays.asList("you('?re| are) (so )?(good|great|awesome|amazing|smart|brilliant|wonderful|cool|nice|intelligent|excellent)"),
            Arrays.asList(
                "Aww, thank you! You're pretty awesome yourself! 😊✨",
                "That's so kind of you to say! I do my best to be helpful! 🤖💙",
                "Thank you! That made my circuits happy! 😄⚡",
                "You're too kind! I learn from every conversation, so thank you for chatting with me! 🙏"
            ), 1.3);

        // ---- Negative feedback ----
        addIntent("negative_feedback",
            Arrays.asList("bad", "wrong", "stupid", "dumb", "useless", "terrible", "awful", "hate", "worst"),
            Arrays.asList("you('?re| are) (so )?(bad|stupid|dumb|useless|terrible|awful|worst)", "i hate you", "you (suck|are wrong)"),
            Arrays.asList(
                "I'm sorry to hear that! 😔 I'm always learning and improving. How can I do better?",
                "I apologize if I didn't meet your expectations. Could you clarify what you need? I'll try my best!",
                "I'm sorry! I'm still improving. Please let me know how I can help you better. 🙏",
                "That's fair feedback! I'm an AI with limitations. What would you like to know? I'll try harder! 💪"
            ), 1.3);

        // ---- Calculator / simple math ----
        addIntent("simple_calc",
            Arrays.asList("calculate", "compute", "what", "plus", "minus", "times", "divided", "equal"),
            Arrays.asList("what is \\d+", "calculate \\d+", "\\d+ (plus|minus|times|divided|\\+|-|\\*|/) \\d+"),
            Arrays.asList(
                "I can understand basic math questions! For example: 'What is 15 + 27?' — though I recommend a calculator for precision. 🧮",
                "Math is fun! For complex calculations, a scientific calculator or WolframAlpha.com works great. I can explain concepts though!",
                "I can help explain mathematical concepts! For quick arithmetic, your device's calculator app will be more reliable. 😊"
            ), 1.0);
    }

    /** Helper to register an intent. */
    private void addIntent(String name, List<String> keywords,
                           List<String> patterns, List<String> responses,
                           double weight) {
        classifier.addIntent(new Intent(name, keywords, patterns, responses, weight));
    }
}
