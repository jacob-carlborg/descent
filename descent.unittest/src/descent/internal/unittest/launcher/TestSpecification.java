package descent.internal.unittest.launcher;

import descent.core.IInitializer;
import descent.unittest.ITestSpecification;

public final class TestSpecification implements ITestSpecification
{
	private final String id;
	private final String name;
	private final IInitializer declaration;
	
	public TestSpecification(String id, String name, IInitializer declaration)
	{
		this.id = id;
		this.name = name;
		this.declaration = declaration;
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public IInitializer getDeclaration()
	{
		return declaration;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(super.equals(other))
			return true;
		
		if(!(other instanceof TestSpecification))
			return false;
		
		TestSpecification ts = (TestSpecification) other;
		return getId().equals(ts.getId());
	}

	@Override
	public int hashCode()
	{
		return getId().hashCode();
	}

	@Override
	public String toString()
	{
		return id;
	}
}
