import Themes.FlatLafDarkCustom;
import com.formdev.flatlaf.FlatDarkLaf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class NoteList {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private Connection connection;
    private int userID;
    private boolean DELETE = false;

    public NoteList(Connection connection, int userID) {
        this.connection = connection;
        this.userID = userID;

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }
    private void createAndShowGUI() {
        Map<JButton, Integer> buttonNoteIDMap = new HashMap<>();

        LOGGER.debug("Creating NoteList GUI");
        FlatDarkLaf.registerCustomDefaultsSource("Themes");
        FlatLafDarkCustom.setup();
        UIManager.put( "Button.arc", 0 );
        UIManager.put( "TextComponent.arc", 0 );

        JFrame frame = new JFrame();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setSize(350, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        JButton addNoteButton = new JButton("+");
        addNoteButton.setFont(new Font(addNoteButton.getFont().getName(), addNoteButton.getFont().getStyle(), 25));
        addNoteButton.setPreferredSize(new Dimension(30,30));
        topPanel.add(addNoteButton, BorderLayout.WEST);

        JLabel title = new JLabel("List of notes");
        title.setFont(title.getFont().deriveFont(Font.BOLD));
        title.setHorizontalAlignment(JTextField.CENTER);
        title.setPreferredSize(new Dimension(30,30));
        title.setFont(new Font(title.getFont().getName(), title.getFont().getStyle(), 20));
        topPanel.add(title, BorderLayout.CENTER);

        JPanel delPanel = new JPanel();
        JCheckBox deleteNoteCheckBox = new JCheckBox("");
        deleteNoteCheckBox.setPreferredSize(new Dimension(30,30));
        JLabel delIcon = new JLabel("DEL");
        delIcon.setPreferredSize(new Dimension(30,30));
        delPanel.setLayout(new BorderLayout());
        delPanel.add(delIcon, BorderLayout.EAST);
        delPanel.add(deleteNoteCheckBox, BorderLayout.WEST);
        topPanel.add(delPanel, BorderLayout.EAST);
        frame.getContentPane().add(topPanel, BorderLayout.NORTH);

        deleteNoteCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (deleteNoteCheckBox.isSelected()) {
                    DELETE = true;
                    LOGGER.info("DELETE flag set to true");
                    return;
                }
                DELETE = false;
                LOGGER.info("DELETE flag set to false");
            }
        });


        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        frame.getContentPane().add(BorderLayout.CENTER, new JScrollPane(panel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

        addNoteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                LOGGER.info("Trying to add a new note");
                title.setVisible(false);
                addNoteButton.setVisible(false);
                delPanel.setVisible(false);

                JTextField titleInsert = new JTextField();
                titleInsert.setFont(titleInsert.getFont().deriveFont(Font.BOLD));
                titleInsert.setHorizontalAlignment(JTextField.CENTER);
                topPanel.add(titleInsert, BorderLayout.CENTER);

                JPanel okCancelPanel = new JPanel();
                okCancelPanel.setLayout(new BorderLayout());
                JButton cancelButton = new JButton("Cancel");
                cancelButton.setPreferredSize(new Dimension(70,30));
                okCancelPanel.add(cancelButton, BorderLayout.EAST);
                JButton okButton = new JButton("OK");
                okButton.setPreferredSize(new Dimension(50,30));
                okCancelPanel.add(okButton, BorderLayout.WEST);
                topPanel.add(okCancelPanel,BorderLayout.EAST);
                topPanel.revalidate();
                topPanel.repaint();

                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        LOGGER.info("The new note creation canceled");
                        title.setVisible(true);
                        addNoteButton.setVisible(true);
                        delPanel.setVisible(true);
                        topPanel.remove(okCancelPanel);
                        topPanel.remove(titleInsert);
                        topPanel.revalidate();
                        topPanel.repaint();
                    }
                });

                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (titleInsert.getText().equals("")) {
                            LOGGER.warn("Note's title cannot be empty");
                            JOptionPane.showMessageDialog(frame, "Note's title cannot be empty",
                                    "Empty title field", JOptionPane.WARNING_MESSAGE);
                            return;
                        }

                        if (!insertNote(titleInsert.getText())) {
                            title.setVisible(true);
                            addNoteButton.setVisible(true);
                            delPanel.setVisible(true);
                            topPanel.remove(okCancelPanel);
                            topPanel.remove(titleInsert);
                            topPanel.revalidate();
                            topPanel.repaint();
                            return;
                        }

                        title.setVisible(true);
                        addNoteButton.setVisible(true);
                        delPanel.setVisible(true);
                        topPanel.remove(okCancelPanel);
                        topPanel.remove(titleInsert);
                        topPanel.revalidate();
                        topPanel.repaint();
                        loadNotesList(panel, buttonNoteIDMap);
                    }
                });
                okButton.getRootPane().setDefaultButton(okButton);
            }
        });
        loadNotesList(panel, buttonNoteIDMap);
    }
    private void addNewNoteVisualiser(JPanel mainPanel, int noteID, String title, Map<JButton, Integer> buttonMap) {
        LOGGER.debug("Creating a button for the note");
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        JButton button = new JButton(title);
        jPanel.setPreferredSize(new Dimension(350, 50));
        jPanel.setMaximumSize(new Dimension(350, 50));
        jPanel.setMinimumSize(new Dimension(350, 50));
        buttonMap.put(button, noteID);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int noteID = buttonMap.get(button);
                if (DELETE==true) {
                    LOGGER.debug("Trying to delete pressed note");
                    deleteNote(noteID);
                    loadNotesList(mainPanel, buttonMap);
                    return;
                }
                //TODO: Opening Note
                LOGGER.debug("Trying to open pressed note");
                CountDownLatch noteIsValid = new CountDownLatch(1);
                new Thread(() -> {
                    Note note = new Note(noteID, button.getText(), noteIsValid);
                    try {
                        noteIsValid.await();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    boolean status = true;
                    while (status) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        status = note.checkStatus();
                    }
                    loadNotesList(mainPanel,buttonMap);
                }).start();
            }
        });

        jPanel.add(button);
        mainPanel.add(jPanel);
        mainPanel.revalidate();
        mainPanel.repaint();
        LOGGER.info("Button successfully created");
    }

    private boolean insertNote(String title) {
        LOGGER.debug("Creating note in the database");
        try {
            CallableStatement cstmt = connection.prepareCall("{call insert_note(?,?)}");
            cstmt.setInt(1, userID);
            cstmt.setString(2, title);
            cstmt.execute();
        } catch (SQLException e) {
            LOGGER.error("Unable to save the new note into the database");
            return false;
        }
        LOGGER.info("Note successfully added into the database");
        return true;
    }

    private void loadNotesList(JPanel mainPanel, Map<JButton, Integer> buttonMap) {
        try {
            CallableStatement stmt = connection.prepareCall("{call loadNotesList(?)}");
            stmt.setInt(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                mainPanel.removeAll();
                while (rs.next()) {
                    int noteID = rs.getInt("noteID");
                    String title = rs.getString("title");
                    addNewNoteVisualiser(mainPanel, noteID, title, buttonMap);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to load the list of the Notes");
            return;
        }
        LOGGER.info("List of note loaded successfully");
    }
    private void deleteNote(int noteID) {
        try {
            CallableStatement statement = connection.prepareCall("{CALL delete_note(?)}");
            statement.setInt(1, noteID);
            statement.execute();
        } catch (SQLException e) {
            LOGGER.error("Unable to delete selected note");
            return;
        }
        LOGGER.info("Selected note deleted successfully");
    }
}
