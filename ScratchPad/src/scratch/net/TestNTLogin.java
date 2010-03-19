package scratch.net;

import javax.naming.NamingException;

import melnorme.miscutil.StringUtil;

import com.sun.security.auth.module.NTSystem;

public class TestNTLogin {

    public static void main(String[] args) throws NamingException {
    	
    	NTSystem ntSystem = new NTSystem();
    	System.out.println(ntSystem.getDomain());
    	System.out.println(ntSystem.getDomainSID());
    	System.out.println(StringUtil.collToString(ntSystem.getGroupIDs(), ""));
    	System.out.println(ntSystem.getImpersonationToken());
    	System.out.println(ntSystem.getName());
    	System.out.println(ntSystem.getUserSID());
    	
    }
	
}