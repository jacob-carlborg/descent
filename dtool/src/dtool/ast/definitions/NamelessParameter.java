package dtool.ast.definitions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Type;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.CommonRefSingle;
import dtool.ast.references.Reference;
import dtool.ast.references.ReferenceConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

import static melnorme.miscutil.Assert.assertNotNull;


/** 
 * A nameless function parameter, such as in: <br>
 * <code> void func(int, int); </code>
 */
public class NamelessParameter extends ASTNeoNode implements IFunctionParameter {

	public final Reference type;
	public final int storageClass;
	public final Resolvable defaultValue;

	protected NamelessParameter(descent.internal.compiler.parser.Argument elem
			, ASTConversionContext convContext) {
		convertNode(elem);
		this.type = ReferenceConverter.convertType(elem.type, convContext);
		assertNotNull(elem.type);
		this.storageClass = elem.storageClass;
		this.defaultValue = Expression.convert(elem.defaultArg, convContext);
	}
	
	public NamelessParameter(Type type, ASTConversionContext convContext) {
		convertNode(type);
		this.type = ReferenceConverter.convertType(type, convContext);
		this.storageClass = 0;
		this.defaultValue = null;
	}

	public NamelessParameter(descent.internal.compiler.parser.Argument elem, 
			IdentifierExp ident, ASTConversionContext convContext) {
		convertNode(elem);
		this.type = CommonRefSingle.convertToSingleRef(ident, convContext);
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
	
	
	@Override
	public String toStringAsFunctionSignaturePart() {
		return type.toStringAsElement();
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
