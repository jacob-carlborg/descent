package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IDElement;

public class TemplateInstance extends Identifier {

	public List<IDElement> tiargs;

	public TemplateInstance(Loc loc, Identifier id) {
		super(id.string, id.value);
		this.start = id.start;
		this.length = id.length;
	}
	
	public IDElement[] getTemplateArguments() {
		if (tiargs == null) {
			return AbstractElement.NO_ELEMENTS;
		} else {
			return tiargs.toArray(new IDElement[tiargs.size()]);
		}
	}

}
