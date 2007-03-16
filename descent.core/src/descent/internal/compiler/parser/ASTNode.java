package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.DDocComment;

// class Object in DMD compiler
public abstract class ASTNode {
	
	public final static int ARGUMENT = 1;
	public final static int CATCH = 2;
	public final static int ALIGN_DECLARATION = 3;
	public final static int ANON_DECLARATION = 4;
	public final static int COMPILE_DECLARATION = 5;
	public final static int CONDITIONAL_DECLARATION = 6;
	public final static int LINK_DECLARATION = 7;
	public final static int PRAGMA_DECLARATION = 8;
	public final static int DEBUG_SYMBOL = 9;
	public final static int ALIAS_DECLARATION = 10;
	public final static int FUNC_DECLARATION = 11;
	public final static int CTOR_DECLARATION = 12;
	public final static int DELETE_DECLARATION = 13;
	public final static int DTOR_DECLARATION = 14;
	public final static int FUNC_LITERAL_DECLARATION = 15;
	public final static int INVARIANT_DECLARATION = 16;
	public final static int NEW_DECLARATION = 17;
	public final static int STATIC_CTOR_DECLARATION = 18;
	public final static int STATIC_DTOR_DECLARATION = 19;
	public final static int UNIT_TEST_DECLARATION = 20;
	public final static int PROT_DECLARATION = 21;
	public final static int STORAGE_CLASS_DECLARATION = 22;
	public final static int BASE_CLASS = 23;
	public final static int TYPEDEF_DECLARATION = 24;
	public final static int VAR_DECLARATION = 25;
	public final static int ENUM_MEMBER = 26;
	public final static int IMPORT = 27;
	public final static int MODIFIER_DECLARATION = 28;
	public final static int MULTI_IMPORT = 29;
	public final static int CLASS_DECLARATION = 30;
	public final static int INTERFACE_DECLARATION = 31;
	public final static int STRUCT_DECLARATION = 32;
	public final static int UNION_DECLARATION = 33;
	public final static int ENUM_DECLARATION = 34;
	public final static int MODULE = 35;
	public final static int TEMPLATE_DECLARATION = 36;
	public final static int TEMPLATE_INSTANCE = 37;
	public final static int TEMPLATE_MIXIN = 38;
	public final static int STATIC_ASSERT = 39;
	public final static int VERSION = 40;
	public final static int VERSION_SYMBOL = 41;
	public final static int ARRAY_LITERAL_EXP = 42;
	public final static int ADD_ASSIGN_EXP = 43;
	public final static int INCREMENT_EXP = 44;
	public final static int ADD_EXP = 45;
	public final static int AND_AND_EXP = 46;
	public final static int AND_ASSIGN_EXP = 47;
	public final static int AND_EXP = 48;
	public final static int ASSIGN_EXP = 49;
	public final static int CAT_ASSIGN_EXP = 50;
	public final static int CAT_EXP = 51;
	public final static int CMP_EXP = 52;
	public final static int COMMA_EXP = 53;
	public final static int COND_EXP = 54;
	public final static int DIV_ASSIGN_EXP = 55;
	public final static int DIV_EXP = 56;
	public final static int EQUAL_EXP = 57;
	public final static int IDENTITY_EXP = 58;
	public final static int IN_EXP = 59;
	public final static int MIN_ASSIGN_EXP = 60;
	public final static int DECREMENT_EXP = 61;
	public final static int MIN_EXP = 62;
	public final static int MOD_ASSIGN_EXP = 63;
	public final static int MOD_EXP = 64;
	public final static int MUL_ASSIGN_EXP = 65;
	public final static int MUL_EXP = 66;
	public final static int OR_ASSIGN_EXP = 67;
	public final static int OR_EXP = 68;
	public final static int OR_OR_EXP = 69;
	public final static int POST_EXP = 70;
	public final static int SHL_ASSIGN_EXP = 71;
	public final static int SHL_EXP = 72;
	public final static int SHR_ASSIGN_EXP = 73;
	public final static int SHR_EXP = 74;
	public final static int USHR_ASSIGN_EXP = 75;
	public final static int USHR_EXP = 76;
	public final static int XOR_ASSIGN_EXP = 77;
	public final static int XOR_EXP = 78;
	public final static int DECLARATION_EXP = 79;
	public final static int DOLLAR_EXP = 80;
	public final static int FUNC_EXP = 81;
	public final static int IDENTIFIER_EXP = 82;
	public final static int TEMPLATE_INSTANCE_WRAPPER = 83;
	public final static int IFTYPE_EXP = 84;
	public final static int INTEGER_EXP = 85;
	public final static int MULTI_STRING_EXP = 86;
	public final static int NEW_ANON_CLASS_EXP = 87;
	public final static int NEW_EXP = 88;
	public final static int NULL_EXP = 89;
	public final static int REAL_EXP = 90;
	public final static int SCOPE_EXP = 91;
	public final static int STRING_EXP = 92;
	public final static int THIS_EXP = 93;
	public final static int SUPER_EXP = 94;
	public final static int TYPE_DOT_ID_EXP = 95;
	public final static int TYPE_EXP = 96;
	public final static int TYPEID_EXP = 97;
	public final static int ADDR_EXP = 98;
	public final static int ARRAY_EXP = 99;
	public final static int ASSERT_EXP = 100;
	public final static int CALL_EXP = 101;
	public final static int CAST_EXP = 102;
	public final static int COM_EXP = 103;
	public final static int COMPILE_EXP = 104;
	public final static int DELETE_EXP = 105;
	public final static int DOT_ID_EXP = 106;
	public final static int DOT_TEMPLATE_INSTANCE_EXP = 107;
	public final static int FILE_EXP = 108;
	public final static int NEG_EXP = 109;
	public final static int NOT_EXP = 110;
	public final static int PAREN_EXP = 111;
	public final static int PTR_EXP = 112;
	public final static int SLICE_EXP = 113;
	public final static int UADD_EXP = 114;
	public final static int ARRAY_INITIALIZER = 115;
	public final static int EXP_INITIALIZER = 116;
	public final static int STRUCT_INITIALIZER = 117;
	public final static int VOID_INITIALIZER = 118;
	public final static int MODIFIER = 119;
	public final static int MODULE_DECLARATION = 120;
	public final static int ASM_STATEMENT = 121;
	public final static int BREAK_STATEMENT = 122;
	public final static int CASE_STATEMENT = 123;
	public final static int COMPILE_STATEMENT = 124;
	public final static int COMPOUND_STATEMENT = 125;
	public final static int ASM_BLOCK = 126;
	public final static int CONDITIONAL_STATEMENT = 127;
	public final static int CONTINUE_STATEMENT = 128;
	public final static int DEFAULT_STATEMENT = 129;
	public final static int DO_STATEMENT = 130;
	public final static int EXP_STATEMENT = 131;
	public final static int DECLARATION_STATEMENT = 132;
	public final static int FOREACH_STATEMENT = 133;
	public final static int FOR_STATEMENT = 134;
	public final static int GOTO_CASE_STATEMENT = 135;
	public final static int GOTO_DEFAULT_STATEMENT = 136;
	public final static int GOTO_STATEMENT = 137;
	public final static int IF_STATEMENT = 138;
	public final static int LABEL_STATEMENT = 139;
	public final static int ON_SCOPE_STATEMENT = 140;
	public final static int PRAGMA_STATEMENT = 141;
	public final static int RETURN_STATEMENT = 142;
	public final static int STATIC_ASSERT_STATEMENT = 143;
	public final static int SWITCH_STATEMENT = 144;
	public final static int SYNCHRONIZED_STATEMENT = 145;
	public final static int THROW_STATEMENT = 146;
	public final static int TRY_STATEMENT = 147;
	public final static int VOLATILE_STATEMENT = 148;
	public final static int WHILE_STATEMENT = 149;
	public final static int WITH_STATEMENT = 150;
	public final static int TEMPLATE_ALIAS_PARAMETER = 151;
	public final static int TEMPLATE_TUPLE_PARAMETER = 152;
	public final static int TEMPLATE_TYPE_PARAMETER = 153;
	public final static int TEMPLATE_VALUE_PARAMETER = 154;
	public final static int TYPE_A_ARRAY = 155;
	public final static int TYPE_BASIC = 156;
	public final static int TYPE_D_ARRAY = 157;
	public final static int TYPE_DELEGATE = 158;
	public final static int TYPE_FUNCTION = 159;
	public final static int TYPE_POINTER = 160;
	public final static int TYPE_IDENTIFIER = 161;
	public final static int TYPE_INSTANCE = 162;
	public final static int TYPE_TYPEOF = 163;
	public final static int TYPE_S_ARRAY = 164;
	public final static int TYPE_SLICE = 165;
	public final static int TYPE_TYPEDEF = 166;
	public final static int TYPE_ENUM = 167;
	
	public int start;
	public int length;
	public int flags;
	
	public List<DDocComment> preDdocs;
	public List<Modifier> modifiers;
	public DDocComment postDdoc;
	
	/**
	 * Denotes a node created during the semantic pass, must be ignored
	 * by ASTConverter.
	 */ 
	public boolean synthetic;  
	
	public void setSourceRange(int startPosition, int length) {
		this.start = startPosition;
		this.length = length;
	}
	
	public void addModifier(Modifier modifier) {
		if (modifiers == null) {
			modifiers = new ArrayList<Modifier>();
		}
		modifiers.add(modifier);
	}
	
	public void addModifiers(List<Modifier> someModifiers) {
		if (modifiers == null) {
			modifiers = new ArrayList<Modifier>();
		}
		modifiers.addAll(someModifiers);
	}
	
	public abstract int kind();

}
