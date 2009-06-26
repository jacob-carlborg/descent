package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.IField;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class AliasThis extends Dsymbol {

	public IdentifierExp ident;
	private IField javaElement;

	public AliasThis(Loc loc, IdentifierExp ident) {
		super(null); // it's anonymous (no identifier)
		this.loc = loc;
		this.ident = ident;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifiers);
			TreeVisitor.acceptChildren(visitor, ident);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public int getNodeType() {
		return ALIAS_THIS;
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s, SemanticContext context) {
		assert (null == s);
		/*
		 * Since there is no semantic information stored here, we don't need to
		 * copy it.
		 */
		return this;
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		Dsymbol parent = sc.parent;
		if (parent != null)
			parent = parent.pastMixin();
		AggregateDeclaration ad = null;
		if (parent != null) {
			ad = parent.isAggregateDeclaration();
		}
		if (ad != null) {
			if (ad.aliasthis != null) {
				if (context.acceptsErrors()) {
					context.acceptProblem(Problem.newSemanticTypeError(IProblem.ThereCanBeOnlyOneAliasThis, this));
				}
			}
			assert (size(ad.members) != 0);
			Dsymbol s = ad.search(loc, ident, 0, context);
			ident.resolvedSymbol = s;
			ad.aliasthis = s;
		} else {
			if (context.acceptsErrors()) {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.AliasThisCanOnlyAppearInStructOrClassDeclaration, this, parent != null ? parent.toChars(context) : "nowhere"));
			}
		}
	}
	
	@Override
	public String kind() {
		return "alias this";
	}
	
	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.writestring("alias ");
	    buf.writestring(ident.toChars());
	    buf.writestring(" this;\n");
	}
	
	public void setJavaElement(IField field) {
		this.javaElement = field;
	}
	
	@Override
	public IField getJavaElement() {
		return javaElement;
	}

}
