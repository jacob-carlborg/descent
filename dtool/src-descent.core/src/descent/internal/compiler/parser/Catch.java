package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
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
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, handler);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return CATCH;
	}

	public void semantic(Scope sc, SemanticContext context) {
		ScopeDsymbol sym;

		if (context.IN_GCC) {
			if (sc.tf != null) {
				error(loc, "cannot put catch statement inside finally block");
			}
		}

		sym = new ScopeDsymbol();
		sym.parent = sc.scopesym;
		sc = sc.push(sym);

		if (type == null) {
			type = new TypeIdentifier(Loc.ZERO, new IdentifierExp(Loc.ZERO,
					Id.Object));
		}
		type = type.semantic(loc, sc, context);
		if (type.toBasetype(context).isClassHandle() == null) {
			error("can only catch class objects, not '%s'", type
					.toChars(context));
		} else if (ident != null) {
			var = new VarDeclaration(loc, type, ident, null);
			var.parent = sc.parent;
			sc.insert(var);
		}
		handler = handler.semantic(sc, context);

		sc.pop();
	}

	public Catch syntaxCopy() {
		Catch c = new Catch(loc, (type != null ? type.syntaxCopy() : null),
				ident, (handler != null ? handler.syntaxCopy() : null));
		return c;
	}

	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("catch");
		if (type != null) {
			buf.writebyte('(');
			type.toCBuffer(buf, ident, hgs, context);
			buf.writebyte(')');
		}
		buf.writenl();
		buf.writebyte('{');
		buf.writenl();
		handler.toCBuffer(buf, hgs, context);
		buf.writebyte('}');
		buf.writenl();
	}

}
