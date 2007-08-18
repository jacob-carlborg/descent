package descent.internal.compiler.parser;

import org.eclipse.core.runtime.Assert;

import descent.core.IProblemRequestor;
import descent.core.compiler.IProblem;
import descent.core.dom.AST;

public class SemanticContext {
	
	public boolean BREAKABI = true;
	public boolean _DH = true;
	private IProblemRequestor problemRequestor;
	public StringTable typeStringTable;
	public Global global = new Global();
	
	public ClassDeclaration object; // ClassDeclaration::object
	public ClassDeclaration classinfo; // ClassDeclaration::classinfo
	public ClassDeclaration typeinfo;
	public ClassDeclaration typeinfoclass;
	public ClassDeclaration typeinfointerface;
	public ClassDeclaration typeinfostruct;
	public ClassDeclaration typeinfotypedef;
	public ClassDeclaration typeinfopointer;
	public ClassDeclaration typeinfoarray;
	public ClassDeclaration typeinfostaticarray;
	public ClassDeclaration typeinfoassociativearray;
	public ClassDeclaration typeinfoenum;
	public ClassDeclaration typeinfofunction;
	public ClassDeclaration typeinfodelegate;
	public ClassDeclaration typeinfotypelist;
	
	public ClassDeclaration moduleinfo;
	
	public DsymbolTable st;
	public AST ast;
	
	public SemanticContext(IProblemRequestor problemRequestor, AST ast) {
		this.problemRequestor = problemRequestor;
		this.ast = ast;
		this.typeStringTable = new StringTable();
		
		Loc loc = Loc.ZERO;
		this.object = new ClassDeclaration(loc, Id.Object);
		this.typeinfo = new ClassDeclaration(loc, Id.TypeInfo);
		this.typeinfoclass = new ClassDeclaration(loc, Id.TypeInfo_Class);
		this.typeinfointerface = new ClassDeclaration(loc, Id.TypeInfo_Interface);
		this.typeinfostruct = new ClassDeclaration(loc, Id.TypeInfo_Struct);
		this.typeinfotypedef = new ClassDeclaration(loc, Id.TypeInfo_Typedef);
		this.typeinfopointer = new ClassDeclaration(loc, Id.TypeInfo_Pointer);
		this.typeinfoarray = new ClassDeclaration(loc, Id.TypeInfo_Array);
		this.typeinfostaticarray = new ClassDeclaration(loc, Id.TypeInfo_StaticArray);
		this.typeinfoassociativearray = new ClassDeclaration(loc, Id.TypeInfo_AssociativeArray);
		this.typeinfoenum = new ClassDeclaration(loc, Id.TypeInfo_Enum);
		this.typeinfofunction = new ClassDeclaration(loc, Id.TypeInfo_Function);
		this.typeinfodelegate = new ClassDeclaration(loc, Id.TypeInfo_Delegate);
		this.typeinfotypelist = new ClassDeclaration(loc, Id.TypeInfo_Tuple);
	}
	
	public void acceptProblem(IProblem problem) {
		problemRequestor.acceptProblem(problem);
	}
	
	public void multiplyDefined(Dsymbol s1, Dsymbol s2) {
		acceptProblem(Problem.newSemanticMemberError("Duplicated symbol " + s2.ident, IProblem.DuplicatedSymbol, 0, s2.ident.start, s2.ident.length));
		acceptProblem(Problem.newSemanticMemberError("Duplicated symbol " + s1.ident, IProblem.DuplicatedSymbol, 0, s1.ident.start, s1.ident.length));		
	}
	
	private int generatedIds;	
	public IdentifierExp generateId(String prefix) {
		String name = prefix + ++generatedIds;
		Identifier id = new Identifier(name.toCharArray(), TOK.TOKidentifier);
		return new IdentifierExp(Loc.ZERO, id);
	}
	
	public FuncDeclaration genCfunc(Type treturn, char[] name) {
		return genCfunc(treturn, new Identifier(name, TOK.TOKidentifier));
	}

	public FuncDeclaration genCfunc(Type treturn, Identifier id) {
		FuncDeclaration fd;
		TypeFunction tf;
		Dsymbol s;

		// See if already in table
		if (st == null)
			st = new DsymbolTable();
		s = st.lookup(id.string);
		if (s != null) {
			fd = s.isFuncDeclaration();
			Assert.isNotNull(fd);
			Assert.isTrue(fd.type.next.equals(treturn));
		} else {
			tf = new TypeFunction(null, treturn, 0, LINK.LINKc);
			fd = new FuncDeclaration(Loc.ZERO, new IdentifierExp(Loc.ZERO, id), STC.STCstatic, tf);
			fd.protection = PROT.PROTpublic;
			fd.linkage = LINK.LINKc;

			st.insert(fd);
		}
		return fd;
	}

}
