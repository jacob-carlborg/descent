package descent.tests.mars;

import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.core.dom.ILinkDeclaration;
import descent.internal.core.dom.ParserFacade;

public class Link_Test extends Parser_Test {
	
	public void test() {
		Object[][] links = {
				{ "", ILinkDeclaration.LINKAGE_D  },
				{ "D", ILinkDeclaration.LINKAGE_D  },
				{ "C", ILinkDeclaration.LINKAGE_C  },
				{ "C++", ILinkDeclaration.LINKAGE_CPP  },
				{ "Windows", ILinkDeclaration.LINKAGE_WINDOWS  },
				{ "Pascal", ILinkDeclaration.LINKAGE_PASCAL },
				
		};
		
		for(Object[] linkX : links) {
			String s = " extern(" + linkX[0] + ") { }";
			ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
			IElement[] declDefs = unit.getDeclarationDefinitions();
			
			ILinkDeclaration link = (ILinkDeclaration) declDefs[0];
			assertEquals(IElement.LINK_DECLARATION, link.getElementType());
			assertEquals(linkX[1], link.getLinkage());
			
			assertPosition(link, 1, s.length() - 1);
		}
	}

}
