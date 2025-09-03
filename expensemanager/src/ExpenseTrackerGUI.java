import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ExpenseTrackerGUI extends JFrame {
    private JTextField dateField, categoryField, amountField;
    private DefaultTableModel tableModel;

    public ExpenseTrackerGUI() {
        setTitle("Expense Tracker");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top input panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        inputPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField();
        inputPanel.add(dateField);

        inputPanel.add(new JLabel("Category:"));
        categoryField = new JTextField();
        inputPanel.add(categoryField);

        inputPanel.add(new JLabel("Amount:"));
        amountField = new JTextField();
        inputPanel.add(amountField);

        JButton addButton = new JButton("Add Expense");
        inputPanel.add(addButton);

        JButton refreshButton = new JButton("Refresh");
        inputPanel.add(refreshButton);

        add(inputPanel, BorderLayout.NORTH);

        // Table to show expenses
        tableModel = new DefaultTableModel(new String[]{"ID", "Date", "Category", "Amount"}, 0);
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Button actions
        addButton.addActionListener(e -> addExpense());
        refreshButton.addActionListener(e -> loadExpenses());

        // Load initial data
        loadExpenses();
    }

    private void addExpense() {
        String date = dateField.getText();
        String category = categoryField.getText();
        String amountText = amountField.getText();

        if (date.isEmpty() || category.isEmpty() || amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO expenses (date, category, amount) VALUES (?, ?, ?)")) {
            ps.setString(1, date);
            ps.setString(2, category);
            ps.setDouble(3, Double.parseDouble(amountText));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Expense Added!");
            loadExpenses();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void loadExpenses() {
    tableModel.setRowCount(0); // Clear old data
    try (Connection con = DBConnect.getConnection();
         Statement st = con.createStatement();
         ResultSet rs = st.executeQuery("SELECT * FROM expenses")) {

        while (rs.next()) {
            tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("date"),   // ✅ String banake dikhaya
                    rs.getString("category"),
                    rs.getDouble("amount")
            });
        }
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading expenses: " + ex.getMessage());
    }
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExpenseTrackerGUI().setVisible(true));
    }
}
