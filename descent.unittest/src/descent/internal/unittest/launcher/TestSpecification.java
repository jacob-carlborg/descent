package descent.internal.unittest.launcher;

import java.util.ArrayList;
import java.util.List;

import descent.core.ICompilationUnit;
import descent.core.IInitializer;
import descent.core.IType;

public final class TestSpecification
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
}
