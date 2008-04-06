package descent.tests.assist;

import descent.core.CompletionProposal;

public class CompletionOnTypeDotIdExp_Test extends AbstractCompletionTest {
	
	public void testNothingOnVoid() throws Exception {
		String s = "void foo() { int x = void.";
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.FIELD_REF);
	}
	
	public void testOnIntegralType() throws Exception {
		String s = "void foo() { int x = int.";
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.FIELD_REF, 
				"alignof", s.length(), s.length(),
				"init", s.length(), s.length(),
				"mangleof", s.length(), s.length(),
				"max", s.length(), s.length(),
				"min", s.length(), s.length(),
				"sizeof", s.length(), s.length(),
				"stringof", s.length(), s.length()
				);
	}
	
	public void testOnIntegralTypeWithSome() throws Exception {
		String s = "void foo() { int x = int.a";
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.FIELD_REF, 
				"alignof", s.length() - 1, s.length()
				);
	}
	
	public void testOnFloatingPointType() throws Exception {
		String s = "void foo() { float x = float.";
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.FIELD_REF, 
				"alignof", s.length(), s.length(),
				"dig", s.length(), s.length(),
				"epsilon", s.length(), s.length(),
				"infinity", s.length(), s.length(),
				"init", s.length(), s.length(),				
				"mangleof", s.length(), s.length(),
				"mant_dig", s.length(), s.length(),
				"max", s.length(), s.length(),
				"max_10_exp", s.length(), s.length(),
				"max_exp", s.length(), s.length(),
				"min", s.length(), s.length(),
				"min_10_exp", s.length(), s.length(),
				"min_exp", s.length(), s.length(),
				"nan", s.length(), s.length(),
				"sizeof", s.length(), s.length(),
				"stringof", s.length(), s.length()
				);
	}
	
	public void testOnFloatingPointTypeWithSomething() throws Exception {
		String s = "void foo() { float x = float.m";
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.FIELD_REF, 
				"mangleof", s.length() - 1, s.length(),
				"mant_dig", s.length() - 1, s.length(),
				"max", s.length() - 1, s.length(),
				"max_10_exp", s.length() - 1, s.length(),
				"max_exp", s.length() - 1, s.length(),
				"min", s.length() - 1, s.length(),
				"min_10_exp", s.length() - 1, s.length(),
				"min_exp", s.length() - 1, s.length()
				);
	}
	
	public void testOnIntegralTypeWithTypeof() throws Exception {
		String s = "void foo() { int x = typeof(1).";
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.FIELD_REF, 
				"alignof", s.length(), s.length(),
				"init", s.length(), s.length(),
				"mangleof", s.length(), s.length(),
				"max", s.length(), s.length(),
				"min", s.length(), s.length(),
				"sizeof", s.length(), s.length(),
				"stringof", s.length(), s.length()
				);
	}
	
	public void testOnFloatingTypeWithTypeof() throws Exception {
		String s = "void foo() { float x = typeof(1.0).";
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.FIELD_REF, 
				"alignof", s.length(), s.length(),
				"dig", s.length(), s.length(),
				"epsilon", s.length(), s.length(),
				"infinity", s.length(), s.length(),
				"init", s.length(), s.length(),				
				"mangleof", s.length(), s.length(),
				"mant_dig", s.length(), s.length(),
				"max", s.length(), s.length(),
				"max_10_exp", s.length(), s.length(),
				"max_exp", s.length(), s.length(),
				"min", s.length(), s.length(),
				"min_10_exp", s.length(), s.length(),
				"min_exp", s.length(), s.length(),
				"nan", s.length(), s.length(),
				"sizeof", s.length(), s.length(),
				"stringof", s.length(), s.length()
				);
	}
	
	public void testOnEnumWithTypeof() throws Exception {
		String s = "enum X { a, b, c } void foo() { int x = typeof(X).";
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.FIELD_REF, 
				"alignof", s.length(), s.length(),
				"init", s.length(), s.length(),
				"mangleof", s.length(), s.length(),
				"max", s.length(), s.length(),
				"min", s.length(), s.length(),
				"sizeof", s.length(), s.length(),
				"stringof", s.length(), s.length()
				);
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.ENUM_MEMBER, 
				"a", s.length(), s.length(),
				"b", s.length(), s.length(),
				"c", s.length(), s.length()
				);
	}

}
