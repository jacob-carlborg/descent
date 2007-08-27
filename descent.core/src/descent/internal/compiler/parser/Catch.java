package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class Catch extends ASTDmdNode {

	public Loc loc;
	public Type type;
	public IdentifierExp ident;
	public VarDeclaration var;
	public Statement handler;

	public Catch(Loc loc, Type type, IdentifierExp id, Statement handler) {
		this.loc = loc;
		this.type = type;
		this.ident = id;
		this.handler = handler;
	}

	@Override
	public int getNodeType() {
		return CATCH;
	}

	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, handler);
		}
		visitor.endVisit(this);
	}

	public void semantic(Scope sc, SemanticContext context) {
		ScopeDsymbol sym;

		//printf("Catch::semantic(%s)\n", ident.toChars());

		/* TODO Gcc
		 #ifndef IN_GCC
		 if (sc.tf)
		 {
		 error(loc, "cannot put catch statement inside finally block");
		 }
		 #endif
		 */

		sym = new ScopeDsymbol();
		sym.parent = sc.scopesym;
		sc = sc.push(sym);

		if (type == null)
			type = new TypeIdentifier(Loc.ZERO, new IdentifierExp(Loc.ZERO,
					Id.Object));
		type = type.semantic(loc, sc, context);
		if (type.toBasetype(context).isClassHandle() == null)
			error("can only catch class objects, not '%s'", type.toChars());
		else if (ident != null) {
			var = new VarDeclaration(loc, type, ident, null);
			var.parent = sc.parent;
			sc.insert(var);
		}
		handler = handler.semantic(sc, context);

		sc.pop();
	}

}
