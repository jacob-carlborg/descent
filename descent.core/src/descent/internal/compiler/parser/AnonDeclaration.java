package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.STC.STCauto;
import static descent.internal.compiler.parser.STC.STCscope;
import static descent.internal.compiler.parser.STC.STCstatic;

// DMD 1.020
public class AnonDeclaration extends AttribDeclaration {

	public boolean isunion;
	public Scope scope; // !=NULL means context to use
	public int sem; // 1 if successful semantic()

	public AnonDeclaration(Loc loc, boolean isunion, Dsymbols decl) {
		super(loc, decl);
		this.isunion = isunion;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, decl);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return ANON_DECLARATION;
	}

	@Override
	public AttribDeclaration isAttribDeclaration() {
		return this;
	}

	@Override
	public String kind() {
		return isunion ? "anonymous union" : "anonymous struct";
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		Scope scx = null;
		if (scope != null) {
			sc = scope;
			scx = scope;
			scope = null;
		}

		Assert.isNotNull(sc.parent);

		Dsymbol parent = sc.parent.pastMixin();
		AggregateDeclaration ad = parent.isAggregateDeclaration();

		if (ad == null
				|| (ad.isStructDeclaration() == null && ad.isClassDeclaration() == null)) {
			if (isunion) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.AnonCanOnlyBePartOfAnAggregate, 0, start,
						"union".length()));
			} else {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.AnonCanOnlyBePartOfAnAggregate, 0, start,
						"struct".length()));
			}
			return;
		}

		if (decl != null) {
			AnonymousAggregateDeclaration aad = new AnonymousAggregateDeclaration(
					loc);
			boolean adisunion;

			if (sc.anonAgg != null) {
				ad = sc.anonAgg;
				adisunion = sc.inunion;
			} else {
				adisunion = ad.isUnionDeclaration() != null;
			}

			sc = sc.push();
			sc.stc &= ~(STCauto | STCscope | STCstatic);
			sc.inunion = isunion;
			sc.offset = 0;
			sc.flags = 0;
			aad.structalign = sc.structalign;
			aad.parent = ad;

			for (int i = 0; i < decl.size(); i++) {
				Dsymbol s = decl.get(i);

				s.semantic(sc, context);
				if (isunion) {
					sc.offset = 0;
				}
				if (aad.sizeok == 2) {
					break;
				}
			}
			sc = sc.pop();

			// If failed due to forward references, unwind and try again later
			if (aad.sizeok == 2) {
				ad.sizeok = 2;
				if (sc.anonAgg == null) {
					scope = scx != null ? scx : new Scope(sc, context);
					scope.setNoFree();
					scope.module.addDeferredSemantic(this);
				}
				return;
			}
			if (sem == 0) {
				context.dprogress++;
				sem = 1;
			} else {
				;
			}

			// 0 sized structs are set to 1 byte
			if (aad.structsize == 0) {
				aad.structsize = 1;
				aad.alignsize = 1;
			}

			// Align size of anonymous aggregate
			int[] sc_offset = { sc.offset };
			ad.alignmember(aad.structalign, aad.alignsize, sc_offset);
			sc.offset = sc_offset[0];
			// ad.structsize = sc.offset;

			// Add members of aad to ad
			for (int i = 0; i < aad.fields.size(); i++) {
				VarDeclaration v = aad.fields.get(i);

				v.offset += sc.offset;
				ad.fields.add(v);
			}

			// Add size of aad to ad
			if (adisunion) {
				if (aad.structsize > ad.structsize) {
					ad.structsize = aad.structsize;
				}
				sc.offset = 0;
			} else {
				ad.structsize = sc.offset + aad.structsize;
				sc.offset = ad.structsize;
			}

			if (ad.alignsize < aad.alignsize) {
				ad.alignsize = aad.alignsize;
			}

			sc.anonAgg = aad;
		}
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		AnonDeclaration ad;

		Assert.isTrue(s == null);
		ad = new AnonDeclaration(loc, isunion, Dsymbol.arraySyntaxCopy(decl));
		return ad;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring(isunion ? "union" : "struct");
		buf.writestring("\n{\n");
		if (decl != null) {
			for (Dsymbol s : decl) {
				s.toCBuffer(buf, hgs, context);
			}
		}
		buf.writestring("}\n");
	}

}
