package descent.internal.compiler.parser;

public class FuncExp extends Expression {

	public FuncLiteralDeclaration fd;

	public FuncExp(FuncLiteralDeclaration fd) {
		super(TOK.TOKfunction);
		this.fd = fd;
	}

	@Override
	public int getNodeType() {
		return FUNC_EXP;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		if (type == null) {
			fd.semantic(sc, context);
			fd.parent = sc.parent;
			if (context.global.errors > 0) {
				if (fd.type.next == null) {
					fd.type.next = Type.terror;
				}
			} else {
				fd.semantic2(sc, context);
				if (context.global.errors == 0) {
					fd.semantic3(sc, context);

					if (context.global.errors == 0
							&& context.global.params.useInline) {
						fd.inlineScan(context);
					}
				}
			}

			// Type is a "delegate to" or "pointer to" the function literal
			if (fd.isNested()) {
				type = new TypeDelegate(fd.type);
				type = type.semantic(sc, context);
			} else {
				type = fd.type.pointerTo(context);
			}
		}
		return this;
	}
	
	@Override
	public Expression syntaxCopy() {
		return new FuncExp((FuncLiteralDeclaration) fd.syntaxCopy(null));
	}
	
	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.writestring(fd.toChars());
	}

	@Override
	public String toChars() {
		return fd.toChars();
	}

}
