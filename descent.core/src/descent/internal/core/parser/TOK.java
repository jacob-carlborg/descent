package descent.internal.core.parser;

import descent.core.compiler.ITerminalSymbols;

public enum TOK implements ITerminalSymbols {
	TOKreserved,

	// Other
	TOKlparen("(", TokenNameLPAREN), TOKrparen(")", TokenNameRPAREN), 
	TOKlbracket("[", TokenNameLBRACKET), TOKrbracket("]", TokenNameRBRACKET), 
	TOKlcurly("{", TokenNameLCURLY), TOKrcurly("}", TokenNameRCURLY), 
	TOKcolon(":", TokenNameCOLON), TOKneg("!", TokenNameNOT), 
	TOKsemicolon(";", TokenNameSEMICOLON), TOKdotdotdot("...", TokenNameDOT_DOT_DOT), 
	TOKeof("EOF", TokenNameEOF), TOKcast("cast", TokenNamecast), 
	TOKnull("null", TokenNamenull), TOKassert("assert", TokenNameassert), 
	TOKtrue("true", TokenNametrue), TOKfalse("false", TokenNamefalse), 
	//TOKarray, TOKcall, TOKaddress, TOKtypedot, TOKtype,
	TOKthrow("throw", TokenNamethrow), TOKnew("new", TokenNamenew), 
	TOKdelete("delete", TokenNamedelete), TOKstar("*", TokenNameMULTIPLY), 
	//TOKsymoff, TOKvar, TOKdotvar, TOKdotti, TOKdotexp, TOKdottype, 
	TOKslice("..", TokenNameDOT_DOT), 
	//TOKarraylength, 
	TOKversion("version", TokenNameversion), TOKmodule("module", TokenNamemodule), 
	TOKdollar("$", TokenNameDOLLAR), TOKtemplate("template", TokenNametemplate), 
	//TOKdottd, TOKdeclaration, 
	TOKtypeof("typeof", TokenNametypeof), TOKpragma("pragma", TokenNamepragma), 
	//TOKdsymbol, 
	TOKtypeid("typeid", TokenNametypeid), 
	//TOKuadd, 
	TOKiftype("iftype", TokenNameiftype), 
	//TOKremove, TOKnewanonclass, 
	TOKlinecomment, TOKdoclinecomment, TOKblockcomment, TOKdocblockcomment, TOKpluscomment, TOKdocpluscomment, 
	//TOKarrayliteral,

	// Operators
	TOKlt("<", TokenNameLESS), TOKgt(">", TokenNameGREATER), 
	TOKle("<=", TokenNameLESS_EQUAL), TOKge(">=", TokenNameGREATER_EQUAL), 
	TOKequal("==", TokenNameEQUAL_EQUAL), TOKnotequal("!=", TokenNameNOT_EQUAL), 
	TOKidentity("===", TokenNameEQUAL_EQUAL_EQUAL), TOKnotidentity("!==", TokenNameNOT_EQUAL_EQUAL), 
	//TOKindex, 
	TOKis("is", TokenNameis), 
	//TOKtobool,

	// NCEG floating point compares
	// !<>= <> <>= !> !>= !< !<= !<>
	TOKunord("!<>=", TokenNameNOT_LESS_GREATER_EQUAL), TOKlg("<>", TokenNameLESS_GREATER), 
	TOKleg("<>=", TokenNameLESS_GREATER_EQUAL), TOKule("!>", TokenNameNOT_GREATER), 
	TOKul("!>=", TokenNameNOT_GREATER_EQUAL), TOKuge("!<", TokenNameNOT_LESS), 
	TOKug("!<=", TokenNameNOT_LESS_EQUAL), TOKue("!<>", TokenNameNOT_LESS_GREATER),

