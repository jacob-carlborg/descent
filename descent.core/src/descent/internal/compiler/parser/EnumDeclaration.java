package descent.internal.compiler.parser;

import java.math.BigInteger;

import descent.core.compiler.IProblem;

public class EnumDeclaration extends ScopeDsymbol {
	
	private final static BigInteger N_2 = new BigInteger("2");
	private final static BigInteger N_128 = new BigInteger("128");
	private final static BigInteger N_256 = new BigInteger("256");
	private final static BigInteger N_0x8000 = new BigInteger("8000", 16);
	private final static BigInteger N_0x10000 = new BigInteger("10000", 16);
	private final static BigInteger N_0x80000000 = new BigInteger("80000000", 16);
	private final static BigInteger N_0x100000000 = new BigInteger("100000000", 16);
	private final static BigInteger N_0x8000000000000000 = new BigInteger("8000000000000000", 16);	

	public Type type;			// the TypeEnum
	public Type memtype;		// type of the members
	BigInteger maxval;
	BigInteger minval;
	BigInteger defaultval;	// default initializer

	public EnumDeclaration(IdentifierExp id, Type memtype) {
		super(id);
		this.type = new TypeEnum(this);
		this.memtype = memtype;
		this.maxval = BigInteger.ZERO;
		this.minval = BigInteger.ZERO;
		this.defaultval = BigInteger.ZERO;
	}
	
	@Override
	public EnumDeclaration isEnumDeclaration() {
		return this;
	}
	
	@Override
	public Type getType() {
		return type;
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

		// printf("EnumDeclaration::semantic(sd = %p, '%s')\n", sc.scopesym,
		// sc.scopesym.toChars());
		if (symtab != null) { // if already done
			return;
		}

		if (memtype == null) {
			memtype = Type.tint32;
		}

		parent = sc.scopesym;
		memtype = memtype.semantic(sc, context);

		/*
		 * Check to see if memtype is forward referenced
		 */
		if (memtype.ty == TY.Tenum) {
			EnumDeclaration sym = (EnumDeclaration) memtype.toDsymbol(sc, context);
			if (sym.memtype == null) {
				context.acceptProblem(Problem
						.newSemanticTypeError(
								"Base enum is forward reference",
								IProblem.ForwardReference, 0,
								memtype.start, memtype.length));
				memtype = Type.tint32;
			}
		}

		if (!memtype.isintegral()) {
			context.acceptProblem(Problem
					.newSemanticTypeError(
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
			context.acceptProblem(Problem
					.newSemanticTypeError(
							"Enum must have at least one member",
							IProblem.EnumMustHaveAtLeastOneMember, 0,
							errorStart, errorLength));
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

			// printf("Enum member '%s'\n",em.toChars());
			e = em.value;
			if (e != null) {
				// assert(e.dyncast() == DYNCAST_EXPRESSION);
				e = e.semantic(sce, context);
				e = e.optimize(Expression.WANTvalue);
				// Need to copy it because we're going to change the type
				e = e.copy();
				e = e.implicitCastTo(sc, memtype);
				e = e.optimize(Expression.WANTvalue);
				number = e.toInteger(context);
				e.type = t;
			} else { // Default is the previous number plus 1

				// Check for overflow
				if (!first) {
					switch (t.toBasetype(context).ty) {
					case Tbool:
						if (number.equals(N_2)) {
							context.acceptProblem(Problem
									.newSemanticTypeError(
											"Overflow of enum value",
											IProblem.EnumValueOverflow, 0,
											em.ident.start, em.ident.length));
						}
						break;

					case Tint8:
						if (number.equals(N_128)) {
							context.acceptProblem(Problem
									.newSemanticTypeError(
											"Overflow of enum value",
											IProblem.EnumValueOverflow, 0,
											em.ident.start, em.ident.length));
						}
						break;

					case Tchar:
					case Tuns8:
						if (number.equals(N_256)) {
							context.acceptProblem(Problem
									.newSemanticTypeError(
											"Overflow of enum value",
											IProblem.EnumValueOverflow, 0,
											em.ident.start, em.ident.length));
						}
						break;

					case Tint16:
						if (number.equals(N_0x8000)) {
							context.acceptProblem(Problem
									.newSemanticTypeError(
											"Overflow of enum value",
											IProblem.EnumValueOverflow, 0,
											em.ident.start, em.ident.length));
						}
						break;

					case Twchar:
					case Tuns16:
						if (number.equals(N_0x10000)) {
							context.acceptProblem(Problem
									.newSemanticTypeError(
											"Overflow of enum value",
											IProblem.EnumValueOverflow, 0,
											em.ident.start, em.ident.length));
						}
						break;

					case Tint32:
						if (number.equals(N_0x80000000)) {
							context.acceptProblem(Problem
									.newSemanticTypeError(
											"Overflow of enum value",
											IProblem.EnumValueOverflow, 0,
											em.ident.start, em.ident.length));
						}
						break;

					case Tdchar:
					case Tuns32:
						if (number.equals(N_0x100000000)) {
							context.acceptProblem(Problem
									.newSemanticTypeError(
											"Overflow of enum value",
											IProblem.EnumValueOverflow, 0,
											em.ident.start, em.ident.length));
						}
						break;

					case Tint64:
						if (number.equals(N_0x8000000000000000)) {
							context.acceptProblem(Problem
									.newSemanticTypeError(
											"Overflow of enum value",
											IProblem.EnumValueOverflow, 0,
											em.ident.start, em.ident.length));
						}
						break;

					case Tuns64:
						// TODO incorrect comparison in Java
						if (number.equals(BigInteger.ZERO)) {
							context.acceptProblem(Problem
									.newSemanticTypeError(
											"Overflow of enum value",
											IProblem.EnumValueOverflow, 0,
											em.ident.start, em.ident.length));
						}
						break;

					default:
						throw new IllegalStateException();
					}
				}
				e = new IntegerExp(number.toString(), number, t);
				e.synthetic = true;
			}
			em.value = e;

			// Add to symbol table only after evaluating 'value'
			if (isAnonymous()) {
				// sce.enclosing.insert(em);
				for (Scope scx = sce.enclosing; scx != null; scx = scx.enclosing) {
					if (scx.scopesym != null) {
						if (scx.scopesym.symtab != null) {
							scx.scopesym.symtab = new DsymbolTable();
						}
						em.addMember(sce, scx.scopesym, 1, context);
						break;
					}
				}
			} else
				em.addMember(sc, this, 1, context);

			if (first) {
				first = false;
				defaultval = number;
				minval = number;
				maxval = number;
			} else if (memtype.isunsigned()) {
				if (number.compareTo(minval) < 0)
					minval = number;
				if (number.compareTo(maxval) > 0)
					maxval = number;
			} else {
				if (number.compareTo(minval) < 0)
					minval = number;
				if (number.compareTo(maxval) > 0)
					maxval = number;
			}
			
			number = number.add(BigInteger.ONE);
		}
		//printf("defaultval = %lld\n", defaultval);

		sce.pop();
		//members.print();
	}
	
	@Override
	public int getNodeType() {
		return ENUM_DECLARATION;
	}

}
