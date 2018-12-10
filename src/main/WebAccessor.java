
package main;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
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
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
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
    private static final InternetExplorerOptions IE_OPTIONS;

    static {
        //set directory of ChromeDriver
        String projectLoc = System.getProperty("user.dir"); //user.dir is the directory property of the project
        System.setProperty("webdriver.chrome.driver", projectLoc + "/lib/chromedriver/chromedriver.exe");
//        System.setProperty("webdriver.ie.driver", projectLoc + "/lib/iedriver/IEDriverServer.exe");
//        System.setProperty("webdriver.gecko.driver", projectLoc + "/lib/geckodriver/geckodriver.exe");
//        System.setProperty("webdriver.opera.driver", projectLoc + "/lib/operadriver/operadriver.exe");

        DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
        capabilities.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, false);
        capabilities.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, false);
        capabilities.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        capabilities.setJavascriptEnabled(false);
        capabilities.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL, LOGIN);
        IE_OPTIONS = new InternetExplorerOptions(capabilities);

        //set up Chrome options
        OPTIONS = new ChromeOptions();
        OPTIONS.addArguments("--disable-web-security"); //disable password
        OPTIONS.addArguments("--no-proxy-server");      //disable password
        OPTIONS.addArguments("--disable-settings-window");
        OPTIONS.addArguments("--disable-notifications");
        OPTIONS.addArguments("--disable-infobars");
        OPTIONS.addArguments("--ignore-certificate-errors");
        OPTIONS.addArguments("--disable-extensions"); // disabling extensions

        OPTIONS.addArguments("user-data-dir=C:\\Users\\Michael\\AppData\\Local\\Google\\Chrome\\User Data");
