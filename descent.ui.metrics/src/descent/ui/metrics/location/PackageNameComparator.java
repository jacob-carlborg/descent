/**
 * 
 */
package descent.ui.metrics.location;


public final class PackageNameComparator extends LocationComparator {
    protected int compareParticular(MetricLocation l1, MetricLocation l2) {
        return l1.getPackageName().compareTo(l2.getPackageName());
    }
}