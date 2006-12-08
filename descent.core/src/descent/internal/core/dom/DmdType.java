package descent.internal.core.dom;

import descent.core.dom.IType;


public abstract class DmdType extends ASTNode implements IType {

	public static StringTable stringtable = new StringTable();
	public static char[] mangleChar;
	
	public TY ty;
	public DmdType next;
	public DmdType pto;		// merged pointer to this type
	public String deco;
	
	public DmdType(TY ty, DmdType next) {
		this.ty = ty;
		this.next = next;
	}

	public int getTypeType() {
		return 0;
	}
	
	public DmdType pointerTo()
	{
	    if (pto == null)
	    {	DmdType t;

		t = new TypePointer(this);
		pto = t.merge();
	    }
	    return pto;
	}
	
	public DmdType merge()
	{   DmdType t;

	    //printf("merge(%s)\n", toChars());
	    t = this;
	    if (deco == null)
	    {
		OutBuffer buf = new OutBuffer();
		StringValue sv;

		if (next != null)
		    next = next.merge();
		toDecoBuffer(buf);
		sv = stringtable.update(buf.data.toString());
		if (sv.ptrvalue != null)
		{   t = (DmdType) sv.ptrvalue;
		    //printf("old value, deco = '%s' %p\n", t.deco, t.deco);
		}
		else
		{
		    sv.ptrvalue = this;
		    deco = sv.lstring;
		    //printf("new value, deco = '%s' %p\n", t.deco, t.deco);
		}
	    }
	    return t;
	}

	public void toDecoBuffer(OutBuffer buf) {
		buf.writeByte('A');
		// TODO: buf.writeByte(mangleChar[ty.ordinal()]);
	    if (next != null)
	    {
		next.toDecoBuffer(buf);
	    }
	}
	
	/********************************
	 * We've mistakenly parsed this as a type.
	 * Redo it as an Expression.
	 * NULL if cannot.
	 */
	public Expression toExpression() {
		return null;
	}
	
	DmdType(AST ast) {
		super(ast);
	}
	
}
