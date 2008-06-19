package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.TY.Tfunction;

public class BaseClass extends ASTDmdNode {

	public Modifier modifier;
	public Type type;
	public Type sourceType;
	public PROT protection;
	public ClassDeclaration base;
	public int offset; // 'this' pointer offset
	public FuncDeclarations vtbl; // for interfaces: Array of
	// FuncDeclaration's
	// making up the vtbl[]

	public BaseClasses baseInterfaces; // if BaseClass is an interface,
										// these are a copy of the InterfaceDeclaration::interfaces
	
	public BaseClass() {
		
	}

	public BaseClass(Type type, Modifier modifier, PROT protection) {
		this.type = type;
		this.sourceType = type;
		this.modifier = modifier;
		this.protection = protection;
	}

	public BaseClass(Type type, PROT protection) {
		this.type = type;
		this.sourceType = type;
		this.protection = protection;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifier);
			TreeVisitor.acceptChildren(visitor, sourceType);
		}
		visitor.endVisit(this);
	}

	@Override
	public BaseClass clone() {
		BaseClass bc = new BaseClass(type, modifier, protection);
		bc.base = base;
		bc.offset = offset;
		bc.vtbl = vtbl;
		bc.baseInterfaces = baseInterfaces;
		return bc;
	}

	public void copyBaseInterfaces(BaseClasses vtblInterfaces) {
		baseInterfaces = new BaseClasses();
		baseInterfaces.memcpy(base.interfaces);

		for (int i = 0; i < size(base.interfaces); i++) {
			BaseClass b = baseInterfaces.get(i);
			BaseClass b2 = base.interfaces.get(i);

			if (size(b2.vtbl) != 0) {
				throw new IllegalStateException("assert(b2.vtbl.size() == 0)"); // should not be filled yet
			}
			b = b2.clone();

			if (i == 0) {
				vtblInterfaces.add(b); // only need for M.I.
			}
			b.copyBaseInterfaces(vtblInterfaces);
		}
	}

	public int fillVtbl(ClassDeclaration cd, Array vtbl, int newinstance,
			SemanticContext context) {
		ClassDeclaration id = base;
		int j;
		int result = 0;
		
		if (vtbl != null) {
			vtbl.setDim(base.vtbl.size());
		}

		// first entry is ClassInfo reference
		for (j = base.vtblOffset(); j < base.vtbl.size(); j++) {
			FuncDeclaration ifd = ((Dsymbol) base.vtbl.get(j))
					.isFuncDeclaration();
			FuncDeclaration fd;
			TypeFunction tf;

			if (ifd == null) {
				throw new IllegalStateException("assert(ifd);");
			}
			// Find corresponding function in this class
			tf = (ifd.type.ty == Tfunction) ? (TypeFunction) (ifd.type) : null;
			fd = cd.findFunc(ifd.ident, tf, context);
			if (fd != null && !fd.isAbstract()) {
				// Check that calling conventions match
				if (fd.linkage != ifd.linkage) {
					if (context.acceptsProblems()) {
						context.acceptProblem(Problem.newSemanticTypeError(
								IProblem.LinkageDoesNotMatchInterfaceFunction, this));
					}
				}

				// Check that it is current
				if (newinstance != 0 && fd.toParent() != cd
						&& ifd.toParent() == base) {
					if (context.acceptsProblems()) {
						context.acceptProblem(Problem.newSemanticTypeError(
								IProblem.InterfaceFunctionIsNotImplemented, this, id
										.toChars(context), ifd.ident.toChars()));
					}
				}

				if (fd.toParent() == cd) {
					result = 1;
				}
			} else {
				// BUG: should mark this class as abstract?
				if (!cd.isAbstract()) {
					if (context.acceptsProblems()) {
						context.acceptProblem(Problem.newSemanticTypeError(
								IProblem.InterfaceFunctionIsNotImplemented, this, id.toChars(context), ifd.ident.toChars()));
					}
				}
				fd = null;
			}
			if (vtbl != null) {
				vtbl.set(j, fd);
			}
		}

		return result;
	}

	@Override
	public int getNodeType() {
		return BASE_CLASS;
	}

	@Override
	public char[] toCharArray() {
		if (modifier == null) {
			return sourceType.toCharArray();
		} else {
			return super.toCharArray();
		}
	}

}
