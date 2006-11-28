package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IElement;

public class TemplateInstance extends Identifier {

	public List<IElement> tiargs;

	public TemplateInstance(Identifier id) {
		super(id.string, id.value);
		this.start = id.start;
		this.length = id.length;
	}
	
	public IElement[] getTemplateArguments() {
		if (tiargs == null) {
			return AbstractElement.NO_ELEMENTS;
		} else {
			return tiargs.toArray(new IElement[tiargs.size()]);
		}
	}

}
