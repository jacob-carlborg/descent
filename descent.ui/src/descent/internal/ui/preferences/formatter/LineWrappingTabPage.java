package descent.internal.ui.preferences.formatter;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import descent.core.JavaCore;
import descent.core.formatter.DefaultCodeFormatterConstants;
import descent.internal.ui.JavaPlugin;
import descent.ui.JavaUI;

//"int x = 1, y = 2, z = 3;" +
//"const int one = 1, two = 2, three = 3, four = 4," +
//

public class LineWrappingTabPage extends ModifyDialogTabPage
{
	// For the "preview width"
	private static final String PREF_PREVIEW_LINE_WIDTH =
		"descent.ui.preferences.formatter.LineWrappingTabPage.preview_width";
	private static final int DEFAULT_PREVIEW_WINDOW_LINE_WIDTH = 40;
	private final IDialogSettings fDialogSettings = 
		JavaPlugin.getDefault().getDialogSettings();
	private final Map<String, String> fPreviewPreferences =
		new HashMap<String, String>();
	{
		String previewLineWidth= fDialogSettings.get(PREF_PREVIEW_LINE_WIDTH);
		fPreviewPreferences.put(
				DefaultCodeFormatterConstants.FORMATTER_PAGE_WIDTH,
				previewLineWidth != null ?
						previewLineWidth :
						Integer.toString(DEFAULT_PREVIEW_WINDOW_LINE_WIDTH));
	}
	
	private final String PREVIEW=
	createPreviewHeader(FormatterMessages.LineWrappingTabPage_preview_header) + 
	"int x = 1, y = 2, z = 3;" +
	"const int one = 1, two = 2, three = 3, four = 4," +
	"five = 5, six = 6, seven = 7, eight = 8, nine= 9, ten = 10;\n\n" +
	"void func(int a, int b);" +
	"void func(int a, int b, int c, int d, int e, int f, int g, int h," +
	"int i, int j, int k);\n\n" +
	"template Templ(T, U){}" +
	"template Templ(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9," +
	"arg10, arg11, arg12){}\n\n" +
	"void func(){func(1, 2); func(variableOne, variableTwo, variableThree," +
	"variableFour, variableFive);\n\n" +
	"alias Templ!(int, long) t1;" +
	"alias Templ!(ubyte, short[char[]], creal, int[][], Foo, Bar, 750) t2;}\n\n" +
	"int[] arr1 = [1, 2];" +
	"real[] dataPoints = [3.17, 2.71, 8.44, 7.3815, 9.653, 0.12, -100.6," +
	"212.5, 716.91, 88.17];\n\n" +
	"class A : B, C {}" +
	"class FileHandler : AbstractFileHandler, IFileReader, IFileWriter," +
	"IStreamingFileReader, IStreamingFileWriter, IOpenable, ICloseable," +
	"IObservable, ISerializable, IHaveTooManyInterfaces {}\n\n" +
	"import j, k;" +
	"import tango.io.Console, tango.io.File, tango.io.FileConduit," +
	"tango.io.FileSystem, tango.io.FileScan, tango.io.GrowBuffer;\n\n" +
	"static Foo bar = {1, 2};" +
	"static Foo baz = {field1:1, field2:2, field4:18, field17:6, field3:9, " +
	"field8:25, field6:14, field5:5};";
	
	private CompilationUnitPreview fPreview;
	
	private final String [] wrappingStyleValues = {
	    String.valueOf(DefaultCodeFormatterConstants.DO_NOT_WRAP),
	    String.valueOf(DefaultCodeFormatterConstants.WRAP_ONLY_WHEN_NECESSARY),
	    String.valueOf(DefaultCodeFormatterConstants.WRAP_ONE_FRAGMENT_PER_LINE)
	};
	
	private final String [] wrappingStyleLabels = {
	    FormatterMessages.LineWrappingTabPage_style_do_not_wrap, 
	    FormatterMessages.LineWrappingTabPage_style_wrap_only_when_necessary, 
	    FormatterMessages.LineWrappingTabPage_style_wrap_one_fragment_per_line
	};
	
	/**
	 * Create a new BracesTabPage.
	 * @param modifyDialog
	 * @param workingValues
	 */
	public LineWrappingTabPage(ModifyDialog modifyDialog, Map workingValues) {
		super(modifyDialog, workingValues);
	}
	
