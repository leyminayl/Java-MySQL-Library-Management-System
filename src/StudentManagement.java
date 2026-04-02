import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StudentManagement extends JFrame {

    JTextField txtName, txtAge, txtGrade, txtYear;
    JTable table;
    DefaultTableModel model;

    String url = "jdbc:mysql://localhost:3306/";
    String dbName = "schooldb";
    String username = "root";
    String password = "Leiminaile24"; // change

    Connection conn;

    public StudentManagement() {

        setTitle("Student Grade Management System");
        setSize(700,500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeDatabase();
        initializeUI();
        loadStudents();
    }

    void initializeDatabase() {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            conn = DriverManager.getConnection(url, username, password);
            Statement stmt = conn.createStatement();

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
            stmt.execute("USE " + dbName);

            String tableSQL =
                    "CREATE TABLE IF NOT EXISTS students(" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "name VARCHAR(100)," +
                            "age INT," +
                            "grade VARCHAR(10)," +
                            "year_level VARCHAR(20))";

            stmt.executeUpdate(tableSQL);

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    void initializeUI() {

        JPanel panel = new JPanel(new GridLayout(5,2));

        panel.add(new JLabel("Name"));
        txtName = new JTextField();
        panel.add(txtName);

        panel.add(new JLabel("Age"));
        txtAge = new JTextField();
        panel.add(txtAge);

        panel.add(new JLabel("Grade"));
        txtGrade = new JTextField();
        panel.add(txtGrade);

        panel.add(new JLabel("Year Level"));
        txtYear = new JTextField();
        panel.add(txtYear);

        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");

        panel.add(btnAdd);
        panel.add(btnUpdate);

        add(panel, BorderLayout.NORTH);

        model = new DefaultTableModel();
        table = new JTable(model);

        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Age");
        model.addColumn("Grade");
        model.addColumn("Year Level");

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel panel2 = new JPanel();
        panel2.add(btnDelete);
        add(panel2, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addStudent());
        btnUpdate.addActionListener(e -> updateStudent());
        btnDelete.addActionListener(e -> deleteStudent());

        table.getSelectionModel().addListSelectionListener(e -> selectStudent());
    }

    void loadStudents() {

        try {

            model.setRowCount(0);

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM schooldb.students");

            while(rs.next()) {

                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("grade"),
                        rs.getString("year_level")
                });

            }

        } catch(Exception e){
            e.printStackTrace();
        }

    }

    void addStudent() {

        try {

            String sql = "INSERT INTO schooldb.students(name,age,grade,year_level) VALUES(?,?,?,?)";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, txtName.getText());
            ps.setInt(2, Integer.parseInt(txtAge.getText()));
            ps.setString(3, txtGrade.getText());
            ps.setString(4, txtYear.getText());

            ps.executeUpdate();

            loadStudents();

        } catch(Exception e){
            e.printStackTrace();
        }

    }

    void updateStudent() {

        try {

            int row = table.getSelectedRow();
            int id = (int) model.getValueAt(row,0);

            String sql = "UPDATE schooldb.students SET name=?,age=?,grade=?,year_level=? WHERE id=?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, txtName.getText());
            ps.setInt(2, Integer.parseInt(txtAge.getText()));
            ps.setString(3, txtGrade.getText());
            ps.setString(4, txtYear.getText());
            ps.setInt(5, id);

            ps.executeUpdate();

            loadStudents();

        } catch(Exception e){
            e.printStackTrace();
        }

    }

    void deleteStudent() {

        try {

            int row = table.getSelectedRow();
            int id = (int) model.getValueAt(row,0);

            String sql = "DELETE FROM schooldb.students WHERE id=?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            ps.executeUpdate();

            loadStudents();

        } catch(Exception e){
            e.printStackTrace();
        }

    }

    void selectStudent() {

        int row = table.getSelectedRow();

        if(row != -1){

            txtName.setText(model.getValueAt(row,1).toString());
            txtAge.setText(model.getValueAt(row,2).toString());
            txtGrade.setText(model.getValueAt(row,3).toString());
            txtYear.setText(model.getValueAt(row,4).toString());

        }

    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new StudentManagement().setVisible(true));

    }

}