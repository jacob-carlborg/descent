package mmrnmhrm.core.dltk;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeNature;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.AbstractSourceElementParser;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.core.ISourceModuleInfoCache.ISourceModuleInfo;

public class DeeSourceElementParser extends AbstractSourceElementParser {
 
	public DeeSourceElementParser() {
	}

	@Override
	protected String getNatureId() {
		return DeeNature.NATURE_ID;
	}
	
	@Override
	public void parseSourceModule(char[] contents, ISourceModuleInfo astCache, char[] filename) {

		ModuleDeclaration moduleDeclaration = SourceParserUtil.getModuleDeclaration(filename,
				contents, getNatureId(), getProblemReporter(), astCache);
		
		DeeModuleDeclaration deeModuleDecl = (DeeModuleDeclaration) moduleDeclaration;

		DeeSourceElementProvider provider = new DeeSourceElementProvider(getRequestor());
		try {
			provider.provide(deeModuleDecl);
		} catch (Exception e) {
			DeeCore.log(e);
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}
	}




}
