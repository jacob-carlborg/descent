package descent.internal.compiler.parser;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;

public class TypedefDeclaration extends Declaration {

	public boolean last; // is this the last declaration in a multi
						 // declaration?
	public Type sourceBasetype; // copy of basetype, because it will change
	public Type basetype;
	public Type htype;
	public Type hbasetype;
	public Initializer init;
	public int sem; // 0: semantic() has not been run
					// 1: semantic() is in progress
					// 2: semantic() has been run
					// 3: semantic2() has been run
	public boolean inuse;

	public TypedefDeclaration(Loc loc, IdentifierExp id, Type basetype, Initializer init) {
		super(loc, id);
		this.type = new TypeTypedef(this);
		this.basetype = basetype;
		this.sourceBasetype = basetype;
		this.htype = null;
		this.hbasetype = null;
		this.init = init;
		this.sem = 0;
		this.inuse = false;
	}

	@Override
	public int getNodeType() {
		return TYPEDEF_DECLARATION;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public String kind() {
		return "typedef";
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		if (sem == 0) {
			sem = 1;
			basetype = basetype.semantic(loc, sc, context);
			sem = 2;
			type = type.semantic(loc, sc, context);
			if (sc.parent.isFuncDeclaration() != null && init != null) {
				semantic2(sc, context);
			}
		} else if (sem == 1) {
			context.acceptProblem(Problem.newSemanticTypeError(
					"Circular definition", IProblem.CircularDefinition, 0,
					ident.start, ident.length));
		}
	}

	@Override
	public void semantic2(Scope sc, SemanticContext context) {
		if (sem == 2) {
			sem = 3;
			if (init != null) {
				init = init.semantic(sc, basetype, context);

				ExpInitializer ie = init.isExpInitializer();
				if (ie != null) {
					if (ie.exp.type == basetype) {
						ie.exp.type = type;
					}
				}
			}
		}
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		Type basetype = this.basetype.syntaxCopy();

		Initializer init = null;
		if (this.init != null) {
			init = this.init.syntaxCopy();
		}

		Assert.isTrue(s == null);
		TypedefDeclaration st;
		st = new TypedefDeclaration(loc, ident, basetype, init);
		// Syntax copy for header file
		if (htype == null) // Don't overwrite original
		{
			if (type != null) // Make copy for both old and new instances
			{
				htype = type.syntaxCopy();
				st.htype = type.syntaxCopy();
			}
		} else {
			// Make copy of original for new instance
			st.htype = htype.syntaxCopy();
		}
		if (hbasetype == null) {
			if (basetype != null) {
				hbasetype = basetype.syntaxCopy();
				st.hbasetype = basetype.syntaxCopy();
			}
		} else {
			st.hbasetype = hbasetype.syntaxCopy();
		}
		return st;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.writestring("typedef ");
		basetype.toCBuffer(buf, ident, hgs);
		if (init != null) {
			buf.writestring(" = ");
			init.toCBuffer(buf, hgs);
		}
		buf.writeByte(';');
		buf.writenl();
	}

	@Override
	public String toString() {
		return "typedef " + basetype + " " + ident + ";";
	}

}