package dtool.dom.declarations;

import java.util.Iterator;
import java.util.List;

import descent.core.domX.ASTNode;
import descent.internal.compiler.parser.AttribDeclaration;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Statement;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.definitions.Definition;
import dtool.refmodel.INonScopedBlock;

public abstract class DeclarationAttrib extends ASTNeoNode implements INonScopedBlock {

	public final NodeList body;

	public DeclarationAttrib(Statement elem, Statement body) {
		convertNode(elem);
		this.body = NodeList.createNodeList(body);
	}

	public DeclarationAttrib(AttribDeclaration elem, List<Dsymbol> bodydecls) {
		convertNode(elem);
		this.body = NodeList.createNodeList(bodydecls);
		// XXX: Ugly hack (due to parser bug?)
		if(elem.preDdocs != null && elem.preDdocs.size() > 0) {
			ASTNode node = this.body.nodes[0];
			if(node instanceof Definition) {
				Definition def = (Definition) node;
				if(def.preComments == null || def.preComments.size() == 0)
					def.preComments = elem.preDdocs;
			}
		}
	}

	public Iterator<ASTNode> getMembersIterator() {
		return body.getNodeIterator();
	}

}