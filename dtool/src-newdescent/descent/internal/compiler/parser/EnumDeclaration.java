package descent.internal.compiler.parser;

import java.math.BigInteger;

import melnorme.miscutil.tree.TreeVisitor;

import descent.core.compiler.IProblem;
import descent.core.domX.IASTVisitor;

public class EnumDeclaration extends ScopeDsymbol {

	private final static BigInteger N_2 = new BigInteger("2");
	private final static BigInteger N_128 = new BigInteger("128");
	private final static BigInteger N_256 = new BigInteger("256");
	private final static BigInteger N_0x8000 = new BigInteger("8000", 16);
	private final static BigInteger N_0x10000 = new BigInteger("10000", 16);
	private final static BigInteger N_0x80000000 = new BigInteger("80000000", 16);
	private final static BigInteger N_0x100000000 = new BigInteger("100000000",	16);
	private final static BigInteger N_0x8000000000000000 = new BigInteger("8000000000000000", 16);

	public Type type; // the TypeEnum
	public Type memtype; // type of the members
	BigInteger maxval;
	BigInteger minval;
	BigInteger defaultval; // default initializer

	public EnumDeclaration(Loc loc, IdentifierExp id, Type memtype) {
		super(loc, id);
		this.type = new TypeEnum(this);
		this.memtype = memtype;
		this.maxval = BigInteger.ZERO;
		this.minval = BigInteger.ZERO;
		this.defaultval = BigInteger.ZERO;
	}

	@Override
	public int getNodeType() {
		return ENUM_DECLARATION;
	}

