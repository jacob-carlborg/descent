package dtool.tests.ref.cc;

import static melnorme.miscutil.Assert.assertFail;
import static melnorme.miscutil.Assert.assertNotNull;
import static melnorme.miscutil.Assert.assertTrue;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuffer;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.ui.PartInitException;

import dtool.ast.definitions.DefUnit;
import dtool.refmodel.PrefixDefUnitSearch;
import dtool.refmodel.PrefixSearchOptions;
import dtool.refmodel.PrefixDefUnitSearch.CompletionSession;
import dtool.refmodel.PrefixDefUnitSearch.IDefUnitMatchAccepter;

public class CodeCompletion__Common {

	protected static IFile file;
	protected static ISourceModule srcModule;
	protected static CodeCompletionTester ccTester;
	
	protected static void setupWithFile(IScriptProject deeProject, String path) throws PartInitException, CoreException {
		IProject project = deeProject.getProject();
		file = project.getFile(path);
		srcModule = DLTKCore.createSourceModuleFrom(file);
		ccTester = new CodeCompletionTester();
	}

	public static class CodeCompletionTester {
		protected void testComputeProposals(int repOffset,
				int prefixLen, String... expectedProposals) throws ModelException {
			CodeCompletion__Common.testComputeProposals(
					repOffset, prefixLen, expectedProposals);
		}
		
		protected void testComputeProposalsWithRepLen(int repOffset, int prefixLen, 
				int repLen, String... expectedProposals) throws ModelException {
			CodeCompletion__Common.testComputeProposalsWithRepLen(
					repOffset, prefixLen, repLen, expectedProposals);
		}

	}

	private static void testComputeProposals(int repOffset,
			int prefixLen, String... expectedProposals) throws ModelException {
		testComputeProposalsWithRepLen(repOffset, prefixLen, 0, expectedProposals);
	}
	
	private static void testComputeProposalsWithRepLen(int repOffset, int prefixLen, 
			int repLen, String... expectedProposals) throws ModelException {
		
		final ArrayList<DefUnit> results;
		results = new ArrayList<DefUnit>();
		
		IDefUnitMatchAccepter defUnitAccepter = new IDefUnitMatchAccepter() {
			public void accept(DefUnit defUnit, PrefixSearchOptions searchOptions) {
				results.add(defUnit);
			};
			
		};
		
		PrefixDefUnitSearch.doCompletionSearch(repOffset, 
				srcModule, srcModule.getSource(), new CompletionSession(), defUnitAccepter);
		
		assertNotNull(results, "Code Completion Unavailable");
		checkProposals(repOffset, repLen, prefixLen, results, expectedProposals);
	}

	protected static void checkProposal(DefUnit defUnit,
			final int repOffset, final String repStr, final int repLen) {
		//defUnit.apply(new TestCompletion_Document(repStr, repLen, repOffset));
	}
	
	protected static void checkProposals(int repOffset, int repLen, int prefixLen,
			ArrayList<DefUnit> results, String... expectedProposals) {
		boolean[] proposalsMatched = new boolean[expectedProposals.length];

		assertTrue(results.size() == expectedProposals.length, 
				"Size mismatch, expected: "+expectedProposals.length
				+" got: "+ results.size());
		
		for (int i = 0; i < results.size(); i++) {
			String defName = results.get(i).getName();

			String repStr;
			// Find this proposal in the expecteds
			int j = 0;
			for (; true; j++) {

				repStr = expectedProposals[j];
				if(defName.substring(prefixLen).equals(repStr))
					break;

				if(j == expectedProposals.length-1)
					assertFail("Got Unmatched proposal:"+defName);
				
			}
			
			checkProposal(results.get(i), repOffset, repStr, repLen);
			// Mark that expected as obtained
			proposalsMatched[j] = true;
		}
		
		for (int i = 0; i < proposalsMatched.length; i++) {
			// assert all expecteds were matched
			assertTrue(proposalsMatched[i] == true, "Assertion failed.");
		}
	}
	
	protected int getMarkerEndOffset(String marker) throws ModelException {
		return srcModule.getSource().indexOf(marker) + marker.length();
	}

	protected int getMarkerStartOffset(String marker) throws ModelException {
		return srcModule.getSource().indexOf(marker);
	}
	
	protected IBuffer getDocument() throws ModelException {
		return srcModule.getBuffer();
	}

}