	TOKshl("<<", TokenNameLEFT_SHIFT), TOKshr(">>", TokenNameRIGHT_SHIFT), 
	TOKshlass("<<=", TokenNameLEFT_SHIFT_EQUAL), TOKshrass(">>=", TokenNameRIGHT_SHIFT_EQUAL), 
	TOKushr(">>>", TokenNameUNSIGNED_RIGHT_SHIFT), TOKushrass(">>>=", TokenNameUNSIGNED_RIGHT_SHIFT_EQUAL), 
	//TOKcat("~", TokenNameTILDE), 
	TOKcatass("~=", TokenNameTILDE_EQUAL), // ~ ~=
	TOKadd("+", TokenNamePLUS), TOKmin("-", TokenNameMINUS), 
	TOKaddass("+=", TokenNamePLUS_EQUAL), TOKminass("-=", TokenNameMINUS_EQUAL), 
	TOKmul("*", TokenNameMULTIPLY), TOKdiv("/", TokenNameDIVIDE), 
	TOKmod("%", TokenNameREMAINDER), TOKmulass("*=", TokenNameMULTIPLY_EQUAL), 
	TOKdivass("/=", TokenNameDIVIDE_EQUAL), TOKmodass("%=", TokenNameREMAINDER_EQUAL), 
	TOKand("&", TokenNameAND), TOKor("|", TokenNameOR), 
	TOKxor("^", TokenNameXOR), TOKandass("&=", TokenNameAND_EQUAL), 
	TOKorass("|=", TokenNameOR_EQUAL), TOKxorass("^=", TokenNameXOR_EQUAL), 
	TOKassign("=", TokenNameEQUAL), TOKnot("!", TokenNameNOT), 
	TOKtilde("~", TokenNameTILDE), TOKplusplus("++", TokenNamePLUS_PLUS), 
	TOKminusminus("--", TokenNameMINUS_MINUS), TOKdot(".", TokenNameDOT), 
	//TOKarrow("->"), 
	TOKcomma(",", TokenNameCOMMA), TOKquestion("?", TokenNameQUESTION), 
	TOKandand("&&", TokenNameAND_AND), TOKoror("||", TokenNameOR_OR),

	// Numeric literals
	TOKint32v(TokenNameIntegerLiteral), TOKuns32v(TokenNameUnsignedIntegerLiteral), 
	TOKint64v(TokenNameLongLiteral), TOKuns64v(TokenNameUnsignedLongLiteral), 
	TOKfloat32v(TokenNameFloatLiteral), TOKfloat64v(TokenNameDoubleLiteral), 
	TOKfloat80v(TokenNameRealLiteral), TOKimaginary32v(TokenNameImaginaryFloatLiteral), 
	TOKimaginary64v(TokenNameImaginaryDoubleLiteral), TOKimaginary80v(TokenNameImaginaryRealLiteral),

	// Char constants
	TOKcharv(TokenNameCharacterLiteral), TOKwcharv(TokenNameWCharacterLiteral), 
	TOKdcharv(TokenNameDCharacterLiteral),

	// Leaf operators
	TOKidentifier(TokenNameIdentifier), TOKstring(TokenNameStringLiteral), 
	TOKthis("this", TokenNamethis), TOKsuper("super", TokenNamesuper), 
	//TOKhalt, TOKtuple,

	// Basic types
	TOKvoid("void", TokenNamevoid), TOKint8("byte", TokenNamebyte), 
	TOKuns8("ubyte", TokenNameubyte), TOKint16("short", TokenNameshort), 
	TOKuns16("ushort", TokenNameushort), TOKint32("int", TokenNameint), 
	TOKuns32("uint", TokenNameuint), TOKint64("long", TokenNamelong), 
	TOKuns64("ulong", TokenNameulong), TOKfloat32("float", TokenNamefloat), 
	TOKfloat64("double", TokenNamedouble), TOKfloat80("real", TokenNamereal), 
	TOKimaginary32("ifloat", TokenNameifloat), TOKimaginary64("idouble", TokenNameidouble), 
	TOKimaginary80("ireal", TokenNameireal), TOKcomplex32("cfloat", TokenNamecfloat), 
	TOKcomplex64("cdouble", TokenNamecdouble), TOKcomplex80("creal", TokenNamecreal), 
	TOKchar("char", TokenNamechar), TOKwchar("wchar", TokenNamewchar), 
	TOKdchar("dchar", TokenNamedchar), TOKbit("bit", TokenNamebit), 
	TOKbool("bool", TokenNamebool), TOKcent("cent", TokenNamecent), 
	TOKucent("ucent", TokenNameucent),

