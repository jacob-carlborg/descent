package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class TemplateMixin extends TemplateInstance {

	public Identifiers idents;
	public Type tqual;
	public Objects tiargs;
	public int typeStart;
	public int typeLength;

	public TemplateMixin(Loc loc, IdentifierExp ident, Type tqual, Identifiers idents,
			Objects tiargs) {
		super(loc, idents.get(idents.size() - 1));
		this.ident = ident;
		this.tqual = tqual;
		this.idents = idents;
		this.tiargs = tiargs != null ? tiargs : new Objects(0);
	}

	public void setTypeSourceRange(int start, int length) {
		this.typeStart = start;
		this.typeLength = length;
	}

	@Override
	public int getNodeType() {
		return TEMPLATE_MIXIN;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, idents);
			TreeVisitor.acceptChildren(visitor, tiargs);
			TreeVisitor.acceptChildren(visitor, ident);
		}
		visitor.endVisit(this);
	}

	@Override
	public TemplateMixin isTemplateMixin() {
		return this;
	}

	@Override
	public String toChars(SemanticContext context) {
		OutBuffer buf = new OutBuffer();
		HdrGenState hgs = new HdrGenState();

		super.toCBuffer(buf, hgs, context);
		String s = buf.toChars();
		buf.data = null;
		return s;
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		TemplateMixin tm;

		Identifiers ids = new Identifiers(idents.size());
		for (int i = 0; i < idents.size(); i++) { // Matches TypeQualified::syntaxCopyHelper()
			IdentifierExp id = (IdentifierExp) idents.get(i);
			if (id.dyncast() == DYNCAST.DYNCAST_DSYMBOL) {
				TemplateInstance ti = ((TemplateInstanceWrapper) id).tempinst;

				ti = (TemplateInstance) ti.syntaxCopy(null);
				id = new TemplateInstanceWrapper(Loc.ZERO, ti);
			}
			ids.set(i, id);
		}

		tm = new TemplateMixin(loc, ident, (Type) (tqual != null ? tqual
				.syntaxCopy() : null), ids, tiargs);
		super.syntaxCopy(tm);
		return tm;
	}

}
