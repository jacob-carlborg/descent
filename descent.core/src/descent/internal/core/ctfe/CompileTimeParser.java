package descent.internal.core.ctfe;

import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.TemplateInstance;

public class CompileTimeParser extends Parser {

	public CompileTimeParser(int apiLevel, char[] source, int offset, int length,
			char[] filename, boolean recordLineSeparator) {
		super(apiLevel, source, offset, length, filename, recordLineSeparator);
	}
	
	@Override
	protected TemplateInstance newTemplateInstance(Loc loc, IdentifierExp id,
			ASTNodeEncoder encoder) {
		return new CompileTimeTemplateInstance(loc, id, encoder);
	}

}
