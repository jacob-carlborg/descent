package dtool.ast.definitions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.TemplateValueParameter;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.Reference;
import dtool.ast.references.ReferenceConverter;
import dtool.refmodel.IScopeNode;

public class TemplateParamValue extends TemplateParameter {

	public Reference type;
	public Resolvable specvalue;
	public Resolvable defaultvalue;

	public TemplateParamValue(TemplateValueParameter elem) {
		super(elem.ident);
		convertNode(elem);
		this.type = ReferenceConverter.convertType(elem.valType);
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