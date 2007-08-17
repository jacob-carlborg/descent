package dtool.dom.definitions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.TemplateValueParameter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.expressions.Expression;
import dtool.dom.references.Reference;
import dtool.refmodel.IScopeNode;

public class TemplateParamValue extends TemplateParameter {

	public Reference type;
	public Expression specvalue;
	public Expression defaultvalue;

	public TemplateParamValue(TemplateValueParameter elem) {
		super(elem.ident);
		convertNode(elem);
		this.type = Reference.convertType(elem.valType);
		this.specvalue = Expression.convert(elem.specValue);
		this.defaultvalue = Expression.convert(elem.defaultValue);
	}

	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}

	@Override
	public IScopeNode getMembersScope() {
		return type.getTargetScope();
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, specvalue);
			TreeVisitor.acceptChildren(visitor, defaultvalue);
		}
		visitor.endVisit(this);	
	}
	
}
