package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.LINK.LINKd;
import static descent.internal.compiler.parser.PROT.PROTnone;
import static descent.internal.compiler.parser.STC.STCconst;
import static descent.internal.compiler.parser.STC.STCin;
import static descent.internal.compiler.parser.STC.STCinvariant;
import static descent.internal.compiler.parser.STC.STCnodtor;
import static descent.internal.compiler.parser.STC.STCref;
import static descent.internal.compiler.parser.STC.STCundefined;
import static descent.internal.compiler.parser.TOK.TOKblit;
import static descent.internal.compiler.parser.TY.Tsarray;
import static descent.internal.compiler.parser.TY.Tstruct;

import java.util.ArrayList;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.core.Flags;
import descent.core.Signature;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;


public class StructDeclaration extends AggregateDeclaration {

	public boolean zeroInit; // !=0 if initialize with 0 fill
	public int hasIdentityAssign;	// !=0 if has identity opAssign
	public FuncDeclaration cpctor;	// generated copy-constructor, if any
	public CtorDeclaration ctor;

	public FuncDeclarations postblits;	// Array of postblit functions
	public FuncDeclaration postblit;	// aggregate postblit

	public StructDeclaration(Loc loc, IdentifierExp id) {
		super(loc, id);
		this.type = new TypeStruct(this);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifiers);
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, members);
			
