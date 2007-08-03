package dtool.descentadapter;

import melnorme.miscutil.Assert;
import descent.core.dom.IDebugDeclaration;
import descent.core.dom.IDebugStatement;
import descent.core.dom.IIftypeDeclaration;
import descent.core.dom.IStaticIfStatement;
import descent.core.dom.IVersionDeclaration;
import descent.core.dom.IVersionStatement;
import descent.internal.core.dom.Argument;
import descent.internal.core.dom.ConditionalStatement;
import descent.internal.core.dom.DebugSymbol;
import descent.internal.core.dom.TemplateAliasParameter;
import descent.internal.core.dom.TemplateTupleParameter;
import descent.internal.core.dom.TemplateTypeParameter;
import descent.internal.core.dom.TemplateValueParameter;
import dtool.dom.declarations.DeclarationAlign;
import dtool.dom.declarations.DeclarationConditional;
import dtool.dom.declarations.DeclarationConditionalDefinition;
import dtool.dom.declarations.DeclarationImport;
import dtool.dom.declarations.DeclarationInvariant;
import dtool.dom.declarations.DeclarationLinkage;
import dtool.dom.declarations.DeclarationPragma;
import dtool.dom.declarations.DeclarationProtection;
import dtool.dom.declarations.DeclarationStaticAssert;
import dtool.dom.declarations.DeclarationStorageClass;
import dtool.dom.declarations.DeclarationUnitTest;
import dtool.dom.definitions.DefinitionAggregate;
import dtool.dom.definitions.DefinitionAlias;
import dtool.dom.definitions.DefinitionEnum;
import dtool.dom.definitions.DefinitionFunction;
import dtool.dom.definitions.DefinitionMixin;
import dtool.dom.definitions.DefinitionTemplate;
import dtool.dom.definitions.DefinitionTypedef;
import dtool.dom.definitions.DefinitionVariable;
import dtool.dom.definitions.EnumMember;
import dtool.dom.definitions.TemplateParamAlias;
import dtool.dom.definitions.TemplateParamTuple;
import dtool.dom.definitions.TemplateParamType;
import dtool.dom.definitions.TemplateParamValue;

/**
 * Converts from DMD's AST to a nicer AST ("Neo AST")
 */
abstract class DeclarationConverter extends BaseConverter {


	public boolean visit(IDebugDeclaration elem) {
		return endAdapt(new DeclarationConditional(elem));
	}

	public boolean visit(IVersionDeclaration elem) {
		return endAdapt(new DeclarationConditional(elem));
	}

	public boolean visit(IIftypeDeclaration elem) {
		return endAdapt(new DeclarationConditional(elem));
	}

	public boolean visit(IDebugStatement elem) {
		return endAdapt(new DeclarationConditional((ConditionalStatement) elem));
	}

	public boolean visit(IVersionStatement elem) {
		return endAdapt(new DeclarationConditional((ConditionalStatement) elem));
	}

	public boolean visit(IStaticIfStatement elem) {
		return endAdapt(new DeclarationConditional((ConditionalStatement) elem));
	}


	/*  =======================================================  */

	public boolean visit(descent.internal.core.dom.Module elem) {
		Assert.fail(); return false;
	}
	
	public boolean visit(descent.internal.core.dom.Import elem) {
		Assert.fail(); return false;
	}
	
	public boolean visit(descent.internal.core.dom.BaseClass elem) {
		return endAdapt(new DefinitionAggregate.BaseClass(elem));
	}
	
	public boolean visit(descent.internal.core.dom.TypeSpecialization elem) {
		Assert.fail(); return false; // singleuse
	}
	
	public boolean visit(descent.internal.core.dom.TemplateParameter elem) {
		Assert.fail(); return false; // abstract class
	}

	/*  ---------  Decls  --------  */
	

	public boolean visit(descent.internal.core.dom.AlignDeclaration elem) {
		return endAdapt(new DeclarationAlign(elem));
	}

	public boolean visit(descent.internal.core.dom.ConditionalDeclaration elem) {
		return endAdapt(new DeclarationConditional(elem));
	}

	public boolean visit(descent.internal.core.dom.ImportDeclaration elem) {
		return endAdapt(new DeclarationImport(elem));
	}

	public boolean visit(descent.internal.core.dom.InvariantDeclaration elem) {
		return endAdapt(new DeclarationInvariant(elem));
	}

	public boolean visit(descent.internal.core.dom.LinkDeclaration elem) {
		return endAdapt(new DeclarationLinkage(elem));
	}

	public boolean visit(descent.internal.core.dom.PragmaDeclaration elem) {
		return endAdapt(new DeclarationPragma(elem));
	}

