package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.internal.compiler.parser.ast.IASTVisitor;

public class ConditionalDeclaration extends AttribDeclaration {

	public Condition condition;
	public List<Dsymbol> elsedecl;

	public ConditionalDeclaration(Loc loc, Condition condition, List<Dsymbol> decl,
			List<Dsymbol> elsedecl) {
		super(loc, decl);
		this.condition = condition;
		this.elsedecl = elsedecl;
	}

	@Override
	public int getNodeType() {
		return CONDITIONAL_DECLARATION;
	}
	
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
	public List<Dsymbol> include(Scope sc, ScopeDsymbol sd) {
		Assert.isNotNull(condition);
		return condition.include(sc, sd) != null ? decl : elsedecl;
	}

	@Override
	public boolean oneMember(Dsymbol[] ps) {
		if (condition.inc) {
			List d = condition.include(null, null) != null ? decl : elsedecl;
			return Dsymbol.oneMembers(d, ps);
		}
		ps[0] = null;
		return true;
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		ConditionalDeclaration dd;

		Assert.isTrue(s == null);
		dd = new ConditionalDeclaration(loc, condition.syntaxCopy(), Dsymbol
				.arraySyntaxCopy(decl), Dsymbol.arraySyntaxCopy(elsedecl));
		return dd;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		condition.toCBuffer(buf, hgs);
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
