package mmrnmhrm.ui.preferences;

import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.text.color.IDeeColorPreferences;

import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Dee Coloring Preference Page, configures syntax highlighting.  
 */
public class DeeColoringPreferencePage extends LangColoringPreferencePage implements
		IWorkbenchPreferencePage {
	

	public DeeColoringPreferencePage() {
		super("D Syntax Coloring", DeePlugin.getInstance());
		setDescription("Configures Syntax Coloring for the D editor.");
	}
	
	protected void initColoringItemsList() {
		ColoringListItem[] deeCodeCategory = {
				new ColoringListItem("Strings", IDeeColorPreferences.DEE_STRING),
				new ColoringListItem("Literals", IDeeColorPreferences.DEE_LITERALS),
				new ColoringListItem("Basic Types", IDeeColorPreferences.DEE_BASICTYPES),
				new ColoringListItem("Keywords", IDeeColorPreferences.DEE_KEYWORD),
				new ColoringListItem("Default", IDeeColorPreferences.DEE_DEFAULT),
		};

		ColoringListItem[] deeCommentCategory = {
				new ColoringListItem("Comment Default", IDeeColorPreferences.DEE_COMMENT),
				new ColoringListItem("DDoc Comment", IDeeColorPreferences.DEE_DOCCOMMENT),
		};
		
		ColoringListCategory[] categories = {
				new ColoringListCategory("Dee", deeCodeCategory),
				new ColoringListCategory("Comments", deeCommentCategory),
		};
		
		catRoot = categories;
	}

	@Override
	protected void fireColoringPreferencesChanged() {
		DeePlugin.getDefaultDeeCodeScanner().loadDeeTokens();
	}

}
