package descent.tests.mars;

import descent.core.compiler.Linkage;
import descent.core.dom.ASTNode;
import descent.core.dom.ExternDeclaration;

public class Link_Test extends Parser_Test {
	
	public void test() {
		Object[][] links = {
				{ "", Linkage.DEFAULT },
				{ "D", Linkage.D  },
				{ "C", Linkage.C  },
				{ "C++", Linkage.CPP  },
				{ "Windows", Linkage.WINDOWS  },
				{ "Pascal", Linkage.PASCAL },
				
		};
		
		for(Object[] linkX : links) {
			String s = " extern(" + linkX[0] + ") { }";
			ExternDeclaration link = (ExternDeclaration) getSingleDeclarationNoProblems(s);
			assertEquals(ASTNode.EXTERN_DECLARATION, link.getNodeType());
			assertEquals(linkX[1], link.getLinkage());
			
			assertPosition(link, 1, s.length() - 1);
		}
	}

}