	@Override
	public Type getType() {
		return type;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifiers);
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, memtype);
			TreeVisitor.acceptChildren(visitor, members);
		}
		visitor.endVisit(this);
	}

	@Override
	public EnumDeclaration isEnumDeclaration() {
		return this;
	}
	
	@Override
	public String kind() {
		return "enum";
	}

	@Override
	public boolean oneMember(Dsymbol[] ps) {
		if (isAnonymous()) {
			return super.oneMembers(members, ps);
		}
		return super.oneMember(ps);
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		BigInteger number;
		Type t;
		Scope sce;

		// EXTRA
		int errorStart, errorLength;
		if (ident == null) {
			// Use "enum" to mark errors
			errorStart = start;
			errorLength = 4;
		} else {
			// Use the name to mark errors
			errorStart = ident.start;
			errorLength = ident.length;
		}
		// EXTRA

		if (symtab != null) { // if already done
			return;
		}

		if (memtype == null) {
			memtype = Type.tint32;
		}

		parent = sc.scopesym;
		memtype = memtype.semantic(loc, sc, context);

		/*
		 * Check to see if memtype is forward referenced
		 */
		if (memtype.ty == TY.Tenum) {
			EnumDeclaration sym = (EnumDeclaration) memtype.toDsymbol(sc,
					context);
			if (sym.memtype == null) {
				context.acceptProblem(Problem.newSemanticTypeError(
						"Base enum is forward reference",
						IProblem.ForwardReference, 0, memtype.start,
						memtype.length));
				memtype = Type.tint32;
			}
		}

		if (!memtype.isintegral()) {
			context.acceptProblem(Problem.newSemanticTypeError(
					"Base type must be of integral type",
					IProblem.EnumBaseTypeMustBeOfIntegralType, 0,
					memtype.start, memtype.length));
			memtype = Type.tint32;
		}

		t = isAnonymous() ? memtype : type;
		symtab = new DsymbolTable();
		sce = sc.push(this);
		sce.parent = this;
		number = BigInteger.ZERO;
		if (members == null) { // enum ident;
			return;
		}

		if (members.size() == 0) {
			context.acceptProblem(Problem.newSemanticTypeError(
					"Enum must have at least one member",
					IProblem.EnumMustHaveAtLeastOneMember, 0, errorStart,
					errorLength));
		}

		boolean first = true;
		for (Dsymbol sym : members) {
			EnumMember em = sym.isEnumMember();
			Expression e;

			if (em == null) {
				/*
				 * The e.semantic(sce) can insert other symbols, such as
				 * template instances and function literals.
				 */
				continue;
			}

			e = em.value;
			if (e != null) {
				e = e.semantic(sce, context);
				e = e.optimize(ASTDmdNode.WANTvalue);
				// Need to copy it because we're going to change the type
				e = e.copy();
				e = e.implicitCastTo(sc, memtype, context);
				e = e.optimize(ASTDmdNode.WANTvalue);
				number = e.toInteger(context);
				e.type = t;
			} else { // Default is the previous number plus 1

				// Check for overflow
				if (!first) {
					switch (t.toBasetype(context).ty) {
					case Tbool:
						if (number.equals(N_2)) {
							context.acceptProblem(Problem.newSemanticTypeError(
									"Overflow of enum value",
									IProblem.EnumValueOverflow, 0,
									em.ident.start, em.ident.length));
						}
						break;

					case Tint8:
						if (number.equals(N_128)) {
							context.acceptProblem(Problem.newSemanticTypeError(
									"Overflow of enum value",
									IProblem.EnumValueOverflow, 0,
									em.ident.start, em.ident.length));
						}
						break;

					case Tchar:
					case Tuns8:
						if (number.equals(N_256)) {
							context.acceptProblem(Problem.newSemanticTypeError(
									"Overflow of enum value",
									IProblem.EnumValueOverflow, 0,
									em.ident.start, em.ident.length));
						}
						break;

					case Tint16:
						if (number.equals(N_0x8000)) {
							context.acceptProblem(Problem.newSemanticTypeError(
									"Overflow of enum value",
									IProblem.EnumValueOverflow, 0,
									em.ident.start, em.ident.length));
						}
						break;

					case Twchar:
					case Tuns16:
						if (number.equals(N_0x10000)) {
							context.acceptProblem(Problem.newSemanticTypeError(
									"Overflow of enum value",
									IProblem.EnumValueOverflow, 0,
									em.ident.start, em.ident.length));
						}
						break;

					case Tint32:
						if (number.equals(N_0x80000000)) {
							context.acceptProblem(Problem.newSemanticTypeError(
									"Overflow of enum value",
									IProblem.EnumValueOverflow, 0,
									em.ident.start, em.ident.length));
						}
						break;

					case Tdchar:
					case Tuns32:
						if (number.equals(N_0x100000000)) {
							context.acceptProblem(Problem.newSemanticTypeError(
									"Overflow of enum value",
									IProblem.EnumValueOverflow, 0,
									em.ident.start, em.ident.length));
						}
						break;

					case Tint64:
						if (number.equals(N_0x8000000000000000)) {
							context.acceptProblem(Problem.newSemanticTypeError(
									"Overflow of enum value",
									IProblem.EnumValueOverflow, 0,
									em.ident.start, em.ident.length));
						}
						break;

					case Tuns64:
						// TODO incorrect comparison in Java
						if (number.equals(BigInteger.ZERO)) {
							context.acceptProblem(Problem.newSemanticTypeError(
									"Overflow of enum value",
									IProblem.EnumValueOverflow, 0,
									em.ident.start, em.ident.length));
						}
						break;

					default:
						throw new IllegalStateException();
					}
				}
				e = new IntegerExp(em.loc, number.toString(), number, t);
				e.synthetic = true;
			}
			em.value = e;

			// Add to symbol table only after evaluating 'value'
			if (isAnonymous()) {
				for (Scope scx = sce.enclosing; scx != null; scx = scx.enclosing) {
					if (scx.scopesym != null) {
						if (scx.scopesym.symtab != null) {
							scx.scopesym.symtab = new DsymbolTable();
						}
						em.addMember(sce, scx.scopesym, 1, context);
						break;
					}
				}
			} else {
				em.addMember(sc, this, 1, context);
			}

			if (first) {
				first = false;
				defaultval = number;
				minval = number;
				maxval = number;
			} else if (memtype.isunsigned()) {
				if (number.compareTo(minval) < 0) {
					minval = number;
				}
				if (number.compareTo(maxval) > 0) {
					maxval = number;
				}
			} else {
				if (number.compareTo(minval) < 0) {
					minval = number;
				}
				if (number.compareTo(maxval) > 0) {
					maxval = number;
				}
			}

			number = number.add(BigInteger.ONE);
		}

		sce.pop();
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		Type t = null;
		if (memtype != null) {
			t = memtype.syntaxCopy();
		}

		EnumDeclaration ed;
		if (s != null) {
			ed = (EnumDeclaration) s;
			ed.memtype = t;
		} else {
			ed = new EnumDeclaration(loc, ident, t);
		}
		super.syntaxCopy(ed);
		return ed;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		int i;

		buf.writestring("enum ");
		if (ident != null) {
			buf.writestring(ident.toChars());
			buf.writeByte(' ');
		}
		if (memtype != null) {
			buf.writestring(": ");
			memtype.toCBuffer(buf, null, hgs);
		}
		if (members == null) {
			buf.writeByte(';');
			buf.writenl();
			return;
		}
		buf.writenl();
		buf.writeByte('{');
		buf.writenl();
		for (i = 0; i < members.size(); i++) {
			EnumMember em = (members.get(i)).isEnumMember();
			if (em == null) {
				continue;
			}
			em.toCBuffer(buf, hgs, context);
			buf.writeByte(',');
			buf.writenl();
		}
		buf.writeByte('}');
		buf.writenl();
	}

}
