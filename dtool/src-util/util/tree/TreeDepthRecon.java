package util.tree;


public class TreeDepthRecon extends TreeWalker {
	
	public int depth = 0;
	public int maxdepth = 0;
	public int mindepth = Integer.MAX_VALUE; //conceptually infinite;
	private boolean findmindepth = false;
	private boolean breakOnChildren = false;
	
	
	public static int findMaxDepth(ITreeNode elem) {
		TreeDepthRecon tdr = new TreeDepthRecon();
		tdr.traverse(elem);
		return tdr.maxdepth;
	}

	public static int findMinLeafDepth(ITreeNode elem) {
		TreeDepthRecon tdr = new TreeDepthRecon();
		tdr.traverse(elem);
		return tdr.maxdepth;
	}
	
	public static int findMaxDistance(ITreeNode elem) {
		return findMaxDepth(elem)-1;
	}
	
	public static int findMinLeafDistance(ITreeNode elem) {
		return findMinLeafDepth(elem)-1;
	}

	public static boolean isLeaf(ITreeNode elem) {
		TreeDepthRecon tdr = new TreeDepthRecon();
		tdr.breakOnChildren = true;
		tdr.traverse(elem);
		return tdr.maxdepth == 1;
	}	
	
	
	public boolean enterNode(ITreeNode element) {
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
	
	public void leaveNode(ITreeNode element) {
		depth--;
	}

}

