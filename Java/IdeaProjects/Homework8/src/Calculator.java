import java.awt.*;
import java.awt.event.*;

public class Calculator extends Frame implements ActionListener {
    //定义组件
    TextField display;
    Button[] numberButtons = new Button[10];
    Button addButton, subButton, mulButton, divButton, equButton, clrButton;
    String operator = "";
    double num1, num2, result;

    public Calculator() {
        //设置框架
        setTitle("Calculator");
        setSize(400, 600);
        setLayout(new BorderLayout());
        setVisible(true);
        
        //创建显示屏
        display = new TextField();
        display.setEditable(false);
        add(display, BorderLayout.NORTH);
        
        //创建面板并设置布局
        Panel panel = new Panel();
        panel.setLayout(new GridLayout(4, 4, 10, 10));
        
        //添加数字按钮
        for (int i = 0; i < 10; i++) {
            numberButtons[i] = new Button(String.valueOf(i));
            numberButtons[i].addActionListener(this);
        }
        
        //创建运算符按钮并添加动作监听
        addButton = new Button("+");
        subButton = new Button("-");
        mulButton = new Button("*");
        divButton = new Button("/");
        equButton = new Button("=");
        clrButton = new Button("C");

        addButton.addActionListener(this);
        subButton.addActionListener(this);
        mulButton.addActionListener(this);
        divButton.addActionListener(this);
        equButton.addActionListener(this);
        clrButton.addActionListener(this);

        //将按钮添加到面板
        for (int i = 1; i < 10; i++) {
            panel.add(numberButtons[i]);
        }
        panel.add(addButton);
        panel.add(numberButtons[0]);
        panel.add(subButton);
        panel.add(mulButton);
        panel.add(divButton);
        panel.add(equButton);
        panel.add(clrButton);

        //将面板添加到框架
        add(panel, BorderLayout.CENTER);

        //添加窗口监听以关闭应用程序
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if ((command.charAt(0) >= '0' && command.charAt(0) <= '9')) {
            display.setText(display.getText() + command);
        } else if (command.equals("+")) {
            num1 = Double.parseDouble(display.getText());
            operator = "+";
            display.setText("");
        } else if (command.equals("-")) {
            num1 = Double.parseDouble(display.getText());
            operator = "-";
            display.setText("");
        } else if (command.equals("*")) {
            num1 = Double.parseDouble(display.getText());
            operator = "*";
            display.setText("");
        } else if (command.equals("/")) {
            num1 = Double.parseDouble(display.getText());
            operator = "/";
            display.setText("");
        } else if (command.equals("=")) {
            num2 = Double.parseDouble(display.getText());
            switch (operator) {
                case "+":
                    result = num1 + num2;
                    break;
                case "-":
                    result = num1 - num2;
                    break;
                case "*":
                    result = num1 * num2;
                    break;
                case "/":
                    result = num1 / num2;
                    break;
            }
            display.setText(String.valueOf(result));
            operator = "";
        } else if (command.equals("C")) {
            display.setText("");
            num1 = num2 = result = 0;
            operator = "";
        }
    }

    public static void main(String[] args) {
        new Calculator();
    }
}
