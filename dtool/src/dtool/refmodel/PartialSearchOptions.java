package dtool.refmodel;

public class PartialSearchOptions {

	
	public IScopeNode searchScope;
	public String searchPrefix;
	public int prefixLen;
	public int rplOffset;
	public int rplLen;
	
	public PartialSearchOptions() {
		searchPrefix = "";
		rplLen = 0;
		prefixLen = 0;
	}

}
