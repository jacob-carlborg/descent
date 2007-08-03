package dtool.descentadapter;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.IDescentElement;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.ASTNodeParentizer;
import dtool.dom.ast.IASTNode;
import dtool.dom.definitions.Module;

public class DescentASTConverter {

	public static StatementConverter converter = new StatementConverter();;

	public static Module convertModule(ASTNode cumodule) {
		Module module = new Module((descent.internal.core.dom.Module) cumodule);
		ASTNodeParentizer.parentize(module);
		return module;
	}
	
	public static ASTNode convertElem(ASTNode elem) {
		if(elem == null) return null;
		StatementConverter conv = new StatementConverter();
		elem.accept(conv);
		return conv.ret;
	}
	
	public static ASTNode[] convertMany(Object[] children) {
		if(children == null) return null;
		ASTNode[] rets = new ASTNode[children.length];
		convertMany(children, rets);
		return rets;
	}
	
	public static ASTNode[] convertMany(List<? extends IDescentElement> children) {
		if(children == null) return null;
		ASTNode[] rets = new ASTNode[children.size()];
		convertMany(children.toArray(), rets);
		return rets;
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T extends IASTNode> T[] convertMany(Object[] children, T[] rets) {
		StatementConverter conv = new StatementConverter();
		for(int i = 0; i < children.length; ++i) {
			ASTNode elem = (ASTNode) children[i];
			elem.accept(conv);
			rets[i] = (T) conv.ret;
		}
		return rets;	
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends IASTNode> List<T> convertManyL(List<? extends ASTNode> children, List<T> dummy) {
		StatementConverter conv = new StatementConverter();
		List<T> rets = new ArrayList<T>(children.size());
		for (int i = 0; i < children.size(); ++i) {
			ASTNode elem = children.get(i);
			elem.accept(conv);
			rets.add((T) conv.ret);
		}
		return rets;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends IASTNode> List<T> convertManyL(ASTNode[] children, List<T> dummy) {
		StatementConverter conv = new StatementConverter();
		List<T> rets = new ArrayList<T>(children.length);
		for (int i = 0; i < children.length; ++i) {
			ASTNode elem = children[i];
			elem.accept(conv);
			rets.add((T) conv.ret);
		}
		return rets;
	}



}
