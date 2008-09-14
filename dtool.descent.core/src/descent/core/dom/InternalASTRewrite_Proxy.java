package descent.core.dom;

import static melnorme.miscutil.Assert.assertFail;

import java.util.Map;

import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;


public abstract class InternalASTRewrite_Proxy extends NodeEventHandler {

	public static InternalASTRewrite_Proxy new_InternalASTRewrite(
			CompilationUnit root) {
		assertFail();
		return null;
	}

	public TextEdit rewriteAST(IDocument document, Map options) {
		return null;
	}

}