	public boolean visit(descent.internal.core.dom.ProtDeclaration elem) {
		return endAdapt(new DeclarationProtection(elem));
	}

	public boolean visit(descent.internal.core.dom.StaticAssert elem) {
		return endAdapt(new DeclarationStaticAssert(elem));
	}

	public boolean visit(descent.internal.core.dom.StaticIfDeclaration elem) {
		return endAdapt(new DeclarationConditional(elem));
	}

	public boolean visit(descent.internal.core.dom.StorageClassDeclaration elem) {
		return endAdapt(new DeclarationStorageClass(elem));
	}

	public boolean visit(descent.internal.core.dom.UnitTestDeclaration elem) {
		return endAdapt(new DeclarationUnitTest(elem));
	}

	public boolean visit(descent.internal.core.dom.VersionSymbol elem) {
		return endAdapt(new DeclarationConditionalDefinition(elem));
	}
	
	
	/*  ---------  DEFINITIONS  --------  */

	public final boolean visit(descent.internal.core.dom.AliasDeclaration elem) {
		return endAdapt(new DefinitionAlias(elem));
	}	

	
	public boolean visit(descent.internal.core.dom.TemplateDeclaration elem) {
		return endAdapt(new DefinitionTemplate(elem));
	}	
	
	public boolean visit(descent.internal.core.dom.TemplateMixin elem) {
		return endAdapt(DefinitionMixin.convertMixinInstance(elem));
	}	
	
	public boolean visit(descent.internal.core.dom.TypedefDeclaration elem) {
		return endAdapt(new DefinitionTypedef(elem));
	}	
	
	public boolean visit(descent.internal.core.dom.VarDeclaration elem) {
		return endAdapt(new DefinitionVariable(elem));
	}	
	
	public boolean visit(descent.internal.core.dom.EnumDeclaration elem) {
		return endAdapt(DefinitionEnum.convertEnumDecl(elem));
	}	
	
	public boolean visit(descent.internal.core.dom.EnumMember elem) {
		return endAdapt(new EnumMember(elem));
	}

	/* agregates */
	
	
	public boolean visit(descent.internal.core.dom.ClassDeclaration elem) {
		return endAdapt(new DefinitionAggregate(elem));
	}	
	
	public boolean visit(descent.internal.core.dom.InterfaceDeclaration elem) {
		return endAdapt(new DefinitionAggregate(elem));
	}	
	
	public boolean visit(descent.internal.core.dom.StructDeclaration elem) {
		return endAdapt(new DefinitionAggregate(elem));
	}	
	
	public boolean visit(descent.internal.core.dom.UnionDeclaration elem) {
		return endAdapt(new DefinitionAggregate(elem));
	}	
	
	
	/* --- func --- */

	public boolean visit(descent.internal.core.dom.FuncDeclaration elem) {
		return endAdapt(new DefinitionFunction(elem));
	}
	public boolean visit(Argument elem) {
		return endAdapt(DefinitionFunction.convertFunctionParameter(elem));
	}
	

	public boolean visit(descent.internal.core.dom.CtorDeclaration elem) {
		return endAdapt(new DefinitionFunction(elem));
	}
	public boolean visit(descent.internal.core.dom.DeleteDeclaration elem) {
		return endAdapt(new DefinitionFunction(elem));
	}
	public boolean visit(descent.internal.core.dom.DtorDeclaration elem) {
		return endAdapt(new DefinitionFunction(elem));
	}
	public boolean visit(descent.internal.core.dom.NewDeclaration elem) {
		return endAdapt(new DefinitionFunction(elem));
	}
	public boolean visit(descent.internal.core.dom.StaticCtorDeclaration elem) {
		return endAdapt(new DefinitionFunction(elem));
	}	
	public boolean visit(descent.internal.core.dom.StaticDtorDeclaration elem) {
		return endAdapt(new DefinitionFunction(elem));
	}
	
	/* ---- other ---- */
	
	public boolean visit(DebugSymbol elem) {
		return endAdapt(new DeclarationConditionalDefinition(elem));
	}

	
	public boolean visit(TemplateAliasParameter elem) {
		return endAdapt(new TemplateParamAlias(elem));
	}

	public boolean visit(TemplateTupleParameter elem) {
		return endAdapt(new TemplateParamTuple(elem));
	}

	public boolean visit(TemplateTypeParameter elem) {
		return endAdapt(new TemplateParamType(elem));
	}

	public boolean visit(TemplateValueParameter elem) {
		return endAdapt(new TemplateParamValue(elem));
	}

}
