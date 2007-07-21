package dtool.dom.definitions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;

import descent.internal.core.dom.TemplateDeclaration;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.declarations.Declaration;
import dtool.refmodel.EntityResolver;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

/*
 */
public class DefinitionTemplate extends DefUnit implements IScopeNode {

	public TemplateParameter[] templateParams; 
	public ASTNode[] decls;

	
	public DefinitionTemplate(TemplateDeclaration elem) {
		convertDsymbol(elem);
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

	@Override
	public EArcheType getArcheType() {
		return EArcheType.Template;
	}

	@Override
	public IScopeNode getMembersScope() {
		return this;
	}

	
	public List<IScope> getSuperScopes() {
		// TODO: template super scope
		return null;
	}
	

	public Iterator<ASTNode> getMembersIterator() {
		// TODO optimize, give a chained iterator
		List<ASTNode> list = new ArrayList<ASTNode>(decls.length + templateParams.length);
		list.addAll(Arrays.asList(decls));
		list.addAll(Arrays.asList(templateParams));
		return 	list.iterator();
	}

}
