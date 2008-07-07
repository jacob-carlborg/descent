package mmrnmhrm.ui.editor.doc;

import static melnorme.miscutil.Assert.assertFailTODO;

import java.io.Reader;

import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.ui.documentation.IScriptDocumentationProvider;

// TODO: DLTK: we are currently own DocProvider, not DLTK's
public class DeeDocumentationProvider implements IScriptDocumentationProvider {

	public Reader getInfo(String content) {
		return null;
	}

	public Reader getInfo(IMember element, boolean lookIntoParents, boolean lookIntoExternal) {
		assertFailTODO();
		return null;
	}

}
