import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private static int[] userID = new int[1];
    private static Connection[] connection = new Connection[1];
    public static void main(String[] args) {

        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            LOGGER.debug("Creating LoginGUI class object");
            LoginGUI loginGUI = new LoginGUI(latch, userID, connection);
        }).start();

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (userID[0] == 0) {
            LOGGER.fatal("Login GUI closed by the user. Shutting down the program");
            System.exit(-1);
        }

        NoteList noteList = new NoteList(connection[0], userID[0]);
    }
}