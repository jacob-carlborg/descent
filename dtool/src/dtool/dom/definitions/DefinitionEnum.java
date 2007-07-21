package dtool.dom.definitions;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;

import descent.internal.core.dom.EnumDeclaration;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.references.Entity;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

public class DefinitionEnum extends Definition implements IScopeNode {

	public List<EnumMember> members;
	public Entity type;
	
	
	public static ASTNeoNode convertEnumDecl(EnumDeclaration elem) {
		if(elem.ident != null) {
			DefinitionEnum defEnum = new DefinitionEnum();
			defEnum.convertDsymbol(elem);
			defEnum.members = DescentASTConverter.convertManyL(elem.members, defEnum.members) ;
			defEnum.type = Entity.convertType(elem.type); 
			return defEnum;
		} else {
			EnumContainer enumContainer = new EnumContainer();
			enumContainer.setSourceRange(elem);
			enumContainer.members = DescentASTConverter.convertManyL(elem.members, enumContainer.members) ;
			enumContainer.type = Entity.convertType(elem.type); 
			return enumContainer;
		}
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, members);
		}
		visitor.endVisit(this);	

	}

	@Override
	public EArcheType getArcheType() {
		return EArcheType.Aggregate;
	}

	@Override
	public IScopeNode getMembersScope() {
		return this;
	}
	
	
	public List<IScope> getSuperScopes() {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	public Iterator<EnumMember> getMembersIterator() {
		return members.iterator();
	}

}
