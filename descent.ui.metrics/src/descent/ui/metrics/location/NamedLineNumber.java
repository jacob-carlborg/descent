package descent.ui.metrics.location;

public final class NamedLineNumber {
    private String name;
    private int lineNumber;

    public NamedLineNumber(String name, int lineNumber) {
        setName(name);
        setLineNumber(lineNumber);
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getName() {
        return name;
    }

    private void setLineNumber(int newLineNumber) {
        lineNumber = newLineNumber;
    }

    private void setName(String newName) {
        name = newName;
    }

    public int hashCode() {
        return getName().hashCode() ^ getLineNumber();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }

        NamedLineNumber that = (NamedLineNumber) obj;
        return getLineNumber() == that.getLineNumber() && getName().equals(that.getName());
    }

    public String toString() {
        return name + ", lineNumber=" + lineNumber;
    }
}