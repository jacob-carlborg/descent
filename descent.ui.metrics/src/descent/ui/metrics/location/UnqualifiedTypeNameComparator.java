/**
 * 
 */
package descent.ui.metrics.location;


public final class UnqualifiedTypeNameComparator extends LocationComparator {
    protected int compareParticular(MetricLocation l1, MetricLocation l2) {
        return l1.getTypeInfo().getName().compareTo(l2.getTypeInfo().getName());
    }
}