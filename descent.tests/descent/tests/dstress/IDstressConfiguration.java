package descent.tests.dstress;

import java.io.File;

public interface IDstressConfiguration {
	
	/**
	 * This path should point to dstress. Once you update your
	 * SVN copy, never commit changes made to this file.
	 */
	public final static String DSTRESS_PATH = "c:\\d\\dstress";
	
	public final static String DSTRESS_WHERE_PATH = new File(DSTRESS_PATH)/*.getParent()*/.toString();

}
