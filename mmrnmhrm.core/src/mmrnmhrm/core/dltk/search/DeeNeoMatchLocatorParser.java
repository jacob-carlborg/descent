package mmrnmhrm.core.dltk.search;


import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.dltk.DeeModuleDeclaration;
import mmrnmhrm.core.dltk.DeeParserUtil;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.core.search.matching.MatchLocator;
import org.eclipse.dltk.core.search.matching.MatchLocatorParser;
import org.eclipse.dltk.core.search.matching.PossibleMatch;

import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefinitionAggregate;
import dtool.ast.definitions.DefinitionClass;
import dtool.ast.definitions.Module;
import dtool.ast.references.NamedReference;

public class DeeNeoMatchLocatorParser extends MatchLocatorParser {

	public DeeNeoMatchLocatorParser(MatchLocator locator) {
		super(locator);
	}

	@Override
	public ModuleDeclaration parse(PossibleMatch possibleMatch) {
		ISourceModule sourceModule = (ISourceModule) possibleMatch.getModelElement();
		ModuleDeclaration module = SourceParserUtil.getModuleDeclaration(
				sourceModule, null);
		DeeModuleDeclaration deeModuleDecl = DeeParserUtil.getFixedDeeModuleDeclaration(module,
				sourceModule);
		if(deeModuleDecl != null)
			return deeModuleDecl;
		return module;
	}

	private ASTVisitor visitor = new ASTVisitor() {

		@Override
		public boolean visitGeneral(ASTNode node) throws Exception {
			processNode(node);
			return true;
		}

		@Override
		public boolean visit(ASTNode node) throws Exception {
			processNode(node);
			return true;
		}
	};
	
	
	@Override
	public void parseBodies(ModuleDeclaration unit) {
		try {
			unit.traverse(visitor);
		} catch (Exception e) {
			e.printStackTrace();
			DeeCore.log(e);
		}
	}

	private void processNode(ASTNode node) {
		//TODO: optimize casts, since getPatternLocator() is a DeePatternLocator
		
		if(node instanceof DefUnit || node instanceof NamedReference) {
			getPatternLocator().match(node, getNodeSet());
			return;
		}

		if(node instanceof Module) {
			getPatternLocator().match(node, getNodeSet());
		} else
		if(node instanceof DefinitionClass) {
			DefinitionClass defClass = (DefinitionClass) node;
			getPatternLocator().match(defClass, getNodeSet());
		} else
		if(node instanceof DefinitionAggregate) {
			DefinitionAggregate defAggr = (DefinitionAggregate) node;
			getPatternLocator().match(defAggr, getNodeSet());
		}
	}
	
}
