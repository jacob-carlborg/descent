package descent.core;

import java.util.StringTokenizer;


public class ASTNodeGenerator {
	
	public static void main(String[] args) {
		
		String description = "module declaration";
		String clazz = toMethod(description);
		String nodeType = toProperty(description);

		Member[] members = {
				Member.childMandatory("name", "Name", NO_CYCLE_RISK, "SimpleName"),
		};
		
		StringBuilder sb = new StringBuilder();
		
		for(Member member : members) {
			sb.append("/**\n");
			sb.append(" * The \"" + member.name + "\" structural property of this node type.\n");
			sb.append(" */\n");
			switch(member.type) {
			case SIMPLE:
				sb.append("public static final SimplePropertyDescriptor " + member.property + "_PROPERTY =\n"); 
				sb.append("\tnew SimplePropertyDescriptor(" + clazz + ".class, \"" + member.name + "\", " + member.clazz + ".class, " + (member.mandatory ? "MANDATORY" : "OPTIONAL") + "); //$NON-NLS-1$\n");
				break;
			case CHILD:
				sb.append("public static final ChildPropertyDescriptor " + member.property + "_PROPERTY =\n"); 
				sb.append("\tnew ChildPropertyDescriptor(" + clazz + ".class, \"" + member.name + "\", " + member.clazz + ".class, " + (member.mandatory ? "MANDATORY" : "OPTIONAL") + ", " + (member.cycleRisk ? "CYCLE_RISK" : "NO_CYCLE_RISK") + "); //$NON-NLS-1$\n");
				break;
			case LIST:
				sb.append("public static final ChildListPropertyDescriptor " + member.property + "_PROPERTY =\n"); 
				sb.append("\tnew ChildListPropertyDescriptor(" + clazz + ".class, \"" + member.name + "\", " + member.clazz + ".class, " + (member.cycleRisk ? "CYCLE_RISK" : "NO_CYCLE_RISK") + "); //$NON-NLS-1$\n");
				break;
			}
			sb.append("\n");
		}
		
		sb.append("/**\n");
		sb.append(" * A list of property descriptors (element type: \n");
		sb.append(" * {@link StructuralPropertyDescriptor}),\n");
		sb.append(" * or null if uninitialized.\n");
		sb.append(" */\n");
		sb.append("private static final List PROPERTY_DESCRIPTORS;\n");
		sb.append("\n");
		
		sb.append("static {\n");
		sb.append("\tList properyList = new ArrayList(" + members.length + ");\n");
		sb.append("\tcreatePropertyList(" + clazz + ".class, properyList);\n");
		for(Member member : members) {
			sb.append("\taddProperty(" + member.property + "_PROPERTY, properyList);\n");
		}
		sb.append("\tPROPERTY_DESCRIPTORS = reapPropertyList(properyList);\n");
		sb.append("}\n");
		sb.append("\n");
		
		sb.append("/**\n");
		sb.append(" * Returns a list of structural property descriptors for this node type.\n");
		sb.append(" * Clients must not modify the result.\n");
		sb.append(" * \n");
		sb.append(" * @param apiLevel the API level; one of the\n");
		sb.append(" * <code>AST.JLS*</code> constants\n");
		sb.append("\n");
		sb.append(" * @return a list of property descriptors (element type: \n");
		sb.append(" * {@link StructuralPropertyDescriptor})\n");
		sb.append(" * @since 3.0\n");
		sb.append(" */\n");
		sb.append("public static List propertyDescriptors(int apiLevel) {\n");
		sb.append("	return PROPERTY_DESCRIPTORS;\n");
		sb.append("}\n");
		sb.append("\n");
		
		for(Member member : members) {
			if (member.type == LIST) {
				sb.append("/**\n");
				sb.append(" * The " + member.description + "\n");
				sb.append(" * (element type: <code>" + member.clazz + "</code>).\n");
				sb.append(" * Defaults to an empty list.\n");
				sb.append(" */\n");
				sb.append("private ASTNode.NodeList " + member.name + " =\n");
				sb.append("	new ASTNode.NodeList(" + member.property + "_PROPERTY);\n");
			} else {
				sb.append("/**\n");
				sb.append(" * The " + member.name + ".\n");
				sb.append(" */\n");
				sb.append("private " + member.clazz + " " + member.name + ";\n");
				sb.append("\n");
			}
		}
		sb.append("\n");
		
		sb.append("/**\n");
		sb.append(" * Creates a new unparented " + description + " node owned by the given \n");
		sb.append(" * AST.\n");
		sb.append(" * <p>\n");
		sb.append(" * N.B. This constructor is package-private.\n");
		sb.append(" * </p>\n");
		sb.append(" * \n");
		sb.append(" * @param ast the AST that is to own this node\n");
		sb.append(" */\n");
		sb.append(clazz + "(AST ast) {\n");
		sb.append("	super(ast);\n");
		sb.append("}\n");
		sb.append("\n");
		
		sb.append("/* (omit javadoc for this method)\n");
		sb.append(" * Method declared on ASTNode.\n");
		sb.append(" */\n");
		sb.append("final List internalStructuralPropertiesForType(int apiLevel) {\n");
		sb.append("	return propertyDescriptors(apiLevel);\n");
		sb.append("}\n");
		sb.append("\n");
		
		if (hasSimpleObject(members)) {
			sb.append("/* (omit javadoc for this method)\n");
			sb.append(" * Method declared on ASTNode.\n");
			sb.append(" */\n");
			sb.append("final Object internalGetSetObjectProperty(SimplePropertyDescriptor property, boolean get, Object value) {\n");
			for(Member member : members) {
				if (member.type != SIMPLE || "int".equals(member.clazz) || "boolean".equals(member.clazz)) continue;
				
				sb.append("	if (property == " + member.property + "_PROPERTY) {\n");
				sb.append("		if (get) {\n");
				sb.append("			return get" + member.method + "();\n");
				sb.append("		} else {\n");
				sb.append("			set" + member.method + "((" + member.clazz + ") value);\n");
				sb.append("			return null;\n");
				sb.append("		}\n");
				sb.append("	}\n");
			}
			sb.append("	// allow default implementation to flag the error\n");
			sb.append("	return super.internalGetSetObjectProperty(property, get, value);\n");
			sb.append("}\n");
			sb.append("\n");
		}
		
		if (hasSimpleInt(members)) {
			sb.append("/* (omit javadoc for this method)\n");
			sb.append(" * Method declared on ASTNode.\n");
			sb.append(" */\n");
			sb.append("final int internalGetSetIntProperty(SimplePropertyDescriptor property, boolean get, int value) {\n");
			for(Member member : members) {
				if (member.type != SIMPLE || !member.clazz.equals("int")) continue;
				
				sb.append("	if (property == " + member.property + "_PROPERTY) {\n");
				sb.append("		if (get) {\n");
				sb.append("			return get" + member.method + "();\n");
				sb.append("		} else {\n");
				sb.append("			set" + member.method + "(value);\n");
				sb.append("			return 0;\n");
				sb.append("		}\n");
				sb.append("	}\n");
			}
			sb.append("	// allow default implementation to flag the error\n");
			sb.append("	return super.internalGetSetIntProperty(property, get, value);\n");
			sb.append("}\n");
			sb.append("\n");
		}
		
		if (hasSimpleBoolean(members)) {
			sb.append("/* (omit javadoc for this method)\n");
			sb.append(" * Method declared on ASTNode.\n");
			sb.append(" */\n");
			sb.append("final boolean internalGetSetBooleanProperty(SimplePropertyDescriptor property, boolean get, boolean value) {\n");
			for(Member member : members) {
				if (member.type != SIMPLE || !member.clazz.equals("boolean")) continue;
				
				sb.append("	if (property == " + member.property + "_PROPERTY) {\n");
				sb.append("		if (get) {\n");
				sb.append("			return get" + member.method + "();\n");
				sb.append("		} else {\n");
				sb.append("			set" + member.method + "(value);\n");
				sb.append("			return false;\n");
				sb.append("		}\n");
				sb.append("	}\n");
			}
			sb.append("	// allow default implementation to flag the error\n");
			sb.append("	return super.internalGetSetBooleanProperty(property, get, value);\n");
			sb.append("}\n");
			sb.append("\n");
		}
		
		if (hasChild(members)) {
			sb.append("/* (omit javadoc for this method)\n");
			sb.append(" * Method declared on ASTNode.\n");
			sb.append(" */\n");
			sb.append("final ASTNode internalGetSetChildProperty(ChildPropertyDescriptor property, boolean get, ASTNode child) {\n");
			for(Member member : members) {
				if (member.type != CHILD) continue;
				
				sb.append("	if (property == " + member.property + "_PROPERTY) {\n");
				sb.append("		if (get) {\n");
				sb.append("			return get" + member.method + "();\n");
				sb.append("		} else {\n");
				sb.append("			set" + member.method + "((" + member.clazz + ") child);\n");
				sb.append("			return null;\n");
				sb.append("		}\n");
				sb.append("	}\n");
			}
			sb.append("	// allow default implementation to flag the error\n");
			sb.append("	return super.internalGetSetChildProperty(property, get, child);\n");
			sb.append("}\n");
			sb.append("\n");
		}
		
		if (hasList(members)) {
			sb.append("/* (omit javadoc for this method)\n");
			sb.append(" * Method declared on ASTNode.\n");
			sb.append(" */\n");
			sb.append("final List internalGetChildListProperty(ChildListPropertyDescriptor property) {\n");
			for(Member member : members) {
				if (member.type != LIST) continue;
				sb.append("	if (property == " + member.property + "_PROPERTY) {\n");
				sb.append("		return " + member.method + "();\n");
				sb.append("	}\n");
			}
			sb.append("	// allow default implementation to flag the error\n");
			sb.append("	return super.internalGetChildListProperty(property);\n");
			sb.append("}\n");
			sb.append("\n");
		}
		
		sb.append("/* (omit javadoc for this method)\n");
		sb.append(" * Method declared on ASTNode.\n");
		sb.append(" * TODO make it package\n");
		sb.append(" */\n");
		sb.append("public final int getNodeType0() {\n");
		sb.append("\treturn " + nodeType + ";\n");
		sb.append("}\n");
		sb.append("\n");
		
		sb.append("/* (omit javadoc for this method)\n");
		sb.append(" * Method declared on ASTNode.\n");
		sb.append(" */\n");
		sb.append("ASTNode clone0(AST target) {\n");
		sb.append("	" + clazz + " result = new " + clazz + "(target);\n");
		sb.append("	result.setSourceRange(this.getStartPosition(), this.getLength());\n");
		for(Member member : members) {
			switch(member.type) {
			case SIMPLE:
				sb.append("	result.set" + member.method + "(get" + member.method + "());\n");
				break;
			case CHILD:
				if (member.mandatory) {
					sb.append("	result.set" + member.method + "((" + member.clazz + ") get" + member.method + "().clone(target));\n");
				} else {
					sb.append("result.set" + member.method + "((" + member.clazz + ") ASTNode.copySubtree(target, get" + member.method + "()));\n");
				}
				break;
			case LIST:
				sb.append("	result." + member.name + ".addAll(ASTNode.copySubtrees(target, " + member.method + "()));\n");
				break;
			}
		}
		sb.append("	return result;\n");
		sb.append("}\n");
		sb.append("\n");
		
		sb.append("/* (omit javadoc for this method)\n");
		sb.append(" * Method declared on ASTNode.\n");
		sb.append(" */\n");
		sb.append("final boolean subtreeMatch0(ASTMatcher matcher, Object other) {\n");
		sb.append("	// dispatch to correct overloaded match method\n");
		sb.append("	return matcher.match(this, other);\n");
		sb.append("}\n");
		sb.append("\n");
		
		sb.append("/* (omit javadoc for this method)\n");
		sb.append(" * Method declared on ASTNode.\n");
		sb.append(" */\n");
		sb.append("void accept0(ASTVisitor visitor) {\n");
		sb.append("	boolean visitChildren = visitor.visit(this);\n");
		sb.append("	if (visitChildren) {\n");
		sb.append("		// visit children in normal left to right reading order\n");
		for(Member member : members) {
			switch(member.type) {
			case SIMPLE:
				break;
			case CHILD:
				sb.append("		acceptChild(visitor, get" + member.method + "());\n");
				break;
			case LIST:
				sb.append("		acceptChildren(visitor, " + member.method + "());\n");
				break;
			}
		}
		sb.append("	}\n");
		sb.append("	visitor.endVisit(this);\n");
		sb.append("}\n");
		sb.append("\n");
		
		for(Member member : members) {
			switch(member.type) {
			case SIMPLE:
				sb.append("/**\n");
				sb.append(" * Returns the " + member.description + " of this " + description + ".\n");
				sb.append(" * \n");
				sb.append(" * @return the " + member.description + "\n");
				sb.append(" */ \n");
				sb.append("public " + member.clazz + " get" + member.method + "() {\n");
				sb.append("	return this." + member.name + ";\n");
				sb.append("}\n");
				sb.append("\n");
				
				sb.append("/**\n");
				sb.append(" * Sets the " + member.description + " of this " + description + ".\n");
				sb.append(" * \n");
				sb.append(" * @param " + member.name + " the " + member.description + "\n");
				sb.append(" * @exception IllegalArgumentException if the argument is incorrect\n");
				sb.append(" */ \n");
				sb.append("public void set" + member.method + "(" + member.clazz + " " + member.name + ") {\n");
				sb.append("	if (" + member.name + " == null) {\n");
				sb.append("		throw new IllegalArgumentException();\n");
				sb.append("	}\n");
				sb.append("	preValueChange(" + member.property + "_PROPERTY);\n");
				sb.append("	this." + member.name + " = " + member.name + ";\n");
				sb.append("	postValueChange(" + member.property + "_PROPERTY);\n");
				sb.append("}\n");
				sb.append("\n");
				break;
			case CHILD:
				sb.append("/**\n");
				sb.append(" * Returns the " + member.description + " of this " + description + ".\n");
				sb.append(" * \n");
				sb.append(" * @return the " + member.description + "\n");
				sb.append(" */ \n");
				sb.append("public " + member.clazz + " get" + member.method + "() {\n");
				if (member.mandatory) {
					sb.append("	if (this." + member.name + " == null) {\n");
					sb.append("		// lazy init must be thread-safe for readers\n");
					sb.append("		synchronized (this) {\n");
					sb.append("			if (this." + member.name + " == null) {\n");
					sb.append("				preLazyInit();\n");
					sb.append("				this." + member.name + " = new " + member.lazyInit + "(this.ast);\n");
					sb.append("				postLazyInit(this." + member.name + ", " + member.property + "_PROPERTY);\n");
					sb.append("			}\n");
					sb.append("		}\n");
					sb.append("	}\n");
				}
				sb.append("	return this." + member.name + ";\n");
				sb.append("}\n");
				sb.append("\n");
				
				sb.append("/**\n");
				sb.append(" * Sets the " + member.description + " of this " + description + ".\n");
				sb.append(" * \n");
				sb.append(" * @param " + member.name + " the " + member.description + "\n");
				sb.append(" * @exception IllegalArgumentException if:\n");
				sb.append(" * <ul>\n");
				sb.append(" * <li>the node belongs to a different AST</li>\n");
				sb.append(" * <li>the node already has a parent</li>\n");
				sb.append(" * <li>a cycle in would be created</li>\n");
				sb.append(" * </ul>\n");
				sb.append(" */ \n");
				sb.append("public void set" + member.method + "(" + member.clazz + " " + member.name + ") {\n");
				if (member.mandatory) {
					sb.append("	if (" + member.name + " == null) {\n");
					sb.append("		throw new IllegalArgumentException();\n");
					sb.append("	}\n");
				}
				sb.append("	ASTNode oldChild = this." + member.name + ";\n");
				sb.append("	preReplaceChild(oldChild, " + member.name + ", " + member.property + "_PROPERTY);\n");
				sb.append("	this." + member.name + " = " + member.name + ";\n");
				sb.append("	postReplaceChild(oldChild, " + member.name + ", " + member.property + "_PROPERTY);\n");
				sb.append("}\n");
				sb.append("\n");
				break;
			case LIST:
				sb.append("/**\n");
				sb.append(" * Returns the live ordered list of " + member.description + " for this\n");
				sb.append(" * " + description + ".\n");
				sb.append(" * \n");
				sb.append(" * @return the live list of " + description + "\n");
				sb.append(" *    (element type: <code>" + member.clazz + "</code>)\n");
				sb.append(" */ \n");
				sb.append("public List<" + member.clazz + "> " + member.method + "() {\n");
				sb.append("	return this." + member.name + ";\n");
				sb.append("}\n");
				sb.append("\n");
				break;
			}
		}
		
		sb.append("/* (omit javadoc for this method)\n");
		sb.append(" * Method declared on ASTNode.\n");
		sb.append(" */\n");
		sb.append("int memSize() {\n");
		sb.append("	return BASE_NODE_SIZE + " + members.length + " * 4;\n");
		sb.append("}\n");
		sb.append("\n");
		sb.append("/* (omit javadoc for this method)\n");
		sb.append(" * Method declared on ASTNode.\n");
		sb.append(" */\n");
		sb.append("int treeSize() {\n");
		sb.append("	return\n");
		sb.append("		memSize()\n");
		for(Member member : members) {
			switch(member.type) {
			case SIMPLE:
				break;
			case CHILD:
				sb.append("		+ (this." + member.name + " == null ? 0 : get" + member.method + "().treeSize())\n");
				break;
			case LIST:
				sb.append("		+ (this." + member.name + ".listSize())\n");
				break;
			}
		}
		sb.append(";\n");
		sb.append("}\n");
		
		sb.append("\n");
		sb.append("===========================================================================\n");
		sb.append("\n");
		
		sb.append("/**\n");
		sb.append(" * Returns whether the given node and the other object match.\n");
		sb.append(" * <p>\n");
		sb.append(" * The default implementation provided by this class tests whether the\n");
		sb.append(" * other object is a node of the same type with structurally isomorphic\n");
		sb.append(" * child subtrees. Subclasses may override this method as needed.\n");
		sb.append(" * </p>\n");
		sb.append(" * \n");
		sb.append(" * @param node the node\n");
		sb.append(" * @param other the other object, or <code>null</code>\n");
		sb.append(" * @return <code>true</code> if the subtree matches, or \n");
		sb.append(" *   <code>false</code> if they do not match or the other object has a\n");
		sb.append(" *   different node type or is <code>null</code>\n");
		sb.append(" */\n");
		sb.append("public boolean match(" + clazz + " node, Object other) {\n");
		if (members.length == 0) {
			sb.append("	return other instanceof " + clazz + ";\n");
		} else {
			sb.append("	if (!(other instanceof " + clazz + ")) {\n");
			sb.append("		return false;\n");
			sb.append("	}\n");
			sb.append("	" + clazz + " o = (" + clazz + ") other;\n");
			sb.append("	return (\n");
			
			boolean first = true;
			for(Member member : members) {
				sb.append("		");
				if (!first) {
					sb.append("&& ");
				}
				
				switch(member.type) {
				case SIMPLE:
					sb.append("node.get" + member.method + "() == o.get" + member.method + "()\n");
					break;
				case CHILD:
					sb.append("safeSubtreeMatch(node.get" + member.method + "(), o.get" + member.method + "())\n");
					break;
				case LIST:
					sb.append("safeSubtreeListMatch(node." + member.method + "(), o." + member.method + "())\n");
					break;
				}
				
				first = false;
			}
			
			sb.append("		);\n");
		}
		sb.append("}\n");
		
		System.out.print(sb);			
	}
	
