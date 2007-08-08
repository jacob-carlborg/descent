package descent.internal.compiler.parser;

import java.util.List;

public class Argument extends ASTNode {
	
	public InOut inout;
	public Type type;
	public IdentifierExp ident;
	public Expression defaultArg;
	public Expression sourceDefaultArg;
	
	public Argument(InOut inout, Type type, IdentifierExp ident, Expression defaultArg) {
		this.inout = inout;
		if (type == null) {
			this.type = Type.terror;
		} else {
			this.type = type;
		}
		this.ident = ident;
		this.defaultArg = defaultArg;
		this.sourceDefaultArg = defaultArg;
	}
    
    public static int dim(List<Argument> args, SemanticContext context) {
    	int n = 0;
		if (args != null) {
			for (Argument arg : args) {
				Type t = arg.type.toBasetype(context);

				if (t.ty == TY.Ttuple) {
					TypeTuple tu = (TypeTuple) t;
					n += dim(tu.arguments, context);
				} else
					n++;
			}
		}
		return n;
    }
    
    public static Argument getNth(List<Argument> args, int nth, SemanticContext context) {
    	return getNth(args, nth, null, context);
    }
    
    public static Argument getNth(List<Argument> args, int nth, int[] pn, SemanticContext context) {
    	if (args == null)
			return null;

		int n = 0;
		for (Argument arg : args) {
			Type t = arg.type.toBasetype(context);

			if (t.ty == TY.Ttuple) {
				TypeTuple tu = (TypeTuple) t;
				arg = getNth(tu.arguments, nth - n, pn, context);
				if (arg != null) {
					return arg;
				}
			} else if (n == nth) {
				return arg;
			} else {
				n++;
			}
		}

		if (pn != null) {
			pn[0] += n;
		}
		return null;
    }
    
    @Override
    public int getNodeType() {
    	return ARGUMENT;
    }

	public Type isLazyArray() {
		// TODO semantic
		return null;
	}

}
