package mmrnmhrm.ui.editor.text;

import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.editor.DeeEditor;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.dom.ast.ASTNode;
import dtool.dom.ast.ASTNodeFinder;
import dtool.dom.definitions.DefSymbol;
import dtool.dom.definitions.DefUnit;
import dtool.dom.references.Reference;

/** 
 *  TODO Learn more about DefaultTextHover
 */
public class DeeTextHover extends AbstractTextHover implements ITextHoverExtension {

	public static class NodeRegion implements IRegion {

		public ASTNode node;

		public NodeRegion(ASTNode node) {
			this.node = node;
		}

		public int getLength() {
			return node.getLength();
		}

		public int getOffset() {
			return node.getOffset();
		}
	}
	
	public DeeTextHover(ISourceViewer sourceViewer, ITextEditor textEditor) {
		super(sourceViewer);
		Assert.isNotNull(textEditor);
		this.fEditor = textEditor;
	}

	private ASTNode getNodeAtOffset(int offset) {
		CompilationUnit cunit;

		if(fEditor instanceof DeeEditor) {
			cunit = ((DeeEditor) fEditor).getCompilationUnit();
		} else {
		
			IEditorInput input = fEditor.getEditorInput();
			if (input instanceof IFileEditorInput) {
				IFileEditorInput feInput = (IFileEditorInput) input;
				cunit = DeePlugin.getCompilationUnitOperation(feInput);
				if (cunit == null)
					return null;
			} else {
				return null;
			}
		}
		
		ASTNode node;
		node = ASTNodeFinder.findElement(cunit.getModule(), offset, false);
		return node;
	}
	
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		ASTNode node = getNodeAtOffset(offset);
		if(node == null)
			return null;
		
		if(!(node instanceof DefSymbol || node instanceof Reference))
			return null;
		
		return new NodeRegion(node);
	}



	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		if(!(hoverRegion instanceof NodeRegion))
			return null;
		ASTNode node = ((NodeRegion) hoverRegion).node;
		
		String info = null;
		
		if(node instanceof DefSymbol) {
			DefUnit defUnit = ((DefSymbol) node).getParent();
			info= HoverUtil.getDefUnitHoverInfoWithDeeDoc(defUnit);
		} else if (node instanceof Reference) {
			DefUnit defUnit;
			try {
				defUnit = ((Reference) node).findTargetDefUnit();
				if(defUnit != null)
					info= HoverUtil.getDefUnitHoverInfoWithDeeDoc(defUnit);
				else
					info= "404 DefUnit not found";
			} catch (UnsupportedOperationException uoe) {
				info= "UnsupportedOperationException:\n" + uoe;
			}
		}
		if(info != null)
			return HoverUtil.getCompleteHoverInfo(info, getCSSStyles());

		return null;
	}

}
