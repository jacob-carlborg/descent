package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;
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

	public Import(Loc loc, Identifiers packages, IdentifierExp id,
			IdentifierExp aliasId, boolean isstatic) {
		super(id);
		this.loc = loc;
		this.id = id;
		this.packages = packages;
		this.aliasId = aliasId;
		this.isstatic = isstatic;

		if (aliasId != null) {
			this.ident = aliasId;
		} else if (packages != null && packages.size() > 0) {
			this.ident = packages.get(0);
		}
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
			SemanticContext context) {
		int result = 0;

		if (size(names) == 0) {
			return super.addMember(sc, sd, memnum, context);
		}

		if (null != aliasId) {
			result = super.addMember(sc, sd, memnum, context);
		}

		for (int i = 0; i < size(names); i++) {
			IdentifierExp name = names.get(i);
			IdentifierExp alias = aliases.get(i);

			if (null == alias) {
				alias = name;
			}

			TypeIdentifier tname = new TypeIdentifier(loc, name);
			AliasDeclaration ad = new AliasDeclaration(loc, alias, tname);
			result |= ad.addMember(sc, sd, memnum, context);

			if (aliasdecls == null) {
				aliasdecls = new Dsymbols();
			}
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
	public String kind() {
		return isstatic ? "static import" : "import";
	}

	public void load(Scope sc, SemanticContext context) {
		DsymbolTable dst;
		Dsymbol s;

		// See if existing module
		Package[] ppkg = { pkg };
		dst = Package.resolve(packages, null, ppkg, context);
		pkg = ppkg[0];

		s = dst.lookup(id);
		if (null != s) {
			if (null != s.isModule()) {
				mod = (Module) s;
			} else {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.PackageAndModuleHaveTheSameName, 0, start,
						length));
			}
		}

		if (null == mod) {
			// Load module
			mod = Module.load(loc, packages, id, context);
			dst.insert(id, mod); // id may be different from mod->ident,
			// if so then insert alias

			if (null == mod.importedFrom) {
				mod.importedFrom = null != sc ? sc.module.importedFrom
						: context.Module_rootModule;
			}
		}

		if (null == pkg) {
			pkg = mod;
		}

		context.muteProblems++;
		mod.semantic(null, context);
		context.muteProblems--;
	}

	@Override
	public boolean overloadInsert(Dsymbol s, SemanticContext context) {
		// Allow multiple imports of the same name
		return s.isImport() != null;
	}

	@Override
	public Dsymbol search(Loc loc, char[] ident, int flags,
			SemanticContext context) {
		if (null == pkg) {
			load(null, context);
		}

		// Forward it to the package/module
		return pkg.search(loc, ident, flags, context);
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		load(sc, context);

		if (null != mod) {

			if (!isstatic && null == aliasId && 0 == size(names)) {
				/* Default to private importing
				 */
				PROT prot = sc.protection;
				if (sc.explicitProtection == 0) {
					prot = PROTprivate;
				}
				sc.scopesym.importScope(mod, prot);
			}

			// Modules need a list of each imported module
			if (sc.module.aimports == null) {
				sc.module.aimports = new Array();
			}
			sc.module.aimports.add(mod);

			if (mod.needmoduleinfo) {
				sc.module.needmoduleinfo = true;
			}

			sc = sc.push(mod);
			for (int i = 0; i < size(aliasdecls); i++) {
				Dsymbol s = (Dsymbol) aliasdecls.get(i);

				if (null == mod.search(loc, names.get(i), 0, context)) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.SomethingNotFound, 0, start,
							length, new String[] { (names.get(i)).toChars() }));
				}

				s.semantic(sc, context);
			}
			sc = sc.pop();
		}
	}

	@Override
	public void semantic2(Scope sc, SemanticContext context) {
		mod.semantic2(sc, context);
		if (mod.needmoduleinfo) {
			sc.module.needmoduleinfo = true;
		}
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		assert (null == s);

		Import si;
		si = new Import(loc, packages, id, aliasId, isstatic);

		for (int i = 0; i < names.size(); i++) {
			si.addAlias(names.get(i), aliases.get(i));
		}

		return si;
	}

	public Dsymbol toAlias() {
		if (null != aliasId) {
			return mod;
		}
		return this;
	}

	@Override
	public Dsymbol toAlias(SemanticContext context) {
		if (aliasId != null) {
			return mod;
		}
		return this;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		if (hgs.hdrgen && CharOperation.equals(id.ident, Id.object)) {
			return; // object is imported by default
		}

		if (isstatic) {
			buf.writestring("static ");
		}
		buf.writestring("import ");
		if (null != aliasId) {
			buf.printf(aliasId.toChars() + " = ");
		}
		if (null != packages && packages.size() > 0) {
			for (int i = 0; i < packages.size(); i++) {
				IdentifierExp pid = packages.get(i);
				buf.printf(new String(pid.ident) + ".");
			}
		}
		buf.printf(new String(id.ident) + ";");
		buf.writenl();
	}
	
	public char[] getFQN() {
		return getFQN(packages, id);
	}

}
