package descent.internal.compiler.parser;

import descent.core.compiler.CharOperation;

public interface Id {
	
	char[] IUnknown = { 'I', 'u', 'n', 'k', 'n', 'o', 'w', 'n' };
	char[] Object = { 'O', 'b', 'j', 'e', 'c', 't' };
	char[] object = { 'o', 'b', 'j', 'e', 'c', 't' }; // shared
	char[] max = { 'm', 'a', 'x' };
	char[] min = { 'm', 'i', 'n' };
	char[] This = { 't', 'h', 'i', 's' };
	char[] ctor = { '_', 'c', 't', 'o', 'r' };
	char[] dtor = { '_', 'd', 't', 'o', 'r' };
	char[] classInvariant = { '_', '_', 'i', 'n', 'v', 'a', 'r', 'i', 'a', 'n', 't' };
	char[] unitTest = { '_', 'u', 'n', 'i', 't', 'T', 'e', 's', 't' };
	char[] staticCtor = { '_', 's', 't', 'a', 't', 'i', 'c', 'C', 't', 'o', 'r' };
	char[] staticDtor = { '_', 's', 't', 'a', 't', 'i', 'c', 'D', 't', 'o', 'r' };
	char[] init = { 'i', 'n', 'i', 't' };
	char[] size = { 's', 'i', 'z', 'e' };
	char[] __sizeof = { 's', 'i', 'z', 'e', 'o', 'f' };
	char[] alignof = { 'a', 'l', 'i', 'g', 'n', 'o', 'f' };
	char[] mangleof = { 'm', 'a', 'n', 'g', 'l', 'e', 'o', 'f' };
	char[] stringof = { 's', 't', 'r', 'i', 'n', 'g', 'o', 'f' };
	char[] length = { 'l', 'e', 'n', 'g', 't', 'h' }; // shared
	char[] remove = { 'r', 'e', 'm', 'o', 'v', 'e' };
	char[] ptr = { 'p', 't', 'r' };
	char[] dollar = { '_', '_', 'd', 'o', 'l', 'l', 'a', 'r' };
	char[] offset = { 'o', 'f', 'f', 's', 'e', 't' };
	char[] offsetof = { 'o', 'f', 'f', 's', 'e', 't', 'o', 'f' };
	char[] ModuleInfo = { 'M', 'o', 'd', 'u', 'l', 'e', 'I', 'n', 'f', 'o' };
	char[] ClassInfo = { 'C', 'l', 'a', 's', 's', 'I', 'n', 'f', 'o' };
	char[] classinfo = { 'c', 'l', 'a', 's', 's', 'i', 'n', 'f', 'o' };
	char[] typeinfo = { 't', 'y', 'p', 'e', 'i', 'n', 'f', 'o' };
	char[] Exception = { 'E', 'x', 'c', 'e', 'p', 't', 'i', 'o', 'n' };
	char[] withSym = { '_', '_', 'w', 'i', 't', 'h', 'S', 'y', 'm' };
	char[] result = { '_', '_', 'r', 'e', 's', 'u', 'l', 't' };
	char[] returnLabel = { '_', '_', 'r', 'e', 't', 'u', 'r', 'n', 'L', 'a', 'b', 'e', 'l' };
	char[] _delegate = { 'd', 'e', 'l', 'e', 'g', 'a', 't', 'e' };
	char[] line = { 'l', 'i', 'n', 'e' };
	char[] empty = CharOperation.NO_CHAR;
	char[] p = { 'p' }; // shared
	char[] coverage = { '_', '_', 'c', 'o', 'v', 'e', 'r', 'a', 'g', 'e' };
	char[] TypeInfo = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o' };
	char[] TypeInfo_Class = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o', '_', 'C', 'l', 'a', 's', 's' };
	char[] TypeInfo_Struct = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o', '_', 'S', 't', 'r', 'u', 'c', 't' };
	char[] TypeInfo_Interface = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o', '_', 'I', 'n', 't', 'e', 'r', 'f', 'a', 'c', 'e' };
	char[] TypeInfo_Enum = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o', '_', 'E', 'n', 'u', 'm' };
	char[] TypeInfo_Typedef = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o', '_', 'T', 'y', 'p', 'e', 'd', 'e', 'f' };
	char[] TypeInfo_Pointer = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o', '_', 'P', 'o', 'i', 'n', 't', 'e', 'r' };
	char[] TypeInfo_Array = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o', '_', 'A', 'r', 'r', 'a', 'y' };
	char[] TypeInfo_StaticArray = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o', '_', 'S', 't', 'a', 't', 'i', 'c', 'A', 'r', 'r', 'a', 'y' };
	char[] TypeInfo_AssociativeArray = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o', '_', 'A', 's', 's', 'o', 'c', 'i', 'a', 't', 'i', 'v', 'e', 'A', 'r', 'r', 'a', 'y' };
	char[] TypeInfo_Function = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o', '_', 'F', 'u', 'n', 'c', 't', 'i', 'o', 'n' };
	char[] TypeInfo_Delegate = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o', '_', 'D', 'e', 'l', 'e', 'g', 'a', 't', 'e' };
	char[] TypeInfo_Tuple = { 'T', 'y', 'p', 'e', 'I', 'n', 'f', 'o', '_', 'T', 'u', 'p', 'l', 'e' };
	char[] _arguments = { '_', 'a', 'r', 'g', 'u', 'm', 'e', 'n', 't', 's' };
	char[] _argptr = { '_', 'a', 'r', 'g', 'p', 't', 'r' };
	char[] _match = { '_', 'm', 'a', 't', 'c', 'h' };
	char[] LINE = { '_', '_', 'L', 'I', 'N', 'E', '_', '_' };
	char[] FILE = { '_', '_', 'F', 'I', 'L', 'E', '_', '_' };
	char[] DATE = { '_', '_', 'D', 'A', 'T', 'E', '_', '_' };
	char[] TIME = { '_', '_', 'T', 'I', 'M', 'E', '_', '_' };
	char[] TIMESTAMP = { '_', '_', 'T', 'I', 'M', 'E', 'S', 'T', 'A', 'M', 'P', '_', '_' };
	char[] VENDOR = { '_', '_', 'V', 'E', 'N', 'D', 'O', 'R', '_', '_' };
	char[] VERSION = { '_', '_', 'V', 'E', 'R', 'S', 'I', 'O', 'N', '_', '_' };
	char[] nan = { 'n', 'a', 'n' };
	char[] infinity = { 'i', 'n', 'f', 'i', 'n', 'i', 't', 'y' };
	char[] dig = { 'd', 'i', 'g' };
	char[] epsilon = { 'e', 'p', 's', 'i', 'l', 'o', 'n' };
	char[] mant_dig = { 'm', 'a', 'n', 't', '_', 'd', 'i', 'g' };
	char[] max_10_exp = { 'm', 'a', 'x', '_', '1', '0', '_', 'e', 'x', 'p' };
	char[] max_exp = { 'm', 'a', 'x', '_', 'e', 'x', 'p' };
	char[] min_10_exp = { 'm', 'i', 'n', '_', '1', '0', '_', 'e', 'x', 'p' };
	char[] min_exp = { 'm', 'i', 'n', '_', 'e', 'x', 'p' };
	char[] re = { 'r', 'e' };
	char[] im = { 'i', 'm' };
	char[] C = { 'C' }; // shared
	char[] D = { 'D' }; // shared
	char[] Windows = { 'W', 'i', 'n', 'd', 'o', 'w', 's' }; // shared
	char[] Pascal = { 'P', 'a', 's', 'c', 'a', 'l' }; // shared
	char[] exit = { 'e', 'x', 'i', 't' }; // shared
	char[] success = { 's', 'u', 'c', 'c', 'e', 's', 's' }; // shared
	char[] failure = { 'f', 'a', 'i', 'l', 'u', 'r', 'e' }; // shared
	char[] keys = { 'k', 'e', 'y', 's' };
	char[] values = { 'v', 'a', 'l', 'u', 'e', 's' };
	char[] rehash = { 'r', 'e', 'h', 'a', 's', 'h' };
	char[] sort = { 's', 'o', 'r', 't' };
	char[] reverse = { 'r', 'e', 'v', 'e', 'r', 's', 'e' };
	char[] dup = { 'd', 'u', 'p' };
	char[] ___out = { 'o', 'u', 't' };
	char[] ___in = { 'i', 'n' };
	char[] __int = { 'i', 'n', 't' };
	char[] __dollar = { '$' };
	char[] __LOCAL_SIZE = { '_', '_', 'L', 'O', 'C', 'A', 'L', '_', 'S', 'I', 'Z', 'E' };
	char[] uadd = { 'o', 'p', 'P', 'o', 's' };
	char[] neg = { 'o', 'p', 'N', 'e', 'g' };
	char[] com = { 'o', 'p', 'C', 'o', 'm' };
	char[] add = { 'o', 'p', 'A', 'd', 'd' };
	char[] add_r = { 'o', 'p', 'A', 'd', 'd', '_', 'r' };
	char[] sub = { 'o', 'p', 'S', 'u', 'b' };
	char[] sub_r = { 'o', 'p', 'S', 'u', 'b', '_', 'r' };
	char[] mul = { 'o', 'p', 'M', 'u', 'l' };
	char[] mul_r = { 'o', 'p', 'M', 'u', 'l', '_', 'r' };
	char[] div = { 'o', 'p', 'D', 'i', 'v' };
	char[] div_r = { 'o', 'p', 'D', 'i', 'v', '_', 'r' };
	char[] mod = { 'o', 'p', 'M', 'o', 'd' };
	char[] mod_r = { 'o', 'p', 'M', 'o', 'd', '_', 'r' };
	char[] eq = { 'o', 'p', 'E', 'q', 'u', 'a', 'l', 's' };
	char[] cmp = { 'o', 'p', 'C', 'm', 'p' };
	char[] iand = { 'o', 'p', 'A', 'n', 'd' };
	char[] iand_r = { 'o', 'p', 'A', 'n', 'd', '_', 'r' };
	char[] ior = { 'o', 'p', 'O', 'r' };
	char[] ior_r = { 'o', 'p', 'O', 'r', '_', 'r' };
	char[] ixor = { 'o', 'p', 'X', 'o', 'r' };
	char[] ixor_r = { 'o', 'p', 'X', 'o', 'r', '_', 'r' };
	char[] shl = { 'o', 'p', 'S', 'h', 'l' };
	char[] shl_r = { 'o', 'p', 'S', 'h', 'l', '_', 'r' };
	char[] shr = { 'o', 'p', 'S', 'h', 'r' };
	char[] shr_r = { 'o', 'p', 'S', 'h', 'r', '_', 'r' };
	char[] ushr = { 'o', 'p', 'U', 'S', 'h', 'r' };
	char[] ushr_r = { 'o', 'p', 'U', 'S', 'h', 'r', '_', 'r' };
	char[] cat = { 'o', 'p', 'C', 'a', 't' };
	char[] cat_r = { 'o', 'p', 'C', 'a', 't', '_', 'r' };
	char[] assign = { 'o', 'p', 'A', 's', 's', 'i', 'g', 'n' };
	char[] addass = { 'o', 'p', 'A', 'd', 'd', 'A', 's', 's', 'i', 'g', 'n' };
	char[] subass = { 'o', 'p', 'S', 'u', 'b', 'A', 's', 's', 'i', 'g', 'n' };
	char[] mulass = { 'o', 'p', 'M', 'u', 'l', 'A', 's', 's', 'i', 'g', 'n' };
	char[] divass = { 'o', 'p', 'D', 'i', 'v', 'A', 's', 's', 'i', 'g', 'n' };
	char[] modass = { 'o', 'p', 'M', 'o', 'd', 'A', 's', 's', 'i', 'g', 'n' };
	char[] andass = { 'o', 'p', 'A', 'n', 'd', 'A', 's', 's', 'i', 'g', 'n' };
	char[] orass = { 'o', 'p', 'O', 'r', 'A', 's', 's', 'i', 'g', 'n' };
	char[] xorass = { 'o', 'p', 'X', 'o', 'r', 'A', 's', 's', 'i', 'g', 'n' };
	char[] shlass = { 'o', 'p', 'S', 'h', 'l', 'A', 's', 's', 'i', 'g', 'n' };
	char[] shrass = { 'o', 'p', 'S', 'h', 'r', 'A', 's', 's', 'i', 'g', 'n' };
	char[] ushrass = { 'o', 'p', 'U', 'S', 'h', 'r', 'A', 's', 's', 'i', 'g', 'n' };
	char[] catass = { 'o', 'p', 'C', 'a', 't', 'A', 's', 's', 'i', 'g', 'n' };
	char[] postinc = { 'o', 'p', 'P', 'o', 's', 't', 'I', 'n', 'c' };
	char[] postdec = { 'o', 'p', 'P', 'o', 's', 't', 'D', 'e', 'c' };
	char[] index = { 'o', 'p', 'I', 'n', 'd', 'e', 'x' };
	char[] indexass = { 'o', 'p', 'I', 'n', 'd', 'e', 'x', 'A', 's', 's', 'i', 'g', 'n' };
	char[] slice = { 'o', 'p', 'S', 'l', 'i', 'c', 'e' };
	char[] sliceass = { 'o', 'p', 'S', 'l', 'i', 'c', 'e', 'A', 's', 's', 'i', 'g', 'n' };
	char[] call = { 'o', 'p', 'C', 'a', 'l', 'l' };
	char[] _cast = { 'o', 'p', 'C', 'a', 's', 't' };
	char[] match = { 'o', 'p', 'M', 'a', 't', 'c', 'h' };
	char[] next = { 'o', 'p', 'N', 'e', 'x', 't' };
	char[] opIn = { 'o', 'p', 'I', 'n' };
	char[] opIn_r = { 'o', 'p', 'I', 'n', '_', 'r' };
	char[] classNew = { 'n', 'e', 'w' };
	char[] classDelete = { 'd', 'e', 'l', 'e', 't', 'e' };
	char[] apply = { 'o', 'p', 'A', 'p', 'p', 'l', 'y' };
	char[] applyReverse = { 'o', 'p', 'A', 'p', 'p', 'l', 'y', 'R', 'e', 'v', 'e', 'r', 's', 'e' };
	char[] lib = { 'l', 'i', 'b' }; // shared
	char[] msg = { 'm', 's', 'g' }; // shared
	char[] GNU_asm = { 'G', 'N', 'U', '_', 'a', 's', 'm' };
	char[] tohash = { 't', 'o', 'H', 'a', 's', 'h' };
	char[] alloca = { 'a', 'l', 'l', 'o', 'c', 'a' };
	char[] main = { 'm', 'a', 'i', 'n' };
	char[] WinMain = { 'W', 'i', 'n', 'M', 'a', 'i', 'n' };
	char[] DllMain = { 'D', 'l', 'l', 'M', 'a', 'i', 'n' };
	char[] tochar = { 't', 'o', 'I', 'd', 'e', 'n', 't', 'i', 'f', 'i', 'e', 'r' };
	char[] _arguments_typeinfo = { '_', 'a', 'r', 'g', 'u', 'm', 'e', 'n', 't', 's', '_', 't', 'y', 'p', 'e', 'i', 'n', 'f', 'o' };
	char[] elements = { 'e', 'l', 'e', 'm', 'e', 'n', 't', 's' };
	char[] outer = { 'o', 'u', 't', 'e', 'r' };
	char[] System = { 'S', 'y', 's', 't', 'e', 'm' }; // shared
	char[] isArithmetic = { 'i', 's', 'A', 'r', 'i', 't', 'h', 'm', 'e', 't', 'i', 'c' };
	char[] isFloating = { 'i', 's', 'F', 'l', 'o', 'a', 't', 'i', 'n', 'g' };
	char[] isIntegral = { 'i', 's', 'I', 'n', 't', 'e', 'g', 'r', 'a', 'l' };
	char[] isScalar = { 'i', 's', 'S', 'c', 'a', 'l', 'a', 'r' };
	char[] isUnsigned = { 'i', 's', 'U', 'n', 's', 'i', 'g', 'n', 'e', 'd' };
	char[] isAssociativeArray = { 'i', 's', 'A', 's', 's', 'o', 'c', 'i', 'a', 't', 'i', 'v', 'e', 'A', 'r', 'r', 'a', 'y' };
	char[] isStaticArray = { 'i', 's', 'S', 't', 'a', 't', 'i', 'c', 'A', 'r', 'r', 'a', 'y' };
	char[] isAbstractClass = { 'i', 's', 'A', 'b', 's', 't', 'r', 'a', 'c', 't', 'C', 'l', 'a', 's', 's' };
	char[] isFinalClass = { 'i', 's', 'F', 'i', 'n', 'a', 'l', 'C', 'l', 'a', 's', 's' };
	char[] isAbstractFunction = { 'i', 's', 'A', 'b', 's', 't', 'r', 'a', 'c', 't', 'F', 'u', 'n', 'c', 't', 'i', 'o', 'n' };
	char[] isVirtualFunction = { 'i', 's', 'V', 'i', 'r', 't', 'u', 'a', 'l', 'F', 'u', 'n', 'c', 't', 'i', 'o', 'n' };
	char[] isFinalFunction = { 'i', 's', 'F', 'i', 'n', 'a', 'l', 'F', 'u', 'n', 'c', 't', 'i', 'o', 'n' };
	char[] hasMember = { 'h', 'a', 's', 'M', 'e', 'm', 'b', 'e', 'r' };
	char[] getMember = { 'g', 'e', 't', 'M', 'e', 'm', 'b', 'e', 'r' };
	char[] getVirtualFunctions = { 'g', 'e', 't', 'V', 'i', 'r', 't', 'u', 'a', 'l', 'F', 'u', 'n', 'c', 't', 'i', 'o', 'n', 's' };
	char[] classInstanceSize = { 'c', 'l', 'a', 's', 's', 'I', 'n', 's', 't', 'a', 'n', 'c', 'e', 'S', 'i', 'z', 'e' };
	char[] allMembers = { 'a', 'l', 'l', 'M', 'e', 'm', 'b', 'e', 'r', 's' };
	char[] derivedMembers = { 'd', 'e', 'r', 'i', 'v', 'e', 'd', 'M', 'e', 'm', 'b', 'e', 'r', 's' };
	char[] tupleof = { 't', 'u', 'p', 'l', 'e', 'o', 'f' };
	char[] adDup = { '_', 'a', 'd', 'D', 'u', 'p' };
	char[] adReverse = { '_', 'a', 'd', 'R', 'e', 'v', 'e', 'r', 's', 'e' };
	char[] aaLen = { '_', 'a', 'a', 'L', 'e', 'n' };
	char[] aaKeys = { '_', 'a', 'a', 'K', 'e', 'y', 's' };
	char[] aaValues = { '_', 'a', 'a', 'V', 'a', 'l', 'u', 'e', 's' };
	char[] aaRehash = { '_', 'a', 'a', 'R', 'e', 'h', 'a', 's', 'h' };
	
	// Others useful
	char[] ZERO = { '0' };
	char[] ONE = { '1' };
	char[] Cpp = { 'C', '+', '+' };
	char[] Ddoc = { 'D', 'd', 'o', 'c' };
	char[] string = { 's', 't', 'r', 'i', 'n', 'g' }; // shared
	char[] size_t = { 's', 'i', 'z', 'e', '_', 't' }; // shared	

}
