package descent.internal.compiler.parser;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;

public class VarDeclaration extends Declaration {

	public boolean last;		// is this the last declaration in a multi declaration?
	public Type type;
	public Initializer init;
	public Dsymbol aliassym;	// if redone as alias to another symbol

	public VarDeclaration(Type type, IdentifierExp ident, Initializer init) {
		this.type = type;
		this.ident = ident;
		this.init = init;
	}
	
	@Override
	public Dsymbol toAlias(SemanticContext context) {
		Assert.isTrue(this != aliassym);
	    Dsymbol s = aliassym != null ? aliassym.toAlias(context) : this;
	    return s;
	}
	
	@Override
	public VarDeclaration isVarDeclaration() {
		return this;
	}
	
	@Override
	public boolean isDataseg(SemanticContext context) {
	    Dsymbol parent = this.toParent();
	    if (parent == null && (storage_class & (STC.STCstatic | STC.STCconst)) == 0)
	    {	
	    	context.acceptProblem(Problem.newSemanticTypeError("Forward referenced", IProblem.ForwardReference, 0, start, length));
	    	type = Type.terror;
	    	return false;
	    }
	    return ((storage_class & (STC.STCstatic | STC.STCconst)) != 0 ||
		   parent.isModule() != null ||
		   parent.isTemplateInstance() != null );
	}
	
	public void checkNestedReference(Scope sc, SemanticContext context) {
		if (!isDataseg(context) && parent != sc.parent && parent != null) {
			FuncDeclaration fdv = toParent().isFuncDeclaration();
			FuncDeclaration fdthis = sc.parent.isFuncDeclaration();

			if (fdv != null && fdthis != null) {
				/* TODO loc???
				if (loc.filename)
					fdthis.getLevel(loc, fdv);
				nestedref = 1;
				fdv.nestedFrameRef = 1;
				*/
			}
		}
	}
	
	public ExpInitializer getExpInitializer() {
		ExpInitializer ei;

		if (init != null) {
			ei = init.isExpInitializer();
		} else {
			Expression e = type.defaultInit();
			if (e != null) {
				ei = new ExpInitializer(e);
			} else {
				ei = null;
			}
		}
		return ei;
	}
	
	@Override
	public int kind() {
		return VAR_DECLARATION;
	}
	
	@Override
	public String toString() {
		return type + " " + ident + ";";
	}

}
