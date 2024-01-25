import javax.swing.*;
import java.awt.*;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CalculatorUI implements ActionListener {
    private JFrame frame;
    private JTextField textfield;
    private JButton[] numberButtons = new JButton[10];
    private JButton[] functionButtons = new JButton[9];
    private JButton addButton, subButton, mulButton, divButton, decButton, equButton, delButton, clrButton;
    private JButton modeButton; // Added mode button
    private JPanel panel;
    private CalculatorLogic calculatorLogic; // Now an instance variable

    public CalculatorUI() {
        calculatorLogic = new CalculatorLogic(new JTextField());
    }

    public void start() {
        frame = new JFrame("Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(520, 550); // Increased width to accommodate the sidebar
        frame.setLayout(null);

        textfield = new JTextField();
        textfield.setBounds(50, 25, 300, 50);
        textfield.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        textfield.setEditable(false);

        addButton = new JButton("+");
        subButton = new JButton("-");
        mulButton = new JButton("*");
        divButton = new JButton("/");
        decButton = new JButton(".");
        equButton = new JButton("=");
        delButton = new JButton("DEL");
        delButton.addActionListener(e -> calculatorLogic.handleDelete());
        clrButton = new JButton("AC");
        
        textfield.setBackground(new Color(240, 240, 240)); // Light Gray
        textfield.setForeground(Color.DARK_GRAY);
        
        addButton.setBackground(new Color(245, 245, 245)); // Light Gray
        subButton.setBackground(new Color(245, 245, 245)); // Light Gray
        mulButton.setBackground(new Color(245, 245, 245)); // Light Gray
        divButton.setBackground(new Color(245, 245, 245)); // Light Gray
        decButton.setBackground(new Color(245, 245, 245)); // Light Gray
        equButton.setBackground(new Color(255, 200, 200)); // Light Red
        delButton.setBackground(new Color(135, 206, 250)); // Light Sky Blue
        delButton.setForeground(Color.WHITE);
        clrButton.setBackground(new Color(135, 206, 250)); // Light Sky Blue
        clrButton.setForeground(Color.WHITE);
        
        functionButtons[0] = addButton;
        functionButtons[1] = subButton;
        functionButtons[2] = mulButton;
        functionButtons[3] = divButton;
        functionButtons[4] = decButton;
        functionButtons[5] = equButton;
        functionButtons[6] = delButton;
        functionButtons[7] = clrButton;
        
        calculatorLogic = new CalculatorLogic(textfield);

        for (int i = 0; i < 8; i++) {
            functionButtons[i].addActionListener(this);
            functionButtons[i].setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
            functionButtons[i].setFocusable(false);
        }

        for (int i = 0; i < 10; i++) {
            numberButtons[i] = new JButton(String.valueOf(i));
            numberButtons[i].addActionListener(this);
            numberButtons[i].setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));
            numberButtons[i].setFocusable(false);
        }

        clrButton.setBounds(210, 430, 100, 50);
        delButton.setBounds(90, 430, 100, 50);

        panel = new JPanel();
        panel.setBounds(50, 100, 300, 300);
        panel.setLayout(new GridLayout(4, 4, 10, 10));

        panel.add(numberButtons[1]);
        panel.add(numberButtons[2]);
        panel.add(numberButtons[3]);
        panel.add(addButton);
        panel.add(numberButtons[4]);
        panel.add(numberButtons[5]);
        panel.add(numberButtons[6]);
        panel.add(subButton);
        panel.add(numberButtons[7]);
        panel.add(numberButtons[8]);
        panel.add(numberButtons[9]);
        panel.add(mulButton);
        panel.add(decButton);
        panel.add(numberButtons[0]);
        panel.add(equButton);
        panel.add(divButton);

        // Adding the mode button to the sidebar
        modeButton = new JButton("Mode");
        modeButton.setBounds(370, 25, 80, 50);
        modeButton.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        modeButton.setFocusable(false);
        modeButton.addActionListener(this);

        frame.add(modeButton);
        frame.add(panel);
        frame.add(delButton);
        frame.add(clrButton);
        frame.add(textfield);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == modeButton) {
            handleModeSelection(); // Handle mode button click event
        } else {
            for (int i = 0; i < 10; i++) {
                if (e.getSource() == numberButtons[i]) {
                    textfield.setText(textfield.getText().concat(String.valueOf(i)));
                }
            }

            /*if (e.getSource() == decButton) {
                textfield.setText(textfield.getText().concat("."));
            } */

            for (int i = 0; i < 9; i++) {
                if (e.getSource() == functionButtons[i]) {
                    calculatorLogic.performOperation(functionButtons[i].getText().charAt(0));
                }
            }

            if (e.getSource() == clrButton) {
                textfield.setText("");
            }

            if (e.getSource() == delButton) {
                String string = textfield.getText();
                textfield.setText("");
                for (int i = 0; i < string.length() - 1; i++) {
                    textfield.setText(textfield.getText() + string.charAt(i));
                }
            }
        }
    }

    private void handleModeSelection() {
        String[] modeOptions = {"Convert Currency", "Round Off", "History"};

        String selectedMode = (String) JOptionPane.showInputDialog(
                null,
                "Select mode:",
                "Mode Selection",
                JOptionPane.QUESTION_MESSAGE,
                null,
                modeOptions,
                modeOptions[0]
        );

        if (selectedMode != null) {
            switch (selectedMode) {
                case "Convert Currency":
                    calculatorLogic.handleCurrencyConversion();
                    break;
                case "Round Off":
                    calculatorLogic.handleRoundOff();
                    break;
                case "History":
                    calculatorLogic.handleHistory();
                    break;
                default:
                    break;
            }
        }
    }

    public static void main(String[] args) {
        CalculatorUI calculatorUI = new CalculatorUI();
        calculatorUI.start();
    }
}
