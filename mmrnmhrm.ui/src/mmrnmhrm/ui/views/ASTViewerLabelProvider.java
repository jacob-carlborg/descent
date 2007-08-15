package mmrnmhrm.ui.views;

import melnorme.util.ui.jface.SimpleLabelProvider;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import descent.core.domX.ASTNode;
import descent.internal.compiler.parser.ASTDmdNode;
import dtool.dom.ast.ASTPrinter;
import dtool.dom.definitions.DefUnit;
import dtool.dom.references.Reference;

public class ASTViewerLabelProvider extends SimpleLabelProvider implements IColorProvider, IFontProvider {

	
	protected final Color cNoSourceRangeColor;
	protected final Color cDefUnitColor;
	protected final Color cEntityColor;
	protected final Color cOldAstColor;
	private ASTViewer viewer;
	
	public ASTViewerLabelProvider(ASTViewer viewer) {
		this.viewer = viewer;
		cNoSourceRangeColor = Display.getDefault().getSystemColor(SWT.COLOR_RED);
		cOldAstColor = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);

		cDefUnitColor = Display.getDefault().getSystemColor(SWT.COLOR_DARK_MAGENTA);
		cEntityColor = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
	}
	
	public void dispose() {
	}
	
	public Image getImage(Object element) {
		return DeeElementImageProvider.getElementImage((ASTNode) element);
	}

	public String getText(Object elem) {
		return ASTPrinter.toStringNodeExtra((ASTNode) elem);
	}

	public Color getBackground(Object element) {
		ASTNode node = (ASTNode) element;
		if(node.hasNoSourceRangeInfo())
			return cNoSourceRangeColor;
		if(node instanceof ASTDmdNode)
			return cOldAstColor;
		
		//int offset = EditorUtil.getSelection(viewer.fEditor).getOffset();
		//ASTNode selNode = ASTNodeFinder.findElement(viewer.fCUnit.getModule(), offset);
		
		if(viewer.selNode == node)
			return Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
		
		return null;
	}

	public Color getForeground(Object element) {
		if(element instanceof DefUnit) {
			return cDefUnitColor;
		}
		if(element instanceof Reference) {
			return cEntityColor;
		}
		return null;
	}

	public Font getFont(Object element) {
		return null;
	}

}
