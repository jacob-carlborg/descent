package descent.internal.core;

import java.util.Stack;

import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.Arguments;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.ISignatureConstants;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.TemplateAliasParameter;
import descent.internal.compiler.parser.TemplateParameter;
import descent.internal.compiler.parser.TemplateTupleParameter;
import descent.internal.compiler.parser.TemplateTypeParameter;
import descent.internal.compiler.parser.TemplateValueParameter;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeAArray;
import descent.internal.compiler.parser.TypeBasic;
import descent.internal.compiler.parser.TypeDArray;
import descent.internal.compiler.parser.TypeDelegate;
import descent.internal.compiler.parser.TypeFunction;
import descent.internal.compiler.parser.TypeIdentifier;
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
		final Stack<Stack<Type>> stack = new Stack<Stack<Type>>();
		stack.push(new Stack<Type>());
		
		final Stack<Stack<Integer>> modifiers = new Stack<Stack<Integer>>();		
		
		final ASTNodeEncoder encoder = new ASTNodeEncoder();
		SignatureProcessor.process(signature, new SignatureRequestorAdapter() {
			@Override
			public void acceptPrimitive(TypeBasic type) {
				Stack<Type> sub = stack.peek();
				sub.push(type);
			}
			@Override
			public void acceptPointer(String signature) {
				Stack<Type> sub = stack.peek();
				sub.push(new TypePointer(sub.pop()));
			}
			@Override
			public void acceptStaticArray(char[] dimension, String signature) {
				Stack<Type> sub = stack.peek();
				sub.push(new TypeSArray(sub.pop(), encoder.decodeExpression(dimension)));
			}
			@Override
			public void acceptDynamicArray(String signature) {
				Stack<Type> sub = stack.peek();
				sub.push(new TypeDArray(sub.pop()));
			}
			@Override
			public void acceptAssociativeArray(String signature) {
				Stack<Type> sub = stack.peek();
				sub.push(new TypeAArray(sub.pop(), sub.pop()));
			}
			@Override
			public void acceptTypeof(char[] expression, String signature) {
				Stack<Type> sub = stack.peek();
				sub.push(new TypeTypeof(Loc.ZERO, encoder.decodeExpression(expression)));
			}
			@Override
			public void acceptSlice(char[] lwr, char[] upr, String signature) {
				Stack<Type> sub = stack.peek();
				sub.push(new TypeSlice(sub.pop(), encoder.decodeExpression(lwr), encoder.decodeExpression(upr)));
			}
			@Override
			public void acceptIdentifier(char[][] compoundName, String signature) {
				Stack<Type> sub = stack.peek();
				
				TypeIdentifier type = new TypeIdentifier(Loc.ZERO, compoundName[compoundName.length - 1]);
				for (int i = 0; i < compoundName.length - 1; i++) {
					type.idents.add(new IdentifierExp(compoundName[i]));
				}
				
				sub.push(type);
			}
			@Override
			public void acceptDelegate(String signature) {
				Stack<Type> sub = stack.peek();
				sub.push(new TypeDelegate(sub.pop()));
			}
			@Override
			public void enterFunctionType() {
				Stack<Type> sub = new Stack<Type>();
				stack.push(sub);
				
				modifiers.push(new Stack<Integer>());
			}
			@Override
			public void acceptArgumentModifier(int stc) {
				modifiers.peek().push(stc);
			}
			@Override
			public void exitFunctionType(LINK link, char argumentBreak, String signature) {
				Stack<Type> sub = stack.pop();
				Stack<Integer> modifiersSub = modifiers.pop();
				
				Type tret = sub.pop();
				
				Arguments args = new Arguments(sub.size());
				for (int i = 0; i < sub.size(); i++) {
					// TODO signature default arg
					Argument arg = new Argument(modifiersSub.get(i), sub.get(i), null, null);
					args.add(arg);
				}
				
				TypeFunction type = new TypeFunction(args, tret, 'Z' - argumentBreak, link);
				
				sub = stack.peek();
				sub.push(type);
			}
		});
		
		return stack.peek().pop();
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

	public static TemplateParameter toTemplateParameter(String signature) {
		final TemplateParameter[] param = { null };
		final Stack<Stack<Type>> stack = new Stack<Stack<Type>>();
		stack.push(new Stack<Type>());
		
		final Stack<Stack<Integer>> modifiers = new Stack<Stack<Integer>>();	
		
		final ASTNodeEncoder encoder = new ASTNodeEncoder();
		
		final Expression[] specValue = { null };
		
		SignatureProcessor.process(signature, new SignatureRequestorAdapter() {
			@Override
			public void acceptPrimitive(TypeBasic type) {
				Stack<Type> sub = stack.peek();
				sub.push(type);
			}
			@Override
			public void acceptPointer(String signature) {
				Stack<Type> sub = stack.peek();
				sub.push(new TypePointer(sub.pop()));
			}
			@Override
			public void acceptStaticArray(char[] dimension, String signature) {
				Stack<Type> sub = stack.peek();
				sub.push(new TypeSArray(sub.pop(), encoder.decodeExpression(dimension)));
			}
			@Override
			public void acceptDynamicArray(String signature) {
				Stack<Type> sub = stack.peek();
				sub.push(new TypeDArray(sub.pop()));
			}
			@Override
			public void acceptAssociativeArray(String signature) {
				Stack<Type> sub = stack.peek();
				sub.push(new TypeAArray(sub.pop(), sub.pop()));
			}
			@Override
			public void acceptTypeof(char[] expression, String signature) {
				Stack<Type> sub = stack.peek();
				sub.push(new TypeTypeof(Loc.ZERO, encoder.decodeExpression(expression)));
			}
			@Override
			public void acceptSlice(char[] lwr, char[] upr, String signature) {
				Stack<Type> sub = stack.peek();
				sub.push(new TypeSlice(sub.pop(), encoder.decodeExpression(lwr), encoder.decodeExpression(upr)));
			}
			@Override
			public void acceptIdentifier(char[][] compoundName, String signature) {
				Stack<Type> sub = stack.peek();
				
				TypeIdentifier type = new TypeIdentifier(Loc.ZERO, compoundName[compoundName.length - 1]);
				for (int i = 0; i < compoundName.length - 1; i++) {
					type.idents.add(new IdentifierExp(compoundName[i]));
				}
				
				sub.push(type);
			}
			@Override
			public void acceptDelegate(String signature) {
				Stack<Type> sub = stack.peek();
				sub.push(new TypeDelegate(sub.pop()));
			}
			@Override
			public void enterFunctionType() {
				Stack<Type> sub = new Stack<Type>();
				stack.push(sub);
				
				modifiers.push(new Stack<Integer>());
			}
			@Override
			public void acceptArgumentModifier(int stc) {
				modifiers.peek().push(stc);
			}
			@Override
			public void exitFunctionType(LINK link, char argumentBreak, String signature) {
				Stack<Type> sub = stack.pop();
				Stack<Integer> modifiersSub = modifiers.pop();
				
				Type tret = sub.pop();
				
				Arguments args = new Arguments(sub.size());
				for (int i = 0; i < sub.size(); i++) {
					// TODO signature default arg
					Argument arg = new Argument(modifiersSub.get(i), sub.get(i), null, null);
					args.add(arg);
				}
				
				TypeFunction type = new TypeFunction(args, tret, 'Z' - argumentBreak, link);
				
				sub = stack.peek();
				sub.push(type);
			}
			@Override
			public void acceptTemplateTupleParameter() {
				param[0] = new TemplateTupleParameter(Loc.ZERO, null);
			}
			@Override
			public void exitTemplateTypeParameter(String signature) {
				Stack<Type> types = stack.peek();
				Type type = types.isEmpty() ? null : types.get(0); 
				
				param[0] = new TemplateTypeParameter(Loc.ZERO, null, type, null);
			}
			@Override
			public void exitTemplateAliasParameter(String signature) {
				Stack<Type> types = stack.peek();
				Type type = types.isEmpty() ? null : types.get(0); 
				
				param[0] = new TemplateAliasParameter(Loc.ZERO, null, type, null);
			}
			@Override
			public void acceptTemplateValueParameterSpecificValue(char[] exp) {
				specValue[0] = encoder.decodeExpression(exp);
			}
			@Override
			public void exitTemplateValueParameter(String signature) {
				Stack<Type> types = stack.peek();
				Type type = types.get(0); 
				
				param[0] = new TemplateValueParameter(Loc.ZERO, null, type, specValue[0], null);
			}
			
		});
		
		return param[0];
	}

}
