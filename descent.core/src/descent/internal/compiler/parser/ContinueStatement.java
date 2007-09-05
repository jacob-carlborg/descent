package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class ContinueStatement extends Statement {

	public IdentifierExp ident;

	public ContinueStatement(Loc loc, IdentifierExp ident) {
		super(loc);
		this.ident = ident;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
		}
		visitor.endVisit(this);
	}

	@Override
	public boolean fallOffEnd(SemanticContext context) {
		return false;
	}

	@Override
	public int getNodeType() {
		return CONTINUE_STATEMENT;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		// START()
		if (istate.start != null) {
			if (istate.start != this) {
				return null;
			}
			istate.start = null;
		}
		// START()
		if (ident != null) {
			return EXP_CANT_INTERPRET;
		} else {
			return EXP_CONTINUE_INTERPRET;
		}
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		if (ident != null) {
			Scope scx;
			FuncDeclaration thisfunc = sc.func;

			for (scx = sc; scx != null; scx = scx.enclosing) {
				LabelStatement ls;

				if (scx.func != thisfunc) // if in enclosing function
				{
					if (sc.fes != null) // if this is the body of a foreach
					{
						for (; scx != null; scx = scx.enclosing) {
							ls = scx.slabel;
							if (ls != null && ls.ident == ident
									&& ls.statement == sc.fes) {
								// Replace continue ident; with return 0;
								return new ReturnStatement(0, new IntegerExp(0));
							}
						}

						/* Post this statement to the fes, and replace
						 * it with a return value that caller will put into
						 * a switch. Caller will figure out where the break
						 * label actually is.
						 * Case numbers start with 2, not 0, as 0 is continue
						 * and 1 is break.
						 */
						Statement s;
						sc.fes.cases.add(this);
						s = new ReturnStatement(0, new IntegerExp(sc.fes.cases
								.size() + 1));
						return s;
					}
					break; // can't continue to it
				}

				ls = scx.slabel;
				if (ls != null && ls.ident == ident) {
					Statement s = ls.statement;

					if (!s.hasContinue()) {
						error("label '%s' has no continue", ident
								.toChars(context));
					}
					if (ls.tf != sc.tf) {
						error("cannot continue out of finally block");
					}
					return this;
				}
			}
			error("enclosing label '%s' for continue not found", ident
					.toChars(context));
		} else if (null == sc.scontinue) {
			if (sc.fes != null) {
				Statement s;

				// Replace continue; with return 0;
				s = new ReturnStatement(0, new IntegerExp(0));
				return s;
			}
			error("continue is not inside a loop");
		}
		return this;
	}

	@Override
	public Statement syntaxCopy() {
		ContinueStatement s = new ContinueStatement(loc, ident);
		return s;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("continue");
		if (ident != null) {
			buf.writebyte(' ');
			buf.writestring(ident.toChars(context));
		}
		buf.writebyte(';');
		buf.writenl();
	}

}
