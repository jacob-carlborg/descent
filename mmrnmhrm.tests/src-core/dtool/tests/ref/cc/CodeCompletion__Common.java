package dtool.tests.ref.cc;

import static melnorme.miscutil.Assert.assertFail;
import static melnorme.miscutil.Assert.assertNotNull;
import static melnorme.miscutil.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;

import melnorme.miscutil.ArrayUtil;
import melnorme.miscutil.StringUtil;

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

public class CodeCompletion__Common implements ICodeCompletionTester {

	protected static IFile file;
	protected static ISourceModule srcModule;
	protected static ICodeCompletionTester ccTester;
	
	protected static void setupWithFile(IScriptProject deeProject, String path) throws PartInitException, CoreException {
		IProject project = deeProject.getProject();
		file = project.getFile(path);
		srcModule = DLTKCore.createSourceModuleFrom(file);
		ccTester = new CodeCompletion__Common();
	}
	
	public void testComputeProposals(int repOffset,
			int prefixLen, boolean removeObjectIntrinsics, String... expectedProposals) throws ModelException {
		CodeCompletion__Common.testComputeProposalsWithRepLen(repOffset,
				prefixLen, removeObjectIntrinsics, expectedProposals);
	}
	
	public void testComputeProposalsWithRepLen(int repOffset, int prefixLen, 
			int repLen, boolean removeObjectIntrinsics, String... expectedProposals) throws ModelException {
		CodeCompletion__Common.testComputeProposalsWithRepLen(
				repOffset, prefixLen, removeObjectIntrinsics, expectedProposals);
	}

	
	private static void testComputeProposalsWithRepLen(int repOffset, int prefixLen, boolean removeObjectIntrinsics, 
			String... expectedProposals) throws ModelException {
		
		final ArrayList<DefUnit> results;
		results = new ArrayList<DefUnit>();
		//LinkedList<DefUnit> list;
		
		IDefUnitMatchAccepter defUnitAccepter = new IDefUnitMatchAccepter() {
			public void accept(DefUnit defUnit, PrefixSearchOptions searchOptions) {
				results.add(defUnit);
			}

			public Iterator<DefUnit> getResultsIterator() {
				return results.iterator();
			}
			
		};
		
		PrefixDefUnitSearch.doCompletionSearch(repOffset, 
				srcModule, srcModule.getSource(), new CompletionSession(), defUnitAccepter);

		assertNotNull(results, "Code Completion Unavailable");

		// TODO can be improved for better accuracy
		ArrayList<DefUnit> newResults = results;
		if(removeObjectIntrinsics) {
			newResults = removeObjectIntrinsics(results, expectedProposals); 
		}
		checkProposals(prefixLen, newResults, expectedProposals);
	}

	
	public static String[] OBJECT_INTRINSIC_DEFUNITS = new String[] {
		"bit", "size_t", "ptrdiff_t", "hash_t", "string", "wstring", "dstring",
		"printf(char*, ...)", "trace_term()", "Object", "Interface", "ClassInfo",
		"OffsetTypeInfo", "TypeInfo",
		"TypeInfo_Typedef",	"TypeInfo_Enum", "TypeInfo_Pointer", "TypeInfo_Array",
		"TypeInfo_StaticArray","TypeInfo_AssociativeArray", "TypeInfo_Function", "TypeInfo_Delegate", 
		"TypeInfo_Class", "TypeInfo_Interface", "TypeInfo_Struct", "TypeInfo_Tuple", "TypeInfo_Const",
		"TypeInfo_Invariant",
		"MemberInfo", "MemberInfo_field", "MemberInfo_function", "Exception", "Error" 
	};
	
	public static ArrayList<DefUnit> removeObjectIntrinsics(ArrayList<DefUnit> results, String[] expectedProposals) {
		ArrayList<DefUnit> newResults = new ArrayList<DefUnit>();
		
		for (int i = 0; i < results.size(); i++) {
			String defName = results.get(i).toStringAsElement();
			
			if(ArrayUtil.contains(expectedProposals, defName) ||
					!ArrayUtil.contains(CodeCompletion__Common.OBJECT_INTRINSIC_DEFUNITS, defName)) {
				newResults.add(results.get(i));
			}
		}
		return newResults;
	}

	protected static void checkProposals(int prefixLen,
			ArrayList<DefUnit> results, String... expectedProposals) {
		int expectedLength = expectedProposals.length;
		boolean[] proposalsMatched = new boolean[expectedLength];

		assertTrue(results.size() == expectedLength, 
				"Size mismatch, expected: " + expectedLength
				+" got: "+ results.size() + "{ \n" +
				StringUtil.collToString(expectedProposals, "\n") +
				"\n----- Results: ----\n" +
				StringUtil.collToString(results, "\n") +
				" }");
		
		for (int i = 0; i < results.size(); i++) {
			String defName = results.get(i).toStringAsElement();

			String repStr;
			// Find this proposal in the expecteds
			int j = 0;
			for (; true; j++) {

				repStr = expectedProposals[j];
				// small repStr fix. Best solution is TODO: refactor testProposals
//				if(repStr.indexOf('(') != -1)
//					repStr = repStr.substring(0, repStr.indexOf('('));
				if(defName.substring(prefixLen).equals(repStr))
					break;

				// if end of cicle
				if(j == expectedLength-1)
					assertFail("Got Unmatched proposal:"+defName);
				
			}
			
			//checkProposal(results.get(i), repOffset, repStr, repLen);
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