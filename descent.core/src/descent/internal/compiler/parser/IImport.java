package descent.internal.compiler.parser;

public interface IImport extends IDsymbol {
	
	IModule mod();
	
	IPackage pkg();

}
