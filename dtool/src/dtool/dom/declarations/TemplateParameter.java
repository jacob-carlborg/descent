package dtool.dom.declarations;

import dtool.descentadapter.DescentASTConverter;

public abstract class TemplateParameter extends DefUnit {



	public static TemplateParameter[] convertMany(descent.internal.core.dom.TemplateParameter[] elems) {
		return (TemplateParameter[]) DescentASTConverter.convertMany(elems);
	}

}
