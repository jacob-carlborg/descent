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

import org.eclipse.jface.util.Assert;

import descent.unittest.ITestRunListener;


public abstract class TestElement {
	public final static class Status {
		public static final Status RUNNING_ERROR= new Status("RUNNING_ERROR", 5); //$NON-NLS-1$
		public static final Status RUNNING_FAILURE= new Status("RUNNING_FAILURE", 6); //$NON-NLS-1$
		public static final Status RUNNING= new Status("RUNNING", 3); //$NON-NLS-1$
		
		public static final Status ERROR=   new Status("ERROR",   /*1*/ITestRunListener.STATUS_ERROR); //$NON-NLS-1$
		public static final Status FAILURE= new Status("FAILURE", /*2*/ITestRunListener.STATUS_FAILURE); //$NON-NLS-1$
		public static final Status OK=      new Status("OK",      /*0*/ITestRunListener.STATUS_OK); //$NON-NLS-1$
		public static final Status NOT_RUN= new Status("NOT_RUN", 4); //$NON-NLS-1$
		
		private static final Status[] OLD_CODE= { OK, ERROR, FAILURE};
		
		private final String fName;
		private final int fOldCode;
		
		private Status(String name, int oldCode) {
			fName= name;
			fOldCode= oldCode;
		}
		
		public int getOldCode() {
			return fOldCode;
		}
		
		public String toString() {
			return fName;
		}

		/* error state predicates */
		
		public boolean isOK() {
			return this == OK || this == RUNNING || this == NOT_RUN;
		}
		
		public boolean isFailure() {
			return this == FAILURE || this == RUNNING_FAILURE;
		}
		
		public boolean isError() {
			return this == ERROR || this == RUNNING_ERROR;
		}
		
		public boolean isErrorOrFailure() {
			return isError() || isFailure();
		}
		
		/* progress state predicates */
		
		public boolean isNotRun() {
			return this == NOT_RUN;
		}
		
		public boolean isRunning() {
			return this == RUNNING || this == RUNNING_FAILURE || this == RUNNING_ERROR;
		}
		
		public boolean isDone() {
			return this == OK || this == FAILURE || this == ERROR;
		}

		public static Status combineStatus(Status one, Status two) {
			Status progress= combineProgress(one, two);
			Status error= combineError(one, two);
			return combineProgressAndErrorStatus(progress, error);
		}

		private static Status combineProgress(Status one, Status two) {
			if (one.isNotRun() && two.isNotRun())
				return NOT_RUN;
			else if (one.isDone() && two.isDone())
				return OK;
			else if (!one.isRunning() && !two.isRunning())
				return OK; // one done, one not-run -> a parent failed and its children are not run
			else
				return RUNNING;
		}
		
		private static Status combineError(Status one, Status two) {
			if (one.isError() || two.isError())
				return ERROR;
			else if (one.isFailure() || two.isFailure())
				return FAILURE;
			else
				return OK;
		}
		
		private static Status combineProgressAndErrorStatus(Status progress, Status error) {
			if (progress.isDone()) {
				if (error.isError())
					return ERROR;
				if (error.isFailure())
					return FAILURE;
				return OK;
			}
			
			if (progress.isNotRun()) {
//				Assert.isTrue(!error.isErrorOrFailure());
				return NOT_RUN;
			}
			
//			Assert.isTrue(progress.isRunning());
			if (error.isError())
				return RUNNING_ERROR;
			if (error.isFailure())
				return RUNNING_FAILURE;
//			Assert.isTrue(error.isOK());
			return RUNNING;
		}
		
		/**
		 * @param oldStatus one of {@link ITestRunListener}'s STATUS_* constants
		 * @return the Status
		 */
		public static Status convert(int oldStatus) {
			return OLD_CODE[oldStatus];
		}
	}
	
	private final TestSuiteElement fParent;
	private final String fId;
	private String fTestName;

	private Status fStatus;
	private String fTrace;
	private String fExpected;
	private String fActual;
	
	/**
	 * @param parent the parent, can be <code>null</code>
	 * @param id the test id
	 * @param testName the test name
	 */
	public TestElement(TestSuiteElement parent, String id, String testName) {
		Assert.isNotNull(id);
		Assert.isNotNull(testName);
		fParent= parent;
		fId= id;
		fTestName= testName;
		fStatus= Status.NOT_RUN;
		if (parent != null)
			parent.addChild(this);
	}
	
	/**
	 * @return the parent suite, or <code>null</code> for the root
	 */
	public TestSuiteElement getParent() {
		return fParent;
	}
	
	public String getId() {
		return fId;
	}
	
	public String getTestName() {
		return fTestName;
	}
	
	public void setName(String name) {
		fTestName= name;
	}
	
	public void setStatus(Status status) {
		//JTODO: notify about change?
		//JTODO: multiple errors/failures per test https://bugs.eclipse.org/bugs/show_bug.cgi?id=125296
		fStatus= status;
		TestSuiteElement parent= getParent();
		if (parent != null)
			parent.childChangedStatus(this, status);
	}
	
	public void setStatus(Status status, String trace, String expected, String actual) {
		//JTODO: notify about change?
		//JTODO: multiple errors/failures per test https://bugs.eclipse.org/bugs/show_bug.cgi?id=125296
		fTrace= trace;
		fExpected= expected;
		fActual= actual;
		setStatus(status);
	}

	public Status getStatus() {
		return fStatus;
	}
	
	public String getTrace() {
		return fTrace;
	}		
	
	public String getExpected() {
		return fExpected;
	}		
	
	public String getActual() {
		return fActual;
	}		
	
	public boolean isComparisonFailure() {
		return fExpected != null && fActual != null;
	}
	
	// JTODO: Format of testName is highly underspecified. See RemoteTestRunner#getTestName(Test).
	
	public String getClassName() {
		return extractClassName(getTestName());
	}
	
	private String extractClassName(String testNameString) {
		int index= testNameString.indexOf('(');
		if (index < 0) 
			return testNameString;
		testNameString= testNameString.substring(index + 1);
		return testNameString.substring(0, testNameString.indexOf(')'));
	}
	
	public TestRoot getRoot() {
		return getParent().getRoot();
	}
	
	public String toString() {
		return getTestName() + ": " + getStatus(); //$NON-NLS-1$
	}
}