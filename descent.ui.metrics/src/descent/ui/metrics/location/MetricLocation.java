package descent.ui.metrics.location;

import java.util.HashMap;
import java.util.Map;

public final class MetricLocation {
    public interface Closure {
        public void execute(MetricLocation location);
    }

    private static Map locations = new HashMap();

    private String packageName;
    private NamedLineNumber typeInfo;
    private NamedLineNumber methodInfo;

    private MetricLocation(String packageName, NamedLineNumber typeInfo, NamedLineNumber methodInfo) {
        this.packageName = packageName;
        this.typeInfo = typeInfo;
        this.methodInfo = methodInfo;
    }

    public static MetricLocation findOrCreate(String packageName, NamedLineNumber typeInfo, NamedLineNumber methodInfo) {
        MetricLocation location = new MetricLocation(packageName, typeInfo, methodInfo);
        if (locations.containsKey(location)) {
            return (MetricLocation) locations.get(location);
        }

        locations.put(location, location);
        return location;
    }

    public static void clearLocations() {
        locations.clear();
    }

    public static MetricLocation findOrCreate(String packageName, NamedLineNumber typeInfo) {
        return findOrCreate(packageName, typeInfo, null);
    }

    public static MetricLocation findOrCreate(String packageName) {
        return findOrCreate(packageName, null, null);
    }

    public MetricLocation createContainingLocation() {
        if (hasMethod()) {
            return new MetricLocation(packageName, typeInfo, null);
        } else if (hasType()) {
            return new MetricLocation(packageName, null, null);
        } else {
            throw new IllegalStateException("No containing location:" + this);
        }
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean hasType() {
        return typeInfo != null;
    }

    public NamedLineNumber getTypeInfo() {
        if (!hasType()) {
            throw new IllegalStateException("No typeInfo available: " + this);
        }
        return typeInfo;
    }

    public NamedLineNumber getMethodInfo() {
        if (!hasMethod()) {
            throw new IllegalStateException("No methodInfo available: " + this);
        }
        return methodInfo;
    }

    public boolean hasMethod() {
        return methodInfo != null;
    }

    public int hashCode() {
        int hash = packageName.hashCode();
        if (!hasType()) {
            return hash;
        }

        hash ^= typeInfo.hashCode();
        if (!hasMethod()) {
            return hash;
        }

        return hash ^ methodInfo.hashCode();
    }

    public boolean equals(Object thatObj) {
        if (thatObj == this) {
            return true;
        }

        if (thatObj == null || !thatObj.getClass().equals(getClass())) {
            return false;
        }

        return equalsMetricLocation((MetricLocation) thatObj);
    }

    private boolean equalsMetricLocation(MetricLocation that) {
        if (!isSameKindOfLocation(that) || !getPackageName().equals(that.getPackageName())) {
            return false;
        }

        return !hasType() || getTypeInfo().equals(that.getTypeInfo()) && (!hasMethod() || getMethodInfo().equals(that.getMethodInfo()));
    }

    private boolean isSameKindOfLocation(MetricLocation that) {
        return hasType() == that.hasType() && hasMethod() == that.hasMethod();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("packageName=").append(packageName);
        sb.append(", type=").append(typeInfo);
        sb.append(", methodName=").append(methodInfo);

        return sb.toString();
    }
}
