import java.util.concurrent.CountDownLatch;

public class Main {
    private static String[] user = new String[2];
    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            LoginGUI loginGUI = new LoginGUI(latch, user);
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