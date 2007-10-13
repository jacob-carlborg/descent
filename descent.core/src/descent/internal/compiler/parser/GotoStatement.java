package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class GotoStatement extends Statement {

	public IdentifierExp ident;
	public LabelDsymbol label;
	public TryFinallyStatement tf;

	public GotoStatement(Loc loc, IdentifierExp ident) {
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
		return GOTO_STATEMENT;
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
		if (label == null || label.statement == null) {
			throw new IllegalStateException(
					"assert(label && label->statement);");
		}
		istate.gotoTarget = label.statement;
		return EXP_GOTO_INTERPRET;
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		FuncDeclaration fd = sc.parent.isFuncDeclaration();

		tf = sc.tf;
		label = fd.searchLabel(ident);
		if (null == label.statement && sc.fes != null) {
			/* Either the goto label is forward referenced or it
			 * is in the function that the enclosing foreach is in.
			 * Can't know yet, so wrap the goto in a compound statement
			 * so we can patch it later, and add it to a 'look at this later'
			 * list.
			 */
			Statements a = new Statements();
			Statement s;

			a.add(this);
			s = new CompoundStatement(loc, a);
			sc.fes.gotos.add(s); // 'look at this later' list
			return s;
		}
		if (label.statement != null && label.statement.tf != sc.tf) {
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.CannotGotoInOrOutOfFinallyBlock, 0, start, length));
		}
		return this;
	}

	@Override
	public Statement syntaxCopy() {
		GotoStatement s = new GotoStatement(loc, ident);
		return s;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("goto ");
		buf.writestring(ident.toChars());
		buf.writebyte(';');
		buf.writenl();
	}

}
