package descent.ui.metrics;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import descent.ui.metrics.calculators.LinesOfCodeCalculator;
import descent.ui.metrics.calculators.NumberOfFieldsCalculator;
import descent.ui.metrics.calculators.NumberOfLevelsCalculator;
import descent.ui.metrics.calculators.NumberOfParametersCalculator;
import descent.ui.metrics.calculators.NumberOfStatementsCalculator;

public final class MetricsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    public static final String COUNT_CASE_STATEMENTS = "countCaseStatements";
    public static final String COUNT_CATCH_CLAUSES = "countCatchClauses";
    public static final String COUNT_TERNARY_OPERATORS = "countTernaryOperators";

    private Set fieldEditors;

    public MetricsPreferencePage() {
        fieldEditors = new HashSet();
    }

    public boolean performOk() {
        saveAndRebuild();
        return super.performOk();
    }

    protected void performApply() {
        saveAndRebuild();
        super.performApply();
    }

    protected void performDefaults() {
        Iterator iter = fieldEditors.iterator();
        while (iter.hasNext()) {
            ((FieldEditor) iter.next()).loadDefault();
        }

        super.performDefaults();
    }

    protected Control createContents(Composite parent) {
        TabFolder folder = new TabFolder(parent, SWT.NULL);

        createMiscellaneousTabItem(folder);

        return folder;
    }

    public void init(IWorkbench workbench) {
        setPreferenceStore(MetricsPlugin.getDefault().getPreferenceStore());
    }

    private void createMiscellaneousTabItem(TabFolder folder) {
        Composite root = createCompositeForTabItem(folder, "Miscellaneous", "Simple Metrics");
        Group group = createGroupForPreferredUpperBounds(root);

        addEnableMetricFields(group, NumberOfFieldsCalculator.METRIC_ID, "Number Of Fields");
        addEnableMetricFields(group, NumberOfLevelsCalculator.METHOD_METRIC_ID, "Number Of Levels");
        addEnableMetricFields(group, NumberOfParametersCalculator.METRIC_ID, "Number Of Parameters");
        addEnableMetricFields(group, NumberOfStatementsCalculator.METRIC_ID, "Number Of Statements");
        addEnableMetricFields(group, LinesOfCodeCalculator.METHOD_METRIC_ID, "Lines Of Code");
    }

    private Group createGroupForPreferredUpperBounds(Composite root) {
        Group group = new Group(root, SWT.NULL);
        group.setText("Preferred Upper Bounds");
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        group.setLayout(layout);
        group.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        Label label = new Label(group, SWT.LEFT);
        label.setText("Enable");

        label = new Label(group, SWT.LEFT);
        label.setText("Upper Bound");
        return group;
    }

    private Composite createCompositeForTabItem(TabFolder folder, String tabName, String tabDescription) {
        Composite root = new Composite(folder, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        root.setLayout(layout);
        root.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        new Label(root, SWT.NULL).setText(tabDescription);

        TabItem tab = new TabItem(folder, SWT.NULL);
        tab.setText(tabName);
        tab.setControl(root);
        return root;
    }

    private void addEnableMetricFields(Composite parent, String metricKey, String displayName) {
        addBooleanEditor(parent, MetricsPlugin.getMetricEnabledKey(metricKey), displayName);
        addIntegerEditor(parent, MetricsPlugin.getMaximumPreferredValueKey(metricKey));
    }

    private void addBooleanEditor(Composite parent, String preferenceKey, String displayName) {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        BooleanFieldEditor button = new BooleanFieldEditor(preferenceKey, displayName, composite);

        initializeFieldEditor(button);
    }

    private void addIntegerEditor(Composite parent, String preferenceKey) {
        Composite editorComposite = new Composite(parent, SWT.NULL);
        editorComposite.setLayout(new GridLayout());
        editorComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        IntegerFieldEditor editor = new IntegerFieldEditor(preferenceKey, "", editorComposite);

        initializeFieldEditor(editor);
    }

    private void initializeFieldEditor(FieldEditor editor) {
        fieldEditors.add(editor);

        editor.setPreferenceStore(getPreferenceStore());
        editor.load();
    }

    private void saveAndRebuild() {
        if (storePreferences()) {
            try {
                rebuild();
            } catch (Exception ex) {
                MetricsPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, MetricsPlugin.PLUGIN_ID, IStatus.OK, "Error during metrics measurements", ex));
            }
        }
    }

    private boolean storePreferences() {
        boolean changes = false;
        Iterator iter = fieldEditors.iterator();
        while (iter.hasNext()) {
            changes |= storePreference((FieldEditor) iter.next());
        }

        return changes;
    }

    private boolean storePreference(FieldEditor editor) {
        if (hasEditorValueChanged(editor)) {
            editor.store();
            return true;
        } else {
            return false;
        }
    }

    private boolean hasEditorValueChanged(FieldEditor editor) {
        if (editor instanceof BooleanFieldEditor) {
            return hasBooleanValueChanged((BooleanFieldEditor) editor);
        } else if (editor instanceof IntegerFieldEditor) {
            return hasIntegerValueChanged((IntegerFieldEditor) editor);
        } else {
            throw new IllegalArgumentException("Unknown editor type " + editor.getClass().getName() + " for preference " + editor.getPreferenceName());
        }
    }

    private boolean hasBooleanValueChanged(BooleanFieldEditor editor) {
        return getPreferenceStore().getBoolean(editor.getPreferenceName()) != editor.getBooleanValue();
    }

    private boolean hasIntegerValueChanged(IntegerFieldEditor editor) {
        return getPreferenceStore().getInt(editor.getPreferenceName()) != editor.getIntValue();
    }

    private void rebuild() throws InvocationTargetException, InterruptedException {
        new ProgressMonitorDialog(getShell()).run(true, true, new ProjectBuilder());
    }
}