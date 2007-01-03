package dtool.model;

@SuppressWarnings("serial")
public class NoSuchEntModelException extends ModelException {

	public NoSuchEntModelException() {
		super("No such entity found.");
	}
}
