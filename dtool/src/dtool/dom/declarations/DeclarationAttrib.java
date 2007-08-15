package dtool.dom.declarations;

import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.IteratorUtil;
import descent.internal.compiler.parser.AttribDeclaration;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Statement;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.statements.MultiNodes;
import dtool.refmodel.INonScopedBlock;

public abstract class DeclarationAttrib extends ASTNeoNode implements INonScopedBlock {

	public ASTNeoNode body;

	public DeclarationAttrib(Statement elem, Statement body) {
		convertNode(elem);
		this.body = MultiNodes.createNodeBlock(body);
	}

	public DeclarationAttrib(AttribDeclaration elem, List<Dsymbol> body) {
		convertNode(elem);
		this.body = MultiNodes.createNodeBlock(body);
	}

	public Iterator<ASTNeoNode> getMembersIterator() {
		return IteratorUtil.singletonIterator(body);
	}

}