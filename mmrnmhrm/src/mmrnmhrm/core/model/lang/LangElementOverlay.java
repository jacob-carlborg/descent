package mmrnmhrm.core.model.lang;


public class LangElementOverlay extends LangElement {
	
	private LangElement rootElement;

	
	public LangElementOverlay(LangElement rootProj) {
		super(rootProj.parent);
		this.rootElement = rootProj;
	}
	
	@Override
	public ILangElement[] newChildrenArray(int size) {
		return rootElement.newChildrenArray(size);
	}


	public String getElementName() {
		return rootElement.getElementName();
	}



	public int getElementType() {
		return rootElement.getElementType();
	}
	
	
	public void save() {
		rootElement.setChildren(getChildren());
	}

}
