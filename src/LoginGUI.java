import Themes.FlatLafDarkCustom;
import com.formdev.flatlaf.FlatDarkLaf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class LoginGUI {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private JFrame jf;
    private Connection[] connection;

    private boolean connectionEstablished = false;
    private CountDownLatch latch;
    private int[] userID;
//    private static Properties userData = new Properties();
    public LoginGUI(CountDownLatch latch, int[] userID, Connection[] connection) {
        this.latch = latch;
        this.userID = userID;
        this.connection = connection;
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGui();
                if (internetConnection(jf)) {
                    new Thread(() -> {
                        connectToDatabase(jf);
                    }).start();
                } else {
                    System.err.println("No internet connection");
                }
            }
        });
    }
    private void createAndShowGui() {
        LOGGER.debug("Creating Login GUI");
        FlatDarkLaf.registerCustomDefaultsSource("Themes");
        FlatLafDarkCustom.setup();
        UIManager.put( "Button.arc", 999 );
        UIManager.put( "TextComponent.arc", 999 );
        UIManager.put("JTextField.selectAllOnFocusPolicy", "once");
        UIManager.put("TextComponent.selectAllOnMouseClick", true);
        UIManager.put("PasswordField.showCapsLock", true);
        UIManager.put("PasswordField.showRevealButton", true);

        jf = new JFrame();
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

        JTextField userLogin = new JTextField("Username");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.ipadx = 100;
        constraints.ipady = 20;
        constraints.anchor = GridBagConstraints.PAGE_START;
        userLogin.setHorizontalAlignment(JTextField.CENTER);
        constraints.insets = new Insets(-100,0,0,0);

        botPanel.add(userLogin,constraints);

        JPasswordField userPassword = new JPasswordField();
        constraints.gridy = 1;
        constraints.insets = new Insets(-50,0,0,0);
//        userPassword.setForeground(Color.white);
        userPassword.setHorizontalAlignment(JTextField.CENTER);
        botPanel.add(userPassword,constraints);

        JPasswordField userSecondPassword = new JPasswordField();
        GridBagConstraints secondConstraints = new GridBagConstraints();
        secondConstraints.gridx = 0;
        secondConstraints.gridy = 2;
        secondConstraints.weightx = 0;
        secondConstraints.weighty = 0;
        secondConstraints.ipadx = 100;
        secondConstraints.ipady = 20;
        secondConstraints.anchor = GridBagConstraints.PAGE_START;
        secondConstraints.insets = new Insets(0,0,0,0);
        userSecondPassword.setHorizontalAlignment(JTextField.CENTER);



        JButton loginButton = new JButton("Log in");

        constraints.gridy = 3;
        constraints.ipadx = 30;
        constraints.ipady = 10;
        constraints.insets = new Insets(5,0,0,0);
        loginButton.setHorizontalAlignment(JTextField.CENTER);
        botPanel.add(loginButton,constraints);

        JLabel registerLabel = new JLabel("I want to create a new account");
        constraints.gridy = 4;
        constraints.ipadx = 0;
        constraints.ipady = 0;
        registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        registerLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                userLogin.setText("Username");
                userPassword.setText("");
                userSecondPassword.setText("");
                if (registerLabel.getText().equals("I want to create a new account")) {
                    JOptionPane.showMessageDialog(jf, "Password needs to include at least 6 letters, one capital letter" +
                            "and one number");
                    loginButton.setText("Register");
                    registerLabel.setText("I already have an account");
                    botPanel.remove(loginButton);
                    botPanel.add(userSecondPassword, secondConstraints);
                    constraints.insets = new Insets(10,0,0,0);
                    constraints.ipadx = 30;
                    constraints.ipady = 10;
                    botPanel.remove(registerLabel);
                    botPanel.add(loginButton, constraints);
                    constraints.insets = new Insets(50,0,0,0);
                    constraints.ipadx = 0;
                    constraints.ipady = 0;
                    botPanel.add(registerLabel,constraints);
                    jf.validate();
                    jf.repaint();
                    return;
                }
                loginButton.setText("Log in");
                registerLabel.setText("I want to create a new account");
                botPanel.remove(userSecondPassword);
                botPanel.remove(loginButton);
                botPanel.remove(registerLabel);
                constraints.insets = new Insets(5,0,0,0);
                constraints.ipadx = 30;
                constraints.ipady = 10;
                botPanel.add(loginButton, constraints);
                constraints.insets = new Insets(45,0,0,0);
                constraints.ipadx = 0;
                constraints.ipady = 0;
                botPanel.add(registerLabel,constraints);
                jf.validate();
                jf.repaint();

            }
        });
        botPanel.add(registerLabel,constraints);

        jf.add(botPanel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!connectionEstablished) {
                    if (!internetConnection(jf)) {
                        return;
                    }

                    CountDownLatch latchdatabaseConnection = new CountDownLatch(1);
                    loginButton.setEnabled(false);
                    new Thread(() -> {
                        if (connectToDatabase(jf, latchdatabaseConnection) != 0) {
                            return;
                        }
                    }).start();
                    try {
                        latchdatabaseConnection.await();
                    } catch (InterruptedException err) {
                        err.printStackTrace();
                    }
                    loginButton.setEnabled(true);
                }

                if (loginButton.getText().equals("Log in")) {
                    loginProcess(userLogin.getText(), String.valueOf(userPassword.getPassword()), jf);
                    return;
                }

                registerProcess(userLogin.getText(), String.valueOf(userPassword.getPassword()),
                        String.valueOf(userSecondPassword.getPassword()), jf);

            }
        });
        jf.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                latch.countDown();
            }
        });
    }

    private int connectToDatabase(JFrame frame) {
        LOGGER.debug("Connecting to the database");
        Properties databaseLoginParameters = new Properties();
        try {
            databaseLoginParameters.load(LoginGUI.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException ioException) {
            LOGGER.fatal("Unable to connect to the database. Connection parameters file \"application.properties\" not found");
            JOptionPane.showMessageDialog(frame, "Unable to connect to the database. Connection parameters file \"application.properties\" not found",
                    "Database connection", JOptionPane.ERROR_MESSAGE);
            return 0;
        }
        try {
            connection[0] =DriverManager.getConnection(databaseLoginParameters.getProperty("url"));
        } catch (SQLException sqlException) {
            LOGGER.fatal("Unable to connect to the database. Connection could not be established");
            JOptionPane.showMessageDialog(frame, "Unable to connect to the database. Connection could not be established",
                    "Database connection", JOptionPane.ERROR_MESSAGE);
            return 0;
        }
        this.connectionEstablished = true;
        LOGGER.info("Successfully connected to the database");
        return 1;
    }
    private int connectToDatabase(JFrame frame, CountDownLatch latch) {
        int retValue = connectToDatabase(frame);
        latch.countDown();
        return retValue;
    }


    private boolean internetConnection(JFrame frame) {
        LOGGER.debug("Checking Internet connection");
        try {
            URL url = new URL("http://www.google.com");
            URLConnection internetConnection = url.openConnection();
            internetConnection.connect();
        } catch (Exception exception) {
            LOGGER.fatal("No Internet connection");
            JOptionPane.showMessageDialog(frame, "No Internet connection",
                    "No Internet connection", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        LOGGER.info("Internet connection: OK");
        return true;
    }

    private boolean passwordValid(String firstPassword, String secondPassword, JFrame frame) {
        LOGGER.debug("Checking if password can be accepted");
        if (!firstPassword.equals(secondPassword)) {
            LOGGER.warn("Passwords are not the same");
            JOptionPane.showMessageDialog(frame, "Passwords are not the same",
                    "Incorrectly set password", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (firstPassword.length() < 6) {
            LOGGER.warn("Password needs to include at least 6 letters");
            JOptionPane.showMessageDialog(frame, "Password needs to include at least one 6 letters",
                    "Incorrectly set password", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (firstPassword.equals(firstPassword.toLowerCase())) {
            LOGGER.warn("Password needs to include at least one uppercase letter");
            JOptionPane.showMessageDialog(frame, "Password needs to include at least one uppercase letter",
                    "Incorrectly set password", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!firstPassword.matches(".*\\d.*")) {
            LOGGER.warn("Password needs to include at least one number");
            JOptionPane.showMessageDialog(frame, "Password needs to include at least one number!",
                    "Incorrectly set password", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        LOGGER.info("Password is accepted");
        return true;
    }

    private void loginProcess(String username, String password, JFrame frame) {
        LOGGER.info("Trying to log in the user");
        try (CallableStatement stmt = connection[0].prepareCall("{CALL check_user_credentials(?,?,?)}")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.registerOutParameter(3, java.sql.Types.INTEGER);
            stmt.execute();
            userID[0] = stmt.getInt(3);
        } catch (SQLException sqlException) {
            LOGGER.fatal("Unable to access the database");
            return;
        }

        if (userID[0] == 0) {
            LOGGER.warn("Invalid username or password");
            JOptionPane.showMessageDialog(frame, "Invalid username or password",
                    "Unable to log in", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LOGGER.info("Successful log in");
        frame.dispose();
        latch.countDown();
    }
    private void registerProcess(String username, String firstPassword, String secondPassword, JFrame frame) {
        LOGGER.info("Trying to register the user");
        if (!passwordValid(firstPassword, secondPassword, frame)) {
            return;
        }

        boolean usernameAlreadyUsed;
        LOGGER.debug("Checking if username is not in use");
        try (CallableStatement cs = connection[0].prepareCall("{call check_username_exists(?,?)}")) {
            cs.setString(1, username);
            cs.registerOutParameter(2, Types.BIT);
            cs.execute();
            usernameAlreadyUsed = cs.getBoolean(2);
        } catch (SQLException sqlException) {
            LOGGER.fatal("Unable to access the database");
            return;
        }

        if (usernameAlreadyUsed) {
            LOGGER.warn("Username already used");
            JOptionPane.showMessageDialog(frame, "Username already used",
                    "Username already used", JOptionPane.WARNING_MESSAGE);
            return;
        }
        System.out.println(usernameAlreadyUsed);
        LOGGER.info("Username is not used, so OK");

        LOGGER.debug("Trying to insert data into database");
        try (CallableStatement cstmt = connection[0].prepareCall("{CALL insert_client(?, ?)}")) {
            cstmt.setString(1, username);
            cstmt.setString(2, firstPassword);
            cstmt.execute();
        } catch (SQLException sqlException) {
            LOGGER.fatal("Unable to access the database");
            return;
        }
        LOGGER.info("Data successfully inserted into database. User correctly created");
        loginProcess(username, firstPassword, frame);
    }
}
