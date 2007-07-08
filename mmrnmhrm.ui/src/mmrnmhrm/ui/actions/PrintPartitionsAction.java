package mmrnmhrm.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;

import mmrnmhrm.ui.DeePluginImages;
import mmrnmhrm.ui.editor.DeeEditor;
import mmrnmhrm.ui.text.DebugPartitioner;

public class PrintPartitionsAction extends AbstractDeeEditorAction {
	
	public PrintPartitionsAction() {
		super("Print Partitions");
	}
	
	public PrintPartitionsAction(DeeEditor deeEditor) {
		this();
		this.deeEditor = deeEditor;
		DeePluginImages.setupActionImages(this, "declipse.gif");
	}
	
	public void run() {
		PrintPartitionsAction.execute(deeEditor);
	}
	
	public void run(IAction action) {
		PrintPartitionsAction.execute(deeEditor);
	}

	public static void execute(DeeEditor deeEditor) {
		MessageDialog.openInformation(deeEditor.getSite().getShell(),
				"Partitions",
				DebugPartitioner.toStringPartitions(deeEditor.getDocument()));
	}

}
