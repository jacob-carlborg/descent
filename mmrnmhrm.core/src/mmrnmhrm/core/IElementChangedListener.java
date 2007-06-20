package mmrnmhrm.core;

/**
 * An element changed listener receives notification of changes to Lang elements
 * maintained by the Lang model.
 */
public interface IElementChangedListener {
	
	/**
	 * Notifies that one or more attributes of one or more Lang elements have 
	 * changed.
	 * The specific details of the change are described by the given event.
	 */
	public void elementChanged(ElementChangedEvent event);
}
