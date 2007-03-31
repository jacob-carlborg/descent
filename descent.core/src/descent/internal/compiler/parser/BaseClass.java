package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;
import static descent.internal.compiler.parser.TY.*;

import org.eclipse.core.runtime.Assert;

public class BaseClass extends ASTNode {

	public Modifier modifier;
	public Type type;
	public Type sourceType;
	public PROT protection;
	public ClassDeclaration base;
	public int offset; // 'this' pointer offset
	public List<FuncDeclaration> vtbl; // for interfaces: Array of
	// FuncDeclaration's
	// making up the vtbl[]

	public List<BaseClass> baseInterfaces; // if BaseClass is an interface,

	// these

	// are a copy of the InterfaceDeclaration::interfaces

	public BaseClass(Type type, Modifier modifier, PROT protection) {
		this.type = type;
		this.sourceType = type;
		this.modifier = modifier;
		this.protection = protection;
	}

	public BaseClass(Type type, PROT protection) {
		this.type = type;
		this.protection = protection;
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

	public void copyBaseInterfaces(List<BaseClass> vtblInterfaces) {
		baseInterfaces = new ArrayList<BaseClass>(base.interfaces.size());

		for (int i = 0; i < base.interfaces.size(); i++) {
			BaseClass b = baseInterfaces.get(i);
			BaseClass b2 = base.interfaces.get(i);

			Assert.isTrue(b2.vtbl.size() == 0); // should not be filled yet
			b = b2.clone();

			if (i == 0) {
				vtblInterfaces.add(b); // only need for M.I.
			}
			b.copyBaseInterfaces(vtblInterfaces);
		}
	}

	public int fillVtbl(ClassDeclaration cd, List vtbl, int newinstance,
			SemanticContext context) {
		ClassDeclaration id = base;
		int j;
		int result = 0;

		// first entry is ClassInfo reference
		for (j = base.vtblOffset(); j < base.vtbl.size(); j++) {
			FuncDeclaration ifd = ((Dsymbol) base.vtbl.get(j))
					.isFuncDeclaration();
			FuncDeclaration fd;
			TypeFunction tf;

			Assert.isNotNull(ifd);
			// Find corresponding function in this class
			tf = (ifd.type.ty == Tfunction) ? (TypeFunction) (ifd.type) : null;
			fd = cd.findFunc(ifd.ident, tf, context);
			if (fd != null && !fd.isAbstract()) {
				// Check that calling conventions match
				if (fd.linkage != ifd.linkage) {
					fd.error("linkage doesn't match interface function");
				}

				// Check that it is current
				if (newinstance != 0 && fd.toParent() != cd
						&& ifd.toParent() == base) {
					cd.error("interface function %s.%s is not implemented", id
							.toChars(), ifd.ident.toChars());
				}

				if (fd.toParent() == cd) {
					result = 1;
				}
			} else {
				// BUG: should mark this class as abstract?
				if (!cd.isAbstract()) {
					cd.error("interface function %s.%s isn't implemented", id
							.toChars(), ifd.ident.toChars());
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

}
