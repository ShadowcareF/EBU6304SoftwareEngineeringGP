package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.Transaction;
import model.TransactionManager;
import model.DeepSeekAPI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import model.SessionManager;
import model.UserManager;

/**
 * BillingView - The billing and subscriptions screen with transaction management.
 * This class displays billing information and allows transaction data entry.
 */
public class BillingView extends JFrame {
    
    // UI Components
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    // Sidebar items
    private JPanel accountPanel;
    private JPanel billingPanel;
    private JPanel budgetPanel;
    private JPanel helpCenterPanel;
    private JPanel financialAdvisorPanel;
    private JPanel manualEntryPanel;
    private JPanel importPanel;
    
    // Billing sub-items container
    private JPanel billingSubItems;
    private boolean billingExpanded = true;
    
    // Transactions panel
    private JPanel transactionsPanel;
    
    // Static shared styling
    private static final Color PRIMARY_BLUE = new Color(52, 152, 219);
    private static final Color LIGHT_GRAY = new Color(245, 245, 245);
    private static final Color DARK_GRAY = new Color(100, 100, 100);
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 22);
    private static final Font SUBHEADER_FONT = new Font("Arial", Font.BOLD, 18);
    private static final Font CONTENT_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font SIDEBAR_FONT = new Font("Arial", Font.PLAIN, 14);
    
    /**
     * Constructor for the BillingView
     */
    public BillingView() {
        setTitle("Billing & Subscriptions");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        createHeader();
        createSidebar();
        
        // Create content with card layout for multiple views
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);
        
        createBillingContent();
        createManualEntryPanel();
        createImportPanel();
        
        // Show billing panel by default
        cardLayout.show(contentPanel, "billing");
        setActiveSidebarItem(null);
        
        add(contentPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setVisible(true);
        
        // Start session monitoring
        SessionManager.getInstance().startSession(this);
        
        refreshTransactionsDisplay();
    }
    
    /**
     * Creates the header panel with title and logout button
     */
    private void createHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_BLUE);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 50));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Billing & Data Entry");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        // Create logout button in the top-right corner
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(231, 76, 60)); // Red color
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setFocusPainted(false);
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);
        
        // Create a panel to hold the logout button with some margin
        JPanel logoutPanel = new JPanel();
        logoutPanel.setBackground(PRIMARY_BLUE);
        logoutPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        logoutPanel.add(logoutButton);
        
        // Add action listener to logout button
        logoutButton.addActionListener(e -> {
            // Stop session monitoring
            SessionManager.getInstance().stopSession();
            
            // Log out the user
            UserManager.getInstance().logout();
            
            dispose(); // Close current window
            new LoginView(); // Return to login screen
        });
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(logoutPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
    }
    
    /**
     * Creates the sidebar navigation panel
     */
    private void createSidebar() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setPreferredSize(new Dimension(200, getHeight()));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        
        // Create sidebar items
        accountPanel = createSidebarItem("Account", false, false);
        billingPanel = createSidebarItem("Billing & Subscriptions", false, true);
        budgetPanel = createSidebarItem("Budget", false, false);
        helpCenterPanel = createSidebarItem("Help Center", false, false);
        financialAdvisorPanel = createSidebarItem("Financial Advisor", false, false);
        manualEntryPanel = createSidebarItem("Manual Entry", true, false);
        importPanel = createSidebarItem("Import Transactions", true, false);
        
        // Create Billing sub-items container
        billingSubItems = new JPanel();
        billingSubItems.setLayout(new BoxLayout(billingSubItems, BoxLayout.Y_AXIS));
        billingSubItems.setBackground(Color.WHITE);
        billingSubItems.setVisible(billingExpanded);
        
        // Create Billing sub-items
        JPanel billingOverviewPanel = createSidebarItem("Overview", true, false);
        JPanel manualEntryItemPanel = createSidebarItem("Manual Entry", true, false);
        JPanel importItemPanel = createSidebarItem("Import Transactions", true, false);
        
        // Add click listeners for main navigation
        accountPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                dispose();
                new AccountView();
            }
        });
        
        billingPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                toggleBillingSubItems();
            }
        });
        
        budgetPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                dispose();
                new BudgetView();
            }
        });
        
        helpCenterPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                dispose();
                new HelpCenterView();
            }
        });
        
        financialAdvisorPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                dispose();
                new FinancialAdvisorView();
            }
        });
        
        // Add click listeners for Billing sub-panels
        billingOverviewPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                cardLayout.show(contentPanel, "billing");
                setActiveSidebarItem(billingOverviewPanel);
            }
        });
        
        manualEntryItemPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                cardLayout.show(contentPanel, "manualEntry");
                setActiveSidebarItem(manualEntryItemPanel);
            }
        });
        
        importItemPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                cardLayout.show(contentPanel, "import");
                setActiveSidebarItem(importItemPanel);
            }
        });
        
        // Add sub-items to container
        billingSubItems.add(billingOverviewPanel);
        billingSubItems.add(Box.createVerticalStrut(5));
        billingSubItems.add(manualEntryItemPanel);
        billingSubItems.add(Box.createVerticalStrut(5));
        billingSubItems.add(importItemPanel);
        
        // Add sidebar items to panel
        sidebarPanel.add(accountPanel);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(billingPanel);
        sidebarPanel.add(billingSubItems);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(budgetPanel);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(helpCenterPanel);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(financialAdvisorPanel);
        
        // Add a glue component to push everything to the top
        sidebarPanel.add(Box.createVerticalGlue());
        
        // Add sidebar to main frame
        add(sidebarPanel, BorderLayout.WEST);
    }
    
    /**
     * Toggle the visibility of Billing sub-items
     */
    private void toggleBillingSubItems() {
        billingExpanded = !billingExpanded;
        billingSubItems.setVisible(billingExpanded);
        
        // Add arrow indicator to Billing item
        JLabel billingLabel = (JLabel) ((JPanel) billingPanel.getComponent(0)).getComponent(0);
        billingLabel.setText("Billing & Subscriptions " + (billingExpanded ? "▼" : "▶"));
        
        // Refresh the UI
        billingPanel.revalidate();
        billingPanel.repaint();
    }
    
    /**
     * Sets the active sidebar item by changing its background color
     * @param activePanel The panel to set as active
     */
    private void setActiveSidebarItem(JPanel activePanel) {
        // Collect all sub-items
        Component[] subItems = billingSubItems.getComponents();
        
        // Reset background of all items
        for (Component comp : subItems) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                panel.setBackground(Color.WHITE);
                if (panel.getComponentCount() > 0 && panel.getComponent(0) instanceof JPanel) {
                    panel.getComponent(0).setBackground(Color.WHITE);
                }
            }
        }
        
        // Set active panel if not null
        if (activePanel != null) {
            activePanel.setBackground(LIGHT_GRAY);
            if (activePanel.getComponentCount() > 0 && activePanel.getComponent(0) instanceof JPanel) {
                activePanel.getComponent(0).setBackground(LIGHT_GRAY);
            }
        }
    }
    
    /**
     * Creates a sidebar item
     * @param text The text for the sidebar item
     * @param isSubItem Whether this is a sub-item (indented)
     * @param isActive Whether this item is currently active
     * @return The created panel
     */
    private JPanel createSidebarItem(String text, boolean isSubItem, boolean isActive) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BorderLayout());
        itemPanel.setBackground(isActive ? LIGHT_GRAY : Color.WHITE);
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        // Inner panel to hold the label for proper alignment
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelPanel.setBackground(isActive ? LIGHT_GRAY : Color.WHITE);
        
        String displayText = text;
        if (text.equals("Billing & Subscriptions")) {
            displayText = text + " " + (billingExpanded ? "▼" : "▶");
        }
        
        JLabel textLabel = new JLabel(displayText);
        textLabel.setFont(SIDEBAR_FONT);
        textLabel.setForeground(DARK_GRAY);
        
        if (isSubItem) {
            labelPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 5));
        } else {
            labelPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        }
        
        labelPanel.add(textLabel);
        itemPanel.add(labelPanel, BorderLayout.CENTER);
        
        // Add hover effect if not active
        if (!isActive) {
            itemPanel.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    if (itemPanel.getBackground() != LIGHT_GRAY) {
                        itemPanel.setBackground(new Color(250, 250, 250));
                        labelPanel.setBackground(new Color(250, 250, 250));
                    }
                }
                
                public void mouseExited(MouseEvent evt) {
                    if (itemPanel.getBackground() != LIGHT_GRAY) {
                        itemPanel.setBackground(Color.WHITE);
                        labelPanel.setBackground(Color.WHITE);
                    }
                }
            });
        }
        
        return itemPanel;
    }
    
    /**
     * Creates the billing content panel
     */
    private void createBillingContent() {
        JPanel billingPanel = new JPanel();
        billingPanel.setBackground(Color.WHITE);
        billingPanel.setLayout(new BorderLayout(10, 10));
        billingPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(Color.WHITE);
        
        // Subscription section
        JLabel subscriptionLabel = new JLabel("Your Subscription");
        subscriptionLabel.setFont(SUBHEADER_FONT);
        subscriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel planInfoPanel = new JPanel();
        planInfoPanel.setLayout(new BorderLayout());
        planInfoPanel.setBackground(LIGHT_GRAY);
        planInfoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        planInfoPanel.setMaximumSize(new Dimension(800, 80));
        planInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel planNameLabel = new JLabel("Finance Tracker Pro");
        planNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JLabel planDetailsLabel = new JLabel("<html>Subscription active • Next billing date: 2024/01/01</html>");
        planDetailsLabel.setFont(CONTENT_FONT);
        
        planInfoPanel.add(planNameLabel, BorderLayout.NORTH);
        planInfoPanel.add(planDetailsLabel, BorderLayout.CENTER);
        
        // Recent transactions title
        JLabel transactionsLabel = new JLabel("Recent Transactions");
        transactionsLabel.setFont(SUBHEADER_FONT);
        transactionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        transactionsLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        // Add some sample transactions using lighter weight components
        transactionsPanel = new JPanel();
        transactionsPanel.setLayout(new BoxLayout(transactionsPanel, BoxLayout.Y_AXIS));
        transactionsPanel.setBackground(Color.WHITE);
        transactionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add a few sample transactions with YYYY/MM/DD format
        addTransactionItem(transactionsPanel, "2024/01/15", "Grocery Shopping", "Food", 85.43);
        addTransactionItem(transactionsPanel, "2024/01/12", "Monthly Rent", "Housing", 1200.00);
        addTransactionItem(transactionsPanel, "2024/01/10", "Gas Station", "Transportation", 45.75);
        addTransactionItem(transactionsPanel, "2024/01/05", "Online Streaming", "Entertainment", 14.99);
        
        // Data entry instructions
        JLabel dataEntryLabel = new JLabel("Transaction Data Entry");
        dataEntryLabel.setFont(SUBHEADER_FONT);
        dataEntryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dataEntryLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        JPanel instructionsPanel = new JPanel();
        instructionsPanel.setLayout(new BoxLayout(instructionsPanel, BoxLayout.Y_AXIS));
        instructionsPanel.setBackground(LIGHT_GRAY);
        instructionsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        instructionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        instructionsPanel.setMaximumSize(new Dimension(800, 150)); // 限制最大尺寸，减少空白区域
        
        JLabel instructionsText = new JLabel("<html>" +
                "<p>You can add transaction data in two ways:</p>" +
                "<p>1. <b>Manual Entry</b>: Add individual transactions manually</p>" +
                "<p>2. <b>Import Transactions</b>: Import CSV files from your bank</p>" +
                "</html>");
        instructionsText.setFont(CONTENT_FONT);
        instructionsText.setAlignmentX(Component.LEFT_ALIGNMENT); // 确保文本左对齐
        
        // 创建一个包装面板，确保按钮左对齐
        JPanel buttonsWrapper = new JPanel();
        buttonsWrapper.setLayout(new BoxLayout(buttonsWrapper, BoxLayout.X_AXIS));
        buttonsWrapper.setBackground(LIGHT_GRAY);
        buttonsWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // 设置FlowLayout的水平和垂直间距为0
        buttonsPanel.setBackground(LIGHT_GRAY);
        
        JButton manualButton = new JButton("Manual Entry");
        manualButton.setFont(CONTENT_FONT);
        manualButton.setFocusPainted(false);
        manualButton.addActionListener(e -> cardLayout.show(contentPanel, "manualEntry"));
        
        JButton importButton = new JButton("Import Transactions");
        importButton.setFont(CONTENT_FONT);
        importButton.setFocusPainted(false);
        importButton.addActionListener(e -> cardLayout.show(contentPanel, "import"));
        
        buttonsPanel.add(manualButton);
        buttonsPanel.add(importButton);
        
        buttonsWrapper.add(buttonsPanel);
        buttonsWrapper.add(Box.createHorizontalGlue()); // 添加水平胶水，确保按钮左对齐
        
        instructionsPanel.add(instructionsText);
        instructionsPanel.add(Box.createVerticalStrut(10));
        instructionsPanel.add(buttonsWrapper);
        
        // Add everything to the main content panel
        mainContent.add(subscriptionLabel);
        mainContent.add(Box.createVerticalStrut(10));
        mainContent.add(planInfoPanel);
        mainContent.add(Box.createVerticalStrut(10));
        mainContent.add(transactionsLabel);
        mainContent.add(transactionsPanel);
        mainContent.add(dataEntryLabel);
        mainContent.add(instructionsPanel);
        
        // Add a scroll pane for the main content
        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        billingPanel.add(scrollPane, BorderLayout.CENTER);
        
        contentPanel.add(billingPanel, "billing");
    }
    
    /**
     * Creates the manual transaction entry panel
     */
    private void createManualEntryPanel() {
        JPanel manualEntryPanel = new JPanel();
        manualEntryPanel.setBackground(Color.WHITE);
        manualEntryPanel.setLayout(new BorderLayout(10, 10));
        manualEntryPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel headerLabel = new JLabel("Manual Transaction Entry");
        headerLabel.setFont(SUBHEADER_FONT);
        
        // Create the form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Date - Updated format for Chinese user preference (YYYY/MM/DD)
        JLabel dateLabel = new JLabel("Date:");
        JTextField dateField = new JTextField(10);
        dateField.setText("YYYY/MM/DD");
        
        // Description
        JLabel descLabel = new JLabel("Description:");
        JTextField descField = new JTextField(20);
        
        // Amount - Clarify meaning
        JLabel amountLabel = new JLabel("Expense Amount ($):");
        JTextField amountField = new JTextField(10);
        
        // Category
        JLabel categoryLabel = new JLabel("Category:");
        // Add AI categorization option
        String[] categories = {"Select Category", "AI Categorize", "Housing", "Food", "Transportation", "Entertainment", "Shopping", "Utilities", "Chinese New Year", "Education", "Medical", "Travel", "Other"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);
        
        // AI Suggestion section - Improved
        JPanel aiSuggestionPanel = new JPanel();
        aiSuggestionPanel.setLayout(new BorderLayout());
        aiSuggestionPanel.setBackground(new Color(240, 248, 255)); // Light blue background
        aiSuggestionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel aiTitleLabel = new JLabel("AI Assistant");
        aiTitleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel aiSuggestion = new JLabel("Enter a transaction description for AI categorization");
        aiSuggestion.setForeground(Color.GRAY);
        
        // Create a panel for the AI title with better layout
        JPanel aiTitlePanel = new JPanel();
        aiTitlePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        aiTitlePanel.setBackground(new Color(240, 248, 255));
        aiTitlePanel.add(aiTitleLabel);
        
        // Create a panel for the AI suggestion text
        JPanel aiSuggestionTextPanel = new JPanel();
        aiSuggestionTextPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        aiSuggestionTextPanel.setBackground(new Color(240, 248, 255));
        aiSuggestionTextPanel.add(aiSuggestion);
        
        JPanel aiContentPanel = new JPanel();
        aiContentPanel.setLayout(new BoxLayout(aiContentPanel, BoxLayout.Y_AXIS));
        aiContentPanel.setBackground(new Color(240, 248, 255));
        aiContentPanel.add(aiTitlePanel);
        aiContentPanel.add(Box.createVerticalStrut(5));
        aiContentPanel.add(aiSuggestionTextPanel);
        
        aiSuggestionPanel.add(aiContentPanel, BorderLayout.CENTER);
        
        // Submit button
        JButton submitButton = new JButton("Add Transaction");
        submitButton.setBackground(PRIMARY_BLUE);
        submitButton.setForeground(Color.WHITE);
        submitButton.setOpaque(true);
        submitButton.setBorderPainted(false);
        submitButton.setFocusPainted(false);
        
        // Add action listener to generate AI suggestion when fields are filled or when "AI Categorize" is selected
        ActionListener suggestListener = e -> {
            if (!descField.getText().isEmpty()) {
                String description = descField.getText().toLowerCase();
                
                // Check if "AI Categorize" is selected
                if (categoryCombo.getSelectedItem().equals("AI Categorize")) {
                    // Call to Deepseek API would go here - for now simulating with basic logic
                    aiSuggestion.setText("Analyzing transaction with Deepseek AI...");
                    
                    // Simulate API call with improved categorization logic
                    // In a real implementation, this would be replaced with an actual API call
                    simulateDeepseekCategorization(description, categoryCombo, aiSuggestion, categories);
                } else if (description.contains("grocery") || description.contains("food") || description.contains("restaurant")) {
                    aiSuggestion.setText("AI suggested category: Food");
                    categoryCombo.setSelectedItem("Food");
                } else if (description.contains("gas") || description.contains("uber") || description.contains("taxi")) {
                    aiSuggestion.setText("AI suggested category: Transportation");
                    categoryCombo.setSelectedItem("Transportation");
                } else if (description.contains("rent") || description.contains("mortgage")) {
                    aiSuggestion.setText("AI suggested category: Housing");
                    categoryCombo.setSelectedItem("Housing");
                } else if (description.contains("movie") || description.contains("netflix") || description.contains("game")) {
                    aiSuggestion.setText("AI suggested category: Entertainment");
                    categoryCombo.setSelectedItem("Entertainment");
                } else if (description.contains("hongbao") || description.contains("gift") || description.contains("new year")) {
                    aiSuggestion.setText("AI suggested category: Chinese New Year");
                    categoryCombo.setSelectedItem("Chinese New Year");
                } else if (categoryCombo.getSelectedItem().equals("Select Category")) {
                    aiSuggestion.setText("Please select a category or use AI categorization");
                }
            }
        };
        
        // Add listener to detect AI Categorize selection
        categoryCombo.addActionListener(e -> {
            if (categoryCombo.getSelectedItem().equals("AI Categorize") && !descField.getText().isEmpty()) {
                suggestListener.actionPerformed(null);
            }
        });
        
        descField.addActionListener(suggestListener);
        descField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                suggestListener.actionPerformed(null);
            }
        });
        
        // Add components to the form panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(dateLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(dateField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(descLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(descField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(amountLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(amountField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(categoryLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(categoryCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(aiSuggestionPanel, gbc);
        
        // Create a button panel to center the submit button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(submitButton);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        formPanel.add(buttonPanel, gbc);
        
        // Create a panel to hold instructions and the form
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(headerLabel, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);
        
        // Enhanced instructions
        JLabel instructionsLabel = new JLabel("<html><p>Enter your transaction details. Our AI will suggest a category based on the description.</p>" +
                "<p>Select \"AI Categorize\" from the dropdown for intelligent categorization.</p></html>");
        instructionsLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        contentPanel.add(instructionsLabel, BorderLayout.SOUTH);
        
        manualEntryPanel.add(contentPanel, BorderLayout.NORTH);
        
        this.contentPanel.add(manualEntryPanel, "manualEntry");
        
        submitButton.addActionListener(e -> {
            try {
                // Update date format to YYYY/MM/DD
                LocalDate date = LocalDate.parse(dateField.getText(), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                String description = descField.getText();
                String category = (String) categoryCombo.getSelectedItem();
                
                // Validate category selection
                if (category.equals("Select Category") || category.equals("AI Categorize")) {
                    JOptionPane.showMessageDialog(this, 
                        "Please select a specific category for the transaction.", 
                        "Invalid Category", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                double amount = Double.parseDouble(amountField.getText());
                
                Transaction transaction = new Transaction(date, description, category, amount);
                TransactionManager.saveTransaction(transaction);
                
                // Clear fields
                dateField.setText("YYYY/MM/DD");
                descField.setText("");
                amountField.setText("");
                categoryCombo.setSelectedIndex(0);
                
                // Show success message
                JOptionPane.showMessageDialog(this, 
                    "Transaction saved successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
                // Refresh the transactions display
                refreshTransactionsDisplay();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error saving transaction: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    /**
     * Uses DeepseekAPI to categorize transactions
     */
    private void simulateDeepseekCategorization(String description, JComboBox<String> categoryCombo, JLabel aiSuggestion, String[] categories) {
        // Create a list of valid categories (excluding "Select Category" and "AI Categorize")
        String[] validCategories = new String[categories.length - 2];
        System.arraycopy(categories, 2, validCategories, 0, categories.length - 2);
        
        // Update UI to show processing state
        SwingUtilities.invokeLater(() -> {
            aiSuggestion.setText("Processing with Deepseek AI...");
        });
        
        // Call DeepseekAPI to categorize the transaction
        DeepSeekAPI.categorizeTransaction(description, validCategories)
            .thenAccept(result -> {
                SwingUtilities.invokeLater(() -> {
                    // Verify the result is one of our predefined categories
                    boolean isValidCategory = false;
                    for (String category : validCategories) {
                        if (category.equals(result)) {
                            isValidCategory = true;
                            break;
                        }
                    }
                    
                    // If somehow we got an invalid category, default to "Other"
                    String finalCategory = isValidCategory ? result : "Other";
                    
                    // Update the UI with the result
                    aiSuggestion.setText("Deepseek AI categorized as: " + finalCategory);
                    categoryCombo.setSelectedItem(finalCategory);
                });
            })
            .exceptionally(throwable -> {
                SwingUtilities.invokeLater(() -> {
                    aiSuggestion.setText("Error during AI categorization. Please try again.");
                    categoryCombo.setSelectedItem("Other");
                });
                return null;
            });
    }
    
    /**
     * Creates the import transactions panel
     */
    private void createImportPanel() {
        JPanel importPanel = new JPanel();
        importPanel.setBackground(Color.WHITE);
        importPanel.setLayout(new BorderLayout(10, 10));
        importPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel headerLabel = new JLabel("Import Transactions");
        headerLabel.setFont(SUBHEADER_FONT);
        
        // Instructions
        JLabel instructionsLabel = new JLabel("<html><p>Import transactions from your bank or financial institution.</p>" +
                "<p>Supported formats: CSV</p>" +
                "<p>Our AI will automatically categorize your transactions based on descriptions.</p></html>");
        instructionsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        // File selection
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.setBackground(Color.WHITE);
        
        JTextField filePathField = new JTextField(25);
        filePathField.setEditable(false);
        
        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            // 添加CSV文件过滤器，避免图标问题
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                @Override
                public boolean accept(java.io.File f) {
                    if (f.isDirectory()) {
                        return true;
                    }
                    String name = f.getName().toLowerCase();
                    return name.endsWith(".csv");
                }
                
                @Override
                public String getDescription() {
                    return "CSV Files (*.csv)";
                }
            });
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);
            
            try {
                int result = fileChooser.showOpenDialog(importPanel);
                if (result == JFileChooser.APPROVE_OPTION) {
                    filePathField.setText(fileChooser.getSelectedFile().getPath());
                }
            } catch (Exception ex) {
                // 出现错误时显示友好的错误消息
                JOptionPane.showMessageDialog(
                    importPanel, 
                    "无法打开文件选择器: " + ex.getMessage(),
                    "文件选择错误", 
                    JOptionPane.ERROR_MESSAGE
                );
                ex.printStackTrace();
            }
        });
        
        selectionPanel.add(filePathField);
        selectionPanel.add(browseButton);
        
        // Bank selection
        JPanel bankPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bankPanel.setBackground(Color.WHITE);
        
        JLabel bankLabel = new JLabel("Bank:");
        String[] banks = {"Select your bank", "Bank of China", "ICBC", "China Construction Bank", "Agricultural Bank of China", "Bank of Communications", "China Merchants Bank", "Other"};
        JComboBox<String> bankCombo = new JComboBox<>(banks);
        
        bankPanel.add(bankLabel);
        bankPanel.add(bankCombo);
        
        // AI settings panel
        JPanel aiPanel = new JPanel();
        aiPanel.setLayout(new BoxLayout(aiPanel, BoxLayout.Y_AXIS));
        aiPanel.setBackground(new Color(240, 248, 255)); // Light blue background
        aiPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        aiPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel aiSettingsLabel = new JLabel("AI Categorization Settings");
        aiSettingsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        aiSettingsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JCheckBox autoCategorizeBox = new JCheckBox("Automatically categorize transactions");
        autoCategorizeBox.setSelected(true);
        autoCategorizeBox.setBackground(new Color(240, 248, 255));
        autoCategorizeBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JCheckBox reviewBeforeSaveBox = new JCheckBox("Review transactions before saving");
        reviewBeforeSaveBox.setSelected(true);
        reviewBeforeSaveBox.setBackground(new Color(240, 248, 255));
        reviewBeforeSaveBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JCheckBox detectSeasonalBox = new JCheckBox("Detect seasonal spending patterns (e.g. Chinese New Year)");
        detectSeasonalBox.setSelected(true);
        detectSeasonalBox.setBackground(new Color(240, 248, 255));
        detectSeasonalBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        aiPanel.add(aiSettingsLabel);
        aiPanel.add(Box.createVerticalStrut(5));
        aiPanel.add(autoCategorizeBox);
        aiPanel.add(Box.createVerticalStrut(2));
        aiPanel.add(reviewBeforeSaveBox);
        aiPanel.add(Box.createVerticalStrut(2));
        aiPanel.add(detectSeasonalBox);
        
        // Import button
        JButton importButton = new JButton("Import Transactions");
        importButton.setBackground(PRIMARY_BLUE);
        importButton.setForeground(Color.WHITE);
        importButton.setOpaque(true);
        importButton.setBorderPainted(false);
        importButton.setFocusPainted(false);
        importButton.setEnabled(false);
        
        // Enable import button only when file is selected and bank is chosen
        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkFields();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkFields();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkFields();
            }
            
            private void checkFields() {
                importButton.setEnabled(!filePathField.getText().isEmpty() && 
                        bankCombo.getSelectedIndex() != 0);
            }
        };
        
        filePathField.getDocument().addDocumentListener(documentListener);
        bankCombo.addActionListener(e -> 
            importButton.setEnabled(!filePathField.getText().isEmpty() && 
                    bankCombo.getSelectedIndex() != 0)
        );
        
        // Create a panel to hold the components
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Add all components
        contentPanel.add(headerLabel);
        contentPanel.add(instructionsLabel);
        contentPanel.add(selectionPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(bankPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(aiPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(importButton);
        contentPanel.add(buttonPanel);
        
        // Add seasonal spending notice - enhanced for Chinese New Year
        JPanel seasonalPanel = new JPanel();
        seasonalPanel.setLayout(new BorderLayout());
        seasonalPanel.setBackground(new Color(255, 240, 240)); // Light red for Chinese New Year theme
        seasonalPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 153, 153), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel seasonalLabel = new JLabel("<html><b>Chinese New Year Spending Notice:</b> " +
                "Our AI has detected higher spending patterns typical for Chinese New Year season. " +
                "Based on your historical data, we predict you might spend approximately 35% more " +
                "in categories like Gifts, Food, and Travel. Would you like to adjust your budget for this period?</html>");
        
        JPanel buttonContainerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonContainerPanel.setBackground(new Color(255, 240, 240));
        
        JButton adjustButton = new JButton("Adjust Budget");
        adjustButton.setBackground(new Color(229, 57, 53)); // Red for Chinese New Year
        adjustButton.setForeground(Color.WHITE);
        adjustButton.setOpaque(true);
        adjustButton.setBorderPainted(false);
        
        JButton remindButton = new JButton("Remind Later");
        remindButton.setBackground(new Color(200, 200, 200));
        remindButton.setForeground(Color.BLACK);
        remindButton.setOpaque(true);
        remindButton.setBorderPainted(false);
        
        buttonContainerPanel.add(adjustButton);
        buttonContainerPanel.add(remindButton);
        
        seasonalPanel.add(seasonalLabel, BorderLayout.CENTER);
        seasonalPanel.add(buttonContainerPanel, BorderLayout.SOUTH);
        
        contentPanel.add(seasonalPanel);
        
        importPanel.add(contentPanel, BorderLayout.NORTH);
        
        // Preview area
        JLabel previewLabel = new JLabel("Transaction Preview (after import)");
        previewLabel.setFont(SUBHEADER_FONT);
        previewLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBackground(Color.WHITE);
        previewPanel.add(previewLabel, BorderLayout.NORTH);
        
        // Create a sample table to show imported data (empty for now)
        String[] columnNames = {"Date", "Description", "Category", "Amount", "AI Confidence"};
        Object[][] data = {};
        JTable previewTable = new JTable(data, columnNames);
        JScrollPane tableScroll = new JScrollPane(previewTable);
        tableScroll.setPreferredSize(new Dimension(600, 150));
        
        previewPanel.add(tableScroll, BorderLayout.CENTER);
        
        importPanel.add(previewPanel, BorderLayout.CENTER);
        
        this.contentPanel.add(importPanel, "import");
        
        importButton.addActionListener(e -> {
            String filePath = filePathField.getText();
            if (!filePath.isEmpty()) {
                try {
                    // 显示处理中的对话框
                    JDialog processingDialog = new JDialog(this, "导入中...", false);
                    JLabel processingLabel = new JLabel("正在导入交易数据，请稍候...");
                    processingLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
                    processingDialog.add(processingLabel);
                    processingDialog.pack();
                    processingDialog.setLocationRelativeTo(this);
                    
                    // 使用SwingWorker在后台线程中处理导入
                    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                        private String errorMessage = null;
                        
                        @Override
                        protected Void doInBackground() {
                            try {
                                TransactionManager.importFromCSV(filePath);
                            } catch (Exception ex) {
                                errorMessage = ex.getMessage();
                                ex.printStackTrace();
                            }
                            return null;
                        }
                        
                        @Override
                        protected void done() {
                            processingDialog.dispose();
                            
                            if (errorMessage == null) {
                                JOptionPane.showMessageDialog(
                                    BillingView.this, 
                                    "交易数据导入成功！", 
                                    "导入成功", 
                                    JOptionPane.INFORMATION_MESSAGE
                                );
                                refreshTransactionsDisplay();
                            } else {
                                JOptionPane.showMessageDialog(
                                    BillingView.this, 
                                    "导入交易时出错: " + errorMessage, 
                                    "导入错误", 
                                    JOptionPane.ERROR_MESSAGE
                                );
                            }
                        }
                    };
                    
                    worker.execute();
                    processingDialog.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                        this, 
                        "导入交易时出错: " + ex.getMessage(), 
                        "导入错误", 
                        JOptionPane.ERROR_MESSAGE
                    );
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "请先选择CSV文件",
                    "文件未选择",
                    JOptionPane.WARNING_MESSAGE
                );
            }
        });
    }
    
    /**
     * Helper method to add a transaction item to a panel
     */
    private void addTransactionItem(JPanel panel, String date, String description, String category, double amount) {
        JPanel transactionItem = new JPanel(new BorderLayout());
        transactionItem.setBackground(Color.WHITE);
        transactionItem.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LIGHT_GRAY));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(Color.WHITE);
        
        JLabel dateLabel = new JLabel(date);
        dateLabel.setFont(CONTENT_FONT);
        dateLabel.setPreferredSize(new Dimension(100, 20));
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(CONTENT_FONT);
        descLabel.setPreferredSize(new Dimension(150, 20));
        
        JLabel categoryLabel = new JLabel(category);
        categoryLabel.setFont(CONTENT_FONT);
        categoryLabel.setPreferredSize(new Dimension(100, 20));
        
        leftPanel.add(dateLabel);
        leftPanel.add(descLabel);
        leftPanel.add(categoryLabel);
        
        JLabel amountLabel = new JLabel(String.format("$%.2f", amount));
        amountLabel.setFont(CONTENT_FONT);
        amountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        transactionItem.add(leftPanel, BorderLayout.WEST);
        transactionItem.add(amountLabel, BorderLayout.EAST);
        
        panel.add(transactionItem);
    }
    
    /**
     * Main method to run the application
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new BillingView());
    }
    
    /**
     * Refreshes the transactions display
     */
    private void refreshTransactionsDisplay() {
        List<Transaction> transactions = TransactionManager.loadTransactions();
        transactionsPanel.removeAll();
        
        // Sort transactions by date (most recent first)
        transactions.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        
        // Display the 10 most recent transactions
        for (int i = 0; i < Math.min(10, transactions.size()); i++) {
            Transaction t = transactions.get(i);
            addTransactionItem(transactionsPanel, 
                t.getDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                t.getDescription(),
                t.getCategory(),
                t.getAmount());
        }
        
        transactionsPanel.revalidate();
        transactionsPanel.repaint();
    }
} 