	public static boolean hasSimpleBoolean(Member[] members) {
		return has(members, SIMPLE, "boolean");
	}
	
	public static boolean hasSimpleInt(Member[] members) {
		return has(members, SIMPLE, "int");
	}
	
	public static boolean hasSimpleObject(Member[] members) {
		for(Member member : members) {
			if (member.type == SIMPLE && !member.clazz.equals("int") && !member.clazz.equals("boolean")) return true;
		}
		return false;
	}
	
	public static boolean hasChild(Member[] members) {
		return has(members, CHILD);
	}
	
	public static boolean hasList(Member[] members) {
		return has(members, LIST);
	}
	
	public static boolean has(Member[] members, int type) {
		for(Member member : members) {
			if (member.type == type) return true;
		}
		return false;
	}
	
	public static boolean has(Member[] members, int type, String match) {
		for(Member member : members) {
			if (member.type == type && member.clazz.equals(match)) return true;
		}
		return false;
	}
	
	final static int SIMPLE = 1;
	final static int CHILD = 2;
	final static int LIST = 3;
	
	final static boolean MANDATORY = true;
	final static boolean OPTIONAL = false;
	final static boolean CYCLE_RISK = true;
	final static boolean NO_CYCLE_RISK = false;
	
	static class Member {
		
		public int type;
		public String property;
		public String name;
		public String description;
		public String clazz;
		public String method;
		public boolean mandatory;
		public boolean cycleRisk;
		public String lazyInit;
		
