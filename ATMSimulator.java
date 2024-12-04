import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class ATMSimulator {
    public static void main(String[] args) {
        try {
            // Predefined accounts and PINs
            Map<String, String> accountData = new HashMap<>();
            accountData.put("1234567890123456", "1234"); // Account 1
            accountData.put("9876543210987654", "4321"); // Account 2

            // Input for account number and PIN
            String accountNumber = JOptionPane.showInputDialog("Enter your 16-digit account number:");
            String pin = JOptionPane.showInputDialog("Enter your PIN:");

            if (!authenticate(accountNumber, pin, accountData)) {
                JOptionPane.showMessageDialog(null, "Authentication failed! Invalid account number or PIN.", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }

            // Account holder name is inferred from account data for this example
            String accountHolderName = "User";

            Account account = new Account(accountHolderName, accountNumber, 1000); // Initial balance: $1000
            SwingUtilities.invokeLater(() -> new ATMSimulatorGUI(account));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static boolean authenticate(String accountNumber, String pin, Map<String, String> accountData) {
        return accountData.containsKey(accountNumber) && accountData.get(accountNumber).equals(pin);
    }
}

class Account {
    private final String accountHolderName;
    private final String accountNumber;
    private double balance;
    private final List<String[]> transactions;

    public Account(String accountHolderName, String accountNumber, double initialBalance) {
        this.accountHolderName = accountHolderName;
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
        addTransaction("Initial Deposit", initialBalance);
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public List<String[]> getTransactions() {
        return transactions;
    }

    public void deposit(double amount) {
        balance += amount;
        addTransaction("Deposit", amount);
    }

    public boolean withdraw(double amount) {
        if (amount > balance) {
            return false; // Insufficient funds
        }
        balance -= amount;
        addTransaction("Withdrawal", -amount);
        return true;
    }

    public boolean transfer(double amount, String toAccount) {
        if (amount > balance) {
            return false; // Insufficient funds
        }
        balance -= amount;
        addTransaction("Transfer to " + toAccount, -amount);
        return true;
    }

    private void addTransaction(String type, double amount) {
        transactions.add(new String[]{type, String.format("$%.2f", amount), String.format("$%.2f", balance)});
    }
}

class ATMSimulatorGUI extends JFrame {
    private final Account account;

    public ATMSimulatorGUI(Account account) {
        this.account = account;

        setTitle("Welcome User");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header with Title and Welcome Message
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new GridLayout(2, 1));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("ATM SIMULATOR", JLabel.CENTER);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(0, 100, 0)); // Dark Green Background
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel);

        // Welcome Message
        JLabel welcomeLabel = new JLabel("Welcome, " + account.getAccountHolderName(), JLabel.CENTER);
        welcomeLabel.setForeground(new Color(0, 102, 204)); // Blue Color
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        headerPanel.add(welcomeLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Main menu panel
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(6, 1, 10, 10));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton withdrawButton = createMenuButton("Cash Withdrawal");
        JButton depositButton = createMenuButton("Deposit");
        JButton balanceButton = createMenuButton("Balance Inquiry");
        JButton transferButton = createMenuButton("Fund Transfer");
        JButton miniStatementButton = createMenuButton("Mini-Statement");
        JButton exitButton = createMenuButton("Exit");

        menuPanel.add(withdrawButton);
        menuPanel.add(depositButton);
        menuPanel.add(balanceButton);
        menuPanel.add(transferButton);
        menuPanel.add(miniStatementButton);
        menuPanel.add(exitButton);

        add(menuPanel, BorderLayout.CENTER);

        // Add button actions
        withdrawButton.addActionListener(e -> performWithdraw());
        depositButton.addActionListener(e -> performDeposit());
        balanceButton.addActionListener(e -> showBalance());
        transferButton.addActionListener(e -> performTransfer());
        miniStatementButton.addActionListener(e -> showMiniStatement());
        exitButton.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0, 102, 204)); // Blue Color
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        return button;
    }

    private void performWithdraw() {
        String input = JOptionPane.showInputDialog(this, "Enter amount to withdraw:");
        try {
            double amount = Double.parseDouble(input);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a positive number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!account.withdraw(amount)) {
                JOptionPane.showMessageDialog(this, "Insufficient funds.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Withdrawal successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performDeposit() {
        String input = JOptionPane.showInputDialog(this, "Enter amount to deposit:");
        try {
            double amount = Double.parseDouble(input);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a positive number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            account.deposit(amount);
            JOptionPane.showMessageDialog(this, "Deposit successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showBalance() {
        JOptionPane.showMessageDialog(this, "Current Balance: $" + String.format("%.2f", account.getBalance()), "Balance Inquiry", JOptionPane.INFORMATION_MESSAGE);
    }

    private void performTransfer() {
        String toAccount = JOptionPane.showInputDialog(this, "Enter the recipient account number:");
        String input = JOptionPane.showInputDialog(this, "Enter amount to transfer:");
        try {
            double amount = Double.parseDouble(input);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a positive number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!account.transfer(amount, toAccount)) {
                JOptionPane.showMessageDialog(this, "Insufficient funds.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Fund transfer successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMiniStatement() {
        StringBuilder statement = new StringBuilder("<html><body><table border='1'>");
        statement.append("<tr><th>Type</th><th>Amount</th><th>Balance</th></tr>");
        for (String[] transaction : account.getTransactions()) {
            statement.append("<tr>");
            for (String data : transaction) {
                statement.append("<td>").append(data).append("</td>");
            }
            statement.append("</tr>");
        }
        statement.append("</table></body></html>");
        JOptionPane.showMessageDialog(this, statement.toString(), "Mini-Statement", JOptionPane.INFORMATION_MESSAGE);
    }
}
