package descent.internal.compiler.lookup;

import descent.core.Flags;
import descent.core.IField;
import descent.core.IJavaElement;
import descent.core.JavaModelException;
import descent.internal.compiler.parser.IDeclaration;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.PROT;
import descent.internal.compiler.parser.STC;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.SemanticMixin;
import descent.internal.compiler.parser.Type;
import descent.internal.core.util.Util;

public class RDeclaration extends RDsymbol implements IDeclaration {
	
	private int storage_class = -1;

	public RDeclaration(IJavaElement element, SemanticContext context) {
		super(element, context);
	}
	
	public boolean isAbstract() {
		return SemanticMixin.isAbstract(this);
	}
	
	public boolean isAuto() {
		return SemanticMixin.isAuto(this);
	}

	public boolean isConst() {
		return SemanticMixin.isConst(this);
	}

	public boolean isCtorinit() {
		return SemanticMixin.isCtorinit(this);
	}

	public boolean isDataseg(SemanticContext context) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isFinal() {
		return SemanticMixin.isFinal(this);
	}

	public boolean isOut() {
		return SemanticMixin.isOut(this);
	}

	public boolean isParameter() {
		return SemanticMixin.isParameter(this);
	}

	public boolean isRef() {
		return SemanticMixin.isRef(this);
	}

	public boolean isScope() {
		return SemanticMixin.isScope(this);
	}

	public boolean isStatic() {
		return SemanticMixin.isStatic(this);
	}

	public LINK linkage() {
		// TODO Auto-generated method stub
		return null;
	}

	public void linkage(LINK linkage) {
		throw new IllegalStateException("Should not be called");
	}

	public PROT protection() {
		return prot();
	}

	public void protection(PROT protection) {
		throw new IllegalStateException("Should not be called");
	}

	public int storage_class() {
		if (storage_class < 0) {
			int flags = getFlags();
			storage_class = 0;
			
			if ((flags & Flags.AccAbstract) != 0) storage_class |= STC.STCabstract;
			if ((flags & Flags.AccAuto) != 0) storage_class |= STC.STCauto;
			// TODO STC.STCcomdat
			if ((flags & Flags.AccConst) != 0) storage_class |= STC.STCconst;
			// TODO STC.STCctorinit
			if ((flags & Flags.AccDeprecated) != 0) storage_class |= STC.STCdeprecated;
			if ((flags & Flags.AccExtern) != 0) storage_class |= STC.STCextern;
			// TODO STC.STCfield
			if ((flags & Flags.AccFinal) != 0) storage_class |= STC.STCfinal;
			// TODO STC.STCforeach
			if ((flags & Flags.AccIn) != 0) storage_class |= STC.STCin;
			if ((flags & Flags.AccInvariant) != 0) storage_class |= STC.STCinvariant;
			if ((flags & Flags.AccLazy) != 0) storage_class |= STC.STClazy;
			if ((flags & Flags.AccOut) != 0) storage_class |= STC.STCout;
			if ((flags & Flags.AccOverride) != 0) storage_class |= STC.STCoverride;
			// TODO STC.STCparameter
			if ((flags & Flags.AccRef) != 0) storage_class |= STC.STCref;
			if ((flags & Flags.AccScope) != 0) storage_class |= STC.STCscope;
			if ((flags & Flags.AccStatic) != 0) storage_class |= STC.STCstatic;
			if ((flags & Flags.AccSynchronized) != 0) storage_class |= STC.STCsynchronized;
			// TODO STC.STCtemplateparameter
			// TODO STC.STCundefined
			// TODO STC.STCvariadic
		}
		return storage_class;
	}

	public void storage_class(int storage_class) {
		throw new IllegalStateException("Should not be called");
	}

	public Type type() {
		return null;
	}
	
	public void type(Type type) {
		throw new IllegalStateException("Should not be called");
	}
	
	@Override
	public IDeclaration isDeclaration() {
		return this;
	}
	
	protected Type getTypeFromField() {
		try {
			if (element.getElementType() == IJavaElement.FIELD) {
				IField f = (IField) element;
				return getTypeFromSignature(f.getTypeSignature());
			} else {
				return null;
			}
		} catch (JavaModelException e) {
			Util.log(e);
			return Type.tint32;
		}
	}
	
	protected int getFlags() {
		try {
			if (element.getElementType() == IJavaElement.FIELD) {
				IField f = (IField) element;
				return f.getFlags();
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
		return 0;
	}

}
