package descent.internal.ui.preferences.formatter;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import descent.core.JavaCore;
import descent.core.formatter.DefaultCodeFormatterConstants;
import descent.ui.JavaUI;

//"int x = 1, y = 2, z = 3;" +
//"const int one = 1, two = 2, three = 3, four = 4," +
//

public class LineWrappingTabPage extends ModifyDialogTabPage
{	
	private final String PREVIEW=
	createPreviewHeader(FormatterMessages.LineWrappingTabPage_preview_header) + 
	"int x = 1, y = 2, z = 3;" +
	"const int one = 1, two = 2, three = 3, four = 4," +
	"five = 5, six = 6, seven = 7, eight = 8, nine= 9, ten = 10;";
	
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
				DefaultCodeFormatterConstants.FORMATTER_PAGE_WIDTH, 
				0,
				9999);
		createNumberPref(lineWidthGroup,
				numColumns,
				FormatterMessages.LineWrappingTabPage_continuation_indentation, 
				DefaultCodeFormatterConstants.FORMATTER_CONTINUATION_INDENTATION,
				0,
				4);
		
		final Group lineWrappingGroup = createGroup(numColumns, composite, FormatterMessages.LineWrappingTabPage_group_wrapping_style); 
		final ComboPreference[] prefs = new ComboPreference[1];
		prefs[0] = createLineWrappingCombo(lineWrappingGroup, numColumns, 
				FormatterMessages.LineWrappingTabPage_alignment_for_multiple_variable_declarations, 
				DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_MULTIPLE_VARIABLE_DECLARATIONS);
		
		final SetAllGroup setAll = createSetAllGroup(numColumns, lineWrappingGroup, FormatterMessages.LineWrappingTabPage_group_set_all_to);
		createSetAllOption(setAll, FormatterMessages.LineWrappingTabPage_style_do_not_wrap, FormatterMessages.LineWrappingTabPage_button_do_not_wrap);
		createSetAllOption(setAll, FormatterMessages.LineWrappingTabPage_style_wrap_only_when_necessary, FormatterMessages.LineWrappingTabPage_button_wrap_only_when_necessary);
		createSetAllOption(setAll, FormatterMessages.LineWrappingTabPage_style_wrap_one_fragment_per_line, FormatterMessages.LineWrappingTabPage_wrap_one_fragment_per_line);
		setAll.addPreferences(prefs);
	}
	
	protected void initializePage() {
	    fPreview.setPreviewText(PREVIEW);
	}
	
	protected JavaPreview doCreateJavaPreview(Composite parent) {
	    fPreview= new CompilationUnitPreview(fWorkingValues, parent);
	    return fPreview;
	}
	
	private ComboPreference createLineWrappingCombo(Composite composite, int numColumns, String message, String key) {
		return createComboPref(composite, numColumns, message, key, wrappingStyleValues, wrappingStyleLabels);
	}

    protected void doUpdatePreview() {
        fPreview.update();
    }

}
