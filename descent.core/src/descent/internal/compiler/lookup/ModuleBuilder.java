package descent.internal.compiler.lookup;

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
import descent.core.ISourceReference;
import descent.core.ITemplated;
import descent.core.IType;
import descent.core.ITypeParameter;
import descent.core.JavaModelException;
import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.AggregateDeclaration;
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
import descent.internal.core.CompilerConfiguration;
import descent.internal.core.InternalSignature;
import descent.internal.core.util.Util;

/**
 * This class can create a {@link Module} from an {@link ICompilationUnit}. 
 */
public class ModuleBuilder {
	
	/*
	 * One ring to rule them all. 
	 */
	private final static boolean LAZY = true;
	
	/*
	 * Wether to make surface ClassDeclaration semantic lazy.
	 */
	private final static boolean LAZY_CLASSES = LAZY & true;
	
	/*
	 * Wether to make surface InterfaceDeclaration semantic lazy.
	 */
	private final static boolean LAZY_INTERFACES = LAZY & true;
	
	/*
	 * Wether to make surface StructDeclaration semantic lazy.
	 */
	private final static boolean LAZY_STRUCTS = LAZY & true;
	
	/*
	 * Wether to make surface UnionDeclaration semantic lazy.
	 */
	private final static boolean LAZY_UNIONS = LAZY & true;
	
	/*
	 * Wether to make surface AliasDeclaration semantic lazy.
	 */
	private final static boolean LAZY_ALIASES = LAZY & true;
	
	/*
	 * Wether to make surface EnumDeclaration semantic lazy.
	 */
	private final static boolean LAZY_ENUMS = LAZY & true;
	
	/*
	 * Wether to make surface TemplateDeclaration semantic lazy.
	 */
	private final static boolean LAZY_TEMPLATES = LAZY & true;
	
	/*
	 * Wether to make surface FuncDeclaration semantic lazy.
	 */
	private final static boolean LAZY_FUNCTIONS = LAZY & true;
	
	/*
	 * Wether to make surface VarDeclaration semantic lazy.
	 * Currently doesn't work.
	 */
	private final static boolean LAZY_VARS = LAZY & false;
	
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
	class State {
		HashtableOfCharArrayAndObject versions = new HashtableOfCharArrayAndObject();
		HashtableOfCharArrayAndObject debugs = new HashtableOfCharArrayAndObject();
		boolean surface = true;
	}
	
	private ASTNodeEncoder encoder = new ASTNodeEncoder();	
	private final CompilerConfiguration config;
	
	/**
	 * Creates this module builder with the given configuration.
	 * Depending on the configuration, some version/debug blocks will not
	 * be part of the returned module.
	 */
	public ModuleBuilder(CompilerConfiguration config) {
		this.config = config;
	}
	
