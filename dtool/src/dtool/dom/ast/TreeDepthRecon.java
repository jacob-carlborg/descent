package dtool.dom.ast;

import dtool.dom.base.ASTNode;


public class TreeDepthRecon extends ASTNeoVisitor {
	
	public int depth = 0;
	public int maxdepth = 0;
	public int mindepth = Integer.MAX_VALUE; //conceptually infinite;
	private boolean findmindepth = false;
	private boolean breakOnChildren = false;
	
	
	public static int findMaxDepth(ASTNode elem) {
		TreeDepthRecon tdr = new TreeDepthRecon();
		elem.accept(tdr);
		return tdr.maxdepth;
	}

	public static int findMinLeafDepth(ASTNode elem) {
		TreeDepthRecon tdr = new TreeDepthRecon();
		elem.accept(tdr);
		return tdr.maxdepth;
	}
	
	public static int findMaxDistance(ASTNode elem) {
		return findMaxDepth(elem)-1;
	}
	
	public static int findMinLeafDistance(ASTNode elem) {
		return findMinLeafDepth(elem)-1;
	}

	public static boolean isLeaf(ASTNode elem) {
		TreeDepthRecon tdr = new TreeDepthRecon();
		tdr.breakOnChildren = true;
		elem.accept(tdr);
		return tdr.maxdepth == 1;
	}	
	
	public boolean visit(ASTNode element) {
		depth++;

		if(breakOnChildren && depth == 2) {
			return false; // We're visiting a children, break
		}

		if (depth > maxdepth)
			maxdepth = depth;
		
		if (findmindepth && depth < mindepth && isLeaf(element))
			mindepth = depth;

		return true;
		
	}
	
	public void endVisit(ASTNode element) {
		depth--;
	}

}

