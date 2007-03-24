package descent.internal.compiler.parser;

import org.eclipse.core.runtime.Assert;

public class IfStatement extends Statement {
	
	public Argument arg;
	public Expression condition;
	public Expression sourceCondition;
	public Statement ifbody;
	public Statement elsebody;
	
	public VarDeclaration match;	// for MatchExpression results

	public IfStatement(Argument arg, Expression condition, Statement ifbody, Statement elsebody) {
		this.arg = arg;
		this.condition = condition;
		this.sourceCondition = condition;
		this.ifbody = ifbody;
		this.elsebody = elsebody;		
	}
	
	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		condition = condition.semantic(sc, context);
		condition = resolveProperties(sc, condition, context);
		condition = condition.checkToBoolean(context);

		// If we can short-circuit evaluate the if statement, don't do the
		// semantic analysis of the skipped code.
		// This feature allows a limited form of conditional compilation.
		condition = condition.optimize(WANTflags);

		// Evaluate at runtime
		int cs0 = sc.callSuper;
		int cs1;

		Scope scd;
		if (arg != null) { /*
							 * Declare arg, which we will set to be the result
							 * of condition.
							 */
			ScopeDsymbol sym = new ScopeDsymbol();
			sym.parent = sc.scopesym;
			scd = sc.push(sym);

			Type t = arg.type != null ? arg.type : condition.type;
			match = new VarDeclaration(t, arg.ident, null);
			match.noauto = true;
			match.semantic(scd, context);
			if (scd.insert(match) == null) {
				Assert.isTrue(false);
			}
			match.parent = sc.func;

			/*
			 * Generate: (arg = condition)
			 */
			VarExp v = new VarExp(match);
			condition = new AssignExp(v, condition);
			condition = condition.semantic(scd, context);
		} else {
			scd = sc.push();
		}
		ifbody = ifbody.semantic(scd, context);
		scd.pop();

		cs1 = sc.callSuper;
		sc.callSuper = cs0;
		if (elsebody != null)
			elsebody = elsebody.semanticScope(sc, null, null, context);
		sc.mergeCallSuper(cs1);

		return this;
	}
	
	@Override
	public int getNodeType() {
		return IF_STATEMENT;
	}

}
