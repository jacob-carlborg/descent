package descent.internal.compiler.parser;

import java.math.BigInteger;

public class IntegerExp extends Expression {

	public String str;
	public BigInteger value;
	
	public IntegerExp(String str, BigInteger value, Type type) {
		super(TOK.TOKint64);
		this.str = str;
		this.value = value;
		this.type = type;
	}
	
	@Override
	public BigInteger toInteger(SemanticContext context) {
		Type t;
		
	    t = type;
	    while (t != null)
	    {
		switch (t.ty)
		{
		    case Tbit:
		    case Tbool:		
		    	value = (value.compareTo(BigInteger.ZERO) != 0) ? BigInteger.ONE : BigInteger.ZERO;	
		    	break;
		    case Tint8:
		    case Tchar:
		    case Tuns8:
		    case Tint16:
		    case Twchar:
		    case Tuns16:
		    case Tint32:
		    case Tpointer:
		    case Tdchar:
		    case Tuns32:
		    case Tint64:
		    case Tuns64:
		    	value = cast(value, t.ty);	
		    	break;

		    case Tenum:
		    {
		    	TypeEnum te = (TypeEnum) t;
		    	t = te.sym.memtype;
		    	continue;
		    }

		    case Ttypedef:
		    {
		    	TypeTypedef tt = (TypeTypedef) t;
		    	t = tt.sym.basetype;
		    	continue;
		    }

		    default:
		    	throw new IllegalStateException();
		}
		break;
	    }
	    return value;
	}
	
	private BigInteger cast(BigInteger num, TY ty) {
		// TODO implement cast in BigInteger
		return num;
	}
	
	@Override
	public int kind() {
		return INTEGER_EXP;
	}

}
