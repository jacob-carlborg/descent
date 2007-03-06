package dtool.model;

import java.util.List;

import util.StringUtil;
import dtool.dom.base.DefUnit;
import dtool.project.DToolProject;

public class BindingResolver {
	
	private DToolProject project;
	
	public BindingResolver(DToolProject project) {
		this.project = project;
	}

	public static DefUnit getDefUnit(List<DefUnit> defunits, String name) {
		for (DefUnit defunit : defunits) {
			if (defunit.symbol.equalsStr(name))
				return defunit;
		}
		return null;
		//throw new NoSuchEntModelException();
		
	}

	public DefUnit findEntity(String fqname) throws ModelException {
		String names[] = fqname.split("\\.");
		System.out.println(StringUtil.collToString(names, " . ") );
		
		IScope scopeent = project;
		DefUnit defunit;
		for (String name : names) {
			if(name.equals("%%")) {
				scopeent = project.testcu.getNeoModule();
				continue;
			}
			defunit = getDefUnit(scopeent.getDefUnits(), name);
			scopeent = defunit.getScope();
		}
		if((scopeent instanceof DefUnit) == false) {
			throw new NotADefUnitModelException();
		}
		return (DefUnit) scopeent;

	}

}
