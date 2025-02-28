import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CalculatorFrame extends JFrame {
    private JTextField displayField;
    private JPanel buttonPanel;
    
    public CalculatorFrame() {
        setTitle("Calculadora de Tablas de Verdad");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        initComponents();
        pack();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        // Panel de visualización
        JPanel displayPanel = new JPanel(new BorderLayout());
        displayField = new JTextField();
        displayField.setFont(new Font("Monospaced", Font.PLAIN, 18));
        displayField.setHorizontalAlignment(JTextField.RIGHT);
        displayField.setEditable(false);
        displayField.setBackground(new Color(200, 230, 140));
        displayPanel.add(displayField, BorderLayout.CENTER);
        add(displayPanel, BorderLayout.NORTH);
        
        // Panel de botones
        buttonPanel = new JPanel(new GridLayout(5, 4, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Crear botones
        String[] buttonLabels = {
            "AC", "DEL", "(", ")",
            "~", "^", "V", "→",
            "p", "q", "↔", "⊕",
            "r", "s", "t", "=", "?"
        };
        
        for (String label : buttonLabels) {
            JButton button = createButton(label);
            buttonPanel.add(button);
            
            // Personalizar algunos botones específicos
            switch(label) {
                case "AC":
                    button.setForeground(Color.RED);
                    break;
                case "DEL":
                    button.setForeground(Color.RED);
                    break;
                case "=":
                    button.setBackground(new Color(120, 190, 230));
                    break;
            }
        }
        
        add(buttonPanel, BorderLayout.CENTER);
    }
    
    private JButton createButton(String label) {
        JButton button = new JButton(label);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lógica de cada botón
                if (label.equals("AC")) {
                    displayField.setText("");
                } else if (label.equals("DEL")) {
                    String currentText = displayField.getText();
                    if (!currentText.isEmpty()) {
                        displayField.setText(currentText.substring(0, currentText.length() - 1));
                    }
                } else if (!label.equals("=")) {
                    displayField.setText(displayField.getText() + label);
                }
            }
        });
        return button;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CalculatorFrame().setVisible(true);
            }
        });
    }
}