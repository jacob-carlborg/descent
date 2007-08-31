package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class BreakStatement extends Statement {

	public IdentifierExp ident;

	public BreakStatement(Loc loc, IdentifierExp ident) {
		super(loc);
		this.ident = ident;
	}

	@Override
	public int getNodeType() {
		return BREAK_STATEMENT;
	}

	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
		}
		visitor.endVisit(this);
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		// If:
		//	break Identifier;
		if (ident != null) {
			Scope scx;
			FuncDeclaration thisfunc = sc.func;

			for (scx = sc; scx != null; scx = scx.enclosing) {
				LabelStatement ls;

				if (scx.func != thisfunc) // if in enclosing function
				{
					if (sc.fes != null) // if this is the body of a foreach
					{
						/* Post this statement to the fes, and replace
						 * it with a return value that caller will put into
						 * a switch. Caller will figure out where the break
						 * label actually is.
						 * Case numbers start with 2, not 0, as 0 is continue
						 * and 1 is break.
						 */
						Statement s;
						sc.fes.cases.add(this);
						s = new ReturnStatement(Loc.ZERO, new IntegerExp(
								sc.fes.cases.size() + 1));
						return s;
					}
					break; // can't break to it
				}

				ls = scx.slabel;
				if (ls != null && ls.ident.equals(ident)) {
					Statement s = ls.statement;

					if (!s.hasBreak())
						error("label '%s' has no break", ident.toChars(context));
					if (ls.tf != sc.tf)
						error("cannot break out of finally block");
					return this;
				}
			}
			error("enclosing label '%s' for break not found", ident.toChars(context));
		} else if (sc.sbreak == null) {
			if (sc.fes != null) {
				Statement s;

				// Replace break; with return 1;
				s = new ReturnStatement(0, new IntegerExp(1));
				return s;
			}
			error("break is not inside a loop or switch");
		}
		return this;
	}

}
