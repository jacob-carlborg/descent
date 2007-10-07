package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.PROT.PROTprivate;

// DMD 1.020
public class Import extends Dsymbol {
	
	public boolean first = true; // Is this the first import in a multi?
	public Import next;
	
	public Identifiers packages;
	public IdentifierExp id;
	public IdentifierExp aliasId;
	
	public Identifiers names;
	public Identifiers aliases;
	public Module mod;
	public Package pkg;
	public boolean isstatic;
	
	public int firstStart;
	public int lastLength;
	
	public descent.internal.compiler.parser.Array aliasdecls;
	
	public Import(Loc loc, Identifiers packages, IdentifierExp id, IdentifierExp aliasId, boolean isstatic) {
		super(loc);
		this.packages = packages;
		this.id = id;
		this.aliasId = aliasId;
		this.isstatic = isstatic;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, packages);
			TreeVisitor.acceptChildren(visitor, id);
			TreeVisitor.acceptChildren(visitor, aliasId);
			TreeVisitor.acceptChildren(visitor, names);
			TreeVisitor.acceptChildren(visitor, aliases);
		}
		visitor.endVisit(this);
	}
	
	public void addAlias(IdentifierExp name, IdentifierExp alias) {
		if (names == null) {
			names = new Identifiers();
			aliases = new Identifiers();
		}
		names.add(name);
		aliases.add(alias);
	}
	
	@Override
	public int addMember(Scope sc, ScopeDsymbol sd, int memnum,
			SemanticContext context)
	{
		int result = 0;

		if (names.size() == 0)
			return super.addMember(sc, sd, memnum, context);

		if (null != aliasId)
			result = super.addMember(sc, sd, memnum, context);

		for (int i = 0; i < names.size(); i++)
		{
			IdentifierExp name = names.get(i);
			IdentifierExp alias = aliases.get(i);

			if (null == alias)
				alias = name;

			TypeIdentifier tname = new TypeIdentifier(loc, name);
			AliasDeclaration ad = new AliasDeclaration(loc, alias, tname);
			result |= ad.addMember(sc, sd, memnum, context);

			aliasdecls.add(ad);
		}

		return result;
	}

	@Override
	public int getNodeType() {
		return IMPORT;
	}
	
	@Override
	public Import isImport() {
		return this;
	}

	@Override
	public String kind()
	{
		return isstatic ? "static import" : "import";
	}

	public void load(Scope sc, SemanticContext context)
	{
		//printf("Import.load('%s')\n", toChars());
		/* TODO I think if we implement our own caching scheme, we won't need
                this.
        
        DsymbolTable dst;
		Dsymbol s;
        
		// See if existing module
		dst = Package.resolve(packages, null, pkg);

		s = dst.lookup(id);
		if (null != s)
		{
			if (null != s.isModule())
				mod = (Module) s;
			else
				error("package and module have the same name");
		}
		*/

		if (null == mod)
		{
			// Load module
			StringBuffer fqn = new StringBuffer();
			for (IdentifierExp pack : packages)
			{
				fqn.append(pack.ident);
				fqn.append(".");
			}
			fqn.append(id.ident);
			mod = context.loadModule(fqn.toString());

			// dst.insert(id, mod); // id may be different from mod.ident, if so then insert alias
			// TODO I think this means we're just going to have to deal with aliases
			// in our caching scheme, but make sure to check
			
			
			if (null == mod.importedFrom)
				mod.importedFrom = null != sc ? sc.module.importedFrom : null /* TODO Module.rootModule */;
		}
		
		if (null == pkg)
			pkg = mod;
		
		mod.semantic(context); // PERHAPS depending on our caching schem, we may not need to do this

		//printf("-Import.load('%s'), pkg = %p\n", toChars(), pkg);
	}

	@Override
	public boolean overloadInsert(Dsymbol s, SemanticContext context)
	{
		// Allow multiple imports of the same name
	    return s.isImport() != null;
	}

	@Override
	public Dsymbol search(Loc loc, char[] ident, int flags,
			SemanticContext context)
	{
		if (null == pkg)
			load(null, context);

		// Forward it to the package/module
		return pkg.search(loc, ident, flags, context);
	}

	@Override
	public void semantic(Scope sc, SemanticContext context)
	{
		//printf("Import.semantic('%s')\n", toChars());

		load(sc, context);

		if (null != mod)
		{

			if (!isstatic && null == aliasId && names.isEmpty())
			{
				/* Default to private importing
				 */
				PROT prot = sc.protection;
				if (sc.explicitProtection == 0)
					prot = PROTprivate;
				sc.scopesym.importScope(mod, prot);
			}

			// Modules need a list of each imported module
			// RETHINK sc.module.aimports.push(mod);

			if (mod.needmoduleinfo)
				sc.module.needmoduleinfo = true;

			sc = sc.push(mod);
			for (int i = 0; i < aliasdecls.size(); i++)
			{
				Dsymbol s = (Dsymbol) aliasdecls.get(i);

				//printf("\tImport alias semantic('%s')\n", s.toChars());
				if (null == mod.search(loc, names.get(i), 0, context))
					error("%s not found", (names.get(i)).toChars());

				s.semantic(sc, context);
			}
			sc = sc.pop();
		}
	}

	@Override
	public void semantic2(Scope sc, SemanticContext context)
	{
	    mod.semantic2(sc, context);
		if (mod.needmoduleinfo)
			sc.module.needmoduleinfo = true;
	}
	
	@Override
	public Dsymbol syntaxCopy(Dsymbol s)
	{
		assert (null == s);

		Import si;
		si = new Import(loc, packages, id, aliasId, isstatic);

		for (int i = 0; i < names.size(); i++)
		{
			si.addAlias(names.get(i), aliases.get(i));
		}

		return si;
	}
	
	public Dsymbol toAlias()
	{
		if (null != aliasId)
			return mod;
		return this;
	}

	@Override
	public Dsymbol toAlias(SemanticContext context) {
		if (aliasId != null)
			return mod;
		return this;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context)
	{
		if (hgs.hdrgen && CharOperation.equals(id.ident, Id.object))
			return; // object is imported by default

		if (isstatic)
			buf.writestring("static ");
		buf.writestring("import ");
		if (null != aliasId)
		{
			buf.printf(aliasId.toChars() + " = ");
		}
		if (null != packages && packages.size() > 0)
		{
			for (int i = 0; i < packages.size(); i++)
			{
				IdentifierExp pid = (IdentifierExp) packages.get(i);
				buf.printf(new String(pid.ident) + ".");
			}
		}
		buf.printf(new String(id.ident) + ";");
		buf.writenl();
	}

}
