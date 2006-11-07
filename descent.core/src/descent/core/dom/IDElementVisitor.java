package descent.core.dom;

/**
 * Visitor design pattern.
 * 
 * First, an element is visited with
 * <code>boolean visit(IDElement)</code>. If it returns
 * true, children of the element are also visited. After,
 * and no matters what the result of <code>visit</code> was,
 * <code>void endVisit(IDElement)</code> is called.
 */
public interface IDElementVisitor {
	
	/**
	 * Visits the element.
	 * @param element the element to visit
	 * @return true if children element should be visited
	 */
	boolean visit(IDElement element);
	
	/**
	 * Ends the visit to the element.
	 * @param element the element to visit
	 */
	void endVisit(IDElement element);

}
