package descent.internal.unittest.flute;

import java.io.IOException;

class RunningOneTest implements IState
{
	FluteTestResult result = null;
	final FluteApplicationInstance cli;
	
	RunningOneTest(FluteApplicationInstance cli)
	{
		this.cli = cli;
	}

	@Override
	public void interpret(String text) throws IOException
	{
		System.out.println("RunningOneTest.interpret(" + text + ");");
		if(text.equals(FluteApplicationInstance.AWAITING_INPUT))
		{
			cli.notifyStateReturn();
		}
	}

}
