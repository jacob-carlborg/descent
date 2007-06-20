package melnorme.util.ui.fields;

/** 
 * A command which takes an Object argument.
 * XXX: Is there something else like this?
 */
public interface IElementCommand {
	void executeCommand(Object element);
}