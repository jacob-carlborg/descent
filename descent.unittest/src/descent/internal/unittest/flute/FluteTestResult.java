package descent.internal.unittest.flute;

public class FluteTestResult
{
	public static enum ResultType
	{
		PASSED,
		FAILED,
		ERROR
	}
	
	private final ResultType type;
	
	FluteTestResult(ResultType type)
	{
		assert(null != type);
		this.type = type;
	}
	
	public ResultType getResultType()
	{
		return type;
	}
}
