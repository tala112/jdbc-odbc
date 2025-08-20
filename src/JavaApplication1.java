import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.table.DefaultTableModel;

public class JavaApplication1 extends JFrame {
    String fileName= "students$";
    private Connection conn;
    private void loadstudentsfromdb() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    class Student {
        int id;
        String  firstName, lastName, sex;
        int[] grades = new int[10]; 
        int gradesCount = 0;

        double getAverage() {
            if (gradesCount == 0) return 0;
            int sum = 0;
            for (int i = 0; i < gradesCount; i++) sum += grades[i];
            return (double) sum / gradesCount;
        }

        public String toString() {
            return id + " - " + firstName + " " + lastName + " (" + sex + ") Avg: " + getAverage();
        }
    }
    private List<Student> students = new ArrayList<Student>();
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private void setupTableUI() {
    String[] columnNames = {"ID", "First Name", "Last Name", "Gender", "G1", "G2", "G3", "G4", "G5","G6", "G7", "G8", "G9", "G10", "Average"};
    tableModel = new DefaultTableModel(columnNames, 0);
    studentTable = new JTable(tableModel);
    JScrollPane scrollPane = new JScrollPane(studentTable);
    add(scrollPane, BorderLayout.CENTER); 
}
    public JavaApplication1() {
        super("Student Management");
        setSize(800, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setupTableUI();
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem importItem = new JMenuItem("Import");
        JMenuItem exportItem = new JMenuItem("Export");
        JMenuItem exitItem = new JMenuItem("Exit");
        fileMenu.add(importItem);
        fileMenu.add(exportItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        JMenu operationsMenu = new JMenu("Operations");
        JMenuItem addItem = new JMenuItem("Add Student");
        JMenuItem deleteItem = new JMenuItem("Delete Student");
        JMenuItem searchItem = new JMenuItem("Search Student");
        JMenuItem modifyItem = new JMenuItem("Modify Grade");
        JMenuItem changeItem = new JMenuItem("Change Grade");
        JMenuItem displayAllItem = new JMenuItem("Display All Students");
        operationsMenu.add(addItem);
        operationsMenu.add(deleteItem);
        operationsMenu.add(searchItem);
        operationsMenu.add(modifyItem);
        operationsMenu.add(changeItem);
        operationsMenu.addSeparator();
        operationsMenu.add(displayAllItem);
        menuBar.add(fileMenu);
        menuBar.add(operationsMenu);
        setJMenuBar(menuBar);
        connectDatabase();
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeConnection();
                System.exit(0);
            }
        });
        importItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                importStudents();
            }
        });
        exportItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportStudents();
            }
        });
        addItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addStudent();
            }
        });
        deleteItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteStudent();
            }
        });
        searchItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchStudent();
            }
        });
        modifyItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                modifyGrade();
            }
        });
        changeItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeGrade();
            }
        });
        displayAllItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayAllStudents();
            }
        });
    }
    private void connectDatabase() {
        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            conn = DriverManager.getConnection("jdbc:odbc:stuxl");
            System.out.println("Connection established");
            loadStudentsFromDB();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to connect to database.");
        }
    }
    private void closeConnection() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    private void loadStudentsFromDB() {
        students.clear();
        if (conn == null) return;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM ["+fileName+"]");

            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();

            while (rs.next()) {
                Student s = new Student();
                s.id = rs.getInt("ID");
                s.firstName = rs.getString("FirstName");
                s.lastName = rs.getString("LastName");
                s.sex = rs.getString("Sex");
                s.gradesCount = Math.min(cols - 4, 10);
                for (int i = 0; i < s.gradesCount; i++) {
                    s.grades[i] = rs.getInt(5 + i);
                }
                students.add(s);
            }
            rs.close();
            stmt.close();
            loadStudentsTable();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error reading students from database.");
        }
    }
    private void loadStudentsTable() {
          tableModel.setRowCount(0); 
          if (students.isEmpty()) {
            return;
           }
         for (Student s : students) {            
            int id = s.id;
            String fname = s.firstName;
            String lname = s.lastName;
            String gender = s.sex;
            int g1 = s.grades[0];
            int g2=s.grades[1];
            int g3=s.grades[2];
            int g4=s.grades[3];
            int g5=s.grades[4];
            int g6 = s.grades[5];
            int g7=s.grades[6];
            int g8=s.grades[7];
            int g9=s.grades[8];
            int g10=s.grades[9];
            double avg=s.getAverage();
            Object[] row = {id, fname, lname, gender, g1, g2, g3, g4, g5,g6,g7,g8,g9,g10, avg};
            tableModel.addRow(row);
        }
  }
   private void importStudents() {
    JFileChooser chooser = new JFileChooser();
    int result = chooser.showOpenDialog(this);
    if (result != JFileChooser.APPROVE_OPTION) return;

    File file = chooser.getSelectedFile();

    try (Scanner scanner = new Scanner(file)) {
        if (!scanner.hasNextLine()) {
            JOptionPane.showMessageDialog(this, "File is empty.");
            return;
        }

        scanner.nextLine(); 

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(";");
            if (parts.length < 4) continue;

            int id = Integer.parseInt(parts[0]);
            String firstName = parts[1];
            String lastName = parts[2];
            String sex = parts[3];

            String checkSql = "SELECT * FROM [" + fileName + "] WHERE ID = " + id;
            Statement checkStmt = conn.createStatement();
            ResultSet rs = checkStmt.executeQuery(checkSql);
            if (rs.next()) {
                rs.close();
                checkStmt.close();
                continue; 
            }
            rs.close();
            checkStmt.close();

            StringBuilder insertSql = new StringBuilder("INSERT INTO [" + fileName + "] VALUES(");
            insertSql.append(id).append(", ");
            insertSql.append("'").append(firstName).append("', ");
            insertSql.append("'").append(lastName).append("', ");
            insertSql.append("'").append(sex).append("'");

            int gradesCount = Math.min(parts.length - 4, 10);
            for (int i = 0; i < 10; i++) {
                if (i < gradesCount) {
                    String g = parts[4 + i].trim();
                    insertSql.append(", ").append(g.isEmpty() ? "NULL" : g);
                } else {
                    insertSql.append(", NULL");
                }
            }

            insertSql.append(")");
            Statement insertStmt = conn.createStatement();
            insertStmt.executeUpdate(insertSql.toString());
            insertStmt.close();
        }

        JOptionPane.showMessageDialog(this, "Import finished.");
        loadStudentsFromDB();

    } catch (IOException | SQLException ex) {
        JOptionPane.showMessageDialog(this, "Import failed: " + ex.getMessage());
        ex.printStackTrace();
    }
}
   private void exportStudents() {
    loadStudentsFromDB();
    if (students.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Data Base is Empty no students exists");
        return;
    }

    JFileChooser chooser = new JFileChooser();
    int result = chooser.showSaveDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.print("ID;FirstName;LastName;Sex");
            int maxGrades = 10;
            for (int i = 1; i <= maxGrades; i++) {
                pw.print(";g" + i);
            }
            pw.println();

            for (Student s : students) {
                pw.print(s.id + ";" + s.firstName + ";" + s.lastName + ";" + s.sex);
                for (int i = 0; i < s.gradesCount; i++) {
                    pw.print(";" + s.grades[i]);
                }
                pw.println();
            }

            JOptionPane.showMessageDialog(this, "Export successful");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error exporting to file.");
        }
    }
}
   private void searchStudent() {
    JTextField idField = new JTextField();

    JPanel panel = new JPanel(new GridLayout(0, 2));
    panel.add(new JLabel("ID:"));
    panel.add(idField);

    int result = JOptionPane.showConfirmDialog(this, panel, "Search Student", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
        String idText = idField.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID is required.");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idText);
            if (id <= 0) {
                JOptionPane.showMessageDialog(this, "ID must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID must be a valid integer.");
            return;
        }

        try {
            Statement checkStmt = conn.createStatement();
            String checkSql = "SELECT * FROM [" + fileName + "] WHERE ID = " + id;
            ResultSet rs = checkStmt.executeQuery(checkSql);

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Student with ID " + rs.getObject(1)
                        + " First Name: " + rs.getObject(2)
                        + " Last Name: " + rs.getObject(3)
                        + " Sex: " + rs.getObject(4)
                        + " G1: " + rs.getObject(5)
                        + " G2: " + rs.getObject(6)
                        + " G3: " + rs.getObject(7)
                        + " G4: " + rs.getObject(8)
                        + " G5: " + rs.getObject(9)
                        + " G6: " + rs.getObject(10)
                        + " G7: " + rs.getObject(11)
                        + " G8: " + rs.getObject(12)
                        + " G9: " + rs.getObject(13)
                        + " G10: " + rs.getObject(14));
            } else {
                JOptionPane.showMessageDialog(this, "Student with ID " + id + " does not exist.");
            }

            rs.close();
            checkStmt.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error searching student: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
    private void displayAllStudents() {
        loadStudentsFromDB();
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this,"empty");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Student s : students) {
            sb.append(s.toString()).append("\n");
        }
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        JOptionPane.showMessageDialog(this, scrollPane, "All Students", JOptionPane.INFORMATION_MESSAGE);
    }
    private void addStudent() {
    JTextField idField = new JTextField();
    JTextField firstNameField = new JTextField();
    JTextField lastNameField = new JTextField();
    JComboBox<String> sexBox = new JComboBox<>(new String[]{"Male", "Female"});
    JTextField[] gradeFields = new JTextField[10];
    for (int i = 0; i < 10; i++) {
        gradeFields[i] = new JTextField();
    }

    JPanel panel = new JPanel(new GridLayout(0, 2));
    panel.add(new JLabel("ID:"));
    panel.add(idField);
    panel.add(new JLabel("First Name:"));
    panel.add(firstNameField);
    panel.add(new JLabel("Last Name:"));
    panel.add(lastNameField);
    panel.add(new JLabel("Sex:"));
    panel.add(sexBox);
    for (int i = 0; i < 10; i++) {
        panel.add(new JLabel("Grade " + (i + 1) + ":"));
        panel.add(gradeFields[i]);
    }

    int result = JOptionPane.showConfirmDialog(this, panel, "Add New Student", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
        String idText = idField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String sex = (String) sexBox.getSelectedItem();

        if (idText.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID, First Name, and Last Name are required.");
            return;
        }

        if (firstName.matches(".*\\d.*") || lastName.matches(".*\\d.*") || sex.matches(".*\\d.*")) {
            JOptionPane.showMessageDialog(this, "First Name, Last Name, and Sex should not contain numbers.");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idText);
            if (id <= 0) {
                JOptionPane.showMessageDialog(this, "ID must be greater than 0.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID must be a valid integer.");
            return;
        }

        Integer[] grades = new Integer[10];  
        for (int i = 0; i < 10; i++) {
            String gradeText = gradeFields[i].getText().trim();
            if (!gradeText.isEmpty()) {
                try {
                    int grade = Integer.parseInt(gradeText);
                    if (grade < 0) {
                        JOptionPane.showMessageDialog(this, "Grade " + (i + 1) + " must be 0 or greater.");
                        return;
                    }
                    grades[i] = grade;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Grade " + (i + 1) + " must be an integer.");
                    return;
                }
            } else {
                grades[i] = null; 
            }
        }

        try {
            Statement checkStmt = conn.createStatement();
            String checkSql = "SELECT ID FROM [" + fileName + "] WHERE ID = " + id;
            ResultSet rs = checkStmt.executeQuery(checkSql);
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Student with ID " + id + " already exists.");
                rs.close();
                checkStmt.close();
                return;
            }
            rs.close();
            checkStmt.close();

            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO [").append(fileName).append("] (ID, FirstName, LastName, Sex");
            for (int i = 1; i <= 10; i++) {
                sql.append(", g").append(i);
            }
            sql.append(") VALUES (");
            sql.append(id).append(", ");
            sql.append("'").append(firstName).append("', ");
            sql.append("'").append(lastName).append("', ");
            sql.append("'").append(sex).append("'");

            for (int i = 0; i < 10; i++) {
                if (grades[i] != null) {
                    sql.append(", ").append(grades[i]);
                } else {
                    sql.append(", NULL");
                }
            }
            sql.append(")");

            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql.toString());
            stmt.close();

            JOptionPane.showMessageDialog(this, "Student added successfully.");
            loadStudentsFromDB(); // refresh UI

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error inserting student: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
   private void modifyGrade() {
    JTextField idField = new JTextField();
    JPanel panel = new JPanel(new GridLayout(0, 2));
    panel.add(new JLabel("ID:"));
    panel.add(idField);

    int result = JOptionPane.showConfirmDialog(this, panel, "Modify Grade for Student", JOptionPane.OK_CANCEL_OPTION);
    if (result != JOptionPane.OK_OPTION) return;

    String idText = idField.getText().trim();
    if (idText.isEmpty()) {
        JOptionPane.showMessageDialog(this, "ID is required.");
        return;
    }

    int id;
    try {
        id = Integer.parseInt(idText);
        if (id <= 0) {
            JOptionPane.showMessageDialog(this, "ID must be a positive number.");
            return;
        }
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "ID must be a number.");
        return;
    }

    try {
        Statement checkStmt = conn.createStatement();
        String checkSql = "SELECT * FROM [" + fileName + "] WHERE ID = " + id;
        ResultSet rs = checkStmt.executeQuery(checkSql);

        if (rs.next()) {
            String firstName = rs.getString(2).trim();
            String lastName = rs.getString(3).trim();
            String sex = rs.getString(4).trim();

            if (firstName.matches(".*\\d.*") || lastName.matches(".*\\d.*") || sex.matches(".*\\d.*")) {
                JOptionPane.showMessageDialog(this, "First Name, Last Name, and Sex must not contain digits.");
                rs.close();
                checkStmt.close();
                return;
            }

            JTextField[] gradeFields = new JTextField[10];
            JPanel gpanel = new JPanel(new GridLayout(0, 2));

            gpanel.add(new JLabel("Student ID:"));
            gpanel.add(new JLabel(String.valueOf(id)));
            gpanel.add(new JLabel("First Name:"));
            gpanel.add(new JLabel(firstName));
            gpanel.add(new JLabel("Last Name:"));
            gpanel.add(new JLabel(lastName));
            gpanel.add(new JLabel("Sex:"));
            gpanel.add(new JLabel(sex));

            String[] oldGrades = new String[10];
            for (int i = 0; i < 10; i++) {
                oldGrades[i] = String.valueOf(rs.getObject(5 + i));
                gpanel.add(new JLabel("Grade " + (i + 1) + " (old: " + oldGrades[i] + "):"));
                gradeFields[i] = new JTextField();
                gpanel.add(gradeFields[i]);
            }

            rs.close();
            checkStmt.close();

            int res = JOptionPane.showConfirmDialog(this, gpanel, "Change Grades", JOptionPane.OK_CANCEL_OPTION);
            if (res != JOptionPane.OK_OPTION) return;

            StringBuilder updateSql = new StringBuilder("UPDATE [" + fileName + "] SET ");
            for (int i = 0; i < 10; i++) {
                String newVal = gradeFields[i].getText().trim();
                if (newVal.isEmpty()) {
                    updateSql.append("g").append(i + 1).append("=").append(oldGrades[i]);
                } else {
                    try {
                        int g = Integer.parseInt(newVal);
                        if (g < 0) {
                            JOptionPane.showMessageDialog(this, "Grades must be positive integers.");
                            return;
                        }
                        updateSql.append("g").append(i + 1).append("=").append(g);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Grades must be valid integers.");
                        return;
                    }
                }
                if (i < 9) updateSql.append(", ");
            }
            updateSql.append(" WHERE ID = ").append(id);

            Statement updateStmt = conn.createStatement();
            updateStmt.executeUpdate(updateSql.toString());
            updateStmt.close();

            JOptionPane.showMessageDialog(this, "Grades updated successfully.");
            loadStudentsFromDB();

        } else {
            JOptionPane.showMessageDialog(this, "Student with ID " + id + " not found.");
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error modifying student: " + ex.getMessage());
        ex.printStackTrace();
    }
}
    private void changeGrade() {
    JTextField idField = new JTextField();
    JPanel panel = new JPanel(new GridLayout(0, 2));
    panel.add(new JLabel("Student ID:"));
    panel.add(idField);

    int result = JOptionPane.showConfirmDialog(this, panel, "Change Grade for Student", JOptionPane.OK_CANCEL_OPTION);
    if (result != JOptionPane.OK_OPTION) return;

    String idStr = idField.getText().trim();
    if (idStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "ID is required.");
        return;
    }

    int id;
    try {
        id = Integer.parseInt(idStr);
        if (id <= 0) {
            JOptionPane.showMessageDialog(this, "ID must be a positive number.");
            return;
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "ID must be a valid integer.");
        return;
    }

    try {
        Statement checkStmt = conn.createStatement();
        String checkSql = "SELECT * FROM [" + fileName + "] WHERE ID = " + id;
        ResultSet rs = checkStmt.executeQuery(checkSql);

        if (rs.next()) {
           
            String firstName = rs.getString(2).trim();
            String lastName = rs.getString(3).trim();
            String gender = rs.getString(4).trim();

            if (firstName.matches(".*\\d.*") || lastName.matches(".*\\d.*") || gender.matches(".*\\d.*")) {
                JOptionPane.showMessageDialog(this, "First name, last name, and gender must not contain digits.");
                rs.close();
                checkStmt.close();
                return;
            }

            JTextField gradeNumberField = new JTextField();
            JPanel gnumPanel = new JPanel(new GridLayout(0, 2));
            gnumPanel.add(new JLabel("Grade number to change (1-10):"));
            gnumPanel.add(gradeNumberField);

            int gnumResult = JOptionPane.showConfirmDialog(this, gnumPanel, "Choose Grade", JOptionPane.OK_CANCEL_OPTION);
            if (gnumResult != JOptionPane.OK_OPTION) return;

            String gnumStr = gradeNumberField.getText().trim();
            if (gnumStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Grade number is required.");
                return;
            }

            int gradeIndex;
            try {
                gradeIndex = Integer.parseInt(gnumStr);
                if (gradeIndex < 1 || gradeIndex > 10) {
                    JOptionPane.showMessageDialog(this, "Grade number must be between 1 and 10.");
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid grade number.");
                return;
            }

            String oldGrade = String.valueOf(rs.getObject(4 + gradeIndex));

            JTextField newGradeField = new JTextField();
            JPanel newGradePanel = new JPanel(new GridLayout(0, 3));
            newGradePanel.add(new JLabel("Old grade g" + gradeIndex + ": " + oldGrade));
            newGradePanel.add(new JLabel("     New grade:"));
            newGradePanel.add(newGradeField);

            int updateResult = JOptionPane.showConfirmDialog(this, newGradePanel, "Enter New Grade", JOptionPane.OK_CANCEL_OPTION);
            if (updateResult != JOptionPane.OK_OPTION) return;

            String newGradeStr = newGradeField.getText().trim();
            if (newGradeStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "New grade is required.");
                return;
            }

            int newGrade;
            try {
                newGrade = Integer.parseInt(newGradeStr);
                if (newGrade < 0) {
                    JOptionPane.showMessageDialog(this, "Grade must be a positive number.");
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Grade must be a valid integer.");
                return;
            }

            String updateSql = "UPDATE [" + fileName + "] SET g" + gradeIndex + " = " + newGrade + " WHERE ID = " + id;
            Statement updateStmt = conn.createStatement();
            updateStmt.executeUpdate(updateSql);
            updateStmt.close();

            JOptionPane.showMessageDialog(this, "Grade g" + gradeIndex + " updated successfully.");
            loadStudentsFromDB();
        } else {
            JOptionPane.showMessageDialog(this, "Student with ID " + id + " not found.");
        }

        rs.close();
        checkStmt.close();

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        ex.printStackTrace();
    }
}   
    private void deleteStudent() {
    JTextField idField = new JTextField();
    JPanel panel = new JPanel(new GridLayout(0, 2));
    panel.add(new JLabel("ID:"));
    panel.add(idField);

    int result = JOptionPane.showConfirmDialog(this, panel, "DELETE Student", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID is required.");
            return;
        }

        try {
            int idInt = Integer.parseInt(id);
            if (idInt <= 0) {
                JOptionPane.showMessageDialog(this, "ID must be a positive number.");
                return;
            }

            Statement checkStmt = conn.createStatement();
            String checkSql = "SELECT id FROM [" + fileName + "] WHERE ID = " + idInt;
            ResultSet rs = checkStmt.executeQuery(checkSql);

            if (rs.next()) {
                Statement stmt = conn.createStatement();
                String sql = "DELETE FROM [" + fileName + "] WHERE ID = " + idInt;
                stmt.execute(sql);
                stmt.close();
                JOptionPane.showMessageDialog(this, "Student DELETED successfully.");
                loadStudentsFromDB();
            } else {
                JOptionPane.showMessageDialog(this, "Student with this ID doesn't exist.");
            }

            rs.close();
            checkStmt.close();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID must be a number.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting student: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}    
    public static void main(String[] args) {
                JavaApplication1 app = new JavaApplication1();
                app.setVisible(true);
    }
}
