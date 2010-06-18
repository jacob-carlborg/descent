package dtool.ast.definitions;

import java.util.Collection;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeBasic;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.Reference;
import dtool.ast.references.ReferenceConverter;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.NodeUtil;

import static melnorme.miscutil.Assert.assertFail;
import static melnorme.miscutil.Assert.assertNotNull;

public class FunctionParameter extends DefUnit implements IFunctionParameter {
	
	public Reference type;
	public int storageClass;
	public Resolvable defaultValue;
	
	protected FunctionParameter(descent.internal.compiler.parser.Argument elem) {
		super(elem.ident);
		setSourceRange(elem);
		
		
		if(elem.type instanceof TypeBasic && ((TypeBasic)elem.type).ty.name == null) {
			assertFail();
			this.type = null;
		} else 
			this.type = ReferenceConverter.convertType(elem.type);
		assertNotNull(this.type);
		this.storageClass = elem.storageClass;
		this.defaultValue = Expression.convert(elem.defaultArg);
			
	}
	
	public FunctionParameter(Type type, IdentifierExp id) {
		super(id);
		setSourceRange(type.getStartPos(), id.getEndPos() - type.getStartPos());
		
		this.type = ReferenceConverter.convertType(type);
	}

	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			//TreeVisitor.acceptChildren(visitor, inout);
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, defaultValue);
		}
		visitor.endVisit(this);	
	}

	@Override
	public IScopeNode getMembersScope() {
		Collection<DefUnit> defunits = type.findTargetDefUnits(true);
		if(defunits == null || defunits.isEmpty())
			return null;
		return defunits.iterator().next().getMembersScope();
		//return defunit.getMembersScope();
	}
	
	@Override
	public String toStringForHoverSignature() {
		String str = getArcheType().toString() + "  "
			+ type.toStringAsElement() + " " + getName();
		return str;
	}
	
	@Override
	public String toStringForCodeCompletion() {
		return getName() + "   " + type.toStringAsElement() + " - "
				+ NodeUtil.getOuterDefUnit(this).toStringAsElement();
	}

	@Override
	public String toStringAsFunctionSignaturePart() {
		return type.toStringAsElement() + " " + getName();
	}
	
	@Override
	public String toStringAsFunctionSimpleSignaturePart() {
		return type.toStringAsElement();
	}

	@Override
	public String toStringInitializer() {
		if(defaultValue == null)
			return null;
		return defaultValue.toStringAsElement();
	}

}
