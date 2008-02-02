package descent.internal.compiler.lookup;

import java.util.Stack;

import descent.core.Flags;
import descent.core.ICompilationUnit;
import descent.core.IField;
import descent.core.IImportDeclaration;
import descent.core.IInitializer;
import descent.core.IJavaElement;
import descent.core.IMember;
import descent.core.IMethod;
import descent.core.IPackageFragment;
import descent.core.IParent;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.Arguments;
import descent.internal.compiler.parser.AttribDeclaration;
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.ClassDeclarations;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.FuncAliasDeclaration;
import descent.internal.compiler.parser.FuncLiteralDeclaration;
import descent.internal.compiler.parser.HdrGenState;
import descent.internal.compiler.parser.IAggregateDeclaration;
import descent.internal.compiler.parser.IAliasDeclaration;
import descent.internal.compiler.parser.IArrayScopeSymbol;
import descent.internal.compiler.parser.IClassDeclaration;
import descent.internal.compiler.parser.ICtorDeclaration;
import descent.internal.compiler.parser.IDeclaration;
import descent.internal.compiler.parser.IDsymbol;
import descent.internal.compiler.parser.IEnumDeclaration;
import descent.internal.compiler.parser.IEnumMember;
import descent.internal.compiler.parser.IFuncDeclaration;
import descent.internal.compiler.parser.IInterfaceDeclaration;
import descent.internal.compiler.parser.IModule;
import descent.internal.compiler.parser.INewDeclaration;
import descent.internal.compiler.parser.INode;
import descent.internal.compiler.parser.IPackage;
import descent.internal.compiler.parser.IScopeDsymbol;
import descent.internal.compiler.parser.ISignatureConstants;
import descent.internal.compiler.parser.IStaticCtorDeclaration;
import descent.internal.compiler.parser.IStructDeclaration;
import descent.internal.compiler.parser.ISymbolDeclaration;
import descent.internal.compiler.parser.ITemplateDeclaration;
import descent.internal.compiler.parser.ITypedefDeclaration;
import descent.internal.compiler.parser.IUnionDeclaration;
import descent.internal.compiler.parser.IVarDeclaration;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Import;
import descent.internal.compiler.parser.IntegerExp;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Objects;
import descent.internal.compiler.parser.OutBuffer;
import descent.internal.compiler.parser.PROT;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.STC;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.SemanticMixin;
import descent.internal.compiler.parser.TemplateInstance;
import descent.internal.compiler.parser.TemplateMixin;
import descent.internal.compiler.parser.TupleDeclaration;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeAArray;
import descent.internal.compiler.parser.TypeBasic;
import descent.internal.compiler.parser.TypeClass;
import descent.internal.compiler.parser.TypeDArray;
import descent.internal.compiler.parser.TypeDelegate;
import descent.internal.compiler.parser.TypeFunction;
import descent.internal.compiler.parser.TypeIdentifier;
import descent.internal.compiler.parser.TypeInstance;
import descent.internal.compiler.parser.TypePointer;
import descent.internal.compiler.parser.TypeSArray;
import descent.internal.compiler.parser.WithScopeSymbol;
import descent.internal.compiler.parser.ast.IASTVisitor;
import descent.internal.core.JavaElementFinder;
import descent.internal.core.SignatureProcessor;
import descent.internal.core.SignatureRequestorAdapter;
import descent.internal.core.util.Util;

public abstract class RDsymbol extends RNode implements IDsymbol {
	
	protected IDsymbol parent;
	protected IdentifierExp ident;

	public RDsymbol(IJavaElement element, SemanticContext context) {
		super(element, context);
	}

	public void addLocalClass(ClassDeclarations aclasses, SemanticContext context) {
		// empty
	}

	public int addMember(Scope sc, IScopeDsymbol sd, int memnum, SemanticContext context) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void checkCtorConstInit(SemanticContext context) {
		// empty
	}

	public void checkDeprecated(Scope sc, SemanticContext context, INode reference) {
		SemanticMixin.checkDeprecated(this, sc, context, reference);
	}

