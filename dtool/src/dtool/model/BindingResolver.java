package dtool.model;

import java.util.List;

import util.StringUtil;
import dtool.dom.DefUnit;
import dtool.dombase.IScope;
import dtool.project.Project;

public class BindingResolver {
	
	private Project project;
	
	public BindingResolver(Project project) {
		this.project = project;
	}

	public static DefUnit findDefUnit(List<DefUnit> defunits, String name)
			throws ModelException {
		for (DefUnit defunit : defunits) {
			if (defunit.name.equals(name))
				return defunit;
		}
		throw new NoSuchEntModelException();
		
	}

	public DefUnit findEntity(String fqname) throws ModelException {
		String names[] = fqname.split("\\.");
		System.out.println(StringUtil.collToString(names, " . ") );
		
		IScope scopeent = project;
		DefUnit defunit;
		for (String name : names) {
			if(name.equals("%%")) {
				scopeent = project.testcu.getModule();
				continue;
			}
			defunit = findDefUnit(scopeent.getDefUnits(), name);
			scopeent = defunit.getScope();
		}
		if((scopeent instanceof DefUnit) == false) {
			throw new NotADefUnitModelException();
		}
		return (DefUnit) scopeent;

	}

}
