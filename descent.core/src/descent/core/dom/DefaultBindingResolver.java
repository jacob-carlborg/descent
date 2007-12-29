package descent.core.dom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import descent.core.ICompilationUnit;
import descent.core.IField;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.core.WorkingCopyOwner;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.AggregateDeclaration;
import descent.internal.compiler.parser.EnumDeclaration;
import descent.internal.compiler.parser.EnumMember;
import descent.internal.compiler.parser.IModule;
import descent.internal.compiler.parser.Import;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeBasic;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.core.JavaElementFinder;
import descent.internal.core.util.Util;

public class DefaultBindingResolver extends BindingResolver {
	
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
	 * Constructor for DefaultBindingResolver.
	 */
	DefaultBindingResolver(IJavaProject project, WorkingCopyOwner workingCopyOwner, BindingTables bindingTables) {
		this.javaProject = project;
		this.bindingsToAstNodes = new HashMap();
		this.bindingTables = bindingTables;
		this.workingCopyOwner = workingCopyOwner;
		this.newAstToOldAst = new HashMap();
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
	ITypeBinding resolveVariable(VariableDeclaration variable) {
		if (variable.fragments().size() == 0) {
			return null;
		}
		
		ASTDmdNode old = newAstToOldAst.get(variable.fragments().get(0));
		if (!(old instanceof VarDeclaration)) {
			return null;
		}
		
		VarDeclaration v = (VarDeclaration) old;
		String key = v.type.getSignature();
		return (ITypeBinding) resolveBinding(variable, key);
	}
	
	@Override
	IVariableBinding resolveVariableFragment(VariableDeclarationFragment variable) {
		ASTDmdNode old = newAstToOldAst.get(variable);
		if (!(old instanceof VarDeclaration)) {
			return null;
		}
		
		VarDeclaration v = (VarDeclaration) old;
		String key = v.getSignature();
		return (IVariableBinding) resolveBinding(variable, key);
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
			default:
				if (parent instanceof descent.core.dom.Type) {
					return resolveType((descent.core.dom.Type) parent);
				}
			}
		}
		return null;
	}
	
	@Override
	ITypeBinding resolveType(descent.core.dom.Type type) {
		// If the type is the last SimpleType of a QualifiedType...
		if (type.getNodeType() == ASTNode.SIMPLE_TYPE) {
			if (type.getParent() != null && type.getParent().getNodeType() == ASTNode.QUALIFIED_TYPE) {
				QualifiedType qType = (QualifiedType) type.getParent();
				if (qType.getType() == type) {
					return resolveType((descent.core.dom.Type) type.getParent());
				}
			}
			
		}
		
		ASTDmdNode old = newAstToOldAst.get(type);
		if (!(old instanceof descent.internal.compiler.parser.Type)) {
			return null;
		}
		
		Type t = (Type) old;
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
				bindingsToAstNodes.put(binding, type);
				return (ITypeBinding) binding;
			}
		}
		
		// Else, try with the signature
		return (ITypeBinding) resolveBinding(type, signature);
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
	
	// TODO see how not tu "duplilcate" this code, it's also
	// in RDsymbol#getTypeFromSignature, although a little different
	IBinding resolveBinding(String signature) {
		signature = JavaElementFinder.correct(signature);
		
		IBinding binding = bindingTables.bindingKeysToBindings.get(signature);
		if (binding != null) {
			return binding;
		}
		
		// TODO optimize using IJavaProject#find and NameLookup
		// TODO make an "extract number" function in order to avoid duplication
		// TODO signature sometimes is wrong, check RDsymbol implementation of this
		try {
			if (signature == null || signature.length() == 0) {
				// TODO signal error
				return null;
			} else {
				char first = signature.charAt(0);
				
				switch(first) {
				case 'E': // enum
				case 'C': // class
				case 'S': // struct
				case 'T': // typedef
				case 'Q': // var, alias, typedef
					IJavaElement current = javaProject;
					
					for(int i = 1; i < signature.length(); i++) {
						char c = signature.charAt(i);
						int n = 0;
						while(Character.isDigit(c)) {
							n = 10 * n + (c - '0');
							i++;
							c = signature.charAt(i);
						}
						String name = signature.substring(i, i + n);
						current = JavaElementFinder.findChild(current, name);
						if (current == null) {
							// TODO signal error
							break;
						}
						i += n - 1;
					}
					
					if (current != null) {
						if (current instanceof IType) {
							binding = new TypeBinding(this, (IType) current, signature);
						} else if (current instanceof IField) {
							binding = new VariableBinding(this, (IField) current, signature);
						}
					}
					break;
				case 'D': { // delegate
					binding = new TypeDelegateBinding(this, (ITypeBinding) resolveBinding(signature.substring(1)), signature);
					break;
				}
				case 'P': { // pointer
					binding = new TypePointerBinding(this, (ITypeBinding) resolveBinding(signature.substring(1)), signature);
					break;
				}
				case 'A': { // dynamic array
					binding = new TypeDArrayBinding(this, (ITypeBinding) resolveBinding(signature.substring(1)), signature);
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
					
					binding = new TypeSArrayBinding(this, (ITypeBinding) resolveBinding(signature.substring(i)), n, signature);
					break;
				}
				case 'H': {// associative array
					ITypeBinding k = (ITypeBinding) resolveBinding(signature.substring(1));
					ITypeBinding v = (ITypeBinding) resolveBinding(signature.substring(1 + k.getKey().length()));
					binding = new TypeAArrayBinding(this, k, v, signature);
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
					
					List<ITypeBinding> args = new ArrayList<ITypeBinding>(); 
					
					int i = 1;
					ITypeBinding targ = (ITypeBinding) resolveBinding(signature.substring(i));
					while(targ != null) {
						// TODO default arg
						args.add(targ);
						targ = (ITypeBinding) resolveBinding(signature.substring(i));
					}
					
					i++;
					ITypeBinding tret = (ITypeBinding) resolveBinding(signature.substring(i));
					
					// TODO varargs
					binding = new TypeFunctionBinding(this, args.toArray(new ITypeBinding[args.size()]), tret, false, link, signature);
					break;
				case 'X': // Argument break
				case 'Y':
				case 'Z':
					return null;
				default: // Try with type basic
					TypeBasic typeBasic = TypeBasic.fromSignature(first);
					if (typeBasic != null) {
						binding = new TypeBasicBinding(this, typeBasic);
					}
					break;
				}
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
		
		if (binding != null) {
			bindingTables.bindingKeysToBindings.put(signature, binding);
		}
		
		return binding;
	}

}
