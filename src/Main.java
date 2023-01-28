import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private static String[] user = new String[2];
    private static Connection connection;
    public static void main(String[] args) {

        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            LOGGER.debug("Creating LoginGUI class object");
            LoginGUI loginGUI = new LoginGUI(latch, user, connection);
        }).start();

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Program continues");
        System.out.println(user[0]);
        System.out.println(user[1]);
        System.out.println(user.length);
    }
}