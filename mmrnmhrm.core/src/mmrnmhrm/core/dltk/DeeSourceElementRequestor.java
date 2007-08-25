package mmrnmhrm.core.dltk;

import java.util.Iterator;
import java.util.List;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.ISourceElementRequestor;

import dtool.dom.definitions.BaseClass;
import dtool.dom.definitions.DefinitionAggregate;
import dtool.dom.definitions.DefinitionClass;
import dtool.dom.definitions.Module;
import dtool.dom.references.Reference;

public class DeeSourceElementRequestor extends ASTVisitor {

	private ISourceElementRequestor requestor = null;
	
	public DeeSourceElementRequestor(ISourceElementRequestor requestor) {
		this.requestor = requestor;
	}
	
	private void setupAggregateTypeInfo(DefinitionAggregate defAggr,
			ISourceElementRequestor.TypeInfo typeInfo) {
		typeInfo.name = defAggr.getName();
		typeInfo.declarationStart = defAggr.sourceStart();
		typeInfo.nameSourceStart = defAggr.defname.sourceStart();
		typeInfo.nameSourceEnd = defAggr.defname.sourceEnd() - 1;
		//typeInfo.modifiers = defAggr.modifiers.to
	}
	

	public boolean visit(ModuleDeclaration modDecl) throws Exception {
		requestor.enterModule();
		return true;
	}

	public boolean endvisit(ModuleDeclaration modDecl) throws Exception {
		requestor.exitModule(modDecl.sourceEnd());
		return true;
	}
	
	
	@Override
	public boolean visit(ASTNode node) throws Exception {
		if(node instanceof Module) {
			//requestor.enterModule();
			return true;
		} else
		if(node instanceof DefinitionClass) {
			DefinitionClass defClass = (DefinitionClass) node;
			ISourceElementRequestor.TypeInfo typeInfo = new ISourceElementRequestor.TypeInfo();
			setupAggregateTypeInfo(defClass, typeInfo);
			typeInfo.superclasses = processClassNames(defClass);
			requestor.enterType(typeInfo);
		} else
		if(node instanceof DefinitionAggregate) {
			DefinitionAggregate defAggr = (DefinitionAggregate) node;
			ISourceElementRequestor.TypeInfo typeInfo = new ISourceElementRequestor.TypeInfo();
			setupAggregateTypeInfo(defAggr, typeInfo);
			typeInfo.superclasses = EMPTY_STRING;
			requestor.enterType(typeInfo);
		}else
		
		if(node instanceof Reference) {
			Reference ref = (Reference) node;
			requestor.acceptTypeReference(ref.toString().toCharArray(), node.sourceStart());
		}
		return true;
	}

	public static final String[] EMPTY_STRING = new String[0];
	
	private String[] processClassNames(DefinitionClass defClass) {
		List<BaseClass> coll = defClass.baseClasses;
		if(coll == null) 
			return EMPTY_STRING;
		String[] strs = new String[coll.size()];
		Iterator<BaseClass> iter = coll.iterator();
		for (int i = 0; i < strs.length; i++) {
			strs[i] = iter.next().type.toString();
		}
		return strs;
	}


	
	
	@Override
	public boolean endvisit(ASTNode node) throws Exception {
		if(node instanceof Module) {
			//requestor.exitModule(node.sourceEnd());
		} else
		if(node instanceof DefinitionAggregate) {
			requestor.exitType(node.sourceEnd() -1);
		}
		return false;
	}

}
