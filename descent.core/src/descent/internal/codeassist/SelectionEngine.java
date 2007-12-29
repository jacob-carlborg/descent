package descent.internal.codeassist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.JavaModelException;
import descent.core.WorkingCopyOwner;
import descent.core.dom.CompilationUnitResolver;
import descent.internal.compiler.env.ICompilationUnit;
import descent.internal.compiler.impl.CompilerOptions;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.EnumDeclaration;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.IDeclaration;
import descent.internal.compiler.parser.IModule;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Import;
import descent.internal.compiler.parser.InterfaceDeclaration;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.StructDeclaration;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeIdentifier;
import descent.internal.compiler.parser.UnionDeclaration;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.compiler.parser.VarExp;
import descent.internal.compiler.parser.ast.AstVisitorAdapter;
import descent.internal.core.JavaElementFinder;
import descent.internal.core.util.Util;

/*
 * For now, don't take the JDT approach: let's parse, visit and see where
 * if the node falls between the given ranges.
 */
public class SelectionEngine extends AstVisitorAdapter {
	
	private final static IJavaElement[] NO_ELEMENTS = new IJavaElement[0];
	
	IJavaProject javaProject;
	WorkingCopyOwner owner;
	Map settings;
	CompilerOptions compilerOptions;
	
	int offset;
	int length;
	List<IJavaElement> selectedElements;
	
	JavaElementFinder finder;

	public SelectionEngine(
			Map settings,
			IJavaProject javaProject,
			WorkingCopyOwner owner) {
		this.javaProject = javaProject;
		this.owner = owner;
		this.settings = settings;
		this.compilerOptions = new CompilerOptions(settings);
		this.finder = new JavaElementFinder(javaProject);
	}
	
	public IJavaElement[] select(ICompilationUnit sourceUnit, int offset, int length) {
		this.offset = offset;
		this.length = length;
		this.selectedElements = new ArrayList<IJavaElement>();
		
		try {
			Module module = CompilationUnitResolver.resolve(Util.getApiLevel(this.compilerOptions.getMap()), sourceUnit, javaProject, settings, owner, true, null).module;
			module.accept(this);
			return selectedElements.toArray(new IJavaElement[selectedElements.size()]);
		} catch (JavaModelException e) {
			Util.log(e);
			return NO_ELEMENTS;
		}
	}
	
	@Override
	public boolean visit(ClassDeclaration node) {
		return visitType(node, node.ident, node.type);
	}
	
	@Override
	public boolean visit(StructDeclaration node) {
		return visitType(node, node.ident, node.type);
	}
	
	@Override
	public boolean visit(InterfaceDeclaration node) {
		return visitType(node, node.ident, node.type);
	}
	
	@Override
	public boolean visit(UnionDeclaration node) {
		return visitType(node, node.ident, node.type);
	}
	
	@Override
	public boolean visit(EnumDeclaration node) {
		return visitType(node, node.ident, node.type);
	}
	
	@Override
	public boolean visit(TypeIdentifier node) {
		if (isInRange(node.ident)) {
			if (node.resolvedSymbol != null) {
				if (node.resolvedSymbol.getJavaElement() != null) {
					selectedElements.add(node.resolvedSymbol.getJavaElement());
				} else {
					addSignature(node.resolvedSymbol.getSignature());
				}
			} else {
				addType(node.resolvedType);
			}
		}
		return false;
	}
	
	@Override
	public boolean visit(VarDeclaration node) {
		if (isInRange(node.ident)) {
			addSignature(node.getSignature());
			return false;
		}
		return true;
	}
	
	@Override
	public boolean visit(IdentifierExp node) {
		if (!isInRange(node)) {
			return false;
		}
		
		Expression resolved = node.resolvedExpression;
		if (resolved == null) {
			return false;
		}
		
		switch(resolved.getNodeType()) {
		case ASTDmdNode.VAR_EXP:
			VarExp varExp = (VarExp) resolved;
			IDeclaration var = varExp.var;
			if (var.getJavaElement() != null) {
				selectedElements.add(var.getJavaElement());
			} else {
				addSignature(var.getSignature());
			}
			break;
		}
		
		return false;
	}
	
	@Override
	public boolean visit(Import node) {
		if (isInRange(node.id)) {
			IModule mod = node.mod;
			if (mod != null) {
				if (mod.getJavaElement() != null) {
					selectedElements.add(mod.getJavaElement());
				} else {
					addSignature(mod.getSignature());
				}
			}
			return false;
		}
		return true;
	}
	
	private boolean visitType(ASTDmdNode node, IdentifierExp ident, Type type) {
		if (isInRange(ident)) {
			addType(type);
		}
		return isInRange(node);
	}
	
	// Other utility methods
	
	private boolean isInRange(IdentifierExp ident) {
		if (ident == null) {
			return false;
		}
		if (ident.ident.length == 0) {
			return false;
		}
		
		return ident.start <= offset && offset + length <= ident.start + ident.length;
	}
	
	private boolean isInRange(ASTDmdNode node) {
		return node.start <= offset && offset + length <= node.start + node.length;
	}
	
	private void addType(Type type) {
		IJavaElement result = finder.find(type.getSignature());
		if (result != null) {
			selectedElements.add(result);
		}
	}
	
	private void addSignature(String signature) {
		IJavaElement result = finder.find(signature);
		if (result != null) {
			selectedElements.add(result);
		}
	}

}
