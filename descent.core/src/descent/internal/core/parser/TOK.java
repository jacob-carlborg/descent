package descent.internal.core.parser;

/* Tokens:
(	)
[	]
{	}
<	>	<=	>=	==	!=	===	!==
<<	>>	<<=	>>=	>>>	>>>=
+	-	+=	-=
*	/	%	*=	/=	%=
&	| 	^	&=	|=	^=
=	!	~
++	--
.	->	:	,
?	&&	||
*/
public enum TOK {
	TOKreserved,

	// Other
	TOKlparen("("),	TOKrparen(")"),
	TOKlbracket("["),	TOKrbracket("]"),
	TOKlcurly("{"),	TOKrcurly("}"),
	TOKcolon(":"),	TOKneg("!"),
	TOKsemicolon(";"),	TOKdotdotdot("..."),
	TOKeof("EOF"),		TOKcast("cast"),
	TOKnull("null"),	TOKassert("assert"),
	TOKtrue("true"),	TOKfalse("false"),
	TOKarray,	TOKcall,
	TOKaddress,	TOKtypedot,
	TOKtype,	TOKthrow("throw"),
	TOKnew("new"),		TOKdelete("delete"),
	TOKstar,	TOKsymoff,
	TOKvar,		TOKdotvar,
	TOKdotti,	TOKdotexp,
	TOKdottype,	TOKslice,
	TOKarraylength,	TOKversion("version"),
	TOKmodule("module"),	TOKdollar,
	TOKtemplate("template"),	TOKdottd,
	TOKdeclaration,	TOKtypeof("typeof"),
	TOKpragma("pragma"),	TOKdsymbol,
	TOKtypeid("typeid"),	TOKuadd,
	TOKiftype("iftype"),	TOKremove,
	TOKnewanonclass, 
	TOKlinecomment, TOKdoclinecomment, TOKblockcomment, TOKdocblockcomment, TOKpluscomment, TOKdocpluscomment,
	TOKarrayliteral,

	// Operators
	TOKlt("<"),		TOKgt(">"),
	TOKle("<="),		TOKge(">="),
	TOKequal("=="),	TOKnotequal("!="),
	TOKidentity,	TOKnotidentity,
	TOKindex,	TOKis("is"),
	TOKtobool,

	// NCEG floating point compares
	// !<>=     <>    <>=    !>     !>=   !<     !<=   !<>
	TOKunord("!<>="),TOKlg,TOKleg,TOKule,TOKul,TOKuge,TOKug,TOKue,

	TOKshl("<<"),		TOKshr(">>"),
	TOKshlass("<<="),	TOKshrass(">>="),
	TOKushr(">>>"),	TOKushrass(">>>="),
	TOKcat("~"),		TOKcatass("~="),	// ~ ~=
	TOKadd("+"),		TOKmin("-"),		TOKaddass("+="),	TOKminass("-="),
	TOKmul("*"),		TOKdiv("/"),		TOKmod("%"),
	TOKmulass("*="),	TOKdivass("/="),	TOKmodass("%="),
	TOKand("&"),		TOKor("|"),		TOKxor("^"),
	TOKandass("&="),	TOKorass("|="),	TOKxorass("^="),
	TOKassign("="),	TOKnot("!"),		TOKtilde,
	TOKplusplus("++"),	TOKminusminus("--"),
	TOKdot("."),		TOKarrow("->"),	TOKcomma(","),
	TOKquestion("?"),	TOKandand("&&"),	TOKoror("||"),

	// Numeric literals
	TOKint32v, TOKuns32v,
	TOKint64v, TOKuns64v,
	TOKfloat32v, TOKfloat64v, TOKfloat80v,
	TOKimaginary32v, TOKimaginary64v, TOKimaginary80v,

	// Char constants
	TOKcharv, TOKwcharv, TOKdcharv,

	// Leaf operators
	TOKidentifier,	TOKstring,
	TOKthis("this"),	TOKsuper("super"),
	TOKhalt,	TOKtuple,

	// Basic types
	TOKvoid("void"),
	TOKint8("byte"), TOKuns8("ubyte"),
	TOKint16("short"), TOKuns16("ushort"),
	TOKint32("int"), TOKuns32("uint"),
	TOKint64("long"), TOKuns64("ulong"),
	TOKfloat32("float"), TOKfloat64("double"), TOKfloat80("real"),
	TOKimaginary32("ifloat"), TOKimaginary64("idouble"), TOKimaginary80("ireal"),
	TOKcomplex32("complex32"), TOKcomplex64("complex64"), TOKcomplex80("complex80"), // TODO
	TOKchar("char"), TOKwchar("wchar"), TOKdchar("dchar"), TOKbit("bit"), TOKbool("bool"),
	TOKcent, TOKucent,

	// Aggregates
	TOKstruct("struct"), TOKclass("class"), TOKinterface("interface"), TOKunion("union"), TOKenum("enum"), TOKimport("import"),
	TOKtypedef("typedef"), TOKalias("alias"), TOKoverride("override"), TOKdelegate("delegate"), TOKfunction("function"),
	TOKmixin("mixin"),

	TOKalign("align"), TOKextern("extern"), TOKprivate("private"), TOKprotected("protected"), TOKpublic("public"), TOKexport("export"),
	TOKstatic("static"), /*TOKvirtual,*/ TOKfinal("final"), TOKconst("const"), TOKabstract("abstract"), TOKvolatile("volatile"),
	TOKdebug("debug"), TOKdeprecated("deprecated"), TOKin("in"), TOKout("out"), TOKinout("inout"), TOKlazy("lazy"),
	TOKauto("auto"), TOKpackage("package"),

	// Statements
	TOKif("if"), TOKelse("else"), TOKwhile("while"), TOKfor("for"), TOKdo("do"), TOKswitch("switch"),
	TOKcase("case"), TOKdefault("default"), TOKbreak("break"), TOKcontinue("continue"), TOKwith("with"),
	TOKsynchronized("synchronized"), TOKreturn("return"), TOKgoto("goto"), TOKtry("try"), TOKcatch("catch"), TOKfinally("finally"),
	TOKasm("asm"), TOKforeach("foreach"), TOKforeach_reverse("foreach_reverse"),
	TOKscope("scope"),
	TOKon_scope_exit("exit"), TOKon_scope_failure("failure"), TOKon_scope_success("success"),

	// Contracts
	TOKbody("body"), TOKinvariant("invariant"),

	// Testing
	TOKunittest("unittest"),

	TOKMAX;
	
	private String value;
	
	TOK() {
		value = this.name();
	}
	
	TOK(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}
	
}
