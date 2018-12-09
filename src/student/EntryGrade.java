
package student;

import java.util.Calendar;
import java.util.Objects;

/**
 * @author Michael
 */
public final class EntryGrade extends Grade {
    private static final long serialVersionUID = 0L;

    private Calendar date;
    private final String originalName; //note: not used for user defined entries
    private String name;
    private boolean major;
    private double weight;

    private boolean grayed;

    public EntryGrade(Calendar date, String name, boolean major, double weight, double grade) {
        super(grade);
        this.date = date;
        this.name = originalName = name;
        this.major = major;
        this.weight = weight;

    }

    public EntryGrade(int modStatus, String name, boolean major, double weight, double grade) {
        super(grade);
        this.name = originalName = name;
        this.major = major;
        this.weight = weight;

        setModStatus(modStatus);
    }

    public boolean isCustom() {
        return getModStatus() != NORMAL;
    }

    public void setGrayed(boolean grayed) {
        this.grayed = grayed;
    }

    public Calendar getDate() {
        return date;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void resetName() {
        this.name = originalName;
    }

    public boolean isMajor() {
        return major;
    }

    public void setMajor(boolean major) {
        if (getModStatus() == NORMAL)
            throw new IllegalStateException("Normal grade cannot have its major/daily status modified.");
        this.major = major;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        if (getModStatus() == NORMAL)
            throw new IllegalStateException("Normal grade cannot have its weight modified.");
        this.weight = weight;
    }

    public boolean isGrayed() {
        return grayed;
    }

    @Override
    public void respond(double grade) {
        if (getModStatus() != RESPONDING)
            throw new IllegalStateException("This grade cannot respond.");
        super.respond(grade);
    }

    @Override
    public void manipulate(double grade) {
        if (getModStatus() != MANIPULATED)
            throw new IllegalStateException("This grade cannot be manipulated.");
        super.manipulate(grade);
    }

    @Override
    public void reset() {
        throw new IllegalStateException("Entry grades cannot be reset.");
    }

    public static double toFivePointScale(double percent) {
        if (percent >= 60)
            return 5.0 - (100 - percent) / 10;
        else
            return percent / 60;
    }

    public static double toPercent(double points) {
        if (points > 1)
            return 100 - (5 - points) * 10;
        else
            return points * 60;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.originalName);
        hash = 61 * hash + (this.major ? 1 : 0);
        hash = 61 * hash + (int) (Double.doubleToLongBits(this.weight) ^ (Double.doubleToLongBits(this.weight) >>> 32));
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
        final EntryGrade other = (EntryGrade) obj;
        if (this.major != other.major)
            return false;
        if (Double.doubleToLongBits(this.weight) != Double.doubleToLongBits(other.weight))
            return false;
        return Objects.equals(this.originalName, other.originalName);
    }


}
