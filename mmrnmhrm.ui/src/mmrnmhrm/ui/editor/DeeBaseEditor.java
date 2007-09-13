package mmrnmhrm.ui.editor;

import java.util.Iterator;

import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ISourceReference;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.ui.editor.BracketInserter;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;

import dtool.dom.ast.ASTNeoNode;
import dtool.dom.definitions.DefUnit;

public abstract class DeeBaseEditor extends ScriptEditor {
	

	protected BracketInserter fBracketInserter = new DeeBracketInserter(this);
	
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		boolean closeBrackets = true;
		boolean closeStrings = true;
		boolean closeAngularBrackets = false;

		fBracketInserter.setCloseBracketsEnabled(closeBrackets);
		fBracketInserter.setCloseStringsEnabled(closeStrings);
		fBracketInserter.setCloseAngularBracketsEnabled(closeAngularBrackets);

		ISourceViewer sourceViewer = getSourceViewer();
		if (sourceViewer instanceof ITextViewerExtension)
			((ITextViewerExtension) sourceViewer)
					.prependVerifyKeyListener(fBracketInserter);
	}
	
	@Override
	public void dispose() {
		ISourceViewer sourceViewer = getSourceViewer();
		if (sourceViewer instanceof ITextViewerExtension)
			((ITextViewerExtension) sourceViewer)
					.removeVerifyKeyListener(fBracketInserter);
		super.dispose();
	}
	
	@Override
	protected void doSelectionChanged(SelectionChangedEvent event) {
		// XXX: DLTK copy 0.9
		ISourceReference reference = null;
		ISelection selection = event.getSelection();
		Iterator iter = ((IStructuredSelection) selection).iterator();
		while (iter.hasNext()) {
			Object obj = iter.next();
			if (obj instanceof ASTNeoNode) {
				reference = adaptNodeToReference((ASTNeoNode)obj);
				break;
			}
		}
		if (!isActivePart() && DLTKUIPlugin.getActivePage() != null)
			DLTKUIPlugin.getActivePage().bringToTop(this);
		setSelection(reference, !isActivePart());
	}

	private ISourceReference adaptNodeToReference(final ASTNeoNode node) {
		return new org.eclipse.dltk.core.ISourceReference(){
			//@Override
			final public ISourceRange getSourceRange() {
				return new ISourceRange() {
					//@Override
					final public int getOffset() {
						if(node instanceof DefUnit)
							return ((DefUnit)node).defname.getOffset();
						return node.getOffset();
					}
				
					//@Override
					final public int getLength() {
						if(node instanceof DefUnit)
							return ((DefUnit)node).defname.getLength();
						return node.getLength();
					}
				
				};
			}
			//@Override
			final public boolean exists() {
				return true;
			}
			//@Override
			final public String getSource() throws ModelException {
				return null;
			}
		};
	}


}