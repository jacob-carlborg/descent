package descent.internal.compiler.parser;

/**
 * A class to hold constant-folding functions used by the interpreter. The
 * functions that use these are in UnaExp and BinExp. In DMD, they are in the
 * file constfold.c. Arguably, these should be moved into their respective
 * classes so the code looks more like DMD's (or even inlined as anonymous
 * classes), but since they're all in one file in DMD, I put them all in one
 * file here.
 * 
 * @author Walter Bright, port by rofraser
 */

// DMD 1.020
public class Constfold
{
	
	/*
	TODO semantic:
	
	Expression *expType(Type *type, Expression *e);

	Expression *Neg(Type *type, Expression *e1);
	Expression *Com(Type *type, Expression *e1);
	Expression *Not(Type *type, Expression *e1);
	Expression *Bool(Type *type, Expression *e1);
	Expression *Cast(Type *type, Type *to, Expression *e1);
	Expression *ArrayLength(Type *type, Expression *e1);
	Expression *Ptr(Type *type, Expression *e1);
	
	Expression *Add(Type *type, Expression *e1, Expression *e2);
	Expression *Min(Type *type, Expression *e1, Expression *e2);
	Expression *Mul(Type *type, Expression *e1, Expression *e2);
	Expression *Div(Type *type, Expression *e1, Expression *e2);
	Expression *Mod(Type *type, Expression *e1, Expression *e2);
	Expression *Shl(Type *type, Expression *e1, Expression *e2);
	Expression *Shr(Type *type, Expression *e1, Expression *e2);
	Expression *Ushr(Type *type, Expression *e1, Expression *e2);
	Expression *And(Type *type, Expression *e1, Expression *e2);
	Expression *Or(Type *type, Expression *e1, Expression *e2);
	Expression *Xor(Type *type, Expression *e1, Expression *e2);
	Expression *Index(Type *type, Expression *e1, Expression *e2);
	Expression *Cat(Type *type, Expression *e1, Expression *e2);
	
	Expression *Equal(enum TOK op, Type *type, Expression *e1, Expression *e2);
	Expression *Cmp(enum TOK op, Type *type, Expression *e1, Expression *e2);
	Expression *Identity(enum TOK op, Type *type, Expression *e1, Expression *e2);
	
	Expression *Slice(Type *type, Expression *e1, Expression *lwr, Expression *upr);
	 */
	
}