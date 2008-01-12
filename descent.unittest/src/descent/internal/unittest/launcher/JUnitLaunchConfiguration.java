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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import descent.internal.debug.core.DescentLaunchConfigurationDelegate;
import descent.internal.unittest.DescentUnittestPlugin;

/**
 * Launch configuration delegate for a plain JUnit test.
 */
public class JUnitLaunchConfiguration extends DescentLaunchConfigurationDelegate  {

	public static final String ID_JUNIT_APPLICATION= "descent.unittest.launchconfig"; //$NON-NLS-1$
	
	/**
	 * The port to connect to.
	 */
	public static final String PORT_ATTR= DescentUnittestPlugin.PLUGIN_ID+".PORT"; //$NON-NLS-1$
	
	/**
	 * The project to be tested, or "" iff running a launch container.
	 */
	public static final String TESTPROJECT_ATTR= DescentUnittestPlugin.PLUGIN_ID+".TESTTYPE"; //$NON-NLS-1$
	
	/**
	 * The signature of the unittest declaration, or "" iff running every test
	 * in the project or a launch container
	 */
	public static final String TESTNAME_ATTR= DescentUnittestPlugin.PLUGIN_ID+".TESTNAME"; //$NON-NLS-1$
	
	/**
	 * The launch container, or "" iff running a project
	 */
	public static final String LAUNCH_CONTAINER_ATTR= DescentUnittestPlugin.PLUGIN_ID+".CONTAINER"; //$NON-NLS-1$
	
	/**
	 * The file to load as the list of tests to prioritize (maybe I should
	 * rethink how this is done -- no need for a whole file if we're using IPC).
	 */
	public static final String FAILURES_FILENAME_ATTR= DescentUnittestPlugin.PLUGIN_ID+".FAILURENAMES"; //$NON-NLS-1$
	
	public void launch(ILaunchConfiguration config, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException
	{
		try
		{
			monitor.beginTask("Launching unit test application", 100);
			monitor.subTask("Finding unit tests");
			findTests(new SubProgressMonitor(monitor, 70));
		}
		finally
		{
			monitor.done();
		}
		
		//super.launch(config, mode, launch, new SubProgressMonitor(monitor, 30));
	}
	
	private void findTests(IProgressMonitor monitor)
	{
		// TODO
	}
	
	@Override
	protected String getPluginID()
	{
		return DescentUnittestPlugin.PLUGIN_ID;
	}
}

