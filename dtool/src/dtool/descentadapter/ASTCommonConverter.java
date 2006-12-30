package dtool.descentadapter;

import java.util.ArrayList;
import java.util.List;

import descent.core.domX.ASTVisitor;
import dtool.dom.base.ASTElement;
import dtool.dom.base.ASTNode;

public abstract class ASTCommonConverter extends ASTVisitor {
	
	ASTNode ret = null;
	
	public ASTNode convert(ASTNode elem) {
		elem.accept(this);
		return ret;
	}
	
	protected ASTNode[] convertMany(Object[] children) {
		ASTNode[] rets = new ASTNode[children.length];
		convertMany(rets, children);
		return rets;
	}

	@SuppressWarnings("unchecked")
	protected <T extends ASTNode> void convertMany(T[] rets, Object[] children) {
		for(int i = 0; i < children.length; ++i) {
			ASTNode elem = (ASTNode) children[i];
			elem.accept(this);
			rets[i] = (T) ret;
		}
	}

	
	protected <T extends ASTNode> void convertMany(T[] rets, List children) {
		convertMany(rets, children, 0);
	}

	@SuppressWarnings("unchecked")
	protected <T extends ASTNode> void convertMany(T[] rets, List children, int ixoffset) {
		for (int i = 0; i < children.size(); ++i) {
			ASTNode elem = (ASTNode) children.get(i);
			elem.accept(this);
			rets[i+ixoffset] = (T) ret;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends ASTNode> List<T> convertMany(List<ASTNode> children) {
		List<T> rets = new ArrayList<T>(children.size());
		for (int i = 0; i < children.size(); ++i) {
			ASTNode elem = (ASTNode) children.get(i);
			elem.accept(this);
			rets.add((T) ret);
		}
		return rets;
	}
	
	/* ---- common adaptors ---- */
	
	protected void rangeAdapt(ASTElement newelem, ASTNode elem) {
		newelem.startPos = elem.getStartPos();
		newelem.length = elem.getLength();
	}

	protected boolean endAdapt(ASTElement newelem) {
		ret = newelem;
		return false;
	}


	public boolean visit(ASTNode elem) {
		ret = elem;
		return false;	
	}

}