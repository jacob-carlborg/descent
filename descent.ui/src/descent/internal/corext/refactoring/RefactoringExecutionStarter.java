/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.internal.corext.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.participants.DeleteRefactoring;
import org.eclipse.swt.widgets.Shell;

import descent.internal.corext.refactoring.reorg.JavaDeleteProcessor;
import descent.internal.ui.refactoring.reorg.DeleteUserInterfaceManager;

/**
 * Helper class to run refactorings from action code.
 * <p>
 * This class has been introduced to decouple actions from the refactoring code, in order not to eagerly load refactoring classes during action initialization.
 * </p>
 * 
 * @since 3.1
 */
public final class RefactoringExecutionStarter {

	/* TODO JDT UI refactor rename
	private static RenameSupport createRenameSupport(IJavaElement element, String newName, int flags) throws CoreException {
		switch (element.getElementType()) {
			case IJavaElement.JAVA_PROJECT:
				return RenameSupport.create((IJavaProject) element, newName, flags);
			case IJavaElement.PACKAGE_FRAGMENT_ROOT:
				return RenameSupport.create((IPackageFragmentRoot) element, newName);
			case IJavaElement.PACKAGE_FRAGMENT:
				return RenameSupport.create((IPackageFragment) element, newName, flags);
			case IJavaElement.COMPILATION_UNIT:
				return RenameSupport.create((ICompilationUnit) element, newName, flags);
			case IJavaElement.TYPE:
				return RenameSupport.create((IType) element, newName, flags);
			case IJavaElement.METHOD:
				final IMethod method= (IMethod) element;
				if (method.isConstructor())
					return createRenameSupport(method.getDeclaringType(), newName, flags);
				else
					return RenameSupport.create((IMethod) element, newName, flags);
			case IJavaElement.FIELD:
				return RenameSupport.create((IField) element, newName, flags);
			case IJavaElement.TYPE_PARAMETER:
				return RenameSupport.create((ITypeParameter) element, newName, flags);
			case IJavaElement.LOCAL_VARIABLE:
				return RenameSupport.create((ILocalVariable) element, newName, flags);
		}
		return null;
	}
	*/

	/* TODO JDT Ui refactor
	public static void startChangeSignatureRefactoring(final IMethod method, final SelectionDispatchAction action, final Shell shell) throws JavaModelException {
		if (!RefactoringAvailabilityTester.isChangeSignatureAvailable(method))
			return;
		final ChangeSignatureRefactoring refactoring= new ChangeSignatureRefactoring(method);
		if (!ActionUtil.isProcessable(shell, refactoring.getMethod()))
			return;
		final UserInterfaceStarter starter= new UserInterfaceStarter() {

			public final void activate(final Refactoring ref, final Shell parent, final boolean save) throws CoreException {
				final RefactoringStatus status= ref.checkInitialConditions(new NullProgressMonitor());
				if (status.hasFatalError()) {
					final RefactoringStatusEntry entry= status.getEntryMatchingSeverity(RefactoringStatus.FATAL);
					if (entry.getCode() == RefactoringStatusCodes.OVERRIDES_ANOTHER_METHOD || entry.getCode() == RefactoringStatusCodes.METHOD_DECLARED_IN_INTERFACE) {

						String message= entry.getMessage();
						final Object element= entry.getData();
						message= message + RefactoringMessages.RefactoringErrorDialogUtil_okToPerformQuestion;
						if (element != null && MessageDialog.openQuestion(shell, RefactoringMessages.OpenRefactoringWizardAction_refactoring, message)) {

							final IStructuredSelection selection= new StructuredSelection(element);
							//TODO: should not hijack this ModifiyParametersAction.
							// The action is set up on an editor, but we use it as if it were set up on a ViewPart.
							boolean wasEnabled= action.isEnabled();
							action.selectionChanged(selection);
							if (action.isEnabled()) {
								action.run(selection);
							} else {
								MessageDialog.openInformation(shell, ActionMessages.ModifyParameterAction_problem_title, ActionMessages.ModifyParameterAction_problem_message);
							}
							action.setEnabled(wasEnabled);
						}
						return;
					}
				}
				super.activate(ref, parent, save);
			}
		};
		starter.initialize(new ChangeSignatureWizard(refactoring));
		try {
			starter.activate(refactoring, shell, true);
		} catch (CoreException e) {
			ExceptionHandler.handle(e, RefactoringMessages.OpenRefactoringWizardAction_refactoring, RefactoringMessages.RefactoringStarter_unexpected_exception);
		}
	}

	public static void startChangeTypeRefactoring(final ICompilationUnit unit, final Shell shell, final int offset, final int length) throws JavaModelException {
		final ChangeTypeRefactoring refactoring= new ChangeTypeRefactoring(unit, offset, length);
		new RefactoringStarter().activate(refactoring, new ChangeTypeWizard(refactoring), shell, RefactoringMessages.ChangeTypeAction_dialog_title, false);
	}

	public static void startConvertAnonymousRefactoring(final ICompilationUnit unit, final int offset, final int length, final Shell shell) throws JavaModelException {
		final ConvertAnonymousToNestedRefactoring refactoring= new ConvertAnonymousToNestedRefactoring(unit, JavaPreferencesSettings.getCodeGenerationSettings(unit.getJavaProject()), offset, length);
		new RefactoringStarter().activate(refactoring, new ConvertAnonymousToNestedWizard(refactoring), shell, RefactoringMessages.ConvertAnonymousToNestedAction_dialog_title, false);
	}

	public static void startCutRefactoring(final Object[] elements, final Shell shell) throws CoreException, InterruptedException, InvocationTargetException {
		final JavaDeleteProcessor processor= new JavaDeleteProcessor(elements);
		processor.setSuggestGetterSetterDeletion(false);
		processor.setQueries(new ReorgQueries(shell));
		new RefactoringExecutionHelper(new DeleteRefactoring(processor), RefactoringCore.getConditionCheckingFailedSeverity(), false, shell, new ProgressMonitorDialog(shell)).perform(false);
	}
	*/

