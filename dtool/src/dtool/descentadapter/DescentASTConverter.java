package dtool.descentadapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import descent.internal.compiler.parser.ast.ASTNode;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.definitions.Module;

public class DescentASTConverter {

	public static StatementConverter converter = new StatementConverter();;

	public static Module convertModule(ASTNode cumodule) {
		Module module = Module.createModule((descent.internal.compiler.parser.Module) cumodule);
		module.accept(new PostConvertionAdapter());
		return module;
	}
	
	public static ASTNeoNode convertElem(ASTNode elem) {
		if(elem == null) return null;
		StatementConverter conv = new StatementConverter();
		elem.accept(conv);
		return conv.ret;
	}
	
	public static ASTNeoNode[] convertMany(Collection<? extends IASTNode> children) {
		if(children == null) return null;
		ASTNeoNode[] rets = new ASTNeoNode[children.size()];
		convertMany(children.toArray(), rets);
		return rets;
	}
	
	public static void convertMany(List<? extends IASTNode> children, ASTNeoNode[] rets) {
		if(children == null) return;
		convertMany(children.toArray(), rets);
		return;
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T extends IASTNode> T[] convertMany(Object[] children, T[] rets) {
		StatementConverter conv = new StatementConverter();
		for(int i = 0; i < children.length; ++i) {
			ASTNode elem = (ASTNode) children[i];
			if(elem == null) {
				rets[i] = null;
			} else {
				elem.accept(conv);
				rets[i] = (T) conv.ret;
			}
		}
		return rets;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends IASTNode> List<T> convertManyL(List<? extends ASTNode> children, List<T> dummy) {
		StatementConverter conv = new StatementConverter();
		if(children == null)
			return null;
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
			if(elem == null) {
				rets.add(null);
			} else {
				elem.accept(conv);
				rets.add((T) conv.ret);
			}
		}
		return rets;
	}

}
