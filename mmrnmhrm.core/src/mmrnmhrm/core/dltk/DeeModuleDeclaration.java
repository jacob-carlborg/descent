package mmrnmhrm.core.dltk;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;

import static melnorme.miscutil.Assert.assertFail;

import dtool.ast.definitions.Module;

public class DeeModuleDeclaration extends ModuleDeclaration {

	public static interface EModelStatus {
		int OK = 0;
		int PARSER_INTERNAL_ERROR = 1;
		int PARSER_SYNTAX_ERRORS = 2;
	}
	
		public int status;
	
	public Module neoModule;
	public descent.internal.compiler.parser.Module dmdModule;
	
	
	public DeeModuleDeclaration(descent.internal.compiler.parser.Module dmdModule) {
		super(dmdModule.getLength());
		this.dmdModule = dmdModule;
	}
	
	public String toStringParseStatus() {
		switch(getParseStatus()) {
		case EModelStatus.PARSER_INTERNAL_ERROR: return "Internal Error";
		case EModelStatus.PARSER_SYNTAX_ERRORS: return "Syntax Errors";
		case EModelStatus.OK: return "OK";
		default: assertFail(); return null;
		}
	}

	public int getParseStatus() {
		return status;
	}

	@SuppressWarnings("unchecked")
	public void setNeoModule(Module neoModule) {
		this.neoModule = neoModule;
		getStatements().add(neoModule);
	}
}
