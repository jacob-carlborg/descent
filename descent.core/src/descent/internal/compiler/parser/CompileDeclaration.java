package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.TOK.TOKeof;
import static descent.internal.compiler.parser.TOK.TOKstring;

// DMD 1.020
public class CompileDeclaration extends AttribDeclaration {

	public Expression exp, sourceExp;
	public IScopeDsymbol sd;

	public CompileDeclaration(Loc loc, Expression exp) {
		super(null);
		this.loc = loc;
		this.exp = exp;
		this.sourceExp = exp;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, decl);
		}
		visitor.endVisit(this);
	}

	@Override
	public int addMember(Scope sc, IScopeDsymbol sd, int memnum,
			SemanticContext context) {
		this.sd = sd;
		return memnum;
	}

	@Override
	public int getNodeType() {
		return COMPILE_DECLARATION;
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		exp = exp.semantic(sc, context);
		exp = resolveProperties(sc, exp, context);
		exp = exp.optimize(WANTvalue | WANTinterpret, context);
		if (exp.op != TOKstring) {
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.ArgumentToMixinMustBeString, this, new String[] { exp.toChars(context) }));
			return;
		}
		StringExp se = (StringExp) exp;
		se = se.toUTF8(sc, context);
		Parser p = new Parser(context.Module_rootModule.apiLevel, se.string);
		// p.nextToken();
		p.loc = loc;
		decl = p.parseModule();
		for(IDsymbol s : decl) {
			s.synthetic(true);
			s.setStart(getStart() + 1);
			s.setLength(getLength());
			s.setLineNumber(getLineNumber());
		}

		// TODO semantic do this better
		if (p.problems != null) {
			for (int i = 0; i < p.problems.size(); i++) {
				Problem problem = (Problem) p.problems.get(i);
				problem.setSourceStart(start);
				problem.setSourceEnd(start + length - 1);
				context.acceptProblem(problem);
			}
		}

		if (p.token.value != TOKeof) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.IncompleteMixinDeclaration, this,
					new String[] { se.toChars(context) }));
		}

		super.addMember(sc, sd, 0, context);
		super.semantic(sc, context);
	}

	@Override
	public IDsymbol syntaxCopy(IDsymbol s, SemanticContext context) {
		CompileDeclaration sc = new CompileDeclaration(loc, exp.syntaxCopy(context));
		return sc;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("mixin(");
		exp.toCBuffer(buf, hgs, context);
		buf.writestring(");");
		buf.writenl();
	}
	
	@Override
	public String getSignature() {
		// TODO Auto-generated method stub
		return null;
	}

}
