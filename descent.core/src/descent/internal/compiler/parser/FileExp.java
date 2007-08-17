package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class FileExp extends UnaExp {

	public FileExp(Loc loc, Expression e) {
		super(loc, TOK.TOKmixin, e);
	}
	
	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.writestring("import(");
	    expToCBuffer(buf, hgs, e1, PREC.PREC_assign, context);
	    buf.writeByte(')');
	}
	
	@Override
	public int getNodeType() {
		return FILE_EXP;
	}
	
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, e1);
		}
		visitor.endVisit(this);
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context)
	{
		String name;
	    StringExp se;
	    
	    super.semantic(sc, context);
	    e1 = resolveProperties(sc, e1, context);
	    e1 = e1.optimize(WANTvalue);
	    if (e1.op != TOK.TOKstring)
	    {
	    	error("file name argument must be a string, not (%s)", e1.toChars());
			se = new StringExp(loc, "");
			return se.semantic(sc, context);
	    }
	    
	    StringExp e1_se = (StringExp) e1;
	    e1_se = e1_se.toUTF8(sc);
	    name = e1_se.string;
	    
	    /* RETHINK
	    if (!global.params.fileImppath)
	    {
	    	error("need -Jpath switch to import text file %s", name);
	    	se = new StringExp(loc, "");
	    	return se->semantic(sc);
	    }
	    
	    if (name != FileName.name(name))
	    {	error("use -Jpath switch to provide path for filename %s", name);
		se = new StringExp(loc, "");
		return se->semantic(sc);
	    }
	    
	    name = FileName.searchPath(global.filePath, name, 0);
	    if (!name)
	    {	error("file %s cannot be found, check -Jpath", se.toChars());
		se = new StringExp(loc, "");
		return se->semantic(sc);
	    }
	    
	    {	File f(name);
		if (f.read())
		{   error("cannot read file %s", f.toChars());
		    se = new StringExp(loc, "");
		return se->semantic(sc);
		}
		else
		{
		    f.ref = 1;
		    se = new StringExp(loc, f.buffer, f.len);
		}
	    }
	    */
	    
	    /* TODO file imoorts */
	    se = new StringExp(loc, "");
		return se.semantic(sc, context);
	}
}
