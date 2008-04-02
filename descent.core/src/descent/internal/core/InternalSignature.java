package descent.internal.core;

import java.util.Stack;

import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IPackageFragment;
import descent.core.IPackageFragmentRoot;
import descent.core.IParent;
import descent.core.ISourceRange;
import descent.core.ISourceReference;
import descent.core.JavaModelException;
import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.Arguments;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.ISignatureConstants;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Objects;
import descent.internal.compiler.parser.TemplateAliasParameter;
import descent.internal.compiler.parser.TemplateInstance;
import descent.internal.compiler.parser.TemplateInstanceWrapper;
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
import descent.internal.compiler.parser.TypeInstance;
import descent.internal.compiler.parser.TypePointer;
import descent.internal.compiler.parser.TypeSArray;
import descent.internal.compiler.parser.TypeSlice;
import descent.internal.compiler.parser.TypeTypeof;
import descent.internal.core.util.Util;

public class InternalSignature implements ISignatureConstants {
	
	private IJavaProject javaProject;
	
	public InternalSignature(IJavaProject javaProject) {
		this.javaProject = javaProject;
	}
	
	public static Type toType(String signature) {
		final Stack<Stack<Type>> stack = new Stack<Stack<Type>>();
		stack.push(new Stack<Type>());
		
		final Stack<Stack<Integer>> modifiers = new Stack<Stack<Integer>>();
		
		final Stack<Objects> tiargsStack = new Stack<Objects>();
		
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
				
				TypeIdentifier type = new TypeIdentifier(Loc.ZERO, compoundName[0]);
				for (int i = 1; i < compoundName.length; i++) {
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
				if (modifiers.isEmpty()) {
					System.out.println(1);
				}
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
			public void enterTemplateInstance() {
				tiargsStack.add(new Objects());
			}
			@Override
			public void enterTemplateInstanceType() {
				stack.push(new Stack<Type>());
			}
			@Override
			public void exitTemplateInstanceType(String signature) {
				Stack<Type> sub = stack.pop();
				Type type = sub.pop();
				tiargsStack.peek().add(type);
			}
			@Override
			public void enterTemplateInstanceSymbol() {
				stack.push(new Stack<Type>());
			}
			@Override
			public void exitTemplateInstanceSymbol(String string) {
				Stack<Type> sub = stack.pop();
				Type type = sub.pop();
				tiargsStack.peek().add(type);
			}
			@Override
			public void acceptTemplateInstanceValue(char[] exp, String signature) {
				tiargsStack.peek().add(encoder.decodeExpression(exp));
			}
			@Override
			public void exitTemplateInstance(String signature) {
				Objects tiargs = tiargsStack.pop();
				
				Stack<Type> previous = stack.peek();
				TypeIdentifier typeIdent = (TypeIdentifier) previous.pop();
				
				if (typeIdent.idents == null || typeIdent.idents.isEmpty()) {
					TemplateInstance templInstance = new TemplateInstance(Loc.ZERO, typeIdent.ident);
					templInstance.tiargs = tiargs;
					
					if (previous.isEmpty()) {
						TypeInstance typeInstance = new TypeInstance(Loc.ZERO, templInstance);
						previous.push(typeInstance);
					} else {
						TypeInstance previousInstance = (TypeInstance) previous.peek();
						previousInstance.idents.add(new TemplateInstanceWrapper(Loc.ZERO, templInstance));
					}
				} else {
					TemplateInstance templInstance = new TemplateInstance(Loc.ZERO, typeIdent.idents.get(typeIdent.idents.size() - 1));
					templInstance.tiargs = tiargs;
					
					typeIdent.idents.set(typeIdent.idents.size() - 1, new TemplateInstanceWrapper(Loc.ZERO, templInstance));
					
					if (previous.isEmpty()) {
						previous.push(typeIdent);
					} else {
						TypeIdentifier previousIdent = (TypeIdentifier) previous.peek();
						previousIdent.idents.add(typeIdent.ident);
						for(IdentifierExp ident : typeIdent.idents) {
							previousIdent.idents.add(ident);
						}
					}
				}
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

	public static TemplateParameter toTemplateParameter(String signature, final String defaultValue) {
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
				Type def = defaultValue == null ? null : toType(defaultValue);
				
				param[0] = new TemplateTypeParameter(Loc.ZERO, null, type, def);
			}
			@Override
			public void exitTemplateAliasParameter(String signature) {
				Stack<Type> types = stack.peek();
				Type type = types.isEmpty() ? null : types.get(0);
				Type def = defaultValue == null ? null : toType(defaultValue);
				
				param[0] = new TemplateAliasParameter(Loc.ZERO, null, type, def);
			}
			@Override
			public void acceptTemplateValueParameterSpecificValue(char[] exp) {
				specValue[0] = encoder.decodeExpression(exp);
			}
			@Override
			public void exitTemplateValueParameter(String signature) {
				Stack<Type> types = stack.peek();
				Type type = types.get(0);
				Expression def = defaultValue == null ? null : encoder.decodeExpression(defaultValue.toCharArray());
				
				param[0] = new TemplateValueParameter(Loc.ZERO, null, type, specValue[0], def);
			}
			
		});
		
		return param[0];
	}
	
	public descent.core.ICompilationUnit getCompilationUnit(String moduleName) {
		String packages;
		String filename;

		int indexOfDot = moduleName.lastIndexOf('.');
		if (indexOfDot == -1) {
			packages = "";
			filename = moduleName + ".d";
		} else {
			packages = moduleName.substring(0, indexOfDot);
			filename = moduleName.substring(indexOfDot + 1) + ".d";
		}

		try {
			for (IPackageFragmentRoot root : javaProject
					.getPackageFragmentRoots()) {
				IPackageFragment pack = root.getPackageFragment(packages);
				if (pack.exists()) {
					// found it
					descent.core.ICompilationUnit unit = pack
							.getCompilationUnit(filename);
					if (!unit.exists()) {
						unit = pack.getClassFile(filename);
						if (!unit.exists())
							continue;
					}

					return unit;
				}
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}

		return null;
	}

	public IJavaElement binarySearch(IJavaElement parent, int start, int end)
			throws JavaModelException {
		if (!(parent instanceof IParent)) {
			return null;
		}

		IJavaElement[] children = ((IParent) parent).getChildren();
		if (children.length == 0) {
			return null;
		}
		
		IJavaElement child = binarySearch(children, start, end);
		if (child == null) {
			return null;
		}
		
		if (child instanceof IParent) {
			IParent childAsParent = (IParent) child;
			if (childAsParent.hasChildren()) {
				IJavaElement found = binarySearch(child, start, end);
				if (found != null) {
					return found;
				} else {
					return child;
				}
			} else {
				return child;
			}
		} else {
			return child;
		}
	}

	private static IJavaElement binarySearch(IJavaElement[] a, int start, int end) throws JavaModelException {
		int low = 0;
		int high = a.length - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			ISourceReference midVal = (ISourceReference) a[mid];
			ISourceRange range = midVal.getSourceRange();

			if (range.getOffset() + range.getLength() < start) {
				low = mid + 1;
			} else if (range.getOffset() > end) {
				high = mid - 1;
			} else {
				if (range.getOffset() <= start
						&& end <= range.getOffset() + range.getLength()) {
					return a[mid]; // key found	
				} else {
					return null;
				}
			}
		}
		return null; // key not found.
	}

}
