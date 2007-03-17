package descent.internal.compiler.parser;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;

public class AliasDeclaration extends Declaration {

	public boolean last; // is this the last declaration in a multi declaration?
	public Type type;
	public Dsymbol aliassym;
	public Dsymbol overnext; // next in overload list
	public int inSemantic;

	public AliasDeclaration(IdentifierExp ident, Type type) {
		super(ident);
		this.type = type;
	}
	
	@Override
	public Type getType() {
		return type;
	}

	@Override
	public Dsymbol toAlias(SemanticContext context) {
		Assert.isTrue(this != aliassym);
		if (inSemantic != 0) {
			context.acceptProblem(Problem.newSemanticTypeError(
					"Circular alias declaration",
					IProblem.CircularDefinition, 0, ident.start, ident.length));
		}
		Dsymbol s = aliassym != null ? aliassym.toAlias(context) : this;
		return s;
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		// printf("AliasDeclaration::semantic() %s\n", toChars());
		if (aliassym != null) {
			if (aliassym.isTemplateInstance() != null)
				aliassym.semantic(sc, context);
			return;
		}
		this.inSemantic = 1;

		if ((storage_class & STC.STCconst) != 0) {
			// Signal better the error using the modifiers (HACK)
			if (modifiers != null) {
				for(Modifier modifier : modifiers) {
					if (modifier.tok == TOK.TOKconst) {
						context.acceptProblem(Problem.newSemanticTypeError(
								"alias cannot be const", IProblem.AliasCannotBeConst, 0, modifier.start,
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
				if (sc.parent.isFuncDeclaration() != null)
					s.semantic2(sc, context);

				for (IdentifierExp id : ti.idents) {
					s = s.search(id, 0, context);
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
		type = type.semantic(sc, context);
		this.inSemantic = 0;
		return;
	}

	public void semantic_L2(Scope sc, SemanticContext context, Dsymbol s) {
		Type tempType = type;
		type = null;
		VarDeclaration v = s.isVarDeclaration();
		if (v != null && v.linkage == LINK.LINKdefault) {
			context.acceptProblem(Problem.newSemanticTypeError(
					"Forward reference",
					IProblem.ForwardReference, 0, tempType.start, tempType.length));
			context.acceptProblem(Problem.newSemanticTypeError(
					v.ident + " is being forward referenced",
					IProblem.ForwardReference, 0, v.ident.start, v.ident.length));
			s = null;
		} else {
			FuncDeclaration f = s.isFuncDeclaration();
			if (f != null) {
				if (overnext != null) {
					FuncAliasDeclaration fa = new FuncAliasDeclaration(f);
					if (!fa.overloadInsert(overnext)) {
						context.multiplyDefined(f, overnext);
					}
					overnext = null;
					s = fa;
					s.parent = sc.parent;
				}
			}
			if (overnext != null)
				context.multiplyDefined(s, overnext);
			if (s == this) {
				s = null;
			}
		}
		aliassym = s;
		this.inSemantic = 0;
	}

	@Override
	public int kind() {
		return ALIAS_DECLARATION;
	}
	
	@Override
	public String toString() {
		return "alias " + type + " " + ident + ";";
	}

}
