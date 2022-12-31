import Themes.FlatLafDarkCustom;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

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
        FlatDarkLaf.registerCustomDefaultsSource("Themes");
        FlatLafDarkCustom.setup();
//        try {
//            UIManager.setLookAndFeel(new FlatDarkLaf());
//        } catch (Exception e) {
//            System.err.println("Failed to initialize LaF");
//            System.exit(1);
//        }
        UIManager.put( "Button.arc", 999 );
        UIManager.put( "TextComponent.arc", 999 );
//        UIManager.put("Panel.background",new Color(26,30,36));
//        UIManager.put("TextComponent.selectAllOnFocusPolicy","once");
//        UIManager.put("TextComponent.selectAllOnMouseClick",true);
//        UIManager.put("PasswordField.showRevealButton",true);
        JFrame jf = new JFrame();
        jf.setVisible(true);
        jf.setResizable(false);
        jf.setSize(400, 600);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setLayout(new BoxLayout(jf.getContentPane(), BoxLayout.PAGE_AXIS));

        JPanel topPanel = new JPanel();
//        topPanel.putClientProperty("Panel.background",new Color(26,30,36));
//        topPanel.setBackground(new Color(26,30,36));
        topPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 250));
        topPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.anchor = GridBagConstraints.PAGE_END;
        constraints.insets = new Insets(0,0,30,0);

        JLabel LogoImage = new JLabel(new ImageIcon(Objects.requireNonNull(Main.class.getResource("Images/LogoTemp.png"))));
        topPanel.add(LogoImage,constraints);
        jf.add(topPanel);

        JPanel botPanel = new JPanel();
        botPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 350));
        botPanel.setLayout(new GridBagLayout());

        JTextField UserLogin = new JTextField("LOGIN");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.ipadx = 100;
        constraints.ipady = 20;
        constraints.anchor = GridBagConstraints.PAGE_START;
//        UserLogin.setForeground(Color.white);
//        UserLogin.putClientProperty("JTextField.background",Color.RED);
//        UserLogin.putClientProperty("TextField.border", BorderFactory.createLineBorder(Color.RED));
//        UserLogin.putClientProperty("TextComponent.selectAllOnFocusPolicy","always");
//        UserLogin.putClientProperty("TextComponent.selectAllOnMouseClick",true);
//        UserLogin.setHorizontalAlignment(JTextField.CENTER);
        constraints.insets = new Insets(-100,0,0,0);

        botPanel.add(UserLogin,constraints);

        JPasswordField UserPassword = new JPasswordField();
        constraints.gridy = 1;
        constraints.insets = new Insets(-50,0,0,0);
        UserPassword.setForeground(Color.white);
//        UserPassword.putClientProperty("PasswordField.showRevealButton",true);
        UserPassword.setHorizontalAlignment(JTextField.CENTER);
        botPanel.add(UserPassword,constraints);

        JButton loginButton = new JButton("LOG IN");
        constraints.gridy = 2;
        constraints.ipadx = 30;
        constraints.ipady = 10;
        constraints.insets = new Insets(5,0,0,0);
        loginButton.setForeground(Color.white);
//        loginButton.putClientProperty("Button.borderWidth",20);
        loginButton.setHorizontalAlignment(JTextField.CENTER);
        botPanel.add(loginButton,constraints);

        jf.add(botPanel);

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
