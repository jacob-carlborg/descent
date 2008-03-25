package descent.internal.core;

import java.util.Stack;

import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.ISignatureConstants;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeAArray;
import descent.internal.compiler.parser.TypeBasic;
import descent.internal.compiler.parser.TypeDArray;
import descent.internal.compiler.parser.TypePointer;
import descent.internal.compiler.parser.TypeSArray;
import descent.internal.compiler.parser.TypeSlice;
import descent.internal.compiler.parser.TypeTypeof;

public class InternalSignature implements ISignatureConstants {
	
	private IJavaProject javaProject;
	
	public InternalSignature(IJavaProject javaProject) {
		this.javaProject = javaProject;
	}
	
	public static Type toType(String signature) {
		final Stack<Type> stack = new Stack<Type>();
		final ASTNodeEncoder encoder = new ASTNodeEncoder();
		SignatureProcessor.process(signature, new SignatureRequestorAdapter() {
			@Override
			public void acceptPrimitive(TypeBasic type) {
				stack.push(type);
			}
			@Override
			public void acceptPointer(String signature) {
				stack.push(new TypePointer(stack.pop()));
			}
			@Override
			public void acceptStaticArray(char[] dimension, String signature) {
				stack.push(new TypeSArray(stack.pop(), encoder.decodeExpression(dimension)));
			}
			@Override
			public void acceptDynamicArray(String signature) {
				stack.push(new TypeDArray(stack.pop()));
			}
			@Override
			public void acceptAssociativeArray(String signature) {
				stack.push(new TypeAArray(stack.pop(), stack.pop()));
			}
			@Override
			public void acceptTypeof(char[] expression, String signature) {
				stack.push(new TypeTypeof(Loc.ZERO, encoder.decodeExpression(expression)));
			}
			@Override
			public void acceptSlice(char[] lwr, char[] upr, String signature) {
				stack.push(new TypeSlice(stack.pop(), encoder.decodeExpression(lwr), encoder.decodeExpression(upr)));
			}
		});
		return stack.pop();
	}
	
	public static Type fromSignature(char[] signature) {
		return toType(new String(signature));
	}
	
	public IJavaElement findType(char[] packageName, char[] typeName) {
		StringBuilder sb = new StringBuilder();
		appendPackageName(packageName, sb);
		sb.append(CLASS);
		appendName(typeName, sb);
		return javaProject.findBySignature(sb.toString());
	}
	
	public IJavaElement findMethod(char[] packageName, char[] methodName, char[] signature) {
		StringBuilder sb = new StringBuilder();
		appendPackageName(packageName, sb);
		sb.append(FUNCTION);
		appendName(methodName, sb);
		sb.append(signature);
		return javaProject.findBySignature(sb.toString());
	}
	
	public IJavaElement findField(char[] packageName, char[] fieldName) {
		StringBuilder sb = new StringBuilder();
		appendPackageName(packageName, sb);
		sb.append(VARIABLE);
		appendName(fieldName, sb);
		return javaProject.findBySignature(sb.toString());
	}
	
	public static void appendPackageName(char[] packageName, StringBuilder sb) {
		sb.append(MODULE);
		
		char[][] pieces = CharOperation.splitOn('.', packageName);
		for (int i = 0; i < pieces.length; i++) {
			appendName(pieces[i], sb);
		}
	}
	
	public static void appendName(char[] name, StringBuilder sb) {
		sb.append(name.length);
		sb.append(name);
	}

}
