package descent.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.internal.core.parser.IDmdType;
import descent.internal.core.parser.TY;

/**
 * Adapts an object to an IDmdType.
 * @see IDmdType
 */
public class TypeAdapter {
	
	private final Parser parser;

	public TypeAdapter(Parser parser) {
		this.parser = parser;		
	}
	
	public IDmdType getAdapter(Object object) {
		if (object == null) return null;
		
		if (object instanceof IDmdType) {
			return (IDmdType) object;
		}
		
		if (object instanceof ASTNode) {
			ASTNode node = (ASTNode) object;
			switch(node.getNodeType()) {
			case ASTNode.PRIMITIVE_TYPE:
				return getAdapter((PrimitiveType) object);
			case ASTNode.POINTER_TYPE:
				return getAdapter((PointerType) object);
			case ASTNode.STATIC_ARRAY_TYPE:
				return getAdapter((StaticArrayType) object);
			case ASTNode.DYNAMIC_ARRAY_TYPE:
				return getAdapter((DynamicArrayType) object);
			case ASTNode.ASSOCIATIVE_ARRAY_TYPE:
				return getAdapter((AssociativeArrayType) object);
			case ASTNode.SLICE_TYPE:
				return getAdapter((SliceType) object);
			case ASTNode.DELEGATE_TYPE:
				return getAdapter((DelegateType) object);
			case ASTNode.SIMPLE_TYPE:
			case ASTNode.QUALIFIED_TYPE:
			case ASTNode.TEMPLATE_TYPE:
				return getAdapterForTident((Type) object);
			case ASTNode.TYPEOF_TYPE:
				return getAdapter((TypeofType) object);
			}
		}
		
		throw new RuntimeException("Can't adapt " + object + " to ITypeWithNextField");
	}
	
	public IDmdType getAdapter(final PrimitiveType type) {
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
	
	private IDmdType getAdapterForTident(final Type type) {
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
				return identToExpression(type);
			}
			public TY getTY() {
				return TY.Tident;
			}
		};
	}
	
	private Expression identToExpression(Type type) {
		switch(type.getNodeType()) {
		case ASTNode.SIMPLE_TYPE:
			SimpleType simpleType = (SimpleType) type;
			return simpleType.getName();
		case ASTNode.QUALIFIED_TYPE:
			QualifiedType qualifiedType = (QualifiedType) type;
			Expression pre = identToExpression(qualifiedType.getQualifier());
			Expression post = identToExpression(qualifiedType.getType());
			if (pre != null && pre instanceof Name && post != null && post instanceof SimpleName) {
				return parser.newQualifiedName((Name) pre, (SimpleName) post);
			}
		case ASTNode.TEMPLATE_TYPE:
			// TODO I don't understand how DMD's front end handles this case...
			// but in the end it seems to return null! :-S
			return null;
		}
		return null;
	}
	
	public IDmdType getAdapter(final PointerType type) {
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
	
	public IDmdType getAdapter(final TypeofType type) {
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
	
	public IDmdType getAdapter(final StaticArrayType type) {
		return new IDmdType() {
			public Object getAdaptedType() {
				return type;
			}
			public Type getNext() {
				return type.getComponentType();
			}
			public void setNext(Type dmdType) {
				TypeAdapter.setNext(type, dmdType);
			}
			public Expression toExpression() {
				Expression e = getAdapter(getNext()).toExpression();
			    if (e != null) {
			    	List<Expression> arguments = new ArrayList<Expression>();
			    	arguments.add(type.getSize());
			    	parser.newArrayAccess(e, arguments);
			    }
			    return e;
			}
			public TY getTY() {
				return TY.Tsarray;
			}
		};
	}
	
	public IDmdType getAdapter(final DynamicArrayType type) {
		return getAdapter(type, TY.Tarray);
	}
	
	public IDmdType getAdapter(final AssociativeArrayType type) {
		return getAdapter(type, TY.Taarray);
	}
	
	public IDmdType getAdapter(final SliceType type) {
		return getAdapter(type, TY.Tslice);
	}
	
	public IDmdType getAdapter(final ArrayType type, final TY ty) {
		return new IDmdType() {
			public Object getAdaptedType() {
				return type;
			}
			public Type getNext() {
				return type.getComponentType();
			}
			public void setNext(Type dmdType) {
				TypeAdapter.setNext(type, dmdType);
			}
			public Expression toExpression() {
				return null;
			}
			public TY getTY() {
				return ty;
			}
		};
	}
	
	public IDmdType getAdapter(final DelegateType type) {
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
	
	private static void setNext(ArrayType type, Type dmdType) {
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

}
