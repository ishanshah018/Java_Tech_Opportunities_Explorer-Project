
import java.io.*;
import java.sql.*;
import java.time.Instant;

public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/tech_opp_exp";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void updatingPersonalityTraits(String pt, int id) {
        try {
            String querry = "UPDATE users SET personality_traits = ? WHERE id = ?";
            PreparedStatement pst = getConnection().prepareStatement(querry);
            pst.setString(1, pt);
            pst.setInt(2, id);
            int r = pst.executeUpdate();
            if (r > 0) {
                System.out.println("Personality Traits updated.");
            } else {
                System.out.println("Persoality Traits Not Update !");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    // personalityresultlog
    public static void updatingPersonalityTraitsResultLog(User a) {
        String querry = "INSERT INTO personalityresultlog(user_id, date, personality_traits) VALUES (?, ?, ?)";
        try {
            PreparedStatement pst = getConnection().prepareStatement(querry);
            pst.setInt(1, a.getId());
            Timestamp eventTime = Timestamp.from(Instant.now());
            pst.setTimestamp(2, eventTime);
            pst.setString(3, a.getPersonalityTraits());
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void updatingSkillAssessmentResultLog(User a, int ps, int sn, int sd, int hd) {
        String querry = "INSERT INTO skillresultlog(user_id, date, programming_skills_out_of_5, systems_and_networking_out_of_5, software_development_out_of_5, hardware_and_embedded_systems_out_of_5) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = getConnection().prepareStatement(querry);
            pst.setInt(1, a.getId());
            Timestamp eventTime = Timestamp.from(Instant.now());
            pst.setTimestamp(2, eventTime);
            pst.setInt(3, ps);
            pst.setInt(4, sn);
            pst.setInt(5, sd);
            pst.setInt(6, hd);
            pst.executeUpdate();
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    public static void getPersonalityTraitsResultLog(User a) {
        String querry = "SELECT * FROM personalityresultlog WHERE user_id = ?";
        try {
            PreparedStatement pst = getConnection().prepareStatement(querry);
            pst.setInt(1, a.getId());
            ResultSet rs = pst.executeQuery();
            int i = 1;
            while (rs.next()) {
                System.out.println(i + ". Date/Time: " + rs.getTimestamp("date") + " Personality Trait: "
                        + rs.getString("personality_traits"));
                i++;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void getSkillResultLog(User a){
    String querry="SELECT * FROM skillresultlog WHERE user_id = ?";
    try{
    PreparedStatement pst=getConnection().prepareStatement(querry);
    pst.setInt(1, a.getId());
    ResultSet rs=pst.executeQuery();
    int i=1;
    if(!rs.next()){
    System.out.println("No Skills Assessment Attempted.");
    return;
    }
    while(rs.next()){
    System.out.println(i+". Date/Time: "+rs.getTimestamp("date")+" Programming Skills: "+rs.getInt("programming_skills_out_of_5")+"/5 Systems and
    Networking: "+
    rs.getInt("systems_and_networking_out_of_5")+"/5 Software Devlopment:
    "+rs.getInt("software_development_out_of_5")+"/5 Hardware and Embedded
    System: "+
    rs.getInt("hardware_and_embedded_systems_out_of_5")+"/5");
    i++;
    }
    }catch(Exception e){
    System.out.println(e.getMessage());
    }
    }

    public static void copySkills(User a) {
        String querry = "SELECT skill_id FROM  user_skills WHERE user_id = ?";
        try {
            PreparedStatement pst = getConnection().prepareStatement(querry);
            pst.setInt(1, a.getId());
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String querry1 = "SELECT skill_name FROM skills WHERE id = ?";
                PreparedStatement pst1 = getConnection().prepareStatement(querry1);
                pst1.setInt(1, rs.getInt("skill_id"));
                ResultSet rs1 = pst1.executeQuery();
                if (rs1.next()) {
                    a.skills.enqueue(new Skill(rs.getInt("skill_id"), rs1.getString("skill_name")));
                }
            }
        } catch (Exception e) {
            // System.out.println(e.getMessage());
        }
    }

    public static void copyPtResultLog(User a) {
        String querry = "SELECT * FROM personalityresultlog WHERE user_id = ?";
        try {
            PreparedStatement pst = getConnection().prepareStatement(querry);
            pst.setInt(1, a.getId());
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                a.personalityAssessmentResultLog.push("Date/Time: " + rs.getTimestamp("date") + "  Personality Trait: "
                        + rs.getString("personality_traits"));

            }
        } catch (Exception e) {
            // System.out.println(e.getMessage());
        }

    }

    public static void copySkillResultLog(User a) {
        String querry = "SELECT * FROM skillresultlog WHERE user_id = ?";
        try {
            PreparedStatement pst = getConnection().prepareStatement(querry);
            pst.setInt(1, a.getId());
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                a.skillAssessmentResultLog.push(" Date/Time: " + rs.getTimestamp("date") + "  Programming Skills: "
                        + rs.getInt("programming_skills_out_of_5") + "/5  Systems and Networking: " +
                        rs.getInt("systems_and_networking_out_of_5") + "/5  Software Devlopment: "
                        + rs.getInt("software_development_out_of_5") + "/5  Hardware and Embedded System: " +
                        rs.getInt("hardware_and_embedded_systems_out_of_5") + "/5");

            }
        } catch (Exception e) {
            // System.out.println(e.getMessage());
        }

    }

}
