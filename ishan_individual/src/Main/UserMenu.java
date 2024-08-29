import java.util.*;
import java.sql.*;

public class UserMenu extends User { // userMenu that inherits from a class called User. UserMenu can access all the
                                     // properties and methods of the User class.
    private static final UserService userService = new UserService();
    static Scanner sc = new Scanner(System.in);
    User u; // This declares a variable of type User named u
    int choice;

    public void signUpMenu() throws Exception {
        boolean exit = true;

        while (exit) {
            System.out.println("================================== User Menu ==================================");
            System.out.println("1. Sign Up");
            System.out.println("2. Login");
            System.out.println("3. Return");

            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("================================== Sign Up =============================");
                    customerLogIn();
                    break;
                case 2:
                    System.out.println("============================== Log In ===============================");
                    System.out.println("Log In: ");
                    customerLogIn();
                    break;
                case 3:
                    exit = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public void customerLogIn() throws Exception {
        while (true) {
            System.out.print("Enter email: ");
            // sc.next();
            String input = sc.next();
            if (isValidEmail(input)) {
                findUserByEmail(input);
                break;
            }
            System.out.println("Invalid email. Please re-enter the email.");
        }
    }

    private void findUserByEmail(String input) throws Exception {
        boolean b = false;
        if (User.getUserByEmail(input) != null) {
            b = true;
            /*
             * Sign Up Case (choice == 1):
             * If the user chose "Sign Up" (choice is 1) and an existing user is found, it
             * displays an
             * error message indicating the email already exists.
             */
            if (choice == 1) {
                System.out.println("Email already exists!");
            } else {
                /*
                 * If the user chose "Login"
                 * A loop prompts the user to enter a password repeatedly
                 * until the password is correct.
                 */
                while (true) {
                    System.out.print("enter password: ");
                    String password = sc.next();
                    if (password.equals(User.getUserByEmail(input).getPassword())) {
                        u = User.getUserByEmail(input);
                        u.pt++;
                        System.out.println(
                                "---------------------------------- Welcome Again! ---------------------------------------");
                        DatabaseUtil.copySkills(u); // will display all skills present in user's data
                        DatabaseUtil.copyPtResultLog(u); // will display Result Log
                        DatabaseUtil.copySkillResultLog(u); // will display Skills wise Marks
                        userMenu(u);
                        break;
                        /*
                         * If the password doesn't match:
                         * An error message is displayed indicating an incorrect password.
                         * The user is prompted if they want to reset the password (y/n).
                         */
                    } else {
                        System.out.println("pasword incorrect!");
                        System.out.println("Reset password ?(y/n): ");
                        String choice = sc.next();
                        if (choice.equalsIgnoreCase("y")) {
                            u = User.getUserByEmail(input);
                            System.out.println("Enter UserId: ");
                            int userid = sc.nextInt();
                            if (userid == User.getUserByEmail(input).getId()) {
                                u.setPassword();
                                String querry = "{call passupdation(?,?)}";
                                CallableStatement cst = DatabaseUtil.getConnection().prepareCall(querry);
                                cst.setString(1, u.getPassword());
                                cst.setString(2, u.getEmail());
                                boolean b1 = cst.execute();
                                if (!b1) {
                                    System.out.println("Password Updated Successfully.");
                                } else {
                                    System.out.println("Password Not Updated !");
                                }
                                break;
                            } else {
                                System.out.println("incorrect user id! login failed");
                                break;
                            }
                        }
                    }
                }
            }
        }
        /*
         * Handling Non-Existing User (b is false):
         * If no user is found with the provided email (!b):
         */
        if (!b) {
            if (choice == 1) {
                createUser(input); // Creating New Account
                userMenu(u);
            } else {
                System.out.println("User Not found!");
                System.out.println("Don't Have an Account? want to sign up ? (y/n):");
                String ch = sc.next();
                if (ch.equalsIgnoreCase("y")) {
                    createUser(input);
                    userMenu(u);
                }
            }
        }
    }

    private void createUser(String email) throws Exception {
        this.u = new User();
        u.setPassword();
        System.out.print("Enter name: ");
        sc.nextLine();
        String name = sc.nextLine();
        System.out.print("Enter Phonenumber: ");
        long pn = sc.nextLong();
        while (pn < 7000000000l || pn > 9999999999l) {
            System.out.println("Enter Valid Phonenumber !");
            pn = sc.nextLong();
        }
        sc.nextLine();
        System.out.print("Enter Education: ");
        String edu = sc.nextLine();
        System.out.print("Enter Experience: ");
        String exp = sc.nextLine();
        System.out.print("Enter Location: ");
        String location = sc.nextLine();
        System.out.println();
        userService.personalityAssessment(this.u);
        u.pt++;
        u.setExperience(exp);
        u.setPhonenumber(pn);
        u.setName(name);
        u.setEmail(email);
        u.setLocation(location);
        u.setEducation(edu);
        userService.addUser(u, email);
        DatabaseUtil.updatingPersonalityTraitsResultLog(u);
        System.out.println("--------------------------------- Set Your Skills -------------------------------");
        u.setSkill();
        System.out.println("User created and signed in successfully.");
    }

    public void userMenu(User u) throws Exception {

        boolean exit = true;
        while (exit) {
            System.out.println(
                    "============================== Tech Opportunity Explorer ===================================");
            System.out.println("1. Profile");
            System.out.println("2. Assessments");
            System.out.println("3. Tech Field Matching");
            System.out.println("4. Search User");
            System.out.println("5. Search Jobs");
            System.out.println("6. Log out");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    u.profile();
                    break;
                case 2:
                    assessments();
                    break;
                case 3:
                    userService.interestProfiling();
                    break;

                case 4:
                    u.searchUser();
                    break;
                case 5:
                    u.searchJobs();
                    break;

                case 6:
                    System.out.println("Logged out successfully.");
                    exit = false;
                    break;

                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    public void assessments() {
        boolean exit = true;
        while (exit) {
            System.out.println("==================================== Assessments ================================");
            System.out.println("1. Tech-Related Assessment");
            System.out.println("2. Personality Assessment");
            System.out.println("3. ResultLog");
            System.out.println("4. Return");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    userService.techAssessment(u);
                    break;
                case 2:
                    userService.personalityAssessment(u);
                    u.pt++;
                    break;
                case 3:
                    boolean b = true;
                    while (b) {
                        System.out.println(
                                "================================= Result Logs =================================");
                        System.out.println("1. skilled Assessment resultlog");
                        System.out.println("2. Personality Assessment Resultlog");
                        System.out.println("3. Return");
                        int c = sc.nextInt();
                        if (c == 1) {
                            System.out.println(
                                    "=============================== Skilled Assessment Result History =================================");
                            userService.printResultLog(u);
                        } else if (c == 2) {
                            System.out.println(
                                    "============================== Personality Assessment Result History ================================");
                            userService.personalityAssessmentResultLog(u);
                        } else if (c == 3) {
                            return;
                        } else {
                            System.out.println("Invalid option. Try again.");
                        }
                    }
                    break;

                case 4:
                    exit = false;
                    break;

                default:
                    System.out.println("Invalid Input !");
                    break;
            }
        }
    }

}