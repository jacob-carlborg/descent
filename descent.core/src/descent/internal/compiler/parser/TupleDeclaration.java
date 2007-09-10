package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.STC.STCin;

public class TupleDeclaration extends Declaration {

	public Objects objects;
	public boolean isexp; // true: expression tuple
	public TypeTuple tupletype; // !=NULL if this is a type tuple

	public TupleDeclaration(Loc loc, IdentifierExp ident,
			Objects objects) {
		super(loc, ident);
		this.type = null;
		this.objects = objects;
		this.isexp = false;
		this.tupletype = null;
	}

	@Override
	public int getNodeType() {
		return TUPLE_DECLARATION;
	}

	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			melnorme.miscutil.Assert.failTODO(); // what is
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, objects);
		}
		visitor.endVisit(this);
	}

	@Override
	public Type getType() {
		/*
		 * If this tuple represents a type, return that type
		 */

		if (isexp) {
			return null;
		}
		if (tupletype == null) {
			/*
			 * It's only a type tuple if all the Object's are types
			 */
			for (int i = 0; i < objects.size(); i++) {
				ASTDmdNode o = objects.get(i);

				if (o.dyncast() != DYNCAST.DYNCAST_TYPE) {
					return null;
				}
			}

			/*
			 * We know it's a type tuple, so build the TypeTuple
			 */
			Arguments args = new Arguments(objects.size());
			for (int i = 0; i < objects.size(); i++) {
				Type t = (Type) objects.get(i);

				Argument arg = new Argument(STCin, t, null, null);
				args.set(i, arg);
			}

			tupletype = TypeTuple.newArguments(args);
		}

		return tupletype;
	}

	@Override
	public TupleDeclaration isTupleDeclaration() {
		return this;
	}

	@Override
	public String kind() {
		return "tuple";
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		Assert.isTrue(false);
		return null;
	}

	@Override
	public boolean needThis() {
		for (int i = 0; i < objects.size(); i++) {
			ASTDmdNode o = (ASTDmdNode) objects.get(i);
			if (o.dyncast() == DYNCAST.DYNCAST_EXPRESSION) {
				Expression e = (Expression) o;
				if (e.op == TOK.TOKdsymbol) {
					DsymbolExp ve = (DsymbolExp) e;
					Declaration d = ve.s.isDeclaration();
					if (d != null && d.needThis()) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
