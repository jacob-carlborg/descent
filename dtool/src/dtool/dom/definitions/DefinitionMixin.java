package dtool.dom.definitions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.TemplateMixin;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.references.RefTemplateInstance;
import dtool.dom.references.Reference;
import dtool.dom.statements.IStatement;
import dtool.refmodel.IScopeNode;

/*
 * TODO mixin
 */
public class DefinitionMixin extends DefUnit implements IStatement {
	
	public Reference type;
	
	public static ASTNeoNode convertMixinInstance(TemplateMixin elem) {
		RefTemplateInstance tplInstance = new RefTemplateInstance();
		tplInstance.setSourceRange(elem);
		tplInstance.name = elem.qName.name; // FIXME should be a prof qualified ref
		tplInstance.tiargs = DescentASTConverter.convertManyL(elem.tiargs, tplInstance.tiargs);
		if(elem.ident != null) {
			DefinitionMixin defMixin = new DefinitionMixin();
			defMixin.convertDsymbol(elem);
			defMixin.type = tplInstance;
			return defMixin;
 		} else {
			MixinContainer contMixin = new MixinContainer();
			contMixin.convertNode(elem);
			contMixin.type = tplInstance;
			return contMixin;
 		}
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, type);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Mixin;
	}

	@Override
	public IScopeNode getMembersScope() {
		return type.getTargetScope();
	}

}
