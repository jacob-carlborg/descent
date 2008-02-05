/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package descent.internal.unittest.model;

import descent.unittest.ITestResult;
import descent.unittest.ITestSpecification;


public class TestCaseElement extends TestElement
{
	private final ITestSpecification fTest;
	private boolean fIgnored;
	private ITestResult fResult;
	
	
	public TestCaseElement(TestSuiteElement parent, ITestSpecification test)
	{
		super(parent, test.getId(), test.getName());
		fTest = test;
	}
	
	public ITestResult getResult()
	{
		return fResult;
	}
	
	public void setResult(ITestResult result)
	{
		fResult = result;
		setStatus(Status.statusOf(result));
	}
	
	public ITestSpecification getTestSpecification()
	{
		return fTest;
	}
	
	public String getTestMethodName()
	{
		// TODO
		return "getTestMethodName (" + getName() + ")";
	}

	public void setIgnored(boolean ignored)
	{
		fIgnored= ignored;
	}
	
	public boolean isIgnored()
	{
		return fIgnored;
	}
}
