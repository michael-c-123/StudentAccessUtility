
package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import student.Course;
import student.EntryGrade;
import student.Profile;

/**
 * @author Michael
 */
public class WebAccessor {

    private static final String HOME = "https://pac.conroeisd.net/cisd.asp";
    private static final String LOGIN = "https://pac.conroeisd.net/slogin.asp";
    private static String[] fields = new String[7]; //temporary fields save for updating
    private static final ChromeOptions OPTIONS;

    static {
        //set directory of ChromeDriver
        String projPath = System.getProperty("user.dir"); //user.dir is the directory property of the project
        System.setProperty("webdriver.chrome.driver", projPath + "/lib/chromedriver/chromedriver.exe");

        //set up Chrome options
        OPTIONS = new ChromeOptions();
        OPTIONS.addArguments("--disable-web-security"); //disable password
        OPTIONS.addArguments("--no-proxy-server");      //disable password
        OPTIONS.addArguments("--disable-settings-window");
        OPTIONS.addArguments("--disable-notifications");
        OPTIONS.addArguments("--disable-infobars");
        OPTIONS.addArguments("--ignore-certificate-errors");
        OPTIONS.addArguments("--disable-extensions"); // disable extensions
        OPTIONS.addArguments("user-data-dir=" + projPath + "\\Automation");
        System.out.println(projPath + "\\Automation");
        OPTIONS.addArguments("--disable-plugins");
        OPTIONS.addArguments("--start-maximized");
        OPTIONS.addArguments("--app=" + LOGIN); //open in app mode on SAC
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        OPTIONS.setExperimentalOption("prefs", prefs);
    }

    public static Profile createNewProfile() {
        //fields that will be used
        fields = new String[7];
        Arrays.fill(fields, "");

        Profile profile;

        WebDriver driver = null;
        try {
            boolean loggedIn = false;
            driver = new ChromeDriver(OPTIONS);
            WebDriverWait wait = new WebDriverWait(driver, 86400);
            while (!loggedIn) {
                wait.until(ExpectedConditions.or(
                        ExpectedConditions.not(ExpectedConditions.urlMatches(Pattern.quote(LOGIN))), //success or clicked away
                        fieldsChanged(driver), //fields are changed
                        closed()
                ));
                System.out.print("past wait: ");
                System.out.println(driver.getCurrentUrl()+"####"+driver.getTitle());

                if (driver.getCurrentUrl().equalsIgnoreCase(HOME)) //success
                    loggedIn = true;
                else if (!driver.getCurrentUrl().equalsIgnoreCase(LOGIN)) //clicked away
                    driver.get(LOGIN);
                else //fields changed
                    updateFields(driver);
            }

            driver.manage().window().setPosition(new Point(-2000, 0));  //hide page
            System.out.println(Arrays.toString(fields));
            profile = new Profile(
                    fields[0].toLowerCase(),
                    Integer.parseInt(fields[1]),
                    new GregorianCalendar(
                            Integer.parseInt(fields[4]), //year
                            Integer.parseInt(fields[2]) - 1, //month (start at 0, so subtract 1)
                            Integer.parseInt(fields[3])), //day
                    Integer.parseInt(fields[5]),
                    Integer.parseInt(fields[6]));
            driver.switchTo().frame("main");
            updateReportCard(driver, profile); //fill out courses for profile
            updateClassWork(driver, profile, true); //get grades from each class
            Collections.sort(profile.getCourses());
        }
        catch (WebDriverException e) {  //closed
            e.printStackTrace();
            return null;
        }
        finally {
            if (driver != null)
                driver.quit();
        }

        return profile;
    }