	public void defineRef(IDsymbol s) {
		// TODO Auto-generated method stub
		
	}
	
	public String kindForError(SemanticContext context) {
		StringBuilder sb = new StringBuilder();
		String p = locToChars(context);
		if (p != null) {
			sb.append(p);
			sb.append(": ");
		}

		sb.append(kind());
		sb.append(" ");
		sb.append(toPrettyChars(context));
		sb.append(" ");
		return sb.toString();
	}
	
	public IJavaElement getJavaElement() {
		return element;
	}

	public IModule getModule() {
		return SemanticMixin.getModule(this);
	}

	public Type getType() {
		return null;
	}

	public boolean hasPointers(SemanticContext context) {
		return false;
	}

	public IdentifierExp ident() {
		if (ident == null) {
			ident = new IdentifierExp(element.getElementName().toCharArray());
		}
		return ident;
	}
	
	public void ident(IdentifierExp ident) {
		// TODO Auto-generated method stub
		
	}

	public void inlineScan(SemanticContext context) {
		// empty
	}

	public IAggregateDeclaration isAggregateDeclaration() {
		return null;
	}

	public IAliasDeclaration isAliasDeclaration() {
		return null;
	}
	
	public IArrayScopeSymbol isArrayScopeSymbol() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public AttribDeclaration isAttribDeclaration() {
		// TODO Auto-generated method stub
		return null;
	}

	public IClassDeclaration isClassDeclaration() {
		return null;
	}

	public IClassDeclaration isClassMember() {
		if (parent != null) {
			return parent.isClassDeclaration();
		}
		
		return null;
	}

	public ICtorDeclaration isCtorDeclaration() {
		return null;
	}

	public IDeclaration isDeclaration() {
		return null;
	}

	public boolean isDeprecated() {
		try {
			return (element instanceof IMember) && 
				(((IMember) element).getFlags() & Flags.AccDeprecated) != 0;
		} catch (JavaModelException e) {
			Util.log(e);
			return false;
		}
	}

	public IEnumDeclaration isEnumDeclaration() {
		return null;
	}

	public IEnumMember isEnumMember() {
		return null;
	}

	public FuncAliasDeclaration isFuncAliasDeclaration() {
		// TODO Auto-generated method stub
		return null;
	}

	public IFuncDeclaration isFuncDeclaration() {
		return null;
	}

	public FuncLiteralDeclaration isFuncLiteralDeclaration() {
		// TODO Auto-generated method stub
		return null;
	}

	public Import isImport() {
		return null;
	}

	public boolean isImportedSymbol() {
		// TODO Auto-generated method stub
		return false;
	}

	public IInterfaceDeclaration isInterfaceDeclaration() {
		return null;
	}

	public IAggregateDeclaration isMember() {
		IDsymbol parent = toParent();
		return parent != null ? parent.isAggregateDeclaration() : null;
	}

	public IModule isModule() {
		return null;
	}

	public INewDeclaration isNewDeclaration() {
		return null;
	}

	public IPackage isPackage() {
		return null;
	}

	public IScopeDsymbol isScopeDsymbol() {
		return null;
	}

	public IStaticCtorDeclaration isStaticCtorDeclaration() {
		return null;
	}

	public IStructDeclaration isStructDeclaration() {
		return null;
	}

	public ISymbolDeclaration isSymbolDeclaration() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITemplateDeclaration isTemplateDeclaration() {
		return null;
	}

	public TemplateInstance isTemplateInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	public TemplateMixin isTemplateMixin() {
		// TODO Auto-generated method stub
		return null;
	}

	public IAggregateDeclaration isThis() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ITypedefDeclaration isTypedefDeclaration() {
		return null;
	}

	public TupleDeclaration isTupleDeclaration() {
		// TODO Auto-generated method stub
		return null;
	}

	public IUnionDeclaration isUnionDeclaration() {
		return null;
	}

	public IVarDeclaration isVarDeclaration() {
		return null;
	}
	
