package descent.core.dom.rewrite;

import java.util.ArrayList;
import java.util.List;

import descent.core.ICompilationUnit;
import descent.core.IImportDeclaration;
import descent.core.IJavaElement;
import descent.core.IParent;
import descent.core.JavaModelException;
import descent.core.dom.ASTNode;
import descent.core.dom.AggregateDeclaration;
import descent.core.dom.AlignDeclaration;
import descent.core.dom.CompilationUnit;
import descent.core.dom.ConstructorDeclaration;
import descent.core.dom.Declaration;
import descent.core.dom.EnumDeclaration;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.GenericVisitor;
import descent.core.dom.Import;
import descent.core.dom.ImportDeclaration;
import descent.core.dom.InvariantDeclaration;
import descent.core.dom.TemplateDeclaration;
import descent.core.dom.UnitTestDeclaration;
import descent.core.dom.rewrite.ImportRewrite.ImportRewriteContext;

/*
 * Temporary class until we fully understand import rewrite.
 */
public class ImportRewriteStub {
	
	public final static IImportDeclaration[] collectImports(ICompilationUnit unit) throws JavaModelException {
		final List<IImportDeclaration> imports = new ArrayList<IImportDeclaration>();
		collectImports(unit, imports);
		return imports.toArray(new IImportDeclaration[imports.size()]);
	}
	
	private static void collectImports(IParent parent, List<IImportDeclaration> imports) throws JavaModelException {
		IJavaElement[] children = parent.getChildren();
		for(IJavaElement child : children) {
			if (child instanceof IImportDeclaration) {
				imports.add((IImportDeclaration) child);
			} else if (child instanceof IParent) {
				collectImports((IParent) child, imports);
			}
		}
	}

	public final static int findInImports(CompilationUnit astRoot, final String qualifier, String name, int kind) {
		// First search in top level imports
		if (astRoot == null) {
			System.out.println(1);
		}
		
		for(Declaration decl : astRoot.declarations()) {
			if (decl.getNodeType() == ASTNode.IMPORT_DECLARATION) {
				ImportDeclaration impDecl = (ImportDeclaration) decl;
				for(Import imp : impDecl.imports()) {
					if (importEquals(imp, qualifier)) {
						return ImportRewriteContext.RES_NAME_FOUND;
					}
				}
			}
		}
		
		final boolean[] found = { false };
		astRoot.accept(new GenericVisitor() {
			@Override
			public boolean visit(FunctionDeclaration node) {
				return false;
			}
			@Override
			public boolean visit(InvariantDeclaration node) {
				return false;
			}
			@Override
			public boolean visit(UnitTestDeclaration node) {
				return false;
			}
			@Override
			public boolean visit(ConstructorDeclaration node) {
				return false;
			}
			@Override
			public boolean visit(AggregateDeclaration node) {
				return !found[0];
			}
			@Override
			public boolean visit(EnumDeclaration node) {
				return !found[0];
			}
			@Override
			public boolean visit(AlignDeclaration node) {
				return !found[0];
			}
			@Override
			public boolean visit(TemplateDeclaration node) {
				return !found[0];
			}
			@Override
			public boolean visit(Import imp) {
				if (importEquals(imp, qualifier)) {
					found[0] = true;
				}
				return false;
			}			
		});
		
		if (found[0]) {
			return ImportRewriteContext.RES_NAME_FOUND;
		} else {
			return ImportRewriteContext.RES_NAME_UNKNOWN;
		}
	}
	
	private static boolean importEquals(Import imp, String qualifier) {
		return imp.getName().toString().equals(qualifier);
	}

}
