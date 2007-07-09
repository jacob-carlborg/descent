package descent.internal.core.dom;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;


import descent.core.dom.IEnumDeclaration;
import descent.core.dom.IEnumMember;
import descent.core.dom.IName;
import descent.core.dom.IType;
import descent.core.domX.IASTVisitor;

public class EnumDeclaration extends Dsymbol implements IEnumDeclaration {

	public List<EnumMember> members;
	public Type type;

	public EnumDeclaration(Identifier id, Type type) {
		this.ident = id;
		this.type = type;
	}
	
	public IName getName() {
		return ident;
	}
	
	public IType getBaseType() {
		return type;
	}
	
	public int getElementType() {
		return ElementTypes.ENUM_DECLARATION;
	}

	public IEnumMember[] getMembers() {
		if (members == null) return new IEnumMember[0];
		return members.toArray(new IEnumMember[members.size()]);
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, ident);
			TreeVisitor.acceptChild(visitor, type);
			TreeVisitor.acceptChildren(visitor, members);
		}
		visitor.endVisit(this);
	}

}