	public WithScopeSymbol isWithScopeSymbol() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isforwardRef() {
		return false;
	}

	public String kind() {
		return "symbol";
	}

	public Loc loc() {
		// TODO Auto-generated method stub
		return null;
	}

	public String locToChars(SemanticContext context) {
		return getModule().getFullyQualifiedName();
	}

	public String mangle(SemanticContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean needThis() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean oneMember(IDsymbol[] ps, SemanticContext context) {
		return SemanticMixin.oneMember(this, ps, context);
	}

	public IDsymbol parent() {
		return parent;
	}

	public void parent(IDsymbol parent) {
		this.parent = parent;
	}

	public IDsymbol pastMixin() {
		return SemanticMixin.pastMixin(this);
	}

	public PROT prot() {
		if (element instanceof IMember) {
			IMember m = (IMember) element;
			try {
				long flags = m.getFlags();
				if ((flags & Flags.AccPublic) != 0) {
					return PROT.PROTpublic;
				} else if ((flags & Flags.AccPrivate) != 0) {
					return PROT.PROTprivate;
				} else if ((flags & Flags.AccPackage) != 0) {
					return PROT.PROTpackage;
				} else if ((flags & Flags.AccProtected) != 0) {
					return PROT.PROTprotected;
				} else if ((flags & Flags.AccExport) != 0) {
					return PROT.PROTexport;
				}
			} catch (JavaModelException e) {
				Util.log(e);
			}
		}
		return PROT.PROTpublic;
	}

	public IDsymbol search(Loc loc, IdentifierExp ident, int flags, SemanticContext context) {
		return search(loc, ident.ident, flags, context);
	}

	public IDsymbol search(Loc loc, char[] ident, int flags, SemanticContext context) {
		return null;
	}

	public IDsymbol searchX(Loc loc, Scope sc, IdentifierExp id, SemanticContext context) {
		return SemanticMixin.searchX(this, loc, sc, id, context);
	}

	public void semantic(Scope scope, SemanticContext context) {
		// empty
	}

	public void semantic2(Scope scope, SemanticContext context) {
		// empty
	}

	public void semantic3(Scope scope, SemanticContext context) {
		// empty
	}

	public IDsymbol syntaxCopy(IDsymbol s, SemanticContext context) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean synthetic() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void synthetic(boolean synthetic) {
		// TODO Auto-generated method stub
		
	}

	public IDsymbol toAlias(SemanticContext context) {
		return this;
	}

	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		SemanticMixin.toCBuffer(this, buf, hgs, context);
	}
	
	@Override
	public String toChars(SemanticContext context) {
		return SemanticMixin.toChars(this, context);
	}

	public IDsymbol toParent() {
		return SemanticMixin.toParent(this);
	}

	public IDsymbol toParent2() {
		return SemanticMixin.toParent2(this);
	}

	public String toPrettyChars(SemanticContext context) {
		// TODO semantic
		return toChars(context);
	}
	
	public Type type() {
		return null;
	}

