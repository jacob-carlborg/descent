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
import descent.core.Signature;
import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.Arguments;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.LINK;
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
import descent.internal.compiler.parser.TypeReturn;
import descent.internal.compiler.parser.TypeSArray;
import descent.internal.compiler.parser.TypeSlice;
import descent.internal.compiler.parser.TypeTuple;
import descent.internal.compiler.parser.TypeTypeof;
import descent.internal.core.util.Util;

public class InternalSignature {
	
	private IJavaProject javaProject;
	
	public InternalSignature(IJavaProject javaProject) {
		this.javaProject = javaProject;
	}
	
	public static Type toType(String signature, final ASTNodeEncoder encoder) {
		return toType(signature, encoder, 0, 0);
	}
	
	public static Type toType(String signature, final ASTNodeEncoder encoder, final int startPosition, final int length) {
		final Stack<Stack<Type>> stack = new Stack<Stack<Type>>();
		stack.push(new Stack<Type>());
		
		final Stack<Stack<Integer>> modifiers = new Stack<Stack<Integer>>();
		
		final Stack<Objects> tiargsStack = new Stack<Objects>();
		
		SignatureProcessor.process(signature, false /* don't want sub-signatures */, 
			new SignatureRequestorAdapter() {
				@Override
				public void acceptPrimitive(TypeBasic type) {
					Stack<Type> sub = stack.peek();
					sub.push(type);
				}
				@Override
				public void acceptAutomaticTypeInference() {
					Stack<Type> sub = stack.peek();
					sub.push(null);
				}
				@Override
				public void acceptPointer(String signature) {
					Stack<Type> sub = stack.peek();
					TypePointer type = new TypePointer(sub.pop());
					type.setSourceRange(startPosition, length);
					sub.push(type);
				}
				@Override
				public void acceptStaticArray(char[] dimension, String signature) {
					Stack<Type> sub = stack.peek();
					TypeSArray type = new TypeSArray(sub.pop(), encoder.decodeExpression(dimension), encoder);
					type.setSourceRange(startPosition, length);
					sub.push(type);
				}
				@Override
				public void acceptDynamicArray(String signature) {
					Stack<Type> sub = stack.peek();
					TypeDArray type = new TypeDArray(sub.pop());
					type.setSourceRange(startPosition, length);
					sub.push(type);
				}
				@Override
				public void acceptAssociativeArray(String signature) {
					Stack<Type> sub = stack.peek();
					TypeAArray type = new TypeAArray(sub.pop(), sub.pop());
					type.setSourceRange(startPosition, length);
					sub.push(type);
				}
				@Override
				public void acceptTuple(String signature, int numberOftypes) {
					Stack<Type> sub = stack.peek();
					
					Arguments args = new Arguments(numberOftypes);
					for (int i = 0; i < numberOftypes; i++) {
						Argument argument = new Argument(0, sub.pop(), null, null);
						argument.setSourceRange(startPosition, length);
						args.add(0, argument);
					}
					
					TypeTuple type = new TypeTuple(args);
					type.setSourceRange(startPosition, length);
					sub.push(type);
				}
				@Override
				public void acceptConst(String signature) {
					Stack<Type> sub = stack.peek();
					Type type = sub.peek();
					// TypeBasic is like a singleton, and we don't want to modify all of them
					if (type instanceof TypeBasic) {
						type = new TypeBasic(type.singleton);
						type.setSourceRange(startPosition, length);
						type.mod |= Type.MODconst;
						sub.pop();
						sub.push(type);
					} else {
						type.mod |= Type.MODconst;
					}
				}
				@Override
				public void acceptInvariant(String signature) {
					Stack<Type> sub = stack.peek();
					Type type = sub.peek();
					// TypeBasic is like a singleton, and we don't want to modify all of them
					if (type instanceof TypeBasic) {
						type = new TypeBasic(type.singleton);
						type.setSourceRange(startPosition, length);
						type.mod |= Type.MODinvariant;
						sub.pop();
						sub.push(type);
					} else {
						type.mod |= Type.MODinvariant;
					}
				}
				@Override
				public void acceptTypeof(char[] expression, String signature) {
					Stack<Type> sub = stack.peek();
					TypeTypeof type = new TypeTypeof(null, 0, encoder.decodeExpression(expression), encoder);
					type.setSourceRange(startPosition, length);
					sub.push(type);
				}
				@Override
				public void acceptTypeofReturn() {
					Stack<Type> sub = stack.peek();
					TypeReturn type = new TypeReturn(null, 0);
					type.setSourceRange(startPosition, length);
					sub.push(type);
				}
				@Override
				public void acceptSlice(char[] lwr, char[] upr, String signature) {
					Stack<Type> sub = stack.peek();
					TypeSlice type = new TypeSlice(sub.pop(), encoder.decodeExpression(lwr), encoder.decodeExpression(upr), encoder);
					type.setSourceRange(startPosition, length);
					sub.push(type);
				}
				@Override
				public void acceptIdentifier(char[][] compoundName, String signature) {
					Stack<Type> sub = stack.peek();
					
					if (sub.size() > 0 && sub.get(sub.size() - 1) instanceof TypeInstance) {
						TypeInstance ti = (TypeInstance) sub.get(sub.size() - 1);
						for(char[] name : compoundName) {
							IdentifierExp id = new IdentifierExp(name);
							id.setSourceRange(startPosition, length);
							ti.idents.add(id);
						}
					} else {
						TypeIdentifier type = new TypeIdentifier(null, 0, compoundName[0]);
						for (int i = 1; i < compoundName.length; i++) {
							IdentifierExp id = new IdentifierExp(compoundName[i]);
							id.setSourceRange(startPosition, length);
							type.idents.add(id);
						}
						
						sub.push(type);
					}
				}
				@Override
				public void acceptDelegate(String signature) {
					Stack<Type> sub = stack.peek();
					TypeDelegate type = new TypeDelegate(sub.pop());
					type.setSourceRange(startPosition, length);
					sub.push(type);
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
						arg.setSourceRange(startPosition, length);
						args.add(arg);
					}
					
					// TODO Descent: for now assume everything has D linkage so that deco
					// comparisons work
					link = LINK.LINKd;
					
					TypeFunction type = new TypeFunction(args, tret, 'Z' - argumentBreak, link);
					type.setSourceRange(startPosition, length);
					
					sub = stack.peek();
					sub.push(type);
				}
				@Override
				public void enterTemplateInstance() {
					tiargsStack.add(new Objects(3));
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
					Type previousType = previous.pop();
					if (previousType instanceof TypeIdentifier) {
						TypeIdentifier typeIdent = (TypeIdentifier) previousType;
						
						if (typeIdent.idents == null || typeIdent.idents.isEmpty()) {
							TemplateInstance templInstance = new TemplateInstance(null, 0, typeIdent.ident, encoder);
							templInstance.setSourceRange(startPosition, length);
							templInstance.tiargs = tiargs;
							
							TypeInstance typeInstance = new TypeInstance(null, 0, templInstance);
							typeInstance.setSourceRange(startPosition, length);
							previous.push(typeInstance);
						} else {
							TemplateInstance templInstance = new TemplateInstance(null, 0, typeIdent.idents.get(typeIdent.idents.size() - 1), encoder);
							templInstance.setSourceRange(startPosition, length);
							templInstance.tiargs = tiargs;
							
							TemplateInstanceWrapper wrapper = new TemplateInstanceWrapper(null, 0, templInstance);
							wrapper.setSourceRange(startPosition, length);
							typeIdent.idents.set(typeIdent.idents.size() - 1, wrapper);
							
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
					} else {
						TypeInstance typeIdent = (TypeInstance) previousType;
						
						TemplateInstance templInstance = new TemplateInstance(null, 0, typeIdent.idents.get(typeIdent.idents.size() - 1), encoder);
						templInstance.setSourceRange(startPosition, length);
						templInstance.tiargs = tiargs;
						TemplateInstanceWrapper wrapper = new TemplateInstanceWrapper(null, 0, templInstance);
						wrapper.setSourceRange(startPosition, length);
						typeIdent.idents.set(typeIdent.idents.size() - 1, wrapper);
						previous.push(typeIdent);
					}
				}
			});
		
		Type ret = stack.peek().pop();
		return ret;
	}
	
