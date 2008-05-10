package descent.internal.compiler.parser;

import descent.core.compiler.CharOperation;


public interface Id {
	
	static final char[] IUnknown = { 'I', 'u', 'n', 'k', 'n', 'o', 'w', 'n' };
	static final char[] Object = { 'O', 'b', 'j', 'e', 'c', 't' };
	static final char[] object = { 'o', 'b', 'j', 'e', 'c', 't' }; // shared
	static final char[] max = { 'm', 'a', 'x' };
	static final char[] min = { 'm', 'i', 'n' };
	static final char[] This = { 't', 'h', 'i', 's' };
	static final char[] ctor = { '_', 'c', 't', 'o', 'r' };
	static final char[] dtor = { '_', 'd', 't', 'o', 'r' };
	static final char[] classInvariant = { '_', '_', 'i', 'n', 'v', 'a', 'r', 'i', 'a', 'n', 't' };
	static final char[] unitTest = { '_', 'u', 'n', 'i', 't', 'T', 'e', 's', 't' };
	static final char[] staticCtor = { '_', 's', 't', 'a', 't', 'i', 'c', 'C', 't', 'o', 'r' };
	static final char[] staticDtor = { '_', 's', 't', 'a', 't', 'i', 'c', 'D', 't', 'o', 'r' };
	static final char[] init = { 'i', 'n', 'i', 't' };
	static final char[] size = { 's', 'i', 'z', 'e' };
	static final char[] __sizeof = { 's', 'i', 'z', 'e', 'o', 'f' };
	static final char[] alignof = { 'a', 'l', 'i', 'g', 'n', 'o', 'f' };
	static final char[] mangleof = { 'm', 'a', 'n', 'g', 'l', 'e', 'o', 'f' };
	static final char[] stringof = { 's', 't', 'r', 'i', 'n', 'g', 'o', 'f' };
	static final char[] length = { 'l', 'e', 'n', 'g', 't', 'h' }; // shared
	static final char[] remove = { 'r', 'e', 'm', 'o', 'v', 'e' };
	static final char[] ptr = { 'p', 't', 'r' };
	static final char[] dollar = { '_', '_', 'd', 'o', 'l', 'l', 'a', 'r' };
	static final char[] offset = { 'o', 'f', 'f', 's', 'e', 't' };
	static final char[] offsetof = { 'o', 'f', 'f', 's', 'e', 't', 'o', 'f' };
	static final char[] ModuleInfo = { 'M', 'o', 'd', 'u', 'l', 'e', 'I', 'n', 'f', 'o' };
	static final char[] ClassInfo = { 'C', 'l', 'a', 's', 's', 'I', 'n', 'f', 'o' };
	static final char[] classinfo = { 'c', 'l', 'a', 's', 's', 'i', 'n', 'f', 'o' };
	static final char[] typeinfo = { 't', 'y', 'p', 'e', 'i', 'n', 'f', 'o' };
	static final char[] Exception = { 'E', 'x', 'c', 'e', 'p', 't', 'i', 'o', 'n' };
	static final char[] withSym = { '_', '_', 'w', 'i', 't', 'h', 'S', 'y', 'm' };
	static final char[] result = { '_', '_', 'r', 'e', 's', 'u', 'l', 't' };
	static final char[] returnLabel = { '_', '_', 'r', 'e', 't', 'u', 'r', 'n', 'L', 'a', 'b', 'e', 'l' };
	static final char[] _delegate = { 'd', 'e', 'l', 'e', 'g', 'a', 't', 'e' };
	static final char[] line = { 'l', 'i', 'n', 'e' };
	static final char[] empty = CharOperation.NO_CHAR;
	static final char[] p = { 'p' }; // shared
	static final char[] coverage = { '_', '_', 'c', 'o', 'v', 'e', 'r', 'a', 'g', 'e' };
	static final char[] TypeInfo = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o' };
	static final char[] TypeInfo_Class = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o', '_', 'C', 'l', 'a', 's', 's' };
	static final char[] TypeInfo_Struct = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o', '_', 'S', 't', 'r', 'u', 'c', 't' };
	static final char[] TypeInfo_Interface = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o', '_', 'I', 'n', 't', 'e', 'r', 'f', 'a', 'c', 'e' };
	static final char[] TypeInfo_Enum = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o', '_', 'E', 'n', 'u', 'm' };
	static final char[] TypeInfo_Typedef = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o', '_', 'T', 'y', 'p', 'e', 'd', 'e', 'f' };
	static final char[] TypeInfo_Pointer = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o', '_', 'P', 'o', 'i', 'n', 't', 'e', 'r' };
	static final char[] TypeInfo_Array = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o', '_', 'A', 'r', 'r', 'a', 'y' };
	static final char[] TypeInfo_StaticArray = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o', '_', 'S', 't', 'a', 't', 'i', 'c', 'A', 'r', 'r', 'a', 'y' };
	static final char[] TypeInfo_AssociativeArray = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o', '_', 'A', 's', 's', 'o', 'c', 'i', 'a', 't', 'i', 'v', 'e', 'A', 'r', 'r', 'a', 'y' };
	static final char[] TypeInfo_Function = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o', '_', 'F', 'u', 'n', 'c', 't', 'i', 'o', 'n' };
	static final char[] TypeInfo_Delegate = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o', '_', 'D', 'e', 'l', 'e', 'g', 'a', 't', 'e' };
	static final char[] TypeInfo_Tuple = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o', '_', 'T', 'u', 'p', 'l', 'e' };
	static final char[] TypeInfo_Const = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o', '_', 'C', 'o', 'n', 's', 't' };
	static final char[] TypeInfo_Invariant = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o', '_', 'I', 'n', 'v', 'a', 'r', 'i', 'a', 'n', 't' };
	static final char[] _arguments = { '_', 'a', 'r', 'g', 'u', 'm', 'e', 'n', 't', 's' };
	static final char[] _argptr = { '_', 'a', 'r', 'g', 'p', 't', 'r' };
	static final char[] _match = { '_', 'm', 'a', 't', 'c', 'h' };
	static final char[] LINE = { '_', '_', 'L', 'I', 'N', 'E', '_', '_' };
	static final char[] FILE = { '_', '_', 'F', 'I', 'L', 'E', '_', '_' };
	static final char[] DATE = { '_', '_', 'D', 'A', 'T', 'E', '_', '_' };
	static final char[] TIME = { '_', '_', 'T', 'I', 'M', 'E', '_', '_' };
	static final char[] TIMESTAMP = { '_', '_', 'T', 'I', 'M', 'E', 'S', 'T', 'A', 'M', 'P', '_', '_' };
	static final char[] VENDOR = { '_', '_', 'V', 'E', 'N', 'D', 'O', 'R', '_', '_' };
	static final char[] VERSION = { '_', '_', 'V', 'E', 'R', 'S', 'I', 'O', 'N', '_', '_' };
	static final char[] EOFX = { '_', '_', 'E', 'O', 'F', '_', '_' };
	static final char[] nan = { 'n', 'a', 'n' };
	static final char[] infinity = { 'i', 'n', 'f', 'i', 'n', 'i', 't', 'y' };
	static final char[] dig = { 'd', 'i', 'g' };
	static final char[] epsilon = { 'e', 'p', 's', 'i', 'l', 'o', 'n' };
	static final char[] mant_dig = { 'm', 'a', 'n', 't', '_', 'd', 'i', 'g' };
	static final char[] max_10_exp = { 'm', 'a', 'x', '_', '1', '0', '_', 'e', 'x', 'p' };
	static final char[] max_exp = { 'm', 'a', 'x', '_', 'e', 'x', 'p' };
	static final char[] min_10_exp = { 'm', 'i', 'n', '_', '1', '0', '_', 'e', 'x', 'p' };
	static final char[] min_exp = { 'm', 'i', 'n', '_', 'e', 'x', 'p' };
	static final char[] re = { 'r', 'e' };
	static final char[] im = { 'i', 'm' };
	static final char[] C = { 'C' }; // shared
	static final char[] D = { 'D' }; // shared
	static final char[] Windows = { 'W', 'i', 'n', 'd', 'o', 'w', 's' }; // shared
	static final char[] Pascal = { 'P', 'a', 's', 'c', 'a', 'l' }; // shared
	static final char[] exit = { 'e', 'x', 'i', 't' }; // shared
	static final char[] success = { 's', 'u', 'c', 'c', 'e', 's', 's' }; // shared
	static final char[] failure = { 'f', 'a', 'i', 'l', 'u', 'r', 'e' }; // shared
	static final char[] keys = { 'k', 'e', 'y', 's' };
	static final char[] values = { 'v', 'a', 'l', 'u', 'e', 's' };
	static final char[] rehash = { 'r', 'e', 'h', 'a', 's', 'h' };
	static final char[] sort = { 's', 'o', 'r', 't' };
	static final char[] reverse = { 'r', 'e', 'v', 'e', 'r', 's', 'e' };
	static final char[] dup = { 'd', 'u', 'p' };
	static final char[] ___out = { 'o', 'u', 't' };
	static final char[] ___in = { 'i', 'n' };
	static final char[] __int = { 'i', 'n', 't' };
	static final char[] __dollar = { '$' };
	static final char[] __LOCAL_SIZE = { '_', '_', 'L', 'O', 'C', 'A', 'L', '_', 'S', 'I', 'Z', 'E' };
	static final char[] uadd = { 'o', 'p', 'P', 'o', 's' };
	static final char[] neg = { 'o', 'p', 'N', 'e', 'g' };
	static final char[] com = { 'o', 'p', 'C', 'o', 'm' };
	static final char[] add = { 'o', 'p', 'A', 'd', 'd' };
	static final char[] add_r = { 'o', 'p', 'A', 'd', 'd', '_', 'r' };
	static final char[] sub = { 'o', 'p', 'S', 'u', 'b' };
	static final char[] sub_r = { 'o', 'p', 'S', 'u', 'b', '_', 'r' };
	static final char[] mul = { 'o', 'p', 'M', 'u', 'l' };
	static final char[] mul_r = { 'o', 'p', 'M', 'u', 'l', '_', 'r' };
	static final char[] div = { 'o', 'p', 'D', 'i', 'v' };
	static final char[] div_r = { 'o', 'p', 'D', 'i', 'v', '_', 'r' };
	static final char[] mod = { 'o', 'p', 'M', 'o', 'd' };
	static final char[] mod_r = { 'o', 'p', 'M', 'o', 'd', '_', 'r' };
	static final char[] eq = { 'o', 'p', 'E', 'q', 'u', 'a', 'l', 's' };
	static final char[] cmp = { 'o', 'p', 'C', 'm', 'p' };
	static final char[] iand = { 'o', 'p', 'A', 'n', 'd' };
	static final char[] iand_r = { 'o', 'p', 'A', 'n', 'd', '_', 'r' };
	static final char[] ior = { 'o', 'p', 'O', 'r' };
	static final char[] ior_r = { 'o', 'p', 'O', 'r', '_', 'r' };
	static final char[] ixor = { 'o', 'p', 'X', 'o', 'r' };
	static final char[] ixor_r = { 'o', 'p', 'X', 'o', 'r', '_', 'r' };
	static final char[] shl = { 'o', 'p', 'S', 'h', 'l' };
	static final char[] shl_r = { 'o', 'p', 'S', 'h', 'l', '_', 'r' };
	static final char[] shr = { 'o', 'p', 'S', 'h', 'r' };
	static final char[] shr_r = { 'o', 'p', 'S', 'h', 'r', '_', 'r' };
	static final char[] ushr = { 'o', 'p', 'U', 'S', 'h', 'r' };
	static final char[] ushr_r = { 'o', 'p', 'U', 'S', 'h', 'r', '_', 'r' };
	static final char[] cat = { 'o', 'p', 'C', 'a', 't' };
	static final char[] cat_r = { 'o', 'p', 'C', 'a', 't', '_', 'r' };
	static final char[] assign = { 'o', 'p', 'A', 's', 's', 'i', 'g', 'n' };
	static final char[] addass = { 'o', 'p', 'A', 'd', 'd', 'A', 's', 's', 'i', 'g', 'n' };
	static final char[] subass = { 'o', 'p', 'S', 'u', 'b', 'A', 's', 's', 'i', 'g', 'n' };
	static final char[] mulass = { 'o', 'p', 'M', 'u', 'l', 'A', 's', 's', 'i', 'g', 'n' };
	static final char[] divass = { 'o', 'p', 'D', 'i', 'v', 'A', 's', 's', 'i', 'g', 'n' };
	static final char[] modass = { 'o', 'p', 'M', 'o', 'd', 'A', 's', 's', 'i', 'g', 'n' };
	static final char[] andass = { 'o', 'p', 'A', 'n', 'd', 'A', 's', 's', 'i', 'g', 'n' };
	static final char[] orass = { 'o', 'p', 'O', 'r', 'A', 's', 's', 'i', 'g', 'n' };
	static final char[] xorass = { 'o', 'p', 'X', 'o', 'r', 'A', 's', 's', 'i', 'g', 'n' };
	static final char[] shlass = { 'o', 'p', 'S', 'h', 'l', 'A', 's', 's', 'i', 'g', 'n' };
	static final char[] shrass = { 'o', 'p', 'S', 'h', 'r', 'A', 's', 's', 'i', 'g', 'n' };
	static final char[] ushrass = { 'o', 'p', 'U', 'S', 'h', 'r', 'A', 's', 's', 'i', 'g', 'n' };
	static final char[] catass = { 'o', 'p', 'C', 'a', 't', 'A', 's', 's', 'i', 'g', 'n' };
	static final char[] postinc = { 'o', 'p', 'P', 'o', 's', 't', 'I', 'n', 'c' };
	static final char[] postdec = { 'o', 'p', 'P', 'o', 's', 't', 'D', 'e', 'c' };
	static final char[] index = { 'o', 'p', 'I', 'n', 'd', 'e', 'x' };
	static final char[] indexass = { 'o', 'p', 'I', 'n', 'd', 'e', 'x', 'A', 's', 's', 'i', 'g', 'n' };
	static final char[] slice = { 'o', 'p', 'S', 'l', 'i', 'c', 'e' };
	static final char[] sliceass = { 'o', 'p', 'S', 'l', 'i', 'c', 'e', 'A', 's', 's', 'i', 'g', 'n' };
	static final char[] call = { 'o', 'p', 'C', 'a', 'l', 'l' };
	static final char[] _cast = { 'o', 'p', 'C', 'a', 's', 't' };
	static final char[] match = { 'o', 'p', 'M', 'a', 't', 'c', 'h' };
	static final char[] next = { 'o', 'p', 'N', 'e', 'x', 't' };
	static final char[] opIn = { 'o', 'p', 'I', 'n' };
	static final char[] opIn_r = { 'o', 'p', 'I', 'n', '_', 'r' };
	static final char[] classNew = { 'n', 'e', 'w' };
	static final char[] classDelete = { 'd', 'e', 'l', 'e', 't', 'e' };
	static final char[] apply = { 'o', 'p', 'A', 'p', 'p', 'l', 'y' };
	static final char[] applyReverse = { 'o', 'p', 'A', 'p', 'p', 'l', 'y', 'R', 'e', 'v', 'e', 'r', 's', 'e' };
	static final char[] lib = { 'l', 'i', 'b' }; // shared
	static final char[] msg = { 'm', 's', 'g' }; // shared
	static final char[] GNU_asm = { 'G', 'N', 'U', '_', 'a', 's', 'm' };
	static final char[] tohash = { 't', 'o', 'H', 'a', 's', 'h' };
	static final char[] alloca = { 'a', 'l', 'l', 'o', 'c', 'a' };
	static final char[] main = { 'm', 'a', 'i', 'n' };
	static final char[] WinMain = { 'W', 'i', 'n', 'M', 'a', 'i', 'n' };
	static final char[] DllMain = { 'D', 'l', 'l', 'M', 'a', 'i', 'n' };
	static final char[] tochar = { 't', 'o', 'I', 'd', 'e', 'n', 't', 'i', 'f', 'i', 'e', 'r' };
	static final char[] _arguments_typeinfo = { '_', 'a', 'r', 'g', 'u', 'm', 'e', 'n', 't', 's', '_', 't', 'y', 'p', 'e', 'i', 'n', 'f', 'o' };
	static final char[] elements = { 'e', 'l', 'e', 'm', 'e', 'n', 't', 's' };
	static final char[] outer = { 'o', 'u', 't', 'e', 'r' };
	static final char[] System = { 'S', 'y', 's', 't', 'e', 'm' }; // shared
	static final char[] isArithmetic = { 'i', 's', 'A', 'r', 'i', 't', 'h', 'm', 'e', 't', 'i', 'c' };
	static final char[] isFloating = { 'i', 's', 'F', 'l', 'o', 'a', 't', 'i', 'n', 'g' };
	static final char[] isIntegral = { 'i', 's', 'I', 'n', 't', 'e', 'g', 'r', 'a', 'l' };
	static final char[] isScalar = { 'i', 's', 'S', 'c', 'a', 'l', 'a', 'r' };
	static final char[] isUnsigned = { 'i', 's', 'U', 'n', 's', 'i', 'g', 'n', 'e', 'd' };
	static final char[] isAssociativeArray = { 'i', 's', 'A', 's', 's', 'o', 'c', 'i', 'a', 't', 'i', 'v', 'e', 'A', 'r', 'r', 'a', 'y' };
	static final char[] isStaticArray = { 'i', 's', 'S', 't', 'a', 't', 'i', 'c', 'A', 'r', 'r', 'a', 'y' };
	static final char[] isAbstractClass = { 'i', 's', 'A', 'b', 's', 't', 'r', 'a', 'c', 't', 'C', 'l', 'a', 's', 's' };
	static final char[] isFinalClass = { 'i', 's', 'F', 'i', 'n', 'a', 'l', 'C', 'l', 'a', 's', 's' };
	static final char[] isAbstractFunction = { 'i', 's', 'A', 'b', 's', 't', 'r', 'a', 'c', 't', 'F', 'u', 'n', 'c', 't', 'i', 'o', 'n' };
	static final char[] isVirtualFunction = { 'i', 's', 'V', 'i', 'r', 't', 'u', 'a', 'l', 'F', 'u', 'n', 'c', 't', 'i', 'o', 'n' };
	static final char[] isFinalFunction = { 'i', 's', 'F', 'i', 'n', 'a', 'l', 'F', 'u', 'n', 'c', 't', 'i', 'o', 'n' };
	static final char[] hasMember = { 'h', 'a', 's', 'M', 'e', 'm', 'b', 'e', 'r' };
	static final char[] getMember = { 'g', 'e', 't', 'M', 'e', 'm', 'b', 'e', 'r' };
	static final char[] getVirtualFunctions = { 'g', 'e', 't', 'V', 'i', 'r', 't', 'u', 'a', 'l', 'F', 'u', 'n', 'c', 't', 'i', 'o', 'n', 's' };
	static final char[] classInstanceSize = { 'c', 'l', 'a', 's', 's', 'I', 'n', 's', 't', 'a', 'n', 'c', 'e', 'S', 'i', 'z', 'e' };
	static final char[] allMembers = { 'a', 'l', 'l', 'M', 'e', 'm', 'b', 'e', 'r', 's' };
	static final char[] derivedMembers = { 'd', 'e', 'r', 'i', 'v', 'e', 'd', 'M', 'e', 'm', 'b', 'e', 'r', 's' };
	static final char[] tupleof = { 't', 'u', 'p', 'l', 'e', 'o', 'f' };
	static final char[] adDup = { '_', 'a', 'd', 'D', 'u', 'p' };
	static final char[] adReverse = { '_', 'a', 'd', 'R', 'e', 'v', 'e', 'r', 's', 'e' };
	static final char[] aaLen = { '_', 'a', 'a', 'L', 'e', 'n' };
	static final char[] aaKeys = { '_', 'a', 'a', 'K', 'e', 'y', 's' };
	static final char[] aaValues = { '_', 'a', 'a', 'V', 'a', 'l', 'u', 'e', 's' };
	static final char[] aaRehash = { '_', 'a', 'a', 'R', 'e', 'h', 'a', 's', 'h' };
	static final char[] funcptr = { 'f', 'u', 'n', 'c', 'p', 't', 'r' };
	static final char[] cast = { 'o', 'p', 'C', 'a', 's', 't' };
	static final char[] getmembers = { 'g', 'e', 't', 'M', 'e', 'm', 'b', 'e', 'r', 's' };
	
