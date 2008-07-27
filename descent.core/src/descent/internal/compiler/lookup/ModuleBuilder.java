package descent.internal.compiler.lookup;

import java.util.ArrayList;
import java.util.List;

import descent.core.Flags;
import descent.core.ICompilationUnit;
import descent.core.IConditional;
import descent.core.IField;
import descent.core.IImportContainer;
import descent.core.IImportDeclaration;
import descent.core.IInitializer;
import descent.core.IJavaElement;
import descent.core.IMember;
import descent.core.IMethod;
import descent.core.IPackageDeclaration;
import descent.core.IParent;
import descent.core.ISourceRange;
import descent.core.ISourceReference;
import descent.core.ITemplated;
import descent.core.IType;
import descent.core.ITypeParameter;
import descent.core.JavaModelException;
import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.AliasDeclaration;
import descent.internal.compiler.parser.AlignDeclaration;
import descent.internal.compiler.parser.AnonDeclaration;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.Arguments;
import descent.internal.compiler.parser.BaseClass;
import descent.internal.compiler.parser.BaseClasses;
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.CompileDeclaration;
import descent.internal.compiler.parser.Condition;
import descent.internal.compiler.parser.ConditionalDeclaration;
import descent.internal.compiler.parser.CtorDeclaration;
import descent.internal.compiler.parser.DebugCondition;
import descent.internal.compiler.parser.DebugSymbol;
import descent.internal.compiler.parser.DeleteDeclaration;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.DtorDeclaration;
import descent.internal.compiler.parser.EnumDeclaration;
import descent.internal.compiler.parser.EnumMember;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.HashtableOfCharArrayAndObject;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Identifiers;
import descent.internal.compiler.parser.Import;
import descent.internal.compiler.parser.Initializer;
import descent.internal.compiler.parser.InterfaceDeclaration;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.LinkDeclaration;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.ModuleDeclaration;
import descent.internal.compiler.parser.NewDeclaration;
import descent.internal.compiler.parser.PROT;
import descent.internal.compiler.parser.ProtDeclaration;
import descent.internal.compiler.parser.STC;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.StaticIfCondition;
import descent.internal.compiler.parser.StaticIfDeclaration;
import descent.internal.compiler.parser.StorageClassDeclaration;
import descent.internal.compiler.parser.StructDeclaration;
import descent.internal.compiler.parser.TemplateDeclaration;
import descent.internal.compiler.parser.TemplateMixin;
import descent.internal.compiler.parser.TemplateParameter;
import descent.internal.compiler.parser.TemplateParameters;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeFunction;
import descent.internal.compiler.parser.TypedefDeclaration;
import descent.internal.compiler.parser.UnionDeclaration;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.compiler.parser.Version;
import descent.internal.compiler.parser.VersionCondition;
import descent.internal.compiler.parser.VersionSymbol;
import descent.internal.core.CompilationUnit;
import descent.internal.core.CompilationUnitElementInfo;
import descent.internal.core.CompilerConfiguration;
import descent.internal.core.InternalSignature;
import descent.internal.core.util.Util;

/**
 * This class can create a {@link Module} from an {@link ICompilationUnit}. 
 */
public class ModuleBuilder {
	
	public boolean LAZY = true;
	
	/*
	 * We want to skip things like:
	 * 
	 * ---
	 * version(Foo) {
	 *   // ...
	 * }
	 * ---
	 * 
	 * if Foo is not in the configuration. But... what if it is like this:
	 * 
	 * ---
	 * version = Foo;
	 * 
	 * version(Foo) {
	 * }
	 * ---
	 * 
	 * Then we can just rely on the configuration.
	 * 
	 * This state is dummy:
	 * if it encounters any "version = ..." or "debug = ...", it stores
	 * it, and any version or debug declaration with that found identifier
	 * is returned normally, without skipping it.
	 */
	public static class State {
		HashtableOfCharArrayAndObject versions = new HashtableOfCharArrayAndObject();
		HashtableOfCharArrayAndObject debugs = new HashtableOfCharArrayAndObject();
	}
	
	private final ASTNodeEncoder encoder;	
	public final CompilerConfiguration config;
	
	/**
	 * Creates this module builder with the given configuration.
	 * Depending on the configuration, some version/debug blocks will not
	 * be part of the returned module.
	 */
	public ModuleBuilder(CompilerConfiguration config, ASTNodeEncoder encoder) {
		this.config = config;
		this.encoder = encoder;
	}
	
