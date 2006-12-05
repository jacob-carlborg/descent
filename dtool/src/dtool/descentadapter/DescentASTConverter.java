package dtool.descentadapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import descent.core.domX.ASTNode;
import descent.core.domX.ASTVisitor;
import descent.core.domX.AbstractElement;
import descent.internal.core.dom.Type;
import dtool.dom.*;
import dtool.dom.Module.DeclarationModule;
import dtool.dom.Entity.BasicEntity;
import dtool.dom.EntityReference.AnyEntityReference;
import dtool.dom.EntityReference.TypeEntityReference;

/**
 * Converts from DMD's AST to a proper DOM AST ("Neo AST")
 */
public class DescentASTConverter extends ASTVisitor {

	ASTNode ret = null;

	public ASTNode convert(descent.core.domX.AbstractElement elem) {
		elem.accept(this);
		return ret;
	}
	
	private ASTNode[] convertMany(Object[] children) {
		ASTNode[] rets = new ASTNode[children.length];
		convertMany(rets, children);
		return rets;
	}

	@SuppressWarnings("unchecked")
	private <T extends ASTNode> void convertMany(T[] rets, Object[] children) {
		for(int i = 0; i < children.length; ++i) {
			ASTNode elem = (ASTNode) children[i];
			elem.accept(this);
			rets[i] = (T) ret;
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T extends ASTNode> void convertMany(T[] rets, List children) {
		for (int i = 0; i < children.size(); ++i) {
			ASTNode elem = (ASTNode) children.get(i);
			elem.accept(this);
			rets[i] = (T) ret;
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T extends ASTNode> List<T> convertMany(List<ASTNode> children) {
		List<T> rets = new ArrayList<T>(children.size());
		for (int i = 0; i < children.size(); ++i) {
			ASTNode elem = (ASTNode) children.get(i);
			elem.accept(this);
			rets.add((T) ret);
		}
		return rets;
	}
	
	/* ---- Simple convertors ---- */
	
	private void baseAdapt(ASTElement newelem, ASTNode elem) {
		newelem.startPos = elem.getStartPos();
		newelem.length = elem.getLength();
	}

	private boolean endAdapt(ASTElement newelem) {
		ret = newelem;
		return false;
	}
	
	private void definitionAdapt(Definition newelem,
			descent.internal.core.dom.Dsymbol elem) {
		baseAdapt(newelem, elem);

		newelem.name = (elem.ident != null) ? elem.ident.string : null; 
		newelem.modifiers = Def_Modifiers.adaptFromDescent(elem.modifiers); 
		newelem.protection = Def_EProtection.adaptFromDescent(elem.modifiers); 
	}
	

	/*  =======================================================  */

	public boolean visit(ASTNode elem) {
		ret = elem;
		return false;	
	}
	
	public boolean visit(descent.internal.core.dom.Module elem) {
		Module newelem = new Module();
		baseAdapt(newelem, elem);
		//newelem.name = (elem.ident != null) ? elem.ident.string : null; 
		if(elem.md != null){
			newelem.name = elem.ident.string; // If there is md there is this
			newelem.md = new DeclarationModule();
			baseAdapt(newelem.md, elem.md);
			// XXX: Check this
			newelem.md.moduleName = new SymbolReference(newelem.name);
			//newelem.md.moduleName = new SymbolReference(elem.md.qName.toString());
			newelem.md.packages = new SymbolReference[elem.md.packages.size()];
			convertMany(newelem.md.packages, elem.md.packages.toArray());
		}
		newelem.members = convertMany(elem.getDeclarationDefinitions());
		return endAdapt(newelem);
	}
	
	public boolean visit(descent.internal.core.dom.ImportDeclaration elem) {
		DeclarationImport newelem = new DeclarationImport();
		baseAdapt(newelem, elem);
		newelem.imports = (List) elem.imports;
		return endAdapt(newelem);
	}
	
	/* ---- Ent Core ---- */
	public boolean visit(descent.internal.core.dom.Identifier elem) {
		SymbolReference newelem = new SymbolReference(elem.string);
		baseAdapt(newelem, elem);
		return endAdapt(newelem);
	}
	
	public boolean visit(descent.internal.core.dom.TypeIdentifier elem) {
		BasicEntity newelem = new BasicEntity();
		newelem.root = null; //FIXME root
		newelem.ents = new SymbolReference[elem.idents.size()];
		convertMany(newelem.ents, elem.idents);
		
		baseAdapt(newelem, elem);
		return endAdapt(newelem);
	}
	public boolean visit(descent.internal.core.dom.TypeTypeof elem) {
		BasicEntity newelem = new BasicEntity();
		newelem.root = null; //FIXME
		newelem.ents = new SymbolReference[elem.idents.size()];
		convertMany(newelem.ents, elem.idents);
		
		baseAdapt(newelem, elem);
		return endAdapt(newelem);
	}
	
	/*  ---------  DEFINITIONS  --------  */

	private void convertType(EntityReference entref, Type type) {
		entref.entity = (Entity) convert(type);
		baseAdapt(entref, type);
	}
	
	public final boolean visit(descent.internal.core.dom.AliasDeclaration elem) {
		DefinitionAlias newelem = new DefinitionAlias();
		definitionAdapt(newelem, elem);
		newelem.target = new AnyEntityReference();
		convertType(newelem.target, elem.type);
		return endAdapt(newelem);
	}	
	public boolean visit(descent.internal.core.dom.VarDeclaration elem) {
		DefinitionVariable newelem = new DefinitionVariable();
		definitionAdapt(newelem, elem);
		newelem.type = new TypeEntityReference();
		convertType(newelem.type, elem.type);
		newelem.init = elem.init;
		return endAdapt(newelem);
	}	

	public boolean visit(descent.internal.core.dom.AggregateDeclaration elem) {
		DefinitionAggregate newelem = new DefinitionAggregate();
		definitionAdapt(newelem, elem);
		newelem.members = elem.members;
		return endAdapt(newelem);
	}	

	public boolean visit(descent.internal.core.dom.FuncDeclaration elem) {
		DefinitionFunction newelem = new DefinitionFunction();
		definitionAdapt(newelem, elem);
		newelem.frequire = elem.frequire;
		newelem.fensure = elem.fensure;
		newelem.fbody = elem.fbody;
		newelem.type = elem.type;
		newelem.templateParameters = elem.templateParameters;
		//assert elem.ident == elem.outId;
		return endAdapt(newelem);
	}		
	
	/*  -------------------------------------  */
	
	public void endVisit(ASTNode element) {
		// TODO Auto-generated method stub
	}
	
}
