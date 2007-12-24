package descent.internal.compiler.lookup;

import java.util.Stack;

import descent.core.Flags;
import descent.core.ICompilationUnit;
import descent.core.IField;
import descent.core.IInitializer;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IMember;
import descent.core.IMethod;
import descent.core.IPackageFragment;
import descent.core.IParent;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.AttribDeclaration;
import descent.internal.compiler.parser.ClassDeclarations;
import descent.internal.compiler.parser.FuncAliasDeclaration;
import descent.internal.compiler.parser.FuncLiteralDeclaration;
import descent.internal.compiler.parser.HashtableOfCharArrayAndObject;
import descent.internal.compiler.parser.HdrGenState;
import descent.internal.compiler.parser.IAggregateDeclaration;
import descent.internal.compiler.parser.IAliasDeclaration;
import descent.internal.compiler.parser.IClassDeclaration;
import descent.internal.compiler.parser.ICtorDeclaration;
import descent.internal.compiler.parser.IDeclaration;
import descent.internal.compiler.parser.IDsymbol;
import descent.internal.compiler.parser.IEnumDeclaration;
import descent.internal.compiler.parser.IEnumMember;
import descent.internal.compiler.parser.IFuncDeclaration;
import descent.internal.compiler.parser.IImport;
import descent.internal.compiler.parser.IInterfaceDeclaration;
import descent.internal.compiler.parser.IModule;
import descent.internal.compiler.parser.INewDeclaration;
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
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.OutBuffer;
import descent.internal.compiler.parser.PROT;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.TemplateInstance;
import descent.internal.compiler.parser.TemplateMixin;
import descent.internal.compiler.parser.TupleDeclaration;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeBasic;
import descent.internal.compiler.parser.TypeClass;
import descent.internal.compiler.parser.TypeEnum;
import descent.internal.compiler.parser.TypeStruct;
import descent.internal.core.util.Util;

public class RDsymbol extends RNode implements IDsymbol {
	
	protected final IJavaElement element;
	
	protected IDsymbol parent;
	protected IdentifierExp ident;
	protected HashtableOfCharArrayAndObject childrenCache;

