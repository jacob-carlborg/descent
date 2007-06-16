package descent.internal.ui.text.spelling.engine;


/**
 * Interface for spell event listeners.
 *
 * @since 3.0
 */
public interface ISpellEventListener {

	/**
	 * Handles a spell event.
	 *
	 * @param event
	 *                  Event to handle
	 */
	public void handle(ISpellEvent event);
}
