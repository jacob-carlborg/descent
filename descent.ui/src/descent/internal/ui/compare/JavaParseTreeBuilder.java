package descent.internal.ui.compare;

import java.util.Iterator;
import java.util.Stack;

import descent.core.dom.ASTNode;
import descent.core.dom.ASTVisitor;
import descent.core.dom.AbstractFunctionDeclaration;
import descent.core.dom.AggregateDeclaration;
import descent.core.dom.AliasDeclaration;
import descent.core.dom.AliasDeclarationFragment;
import descent.core.dom.AlignDeclaration;
import descent.core.dom.Argument;
import descent.core.dom.CompilationUnit;
import descent.core.dom.ConstructorDeclaration;
import descent.core.dom.EnumDeclaration;
import descent.core.dom.EnumMember;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.Import;
import descent.core.dom.ModuleDeclaration;
import descent.core.dom.TemplateDeclaration;
import descent.core.dom.Type;
import descent.core.dom.TypedefDeclaration;
import descent.core.dom.TypedefDeclarationFragment;
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
        new JavaNode(getCurrentContainer(), JavaNode.MODULE, null, node.getStartPosition(), node.getLength());
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
    	String name = node.getName() == null ? "(anonymous)" : node.getName().toString();
    	switch(node.getKind()) {
    	case CLASS:
    		push(JavaNode.CLASS, name, node.getStartPosition(), node.getLength());
    		break;
    	case INTERFACE:
    		push(JavaNode.INTERFACE, name, node.getStartPosition(), node.getLength());
    		break;
    	case STRUCT:
    		push(JavaNode.STRUCT, name, node.getStartPosition(), node.getLength());
    		break;
    	case UNION:
    		push(JavaNode.UNION, name, node.getStartPosition(), node.getLength());
    		break;
    	}
        return true;
    }

    public void endVisit(AggregateDeclaration node) {
        pop();
    }
    
    @Override
    public boolean visit(TemplateDeclaration node) {
    	push(JavaNode.TEMPLATE, node.getName().toString(), node.getStartPosition(), node.getLength());
    	return true;
    }
    
    @Override
    public void endVisit(AliasDeclaration node) {
    	pop();
    }

    public boolean visit(EnumDeclaration node) {
    	String name = node.getName() == null ? "(anonymous)" : node.getName().toString();
        push(JavaNode.ENUM, name, node.getStartPosition(), node.getLength());
        return true;
    }

    public void endVisit(EnumDeclaration node) {
        pop();
    }

    public boolean visit(FunctionDeclaration node) {
        String signature= getSignature(node);
        push(JavaNode.METHOD, signature, node.getStartPosition(), node.getLength());
        return false;
    }

    public void endVisit(FunctionDeclaration node) {
        pop();
    }
    
    @Override
    public boolean visit(ConstructorDeclaration node) {
    	String signature= getSignature(node);
    	
    	int type = 0;
    	switch(node.getKind()) {
    	case CONSTRUCTOR: type = JavaNode.CONSTRUCTOR; break;
    	case DELETE: type = JavaNode.DELETE; break;
    	case DESTRUCTOR: type = JavaNode.DESTRUCTOR; break;
    	case NEW: type = JavaNode.NEW; break;
    	case STATIC_CONSTRUCTOR: type = JavaNode.STATIC_CONSTRUCTOR; break;
    	case STATIC_DESTRUCTOR: type = JavaNode.STATIC_DESTRUCTOR; break;
    	}
    	
        push(type, signature, node.getStartPosition(), node.getLength());
        return false;
    }
    
    @Override
    public void endVisit(ConstructorDeclaration node) {
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
    
    @Override
    public boolean visit(AliasDeclarationFragment node) {
    	String name= getFieldName(node);
        ASTNode parent= node.getParent();
        push(JavaNode.ALILAS, name, parent.getStartPosition(), parent.getLength());
        return false;
    }
    
    @Override
    public void endVisit(AliasDeclarationFragment node) {
    	pop();
    }
    
    @Override
    public boolean visit(TypedefDeclarationFragment node) {
    	String name= getFieldName(node);
        ASTNode parent= node.getParent();
        push(JavaNode.TYPEDEF, name, parent.getStartPosition(), parent.getLength());
        return false;
    }
    
    @Override
    public void endVisit(TypedefDeclarationFragment node) {
    	pop();
    }

    public boolean visit(EnumMember node) {
        push(JavaNode.FIELD, node.getName().toString(), node.getStartPosition(), node.getLength());
        return false;
    }

    public void endVisit(EnumMember node) {
        pop();
    }
    
    @Override
    public boolean visit(AlignDeclaration node) {
    	push(JavaNode.ALIGN, String.valueOf(node.getAlign()), node.getStartPosition(), node.getLength());
        return true;
    }
    
    @Override
    public void endVisit(AlignDeclaration node) {
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
    
    private String getFieldName(AliasDeclarationFragment node) {
        StringBuffer buffer= new StringBuffer();
        buffer.append(node.getName().toString());
        ASTNode parent= node.getParent();
        if (parent instanceof AliasDeclaration) {
        	AliasDeclaration fd= (AliasDeclaration) parent;
            buffer.append(" : "); //$NON-NLS-1$
            buffer.append(getType(fd.getType()));
        }
        return buffer.toString();
    }
    
    private String getFieldName(TypedefDeclarationFragment node) {
        StringBuffer buffer= new StringBuffer();
        buffer.append(node.getName().toString());
        ASTNode parent= node.getParent();
        if (parent instanceof TypedefDeclaration) {
        	TypedefDeclaration fd= (TypedefDeclaration) parent;
            buffer.append(" : "); //$NON-NLS-1$
            buffer.append(getType(fd.getType()));
        }
        return buffer.toString();
    }

    private String getSignature(AbstractFunctionDeclaration node) {
        StringBuffer buffer= new StringBuffer();
        
        if (node.isFunction()) {
        	buffer.append(((FunctionDeclaration) node).getName().toString());
        } else {
        	ConstructorDeclaration c = (ConstructorDeclaration) node;
        	switch(c.getKind()) {
        	case CONSTRUCTOR: buffer.append("this"); break; //$NON-NLS-1$
        	case DELETE: buffer.append("delete"); break; //$NON-NLS-1$
        	case DESTRUCTOR: buffer.append("~this"); break; //$NON-NLS-1$
        	case NEW: buffer.append("new"); break; //$NON-NLS-1$
        	case STATIC_CONSTRUCTOR: buffer.append("static this"); break; //$NON-NLS-1$
        	case STATIC_DESTRUCTOR: buffer.append("static ~this"); break; //$NON-NLS-1$
        	default: throw new IllegalStateException();
        	}
        }
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
