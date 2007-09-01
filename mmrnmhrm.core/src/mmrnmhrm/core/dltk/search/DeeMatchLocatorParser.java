package mmrnmhrm.core.dltk.search;


import mmrnmhrm.core.dltk.ParsingUtil;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.search.matching.MatchLocator;
import org.eclipse.dltk.core.search.matching.MatchLocatorParser;
import org.eclipse.dltk.core.search.matching.PossibleMatch;

import dtool.dom.definitions.DefinitionAggregate;
import dtool.dom.definitions.DefinitionClass;
import dtool.dom.definitions.Module;

public class DeeMatchLocatorParser extends MatchLocatorParser {

	public DeeMatchLocatorParser(MatchLocator locator) {
		super(locator);
	}

	public ModuleDeclaration parse(PossibleMatch possibleMatch) {
		// ModuleDeclaration module =
		// parser.parse(possibleMatch.getSourceContents());
		ISourceModule sourceModule = 
		(org.eclipse.dltk.core.ISourceModule) possibleMatch.getModelElement();
		
		return ParsingUtil.parseModule(sourceModule);
	}

	private ASTVisitor visitor = new ASTVisitor() {

		public boolean visitGeneral(ASTNode node) throws Exception {
			processNode(node);
			return true;
			// return super.visitGeneral(node);
		}

		public boolean visit(ASTNode node) throws Exception {
			processNode(node);
			return true;
			// return super.visitGeneral(node);
		}
	};
	
	@Override
	public void parseBodies(ModuleDeclaration unit) {
		try {
			unit.traverse(visitor);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processNode(ASTNode node) {
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
