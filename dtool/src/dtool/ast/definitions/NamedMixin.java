package dtool.ast.definitions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.TemplateMixin;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.references.Reference;
import dtool.ast.references.ReferenceConverter;
import dtool.ast.statements.IStatement;
import dtool.refmodel.IScopeNode;

/*
 */
public class NamedMixin extends DefUnit implements IStatement {
	
	public Reference type;
	
	private NamedMixin(TemplateMixin elem, Reference tplInstance) {
		super(elem);
		this.type = tplInstance;
	}

	public static ASTNeoNode convertMixinInstance(TemplateMixin elem) {
		if(elem.ident != null) {
			Reference typeref = ReferenceConverter.convertTemplateInstance(elem, elem.tiargs);
			return new NamedMixin(elem, typeref);
 		} else {
 			elem.setSourceRange(elem.typeStart, elem.typeLength);
 			Reference typeref = ReferenceConverter.convertTemplateInstance(elem, elem.tiargs);
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
