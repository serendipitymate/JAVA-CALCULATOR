import javax.swing.*;
public abstract class BaseCalculatorLogic {
    protected JTextField textfield;

    public BaseCalculatorLogic(JTextField textfield) {
        this.textfield = textfield;
    }

    public abstract void performOperation(char operation);

    public abstract void handleModeSelection(int modeChoice);

    public abstract void handleNegation();
    
    public abstract void handleHistory();
}