package dtool.dom.declarations;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import descent.internal.compiler.parser.CompoundStatement;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Statement;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.descentadapter.DescentASTConverter;

/**
 * A helper class for AST nodes, 
 * to hold a group of declarations or statements.
 */
public class NodeList  {
	
	public ASTNode[] nodes;
	public boolean hasCurlies; // Accurate detection not implement yet

	private NodeList() {
	}

	public static NodeList createNodeList(Statement body) {
		NodeList nodes = new NodeList();
		if(body instanceof CompoundStatement) {
			CompoundStatement cst = (CompoundStatement) body;
			nodes.nodes = DescentASTConverter.convertMany(cst.sourceStatements);
			nodes.hasCurlies = true; 
		} else {
			nodes.nodes = new ASTNode[] { DescentASTConverter.convertElem(body) };
		}
		return nodes;
	}

	public static NodeList createNodeList(Collection<Dsymbol> decl) {
		NodeList nodes = new NodeList();
		if(decl != null)
			nodes.nodes = DescentASTConverter.convertMany(decl);
		return nodes;
	}

	public Iterator<ASTNode> getNodeIterator() {
		return Arrays.asList(nodes).iterator();
	}
}
