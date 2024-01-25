import javax.swing.JTextField;
import javax.swing.JOptionPane;
import java.util.ArrayList;
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
    
                JComboBox<String> fromCurrencyBox = new JComboBox<>();
                JComboBox<String> toCurrencyBox = new JComboBox<>();
    
                fromCurrencyBox.addItem("USD : United States Dollar");
                fromCurrencyBox.addItem("EUR : Euro");
                fromCurrencyBox.addItem("GBP : Pound Sterling");
                fromCurrencyBox.addItem("MYR : Malaysian Ringgit");
    
                toCurrencyBox.addItem("USD : United States Dollar");
                toCurrencyBox.addItem("EUR : Euro");
                toCurrencyBox.addItem("GBP : Pound Sterling");
                toCurrencyBox.addItem("MYR : Malaysian Ringgit");
    
                String[] options = {"OK", "Cancel"};
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
                }
    
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.");
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
            return 0.0; // Handle the exception appropriately in your application
        }
    }
    
    private double getExchangeRate(String jsonData, String currency) {
        // Parse JSON to get exchange rate for the specified currency
        // You may want to use a JSON parsing library for a more robust solution
        // For simplicity, we'll use a basic string search here
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
