package descent.internal.compiler.parser;

// DMD 1.020
public enum PREC {
	PREC_zero,
    PREC_expr,
    PREC_assign,
    PREC_cond,
    PREC_oror,
    PREC_andand,
    PREC_or,
    PREC_xor,
    PREC_and,
    PREC_equal,
    PREC_rel,
    PREC_shift,
    PREC_add,
    PREC_mul,
    PREC_unary,
    PREC_primary,
}