	protected IDsymbol toDsymbol(IJavaElement element) {
		IDsymbol symbol = null;
		
		try {
			switch(element.getElementType()) {
			case IJavaElement.JAVA_MODEL:
			case IJavaElement.JAVA_PROJECT:
			case IJavaElement.PACKAGE_FRAGMENT_ROOT:
			default:
				return null;
			case IJavaElement.PACKAGE_FRAGMENT:
				symbol = new RPackage((IPackageFragment) element, context);
				break;
			case IJavaElement.COMPILATION_UNIT:
			case IJavaElement.CLASS_FILE:
				symbol = new RModule((ICompilationUnit) element, context);
				break;
			case IJavaElement.TYPE:
				IType type = (IType) element;
				if (type.isTemplate()) {
					symbol = new RTemplateDeclaration(type, context);
				} else if (type.isClass()) {
					symbol = new RClassDeclaration(type, context);
				} else if (type.isInterface()) {
					symbol = new RInterfaceDeclaration(type, context);
				} else if (type.isStruct()) {
					symbol = new RStructDeclaration(type, context);
				} else if (type.isUnion()) {
					symbol = new RUnionDeclaration(type, context);
				} else if (type.isEnum()) {
					symbol = new REnumDeclaration(type, context);
				} else {
					throw new IllegalStateException("Should not happen");
				}
				break;
			case IJavaElement.FIELD:
				IField field = (IField) element;
				if (field.isVariable()) {
					symbol = new RVarDeclaration(field, context);
				} else if (field.isEnumConstant()) {
					symbol = new REnumMember(field, context);
				} else if (field.isAlias()) {
					symbol = new RAliasDeclaration(field, context);
				} else if (field.isTypedef()) {
					symbol = new RTypedefDeclaration(field, context);
				} else {
					return null;
				}
				break;
			case IJavaElement.METHOD:
				IMethod method = (IMethod) element;
				if (method.isTemplate()) {
					symbol = new RTemplateDeclaration(method, context);
				} else if (method.isMethod()) {
					symbol = new RFuncDeclaration(method, context);
				} else if (method.isConstructor()) {
					symbol = new RCtorDeclaration(method, context);
				} else if (method.isDestructor()) {
					symbol = new RDtorDeclaration(method, context);
				} else if (method.isNew()) {
					symbol = new RNewDeclaration(method, context);
				} else if (method.isDelete()) {
					symbol = new RDeleteDeclaration(method, context);
				} else {
					throw new IllegalStateException("Should not happen");
				}
				break;
			case IJavaElement.INITIALIZER:
				IInitializer init = (IInitializer) element;
				return null;
			case IJavaElement.IMPORT_DECLARATION:
				IImportDeclaration imp = (IImportDeclaration) element;
				
				// TODO improve performance of this
				StringBuilder sb = new StringBuilder();
				sb.append("import ");
				sb.append(imp.getElementName());
				sb.append(";");
				
				Parser parser = new Parser(Util.getApiLevel(element), sb.toString());
				parser.nextToken();
				Module mod = parser.parseModuleObj();
				return mod.members.get(0);
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
		
		if (symbol != null) {
			symbol.parent(this);
		}
		
		return symbol;
	}
	
	// Fake type to hold a dsymbol
	class DsymbolType extends Type {
		
		final IDsymbol symbol;
		final TemplateInstance tempinst;

		public DsymbolType(IDsymbol symbol, TemplateInstance tempinst) {
			super(null, null);
			this.symbol = symbol;
			this.tempinst = tempinst;
		}

		@Override
		protected void appendSignature(StringBuilder sb) {
		}

		@Override
		public String getSignature() {
			if (symbol instanceof IFuncDeclaration) {
				return ((IFuncDeclaration) symbol).getSignature();
			}
			return null;
		}

		@Override
		public int getNodeType() {
			return 0;
		}

		@Override
		protected void accept0(IASTVisitor visitor) {
		}
		
		@Override
		public String toString() {
			return symbol.toString();
		}
		
	}
	
	// TODO Descent templates!
	private class SignatureToType extends SignatureRequestorAdapter {
		
		private Stack<Stack<Type>> typesStack = new Stack<Stack<Type>>();
		private Stack<Stack<Integer>> modifiersStack = new Stack<Stack<Integer>>();
		private Stack<String> templateStack = new Stack<String>();
		private Stack<Objects> instanceStack = new Stack<Objects>();
		
		public SignatureToType() {
			typesStack.push(new Stack<Type>());
			modifiersStack.push(new Stack<Integer>());
		}
		
		public Type getType() {
			if (typesStack.isEmpty()) {
				return null;
			} else {
				Stack<Type> stack = typesStack.pop();
				if (stack.isEmpty()) {
					return null;
				}
				
				Type t = stack.pop();
				if (t instanceof DsymbolType) {
					DsymbolType dt = (DsymbolType) t;
					IDsymbol sym = dt.symbol;
					if (sym instanceof IEnumDeclaration) {
						return sym.getType();
					} else if (sym instanceof IFuncDeclaration) {
						// if it's a function, then probably RAliasDeclaration.toAlias was invoked
						return dt;
					} else {
						return sym.type();
					}
				} else {
					return t;
				}
			}
		}
		
		public void acceptModule(char[][] compoundName, String signature) {
			Type type = context.signatureToTypeCache.get(signature);
			if (type == null) {			
				IModule module = context.moduleFinder.findModule(compoundName, context);
				if (module != null) {
					type = new DsymbolType(module, null);
					context.signatureToTypeCache.put(signature, type);
				}
			}
			if (type != null) {
				typesStack.peek().push(type);
			}
		}
		
		public void acceptSymbol(char kind, char[] name, int startPosition, String signature) {
			Stack<Type> stack = typesStack.peek();
			if (stack.isEmpty()) {
				return;
			}
			
			Type t = context.signatureToTypeCache.get(signature);
			if (t == null) {
				if (typesStack.isEmpty()) {
					return;
				}
				
				TypeFunction tf = null;
				if (kind == ISignatureConstants.FUNCTION ||
						kind == ISignatureConstants.TEMPLATED_FUNCTION) {
					tf = (TypeFunction) stack.pop();
				}
				
				t = stack.pop();
				
				TemplateInstance tempinst = null;
				
				IDsymbol parent;
				if (t instanceof TypeInstance) {
					TypeInstance ti = (TypeInstance) t;
					tempinst = ti.tempinst;
					parent = ti.tempinst.tempdecl;
				} else {
					if (!(t instanceof DsymbolType)) {
						return;
					} else {
						parent = ((DsymbolType) t).symbol;
					}
				}
				
				if (startPosition >= 0) {
					IJavaElement element = parent.getJavaElement();
					element = getFinder().findChild(element, startPosition);
					if (element == null) {
						return;
					}
					
					IDsymbol symbol = toDsymbol(element);
					symbol.parent(parent);
					t = new DsymbolType(symbol, tempinst);
					context.signatureToTypeCache.put(signature, t);
				} else {
					if (kind == ISignatureConstants.FUNCTION ||
							kind == ISignatureConstants.TEMPLATED_FUNCTION) {
						IJavaElement element = parent.getJavaElement();
						if (element == null) {
							return;
						}
						
						if (tf == null) {
							return;
						}
						
						int paramsSize = tf.parameters == null ? 0 : tf.parameters.size();
						
						String[] paramsAndRetTypes = new String[paramsSize + 1];
						for (int i = 0; i < paramsSize; i++) {
							paramsAndRetTypes[i] = tf.parameters.get(i).getSignature();
						}
						paramsAndRetTypes[paramsAndRetTypes.length - 1] = tf.next.getSignature();
						
						if (kind == ISignatureConstants.FUNCTION) {
							element = getFinder().findFunction((IParent) element, new String(name), paramsAndRetTypes);
						} else {
							String[] paramsTypes = new String[templateStack.size()];
							int i = paramsTypes.length - 1;
							while(!templateStack.isEmpty()) {
								paramsTypes[i] = templateStack.pop();
								i--;
							}
							
							element = getFinder().findTemplatedFunction((IParent) element, new String(name), paramsAndRetTypes, paramsTypes);
						}
						if (element != null) {
							IDsymbol symbol = toDsymbol(element);
							symbol.parent(parent);
							t = new DsymbolType(symbol, tempinst);
							context.signatureToTypeCache.put(signature, t);
						}
					} else {
						IDsymbol symbol = null;
						if (kind == ISignatureConstants.TEMPLATE ||
								kind == ISignatureConstants.TEMPLATED_CLASS ||
								kind == ISignatureConstants.TEMPLATED_STRUCT ||
								kind == ISignatureConstants.TEMPLATED_INTERFACE ||
								kind == ISignatureConstants.TEMPLATED_UNION) {
							
							String[] paramsTypes = new String[templateStack.size()];
							int i = paramsTypes.length - 1;
							while(!templateStack.isEmpty()) {
								paramsTypes[i] = templateStack.pop();
								i--;
							}
							
							IJavaElement element = parent.getJavaElement();
							if (kind == ISignatureConstants.TEMPLATE) {
								element = getFinder().findTemplate((IParent) element, new String(name), paramsTypes);
							} else {
								element = getFinder().findTemplatedAggregate((IParent) element, new String(name), paramsTypes);
							}
							if (element != null) {
								symbol = toDsymbol(element);
								symbol.parent(parent);
							}
						} else {
							symbol = parent.search(Loc.ZERO, name, 0, context);
						}
						if (symbol != null) {
							t = new DsymbolType(symbol, tempinst);
							context.signatureToTypeCache.put(signature, t);
						}
					}
				}
			} else {
				if (kind == ISignatureConstants.FUNCTION) {
					stack.pop();
				}
				stack.pop();
			}
			if (t != null) {
				stack.push(t);
			}
		}

		public void acceptArgumentBreak(char c) {
			// empty
		}

		public void acceptArgumentModifier(int stc) {
			modifiersStack.peek().push(stc);
		}

		public void acceptAssociativeArray(String signature) {
			Type type = context.signatureToTypeCache.get(signature);
			if (type == null) {
				Type t = getPreviousType();
				if (t == null) return;
				Type index = getPreviousType();
				if (index == null) return;
				
				type = new TypeAArray(t, index);
				((TypeAArray) type).key = index.toBasetype(context);
				type = type.merge(context);
				context.signatureToTypeCache.put(signature, type);
			} else {
				getPreviousType();
				getPreviousType();
			}
			typesStack.peek().push(type);
		}

		public void acceptDelegate(String signature) {
			Type type = context.signatureToTypeCache.get(signature);
			if (type == null) {
				Type next = getPreviousType();
				if (next == null) return;
				
				type = new TypeDelegate(next);
				type = type.merge(context);
				context.signatureToTypeCache.put(signature, type);
			} else {
				getPreviousType();
			}
			typesStack.peek().push(type);
		}

		public void acceptDynamicArray(String signature) {
			Type type = context.signatureToTypeCache.get(signature);
			if (type == null) {
				Type next = getPreviousType();
				if (next == null) return;
				
				type = new TypeDArray(next);
				type = type.merge(context);
				context.signatureToTypeCache.put(signature, type);
			} else {
				getPreviousType();
			}
			typesStack.peek().push(type);
		}
		
		public void enterFunctionType() {
			typesStack.push(new Stack<Type>());
			modifiersStack.push(new Stack<Integer>());
		}

		public void exitFunctionType(LINK link, String signature) {
			Type type = context.signatureToTypeCache.get(signature);
			if (type == null) {
				if (typesStack.isEmpty()) {
					return;
				}
				
				Arguments arguments = new Arguments();
				
				Type retType = getPreviousType();
				if (retType == null) {
					System.out.println(1);
				}
				
				// TODO varargs
				type = new TypeFunction(arguments, retType, 0, link);
				while(!typesStack.peek().isEmpty()) {
					Type argType = getPreviousType();
					if (modifiersStack.peek().isEmpty()) {
						return;
					}
					
					// TODO default value
					arguments.add(0, new Argument(modifiersStack.peek().pop(), argType, IdentifierExp.EMPTY, null));
				}
				type = type.merge(context);
				context.signatureToTypeCache.put(signature, type);
			}
			
			// TODO varargs
			typesStack.pop();
			typesStack.peek().push(type);
			modifiersStack.pop();
		}

		public void acceptPointer(String signature) {
			Type type = context.signatureToTypeCache.get(signature);
			if (type == null) {
				Type next = getPreviousType();
				if (next == null) return;
				
				type = new TypePointer(next);
				type = type.merge(context);
				context.signatureToTypeCache.put(signature, type);
			} else {
				getPreviousType();
			}
			typesStack.peek().push(type);
		}

		public void acceptPrimitive(TypeBasic type) {
			typesStack.peek().push(type);
		}

		public void acceptStaticArray(int dimension, String signature) {
			Type type = context.signatureToTypeCache.get(signature);
			if (type == null) {
				Type next = getPreviousType();
				if (next == null) return;
				
				type = new TypeSArray(next, new IntegerExp(dimension));
				type = type.merge(context);
				context.signatureToTypeCache.put(signature, type);
			} else {
				getPreviousType();
			}
			typesStack.peek().push(type);
		}
		
		@Override
		public void acceptIdentifier(char[] name, String signature) {
			Type type = context.signatureToTypeCache.get(signature);
			if (type == null) {
				type = new TypeIdentifier(Loc.ZERO, name);
				context.signatureToTypeCache.put(signature, type);
			}
			typesStack.peek().push(type);
		}
		
		@Override
		public void acceptTemplateTupleParameter() {
			templateStack.push(String.valueOf(ISignatureConstants.TEMPLATE_TUPLE_PARAMETER));
		}
		
		@Override
		public void exitTemplateAliasParameter(String signature) {
			templateStack.push(signature);
		}
		
		@Override
		public void exitTemplateTypeParameter(String signature) {
			templateStack.push(signature);
		}
		
		@Override
		public void exitTemplateValueParameter(String signature) {
			Stack<Type> stack = typesStack.peek();
			if (stack.isEmpty()) {
				return;
			}
			stack.pop();
			
			templateStack.push(signature);
		}
		
		@Override
		public void enterTemplateInstance() {
			typesStack.push(new Stack<Type>());
			instanceStack.push(new Objects());
		}
		
		@Override
		public void exitTemplateInstanceSymbol(String string) {
			Stack<Type> stack = typesStack.peek();
			if (stack.isEmpty()) {
				return;
			}
			Type type = stack.pop();
			if (!(type instanceof DsymbolType)) {
				return;
			}
			
			instanceStack.peek().add(((DsymbolType) type).symbol);
		}
		
		@Override
		public void exitTemplateInstanceTypeParameter(String signature) {
			Stack<Type> stack = typesStack.peek();
			if (stack.isEmpty()) {
				return;
			}
			Type type = stack.pop();
			instanceStack.peek().add(type);
		}
		
		@Override
		public void acceptTemplateInstanceValue(Expression exp, String signature) {
			instanceStack.peek().add(exp);
		}
		
		@Override
		public void exitTemplateInstance(String signature) {
			typesStack.pop();
			
			Type type = typesStack.peek().pop();
			
			if (!(type instanceof DsymbolType)) {
				return;
			}
			
			DsymbolType dstype = (DsymbolType) type;
			IDsymbol sym = dstype.symbol;
			if (!(sym instanceof ITemplateDeclaration)) {
				return;
			}
			
			ITemplateDeclaration tempdecl;
			
//			if (dstype.tempinst == null) {
				tempdecl = (ITemplateDeclaration) sym;
//			} else {
//				tempdecl = dstype.tempinst.tempdecl;
//			}
			
			Objects tiargs = instanceStack.pop();
			
			TemplateInstance tempinst = new TemplateInstance(Loc.ZERO, tempdecl, tiargs);
			tempinst.semantic(((RModule) getModule()).getScope(), context);
			
			Dsymbols members = tempdecl.members();
			
			if (!members.isEmpty() && members.get(0) instanceof ClassDeclaration) {
				ClassDeclaration cd = (ClassDeclaration) members.get(0);
				cd.parent = tempinst;
				
				TypeClass typeClass = new TypeClass(cd);
				typeClass = (TypeClass) typeClass.merge(context);
				
				typesStack.peek().push(typeClass);
			} else {
				typesStack.peek().push(new TypeInstance(Loc.ZERO, tempinst));
			}
		}
		
		@Override
		public void enterTemplateParameters() {
			typesStack.push(new Stack<Type>());
			modifiersStack.push(new Stack<Integer>());
		}
		
		@Override
		public void exitTemplateParameters() {
			typesStack.pop();
			modifiersStack.pop();
		}
		
		private Type getPreviousType() {
			if (typesStack.isEmpty()) {
				return null;
			}
			
			Stack<Type> stack = typesStack.peek();
			if (stack.isEmpty()) {
				return null;
			}
			
			Type t = stack.pop();
			if (t instanceof DsymbolType) {
				DsymbolType dt = (DsymbolType) t;
				t = dt.symbol.type();
				if (t == null) {
					t = dt.symbol.getType();
				}
				return t;
			} else {
				return t;
			}
		}
		
	}
	
	protected JavaElementFinder getFinder() {
		return ((RModule) getModule()).finder;
	}
	
	protected Type getTypeFromSignature(String signature) {
		if (signature == null || signature.length() == 0) {
			return null;
		}
		
		Type type = context.signatureToTypeCache.get(signature);
		if (type == null) {
			SignatureToType tfs = new SignatureToType();
			try {
				SignatureProcessor.process(signature, tfs);
				type = tfs.getType();
				if (type != null && !(type instanceof DsymbolType)) {
					type = type.merge(context);
				}
			} catch (IllegalArgumentException e) {
				Util.log(e, "processing signature: " + signature);
			}
		}
		
		if (type == null) {
			type = Type.terror;
		}
		
		if (type instanceof DsymbolType && !(((DsymbolType) type).symbol instanceof IFuncDeclaration)) {
			Type t = ((DsymbolType) type).symbol.type();
			if (t == null) {
				t = ((DsymbolType) type).symbol.getType();
			}
			type = t;
		}
		
		return type;
	}
	
	public long getFlags() {
		try {
			if (element instanceof IMember) {
				IMember f = (IMember) element;
				return f.getFlags();
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
		return 0;
	}
	
	protected int getStorageClass() {
		long flags = getFlags();
		int storage_class = 0;
		
		if ((flags & Flags.AccAbstract) != 0) storage_class |= STC.STCabstract;
		if ((flags & Flags.AccAuto) != 0) storage_class |= STC.STCauto;
		// TODO STC.STCcomdat
		if ((flags & Flags.AccConst) != 0) storage_class |= STC.STCconst;
		// TODO STC.STCctorinit
		if ((flags & Flags.AccDeprecated) != 0) storage_class |= STC.STCdeprecated;
		if ((flags & Flags.AccExtern) != 0) storage_class |= STC.STCextern;
		// TODO STC.STCfield
		if ((flags & Flags.AccFinal) != 0) storage_class |= STC.STCfinal;
		// TODO STC.STCforeach
		if ((flags & Flags.AccIn) != 0) storage_class |= STC.STCin;
		if ((flags & Flags.AccInvariant) != 0) storage_class |= STC.STCinvariant;
		if ((flags & Flags.AccLazy) != 0) storage_class |= STC.STClazy;
		if ((flags & Flags.AccOut) != 0) storage_class |= STC.STCout;
		if ((flags & Flags.AccOverride) != 0) storage_class |= STC.STCoverride;
		// TODO STC.STCparameter
		if ((flags & Flags.AccRef) != 0) storage_class |= STC.STCref;
		if ((flags & Flags.AccScope) != 0) storage_class |= STC.STCscope;
		if ((flags & Flags.AccStatic) != 0) storage_class |= STC.STCstatic;
		if ((flags & Flags.AccSynchronized) != 0) storage_class |= STC.STCsynchronized;
		// TODO STC.STCtemplateparameter
		// TODO STC.STCundefined
		// TODO STC.STCvariadic
		
		return storage_class;
	}
	
	public String getSignature() {
		return SemanticMixin.getSignature(this);
	}
	
	public void appendSignature(StringBuilder sb) {
		SemanticMixin.appendSignature(this, sb);
	}
	
	public IDsymbol effectiveParent() {
		// It's impossible that my parent is a FuncLiteralDeclaration
		return parent;
	}
	
	public boolean templated() {
		return false;
	}
	
	@Override
	public String toString() {
		return element.toString();
	}

}
