package dtool.dom.definitions;

import java.util.ArrayList;
import java.util.List;

import util.tree.TreeVisitor;
import descent.internal.core.dom.TemplateDeclaration;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.declarations.Declaration;
import dtool.model.IScope;

/*
 */
public class DefinitionTemplate extends DefUnit implements IScope {

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
	public IScope getBindingScope() {
		return this;
	}

	public List<DefUnit> getDefUnits() {
		List<DefUnit> defunits;
		defunits = new ArrayList<DefUnit>(templateParams.length + decls.length);
		
		for(int i = 0; i < templateParams.length; i++) {
			defunits.add(templateParams[i]);
		}
		for(ASTNode elem: decls) {
			if(elem instanceof DefUnit)
				defunits.add((DefUnit)elem);
		}
		return defunits;
	}
	
	public IScope getSuperScope() {
		// TODO: template super scope
		return null;
	}

}