	// Aggregates
	TOKstruct("struct", TokenNamestruct), TOKclass("class", TokenNameclass), 
	TOKinterface("interface", TokenNameinterface), TOKunion("union", TokenNameunion), 
	TOKenum("enum", TokenNameenum), TOKimport("import", TokenNameimport), 
	TOKtypedef("typedef", TokenNametypedef), TOKalias("alias", TokenNamealias), 
	TOKoverride("override", TokenNameoverride), TOKdelegate("delegate", TokenNamedelegate), 
	TOKfunction("function", TokenNamefunction), TOKmixin("mixin", TokenNamemixin),

	TOKalign("align", TokenNamealign), TOKextern("extern", TokenNameextern), 
	TOKprivate("private", TokenNameprivate), TOKprotected("protected", TokenNameprotected), 
	TOKpublic("public", TokenNamepublic), TOKexport("export", TokenNameexport), 
	TOKstatic("static", TokenNamestatic), 
	// TOKvirtual,
	TOKfinal("final", TokenNamefinal), TOKconst("const", TokenNameconst), 
	TOKabstract("abstract", TokenNameabstract), TOKvolatile("volatile", TokenNamevolatile), 
	TOKdebug("debug", TokenNamedebug), TOKdeprecated("deprecated", TokenNamedeprecated), 
	TOKin("in", TokenNamein), TOKout("out", TokenNameout), 
	TOKinout("inout", TokenNameinout), TOKlazy("lazy", TokenNamelazy), 
	TOKauto("auto", TokenNameauto), TOKpackage("package", TokenNamepackage),

	// Statements
	TOKif("if", TokenNameif), TOKelse("else", TokenNameelse), 
	TOKwhile("while", TokenNamewhile), TOKfor("for", TokenNamefor), 
	TOKdo("do", TokenNamedo), TOKswitch("switch", TokenNameswitch), 
	TOKcase("case", TokenNamecase), TOKdefault("default", TokenNamedefault), 
	TOKbreak("break", TokenNamebreak), TOKcontinue("continue", TokenNamecontinue), 
	TOKwith("with", TokenNamewith), TOKsynchronized("synchronized", TokenNamesynchronized), 
	TOKreturn("return", TokenNamereturn), TOKgoto("goto", TokenNamegoto), 
	TOKtry("try", TokenNametry), TOKcatch("catch", TokenNamecatch), 
	TOKfinally("finally", TokenNamefinally), TOKasm("asm", TokenNameasm), 
	TOKforeach("foreach", TokenNameforeach), TOKforeach_reverse("foreach_reverse", TokenNameforeach_reverse), 
	TOKscope("scope", TokenNamescope), TOKon_scope_exit("exit", TokenNameexit), 
	TOKon_scope_failure("failure", TokenNamefailure), TOKon_scope_success("success", TokenNamesuccess),

	// Contracts
	TOKbody("body", TokenNamebody), TOKinvariant("invariant", TokenNameinvariant),

	// Testing
	TOKunittest("unittest", TokenNameunittest),

	//TOKMAX
	;

	public String value;

	public int terminalSymbol;

	TOK() {
		value = this.name();
	}
	
	TOK(int terminalSymbol) {
		this.terminalSymbol = terminalSymbol;
	}

	TOK(String value, int terminalSymbol) {
		this.value = value;
		this.terminalSymbol = terminalSymbol;
	}

	@Override
	public String toString() {
		return value;
	}

}
