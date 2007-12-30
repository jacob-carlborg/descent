package descent.internal.compiler.lookup;

import java.util.Stack;

import descent.core.Flags;
import descent.core.ICompilationUnit;
import descent.core.IField;
import descent.core.IImportDeclaration;
import descent.core.IInitializer;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IMember;
import descent.core.IMethod;
import descent.core.IPackageFragment;
import descent.core.IParent;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.Arguments;
import descent.internal.compiler.parser.AttribDeclaration;
import descent.internal.compiler.parser.ClassDeclarations;
import descent.internal.compiler.parser.FuncAliasDeclaration;
import descent.internal.compiler.parser.FuncLiteralDeclaration;
import descent.internal.compiler.parser.HashtableOfCharArrayAndObject;
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
import descent.internal.compiler.parser.IStaticCtorDeclaration;
import descent.internal.compiler.parser.IStructDeclaration;
import descent.internal.compiler.parser.ISymbolDeclaration;
import descent.internal.compiler.parser.ITemplateDeclaration;
import descent.internal.compiler.parser.ITypedefDeclaration;
import descent.internal.compiler.parser.IUnionDeclaration;
import descent.internal.compiler.parser.IVarDeclaration;
import descent.internal.compiler.parser.Id;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Import;
import descent.internal.compiler.parser.IntegerExp;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Module;
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
import descent.internal.compiler.parser.TypeEnum;
import descent.internal.compiler.parser.TypeFunction;
import descent.internal.compiler.parser.TypePointer;
import descent.internal.compiler.parser.TypeSArray;
import descent.internal.compiler.parser.TypeStruct;
import descent.internal.compiler.parser.WithScopeSymbol;
import descent.internal.core.JavaElementFinder;
import descent.internal.core.util.Util;

public class RDsymbol extends RNode implements IDsymbol {
	
	protected IDsymbol parent;
	protected IdentifierExp ident;
	
