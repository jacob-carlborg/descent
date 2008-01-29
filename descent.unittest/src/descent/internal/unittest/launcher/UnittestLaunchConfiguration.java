/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Saff (saff@mit.edu) - bug 102632: [JUnit] Support for JUnit 4.
 *******************************************************************************/
package descent.internal.unittest.launcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import descent.core.ICompilationUnit;
import descent.core.IJavaProject;
import descent.internal.debug.core.DescentLaunchConfigurationDelegate;
import descent.internal.unittest.DescentUnittestPlugin;

/**
 * Launch configuration delegate for a plain JUnit test.
 */
public class UnittestLaunchConfiguration extends 
	DescentLaunchConfigurationDelegate 
{
	/**
	 * The file to load as the list of tests to prioritize (maybe I should
	 * rethink how this is done -- no need for a whole file if we're using IPC).
	 */
	// RETHINK
	public static final String FAILURES_FILENAME_ATTR= DescentUnittestPlugin.PLUGIN_ID+".FAILURENAMES"; //$NON-NLS-1$
	
	@Override
	public void launch(ILaunchConfiguration config, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException
	{
		if (monitor == null)
			monitor = new NullProgressMonitor();
		
		try
		{
			monitor.beginTask("Launching unit test application", 100);
			if (monitor.isCanceled())
				return;
			
			//super.launch(config, mode, launch, new SubProgressMonitor(monitor, 30));
			monitor.worked(30);
			
			List<TestSpecification> tests = findTests(config, new SubProgressMonitor(monitor, 70));
			for(TestSpecification test : tests)
				System.out.println(test.getId());
		}
		finally
		{
			monitor.done();
		}
	}
	
	List<TestSpecification> findTests(ILaunchConfiguration config, IProgressMonitor monitor) 
		throws CoreException
	{
		IJavaProject project = getJavaProject(config);
		Object[] elements = new Object[] { project };
		final List<TestSpecification> result = 
			new ArrayList<TestSpecification>(DUnittestFinder.LIST_PREALLOC);
		DUnittestFinder.findTestsInContainer(elements, result, monitor);
		return result;
	}
	
	@Override
	protected String getPluginID()
	{
		return DescentUnittestPlugin.PLUGIN_ID;
	}
}
