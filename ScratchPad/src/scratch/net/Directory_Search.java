package scratch.net;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class Directory_Search {
	
	public static void main(String[] args) throws NamingException {
		
		String hostname = "forge";
		int port = 20389;
		String principal = "hnelson@EXAMPLE.COM";
		String password = "foobar1";
		
		InitialDirContext dirContext;
		
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		String contextBase = "dc=example,dc=com";
		env.put(Context.PROVIDER_URL, "ldap://" + hostname + ":" + port + "/" + contextBase);
		env.put(Context.SECURITY_AUTHENTICATION, "GSSAPI");
		env.put(Context.SECURITY_PRINCIPAL, principal); // Change this to user query?
		
		env.put(Context.SECURITY_CREDENTIALS, password);
		env.put("javax.security.sasl.policy.noactive", "true");
//		System.setProperty("java.naming.security.sasl.authorizationId", principal);
//		env.put("java.naming.security.sasl.authorizationId", principal);
		
		
		dirContext = new InitialDirContext(env);
		
		searchContext(dirContext);
	}
	
	public static void searchContext(DirContext dirContext) {
		final String usersDir = "ou=users" /*+ contextBase*/;
		try {
			
//			String base = user;
			String base = usersDir;
			String filter = "(&(objectClass=person))";
			
			SearchControls searchCtrls = new SearchControls();
			searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			searchCtrls.setReturningAttributes(new String[] { "description", "mail" });
			
			
			NamingEnumeration<SearchResult> resultEnum = dirContext.search(base, filter, searchCtrls);
			while (resultEnum.hasMore()) {
				SearchResult result = resultEnum.next();
				
				// print DN of entry
				System.out.println(" ----- " + result.getNameInNamespace());
				
				// print attributes returned by search
				Attributes attrs = result.getAttributes();
				NamingEnumeration<? extends Attribute> e = attrs.getAll();
				while (e.hasMore()) {
					Attribute attr = (Attribute) e.next();
					System.out.println(attr);
				}
				System.out.println();
			}
			
			dirContext.close();
		} catch (NamingException e) {
			System.out.println(e.getMessage());
		}
	}
}