    public static boolean studentAccess(Profile profile) {
        WebDriver driver = null;

        //wait for user to successfully login
        try {
            boolean loggedIn = false;
            driver = new ChromeDriver(OPTIONS);
            WebDriverWait wait = new WebDriverWait(driver, 86400);
            while (!loggedIn) {
                sendKeys(driver, profile);   //enter data
                try {
                    System.out.println("waiting");
                    wait.until(ExpectedConditions.not(ExpectedConditions.urlMatches(Pattern.quote(LOGIN))));
                    System.out.print("done waiting, now at: ");
                    System.out.println(driver.getCurrentUrl());

                }
                catch (StaleElementReferenceException e) {
                    System.out.print("its stale at: ");
                    System.out.println(driver.getCurrentUrl());

                }
                System.out.println(driver.getCurrentUrl());

                if (driver.getCurrentUrl().equals(HOME)) //success
                    loggedIn = true;
                else
                    driver.get(LOGIN);
            }
            driver.manage().window().setPosition(new Point(-2000, 0));  //hide page

            driver.switchTo().frame("main");    //go to the SAC frame
            updateReportCard(driver, profile);
            updateClassWork(driver, profile, false);
            Collections.sort(profile.getCourses());
            return true;
        }
        catch (WebDriverException | NullPointerException e) {
            System.out.println("TEST: webdriverexception");
            e.printStackTrace();
            return false;
        }
        finally {
            if (driver != null)
                driver.quit();
        }
    }

    private static boolean updateFields(WebDriver driver) throws NoSuchElementException {
        try {
            WebElement[] elements = new WebElement[7];
            elements[0] = driver.findElement(By.name("parentid"));
            elements[1] = driver.findElement(By.name("parentnumber"));
            elements[2] = driver.findElement(By.name("bdaymonth"));
            elements[3] = driver.findElement(By.name("bday"));
            elements[4] = driver.findElement(By.name("bdayyear"));
            elements[5] = driver.findElement(By.name("grade"));
            elements[6] = driver.findElement(By.name("building"));

            boolean fieldsUpdated = false;
            //go thru elements and compare them to original, change if different
            for (int i = 0; i < elements.length; i++) {
                WebElement element = elements[i];
                if (!element.getAttribute("value").equalsIgnoreCase(fields[i])) {
                    fields[i] = element.getAttribute("value").toLowerCase();
                    fieldsUpdated = true;
                }
            }
            return fieldsUpdated;
        }
        catch (NoSuchElementException | StaleElementReferenceException e) {
            System.out.println("TEST: not changed");
            return false;
        }
    }

    private static void sendKeys(WebDriver driver, Profile p) throws NoSuchElementException {
        WebElement table = driver.findElement(By.cssSelector(
                "body > font > center > form > table > tbody > tr:nth-child(2) > td:nth-child(2) > table > tbody"));
        //find all the elements
        table.findElement(By.cssSelector(
                "tr:nth-child(1) > td:nth-child(2) > input[type=\"text\"]")
        ).sendKeys(p.getFields()[0]);
        table.findElement(By.cssSelector(
                "tr:nth-child(2) > td:nth-child(2) > input[type=\"password\"]")).sendKeys(p.getFields()[1]);
        new Select(table.findElement(By.cssSelector(
                "tr:nth-child(3) > td:nth-child(2) > select:nth-child(1)"))).selectByValue(p.getFields()[2]);
        new Select(table.findElement(By.cssSelector(
                "tr:nth-child(3) > td:nth-child(2) > select:nth-child(2)"))).selectByValue(p.getFields()[3]);
        new Select(table.findElement(By.cssSelector(
                "tr:nth-child(3) > td:nth-child(2) > select:nth-child(3)"))).selectByValue(p.getFields()[4]);
        new Select(table.findElement(By.cssSelector(
                "tr:nth-child(4) > td:nth-child(2) > select"))).selectByValue(p.getFields()[5]);
        new Select(table.findElement(By.cssSelector(
                "tr:nth-child(5) > td:nth-child(2) > select"))).selectByValue(p.getFields()[6]);
    }

