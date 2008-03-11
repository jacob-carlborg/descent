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
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import descent.core.IJavaProject;
import descent.debug.core.AbstractDescentLaunchConfigurationDelegate;
import descent.debug.core.IDescentLaunchConfigurationConstants;
import descent.internal.unittest.DescentUnittestPlugin;
import descent.unittest.ITestSpecification;

/**
 * Launch configuration delegate for a set of D unit tests
 */
public class UnittestLaunchConfiguration extends 
	AbstractDescentLaunchConfigurationDelegate 
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
			
			//Find the tests
			IJavaProject project = verifyJavaProject(config);
			List<ITestSpecification> tests = findTests(project,
					new SubProgressMonitor(monitor, 15));
			
			// Create the executale target
			UnittestExecutableTarget target = new UnittestExecutableTarget();
			target.setProject(project);
			/* for(ITestSpecification test : tests)
				target.addModule(test.getDeclaration().getCompilationUnit().
						getFullyQualifiedName()); */
            target.addModule("sss.main");
			
			// Build and launch the applicataion
			boolean launched = launchExecutableTarget(config, target, mode, launch, 
					new SubProgressMonitor(monitor, 80));
			if(!launched)
				return;
			
			// Transfer the launch config attributes to the launch
			launch.setAttribute(IDescentLaunchConfigurationConstants.ATTR_PROJECT_NAME, getProjectName(config));
			launch.setAttribute(IUnittestLaunchConfigurationAttributes.PORT_ATTR, "30587");
			
			// Start the Descent side of things
			DescentUnittestPlugin.getModel().notifyLaunch(launch, tests);
			monitor.worked(5);
		}
		finally
		{
			monitor.done();
		}
	}
	
	private static List<ITestSpecification> findTests(IJavaProject project, 
			IProgressMonitor monitor) 
		throws CoreException
	{
		Object[] elements = new Object[] { project };
		final List<ITestSpecification> result = 
			new ArrayList<ITestSpecification>(DUnittestFinder.LIST_PREALLOC);
		DUnittestFinder.findTestsInContainer(elements, result, monitor);
		return result;
	}

	@Override
	protected String getPluginID()
	{
		return DescentUnittestPlugin.PLUGIN_ID;
	}
}

