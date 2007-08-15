package dtool.dom.definitions;

import java.util.List;

import dtool.descentadapter.DescentASTConverter;

/**
 * TODO clean up template parameter semantics a bit
 */
public abstract class TemplateParameter extends DefUnit {

	public static TemplateParameter[] convertMany(descent.internal.compiler.parser.TemplateParameter[] elems) {
		TemplateParameter[] tplParams = new TemplateParameter[elems.length];
		return DescentASTConverter.convertMany(elems, tplParams);
	}
	
	public static TemplateParameter[] convertMany(List<descent.internal.compiler.parser.TemplateParameter> elems) {
		TemplateParameter[] tplParams = new TemplateParameter[elems.size()];
		DescentASTConverter.convertMany(elems, tplParams);
		return tplParams;
	}

}
