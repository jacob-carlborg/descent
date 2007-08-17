package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class TemplateDeclaration extends ScopeDsymbol {
	
	// Wether this template declaration is just a wrapper for "class B(T) ..."
	public boolean wrapper; 
	public List<TemplateParameter> parameters;
	public Scope scope;
	public Dsymbol onemember;
	public TemplateDeclaration overnext;	// next overloaded TemplateDeclaration
	public TemplateDeclaration overroot;	// first in overnext list
	
	public TemplateDeclaration(Loc loc, IdentifierExp id, List<TemplateParameter> parameters, List<Dsymbol> decldefs) {
		super(loc, id);
		this.parameters = parameters;
		this.members = decldefs;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, parameters);
			TreeVisitor.acceptChildren(visitor, members);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		if (scope != null)
			return; // semantic() already run

		if (sc.func != null) {
			error("cannot declare template at function scope %s", sc.func
					.toChars());
		}

		if (/* global.params.useArrayBounds && */sc.module != null) {
			// Generate this function as it may be used
			// when template is instantiated in other modules
			sc.module.toModuleArray();
		}

		if (/* global.params.useAssert && */sc.module != null) {
			// Generate this function as it may be used
			// when template is instantiated in other modules
			sc.module.toModuleAssert();
		}

		/*
		 * Remember Scope for later instantiations, but make a copy since
		 * attributes can change.
		 */
		this.scope = new Scope(sc);
		this.scope.setNoFree();

		// Set up scope for parameters
		ScopeDsymbol paramsym = new ScopeDsymbol(loc);
		paramsym.parent = sc.parent;
		Scope paramscope = sc.push(paramsym);

		for (TemplateParameter tp : parameters) {
			tp.semantic(paramscope, context);
		}

		paramscope.pop();

		if (members != null) {
			Dsymbol[] s = { null };
			if (Dsymbol.oneMembers(members, s)) {
				if (s[0] != null && s[0].ident != null
						&& s[0].ident.ident.equals(ident.ident)) {
					onemember = s[0];
					s[0].parent = this;
				}
			}
		}
	}
	
	@Override
	public TemplateDeclaration isTemplateDeclaration() {
		return this;
	}
	
	@Override
	public int getNodeType() {
		return TEMPLATE_DECLARATION;
	}

	public FuncDeclaration deduce(Scope sc, Object object, List<Expression> arguments, SemanticContext context) {
		// TODO semantic
		return null;
	}

}