	/**
	 * Returns a {@link Module} representing the given {@link ICompilationUnit}.
	 * @param unit the unit to transform
	 * @return the module representing the unit
	 */
	public Module build(final ICompilationUnit unit) {		
		final Module module = new Module(unit.getElementName(), new IdentifierExp(unit.getModuleName().toCharArray()));
		module.setJavaElement(unit);
		module.moduleName = unit.getFullyQualifiedName();
		
		try {
			IPackageDeclaration[] packageDeclarations = unit.getPackageDeclarations();
			if (packageDeclarations.length == 1) {
				String elementName = packageDeclarations[0].getElementName();
				Identifiers packages = new Identifiers();
				IdentifierExp name = splitName(elementName, packages);	
				module.md = new ModuleDeclaration(packages, name);
			}
			
			long time = System.currentTimeMillis();
			
			State state = new State();
			
			module.members = new Dsymbols();
			fill(module, module.members, unit.getChildren(), state);
			
			state.surface = false;
			
			time = System.currentTimeMillis() - time;
			if (time != 0) {
				System.out.println("Building module " + module.moduleName + " took: " + time + " milliseconds to complete.");
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
			Util.log(e);
			return null;
		}
		
		return module;
	}
	
	private void fill(Module module, Dsymbols members, IJavaElement[] elements, 
			State state) throws JavaModelException {
		for(IJavaElement elem : elements) {
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
				fillField(module, members, field, state);
				break;
			case IJavaElement.TYPE:
				IType type = (IType) elem;
				fillType(module, members, type, state);
				break;
			case IJavaElement.METHOD:
				IMethod method = (IMethod) elem;
				fillMethod(module, members, method, state.surface);
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
				
				if (state.versions.containsKey(nameC)) {
					buildConditional(module, members, cond, state, nameC, value, false /* not debug */);
				} else {
					if (config.isVersionEnabled(value)) {
						fill(module, members, cond.getThenChildren(), state);
					} else {
						fill(module, members, cond.getElseChildren(), state);
					}
				}
			} catch(NumberFormatException e) {
				if (state.versions.containsKey(nameC)) {
					buildConditional(module, members, cond, state, nameC, 0, false /* not debug */);
				} else {
					if (config.isVersionEnabled(name.toCharArray())) {
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
				if (state.debugs.containsKey(nameC)) {
					buildConditional(module, members, cond, state, nameC, value, true /* debug */);
				} else {					
					if (config.isDebugEnabled(value)) {
						fill(module, members, cond.getThenChildren(), state);
					} else {
						fill(module, members, cond.getElseChildren(), state);
					}
				}
			} catch(NumberFormatException e) {
				if (state.debugs.containsKey(nameC)) {
					buildConditional(module, members, cond, state, nameC, 0, true /* debug */);
				} else {	
					if (config.isDebugEnabled(name.toCharArray())) {
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
		boolean surface = state.surface;		
		state.surface = false;
		
		if (init.isAlign()) {
			Dsymbols sub = new Dsymbols();
			fill(module, sub, init.getChildren(), state);
			
			AlignDeclaration member = new AlignDeclaration(Integer.parseInt(init.getElementName()), sub);
			members.add(member);
		} else if (init.isDebugAssignment()) {
			char[] ident = init.getElementName().toCharArray();
			
			state.debugs.put(ident, this);
			
			Version version = new Version(getLoc(module, init), ident);
			try {
				long level = Long.parseLong(init.getElementName());
				DebugSymbol member = new DebugSymbol(getLoc(module, init), level, version);
				members.add(member);
			} catch(NumberFormatException e) {
				DebugSymbol member = new DebugSymbol(getLoc(module, init), new IdentifierExp(ident), version);
				members.add(member);
			}
		} else if (init.isVersionAssignment()) {
			char[] ident = init.getElementName().toCharArray();
			
			state.versions.put(ident, this);
			
			Version version = new Version(getLoc(module, init), ident);
			try {
				long level = Long.parseLong(init.getElementName());
				VersionSymbol member = new VersionSymbol(getLoc(module, init), level, version);
				members.add(member);
			} catch(NumberFormatException e) {
				VersionSymbol member = new VersionSymbol(getLoc(module, init), new IdentifierExp(ident), version);
				members.add(member);
			}
		} else if (init.isMixin()) {
			Expression exp = encoder.decodeExpression(init.getElementName().toCharArray());
			CompileDeclaration member = new CompileDeclaration(getLoc(module, init), exp);
			members.add(member);
		} else if (init.isExtern()) {
			// Also try to lazily initialize things inside:
			// extern(C) {
			//   // ...
			// }
			if (surface) {
				state.surface = surface;
			}
			
			Dsymbols symbols = new Dsymbols();
			fill(module, symbols, init.getChildren(), state);
			
			LinkDeclaration member = new LinkDeclaration(getLink(init), symbols);
			members.add(wrap(member, init));
		}
		
		state.surface = surface;
	}

	private void fillMethod(Module module, Dsymbols members, final IMethod method, boolean surface) throws JavaModelException {
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
			final FuncDeclaration member;
			if (LAZY_FUNCTIONS && surface) {
				member = new FuncDeclaration(getLoc(module, method), getIdent(method), getStorageClass(method), null);
				member.rest = new SemanticRest(new Runnable() {
					public void run() {
						try {
							member.type = getType(method);
						} catch (JavaModelException e) {
							Util.log(e);
						}
					}
				});
			} else {
				member = new FuncDeclaration(getLoc(module, method), getIdent(method), getStorageClass(method), getType(method));
			}
			
			member.setJavaElement(method);
			members.add(wrapWithTemplate(module, member, method, surface));
		}
	}

	private void fillType(final Module module, Dsymbols members, final IType type, 
			final State state) throws JavaModelException {
		boolean surface = state.surface;
		state.surface = false;
		
		if (type.isClass()) {
			final ClassDeclaration member;
			
			if (LAZY_CLASSES && surface) {
				member = new ClassDeclaration(getLoc(module, type), getIdent(type));
				member.rest = new SemanticRest(new Runnable() {
					public void run() {
						try {
							member.baseclasses = getBaseClasses(type);
							member.members = new Dsymbols();
							fill(module, member.members, type.getChildren(), state);
						} catch (JavaModelException e) {
							Util.log(e);
						}	
					};
				});
			} else {
				member = new ClassDeclaration(getLoc(module, type), getIdent(type), getBaseClasses(type));
				member.members = new Dsymbols();
				fill(module, member.members, type.getChildren(), state);
			}

			member.setJavaElement(type);
			members.add(wrapWithTemplate(module, member, type, surface));
		} else if (type.isInterface()) {
			final InterfaceDeclaration member;
			
			if (LAZY_INTERFACES && surface) {
				member = new InterfaceDeclaration(getLoc(module, type), getIdent(type), null);
				member.rest = new SemanticRest(new Runnable() {
					public void run() {
						try {
							member.baseclasses = getBaseClasses(type);
							member.members = new Dsymbols();
							fill(module, member.members, type.getChildren(), state);
						} catch (JavaModelException e) {
							Util.log(e);
						}	
					};
				});
				
			} else {
				member = new InterfaceDeclaration(getLoc(module, type), getIdent(type), getBaseClasses(type));
				
				member.members = new Dsymbols();
				fill(module, member.members, type.getChildren(), state);
			}
			
			member.setJavaElement(type);
			members.add(wrapWithTemplate(module, member, type, surface));
		} else if (type.isStruct()) {
			IdentifierExp id = getIdent(type);
			if (id == null) {
				fillAnon(module, members, type, false /* is not union, is struct */, state);
			} else {
				final StructDeclaration member;
				
				if (LAZY_STRUCTS && surface) {
					member = new StructDeclaration(getLoc(module, type), id);
					member.rest = new SemanticRest(new Runnable() {
						public void run() {
							member.members = new Dsymbols();
							try {
								fill(module, member.members, type.getChildren(), state);
							} catch (JavaModelException e) {
								Util.log(e);
							}
						}
					});
				} else {
					member = new StructDeclaration(getLoc(module, type), id);
					member.members = new Dsymbols();
					fill(module, member.members, type.getChildren(), state);
				}
				
				member.setJavaElement(type);
				members.add(wrapWithTemplate(module, member, type, surface));
			}
		} else if (type.isUnion()) {
			IdentifierExp id = getIdent(type);
			if (id == null) {
				fillAnon(module, members, type, true /* is union */, state);
			} else {
				final UnionDeclaration member;
				
				if (LAZY_UNIONS && surface) {
					member = new UnionDeclaration(getLoc(module, type), id);
					member.rest = new SemanticRest(new Runnable() {
						public void run() {
							member.members = new Dsymbols();
							try {
								fill(module, member.members, type.getChildren(), state);
							} catch (JavaModelException e) {
								Util.log(e);
							}
						}
					});
				} else {
					member = new UnionDeclaration(getLoc(module, type), id);
					
					member.members = new Dsymbols();
					fill(module, member.members, type.getChildren(), state);
				}
				
				member.setJavaElement(type);
				members.add(wrapWithTemplate(module, member, type, surface));
			}
		} else if (type.isEnum()) {
			IdentifierExp ident = getIdent(type);
			
			final EnumDeclaration member;
			
			// For anonymous enums we can do it lazily 
			if (LAZY_ENUMS && surface && ident != null) {
				member = new EnumDeclaration(getLoc(module, type), ident, null);
				member.rest = new SemanticRest(new Runnable() {
					public void run() {
						try {
							BaseClasses baseClasses = getBaseClasses(type);
							member.memtype = baseClasses.isEmpty() ? Type.tint32 : baseClasses.get(0).type;
							
							member.members = new Dsymbols();
							for(IJavaElement sub : type.getChildren()) {
								IField field = (IField) sub;
								EnumMember enumMember = new EnumMember(getLoc(module, field), getIdent(field), getExpression(field));
								enumMember.setJavaElement(field);
								member.members.add(enumMember);
							}
						} catch (JavaModelException e) {
							Util.log(e);
						}
					}
				});
			} else {
				BaseClasses baseClasses = getBaseClasses(type);
				member = new EnumDeclaration(getLoc(module, type), ident, baseClasses.isEmpty() ? Type.tint32 : baseClasses.get(0).type);
				
				member.members = new Dsymbols();
				for(IJavaElement sub : type.getChildren()) {
					IField field = (IField) sub;
					EnumMember enumMember = new EnumMember(getLoc(module, field), getIdent(field), getExpression(field));
					enumMember.setJavaElement(field);
					member.members.add(enumMember);
				}
			}
			
			member.setJavaElement(type);
			members.add(wrap(member, type));
		} else if (type.isTemplate()) {
			final TemplateDeclaration member;
			
			if (LAZY_TEMPLATES && surface) {
				member = new TemplateDeclaration(getLoc(module, type), getIdent(type), null, null);
				member.rest = new SemanticRest(new Runnable() {
					public void run() {
						try {
							Dsymbols symbols = new Dsymbols();
							fill(module, symbols, type.getChildren(), state);
							
							member.members = symbols;
							member.parameters = getTemplateParameters(type);
						} catch (JavaModelException e) {
							Util.log(e);
						}
					}
				});
			} else {
				Dsymbols symbols = new Dsymbols();
				fill(module, symbols, type.getChildren(), state);
				
				member = new TemplateDeclaration(getLoc(module, type), getIdent(type), getTemplateParameters(type), symbols);
			}
			
			member.setJavaElement(type);
			members.add(wrap(member, type));
		}
		
		state.surface = surface;
	}

	private void fillAnon(Module module, Dsymbols members, IType type, 
			boolean isUnion, State state) throws JavaModelException {
		Dsymbols symbols = new Dsymbols();
		fill(module, symbols, type.getChildren(), state);
		AnonDeclaration member = new AnonDeclaration(getLoc(module, type), isUnion, symbols);
		member.setJavaElement(type);
		members.add(wrap(member, type));
	}

	private void fillField(Module module, Dsymbols members, final IField field, State state) throws JavaModelException {
		if (field.isVariable()) {
			final VarDeclaration member;
			if (LAZY_VARS && state.surface) {
				member = new VarDeclaration(getLoc(module, field), null, getIdent(field), null);
				member.rest = new SemanticRest(new Runnable() {
					public void run() {
						try {
							member.type = getType(field.getTypeSignature());
							member.init = getInitializer(field);
						} catch (JavaModelException e) {
							Util.log(e);
						}
					}
				});
			} else {
				member = new VarDeclaration(getLoc(module, field), getType(field.getTypeSignature()), getIdent(field), getInitializer(field));
			}
			
			member.setJavaElement(field);
			members.add(wrap(member, field));
		} else if (field.isAlias()) {
			final AliasDeclaration member;
			if (LAZY_ALIASES && state.surface) {
				member = new AliasDeclaration(getLoc(module, field), getIdent(field), (Type) null);
				member.rest = new SemanticRest(new Runnable() {
					public void run() {
						try {
							member.type = getType(field.getTypeSignature());
						} catch (JavaModelException e) {
							Util.log(e);
						}
					}
				});
			} else {
				member = new AliasDeclaration(getLoc(module, field), getIdent(field), getType(field.getTypeSignature()));
			}
			
			member.setJavaElement(field);
			members.add(wrap(member, field));
		} else if (field.isTypedef()) {
			TypedefDeclaration member = new TypedefDeclaration(getLoc(module, field), getIdent(field), getType(field.getTypeSignature()), (Initializer) getInitializer(field));
			member.setJavaElement(field);
			members.add(wrap(member, field));
		} else if (field.isTemplateMixin()) {
			TemplateMixin member = encoder.decodeTemplateMixin(field.getTypeSignature(), field.getElementName());
			member.loc = getLoc(module, field);
			members.add(wrap(member, field));
		}
	}

	private void fillImportDeclaration(Module module, Dsymbols members, IImportDeclaration impDecl) throws JavaModelException {
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
		
//		System.out.println(imp);
		members.add(wrap(imp, flags));
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
	
	private LINK getLink(IInitializer init) {
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

	private Dsymbol wrapWithTemplate(Module module, Dsymbol symbol, final ITemplated templated, boolean surface) throws JavaModelException {
		if (templated.isTemplate()) {
			final TemplateDeclaration temp;
			
			if (LAZY_TEMPLATES && surface) {
				temp = new TemplateDeclaration(getLoc(module, (ISourceReference) templated), getIdent((IJavaElement) templated), null, toDsymbols(symbol));
				temp.wrapper = true;
				
				if (symbol instanceof AggregateDeclaration) {
					((AggregateDeclaration) symbol).templated = true;
				} else if (symbol instanceof FuncDeclaration) {
					((FuncDeclaration) symbol).templated = true;
				}
				
				temp.rest = new SemanticRest(new Runnable() {
					public void run() {
						try {
							temp.parameters = getTemplateParameters(templated);
						} catch (JavaModelException e) {
							Util.log(e);
						}
					}
				});
			} else {
				temp = new TemplateDeclaration(getLoc(module, (ISourceReference) templated), getIdent((IJavaElement) templated), getTemplateParameters(templated), toDsymbols(symbol));
			}
			
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
		TemplateParameter param = InternalSignature.toTemplateParameter(signature, defaultValue);
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

	private IdentifierExp getIdent(IJavaElement element) {
		String name = element.getElementName();
		if (name.length() == 0) {
			return null;
		} else {
			return new IdentifierExp(name.toCharArray());
		}
	}
	
	private BaseClasses getBaseClasses(IType type) throws JavaModelException {
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
	
	private BaseClass getBaseClass(String signature) {
		if (signature == null) {
			return null;
		}
		Type type = InternalSignature.toType(signature);
		return new BaseClass(type, PROT.PROTpublic);
	}
	
	private Type getType(String signature) {
		if (signature == null || signature.length() == 0) {
			return null;
		}
		return InternalSignature.toType(signature);
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
	
	
	
	private Dsymbol wrap(Dsymbol symbol, IMember member) throws JavaModelException {
		return wrap(symbol, member.getFlags());
	}
	
	private Dsymbol wrap(Dsymbol symbol, long flags) throws JavaModelException {
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
	
	private Loc getLoc(Module module, ISourceReference source) {
		// TODO line number
		Loc loc = new Loc(module.moduleName.toCharArray(), 0);
		return loc;
	}

}
