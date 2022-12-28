public class Main {
    private static String userID;
    public static void main(String[] args) {
        LoginGUI lg = new LoginGUI();
        while ((userID = lg.getResult()) == null);
        System.out.println(userID);
//        System.out.println(new LoginGUI());
    }
}