import javax.swing.JTextField;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;

public class CalculatorLogic extends BaseCalculatorLogic {
    private double num1;
    private double num2;
    private double result;
    private char operator;
    private List<String> history;

    public CalculatorLogic(JTextField textfield) {
        super(textfield);
        history = new ArrayList<>();
    }

    @Override
    public void performOperation(char operation) {
        switch (operation) {
            case '+':
            case '-':
            case '*':
            case '/':
                handleArithmeticOperation(operation);
                break;
            case '=':
                handleEqualsOperation();
                break;
        }
        updateHistory(operation);
    }

    @Override
    public void handleModeSelection(int modeChoice) {
        switch (modeChoice) {
            case 0:
                // Convert Currency
                handleCurrencyConversion();
                break;
            case 1:
                // Round Off
                handleRoundOff();
                break;
            case 2:
                // History
                handleHistory();
                break;
            default:
                break;
        }
    }

    @Override
    public void handleNegation() {
        String currentText = textfield.getText();
        if (!currentText.isEmpty() && !currentText.equals("Error")) {
            double currentNumber = Double.parseDouble(currentText);
            currentNumber *= -1;
            textfield.setText(String.valueOf(currentNumber));
        }
    }

    private void handleArithmeticOperation(char operation) {
        num1 = Double.parseDouble(textfield.getText());
        operator = operation;
        textfield.setText("");
    }

    private void handleEqualsOperation() {
        num2 = Double.parseDouble(textfield.getText());
        switch (operator) {
            case '+':
                result = num1 + num2;
                break;
            case '-':
                result = num1 - num2;
                break;
            case '*':
                result = num1 * num2;
                break;
            case '/':
                if (num2 != 0) {
                    result = num1 / num2;
                } else {
                    // Handle division by zero error
                    textfield.setText("Error");
                    return;
                }
                break;
        }
        textfield.setText(String.format("%.2f", result));
        updateHistory('=');
    }
    
    private void updateHistory(char operation) {
        if (operation == '=') {
            String expression = num1 + " " + operator + " " + num2 + " = " + String.format("%.2f", result);
            if (!history.contains(expression)) {
                history.add(expression);
            }
        }
    }
    
    protected void handleCurrencyConversion() {
        String inputAmount = JOptionPane.showInputDialog(null, "Enter amount to convert:");

        if (inputAmount != null && !inputAmount.isEmpty()) {
            try {
                double amountToConvert = Double.parseDouble(inputAmount);

                String[] currencies = {"MYR", "SGD", "USD"};
                String fromCurrency = (String) JOptionPane.showInputDialog(
                        null,
                        "Select source currency:",
                        "Currency Converter",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        currencies,
                        currencies[0]
                );

                String toCurrency = (String) JOptionPane.showInputDialog(
                        null,
                        "Select target currency:",
                        "Currency Converter",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        currencies,
                        currencies[2]
                );

                double convertedAmount = convertCurrency(amountToConvert, fromCurrency, toCurrency);

                JOptionPane.showMessageDialog(
                        null,
                        amountToConvert + " " + fromCurrency + " = " + convertedAmount + " " + toCurrency,
                        "Currency Conversion Result",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.");
            }
        }
    }

    public double convertCurrency(double amount, String fromCurrency, String toCurrency) {
        // Simulated fixed exchange rates
        double myrToUsdRate = 0.25;
        double sgdToUsdRate = 0.75;

        double convertedAmount = 0.0;

        switch (fromCurrency) {
            case "MYR":
                convertedAmount = amount * myrToUsdRate;
                break;
            case "SGD":
                convertedAmount = amount * sgdToUsdRate;
                break;
            case "USD":
                convertedAmount = amount; // No conversion needed for USD
                break;
            default:
                break;
        }

        return convertedAmount;
    }

    protected void handleRoundOff() {
        String inputAmount = JOptionPane.showInputDialog(null, "Enter amount to round off:");

        if (inputAmount != null && !inputAmount.isEmpty()) {
            try {
                double amountToRound = Double.parseDouble(inputAmount);

                int decimalPlaces = Integer.parseInt(
                        JOptionPane.showInputDialog(null, "Enter number of decimal places:")
                );

                double roundedAmount = roundOff(amountToRound, decimalPlaces);

                JOptionPane.showMessageDialog(
                        null,
                        "Rounded off amount: " + roundedAmount,
                        "Round Off Result",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.");
            }
        }
    }

    public double roundOff(double amount, int decimalPlaces) {
        if (decimalPlaces < 0) {
            throw new IllegalArgumentException("Decimal places cannot be negative");
        }

        double multiplier = Math.pow(10, decimalPlaces);
        return Math.round(amount * multiplier) / multiplier;
    }
    
    @Override
    public void handleHistory() {
        StringBuilder historyText = new StringBuilder("Calculation History:\n");
        for (String expression : history) {
        historyText.append(expression).append("\n");
        }

        // Add "Clear History" button
        Object[] options = {"OK", "Clear History"};
        int choice = JOptionPane.showOptionDialog(null, historyText.toString(), "Calculation History",
        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 1) {
            clearHistory();
        // Do not call handleHistory() recursively after clearing
        }
    }    
    private void clearHistory() {
        history.clear();
        JOptionPane.showMessageDialog(null, "History Cleared", "Clear History", JOptionPane.INFORMATION_MESSAGE);
    }
}
