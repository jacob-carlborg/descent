package scratch.swt.jface;

public class FooElement {
	
	protected final String name;
	protected FooElement[] children;
	protected FooElement parent;
	
	public FooElement(String name) {
		this.name = name;
	}
	
	public FooElement(String name, FooElement[] children ) {
		this(name);
		this.children = children;
		for (FooElement fooElement : children) {
			fooElement.parent = this;
		}
	}
	
	public String getName() {
		return name;
	}

	public FooElement[] getChildren() {
		return children;
	}

	public Object getParent() {
		return parent;
	}

}
