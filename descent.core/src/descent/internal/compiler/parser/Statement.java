package descent.internal.compiler.parser;


// DMD 1.020
public abstract class Statement extends ASTDmdNode {

	public Loc loc;
	public boolean incontract;

	public Statement(Loc loc) {
		this.loc = loc;
	}

	public boolean comeFrom() {
		return false;
	}

	public Expression doInline(InlineDoState ids) {
		throw new IllegalStateException("assert(0);");
	}

	public boolean fallOffEnd(SemanticContext context) {
		return true;
	}

	public Statements flatten(Scope sc, SemanticContext context) {
		return null;
	}

	public boolean hasBreak() {
		return false;
	}

	public boolean hasContinue() {
		return false;
	}

	public int inlineCost(InlineCostState ics, SemanticContext context) {
		return COST_MAX;
	}

	public Statement inlineScan(InlineScanState iss, SemanticContext context) {
		return this;
	}

	public Expression interpret(InterState istate, SemanticContext context) {
		// START()
		if (istate.start != null) {
			if (istate.start != this) {
				return null;
			}
			istate.start = null;
		}
		// START()
		return EXP_CANT_INTERPRET;
	}

	public AsmStatement isAsmStatement() {
		return null;
	}

	public CompoundStatement isCompoundStatement() {
		return null;
	}

	public DeclarationStatement isDeclarationStatement() {
		return null;
	}

	public GotoStatement isGotoStatement() {
		return null;
	}

	public IfStatement isIfStatement() {
		return null;
	}

	public ReturnStatement isReturnStatement() {
		return null;
	}

	public ScopeStatement isScopeStatement() {
		return null;
	}

	public TryCatchStatement isTryCatchStatement() {
		return null;
	}

	public void scopeCode(Statement[] sentry, Statement[] sexception,
			Statement[] sfinally) {
		sentry[0] = null;
		sexception[0] = null;
		sfinally[0] = null;
	}

	public Statement semantic(Scope sc, SemanticContext context) {
		return this;
	}

	public Statement semanticScope(Scope sc, Statement sbreak,
			Statement scontinue, SemanticContext context) {
		Scope scd;
		Statement s;

		scd = sc.push();
		if (sbreak != null) {
			scd.sbreak = sbreak;
		}
		if (scontinue != null) {
			scd.scontinue = scontinue;
		}
		s = semantic(scd, context);
		scd.pop();
		return s;
	}

	public Statement syntaxCopy() {
		throw new IllegalStateException("assert(0);");
	}

	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("Statement::toCBuffer()");
		buf.writenl();
	}

	@Override
	public String toChars(SemanticContext context) {
		OutBuffer buf = new OutBuffer();
		HdrGenState hgs = new HdrGenState();
		toCBuffer(buf, hgs, context);
		return buf.toChars();
	}

	public boolean usesEH() {
		return false;
	}
	
	@Override
	public int getLineNumber() {
		return loc.linnum;
	}

}
