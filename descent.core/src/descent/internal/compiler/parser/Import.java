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
	public Import isImport() {
		return this;
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
	
	@Override
	public Dsymbol toAlias(SemanticContext context) {
		if (aliasId != null)
			return mod;
		return this;
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
	public int getNodeType() {
		return IMPORT;
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
	
	public Dsymbol toAlias()
	{
		if (null != aliasId)
			return mod;
		return this;
	}
	
	public void load(Scope sc, SemanticContext context)
	{
		// TODO semantic: load imports (see how the JDT does it, implement
		// caching, etc, etc.)
	}

}