	public static void startDeleteRefactoring(final Object[] elements, final Shell shell) throws CoreException {
		final DeleteRefactoring refactoring= new DeleteRefactoring(new JavaDeleteProcessor(elements));
		DeleteUserInterfaceManager.getDefault().getStarter(refactoring).activate(refactoring, shell, false);
	}

	/* TODO JDT Ui refactor
	public static void startExtractInterfaceRefactoring(final IType type, final Shell shell) throws JavaModelException {
		final ExtractInterfaceRefactoring refactoring= new ExtractInterfaceRefactoring(new ExtractInterfaceProcessor(type, JavaPreferencesSettings.getCodeGenerationSettings(type.getJavaProject())));
		if (!ActionUtil.isProcessable(shell, refactoring.getExtractInterfaceProcessor().getType()))
			return;
		new RefactoringStarter().activate(refactoring, new ExtractInterfaceWizard(refactoring), shell, RefactoringMessages.OpenRefactoringWizardAction_refactoring, true); 
	}

	public static void startInferTypeArgumentsRefactoring(final IJavaElement[] elements, final Shell shell) {
		if (!ActionUtil.areProcessable(shell, elements))
			return;
		try {
			if (!RefactoringAvailabilityTester.isInferTypeArgumentsAvailable(elements))
				return;
			final InferTypeArgumentsRefactoring refactoring= new InferTypeArgumentsRefactoring(elements);
			new RefactoringStarter().activate(refactoring, new InferTypeArgumentsWizard(refactoring), shell, RefactoringMessages.InferTypeArgumentsAction_dialog_title, true);
		} catch (CoreException e) {
			ExceptionHandler.handle(e, RefactoringMessages.InferTypeArgumentsAction_dialog_title, RefactoringMessages.OpenRefactoringWizardAction_exception);
		}
	}

	public static boolean startInlineConstantRefactoring(final ICompilationUnit unit, final CompilationUnit node, final int offset, final int length, final Shell shell, final boolean activate) throws JavaModelException {
		final InlineConstantRefactoring refactoring= new InlineConstantRefactoring(unit, node, offset, length);
		if (refactoring.checkStaticFinalConstantNameSelected().hasFatalError()) {
			if (activate)
				MessageDialog.openInformation(shell, RefactoringMessages.InlineConstantAction_dialog_title, RefactoringMessages.InlineConstantAction_no_constant_reference_or_declaration);
			return false;
		}
		if (activate)
			new RefactoringStarter().activate(refactoring, new InlineConstantWizard(refactoring), shell, RefactoringMessages.InlineConstantAction_dialog_title, true);
		return true;
	}

	public static boolean startInlineMethodRefactoring(final IJavaElement unit, final CompilationUnit node, final int offset, final int length, final Shell shell, final boolean activate) throws JavaModelException {
		final InlineMethodRefactoring refactoring= InlineMethodRefactoring.create(unit, node, offset, length);
		if (refactoring == null) {
			if (activate)
				MessageDialog.openInformation(shell, RefactoringMessages.InlineMethodAction_dialog_title, RefactoringMessages.InlineMethodAction_no_method_invocation_or_declaration_selected);
			return false;
		}
		if (activate)
			new RefactoringStarter().activate(refactoring, new InlineMethodWizard(refactoring), shell, RefactoringMessages.InlineMethodAction_dialog_title, true);
		return true;
	}

	public static boolean startInlineTempRefactoring(final ICompilationUnit unit, final CompilationUnit node, final ITextSelection selection, final Shell shell, final boolean activate) throws JavaModelException {
		final InlineTempRefactoring refactoring= new InlineTempRefactoring(unit, selection.getOffset(), selection.getLength());
		if (!refactoring.checkIfTempSelected(node).hasFatalError()) {
			if (activate)
				new RefactoringStarter().activate(refactoring, new InlineTempWizard(refactoring), shell, RefactoringMessages.InlineTempAction_inline_temp, false);
			return true;
		}
		return false;
	}

	public static void startIntroduceFactoryRefactoring(final ICompilationUnit unit, final ITextSelection selection, final Shell shell) throws JavaModelException {
		final IntroduceFactoryRefactoring refactoring= new IntroduceFactoryRefactoring(unit, selection.getOffset(), selection.getLength());
		new RefactoringStarter().activate(refactoring, new IntroduceFactoryWizard(refactoring, RefactoringMessages.IntroduceFactoryAction_use_factory), shell, RefactoringMessages.IntroduceFactoryAction_dialog_title, false);
	}
	
	public static void startIntroduceIndirectionRefactoring(final ICompilationUnit unit, final int offset, final int length, final Shell shell) throws JavaModelException {
		final IntroduceIndirectionRefactoring refactoring= new IntroduceIndirectionRefactoring(unit, offset, length);
		new RefactoringStarter().activate(refactoring, new IntroduceIndirectionWizard(refactoring, RefactoringMessages.IntroduceIndirectionAction_dialog_title), shell, RefactoringMessages.IntroduceIndirectionAction_dialog_title, true);
	}
	
	public static void startIntroduceIndirectionRefactoring(final IClassFile file, final int offset, final int length, final Shell shell) throws JavaModelException {
		final IntroduceIndirectionRefactoring refactoring= new IntroduceIndirectionRefactoring(file, offset, length);
		new RefactoringStarter().activate(refactoring, new IntroduceIndirectionWizard(refactoring, RefactoringMessages.IntroduceIndirectionAction_dialog_title), shell, RefactoringMessages.IntroduceIndirectionAction_dialog_title, true);
	}
	
	public static void startIntroduceIndirectionRefactoring(final IMethod method, final Shell shell) throws JavaModelException {
		final IntroduceIndirectionRefactoring refactoring= new IntroduceIndirectionRefactoring(method);
		new RefactoringStarter().activate(refactoring, new IntroduceIndirectionWizard(refactoring, RefactoringMessages.IntroduceIndirectionAction_dialog_title), shell, RefactoringMessages.IntroduceIndirectionAction_dialog_title, true);
	}

	public static void startMoveInnerRefactoring(final IType type, final Shell shell) throws JavaModelException {
		if (!RefactoringAvailabilityTester.isMoveInnerAvailable(type))
			return;
		final MoveInnerToTopRefactoring refactoring= new MoveInnerToTopRefactoring(type, JavaPreferencesSettings.getCodeGenerationSettings(type.getJavaProject()));
		if (!ActionUtil.isProcessable(shell, refactoring.getInputType()))
			return;
		new RefactoringStarter().activate(refactoring, new MoveInnerToTopWizard(refactoring), shell, RefactoringMessages.OpenRefactoringWizardAction_refactoring, true);
	}

	public static void startMoveMethodRefactoring(final IMethod method, final Shell shell) throws JavaModelException {
		final MoveInstanceMethodRefactoring refactoring= new MoveInstanceMethodRefactoring(new MoveInstanceMethodProcessor(method, JavaPreferencesSettings.getCodeGenerationSettings(method.getJavaProject())));
		new RefactoringStarter().activate(refactoring, new MoveInstanceMethodWizard(refactoring), shell, RefactoringMessages.MoveInstanceMethodAction_dialog_title, true);
	}

	public static void startMoveStaticMembersRefactoring(final IMember[] members, final Shell shell) throws JavaModelException {
		if (!RefactoringAvailabilityTester.isMoveStaticAvailable(members))
			return;
		final Set set= new HashSet();
		set.addAll(Arrays.asList(members));
		final IMember[] elements= (IMember[]) set.toArray(new IMember[set.size()]);
		IJavaProject project= null;
		if (elements.length > 0)
			project= elements[0].getJavaProject();
		final JavaMoveRefactoring refactoring= new JavaMoveRefactoring(new MoveStaticMembersProcessor(elements, JavaPreferencesSettings.getCodeGenerationSettings(project)));
		if (ActionUtil.isProcessable(shell, ((MoveStaticMembersProcessor) refactoring.getAdapter(MoveStaticMembersProcessor.class)).getMembersToMove()[0].getCompilationUnit()))
			new RefactoringStarter().activate(refactoring, new MoveMembersWizard(refactoring), shell, RefactoringMessages.OpenRefactoringWizardAction_refactoring, true);
	}

	public static void startExtractSupertypeRefactoring(final IMember[] members, final Shell shell) throws JavaModelException {
		if (!RefactoringAvailabilityTester.isExtractSupertypeAvailable(members))
			return;
		IJavaProject project= null;
		if (members != null && members.length > 0)
			project= members[0].getJavaProject();
		final ExtractSupertypeRefactoring refactoring= new ExtractSupertypeRefactoring(new ExtractSupertypeProcessor(members, JavaPreferencesSettings.getCodeGenerationSettings(project)));
		if (!ActionUtil.isProcessable(shell, refactoring.getExtractSupertypeProcessor().getDeclaringType()))
			return;
		new RefactoringStarter().activate(refactoring, new ExtractSupertypeWizard(refactoring), shell, RefactoringMessages.OpenRefactoringWizardAction_refactoring, true);
	}

	public static void startPullUpRefactoring(final IMember[] members, final Shell shell) throws JavaModelException {
		if (!RefactoringAvailabilityTester.isPullUpAvailable(members))
			return;
		IJavaProject project= null;
		if (members != null && members.length > 0)
			project= members[0].getJavaProject();
		final PullUpRefactoring refactoring= new PullUpRefactoring(new PullUpRefactoringProcessor(members, JavaPreferencesSettings.getCodeGenerationSettings(project)));
		if (!ActionUtil.isProcessable(shell, refactoring.getPullUpProcessor().getDeclaringType()))
			return;
		new RefactoringStarter().activate(refactoring, new PullUpWizard(refactoring), shell, RefactoringMessages.OpenRefactoringWizardAction_refactoring, true);
	}

	public static void startPushDownRefactoring(final IMember[] members, final Shell shell) throws JavaModelException {
		if (!RefactoringAvailabilityTester.isPushDownAvailable(members))
			return;
		final PushDownRefactoring refactoring= new PushDownRefactoring(new PushDownRefactoringProcessor(members));
		if (!ActionUtil.isProcessable(shell, refactoring.getPushDownProcessor().getDeclaringType()))
			return;
		new RefactoringStarter().activate(refactoring, new PushDownWizard(refactoring), shell, RefactoringMessages.OpenRefactoringWizardAction_refactoring, true);
	}

	public static void startRefactoring(final IResource[] resources, final IJavaElement[] elements, final Shell shell) throws JavaModelException {
		IMovePolicy policy= ReorgPolicyFactory.createMovePolicy(resources, elements);
		if (policy.canEnable()) {
			final JavaMoveProcessor processor= new JavaMoveProcessor(policy);
			final JavaMoveRefactoring refactoring= new JavaMoveRefactoring(processor);
			final RefactoringWizard wizard= new ReorgMoveWizard(refactoring);
			processor.setCreateTargetQueries(new CreateTargetQueries(wizard));
			processor.setReorgQueries(new ReorgQueries(wizard));
			new RefactoringStarter().activate(refactoring, wizard, shell, RefactoringMessages.OpenRefactoringWizardAction_refactoring, true);
		}
	}

	public static void startRenameRefactoring(final IJavaElement element, final Shell shell) throws CoreException {
		final RenameSupport support= createRenameSupport(element, null, RenameSupport.UPDATE_REFERENCES);
		if (support != null && support.preCheck().isOK())
			support.openDialog(shell);
	}

	public static void startRenameResourceRefactoring(final IResource resource, final Shell shell) throws CoreException {
		final JavaRenameRefactoring refactoring= new JavaRenameRefactoring(new RenameResourceProcessor(resource));
		RenameUserInterfaceManager.getDefault().getStarter(refactoring).activate(refactoring, shell, true);
	}
	
	public static void startReplaceInvocationsRefactoring(final IJavaElement unit, final int offset, final int length, final Shell shell) throws JavaModelException {
		final ReplaceInvocationsRefactoring refactoring= new ReplaceInvocationsRefactoring(unit, offset, length);
		new RefactoringStarter().activate(refactoring, new ReplaceInvocationsWizard(refactoring), shell, RefactoringMessages.ReplaceInvocationsAction_dialog_title, true);
	}
	
	public static void startReplaceInvocationsRefactoring(final IMethod method, final Shell shell) throws JavaModelException {
		final ReplaceInvocationsRefactoring refactoring= new ReplaceInvocationsRefactoring(method);
		new RefactoringStarter().activate(refactoring, new ReplaceInvocationsWizard(refactoring), shell, RefactoringMessages.ReplaceInvocationsAction_dialog_title, true);
	}

	public static void startSelfEncapsulateRefactoring(final IField field, final Shell shell) {
		if (!ActionUtil.isProcessable(shell, field))
			return;
		try {
			if (!RefactoringAvailabilityTester.isSelfEncapsulateAvailable(field))
				return;
			final SelfEncapsulateFieldRefactoring refactoring= new SelfEncapsulateFieldRefactoring(field);
			new RefactoringStarter().activate(refactoring, new SelfEncapsulateFieldWizard(refactoring), shell, ActionMessages.SelfEncapsulateFieldAction_dialog_title, true);
		} catch (JavaModelException e) {
			ExceptionHandler.handle(e, ActionMessages.SelfEncapsulateFieldAction_dialog_title, ActionMessages.SelfEncapsulateFieldAction_dialog_cannot_perform);
		}
	}

	public static void startUseSupertypeRefactoring(final IType type, final Shell shell) throws JavaModelException {
		final UseSuperTypeRefactoring refactoring= new UseSuperTypeRefactoring(new UseSuperTypeProcessor(type));
		if (!ActionUtil.isProcessable(shell, refactoring.getUseSuperTypeProcessor().getSubType()))
			return;
		new RefactoringStarter().activate(refactoring, new UseSupertypeWizard(refactoring), shell, RefactoringMessages.OpenRefactoringWizardAction_refactoring, true);
	}

	private RefactoringExecutionStarter() {
		// Not for instantiation
	}

	public static void startCleanupRefactoring(final ICompilationUnit[] cus) throws JavaModelException {
		CleanUpRefactoring refactoring= new CleanUpRefactoring();
		for (int i= 0; i < cus.length; i++) {
			refactoring.addCompilationUnit(cus[i]);
		}
		CleanUpRefactoringWizard refactoringWizard= new CleanUpRefactoringWizard(refactoring, RefactoringWizard.WIZARD_BASED_USER_INTERFACE, false, true);
		RefactoringStarter starter= new RefactoringStarter();
			starter.activate(refactoring, refactoringWizard, JavaPlugin.getActiveWorkbenchShell(), "Clean ups", false); //$NON-NLS-1$
	}

	public static void startCleanupRefactoring(ICompilationUnit cu) throws JavaModelException {
		CleanUpRefactoring refactoring= new CleanUpRefactoring();
		refactoring.addCompilationUnit(cu);
		CleanUpRefactoringWizard refactoringWizard= new CleanUpRefactoringWizard(refactoring, RefactoringWizard.WIZARD_BASED_USER_INTERFACE, false, true);
		RefactoringStarter starter= new RefactoringStarter();
		starter.activate(refactoring, refactoringWizard, JavaPlugin.getActiveWorkbenchShell(), "Clean ups", false); //$NON-NLS-1$
	}
	*/
}
