import Themes.FlatLafDarkCustom;
import com.formdev.flatlaf.FlatDarkLaf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class Note extends LoginGUI{
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private int noteID;
    private String title;
    private JFrame jf;
    private CountDownLatch latch;
    private Connection[] connection = new Connection[1];
    private CountDownLatch connectionLatch = new CountDownLatch(1);
    private boolean connectionEstablished = false;

    public Note(int noteID, String title,CountDownLatch latch) {
        this.latch = latch;
        this.noteID = noteID;
        this.title = title;

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGui();
            }
        });
    }
    private void createAndShowGui() {
        LOGGER.info("Note successfully opened");
        FlatDarkLaf.registerCustomDefaultsSource("Themes");
        FlatLafDarkCustom.setup();
        UIManager.put( "Button.arc", 0 );
        UIManager.put( "TextComponent.arc", 0 );

        jf = new JFrame();
        jf.setVisible(true);
        jf.setResizable(false);
        jf.setSize(400, 400);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        if (internetConnection(jf)) {
            new Thread(() -> {
                connectToDatabase(jf);
            }).start();
        } else {
            LOGGER.fatal("No internet connection");
        }

        try {
            connectionLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Title");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        titleLabel.setHorizontalAlignment(JTextField.LEFT);
        titleLabel.setMaximumSize(new Dimension(30,30));
        titleLabel.setMaximumSize(new Dimension(30,30));
        panel.add(titleLabel);

        JTextField titleInput = new JTextField(title);
        titleInput.setMinimumSize(new Dimension(400, 120));
        titleInput.setMaximumSize(new Dimension(400, 120));
        panel.add(titleInput);

        JTextArea input = new JTextArea(downloadNote());
//        panel.add(input);

        panel.add(new JScrollPane(input, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

        jf.getContentPane().add(panel);
        latch.countDown();

        jf.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (!connectionEstablished) {
                    jf.dispose();
                }

                LOGGER.debug("Trying to save the note before closing.");
                CountDownLatch latch = new CountDownLatch(1);
                new Thread(() -> {
                    updateNote(titleInput.getText(), input.getText(), latch);
                }).start();
                try {
                    latch.await();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                try {
                    connection[0].close();
                } catch (SQLException ex) {
                    LOGGER.warn("Problem with closing Note's connection to the database occurred");
                }
                jf.dispose();
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
            connectionLatch.countDown();
            return 0;
        }
        try {
            connection[0] = DriverManager.getConnection(databaseLoginParameters.getProperty("url"));
        } catch (SQLException sqlException) {
            LOGGER.fatal("Unable to connect to the database. Connection could not be established");
            JOptionPane.showMessageDialog(frame, "Unable to connect to the database. Connection could not be established",
                    "Database connection", JOptionPane.ERROR_MESSAGE);
            connectionLatch.countDown();
            return 0;
        }
        this.connectionEstablished = true;
        connectionLatch.countDown();
        LOGGER.info("Successfully connected to the database");
        return 1;
    }

    private void updateNote(String title, String note, CountDownLatch latch) {
        try {
            CallableStatement statement = connection[0].prepareCall("{call update_note(?,?,?)}");
            statement.setInt(1, noteID);
            statement.setString(2, title);
            statement.setString(3, note);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Unable to save the note");
            latch.countDown();
            return;
        }
        latch.countDown();
        LOGGER.info("Successfully saved the note");
    }

    private String downloadNote() {
        String note;
        try {
            CallableStatement stmt = connection[0].prepareCall("{call download_note(?)}");
            stmt.setInt(1, noteID);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                LOGGER.info("Note successfully loaded from the database. The note was empty.");
                connectionEstablished = false;
                jf.dispose();
                return "";
            }
            note = rs.getString("note");
        } catch (SQLException e) {
            LOGGER.error("Cannot load note from the database");
            connectionEstablished = false;
            jf.dispose();
            return "";
        }
        LOGGER.info("Note successfully loaded from the database");
        return note;
    }
    public boolean checkStatus() {
        return jf.isShowing();
    }
}
