package descent.internal.core;

import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.TypeBasic;

public class SignatureRequestorAdapter implements ISignatureRequestor {

	public void acceptArgumentBreak(char c) {
		
	}

	public void acceptArgumentModifier(int stc) {
		
	}

	public void acceptAssociativeArray(String signature) {
		
	}

	public void acceptDelegate(String signature) {
		
	}

	public void acceptDynamicArray(String signature) {
		
	}
	
	public void acceptIdentifier(char[] name, String signature) {
		
	}

	public void acceptModule(char[][] compoundName, String signature) {
		
	}

	public void acceptPointer(String signature) {
		
	}

	public void acceptPrimitive(TypeBasic type) {
		
	}

	public void acceptStaticArray(int dimension, String signature) {
		
	}

	public void acceptSymbol(char type, char[] name, int startPosition, String signature) {
		
	}

	public void enterFunctionType() {
		
	}

	public void exitFunctionType(LINK link, String signature) {
		
	}
	
	public void acceptTemplateTupleParameter() {
		
	}
	
	public void acceptTemplateValueParameterSpecificValue(Expression exp) {
		
	}
	
	public void enterTemplateAliasParameter() {
		
	}
	
	public void enterTemplateParameters() {
		
	}
	
	public void enterTemplateTypeParameter() {
		
	}
	
	public void enterTemplateValueParameter() {
		
	}
	
	public void exitTemplateAliasParameter(String signature) {
		
	}
	
	public void exitTemplateParameters() {
		
	}
	
	public void exitTemplateTypeParameter(String signature) {
		
	}
	
	public void exitTemplateValueParameter(String signature) {
		
	}
	
	public void enterTemplateInstance() {
		
	}
	
	public void exitTemplateInstance(String signature) {
		
	}
	
	public void enterTemplateInstanceType() {
		
	}
	
	public void exitTemplateInstanceTypeParameter(String signature) {
		
	}
	
	public void acceptTemplateInstanceValue(Expression exp, String signature) {
		
	}
	
	public void enterTemplateInstanceSymbol() {
	
	}
	
	public void exitTemplateInstanceSymbol(String string) {
		
	}

}