//        OPTIONS.addArguments("user-data-dir=C:\\Users\\Michael\\Desktop");
//        OPTIONS.addArguments("profile-directory=".concat("Default"));
        OPTIONS.addArguments("--disable-plugins");
        OPTIONS.addArguments("--start-maximized");
        OPTIONS.addArguments("--app=" + LOGIN); //open in app mode on SAC
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        OPTIONS.setExperimentalOption("prefs", prefs);
    }

    public static Profile createNewProfile() throws WebDriverException {
        //fields that will be used
        fields = new String[7];
        Arrays.fill(fields, "");

//        List<Course>[] lists; //TODO
        Profile profile;

        WebDriver driver = null;
        try {
            driver = new ChromeDriver(OPTIONS);
            //wait for user to successfully login
            boolean loggedIn = false;
            WebDriverWait wait = new WebDriverWait(driver, 86400);
            do {
                try {
                    wait.until(ExpectedConditions.or(
                            ExpectedConditions.urlMatches(HOME), //successful
                            fieldsChanged(driver, fields) //fields are changed
                    ));
                }
                catch (StaleElementReferenceException e) {
                    if (driver.findElements(By.name("parentid")).isEmpty() //no userID field found
                            && !driver.getCurrentUrl().equals(HOME)) { //not currently on the login screen
                        driver.navigate().refresh(); //refresh page
                        System.out.println("TEST: halp when does this trigger");
                        continue;
                    }
                }
//            System.out.println("TEST: passed wait");

                if (driver.getCurrentUrl().equals(HOME))
                    loggedIn = true;
                else
                    updateFields(driver);
            }
            while (!loggedIn);

            driver.manage().window().setPosition(new Point(-2000, 0));  //hide page
            System.out.println(Arrays.toString(fields));
            profile = new Profile(
                    fields[0].toLowerCase(),
                    Integer.parseInt(fields[1]),
                    new GregorianCalendar(
                            Integer.parseInt(fields[4]),
                            Integer.parseInt(fields[2]) - 1, //because months start at zero
                            Integer.parseInt(fields[3])),
                    Integer.parseInt(fields[5]),
                    Integer.parseInt(fields[6]));
            driver.switchTo().frame("main");
            updateReportCard(driver, profile); //fill out courses for profile
            updateClassWork(driver, profile); //get grades from each class
        }
        catch (WebDriverException e) {  //brower closed
//            e.printStackTrace();
            return null;
        }
        finally {
            if (driver != null)
                driver.quit();
        }

        //TODO filter out non high school grade levels, loop back to login screen
        return profile;
    }

    public static void studentAccess(Profile profile) throws InterruptedException {
        WebDriver driver = null;

        //wait for user to successfully login
        try {
            fields = new String[7]; //fields that will be compared to check if user changed them
            Arrays.fill(fields, "");
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
            try {
                updateReportCard(driver, profile);
                updateClassWork(driver, profile);
            }
            catch (NoSuchElementException e) {
                System.err.println("TEST: failed to get grades.");
            }
        }
        catch (WebDriverException e) {
            System.out.println("TEST: webdriverexception");
            e.printStackTrace();
        }
        finally {
            if (driver != null)
                driver.quit();
        }
    }

    private static boolean updateFields(WebDriver driver) {
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

    private static void sendKeys(WebDriver driver, Profile p) {
        WebElement table = driver.findElement(By.cssSelector(
                "body > font > center > form > table > tbody > tr:nth-child(2) > td:nth-child(2) > table > tbody"));
        long time = System.currentTimeMillis(); //check time taken for each element filled
        //find all the elements
        System.out.println(0);
        table.findElement(By.cssSelector(
                "tr:nth-child(1) > td:nth-child(2) > input[type=\"text\"]")
        ).sendKeys(p.getFields()[0]);
        System.out.println(System.currentTimeMillis() - time);
        time = System.currentTimeMillis();
        table.findElement(By.cssSelector(
                "tr:nth-child(2) > td:nth-child(2) > input[type=\"password\"]")).sendKeys(p.getFields()[1]);
        System.out.println(System.currentTimeMillis() - time);
        time = System.currentTimeMillis();
        new Select(table.findElement(By.cssSelector(
                "tr:nth-child(3) > td:nth-child(2) > select:nth-child(1)"))).selectByValue(p.getFields()[2]);
        System.out.println(System.currentTimeMillis() - time);
        time = System.currentTimeMillis();
        new Select(table.findElement(By.cssSelector(
                "tr:nth-child(3) > td:nth-child(2) > select:nth-child(2)"))).selectByValue(p.getFields()[3]);
        System.out.println(System.currentTimeMillis() - time);
        time = System.currentTimeMillis();
        new Select(table.findElement(By.cssSelector(
                "tr:nth-child(3) > td:nth-child(2) > select:nth-child(3)"))).selectByValue(p.getFields()[4]);
        System.out.println(System.currentTimeMillis() - time);
        time = System.currentTimeMillis();
        new Select(table.findElement(By.cssSelector(
                "tr:nth-child(4) > td:nth-child(2) > select"))).selectByValue(p.getFields()[5]);
        System.out.println(System.currentTimeMillis() - time);
        time = System.currentTimeMillis();
        new Select(table.findElement(By.cssSelector(
                "tr:nth-child(5) > td:nth-child(2) > select"))).selectByValue(p.getFields()[6]);
        System.out.println(System.currentTimeMillis() - time);
    }

    private static void updateReportCard(WebDriver driver, Profile profile) {
        try {
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
        catch (NoSuchElementException e) {
            System.out.println("TEST: failed to get report card");
            e.printStackTrace();
        }
    }

    private static void updateClassWork(WebDriver driver, Profile profile) {
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
            recursiveNavigateTables(profile, center);

        }
        catch (NoSuchElementException e) {
            System.out.println("TEST: failed to get courses");
        }
    }

    public static void recursiveNavigateTables(Profile profile, WebElement center) {
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
                String[] date = rowCells.get(0).getText().split("/");
                if (gradeString.equalsIgnoreCase("z"))
                    gradeString = "0";
                EntryGrade read = new EntryGrade(
                        new GregorianCalendar(Integer.parseInt(date[2]), Integer.parseInt(date[0]) - 1, Integer.parseInt(date[1])),
                        rowCells.get(2).getText(),
                        rowCells.get(3).getText().equalsIgnoreCase("Major"),
                        Double.parseDouble(rowCells.get(5).getText()),
                        Double.parseDouble(gradeString));
                newGrades.add(read);
            }

            //update the course's gradeList using newGrades
            Predicate<EntryGrade> predicate
                    = grade -> !grade.isCustom() && !newGrades.contains(grade);
            grades.removeIf(predicate); //remove all normal grades that are no longer present in the table
            for (int i = 0; i < newGrades.size(); i++) {
                EntryGrade entry = newGrades.get(i);
                if (grades.contains(entry)) {
                    if (!grades.get(i).equals(entry))
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
            course.verifySplit(finalScore);
        }
        else
            throw new IllegalStateException("No such course: " + headTitle);

        List<WebElement> cenList = center.findElements(By.cssSelector("center"));
        if (!cenList.isEmpty())
            recursiveNavigateTables(profile, cenList.get(0));
    }

    private static ExpectedCondition<Boolean> fieldsChanged(WebDriver driver, String[] fields) throws NoSuchWindowException {
        try {
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
        catch (NoSuchElementException | StaleElementReferenceException e) {
            System.out.println("TEST: not changed");
            return (WebDriver f) -> {
                return false;
            };
        }
    }

    public static boolean isInternetReachable() {
        try {
            //make a URL to a known source
            URL url = new URL(LOGIN);

            //open a connection to that source
            HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();

            //trying to retrieve data from the source. If there
            //is no connection, this line will fail
            urlConnect.getContent();
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
