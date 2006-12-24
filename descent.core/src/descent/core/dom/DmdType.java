package descent.core.dom;

import descent.internal.core.parser.IDmdType;
import descent.internal.core.parser.StringTable;
import descent.internal.core.parser.TY;

/**
 * These are types that didn't make it to the AST, but are needed
 * in the parser.
 */
abstract class DmdType extends Type implements IDmdType {

	public static StringTable stringtable = new StringTable();
	public static char[] mangleChar;
	
	public TY ty;
	public Type next;
	
	public DmdType(AST ast, TY ty, Type next) {
		super(ast);
		this.ty = ty;
		this.next = next;
	}

	public int getTypeType() {
		return 0;
	}
	
	/********************************
	 * We've mistakenly parsed this as a type.
	 * Redo it as an Expression.
	 * NULL if cannot.
	 */
	public Expression toExpression() {
		return null;
	}
	
	public void setNext(Type type) {
		this.next = type;
	}
	
	public Type getNext() {
		return next;
	}
	
	public TY getTY() {
		return ty;
	}
	
	public Object getAdaptedType() {
		return this;
	}
	
}
