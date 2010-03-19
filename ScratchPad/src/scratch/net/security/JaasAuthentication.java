package scratch.net.security;
import java.io.IOException;

import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import melnorme.miscutil.StringUtil;
import scratch.net.Directory_Search;

/**
 * This JaasAcn application attempts to authenticate a user
 * and reports whether or not the authentication was successful.
 */
public class JaasAuthentication {
	
	public static void main(String[] args) {
		
		// Obtain a LoginContext, needed for authentication. Tell 
		// it to use the LoginModule implementation specified by 
		// the entry named "JaasSample" in the JAAS login 
		// configuration file and to also use the specified 
		// CallbackHandler.
		
		System.setProperty("java.security.auth.login.config", "jaasConfig.conf");
		System.setProperty("java.security.krb5.conf", "D:/devel/tools/kfw-3-2-2-final/krb5.ini");
		System.setProperty("javax.security.auth.useSubjectCredsOnly", "true");
		
//		System.setProperty("java.security.krb5.realm", "EXAMPLE.COM");
//		System.setProperty("java.security.krb5.kdc", "pak.emarket.local");
		
		LoginContext lc = null;
		try {
			lc = new LoginContext("JaasSample", new TextCallbackHandler() {
				@Override
				protected String getName() throws IOException {
					return "hnelson";
				}
				@Override
				protected char[] readPassword() throws IOException {
					return "foobar1".toCharArray();
				}
			});
		} catch (LoginException le) {
			System.err.println("Cannot create LoginContext. " + le.getMessage());
			System.exit(-1);
		} catch (SecurityException se) {
			System.err.println("Cannot create LoginContext. " + se.getMessage());
			System.exit(-1);
		}
		
		
		try {
			// attempt authentication
			lc.login();
		} catch (LoginException le) {
			System.err.println("Authentication failed: ");
			System.err.println("  " + le.getMessage());
			System.exit(-1);
		}
		System.err.println();
		System.out.println("Authentication succeeded!");

		Subject subject = lc.getSubject();
		System.out.println(" public credentials: \n " +
				StringUtil.collToString(subject.getPublicCredentials(), "  \n"));

		System.out.println(" private credentials: \n " +
				StringUtil.collToString(subject.getPrivateCredentials(), "  \n"));

		

		System.out.println(" ------------ Doing LDAP! ");
		
		Subject.doAs(subject, new JndiAction());
		System.out.println(" ------------ Doing LDAP2! ");
		Subject.doAs(subject, new JndiAction());
		System.out.println(" ------------ End LDAP2! ");
		
	}
}

class JndiAction implements java.security.PrivilegedAction<Object> {
	@Override
	public Object run() {
		try {
			Directory_Search.main(null);
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