    private static void updateReportCard(WebDriver driver, Profile profile) throws NoSuchElementException {
        WebElement reportTab = driver.findElement(By.xpath("//*[text()[contains(.,'Report')]]"));
        reportTab.click();
        WebElement table = driver.findElement(By.cssSelector("body > center:nth-child(6) > form > table > tbody"));
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        List<String> names = new ArrayList<>();
        List<Integer> periods = new ArrayList<>();
        List<String> grades = new ArrayList<>();

        for (int i = 2; i < rows.size(); i++) {
            WebElement row = rows.get(i);
            List<WebElement> cells = row.findElements(By.tagName("td"));
            //TEST
            for (WebElement cell : cells) {
                System.out.println(cell.getText());
            }
            System.out.println("END ROW");
            //ENDTEST
            names.add(cells.get(0).getText());
            periods.add(Integer.parseInt(cells.get(1).getText()));
            grades.add(cells.get(3).getText());
            System.out.println(cells.get(3).getText());

        }
        //add/update courses
        for (int i = 0; i < grades.size(); i++) {
            Course rowCourse = profile.getCourseByName(names.get(i));
            if (rowCourse == null) {
                rowCourse = new Course(names.get(i),
                        periods.get(i));
                profile.addCourse(rowCourse);
            }
            String m1 = grades.get(i);
            if (m1 != null && !m1.isEmpty()) {
                rowCourse.getM1().setValue(Integer.parseInt(m1));
                rowCourse.setOnM1(true);
            }
            else
                rowCourse.setOnM1(false);
        }
        //clean courses for extras
        if (profile.getCourses().size() != names.size())
            for (Course course : profile.getCourses()) {
                String courseName = course.getName();
                if (!names.contains(courseName))
                    profile.removeCourse(course);
            }
        //TEST
        for (Course course : profile.getCourses()) {
            System.out.println(course);
        }
    }

    private static void updateClassWork(WebDriver driver, Profile profile, boolean verify) throws NoSuchElementException {
        try {
            WebElement workTab = driver.findElement(By.xpath("//*[text()[contains(.,'Work')]]"));
            workTab.click();
            WebElement top = driver.findElement(By.xpath("/html/body/center[2]/table/tbody/tr[1]/td"));
            int index = top.getText().indexOf(" is "); //find this in the text
            int markingPeriod = top.getText().charAt(index + 4) - 48; //move 4 indices up to get marking period, -48 to convert to int
            if (markingPeriod > 4 || markingPeriod < 1)
                throw new ArrayIndexOutOfBoundsException("Invalid makring period:" + markingPeriod);
            for (Course course : profile.getCourses()) {
                course.setOnM1(markingPeriod % 2 != 0); //1 or 3 means set to M1
            }
            WebElement center = driver.findElement(By.cssSelector("body > center:nth-child(7) > center"));
            recursiveNavigateTables(profile, center, verify);

        }
        catch (NoSuchElementException e) {
            System.out.println("TEST: failed to get courses");
        }
    }

