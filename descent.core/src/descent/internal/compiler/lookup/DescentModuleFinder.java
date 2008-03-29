package descent.internal.compiler.lookup;

import descent.core.Flags;
import descent.core.ICompilationUnit;
import descent.core.IField;
import descent.core.IJavaElement;
import descent.core.IMember;
import descent.core.IMethod;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.internal.compiler.env.IModuleFinder;
import descent.internal.compiler.env.INameEnvironment;
import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.AliasDeclaration;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.Arguments;
import descent.internal.compiler.parser.BaseClass;
import descent.internal.compiler.parser.BaseClasses;
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.CtorDeclaration;
import descent.internal.compiler.parser.DeleteDeclaration;
import descent.internal.compiler.parser.Dsymbol;
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
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.NewDeclaration;
import descent.internal.compiler.parser.PROT;
import descent.internal.compiler.parser.ProtDeclaration;
import descent.internal.compiler.parser.STC;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.StorageClassDeclaration;
import descent.internal.compiler.parser.StructDeclaration;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeFunction;
import descent.internal.compiler.parser.TypedefDeclaration;
import descent.internal.compiler.parser.UnionDeclaration;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.core.InternalSignature;
import descent.internal.core.util.Util;

public class DescentModuleFinder implements IModuleFinder {

	private final INameEnvironment environment;
	private final static ASTNodeEncoder encoder = new ASTNodeEncoder();

	public DescentModuleFinder(INameEnvironment environment) {
		this.environment = environment;
	}

	public Module findModule(char[][] compoundName, SemanticContext context) {
		ICompilationUnit unit = environment.findCompilationUnit(compoundName);
		if (unit != null){
			Module module = new Module(unit.getElementName(), new IdentifierExp(unit.getModuleName().toCharArray()));
			module.setJavaElement(unit);
			module.moduleName = unit.getFullyQualifiedName();
			module.members = new Dsymbols();
			
			try {
				fill(module.members, unit.getChildren());
			} catch (JavaModelException e) {
				e.printStackTrace();
				Util.log(e);
				return null;
			}
			
			return module;
		}
		return null;
	}
	
	private void fill(Dsymbols members, IJavaElement[] elements) throws JavaModelException {
		for(IJavaElement elem : elements) {
			switch(elem.getElementType()) {
			case IJavaElement.FIELD: {
				IField field = (IField) elem;
				if (field.isVariable()) {
					VarDeclaration member = new VarDeclaration(Loc.ZERO, getType(field.getTypeSignature()), getIdent(field), getInitializer(field));
					member.setJavaElement(field);
					members.add(wrap(member, field));
				} else if (field.isAlias()) {
					AliasDeclaration member = new AliasDeclaration(Loc.ZERO, getIdent(field), getType(field.getTypeSignature()));
					member.setJavaElement(field);
					members.add(wrap(member, field));
				} else if (field.isTypedef()) {
					TypedefDeclaration member = new TypedefDeclaration(Loc.ZERO, getIdent(field), getType(field.getTypeSignature()), (Initializer) getInitializer(field));
					member.setJavaElement(field);
					members.add(wrap(member, field));
				}
				break;
			}
			case IJavaElement.TYPE: {
				IType type = (IType) elem;
				if (type.isClass()) {
					ClassDeclaration member = new ClassDeclaration(Loc.ZERO, getIdent(type), getBaseClasses(type));
					member.setJavaElement(type);
					member.members = new Dsymbols();
					fill(member.members, type.getChildren());
					
					members.add(wrap(member, type));
				} else if (type.isInterface()) {
					InterfaceDeclaration member = new InterfaceDeclaration(Loc.ZERO, getIdent(type), getBaseClasses(type));
					member.setJavaElement(type);
					member.members = new Dsymbols();
					fill(member.members, type.getChildren());
					
					members.add(wrap(member, type));
				} else if (type.isStruct()) {
					StructDeclaration member = new StructDeclaration(Loc.ZERO, getIdent(type));
					member.setJavaElement(type);
					member.members = new Dsymbols();
					fill(member.members, type.getChildren());
					
					members.add(wrap(member, type));
				} else if (type.isUnion()) {
					UnionDeclaration member = new UnionDeclaration(Loc.ZERO, getIdent(type));
					member.setJavaElement(type);
					member.members = new Dsymbols();
					fill(member.members, type.getChildren());
					
					members.add(wrap(member, type));
				} else if (type.isEnum()) {
					BaseClasses baseClasses = getBaseClasses(type);
					EnumDeclaration member = new EnumDeclaration(Loc.ZERO, getIdent(type), baseClasses.isEmpty() ? Type.tint32 : baseClasses.get(0).type);
					member.setJavaElement(type);
					member.members = new Dsymbols();
					for(IJavaElement sub : type.getChildren()) {
						IField field = (IField) sub;
						EnumMember enumMember = new EnumMember(Loc.ZERO, getIdent(field), getExpression(field));
						enumMember.setJavaElement(field);
						
						member.members.add(enumMember);
					}
					
					members.add(wrap(member, type));
				}
				break;
			}
			case IJavaElement.METHOD: {
				IMethod method = (IMethod) elem;
				if (method.isConstructor()) {
					// TODO varargs
					CtorDeclaration member = new CtorDeclaration(Loc.ZERO, getArguments(method), 0);
					members.add(wrap(member, method));
				} else if (method.isDestructor()) {
					DtorDeclaration member = new DtorDeclaration(Loc.ZERO);
					members.add(wrap(member, method));
				} else if (method.isNew()) {
					// TODO varargs
					NewDeclaration member = new NewDeclaration(Loc.ZERO, getArguments(method), 0);
					members.add(wrap(member, method));
				} else if (method.isDelete()) {
					DeleteDeclaration member = new DeleteDeclaration(Loc.ZERO, getArguments(method));
					members.add(wrap(member, method));	
				} else { 
					FuncDeclaration member = new FuncDeclaration(Loc.ZERO, getIdent(method), getStorageClass(method), getType(method));
					members.add(wrap(member, method));
				}
				break;
			}
			}
		}
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
		return new IdentifierExp(element.getElementName().toCharArray());
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
		return InternalSignature.toType(signature);
	}
	
