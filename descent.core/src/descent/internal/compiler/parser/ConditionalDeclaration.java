package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class ConditionalDeclaration extends AttribDeclaration {

	public Condition condition;
	public Dsymbols elsedecl;

	public ConditionalDeclaration(Condition condition, Dsymbols decl,
			Dsymbols elsedecl) {
		super(decl);
		this.condition = condition;
		this.elsedecl = elsedecl;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, condition);
			TreeVisitor.acceptChildren(visitor, decl);
			TreeVisitor.acceptChildren(visitor, elsedecl);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return CONDITIONAL_DECLARATION;
	}

	@Override
	public Dsymbols include(Scope sc, ScopeDsymbol sd, SemanticContext context) {
		Assert.isNotNull(condition);
		return condition.include(sc, sd, context) ? decl : elsedecl;
	}

	@Override
	public boolean oneMember(Dsymbol[] ps, SemanticContext context) {
		if (condition.inc != 0) {
			Dsymbols d = condition.include(null, null, context) ? decl
					: elsedecl;
			return Dsymbol.oneMembers(d, ps, context);
		}
		ps[0] = null;
		return true;
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		ConditionalDeclaration dd;

		Assert.isTrue(s == null);
		dd = new ConditionalDeclaration(condition.syntaxCopy(), Dsymbol
				.arraySyntaxCopy(decl), Dsymbol.arraySyntaxCopy(elsedecl));
		return dd;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		condition.toCBuffer(buf, hgs, context);
		if (decl != null || elsedecl != null) {
			buf.writenl();
			buf.writeByte('{');
			buf.writenl();
			if (decl != null) {
				for (Dsymbol s : decl) {
					buf.writestring("    ");
					s.toCBuffer(buf, hgs, context);
				}
			}
			buf.writeByte('}');
			if (elsedecl != null) {
				buf.writenl();
				buf.writestring("else");
				buf.writenl();
				buf.writeByte('{');
				buf.writenl();
				for (Dsymbol s : elsedecl) {
					buf.writestring("    ");
					s.toCBuffer(buf, hgs, context);
				}
				buf.writeByte('}');
			}
		} else {
			buf.writeByte(':');
		}
		buf.writenl();
	}

}
