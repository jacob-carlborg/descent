package dtool.descentadapter;

import java.util.List;

import descent.core.domX.ASTNode;
import dtool.dom.DeclarationImport;
import dtool.dom.Def_EProtection;
import dtool.dom.Def_Modifiers;
import dtool.dom.Definition;
import dtool.dom.DefinitionAggregate;
import dtool.dom.DefinitionAlias;
import dtool.dom.DefinitionFunction;
import dtool.dom.DefinitionVariable;
import dtool.dom.EntityReference;
import dtool.dom.Module;
import dtool.dom.SingleEntityRef;
import dtool.dom.EntityReference.AnyEntityReference;
import dtool.dom.EntityReference.TypeEntityReference;
import dtool.dom.Module.DeclarationModule;

/**
 * Converts from DMD's AST to a proper DOM AST ("Neo AST")
 */
public class DescentASTConverter extends ASTCommonConverter {


	
	private void definitionAdapt(Definition newelem, descent.internal.core.dom.Dsymbol elem) {
		rangeAdapt(newelem, elem);

		newelem.name = (elem.ident != null) ? elem.ident.string : null; 
		newelem.modifiers = Def_Modifiers.adaptFromDescent(elem.modifiers); 
		newelem.protection = Def_EProtection.adaptFromDescent(elem.modifiers); 
	}
	

	/*  =======================================================  */


	
	public boolean visit(descent.internal.core.dom.Module elem) {
		Module newelem = new Module();
		rangeAdapt(newelem, elem);
		//newelem.name = (elem.ident != null) ? elem.ident.string : null; 
		if(elem.md != null){
			newelem.name = elem.ident.string; // If there is md there is this
			newelem.md = new DeclarationModule();
			rangeAdapt(newelem.md, elem.md);
			// XXX: Check this
			newelem.md.moduleName = new SingleEntityRef.Identifier(newelem.name);
			//newelem.md.moduleName = new SymbolReference(elem.md.qName.toString());
			newelem.md.packages = new SingleEntityRef.Identifier[elem.md.packages.size()];
			convertMany(newelem.md.packages, elem.md.packages.toArray());
		}
		newelem.members = convertMany(elem.getDeclarationDefinitions());
		return endAdapt(newelem);
	}
	
	public boolean visit(descent.internal.core.dom.ImportDeclaration elem) {
		DeclarationImport newelem = new DeclarationImport();
		rangeAdapt(newelem, elem);
		newelem.imports = (List) elem.imports;
		return endAdapt(newelem);
	}
	
	/* ---- Entities Core ---- */
	public boolean visit(descent.internal.core.dom.Identifier elem) {
		SingleEntityRef.Identifier newelem = new SingleEntityRef.Identifier(elem.string);
		rangeAdapt(newelem, elem);
		return endAdapt(newelem);
	}
	
	public boolean visit(descent.internal.core.dom.TypeIdentifier elem) {
		EntityReference newelem = new EntityReference.TypeEntityReference();

		// Check the entity reference root for module QN root 
		if(elem.startPos == -1) { 
			assert(elem.ident.string.equals(""));
			newelem.moduleRoot = true;
			newelem.ents = new SingleEntityRef[elem.idents.size()];
			convertMany(newelem.ents, elem.idents);
			// FIXME: the source range startpos
			newelem.startPos = newelem.ents[0].startPos-1;
			newelem.setEndPos(newelem.ents[newelem.ents.length-1].getEndPos());
		} else {
			newelem.moduleRoot = false;
			newelem.ents = new SingleEntityRef[elem.idents.size()+1];
			newelem.ents[0] = (SingleEntityRef) convert(elem.ident);
			convertMany(newelem.ents, elem.idents, 1);
			rangeAdapt(newelem, elem);
		}
				
		return endAdapt(newelem);
	}
	public boolean visit(descent.internal.core.dom.TypeTypeof elem) {
		EntityReference newelem = new EntityReference.TypeEntityReference();
		
		SingleEntityRef.TypeofRef typeofRef = new SingleEntityRef.TypeofRef();
		// The qualified root reference has an adapted source range
		typeofRef.startPos = elem.startPos;
		if(elem.idents.size() == 0) {
			typeofRef.length = elem.length;
		} else {
			typeofRef.length = elem.idents.get(0).startPos - elem.startPos;
		}
		typeofRef.expression = convert(elem.exp);

		newelem.moduleRoot = false;
		newelem.ents = new SingleEntityRef[elem.idents.size()+1];
		newelem.ents[0] = (SingleEntityRef) typeofRef;
		convertMany(newelem.ents, elem.idents, 1);

		rangeAdapt(newelem, elem);
		return endAdapt(newelem);
	}
	
	
	public boolean visit(descent.internal.core.dom.TypeDArray elem) {
		EntityReference newelem = (EntityReference) convert(elem.next);
		
		SingleEntityRef.TypeDynArray dynar = new SingleEntityRef.TypeDynArray();
		dynar.elemtype = newelem.ents[newelem.ents.length-1];
		newelem.ents[newelem.ents.length-1] = dynar;

		dynar.startPos = dynar.elemtype.startPos;
		dynar.setEndPos(newelem.getEndPos());

		return endAdapt(newelem);
	}
	
	
	/*  ---------  DEFINITIONS  --------  */

	public final boolean visit(descent.internal.core.dom.AliasDeclaration elem) {
		DefinitionAlias newelem = new DefinitionAlias();
		definitionAdapt(newelem, elem);
		newelem.target = new AnyEntityReference();
		TypeEntityReference typeEntRef = (TypeEntityReference)convert(elem.type);
		newelem.target.ents = typeEntRef.ents;
		newelem.target.moduleRoot = typeEntRef.moduleRoot;
		rangeAdapt(newelem.target, typeEntRef);
		return endAdapt(newelem);
	}	
	public boolean visit(descent.internal.core.dom.VarDeclaration elem) {
		DefinitionVariable newelem = new DefinitionVariable();
		definitionAdapt(newelem, elem);
		newelem.type = (TypeEntityReference)convert(elem.type);
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
		//newelem.type = elem.type;
		newelem.templateParameters = elem.templateParameters;
		//assert elem.ident == elem.outId;
		return endAdapt(newelem);
	}		
	
	/*  -------------------------------------  */
	
	public void endVisit(ASTNode element) {
		// TODO Auto-generated method stub
	}
	
}
