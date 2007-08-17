package dtool.dom.definitions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.TemplateMixin;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.references.Reference;
import dtool.dom.statements.IStatement;
import dtool.refmodel.IScopeNode;

/*
 */
public class DefinitionMixin extends DefUnit implements IStatement {
	
	public Reference type;
	
	private DefinitionMixin(TemplateMixin elem, Reference tplInstance) {
		super(elem);
		this.type = tplInstance;
	}

	public static ASTNeoNode convertMixinInstance(TemplateMixin elem) {
		if(elem.ident != null) {
			Reference typeref = Reference.convertTemplateInstance(elem, elem.tiargs);
			return new DefinitionMixin(elem, typeref);
 		} else {
 			elem.setSourceRange(elem.typeStart, elem.typeLength);
 			Reference typeref = Reference.convertTemplateInstance(elem, elem.tiargs);
			return new MixinContainer(elem, typeref);
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
