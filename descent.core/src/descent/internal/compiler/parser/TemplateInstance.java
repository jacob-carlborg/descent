package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.MATCH.MATCHnomatch;

import static descent.internal.compiler.parser.TOK.TOKfunction;
import static descent.internal.compiler.parser.TOK.TOKtuple;
import static descent.internal.compiler.parser.TOK.TOKvar;

import static descent.internal.compiler.parser.TY.Ttuple;

// DMD 1.020
public class TemplateInstance extends ScopeDsymbol {

	public Objects tiargs;
	public TemplateDeclaration tempdecl; // referenced by foo.bar.abc
	public TemplateInstance inst; // refer to existing instance
	public AliasDeclaration aliasdecl; // != null if instance is an alias for its
	public int semanticdone; // has semantic() been done?
	public WithScopeSymbol withsym;
	public IdentifierExp name;
	public ScopeDsymbol argsym; // argument symbol table
	public Objects tdtypes; // Array of Types/Expressions corresponding
	public int havetempdecl; // 1 if used second constructor
	Dsymbol isnested;	// if referencing local symbols, this is the context
	boolean nest; // For recursion detection

	// to TemplateDeclaration.parameters
	// [int, char, 100]

	public TemplateInstance(Loc loc, IdentifierExp id) {
		super(loc);
		this.name = id;
	}

