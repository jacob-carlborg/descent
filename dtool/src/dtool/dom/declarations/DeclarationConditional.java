package dtool.dom.declarations;

import java.util.Arrays;
import java.util.Iterator;

import melnorme.miscutil.ArrayUtil;
import descent.core.domX.ASTNode;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.Condition;
import descent.internal.compiler.parser.ConditionalDeclaration;
import descent.internal.compiler.parser.ConditionalStatement;
import descent.internal.compiler.parser.DVCondition;
import descent.internal.compiler.parser.IftypeExp;
import descent.internal.compiler.parser.StaticIfCondition;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.statements.IStatement;
import dtool.refmodel.INonScopedBlock;

public abstract class DeclarationConditional extends ASTNeoNode implements IStatement, INonScopedBlock {
	
	public NodeList thendecls;
	public NodeList elsedecls;

	public static DeclarationConditional create(ConditionalDeclaration elem) {
		NodeList thendecls = NodeList.createNodeList(elem.decl); 
		NodeList elsedecls = NodeList.createNodeList(elem.elsedecl);

		Condition condition = elem.condition;
		return createConditional(elem, thendecls, elsedecls, condition);		
	}
	
	public static DeclarationConditional create(ConditionalStatement elem) {
		NodeList thendecls = NodeList.createNodeList(elem.ifbody); 
		NodeList elsedecls = NodeList.createNodeList(elem.elsebody);

		Condition condition = elem.condition;
		return createConditional(elem, thendecls, elsedecls, condition);	
	}

	private static DeclarationConditional createConditional(
			ASTDmdNode elem, NodeList thendecls,
			NodeList elsedecls, Condition condition) {
		if(condition instanceof DVCondition) {
			return new DeclarationConditionalDV(elem, (DVCondition) condition, thendecls, elsedecls);
		}
		StaticIfCondition stIfCondition = (StaticIfCondition) condition;
		if(stIfCondition.exp instanceof IftypeExp && ((IftypeExp) stIfCondition.exp).ident != null) {
			return new DeclarationStaticIfIsType(elem, (IftypeExp) stIfCondition.exp, thendecls, elsedecls);
		} else { 
			return new DeclarationStaticIf(elem, stIfCondition, thendecls, elsedecls);
		}
	}


	protected ASTNode[] getMembers() {
		if(thendecls.nodes == null)
			return elsedecls.nodes;
		if(elsedecls.nodes == null)
			return thendecls.nodes;
		
		return ArrayUtil.concat(thendecls.nodes, elsedecls.nodes);
	}

	public Iterator<ASTNode> getMembersIterator() {
		return Arrays.asList(getMembers()).iterator();
	}

}