		private Member() { }
		
		public static Member simple(String description, String clazz) {
			Member member = new Member();
			member.type = SIMPLE;
			member.property = toProperty(description);
			member.name = toName(description);
			member.description = description;
			member.clazz = clazz;
			member.method = toMethod(description);
			member.mandatory = OPTIONAL;
			return member;
		}
		
		public static Member simpleMandatory(String description, String clazz, String lazyInit) {
			Member member = new Member();
			member.type = SIMPLE;
			member.property = toProperty(description);
			member.name = toName(description);
			member.description = description;
			member.clazz = clazz;
			member.method = toMethod(description);
			member.mandatory = MANDATORY;
			member.lazyInit = lazyInit;
			return member;
		}
		
		public static Member child(String description, String clazz, boolean cycleRisk) {
			Member member = new Member();
			member.type = CHILD;
			member.property = toProperty(description);
			member.name = toName(description);
			member.description = description;
			member.clazz = clazz;
			member.method = toMethod(description);
			member.mandatory = OPTIONAL;
			member.cycleRisk = cycleRisk;
			return member;
		}
		
		public static Member childMandatory(String description, String clazz, boolean cycleRisk, String lazyInit) {
			Member member = new Member();
			member.type = CHILD;
			member.property = toProperty(description);
			member.name = toName(description);
			member.description = description;
			member.clazz = clazz;
			member.method = toMethod(description);
			member.mandatory = MANDATORY;
			member.cycleRisk = cycleRisk;
			member.lazyInit = lazyInit;
			return member;
		}
		
