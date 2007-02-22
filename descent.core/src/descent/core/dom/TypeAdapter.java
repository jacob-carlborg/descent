package descent.core.dom;

import descent.internal.core.parser.IDmdType;
import descent.internal.core.parser.TY;

/**
 * Adapts an object to an IDmdType.
 * @see IDmdType
 */
public abstract class TypeAdapter {
	
	// TODO implement toExpression() for TypeSArray (now StaticArrayType) and TypeIdentifier
	// (this can be SimpleType, QualifiedType or TemplateType)
	
	public final static IDmdType getAdapter(Object object) {
		if (object == null) return null;
		
		if (object instanceof IDmdType) {
			return (IDmdType) object;
		}
		
		if (object instanceof PrimitiveType) {
			return getAdapter((PrimitiveType) object);
		}
		
		if (object instanceof PointerType) {
			return getAdapter((PointerType) object);
		}
		
		if (object instanceof StaticArrayType) {
			return getAdapter((StaticArrayType) object);
		}
		
		if (object instanceof DynamicArrayType) {
			return getAdapter((DynamicArrayType) object);
		}
		
		if (object instanceof AssociativeArrayType) {
			return getAdapter((AssociativeArrayType) object);
		}
		
		if (object instanceof SliceType) {
			return getAdapter((SliceType) object);
		}
		
		if (object instanceof DelegateType) {
			return getAdapter((DelegateType) object);
		}
		
		if (object instanceof SimpleType 
				|| object instanceof QualifiedType 
				|| object instanceof TemplateType) {
			return getAdapterForTident((Type) object);
		}
		
		if (object instanceof TypeofType) {
			return getAdapter((TypeofType) object);
		}
		
		throw new RuntimeException("Can't adapt " + object + " to ITypeWithNextField");
	}
	
	public final static IDmdType getAdapter(final PrimitiveType type) {
		return new IDmdType() {
			public Object getAdaptedType() {
				return type;
			}
			public Type getNext() {
				return null;
			}
			public void setNext(Type dmdType) {
			}
			public Expression toExpression() {
				return null;
			}
			public TY getTY() {
				switch(type.getPrimitiveTypeCode()) {
				case BIT: return TY.Tbit;
				case BOOL: return TY.Tbool;
				case BYTE: return TY.Tint8;
				case CHAR: return TY.Tchar;
				case COMPLEX32: return TY.Tcomplex32;
				case COMPLEX64: return TY.Tcomplex64;
				case COMPLEX80: return TY.Tcomplex80;
				case DCHAR: return TY.Tdchar;
				case DOUBLE: return TY.Tfloat64;
				case FLOAT: return TY.Tfloat32;
				case IDOUBLE: return TY.Timaginary64;
				case IFLOAT: return TY.Timaginary32;
				case INT: return TY.Tint32;
				case IREAL: return TY.Timaginary80;
				case LONG: return TY.Tint64;
				case REAL: return TY.Tfloat80;
				case SHORT: return TY.Tint16;
				case UBYTE: return TY.Tuns8;
				case UINT: return TY.Tuns32;
				case ULONG: return TY.Tuns64;
				case USHORT: return TY.Tuns16;
				case VOID: return TY.Tvoid;
				case WCHAR: return TY.Twchar;
				default: throw new RuntimeException("Can't happen");
				}
			}
		};
	}
	
	private final static IDmdType getAdapterForTident(final Type type) {
		return new IDmdType() {
			public Object getAdaptedType() {
				return type;
			}
			public Type getNext() {
				return null;
			}
			public void setNext(Type dmdType) {
			}
			public Expression toExpression() {
				return null;
			}
			public TY getTY() {
				return TY.Tident;
			}
		};
	}
	
	public final static IDmdType getAdapter(final PointerType type) {
		return new IDmdType() {
			public Object getAdaptedType() {
				return type;
			}
			public Type getNext() {
				return type.getComponentType();
			}
			public void setNext(Type dmdType) {
				// Must preserve the parent of the old assigned type
				Type oldType = type.getComponentType();
				ASTNode parent = null;
				StructuralPropertyDescriptor locationInParent = null;
				if (oldType != null) {
					parent = oldType.getParent();
					locationInParent = oldType.getLocationInParent();
				}
				type.setComponentType(dmdType);
				if (oldType != null) {
					oldType.setParent(parent, locationInParent);
				}
			}
			public Expression toExpression() {
				return null;
			}
			public TY getTY() {
				return TY.Tpointer;
			}
		};
	}
	
	public final static IDmdType getAdapter(final TypeofType type) {
		return new IDmdType() {
			public Object getAdaptedType() {
				return type;
			}
			public Type getNext() {
				return null;
			}
			public void setNext(Type dmdType) {
				
			}
			public Expression toExpression() {
				return null;
			}
			public TY getTY() {
				return TY.Ttypeof;
			}
		};
	}
	
	public final static IDmdType getAdapter(final StaticArrayType type) {
		return getAdapter(type, TY.Tsarray);
	}
	
	public final static IDmdType getAdapter(final DynamicArrayType type) {
		return getAdapter(type, TY.Tarray);
	}
	
	public final static IDmdType getAdapter(final AssociativeArrayType type) {
		return getAdapter(type, TY.Taarray);
	}
	
	public final static IDmdType getAdapter(final SliceType type) {
		return getAdapter(type, TY.Tslice);
	}
	
	public final static IDmdType getAdapter(final ArrayType type, final TY ty) {
		return new IDmdType() {
			public Object getAdaptedType() {
				return type;
			}
			public Type getNext() {
				return type.getComponentType();
			}
			public void setNext(Type dmdType) {
				// Must preserve the parent of the old assigned type
				Type oldType = type.getComponentType();
				ASTNode parent = null;
				StructuralPropertyDescriptor locationInParent = null;
				if (oldType != null) {
					parent = oldType.getParent();
					locationInParent = oldType.getLocationInParent();
				}
				type.setComponentType(dmdType);
				if (oldType != null) {
					oldType.setParent(parent, locationInParent);
				}
			}
			public Expression toExpression() {
				return null;
			}
			public TY getTY() {
				return ty;
			}
		};
	}
	
	public final static IDmdType getAdapter(final DelegateType type) {
		return new IDmdType() {
			public Object getAdaptedType() {
				return type;
			}
			public Type getNext() {
				return null;
			}
			public void setNext(Type dmdType) {
			}
			public Expression toExpression() {
				return null;
			}
			public TY getTY() {
				return TY.Tdelegate;
			}
		};
	}

}