    private static void recursiveNavigateTables(Profile profile, WebElement center, boolean verify) throws NoSuchElementException {
        WebElement head = center.findElement(By.cssSelector("table:nth-child(1) > tbody > tr:nth-child(1)"));
        WebElement gradeTable = center.findElement(By.cssSelector("table:nth-child(1) > tbody > tr:nth-child(2) > td > table > tbody"));

        String headTitle = head.getText().split(" - ")[1];
        System.out.println(headTitle);
        Course course = profile.getCourseByName(headTitle);

        if (course != null) {

            List<WebElement> rows = gradeTable.findElements(By.tagName("tr"));
            List<EntryGrade> grades = course.getGradeList();
            List<EntryGrade> newGrades = new ArrayList<>();

            //go through the table, adding each row as a EntryGrade to newGrades
            for (int i = 1; i < rows.size() - 1; i++) { //ignore the first and last rows
                List<WebElement> rowCells = rows.get(i).findElements(By.tagName("td"));
                String gradeString = rowCells.get(4).getText();
                if (gradeString.equals("-") || gradeString.equalsIgnoreCase("X"))
                    continue;
                boolean extra = rowCells.get(7).getText().equals("0");

                if (gradeString.equalsIgnoreCase("Z"))
                    gradeString = "0";
                String[] date = rowCells.get(0).getText().split("/");
                EntryGrade read = new EntryGrade(
                        new GregorianCalendar(Integer.parseInt(date[2]), Integer.parseInt(date[0]) - 1, Integer.parseInt(date[1])),
                        rowCells.get(2).getText(),
                        rowCells.get(3).getText().equalsIgnoreCase("Major"),
                        Double.parseDouble(rowCells.get(5).getText()),
                        Double.parseDouble(gradeString));
                read.setExtra(extra);
                newGrades.add(read);
            }

            //update the course's gradeList using newGrades
            Predicate<EntryGrade> predicate
                    = grade -> !grade.isCustom() && !newGrades.contains(grade);
            grades.removeIf(predicate); //remove all normal grades that are no longer present in the table
            for (int i = 0; i < newGrades.size(); i++) {
                EntryGrade entry = newGrades.get(i);
                if (grades.contains(entry)) {
                    if (i < grades.size() && !grades.get(i).equals(entry))
                        Collections.swap(grades, i, grades.indexOf(entry)); //update ordering by swapping it to the correct spot
                    //don't do anything if it's already at the correct spot
                }
                else
                    grades.add(i, entry); //add a new one to the correct spot
            }

            //get actual grade, compare with calculated
            WebElement tail = center.findElement(By.cssSelector("table:nth-child(2) > tbody > tr > td > b"));
            String text = tail.getText();
            System.out.println(text);
            text = text.substring(0, text.indexOf("("));
            text = text.substring(text.indexOf(":") + 1);
            text = text.trim();
            int finalScore = Integer.parseInt(text);
            if (verify)
                course.verifySplit(finalScore);
            course.setActualEstimate(finalScore);
        }
        else
            throw new IllegalStateException("No such course: " + headTitle);

        List<WebElement> cenList = center.findElements(By.cssSelector("center"));
        if (!cenList.isEmpty())
            recursiveNavigateTables(profile, cenList.get(0), verify);
    }

    private static ExpectedCondition<Boolean> fieldsChanged(WebDriver driver) {
        WebElement[] elements = new WebElement[7];
        elements[0] = driver.findElement(By.name("parentid"));
        elements[1] = driver.findElement(By.name("parentnumber"));
        elements[2] = driver.findElement(By.name("bdaymonth"));
        elements[3] = driver.findElement(By.name("bday"));
        elements[4] = driver.findElement(By.name("bdayyear"));
        elements[5] = driver.findElement(By.name("grade"));
        elements[6] = driver.findElement(By.name("building"));
        ExpectedCondition<Boolean> condition = ExpectedConditions.not(
                ExpectedConditions.and(
                        ExpectedConditions.attributeToBe(elements[0], "value", fields[0]),
                        ExpectedConditions.attributeToBe(elements[1], "value", fields[1]),
                        ExpectedConditions.attributeToBe(elements[2], "value", fields[2]),
                        ExpectedConditions.attributeToBe(elements[3], "value", fields[3]),
                        ExpectedConditions.attributeToBe(elements[4], "value", fields[4]),
                        ExpectedConditions.attributeToBe(elements[5], "value", fields[5]),
                        ExpectedConditions.attributeToBe(elements[6], "value", fields[6])
                ));
        return condition;
    }

    private static ExpectedCondition<Boolean> closed() {
        ExpectedCondition<Boolean> condition = (WebDriver driver) -> {
            try {
                driver.getTitle();
                return true;
            }
            catch (Exception ex) {
                System.out.println("CLOSED");
                return false;
            }
        };
        return ExpectedConditions.not(condition);
    }
}