	public TemplateInstance(Loc loc, TemplateDeclaration td, Objects tiargs) {
		super(null);
		this.loc = loc;
		this.name = td.ident;
		this.tiargs = tiargs;
		this.tempdecl = td;
		this.havetempdecl = 1;
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

	public Objects arraySyntaxCopy(Objects objs) {
		Objects a = null;
		if (objs != null) {
			a = new Objects();
			a.ensureCapacity(objs.size());
			for (int i = 0; i < objs.size(); i++) {
				Type ta = isType((ASTDmdNode) objs.get(i));
				if (ta != null)
					a.set(i, ta.syntaxCopy());
				else {
					Expression ea = isExpression((ASTDmdNode) objs.get(i));
					if (ea == null) {
						throw new IllegalStateException("assert(ea);");
					}
					a.set(i, ea.syntaxCopy());
				}
			}
		}
		return a;
	}

	public void declareParameters(Scope scope, SemanticContext context) {
		for (int i = 0; i < tdtypes.size(); i++) {
			TemplateParameter tp = (TemplateParameter) tempdecl.parameters
					.get(i);
			//Object o = (Object )tiargs.data[i];
			ASTDmdNode o = (ASTDmdNode) tdtypes.get(i);

			tempdecl.declareParameter(scope, tp, o, context);
		}
	}

	public TemplateDeclaration findBestMatch(Scope sc, SemanticContext context) {
		/* Since there can be multiple TemplateDeclaration's with the same
		 * name, look for the best match.
		 */
		TemplateDeclaration td_ambig = null;
		TemplateDeclaration td_best = null;
		MATCH m_best = MATCHnomatch;
		Objects dedtypes = new Objects();

		for (TemplateDeclaration td = tempdecl; td != null; td = td.overnext) {
			MATCH m;

			//	if (tiargs.dim) printf("2: tiargs.dim = %d, data[0] = %p\n", tiargs.dim, tiargs.data[0]);

			// If more arguments than parameters,
			// then this is no match.
			if (td.parameters.size() < tiargs.size()) {
				if (null == td.isVariadic())
					continue;
			}

			dedtypes.ensureCapacity(td.parameters.size());
			if (null == td.scope) {
				error("forward reference to template declaration %s", td
						.toChars(context));
				return null;
			}
			m = td.matchWithInstance(this, dedtypes, 0, context);
			if (null == m) // no match at all
				continue;

			if (m.ordinal() < m_best.ordinal()) {
				// goto Ltd_best;
				td_ambig = null;
				continue;
			}
			if (m.ordinal() > m_best.ordinal()) {
				// goto Ltd;
				td_ambig = null;
				td_best = td;
				m_best = m;
				tdtypes.ensureCapacity(dedtypes.size());
				for (ASTDmdNode a : dedtypes) {
					tdtypes.add(a);
				}
				continue;
			}
			{
				// Disambiguate by picking the most specialized TemplateDeclaration
				int c1 = td.leastAsSpecialized(td_best, context);
				int c2 = td_best.leastAsSpecialized(td, context);

				if (c1 != 0 && 0 == c2) {
					// goto Ltd;
					td_ambig = null;
					td_best = td;
					m_best = m;
					tdtypes.ensureCapacity(dedtypes.size());
					for (ASTDmdNode a : dedtypes) {
						tdtypes.add(a);
					}
					continue;
				} else if (0 == c1 && c2 != 0) {
					// goto Ltd_best;
					td_ambig = null;
					continue;
				} else {
					// goto Lambig;
					td_ambig = td;
					continue;
				}
			}
		}

		if (null == td_best) {
			error("%s does not match any template declaration",
					toChars(context));
			return null;
		}
		if (td_ambig != null) {
			error("%s matches more than one template declaration, %s and %s",
					toChars(context), td_best.toChars(context), td_ambig
							.toChars(context));
		}

		/* The best match is td_best
		 */
		tempdecl = td_best;
		return tempdecl;
	}

	public TemplateDeclaration findTemplateDeclaration(Scope sc,
			SemanticContext context)
	{
		//printf("TemplateInstance.findTemplateDeclaration() %s\n", toChars());
		if(null == tempdecl)
		{
			/* Given:
			 *    foo!( ... )
			 * figure out which TemplateDeclaration foo refers to.
			 */
			Dsymbol s;
			Dsymbol[] scopesym = new Dsymbol[] { null };
			IdentifierExp id;
			//int i;
			
			id = name;
			s = sc.search(loc, id, scopesym, context);
			if(null == s)
			{
				error("identifier '%s' is not defined", id.toChars());
				return null;
			}
			withsym = scopesym[0].isWithScopeSymbol();
			
			/* We might have found an alias within a template when
			 * we really want the template.
			 */
			TemplateInstance ti;
			if(null != s.parent && null != (ti = s.parent.isTemplateInstance()))
			{
				if((ti.name == id || ti.toAlias(context).ident == id) &&
						null != ti.tempdecl)
				{
					/* This is so that one can refer to the enclosing
					 * template, even if it has the same name as a member
					 * of the template, if it has a !(arguments)
					 */
					tempdecl = ti.tempdecl;
					if(null != tempdecl.overroot) // if not start of overloaded list of TemplateDeclaration's
						tempdecl = tempdecl.overroot; // then get the start
					s = tempdecl;
				}
			}
			
			s = s.toAlias(context);
			
			/* It should be a TemplateDeclaration, not some other symbol
			 */
			tempdecl = s.isTemplateDeclaration();
			if(null == tempdecl)
			{
				if(null == s.parent && context.global.errors > 0)
					return null;
				if(null == s.parent && null != s.getType())
				{
					Dsymbol s2 = s.getType().toDsymbol(sc, context);
					if(null == s2)
					{
						error("%s is not a template declaration, it is a %s",
								id.toChars(), s.kind());
						return null;
					}
					s = s2;
				}
				//assert(s.parent);
				TemplateInstance $ti = null != s.parent ? s.parent
						.isTemplateInstance() : null;
				if(null != $ti &&
						(CharOperation.equals($ti.name.ident, id.ident) || CharOperation
								.equals($ti.toAlias(context).ident.ident,
										id.ident)) && null != $ti.tempdecl)
				{
					/* This is so that one can refer to the enclosing
					 * template, even if it has the same name as a member
					 * of the template, if it has a !(arguments)
					 */
					tempdecl = $ti.tempdecl;
					if(null != tempdecl.overroot) // if not start of overloaded list of TemplateDeclara$tion's
						tempdecl = tempdecl.overroot; // then get the start
				}
				else
				{
					error("%s is not a template declaration, it is a %s", id
							.toChars(), s.kind());
					return null;
				}
			}
		}
		else
			assert (null != tempdecl.isTemplateDeclaration());
		return tempdecl;
	}

	public IdentifierExp genIdent(SemanticContext context) {
		OutBuffer buf = new OutBuffer();
		String id;
		Objects args;

		id = tempdecl.ident.toChars(context);
		// TODO semantic
		// buf.printf("__T%zu%s", id.length(), id);
		args = tiargs;
		for (int i = 0; i < args.size(); i++) {
			ASTDmdNode o = (ASTDmdNode) args.get(i);
			Type ta = isType(o);
			Expression ea = isExpression(o);
			Dsymbol sa = isDsymbol(o);
			Tuple va = isTuple(o);
			if (ta != null) {
				buf.writeByte('T');
				if (ta.deco != null)
					buf.writestring(ta.deco);
				else {
					if (context.global.errors == 0) {
						throw new IllegalStateException(
								"assert(context.global.errors);");
					}
				}
			} else if (ea != null) {
				sinteger_t v;
				real_t r;
				char p;

				if (ea.op == TOKvar) {
					sa = ((VarExp) ea).var;
					ea = null;
					// goto Lsa;
					buf.writeByte('S');
					Declaration d = sa.isDeclaration();
					if (d != null && null == d.type.deco)
						error("forward reference of %s", d.toChars(context));
					else {
						String p2 = sa.mangle(context);
						// TODO semantic
						// buf.printf("%zu%s", strlen(p2), p2);
					}
				}
				if (ea.op == TOKfunction) {
					sa = ((FuncExp) ea).fd;
					ea = null;
					// goto Lsa;
					buf.writeByte('S');
					Declaration d = sa.isDeclaration();
					if (d != null && null == d.type.deco)
						error("forward reference of %s", d.toChars(context));
					else {
						String p2 = sa.mangle(context);
						// TODO semantic
						// buf.printf("%zu%s", strlen(p2), p2);
					}
				}
				buf.writeByte('V');
				if (ea.op == TOKtuple) {
					ea.error("tuple is not a valid template value argument");
					continue;
				}
				buf.writestring(ea.type.deco);
				ea.toMangleBuffer(buf, context);
			} else if (sa != null) {
				// Lsa: 
				buf.writeByte('S');
				Declaration d = sa.isDeclaration();
				if (d != null && null == d.type.deco)
					error("forward reference of %s", d.toChars(context));
				else {
					String p = sa.mangle(context);
					// TODO semantic
					// buf.printf("%zu%s", strlen(p), p);
				}
			} else if (va != null) {
				assert (i + 1 == args.size()); // must be last one
				args = /* & */va.objects;
				i = -1;
			} else
				throw new IllegalStateException("assert(0);");
		}
		buf.writeByte('Z');
		id = buf.toChars();
		buf.data = null;
		return new IdentifierExp(id.toCharArray());
	}

	@Override
	public int getNodeType() {
		return TEMPLATE_INSTANCE;
	}

	@Override
	public AliasDeclaration isAliasDeclaration()
	{
		return aliasdecl;
	}

	public boolean isNested(Objects args, SemanticContext context)
	{
		boolean nested = false;
		//printf("TemplateInstance.isNested('%s')\n", tempdecl.ident.toChars());
		
		/* A nested instance happens when an argument references a local
		 * symbol that is on the stack.
		 */
		for(int i = 0; i < args.size(); i++)
		{
			ASTDmdNode o = (ASTDmdNode) args.get(i);
			Expression ea = isExpression(o);
			Dsymbol sa = isDsymbol(o);
			Tuple va = isTuple(o);
			if(null != ea || null != sa)
			{
				// if(ea) was the first condition in DMD's code, so we check
				// ea first.
				boolean gotoLsa = null == ea;
				if(!gotoLsa)
				{
					if(null != ea)
					{
						if(ea.op == TOKvar)
						{
							sa = ((VarExp) ea).var;
							gotoLsa = true;
						}
						if(ea.op == TOKfunction)
						{
							sa = ((FuncExp) ea).fd;
							gotoLsa = true;
						}
					}
				}
				
				// else if(null != sa)
				if(gotoLsa)
				{
					Declaration d = sa.isDeclaration();
					if(null != d &&
							!d.isDataseg(context) &&
							(null == d.isFuncDeclaration() || d
									.isFuncDeclaration().isNested()) &&
							null == isTemplateMixin())
					{
						// if module level template
						if(null != tempdecl.toParent().isModule())
						{
							if(null != isnested && isnested != d.toParent())
								error("inconsistent nesting levels %s and %s",
										isnested.toChars(context), d.toParent()
												.toChars(context));
							isnested = d.toParent();
							nested = true;
						}
						else
							error(
									"cannot use local '%s' as template parameter",
									d.toChars(context));
					}
				}
				
			}
			else if(null != va)
			{
				nested |= isNested(va.objects, context);
			}
		}
		return nested;
	}

	@Override
	public TemplateInstance isTemplateInstance() {
		return this;
	}

	@Override
	public String kind()
	{
		return "template instance";
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
		// TODO semantic this was %zu . what's that?
		buf.writestring(id.length());
		buf.writestring(id);
		id = buf.toChars();
		buf.data = null;
		return id;
	}

	@Override
	public boolean oneMember(Dsymbol[] ps, SemanticContext context)
	{
		ps[0] = null;
	    return true;
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context)
	{
		if(context.global.errors > 0)
		{
			if(0 == context.global.gag)
			{
				/* Trying to soldier on rarely generates useful messages
				 * at this point.
				 */
				fatal();
			}
			return;
		}
		
		if(null != inst) // if semantic() was already run
		{
			return;
		}
		
		if(semanticdone != 0)
		{
			error(loc, "recursive template expansion");
			//		inst = this;
			return;
		}
		semanticdone = 1;
		
		if(havetempdecl > 0)
		{
			// WTF assert((size_t)tempdecl.scope > 0x10000);
			// Deduce tdtypes
			tdtypes.setDim(tempdecl.parameters.size());
			if(MATCHnomatch == tempdecl.matchWithInstance(this, tdtypes, 0,
					context))
			{
				error("incompatible arguments for template instantiation");
				inst = this;
				return;
			}
		}
		else
		{
			// Run semantic on each argument, place results in tiargs[]
			semanticTiargs(sc, context);
			
			tempdecl = findTemplateDeclaration(sc, context);
			if(null != tempdecl)
				tempdecl = findBestMatch(sc, context);
			if(null == tempdecl || context.global.errors > 0)
			{
				inst = this;
				//printf("error return %p, %d\n", tempdecl, global.errors);
				return; // error recovery
			}
		}
		
		isNested(tiargs, context);
		
		/* See if there is an existing TemplateInstantiation that already
		 * implements the typeargs. If so, just refer to that one instead.
		 */

		L1: for(int i = 0; i < tempdecl.instances.size(); i++)
		{
			TemplateInstance ti = (TemplateInstance) tempdecl.instances.get(i);
			assert (tdtypes.size() == ti.tdtypes.size());
			
			// Nesting must match
			if(isnested != ti.isnested)
				continue;
			for(int j = 0; j < tdtypes.size(); j++)
			{
				Object o1 = (Object) tdtypes.get(j);
				Object o2 = (Object) ti.tdtypes.get(j);
				if(false /* TODO semantic !match(o1, o2, tempdecl, sc) */)
					continue L1; // goto L1;
			}
			
			// It's a match
			inst = ti;
			parent = ti.parent;
			return;
			
			//L1:
			//;
		}
		
		/* So, we need to implement 'this' instance.
		 */
		int errorsave = context.global.errors;
		inst = this;
		int tempdecl_instance_idx = tempdecl.instances.size();
		tempdecl.instances.add(this);
		parent = tempdecl.parent;
		//printf("parent = '%s'\n", parent.kind());
		
		ident = genIdent(context); // need an identifier for name mangling purposes.
		
		if(null != isnested)
			parent = isnested;
		//printf("parent = '%s'\n", parent.kind());
		
		// Add 'this' to the enclosing scope's members[] so the semantic routines
		// will get called on the instance members
		int dosemantic3 = 0;
		
		{
			List a = new ArrayList();
			int i;
			
			if(null != sc.scopesym && null != sc.scopesym.members &&
					null == sc.scopesym.isTemplateMixin())
			{
				//printf("\t1: adding to %s %s\n", sc.scopesym.kind(), sc.scopesym.toChars());
				a = sc.scopesym.members;
			}
			else
			{
				Module m = sc.module.importedFrom;
				//printf("\t2: adding to module %s\n", m.toChars());
				a = m.members;
				if(m.semanticdone >= 3)
					dosemantic3 = 1;
			}
			for(i = 0; true; i++)
			{
				if(i == a.size())
				{
					a.add(this);
					break;
				}
				if(this == (Dsymbol) a.get(i)) // if already in Array
					break;
			}
		}
		
		// Copy the syntax trees from the TemplateDeclaration
		members = Dsymbol.arraySyntaxCopy(tempdecl.members);
		
		// Create our own scope for the template parameters
		Scope scope = tempdecl.scope;
		if(null == scope)
		{
			error("forward reference to template declaration %s\n", tempdecl
					.toChars(context));
			return;
		}
		argsym = new ScopeDsymbol();
		argsym.parent = scope.parent;
		scope = scope.push(argsym);
		
		// Declare each template parameter as an alias for the argument type
		declareParameters(scope, context);
		
		// Add members of template instance to template instance symbol table
		//	    parent = scope.scopesym;
		symtab = new DsymbolTable();
		int memnum = 0;
		for(int i = 0; i < members.size(); i++)
		{
			Dsymbol s = (Dsymbol) members.get(i);
			memnum |= s.addMember(scope, this, memnum, context);
		}
		
		/* See if there is only one member of template instance, and that
		 * member has the same name as the template instance.
		 * If so, this template instance becomes an alias for that member.
		 */
		//printf("members.dim = %d\n", members.dim);
		if(members.size() > 0)
		{
			Dsymbol[] s = new Dsymbol[]
			{ null };
			if(Dsymbol.oneMembers(members, s, context) && null != s[0])
			{
				//printf("s.kind = '%s'\n", s.kind());
				//s.print();
				//printf("'%s', '%s'\n", s.ident.toChars(), tempdecl.ident.toChars());
				if(null != s[0].ident && s[0].ident.equals(tempdecl.ident))
				{
					//printf("setting aliasdecl\n");
					aliasdecl = new AliasDeclaration(loc, s[0].ident, s[0]);
				}
			}
		}
		
		// Do semantic() analysis on template instance members
		Scope sc2;
		sc2 = scope.push(this);
		//printf("isnested = %d, sc.parent = %s\n", isnested, sc.parent.toChars());
		sc2.parent = /*isnested ? sc.parent :*/this;
		
		for(int i = 0; i < members.size(); i++)
		{
			Dsymbol s = (Dsymbol) members.get(i);
			//printf("\t[%d] semantic on '%s' %p kind %s in '%s'\n", i, s.toChars(), s, s.kind(), this.toChars());
			//printf("test: isnested = %d, sc2.parent = %s\n", isnested, sc2.parent.toChars());
			//		if (isnested)
			//		    s.parent = sc.parent;
			//printf("test3: isnested = %d, s.parent = %s\n", isnested, s.parent.toChars());
			s.semantic(sc2, context);
			//printf("test4: isnested = %d, s.parent = %s\n", isnested, s.parent.toChars());
			sc2.module.runDeferredSemantic(context);
		}
		
		/* If any of the instantiation members didn't get semantic() run
		 * on them due to forward references, we cannot run semantic2()
		 * or semantic3() yet.
		 */
		boolean gotoLaftersemantic = false;
		for(int j = 0; j < 0/* TODO Module.deferred.size() */; j++)
		{
			Dsymbol sd = null;/* TODO (Dsymbol )Module.deferred.get(j); */
			
			if(sd.parent == this)
				gotoLaftersemantic = true;
		}
		
		/* The problem is when to parse the initializer for a variable.
		 * Perhaps VarDeclaration.semantic() should do it like it does
		 * for initializers inside a function.
		 */
		//	    if (sc.parent.isFuncDeclaration())
		/* BUG 782: this has problems if the classes this depends on
		 * are forward referenced. Find a way to defer semantic()
		 * on this template.
		 */
		if(!gotoLaftersemantic)
		{
			semantic2(sc2, context);
			
			if(null != sc.func || dosemantic3 > 0)
			{
				semantic3(sc2, context);
			}
		}
		
		//Laftersemantic:
		sc2.pop();
		
		scope.pop();
		
		// Give additional context info if error occurred during instantiation
		if(context.global.errors != errorsave)
		{
			error("error instantiating");
			/* errors = 1; TODO another reference to "errors" rather than global.errors */
			if(context.global.gag > 0)
				tempdecl.instances.remove(tempdecl_instance_idx);
		}
	}

	@Override
	public void semantic2(Scope sc, SemanticContext context)
	{
		int i;
		
		if(semanticdone >= 2)
			return;
		semanticdone = 2;
		
		if(context.global.errors == 0 // TODO this just says errors; I'm assuming it's global.errors
				&&
				null != members)
		{
			sc = tempdecl.scope;
			assert (null != sc);
			sc = sc.push(argsym);
			sc = sc.push(this);
			for(i = 0; i < members.size(); i++)
			{
				Dsymbol s = (Dsymbol) members.get(i);
				s.semantic2(sc, context);
			}
			sc = sc.pop();
			sc.pop();
		}
	}

	@Override
	public void semantic3(Scope sc, SemanticContext context)
	{
		int i;
		
		//if (toChars()[0] == 'D') *(char*)0=0;
		if(semanticdone >= 3)
			return;
		semanticdone = 3;
		if(0 == context.global.errors // TODO this just says errors; I'm assuming it's global.errors
				&&
				null != members)
		{
			sc = tempdecl.scope;
			sc = sc.push(argsym);
			sc = sc.push(this);
			for(i = 0; i < members.size(); i++)
			{
				Dsymbol s = (Dsymbol) members.get(i);
				s.semantic3(sc, context);
			}
			sc = sc.pop();
			sc.pop();
		}
	}

	public void semanticTiargs(Scope sc, SemanticContext context) {
		semanticTiargs(loc, sc, tiargs, context);
	}

	public Dsymbol syntaxCopy(Dsymbol s) {
		TemplateInstance ti;
		// int i;

		if (s != null)
			ti = (TemplateInstance) s;
		else
			ti = new TemplateInstance(loc, name);

		ti.tiargs = arraySyntaxCopy(tiargs);

		super.syntaxCopy(ti);
		return ti;
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
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context)
	{
		int i;
		
		IdentifierExp id = name;
		buf.writestring(id.toChars());
		buf.writestring("!(");
		if(nest)
			buf.writestring("...");
		else
		{
			nest = true;
			Objects args = tiargs;
			for(i = 0; i < args.size(); i++)
			{
				if(i > 0)
					buf.writeByte(',');
				ASTDmdNode oarg = (ASTDmdNode) args.get(i);
				ObjectToCBuffer(buf, hgs, oarg, context);
			}
			nest = false;
		}
		buf.writeByte(')');
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
	
    public static void semanticTiargs(Loc loc, Scope sc, Objects tiargs,
			SemanticContext context) {
		// Run semantic on each argument, place results in tiargs[]
		if (null == tiargs)
			return;
		for (int j = 0; j < tiargs.size(); j++) {
			ASTDmdNode o = (ASTDmdNode) tiargs.get(j);
			Type[] ta = { isType(o) };
			Expression[] ea = { isExpression(o) };
			Dsymbol[] sa = { isDsymbol(o) };

			if (ta != null) {
				// It might really be an Expression or an Alias
				ta[0].resolve(loc, sc, ea, ta, sa, context);
				if (ea != null) {
					ea[0] = ea[0].semantic(sc, context);
					ea[0] = ea[0].optimize(WANTvalue | WANTinterpret, context);
					tiargs.set(j, ea[0]);
				} else if (sa[0] != null) {
					tiargs.set(j, sa[0]);
					TupleDeclaration d = sa[0].toAlias(context)
							.isTupleDeclaration();
					if (d != null) {
						// int dim = d.objects.size();
						tiargs.remove(j);
						tiargs.addAll(j, d.objects);
						j--;
					}
				} else if (ta != null) {
					if (ta[0].ty == Ttuple) { // Expand tuple
						TypeTuple tt = (TypeTuple) ta[0];
						int dim = tt.arguments.size();
						tiargs.remove(j);
						if (dim != 0) {
							tiargs.ensureCapacity(dim);
							for (int i = 0; i < dim; i++) {
								Argument arg = (Argument) tt.arguments.get(i);
								tiargs.add(j + i, arg.type);
							}
						}
						j--;
					} else
						tiargs.add(j, ta[0]);
				} else {
					if (context.global.errors == 0) {
						throw new IllegalStateException(
								"assert(context.global.errors);");
					}
					tiargs.set(j, Type.terror);
				}
			} else if (ea[0] != null) {
				if (null == ea[0]) {
					if (context.global.errors == 0) {
						throw new IllegalStateException(
								"assert(context.global.errors);");
					}
					ea[0] = new IntegerExp(Loc.ZERO, 0);
				}
				if (ea[0] == null) {
					throw new IllegalStateException("assert(ea);");
				}
				ea[0] = ea[0].semantic(sc, context);
				ea[0] = ea[0].optimize(WANTvalue | WANTinterpret, context);
				tiargs.set(j, ea[0]);
			} else if (sa[0] != null) {
			} else {
				throw new IllegalStateException("assert (0);");
			}
		}
	}
    
	 // PERHAPS void inlineScan();
}