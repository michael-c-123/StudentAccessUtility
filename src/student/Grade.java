
package student;

import java.awt.Color;
import java.io.Serializable;
import java.util.Map;

/**
 * @author Michael
 */
public class Grade implements Serializable {
    private static final long serialVersionUID = 0L;

    private final double originalValue;
    private double value;

    public static final int MANIPULATED = 1, RESPONDING = 2, REACTING = 3, NORMAL = 0;
    public static final double NO_VALUE = Double.NEGATIVE_INFINITY; //no grade number assigned to grade
    private int modStatus = NORMAL;

    public Grade(double grade) {
        this.originalValue = grade;
        this.value = grade;
    }

    public double getOriginalValue() {
        return originalValue;
    }

    public double getValue() {
        return value;
    }

    public String getFormattedString() {
        if (isEmpty())
            return "-";
        return String.format("%.1f", value);
    }

    public boolean isEmpty() {
        return value == NO_VALUE;
    }

    public boolean isFixed() {
        if (isEmpty())
            return false;
        return (modStatus == MANIPULATED) || (modStatus == NORMAL);
    }

    public boolean isPliable() {
        return isEmpty() || modStatus == RESPONDING;
    }

    public int getModStatus() {
        return modStatus;
    }

    public void setModStatus(int modStatus) {
        if (modStatus == MANIPULATED || modStatus == RESPONDING || modStatus == NORMAL)
            this.modStatus = modStatus;
        else
            throw new IllegalArgumentException("Invalid modification status- must be manipulated, responding, or normal.");
    }

    public void reset() {
        setValue(originalValue);
        modStatus = NORMAL;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void manipulate(double grade) {
        if (grade == NO_VALUE)
            throw new IllegalArgumentException("Can't manipulate with no value.");
        setValue(grade);
        modStatus = MANIPULATED;
    }

    public void respond(double grade) {
        if (grade == NO_VALUE)
            throw new IllegalArgumentException("Can't manipulate with no value.");
        setValue(grade);
        modStatus = RESPONDING;
    }

    public void react(double grade) {
        if (grade == NO_VALUE)
            throw new IllegalArgumentException("Can't manipulate with no value.");
        setValue(grade);
        modStatus = REACTING;
    }

    public static Color getColor(int modStatus, Map<String, Object> settings) {
        if (modStatus == Grade.MANIPULATED)
            return (Color) settings.get("color manipulated");
        else if (modStatus == Grade.RESPONDING)
            return (Color) settings.get("color responding");
        else if (modStatus == Grade.NORMAL)
            return (Color) settings.get("color normal");
        throw new RuntimeException("Invalid mod status");
    }

    public static Color getColorDark(int modStatus, Map<String, Object> settings) {
        if (modStatus == Grade.NORMAL || modStatus == Grade.REACTING)
            return (Color) settings.get("color dark");
        else
            return getColor(modStatus, settings);
    }

    @Override
    public String toString() {
        return "Grade{" + "value=" + value + ", modStatus=" + modStatus + '}';
    }

}
