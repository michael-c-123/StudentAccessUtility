
package student;

import button.Button;
import java.awt.Color;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author Michael
 */
public class Profile implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int USERNAME = 0, ID = 1,
            MONTH = 2, DAY = 3, YEAR = 4,
            GRADE = 5, SCHOOL = 6;

    private String username;
    private int userID;
    private Calendar dob;
    private int grade;
    private int school;

    private boolean locked; //protected by password
    private transient int attempts; //how many attempts have been made with password

    private Map<String, Object> settings;

    private Calendar lastCourseUpdate; //last updated when we could access courses
    private Calendar lastUpdate; //last checked to see if we could access courses

    private List<Course> courses;

    private static Calendar lastSchoolUpdate; //last time school map was updated

    private static final Map<Integer, String> SCHOOLS = new HashMap<>();

    //initialize school map
    static {
        Scanner sc;
        try {
            //TODO update schooldata.txt by checking webpage
            sc = new Scanner(new File("schooldata.dat"));
            String mess = sc.nextLine(); //grab whole thing and clean it up
            System.out.println(mess);
            mess = mess.replaceAll("[<>]", ""); //remove '<' and '>'
            mess = mess.replaceAll(Pattern.quote("option value="), "");
            mess = mess.replaceAll(Pattern.quote("/option"), "");
            System.out.println(mess);
            StringTokenizer tokenizer = new StringTokenizer(mess, "\"");
            while (tokenizer.hasMoreTokens()) {
                String key = tokenizer.nextToken(), schoolName = tokenizer.nextToken();
                SCHOOLS.put(Integer.valueOf(key), schoolName);
            }
            sc.close();
        }
        catch (FileNotFoundException ex) {
            //TODO recovery option
            Logger.getLogger(Profile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Profile(String username, int userID, Calendar dob, int grade, int school) {
        this.username = username;
        this.userID = userID;
        this.dob = dob;
        this.grade = grade;
        this.school = school;
        courses = new ArrayList<>();
        settings = new HashMap<>();
        setToDefaultSettings();
        System.out.println("YEET");
    }

    public String[] getFields() {
        return new String[]{
            username,
            String.valueOf(userID),
            String.format("%tm", dob), //month in two digits
            String.format("%td", dob), //date in two digits
            String.format("%tY", dob), //year in four digits
            String.format("%02d", grade), //grade with two digits
            String.valueOf(school)
        };
    }

    public String getField(int field) {
        return getFields()[field];
    }

    public String[] getStrings() {
        return new String[]{
            username,
            String.valueOf(userID),
            String.format("%tB", dob), //full month name
            String.format("%te", dob), //date without specified digits
            String.format("%tY", dob), //year in four digits
            String.valueOf(grade), //just the grade number
            SCHOOLS.get(school) //value that corresponds with school key
        };
    }

    public String getString(int field) {
        return getStrings()[field];
    }

    public List<Course> getCourses() {
        return courses;
    }

    public Course getCourseByName(String name) {
//        name = name.replaceAll("\\s(A|B)$", "").trim();
        for (Course course : courses) {
            if (course.getName().equalsIgnoreCase(name))
                return course;
        }
        return null;
    }

    public void addCourse(Course course) {
        courses.add(course);
    }

    public void removeCourse(Course course) {
        courses.remove(course);
    }

    /**
     * Keys for settings: style, color manipulated, color responding, color
     * normal, scroll sensitivity, grade bar size
     *
     * @return the settings map
     */
    public Map<String, Object> getSettings() {
        return settings;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void incAttempts() {
        attempts++;
    }

    public void grantAccess() {
        attempts = -1;
    }

    public boolean isDenied() {
        return attempts >= 3;
    }

    public boolean isAccessGranted() {
        return attempts == -1;
    }

    public void write() {
        File file;
        try {
            file = new File("profiledata.ser");
            if (!file.exists())
                file.createNewFile();
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file));
            output.writeObject(this);
            System.out.println(this); //TEST: print profile
            System.out.println("TEST: wrote file");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Profile read() {
        File file;
        try {
            file = new File("profiledata.ser");
            if (!file.exists())
                file.createNewFile();
            //use object input to read in a profile
            ObjectInputStream input = null;
            try {
                input = new ObjectInputStream(new FileInputStream(file));
                return (Profile) input.readObject(); //feed in Profile
            }
            catch (EOFException ex) { //empty file
                return null;
            }
            finally {
                if (input != null)
                    input.close();
            }
        }
        catch (IOException | ClassNotFoundException | ClassCastException ex) {
            System.err.println("TEST: file corrupt"); //TODO replace with dialog warning
            ex.printStackTrace();
        }
        return null;
    }

    public final void setToDefaultSettings() {
        settings.put("style", Button.STANDARD);
        settings.put("color manipulated", Color.YELLOW.darker().darker());
        settings.put("color responding", new Color(85, 43, 117)); //royal purple
        settings.put("color normal", Color.LIGHT_GRAY);
        settings.put("color dark", new Color(100, 100, 100));
        settings.put("scroll sensitivity", 25);
        settings.put("grade bar size", 30);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + Objects.hashCode(this.username);
        hash = 61 * hash + this.school;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Profile other = (Profile) obj;
        if (this.userID != other.userID)
            return false;
        if (this.grade != other.grade)
            return false;
        if (this.school != other.school)
            return false;
        if (!Objects.equals(this.username, other.username))
            return false;
        return Objects.equals(this.dob, other.dob);
    }

    @Override
    public String toString() {
        String string = "Profile{" + "username=" + username + ", userID=" + userID + ", courses=";
        if (courses != null)
            for (Course course : courses) {
                string += "\n"+course + ", ";
            }
        else
            string += "null";
        string += "}";
        return string;
    }

}
