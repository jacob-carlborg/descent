package dtool.dom.definitions;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.ArrayUtil;
import melnorme.miscutil.Assert;
import melnorme.miscutil.ChainedIterator;
import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.TemplateDeclaration;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.declarations.Declaration;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

/*
 */
public class DefinitionTemplate extends Definition implements IScopeNode {

	public final TemplateParameter[] templateParams; 
	public final ASTNeoNode[] decls;
	public final boolean wrapper;

	
	public DefinitionTemplate(TemplateDeclaration elem) {
		super(elem);
		this.decls = Declaration.convertMany(elem.members);
		this.templateParams = TemplateParameter.convertMany(elem.parameters);
		this.wrapper = elem.wrapper;
		if(wrapper)
			Assert.isTrue(decls.length == 1);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, templateParams);
			TreeVisitor.acceptChildren(visitor, decls);
		}
		visitor.endVisit(this);
	}

	@Override
	public EArcheType getArcheType() {
		return EArcheType.Template;
	}

	@Override
	public IScopeNode getMembersScope() {
		return this;
	}

	
	
	public List<IScope> getSuperScopes() {
		// TODO: template super scope
		return null;
	}
	

	public Iterator<? extends IASTNode> getMembersIterator() {
		// TODO: check if in a template invocation
		if(wrapper) {
			// Go straight to decls member's members
			IScopeNode scope = ((DefUnit)decls[0]).getMembersScope();
			Iterator<? extends IASTNode> tplIter = Arrays.asList(templateParams).iterator();
			return ChainedIterator.create(tplIter, scope.getMembersIterator());
		}
		ASTNeoNode[] newar = ArrayUtil.concat(templateParams, decls, ASTNeoNode.class);
		return Arrays.asList(newar).iterator();
/*		List<ASTNode> list = new ArrayList<ASTNode>(decls.length + templateParams.length);
		list.addAll(Arrays.asList(decls));
		list.addAll(Arrays.asList(templateParams));
		return 	list.iterator();*/
	}

}
