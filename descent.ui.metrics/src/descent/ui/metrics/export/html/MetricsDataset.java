package descent.ui.metrics.export.html;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.data.AbstractDataset;
import org.jfree.data.CategoryDataset;

import descent.ui.metrics.MetricsPlugin;
import descent.ui.metrics.collator.MetricsCollator;
import descent.ui.metrics.location.MetricLocation;

final class MetricsDataset extends AbstractDataset implements CategoryDataset {
    private static final class Counter {
        private int value;

        public Counter(int initialValue) {
            value = initialValue;
        }
    }

    private static final String IN_RANGE = "In Range";
    private static final String OUT_OF_RANGE = "Out of Range";
    private static final String[] ROW_KEYS = {OUT_OF_RANGE, IN_RANGE};
    private static final Integer ZERO = new Integer(0);

    private int preferredUpperBound;
    private Integer maxX;
    private String maxCategory;
    private List xValues;
    private List categoryValues;
    private Map categoryToYValueMap;

    public MetricsDataset(MetricsCollator collator, String metricId) {
        preferredUpperBound = MetricsPlugin.getDefault().getMaximumPreferredValue(metricId);
        maxX = new Integer(preferredUpperBound * 2);
        maxCategory = ">=" + maxX;
        xValues = new ArrayList();
        categoryValues = new ArrayList();
        categoryToYValueMap = new HashMap();

        initialise(collator, metricId);
    }

    private void initialise(final MetricsCollator collator, final String metricId) {
        collator.forEach(new MetricLocation.Closure() {
            public void execute(MetricLocation location) {
                add(collator, location, metricId);
            }
        });
    }

    private void add(MetricsCollator collator, MetricLocation location, String metricId) {
        if (collator.hasMetric(location, metricId)) {
            incrementCategoryForValue(collator.getMetric(location, metricId).getValue());
        }

    }

    private void incrementCategoryForValue(int value) {
        Integer constrainedX;
        String category;
        if (value < maxX.intValue()) {
            constrainedX = new Integer(value);
            category = Integer.toString(value);
        } else {
            constrainedX = maxX;
            category = maxCategory;
        }
        pad(constrainedX);
        incrementCategoryForValueInternal(constrainedX, category, 1);
    }

    private void pad(Integer x) {
        if (xValues.isEmpty()) {
            return;
        }

        int lastValue = ((Integer) xValues.get(xValues.size() - 1)).intValue();
        for (int i = lastValue + 1; i < x.intValue(); i++) {
            incrementCategoryForValueInternal(new Integer(i), Integer.toString(i), 0);
        }
    }

    private void incrementCategoryForValueInternal(Integer x, String category, int amount) {
        if (!xValues.contains(x)) {
            int insertionPoint = findInsertionPoint(x);
            xValues.add(insertionPoint, x);
            categoryValues.add(insertionPoint, category);
            categoryToYValueMap.put(category, new Counter(amount));
        } else {
            ((Counter) categoryToYValueMap.get(category)).value += amount;
        }
    }

    private int findInsertionPoint(Integer x) {
        for (int i = 0; i < xValues.size(); i++) {
            if (x.compareTo((Integer) xValues.get(i)) < 0) {
                return i;
            }
        }

        return xValues.size();
    }

    public int getRowIndex(Comparable key) {
        return MetricsDataset.ROW_KEYS[0].equals(key) ? 0 : 1;
    }

    public Comparable getRowKey(int index) {
        return MetricsDataset.ROW_KEYS[index];
    }

    public List getRowKeys() {
        return Arrays.asList(MetricsDataset.ROW_KEYS);
    }

    public int getColumnIndex(Comparable key) {
        return categoryValues.indexOf(key);
    }

    public Comparable getColumnKey(int index) {
        return (Comparable) categoryValues.get(index);
    }

    public List getColumnKeys() {
        return categoryValues;
    }

    public Number getValue(Comparable rowKey, Comparable columnKey) {
        int xValue = ((Integer) xValues.get(getColumnIndex(columnKey))).intValue();
        if (MetricsDataset.IN_RANGE.equals(rowKey) && xValue > preferredUpperBound || MetricsDataset.OUT_OF_RANGE.equals(rowKey) && xValue <= preferredUpperBound) {
            return MetricsDataset.ZERO;
        } else {
            return new Integer(((Counter) categoryToYValueMap.get(columnKey)).value);
        }
    }

    public int getRowCount() {
        return MetricsDataset.ROW_KEYS.length;
    }

    public int getColumnCount() {
        return xValues.size();
    }

    public Number getValue(int row, int column) {
        return getValue(getRowKey(row), getColumnKey(column));
    }
}