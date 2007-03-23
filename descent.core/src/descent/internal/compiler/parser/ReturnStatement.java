package descent.internal.compiler.parser;

public class ReturnStatement extends Statement {

	public Expression exp;

	public ReturnStatement(Expression exp) {
		this.exp = exp;		
	}
	
	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		FuncDeclaration *fd = sc->parent->isFuncDeclaration();
	    Scope *scx = sc;
	    int implicit0 = 0;

	    if (sc->fes)
	    {
		// Find scope of function foreach is in
		for (; 1; scx = scx->enclosing)
		{
		    assert(scx);
		    if (scx->func != fd)
		    {	fd = scx->func;		// fd is now function enclosing foreach
			break;
		    }
		}
	    }

	    Type *tret = fd->type->next;
	    if (fd->tintro)
		tret = fd->tintro->next;
	    Type *tbret = NULL;

	    if (tret)
		tbret = tret->toBasetype();

	    // main() returns 0, even if it returns void
	    if (!exp && (!tbret || tbret->ty == Tvoid) && fd->isMain())
	    {	implicit0 = 1;
		exp = new IntegerExp(0);
	    }

	    if (sc->incontract || scx->incontract)
		error("return statements cannot be in contracts");
	    if (sc->tf || scx->tf)
		error("return statements cannot be in finally, scope(exit) or scope(success) bodies");

	    if (fd->isCtorDeclaration())
	    {
		// Constructors implicitly do:
		//	return this;
		if (exp && exp->op != TOKthis)
		    error("cannot return expression from constructor");
		exp = new ThisExp(0);
	    }

	    if (!exp)
		fd->nrvo_can = 0;

	    if (exp)
	    {
		fd->hasReturnExp |= 1;

		exp = exp->semantic(sc);
		exp = resolveProperties(sc, exp);
		exp = exp->optimize(WANTvalue);

		if (fd->nrvo_can && exp->op == TOKvar)
		{   VarExp *ve = (VarExp *)exp;
		    VarDeclaration *v = ve->var->isVarDeclaration();

		    if (!v || v->isOut())
			fd->nrvo_can = 0;
		    else if (fd->nrvo_var == NULL)
		    {	if (!v->isDataseg() && !v->isParameter() && v->toParent2() == fd)
			    fd->nrvo_var = v;
			else
			    fd->nrvo_can = 0;
		    }
		    else if (fd->nrvo_var != v)
			fd->nrvo_can = 0;
		}

		if (fd->returnLabel && tbret->ty != Tvoid)
		{
		}
		else if (fd->inferRetType)
		{
		    if (fd->type->next)
		    {
			if (!exp->type->equals(fd->type->next))
			    error("mismatched function return type inference of %s and %s",
				exp->type->toChars(), fd->type->next->toChars());
		    }
		    else
		    {
			fd->type->next = exp->type;
			fd->type = fd->type->semantic(loc, sc);
			if (!fd->tintro)
			{   tret = fd->type->next;
			    tbret = tret->toBasetype();
			}
		    }
		}
		else if (tbret->ty != Tvoid)
		{
		    exp = exp->implicitCastTo(sc, tret);
		}
	    }
	    else if (fd->inferRetType)
	    {
		if (fd->type->next)
		{
		    if (fd->type->next->ty != Tvoid)
			error("mismatched function return type inference of void and %s",
			    fd->type->next->toChars());
		}
		else
		{
		    fd->type->next = Type::tvoid;
		    fd->type = fd->type->semantic(loc, sc);
		    if (!fd->tintro)
		    {   tret = Type::tvoid;
			tbret = tret;
		    }
		}
	    }
	    else if (tbret->ty != Tvoid)	// if non-void return
		error("return expression expected");

	    if (sc->fes)
	    {
		Statement *s;

		if (exp && !implicit0)
		{
		    exp = exp->implicitCastTo(sc, tret);
		}
		if (!exp || exp->op == TOKint64 || exp->op == TOKfloat64 ||
		    exp->op == TOKimaginary80 || exp->op == TOKcomplex80 ||
		    exp->op == TOKthis || exp->op == TOKsuper || exp->op == TOKnull ||
		    exp->op == TOKstring)
		{
		    sc->fes->cases.push(this);
		    s = new ReturnStatement(0, new IntegerExp(sc->fes->cases.dim + 1));
		}
		else if (fd->type->next->toBasetype() == Type::tvoid)
		{
		    Statement *s1;
		    Statement *s2;

		    s = new ReturnStatement(0, NULL);
		    sc->fes->cases.push(s);

		    // Construct: { exp; return cases.dim + 1; }
		    s1 = new ExpStatement(loc, exp);
		    s2 = new ReturnStatement(0, new IntegerExp(sc->fes->cases.dim + 1));
		    s = new CompoundStatement(loc, s1, s2);
		}
		else
		{
		    VarExp *v;
		    Statement *s1;
		    Statement *s2;

		    // Construct: return vresult;
		    if (!fd->vresult)
		    {	VarDeclaration *v;

			v = new VarDeclaration(loc, tret, Id::result, NULL);
			v->noauto = 1;
			v->semantic(scx);
			if (!scx->insert(v))
			    assert(0);
			v->parent = fd;
			fd->vresult = v;
		    }

		    v = new VarExp(0, fd->vresult);
		    s = new ReturnStatement(0, v);
		    sc->fes->cases.push(s);

		    // Construct: { vresult = exp; return cases.dim + 1; }
		    v = new VarExp(0, fd->vresult);
		    exp = new AssignExp(loc, v, exp);
		    exp = exp->semantic(sc);
		    s1 = new ExpStatement(loc, exp);
		    s2 = new ReturnStatement(0, new IntegerExp(sc->fes->cases.dim + 1));
		    s = new CompoundStatement(loc, s1, s2);
		}
		return s;
	    }

	    if (exp)
	    {
		if (fd->returnLabel && tbret->ty != Tvoid)
		{
		    assert(fd->vresult);
		    VarExp *v = new VarExp(0, fd->vresult);

		    exp = new AssignExp(loc, v, exp);
		    exp = exp->semantic(sc);
		}
		//exp->dump(0);
		//exp->print();
		exp->checkEscape();
	    }

	    /* BUG: need to issue an error on:
	     *	this
	     *	{   if (x) return;
	     *	    super();
	     *	}
	     */

	    if (sc->callSuper & CSXany_ctor &&
		!(sc->callSuper & (CSXthis_ctor | CSXsuper_ctor)))
		error("return without calling constructor");

	    sc->callSuper |= CSXreturn;

	    // See if all returns are instead to be replaced with a goto returnLabel;
	    if (fd->returnLabel)
	    {
		GotoStatement *gs = new GotoStatement(loc, Id::returnLabel);

		gs->label = fd->returnLabel;
		if (exp)
		{   Statement *s;

		    s = new ExpStatement(0, exp);
		    return new CompoundStatement(loc, s, gs);
		}
		return gs;
	    }

	    if (exp && tbret->ty == Tvoid && !fd->isMain())
	    {   Statement *s;

		s = new ExpStatement(loc, exp);
		loc = 0;
		exp = NULL;
		return new CompoundStatement(loc, s, this);
	    }

	    return this;
	}
	
	@Override
	public int getNodeType() {
		return RETURN_STATEMENT;
	}

}
