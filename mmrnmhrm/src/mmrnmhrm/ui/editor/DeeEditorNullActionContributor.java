package mmrnmhrm.ui.editor;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorActionBarContributor;

public class DeeEditorNullActionContributor extends EditorActionBarContributor {

	public DeeEditorNullActionContributor() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void init(IActionBars bars) {
		super.init(bars);
		getActionBars().setGlobalActionHandler(
				ActionFactory.DELETE.getId(), null);
		
		

		//getActionBars().updateActionBars();
	}	

	public void setActiveEditor(IEditorPart targetEditor) {

	}
	

}
