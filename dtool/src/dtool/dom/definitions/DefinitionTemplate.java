package dtool.dom.definitions;

import util.tree.TreeVisitor;
import descent.internal.core.dom.TemplateDeclaration;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.declarations.Declaration;

/*
 * TODO TEMPLATE
 */
public class DefinitionTemplate extends ASTNeoNode {

	public TemplateParameter[] templateParams; 
	public ASTNode[] decls;

	
	public DefinitionTemplate(TemplateDeclaration elem) {
		decls = Declaration.convertMany(elem.getDeclarationDefinitions());
		templateParams = TemplateParameter.convertMany(elem.getTemplateParameters());
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, templateParams);
			TreeVisitor.acceptChildren(visitor, decls);
		}
		visitor.endVisit(this);	}

}
