package descent.core.dom;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import descent.core.Flags;
import descent.core.ICompilationUnit;
import descent.core.IField;
import descent.core.IInitializer;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IMethod;
import descent.core.IParent;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.core.WorkingCopyOwner;
import descent.core.compiler.CharOperation;
import descent.internal.compiler.env.INameEnvironment;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.AggregateDeclaration;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.CallExp;
import descent.internal.compiler.parser.ConditionalDeclaration;
import descent.internal.compiler.parser.ConditionalStatement;
import descent.internal.compiler.parser.Declaration;
import descent.internal.compiler.parser.DotVarExp;
import descent.internal.compiler.parser.EnumDeclaration;
import descent.internal.compiler.parser.EnumMember;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.ICtorDeclaration;
import descent.internal.compiler.parser.IDsymbol;
import descent.internal.compiler.parser.IFuncDeclaration;
import descent.internal.compiler.parser.IModule;
import descent.internal.compiler.parser.ISignatureConstants;
import descent.internal.compiler.parser.Id;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Import;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.NewExp;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeBasic;
import descent.internal.compiler.parser.TypeExp;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.compiler.parser.VarExp;
import descent.internal.core.ISignatureRequestor;
import descent.internal.core.JavaElement;
import descent.internal.core.JavaElementFinder;
import descent.internal.core.JavaProject;
import descent.internal.core.LocalVariable;
import descent.internal.core.SearchableEnvironment;
import descent.internal.core.SignatureProcessor;
import descent.internal.core.util.Util;

class DefaultBindingResolver extends BindingResolver {
	
	/*
	 * Holds on binding tables that can be shared by several ASTs.
	 */
	static class BindingTables {
	
		/**
		 * This map is used to get a binding from its binding key.
		 */
		Map<String, IBinding> bindingKeysToBindings;
		
		BindingTables() {
			this.bindingKeysToBindings = new HashMap<String, IBinding>();
		}
	
	}
	
	/**
	 * This map is used to get an ast node from its binding (new binding) or DOM
	 */
	Map<IBinding, ASTNode> bindingsToAstNodes;
	
	/*
	 * The shared binding tables accros ASTs.
	 */
	BindingTables bindingTables;
	
	/**
	 * This map is used to retrieve an old ast node using the new ast node. This is not an
	 * identity map.
	 */
	Map<ASTNode, ASTDmdNode> newAstToOldAst;
	
	/**
	 * The working copy owner that defines the context in which this resolver is creating the bindings.
	 */
	WorkingCopyOwner workingCopyOwner;

	/**
	 * The project to lookup types.
	 */
	IJavaProject javaProject;
	
	/**
	 * The finder to lookup inside elements.
	 */
	JavaElementFinder finder;
	
	/**
	 * The name environment to search for compilation units.
	 */
	INameEnvironment environment;
	
