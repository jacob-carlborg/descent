package descent.core;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

class ElementVisitorGenerator {
	
	private static Set<String> excludeList;
	
	static {
		excludeList = new TreeSet<String>();
		excludeList.add("ElementVisitor");
		excludeList.add("IArrayType");
		excludeList.add("ICommented");
		excludeList.add("IDeclaration");
		excludeList.add("IDElementVisitor");
		excludeList.add("IElement");
		excludeList.add("IExpression");
		excludeList.add("IInitializer");
		excludeList.add("IModifier");
		excludeList.add("IModifiersContainer");
		excludeList.add("IParser");
		excludeList.add("IProblem");
		excludeList.add("IProblemCollector");
		excludeList.add("IStatement");
		excludeList.add("ITemplateParameter");
		excludeList.add("IType");
	}
	
	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder();
		sb.append("package descent.core.dom;\n");
		sb.append("\n");
		sb.append("/**\n");
		sb.append(" * A visitor for abstract syntax trees.\n");
		sb.append(" * <p>\n");
		sb.append(" * For each different concrete AST node type <i>T</i> there are\n");
		sb.append(" * a pair of methods:\n");
		sb.append(" * <ul>\n");
		sb.append(" * <li><code>public boolean visit(<i>T</i> node)</code> - Visits\n");
		sb.append(" * the given node to perform some arbitrary operation. If <code>true</code>\n");
		sb.append(" * is returned, the given node's child nodes will be visited next; however,\n");
		sb.append(" * if <code>false</code> is returned, the given node's child nodes will \n");
		sb.append(" * not be visited. The default implementation provided by this class does\n");
		sb.append(" * nothing and returns <code>true</code>.\n");
		sb.append(" * Subclasses may reimplement this method as needed.</li>\n");
		sb.append(" * <li><code>public void endVisit(<i>T</i> node)</code> - Visits\n");
		sb.append(" * the given node to perform some arbitrary operation. When used in the\n");
		sb.append(" * conventional way, this method is called after all of the given node's\n");
		sb.append(" * children have been visited (or immediately, if <code>visit</code> returned\n");
		sb.append(" * <code>false</code>). The default implementation provided by this class does\n");
		sb.append(" * nothing. Subclasses may reimplement this method as needed.</li>\n");
		sb.append(" * </ul>\n");
		sb.append(" * </p>\n");
		sb.append(" * In addition, there are a pair of methods for visiting AST nodes in the \n");
		sb.append(" * abstract, regardless of node type:\n");
		sb.append(" * <ul>\n");
		sb.append(" * <li><code>public void preVisit(IElement node)</code> - Visits\n");
		sb.append(" * the given node to perform some arbitrary operation. \n");
		sb.append(" * This method is invoked prior to the appropriate type-specific\n");
		sb.append(" * <code>visit</code> method.\n");
		sb.append(" * The default implementation of this method does nothing.\n");
		sb.append(" * Subclasses may reimplement this method as needed.</li>\n");
		sb.append(" * <li><code>public void postVisit(IElement node)</code> - Visits\n");
		sb.append(" * the given node to perform some arbitrary operation. \n");
		sb.append(" * This method is invoked after the appropriate type-specific\n");
		sb.append(" * <code>endVisit</code> method.\n");
		sb.append(" * The default implementation of this method does nothing.\n");
		sb.append(" * Subclasses may reimplement this method as needed.</li>\n");
		sb.append(" * </ul>\n");
		sb.append(" * \n");
		sb.append(" * @see descent.core.IElement#accept(ElementVisitor)\n");
		sb.append(" */\n");
		sb.append("public abstract class ElementVisitor {\n");
		sb.append("\n");
		sb.append("\t/**\n");
		sb.append("\t * Visits the given AST node prior to the type-specific visit.\n");
		sb.append("\t * (before <code>visit</code>).\n");
		sb.append("\t * <p>\n");
		sb.append("\t * The default implementation does nothing. Subclasses may reimplement.\n");
		sb.append("\t * </p>\n");
		sb.append("\t * \n");
		sb.append("\t * @param node the node to visit\n");
		sb.append("\t */\n");
		sb.append("\tpublic void preVisit(IElement node) {\n");
		sb.append("\t	// default implementation: do nothing\n");
		sb.append("\t}\n");
		sb.append("\t/**\n");
		sb.append("\t * Visits the given AST node following the type-specific visit\n");
		sb.append("\t * (after <code>endVisit</code>).\n");
		sb.append("\t * <p>\n");
		sb.append("\t * The default implementation does nothing. Subclasses may reimplement.\n");
		sb.append("\t * </p>\n");
		sb.append("\t * \n");
		sb.append("\t * @param node the node to visit\n");
		sb.append("\t */\n");
		sb.append("\tpublic void postVisit(IElement node) {\n");
		sb.append("\t	// default implementation: do nothing\n");
		sb.append("\t}\n");
		
		File dir = new File("c:\\ary\\programacion\\java\\descent\\descent.core\\src\\descent\\core\\dom");
		String[] filenames = dir.list();
		for(String filename : filenames) {
			if (!filename.endsWith(".java")) continue;
			
			String name = filename.substring(0, filename.length() - 5);
			if (excludeList.contains(name)) continue;
			
			sb.append("\t/**\n");
			sb.append("\t * Visits the given type-specific AST node.\n");
			sb.append("\t * <p>\n");
			sb.append("\t * The default implementation does nothing and return true.\n");
			sb.append("\t * Subclasses may reimplement.\n");
			sb.append("\t * </p>\n");
			sb.append("\t * \n");
			sb.append("\t * @param node the node to visit\n");
			sb.append("\t * @return <code>true</code> if the children of this node should be\n");
			sb.append("\t * visited, and <code>false</code> if the children of this node should\n");
			sb.append("\t * be skipped\n");
			sb.append("\t */\n");
			sb.append("\tpublic boolean visit(" + name + " node) {\n");
			sb.append("\t\treturn true;\n");
			sb.append("\t}\n");
			
			sb.append("\n");
			
			sb.append("\t/**\n");
			sb.append("\t * End of visit the given type-specific AST node.\n");
			sb.append("\t * <p>\n");
			sb.append("\t * The default implementation does nothing. Subclasses may reimplement.\n");
			sb.append("\t * </p>\n");
			sb.append("\t * \n");
			sb.append("\t * @param node the node to visit\n");
			sb.append("\t */\n");
			sb.append("\tpublic void endVisit(" + name + " node) {\n");
			sb.append("\n");
			sb.append("\t}\n");		
		}
		
		sb.append("}");
		System.out.println(sb);
	}

}
