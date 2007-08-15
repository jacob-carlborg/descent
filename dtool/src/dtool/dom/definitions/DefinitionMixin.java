package dtool.dom.definitions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.TemplateMixin;
import descent.internal.compiler.parser.TypeQualified;
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
		RefTemplateInstance tplInstance = new RefTemplateInstance(elem);
		Reference.convertTypeQualified(null, (TypeQualified) elem.tqual);
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