	public RDsymbol(IJavaElement element) {
		this.element = element;
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

	public void checkDeprecated(Scope sc, SemanticContext context) {
		// TODO Auto-generated method stub
		
	}

	public void defineRef(IDsymbol s) {
		// TODO Auto-generated method stub
		
	}

	public IModule getModule() {
		IModule m;
		IDsymbol s;

		s = this;
		while (s != null) {
			m = s.isModule();
			if (m != null) {
				return m;
			}
			s = s.parent();
		}
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

	public IImport isImport() {
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

	public boolean isforwardRef() {
		// TODO Auto-generated method stub
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
		ps[0] = this;
		return true;
	}

	public IDsymbol parent() {
		return parent;
	}

	public void parent(IDsymbol parent) {
		this.parent = parent;
	}

	public IDsymbol pastMixin() {
		IDsymbol s = this;
		while (s != null && s.isTemplateMixin() != null) {
			s = s.parent();
		}
		return s;
	}

	public PROT prot() {
		// TODO Auto-generated method stub
		return null;
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
		try {
			IJavaElement[] children = parent.getChildren();
			for(IJavaElement child : children) {
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
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		
	}

	public IDsymbol toParent() {
		return parent != null ? parent.pastMixin() : null;
	}

	public IDsymbol toParent2() {
		IDsymbol s = parent;
		while (s != null && s.isTemplateInstance() != null) {
			s = s.parent();
		}
		return s;
	}

	public String toPrettyChars(SemanticContext context) {
		// TODO semantic
		return toChars(context);
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
			symbol = new RPackage((IPackageFragment) element);
			break;
		case IJavaElement.COMPILATION_UNIT:
		case IJavaElement.CLASS_FILE:
			symbol = new RModule((ICompilationUnit) element);
			break;
		case IJavaElement.TYPE:
			IType type = (IType) element;
			if (type.isClass()) {
				symbol = new RClassDeclaration(type);
			} else if (type.isInterface()) {
				symbol = new RInterfaceDeclaration(type);
			} else if (type.isStruct()) {
				symbol = new RStructDeclaration(type);
			} else if (type.isUnion()) {
				symbol = new RUnionDeclaration(type);
			} else if (type.isEnum()) {
				symbol = new REnumDeclaration(type);
			} else {
				throw new IllegalStateException("Should not happen");
			}
			break;
		case IJavaElement.FIELD:
			IField field = (IField) element;
			if (field.isVariable()) {
				symbol = new RVarDeclaration(field);
			} else if (field.isEnumConstant()) {
				symbol = new REnumMember(field);
			} else if (field.isAlias()) {
				symbol = new RAliasDeclaration(field);
			} else if (field.isTypedef()) {
				symbol = new RTypedefDeclaration(field);
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
				symbol = new RFuncDeclaration(method);
			} else if (method.isConstructor()) {
				symbol = new RCtorDeclaration(method);
			} else if (method.isDestructor()) {
				symbol = new RDtorDeclaration(method);
			} else if (method.isNew()) {
				symbol = new RNewDeclaration(method);
			} else if (method.isDelete()) {
				symbol = new RDeleteDeclaration(method);
			} else {
				throw new IllegalStateException("Should not happen");
			}
			break;
		case IJavaElement.INITIALIZER:
			IInitializer init = (IInitializer) element;
			return null;
		}
		
		if (symbol != null) {
			symbol.parent(this);
		}
		
		return symbol;
	}
	
	protected Type getType(String signature) {
		// TODO optimize using IJavaProject#find and NameLookup
		try {
			if (signature.length() == 0) {
				// Signal error
				return Type.tint32;
			} else if (signature.length() == 1) {
				// It's a basic type
				char c = signature.charAt(0);
				return TypeBasic.fromSignature(c);
			} else {
				char first = signature.charAt(0);
				
				Object current = element.getJavaProject();
				switch(first) {
				case 'E': // enum
				case 'C': // class
				case 'S': // struct
					// It's an enum
					for(int i = 1; i < signature.length(); i++) {
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
					break;
				}
				
				if (current != null && current instanceof IDsymbol) {
					IDsymbol symbol = (IDsymbol) current;
					switch(first) {
					case 'E':
						IEnumDeclaration e = symbol.isEnumDeclaration();
						if (e != null) {
							return new TypeEnum(e);
						}
						break;
					case 'C':
						IClassDeclaration c = symbol.isClassDeclaration();
						if (c != null) {
							return new TypeClass(c);
						}
						break;
					case 'S':
						IStructDeclaration s = symbol.isStructDeclaration();
						if (s != null) {
							return new TypeStruct(s);
						}
						break;
					}
					
				}
				
				return Type.tint32;
			}
		} catch (JavaModelException e) {
			Util.log(e);
			return Type.tint32;
		}
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
			ICompilationUnit unit = fragment.getCompilationUnit(name + ".d");
			if (unit != null) {
				return new RModule(unit);
			}
		} else if (fragment.getElementName().equals(name)) {
			return fragment;
		}
		return null;
	}
	
	protected String getTypeDeco() {
		// Hack to take care of inconsistency of mangleof
		if (parent.isModule() != null && CharOperation.equals(parent.ident().ident, Id.object)) {
			String elemName = element.getElementName();
			if (elemName.equals("Object")) {
				return "6Object";
			}	
		}
		
		Stack<IDsymbol> stack = new Stack<IDsymbol>();
		
		IDsymbol current = this;
		while(current != null) {
			stack.push(current);
			current = current.parent();
		}
		
		StringBuilder sb = new StringBuilder();
		while(!stack.isEmpty()) {
			IDsymbol s = stack.pop();
			char[] ident = s.ident().ident;
			sb.append(ident.length);
			sb.append(ident);
		}
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return element.toString();
	}

}
