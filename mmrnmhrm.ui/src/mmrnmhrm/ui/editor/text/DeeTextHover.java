package mmrnmhrm.ui.editor.text;

import melnorme.lang.ui.EditorUtil;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNodeFinder;
import dtool.dom.definitions.DefSymbol;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;
import dtool.dom.references.Reference;

/** 
 *  TODO Learn more about DefaultTextHover
 */
public class DeeTextHover extends AbstractTextHover implements ITextHoverExtension {

	public static class NodeRegion implements IRegion {

		public ASTNeoNode node;

		public NodeRegion(ASTNeoNode node) {
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

	private ASTNeoNode getNodeAtOffset(int offset) {
		Module module = EditorUtil.getNeoModuleFromEditor(fEditor);
		if(module == null)
			return null;

		ASTNeoNode node;
		node = ASTNodeFinder.findNeoElement(module, offset, false);
		return node;
	}
	
	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		ASTNeoNode node = getNodeAtOffset(offset);
		if(node == null)
			return null;
		
		if(!(node instanceof DefSymbol || node instanceof Reference))
			return null;
		
		return new NodeRegion(node);
	}


	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		if(!(hoverRegion instanceof NodeRegion))
			return null;
		ASTNeoNode node = ((NodeRegion) hoverRegion).node;
		
		String info = null;
		
		if(node instanceof DefSymbol) {
			DefUnit defUnit = ((DefSymbol) node).getDefUnit();
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
