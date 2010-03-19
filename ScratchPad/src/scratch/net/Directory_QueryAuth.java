package scratch.net;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class Directory_QueryAuth {
	
	public static void main(String[] args) throws NamingException {
		
		String hostname = "localhost";
		int port = 20389;
		
		// Create initial context
		DirContext ctx = new InitialDirContext();
		
		// Read supportedSASLMechanisms from root DSE
		Attributes attrs = ctx.getAttributes(
				"ldap://" + hostname + ":" + port, new String[] { "supportedSASLMechanisms" });
		
		
		NamingEnumeration<? extends Attribute> e = attrs.getAll();
		while (e.hasMore()) {
			Attribute attr = (Attribute) e.next();
			System.out.println(attr);
		}
		
	}
}