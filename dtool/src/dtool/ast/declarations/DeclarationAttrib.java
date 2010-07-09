package dtool.ast.declarations;

import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.IteratorUtil;
import descent.internal.compiler.parser.AttribDeclaration;
import descent.internal.compiler.parser.Comment;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Statement;
import dtool.ast.ASTNeoNode;
import dtool.ast.definitions.Definition;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.INonScopedBlock;

public abstract class DeclarationAttrib extends ASTNeoNode implements INonScopedBlock {

	public final NodeList body;

	public DeclarationAttrib(Statement elem, Statement body, ASTConversionContext convContex) {
		convertNode(elem);
		this.body = NodeList.createNodeList(body, convContex);
	}

	public DeclarationAttrib(AttribDeclaration elem, List<Dsymbol> bodydecls, ASTConversionContext convContex) {
		convertNode(elem);
		this.body = NodeList.createNodeList(bodydecls, convContex);
		// XXX: AST: Convertion ugly hack (due to parser bug?)
//		if (elem.preDdocs != null && elem.preDdocs.size() > 0 && this.body != null && this.body.nodes.length > 0) {
//			ASTNeoNode node = this.body.nodes[0];
//			if(node instanceof Definition) {
//				Definition def = (Definition) node;
//				if(def.preComments == null || def.preComments.length == 0) {
//					def.preComments = elem.preDdocs.toArray(new Comment[elem.preDdocs.size()]);
//				}
//			}
//		}
	}
	
	@Override
	public Iterator<ASTNeoNode> getMembersIterator() {
		if(body == null)
			return IteratorUtil.getEMPTY_ITERATOR();
		return body.getNodeIterator();
	}

}