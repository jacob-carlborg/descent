package descent.internal.core.ctfe.dom;

import descent.internal.compiler.lookup.ModuleBuilder;
import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.AliasDeclaration;
import descent.internal.compiler.parser.AlignDeclaration;
import descent.internal.compiler.parser.AnonDeclaration;
import descent.internal.compiler.parser.Arguments;
import descent.internal.compiler.parser.BaseClasses;
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.CompileDeclaration;
import descent.internal.compiler.parser.Condition;
import descent.internal.compiler.parser.ConditionalDeclaration;
import descent.internal.compiler.parser.CtorDeclaration;
import descent.internal.compiler.parser.DebugSymbol;
import descent.internal.compiler.parser.DeleteDeclaration;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.DtorDeclaration;
import descent.internal.compiler.parser.EnumDeclaration;
import descent.internal.compiler.parser.EnumMember;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Initializer;
import descent.internal.compiler.parser.InterfaceDeclaration;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.LinkDeclaration;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.NewDeclaration;
import descent.internal.compiler.parser.StaticIfCondition;
import descent.internal.compiler.parser.StaticIfDeclaration;
import descent.internal.compiler.parser.StructDeclaration;
import descent.internal.compiler.parser.TemplateDeclaration;
import descent.internal.compiler.parser.TemplateParameters;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypedefDeclaration;
import descent.internal.compiler.parser.UnionDeclaration;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.compiler.parser.Version;
import descent.internal.compiler.parser.VersionSymbol;
import descent.internal.core.CompilerConfiguration;

public class CompileTimeModuleBuilder extends ModuleBuilder {

	public CompileTimeModuleBuilder(CompilerConfiguration config, ASTNodeEncoder encoder) {
		super(config, encoder);
	}
	
	@Override
	protected FuncDeclaration newFuncDeclaration(Loc loc, IdentifierExp ident, int storageClass, Type type) {
		return new CompileTimeFuncDeclaration(loc, ident, storageClass, type);
	}
	
	@Override
	protected AliasDeclaration newAliasDeclaration(Loc loc, IdentifierExp ident, Type type) {
		return new CompileTimeAliasDeclaration(loc, ident, type);
	}
	
	@Override
	protected AlignDeclaration newAlignDeclaration(int i, Dsymbols sub) {
		return new CompileTimeAlignDeclaration(i, sub);
	}
	
	@Override
	protected AnonDeclaration newAnonDeclaration(Loc loc, boolean isUnion, Dsymbols symbols) {
		return new CompileTimeAnonDeclaration(loc, isUnion, symbols);
	}
	
	@Override
	protected ClassDeclaration newClassDeclaration(Loc loc, IdentifierExp ident, BaseClasses baseClasses) {
		return new CompileTimeClassDeclaration(loc, ident, baseClasses);
	}
	
	@Override
	protected CompileDeclaration newCompileDeclaration(Loc loc, Expression exp) {
		return new CompileTimeCompileDeclaration(loc, exp);
	}
	
	@Override
	protected ConditionalDeclaration newConditionalDeclaration(Condition condition, Dsymbols thenDecls, Dsymbols elseDecls) {
		return new CompileTimeConditionalDeclaration(condition, thenDecls, elseDecls);
	}
	
	@Override
	protected CtorDeclaration newCtorDeclaration(Loc loc, Arguments arguments, int varargs) {
		return new CompileTimeCtorDeclaration(loc, arguments, varargs);
	}
	
	@Override
	protected DebugSymbol newDebugSymbol(Loc loc, IdentifierExp exp, Version version) {
		return new CompileTimeDebugSymbol(loc, exp, version);
	}
	
	@Override
	protected DebugSymbol newDebugSymbol(Loc loc, long level, Version version) {
		return new CompileTimeDebugSymbol(loc, level, version);
	}
	
	@Override
	protected DeleteDeclaration newDeleteDeclaration(Loc loc, Arguments arguments) {
		return new CompileTimeDeleteDeclaration(loc, arguments);
	}
	
	@Override
	protected DtorDeclaration newDtorDeclaration(Loc loc) {
		return new CompileTimeDtorDeclaration(loc);
	}
	
	@Override
	protected EnumDeclaration newEnumDeclaration(Loc loc, IdentifierExp ident, Type type) {
		return new CompileTimeEnumDeclaration(loc, ident, type);
	}
	
	@Override
	protected EnumMember newEnumMember(Loc loc, IdentifierExp ident, Expression expression) {
		return new CompileTimeEnumMember(loc, ident, expression);
	}
	
	@Override
	protected InterfaceDeclaration newInterfaceDeclaration(Loc loc, IdentifierExp ident, BaseClasses baseClasses) {
		return new CompileTimeInterfaceDeclaration(loc, ident, baseClasses);
	}
	
	@Override
	protected LinkDeclaration newLinkDeclaration(LINK link, Dsymbols symbols) {
		return new CompileTimeLinkDeclaration(link, symbols);
	}
	
	@Override
	protected NewDeclaration newNewDeclaration(Loc loc, Arguments arguments, int varargs) {
		return new CompileTimeNewDeclaration(loc, arguments, varargs);
	}
	
	@Override
	protected StaticIfDeclaration newStaticIfDeclaration(StaticIfCondition condition, Dsymbols thenDecls, Dsymbols elseDecls) {
		return new CompileTimeStaticIfDeclaration(condition, thenDecls, elseDecls);
	}
	
	@Override
	protected StructDeclaration newStructDeclaration(Loc loc, IdentifierExp id) {
		return new CompileTimeStructDeclaration(loc, id);
	}
	
	@Override
	protected TemplateDeclaration newTemplateDeclaration(Loc loc, IdentifierExp ident, TemplateParameters templateParameters, Expression constraint, Dsymbols symbols) {
		return new CompileTimeTemplateDeclaration(loc, ident, templateParameters, constraint,
				symbols);
	}
	
	@Override
	protected TypedefDeclaration newTypedefDeclaration(Loc loc, IdentifierExp ident, Type type, Initializer initializer) {
		return new CompileTimeTypedefDeclaration(loc, ident, type, initializer);
	}
	
	@Override
	protected UnionDeclaration newUnionDeclaration(Loc loc, IdentifierExp id) {
		return new CompileTimeUnionDeclaration(loc, id);
	}
	
	@Override
	protected VarDeclaration newVarDeclaration(Loc loc, Type type, IdentifierExp ident, Initializer initializer) {
		return new CompileTimeVarDeclaration(loc, type, ident, initializer);
	}
	
	@Override
	protected VersionSymbol newVersionSymbol(Loc loc, IdentifierExp exp, Version version) {
		return new CompileTimeVersionSymbol(loc, exp, version);
	}
	
	@Override
	protected VersionSymbol newVersionSymbol(Loc loc, long level, Version version) {
		return new CompileTimeVersionSymbol(loc, level, version);
	}

}
