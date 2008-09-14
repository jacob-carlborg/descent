package mmrnmhrm.ui.editor.text;

import mmrnmhrm.lang.ui.EditorUtil;
import mmrnmhrm.ui.editor.DeeEditor;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeFinder;
import dtool.ast.references.Reference;

public class DeeHyperlinkDetector extends AbstractHyperlinkDetector {

	public static final String DEE_EDITOR_TARGET = "mmrnmhrm.ui.texteditor.deeCodeTarget";

	
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		ITextEditor textEditor= (ITextEditor)getAdapter(ITextEditor.class);
		if (region == null || !(textEditor instanceof DeeEditor))
			return null;
		
		ASTNeoNode module = EditorUtil.getNeoModuleFromEditor(textEditor);
		ASTNeoNode selNode = ASTNodeFinder.findNeoElement(module, region.getOffset(), false);
		if(!(selNode instanceof Reference))
			return null;
		
		IRegion elemRegion = new Region(selNode.getOffset(), selNode.getLength());

		return new IHyperlink[] {new DeeElementHyperlink(region.getOffset(), elemRegion, textEditor)};
	}

}
