import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

import java.sql.Statement;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.CallableStatement;
import java.sql.Clob;

public class User {
    static Scanner sc = new Scanner(System.in);
    public Stack<String> skillAssessmentResultLog = new Stack<>();
    public Stack<String> personalityAssessmentResultLog = new Stack<>();
    public ArrayList1<String> resumes = new ArrayList1<>();
    public Queue<Skill> skills = new Queue<>();
    public static HashMap<String, User> users = new HashMap<>();
    private int id;
    private String name;
    private String email;
    private String password;
    private String location;
    private String education;
    private String personalityTraits;
    private long phonenumber;
    private String experience;
    int pt = 1;

    public int getId() {
        return id;
    }

    public String getEducation() {
        return education;
    }

    public long getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(long phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getPassword() {
        return password;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPassword() {
        while (true) {
            System.out.print("Enter password (minimum 8 characters, must include letters and digits): ");
            String input = sc.nextLine();
            if (isValidPassword(input)) {
                this.password = input;
                break;
            }
            System.out.println("Invalid password. Please re-enter the password.");
        }

    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Queue<Skill> getSkills() {
        return skills;
    }

    public String getPersonalityTraits() {
        return personalityTraits;
    }

    public void setPersonalityTraits(String personalityTraits) {
        this.personalityTraits = personalityTraits;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", email=" + email + ", location="
                + location + ", education=" + education + ", personalityTraits=" + personalityTraits + "]";
    }

    public boolean isValidEmail(String email) {
        // Simple validation to check the email format contains "@" and "."
        int atPosition = email.indexOf('@');
        int dotPosition = email.lastIndexOf('.');
        return atPosition > 0 && dotPosition > atPosition;
    }

    public static User getUserByEmail(String email) throws Exception {
        User user = null;
        try (Connection connection = DatabaseUtil.getConnection()) {
            String query = "SELECT * FROM users WHERE email = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setEmail(resultSet.getString("email"));
                user.setExperience(resultSet.getString("experience"));
                user.setPhonenumber(resultSet.getLong("phonenumber"));
                user.setLocation(resultSet.getString("location"));
                user.setEducation(resultSet.getString("education"));
                user.setPersonalityTraits(resultSet.getString("personality_traits"));
                user.setPassword(resultSet.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    boolean isValidPassword(String password) {
        // Password must be at least 8 characters long and contain both letters and
        // digits
        if (password.length() < 8) {
            return false;
        }
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
            if (hasLetter && hasDigit) {
                return true;
            }
        }
        return false;
    }

    public void profile() throws Exception {
        boolean exit = true;
        while (exit) {
            System.out.println("=================================== Profile =================================");
            System.out.println("1. View Details");
            System.out.println("2. Edit Name");
            System.out.println("3. Edit password");
            System.out.println("4. Edit email");
            System.out.println("5. Edit skills");
            System.out.println("6. Edit Location");
            System.out.println("7. Edit Education");
            System.out.println("8. Return");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    viewDetails();
                    break;

                case 2:
                    editName();
                    break;

                case 3:
                    editPassword();
                    break;

                case 4:
                    editEmail();
                    break;

                case 5:
                    editSkills();
                    break;

                case 6:
                    editLocation();
                    break;

                case 7:
                    editEducation();
                    break;

                case 8:
                    exit = false;
                    break;
                default:
                    System.out.println("Invalid Input !");
                    break;
            }
        }
    }

    public void viewDetails() {
        System.out.println("----------------------------- Personal Details -------------------------------");
        System.out.println("User Id: " + this.getId());
        System.out.println("Name: " + this.getName());
        System.out.println("Email: " + this.getEmail());
        System.out.println("Contact Number: " + this.getPhonenumber());
        System.out.println("Location: " + this.getLocation());
        System.out.println("Education: " + this.getEducation());
        System.out.println("Experience: " + this.getExperience());
        System.out.println("Personality Trait: " + this.getPersonalityTraits());
        System.out.println("---------------------------------- Skills --------------------------------------");
        viewSkills();

    }

    public void editName() throws Exception {
        System.out.println("---------------------------------- Edit Name -------------------------------");
        System.out.print("Enter new Name: ");
        sc.nextLine();
        this.name = sc.nextLine();
        String querry = "{call nameupdation(?,?)}";
        CallableStatement cst = DatabaseUtil.getConnection().prepareCall(querry);
        cst.setString(1, this.name);
        cst.setString(2, this.email);
        boolean b = cst.execute();
        if (!b) {
            System.out.println("Name Updated Successfully.");
        } else {
            System.out.println("Name not updated !");
        }
    }

    public void editPassword() throws Exception {
        System.out.println("---------------------------------- Edit Password ----------------------------------");
        System.out.print("Enter email: ");
        String email = sc.next();
        System.out.println();
        if (isValidEmail(email)) {
            if (email.equals(this.email)) {
                setPassword();
                String querry = "{call passupdation(?,?)}";
                CallableStatement cst = DatabaseUtil.getConnection().prepareCall(querry);
                cst.setString(1, this.password);
                cst.setString(2, this.email);
                boolean b = cst.execute();
                if (!b) {
                    System.out.println("Password Updated Successfully.");
                } else {
                    System.out.println("Password Not Updated !");
                }
            } else {
                System.out.println("Email Not Match !");
            }
        } else {
            System.out.println("Enter valid Email !");
        }
    }

    public void editEmail() throws Exception {
        System.out.println("-------------------------------------- Edit Email ----------------------------------");
        System.out.print("Enter your Id: ");
        int id = sc.nextInt();
        System.out.println();
        if (id == this.id) {
            System.out.print("Enter New Email: ");
            String email = sc.next();
            if (isValidEmail(email)) {
                String querry = "SELECT * FROM users WHERE email=?";
                PreparedStatement pst = DatabaseUtil.getConnection().prepareStatement(querry);
                pst.setString(1, email);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    System.out.println("Email already exists ! ");
                } else {
                    this.email = email;
                    String querry1 = "{call emailupdation(?,?)}";
                    CallableStatement cst = DatabaseUtil.getConnection().prepareCall(querry1);
                    cst.setInt(1, this.id);
                    cst.setString(2, email);
                    boolean b = cst.execute();
                    if (!b) {
                        System.out.println("Email Updated Successfully.");
                    } else {
                        System.out.println("Email not Updated Successfully !");
                    }
                }
            } else {
                System.out.println("Email Invalid ! ");
            }
        } else {
            System.out.println("Invalid ID!");
        }
    }

    public void setSkill() throws Exception {
        String querry = "SELECT * FROM skills";
        Statement st = DatabaseUtil.getConnection().createStatement();
        ResultSet rs = st.executeQuery(querry);
        int i = 0;
        while (rs.next()) {
            System.out.print(rs.getInt("id") + ". " + rs.getString("skill_name") + "   ");
            if (i % 4 == 0 && i != 0) {
                System.out.println();
            }
            /*
             * This code block is responsible for inserting a newline character (\n) after
             * every four skills are printed.
             * This helps to organize the list of skills in a more visually appealing way,
             * making it easier for the user to read and select the desired skills.
             */
            i++;
        }
        System.out.print("Enter Number of Skill you want to add: ");
        int num = sc.nextInt();
        int[] arr = new int[num];
        System.out.print("Enter Id of Skills You want Add: ");
        String querry2 = "SELECT user_id  FROM user_skills WHERE user_id = ? AND skill_id= ?";
        PreparedStatement pst1 = DatabaseUtil.getConnection().prepareStatement(querry2);
        pst1.setInt(1, this.getId());
        for (int j = 0; j < num; j++) {
            int num1 = sc.nextInt();
            while (num1 < 7 || num1 > 31) {
                System.out.println("Enter valid Id !");
                num1 = sc.nextInt();
            }
            pst1.setInt(2, num1);
            ResultSet rs1 = pst1.executeQuery();
            if (!rs1.next()) {
                UserService.insertUserSkill(this.getId(), num1);
                arr[j] = num1; // The chosen skill ID is stored in the arr array.
            }

        }
        String querry1 = "SELECT skill_name FROM skills WHERE id = ?";
        PreparedStatement pst = DatabaseUtil.getConnection().prepareStatement(querry1);
        for (int k = 0; k < arr.length; k++) {
            pst.setInt(1, arr[k]);
            ResultSet rs1 = pst.executeQuery();
            if (rs1.next()) {
                this.skills.enqueue(new Skill(arr[k], rs1.getString("skill_name")));
            }
        }
    }

    public void editSkills() throws Exception {
        boolean exit = true;
        while (exit) {
            System.out
                    .println("-------------------------------------- Edit Skills ------------------------------------");
            System.out.println("1. Add Skills");
            System.out.println("2. Remove Skills");
            System.out.println("3. View Skills");
            System.out.println("4. Return");
            int c = sc.nextInt();
            switch (c) {
                case 1:
                    System.out
                            .println("----------------------------------- Add Skills --------------------------------");
                    setSkill();
                    break;

                case 2:
                    System.out.println(
                            "------------------------------------- Remove Skills ---------------------------------");
                    removeSkills();
                    break;

                case 3:
                    System.out.println("------------------------------- Skills ----------------------------------");
                    viewSkills();
                    break;

                case 4:
                    exit = false;
                    break;

                default:
                    System.out.println("Input Invalid !");
                    break;
            }
        }

    }

    public void viewSkills() {
        int i = 1;
        Node<Skill> current = this.skills.list.head;
        while (current != null) {
            if (i % 3 == 0) {
                System.out.println();
            }
            System.out.print(i + ". " + current.data.toString() + "  ");

            current = current.next;
            i++;
        }
        System.out.println();
    }

    public void removeSkills() throws Exception {
        System.out.println("------------------------------------ Your Skills ----------------------------------");
        viewSkills();
        System.out.print("Enter Number of skills you want to delete: ");
        int num = sc.nextInt();
        String querry = "DELETE FROM user_skills WHERE user_id= ? AND skill_id= ?";
        PreparedStatement pst = DatabaseUtil.getConnection().prepareStatement(querry);
        System.out.print("Enter Id of Skills You want Delete: ");
        for (int j = 0; j < num; j++) {
            int num1 = sc.nextInt();
            while (num1 < 7 || num1 > 31) {
                System.out.println("Enter valid Id !");
                num1 = sc.nextInt();
            }
            Node<Skill> current = this.skills.list.head;

            while (current != null) {
                if (current.data.skillId == num1) {
                    this.skills.list.remove(current.data);
                }
                current = current.next;
            }
            pst.setInt(1, this.id);
            pst.setInt(2, num1);
            pst.addBatch();
        }
        int[] arr = pst.executeBatch();
        int sum = 0;
        for (int x : arr) {
            sum += x;
        }
        if (sum == 0) {
            System.out.println("You already dont have this skills ! ");
        } else if (sum == arr.length) {
            System.out.println("Skills Removed successfully. ");
        } else {
            System.out.println("Removed available skills.");
        }
    }

    public void editLocation() throws Exception {
        System.out.println("---------------------------------- Edit Location ---------------------------------");
        System.out.print("Enter new Location: ");
        sc.nextLine();
        String loc = sc.nextLine();
        setLocation(loc);
        String querry = "UPDATE users SET location = ? WHERE email= ?";
        PreparedStatement pst = DatabaseUtil.getConnection().prepareStatement(querry);
        pst.setString(1, this.location);
        pst.setString(2, this.email);
        pst.executeUpdate();
        System.out.println();
    }

    public void editEducation() throws Exception {
        System.out.println("------------------------------------- Edit Education ----------------------------------");
        System.out.print("Enter new Education: ");
        sc.nextLine();
        String edu = sc.nextLine();
        setEducation(edu);
        String querry = "UPDATE users SET education = ? WHERE email= ?";
        PreparedStatement pst = DatabaseUtil.getConnection().prepareStatement(querry);
        pst.setString(1, this.education);
        pst.setString(2, this.email);
        pst.executeUpdate();
        System.out.println();
    }

    public void searchUser() {
        boolean exit = true;
        while (exit) {
            System.out.println(
                    "---------------------------------------- Search User ---------------------------------------");
            System.out.println("1. Search User By Name");
            System.out.println("2. Search User By Email");
            System.out.println("3. Return");
            int n = sc.nextInt();
            switch (n) {
                case 1:
                    this.searchUserByName();
                    break;

                case 2:
                    System.out.print("Enter Email To Search: ");
                    String email = sc.next();
                    try {
                        User u = getUserByEmail(email);
                        if (u != null) {
                            System.out.println(
                                    "----------------------------- Personal Details -------------------------------");
                            System.out.println("User Id: " + u.getId());
                            System.out.println("Name: " + u.getName());
                            System.out.println("Email: " + u.getEmail());
                            System.out.println("Contact Number: " + u.getPhonenumber());
                            System.out.println("Location: " + u.getLocation());
                            System.out.println("Education: " + u.getEducation());
                            System.out.println("Experience: " + u.getExperience());
                            System.out.println("Personality Trait: " + u.getPersonalityTraits());
                            System.out.println(
                                    "---------------------------------- Skills --------------------------------------");
                            String querry2 = "SELECT skill_id FROM user_skills WHERE user_id = ?";
                            PreparedStatement pst2 = DatabaseUtil.getConnection().prepareStatement(querry2);
                            pst2.setInt(1, u.getId());
                            ResultSet rs2 = pst2.executeQuery();
                            int i = 1;
                            boolean b1 = true;
                            while (rs2.next()) {
                                b1 = false;
                                String querry3 = "SELECT skill_name FROM skills WHERE id = ?";
                                PreparedStatement pst3 = DatabaseUtil.getConnection().prepareStatement(querry3);
                                pst3.setInt(1, rs2.getInt("skill_id"));
                                ResultSet rs3 = pst3.executeQuery();
                                if (i % 3 == 0) {
                                    System.out.println();
                                }
                                if (rs3.next()) {
                                    System.out.print(i + ". " + rs3.getString("skill_name") + "  ");
                                }
                                i++;

                            }
                            if (b1) {
                                System.out.println("No Skills Addedd !");
                                return;

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println();
                    break;
                case 3:
                    exit = false;
                    break;
                default:
                    break;
            }
        }

    }

    public void searchUserByName() {
        sc.nextLine();
        System.out.print("Enter Name To Search: ");
        String name1 = sc.nextLine();
        String querry = "SELECT * FROM users WHERE name = ?";
        try {
            PreparedStatement pst = DatabaseUtil.getConnection().prepareStatement(querry);

            boolean b = true;
            while (true) {
                pst.setString(1, name1);
                ResultSet rs = pst.executeQuery();
                System.out.println("---------------------------------- Users --------------------------------");
                while (rs.next()) {
                    System.out.println("User Id: " + rs.getInt("id") + "  Name: " + rs.getString("name"));
                    b = false;
                }
                if (b) {
                    System.out.println("No User Found !");
                    return;
                }
                System.out.print("Enter User Id to View Profile(0 to return): ");
                int n = sc.nextInt();
                if (n == 0) {
                    return;
                }
                String querry1 = "SELECT email FROM users WHERE id = ?";
                PreparedStatement pst1 = DatabaseUtil.getConnection().prepareStatement(querry1);
                pst1.setInt(1, n);
                ResultSet rs1 = pst1.executeQuery();
                if (rs1.next()) {
                    User u = getUserByEmail(rs1.getString("email"));
                    System.out
                            .println("----------------------------- Personal Details -------------------------------");
                    System.out.println("User Id: " + u.getId());
                    System.out.println("Name: " + u.getName());
                    System.out.println("Email: " + u.getEmail());
                    System.out.println("Contact Number: " + u.getPhonenumber());
                    System.out.println("Location: " + u.getLocation());
                    System.out.println("Education: " + u.getEducation());
                    System.out.println("Experience: " + u.getExperience());
                    System.out.println("Personality Trait: " + u.getPersonalityTraits());
                    System.out.println(
                            "---------------------------------- Skills --------------------------------------");
                    String querry2 = "SELECT skill_id FROM user_skills WHERE user_id = ?";
                    PreparedStatement pst2 = DatabaseUtil.getConnection().prepareStatement(querry2);
                    pst2.setInt(1, u.getId());
                    ResultSet rs2 = pst2.executeQuery();
                    int i = 1;
                    boolean b1 = true;
                    while (rs2.next()) {
                        b1 = false;
                        String querry3 = "SELECT skill_name FROM skills WHERE id = ?";
                        PreparedStatement pst3 = DatabaseUtil.getConnection().prepareStatement(querry3);
                        pst3.setInt(1, rs2.getInt("skill_id"));
                        ResultSet rs3 = pst3.executeQuery();
                        if (i % 3 == 0) {
                            System.out.println();
                        }
                        if (rs3.next()) {
                            System.out.print(i + ". " + rs3.getString("skill_name") + "  ");
                        }
                        i++;

                    }
                    if (b1) {
                        System.out.println("No Skills Addedd !");
                        return;
                    }

                }
            }

        } catch (Exception e) {
        }
    }

    public void searchJobs() {
        boolean b = true;
        while (b) {
            System.out.println(
                    "-------------------------------------- Search Jobs -------------------------------------------");
            System.out.println("1. Search Jobs By Job Name");
            System.out.println("2. Search Jobs By Company");
            System.out.println("3. Search Jobs By Educational Requirements");
            System.out.println("4. View Jobs You've Applied to");
            System.out.println("5. Return");
            int n = sc.nextInt();
            switch (n) {
                case 1:
                    System.out.print("Enter Job Name: ");
                    sc.nextLine();
                    String jn = sc.nextLine();
                    this.searchJobName("job_listings1", "name", jn);
                    break;

                case 2:
                    System.out.print("Enter Company Name: ");
                    sc.nextLine();
                    String cn = sc.nextLine();
                    this.searchJobName("job_listings1", "company_name", cn);
                    break;

                case 3:
                    this.searchEducationalRequirements();
                    break;

                case 4:
                    // also so is active status of job and check all code first
                    try {
                        this.jobsApllied();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 5:
                    b = false;
                    break;

                default:
                    System.out.println("Invalid Input!");
                    break;
            }

        }
    }

    public void jobsApllied() throws Exception {
        System.out.println("---------------------------------- Jobs Applied ------------------------------");
        String sql = "SELECT * FROM user_jobs1 WHERE user_id=?";
        PreparedStatement pst = DatabaseUtil.getConnection().prepareStatement(sql);
        pst.setInt(1, this.getId());
        ResultSet rs = pst.executeQuery();
        String sql1 = "SELECT * FROM job_listings1 where id=?";
        int i = 1;
        while (rs.next()) {
            PreparedStatement pst1 = DatabaseUtil.getConnection().prepareStatement(sql1);
            pst1.setInt(1, rs.getInt("job_id"));
            ResultSet rs1 = pst1.executeQuery();
            if (rs1.next()) {
                System.out.println(i + ".  Job Title: " + rs1.getString("name") + "   Company Name: "
                        + rs1.getString("company_name") + "   Job ID: " + rs1.getInt("ID"));
            }
            i++;
        }
        if (i == 1) {
            System.out.println("No Jobs submission.");
            return;
        }
        System.out.println("0. Return");
        System.out.print("Enter Job ID to view Job details and status:");
        int ch = sc.nextInt();
        if (ch == 0) {
            return;
        }
        String q1 = "SELECT job_id, hired FROM user_jobs1 WHERE user_id=? AND job_id=? ";
        PreparedStatement pst1 = DatabaseUtil.getConnection().prepareStatement(q1);
        pst1.setInt(1, this.getId());
        pst1.setInt(2, ch);
        ResultSet rs1 = pst1.executeQuery();
        if (rs1.next()) {
            String querry = "SELECT rec_id FROM job_listings1 WHERE id = ?";
            PreparedStatement pst2 = DatabaseUtil.getConnection().prepareStatement(querry);
            pst2.setInt(1, rs1.getInt("job_id"));
            ResultSet rs2 = pst2.executeQuery();
            ResultSet rs3 = null;
            String q2 = "SELECT * FROM job_listings1 WHERE id=?";
            PreparedStatement pst3 = DatabaseUtil.getConnection().prepareStatement(q2);
            pst3.setInt(1, rs1.getInt("job_id"));
            ResultSet rs8 = pst3.executeQuery();
            if (rs8.next()) {
                System.out.println(
                        "-------------------------------------- Job Details -----------------------------------");
                System.out.println("Job Id: " + rs8.getInt("id"));
                System.out.println("Job Name: " + rs8.getString("name"));
                System.out.println("Company: " + rs8.getString("company_name"));
                System.out.println("Description: " + rs8.getString("description"));
                System.out.println("Industry Insights: " + rs8.getString("industry_insights"));
                System.out.println("Educational Requirements: " + rs8.getString("educational_requirements"));
                if (rs8.getBoolean("is_active")) {
                    System.out.println("Job Status: Inactive");
                } else {
                    System.out.println("Job Status: Active");
                }

            }

            String sql2 = "SELECT skill_id FROM job_skills1 WHERE job_id = ?";
            PreparedStatement pre = DatabaseUtil.getConnection().prepareStatement(sql2);
            pre.setInt(1, rs1.getInt("job_id"));
            System.out.println("Skills Required: ");
            ResultSet rs6 = pre.executeQuery();
            String q = "SELECT skill_name FROM skills WHERE id = ?";
            PreparedStatement pt = DatabaseUtil.getConnection().prepareStatement(q);
            int i1 = 1;
            while (rs6.next()) {
                pt.setInt(1, rs6.getInt("skill_id"));
                ResultSet r1 = pt.executeQuery();
                if (i1 % 4 == 0) {
                    System.out.println();
                }
                if (r1.next()) {
                    System.out.print(i1 + ". " + r1.getString("skill_name") + "  ");
                }
                i1++;
            }
            System.out.println();

            if (rs1.getBoolean("hired")) {
                System.out.println("**************** Congratulations Your Application is Accepted ! ****************");
            } else {
                System.out.println("______________ Application InReview ______________");
            }

        } else {
            System.out.println("Job Id Invalid !");

        }
        jobsApllied();
    }

    // public void searchBySkills() {
    // HashSet<Integer> jobId = new HashSet<>();
    // while (true) {
    // System.out.println(
    // "-------------------------------------- Search Job By Skills
    // ------------------------------------");
    // System.out.println("1. Search By your Skills");
    // System.out.println("2. Search By Manual Skills");
    // System.out.println("3. Return");
    // int n = sc.nextInt();
    // switch (n) {
    // case 1:
    // String querry = "SELECT skill_id FROM user_skills WHERE user_id = ?";
    // try {
    // PreparedStatement ps = DatabaseUtil.getConnection().prepareStatement(querry);
    // ps.setInt(1, this.id);
    // ResultSet rs = ps.executeQuery();
    // String sql = "SELECT job_id FROM job_skills1 WHERE skill_id = ?";
    // PreparedStatement pst = DatabaseUtil.getConnection().prepareStatement(sql);
    // while (rs.next()) {
    // pst.setInt(1, rs.getInt("skill_id"));
    // ResultSet rs1 = pst.executeQuery();
    // while (rs1.next()) {
    // jobId.add(rs1.getInt("job_id"));
    // }
    // }
    // this.displayJobByUserSkill(jobId);
    // } catch (Exception e) {
    // System.out.println(e.getMessage());
    // }
    // break;

    // case 2:
    // this.displayJobsByManualSkills();
    // break;
    // case 3:
    // return;
    // default:
    // System.out.println("Invalid Input!");
    // break;
    // }
    // }
    // }

    // public void displayJobsByManualSkills() {
    // HashSet<Integer> skillid = new HashSet<>();
    // HashSet<Integer> jobId = new HashSet<>();
    // try {
    // String querry = "SELECT * FROM skills"; // query to retrieve all skills from
    // the skills table.
    // PreparedStatement pst =
    // DatabaseUtil.getConnection().prepareStatement(querry);
    // ResultSet rs = pst.executeQuery();
    // int i = 1;
    // while (rs.next()) {
    // if (i % 4 == 0) {
    // System.out.println();
    // }
    // System.out.print(rs.getInt("id") + ". " + rs.getString("skill_name") + " ");
    // i++;
    // }
    // System.out.println();
    // } catch (Exception e) {

    // }
    // System.out.print("Enter No. of Skills You Want to Enter: ");
    // int n = sc.nextInt();
    // for (int i = 0; i < n; i++) {
    // System.out.println("skill Id no. " + (i + 1));
    // int a = sc.nextInt();
    // while (a < 7 || a > 31) {
    // System.out.println("Enter Valid Skill Id!");
    // a = sc.nextInt();
    // }
    // skillid.add(a);
    // }
    // try {
    // String sql = "SELECT job_id FROM job_skills1 WHERE skill_id = ?";
    // PreparedStatement pst = DatabaseUtil.getConnection().prepareStatement(sql);
    // for (int id : skillid) {
    // pst.setInt(1, id);
    // ResultSet rs = pst.executeQuery();
    // while (rs.next()) {
    // jobId.add(rs.getInt("job_id"));
    // }
    // }
    // } catch (Exception e) {

    // }
    // this.displayJobByUserSkill(jobId);
    // }

    // public void displayJobByUserSkill(HashSet<Integer> jobId) {
    // try {
    // // Code to display job details based on job IDs in HashSet.
    // while (true) {
    // String sql1 = "SELECT * FROM job_listings1 WHERE id = ?";
    // PreparedStatement ps1 = DatabaseUtil.getConnection().prepareStatement(sql1);
    // boolean b = false;

    // System.out.println(
    // "-------------------------------------- Job Listings
    // -----------------------------------");
    // for (int id : jobId) {
    // // Code to retrieve and display details for each job ID
    // ps1.setInt(1, id);
    // ResultSet rs1 = ps1.executeQuery();
    // while (rs1.next()) {
    // b = true;
    // System.out.println("Job Id: " + rs1.getInt("id") + " Job Name: " +
    // rs1.getString("name")
    // + " Company: " + rs1.getString("company_name"));
    // }
    // }
    // // If b is still false, it means no jobs were found matching the user's
    // skills
    // if (!b) {
    // System.out.println("No Jobs Listings matching your SKills !");
    // return;
    // }

    // // If jobs were found,this line prompts the user to enter specific jobID to
    // view

    // System.out.print("Enter Job Id To View Job Details(OR Enter 0 to Return): ");
    // int jid = sc.nextInt();
    // if (jid == 0) {
    // return;
    // }

    // String querry1 = "SELECT * FROM job_listings1 WHERE id = ?";
    // PreparedStatement pst =
    // DatabaseUtil.getConnection().prepareStatement(querry1);
    // pst.setInt(1, jid);
    // ResultSet r = pst.executeQuery();
    // if (r.next()) {
    // String querry = "SELECT rec_id FROM job_listings1 WHERE id = ?";
    // PreparedStatement pst2 =
    // DatabaseUtil.getConnection().prepareStatement(querry);
    // pst2.setInt(1, jid);
    // ResultSet rs2 = pst2.executeQuery();
    // ResultSet rs3 = null;
    // System.out.println(
    // "----------------------------------- Job Details
    // --------------------------------");
    // System.out.println("Job Id: " + r.getInt("id"));
    // System.out.println("Job Name: " + r.getString("name"));
    // System.out.println("Company: " + r.getString("company_name"));
    // System.out.println("Description: " + r.getString("description"));
    // System.out.println("Industry Insights: " + r.getString("industry_insights"));
    // System.out.println("Educational Requirements: " +
    // r.getString("educational_requirements"));
    // if (r.getBoolean("is_active")) {
    // System.out.println("Job Status: Inactive");
    // } else {
    // System.out.println("Job Status: Active");
    // }

    // String sql2 = "SELECT skill_id FROM job_skills1 WHERE job_id = ?";
    // PreparedStatement pre = DatabaseUtil.getConnection().prepareStatement(sql2);
    // pre.setInt(1, jid);

    // System.out.println("Skills Required: ");

    // ResultSet rs1 = pre.executeQuery();
    // String q = "SELECT skill_name FROM skills WHERE id = ?";
    // PreparedStatement pt = DatabaseUtil.getConnection().prepareStatement(q);
    // int i = 1;
    // while (rs1.next()) {
    // pt.setInt(1, rs1.getInt("skill_id"));
    // ResultSet r1 = pt.executeQuery();
    // if (i % 4 == 0) {
    // System.out.println();
    // }
    // if (r1.next()) {
    // System.out.print(i + ". " + r1.getString("skill_name") + " ");
    // }
    // i++;
    // }
    // System.out.println();

    // System.out.println("Apply ?(yes/no): ");
    // String ch = sc.next();
    // if (ch.equalsIgnoreCase("yes")) {
    // this.apply(r.getInt("id"));
    // }
    // } else {
    // System.out.println("Job Id Invalid !");
    // }
    // }
    // } catch (Exception e) {
    // System.out.println(e.getMessage());
    // }
    // displayJobByUserSkill(jobId);

    // }

    public void searchJobName(String table, String column, String jn) {
        String querry = "SELECT * FROM " + table + " WHERE " + column + " = ?";
        while (true) {
            Connection con;
            try {
                con = DatabaseUtil.getConnection();
                PreparedStatement pst = con.prepareStatement(querry);
                pst.setString(1, jn); // Name of The Job
                ResultSet rs = pst.executeQuery();
                System.out.println(
                        "---------------------------------------- Job Listings ------------------------------------");
                boolean b = false;
                while (rs.next()) {
                    b = true;
                    System.out.println("Job Id: " + rs.getInt("id") + "  Job Name: " + rs.getString("name")
                            + "  Company: " + rs.getString("company_name"));
                }
                if (!b) {
                    System.out.println("No Jobs Listings Having " + column + ": " + jn);
                    return;
                }
                System.out.print("Enter Job Id To View Job Details(OR Enter 0 to Return): ");
                int jid = sc.nextInt();
                if (jid == 0) {
                    return;
                }
                String sql = "SELECT * FROM job_listings1 WHERE " + column + " = ? AND id = ?";
                PreparedStatement ps = DatabaseUtil.getConnection().prepareStatement(sql);
                ps.setString(1, jn);
                ps.setInt(2, jid);
                ResultSet r = ps.executeQuery();

                if (r.next()) {
                    System.out.println(
                            "----------------------------------- Job Details --------------------------------");
                    System.out.println("Job Id: " + r.getInt("id"));
                    System.out.println("Job Name: " + r.getString("name"));
                    System.out.println("Company: " + r.getString("company_name"));
                    System.out.println("Description: " + r.getString("description"));
                    System.out.println("Industry Insights: " + r.getString("industry_insights"));
                    System.out.println("Educational Requirements: " + r.getString("educational_requirements"));
                    if (r.getBoolean("is_active")) {
                        System.out.println("Job Status: Active");
                    } else {
                        System.out.println("Job Status: Inactive");
                    }

                    System.out.println("Apply ?(yes/no): ");
                    String ch = sc.next();
                    if (ch.equalsIgnoreCase("yes")) {
                        this.apply(r.getInt("id"));
                    }
                } else {
                    System.out.println("Job Id Invalid !");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void apply(int jobId) {

        // Check if user already applied for this job
        String sql = "SELECT user_id FROM user_jobs1 WHERE job_id= ? AND user_id= ?";
        String sql1 = "SELECT is_active FROM job_listings1 WHERE id= ?";
        try {
            PreparedStatement pst = DatabaseUtil.getConnection().prepareStatement(sql);
            PreparedStatement pst2 = DatabaseUtil.getConnection().prepareStatement(sql1);
            pst2.setInt(1, jobId);
            pst.setInt(1, jobId);
            pst.setInt(2, this.getId());
            ResultSet rs = pst.executeQuery();
            ResultSet rs1 = pst2.executeQuery();

            // If user already applied, inform and exit
            if (rs.next()) {
                System.out.println("You've already applied for this job.");
                return;
            }

            // Check if job is still active
            if (rs1.next()) {
                if (!rs1.getBoolean("is_active")) {
                    System.out.println("Job is No Longer Available!");
                    return;
                }
            }
            Connection con = DatabaseUtil.getConnection();
            con.setAutoCommit(false);

            // Prepare query to insert application record
            String querry = "INSERT INTO user_jobs1(user_id, job_id, hired) VALUES(?, ?, ?)";
            PreparedStatement pst1 = con.prepareStatement(querry);
            pst1.setInt(1, this.getId());
            pst1.setInt(2, jobId);
            pst1.setBoolean(3, false);
            int r = pst1.executeUpdate();

            // Confirmation prompt before finalizing application
            System.out.print("Are you Sure to apply to this job?(yes/no): ");
            String h = sc.next();
            if (h.equalsIgnoreCase("yes")) {
                System.out.println("Successfully Applied.");
                con.commit();
            } else {
                System.out.println("Not Apllied!");
                con.rollback();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void searchEducationalRequirements() {
        while (true) {
            System.out.println("1. Search By Your Educational Details");
            System.out.println("2. Enter Educational Details Manually");
            System.out.println("3. Return");
            int n = sc.nextInt();
            switch (n) {
                case 1:
                    this.searchJobName("job_listings1", "educational_requirements", this.education);
                    break;
                case 2:
                    System.out.print("Enter Education: ");
                    sc.nextLine();
                    String jn = sc.nextLine();
                    this.searchJobName("job_listings1", "educational_requirements", jn);
                    break;
                case 3:
                    return;

                default:
                    System.out.println("Invalid Input !");
                    break;
            }
        }

    }
}