package descent.internal.codeassist;

import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.ISignatureConstants;

public class InternalSignature implements ISignatureConstants {
	
	private IJavaProject javaProject;
	
	public InternalSignature(IJavaProject javaProject) {
		this.javaProject = javaProject;
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
	
	private static void appendPackageName(char[] packageName, StringBuilder sb) {
		sb.append(MODULE);
		
		char[][] pieces = CharOperation.splitOn('.', packageName);
		for (int i = 0; i < pieces.length; i++) {
			appendName(pieces[i], sb);
		}
	}
	
	private static void appendName(char[] name, StringBuilder sb) {
		sb.append(name.length);
		sb.append(name);
	}

}
