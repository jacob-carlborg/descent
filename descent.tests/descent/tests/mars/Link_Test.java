package descent.tests.mars;

import descent.core.dom.ASTNode;
import descent.core.dom.ExternDeclaration;

public class Link_Test extends Parser_Test {
	
	public void test() {
		Object[][] links = {
				{ "", ExternDeclaration.Linkage.DEFAULT },
				{ "D", ExternDeclaration.Linkage.D  },
				{ "C", ExternDeclaration.Linkage.C  },
				{ "C++", ExternDeclaration.Linkage.CPP  },
				{ "Windows", ExternDeclaration.Linkage.WINDOWS  },
				{ "Pascal", ExternDeclaration.Linkage.PASCAL },
				
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
