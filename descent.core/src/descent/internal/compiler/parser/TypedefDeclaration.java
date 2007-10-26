package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class TypedefDeclaration extends Declaration {

	public boolean first = true; // is this the first declaration in a multi
	public TypedefDeclaration next;

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

	public TypedefDeclaration(Loc loc, IdentifierExp id, Type basetype,
			Initializer init) {
		super(id);
		this.loc = loc;
		this.type = new TypeTypedef(this);
		this.basetype = basetype;
		this.sourceBasetype = basetype;
		// TODO I don't have a context to pass
		// this.basetype = basetype.toBasetype(context);
		this.htype = null;
		this.hbasetype = null;
		this.init = init;
		this.sem = 0;
		this.inuse = false;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceBasetype);
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, init);
		}
		visitor.endVisit(this);
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
	public TypedefDeclaration isTypedefDeclaration() {
		return this;
	}

	@Override
	public String kind() {
		return "typedef";
	}

	@Override
	public String mangle(SemanticContext context) {
		return Dsymbol_mangle(context);
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
					IProblem.CircularDefinition, 0, ident.start, ident.length, new String[] { toChars(context) }));
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
					if (same(ie.exp.type, basetype)) {
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
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("typedef ");
		basetype.toCBuffer(buf, ident, hgs, context);
		if (init != null) {
			buf.writestring(" = ");
			init.toCBuffer(buf, hgs, context);
		}
		buf.writeByte(';');
		buf.writenl();
	}

}
