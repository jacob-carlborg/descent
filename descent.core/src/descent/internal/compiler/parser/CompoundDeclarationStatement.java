package descent.internal.compiler.parser;

public class CompoundDeclarationStatement extends CompoundStatement {

	public CompoundDeclarationStatement(Loc loc, Statements statements) {
		super(loc, statements);
	}

	@Override
	public Statement syntaxCopy(SemanticContext context) {
		Statements a = new Statements();
		a.setDim(size(statements));
		for (int i = 0; i < size(statements); i++) {
			Statement s = (Statement) statements.get(i);
			if (s != null)
				s = s.syntaxCopy(context);
			a.set(i, s);
		}
		CompoundDeclarationStatement cs = context
				.newCompoundDeclarationStatement(loc, a);
		return cs;
	}

}
