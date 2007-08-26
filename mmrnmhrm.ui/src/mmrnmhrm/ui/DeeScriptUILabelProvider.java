package mmrnmhrm.ui;

import mmrnmhrm.ui.views.DeeElementImageProvider;

import org.eclipse.dltk.ui.ScriptElementImageProvider;
import org.eclipse.dltk.ui.viewsupport.ScriptUILabelProvider;
import org.eclipse.swt.graphics.Image;

import dtool.dom.ast.ASTNeoNode;

public class DeeScriptUILabelProvider extends ScriptUILabelProvider {
	public static class DeeScriptElementImageProvider extends ScriptElementImageProvider {
		
		@Override
		public Image getImageLabel(Object element, int flags) {
			if(element instanceof ASTNeoNode)
				return DeeElementImageProvider.getNodeImage((ASTNeoNode)element);
			else
				return super.getImageLabel(element, flags);
		}
	}
	
	public DeeScriptUILabelProvider() {
		super();
		
		fImageLabelProvider = new DeeScriptElementImageProvider();
	}
	
	@Override
	public Image getImage(Object element) {
		return super.getImage(element);
	}
	
}