//			acceptSynthetic(visitor);
		}
		visitor.endVisit(this);
	}

	@Override
	public PROT getAccess(Dsymbol smember) {
		PROT access_ret = PROTnone;

		Dsymbol p = smember.toParent();
		if (p != null && p.isAggregateDeclaration() != null && SemanticMixin.equals(p.isAggregateDeclaration(), this)) {
			access_ret = smember.prot();
		} else if (smember.isDeclaration().isStatic()) {
			access_ret = smember.prot();
		}
		return access_ret;
	}

	@Override
	public int getNodeType() {
		return STRUCT_DECLARATION;
	}

	@Override
	public StructDeclaration isStructDeclaration() {
		return this;
	}

	@Override
	public String kind() {
		return "struct";
	}

	@Override
	public String mangle(SemanticContext context) {
		return Dsymbol_mangle(context);
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		Scope sc2;

		Assert.isNotNull(type);
		if (members == null) { // if forward reference
			return;
		}

		if (symtab != null) {
			if (scope == null) {
				return; // semantic() already completed
			}
		} else {
			symtab = new DsymbolTable();
		}

		Scope scx = null;
		if (scope != null) {
			sc = scope;
			scx = scope; // save so we don't make redundant copies
			scope = null;
		}

		parent = sc.parent;
		
		if (context.STRUCTTHISREF()) {
			handle = type;
		} else {
			handle = type.pointerTo(context);
		}
		structalign = sc.structalign;
		protection = sc.protection;
		if ((sc.stc & STC.STCdeprecated) != 0) {
			isdeprecated = true;
		}
		
		if (context.isD2()) {
			 storage_class |= sc.stc;
		}
		
		assert (!isAnonymous());
		if ((sc.stc & STC.STCabstract) != 0) {
			if (isUnionDeclaration() != null) {
				if (context.acceptsErrors()) {
					context.acceptProblem(Problem.newSemanticTypeErrorLoc(
							IProblem.UnionsCannotBeAbstract, this));
				}
			} else {
				if (context.acceptsErrors()) {
					context.acceptProblem(Problem.newSemanticTypeErrorLoc(
							IProblem.StructsCannotBeAbstract, this));
				}
			}
		}
		
		if (context.isD2()) {
			if ((storage_class & STCinvariant) != 0) {
		        type = type.invariantOf(context);
			} else if ((storage_class & STCconst) != 0) {
		        type = type.constOf(context);
			}
		}

		if (sizeok == 0) { // if not already done the addMember step
			for (Dsymbol s : members) {
				s.addMember(sc, this, 1, context);
			}
		}

		sizeok = 0;
		sc2 = sc.push(this);
		if (context.isD2()) {
			sc2.stc &= storage_class & (STCconst | STCinvariant);
		} else {
			sc2.stc = 0;
		}
		sc2.parent = this;
		if (isUnionDeclaration() != null) {
			sc2.inunion = true;
		}
		sc2.protection = PROT.PROTpublic;
		sc2.explicitProtection = 0;
		
		semanticScope(sc2);

		int members_dim = members.size();
		for (int i = 0; i < members_dim; i++) {
			Dsymbol s = members.get(i);
			s.semantic(sc2, context);
			if (isUnionDeclaration() != null) {
				sc2.offset = 0;
			}
		}

		/* The TypeInfo_Struct is expecting an opEquals and opCmp with
		 * a parameter that is a pointer to the struct. But if there
		 * isn't one, but is an opEquals or opCmp with a value, write
		 * another that is a shell around the value:
		 *	int opCmp(struct *p) { return opCmp(*p); }
		 */

		TypeFunction tfeqptr;
		{
			Arguments arguments = new Arguments();
			Argument arg = new Argument(STCin, handle, new IdentifierExp(loc,
					Id.p), null);

			arguments.add(arg);
			tfeqptr = new TypeFunction(arguments, Type.tint32, 0, LINK.LINKd);
			tfeqptr = (TypeFunction) tfeqptr.semantic(loc, sc, context);
		}

		TypeFunction tfeq;
		{
			Arguments arguments = new Arguments();
			Argument arg = new Argument(STCin, type, null, null);

			arguments.add(arg);
			tfeq = new TypeFunction(arguments, Type.tint32, 0, LINK.LINKd);
			tfeq = (TypeFunction) tfeq.semantic(loc, sc, context);
		}

		char[] id = Id.eq;
		for (int j = 0; j < 2; j++) {
			Dsymbol s = ASTDmdNode.search_function(this, id, context);
			FuncDeclaration fdx = s != null ? s.isFuncDeclaration() : null;
			if (fdx != null) {
				FuncDeclaration fd = fdx.overloadExactMatch(tfeqptr, context);
				if (fd == null) {
					fd = fdx.overloadExactMatch(tfeq, context);
					if (fd != null) { // Create the thunk, fdptr
						FuncDeclaration fdptr = new FuncDeclaration(loc, 
								fdx.ident, STC.STCundefined, tfeqptr);
						Expression e = new IdentifierExp(loc, Id.p);
						e = new PtrExp(loc, e);
						Expressions args = new Expressions();
						args.add(e);
						e = new IdentifierExp(loc, id);
						e = new CallExp(loc, e, args);
						fdptr.fbody = new ReturnStatement(loc, e);
						ScopeDsymbol s2 = fdx.parent.isScopeDsymbol();
						Assert.isNotNull(s2);
						s2.members.add(fdptr);
						fdptr.addMember(sc, s2, 1, context);
						fdptr.semantic(sc2, context);
					}
				}
			}

			id = Id.cmp;
		}
		
		if (context.isD2()) {
			dtor = buildDtor(sc2, context);
			postblit = buildPostBlit(sc2, context);
			cpctor = buildCpCtor(sc2, context);
			buildOpAssign(sc2, context);
		}

		sc2.pop();

		if (sizeok == 2) { // semantic() failed because of forward references.
			// Unwind what we did, and defer it for later
			fields = new ArrayList<VarDeclaration>(0);
			structsize = 0;
			alignsize = 0;
			structalign = 0;

			scope = scx != null ? scx : new Scope(sc, context);
			scope.setNoFree();
			scope.module.addDeferredSemantic(this, context);
			return;
		}

		// 0 sized struct's are set to 1 byte
		if (structsize == 0) {
			structsize = 1;
			alignsize = 1;
		}

		// Round struct size up to next alignsize boundary.
		// This will ensure that arrays of structs will get their internals
		// aligned properly.
		structsize = (structsize + alignsize - 1) & ~(alignsize - 1);

		sizeok = 1;

		context.Module_dprogress++;

		// Determine if struct is all zeros or not
		zeroInit = true;
		for (int j = 0; j < this.fields.size(); j++) {
			Dsymbol s = this.fields.get(j);
			VarDeclaration vd = s.isVarDeclaration();
			if (vd != null && !vd.isDataseg(context)) {
				if (vd.init() != null) {
					// Should examine init to see if it is really all 0's
					zeroInit = true;
					break;
				} else {
					if (!vd.type.isZeroInit(loc, context)) {
						zeroInit = false;
						break;
					}
				}
			}
		}

		/* Look for special member functions.
		 */
		if (context.isD2()) {
		    ctor = (CtorDeclaration)search(Loc.ZERO, Id.ctor, 0, context);
		}
		inv = (InvariantDeclaration) search(loc, Id.classInvariant, 0, context);
		aggNew((NewDeclaration) search(loc, Id.classNew, 0, context));
		aggDelete((DeleteDeclaration) search(loc, Id.classDelete, 0, context));

		if (sc.func != null) {
			semantic2(sc, context);
			semantic3(sc, context);
		}
	}
	
	public FuncDeclaration buildPostBlit(Scope sc, SemanticContext context) {
		Expression e = null;

		for (int i = 0; i < size(fields); i++) {
			Dsymbol s = (Dsymbol) fields.get(i);
			VarDeclaration v = s.isVarDeclaration();
			if ((v.storage_class & STCref) != 0)
			    continue;
			Type tv = v.type.toBasetype(context);
			int dim = 1;
			while (tv.ty == Tsarray) {
//				TypeSArray ta = (TypeSArray) tv;
				dim *= ((TypeSArray) tv).dim.toInteger(context).intValue();
				tv = tv.nextOf().toBasetype(context);
			}
			if (tv.ty == Tstruct) {
				TypeStruct ts = (TypeStruct) tv;
				StructDeclaration sd = ts.sym;
				if (sd.postblit != null) {
					Expression ex;

					// this.v
					ex = new ThisExp(Loc.ZERO);
					ex = new DotVarExp(Loc.ZERO, ex, v, false);

					if (dim == 1) { // this.v.dtor()
						ex = new DotVarExp(Loc.ZERO, ex, sd.postblit, false);
						ex = new CallExp(Loc.ZERO, ex);
					} else {
						// Typeinfo.postblit(cast(void*)&this.v);
						Expression ea = new AddrExp(Loc.ZERO, ex);
						ea = new CastExp(Loc.ZERO, ea, Type.tvoid
								.pointerTo(context));

						Expression et = v.type.getTypeInfo(sc, context);
						et = new DotIdExp(Loc.ZERO, et, Id.postblit);

						ex = new CallExp(Loc.ZERO, et, ea);
					}
					e = Expression.combine(e, ex); // combine in forward order
				}
			}
		}

		/*
		 * Build our own "postblit" which executes e
		 */
		if (e != null) {
			PostBlitDeclaration dd = new PostBlitDeclaration(Loc.ZERO,
					new IdentifierExp(Id.__fieldPostBlit));
			dd.fbody = new ExpStatement(Loc.ZERO, e);
			if (dtors == null) {
				dtors = new FuncDeclarations();
			}
			dtors.add(dd);
			if (members == null) {
				members = new Dsymbols();
			}
			members.add(dd);
			dd.semantic(sc, context);
		}

		switch (size(postblits)) {
		case 0:
			return null;

		case 1:
			return (FuncDeclaration) postblits.get(0);

		default:
			e = null;
			for (int i = 0; i < size(postblits); i++) {
				FuncDeclaration fd = (FuncDeclaration) postblits.get(i);
				Expression ex = new ThisExp(Loc.ZERO);
				ex = new DotVarExp(Loc.ZERO, ex, fd, false);
				ex = new CallExp(Loc.ZERO, ex);
				e = Expression.combine(e, ex);
			}
			PostBlitDeclaration dd = new PostBlitDeclaration(Loc.ZERO,
					new IdentifierExp(Id.__aggrPostBlit));
			dd.fbody = new ExpStatement(Loc.ZERO, e);
			if (members == null) {
				members = new Dsymbols();
			}
			members.add(dd);
			dd.semantic(sc, context);
			return dd;
		}
	}
	
	public FuncDeclaration buildCpCtor(Scope sc, SemanticContext context) {
		FuncDeclaration fcp = null;

		/*
		 * Copy constructor is only necessary if there is a postblit function,
		 * otherwise the code generator will just do a bit copy.
		 */
		if (postblit != null) {
			Argument param = new Argument(STCref, type,
					new IdentifierExp(Id.p), null);
			Arguments fparams = new Arguments();
			fparams.add(param);
			Type ftype = new TypeFunction(fparams, Type.tvoid, 0, LINKd);

			fcp = new FuncDeclaration(Loc.ZERO, new IdentifierExp(Id.cpctor), STCundefined, ftype);

			// Build *this = p;
			Expression e = new ThisExp(Loc.ZERO);
			if (!context.STRUCTTHISREF()) {
				e = new PtrExp(Loc.ZERO, e);
			}
			AssignExp ea = new AssignExp(Loc.ZERO, e, new IdentifierExp(Id.p));
			ea.op = TOKblit;
			Statement s = new ExpStatement(Loc.ZERO, ea);

			// Build postBlit();
			e = new VarExp(Loc.ZERO, postblit, false);
			e = new CallExp(Loc.ZERO, e);

			s = new CompoundStatement(Loc.ZERO, s,
					new ExpStatement(Loc.ZERO, e));
			fcp.fbody = s;

			if (members == null) {
				members = new Dsymbols();
			}
			members.add(fcp);

			sc = sc.push();
			sc.stc = 0;
			sc.linkage = LINKd;

			fcp.semantic(sc, context);

			sc.pop();
		}

		return fcp;
	}
	
	public FuncDeclaration buildOpAssign(Scope sc, SemanticContext context) {
		if (!needOpAssign(context)) {
			return null;
		}

		FuncDeclaration fop = null;

		Argument param = new Argument(STCnodtor, type, new IdentifierExp(Id.p),
				null);
		Arguments fparams = new Arguments();
		fparams.add(param);
		Type ftype = new TypeFunction(fparams, handle, 0, LINKd);

		fop = new FuncDeclaration(Loc.ZERO, new IdentifierExp(Id.assign),
				STCundefined, ftype);

		Expression e = null;
		if (postblit != null) {
			/*
			 * Swap: tmp =this;this = s; tmp.dtor();
			 */
			IdentifierExp idtmp = context.uniqueId("__tmp");
			VarDeclaration tmp = null;
			AssignExp ec = null;
			if (dtor != null) {
				tmp = new VarDeclaration(Loc.ZERO, type, idtmp,
						new VoidInitializer(Loc.ZERO));
				tmp.noauto = true;
				e = new DeclarationExp(Loc.ZERO, tmp);
				ec = new AssignExp(Loc.ZERO, new VarExp(Loc.ZERO, tmp),
						context.STRUCTTHISREF() ?
							new ThisExp(Loc.ZERO) :
							new PtrExp(Loc.ZERO, new ThisExp(Loc.ZERO)));
				ec.op = TOKblit;
				e = Expression.combine(e, ec);
			}
			ec = new AssignExp(Loc.ZERO, 
					context.STRUCTTHISREF() ?
						new ThisExp(Loc.ZERO) :
						new PtrExp(Loc.ZERO, new ThisExp(Loc.ZERO)), 
					new IdentifierExp(Id.p));
			ec.op = TOKblit;
			e = Expression.combine(e, ec);
			if (dtor != null) {
				/*
				 * Instead of running the destructor on s, run it on tmp. This
				 * avoids needing to copy tmp back in to s.
				 */
				Expression ec2 = new DotVarExp(Loc.ZERO, new VarExp(Loc.ZERO,
						tmp), dtor, false);
				ec2 = new CallExp(Loc.ZERO, ec2);
				e = Expression.combine(e, ec2);
			}
		} else {
			/*
			 * Do memberwise copy
			 */
			for (int i = 0; i < size(fields); i++) {
				Dsymbol s = (Dsymbol) fields.get(i);
				VarDeclaration v = s.isVarDeclaration();
				// this.v = s.v;
				AssignExp ec = new AssignExp(Loc.ZERO, new DotVarExp(Loc.ZERO,
						new ThisExp(Loc.ZERO), v, false), new DotVarExp(Loc.ZERO,
						new IdentifierExp(Id.p), v, false));
				ec.op = TOKblit;
				e = Expression.combine(e, ec);
			}
		}
		Statement s1 = new ExpStatement(Loc.ZERO, e);

		/*
		 * Add: return this;
		 */
		e = new ThisExp(Loc.ZERO);
		Statement s2 = new ReturnStatement(Loc.ZERO, e);

		fop.fbody = new CompoundStatement(Loc.ZERO, s1, s2);

		if (members == null) {
			members = new Dsymbols();
		}
		members.add(fop);
		fop.addMember(sc, this, 1, context);

		sc = sc.push();
		sc.stc = 0;
		sc.linkage = LINKd;

		fop.semantic(sc, context);

		sc.pop();

		return fop;
	}
	
	public boolean needOpAssign(SemanticContext context) {
		if (hasIdentityAssign != 0) {
			// goto Ldontneed;
			return false;
		}

		if (dtor != null || postblit != null) {
			// goto Lneed;
			return true;
		}

		/*
		 * If any of the fields need an opAssign, then we need it too.
		 */
		for (int i = 0; i < size(fields); i++) {
			Dsymbol s = (Dsymbol) fields.get(i);
			VarDeclaration v = s.isVarDeclaration();
			if ((v.storage_class & STCref) != 0)
			    continue;
			Type tv = v.type.toBasetype(context);
			while (tv.ty == Tsarray) {
				// TypeSArray ta = (TypeSArray) tv;
				tv = tv.nextOf().toBasetype(context);
			}
			if (tv.ty == Tstruct) {
				TypeStruct ts = (TypeStruct) tv;
				StructDeclaration sd = ts.sym;
				if (sd.needOpAssign(context)) {
					// goto Lneed;
					return true;
				}
			}
		}
		// Ldontneed:
		return false;

		// Lneed:
		// return 1;
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s, SemanticContext context) {
		StructDeclaration sd;

		if (s != null) {
			sd = (StructDeclaration) s;
		} else {
			sd = context.newStructDeclaration(loc, ident);
		}
		super.syntaxCopy(sd, context);
		
		sd.copySourceRange(this);
		sd.javaElement = javaElement;
		sd.templated = templated;
		
		return sd;		
	}
	
	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		int i;

		buf.writestring(kind());
		if (!isAnonymous()) {
			buf.writestring(toChars(context));
		}
		if (null == members) {
			buf.writeByte(';');
			buf.writenl();
			return;
		}
		buf.writenl();
		buf.writeByte('{');
		buf.writenl();
		for (i = 0; i < members.size(); i++) {
			Dsymbol s = members.get(i);

			buf.writestring("    ");
			s.toCBuffer(buf, hgs, context);
		}
		buf.writeByte('}');
		buf.writenl();
	}
	
	@Override
	public int getErrorStart() {
		if (ident != null) {
			return ident.start;
		}
		return start;
	}
	
	@Override
	public int getErrorLength() {
		if (ident != null) {
			return ident.length;
		}
		return 6; // "struct".length()
	}
	
	public char getSignaturePrefix() {
		if (templated) {
			return Signature.C_TEMPLATED_STRUCT;
		} else {
			return Signature.C_STRUCT;
		}
	}
	
	@Override
	public long getFlags() {
		return super.getFlags() | Flags.AccStruct;
	}
	
	@Override
	public StructDeclaration unlazy(char[] prefix, SemanticContext context) {
		return this;
	}

}
