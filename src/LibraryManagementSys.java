import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

// LOGIN FORM (Extra Feature)
class LoginForm extends JFrame {

    JTextField txtUser;
    JPasswordField txtPass;

    public LoginForm() {
        setTitle("Login");
        setSize(700,500);
        setLayout(new BorderLayout());

        //LOGIN FORM - Header
        JPanel header = new JPanel(); //Container1
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Color.WHITE); //bg color

        //LOGIN FORM - Header - Img Logo
        ImageIcon icon = new ImageIcon(getClass().getResource("/img/iac_logo.jpg"));
        Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        JLabel pic = new JLabel(new ImageIcon(img));
        pic.setAlignmentX(Component.CENTER_ALIGNMENT);

        //LOGIN FORM - Title
        JLabel title = new JLabel("Library Management System");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        //LOGIN FORM - Subtitle
        JLabel subtitle = new JLabel("iAcademy");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        //LOGIN FORM - Header Components
        add(header, BorderLayout.NORTH);
        header.add(pic);
        header.add(title);  
        header.add(subtitle);

        //LOGIN FORM - Form
        JPanel form = new JPanel(); //Container2
        form.setLayout(new GridLayout(4,1,5,5));
        form.setBorder(BorderFactory.createEmptyBorder(20,40,20,40));

        //LOGIN FORM - Form - Username Input
        form.add(new JLabel("Username:"));
        txtUser = new JTextField();
        form.add(txtUser);
        txtUser.setBorder(BorderFactory.createEmptyBorder(10,20,10,20)); //padding

        //LOGIN FORM - Form - Password Input
        form.add(new JLabel("Password:"));
        txtPass = new JPasswordField();
        form.add(txtPass);
        txtPass.setBorder(BorderFactory.createEmptyBorder(10,20,10,20)); //padding

        //LOGIN FORM - Form - Input at Center
        add(form, BorderLayout.CENTER);

        //LOGIN FORM - Login Button
        JButton btnLogin = new JButton("Login"); //Button
        btnLogin.setBackground(new Color(10,40,80)); //Button - bg color
        btnLogin.setForeground(Color.WHITE); //Button - text color
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14)); //fontstyle
        btnLogin.setBorder(BorderFactory.createEmptyBorder(10,20,10,20)); //padding
        btnLogin.setFocusPainted(false);

        add(btnLogin, BorderLayout.SOUTH);
        btnLogin.addActionListener(e -> login());
        getContentPane().setBackground(Color.BLACK);

    }

    void login() {
        String user = txtUser.getText();
        String pass = new String(txtPass.getPassword());

        if(user.equals("admin") && pass.equals("admin")) {
            new LibraryManagementSys().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid login!");
        }
    }
}

//SYSTEM
public class LibraryManagementSys extends JFrame {

    JTextField txtTitle, txtAuthor, txtYear, txtStatus;
    JTable table;
    DefaultTableModel model;

    String url = "jdbc:mysql://localhost:3306/";
    String dbName = "library_system_db";
    String username = "root";
    String password = "Leiminaile24"; // change

    Connection conn;

    public LibraryManagementSys() {

        setTitle("Library Management System");
        setSize(900, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeDatabase();
        initializeUI();
        loadBooks();
    }

    void initializeDatabase() {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            conn = DriverManager.getConnection(url, username, password);
            Statement stmt = conn.createStatement();

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
            stmt.execute("USE " + dbName);

            String tableSQL =
                    "CREATE TABLE IF NOT EXISTS books(" +
                             "id INT AUTO_INCREMENT PRIMARY KEY," +
                             "book_title VARCHAR(200)," +
                             "author VARCHAR(100)," +
                             "year INT," +
                             "status VARCHAR(50))";

            stmt.executeUpdate(tableSQL);

        } catch(Exception e){
            e.printStackTrace();
        }
    }

