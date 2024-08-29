
import java.sql.*;
import java.io.*;
import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserService userService = new UserService();
    private static final CareerService careerService = new CareerService();

    public static void main(String[] args) throws Exception {
        while (true) {
            System.out.println("=============================== Choose Account Type ==============================");
            System.out.println("1. Jobseeker");
            System.out.println("2. Exit");
            int ch = scanner.nextInt();
            switch (ch) {
                case 1:
                    UserMenu um = new UserMenu();
                    um.signUpMenu();
                    break;

                case 2:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Enter Valid Input!");
                    break;
            }
        }

    }
}
