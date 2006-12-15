package dtool.model;

public class NoSuchEntModelException extends ModelException {

	public NoSuchEntModelException() {
		super("No such entity found.");
	}
}
