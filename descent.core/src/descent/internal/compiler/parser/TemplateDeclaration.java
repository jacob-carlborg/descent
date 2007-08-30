package descent.internal.compiler.parser;

import java.util.ArrayList;
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

	public FuncDeclaration deduce(Scope sc, Loc loc, List<ASTDmdNode> targsi,
			List<Expression> fargs, SemanticContext context) {
		
		MATCH m_best = MATCH.MATCHnomatch;
	    TemplateDeclaration td_ambig = null;
	    TemplateDeclaration td_best = null;
	    List<ASTDmdNode> tdargs = new ArrayList<ASTDmdNode>();
	    TemplateInstance ti;
	    FuncDeclaration fd;
	    
	    for (TemplateDeclaration td = this; null != td; td = td.overnext)
	    {
			if (null == td.scope)
			{
			    error("forward reference to template %s", td.toChars());
			    Lerror(fargs, context);
			}
			if (null == td.onemember ||
				null == td.onemember.toAlias(context).isFuncDeclaration())
			{
			    error("is not a function template");
			    Lerror(fargs, context);
			}
			
			MATCH m;
			
			List[] dedargs_ = new List[] { null }; // Pass a one-element array to get reference semantics
			m = MATCH.MATCHnomatch; /* td.deduceMatch(targsi, fargs, dedargs_); */
			List<ASTDmdNode> dedargs = dedargs_[0];
			
			//printf("deduceMatch = %d\n", m);
			if (m == MATCH.MATCHnomatch)
			{
			    continue;
			}
			else if (m.ordinal() < m_best.ordinal())
			{
				// Ltd:
				td_ambig = null;
				continue;
			}
			else if (m.ordinal() > m_best.ordinal())
			{
				// Ltd_best:
				td_ambig = null;
				/* WTF assert((size_t)td.scope > 0x10000); */
				td_best = td;
				m_best = m;
				//tdargs.setDim(dedargs.dim);
				//memcpy(tdargs.data, dedargs.data, tdargs.dim * sizeof(void *));
				tdargs = new ArrayList<ASTDmdNode>(dedargs); // Looks like a shallow copy
				continue;
			}
			
			assert(m.ordinal() == m_best.ordinal());
			
			// Disambiguate by picking the most specialized TemplateDeclaration
			int c1 = 0; /* TODO td.leastAsSpecialized(td_best); */
			int c2 = 0; /* TODO td_best.leastAsSpecialized(td); */
	
			if (0 != c1 && 0 == c2)
			{
				// Ltd:
				td_ambig = null;
				continue;
			}
			else if (0 == c1 && 0 != c2)
			{
				// Ltd_best:
				td_ambig = null;
				/* WTF assert((size_t)td.scope > 0x10000); */
				td_best = td;
				m_best = m;
				tdargs = new ArrayList<ASTDmdNode>(dedargs);
				continue;
			}
			else
			{
				// Lambig:
				td_ambig = td;
				continue;
			}
	    }
	    
	    if (null == td_best)
	    {
			error("does not match any template declaration");
			Lerror(fargs, context);
	    }
	    if (null != td_ambig)
	    {
			error("%s matches more than one function template declaration, %s and %s",
				toChars(), td_best.toChars(), td_ambig.toChars());
	    }

	    /* The best match is td_best with arguments tdargs.
	     * Now instantiate the template.
	     */
	    /* WTF assert((size_t)td_best.scope > 0x10000); */
	    ti = null; /* TODO new TemplateInstance(loc, td_best, tdargs); */
	    ti.semantic(sc, context);
	    fd = ti.toAlias(context).isFuncDeclaration();
	    if (null == fd)
	    	Lerror(fargs, context);
	    return fd;
	}
	
	// Lerror:
	FuncDeclaration Lerror(List<Expression> fargs, SemanticContext context)
	{
		OutBuffer buf = new OutBuffer();
		HdrGenState hgs = new HdrGenState();

		argExpTypesToCBuffer(buf, fargs, hgs);
		error("cannot deduce template function from argument types (%s)",
			buf.toChars());
		return null;
	}

}
