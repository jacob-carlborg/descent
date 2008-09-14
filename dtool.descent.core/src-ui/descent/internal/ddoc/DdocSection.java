package descent.internal.ddoc;


public class DdocSection {
	
	public static class Parameter {
		
		private String name;
		private String text;
		
		public Parameter(String name, String text) {
			this.name = name;
			this.text = text;
		}
		
		public String getName() {
			return name;
		}
		
		public String getText() {
			return text;
		}
		
	}
	
	public final static int NORMAL_SECTION = 0;
	public final static int PARAMS_SECTION = 1;
	public final static int MACROS_SECTION = 2;
	public final static int CODE_SECTION = 3;
	
	private final String name;
	private final String text;
	private final int kind;
	
	private Parameter[] parameters;
	
	public DdocSection(String name, int kind, String text) {
		this.name = name;
		this.text = text;
		this.kind = kind;
	}
	
	public DdocSection(String name, int kind, String text, Parameter[] parameters) {
		this.name = name;
		this.text = text;
		this.kind = kind;
		this.parameters = parameters;
	}
	
	public int getKind() {
		return this.kind;
	}
	
	public String getName() {
		return name;
	}
	
	public String getText() {
		return text;
	}
	
	public Parameter[] getParameters() {
		if (parameters == null) {
			return new Parameter[0];
		}		
		return parameters;
	}
	
	public void addParameters(Parameter[] others) {
		if (others.length == 0) return;
		
		Parameter[] newParameters = new Parameter[parameters.length + others.length];
		System.arraycopy(parameters, 0, newParameters, 0, parameters.length);
		System.arraycopy(others, 0, newParameters, parameters.length, others.length);
		parameters = newParameters;
	}

}
