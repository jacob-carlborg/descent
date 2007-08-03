package mmrnmhrm.ui.editor.text;

import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.editor.DeeEditor;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.dom.ast.ASTNode;
import dtool.dom.ast.ASTNodeFinder;
import dtool.dom.definitions.DefSymbol;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Symbol;
import dtool.dom.references.Entity;

/** 
 *  TODO Learn more about DefaultTextHover
 */
public class DeeDocTextHover extends AbstractTextHover implements ITextHoverExtension {


	
	public DeeDocTextHover(ISourceViewer sourceViewer, ITextEditor textEditor) {
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
		
		if(!(node instanceof DefSymbol || node instanceof Entity))
			return null;
		
		return new Region(node.getOffset(), node.getLength());
	}



	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		ASTNode node = getNodeAtOffset(hoverRegion.getOffset());
		if(node == null)
			return null;
		
		String info = null;
		
		if(node instanceof DefSymbol) {
			DefUnit defUnit = ((DefSymbol) node).getParent();
			info= HoverUtil.getDefUnitHoverInfoWithDeeDoc(defUnit);
		} else if (node instanceof Entity) {
			DefUnit defUnit = ((Entity) node).findTargetDefUnit();
			if(defUnit != null)
				info= HoverUtil.getDefUnitHoverInfoWithDeeDoc(defUnit);
			else
				info= "404 DefUnit not found";
		}
		if(info != null)
			return HoverUtil.getCompleteHoverInfo(info, getCSSStyles());

		return null;
	}

}
