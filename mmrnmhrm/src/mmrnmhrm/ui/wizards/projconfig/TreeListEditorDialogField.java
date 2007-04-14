package mmrnmhrm.ui.wizards.projconfig;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ITreeListAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.TreeListDialogField;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

import util.Assert;
import util.tree.IElement;

public class TreeListEditorDialogField {
	
	public static enum COMMAND {
		CUSTOM,
		ADD,
		EDIT,
		REMOVE,
	}
	
	public static class EditorFieldCommand {
		public COMMAND type;
		public String label;
		public IEditorFieldAction action;
		
		public EditorFieldCommand(COMMAND id, String label, IEditorFieldAction action) {
			this.type = id;
			this.label = label;
			this.action = action;
		}
		
		public void execute(DialogField field) {
			action.execute(field);
		}
		
	}
	
	public interface IElementCommand {
		void executeElementCommand(Object element);
	}


/*	public TreeListEditorDialogField(ITreeListAdapter adapter, String[] buttonLabels, ILabelProvider lprovider) {
		super(adapter, buttonLabels, lprovider);
	}
*/
	public List<EditorFieldCommand> fButtonCmds;
	private IElementCommand editCmd;
	private IElementCommand removeCmd;
	private int editIx;
	private int removeIx;
	
	
	public TreeListEditorDialogField() {
		fButtonCmds = new ArrayList<EditorFieldCommand>(10);
	}

	public void addCommand(String label, IEditorFieldAction action) {
		fButtonCmds.add(new EditorFieldCommand(COMMAND.CUSTOM, label, action));
	}
	
	public void addEditCommand(String label, IElementCommand cmd) {
		Assert.isTrue(editCmd == null);
		fButtonCmds.add(new EditorFieldCommand(COMMAND.EDIT, label, null));
		editCmd = cmd;
		editIx = fButtonCmds.size()-1; 
	}
	
	public void addRemoveCommand(String label, IElementCommand cmd) {
		Assert.isTrue(removeCmd == null);
		fButtonCmds.add(new EditorFieldCommand(COMMAND.REMOVE, label, null));
		removeCmd = cmd;
		removeIx = fButtonCmds.size()-1;
	}

	
	/***** */
	

	private EditorFieldCommand getCommand(int index) {
		return fButtonCmds.get(index);
	}
	
	public TreeListDialogField createTreeListEditor() {
		return createTreeListEditor(getTreeListAdapter(), new LabelProvider());
	}
	
	public TreeListDialogField createTreeListEditor(ITreeListAdapter adapter, ILabelProvider lprovider) {
		
		String[] buttonLabels = new String[fButtonCmds.size()];
		for(int i = 0; i < fButtonCmds.size(); i++) {
			buttonLabels[i] = fButtonCmds.get(i).label;
		}
		
		TreeListDialogField field; 
		field = new TreeListDialogField(adapter, buttonLabels, lprovider);
		
		return field;
	}

	
	private ITreeListAdapter getTreeListAdapter() {
		return new TreeListEditorDialogFieldAdapter();
	}

	public class TreeListEditorDialogFieldAdapter implements ITreeListAdapter {
		
		public Object[] getChildren(TreeListDialogField field, Object element) {
			return ((IElement) element).getChildren();
		}

		public Object getParent(TreeListDialogField field, Object element) {
			return ((IElement) element).getParent();
		}

		public boolean hasChildren(TreeListDialogField field, Object element) {
			return ((IElement) element).hasChildren();
		}

		public void customButtonPressed(TreeListDialogField field, int index) {
			if (getCommand(index).type == COMMAND.EDIT) {
				editEntry(field);
			} else if (getCommand(index).type == COMMAND.REMOVE) {
				removeEntries(field);
			} else {
				getCommand(index).execute(field);
			}
		}

		public void keyPressed(TreeListDialogField field, KeyEvent event) {
			if (event.character == SWT.DEL && event.stateMask == 0) {
				List selection= field.getSelectedElements();
				if (canRemove(selection)) {
					removeEntries(field);
				}
			}			
		}

		public void selectionChanged(TreeListDialogField field) {
			List selected= field.getSelectedElements();
			if(editCmd != null)
				field.enableButton(editIx, canEdit(selected));
			if(removeCmd != null)
				field.enableButton(removeIx, canRemove(selected));
		}

		public void doubleClicked(TreeListDialogField field) {
			List selection= field.getSelectedElements();
			if (editCmd != null && canEdit(selection)) {
				editEntry(field);
			}
		}


		private boolean canEdit(List selElements) {
			if (selElements.size() != 1) {
				return false;
			}
			return true;
		}


		private boolean canRemove(List selElements) {
			if (selElements.size() == 0) {
				return false;
			}
			return true;
		}
		
		private void editEntry(TreeListDialogField field) {
			List selElements= field.getSelectedElements();
			if (selElements.size() != 1) {
				return;
			}
			Object elem= selElements.get(0);
			if (field.getIndexOfElement(elem) != -1) {
				editCmd.executeElementCommand(elem);
			} 
		}
		
		
		private void removeEntries(TreeListDialogField field) {
			List selElements= field.getSelectedElements();
			for (int i= selElements.size() - 1; i >= 0 ; i--) {
				Object elem= selElements.get(i);
				removeCmd.executeElementCommand(elem);
			}
		}
	}
}
