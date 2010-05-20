package mmrnmhrm.ui.actions;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;


public class DeeInvokeContentAssistHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		
		OperationsManager.executeSimple(new ISimpleRunnable() {
			@Override
			public void run() throws CoreException {
				IEditorPart editor = HandlerUtil.getActiveEditor(event);
				
				ITextOperationTarget target= 
					(ITextOperationTarget) editor.getAdapter(ITextOperationTarget.class);
				if (target != null && target.canDoOperation(ISourceViewer.CONTENTASSIST_PROPOSALS))
					target.doOperation(ISourceViewer.CONTENTASSIST_PROPOSALS);
			}
			
		}, "Invoke Content Assist");
		
		return null;
	}

}