		public static Member list(String description, String clazz, boolean cycleRisk) {
			Member member = new Member();
			member.type = LIST;
			member.property = toProperty(description);
			member.name = toName(description);
			member.description = description;
			member.clazz = clazz;
			member.method = toName(description);
			member.cycleRisk = cycleRisk;
			return member;
		}
		
	}
	
	private static String toProperty(String s) {
		StringBuilder sb = new StringBuilder();
		
		StringTokenizer st = new StringTokenizer(s, " ");
		String first = st.nextToken();
		sb.append(first.toUpperCase());
		while(st.hasMoreTokens()) {
			sb.append("_");
			sb.append(st.nextToken().toUpperCase());
		}
		
		return sb.toString();
	}
	
	private static String toName(String s) {
		StringBuilder sb = new StringBuilder();
		
		StringTokenizer st = new StringTokenizer(s, " ");
		String first = st.nextToken();
		sb.append(first);
		while(st.hasMoreTokens()) {
			sb.append(toWord(st.nextToken()));
		}
		
		return sb.toString();
	}
	
	private static String toMethod(String s) {
		StringBuilder sb = new StringBuilder();
		
		StringTokenizer st = new StringTokenizer(s, " ");
		String first = st.nextToken();
		sb.append(toWord(first));
		while(st.hasMoreTokens()) {
			sb.append(toWord(st.nextToken()));
		}
		
		return sb.toString();
	}
	
	private static String toWord(String s) {
		if (s.length() == 0) return s;
		if (s.length() == 1) return s.toUpperCase();
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

}
