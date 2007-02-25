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
package descent.internal.corext.refactoring.reorg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;

import org.eclipse.core.resources.IResource;

import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.CopyProcessor;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.ReorgExecutionLog;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;

import descent.core.IJavaElement;
import descent.core.JavaModelException;

import descent.internal.corext.refactoring.IInternalRefactoringProcessorIds;
import descent.internal.corext.refactoring.RefactoringCoreMessages;
import descent.internal.corext.refactoring.changes.DynamicValidationStateChange;
import descent.internal.corext.refactoring.participants.JavaProcessors;
import descent.internal.corext.refactoring.participants.ResourceProcessors;
import descent.internal.corext.refactoring.reorg.IReorgPolicy.ICopyPolicy;
import descent.internal.corext.refactoring.tagging.ICommentProvider;
import descent.internal.corext.util.Resources;

public final class JavaCopyProcessor extends CopyProcessor implements IReorgDestinationValidator, ICommentProvider {
	//TODO: offer ICopyPolicy getCopyPolicy(); IReorgPolicy getReorgPolicy();
	// and remove delegate methods (also for JavaMoveProcessor)?

	private INewNameQueries fNewNameQueries;
	private IReorgQueries fReorgQueries;
	private ICopyPolicy fCopyPolicy;
	private ReorgExecutionLog fExecutionLog;
	private String fComment;
	
	public static JavaCopyProcessor create(IResource[] resources, IJavaElement[] javaElements) throws JavaModelException{
		ICopyPolicy copyPolicy= ReorgPolicyFactory.createCopyPolicy(resources, javaElements);
		if (! copyPolicy.canEnable())
			return null;
		return new JavaCopyProcessor(copyPolicy);
	}

	private JavaCopyProcessor(ICopyPolicy copyPolicy) {
		fCopyPolicy= copyPolicy;
	}
	
	public String getProcessorName() {
		return RefactoringCoreMessages.JavaCopyProcessor_processorName; 
	}
	
	public String getIdentifier() {
		return IInternalRefactoringProcessorIds.COPY_PROCESSOR;
	}
	
	public boolean isApplicable() throws CoreException {
		return fCopyPolicy.canEnable();
	}
	
	public void setNewNameQueries(INewNameQueries newNameQueries){
		Assert.isNotNull(newNameQueries);
		fNewNameQueries= newNameQueries;
	}

	public void setReorgQueries(IReorgQueries queries){
		Assert.isNotNull(queries);
		fReorgQueries= queries;
	}

	public IJavaElement[] getJavaElements() {
		return fCopyPolicy.getJavaElements();
	}

	public IResource[] getResources() {
		return fCopyPolicy.getResources();
	}
	
	public Object[] getElements() {
		IJavaElement[] jElements= fCopyPolicy.getJavaElements();
		IResource[] resources= fCopyPolicy.getResources();
		List result= new ArrayList(jElements.length + resources.length);
		result.addAll(Arrays.asList(jElements));
		result.addAll(Arrays.asList(resources));
		return result.toArray();
	}

	public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException {
		RefactoringStatus result= new RefactoringStatus();
		result.merge(RefactoringStatus.create(Resources.checkInSync(ReorgUtils.getNotNulls(fCopyPolicy.getResources()))));
		IResource[] javaResources= ReorgUtils.getResources(fCopyPolicy.getJavaElements());
		result.merge(RefactoringStatus.create(Resources.checkInSync(ReorgUtils.getNotNulls(javaResources))));
		return result;
	}

	public Object getCommonParentForInputElements(){
		return new ParentChecker(fCopyPolicy.getResources(), fCopyPolicy.getJavaElements()).getCommonParent();
	}
	
	public RefactoringStatus setDestination(IJavaElement destination) throws JavaModelException{
		return fCopyPolicy.setDestination(destination);
	}

	public RefactoringStatus setDestination(IResource destination) throws JavaModelException{
		return fCopyPolicy.setDestination(destination);
	}
	
	public boolean canChildrenBeDestinations(IJavaElement javaElement) {
		return fCopyPolicy.canChildrenBeDestinations(javaElement);
	}
	public boolean canChildrenBeDestinations(IResource resource) {
		return fCopyPolicy.canChildrenBeDestinations(resource);
	}
	public boolean canElementBeDestination(IJavaElement javaElement) {
		return fCopyPolicy.canElementBeDestination(javaElement);
	}
	public boolean canElementBeDestination(IResource resource) {
		return fCopyPolicy.canElementBeDestination(resource);
	}
	
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm, CheckConditionsContext context) throws CoreException {
		Assert.isNotNull(fNewNameQueries, "Missing new name queries"); //$NON-NLS-1$
		Assert.isNotNull(fReorgQueries, "Missing reorg queries"); //$NON-NLS-1$
		pm.beginTask("", 2); //$NON-NLS-1$
		RefactoringStatus result= fCopyPolicy.checkFinalConditions(new SubProgressMonitor(pm, 1), context, fReorgQueries);
		result.merge(context.check(new SubProgressMonitor(pm, 1)));
		return result;
	}

	/* (non-Javadoc)
	 * @see descent.internal.corext.refactoring.base.IRefactoring#createChange(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public Change createChange(IProgressMonitor pm) throws CoreException {
		Assert.isNotNull(fNewNameQueries);
		Assert.isTrue(fCopyPolicy.getJavaElementDestination() == null || fCopyPolicy.getResourceDestination() == null);
		Assert.isTrue(fCopyPolicy.getJavaElementDestination() != null || fCopyPolicy.getResourceDestination() != null);		
		try {
			final DynamicValidationStateChange result= new DynamicValidationStateChange(getChangeName()) {
				public Change perform(IProgressMonitor pm2) throws CoreException {
					try {
						super.perform(pm2);
					} catch(OperationCanceledException e) {
						fExecutionLog.markAsCanceled();
						throw e;
					}
					return null;
				}
				public Object getAdapter(Class adapter) {
					if (ReorgExecutionLog.class.equals(adapter))
						return fExecutionLog;
					return super.getAdapter(adapter);
				}
			};
			Change change= fCopyPolicy.createChange(pm, new MonitoringNewNameQueries(fNewNameQueries, fExecutionLog));
			if (change instanceof CompositeChange){
				CompositeChange subComposite= (CompositeChange)change;
				result.merge(subComposite);
			} else{
				result.add(change);
			}
			return result;		
		} finally {
			pm.done();
		}
	}

	private String getChangeName() {
		return RefactoringCoreMessages.JavaCopyProcessor_changeName; 
	}
	
	public RefactoringParticipant[] loadParticipants(RefactoringStatus status, SharableParticipants sharedParticipants) throws CoreException {
		RefactoringParticipant[] result= fCopyPolicy.loadParticipants(status, this, getAffectedProjectNatures(), sharedParticipants);
		fExecutionLog= fCopyPolicy.getReorgExecutionLog();
		return result;
	}
	
	private String[] getAffectedProjectNatures() throws CoreException {
		String[] jNatures= JavaProcessors.computeAffectedNaturs(fCopyPolicy.getJavaElements());
		String[] rNatures= ResourceProcessors.computeAffectedNatures(fCopyPolicy.getResources());
		Set result= new HashSet();
		result.addAll(Arrays.asList(jNatures));
		result.addAll(Arrays.asList(rNatures));
		return (String[])result.toArray(new String[result.size()]);
	}

	public boolean canEnableComment() {
		return true;
	}

	public String getComment() {
		return fComment;
	}

	public void setComment(String comment) {
		fComment= comment;
	}
}
