package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IEnumDeclaration;
import descent.core.dom.IEnumMember;
import descent.core.dom.ISimpleName;
import descent.core.dom.IType;

public class EnumDeclaration extends Dsymbol implements IEnumDeclaration {

	public List<IEnumMember> members;
	public Type type;

	public EnumDeclaration(Identifier id, Type type) {
		this.ident = id;
		this.type = type;
	}
	
	public ISimpleName getName() {
		return ident;
	}
	
	public IType getBaseType() {
		return type;
	}
	
	public int getNodeType0() {
		return ENUM_DECLARATION;
	}

	public IEnumMember[] getMembers() {
		if (members == null) return new IEnumMember[0];
		return members.toArray(new IEnumMember[members.size()]);
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, ident);
			acceptChild(visitor, type);
			acceptChildren(visitor, members);
		}
		visitor.endVisit(this);
	}

}
