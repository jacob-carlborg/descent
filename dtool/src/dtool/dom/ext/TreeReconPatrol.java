package dtool.dom.ext;

import descent.core.domX.ASTNode;


public class TreeReconPatrol extends ASTNeoVisitor {
	
	public int depth;
	private boolean findmaxdepth;
	public int maxdepth;
	private boolean findmindepth;
	public int mindepth;
	
	public TreeReconPatrol() {
		this.depth = 0;
		this.maxdepth = 0;
		this.mindepth = Integer.MAX_VALUE;
	}
	
	public static int findMaxDepth(ASTNode elem) {
		TreeReconPatrol tdrp = new TreeReconPatrol();
		tdrp.findmaxdepth = true;
		elem.accept(tdrp);
		return tdrp.maxdepth;
	}

	public static int findMinLeafDepth(ASTNode elem) {
		TreeReconPatrol tdrp = new TreeReconPatrol();
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

