package descent.internal.compiler.parser;

import java.util.List;

public interface IAggregateDeclaration extends IScopeDsymbol {
	
	IInvariantDeclaration inv();
	
	void inv(IInvariantDeclaration inv);
	
	int sizeok();
	
	void sizeok(int sizeok);
	
	Type type();
	
	Type handle();
	
	List<IVarDeclaration> fields();
	
	void accessCheck(Scope sc, IDsymbol smember, SemanticContext context, INode reference);
	
	int structsize();
	
	void structsize(int structsize);

	boolean isFriendOf(IAggregateDeclaration cd);
	
	boolean hasPrivateAccess(IDsymbol smember);
	
	int alignsize();
	
	void alignsize(int alignsize);

	void alignmember(int salign, int size, int[] poffset);
	
	int hasUnions();
	
	void hasUnions(int hasUnions);
	
	void addField(Scope sc, IVarDeclaration v, SemanticContext context);
	
	Symbol toInitializer();
	
	Scope scope();
	
	int storage_class();
	
	INewDeclaration aggNew();
	
	IDeleteDeclaration aggDelete();
	
	int size(SemanticContext context);
	
	int structalign();

}
