import Themes.FlatLafDarkCustom;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class LoginGUI {
    private CountDownLatch latch;
    private String[] userData;
//    private static Properties userData = new Properties();
    public LoginGUI(CountDownLatch latch, String[] userData) {
        this.latch = latch;
        this.userData = userData;
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
        UIManager.put( "Button.arc", 999 );
        UIManager.put( "TextComponent.arc", 999 );
        UIManager.put("JTextField.selectAllOnFocusPolicy", "once");
        UIManager.put("TextComponent.selectAllOnMouseClick", true);
        UIManager.put("PasswordField.showCapsLock", true);
        UIManager.put("PasswordField.showRevealButton", true);

        JFrame jf = new JFrame();
        jf.setVisible(true);
        jf.setResizable(false);
        jf.setSize(400, 600);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setLayout(new BoxLayout(jf.getContentPane(), BoxLayout.PAGE_AXIS));

        JPanel topPanel = new JPanel();
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

        JTextField userLogin = new JTextField("LOGIN");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.ipadx = 100;
        constraints.ipady = 20;
        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.insets = new Insets(-100,0,0,0);

        botPanel.add(userLogin,constraints);

        JPasswordField userPassword = new JPasswordField();
        constraints.gridy = 1;
        constraints.insets = new Insets(-50,0,0,0);
        userPassword.setForeground(Color.white);
        userPassword.setHorizontalAlignment(JTextField.CENTER);
        botPanel.add(userPassword,constraints);

        JButton loginButton = new JButton("LOG IN");
        constraints.gridy = 2;
        constraints.ipadx = 30;
        constraints.ipady = 10;
        constraints.insets = new Insets(5,0,0,0);
        loginButton.setForeground(Color.white);
        loginButton.setHorizontalAlignment(JTextField.CENTER);
        botPanel.add(loginButton,constraints);

        jf.add(botPanel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setUserData(userLogin.getText(), String.valueOf(userPassword.getPassword()));
                System.out.println("Button pressed");
            }
        });
//        JUST TO TEST THINGS, NOT USED LATER IN FINAL VERSION
        jf.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                latch.countDown();
            }
        });
    }
    public void setUserData(String login, String password) {
        userData[0] = login.isEmpty() ? null : login;
        userData[1] = password.isEmpty() ? null : password;
    }
}
