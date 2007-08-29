package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class AliasDeclaration extends Declaration {

	public boolean first; // is this the first declaration in a multi
	public boolean last; // is this the last declaration in a multi
	public AliasDeclaration next;
	
	public Type type;
	public Type htype;
	public Dsymbol haliassym;
	public Dsymbol aliassym;
	public Dsymbol overnext; // next in overload list
	public int inSemantic;

	public AliasDeclaration(Loc loc, IdentifierExp id, Dsymbol s) {
		super(loc, id);

		Assert.isTrue(s != this);

		this.type = null;
		this.aliassym = s;
		this.htype = null;
		this.haliassym = null;
		this.overnext = null;
		this.inSemantic = 0;

		Assert.isNotNull(s);
	}

	public AliasDeclaration(Loc loc, IdentifierExp id, Type type) {
		super(loc, id);
		this.type = type;
		this.aliassym = null;
		this.htype = null;
		this.haliassym = null;
		this.overnext = null;
		this.inSemantic = 0;
	}

	@Override
	public int getNodeType() {
		return ALIAS_DECLARATION;
	}

	@Override
	public Type getType() {
		return type;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifiers);
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, ident);
		}
		visitor.endVisit(this);
	}

	@Override
	public AliasDeclaration isAliasDeclaration() {
		return this;
	}

	@Override
	public String kind() {
		return "alias";
	}
	
	@Override
	public boolean overloadInsert(Dsymbol s, SemanticContext context) {
		/*
		 * Don't know yet what the aliased symbol is, so assume it can be
		 * overloaded and check later for correctness.
		 */

		if (overnext == null) {
			overnext = s;
			return true;
		} else {
			return overnext.overloadInsert(s, context);
		}
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		// TODO semantic missing porting from 1.007 to 1.020
		
		// printf("AliasDeclaration::semantic() %s\n", toChars());
		if (aliassym != null) {
			if (aliassym.isTemplateInstance() != null) {
				aliassym.semantic(sc, context);
			}
			return;
		}
		this.inSemantic = 1;

		if ((storage_class & STC.STCconst) != 0) {
			// Signal better the error using the modifiers (HACK)
			if (modifiers != null) {
				for (Modifier modifier : modifiers) {
					if (modifier.tok == TOK.TOKconst) {
						context.acceptProblem(Problem.newSemanticTypeError(
								IProblem.AliasCannotBeConst, 0, modifier.start,
								modifier.length));
					}
				}
			}
		}

		storage_class |= sc.stc & STC.STCdeprecated;

		// Given:
		// alias foo.bar.abc def;
		// it is not knowable from the syntax whether this is an alias
		// for a type or an alias for a symbol. It is up to the semantic()
		// pass to distinguish.
		// If it is a type, then type is set and getType() will return that
		// type. If it is a symbol, then aliassym is set and type is NULL -
		// toAlias() will return aliasssym.

		Dsymbol s;

		if (type.ty == TY.Tident) {
			TypeIdentifier ti = (TypeIdentifier) type;

			s = ti.toDsymbol(sc, context);
			if (s != null) {
				// goto L2;
				semantic_L2(sc, context, s); // it's a symbolic alias
				return;
			}
		} else if (type.ty == TY.Tinstance) {
			// Handle forms like:
			// alias instance TFoo(int).bar.abc def;

			TypeInstance ti = (TypeInstance) type;

			s = ti.tempinst;
			if (s != null) {
				s.semantic(sc, context);
				s = s.toAlias(context);
				if (sc.parent.isFuncDeclaration() != null) {
					s.semantic2(sc, context);
				}

				for (IdentifierExp id : ti.idents) {
					s = s.search(loc, id, 0, context);
					if (s == null) { // failed to find a symbol
						semantic_L1(sc, context); // it must be a type
						return;
					}
					s = s.toAlias(context);
				}
				semantic_L2(sc, context, s); // it's a symbolic alias
				return;
			}
		}
		semantic_L1(sc, context);
	}
	
	public void semantic_L1(Scope sc, SemanticContext context) {
		if (overnext != null) {
			context.multiplyDefined(this, overnext);
		}
		type = type.semantic(loc, sc, context);
		this.inSemantic = 0;
		return;
	}

	public void semantic_L2(Scope sc, SemanticContext context, Dsymbol s) {
		Type tempType = type;
		type = null;
		VarDeclaration v = s.isVarDeclaration();
		if (v != null && v.linkage == LINK.LINKdefault) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.ForwardReference, 0,
					tempType.start, tempType.length, new String[] { tempType.toString() }));
			context.acceptProblem(Problem
					.newSemanticTypeError(
							IProblem.ForwardReference, 0, v.ident.start,
							v.ident.length, new String[] { new String(v.ident.ident) }));
			s = null;
		} else {
			FuncDeclaration f = s.isFuncDeclaration();
			if (f != null) {
				if (overnext != null) {
					FuncAliasDeclaration fa = new FuncAliasDeclaration(loc, f);
					if (!fa.overloadInsert(overnext, context)) {
						context.multiplyDefined(f, overnext);
					}
					overnext = null;
					s = fa;
					s.parent = sc.parent;
				}
			}
			if (overnext != null) {
				context.multiplyDefined(s, overnext);
			}
			if (s == this) {
				s = null;
			}
		}
		aliassym = s;
		this.inSemantic = 0;
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		Assert.isTrue(s == null);
		AliasDeclaration sa;
		if (type != null) {
			sa = new AliasDeclaration(loc, ident, type.syntaxCopy());
		} else {
			sa = new AliasDeclaration(loc, ident, aliassym.syntaxCopy(null));
		}
		// Syntax copy for header file
		if (htype == null) // Don't overwrite original
		{
			if (type != null) // Make copy for both old and new instances
			{
				htype = type.syntaxCopy();
				sa.htype = type.syntaxCopy();
			}
		} else {
			// Make copy of original for new instance
			sa.htype = htype.syntaxCopy();
		}
		if (haliassym == null) {
			if (aliassym != null) {
				haliassym = aliassym.syntaxCopy(s);
				sa.haliassym = aliassym.syntaxCopy(s);
			}
		} else {
			sa.haliassym = haliassym.syntaxCopy(s);
		}
		return sa;
	}

	@Override
	public Dsymbol toAlias(SemanticContext context) {
		Assert.isTrue(this != aliassym);
		if (inSemantic != 0) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.CircularDefinition,
					0, ident.start, ident.length, new String[] { "Circular alias declaration" }));
		}
		Dsymbol s = aliassym != null ? aliassym.toAlias(context) : this;
		return s;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.writestring("alias ");
		if (aliassym != null) {
			aliassym.toCBuffer(buf, hgs, context);
			buf.writeByte(' ');
			buf.writestring(ident.toChars());
		} else {
			type.toCBuffer(buf, ident, hgs);
		}
		buf.writeByte(';');
		buf.writenl();
	}

	@Override
	public String toString() {
		return "alias " + type + " " + ident + ";";
	}

}
