package mmrnmhrm.ui.views;

import melnorme.util.ui.jface.SimpleLabelProvider;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import descent.core.domX.AbstractElement;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.ASTPrinter;
import dtool.dom.declarations.DefUnit;

public class ASTViewerLabelProvider extends SimpleLabelProvider implements IColorProvider, IFontProvider {

	
	protected final Color cNoSourceRangeColor;
	protected final Color cDefUnitColor;
	protected final Color cOldAstColor;
	
	public ASTViewerLabelProvider() {
		cNoSourceRangeColor = Display.getDefault().getSystemColor(SWT.COLOR_RED);
		cDefUnitColor = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
		cOldAstColor = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
	}
	
	public void dispose() {
	}
	
	public Image getImage(Object element) {
		return DeeElementImageProvider.getElementImage((ASTNode) element);
	}

	public String getText(Object elem) {
		return ASTPrinter.toStringElement((ASTNode) elem);
	}

	public Color getBackground(Object element) {
		ASTNode node = (ASTNode) element;
		if(node.hasNoSourceRangeInfo())
			return cNoSourceRangeColor;
		if(node instanceof AbstractElement)
			return cOldAstColor;
		return null;
	}

	public Color getForeground(Object element) {
		if(element instanceof DefUnit) {
			return cDefUnitColor;
		}
		return null;
	}

	public Font getFont(Object element) {
		return null;
	}

}