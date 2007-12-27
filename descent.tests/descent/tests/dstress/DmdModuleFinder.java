package descent.tests.dstress;

import java.io.File;
import java.io.FileReader;

import descent.core.compiler.CharOperation;
import descent.internal.compiler.env.IModuleFinder;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.Global;
import descent.internal.compiler.parser.IModule;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.SemanticContext;

/*
 * Finds modules the DMD way.
 */
public class DmdModuleFinder implements IModuleFinder {
	
	private final Global global;

	public DmdModuleFinder(Global global) {
		this.global = global;
	}

	public IModule findModule(char[][] compoundName, SemanticContext context) {
		Module m;
		String filename = new String(CharOperation.concatWith(compoundName, '\\'));
		IdentifierExp ident = new IdentifierExp(compoundName[compoundName.length - 1]);

		m = new Module(filename, ident);
		m.loc = new Loc(filename.toCharArray(), 0);
		m.moduleName = filename;

		/* Search along global.path for .di file, then .d file.
		 */
		File result = null;
		String resultRelative = null;

		File fdi = new File(filename + ".di");
		File fd = new File(filename + ".d");

		if (fdi.exists()) {
			result = fdi;
		} else if (fd.exists()) {
			result = fd;
		} else if (null == global.path) {

		} else {
			for (int i = 0; i < ASTDmdNode.size(global.path); i++) {
				String p = global.path.get(i);
				File n = new File(p, fdi.toString());
				if (n.exists()) {
					result = n;
					resultRelative = fdi.toString();
					break;
				}
				n = new File(p, fd.toString());
				if (n.exists()) {
					result = n;
					resultRelative = fd.toString();
					break;
				}
			}
		}

		if (result != null) {
			m.srcfile = result;
		}

		char[] contents = getContents(result);
		if (contents == null) {
			return null;
		}

		Parser parser = new Parser(context.Module_rootModule.apiLevel, contents, resultRelative.toCharArray());
		parser.parseModuleObj(m);

		return m;
	}
	
	private static char[] getContents(File file) {
		try {
			char[] contents = new char[(int) file.length()];
			FileReader r = new FileReader(file);
			r.read(contents);
			r.close();
			return contents;
		} catch (Exception e) {
			return null;
		}
	}

}
