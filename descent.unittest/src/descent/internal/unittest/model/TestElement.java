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
import descent.unittest.ITestResult.ResultType;

/**
 * Represents a test element in a "tree" of tests, which can either be a single
 * test or a suite of tests (the latter represented by a {@link TestSuiteElement}
 */
public abstract class TestElement
{
	public enum Status
	{
		NOT_RUN,         // The test case hasn't been run yet
		
		ERROR,           // There was an error running a test
		FAILURE,         // There was a failure running a test
		OK,              // The tests (or all the tests in the group) ran fine
		
		RUNNING_ERROR,   // Tests in the group are running, but at least one errored
		RUNNING_FAILURE, // Tetss in the group are running, but at least one failed
		RUNNING;         // Tests in the group are running, and all is well so far

		/* error state predicates */
		
		public boolean isOK()
		{
			return this == OK || this == RUNNING || this == NOT_RUN;
		}
		
		public boolean isFailure()
		{
			return this == FAILURE || this == RUNNING_FAILURE;
		}
		
		public boolean isError()
		{
			return this == ERROR || this == RUNNING_ERROR;
		}
		
		public boolean isErrorOrFailure()
		{
			return isError() || isFailure();
		}
		
		/* progress state predicates */
		
		public boolean isNotRun()
		{
			return this == NOT_RUN;
		}
		
		public boolean isRunning()
		{
			return this == RUNNING || this == RUNNING_FAILURE || this == RUNNING_ERROR;
		}
		
		public boolean isDone()
		{
			return this == OK || this == FAILURE || this == ERROR;
		}

		public static Status combineStatus(Status one, Status two)
		{
			Status progress= combineProgress(one, two);
			Status error= combineError(one, two);
			return combineProgressAndErrorStatus(progress, error);
		}

		private static Status combineProgress(Status one, Status two)
		{
			if (one.isNotRun() && two.isNotRun())
				return NOT_RUN;
			else if (one.isDone() && two.isDone())
				return OK;
			else if (!one.isRunning() && !two.isRunning())
				return OK; // one done, one not-run -> a parent failed and its children are not run
			else
				return RUNNING;
		}
		
		private static Status combineError(Status one, Status two)
		{
			if (one.isError() || two.isError())
				return ERROR;
			else if (one.isFailure() || two.isFailure())
				return FAILURE;
			else
				return OK;
		}
		
		private static Status combineProgressAndErrorStatus
				(Status progress, Status error)
		{
			if (progress.isDone())
			{
				if (error.isError())
					return ERROR;
				if (error.isFailure())
					return FAILURE;
				return OK;
			}
			
			if (progress.isNotRun())
			{
				assert(!error.isErrorOrFailure());
				return NOT_RUN;
			}
			
			assert(progress.isRunning());
			if (error.isError())
				return RUNNING_ERROR;
			if (error.isFailure())
				return RUNNING_FAILURE;
			assert(error.isOK());
			return RUNNING;
		}
		
		public static Status statusOf(ITestResult result)
		{
			ResultType type = result.getResultType();
			
			switch(type)
			{
				case ERROR:
					/* I know I don't have to qualify this, but having
					 * case ERROR: return ERROR; where ERROR refers to two
					 * different symbols is confusing.
					 */
					return Status.ERROR;
				case FAILED:
					return Status.FAILURE;
				case PASSED:
					return Status.OK;
				default:
					throw new IllegalStateException();
			}
		}
	}
	
	private final TestSuiteElement fParent;
	private final String fName;
	private final String fId;
	
	private Status fStatus;
	
	public TestElement(TestSuiteElement parent, String id, String name)
	{
		assert(null != id);
		assert(null != name);
		
		fParent= parent;
		fId = id;
		fName = name;
		fStatus= Status.NOT_RUN;
		if (parent != null)
			parent.addChild(this);
	}
	
	/**
	 * @return the parent suite, or <code>null</code> for the root
	 */
	public TestSuiteElement getParent()
	{
		return fParent;
	}
	
	public String getId()
	{
		return fId;
	}
	
	public String getName()
	{
		return fName;
	}
	
	public void setStatus(Status status)
	{
		//JTODO: notify about change?
		//JTODO: multiple errors/failures per test https://bugs.eclipse.org/bugs/show_bug.cgi?id=125296
		fStatus= status;
		TestSuiteElement parent = getParent();
		if (parent != null)
			parent.childChangedStatus(this, status);
	}
	
	public Status getStatus()
	{
		return fStatus;
	}
	
	public TestRoot getRoot()
	{
		return fParent !=  null ? fParent.getRoot() : null;
	}
	
	@Override
	public String toString()
	{
		return getName() + ": " + getStatus(); //$NON-NLS-1$
	}
}