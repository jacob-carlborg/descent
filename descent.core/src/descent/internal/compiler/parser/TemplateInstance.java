package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class TemplateInstance extends ScopeDsymbol {

	public List<IdentifierExp> idents;
	public List<ASTDmdNode> tiargs;
	public TemplateDeclaration tempdecl; // referenced by foo.bar.abc
	public TemplateInstance inst; // refer to existing instance
	public AliasDeclaration aliasdecl; // != null if instance is an alias for its
	public boolean semanticdone; // has semantic() been done?
	public WithScopeSymbol withsym;
	public IdentifierExp name;

	public TemplateInstance(IdentifierExp id) {
		super(null);
		this.idents = new ArrayList<IdentifierExp>(3);
		this.idents.add(id);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, tiargs);
		}
		visitor.endVisit(this);
	}

	@Override
	public Dsymbol toAlias(SemanticContext context) {
		if (inst == null) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.CannotResolveForwardReference, 0, start, length));
			return this;
		}

		if (inst != this)
			return inst.toAlias(context);

		if (aliasdecl != null)
			return aliasdecl.toAlias(context);

		return inst;
	}

	@Override
	public TemplateInstance isTemplateInstance() {
		return this;
	}

	@Override
	public int getNodeType() {
		return TEMPLATE_INSTANCE;
	}

	@Override
	public String toChars(SemanticContext context) {
		OutBuffer buf = new OutBuffer();
		HdrGenState hgs = new HdrGenState();
		toCBuffer(buf, hgs, context);
		String s = buf.toChars();
		buf.data = null;
		return s;
	}

	@Override
	public String mangle(SemanticContext context) {
		OutBuffer buf = new OutBuffer();
		String id;

		id = ident != null ? ident.toChars() : toChars(context);
		if (tempdecl.parent != null) {
			String p = tempdecl.parent.mangle(context);
			if (p.charAt(0) == '_' && p.charAt(1) == 'D')
				p += 2;
			buf.writestring(p);
		}
		// TODO semantic this was %zu -> what's that?
		buf.writestring(id.length());
		buf.writestring(id);
		id = buf.toChars();
		buf.data = null;
		return id;
	}

}