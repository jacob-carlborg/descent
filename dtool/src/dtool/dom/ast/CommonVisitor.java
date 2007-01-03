package dtool.dom.ast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import util.Assert;
import util.ExceptionAdapter;
import dtool.dom.ast.tree.TreeNode;
import dtool.dom.base.ASTNode;

public class CommonVisitor<T extends TreeNode> {
	
	public void preVisit(T elem) {
		// Default implementation: do nothing
	}

	public void postVisit(T elem) {
		// Default implementation: do nothing
	}
	
	protected static void ensureFirstIsSuperOfLast(Class basecl, Class supercl) {
		Assert.isTrue(basecl.getSuperclass().equals(supercl));
	}

	protected boolean checkASTtypes = false;
	protected boolean visitingAsSuper = false;
	
	public boolean visitAsSuperType(Object element, Class elemclass)  {
		Class elemsuper = elemclass.getSuperclass();
		Method method;
		try {
			method = this.getClass().getMethod("visit", new Class[]{elemsuper});
			visitingAsSuper = true;
			boolean result = (Boolean) method.invoke(this, element);
			visitingAsSuper = false;
			return result;
		} catch (InvocationTargetException ite) {
			Throwable e = (Throwable)ite.getTargetException();
			throw ExceptionAdapter.unchecked(e);
		} catch (Exception e) {
			throw ExceptionAdapter.unchecked(e);
		} 
	}
	
	@SuppressWarnings("serial")
	public static class UnknownASTElementException extends Exception {

		public UnknownASTElementException(ASTNode element) {
			super("Tree Visitor: Unknown ASTElement type:"+element);
		}
	}
	
	protected void ensureVisitIsNotDirectVisit(ASTNode element) {
		if(checkASTtypes && visitingAsSuper != true)
			throw new RuntimeException(new UnknownASTElementException(element));
	}

	
}