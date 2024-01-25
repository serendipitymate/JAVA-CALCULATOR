import javax.swing.JTextField;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.io.*;
import javax.swing.JComboBox;

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
        String currentText = textfield.getText();
    
        // Validate if the current expression is valid
        if (!isValidExpression(currentText, operation)) {
            textfield.setText("Error");
            return;
        }
    
        // If the operation is '=', evaluate the entire expression
        if (operation == '=') {
            evaluateExpression(currentText);
            updateHistory(currentText + " = " + String.format("%.2f", result));  // Update history with the entire expression and result
        } else {
            // For other operations, append them to the current expression
            textfield.setText(currentText + operation);
        }
    }
    
    private boolean isValidExpression(String currentText, char operation) {
        // Check if the current expression is valid for the given operation
        if (currentText.isEmpty() || currentText.equals("Error")) {
            return false; // Invalid if the current text is empty or already an error
        }
    
        char lastChar = currentText.charAt(currentText.length() - 1);
    
        // Invalid if the last character is an operator and the new operation is also an operator
        if (isOperator(lastChar) && isOperator(operation)) {
            return false;
        }
    
        // For all other cases, the expression is considered valid
        return true;
    }

    
    private boolean isOperator(char c) {
        // Check if the character is an arithmetic operator
        return c == '+' || c == '-' || c == '*' || c == '/';
    }
    
    private void evaluateExpression(String expression) {
        try {
            // Split the expression into individual operations
            String[] operations = expression.split("(?<=[-+*/=])|(?=[-+*/=])");
    
            // Convert the array to a list for easy removal of elements
            List<String> operationList = new ArrayList<>(Arrays.asList(operations));
    
            // Process brackets first
            while (operationList.contains("(")) {
                int openIndex = operationList.lastIndexOf("(");
                int closeIndex = operationList.subList(openIndex, operationList.size()).indexOf(")") + openIndex;
    
                if (openIndex >= 0 && closeIndex > openIndex) {
                    // Evaluate the expression within the brackets
                    evaluateExpression(String.join("", operationList.subList(openIndex + 1, closeIndex)));
    
                    // Replace the bracketed expression with its result
                    operationList.subList(openIndex, closeIndex + 1).clear();
                    operationList.add(openIndex, String.valueOf(result));
                } else {
                    // Mismatched brackets
                    textfield.setText("Error");
                    return;
                }
            }
    
            // Process powers/exponents
            for (int i = 0; i < operationList.size(); i++) {
                if (operationList.get(i).equals("^")) {
                    double base = Double.parseDouble(operationList.get(i - 1));
                    double exponent = Double.parseDouble(operationList.get(i + 1));
                    double result = Math.pow(base, exponent);
    
                    // Replace the base, exponent, and "^" with the result
                    operationList.subList(i - 1, i + 2).clear();
                    operationList.add(i - 1, String.valueOf(result));
                    i--; // Move back to the updated position
                }
            }
    
            // Process multiplication and division
            for (String operator : new String[]{"*", "/"}) {
                while (operationList.contains(operator)) {
                    int operatorIndex = operationList.indexOf(operator);
    
                    double operand1 = Double.parseDouble(operationList.get(operatorIndex - 1));
                    double operand2 = Double.parseDouble(operationList.get(operatorIndex + 1));
                    double result = operator.equals("*") ? operand1 * operand2 : operand1 / operand2;
    
                    // Replace the operands and operator with the result
                    operationList.subList(operatorIndex - 1, operatorIndex + 2).clear();
                    operationList.add(operatorIndex - 1, String.valueOf(result));
                }
            }
    
            // Process addition and subtraction
            for (String operator : new String[]{"+", "-"}) {
                while (operationList.contains(operator)) {
                    int operatorIndex = operationList.indexOf(operator);
    
                    double operand1 = Double.parseDouble(operationList.get(operatorIndex - 1));
                    double operand2 = Double.parseDouble(operationList.get(operatorIndex + 1));
                    double result = operator.equals("+") ? operand1 + operand2 : operand1 - operand2;
    
                    // Replace the operands and operator with the result
                    operationList.subList(operatorIndex - 1, operatorIndex + 2).clear();
                    operationList.add(operatorIndex - 1, String.valueOf(result));
                }
            }
    
            // The final result should be at the first position in the list
            this.result = Double.parseDouble(operationList.get(0));
    
            // Update the textfield with the result
            textfield.setText(String.format("%.2f", this.result));
        } catch (NumberFormatException | ArithmeticException e) {
            // Handle invalid number format or arithmetic errors
            textfield.setText("Error");
        }
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
        updateHistory(num1 + "  " + operator + "  " + num2 + " = " + String.format("%.2f", result));
    }
    
    private void updateHistory(String expression) {
        if (!expression.isEmpty() && !history.contains(expression)) {
            history.add(expression);
        }
    }
    
    protected void handleCurrencyConversion() {
        boolean convertAgain = true;
    
        while (convertAgain) {
            String inputAmount = JOptionPane.showInputDialog(null, "Enter amount to convert:");
    
            if (inputAmount != null && !inputAmount.isEmpty()) {
                try {
                    double amountToConvert = Double.parseDouble(inputAmount);
    
                    JComboBox<String> fromCurrencyBox = new JComboBox<>();
                    JComboBox<String> toCurrencyBox = new JComboBox<>();
    
                    fromCurrencyBox.addItem("USD : United States Dollar");
                    fromCurrencyBox.addItem("JPY : Japanese Yen");
                    fromCurrencyBox.addItem("CNY : Chinese Yuan");
                    fromCurrencyBox.addItem("KRW : South Korean Won");
                    fromCurrencyBox.addItem("SGD : Singapore Dollar");
                    fromCurrencyBox.addItem("MYR : Malaysian Ringgit");
                    fromCurrencyBox.addItem("INR : Indian Rupee");
                    fromCurrencyBox.addItem("IDR : Indonesian Rupiah");
                    fromCurrencyBox.addItem("THB : Thai Baht");
                    fromCurrencyBox.addItem("BND : Brunei Dollar");
                    fromCurrencyBox.addItem("MMK : Myanmar Kyat");
                    fromCurrencyBox.addItem("LAK : Laotian Kip");
                    fromCurrencyBox.addItem("VND : Vietnamese Dong");
                    fromCurrencyBox.addItem("KHR : Cambodian Riel");
                    fromCurrencyBox.addItem("PHP : Philippine Peso");
                    // Add more currencies as needed
                    
                    toCurrencyBox.addItem("USD : United States Dollar");
                    toCurrencyBox.addItem("JPY : Japanese Yen");
                    toCurrencyBox.addItem("CNY : Chinese Yuan");
                    toCurrencyBox.addItem("KRW : South Korean Won");
                    toCurrencyBox.addItem("SGD : Singapore Dollar");
                    toCurrencyBox.addItem("MYR : Malaysian Ringgit");
                    toCurrencyBox.addItem("INR : Indian Rupee");
                    toCurrencyBox.addItem("IDR : Indonesian Rupiah");
                    toCurrencyBox.addItem("THB : Thai Baht");
                    toCurrencyBox.addItem("BND : Brunei Dollar");
                    toCurrencyBox.addItem("MMK : Myanmar Kyat");
                    toCurrencyBox.addItem("LAK : Laotian Kip");
                    toCurrencyBox.addItem("VND : Vietnamese Dong");
                    toCurrencyBox.addItem("KHR : Cambodian Riel");
                    toCurrencyBox.addItem("PHP : Philippine Peso");
    
                    String[] options = {"OK", "Convert Again", "Cancel"};
                    Object[] message = {
                            "Enter amount to convert:", inputAmount,
                            "Select source currency:", fromCurrencyBox,
                            "Select target currency:", toCurrencyBox
                    };
    
                    int choice = JOptionPane.showOptionDialog(null, message, "Currency Converter", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    
                    if (choice == 0) { // OK button is pressed
                        String fromCurrency = fromCurrencyBox.getSelectedItem().toString().split(" ")[0];
                        String toCurrency = toCurrencyBox.getSelectedItem().toString().split(" ")[0];
    
                        double convertedAmount = convertCurrency(amountToConvert, fromCurrency, toCurrency);
    
                        JOptionPane.showMessageDialog(
                                null,
                                amountToConvert + " " + fromCurrency + " = " + convertedAmount + " " + toCurrency,
                                "Currency Conversion Result",
                                JOptionPane.INFORMATION_MESSAGE
                        );
    
                        // Ask the user if they want to convert again
                        choice = JOptionPane.showOptionDialog(
                                null,
                                "Do you want to convert again?",
                                "Convert Again",
                                JOptionPane.DEFAULT_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                new Object[]{"Yes", "No"},
                                "No"
                        );
    
                        convertAgain = (choice == 0); // Continue if "Yes" is chosen
                    } else {
                        // If the user clicks "Convert Again" or "Cancel", exit the loop
                        convertAgain = (choice == 1);
                    }
    
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.");
                }
            } else {
                // If the user clicks Cancel or closes the input dialog, exit the loop
                convertAgain = false;
            }
        }
    }

    private static final String EXCHANGE_RATE_API = "https://api.exchangerate-api.com/v4/latest/USD";

    public double convertCurrency(double amount, String fromCurrency, String toCurrency) {
        try {
            URL url = new URL(EXCHANGE_RATE_API);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream responseStream = connection.getInputStream();
            Scanner scanner = new Scanner(responseStream).useDelimiter("\\A");
            String responseData = scanner.hasNext() ? scanner.next() : "";

            // Parse JSON response to get exchange rates
            double fromRate = getExchangeRate(responseData, fromCurrency);
            double toRate = getExchangeRate(responseData, toCurrency);

            // Calculate converted amount
            double convertedAmount = amount * (toRate / fromRate);

            // Close resources
            responseStream.close();
            connection.disconnect();

            return convertedAmount;
        } catch (IOException e) {
            e.printStackTrace();
            return 0.0; // Handle the exception appropriately 
        }
    }
    
    private double getExchangeRate(String jsonData, String currency) {
        // Parse JSON to get exchange rate for the specified currency
        String key = "\""+currency+"\":";
        int index = jsonData.indexOf(key);
        if (index != -1) {
            int startIndex = index + key.length();
            int endIndex = jsonData.indexOf(",", startIndex);
            if (endIndex == -1) {
                endIndex = jsonData.indexOf("}", startIndex);
            }
            String rateString = jsonData.substring(startIndex, endIndex);
            return Double.parseDouble(rateString);
        } else {
            return 1.0; // Default to 1.0 if the currency is not found
        }
    }
    
    public void handleDelete() {
        String currentText = textfield.getText();

        // Check if the current expression is not empty
        if (!currentText.isEmpty() && !currentText.equals("Error")) {
            // Remove the last character from the current expression
            String newText = currentText.substring(0, currentText.length() - 1);
            textfield.setText(newText);
        }
    }

    protected void handleRoundOff() {
        boolean continueRoundOff = true;
    
        while (continueRoundOff) {
            String inputAmount = JOptionPane.showInputDialog(null, "Enter amount to round off:");
    
            if (inputAmount != null && !inputAmount.isEmpty()) {
                try {
                    double amountToRound = Double.parseDouble(inputAmount);
    
                    String[] options = {"Ten", "Hundred", "Thousand"};
                    int choice = JOptionPane.showOptionDialog(
                            null,
                            "Select round off option:",
                            "Round Off Options",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]
                    );
    
                    double roundedAmount;
    
                    switch (choice) {
                        case 0:
                            roundedAmount = roundOffToNearest(amountToRound, 10);
                            break;
                        case 1:
                            roundedAmount = roundOffToNearest(amountToRound, 100);
                            break;
                        case 2:
                            roundedAmount = roundOffToNearest(amountToRound, 1000);
                            break;
                        default:
                            roundedAmount = amountToRound; // Default to no rounding
                    }
    
                    int option = JOptionPane.showOptionDialog(
                            null,
                            "Original amount: " + amountToRound + "\nRounded off to nearest " + options[choice] + ": " + roundedAmount,
                            "Round Off Result",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null,
                            new Object[]{"OK", "Round Off Again"},
                            "OK"
                    );
    
                    continueRoundOff = (option == 1); // Continue if "Round Off Again" is chosen
    
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.");
                }
            } else {
                // If the user clicks Cancel or closes the input dialog, exit the loop
                continueRoundOff = false;
            }
        }
    }

    
    private double roundOffToNearest(double amount, int interval) {
        return Math.round(amount / interval) * interval;
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