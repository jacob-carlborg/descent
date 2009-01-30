package descent.ui.metrics.collator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import descent.ui.metrics.export.html.CollatedMetricIdComparator;
import descent.ui.metrics.location.MetricLocation;

public final class MetricsCollator {
    private Map metricLocationToMetricsMap;
    private Map metricIdsToAggregationSteps;

    public MetricsCollator() {
        metricLocationToMetricsMap = new HashMap();
        metricIdsToAggregationSteps = new HashMap();
    }

    public void addMetric(MetricLocation location, Metric metric) {
        Map metricsMap = findOrCreateMetricsMap(location);
        metricsMap.put(metric.getId(), metric);

        if (!metricIdsToAggregationSteps.containsKey(metric.getId())) {
            metricIdsToAggregationSteps.put(metric.getId(), new Integer(metric.getAggregationSteps()));
        }
    }

    public void addMetric(Metric metric) {
        addMetric(metric.getLocation(), metric);
    }

    public String[] getMetricIds() {
        return (String[]) metricIdsToAggregationSteps.keySet().toArray(new String[metricIdsToAggregationSteps.size()]);
    }

    public int getAggregationSteps(String metricId) {
        return ((Integer) metricIdsToAggregationSteps.get(metricId)).intValue();
    }

    public Metric getMetric(MetricLocation location, String metricId) {
        return (Metric) getMetricsMap(location).get(metricId);
    }

    public boolean hasMetric(MetricLocation location, String metricId) {
        Map map = getMetricsMap(location);
        if (map == null) {
            return false;
        }

        return map.containsKey(metricId);
    }

    public void forEach(Comparator sortOrder, MetricLocation.Closure closure) {
        TreeMap map = new TreeMap(sortOrder);
        map.putAll(metricLocationToMetricsMap);
        Iterator iter = map.keySet().iterator();
        while (iter.hasNext()) {
            closure.execute((MetricLocation) iter.next());
        }
    }

    public void forEach(MetricLocation.Closure closure) {
        Iterator iter = metricLocationToMetricsMap.keySet().iterator();
        while (iter.hasNext()) {
            closure.execute((MetricLocation) iter.next());
        }
    }

    public int size() {
        return metricLocationToMetricsMap.size();
    }

    private Map getMetricsMap(MetricLocation location) {
        return (Map) metricLocationToMetricsMap.get(location);
    }

    private Map findOrCreateMetricsMap(MetricLocation location) {
        if (metricLocationToMetricsMap.containsKey(location)) {
            return getMetricsMap(location);
        }

        HashMap map = new HashMap();
        metricLocationToMetricsMap.put(location, map);
        return map;
    }
    
    public int getNumberOfMetrics() {
        return metricIdsToAggregationSteps.size();
    }

    public String[] getSortedMetricIds() {
        String[] ids = getMetricIds();
        Arrays.sort(ids, new CollatedMetricIdComparator(this));
        return ids;
    }
}