        void initializeUI() {

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        
        panel.setBackground(new Color(10,40,80)); //bg color
        panel.setForeground(Color.WHITE); //text color
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Book Title");
        lblTitle.setForeground(Color.WHITE); //text color
        panel.add(lblTitle);
        txtTitle = new JTextField();
        panel.add(txtTitle);

        JLabel lblAuthor = new JLabel("Author");
        lblAuthor.setForeground(Color.WHITE); //text color
        panel.add(lblAuthor);
        txtAuthor = new JTextField();
        panel.add(txtAuthor);

        JLabel lblYear = new JLabel("Year");
        lblYear.setForeground(Color.WHITE); //text color
        panel.add(lblYear);
        txtYear = new JTextField();
        panel.add(txtYear);

        JLabel lblStatus = new JLabel("Status");
        lblStatus.setForeground(Color.WHITE); //text color
        panel.add(lblStatus);
        txtStatus = new JTextField();
        panel.add(txtStatus);


        //System - Buttons
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Add Book");
        JButton btnUpdate = new JButton("Update Record");
        JButton btnDelete = new JButton("Delete Record");

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);

        //Container for the input fields and buttons at west side
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(panel, BorderLayout.CENTER);
        topContainer.add(btnPanel, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.WEST);

        //Table Panel
        model = new DefaultTableModel();
        table = new JTable(model);

        model.addColumn("ID");
        model.addColumn("Book Title");
        model.addColumn("Author");
        model.addColumn("Year");
        model.addColumn("Status");

        //columnname at top of the table
        add(new JScrollPane(table), BorderLayout.CENTER);

        //Table Panel - Delete Button at the bottom
        JPanel panel2 = new JPanel(); //container for delete button at the bottom
        panel2.add(btnDelete);
        add(panel2, BorderLayout.SOUTH);

        //Event Listeners
        btnAdd.addActionListener(e -> addBook());
        btnUpdate.addActionListener(e -> updateBook());
        btnDelete.addActionListener(e -> deleteBook());

        table.getSelectionModel().addListSelectionListener(e -> selectBook());
    }

        void loadBooks() {
        try {
            model.setRowCount(0);

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM library_system_db.books");

            while(rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("book_title"),
                        rs.getString("author"),
                        rs.getInt("year"),
                        rs.getString("status")
                });
            }

        } catch(Exception e){
            e.printStackTrace();
        }
    }

        void addBook() {
        try {
            String sql = "INSERT INTO library_system_db.books(book_title,author,year, status) VALUES(?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtTitle.getText());
            ps.setString(2, txtAuthor.getText());
            ps.setInt(3, Integer.parseInt(txtYear.getText()));
            ps.setString(4, txtStatus.getText());
            ps.executeUpdate();
            loadBooks();
            clearFields();
            JOptionPane.showMessageDialog(this, "Book added successfully!");
        } catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding book!");
        }
    }

        void updateBook() {
        try {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a book to update!");
                return;
            }
            int id = (int) model.getValueAt(row,0);

            String sql = "UPDATE library_system_db.books SET book_title=?,author=?,year=?, status=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, txtTitle.getText());
            ps.setString(2, txtAuthor.getText());
            ps.setInt(3, Integer.parseInt(txtYear.getText()));
            ps.setString(4, txtStatus.getText());
            ps.setInt(5, id);

            ps.executeUpdate();
            loadBooks();
            JOptionPane.showMessageDialog(this, "Record updated successfully!");

        } catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating record!");
        }
    }

    void deleteBook() {
        try {
            int row = table.getSelectedRow();
            if(row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a book to delete!");
                return;
            }
            int id = (int) model.getValueAt(row,0);

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this book?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if(confirm != JOptionPane.YES_OPTION) {
                return;
            }
            String sql = "DELETE FROM library_system_db.books WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            loadBooks();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    void selectBook() {
        int row = table.getSelectedRow();
        if(row >= 0) {
            txtTitle.setText(model.getValueAt(row,1).toString());
            txtAuthor.setText(model.getValueAt(row,2).toString());
            txtYear.setText(model.getValueAt(row,3).toString());
            txtStatus.setText(model.getValueAt(row, 4).toString());
        }
    }

    void clearFields() {
        txtTitle.setText("");
        txtAuthor.setText("");
        txtYear.setText("");
        txtStatus.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true)); 
}
 }