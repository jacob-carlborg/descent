package descent.internal.ui.compare;

import java.util.Iterator;
import java.util.Stack;

import descent.core.dom.ASTNode;
import descent.core.dom.ASTVisitor;
import descent.core.dom.AggregateDeclaration;
import descent.core.dom.Argument;
import descent.core.dom.CompilationUnit;
import descent.core.dom.EnumDeclaration;
import descent.core.dom.EnumMember;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.Import;
import descent.core.dom.Initializer;
import descent.core.dom.ModuleDeclaration;
import descent.core.dom.Type;
import descent.core.dom.VariableDeclaration;
import descent.core.dom.VariableDeclarationFragment;

// TODO JDT UI compare implement well with D's AST
class JavaParseTreeBuilder extends ASTVisitor {

    private char[] fBuffer;
    private Stack fStack= new Stack();
    private JavaNode fImportContainer;
    private boolean fShowCU;

    /*
     * Parsing is performed on the given buffer and the resulting tree (if any)
     * hangs below the given root.
     */
    JavaParseTreeBuilder(JavaNode root, char[] buffer, boolean showCU) {
        fBuffer= buffer;
        fShowCU= showCU;
        fStack.clear();
        fStack.push(root);
    }

    public boolean visit(ModuleDeclaration node) {
        new JavaNode(getCurrentContainer(), JavaNode.PACKAGE, null, node.getStartPosition(), node.getLength());
        return false;
    }

    public boolean visit(CompilationUnit node) {
        if (fShowCU)
            push(JavaNode.CU, null, node.getStartPosition(), node.getLength());
        return true;
    }

    public void endVisit(CompilationUnit node) {
        if (fShowCU)
            pop();
    }

    public boolean visit(AggregateDeclaration node) {
    	switch(node.getKind()) {
    	case CLASS:
    		push(JavaNode.CLASS, node.getName().toString(), node.getStartPosition(), node.getLength());
    		break;
    	case INTERFACE:
    		push(JavaNode.INTERFACE, node.getName().toString(), node.getStartPosition(), node.getLength());
    		break;
    	case STRUCT:
    		push(JavaNode.STRUCT, node.getName().toString(), node.getStartPosition(), node.getLength());
    		break;
    	case UNION:
    		push(JavaNode.UNION, node.getName().toString(), node.getStartPosition(), node.getLength());
    		break;
    	}
        return true;
    }

    public void endVisit(AggregateDeclaration node) {
        pop();
    }

    public boolean visit(EnumDeclaration node) {
        push(JavaNode.ENUM, node.getName().toString(), node.getStartPosition(), node.getLength());
        return true;
    }

    public void endVisit(EnumDeclaration node) {
        pop();
    }

    public boolean visit(FunctionDeclaration node) {
        String signature= getSignature(node);
        push(node.isConstructor() ? JavaNode.CONSTRUCTOR : JavaNode.METHOD, signature, node.getStartPosition(), node.getLength());
        return false;
    }

    public void endVisit(FunctionDeclaration node) {
        pop();
    }

    public boolean visit(Initializer node) {
        push(JavaNode.INIT, getCurrentContainer().getInitializerCount(), node.getStartPosition(), node.getLength());
        return false;
    }

    public void endVisit(Initializer node) {
        pop();
    }

    public boolean visit(Import node) {
        int s= node.getStartPosition();
        int l= node.getLength();
        int declarationEnd= s + l;
        if (fImportContainer == null)
            fImportContainer= new JavaNode(getCurrentContainer(), JavaNode.IMPORT_CONTAINER, null, s, l);
        String nm= node.getName().toString();
        /*
        if (node.isOnDemand())
            nm+= ".*"; //$NON-NLS-1$
        */
        new JavaNode(fImportContainer, JavaNode.IMPORT, nm, s, l);
        fImportContainer.setLength(declarationEnd - fImportContainer.getRange().getOffset() + 1);
        fImportContainer.setAppendPosition(declarationEnd + 2); // FIXME
        return false;
    }

    public boolean visit(VariableDeclarationFragment node) {
        String name= getFieldName(node);
        ASTNode parent= node.getParent();
        push(JavaNode.FIELD, name, parent.getStartPosition(), parent.getLength());
        return false;
    }

    public void endVisit(VariableDeclarationFragment node) {
        pop();
    }

    public boolean visit(EnumMember node) {
        push(JavaNode.FIELD, node.getName().toString(), node.getStartPosition(), node.getLength());
        return false;
    }

    public void endVisit(EnumMember node) {
        pop();
    }
    
    // private stuff

    /**
     * Adds a new JavaNode with the given type and name to the current
     * container.
     */
    private void push(int type, String name, int declarationStart, int length) {

        while (declarationStart > 0) {
            char c= fBuffer[declarationStart - 1];
            if (c != ' ' && c != '\t')
                break;
            declarationStart--;
            length++;
        }

        JavaNode node= new JavaNode(getCurrentContainer(), type, name, declarationStart, length);
        if (type == JavaNode.CU)
            node.setAppendPosition(declarationStart + length + 1);
        else
            node.setAppendPosition(declarationStart + length);

        fStack.push(node);
    }

    /**
     * Closes the current Java node by setting its end position and pops it off
     * the stack.
     */
    private void pop() {
        fStack.pop();
    }

    private JavaNode getCurrentContainer() {
        return (JavaNode) fStack.peek();
    }

    private String getFieldName(VariableDeclarationFragment node) {
        StringBuffer buffer= new StringBuffer();
        buffer.append(node.getName().toString());
        ASTNode parent= node.getParent();
        if (parent instanceof VariableDeclaration) {
        	VariableDeclaration fd= (VariableDeclaration) parent;
            buffer.append(" : "); //$NON-NLS-1$
            buffer.append(getType(fd.getType()));
        }
        return buffer.toString();
    }

    private String getSignature(FunctionDeclaration node) {
        StringBuffer buffer= new StringBuffer();
        buffer.append(node.getName().toString());
        buffer.append('(');
        boolean first= true;
        Iterator iterator= node.arguments().iterator();
        while (iterator.hasNext()) {
            Object parameterDecl= iterator.next();
            if (parameterDecl instanceof Argument) {
            	Argument svd= (Argument) parameterDecl;
                if (!first)
                    buffer.append(", "); //$NON-NLS-1$
                buffer.append(getType(svd.getType()));
                   
                first= false;
            }
        }
        if (node.isVariadic()) {
        	 buffer.append(", ..."); //$NON-NLS-1$
        }
        buffer.append(')');
        return buffer.toString();
    }

    private String getType(Type type) {
        String name= type.toString();
        int pos= name.lastIndexOf('.');
        if (pos >= 0)
            name= name.substring(pos + 1);
        return name;
    }
}