	// Others useful
	static final char[] ZERO = { '0' };
	static final char[] ONE = { '1' };
	static final char[] Cpp = { 'C', '+', '+' };
	static final char[] Ddoc = { 'D', 'd', 'o', 'c' };
	static final char[] string = { 's', 't', 'r', 'i', 'n', 'g' }; // shared
	static final char[] size_t = { 's', 'i', 'z', 'e', '_', 't' }; // shared
	
	static final char[] FILE_DUMMY = { 'u', 'n', 'n', 'a', 'm', 'e', 'd', '.', 'd' };
	static final char[] DATE_DUMMY = { 'J', 'u', 'n', ' ', '2', '6', ' ', '1', '9', '8', '1' };
	static final char[] TIME_DUMMY = { '2', '1', ':', '0', '0', ':', '0', '0' };
	static final char[] TIMESTAMP_DUMMY = { 'F', 'r', 'i', ' ', 'J', 'u', 'n', ' ', '2', '6', ' ', '2', '1', ':', '0', '0', ':', '0', '0', ' ', '1', '9', '8', '1' };
	static final char[] VENDOR_DUMMY = { 'D', 'e', 's', 'c', 'e', 'n', 't' };
	
	static final char[] isSame = { 'i', 's', 'S', 'a', 'm', 'e' };
	static final char[] compiles = { 'c', 'o', 'm', 'p', 'i', 'l', 'e', 's' };

}