	/**
	 * Constructor for DefaultBindingResolver.
	 */
	DefaultBindingResolver(IJavaProject project, WorkingCopyOwner workingCopyOwner, BindingTables bindingTables) {
		this.javaProject = project;
		this.bindingsToAstNodes = new HashMap();
		this.bindingTables = bindingTables;
		this.workingCopyOwner = workingCopyOwner;
		this.newAstToOldAst = new HashMap();
		this.finder = new JavaElementFinder(javaProject, workingCopyOwner);
		try {
			this.environment = new SearchableEnvironment((JavaProject) javaProject, workingCopyOwner);
		} catch (JavaModelException e) {
			Util.log(e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	ASTNode findDeclaringNode(IBinding binding) {
		return bindingsToAstNodes.get(binding);
	}
	
	@Override
	ASTNode findDeclaringNode(String bindingKey) {
		IBinding binding = bindingTables.bindingKeysToBindings.get(bindingKey);
		if (binding != null) {
			return findDeclaringNode(binding);
		}
		return null;
	}
	
	@Override
	public Boolean resolveConditionalDeclaration(descent.core.dom.ConditionalDeclaration declaration) {
		ASTDmdNode old = newAstToOldAst.get(declaration);
		if (!(old instanceof ConditionalDeclaration)) {
			return false;
		}
		
		int c = ((ConditionalDeclaration) old).condition.inc;
		switch(c) {
		case 0: return null;
		case 1: return true;
		case 2: return false;
		}
		
		return null;
	}
	
	@Override
	public Boolean resolveConditionalStatement(descent.core.dom.ConditionalStatement statement) {
		ASTDmdNode old = newAstToOldAst.get(statement);
		if (!(old instanceof ConditionalStatement)) {
			return false;
		}
		
		int c = ((ConditionalStatement) old).condition.inc;
		switch(c) {
		case 0: return null;
		case 1: return true;
		case 2: return false;
		}
		
		return null;
	}
	
	@Override
	ITypeBinding resolveAggregate(descent.core.dom.AggregateDeclaration type) {
		ASTDmdNode old = newAstToOldAst.get(type);
		if (!(old instanceof AggregateDeclaration)) {
			return null;
		}
		
		AggregateDeclaration agg = (AggregateDeclaration) old;
		String key = agg.type().getSignature();
		return (ITypeBinding) resolveBinding(type, key);
	}
	
	@Override
	ITypeBinding resolveEnum(descent.core.dom.EnumDeclaration type) {
		ASTDmdNode old = newAstToOldAst.get(type);
		if (!(old instanceof EnumDeclaration)) {
			return null;
		}
		
		EnumDeclaration e = (EnumDeclaration) old;
		String key = e.type.getSignature();
		return (ITypeBinding) resolveBinding(type, key);
	}
	
	@Override
	IBinding resolveVariable(VariableDeclaration variable) {
		if (variable.fragments().size() == 0) {
			return null;
		}
		
		ASTDmdNode old = newAstToOldAst.get(variable.fragments().get(0));
		if (!(old instanceof VarDeclaration)) {
			return null;
		}
		
		VarDeclaration v = (VarDeclaration) old;
		String key = v.type.getSignature();
		return resolveBinding(variable, key);
	}
	
	@Override
	IVariableBinding resolveVariableFragment(VariableDeclarationFragment variable) {
		ASTDmdNode old = newAstToOldAst.get(variable);
		if (!(old instanceof VarDeclaration)) {
			return null;
		}
		
		IBinding binding = resolveVar((VarDeclaration) old, variable);
		if (binding instanceof IVariableBinding) {
			return (IVariableBinding) binding;
		}
		
		return null;
	}
	
	@Override
	IBinding resolveAlias(AliasDeclaration alias) {
		if (alias.fragments().size() == 0) {
			return null;
		}
		
		ASTDmdNode old = newAstToOldAst.get(alias.fragments().get(0));
		if (!(old instanceof descent.internal.compiler.parser.AliasDeclaration)) {
			return null;
		}
		
		descent.internal.compiler.parser.AliasDeclaration a = (descent.internal.compiler.parser.AliasDeclaration) old;
		IDsymbol elem = a.aliassym;
		if (elem == null) {
			return null;
		}
		
		String signature = elem.getSignature();
		if (elem.getJavaElement() != null) {
			IBinding binding = resolveBinding(elem.getJavaElement(), signature, alias);
			if (binding != null) {
				return binding;
			}
		} else {
			return resolveBinding(signature);
		}
		
		return null;
	}
	
	@Override
	IVariableBinding resolveAliasFragment(AliasDeclarationFragment variable) {
		ASTDmdNode old = newAstToOldAst.get(variable);
		if (!(old instanceof descent.internal.compiler.parser.AliasDeclaration)) {
			return null;
		}
		
		IBinding binding = resolveAlias((descent.internal.compiler.parser.AliasDeclaration) old, variable);
		if (binding instanceof IVariableBinding) {
			return (IVariableBinding) binding;
		}
		
		return null;
	}
	
	@Override
	IBinding resolveTypedef(descent.core.dom.TypedefDeclaration alias) {
		if (alias.fragments().size() == 0) {
			return null;
		}
		
		ASTDmdNode old = newAstToOldAst.get(alias.fragments().get(0));
		if (!(old instanceof descent.internal.compiler.parser.TypedefDeclaration)) {
			return null;
		}
		
		descent.internal.compiler.parser.TypedefDeclaration a = (descent.internal.compiler.parser.TypedefDeclaration) old;
		return resolveType(alias, a.basetype);
	}
	
	@Override
	IVariableBinding resolveTypedefFragment(TypedefDeclarationFragment variable) {
		ASTDmdNode old = newAstToOldAst.get(variable);
		if (!(old instanceof descent.internal.compiler.parser.TypedefDeclaration)) {
			return null;
		}
		
		IBinding binding = resolveTypedef((descent.internal.compiler.parser.TypedefDeclaration) old, variable);
		if (binding instanceof IVariableBinding) {
			return (IVariableBinding) binding;
		}
		
		return null;
	}
	
	@Override
	IBinding resolveName(Name name) {
		ASTNode parent = name.getParent();
		if (parent != null) {
			switch(parent.getNodeType()) {
			case ASTNode.AGGREGATE_DECLARATION:
				return resolveAggregate((descent.core.dom.AggregateDeclaration) parent);
			case ASTNode.ENUM_DECLARATION:
				return resolveEnum((descent.core.dom.EnumDeclaration) parent);
			case ASTNode.VARIABLE_DECLARATION_FRAGMENT:
				return resolveVariableFragment((VariableDeclarationFragment) parent);
			case ASTNode.ALIAS_DECLARATION_FRAGMENT:
				return resolveAliasFragment((AliasDeclarationFragment) parent);
			case ASTNode.TYPEDEF_DECLARATION_FRAGMENT:
				return resolveTypedefFragment((TypedefDeclarationFragment) parent);
			case ASTNode.ENUM_MEMBER:
				return resolveEnumMember((descent.core.dom.EnumMember) parent);
			case ASTNode.SIMPLE_TYPE:
				return resolveType((descent.core.dom.Type) parent);
			case ASTNode.IMPORT:
				return resolveImport((descent.core.dom.Import) parent);
			case ASTNode.QUALIFIED_NAME:
				QualifiedName qName = (QualifiedName) parent;
				if (qName.getName() == name) {
					return resolveName(qName);
				}
				break;
			case ASTNode.FUNCTION_DECLARATION:
				if (name.getLocationInParent() == FunctionDeclaration.NAME_PROPERTY) {
					return resolveMethod((FunctionDeclaration) parent);
				}
				break;
			case ASTNode.ARGUMENT:
				return resolveArgument((descent.core.dom.Argument) parent);
			default:
				if (parent instanceof descent.core.dom.Type) {
					return resolveType((descent.core.dom.Type) parent);
				}
				if (name.isSimpleName()) {
					ASTDmdNode old = newAstToOldAst.get(name);
					if (!(old instanceof IdentifierExp)) {
						return null;
					}
					
					IdentifierExp node = (IdentifierExp) old;
					return resolveIdentifierExp(name, node);
				}
			}
		}
		return null;
	}
	
	@Override
	public IMethodBinding resolveNewExpression(NewExpression expression) {
		ASTDmdNode old = newAstToOldAst.get(expression);
		if (!(old instanceof descent.internal.compiler.parser.NewExp)) {
			return null;
		}
		
		NewExp exp = (NewExp) old;
		ICtorDeclaration ctor = exp.member;
		if (ctor == null) {
			return null;
		}
		
		String signature = ctor.getSignature();
		
		IBinding binding = bindingTables.bindingKeysToBindings.get(signature);
		if (binding != null) {
			return (IMethodBinding) binding;
		}
		
		if (ctor.getJavaElement() != null) {
			binding = new MethodBinding(this, (IMethod) ctor.getJavaElement(), signature);
		} else {
			binding = resolveBinding(signature);
		}
		
		bindingTables.bindingKeysToBindings.put(signature, binding);
		bindingsToAstNodes.put(binding, expression);
		return (IMethodBinding) binding;
	}
	
	@Override
	public IMethodBinding resolveCallExpression(CallExpression expression) {
		ASTDmdNode old = newAstToOldAst.get(expression);
		if (!(old instanceof descent.internal.compiler.parser.CallExp)) {
			return null;
		}
		
		CallExp exp = (CallExp) old;
		if (exp.sourceE1.getResolvedSymbol() == null ||
				!(exp.sourceE1.getResolvedSymbol() instanceof IFuncDeclaration)) {
			return null;
		}
		
		IFuncDeclaration sym = (IFuncDeclaration) exp.sourceE1.getResolvedSymbol();
		String signature = sym.getSignature();
		
		IBinding binding = bindingTables.bindingKeysToBindings.get(signature);
		if (binding != null) {
			return (IMethodBinding) binding;
		}
		
		if (sym.getJavaElement() != null) {
			binding = new MethodBinding(this, (IMethod) sym.getJavaElement(), signature);
		} else {
			binding = resolveBinding(signature);
		}
		
		bindingTables.bindingKeysToBindings.put(signature, binding);
		bindingsToAstNodes.put(binding, expression);
		return (IMethodBinding) binding;
	}

	private IBinding resolveIdentifierExp(ASTNode node, IdentifierExp id) {
		if (id.resolvedSymbol != null) {
			IDsymbol sym = id.resolvedSymbol;
			
			// If it resolves to an opCall, use the parent
			if (sym.isFuncDeclaration() != null && CharOperation.equals(sym.ident().ident, Id.call)) {
				sym = sym.parent();
			}
			
			if (sym.getJavaElement() != null) {
				IJavaElement elem = sym.getJavaElement();
				IBinding binding = resolveBinding(elem, sym.getSignature(), node);
				if (binding != null) {
					return binding;
				}
			} else if (sym instanceof VarDeclaration) {
				IBinding binding = resolveVar((VarDeclaration) sym, node);
				if (binding != null) {
					return binding;
				}
			} else if (sym instanceof descent.internal.compiler.parser.AliasDeclaration) {
				IBinding binding = resolveAlias((descent.internal.compiler.parser.AliasDeclaration) sym, node);
				if (binding != null) {
					return binding;
				}
			} else if (sym instanceof descent.internal.compiler.parser.TypedefDeclaration) {
				IBinding binding = resolveTypedef((descent.internal.compiler.parser.TypedefDeclaration) sym, node);
				if (binding != null) {
					return binding;
				}
			} else {
				IBinding binding = resolveBinding(sym.getSignature());
				if (binding != null) {
					return binding;
				}
			}
		}
		
		Expression resolved = id.resolvedExpression;
		if (resolved == null) {
			return null;
		}
		
		switch(resolved.getNodeType()) {
		case ASTDmdNode.VAR_EXP:
			resolveVarExp((VarExp) resolved);
			break;
		case ASTDmdNode.DOT_VAR_EXP:
			resolveDotVarExp((DotVarExp) resolved);
			break;
		case ASTDmdNode.TYPE_EXP:
			resolveType(node, ((TypeExp) resolved).type);
			break;
		}
		
		return null;
	}
	
	private IBinding resolveBinding(IJavaElement elem, String signature, ASTNode node) {
		IBinding binding = bindingTables.bindingKeysToBindings.get(signature);
		if (binding != null) {
			return binding;
		}
		
		switch(elem.getElementType()) {
		case IJavaElement.COMPILATION_UNIT:
			binding = new PackageBinding(this, (ICompilationUnit) elem, signature);
			break;
		case IJavaElement.TYPE:
			binding = new TypeBinding(this, (IType) elem, signature);
			break;
		case IJavaElement.METHOD:
			binding = new MethodBinding(this, (IMethod) elem, signature);
			break;
		case IJavaElement.FIELD:
			binding = new VariableBinding(this, (IField) elem, false /* not a parameter */, signature);
			break;
		}
		
		if (binding != null) {
			bindingTables.bindingKeysToBindings.put(signature, binding);
			bindingsToAstNodes.put(binding, node);
			return binding;
		}
		
		return binding;
	}

	private IBinding resolveVarExp(VarExp varExp) {
		return null;
	}
	
	private IBinding resolveDotVarExp(DotVarExp dotVarExp) {
		return null;
	}
	
	private IBinding resolveVar(VarDeclaration var, ASTNode node) {
		if (isLocal(var)) {
			return resolveLocalVar(var, node);
		} else {
			String key = var.getSignature();
			return resolveBinding(node, key);
		}
	}
	
	private IBinding resolveAlias(descent.internal.compiler.parser.AliasDeclaration var, ASTNode node) {
		if (isLocal(var)) {
			return resolveLocalAlias(var, node);
		} else {
			String key = var.getSignature();
			return resolveBinding(node, key);
		}
	}
	
	private IBinding resolveTypedef(descent.internal.compiler.parser.TypedefDeclaration var, ASTNode node) {
		if (isLocal(var)) {
			return resolveLocalTypedef(var, node);
		} else {
			String key = var.getSignature();
			return resolveBinding(node, key);
		}
	}
	
	private boolean isLocal(descent.internal.compiler.parser.Declaration node) {
		return node.parent instanceof FuncDeclaration;
	}
	
	private IBinding resolveLocalVar(VarDeclaration var, ASTNode node) {
		return resolveLocal(node, var, Flags.AccDefault);
	}
	
	private IBinding resolveLocalAlias(descent.internal.compiler.parser.AliasDeclaration var, ASTNode node) {
		return resolveLocal(node, var, Flags.AccAlias);
	}
	
	private IBinding resolveLocalTypedef(descent.internal.compiler.parser.TypedefDeclaration var, ASTNode node) {
		return resolveLocal(node, var, Flags.AccTypedef);
	}
	
	private IBinding resolveLocal(ASTNode node, Declaration var, long modifiers) {
		String signature = var.getSignature();
		
		IBinding binding = bindingTables.bindingKeysToBindings.get(signature);
		if (binding != null) {
			return (IVariableBinding) binding;
		}
		
		if (var.type == null) {
			return null;
		}
		
		FuncDeclaration parent = (FuncDeclaration) var.parent;
		JavaElement func = (JavaElement) finder.find(parent.getSignature());
		
		IJavaElement element = new LocalVariable(
				func, 
				var.ident.toString(),
				var.start,
				var.start + var.length - 1,
				var.ident.start,
				var.ident.start + var.ident.length - 1,
				var.type.getSignature(),
				modifiers);
		
		binding = new VariableBinding(this, element, var.isParameter(), var.getSignature());
		bindingTables.bindingKeysToBindings.put(signature, binding);
		bindingsToAstNodes.put(binding, node);
		return binding;
	}
	
	@Override
	IBinding resolveType(descent.core.dom.Type type) {
		ASTDmdNode old = newAstToOldAst.get(type);
		if (old instanceof descent.internal.compiler.parser.Type) {
			return resolveType(type, (Type) old);
		} else if (old instanceof IdentifierExp) {
			return resolveIdentifierExp(type, (IdentifierExp) old);
		} else {
			return null;
		}
	}
	
	private IBinding resolveType(ASTNode node, Type t) {
		if (t == null) {
			return null;
		}
		
		String signature = t.getSignature();
		
		IBinding binding = bindingTables.bindingKeysToBindings.get(signature);
		if (binding != null) {
			return (ITypeBinding) binding;
		}
		
		// First try with the associated java element
		if (t.getJavaElement() != null) {
			IJavaElement elem = t.getJavaElement();
			if (elem.getElementType() == IJavaElement.TYPE) {
				binding = new TypeBinding(this, (IType) elem, signature);
				bindingTables.bindingKeysToBindings.put(signature, binding);
				bindingsToAstNodes.put(binding, node);
				return binding;
			}
		}
		
		// Else, try with the signature
		return resolveBinding(node, signature);
	}
	
	@Override
	IVariableBinding resolveEnumMember(descent.core.dom.EnumMember member) {
		ASTDmdNode old = newAstToOldAst.get(member);
		if (!(old instanceof descent.internal.compiler.parser.EnumMember)) {
			return null;
		}
		
		descent.internal.compiler.parser.EnumMember em = (EnumMember) old;
		String key = em.getSignature();
		return (IVariableBinding) resolveBinding(member, key);
	}
	
	@Override
	IPackageBinding resolveImport(descent.core.dom.Import imp) {
		ASTDmdNode old = newAstToOldAst.get(imp);
		if (!(old instanceof descent.internal.compiler.parser.Import)) {
			return null;
		}
		
		descent.internal.compiler.parser.Import i = (Import) old;
		IModule mod = i.mod;
		if (mod == null) {
			return null;
		}
		
		String signature = mod.getSignature();
		IBinding binding = bindingTables.bindingKeysToBindings.get(signature);
		if (binding != null) {
			return (IPackageBinding) binding;
		}
		
		if (mod.getJavaElement() != null) {
			IJavaElement elem = mod.getJavaElement();
			if (elem instanceof ICompilationUnit) {
				binding = new PackageBinding(this, (ICompilationUnit) elem, signature);
				bindingTables.bindingKeysToBindings.put(signature, binding);
				bindingsToAstNodes.put(binding, imp);
				return (IPackageBinding) binding;
			}
		}
		
		return null;
	}
	
	@Override
	IMethodBinding resolveMethod(FunctionDeclaration method) {
		ASTDmdNode old = newAstToOldAst.get(method);
		if (!(old instanceof FuncDeclaration)) {
			return null;
		}
		
		FuncDeclaration func = (FuncDeclaration) old;
		
		IBinding binding = resolveBinding(method, func.getSignature());
		if (binding instanceof IMethodBinding) {
			return (IMethodBinding) binding;
		}
		
		return null; 
	}
	
	@Override
	public IVariableBinding resolveArgument(descent.core.dom.Argument argument) {
		ASTDmdNode old = newAstToOldAst.get(argument);
		if (!(old instanceof descent.internal.compiler.parser.Argument)) {
			return null;
		}
		
		descent.internal.compiler.parser.Argument arg = (Argument) old;
		if (arg.var != null) {
			if (arg.var instanceof VarDeclaration) {
				IBinding binding = resolveVar((VarDeclaration) arg.var, argument);
				if (binding instanceof IVariableBinding) {
					return (IVariableBinding) binding;
				}
			}
		}
		
		return null;
	}
	
	@Override
	IBinding resolveExpressionType(descent.core.dom.Expression expression) {
		ASTDmdNode old = newAstToOldAst.get(expression);
		if (!(old instanceof descent.internal.compiler.parser.Expression)) {
			return null;
		}
		
		descent.internal.compiler.parser.Expression exp = (Expression) old;
		
		if (expression instanceof Name) {
			IBinding binding = resolveName((Name) expression);
			if (binding != null) {
				if (binding instanceof ITypeBinding) {
					return (ITypeBinding) binding;
				} else if (binding instanceof IVariableBinding) {
					return ((IVariableBinding) binding).getType();
				}
			}
			return null;
		} else {
			return resolveType(expression, exp.type);
		}
	}
	
	/*
	 * Method declared on BindingResolver.
	 */
	@Override
	synchronized void store(ASTNode node, ASTDmdNode oldASTNode) {
		this.newAstToOldAst.put(node, oldASTNode);
	}
	
	IBinding resolveBinding(ASTNode node, String signature) {
		IBinding binding = resolveBinding(signature);
		bindingsToAstNodes.put(binding, node);
		return binding;
	}
	
	/*
	 * Dummy binding for IInitializer, to make SingatureSolver work
	 * for symbols declared inside unittests, invariants, static
	 * ctos and dtors.
	 */
	private class InitializerBinding extends JavaElementBasedBinding {

		public InitializerBinding(IJavaElement element) {
			super(element);
		}

		public String getKey() {
			return null;
		}

		public int getKind() {
			return 0;
		}

		public boolean isEqualTo(IBinding binding) {
			return false;
		}

		public boolean isSynthetic() {
			return false;
		}
		
	}
	
	private class SignatureSolver implements ISignatureRequestor {
		
		private Stack<Stack<IBinding>> stack = new Stack<Stack<IBinding>>();
		
		public SignatureSolver() {
			stack.push(new Stack<IBinding>());
		}
		
		public void acceptModule(char[][] compoundName, String signature) {
			Stack<IBinding> stack = this.stack.peek();
			
			IBinding binding = bindingTables.bindingKeysToBindings.get(signature);
			if (binding == null) {
				ICompilationUnit unit = finder.findCompilationUnit(compoundName);
				if (unit == null) {
					return;
				}
				binding = new PackageBinding(DefaultBindingResolver.this, unit, signature);
				bindingTables.bindingKeysToBindings.put(signature, binding);
			}
			stack.push(binding);
		}
		
		public void acceptSymbol(char type, char[] name, int startPosition, String signature) {
			Stack<IBinding> stack = this.stack.peek();
			
			IBinding binding = bindingTables.bindingKeysToBindings.get(signature);
			if (binding == null) {
				if (stack.isEmpty()) {
					return;
				}
				
				if (startPosition >= 0) {
					if (type == ISignatureConstants.FUNCTION) {
						this.stack.pop();
						stack = this.stack.peek();
						
						if (stack.isEmpty()) {
							return;
						}
					}
					
					binding = stack.pop();
					
					IJavaElement element = binding.getJavaElement();
					element =  JavaElementFinder.findChild(element, startPosition);
					
					if (element instanceof IType) {
						binding = new TypeBinding(DefaultBindingResolver.this, (IType) element, signature);
						bindingTables.bindingKeysToBindings.put(signature, binding);
					} else if (element instanceof IField) {
						binding = new VariableBinding(DefaultBindingResolver.this, element, false, signature);
						bindingTables.bindingKeysToBindings.put(signature, binding);
					} else if (element instanceof IMethod) {
						binding = new MethodBinding(DefaultBindingResolver.this, (IMethod) element, signature);
						bindingTables.bindingKeysToBindings.put(signature, binding);
					} else if (element instanceof IInitializer) {
						binding = new InitializerBinding(element);
						bindingTables.bindingKeysToBindings.put(signature, binding);
					} else {
						return;
					}
					
				} else {
					binding = stack.pop();
					
					if (type == ISignatureConstants.FUNCTION) {
						ITypeBinding func = (ITypeBinding) binding;
						
						String[] paramsAndReturnTypes = new String[func.getParametersTypes().length + 1];
						for(int i = 0; i < func.getParametersTypes().length; i++) {
							paramsAndReturnTypes[i] = func.getParametersTypes()[i].getKey();
						}
						paramsAndReturnTypes[paramsAndReturnTypes.length - 1] = func.getReturnType().getKey();
						
						this.stack.pop();
						stack = this.stack.peek();
						
						if (stack.isEmpty()) {
							return;
						}
						
						IJavaElement element = stack.pop().getJavaElement();
						if (element == null) {
							return;
						}
						
						element = JavaElementFinder.findFunction((IParent) element, new String(name), paramsAndReturnTypes);
						if (element == null) {
							return;
						}
						
						binding = new MethodBinding(DefaultBindingResolver.this, (IMethod) element, signature);
						bindingTables.bindingKeysToBindings.put(signature, binding);
					} else {
						IJavaElement element = binding.getJavaElement(); 
						if (element == null) {
							return;
						}
						
						element = JavaElementFinder.findChild(element, new String(name));
						if (element == null) {
							return;
						}
						
						if (element instanceof IType) {
							binding = new TypeBinding(DefaultBindingResolver.this, (IType) element, signature);
							bindingTables.bindingKeysToBindings.put(signature, binding);
						} else if (element instanceof IField) {
							binding = new VariableBinding(DefaultBindingResolver.this, element, false, signature);
							bindingTables.bindingKeysToBindings.put(signature, binding);
						} else {
							return;
						}
					}
				}
			} else {
				if (type == ISignatureConstants.FUNCTION) {
					this.stack.pop();
					stack = this.stack.peek();
					
					if (stack.isEmpty()) {
						return;
					}
					stack.pop();
				} else {
					stack.pop();
				}
			}
			stack.push(binding);
		}

		public void acceptArgumentBreak(char c) {
			// empty
		}

		public void acceptArgumentModifier(int stc) {
			// TODO Descent binding argument modifier
		}		

		public void acceptAssociativeArray(String signature) {
			Stack<IBinding> stack = this.stack.peek();
			
			IBinding binding = bindingTables.bindingKeysToBindings.get(signature);
			if (binding == null) {
				binding = new TypeAArrayBinding(
						DefaultBindingResolver.this, 
						(ITypeBinding) stack.pop(), 
						(ITypeBinding) stack.pop(), 
						signature);
				bindingTables.bindingKeysToBindings.put(signature, binding);
			}
			stack.push(binding);
		}
		
		public void acceptDelegate(String signature) {
			Stack<IBinding> stack = this.stack.peek();
			
			IBinding binding = bindingTables.bindingKeysToBindings.get(signature);
			if (binding == null) {
				TypeFunctionOrDelegateBinding next = (TypeFunctionOrDelegateBinding) stack.pop();
				next.isFunction = false;
				next.signature = signature;
				binding = next;
				bindingTables.bindingKeysToBindings.put(signature, binding);
			}
			stack.push(binding);
		}

		public void acceptDynamicArray(String signature) {
			Stack<IBinding> stack = this.stack.peek();
			
			IBinding binding = bindingTables.bindingKeysToBindings.get(signature);
			if (binding == null) {
				binding = new TypeDArrayBinding(
						DefaultBindingResolver.this,
						(ITypeBinding) stack.pop(),
						signature);
				bindingTables.bindingKeysToBindings.put(signature, binding);
			}
			stack.push(binding);
		}

		public void acceptPointer(String signature) {
			Stack<IBinding> stack = this.stack.peek();
			
			IBinding binding = bindingTables.bindingKeysToBindings.get(signature);
			if (binding == null) {
				if (stack.isEmpty() || !(stack.peek() instanceof ITypeBinding)) {
					return;
				}
				
				binding = new TypePointerBinding(
						DefaultBindingResolver.this,
						(ITypeBinding) stack.pop(),
						signature);
				bindingTables.bindingKeysToBindings.put(signature, binding);
			}
			stack.push(binding);
		}

		public void acceptPrimitive(TypeBasic type) {
			Stack<IBinding> stack = this.stack.peek();
			
			IBinding binding = bindingTables.bindingKeysToBindings.get(type.deco);
			if (binding == null) {
				binding = new TypeBasicBinding(DefaultBindingResolver.this, type);
				bindingTables.bindingKeysToBindings.put(type.deco, binding);
			}
			stack.push(binding);
		}

		public void acceptStaticArray(int dimension, String signature) {
			Stack<IBinding> stack = this.stack.peek();
			
			IBinding binding = bindingTables.bindingKeysToBindings.get(signature);
			if (binding == null) {
				if (stack.isEmpty() || !(stack.peek() instanceof ITypeBinding)) {
					return;
				}
				
				binding = new TypeSArrayBinding(
					DefaultBindingResolver.this,
					(ITypeBinding) stack.pop(),
					dimension,
					signature);
				bindingTables.bindingKeysToBindings.put(signature, binding);
			}
			stack.push(binding);
		}

		public void enterFunctionType() {
			stack.push(new Stack<IBinding>());
		}

		public void exitFunctionType(LINK link, String signature) {
			Stack<IBinding> stack = this.stack.peek();
			
			IBinding binding = bindingTables.bindingKeysToBindings.get(signature);
			if (binding == null) {
				if (stack.isEmpty()) {
					return;
				}
				
				IBinding[] parameterTypes = new IBinding[stack.size() - 1];
				
				IBinding returnType = stack.pop();
				
				int i = stack.size() - 1;
				while(!stack.isEmpty()) {
					parameterTypes[i] = stack.pop();
					i--;
				}
				
				binding = new TypeFunctionOrDelegateBinding(
						DefaultBindingResolver.this,
						parameterTypes,
						returnType,
						false, // TODO varargs
						link,
						signature,
						true /* is function */);
				bindingTables.bindingKeysToBindings.put(signature, binding);
			} else {
				stack.clear();
			}
			
			stack.push(binding);
		}
		
	}
	
	IBinding resolveBinding(String signature) {
		if (signature == null || signature.length() == 0) {
			return null;
		}
		
		IBinding binding = bindingTables.bindingKeysToBindings.get(signature);
		if (binding != null) {
			return binding;
		}
		
		SignatureSolver solver = new SignatureSolver();
		try {
			SignatureProcessor.process(signature, solver);
		} catch (IllegalArgumentException e) {
			Util.log(e);
			return null;
		}
		
		if (solver.stack.isEmpty()) {
			binding = null;
		} else {
			Stack<IBinding> stack = solver.stack.pop();
			if (stack.isEmpty()) {
				return null;
			}
			binding = stack.pop();
		}
		
		if (binding != null) {
			bindingTables.bindingKeysToBindings.put(signature, binding);
		}
		
		return binding;
	}

}
