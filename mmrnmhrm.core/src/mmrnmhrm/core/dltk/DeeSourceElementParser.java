package mmrnmhrm.core.dltk;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeNature;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.SourceElementRequestVisitor;
import org.eclipse.dltk.core.ISourceModule;
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
	public void parseSourceModule(
			org.eclipse.dltk.compiler.env.ISourceModule module,
			ISourceModuleInfo astCache) {
		final ModuleDeclaration moduleDeclaration = parse(module, astCache);
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
