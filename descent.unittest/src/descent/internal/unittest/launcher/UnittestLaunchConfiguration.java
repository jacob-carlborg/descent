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

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import descent.core.IJavaElement;
import descent.core.JavaCore;
import descent.debug.core.AbstractDescentLaunchConfigurationDelegate;
import descent.debug.core.IDescentLaunchConfigurationConstants;
import descent.internal.unittest.DescentUnittestPlugin;
import descent.internal.unittest.ui.JUnitMessages;
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
			monitor.beginTask(JUnitMessages.UnittestLaunchConfiguration_task_name, 100);
			if (monitor.isCanceled())
				return;
			
			//Find the tests
			List<ITestSpecification> tests = findTests(config, 
			        new SubProgressMonitor(monitor, 60));
			if(tests.isEmpty())
			    throw error(JUnitMessages.UnittestLaunchConfiguration_no_tests_found);
			if(monitor.isCanceled())
                return;
			
			// Get the port
            String portStr = config.getAttribute(IUnittestLaunchConfigurationAttributes.PORT_ATTR, ""); //$NON-NLS-1$
            int port = getPort(portStr);
            if(port < 1024 || port > 65535)
                throw error(JUnitMessages.UnittestLaunchConfiguration_invalid_port);
            if(monitor.isCanceled())
                return;
            monitor.worked(5);
			
			// Launch the applicataion
			super.launch(config, mode, launch, new SubProgressMonitor(monitor, 30));
			if(monitor.isCanceled())
			    return;
			
			// Transfer the launch config attributes to the launch
			launch.setAttribute(IDescentLaunchConfigurationConstants.ATTR_PROJECT_NAME, getProjectName(config));
			launch.setAttribute(IUnittestLaunchConfigurationAttributes.PORT_ATTR, String.valueOf(port));
			
			// Start the Descent side of things
			DescentUnittestPlugin.getModel().notifyLaunch(launch, tests);
			monitor.worked(5);
		}
		finally
		{
			monitor.done();
		}
	}
	
	private static List<ITestSpecification> findTests(ILaunchConfiguration config, 
			IProgressMonitor monitor) 
		throws CoreException
	{
	    String containerStr = config.getAttribute(
	            IUnittestLaunchConfigurationAttributes.LAUNCH_CONTAINER_ATTR, ""); //$NON-NLS-1$
	    IJavaElement container = !("".equals(containerStr)) ? //$NON-NLS-1$
	            JavaCore.create(containerStr) : null;
	    if(null == container)
	        throw error(JUnitMessages.UnittestLaunchConfiguration_container_not_found);
	    
	    boolean includeSubpackages = config.getAttribute(
	            IUnittestLaunchConfigurationAttributes.INCLUDE_SUBPACKAGES_ATTR,
	            "false").equals("true"); //$NON-NLS-1$ //$NON-NLS-2$
	    
		final List<ITestSpecification> result = new ArrayList<ITestSpecification>(DUnittestFinder.LIST_PREALLOC);
		DUnittestFinder.findTestsInContainer(container, result, monitor, includeSubpackages);
		return result;
	}
	
	private static int getPort(String portStr) throws CoreException
    {
        int port = 0;
        try
        {
            port = Integer.parseInt(portStr);
        }
        catch(NumberFormatException e) { }
        
        if(port < 1024 || port > 65535)
            port = findFreePort();
        
        return port;
    }
    
    private static int findFreePort() throws CoreException
    {
        ServerSocket socket= null;
        try {
            socket= new ServerSocket(0);
            return socket.getLocalPort();
        } catch (IOException e) { 
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
        throw error(JUnitMessages.UnittestLaunchConfiguration_no_open_port);
    }
	
	private static CoreException error(String message)
	{
	    return new CoreException(new Status(IStatus.ERROR, 
	            DescentUnittestPlugin.PLUGIN_ID, message));
	}

	@Override
	protected String getPluginID()
	{
		return DescentUnittestPlugin.PLUGIN_ID;
	}
}

