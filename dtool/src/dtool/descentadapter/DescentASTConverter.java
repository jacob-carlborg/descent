package dtool.descentadapter;

import java.util.ArrayList;
import java.util.List;

import dtool.dom.ast.ASTNode;
import dtool.dom.declarations.Module;

public class DescentASTConverter {

	public StatementConverter converter;

	public DescentASTConverter() {
		this.converter = new StatementConverter();
	}
	
	public Module convertModule(ASTNode cumodule) {
		return new Module((descent.internal.core.dom.Module) cumodule);
	}
	
	public ASTNode convert(ASTNode elem) {
		elem.accept(converter);
		return converter.ret;
	}
	
	public static ASTNode convertElem(ASTNode elem) {
		if(elem == null) return null;
		StatementConverter conv = new StatementConverter();
		elem.accept(conv);
		return conv.ret;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends ASTNode> List<T> convertMany(List<? extends ASTNode> children, List<T> dummy) {
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
	public static <T extends ASTNode> List<T> convertMany(ASTNode[] children, List<T> dummy) {
		StatementConverter conv = new StatementConverter();
		List<T> rets = new ArrayList<T>(children.length);
		for (int i = 0; i < children.length; ++i) {
			ASTNode elem = children[i];
			elem.accept(conv);
			rets.add((T) conv.ret);
		}
		return rets;
	}
	
	public static ASTNode[] convertMany(Object[] children) {
		ASTNode[] rets = new ASTNode[children.length];
		convertMany(children, rets);
		return rets;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends ASTNode> T[] convertMany(Object[] children, T[] rets) {
		StatementConverter conv = new StatementConverter();
		for(int i = 0; i < children.length; ++i) {
			ASTNode elem = (ASTNode) children[i];
			elem.accept(conv);
			rets[i] = (T) conv.ret;
		}
		return rets;	
	}
	
}
