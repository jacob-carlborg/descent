package mmrnmhrm.ui.preferences;

import melnorme.util.ui.fields.ItemSelectionListField.SelectionListCategory;
import melnorme.util.ui.fields.ItemSelectionListField.SelectionListItem;
import mmrnmhrm.ui.text.color.IDeeColorPreferences;

/**
 * Dee Coloring Preference Page, configures syntax highlighting.  
 */
public class DeeColoringPreferencePage extends LangColoringPreferencePage {


	public DeeColoringPreferencePage() {
		super("D Syntax Coloring");
		setDescription("Configures Syntax Coloring for the D editor.");
	}
	
	protected void initColoringItemsList() {
		SelectionListItem[] deeCodeCategory = {
				createSelectionItem("Strings", IDeeColorPreferences.DEE_STRING),
				createSelectionItem("Literals", IDeeColorPreferences.DEE_LITERALS),
				createSelectionItem("Basic Types", IDeeColorPreferences.DEE_BASICTYPES),
				createSelectionItem("Keywords", IDeeColorPreferences.DEE_KEYWORD),
				createSelectionItem("Default", IDeeColorPreferences.DEE_DEFAULT),
		};

		ColoringListItem[] deeCommentCategory = {
				createSelectionItem("Comment Default", IDeeColorPreferences.DEE_COMMENT),
				createSelectionItem("DDoc Comment", IDeeColorPreferences.DEE_DOCCOMMENT),
		};
		
		SelectionListCategory[] categories = {
				new SelectionListCategory("Dee", deeCodeCategory),
				new SelectionListCategory("Comments", deeCommentCategory),
		};
		
		catRoot = categories;
	}

}