	/**
	 * Returns a {@link Module} representing the given {@link ICompilationUnit}.
	 * @param unit the unit to transform
	 * @return the module representing the unit
	 */
	public Module build(final ICompilationUnit unit) {
		CompilationUnitElementInfo info = null;
		try {
			info = (CompilationUnitElementInfo) ((CompilationUnit) unit).getElementInfo();
		} catch (JavaModelException e) {
			Util.log(e);
		}
		
		String moduleName = unit.getFullyQualifiedName();
				
		final Module module;
		if (!LAZY || "object".equals(moduleName) || (info != null && info.hasTopLevelCompileTimeDifficulties())) {
			module = new Module(unit.getElementName(), new IdentifierExp(unit.getModuleName().toCharArray()));
			assignMembers(unit, moduleName, module);
		} else {
			module = new LazyModule(unit.getElementName(), new IdentifierExp(unit.getModuleName().toCharArray()), this, info.getTopLevelIdentifiers(), info.getLastImportLocation());
			module.moduleName = moduleName;
		}
		module.setJavaElement(unit);
		
		assignPackageDeclaration(unit, module);
		
		return module;
	}
	
	public Module buildNonLazyModule(ICompilationUnit unit) {
		String moduleName = unit.getFullyQualifiedName();
		
		final Module module = new Module(unit.getElementName(), new IdentifierExp(unit.getModuleName().toCharArray()));
		assignMembers(unit, moduleName, module);
		module.setJavaElement(unit);
		
		assignPackageDeclaration(unit, module);
		
		return module;
	}

	private void assignMembers(ICompilationUnit unit, String moduleName, final Module module) {
		try {
			State state = new State();
			module.moduleName = moduleName;
			module.members = new Dsymbols();
			fill(module, module.members, unit.getChildren(), state);
		} catch (JavaModelException e) {
			Util.log(e);
		}
	}

