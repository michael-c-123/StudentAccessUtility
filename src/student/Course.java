
package student;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael
 */
public class Course implements Serializable, Comparable<Course> {
    private static final long serialVersionUID = 0L;
    private static final double TEST_SPLIT = .15;

    private String name;
    private final int period;
    private ArrayList<EntryGrade> gradeList;
    private Grade exam, m1, m2, sem;
    private Grade major, daily;
    private double majorSplit;
    private boolean onM1 = true;
    private int actualEstimate;

    public Course(String name, int period) {
        this.name = name;
        gradeList = new ArrayList<>();
        m1 = new Grade(Grade.NO_VALUE);
        m2 = new Grade(Grade.NO_VALUE);
        exam = new Grade(Grade.NO_VALUE);
        sem = new Grade(Grade.NO_VALUE);
        major = new Grade(Grade.NO_VALUE);
        daily = new Grade(Grade.NO_VALUE);
        this.period = period;

        double split;
        if (name.matches(".*\\WAP(\\W|$).*") || name.matches(".*\\WDC(\\W|$).*"))
            split = .8;
        else if (name.matches(".*?H$") || name.contains("PreAP"))
            split = .7;
        else
            split = .6;

        majorSplit = split;
    }

    public String getName() {
        return name;
    }

    public ArrayList<EntryGrade> getGradeList() {
        return gradeList;
    }

    public double getMajorSplit() {
        return majorSplit;
    }

    private boolean hasGradeType(int type) {
        for (EntryGrade grade : gradeList) {
            if (grade.getModStatus() == type) {
                return true;
            }
        }
        return false;
    }

    public Grade getMajor() {
        return major;
    }

    public Grade getDaily() {
        return daily;
    }

    public Grade getExam() {
        return exam;
    }

    public Grade getM1() {
        return m1;
    }

    public Grade getM2() {
        return m2;
    }

    public Grade getSem() {
        return sem;
    }

    public Grade getCurrent(boolean current) {
        if (current)
            return onM1 ? m1 : m2;
        return onM1 ? m2 : m1;
    }

    public void setCurrent(boolean current, double value) {
        Grade gr = getCurrent(current);
        gr.setValue(value);

    }

    public boolean isOnM1() {
        return onM1;
    }

    public void setOnM1(boolean onM1) {
        this.onM1 = onM1;
    }

    public void addGrade(EntryGrade grade) {
        gradeList.add(grade);
    }

    public boolean hasGrade(EntryGrade grade) {
        return gradeList.contains(grade);
    }

    //verify the split between major/daily to see if it adds up
    public void verifySplit(int checkAgainst) {
        double splitTest = majorSplit;
        boolean match = Math.round(calcActualGradeUsing(splitTest)) == checkAgainst;
        if (match)
            return;

        for (int percent = 60; percent <= 80 && !match; percent += 10) {
            splitTest = percent / 100.0; //prevents roundoff errors
            double splitCalc = calcActualGradeUsing(splitTest);
            match = Math.ceil(splitCalc) == checkAgainst;
            System.out.println(name + " matches " + splitTest + ": " + splitCalc);
        }
        for (int percent = 50; percent <= 90 && !match; percent += 5) {
            splitTest = percent / 100.0;
            double splitCalc = calcActualGradeUsing(splitTest);
            match = Math.ceil(splitCalc) == checkAgainst;
            System.out.println(name + " matches " + splitTest + ": " + splitCalc);
        }

        if (!match) { //does not match typical splits, must recalculate
            double majorGrade = calcActualSplit(true);
            double dailyGrade = calcActualSplit(false);
            //divide by zero will never occur because major and daily will never be the same
            splitTest = (checkAgainst - dailyGrade) / (majorGrade - dailyGrade);
        }

        majorSplit = splitTest;
    }

    private double calcActualGradeUsing(double split) {
        double majorGrade = calcActualSplit(true);
        double dailyGrade = calcActualSplit(false);

        if (majorGrade == Double.NEGATIVE_INFINITY)
            return dailyGrade;
        else if (dailyGrade == Double.NEGATIVE_INFINITY)
            return majorGrade;
        else
            return majorGrade * split + dailyGrade * (1 - split);
    }

    private double calcActualSplit(boolean isMajor) {
        double total = 0;
        double weight = 0;
        for (EntryGrade grade : gradeList) {
            boolean match = isMajor == grade.isMajor();
            if (match && !grade.isEmpty() && !grade.isCustom()) { //add all requested non-empty, non-custom grades
                total += grade.getValue() * grade.getWeight();
                if (!grade.isExtra())
                    weight += grade.getWeight();
            }
        }
        if (weight == 0)
            return Double.NEGATIVE_INFINITY;
        else
            return EntryGrade.toPercent(total / weight);
    }

    private void calcSplit(boolean isMajor) {
        double total = 0;
        double weight = 0;
        boolean changed = false;
        for (EntryGrade grade : gradeList) {
            boolean match = isMajor == grade.isMajor();
            if (match && !grade.isEmpty()) {
                total += grade.getValue() * grade.getWeight();
                if (!grade.isExtra())
                    weight += grade.getWeight();
                if (!changed && grade.isCustom())
                    changed = true;
            }
        }
        Grade toChange = isMajor ? major : daily;
        toChange.reset();
        if (weight == 0)
            toChange.setValue(Grade.NO_VALUE);
        else {
            double score = total / weight;
            score = EntryGrade.toPercent(score);
            if (changed)
                toChange.react(score);
            else
                toChange.setValue(score);
        }
    }

