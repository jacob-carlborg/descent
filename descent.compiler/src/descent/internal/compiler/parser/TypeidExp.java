package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;


public class TypeidExp extends Expression {

	public Type typeidType, sourceTypeidType;

	public TypeidExp(char[] filename, int lineNumber, Type typeidType) {
		super(filename, lineNumber, TOK.TOKtypeid);
		this.typeidType = this.sourceTypeidType = typeidType;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceTypeidType);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return TYPEID_EXP;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Expression e;
		typeidType = typeidType.semantic(filename, lineNumber, sc, context);
		e = typeidType.getTypeInfo(sc, context);
		if (e.lineNumber == 0) {
			e.lineNumber = lineNumber;		// so there's at least some line number info
		}
		return e;
	}

	@Override
	public Expression syntaxCopy(SemanticContext context) {
		return new TypeidExp(filename, lineNumber, typeidType.syntaxCopy(context));
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("typeid(");
		typeidType.toCBuffer(buf, null, hgs, context);
		buf.writeByte(')');
	}

}
