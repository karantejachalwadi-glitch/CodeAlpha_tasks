package com.chatbot.gui;

import com.chatbot.core.ChatbotEngine;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * ChatbotGUI – a modern, dark-themed Swing chat interface.
 *
 * Features:
 *  - Gradient header with animated bot status indicator
 *  - Scrollable chat panel with distinct user / bot bubbles
 *  - Typing indicator animation (3 dots)
 *  - Smooth auto-scroll
 *  - Responsive input bar with Send button + Enter key support
 */
public class ChatbotGUI extends JFrame {

    // ---- Colors ----
    private static final Color BG_DARK        = new Color(15, 17, 26);
    private static final Color BG_PANEL       = new Color(22, 26, 40);
    private static final Color BG_INPUT       = new Color(30, 35, 54);
    private static final Color ACCENT_PURPLE  = new Color(108, 92, 231);
    private static final Color ACCENT_BLUE    = new Color(74, 144, 226);
    private static final Color USER_BUBBLE    = new Color(108, 92, 231);
    private static final Color BOT_BUBBLE     = new Color(35, 41, 64);
    private static final Color TEXT_PRIMARY   = new Color(236, 240, 255);
    private static final Color TEXT_SECONDARY = new Color(140, 150, 180);
    private static final Color SEND_GREEN     = new Color(0, 210, 145);
    private static final Color BORDER_COLOR   = new Color(50, 60, 90);

