package melnorme.util.ui.fields;

import java.util.ArrayList;
import java.util.List;

import melnorme.util.ui.jface.ElementContentProvider;

import org.eclipse.jdt.internal.ui.wizards.dialogfields.ITreeListAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.TreeListDialogField;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

import util.Assert;

/**
 * A one-time factory and adapter for a TreeListEditorDialogField.
 * Usage:
 * instantiante, add commands, createTreeListEditor.
 * Note that the created TreeListEditor elements should implement IElement.
 * 
 * XXX: This class may need some redesigning
 */
public class TreeListEditorField {
	
	protected static class FieldOperation {
		public String label;
		public TreeListDialogField field;
		protected int buttonIx;
		
		public FieldOperation(String label) {
			this.label = label;
		}

		public void execute() {
		}
		
		public boolean canExecute() {
			return true;
		}
		
	}

	public List<FieldOperation> fOperations;
	private FieldOperation editOp;
	private FieldOperation removeOp;

	public TreeListDialogField fListField;
	public ITreeContentProvider provider;


	public static class TreeListContentProvider extends ElementContentProvider {
		public Object[] getElements(Object inputElement) {
			Assert.fail("Unreachable code.");
			return null;
		}
	}
	
	public TreeListEditorField() {
		fOperations = new ArrayList<FieldOperation>(10);
	}

	/** Adds a FieldOperation, attaching it to the field. */
	public void addOperation(FieldOperation op) {
		op.field = this.fListField;
		op.buttonIx = fOperations.size();
		fOperations.add(op);
	}
	
	/** Adds a FieldOperation creating it from an IElementCommand. */
	public void addElementOperation(String label, final IElementCommand cmd) {
		FieldOperation op = new FieldOperation(label) {
			public void execute() {
				cmd.executeCommand(null);
			}
		};
		addOperation(op);
	}
	

	/** Adds a pre-made edit operation, from an IElementCommand. */
	public void addEditOperation(String label, final IElementCommand cmd) {
		FieldOperation op = new FieldOperation(label) {
			public boolean canExecute() {
				return field.getSelectedElements().size() == 1; 
			}
			@Override
			public void execute() {
				Object elem = field.getSelectedElements().get(0);
				Assert.isTrue(field.getIndexOfElement(elem) != -1);
				cmd.executeCommand(elem);
			}
		};
		addOperation(op);
	}
	
	/** Adds a pre-made remove operation, from an IElementCommand. */
	public void addRemoveOperation(String label, final IElementCommand cmd) {
		FieldOperation op = new FieldOperation(label) {
			public boolean canExecute() {
				return field.getSelectedElements().size() > 0; 
			}
			@Override
			public void execute() {
				List selElements = field.getSelectedElements();
				for (int i = selElements.size() - 1; i >= 0 ; i--) {
					Object elem = selElements.get(i);
					cmd.executeCommand(elem);
				}
			}
		};
		addOperation(op);
	}

	/* ---- */
	
	/** Creates the TreeListDialogField, based on how this ListEditorField 
	 * was configured. */
	public TreeListDialogField createTreeList(ITreeContentProvider cprovider, 
			ILabelProvider lprovider) {
		
		String[] buttonLabels = new String[fOperations.size()];
		for(int i = 0; i < fOperations.size(); i++) {
			buttonLabels[i] = fOperations.get(i).label;
		}
		
		this.provider = cprovider;
		fListField = new TreeListDialogField(getTreeAdapter(), buttonLabels, lprovider);
		//fElementList.getTreeControl(parent)
		//fElementList.getTreeViewer().expandToLevel(1);
		for(FieldOperation op : fOperations) {
			op.field = fListField;
		}
		return fListField;
	}
	
	protected ITreeListAdapter getTreeAdapter() {
		return new TreeListEditorDialogFieldAdapter();
	}


	protected class TreeListEditorDialogFieldAdapter implements ITreeListAdapter {
		
		public Object[] getChildren(TreeListDialogField field, Object element) {
			return provider.getChildren(element);
		}

		public Object getParent(TreeListDialogField field, Object element) {
			return provider.getParent(element);
		}

		public boolean hasChildren(TreeListDialogField field, Object element) {
			return provider.hasChildren(element);
		}

		public void selectionChanged(TreeListDialogField field) {
			// Update button state
			for(FieldOperation op : fOperations) 
				field.enableButton(op.buttonIx, op.canExecute());
		}
		

		public void customButtonPressed(TreeListDialogField field, int index) {
			fOperations.get(index).execute();
		}

		public void doubleClicked(TreeListDialogField field) {
			if (editOp != null && editOp.canExecute()) {
				editOp.execute();
			}
		}

		public void keyPressed(TreeListDialogField field, KeyEvent event) {
			if (event.character == SWT.DEL && event.stateMask == 0) {
				if (removeOp != null && removeOp.canExecute()) {
					removeOp.execute();
				}
			}			
		}

	}

}
