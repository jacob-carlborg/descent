package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.IInitializer;
import descent.core.IJavaElement;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.ASTNode;
import descent.internal.compiler.parser.ast.AstVisitorAdapter;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.TOK.TOKeof;
import static descent.internal.compiler.parser.TOK.TOKstring;


public class CompileDeclaration extends AttribDeclaration {

	public Expression exp, sourceExp;
	public ScopeDsymbol sd;
	
	protected IInitializer javaElement;
	
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
			TreeVisitor.acceptChildren(visitor, sourceExp);
			TreeVisitor.acceptChildren(visitor, decl);
		}
		visitor.endVisit(this);
	}

	@Override
	public int addMember(Scope sc, ScopeDsymbol sd, int memnum,
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
			if (context.acceptsErrors()) {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.ArgumentToMixinMustBeString, this, exp.toChars(context)));
			}
			return;
		}
		StringExp se = (StringExp) exp;
		se = se.toUTF8(sc, context);
		Parser p = new Parser(context.Module_rootModule.apiLevel, se.string);
		// p.nextToken();
		p.loc = loc;
		decl = p.parseModule();
		for(Dsymbol s : decl) {
			s.accept(new AstVisitorAdapter() {
				@Override
				public void preVisit(ASTNode node) {
					if (node instanceof ASTDmdNode) {
						ASTDmdNode s = (ASTDmdNode) node;
						s.synthetic = true;
						s.setStart(getStart() + 1);
						s.setLength(getLength());
						s.setLineNumber(getLineNumber());					
						s.creator = CompileDeclaration.this;
					}
				}
			});
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
			if (context.acceptsErrors()) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.IncompleteMixinDeclaration, this,
						new String[] { se.toChars(context) }));
			}
		}

		super.addMember(sc, sd, 0, context);
		super.semantic(sc, context);
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s, SemanticContext context) {
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
	public IJavaElement getJavaElement() {
		return javaElement;
	}
	
	public void setJavaElement(IInitializer javaElement) {
		this.javaElement = javaElement;
	}

}
