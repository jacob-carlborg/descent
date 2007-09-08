package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class IftypeExp extends Expression {

	public Type targ;
	public IdentifierExp id;
	public TOK tok;
	public Type tspec;
	public TOK tok2;

	public IftypeExp(Loc loc, Type targ, IdentifierExp id, TOK tok, Type tspec, TOK tok2) {
		super(loc, TOK.TOKis);
		this.targ = targ;
		this.id = id;
		this.tok = tok;
		this.tspec = tspec;
		this.tok2 = tok2;		
	}
	
	@Override
	public int getNodeType() {
		return IFTYPE_EXP;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, targ);
			TreeVisitor.acceptChildren(visitor, id);
			TreeVisitor.acceptChildren(visitor, tspec);
		}
		visitor.endVisit(this);
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context)
	{
	 	Type tded = null;

	    //printf("IftypeExp.semantic()\n");
	    if (null != id && ((sc.flags & Scope.SCOPEstaticif) == 0))
	    	error("can only declare type aliases within static if conditionals");
	    
	    int errors_save = context.global.errors;
	    context.global.errors = 0;
	    context.global.gag++;			// suppress printing of error messages
	    targ = targ.semantic(loc, sc, context);
	    context.global.gag--;
	    int gerrors = context.global.errors;
	    context.global.errors = errors_save;
	    
	    
	    if(0 < gerrors)     // if any errors happened
	    {                   // then condition is false
	    	return new IntegerExp(Loc.ZERO, 0);
	    }
	    else if (tok2 != TOK.TOKreserved)
	    {
			switch (tok2)
			{
			    case TOKtypedef:
			    	if (targ.ty != TY.Ttypedef)
						return new IntegerExp(Loc.ZERO, 0); // goto Lno;
			    	tded = ((TypeTypedef) targ).sym.basetype;
					break;
	
			    case TOKstruct:
					if (targ.ty != TY.Tstruct)
						return new IntegerExp(Loc.ZERO, 0); // goto Lno;
					if (null != ((TypeStruct) targ).sym.isUnionDeclaration())
						return new IntegerExp(Loc.ZERO, 0); // goto Lno;
					tded = targ;
					break;
	
			    case TOKunion:
					if (targ.ty != TY.Tstruct)
						return new IntegerExp(Loc.ZERO, 0); // goto Lno;
					if (null == ((TypeStruct) targ).sym.isUnionDeclaration())
						return new IntegerExp(Loc.ZERO, 0); // goto Lno;
					tded = targ;
					break;
	
			    case TOKclass:
					if (targ.ty != TY.Tclass)
						return new IntegerExp(Loc.ZERO, 0); // goto Lno;
					if (null != ((TypeClass) targ).sym.isInterfaceDeclaration())
						return new IntegerExp(Loc.ZERO, 0); // goto Lno;
					tded = targ;
					break;
	
			    case TOKinterface:
					if (targ.ty != TY.Tclass)
						return new IntegerExp(Loc.ZERO, 0); // goto Lno;
					if (null == ((TypeClass) targ).sym.isInterfaceDeclaration())
						return new IntegerExp(Loc.ZERO, 0); // goto Lno;
					tded = targ;
					break;
	
			    case TOKsuper:
					// If class or interface, get the base class and interfaces
			    	if (targ.ty != TY.Tclass)
			    		return new IntegerExp(Loc.ZERO, 0); // goto Lno;
					else
					{   ClassDeclaration cd = ((TypeClass)targ).sym;
					    List<Argument> args = new ArrayList<Argument>(cd.baseclasses.size());
					    for(int i = 0; i < cd.baseclasses.size(); i++)
					    {
					    	BaseClass b = (BaseClass) cd.baseclasses.get(i);
					    	args.add(new Argument(STC.STCin, b.type, null, null));
					    }
					    tded = TypeTuple.newArguments(args);
					}
					break;
	
			    case TOKenum:
					if (targ.ty != TY.Tenum)
						return new IntegerExp(Loc.ZERO, 0); // goto Lno;
					tded = ((TypeEnum) targ).sym.memtype;
					break;
	
			    case TOKdelegate:
					if (targ.ty != TY.Tdelegate)
						return new IntegerExp(Loc.ZERO, 0); // goto Lno;
					tded = targ.next;	// the underlying function type
					break;
	
			    case TOKfunction:
			    {
			    	if (targ.ty != TY.Tfunction)
			    		return new IntegerExp(Loc.ZERO, 0); // goto Lno;
					tded = targ;
		
					/* Generate tuple from function parameter types.
					 */
					assert(tded.ty == TY.Tfunction);
					List<Argument> params = ((TypeFunction)tded).parameters;
					int dim = params.size();
					List<Argument> args = new ArrayList<Argument>(dim);
					for(int i = 0; i < dim; i++)
					{
						Argument arg = params.get(i);
					    assert(null != arg && null != arg.type);
					    args.add(new Argument(arg.storageClass, arg.type, null, null));
					}
					tded = TypeTuple.newArguments(args);
					break;
			    }
			    
			    case TOKreturn:
					/* Get the 'return type' for the function,
					 * delegate, or pointer to function.
					 */
					if (targ.ty == TY.Tfunction)
					    tded = targ.next;
					else if (targ.ty == TY.Tdelegate)
					    tded = targ.next.next;
					else if (targ.ty == TY.Tpointer &&
                             targ.next.ty == TY.Tfunction)
					    tded = targ.next.next;
					else
					    return new IntegerExp(Loc.ZERO, 0); // goto Lno;
					break;
		
			    default:
					assert(false);
			}
			
			return yes(tded, sc, context); // goto Lyes;
	    }
	    else if((null != id) && (null != tspec))
	    {
			/* Evaluate to TRUE if targ matches tspec.
			 * If TRUE, declare id as an alias for the specialized type.
			 */
			MATCH m;
			TemplateTypeParameter tp = 
				new TemplateTypeParameter(loc, id, null, null);
	
			List<TemplateParameter> parameters = new ArrayList<TemplateParameter>(1);
			parameters.add(tp);
	
			List<ASTDmdNode> dedtypes = new ArrayList<ASTDmdNode>(1);
	
			m = null; /* TODO semantic targ.deduceType(null, tspec, parameters, dedtypes); */
			if (m == MATCH.MATCHnomatch ||
			    (m != MATCH.MATCHexact && tok == TOK.TOKequal))
			{
				return new IntegerExp(Loc.ZERO, 0); // goto Lno;
			}
			else
			{
			    assert(dedtypes.size() == 1);
			    tded = (Type) dedtypes.get(0);
			    if(null == tded)
			    	tded = targ;
			    return yes(tded, sc, context); // goto Lyes;
			}
	    }
	    else if(null != id)
	    {
			/* Declare id as an alias for type targ. Evaluate to TRUE
			 */
			tded = targ;
			return yes(tded, sc, context); // goto Lyes;
	    }
	    else if (null != tspec)
	    {
			/* Evaluate to TRUE if targ matches tspec
			 */
			tspec = tspec.semantic(loc, sc, context);
			//printf("targ  = %s\n", targ.toChars());
			//printf("tspec = %s\n", tspec.toChars());
			if (tok == TOK.TOKcolon)
			{
				if (null != targ.implicitConvTo(tspec, context))
					return yes(tded, sc, context); // goto Lyes;
			    else
					return new IntegerExp(Loc.ZERO, 0); // goto Lno;
			}
			else /* == */
			{
				if (targ.equals(tspec))
					return yes(tded, sc, context); // goto Lyes;
			    else
					return new IntegerExp(Loc.ZERO, 0); // goto Lno;
			}
	    }
	    
	    return yes(tded, sc, context);
	}
	
	// Lyes:
	private Expression yes(Type tded, Scope sc, SemanticContext context)
	{
		if(null != id)
	    {
			Dsymbol s = new AliasDeclaration(loc, id, tded);
			s.semantic(sc, context);
			sc.insert(s);
			if(null != sc.sd)
			    s.addMember(sc, sc.sd, 1, context);
	    }
	    return new IntegerExp(Loc.ZERO, 1);
	}

	@Override
	public Expression syntaxCopy()
	{
		return new IftypeExp(loc, targ.syntaxCopy(), id, tok,
				null != tspec ? tspec.syntaxCopy() : null, tok2);
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context)
	{
		buf.writestring("is(");
		targ.toCBuffer(buf, id, hgs, context);
		if(null != tspec)
		{
			if(tok == TOK.TOKcolon)
				buf.writestring(" : ");
			else
				buf.writestring(" == ");
			tspec.toCBuffer(buf, null, hgs, context);
		}
		buf.writeByte(')');
	}
	
}
