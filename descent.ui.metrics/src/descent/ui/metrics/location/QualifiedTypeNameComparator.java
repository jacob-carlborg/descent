/**
 * 
 */
package descent.ui.metrics.location;


public final class QualifiedTypeNameComparator extends LocationComparator {
    protected int compareParticular(MetricLocation l1, MetricLocation l2) {
        int compare = l1.getPackageName().compareTo(l2.getPackageName());
        if (compare != 0) {
            return compare;
        }
        return l1.getTypeInfo().getName().compareTo(l2.getTypeInfo().getName());
    }
}