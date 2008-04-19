package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class FuncExp extends Expression {

	public FuncLiteralDeclaration fd, sourceFd;
	
	// Descent: in case neither function nor delegate was present in the source file
	public boolean isEmptySyntax; 

	public FuncExp(Loc loc, FuncLiteralDeclaration fd) {
		super(loc, TOK.TOKfunction);
		this.fd = this.sourceFd = fd;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceFd);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return FUNC_EXP;
	}

	@Override
	public void scanForNestedRef(Scope sc, SemanticContext context) {
		// empty
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		if (type == null) {
			fd.semantic(sc, context);
			fd.parent = sc.parent;
			if (context.global.errors > 0) {
				if (fd.type.next == null) {
					fd.type.next = Type.terror;
				}
			} else {
				fd.semantic2(sc, context);
				if (context.global.errors == 0) {
					fd.semantic3(sc, context);
				}
			}

			// Type is a "delegate to" or "pointer to" the function literal
			if (fd.isNested()) {
				type = new TypeDelegate(fd.type);
				type = type.semantic(loc, sc, context);
			} else {
				type = fd.type.pointerTo(context);
			}
		}
		return this;
	}

	@Override
	public Expression syntaxCopy(SemanticContext context) {
		return new FuncExp(loc, (FuncLiteralDeclaration) fd.syntaxCopy(null, context));
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring(fd.toChars(context));
	}

	@Override
	public String toChars(SemanticContext context) {
		return fd.toChars(context);
	}

}
