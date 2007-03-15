package descent.internal.compiler.parser;

public interface Id {
	
	Identifier IUnknown = new Identifier("IUnknown", TOK.TOKidentifier);
	Identifier Object = new Identifier("Object", TOK.TOKidentifier);
	Identifier object = new Identifier("object", TOK.TOKidentifier);
	Identifier max = new Identifier("max", TOK.TOKidentifier);
	Identifier min = new Identifier("min", TOK.TOKidentifier);
	Identifier This = new Identifier("this", TOK.TOKidentifier);
	Identifier ctor = new Identifier("_ctor", TOK.TOKidentifier);
	Identifier dtor = new Identifier("_dtor", TOK.TOKidentifier);
	Identifier classInvariant = new Identifier("__invariant", TOK.TOKidentifier);
	Identifier unitTest = new Identifier("_unitTest", TOK.TOKidentifier);
	Identifier staticCtor = new Identifier("_staticCtor", TOK.TOKidentifier);
	Identifier staticDtor = new Identifier("_staticDtor", TOK.TOKidentifier);
	Identifier init = new Identifier("init", TOK.TOKidentifier);
	Identifier size = new Identifier("size", TOK.TOKidentifier);
	Identifier __sizeof = new Identifier("sizeof", TOK.TOKidentifier);
	Identifier alignof = new Identifier("alignof", TOK.TOKidentifier);
	Identifier mangleof = new Identifier("mangleof", TOK.TOKidentifier);
	Identifier length = new Identifier("length", TOK.TOKidentifier);
	Identifier remove = new Identifier("remove", TOK.TOKidentifier);
	Identifier ptr = new Identifier("ptr", TOK.TOKidentifier);
	Identifier dollar = new Identifier("__dollar", TOK.TOKidentifier);
	Identifier offset = new Identifier("offset", TOK.TOKidentifier);
	Identifier offsetof = new Identifier("offsetof", TOK.TOKidentifier);
	Identifier ModuleInfo = new Identifier("ModuleInfo", TOK.TOKidentifier);
	Identifier ClassInfo = new Identifier("ClassInfo", TOK.TOKidentifier);
	Identifier classinfo = new Identifier("classinfo", TOK.TOKidentifier);
	Identifier typeinfo = new Identifier("typeinfo", TOK.TOKidentifier);
	Identifier Exception = new Identifier("Exception", TOK.TOKidentifier);
	Identifier withSym = new Identifier("__withSym", TOK.TOKidentifier);
	Identifier result = new Identifier("__result", TOK.TOKidentifier);
	Identifier returnLabel = new Identifier("__returnLabel", TOK.TOKidentifier);
	Identifier _delegate = new Identifier("delegate", TOK.TOKidentifier);
	Identifier line = new Identifier("line", TOK.TOKidentifier);
	Identifier empty = new Identifier("", TOK.TOKidentifier);
	Identifier p = new Identifier("p", TOK.TOKidentifier);
	Identifier coverage = new Identifier("__coverage", TOK.TOKidentifier);
	Identifier TypeInfo = new Identifier("TypeInfo", TOK.TOKidentifier);
	Identifier TypeInfo_Class = new Identifier("TypeInfo_Class", TOK.TOKidentifier);
	Identifier TypeInfo_Struct = new Identifier("TypeInfo_Struct", TOK.TOKidentifier);
	Identifier TypeInfo_Enum = new Identifier("TypeInfo_Enum", TOK.TOKidentifier);
	Identifier TypeInfo_Typedef = new Identifier("TypeInfo_Typedef", TOK.TOKidentifier);
	Identifier TypeInfo_Pointer = new Identifier("TypeInfo_Pointer", TOK.TOKidentifier);
	Identifier TypeInfo_Array = new Identifier("TypeInfo_Array", TOK.TOKidentifier);
	Identifier TypeInfo_StaticArray = new Identifier("TypeInfo_StaticArray", TOK.TOKidentifier);
	Identifier TypeInfo_AssociativeArray = new Identifier("TypeInfo_AssociativeArray", TOK.TOKidentifier);
	Identifier TypeInfo_Function = new Identifier("TypeInfo_Function", TOK.TOKidentifier);
	Identifier TypeInfo_Delegate = new Identifier("TypeInfo_Delegate", TOK.TOKidentifier);
	Identifier _arguments = new Identifier("_arguments", TOK.TOKidentifier);
	Identifier _argptr = new Identifier("_argptr", TOK.TOKidentifier);
	Identifier _match = new Identifier("_match", TOK.TOKidentifier);
	Identifier LINE = new Identifier("__LINE__", TOK.TOKidentifier);
	Identifier FILE = new Identifier("__FILE__", TOK.TOKidentifier);
	Identifier DATE = new Identifier("__DATE__", TOK.TOKidentifier);
	Identifier TIME = new Identifier("__TIME__", TOK.TOKidentifier);
	Identifier TIMESTAMP = new Identifier("__TIMESTAMP__", TOK.TOKidentifier);
	Identifier nan = new Identifier("nan", TOK.TOKidentifier);
	Identifier infinity = new Identifier("infinity", TOK.TOKidentifier);
	Identifier dig = new Identifier("dig", TOK.TOKidentifier);
	Identifier epsilon = new Identifier("epsilon", TOK.TOKidentifier);
	Identifier mant_dig = new Identifier("mant_dig", TOK.TOKidentifier);
	Identifier max_10_exp = new Identifier("max_10_exp", TOK.TOKidentifier);
	Identifier max_exp = new Identifier("max_exp", TOK.TOKidentifier);
	Identifier min_10_exp = new Identifier("min_10_exp", TOK.TOKidentifier);
	Identifier min_exp = new Identifier("min_exp", TOK.TOKidentifier);
	Identifier re = new Identifier("re", TOK.TOKidentifier);
	Identifier im = new Identifier("im", TOK.TOKidentifier);
	Identifier C = new Identifier("C", TOK.TOKidentifier);
	Identifier D = new Identifier("D", TOK.TOKidentifier);
	Identifier Windows = new Identifier("Windows", TOK.TOKidentifier);
	Identifier Pascal = new Identifier("Pascal", TOK.TOKidentifier);
	Identifier exit = new Identifier("exit", TOK.TOKidentifier);
	Identifier success = new Identifier("success", TOK.TOKidentifier);
	Identifier failure = new Identifier("failure", TOK.TOKidentifier);
	Identifier keys = new Identifier("keys", TOK.TOKidentifier);
	Identifier values = new Identifier("values", TOK.TOKidentifier);
	Identifier rehash = new Identifier("rehash", TOK.TOKidentifier);
	Identifier sort = new Identifier("sort", TOK.TOKidentifier);
	Identifier reverse = new Identifier("reverse", TOK.TOKidentifier);
	Identifier dup = new Identifier("dup", TOK.TOKidentifier);
	Identifier ___out = new Identifier("out", TOK.TOKidentifier);
	Identifier ___in = new Identifier("in", TOK.TOKidentifier);
	Identifier __int = new Identifier("int", TOK.TOKidentifier);
	Identifier __dollar = new Identifier("$", TOK.TOKidentifier);
	Identifier __LOCAL_SIZE = new Identifier("__LOCAL_SIZE", TOK.TOKidentifier);
	Identifier uadd = new Identifier("opPos", TOK.TOKidentifier);
	Identifier neg = new Identifier("opNeg", TOK.TOKidentifier);
	Identifier com = new Identifier("opCom", TOK.TOKidentifier);
	Identifier add = new Identifier("opAdd", TOK.TOKidentifier);
	Identifier add_r = new Identifier("opAdd_r", TOK.TOKidentifier);
	Identifier sub = new Identifier("opSub", TOK.TOKidentifier);
	Identifier sub_r = new Identifier("opSub_r", TOK.TOKidentifier);
	Identifier mul = new Identifier("opMul", TOK.TOKidentifier);
	Identifier mul_r = new Identifier("opMul_r", TOK.TOKidentifier);
	Identifier div = new Identifier("opDiv", TOK.TOKidentifier);
	Identifier div_r = new Identifier("opDiv_r", TOK.TOKidentifier);
	Identifier mod = new Identifier("opMod", TOK.TOKidentifier);
	Identifier mod_r = new Identifier("opMod_r", TOK.TOKidentifier);
	Identifier eq = new Identifier("opEquals", TOK.TOKidentifier);
	Identifier cmp = new Identifier("opCmp", TOK.TOKidentifier);
	Identifier iand = new Identifier("opAnd", TOK.TOKidentifier);
	Identifier iand_r = new Identifier("opAnd_r", TOK.TOKidentifier);
	Identifier ior = new Identifier("opOr", TOK.TOKidentifier);
	Identifier ior_r = new Identifier("opOr_r", TOK.TOKidentifier);
	Identifier ixor = new Identifier("opXor", TOK.TOKidentifier);
	Identifier ixor_r = new Identifier("opXor_r", TOK.TOKidentifier);
	Identifier shl = new Identifier("opShl", TOK.TOKidentifier);
	Identifier shl_r = new Identifier("opShl_r", TOK.TOKidentifier);
	Identifier shr = new Identifier("opShr", TOK.TOKidentifier);
	Identifier shr_r = new Identifier("opShr_r", TOK.TOKidentifier);
	Identifier ushr = new Identifier("opUShr", TOK.TOKidentifier);
	Identifier ushr_r = new Identifier("opUShr_r", TOK.TOKidentifier);
	Identifier cat = new Identifier("opCat", TOK.TOKidentifier);
	Identifier cat_r = new Identifier("opCat_r", TOK.TOKidentifier);
	Identifier addass = new Identifier("opAddAssign", TOK.TOKidentifier);
	Identifier subass = new Identifier("opSubAssign", TOK.TOKidentifier);
	Identifier mulass = new Identifier("opMulAssign", TOK.TOKidentifier);
	Identifier divass = new Identifier("opDivAssign", TOK.TOKidentifier);
	Identifier modass = new Identifier("opModAssign", TOK.TOKidentifier);
	Identifier andass = new Identifier("opAndAssign", TOK.TOKidentifier);
	Identifier orass = new Identifier("opOrAssign", TOK.TOKidentifier);
	Identifier xorass = new Identifier("opXorAssign", TOK.TOKidentifier);
	Identifier shlass = new Identifier("opShlAssign", TOK.TOKidentifier);
	Identifier shrass = new Identifier("opShrAssign", TOK.TOKidentifier);
	Identifier ushrass = new Identifier("opUShrAssign", TOK.TOKidentifier);
	Identifier catass = new Identifier("opCatAssign", TOK.TOKidentifier);
	Identifier postinc = new Identifier("opPostInc", TOK.TOKidentifier);
	Identifier postdec = new Identifier("opPostDec", TOK.TOKidentifier);
	Identifier index = new Identifier("opIndex", TOK.TOKidentifier);
	Identifier indexass = new Identifier("opIndexAssign", TOK.TOKidentifier);
	Identifier slice = new Identifier("opSlice", TOK.TOKidentifier);
	Identifier sliceass = new Identifier("opSliceAssign", TOK.TOKidentifier);
	Identifier call = new Identifier("opCall", TOK.TOKidentifier);
	Identifier _cast = new Identifier("opCast", TOK.TOKidentifier);
	Identifier match = new Identifier("opMatch", TOK.TOKidentifier);
	Identifier next = new Identifier("opNext", TOK.TOKidentifier);
	Identifier opIn = new Identifier("opIn", TOK.TOKidentifier);
	Identifier opIn_r = new Identifier("opIn_r", TOK.TOKidentifier);
	Identifier classNew = new Identifier("new", TOK.TOKidentifier);
	Identifier classDelete = new Identifier("delete", TOK.TOKidentifier);
	Identifier apply = new Identifier("opApply", TOK.TOKidentifier);
	Identifier lib = new Identifier("lib", TOK.TOKidentifier);
	Identifier msg = new Identifier("msg", TOK.TOKidentifier);
	Identifier GNU_asm = new Identifier("GNU_asm", TOK.TOKidentifier);
	Identifier tohash = new Identifier("toHash", TOK.TOKidentifier);
	Identifier alloca = new Identifier("alloca", TOK.TOKidentifier);
	Identifier toIdentifier = new Identifier("toIdentifier", TOK.TOKidentifier);
	
