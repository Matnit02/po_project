import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginGUI {
    private String result;
    public LoginGUI() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGui();
            }
        });
    }
    private void createAndShowGui() {
        JFrame jf = new JFrame();
        jf.setVisible(true);
        jf.setResizable(false);
        jf.setSize(400, 600);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        jf.getContentPane().setBackground(new Color(68,64,64));
        jf.setLayout(new BoxLayout(jf.getContentPane(), BoxLayout.Y_AXIS));

        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.red);
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22000));
        jf.add(topPanel);

        JPanel midPanel = new JPanel();
        midPanel.setBackground(Color.green);
        jf.add(midPanel);


        jf.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setResult("Working");
            }
        });
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
