package dtool.tests.ref.cc;

import org.eclipse.dltk.core.ModelException;

public interface ICodeCompletionTester {

	void testComputeProposals(int repOffset, int prefixLen, boolean removeObjectIntrinsics,
			String... expectedProposals) throws ModelException;

	void testComputeProposalsWithRepLen(int repOffset, int prefixLen, int repLen,
			boolean removeObjectIntrinsics, String... expectedProposals) throws ModelException;

}