	public static void appendPackageName(char[] packageName, StringBuilder sb) {
		sb.append(Signature.C_MODULE);
		
		char[][] pieces = CharOperation.splitOn('.', packageName);
		for (int i = 0; i < pieces.length; i++) {
			appendName(pieces[i], sb);
		}
	}
	
	public static void appendName(char[] name, StringBuilder sb) {
		sb.append(name.length);
		sb.append(name);
	}
	
	public static TemplateParameter toTemplateParameter(String signature, final String defaultValue, final ASTNodeEncoder encoder) {
		return toTemplateParameter(signature, defaultValue, encoder, 0, 0);
	}

	public static TemplateParameter toTemplateParameter(String signature, final String defaultValue, final ASTNodeEncoder encoder, final int startPosition, final int length) {
		final TemplateParameter[] param = { null };
		final Stack<Stack<Type>> stack = new Stack<Stack<Type>>();
		stack.push(new Stack<Type>());
		
		final Stack<Stack<Integer>> modifiers = new Stack<Stack<Integer>>();	
		
		final Expression[] specValue = { null };
		
		SignatureProcessor.process(signature, false /* don't want sub-signatures */,  
			new SignatureRequestorAdapter() {
				@Override
				public void acceptPrimitive(TypeBasic type) {
					Stack<Type> sub = stack.peek();
					sub.push(type);
				}
				@Override
				public void acceptAutomaticTypeInference() {
					Stack<Type> sub = stack.peek();
					sub.push(null);
				}
				@Override
				public void acceptPointer(String signature) {
					Stack<Type> sub = stack.peek();
					TypePointer type = new TypePointer(sub.pop());
					type.setSourceRange(startPosition, length);
					sub.push(type);
				}
				@Override
				public void acceptStaticArray(char[] dimension, String signature) {
					Stack<Type> sub = stack.peek();
					TypeSArray type = new TypeSArray(sub.pop(), encoder.decodeExpression(dimension), encoder);
					type.setSourceRange(startPosition, length);
					sub.push(type);
				}
				@Override
				public void acceptDynamicArray(String signature) {
					Stack<Type> sub = stack.peek();
					TypeDArray type = new TypeDArray(sub.pop());
					type.setSourceRange(startPosition, length);
					sub.push(type);
				}
				@Override
				public void acceptAssociativeArray(String signature) {
					Stack<Type> sub = stack.peek();
					TypeAArray type = new TypeAArray(sub.pop(), sub.pop());
					type.setSourceRange(startPosition, length);
					sub.push(type);
				}
				@Override
				public void acceptTuple(String signature, int numberOftypes) {
					Stack<Type> sub = stack.peek();
					
					Arguments args = new Arguments(numberOftypes);
					for (int i = 0; i < numberOftypes; i++) {
						Argument argument = new Argument(0, sub.pop(), null, null);
						argument.setSourceRange(startPosition, length);
						args.add(0, argument);
					}
					
					TypeTuple type = new TypeTuple(args);
					type.setSourceRange(startPosition, length);
					sub.push(type);
				}
				@Override
				public void acceptTypeof(char[] expression, String signature) {
					Stack<Type> sub = stack.peek();
					TypeTypeof type = new TypeTypeof(null, 0, encoder.decodeExpression(expression), encoder);
					type.setSourceRange(startPosition, length);
					sub.push(type);
				}
				@Override
				public void acceptTypeofReturn() {
					Stack<Type> sub = stack.peek();
					TypeReturn type = new TypeReturn(null, 0);
					type.setSourceRange(startPosition, length);
					sub.push(type);
				}
				@Override
				public void acceptConst(String signature) {
					Stack<Type> sub = stack.peek();
					Type type = sub.peek();
					// TypeBasic is like a singleton, and we don't want to modify all of them
					if (type instanceof TypeBasic) {
						type = new TypeBasic(type.singleton);
						type.setSourceRange(startPosition, length);
						type.mod |= Type.MODconst;
						sub.pop();
						sub.push(type);
					} else {
						type.mod |= Type.MODconst;
					}
				}
				@Override
				public void acceptInvariant(String signature) {
					Stack<Type> sub = stack.peek();
					Type type = sub.peek();
					// TypeBasic is like a singleton, and we don't want to modify all of them
					if (type instanceof TypeBasic) {
						type = new TypeBasic(type.singleton);
						type.setSourceRange(startPosition, length);
						type.mod |= Type.MODinvariant;
						sub.pop();
						sub.push(type);
					} else {
						type.mod |= Type.MODinvariant;
					}
				}
				@Override
				public void acceptSlice(char[] lwr, char[] upr, String signature) {
					Stack<Type> sub = stack.peek();
					TypeSlice type = new TypeSlice(sub.pop(), encoder.decodeExpression(lwr), encoder.decodeExpression(upr), encoder);
					type.setSourceRange(startPosition, length);
					sub.push(type);
				}
				@Override
				public void acceptIdentifier(char[][] compoundName, String signature) {
					Stack<Type> sub = stack.peek();
					
					TypeIdentifier type = new TypeIdentifier(null, 0, compoundName[compoundName.length - 1]);
					type.setSourceRange(startPosition, length);
					for (int i = 0; i < compoundName.length - 1; i++) {
						IdentifierExp id = new IdentifierExp(compoundName[i]);
						id.setSourceRange(startPosition, length);
						type.idents.add(id);
					}
					
					sub.push(type);
				}
				@Override
				public void acceptDelegate(String signature) {
					Stack<Type> sub = stack.peek();
					TypeDelegate type = new TypeDelegate(sub.pop());
					type.setSourceRange(startPosition, length);
					sub.push(type);
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
						arg.setSourceRange(startPosition, length);
						args.add(arg);
					}
					
					TypeFunction type = new TypeFunction(args, tret, 'Z' - argumentBreak, link);
					type.setSourceRange(startPosition, length);
					
					sub = stack.peek();
					sub.push(type);
				}
				@Override
				public void acceptTemplateTupleParameter() {
					param[0] = new TemplateTupleParameter(null, 0, null);
					param[0].setSourceRange(startPosition, length);
				}
				@Override
				public void exitTemplateTypeParameter(String signature) {
					Stack<Type> types = stack.peek();
					Type type = types.isEmpty() ? null : types.get(0);				
					Type def = defaultValue == null ? null : toType(defaultValue, encoder, startPosition,  length);
					
					param[0] = new TemplateTypeParameter(null, 0, null, type, def);
					param[0].setSourceRange(startPosition, length);
				}
				@Override
				public void exitTemplateAliasParameter(String signature) {
					Stack<Type> types = stack.peek();
					Type type = types.isEmpty() ? null : types.get(0);
					Type def = defaultValue == null ? null : toType(defaultValue, encoder, startPosition, length);
					
					// SEMANTIC signature
					param[0] = new TemplateAliasParameter(null, 0, null, null, type, def);
					param[0].setSourceRange(startPosition, length);
				}
				@Override
				public void acceptTemplateValueParameterSpecificValue(char[] exp) {
					specValue[0] = encoder.decodeExpression(exp);
				}
				@Override
				public void exitTemplateValueParameter(String signature) {
					Stack<Type> types = stack.peek();
					Type type = types.get(0);
					Expression def = defaultValue == null ? null : encoder.decodeDefaultInitExpression(defaultValue.toCharArray());
					
					param[0] = new TemplateValueParameter(null, 0, null, type, specValue[0], def, encoder);
					param[0].setSourceRange(startPosition, length);
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
					.getAllPackageFragmentRoots()) {
				IPackageFragment pack = root.getPackageFragment(packages);
				if (pack.exists()) {
					// found it
					descent.core.ICompilationUnit unit = pack
							.getClassFile(filename);
					if (!unit.exists()) {
						// For .di files
						unit = pack.getClassFile(filename + "i");
						if (!unit.exists()) {
							unit = pack.getCompilationUnit(filename);
							if (!unit.exists()) {
								// For .di files
								unit = pack.getCompilationUnit(filename + "i");
								if (!unit.exists()) {
									continue;
								}
							}
						}
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
		
		if (start == end && child instanceof ISourceReference 
				&& ((ISourceReference) child).getSourceRange().getOffset() == start) {
			return child;
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
					// It might be that the next element's start is the one we are looking for
					if (start == end && mid + 1 < a.length && ((ISourceReference) a[mid + 1]).getSourceRange().getOffset() == start) {
						return a[mid + 1]; // key found
					} else {
						return a[mid]; // key found
					}
				} else {
					if (range.getOffset() + range.getLength() == start) {
						return a[mid + 1]; // key found
					} else {
						return null;
					}
				}
			}
		}
		return null; // key not found.
	}

}
