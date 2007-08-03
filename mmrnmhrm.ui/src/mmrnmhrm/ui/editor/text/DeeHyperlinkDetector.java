package mmrnmhrm.ui.editor.text;

import mmrnmhrm.ui.editor.DeeEditor;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.dom.ast.ASTNodeFinder;
import dtool.dom.ast.ASTNode;
import dtool.dom.references.Entity;

public class DeeHyperlinkDetector extends AbstractHyperlinkDetector {

	public static final String DEE_EDITOR_TARGET = "mmrnmhrm.ui.texteditor.deeCodeTarget";

	
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		ITextEditor textEditor= (ITextEditor)getAdapter(ITextEditor.class);
		if (region == null || canShowMultipleHyperlinks || !(textEditor instanceof DeeEditor))
			return null;
		
		ASTNode module = ((DeeEditor) textEditor).getCompilationUnit().getModule();
		ASTNode selNode = ASTNodeFinder.findElement(module, region.getOffset());
		if(!(selNode instanceof Entity))
			return null;
		
		IRegion elemRegion = new Region(selNode.getOffset(), selNode.getLength());

		return new IHyperlink[] {new DeeElementHyperlink(region.getOffset(), elemRegion, textEditor)};
	}

}
