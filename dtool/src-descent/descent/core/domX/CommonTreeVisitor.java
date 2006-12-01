package descent.core.domX;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import util.ExceptionAdapter;

public class CommonTreeVisitor {

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
			Exception e = (Exception)ite.getTargetException();
			if(e instanceof RuntimeException)
				throw (RuntimeException) e;
			else
				throw new ExceptionAdapter(e); 
		} catch (Exception e) {
			throw new ExceptionAdapter(e); 
		} 
	}
	
	public static class UnknownASTElementException extends Exception {
		public UnknownASTElementException(ASTNode element) {
			super("ASTVisitor: Unknown ASTElement type:"+element);
		}
	}
	
	protected void ensureVisitIsNotDirectVisit(AbstractElement element) {
		if(checkASTtypes && visitingAsSuper != true)
			throw new RuntimeException(new UnknownASTElementException(element));
	}

	
}