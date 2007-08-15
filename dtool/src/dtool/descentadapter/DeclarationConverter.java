package dtool.descentadapter;

import melnorme.miscutil.Assert;
import descent.internal.compiler.parser.AnonDeclaration;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.AttribDeclaration;
import descent.internal.compiler.parser.DebugCondition;
import descent.internal.compiler.parser.DebugStatement;
import descent.internal.compiler.parser.DebugSymbol;
import descent.internal.compiler.parser.IftypeCondition;
import descent.internal.compiler.parser.Modifier;
import descent.internal.compiler.parser.MultiImport;
import descent.internal.compiler.parser.StaticIfCondition;
import descent.internal.compiler.parser.TemplateAliasParameter;
import descent.internal.compiler.parser.TemplateTupleParameter;
import descent.internal.compiler.parser.TemplateTypeParameter;
import descent.internal.compiler.parser.TemplateValueParameter;
import descent.internal.compiler.parser.Version;
import descent.internal.compiler.parser.VersionCondition;
import dtool.dom.declarations.DeclarationAlign;
import dtool.dom.declarations.DeclarationAnonMember;
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
import dtool.dom.definitions.BaseClass;
import dtool.dom.definitions.DefModifier;
import dtool.dom.definitions.DefinitionAlias;
import dtool.dom.definitions.DefinitionClass;
import dtool.dom.definitions.DefinitionCtor;
import dtool.dom.definitions.DefinitionEnum;
import dtool.dom.definitions.DefinitionFunction;
import dtool.dom.definitions.DefinitionInterface;
import dtool.dom.definitions.DefinitionMixin;
import dtool.dom.definitions.DefinitionStruct;
import dtool.dom.definitions.DefinitionTemplate;
import dtool.dom.definitions.DefinitionTypedef;
import dtool.dom.definitions.DefinitionUnion;
import dtool.dom.definitions.DefinitionVariable;
import dtool.dom.definitions.EnumMember;
import dtool.dom.definitions.TemplateParamAlias;
import dtool.dom.definitions.TemplateParamTuple;
import dtool.dom.definitions.TemplateParamType;
import dtool.dom.definitions.TemplateParamValue;

/**
 * Converts from DMD's AST to a nicer AST ("Neo AST")
 */
abstract class DeclarationConverter extends RefConverter {

	public boolean visit(Version node) {
		Assert.failTODO();
		return false;
	}
	
	public boolean visit(DebugSymbol elem) {
		return endAdapt(new DeclarationConditionalDefinition(elem));
	}
	
	public boolean visit(StaticIfCondition node) {
		Assert.failTODO();
		return false;
	}
	
	public boolean visit(DebugCondition node) {
		Assert.failTODO();
		return false;
	}
	
	public boolean visit(VersionCondition node) {
		Assert.failTODO();
		return false;
	}
	
	public boolean visit(IftypeCondition node) {
		Assert.failTODO();
		return false;
	}

	public boolean visit(DebugStatement node) {
		Assert.fail();
		return false;
	}


	
	public boolean visit(AnonDeclaration node) {
		return endAdapt(new DeclarationAnonMember(node));
	}

	public boolean visit(AttribDeclaration node) {
		return assertFailABSTRACT_NODE();
	}
	
	public boolean visit(Modifier node) {
		return endAdapt(new DefModifier(node));
	}

	/*  =======================================================  */

	public boolean visit(descent.internal.compiler.parser.Module elem) {
		Assert.fail(); return false;
	}
	
	public boolean visit(descent.internal.compiler.parser.Import elem) {
		Assert.fail(); return false;
	}
	
	public boolean visit(descent.internal.compiler.parser.BaseClass elem) {
		return endAdapt(new BaseClass(elem));
	}
	
	/*public boolean visit(descent.internal.compiler.parser.TypeSpecialization elem) {
		Assert.fail(); return false; // singleuse
	}*/
	
	public boolean visit(descent.internal.compiler.parser.TemplateParameter elem) {
		Assert.fail(); return false; // abstract class
	}

	/*  ---------  Decls  --------  */
	

	public boolean visit(descent.internal.compiler.parser.AlignDeclaration elem) {
		return endAdapt(new DeclarationAlign(elem));
	}

	public boolean visit(descent.internal.compiler.parser.ConditionalDeclaration elem) {
		return endAdapt(new DeclarationConditional(elem));
	}

	public boolean visit(MultiImport node) {
		return endAdapt(new DeclarationImport(node));
	}
	
