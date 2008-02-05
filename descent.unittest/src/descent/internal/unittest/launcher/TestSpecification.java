package descent.internal.unittest.launcher;

import java.util.ArrayList;
import java.util.List;

import descent.core.ICompilationUnit;
import descent.core.IInitializer;
import descent.core.IJavaProject;
import descent.core.IType;
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
	
	public IType[] getEnclosingTypes()
	{
		List<IType> types = new ArrayList<IType>(4);
		IType type = declaration.getDeclaringType();
		while(null != type)
		{
			types.add(type);
			type = type.getDeclaringType();
		}
		return types.toArray(new IType[types.size()]);
	}
	
	public ICompilationUnit getModule()
	{
		return declaration.getCompilationUnit();
	}
	
	public IJavaProject getProject()
	{
		return declaration.getJavaProject();
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