    // ---- Fonts ----
    private static final Font FONT_MSG   = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOLD  = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 17);

    // ---- Core components ----
    private final ChatbotEngine engine = new ChatbotEngine();
    private JPanel  chatPanel;
    private JScrollPane scrollPane;
    private JTextArea inputField;
    private JButton sendButton;
    private JLabel  typingLabel;
    private Timer   typingTimer;
    private int     dotCount = 0;

    // ---- Constructor ----
    public ChatbotGUI() {
        setTitle("AI Chatbot — Powered by NLP");
        setSize(780, 620);
        setMinimumSize(new Dimension(550, 450));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        buildUI();
        showWelcome();
        setVisible(true);
    }

    // =========================================================
    //  UI Construction
    // =========================================================
    private void buildUI() {
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildChatArea(), BorderLayout.CENTER);
        add(buildInputBar(), BorderLayout.SOUTH);
    }

    /** Gradient header panel with bot name and status indicator. */
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(70, 50, 160),
                                                      getWidth(), 0, new Color(40, 100, 200));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setPreferredSize(new Dimension(0, 70));
        header.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Bot avatar + name
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        // Animated avatar icon
        JLabel avatar = new JLabel("🤖") {
            { setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28)); }
        };
        left.add(avatar);

        JPanel namePanel = new JPanel(new GridLayout(2, 1, 0, 2));
        namePanel.setOpaque(false);

        JLabel nameLabel = new JLabel("AI Chatbot");
        nameLabel.setFont(FONT_TITLE);
        nameLabel.setForeground(Color.WHITE);

        // Status row
        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        statusRow.setOpaque(false);
        JLabel dot = new JLabel("●");
        dot.setForeground(SEND_GREEN);
        dot.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        JLabel status = new JLabel("Online · NLP Engine Active");
        status.setFont(FONT_SMALL);
        status.setForeground(new Color(200, 220, 255));
        statusRow.add(dot);
        statusRow.add(status);

        namePanel.add(nameLabel);
        namePanel.add(statusRow);
        left.add(namePanel);
        header.add(left, BorderLayout.WEST);

        // Right side: info label
        JLabel info = new JLabel("CodeAlpha Internship Project");
        info.setFont(FONT_SMALL);
        info.setForeground(new Color(180, 200, 255));
        info.setHorizontalAlignment(SwingConstants.RIGHT);
        header.add(info, BorderLayout.EAST);

        return header;
    }

    /** Scrollable chat message panel. */
    private JScrollPane buildChatArea() {
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(BG_DARK);
        chatPanel.setBorder(new EmptyBorder(16, 16, 16, 16));

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, BORDER_COLOR));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBackground(BG_PANEL);
        scrollPane.setBackground(BG_DARK);

        // Style the scrollbar
        scrollPane.getVerticalScrollBar().setUI(
            new javax.swing.plaf.basic.BasicScrollBarUI() {
                @Override protected void configureScrollBarColors() {
                    this.thumbColor   = new Color(80, 90, 130);
                    this.trackColor   = BG_PANEL;
                }
                @Override protected JButton createDecreaseButton(int o) { return zeroButton(); }
                @Override protected JButton createIncreaseButton(int o) { return zeroButton(); }
                private JButton zeroButton() {
                    JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b;
                }
            });

        return scrollPane;
    }

    /** Bottom input bar. */
    private JPanel buildInputBar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setBackground(BG_PANEL);
        bar.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Typing indicator (shows above input when bot is "typing")
        typingLabel = new JLabel(" ");
        typingLabel.setFont(FONT_SMALL);
        typingLabel.setForeground(TEXT_SECONDARY);

        // Text area
        inputField = new JTextArea(2, 30);
        inputField.setFont(FONT_MSG);
        inputField.setBackground(BG_INPUT);
        inputField.setForeground(TEXT_PRIMARY);
        inputField.setCaretColor(ACCENT_BLUE);
        inputField.setBorder(new EmptyBorder(8, 12, 8, 12));
        inputField.setLineWrap(true);
        inputField.setWrapStyleWord(true);
        inputField.setOpaque(true);

        // Placeholder text hint
        inputField.setText("Type a message...");
        inputField.setForeground(TEXT_SECONDARY);
        inputField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (inputField.getText().equals("Type a message...")) {
                    inputField.setText("");
                    inputField.setForeground(TEXT_PRIMARY);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (inputField.getText().isBlank()) {
                    inputField.setText("Type a message...");
                    inputField.setForeground(TEXT_SECONDARY);
                }
            }
        });

        // Enter = send; Shift+Enter = newline
        inputField.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    e.consume();
                    sendMessage();
                }
            }
        });

        JScrollPane inputScroll = new JScrollPane(inputField);
        inputScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        inputScroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));

        // Send button
        sendButton = new JButton("Send ▶") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, ACCENT_PURPLE,
                                                      0, getHeight(), ACCENT_BLUE);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        sendButton.setFont(FONT_BOLD);
        sendButton.setForeground(Color.WHITE);
        sendButton.setPreferredSize(new Dimension(100, 50));
        sendButton.setContentAreaFilled(false);
        sendButton.setBorderPainted(false);
        sendButton.setFocusPainted(false);
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sendButton.addActionListener(e -> sendMessage());
        sendButton.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { sendButton.setForeground(new Color(255, 230, 255)); }
            @Override public void mouseExited(MouseEvent e)  { sendButton.setForeground(Color.WHITE); }
        });

        JPanel inputWrapper = new JPanel(new BorderLayout(0, 4));
        inputWrapper.setOpaque(false);
        inputWrapper.add(typingLabel, BorderLayout.NORTH);
        inputWrapper.add(inputScroll, BorderLayout.CENTER);

        bar.add(inputWrapper, BorderLayout.CENTER);
        bar.add(sendButton, BorderLayout.EAST);

        return bar;
    }

    // =========================================================
    //  Message Handling
    // =========================================================

    private void showWelcome() {
        String welcome = "👋 Hello! I'm your AI Chatbot powered by NLP.\n\n" +
                         "I can help you with topics like:\n" +
                         "💻 Programming (Java, Python, AI, Web Dev)\n" +
                         "🔬 Science (Physics, Chemistry, Biology)\n" +
                         "🌍 History, Geography & General Knowledge\n" +
                         "🏥 Health, Technology, Space & more!\n\n" +
                         "Type 'What can you do?' to see all my capabilities.\n" +
                         "What's on your mind? 😊";
        appendBotBubble(welcome);
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty() || text.equals("Type a message...")) return;

        // Display user bubble
        appendUserBubble(text);

        // Clear input
        inputField.setText("");
        inputField.setForeground(TEXT_PRIMARY);
        inputField.requestFocus();

        // Disable input while bot "thinks"
        setInputEnabled(false);
        startTypingAnimation();

        // Simulate response delay for realism
        String userText = text;
        Timer responseTimer = new Timer(600 + (int)(Math.random() * 700), e -> {
            stopTypingAnimation();
            String response = engine.respond(userText);
            appendBotBubble(response);
            setInputEnabled(true);
            inputField.requestFocus();
        });
        responseTimer.setRepeats(false);
        responseTimer.start();
    }

    // =========================================================
    //  Chat Bubble Rendering
    // =========================================================

    private void appendUserBubble(String message) {
        JPanel bubble = createBubble(message, true);
        chatPanel.add(bubble);
        chatPanel.add(Box.createVerticalStrut(8));
        chatPanel.revalidate();
        scrollToBottom();
    }

    private void appendBotBubble(String message) {
        JPanel bubble = createBubble(message, false);
        chatPanel.add(bubble);
        chatPanel.add(Box.createVerticalStrut(8));
        chatPanel.revalidate();
        scrollToBottom();
    }

    private JPanel createBubble(String message, boolean isUser) {
        // Outer row panel (determines alignment)
        JPanel row = new JPanel(new FlowLayout(isUser ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // Bubble itself
        JTextArea textArea = new JTextArea(message);
        textArea.setFont(FONT_MSG);
        textArea.setForeground(TEXT_PRIMARY);
        textArea.setBackground(isUser ? USER_BUBBLE : BOT_BUBBLE);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(new EmptyBorder(10, 14, 10, 14));
        textArea.setOpaque(true);

        // Wrap in a panel with rounded appearance
        JPanel bubblePanel = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isUser) {
                    GradientPaint gp = new GradientPaint(0, 0, USER_BUBBLE,
                                                          getWidth(), getHeight(),
                                                          new Color(74, 60, 200));
                    g2.setPaint(gp);
                } else {
                    g2.setColor(BOT_BUBBLE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
            }
        };
        bubblePanel.setOpaque(false);
        bubblePanel.add(textArea, BorderLayout.CENTER);
        textArea.setBackground(new Color(0,0,0,0));
        textArea.setOpaque(false);

        // Max width for bubble
        int maxWidth = (int)(getWidth() * 0.65);
        if (maxWidth < 200) maxWidth = 200;
        bubblePanel.setMaximumSize(new Dimension(maxWidth, Integer.MAX_VALUE));

        // Timestamp
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(FONT_SMALL);
        timeLabel.setForeground(TEXT_SECONDARY);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);

        if (!isUser) {
            // Bot: icon + bubble side by side
            JPanel bRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            bRow.setOpaque(false);
            JLabel botIcon = new JLabel("🤖");
            botIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            bRow.add(botIcon);
            bRow.add(bubblePanel);
            container.add(bRow);
            JPanel tRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 0));
            tRow.setOpaque(false);
            tRow.add(timeLabel);
            container.add(tRow);
        } else {
            JPanel bRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            bRow.setOpaque(false);
            bRow.add(bubblePanel);
            JLabel userIcon = new JLabel("👤");
            userIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            bRow.add(userIcon);
            container.add(bRow);
            JPanel tRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 40, 0));
            tRow.setOpaque(false);
            tRow.add(timeLabel);
            container.add(tRow);
        }

        row.add(container);
        return row;
    }

    // =========================================================
    //  Typing indicator
    // =========================================================
    private void startTypingAnimation() {
        dotCount = 0;
        typingTimer = new Timer(400, e -> {
            dotCount = (dotCount % 3) + 1;
            typingLabel.setText("AI is typing" + ".".repeat(dotCount));
        });
        typingTimer.start();
    }

    private void stopTypingAnimation() {
        if (typingTimer != null) typingTimer.stop();
        typingLabel.setText(" ");
    }

    // =========================================================
    //  Utilities
    // =========================================================
    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    private void setInputEnabled(boolean enabled) {
        inputField.setEnabled(enabled);
        sendButton.setEnabled(enabled);
    }
}
