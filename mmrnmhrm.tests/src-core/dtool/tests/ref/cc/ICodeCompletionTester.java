package dtool.tests.ref.cc;

import org.eclipse.dltk.core.ModelException;

public interface ICodeCompletionTester {

	void testComputeProposals(int repOffset, int prefixLen,
			String... expectedProposals) throws ModelException;

	void testComputeProposalsWithRepLen(int repOffset, int prefixLen,
			int repLen, String... expectedProposals) throws ModelException;

}