	private Type getType(IMethod method) throws JavaModelException {
		Type returnType = getType(method.getReturnType());
		Arguments args = getArguments(method);
		
		// TODO varargs and linkage
		return new TypeFunction(args, returnType, 0, LINK.LINKd);
	}
	
	private Arguments getArguments(IMethod method) {
		Arguments args = new Arguments();
		String[] parameterTypesSignatures = method.getParameterTypes();
		for(String parameterTypeSignature : parameterTypesSignatures) {
			args.add(getArgument(parameterTypeSignature));
		}
		return args;
	}
	
	private Argument getArgument(String signature) {
		int stc = STC.STCin;
		if (signature.charAt(0) == 'J') {
			stc = STC.STCout;
			signature = signature.substring(0);
		} else if (signature.charAt(0) == 'K') {
			stc = STC.STCref;
			signature = signature.substring(0);
		} else if (signature.charAt(0) == 'L') {
			stc = STC.STClazy;
			signature = signature.substring(0);
		}
		
		// TODO name and default arg
		return new Argument(stc, getType(signature), null, null);
	}
	
	private Dsymbol wrap(Dsymbol symbol, IMember member) throws JavaModelException {
		long flags = member.getFlags();
		int stc = getStorageClass(member);
		if (stc != 0) {
			StorageClassDeclaration sc = new StorageClassDeclaration(stc, toDsymbols(symbol), null, false, false);
			sc.flags = flags;
			symbol = sc;
		}
		PROT prot = getProtection(member);
		if (prot != PROT.PROTpublic) {
			ProtDeclaration pd = new ProtDeclaration(prot, toDsymbols(symbol), null, false, false);
			pd.flags = flags;
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
		long flags = member.getFlags();
		int storage_class = 0;
		
		if ((flags & Flags.AccAbstract) != 0) storage_class |= STC.STCabstract;
		if ((flags & Flags.AccAuto) != 0) storage_class |= STC.STCauto;
		// TODO STC.STCcomdat
		if ((flags & Flags.AccConst) != 0) storage_class |= STC.STCconst;
		// TODO STC.STCctorinit
		if ((flags & Flags.AccDeprecated) != 0) storage_class |= STC.STCdeprecated;
		if ((flags & Flags.AccExtern) != 0) storage_class |= STC.STCextern;
		// TODO STC.STCfield
		if ((flags & Flags.AccFinal) != 0) storage_class |= STC.STCfinal;
		// TODO STC.STCforeach
		if ((flags & Flags.AccIn) != 0) storage_class |= STC.STCin;
		if ((flags & Flags.AccInvariant) != 0) storage_class |= STC.STCinvariant;
		if ((flags & Flags.AccLazy) != 0) storage_class |= STC.STClazy;
		if ((flags & Flags.AccOut) != 0) storage_class |= STC.STCout;
		if ((flags & Flags.AccOverride) != 0) storage_class |= STC.STCoverride;
		// TODO STC.STCparameter
		if ((flags & Flags.AccRef) != 0) storage_class |= STC.STCref;
		if ((flags & Flags.AccScope) != 0) storage_class |= STC.STCscope;
		if ((flags & Flags.AccStatic) != 0) storage_class |= STC.STCstatic;
		if ((flags & Flags.AccSynchronized) != 0) storage_class |= STC.STCsynchronized;
		// TODO STC.STCtemplateparameter
		// TODO STC.STCundefined
		// TODO STC.STCvariadic
		
		return storage_class;
	}
	
	private PROT getProtection(IMember member) throws JavaModelException {
		long flags = member.getFlags();
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
			return PROT.PROTpublic;
		}
	}

}
