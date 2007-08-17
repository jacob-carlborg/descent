/**
 * 
 */
package dtool.dom.definitions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.PROT;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.references.Reference;

public class BaseClass extends ASTNeoNode {
	
	public PROT prot;
	public Reference type;
	
	public BaseClass(descent.internal.compiler.parser.BaseClass elem) {
		convertNode(elem);
		if(elem.hasNoSourceRangeInfo()) 
			convertNode(elem.type); // Try to have some range
			
		this.prot = elem.protection;
		this.type = Reference.convertType(elem.type);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
		}
		visitor.endVisit(this);	 			
	}
}