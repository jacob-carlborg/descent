package descent.ui.metrics.location;

import java.util.Comparator;


public abstract class LocationComparator implements Comparator {
    public final int compare(Object obj1, Object obj2) {
        return compareMetricLocation((MetricLocation) obj1, (MetricLocation) obj2);
    }

    private int compareMetricLocation(MetricLocation l1, MetricLocation l2) {
        int comparison = compareParticular(l1, l2);
        if (comparison == 0) {
            comparison = compareDefault(l1, l2);
        }

        return comparison;
    }

    private int compareDefault(MetricLocation l1, MetricLocation l2) {
        int comparison = comparePackageNames(l1, l2);
        if (comparison != 0) {
            return comparison;
        }

        comparison = compareTypeNames(l1, l2);
        if (comparison != 0) {
            return comparison;
        }

        return compareMethodInfo(l1, l2);
    }

    private int compareMethodInfo(MetricLocation l1, MetricLocation l2) {
        if (l1.hasMethod()) {
            return l2.hasMethod() ? l1.getMethodInfo().getLineNumber() - l2.getMethodInfo().getLineNumber() : 1;
        } else {
            return l2.hasMethod() ? -1 : 0;
        }
    }

    private int compareTypeNames(MetricLocation l1, MetricLocation l2) {
        return l1.getTypeInfo().getName().compareTo(l2.getTypeInfo().getName());
    }

    private int comparePackageNames(MetricLocation l1, MetricLocation l2) {
        return l1.getPackageName().compareTo(l2.getPackageName());
    }

    protected abstract int compareParticular(MetricLocation l1, MetricLocation l2);
}