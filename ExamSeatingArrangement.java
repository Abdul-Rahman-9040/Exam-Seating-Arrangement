import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.*;

public class ExamSeatingArrangement extends JFrame implements ActionListener {
    private JTextField examNameField, studentCountField, attendedCountField, classroomCountField;
    private JTextField[] seatingCapacityFields;
    private JTextArea absentListArea, seatingListArea;
    private JButton submitButton;
    private HashMap<String, Boolean> studentsMap;

    public ExamSeatingArrangement() {
        // Create GUI components
        setTitle("Exam Seating Arrangement");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel examLabel = new JLabel("Exam Name:");
        examNameField = new JTextField(20);

        JLabel studentCountLabel = new JLabel("Total Students:");
        studentCountField = new JTextField(5);

        JLabel attendedCountLabel = new JLabel("Attended Exams:");
        attendedCountField = new JTextField(5);

        JLabel classroomCountLabel = new JLabel("Number of Classrooms:");
        classroomCountField = new JTextField(5);

        submitButton = new JButton("Submit");
        submitButton.addActionListener(this);

        absentListArea = new JTextArea(10, 30);
        seatingListArea = new JTextArea(10, 30);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2));

        panel.add(examLabel);
        panel.add(examNameField);
        panel.add(studentCountLabel);
        panel.add(studentCountField);
        panel.add(attendedCountLabel);
        panel.add(attendedCountField);
        panel.add(classroomCountLabel);
        panel.add(classroomCountField);
        panel.add(submitButton);

        seatingListArea.setBorder(BorderFactory.createTitledBorder("Seating Arrangement"));
        absentListArea.setBorder(BorderFactory.createTitledBorder("Absent Students"));

        getContentPane().add(panel, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(seatingListArea), BorderLayout.CENTER);
        getContentPane().add(new JScrollPane(absentListArea), BorderLayout.SOUTH);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String examName = examNameField.getText();
        int totalStudents = Integer.parseInt(studentCountField.getText());
        int attendedStudents = Integer.parseInt(attendedCountField.getText());
        int numClassrooms = Integer.parseInt(classroomCountField.getText());

        // Validation to ensure attended students <= total students
        if (attendedStudents > totalStudents) {
            JOptionPane.showMessageDialog(this, "Attended students cannot exceed total students!");
            return;
        }

        // Validation to check if there are enough classrooms
        if (numClassrooms <= 0) {
            JOptionPane.showMessageDialog(this, "Number of classrooms must be greater than zero!");
            return;
        }

        // Prompt user to enter seating capacity for each classroom
        seatingCapacityFields = new JTextField[numClassrooms];
        JPanel capacityPanel = new JPanel(new GridLayout(numClassrooms, 2));
        for (int i = 0; i < numClassrooms; i++) {
            capacityPanel.add(new JLabel("Seats in Classroom " + (i + 1) + ":"));
            seatingCapacityFields[i] = new JTextField(5);
            capacityPanel.add(seatingCapacityFields[i]);
        }

        int response = JOptionPane.showConfirmDialog(this, capacityPanel, "Enter Seating Capacities", JOptionPane.OK_CANCEL_OPTION);
        if (response != JOptionPane.OK_OPTION) {
            return; // User canceled, exit the action
        }

        // Check total seating capacity
        int totalSeatingCapacity = 0;
        for (int i = 0; i < numClassrooms; i++) {
            totalSeatingCapacity += Integer.parseInt(seatingCapacityFields[i].getText());
        }

        // Validate if total seating capacity is sufficient for attended students
        if (totalSeatingCapacity < attendedStudents) {
            JOptionPane.showMessageDialog(this, "Not enough seating capacity for attended students!\n" +
                    "Total seating capacity: " + totalSeatingCapacity + "\n" +
                    "Attended students: " + attendedStudents);
            return;
        }

        // Create student map
        studentsMap = new HashMap<>();
        for (int i = 1; i <= totalStudents; i++) {
            studentsMap.put("Student " + i, i <= attendedStudents);
        }

        displaySeatingArrangement(numClassrooms, attendedStudents);
        displayAbsentStudents();
    }

    private void displaySeatingArrangement(int numClassrooms, int attendedStudents) {
        seatingListArea.setText("");
        seatingListArea.append("Exam: " + examNameField.getText() + "\n\n");

        int currentClassroom = 1;
        int seatNumber = 1;
        int[] seatingCapacities = new int[numClassrooms];

        // Get seating capacities from input fields
        for (int i = 0; i < numClassrooms; i++) {
            seatingCapacities[i] = Integer.parseInt(seatingCapacityFields[i].getText());
        }

        for (String student : studentsMap.keySet()) {
            if (studentsMap.get(student)) {
                seatingListArea.append("Classroom " + currentClassroom + " - Seat No " + seatNumber + ": " + student + "\n");
                seatNumber++;

                // Check if the current classroom has reached its seating capacity
                if (seatNumber > seatingCapacities[currentClassroom - 1]) {
                    currentClassroom++;
                    seatNumber = 1;
                }

                // If current classroom exceeds the number of classrooms, break out
                if (currentClassroom > numClassrooms) {
                    break;
                }
            }
        }
    }

    private void displayAbsentStudents() {
        absentListArea.setText("");
        absentListArea.append("Absent Students:\n\n");
        for (String student : studentsMap.keySet()) {
            if (!studentsMap.get(student)) {
                absentListArea.append(student + "\n");
            }
        }
    }

    public static void main(String[] args) {
        new ExamSeatingArrangement();
    }
}
