package dtool.dom.definitions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.Type;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.expressions.Expression;
import dtool.dom.expressions.Resolvable;
import dtool.dom.references.Reference;
import dtool.dom.references.ReferenceConverter;

import static melnorme.miscutil.Assert.assertNotNull;

public class NamelessParameter extends ASTNeoNode implements IFunctionParameter {

	public final Reference type;
	public final int storageClass;
	public final Resolvable defaultValue;

	protected NamelessParameter(descent.internal.compiler.parser.Argument elem) {
		convertNode(elem);
		this.type = ReferenceConverter.convertType(elem.type);
		assertNotNull(elem.type);
		this.storageClass = elem.storageClass;
		this.defaultValue = Expression.convert(elem.defaultArg);
			
	}
	
	public NamelessParameter(Type type) {
		convertNode(type);
		this.type = ReferenceConverter.convertType(type);
		this.storageClass = 0;
		this.defaultValue = null;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, defaultValue);
		}
		visitor.endVisit(this);	
	}
	
	
	//@Override
	public String toStringAsFunctionSignaturePart() {
		return type.toStringAsElement();
	}

	//@Override
	public String toStringAsFunctionSimpleSignaturePart() {
		return type.toStringAsElement();
	}

	//@Override
	public String toStringInitializer() {
		if(defaultValue == null)
			return null;
		return defaultValue.toStringAsElement();
	}

}