	private void assignPackageDeclaration(ICompilationUnit unit, final Module module) {
		IPackageDeclaration[] packageDeclarations;
		try {
			packageDeclarations = unit.getPackageDeclarations();
			if (packageDeclarations.length == 1) {
				String elementName = packageDeclarations[0].getElementName();
				Identifiers packages = new Identifiers();
				IdentifierExp name = splitName(elementName, packages);	
				module.md = new ModuleDeclaration(packages, name);
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
	}
	
	public void fill(Module module, Dsymbols members, IJavaElement[] elements, 
			State state) throws JavaModelException {
		for(IJavaElement elem : elements) {
			fill(module, members, state, elem);
		}
	}

	public void fill(Module module, Dsymbols members, State state, IJavaElement elem) throws JavaModelException {
		switch(elem.getElementType()) {
		case IJavaElement.IMPORT_CONTAINER:
			IImportContainer container = (IImportContainer) elem;
			fillImportContainer(module, members, container, state);
			break;
		case IJavaElement.IMPORT_DECLARATION:
			IImportDeclaration impDecl = ((IImportDeclaration) elem);
			fillImportDeclaration(module, members, impDecl);
			break;
		case IJavaElement.FIELD:
			IField field = (IField) elem;
			fillField(module, members, field);
			break;
		case IJavaElement.TYPE:
			IType type = (IType) elem;
			fillType(module, members, type, state);
			break;
		case IJavaElement.METHOD:
			IMethod method = (IMethod) elem;
			fillMethod(module, members, method);
			break;
		case IJavaElement.INITIALIZER:
			IInitializer init = (IInitializer) elem;
			fillInitializer(module, members, init, state);
			break;
		case IJavaElement.CONDITIONAL:
			IConditional cond = (IConditional) elem;
			fillConditional(module, members, cond, state);
			break;
		}
	}

	private void fillConditional(Module module, Dsymbols members, IConditional cond,
			State state) throws JavaModelException {
		if (cond.isStaticIfDeclaration()) {
			Dsymbols thenDecls = new Dsymbols();
			fill(module, thenDecls, cond.getThenChildren(), state);
			
			Dsymbols elseDecls = new Dsymbols();
			fill(module, elseDecls, cond.getElseChildren(), state);
			
			Expression exp = encoder.decodeExpression(cond.getElementName().toCharArray());
			StaticIfCondition condition = new StaticIfCondition(getLoc(module, cond), exp);
			
			StaticIfDeclaration member = new StaticIfDeclaration(condition, thenDecls, elseDecls);
			members.add(member);
		} else if (cond.isVersionDeclaration()) {
			String name = cond.getElementName();
			char[] nameC = name.toCharArray();
			try {
				long value = Long.parseLong(name);				
				if ((state != null && state.versions.containsKey(nameC))) {
					buildConditional(module, members, cond, state, nameC, value, false /* not debug */);
				} else {
					if (config.isVersionEnabled(value) || module.versionlevel >= value) {
						fill(module, members, cond.getThenChildren(), state);
					} else {
						fill(module, members, cond.getElseChildren(), state);
					}
				}
			} catch(NumberFormatException e) {
				if ((state != null && state.versions.containsKey(nameC))) {
					buildConditional(module, members, cond, state, nameC, 0, false /* not debug */);
				} else {
					if (config.isVersionEnabled(name.toCharArray()) || (module.versionids != null && module.versionids.containsKey(nameC))) {
						fill(module, members, cond.getThenChildren(), state);
					} else {
						fill(module, members, cond.getElseChildren(), state);
					}
				}
			}
		} else if (cond.isDebugDeclaration()) {
			String name = cond.getElementName();
			char[] nameC = name.toCharArray();
			try {
				long value = Long.parseLong(name);
				if (state != null && state.debugs.containsKey(nameC)) {
					buildConditional(module, members, cond, state, nameC, value, true /* debug */);
				} else {					
					if (config.isDebugEnabled(value) || module.debuglevel >= value) {
						fill(module, members, cond.getThenChildren(), state);
					} else {
						fill(module, members, cond.getElseChildren(), state);
					}
				}
			} catch(NumberFormatException e) {
				if (state != null && state.debugs.containsKey(nameC)) {
					buildConditional(module, members, cond, state, nameC, 0, true /* debug */);
				} else {	
					if (config.isDebugEnabled(name.toCharArray()) || (module.debugids != null && module.debugids.containsKey(nameC))) {
						fill(module, members, cond.getThenChildren(), state);
					} else {
						fill(module, members, cond.getElseChildren(), state);
					}
				}
			}
		}
		// TODO iftype
	}
	
	private void buildConditional(Module module, 
			Dsymbols members, 
			IConditional cond, 
			State state, 
			char[] idC, 
			long value,
			boolean debug) throws JavaModelException {
		Dsymbols thenDecls = new Dsymbols();
		fill(module, thenDecls, cond.getThenChildren(), state);
		
		Dsymbols elseDecls = new Dsymbols();
		fill(module, elseDecls, cond.getElseChildren(), state);
		
		Condition condition = debug ? 
				new DebugCondition(module, Loc.ZERO, value, idC) : 
				new VersionCondition(module, Loc.ZERO, value, idC);
		ConditionalDeclaration member = new ConditionalDeclaration(condition, thenDecls, elseDecls);
		members.add(member);
	}

	private void fillInitializer(Module module, Dsymbols members, IInitializer init, 
			State state) throws JavaModelException {
		if (init.isAlign()) {
			Dsymbols sub = new Dsymbols();
			fill(module, sub, init.getChildren(), state);
			
			AlignDeclaration member = new AlignDeclaration(Integer.parseInt(init.getElementName()), sub);
			members.add(member);
		} else if (init.isDebugAssignment()) {
			fillDebugAssignment(module, members, init, state);
		} else if (init.isVersionAssignment()) {
			fillVersionAssignment(module, members, init, state);
		} else if (init.isMixin()) {
			Expression exp = encoder.decodeExpression(init.getElementName().toCharArray());
			CompileDeclaration member = new CompileDeclaration(getLoc(module, init), exp);
			member.setJavaElement(init);
			members.add(member);
		} else if (init.isExtern()) {
			Dsymbols symbols = new Dsymbols();
			fill(module, symbols, init.getChildren(), state);
			
			LinkDeclaration member = new LinkDeclaration(getLink(init), symbols);
			members.add(wrap(member, init));
		}
	}

	public DebugSymbol fillDebugAssignment(Module module, Dsymbols members, IInitializer init, State state) {
		char[] ident = init.getElementName().toCharArray();
		
		if (state != null) {
			state.debugs.put(ident, this);
		}
		
		Version version = new Version(getLoc(module, init), ident);
		DebugSymbol member;
		try {
			long level = Long.parseLong(init.getElementName());
			member = new DebugSymbol(getLoc(module, init), level, version);
		} catch(NumberFormatException e) {
			member = new DebugSymbol(getLoc(module, init), new IdentifierExp(ident), version);
		}
		members.add(member);
		return member;
	}

	public VersionSymbol fillVersionAssignment(Module module, Dsymbols members, IInitializer init, State state) {
		char[] ident = init.getElementName().toCharArray();
		
		if (state != null) {
			state.versions.put(ident, this);
		}
		
		Version version = new Version(getLoc(module, init), ident);
		VersionSymbol member;
		try {
			long level = Long.parseLong(init.getElementName());
			member = new VersionSymbol(getLoc(module, init), level, version);
		} catch(NumberFormatException e) {
			member = new VersionSymbol(getLoc(module, init), new IdentifierExp(ident), version);
		}
		members.add(member);
		return member;
	}

	private void fillMethod(Module module, Dsymbols members, final IMethod method) throws JavaModelException {
		if (method.isConstructor()) {
			CtorDeclaration member = new CtorDeclaration(getLoc(module, method), getArguments(method), getVarargs(method));
			member.setJavaElement(method);
			members.add(wrap(member, method));
		} else if (method.isDestructor()) {
			DtorDeclaration member = new DtorDeclaration(getLoc(module, method));
			member.setJavaElement(method);
			members.add(wrap(member, method));
		} else if (method.isNew()) {
			NewDeclaration member = new NewDeclaration(getLoc(module, method), getArguments(method), getVarargs(method));
			member.setJavaElement(method);
			members.add(wrap(member, method));
		} else if (method.isDelete()) {
			DeleteDeclaration member = new DeleteDeclaration(getLoc(module, method), getArguments(method));
			member.setJavaElement(method);
			members.add(wrap(member, method));	
		} else { 
			FuncDeclaration member = new FuncDeclaration(getLoc(module, method), getIdent(method), getStorageClass(method), getType(method));
			member.setJavaElement(method);
			members.add(wrapWithTemplate(module, member, method));
		}
	}

	public void fillType(final Module module, Dsymbols members, final IType type, 
			final State state) throws JavaModelException {
		if (type.isClass()) {
			ClassDeclaration member = new ClassDeclaration(getLoc(module, type), getIdent(type), getBaseClasses(type));
			if (!type.isForwardDeclaration()) {
				member.members = new Dsymbols();
				fill(module, member.members, type.getChildren(), state);
				member.sourceMembers = new Dsymbols(member.members);
			}
			member.setJavaElement(type);
			members.add(wrapWithTemplate(module, member, type));
		} else if (type.isInterface()) {
			InterfaceDeclaration member = new InterfaceDeclaration(getLoc(module, type), getIdent(type), getBaseClasses(type));
			if (!type.isForwardDeclaration()) {
				member.members = new Dsymbols();
				fill(module, member.members, type.getChildren(), state);
				member.sourceMembers = new Dsymbols(member.members);
			}
			member.setJavaElement(type);
			members.add(wrapWithTemplate(module, member, type));
		} else if (type.isStruct()) {
			IdentifierExp id = getIdent(type);
			if (id == null) {
				fillAnon(module, members, type, false /* is not union, is struct */, state);
			} else {
				StructDeclaration member = new StructDeclaration(getLoc(module, type), id);
				if (!type.isForwardDeclaration()) {
					member.members = new Dsymbols();
					fill(module, member.members, type.getChildren(), state);
					member.sourceMembers = new Dsymbols(member.members);
				}
				member.setJavaElement(type);
				members.add(wrapWithTemplate(module, member, type));
			}
		} else if (type.isUnion()) {
			IdentifierExp id = getIdent(type);
			if (id == null) {
				fillAnon(module, members, type, true /* is union */, state);
			} else {
				UnionDeclaration member = new UnionDeclaration(getLoc(module, type), id);
				if (!type.isForwardDeclaration()) {
					member.members = new Dsymbols();
					fill(module, member.members, type.getChildren(), state);
					member.sourceMembers = new Dsymbols(member.members);
				}
				member.setJavaElement(type);
				members.add(wrapWithTemplate(module, member, type));
			}
		} else if (type.isEnum()) {
			fillEnum(module, members, type);
		} else if (type.isTemplate()) {
			Dsymbols symbols = new Dsymbols();
			fill(module, symbols, type.getChildren(), state);
				
			TemplateDeclaration member = new TemplateDeclaration(
					getLoc(module, type), 
					getIdent(type), 
					getTemplateParameters(type),
					null, // XXX Template Constraints
					symbols);
			member.setJavaElement(type);
			members.add(wrap(member, type));
		}
	}

	public Dsymbol fillEnum(final Module module, Dsymbols members, final IType type) throws JavaModelException {
		IdentifierExp ident = getIdent(type);
		
		BaseClasses baseClasses = getBaseClasses(type);
		EnumDeclaration member = new EnumDeclaration(getLoc(module, type), ident, baseClasses.isEmpty() ? Type.tint32 : baseClasses.get(0).type);
		
		if (!type.isForwardDeclaration()) {
			member.members = new Dsymbols();
			for(IJavaElement sub : type.getChildren()) {
				IField field = (IField) sub;
				EnumMember enumMember = new EnumMember(getLoc(module, field), getIdent(field), getExpression(field));
				enumMember.setJavaElement(field);
				member.members.add(enumMember);
			}
			member.sourceMembers = new Dsymbols(member.members);
		}
		
		member.setJavaElement(type);
		Dsymbol sym = wrap(member, type);
		members.add(sym);
		return sym;
	}

	public void fillAnon(Module module, Dsymbols members, IType type, 
			boolean isUnion, State state) throws JavaModelException {
		Dsymbols symbols = new Dsymbols();
		fill(module, symbols, type.getChildren(), state);
		AnonDeclaration member = new AnonDeclaration(getLoc(module, type), isUnion, symbols);
		member.setJavaElement(type);
		members.add(wrap(member, type));
	}

	public void fillField(Module module, Dsymbols members, final IField field) throws JavaModelException {
		if (field.isVariable()) {
			VarDeclaration member = new VarDeclaration(getLoc(module, field), getType(field.getTypeSignature()), getIdent(field), getInitializer(field));
			member.setJavaElement(field);
			members.add(wrap(member, field));
		} else if (field.isAlias()) {
			AliasDeclaration member = new AliasDeclaration(getLoc(module, field), getIdent(field), getType(field.getTypeSignature()));
			member.setJavaElement(field);
			members.add(wrap(member, field));
		} else if (field.isTypedef()) {
			TypedefDeclaration member = new TypedefDeclaration(getLoc(module, field), getIdent(field), getType(field.getTypeSignature()), getInitializer(field));
			member.setJavaElement(field);
			members.add(wrap(member, field));
		} else if (field.isTemplateMixin()) {
			TemplateMixin member = encoder.decodeTemplateMixin(field.getTypeSignature(), field.getElementName());
			member.loc = getLoc(module, field);
			members.add(wrap(member, field));
		}
	}

	public Dsymbol fillImportDeclaration(Module module, Dsymbols members, IImportDeclaration impDecl) throws JavaModelException {
		String elementName = impDecl.getElementName();
		Identifiers packages = new Identifiers();
		IdentifierExp name = splitName(elementName, packages);
		IdentifierExp alias = impDecl.getAlias() == null ? null : new IdentifierExp(impDecl.getAlias().toCharArray());
		
		long flags = impDecl.getFlags();
		
		Import imp = new Import(getLoc(module, impDecl), packages, name, alias, (flags & Flags.AccStatic) != 0);
		
		String[] names = impDecl.getSelectiveImportsNames();
		String[] aliases = impDecl.getSelectiveImportsAliases();
		if (names != null) {
			imp.names = new Identifiers();
			imp.aliases = new Identifiers();
			for (int i = 0; i < names.length; i++) {
				imp.names.add(new IdentifierExp(names[i].toCharArray()));
				if (aliases[i] != null) {
					imp.aliases.add(new IdentifierExp(aliases[i].toCharArray()));
				} else {
					imp.aliases.add(null);
				}
			}
		}
		
		Dsymbol sym = wrap(imp, flags);
		members.add(sym);
		return sym;
	}
	
	private IdentifierExp splitName(String name, Identifiers packages) {
		String[] pieces = name.split("\\.");
		for (int i = 0; i < pieces.length - 1; i++) {
			packages.add(new IdentifierExp(pieces[i].toCharArray()));
		}
		return new IdentifierExp(pieces[pieces.length - 1].toCharArray());
	}

	private void fillImportContainer(Module module, Dsymbols members, IImportContainer container,
			State state) throws JavaModelException {
		fill(module, members, container.getChildren(), state);
	}
	
	public static LINK getLink(IInitializer init) {
		String name = init.getElementName();
		if ("".equals(name) || "D".equals(name)) {
			return LINK.LINKd;
		} else if ("C".equals(name)) {
			return LINK.LINKc;
		} else if ("C++".equals(name)) {
			return LINK.LINKcpp;
		} else if ("Windows".equals(name)) {
			return LINK.LINKwindows;
		} else if ("Pascal".equals(name)) {
			return LINK.LINKpascal;
		} else {
			return LINK.LINKd;
		}
	}

	private Dsymbol wrapWithTemplate(Module module, Dsymbol symbol, final ITemplated templated) throws JavaModelException {
		if (templated.isTemplate()) {
			TemplateDeclaration temp = new TemplateDeclaration(
					getLoc(module, 
							(ISourceReference) templated), 
							getIdent((IJavaElement) templated), 
							getTemplateParameters(templated),
							null, // XXX Template Constraint
							toDsymbols(symbol));
			temp.setJavaElement((IJavaElement) templated);
			return wrap(temp, (IMember) templated);
		} else {
			return wrap(symbol, (IMember) templated);
		} 
	}
	
	private TemplateParameters getTemplateParameters(ITemplated templated) throws JavaModelException {
		TemplateParameters params = new TemplateParameters();
		
		for(ITypeParameter typeParameter : templated.getTypeParameters()) {
			params.add(getTemplateParameter(
					typeParameter.getElementName(),
					typeParameter.getSignature(),
					typeParameter.getDefaultValue()));
		}
		return params;
	}

	private TemplateParameter getTemplateParameter(String name, String signature, String defaultValue) {
		TemplateParameter param = InternalSignature.toTemplateParameter(signature, defaultValue, encoder);
		param.ident = new IdentifierExp(name.toCharArray());
		return param;
	}

	private Initializer getInitializer(IField field) throws JavaModelException {
		String source = field.getInitializerSource();
		if (source == null) {
			return null;
		} else {
			return encoder.decodeInitializer(source.toCharArray());
		}
	}
	
	private Expression getExpression(IField field) throws JavaModelException {
		String source = field.getInitializerSource();
		if (source == null) {
			return null;
		} else {
			return encoder.decodeExpression(source.toCharArray());
		}
	}

	public static IdentifierExp getIdent(IJavaElement element) {
		String name = element.getElementName();
		if (name.length() == 0) {
			return null;
		} else {
			return new IdentifierExp(name.toCharArray());
		}
	}
	
	public BaseClasses getBaseClasses(IType type) throws JavaModelException {
		BaseClasses baseClasses = new BaseClasses();
		BaseClass baseClass = getBaseClass(type.getSuperclassTypeSignature());
		if (baseClass != null) {
			baseClasses.add(baseClass);
		}
		
		for(String signature : type.getSuperInterfaceTypeSignatures()) {
			baseClass = getBaseClass(signature);
			if (baseClass != null) {
				baseClasses.add(baseClass);
			}
		}
		return baseClasses;
	}
	
	public BaseClass getBaseClass(String signature) {
		if (signature == null) {
			return null;
		}
		Type type = InternalSignature.toType(signature, encoder);
		return new BaseClass(type, PROT.PROTpublic);
	}
	
	private Type getType(String signature) {
		if (signature == null || signature.length() == 0) {
			return null;
		}
		return InternalSignature.toType(signature, encoder);
	}
	
	private Type getType(IMethod method) throws JavaModelException {
		Type returnType = getType(method.getReturnType());
		Arguments args = getArguments(method);
		
		// TODO linkage
		return new TypeFunction(args, returnType, 
				getVarargs(method), 
				LINK.LINKd);
	}
	
	private int getVarargs(IMethod method) throws JavaModelException {
		return method.getVarargs();
	}

	private Arguments getArguments(IMethod method) throws JavaModelException {
		Arguments args = new Arguments();
		String[] names = method.getParameterNames();
		String[] types = method.getParameterTypes();
		String[] defaultValues = method.getParameterDefaultValues();
		for (int i = 0; i < types.length; i++) {
			args.add(getArgument(names[i], types[i], defaultValues == null ? null : defaultValues[i]));
		}
		return args;
	}
	
	private Argument getArgument(String name, String signature, String defaultValue) {
		int stc = STC.STCin;
		if (signature.charAt(0) == 'J') {
			stc = STC.STCout;
			signature = signature.substring(1);
		} else if (signature.charAt(0) == 'K') {
			stc = STC.STCref;
			signature = signature.substring(1);
		} else if (signature.charAt(0) == 'L') {
			stc = STC.STClazy;
			signature = signature.substring(1);
		}
		
		return new Argument(stc, 
				getType(signature), 
				name == null || name.length() == 0 ? null : new IdentifierExp(name.toCharArray()), 
				defaultValue == null ? null : 
					encoder.decodeExpression(defaultValue.toCharArray()));
	}
	
	
	
	public Dsymbol wrap(Dsymbol symbol, IMember member) throws JavaModelException {
		return wrap(symbol, member.getFlags());
	}
	
	public Dsymbol wrap(Dsymbol symbol, long flags) throws JavaModelException {
		int stc = getStorageClass(flags);
		if (stc != 0) {
			StorageClassDeclaration sc = new StorageClassDeclaration(stc, toDsymbols(symbol), null, false, false);
			symbol = sc;
		}
		PROT prot = getProtection(flags);
		if (prot != PROT.PROTnone) {
			ProtDeclaration pd = new ProtDeclaration(prot, toDsymbols(symbol), null, false, false);
			symbol = pd;
		}
		return symbol;
	}

	private Dsymbols toDsymbols(Dsymbol symbol) {
		Dsymbols dsymbols = new Dsymbols();
		dsymbols.add(symbol);
		return dsymbols;
	}

	private int getStorageClass(IMember member) throws JavaModelException {
		return getStorageClass(member.getFlags());
	}
		
	private int getStorageClass(long flags) throws JavaModelException {
		int storage_class = 0;
		
		if ((flags & Flags.AccAbstract) != 0) storage_class |= STC.STCabstract;
		if ((flags & Flags.AccAuto) != 0) storage_class |= STC.STCauto;
		// STC.STCcomdat
		if ((flags & Flags.AccConst) != 0) storage_class |= STC.STCconst;
		// STC.STCctorinit
		if ((flags & Flags.AccDeprecated) != 0) storage_class |= STC.STCdeprecated;
		if ((flags & Flags.AccExtern) != 0) storage_class |= STC.STCextern;
		// STC.STCfield
		if ((flags & Flags.AccFinal) != 0) storage_class |= STC.STCfinal;
		// STC.STCforeach
		if ((flags & Flags.AccIn) != 0) storage_class |= STC.STCin;
		if ((flags & Flags.AccInvariant) != 0) storage_class |= STC.STCinvariant;
		if ((flags & Flags.AccLazy) != 0) storage_class |= STC.STClazy;
		if ((flags & Flags.AccOut) != 0) storage_class |= STC.STCout;
		if ((flags & Flags.AccOverride) != 0) storage_class |= STC.STCoverride;
		// STC.STCparameter
		if ((flags & Flags.AccRef) != 0) storage_class |= STC.STCref;
		if ((flags & Flags.AccScope) != 0) storage_class |= STC.STCscope;
		if ((flags & Flags.AccStatic) != 0) storage_class |= STC.STCstatic;
		if ((flags & Flags.AccSynchronized) != 0) storage_class |= STC.STCsynchronized;
		// STC.STCtemplateparameter
		// STC.STCundefined
		// STC.STCvariadic
		
		return storage_class;
	}
	
	private PROT getProtection(long flags) throws JavaModelException {
		if ((flags & Flags.AccPublic) != 0) {
			return PROT.PROTpublic;
		} else if ((flags & Flags.AccProtected) != 0) {
			return PROT.PROTprotected;
		} else if ((flags & Flags.AccPackage) != 0) {
			return PROT.PROTpackage;
		} else if ((flags & Flags.AccExport) != 0) {
			return PROT.PROTexport;
		} else if ((flags & Flags.AccPrivate) != 0) {
			return PROT.PROTprivate;
		} else {
			return PROT.PROTnone;
		}
	}
	
	public Loc getLoc(Module module, ISourceReference source) {
		// TODO line number
		Loc loc = new Loc(module.moduleName.toCharArray(), 0);
		return loc;
	}
	
	public static class FillResult {
		public boolean hasAnonEnum;
		public boolean hasStaticIf;
		public boolean hasMixinDeclaration;
		public boolean hasAnon;
	}
	
	public FillResult fillJavaElementMembersCache(ILazy lazy, IJavaElement[] elements, HashtableOfCharArrayAndObject javaElementMembersCache, Dsymbols symbols, List<Dsymbol> privateImports, List<Dsymbol> publicImports, SemanticContext context) {
		FillResult result = new FillResult();
		
		internalFillJavaElementMembersCache(lazy, elements, javaElementMembersCache, symbols, privateImports, publicImports, context, result);
		
		return result;
	}
	
	// true if we encountered an anonymous enum
	public void internalFillJavaElementMembersCache(ILazy lazy, IJavaElement[] elements, HashtableOfCharArrayAndObject javaElementMembersCache, Dsymbols symbols, List<Dsymbol> privateImports, List<Dsymbol> publicImports, SemanticContext context, FillResult result) {
		try {
			for(IJavaElement child : elements) {
				switch(child.getElementType()) {
				case IJavaElement.IMPORT_CONTAINER:
					internalFillJavaElementMembersCache(lazy, ((IParent) child).getChildren(), javaElementMembersCache, symbols, privateImports, publicImports, context, result);
					break;
				case IJavaElement.IMPORT_DECLARATION:
					IImportDeclaration imp = (IImportDeclaration) child;
					Dsymbol s = fillImportDeclaration(lazy.getModule(), symbols, imp);
					symbols.add(s);
					if (Flags.isPublic(imp.getFlags())) {
						publicImports.add(s);
					} else {
						privateImports.add(s);
					}
					break;
				case IJavaElement.CONDITIONAL:
					IConditional cond = (IConditional) child;
					if (cond.isStaticIfDeclaration()) {
						result.hasStaticIf = true;
					} else if (cond.isVersionDeclaration() || cond.isDebugDeclaration()) {
						if (isThenActive(cond, lazy)) {
							internalFillJavaElementMembersCache(lazy, cond.getThenChildren(), javaElementMembersCache, symbols, privateImports, publicImports, context, result);
						} else {
							internalFillJavaElementMembersCache(lazy, cond.getElseChildren(), javaElementMembersCache, symbols, privateImports, publicImports, context, result);
						}
					}
					break;
				case IJavaElement.INITIALIZER:
					IInitializer init = (IInitializer) child;
					if (init.isAlign()) {
						Dsymbols sub = new Dsymbols();
						internalFillJavaElementMembersCache(lazy, init.getChildren(), javaElementMembersCache, sub, privateImports, publicImports, context, result);
						
						AlignDeclaration member = new AlignDeclaration(Integer.parseInt(init.getElementName()), sub);
						symbols.add(member);
					} else if (init.isDebugAssignment()) {
						DebugSymbol symbol = fillDebugAssignment(lazy.getModule(), symbols, init, new ModuleBuilder.State());
						symbol.addMember(lazy.getSemanticScope(), lazy.asScopeDsymbol(), 0, context);
					} else if (init.isVersionAssignment()) {
						VersionSymbol symbol = fillVersionAssignment(lazy.getModule(), symbols, init, new ModuleBuilder.State());
						symbol.addMember(lazy.getSemanticScope(), lazy.asScopeDsymbol(), 0, context);
					} else if (init.isMixin()) {
						result.hasMixinDeclaration = true;
					} else if (init.isExtern()) {
						Dsymbols sub = new Dsymbols();
						internalFillJavaElementMembersCache(lazy, init.getChildren(), javaElementMembersCache, sub, privateImports, publicImports, context, result);
						
						LinkDeclaration member = new LinkDeclaration(ModuleBuilder.getLink(init), sub);
						symbols.add(wrap(member, init));
					}
					break;
				case IJavaElement.FIELD:
				case IJavaElement.METHOD:
				case IJavaElement.TYPE:
					char[] ident = child.getElementName().toCharArray();
					if (ident == null || ident.length == 0) {
						// Anonymous: it must be an enum, at the top level there
						// isn't a use for an annonymous class, template, etc.
						if (child instanceof IType) {
							IType type = (IType) child;
							if (type.isEnum()) {
								Dsymbol sym = fillEnum(lazy.getModule(), symbols, type);
								sym.addMember(lazy.getSemanticScope(), lazy.asScopeDsymbol(), 0, context);
								lazy.runMissingSemantic(sym, context);
								result.hasAnonEnum = true;
							} else {
								result.hasAnon = true;
							}
						} else {
							result.hasAnon = true;
						}
					} else {
						if (javaElementMembersCache.containsKey(ident)) {
							Object object = javaElementMembersCache.get(ident);
							
							if (object instanceof IJavaElement) {
								List<IJavaElement> elemsList = new ArrayList<IJavaElement>();
								elemsList.add((IJavaElement) object);
								elemsList.add(child);
								javaElementMembersCache.put(ident, elemsList);
							} else {
								List<IJavaElement> elemsList = (List<IJavaElement>) object;
								elemsList.add(child);
							}
						} else {
							javaElementMembersCache.put(ident, child);
						}
					}
					break;
				case IJavaElement.PACKAGE_DECLARATION:
					break;
				default:
					throw new IllegalStateException("Unknown type: " + child.getElementType());
				}
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
	}
	
	public boolean isThenActive(IConditional cond, ILazy lazy) throws JavaModelException {
		if (cond.isVersionDeclaration()) {
			String name = cond.getElementName();
			char[] nameC = name.toCharArray();
			try {
				long value = Long.parseLong(name);
				return config.isVersionEnabled(value) || lazy.getModule().versionlevel >= value;
			} catch(NumberFormatException e) {
				return config.isVersionEnabled(nameC) || (lazy.getModule().versionids != null && lazy.getModule().versionids.containsKey(nameC));
			}
		} else if (cond.isDebugDeclaration()) {
			String name = cond.getElementName();
			char[] nameC = name.toCharArray();
			try {
				long value = Long.parseLong(name);
				return config.isDebugEnabled(value) || lazy.getModule().debuglevel >= value;
			} catch(NumberFormatException e) {
				return config.isDebugEnabled(nameC) || (lazy.getModule().debugids != null && lazy.getModule().debugids.containsKey(nameC));
			}
		} else {
			throw new IllegalStateException("Can't happen");
		}
	}
	
	public void fillImports(ILazy lazy, IJavaElement[] elements, List<Dsymbol> privateImports, List<Dsymbol> publicImports, SemanticContext context, int lastImportLocation) {
		try {
			for(IJavaElement child : elements) {
				if (child instanceof ISourceReference) {
					ISourceReference sr = (ISourceReference) child;
					ISourceRange srg = sr.getSourceRange();
					if (!(child instanceof IParent) && srg != null && srg.getOffset() + srg.getLength() > lastImportLocation) {
						break;
					}
				}
				
				switch(child.getElementType()) {
				case IJavaElement.IMPORT_CONTAINER:
					fillImports(lazy, ((IParent) child).getChildren(), privateImports, publicImports, context, lastImportLocation);
					break;
				case IJavaElement.IMPORT_DECLARATION:
					IImportDeclaration imp = (IImportDeclaration) child;
					Dsymbol s = fillImportDeclaration(lazy.getModule(), new Dsymbols(), imp);
					lazy.getModule().members.add(s);
					if (Flags.isPublic(imp.getFlags())) {
						publicImports.add(s);
					} else {
						privateImports.add(s);
					}
					break;
				case IJavaElement.CONDITIONAL:
					IConditional cond = (IConditional) child;
					if (cond.isStaticIfDeclaration()) {
						
					} else if (cond.isVersionDeclaration() || cond.isDebugDeclaration()) {
						if (isThenActive(cond, lazy)) {
							fillImports(lazy, cond.getThenChildren(), privateImports, publicImports, context, lastImportLocation);
						} else {
							fillImports(lazy, cond.getElseChildren(), privateImports, publicImports, context, lastImportLocation);
						}
					}
					break;
				case IJavaElement.INITIALIZER:
					IInitializer init = (IInitializer) child;
					if (init.isAlign()) {
						fillImports(lazy, init.getChildren(), privateImports, publicImports, context, lastImportLocation);
					} else if (init.isDebugAssignment()) {
						DebugSymbol symbol = fillDebugAssignment(lazy.getModule(), lazy.getModule().members, init, new ModuleBuilder.State());
						symbol.addMember(lazy.getSemanticScope(), lazy.asScopeDsymbol(), 0, context);
					} else if (init.isVersionAssignment()) {
						VersionSymbol symbol = fillVersionAssignment(lazy.getModule(), lazy.getModule().members, init, new ModuleBuilder.State());
						symbol.addMember(lazy.getSemanticScope(), lazy.asScopeDsymbol(), 0, context);
					} else if (init.isMixin()) {
					} else if (init.isExtern()) {
						fillImports(lazy, init.getChildren(), privateImports, publicImports, context, lastImportLocation);
					}
					break;
				case IJavaElement.FIELD:
				case IJavaElement.METHOD:
				case IJavaElement.TYPE:
				case IJavaElement.PACKAGE_DECLARATION:
					break;
				default:
					throw new IllegalStateException("Unknown type: " + child.getElementType());
				}
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
	}

}
