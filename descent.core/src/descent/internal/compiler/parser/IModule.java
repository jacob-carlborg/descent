package descent.internal.compiler.parser;

import java.util.List;

public interface IModule extends IPackage {
	
	IModule importedFrom();
	
	void importedFrom(IModule module);
	
	boolean needmoduleinfo();
	
	void needmoduleinfo(boolean value);
	
	int semanticdone();
	
	List<char[]> debugids();
	
	void debugids(List<char[]> debugids);
	
	List<char[]> debugidsNot();
	
	void debugidsNot(List<char[]> debugidsNot);
	
	long debuglevel();
	
	void debuglevel(long debuglevel);
	
	long versionlevel();
	
	void versionlevel(long versionlevel);
	
	List<char[]> versionids();
	
	void versionids(List<char[]> versionids);
	
	List<char[]> versionidsNot();
	
	void versionidsNot(List<char[]> versionidsNot);

}
