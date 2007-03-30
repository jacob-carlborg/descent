package descent.ui.metrics;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import descent.ui.metrics.calculators.LinesOfCodeCalculator;
import descent.ui.metrics.calculators.NumberOfFieldsCalculator;
import descent.ui.metrics.calculators.NumberOfLevelsCalculator;
import descent.ui.metrics.calculators.NumberOfParametersCalculator;
import descent.ui.metrics.calculators.NumberOfStatementsCalculator;

public final class MetricsPlugin extends AbstractUIPlugin {
    public static final String PLUGIN_ID = "descent.ui.metrics.MetricsPlugin";
    public static final String TASK_LIST_MESSAGE_SUFFIX = ".taskListMessage";
    public static final String PRESENTATION_NAME_SUFFIX = ".presentationName";
    public static final String SHORT_PRESENTATION_NAME_SUFFIX = ".shortPresentationName";
    public static final String DESCRIPTION_FILE_SUFFIX = ".descriptionFile";

    private static MetricsPlugin plugin;

    private ResourceBundle resourceBundle;
    
    public MetricsPlugin() {
    	super();
	}
    
    @Override
    public void start(BundleContext context) throws Exception {
    	super.start(context);
    	plugin = this;
    	
    	 try {
             resourceBundle = ResourceBundle.getBundle("descent.ui.metrics.MetricsPluginResources");
         } catch (MissingResourceException x) {
             resourceBundle = null;
         }

         initialisePreferences();
    }
    
    @Override
    public void stop(BundleContext context) throws Exception {
    	super.stop(context);
    }

    public static MetricsPlugin getDefault() {
        return plugin;
    }

    public static IWorkspace getWorkspace() {
        return ResourcesPlugin.getWorkspace();
    }

    public static void log(String message) {
        getDefault().getLog().log(new Status(IStatus.OK, PLUGIN_ID, IStatus.OK, message, null));
    }

    public static void log(Throwable ex) {
        if (ex instanceof CoreException) {
            getDefault().getLog().log(((CoreException) ex).getStatus());
        } else {
            getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, "Caught exception", ex));
        }
    }

    public static String getTaskListMetricViolationMessage(String metricId) {
        return getResourceString(metricId + TASK_LIST_MESSAGE_SUFFIX);
    }

    public static String getMetricPresentationName(String metricId) {
        return getResourceString(metricId + PRESENTATION_NAME_SUFFIX);
    }

    public static String getMetricShortPresentationName(String metricId) {
        return getResourceString(metricId + SHORT_PRESENTATION_NAME_SUFFIX);
    }

    public static String getMetricDescriptionFile(String metricId) {
        String file = getSourceMetricDescriptionFile(metricId);
        return file.substring(file.lastIndexOf('/') + 1);
    }
    
    public static String getSourceMetricDescriptionFile(String metricId) {
        return getResourceString(metricId + DESCRIPTION_FILE_SUFFIX);
    }

    public static String getMaximumPreferredValueKey(String metricId) {
        return metricId;
    }

    public static String getKey(Class calculatorClass, String key) {
        return calculatorClass.getName() + "." + key;
    }

    public static String getResourceString(String key) {
        ResourceBundle bundle = MetricsPlugin.getDefault().getResourceBundle();
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    public boolean isMetricEnabled(String metricId) {
        return getPreferenceStore().getBoolean(getMetricEnabledKey(metricId));
    }

    public static String getMetricEnabledKey(String metricId) {
        return metricId + ".enabled";
    }

    public int getMaximumPreferredValue(String metricId) {
        return getPreferenceStore().getInt(MetricsPlugin.getMaximumPreferredValueKey(metricId));
    }

    protected void initialisePreferences() {
        initialiseBasicPreferencesForMetric(NumberOfLevelsCalculator.METHOD_METRIC_ID, 4);
        initialiseBasicPreferencesForMetric(NumberOfParametersCalculator.METRIC_ID, 4);
        initialiseBasicPreferencesForMetric(NumberOfStatementsCalculator.METRIC_ID, 20);
        initialiseBasicPreferencesForMetric(NumberOfFieldsCalculator.METRIC_ID, 10);
        initialiseBasicPreferencesForMetric(LinesOfCodeCalculator.METHOD_METRIC_ID, 15);

        MetricsPlugin.getDefault().savePluginPreferences();
    }

    private void initialiseBasicPreferencesForMetric(String metricId, int preferredUpperBound) {
        getPreferenceStore().setDefault(MetricsPlugin.getMetricEnabledKey(metricId), true);
        getPreferenceStore().setDefault(MetricsPlugin.getMaximumPreferredValueKey(metricId), preferredUpperBound);
    }

    private ResourceBundle getResourceBundle() {
        return resourceBundle;
    }
}
