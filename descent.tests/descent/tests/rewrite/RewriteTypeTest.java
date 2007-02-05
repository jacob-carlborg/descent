package descent.tests.rewrite;

import descent.core.dom.Argument;
import descent.core.dom.AssociativeArrayType;
import descent.core.dom.DelegateType;
import descent.core.dom.PrimitiveType;

public class RewriteTypeTest extends AbstractRewriteTest {
	
	public void testAssociativeArrayTypeChangeComponentType() throws Exception {
		AssociativeArrayType type = (AssociativeArrayType) beginType("a[b]");
		type.setComponentType(ast.newSimpleType(ast.newSimpleName("xxx")));
		assertTypeEqualsTokenByToken("xxx[b]", end());
	}
	
	public void testAssociativeArrayTypeChangeKeyType() throws Exception {
		AssociativeArrayType type = (AssociativeArrayType) beginType("a[b]");
		type.setKeyType(ast.newSimpleType(ast.newSimpleName("xxx")));
		assertTypeEqualsTokenByToken("a[xxx]", end());
	}
	
	public void testDelegateTypeChangeReturnType() throws Exception {
		DelegateType type = (DelegateType) beginType("int delegate()");
		type.setReturnType(ast.newPrimitiveType(PrimitiveType.Code.LONG));
		assertTypeEqualsTokenByToken("long delegate()", end());
	}
	
	public void testDelegateTypeChangeFunctionPointerType1() throws Exception {
		DelegateType type = (DelegateType) beginType("int function()");
		type.setFunctionPointer(false);
		assertTypeEqualsTokenByToken("int delegate()", end());
	}
	
	public void testDelegateTypeChangeFunctionPointerType2() throws Exception {
		DelegateType type = (DelegateType) beginType("int delegate()");
		type.setFunctionPointer(true);
		assertTypeEqualsTokenByToken("int function()", end());
	}
	
	public void testDelegateTypeAddArguments() throws Exception {
		DelegateType type = (DelegateType) beginType("int delegate()");
		
		Argument arg1 = ast.newArgument();
		arg1.setType(ast.newPrimitiveType(PrimitiveType.Code.CHAR));
		
		Argument arg2 = ast.newArgument();
		arg2.setType(ast.newPrimitiveType(PrimitiveType.Code.WCHAR));
		
		type.arguments().add(arg1);
		type.arguments().add(arg2);
		
		assertTypeEqualsTokenByToken("int delegate(char, wchar)", end());
	}
	
	public void testDelegateTypeRemoveArguments() throws Exception {
		DelegateType type = (DelegateType) beginType("int delegate(char, wchar)");
		type.arguments().clear();
		
		assertTypeEqualsTokenByToken("int delegate()", end());
	}
	
	public void testDelegateTypeAddVariadic() throws Exception {
		DelegateType type = (DelegateType) beginType("int delegate(char, wchar)");
		type.setVariadic(true);
		
		assertTypeEqualsTokenByToken("int delegate(char, wchar ...)", end());
	}
	
	public void testDelegateTypeRemoveVariadic() throws Exception {
		DelegateType type = (DelegateType) beginType("int delegate(char, wchar ...)");
		type.setVariadic(false);
		
		assertTypeEqualsTokenByToken("int delegate(char, wchar)", end());
	}

}
