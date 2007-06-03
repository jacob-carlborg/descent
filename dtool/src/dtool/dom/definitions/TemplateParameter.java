package dtool.dom.definitions;

import dtool.descentadapter.DescentASTConverter;

/**
 * TODO clean up template parameter semantics a bit
 */
public abstract class TemplateParameter extends DefUnit {

	public static TemplateParameter[] convertMany(descent.internal.core.dom.TemplateParameter[] elems) {
		TemplateParameter[] tplParams = new TemplateParameter[elems.length];
		return DescentASTConverter.convertMany(elems, tplParams);
	}

}
