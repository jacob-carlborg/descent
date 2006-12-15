package dtool.dombase;

import descent.core.domX.ASTNode;


public class TreeDepthRecon extends ASTNeoVisitor {
	
	public int depth;
	private boolean findmaxdepth;
	public int maxdepth;
	private boolean findmindepth;
	public int mindepth;
	
	public TreeDepthRecon() {
		this.depth = 0;
		this.maxdepth = 0;
		this.mindepth = Integer.MAX_VALUE;
	}
	
	public static int findMaxDepth(ASTNode elem) {
		TreeDepthRecon tdrp = new TreeDepthRecon();
		tdrp.findmaxdepth = true;
		elem.accept(tdrp);
		return tdrp.maxdepth;
	}

	public static int findMinLeafDepth(ASTNode elem) {
		TreeDepthRecon tdrp = new TreeDepthRecon();
		tdrp.findmindepth = true;
		elem.accept(tdrp);
		return tdrp.mindepth;
	}
	
	public static boolean isLeaf(ASTNode elem) {
		return findMaxDepth(elem) == 1;
	}	
	
	public boolean visit(ASTNode element) {
		depth++;
		if (findmaxdepth && depth > maxdepth)
			maxdepth = depth;
		
		if (findmindepth && isLeaf(element) && depth < mindepth)
			mindepth = depth;
		return true;
	}
	
	public void endVisit(ASTNode element) {
		depth--;
	}


}

