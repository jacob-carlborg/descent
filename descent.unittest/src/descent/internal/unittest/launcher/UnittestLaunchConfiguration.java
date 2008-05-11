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

import java.io.File;
import java.io.FileWriter;
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
import descent.core.IJavaProject;
import descent.core.JavaCore;
import descent.debug.core.AbstractDescentLaunchConfigurationDelegate;
import descent.debug.core.IDescentLaunchConfigurationConstants;
import descent.internal.unittest.DescentUnittestPlugin;
import descent.internal.unittest.flute.FluteApplicationInstance;
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
	
	/**
	 * The file to create next to the fluted executable to use to find the port.
	 * This is a hack because it's impossible to pass command-line arguments to
	 * the program (well, it's not, but this might be easier than finding the
	 * stack frame and trying to read the arguments like that).
	 */
	public static final String PORT_FILENAME = ".fluteport"; //$NON-NLS-1$
	
	@Override
	public void launch(ILaunchConfiguration config, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException
	{
	    System.out.println(launch.getClass());
	    
		if (monitor == null)
			monitor = new NullProgressMonitor();
		boolean shouldKill = false; // Should the process be killed?
		
		try
		{
			monitor.beginTask(JUnitMessages.UnittestLaunchConfiguration_task_name, 100);
			if (monitor.isCanceled())
				return;
			
			//Find the tests
			List<ITestSpecification> tests = findTests(config, 
			        new SubProgressMonitor(monitor, 40)); // 40
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
            
            // Create the file with the port
            String portFilePath = getPortFilePath(config);
            File portFile = createFile(portFilePath);
            writeToFile(Integer.toString(port), portFile);
            monitor.worked(15); // 55
			
			// Launch the applicataion
			super.launch(config, mode, launch, new SubProgressMonitor(monitor, 20)); // 75
			shouldKill = true;
			if(monitor.isCanceled())
			    return;
			
			// Connect to the application
			FluteApplicationInstance app;
			try
	        {
	            synchronized(this)
	            {
	                app = new FluteApplicationInstance(port);
	            }
	            app.init();
	        }
	        catch(IOException e)
	        {
	            throw error(String.format(JUnitMessages.UnittestLaunchConfiguration_could_not_connect, e.getMessage()));
	        }
	        monitor.worked(20); // 95
	        if(monitor.isCanceled())
                return;

			// Transfer the launch config attributes to the launch
			launch.setAttribute(IDescentLaunchConfigurationConstants.ATTR_PROJECT_NAME, getProjectName(config));
			
			// Start the Descent side of things
			IJavaProject project = verifyJavaProject(config);
			DescentUnittestPlugin.getModel().notifyLaunch(launch, app, project, tests);
			shouldKill = false;
			monitor.worked(5); // 100
		}
		finally
		{
		    monitor.done();
		    if(shouldKill)
		    {
		        try
		        {
		            if(launch.canTerminate())
		                launch.terminate();
		        }
		        catch(Exception e) { } // Don't throw from finally
		    }
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
	
	private static File createFile(String path) throws CoreException
	{
	    File file = new File(path);
        if(file.exists())
        {
            if(!file.delete())
            {
                throw error(String.format(JUnitMessages.UnittestLaunchConfiguration_could_not_delete_file, path));
            }
        }
        try
        {
            file.createNewFile();
        }
        catch(IOException e)
        {
            throw error(String.format(JUnitMessages.UnittestLaunchConfiguration_error_crearing_file, e.getMessage()));
        }
        return file;
	}
	
	private static void writeToFile(String str, File file) throws CoreException
	{
	    try
	    {
	        FileWriter writer = new FileWriter(file);
	        writer.append(str);
	        writer.flush();
	        writer.close();
	    }
	    catch(IOException e)
	    {
	        throw error(String.format(JUnitMessages.UnittestLaunchConfiguration_error_writing_file, e.getMessage()));
	    }
	}
	
	private String getPortFilePath(ILaunchConfiguration config) throws CoreException
	{
	    File dirFile = verifyWorkingDirectory(config);
        String dirPath;
        if(null != dirFile)
        {
            dirPath = dirFile.getPath();
        }
        else
        {
            File exe = new File(verifyProgramPath(config).toOSString());
            if(!exe.exists() || !exe.isFile())
                throw error(JUnitMessages.UnittestLaunchConfiguration_program_does_not_exist);
            dirPath = exe.getParent();
        }
        return dirPath + File.separator + PORT_FILENAME;
	}

	@Override
	protected String getPluginID()
	{
		return DescentUnittestPlugin.PLUGIN_ID;
	}
}