	public boolean visit(descent.internal.compiler.parser.InvariantDeclaration elem) {
		return endAdapt(new DeclarationInvariant(elem));
	}

	public boolean visit(descent.internal.compiler.parser.LinkDeclaration elem) {
		return endAdapt(new DeclarationLinkage(elem));
	}

	public boolean visit(descent.internal.compiler.parser.PragmaDeclaration elem) {
		return endAdapt(new DeclarationPragma(elem));
	}

	public boolean visit(descent.internal.compiler.parser.ProtDeclaration elem) {
		return endAdapt(new DeclarationProtection(elem));
	}

	public boolean visit(descent.internal.compiler.parser.StaticAssert elem) {
		return endAdapt(new DeclarationStaticAssert(elem));
	}

	public boolean visit(descent.internal.compiler.parser.StaticIfDeclaration elem) {
		return endAdapt(new DeclarationConditional(elem));
	}

	public boolean visit(descent.internal.compiler.parser.StorageClassDeclaration elem) {
		return endAdapt(new DeclarationStorageClass(elem));
	}

	public boolean visit(descent.internal.compiler.parser.UnitTestDeclaration elem) {
		return endAdapt(new DeclarationUnitTest(elem));
	}

	public boolean visit(descent.internal.compiler.parser.VersionSymbol elem) {
		return endAdapt(new DeclarationConditionalDefinition(elem));
	}
	
	
	/*  ---------  DEFINITIONS  --------  */

	public final boolean visit(descent.internal.compiler.parser.AliasDeclaration elem) {
		return endAdapt(new DefinitionAlias(elem));
	}	

	
	public boolean visit(descent.internal.compiler.parser.TemplateDeclaration elem) {
		return endAdapt(new DefinitionTemplate(elem));
	}	
	
	public boolean visit(descent.internal.compiler.parser.TemplateMixin elem) {
		return endAdapt(DefinitionMixin.convertMixinInstance(elem));
	}	
	
	public boolean visit(descent.internal.compiler.parser.TypedefDeclaration elem) {
		return endAdapt(new DefinitionTypedef(elem));
	}	
	
	public boolean visit(descent.internal.compiler.parser.VarDeclaration elem) {
		return endAdapt(new DefinitionVariable(elem));
	}	
	
	public boolean visit(descent.internal.compiler.parser.EnumDeclaration elem) {
		return endAdapt(DefinitionEnum.convertEnumDecl(elem));
	}	
	
	public boolean visit(descent.internal.compiler.parser.EnumMember elem) {
		return endAdapt(new EnumMember(elem));
	}

	/* agregates */
	
	
	public boolean visit(descent.internal.compiler.parser.ClassDeclaration elem) {
		return endAdapt(new DefinitionClass(elem));
	}	
	
	public boolean visit(descent.internal.compiler.parser.InterfaceDeclaration elem) {
		return endAdapt(new DefinitionInterface(elem));
	}	
	
	public boolean visit(descent.internal.compiler.parser.StructDeclaration elem) {
		return endAdapt(new DefinitionStruct(elem));
	}	
	
	public boolean visit(descent.internal.compiler.parser.UnionDeclaration elem) {
		return endAdapt(new DefinitionUnion(elem));
	}	
	
	
	/* --- func --- */

	public boolean visit(descent.internal.compiler.parser.FuncDeclaration elem) {
		return endAdapt(new DefinitionFunction(elem));
	}
	public boolean visit(Argument elem) {
		return endAdapt(DefinitionFunction.convertFunctionParameter(elem));
	}
	

	public boolean visit(descent.internal.compiler.parser.CtorDeclaration elem) {
		return endAdapt(new DefinitionCtor(elem));
	}
	public boolean visit(descent.internal.compiler.parser.DtorDeclaration elem) {
		return endAdapt(new DefinitionCtor(elem));
	}
	public boolean visit(descent.internal.compiler.parser.StaticCtorDeclaration elem) {
		return endAdapt(new DefinitionCtor(elem));
	}	
	public boolean visit(descent.internal.compiler.parser.StaticDtorDeclaration elem) {
		return endAdapt(new DefinitionCtor(elem));
	}

	public boolean visit(descent.internal.compiler.parser.NewDeclaration elem) {
		return endAdapt(new DefinitionCtor(elem));
	}
	public boolean visit(descent.internal.compiler.parser.DeleteDeclaration elem) {
		return endAdapt(new DefinitionCtor(elem));
	}

	/* ---- other ---- */
	

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