	protected void doCreatePreferences(Composite composite, int numColumns)
	{
		final Group lineWidthGroup = createGroup(numColumns, composite, 
				FormatterMessages.LineWrappingTabPage_group_line_width_and_indentation);
		createNumberPref(lineWidthGroup, numColumns, 
				FormatterMessages.LineWrappingTabPage_page_width, 
				DefaultCodeFormatterConstants.FORMATTER_PAGE_WIDTH, 0, 9999);
		createNumberPref(lineWidthGroup,
				numColumns,
				FormatterMessages.LineWrappingTabPage_continuation_indentation, 
				DefaultCodeFormatterConstants.FORMATTER_CONTINUATION_INDENTATION, 0, 4);
		
		final Group lineWrappingGroup = createGroup(numColumns, composite, FormatterMessages.LineWrappingTabPage_group_wrapping_style); 
		final ComboPreference[] prefs = new ComboPreference[9];
		prefs[0] = createLineWrappingCombo(lineWrappingGroup, numColumns, 
				FormatterMessages.LineWrappingTabPage_alignment_for_multiple_variable_declarations, 
				DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_MULTIPLE_VARIABLE_DECLARATIONS);
		prefs[1] = createLineWrappingCombo(lineWrappingGroup, numColumns, 
				FormatterMessages.LineWrappingTabPage_alignment_for_function_declaration_parameters, 
				DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_FUNCTION_DECLARATION_PARAMETERS);
		prefs[2] = createLineWrappingCombo(lineWrappingGroup, numColumns, 
				FormatterMessages.LineWrappingTabPage_alignment_for_function_invocation_arguments, 
				DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_FUNCTION_INVOCATION_ARGUMENTS);
		prefs[3] = createLineWrappingCombo(lineWrappingGroup, numColumns, 
				FormatterMessages.LineWrappingTabPage_alignment_for_template_declaration_parameters, 
				DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_TEMPLATE_DECLARATION_PARAMETERS);
		prefs[4] = createLineWrappingCombo(lineWrappingGroup, numColumns, 
				FormatterMessages.LineWrappingTabPage_alignment_for_template_invocation_arguments, 
				DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_TEMPLATE_INVOCATION_ARGUMENTS);
		prefs[5] = createLineWrappingCombo(lineWrappingGroup, numColumns, 
				FormatterMessages.LineWrappingTabPage_alignment_for_array_literals, 
				DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_ARRAY_LITERALS);
		prefs[6] = createLineWrappingCombo(lineWrappingGroup, numColumns, 
				FormatterMessages.LineWrappingTabPage_alignment_for_base_class_lists, 
				DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_BASE_CLASS_LISTS);
		prefs[7] = createLineWrappingCombo(lineWrappingGroup, numColumns, 
				FormatterMessages.LineWrappingTabPage_alignment_for_selective_imports, 
				DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_SELECTIVE_IMPORTS);
		prefs[8] = createLineWrappingCombo(lineWrappingGroup, numColumns, 
				FormatterMessages.LineWrappingTabPage_alignment_for_struct_initializer, 
				DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_STRUCT_INITIALIZER);
		
		final SetAllGroup setAll = createSetAllGroup(numColumns, lineWrappingGroup, FormatterMessages.LineWrappingTabPage_group_set_all_to);
		createSetAllOption(setAll, FormatterMessages.LineWrappingTabPage_style_do_not_wrap, FormatterMessages.LineWrappingTabPage_button_do_not_wrap);
		createSetAllOption(setAll, FormatterMessages.LineWrappingTabPage_style_wrap_only_when_necessary, FormatterMessages.LineWrappingTabPage_button_wrap_only_when_necessary);
		createSetAllOption(setAll, FormatterMessages.LineWrappingTabPage_style_wrap_one_fragment_per_line, FormatterMessages.LineWrappingTabPage_wrap_one_fragment_per_line);
		setAll.addPreferences(prefs);
	}
	
	protected void initializePage() {
	    setPreviewText(PREVIEW);
	}
	
	protected JavaPreview doCreateJavaPreview(Composite parent) {
	    fPreview= new CompilationUnitPreview(fWorkingValues, parent);
	    return fPreview;
	}
	
	private ComboPreference createLineWrappingCombo(Composite composite, int numColumns, String message, String key) {
		return createComboPref(composite, numColumns, message, key, wrappingStyleValues, wrappingStyleLabels);
	}
	
protected Composite doCreatePreviewPane(Composite composite, int numColumns) {
		
		super.doCreatePreviewPane(composite, numColumns);
		
		final NumberPreference previewLineWidth= new NumberPreference(
				composite,
				numColumns / 2,
				fPreviewPreferences,
				DefaultCodeFormatterConstants.FORMATTER_PAGE_WIDTH,
				0, 9999,
				FormatterMessages.LineWrappingTabPage_line_width_for_preview_label_text); 
		fDefaultFocusManager.add(previewLineWidth);
		previewLineWidth.addObserver(fUpdater);
		previewLineWidth.addObserver(new Observer() {
			public void update(Observable o, Object arg) {
				fDialogSettings.put(PREF_PREVIEW_LINE_WIDTH, 
						fPreviewPreferences.get(DefaultCodeFormatterConstants.FORMATTER_PAGE_WIDTH));
			}
		});
		
		return composite;
	}
	
    protected void doUpdatePreview() {
    	final Object normalSetting = fWorkingValues.get(
    			DefaultCodeFormatterConstants.FORMATTER_PAGE_WIDTH);
		fWorkingValues.put(
				DefaultCodeFormatterConstants.FORMATTER_PAGE_WIDTH,
				fPreviewPreferences.get(DefaultCodeFormatterConstants.FORMATTER_PAGE_WIDTH));
		fPreview.update();
		fWorkingValues.put(
				DefaultCodeFormatterConstants.FORMATTER_PAGE_WIDTH,
				normalSetting);
    }
    
    protected void setPreviewText(String text) {
    	final Object normalSetting = fWorkingValues.get(
    			DefaultCodeFormatterConstants.FORMATTER_PAGE_WIDTH);
		fWorkingValues.put(
				DefaultCodeFormatterConstants.FORMATTER_PAGE_WIDTH,
				fPreviewPreferences.get(DefaultCodeFormatterConstants.FORMATTER_PAGE_WIDTH));
		fPreview.setPreviewText(text);
		fWorkingValues.put(
				DefaultCodeFormatterConstants.FORMATTER_PAGE_WIDTH,
				normalSetting);
	}

}