	// This hashtable is here to:
	// - speed up searches
	// - avoid creating duplicated classes
	protected HashtableOfCharArrayAndObject childrenCache;

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
		// TODO Auto-generated method stub
		return null;
	}
	
	public IJavaElement getJavaElement() {
		return element;
	}

	public IModule getModule() {
		return SemanticMixin.getModule(this);
	}
	
	public String getSignature() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	public Loc loc() {
		// TODO Auto-generated method stub
		return null;
	}

	public String locToChars(SemanticContext context) {
		// TODO Auto-generated method stub
		return null;
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
		if (!(element instanceof IParent)) {
			return null;
		}
		
		if (childrenCache == null) {
			childrenCache = new HashtableOfCharArrayAndObject();
		} else {
			Object result = childrenCache.get(ident);
			if (result != null) {
				return (IDsymbol) result;
			}
		}
		
		String sident = new String(ident);		
		IParent parent = (IParent) element;
		return searchInChildren(parent, ident, sident);
	}
	
	private IDsymbol searchInChildren(IParent parent, char[] ident, String sident) {
		try {
			IJavaElement[] children = parent.getChildren();
			for(IJavaElement child : children) {
				IParent searchInChildren = JavaElementFinder.mustSearchInChildren(child);
				if (searchInChildren != null) {
					IDsymbol result = searchInChildren(searchInChildren, ident, sident);
					if (result != null) {
						return result;
					}
				}
				
				String elementName = child.getElementName();
				if (elementName.equals(sident)) {
					IDsymbol result = toDsymbol(child);
					childrenCache.put(ident, result);
					return result;
				}
			}
		} catch (JavaModelException e) {
			Util.log(e, "Exception retrieveing children");
		}
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

	protected IDsymbol toDsymbol(IJavaElement element) throws JavaModelException {
		IDsymbol symbol = null;
		
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
			if (type.isClass()) {
				symbol = new RClassDeclaration(type, context);
			} else if (type.isInterface()) {
				symbol = new RInterfaceDeclaration(type, context);
			} else if (type.isStruct()) {
				symbol = new RStructDeclaration(type, context);
			} else if (type.isUnion()) {
				symbol = new RUnionDeclaration(type, context);
			} else if (type.isEnum()) {
				symbol = new REnumDeclaration(type, context);
			} else if (type.isTemplate()) {
				symbol = new RTemplateDeclaration(type, context);
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
			} else if (field.isTemplateMixin()) {
				// TODO should never hit this, since it will already be expanded
				// But check...
				throw new IllegalStateException("Should not happen");
			} else {
				throw new IllegalStateException("Should not happen");
			}
			break;
		case IJavaElement.METHOD:
			IMethod method = (IMethod) element;
			if (method.isMethod()) {
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
		
		if (symbol != null) {
			symbol.parent(this);
		}
		
		return symbol;
	}
	
	protected Type getTypeFromSignature(String signature) {
		signature = JavaElementFinder.correct(signature);
		
		Type type = context.signatureToTypeCache.get(signature);
		if (type != null) {
			return type;
		}
		
		// TODO optimize using IJavaProject#find and NameLookup
		// TODO make an "extract number" function in order to avoid duplication
		try {
			if (signature == null || signature.length() == 0) {
				// TODO signal error
				type = Type.tint32;
			} else {
				char first = signature.charAt(0);
				
				switch(first) {
				case 'E':   // enum
				case 'C':   // class
				case 'S':   // struct
				case 'T': { // typedef
					Object current = element.getJavaProject();
					
					int i;
					for(i = 1; i < signature.length(); i++) {
						char c = signature.charAt(i);
						int n = 0;
						while(Character.isDigit(c)) {
							n = 10 * n + (c - '0');
							i++;
							c = signature.charAt(i);
						}
						String name = signature.substring(i, i + n);
						current = findChild(current, name);
						if (current == null) {
							// TODO signal error
							break;
						}
						i += n - 1;
					}
					
					if (current != null && current instanceof IDsymbol) {
						IDsymbol symbol = (IDsymbol) current;
						switch(first) {
						case 'E':
							IEnumDeclaration e = symbol.isEnumDeclaration();
							if (e != null) {
								type = new TypeEnum(e);
								type.deco = JavaElementFinder.uncorrect(signature.substring(0, i));
							}
							break;
						case 'C':
							IClassDeclaration c = symbol.isClassDeclaration();
							if (c != null) {
								type = new TypeClass(c);
								type.deco = JavaElementFinder.uncorrect(signature.substring(0, i));
							}
							break;
						case 'S':
							IStructDeclaration s = symbol.isStructDeclaration();
							if (s != null) {
								type = new TypeStruct(s);
								type.deco = JavaElementFinder.uncorrect(signature.substring(0, i));
							}
							break;
						}						
					}
					break;
				}
				case 'D': { // delegate
					type = new TypeDelegate(getTypeFromSignature(signature.substring(1)));
					type.deco = signature.substring(0, type.next.deco.length() + 1);
					break;
				}
				case 'P': { // pointer
					type = new TypePointer(getTypeFromSignature(signature.substring(1)));
					type.deco = signature.substring(0, type.next.deco.length() + 1);
					break;
				}
				case 'A': { // dynamic array
					type = new TypeDArray(getTypeFromSignature(signature.substring(1)));
					type.deco = signature.substring(0, type.next.deco.length() + 1);
					break;
				}
				case 'G': { // static array
					int n = 0;
					int i;
					for(i = 1; i < signature.length(); i++) {
						char c = signature.charAt(i);
						while(Character.isDigit(c)) {
							n = 10 * n + (c - '0');
							i++;
							c = signature.charAt(i);
						}
						break;
					}
					
					type = new TypeSArray(getTypeFromSignature(signature.substring(i)), new IntegerExp(n));
					type.deco = signature.substring(0, i + type.next.deco.length());
					break;
				}
				case 'H': {// associative array
					Type k = getTypeFromSignature(signature.substring(1));
					Type v = getTypeFromSignature(signature.substring(1 + k.deco.length()));
					type = new TypeAArray(k, v);
					type.deco = signature.substring(0, k.deco.length() + v.deco.length() + 1);
					break;
				}
				case 'F': // Type function
				case 'U':
				case 'W':
				case 'V':
				case 'R':
					LINK link;
					switch(first) {
					case 'F': link = LINK.LINKd; break;
					case 'U': link = LINK.LINKc; break;
					case 'W': link = LINK.LINKwindows; break;
					case 'V': link = LINK.LINKpascal; break;
					case 'R': link = LINK.LINKcpp; break;
					default: throw new IllegalStateException("Should not happen");
					}
					
					Arguments args = new Arguments();
					
					int i = 1;
					Type targ = getTypeFromSignature(signature.substring(i));
					while(targ != null) {
						// TODO default arg
						args.add(new Argument(0, targ, new IdentifierExp(Id.empty), null));
						i += targ.deco.length();
						
						targ = getTypeFromSignature(signature.substring(i));
					}
					
					i++;
					Type tret = getTypeFromSignature(signature.substring(i));
					
					// TODO varargs
					type = new TypeFunction(args, tret, 0, link);
					type.deco = signature.substring(0, i + tret.deco.length());
					break;
				case 'X': // Argument break
				case 'Y':
				case 'Z':
					return null;
				default: // Try with type basic
					char c = signature.charAt(0);
					type = TypeBasic.fromSignature(c);
				}
			}
		} catch (JavaModelException e) {
			Util.log(e);
			type = Type.tint32;
		}
		
		if (type == null) {
			type = Type.tint32;
		}
		
		context.signatureToTypeCache.put(signature, type);
		
		return type;
	}
	
	private Object findChild(Object current, String name) throws JavaModelException {
		if (current instanceof IJavaProject) {
			return findChild((IJavaProject) current, name);
		} else if (current instanceof IPackageFragment) {
			return findChild((IPackageFragment) current, name);
		} else if (current instanceof IDsymbol) {
			return ((IDsymbol) current).search(null, name.toCharArray(), 0, null);
		} else {
			return null;
		}
	}
	
	private Object findChild(IJavaProject project, String name) throws JavaModelException {
		IPackageFragment[] fragments = project.getPackageFragments();
		for(IPackageFragment fragment : fragments) {
			Object child = findChild(fragment, name);
			if (child != null) {
				return child;
			}
		}
		return null;
	}
	
	private Object findChild(IPackageFragment fragment, String name) throws JavaModelException {
		if (fragment.isDefaultPackage()) {
			ICompilationUnit unit;
			
			// First class files, because if a class file is open, then there
			// will be a working copy *which does not have an underlying resource
			// with the CompilationUnit semantics*, and something fails.
			unit = fragment.getClassFile(name + ".d");
			if (unit != null && unit.exists()) {
				return new RModule(unit, context);
			}
			unit = fragment.getCompilationUnit(name + ".d");
			if (unit != null && unit.exists()) {
				return new RModule(unit, context);
			}
		} else if (fragment.getElementName().equals(name)) {
			return fragment;
		}
		
		IJavaElement element = JavaElementFinder.searchInChildren(fragment, name);
		if (element instanceof ICompilationUnit) {
			return new RModule((ICompilationUnit) element, context);
		} else {
			return element;
		}
	}
	
	protected String getTypeDeco(String prefix) {
		Stack<IDsymbol> stack = new Stack<IDsymbol>();
		
		IDsymbol current = this;
		while(current != null) {
			stack.push(current);
			current = current.parent();
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		while(!stack.isEmpty()) {
			IDsymbol s = stack.pop();
			char[] ident = s.ident().ident;
			sb.append(ident.length);
			sb.append(ident);
		}
		String signature = sb.toString();
		return JavaElementFinder.uncorrect(signature);
	}
	
	protected long getFlags() {
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
	
	@Override
	public String toString() {
		return element.toString();
	}

}
