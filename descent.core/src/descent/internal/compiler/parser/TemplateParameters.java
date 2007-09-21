package descent.internal.compiler.parser;

// DMD 1.020
public class TemplateParameters extends Array<TemplateParameter> {

	private static final long serialVersionUID = 1L;
	
	public TemplateParameters() {
	}
	
	public TemplateParameters(int capacity) {
		super(capacity);
	}
	
	public TemplateParameters(TemplateParameters elements) {
		super(elements);
	}

}
