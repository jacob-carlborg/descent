package descent.tests.mars;

import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.core.dom.IExternDeclaration;
import descent.internal.core.dom.ExternDeclaration;
import descent.internal.core.dom.ParserFacade;

public class Link_Test extends Parser_Test {
	
	public void test() {
		Object[][] links = {
				{ "", ExternDeclaration.Linkage.D },
				{ "D", ExternDeclaration.Linkage.D  },
				{ "C", ExternDeclaration.Linkage.C  },
				{ "C++", ExternDeclaration.Linkage.CPP  },
				{ "Windows", ExternDeclaration.Linkage.WINDOWS  },
				{ "Pascal", ExternDeclaration.Linkage.PASCAL },
				
		};
		
		for(Object[] linkX : links) {
			String s = " extern(" + linkX[0] + ") { }";
			ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
			assertEquals(0, unit.getProblems().length);
			IElement[] declDefs = unit.getDeclarationDefinitions();
			
			IExternDeclaration link = (IExternDeclaration) declDefs[0];
			assertEquals(IElement.EXTERN_DECLARATION, link.getNodeType0());
			assertEquals(linkX[1], link.getLinkage());
			
			assertPosition(link, 1, s.length() - 1);
		}
	}

}