	Identifier[] VALUES = {
		IUnknown,
		Object,
		object,
		max,
		min,
		This,
		ctor,
		dtor,
		classInvariant,
		unitTest,
		staticCtor,
		staticDtor,
		init,
		size,
		__sizeof,
		alignof,
		mangleof,
		length,
		remove,
		ptr,
		dollar,
		offset,
		offsetof,
		ModuleInfo,
		ClassInfo,
		classinfo,
		typeinfo,
		Exception,
		withSym,
		result,
		returnLabel,
		_delegate,
		line,
		empty,
		p,
		coverage,
		TypeInfo,
		TypeInfo_Class,
		TypeInfo_Struct,
		TypeInfo_Enum,
		TypeInfo_Typedef,
		TypeInfo_Pointer,
		TypeInfo_Array,
		TypeInfo_StaticArray,
		TypeInfo_AssociativeArray,
		TypeInfo_Function,
		TypeInfo_Delegate,
		_arguments,
		_argptr,
		_match,
		LINE,
		FILE,
		DATE,
		TIME,
		TIMESTAMP,
		nan,
		infinity,
		dig,
		epsilon,
		mant_dig,
		max_10_exp,
		max_exp,
		min_10_exp,
		min_exp,
		re,
		im,
		C,
		D,
		Windows,
		Pascal,
		exit,
		success,
		failure,
		keys,
		values,
		rehash,
		sort,
		reverse,
		dup,
		___out,
		___in,
		__int,
		__dollar,
		__LOCAL_SIZE,
		uadd,
		neg,
		com,
		add,
		add_r,
		sub,
		sub_r,
		mul,
		mul_r,
		div,
		div_r,
		mod,
		mod_r,
		eq,
		cmp,
		iand,
		iand_r,
		ior,
		ior_r,
		ixor,
		ixor_r,
		shl,
		shl_r,
		shr,
		shr_r,
		ushr,
		ushr_r,
		cat,
		cat_r,
		addass,
		subass,
		mulass,
		divass,
		modass,
		andass,
		orass,
		xorass,
		shlass,
		shrass,
		ushrass,
		catass,
		postinc,
		postdec,
		index,
		indexass,
		slice,
		sliceass,
		call,
		_cast,
		match,
		next,
		opIn,
		opIn_r,
		classNew,
		classDelete,
		apply,
		lib,
		msg,
		GNU_asm,
		tohash,
		alloca,
		toIdentifier,
	};

}