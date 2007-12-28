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
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.EnumDeclaration;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.InterfaceDeclaration;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.StructDeclaration;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeIdentifier;
import descent.internal.compiler.parser.UnionDeclaration;
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
		return visitType(node.ident, node.type);
	}
	
	@Override
	public boolean visit(StructDeclaration node) {
		return visitType(node.ident, node.type);
	}
	
	@Override
	public boolean visit(InterfaceDeclaration node) {
		return visitType(node.ident, node.type);
	}
	
	@Override
	public boolean visit(UnionDeclaration node) {
		return visitType(node.ident, node.type);
	}
	
	@Override
	public boolean visit(EnumDeclaration node) {
		return visitType(node.ident, node.type);
	}
	
	@Override
	public boolean visit(TypeIdentifier node) {
		if (identIsInRange(node.ident)) {
			addType(node.resolvedType);
		}
		return super.visit(node);
	}
	
	private boolean visitType(IdentifierExp ident, Type type) {
		if (identIsInRange(ident)) {
			addType(type);
		}
		return true;
	}
	
	private boolean identIsInRange(IdentifierExp ident) {
		if (ident == null) {
			return false;
		}
		if (ident.ident.length == 0) {
			return false;
		}
		
		return ident.start <= offset && offset + length <= ident.start + ident.length;
	}
	
	private void addType(Type type) {
		IJavaElement result = finder.find(type.getSignature());
		if (result != null) {
			selectedElements.add(result);
		}
	}

}
