package descent.internal.unittest.flute;

import java.io.IOException;

public class Waiting implements IState
{
	private FluteApplicationInstance cli;
	
	public Waiting(FluteApplicationInstance cli)
	{
		this.cli = cli;
	}

	@Override
	public void interpret(String text) throws IOException
	{
		System.out.println("Waiting.interpret(" + text + ");");
		// Ignore
	}

}