    public void update() {
        Grade current = getCurrent(true);
        Grade other = getCurrent(false);

        if (other.isFixed() && exam.isFixed() && sem.isFixed()) { //all others manipulated
            //current and list must respond
            double result = (sem.getValue() - TEST_SPLIT * exam.getValue())
                    * (2 / (1 - TEST_SPLIT)) - other.getValue(); //CALC B
            current.respond(result);
            calcRespondingGrades(result);
            calcSplit(true);
            calcSplit(false);
        }
        else if (current.getModStatus() == Grade.MANIPULATED) { //manipulated current
            calcRespondingGrades(current.getValue());
            calcSplit(true);
            calcSplit(false);
        }
        else {
            //normal calculation to get current grade
            resetRespondingGrades();
            calcSplit(true);
            calcSplit(false);
            double result; //CALC C
            if (major.isEmpty() ^ daily.isEmpty()) {
                result = daily.isEmpty() ? major.getValue() : daily.getValue();
            }
            else
                result = major.getValue() * majorSplit + daily.getValue() * (1 - majorSplit);
            current.reset();
            if (hasGradeType(Grade.MANIPULATED))
                current.react(result);
            else
                current.setValue(result); //the most normal possible result
        }

        if (other.isFixed()
                && exam.isFixed()
                && sem.isPliable()) {
            double result = (current.getValue() + other.getValue()) //CALC D
                    * ((1 - TEST_SPLIT) / 2)
                    + TEST_SPLIT * exam.getValue();
            sem.respond(result);

        }
        else if (other.isFixed()
                && sem.isFixed()
                && exam.isPliable()) {
            double result = (sem.getValue() //CALC E
                    - (current.getValue() + other.getValue())
                    * ((1 - TEST_SPLIT) / 2)) / TEST_SPLIT;
            exam.respond(result);
        }
        else if (exam.isFixed()
                && sem.isFixed()
                && other.isPliable()) {
            double result = (sem.getValue() - TEST_SPLIT * exam.getValue()) //CALC F
                    * (2 / (1 - TEST_SPLIT)) - current.getValue();
            other.respond(result);
        }
        //if lost information, reset grades that were once responding
        else {
            if (other.getModStatus() == Grade.RESPONDING)
                other.reset();
            if (exam.getModStatus() == Grade.RESPONDING)
                exam.reset();
            if (sem.getModStatus() == Grade.RESPONDING)
                sem.reset();
        }
    }

    private void calcRespondingGrades(double q) { //q is target grade variable
        calcSplit(true);
        calcSplit(false);

        List<EntryGrade> list = new ArrayList<>();
        double gm = 0, gd = 0;
        double wm = 0, wd = 0;
        double nm = 0, nd = 0;

        if (!hasGradeType(Grade.RESPONDING)) //if there are no responding grades, make one
            addGrade(new EntryGrade(Grade.RESPONDING, "Response Grade", true, 1, Grade.NO_VALUE));

        for (EntryGrade grade : gradeList) {
            if (grade.getModStatus() == Grade.RESPONDING) {
                list.add(grade);
                if (grade.isMajor())
                    nm += grade.getWeight();
                else
                    nd += grade.getWeight();
            }
            else {
                if (grade.isMajor()) {
                    gm += grade.getValue() * grade.getWeight();
                    wm += grade.getWeight();
                }
                else {
                    gd += grade.getValue() * grade.getWeight();
                    wd += grade.getWeight();
                }
            }
        }

        q = EntryGrade.toFivePointScale(q);

        double x;
        if (nd == 0 && wd == 0)
            x = (q * (wm + nm) - gm) / nm;
        else if (nm == 0 && wm == 0)
            x = (q * (wd + nd) - gd) / nd;
        else {
            double k = majorSplit;
            x = -(gm * k * (nd + wd) - (nm + wm) * (gd * (k - 1) + q * (nd + wd)))
                    / (nd * (-k * wm + nm + wm) + k * nm * wd); //CALC A
        }

        for (EntryGrade grade : list) {
            grade.respond(x);
        }
    }

    private void resetRespondingGrades() {
        for (EntryGrade grade : gradeList) {
            if (grade.getModStatus() == Grade.RESPONDING) {
                grade.setValue(Grade.NO_VALUE);
            }
        }
    }

    public int getActualEstimate() {
        return actualEstimate;
    }

    public void setActualEstimate(int actualEstimate) {
        this.actualEstimate = actualEstimate;
    }

    @Override
    public String toString() {
        return "Course{" + "name=" + name + ", period=" + period + ", exam=" + exam + ", m1=" + m1 + ", m2=" + m2 + ", sem=" + sem + ", major=" + major + ", daily=" + daily + ", majorSplit=" + majorSplit + ", onM1=" + onM1 + '}';
    }

    @Override
    public int compareTo(Course other) {
        return period - other.period;
    }

}
