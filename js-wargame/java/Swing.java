import javax.swing.*;

public class Swing {
    JTextField TextField1 = new JTextField(20);
    JTextField TextField2 = new JTextField(20);


    JLabel Label = new JLabel(System.getenv().getOrDefault("FLAG", "DH{FAKE_FLAG}"));

    Swing(String t1, String t2) {
        TextField1.addActionListener(e -> Label.setText(TextField1.getText() + Label.getText()));
        TextField2.addActionListener(e -> Label.setText(Label.getText() + TextField2.getText()));

        TextField1.setText(t1);
        TextField2.setText(t2);


        TextField1.postActionEvent();
        TextField2.postActionEvent();
    }

    public static void main(String[] args) throws Exception {        
        System.setProperty("java.awt.headless", "true");
        SwingUtilities.invokeAndWait(() -> new Swing(args[0], args[1]));
    }
}
