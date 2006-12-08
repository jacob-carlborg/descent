package descent.ui.text.outline;

import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import descent.core.dom.IAggregateDeclaration;
import descent.core.dom.IAliasDeclaration;
import descent.core.dom.IAliasTemplateParameter;
import descent.core.dom.IArgument;
import descent.core.dom.IAssociativeArrayType;
import descent.core.dom.IDebugDeclaration;
import descent.core.dom.IDelegateType;
import descent.core.dom.IDynamicArrayType;
import descent.core.dom.IElement;
import descent.core.dom.IEnumDeclaration;
import descent.core.dom.IEnumMember;
import descent.core.dom.IFunctionDeclaration;
import descent.core.dom.IIdentifierType;
import descent.core.dom.IImport;
import descent.core.dom.IImportDeclaration;
import descent.core.dom.ILinkDeclaration;
import descent.core.dom.IMixinDeclaration;
import descent.core.dom.IModifier;
import descent.core.dom.IModuleDeclaration;
import descent.core.dom.IPointerType;
import descent.core.dom.IPragmaDeclaration;
import descent.core.dom.ISelectiveImport;
import descent.core.dom.ISimpleName;
import descent.core.dom.IStaticArrayType;
import descent.core.dom.ITemplateDeclaration;
import descent.core.dom.ITemplateInstanceType;
import descent.core.dom.ITemplateParameter;
import descent.core.dom.IType;
import descent.core.dom.ITypeTemplateParameter;
import descent.core.dom.ITypedefDeclaration;
import descent.core.dom.ITypeofType;
import descent.core.dom.IVariableDeclaration;
import descent.core.dom.IVersionDeclaration;
import descent.internal.core.dom.Argument;
import descent.internal.core.dom.DebugAssignment;
import descent.internal.core.dom.TemplateParameter;
import descent.internal.core.dom.VersionAssignment;
import descent.ui.DescentUI;
import descent.ui.IImages;

public class DOutlineLabelProvider extends LabelProvider {
	
	private Image moduleImage;
	private Image importsImage;
	private Image importImage;
	private Image classImage;
	private Image enumImage;
	private Image interfaceImage;
	private Image structImage;
	private Image unionImage;
	private Image templateImage;
	private Image invariantImage;
	private Image unittestImage;
	private Image typedefImage;
	private Image aliasImage;
	private Image linkImage;
	private Image versionImage;
	private Image debugImage;
	private Image functionPublicImage;
	private Image functionPackageImage;
	private Image functionProtectedImage;
	private Image functionPrivateImage;
	private Image ctorPublicImage;
	private Image ctorPackageImage;
	private Image ctorProtectedImage;
	private Image ctorPrivateImage;
	private Image dtorPublicImage;
	private Image dtorPackageImage;
	private Image dtorProtectedImage;
	private Image dtorPrivateImage;
	private Image fieldPublicImage;
	private Image fieldPackageImage;
	private Image fieldProtectedImage;
	private Image fieldPrivateImage;
	
	public DOutlineLabelProvider() {
		this.moduleImage = DescentUI.getImageDescriptor(IImages.MODULE).createImage();
		this.importsImage = DescentUI.getImageDescriptor(IImages.IMPORTS).createImage();
		this.importImage = DescentUI.getImageDescriptor(IImages.IMPORT).createImage();
		this.classImage = DescentUI.getImageDescriptor(IImages.CLASS).createImage();
		this.enumImage = DescentUI.getImageDescriptor(IImages.ENUM).createImage();
		this.interfaceImage = DescentUI.getImageDescriptor(IImages.INTERFACE).createImage();
		this.structImage = DescentUI.getImageDescriptor(IImages.STRUCT).createImage();
		this.unionImage = DescentUI.getImageDescriptor(IImages.UNION).createImage();
		this.templateImage = DescentUI.getImageDescriptor(IImages.TEMPLATE).createImage();
		this.invariantImage = DescentUI.getImageDescriptor(IImages.INVARIANT).createImage();
		this.unittestImage = DescentUI.getImageDescriptor(IImages.UNIT_TEST).createImage();
		this.typedefImage = DescentUI.getImageDescriptor(IImages.TYPEDEF).createImage();
		this.aliasImage = DescentUI.getImageDescriptor(IImages.ALIAS).createImage();
		this.linkImage = DescentUI.getImageDescriptor(IImages.LINK).createImage();
		this.versionImage = DescentUI.getImageDescriptor(IImages.VERSION).createImage();
		this.debugImage = DescentUI.getImageDescriptor(IImages.DEBUG).createImage();
		this.functionPublicImage = DescentUI.getImageDescriptor(IImages.FUNCTION_PUBLIC).createImage();
		this.functionPackageImage = DescentUI.getImageDescriptor(IImages.FUNCTION_PACKAGE).createImage();
		this.functionProtectedImage = DescentUI.getImageDescriptor(IImages.FUNCTION_PROTECTED).createImage();
		this.functionPrivateImage = DescentUI.getImageDescriptor(IImages.FUNCTION_PRIVATE).createImage();
		this.fieldPublicImage = DescentUI.getImageDescriptor(IImages.FIELD_PUBLIC).createImage();
		this.fieldPackageImage = DescentUI.getImageDescriptor(IImages.FIELD_PACKAGE).createImage();
		this.fieldProtectedImage = DescentUI.getImageDescriptor(IImages.FIELD_PROTECTED).createImage();
		this.fieldPrivateImage = DescentUI.getImageDescriptor(IImages.FIELD_PRIVATE).createImage();
		this.ctorPublicImage = DescentUI.getImageDescriptor(IImages.CONSTUCTOR_PUBLIC).createImage();
		this.ctorPackageImage = DescentUI.getImageDescriptor(IImages.CONSTUCTOR_PACKAGE).createImage();
		this.ctorProtectedImage = DescentUI.getImageDescriptor(IImages.CONSTUCTOR_PROTECTED).createImage();
		this.ctorPrivateImage = DescentUI.getImageDescriptor(IImages.CONSTUCTOR_PRIVATE).createImage();
		this.dtorPublicImage = DescentUI.getImageDescriptor(IImages.DESTUCTOR_PUBLIC).createImage();
		this.dtorPackageImage = DescentUI.getImageDescriptor(IImages.DESTUCTOR_PACKAGE).createImage();
		this.dtorProtectedImage = DescentUI.getImageDescriptor(IImages.DESTUCTOR_PROTECTED).createImage();
		this.dtorPrivateImage = DescentUI.getImageDescriptor(IImages.DESTUCTOR_PRIVATE).createImage();
	}
	
	@Override
	public String getText(Object element) {
		IElement e = (IElement) element;
		ISimpleName name;
		StringBuilder s;
		IElement[] templateArguments;
		
		switch(e.getNodeType0()) {
		case IElement.MODULE_DECLARATION:
			return ((IModuleDeclaration) element).getQualifiedName().toString();
		case IElement.IMPORT_DECLARATION:
			IImportDeclaration importDeclaration = (IImportDeclaration) element;
			return getImportText(importDeclaration);
		case IElement.AGGREGATE_DECLARATION:
			IAggregateDeclaration aggregateDeclaration = (IAggregateDeclaration) element;
			s = new StringBuilder();
			name = aggregateDeclaration.getName();
			if (name != null) s.append(name.toString());
			if (!aggregateDeclaration.templateParameters().isEmpty()) {
				appendTemplateParameters(s, aggregateDeclaration.templateParameters());
			}
			return s.toString();
		case IElement.FUNCTION_DECLARATION:
			IFunctionDeclaration f = (IFunctionDeclaration) e;
			s = new StringBuilder();
			switch(f.getKind()) {
			case FUNCTION:
				s.append(f.getName() == null ? "" : f.getName().toString());
				break;
			case CONSTRUCTOR:
			case STATIC_CONSTRUCTOR:
				s.append("this");
				break;
			case DESTRUCTOR:
			case STATIC_DESTRUCTOR:
				s.append("~this");
				break;
			case NEW:
				s.append("new");
				break;
			case DELETE:
				s.append("delete");
				break;
			}
			if (!f.templateParameters().isEmpty()) {
				appendTemplateParameters(s, f.templateParameters());
			}
			appendArguments(s, f.arguments());
			return s.toString();
		case IElement.ENUM_DECLARATION:
			name = ((IEnumDeclaration) element).getName();
			return name == null ? "" : name.toString();
		case IElement.ENUM_MEMBER:
			return ((IEnumMember) element).getName().toString();
		case IElement.INVARIANT_DECLARATION:
			return "invariant";
		case IElement.UNIT_TEST_DECLARATION:
			return "unit test";
		case IElement.VARIABLE_DECLARATION:
			IVariableDeclaration var = (IVariableDeclaration) element;
			name = var.getName();
			s = new StringBuilder();
			if (name != null) s.append(name.toString());
			if (var.getType() != null) {
				s.append(" : ");
				appendType(s, var.getType());
			}
			return s.toString();
		case IElement.TYPEDEF_DECLARATION:
			ITypedefDeclaration td = (ITypedefDeclaration) element;
			name = td.getName();
			s = new StringBuilder();
			if (name != null) s.append(name.toString());
			s.append(" : ");
			appendType(s, td.getType());
			return s.toString();
		case IElement.ALIAS_DECLARATION:
			IAliasDeclaration al = (IAliasDeclaration) element;
			name = al.getName();
			s = new StringBuilder();
			if (name != null) s.append(name.toString());
			s.append(" : ");
			appendType(s, al.getType());
			return s.toString();
		case IElement.TEMPLATE_DECLARATION:
			ITemplateDeclaration t = (ITemplateDeclaration) element;
			name = t.getName();
			s = new StringBuilder();
			if (name != null) s.append(name.toString());
			appendTemplateParameters(s, t.getTemplateParameters());
			return s.toString();
		case IElement.LINK_DECLARATION:
			ILinkDeclaration link = (ILinkDeclaration) element;
			switch(link.getLinkage()) {
			case ILinkDeclaration.LINKAGE_C: return "C";
			case ILinkDeclaration.LINKAGE_CPP: return "Cpp";
			case ILinkDeclaration.LINKAGE_D: return "D";
			case ILinkDeclaration.LINKAGE_WINDOWS: return "Windows";
			case ILinkDeclaration.LINKAGE_PASCAL: return "Pascal";
			}
			break;
		case IElement.VERSION_DECLARATION:
			IVersionDeclaration v = (IVersionDeclaration) element;
			name = v.getVersion();
			return name == null ? "" : name.toString();
		case IElement.DEBUG_DECLARATION:
			IDebugDeclaration d = (IDebugDeclaration) element;
			name = d.getDebug();
			return name == null ? "" : name.toString();
		case IElement.DEBUG_ASSIGNMENT:
			DebugAssignment da = (DebugAssignment) element;
			return "debug = " + da.getVersion().getValue();
		case IElement.VERSION_ASSIGNMENT:
			VersionAssignment va = (VersionAssignment) element;
			return "version = " + va.getVersion().getValue();
		case IElement.PRAGMA_DECLARATION:
			IPragmaDeclaration pd = (IPragmaDeclaration) element;
			return pd.getIdentifier() == null ? "" : pd.getIdentifier().toString();
		case IElement.MIXIN_DECLARATION:
			IMixinDeclaration mix = (IMixinDeclaration) element;
			s = new StringBuilder();
			if (mix.getName() != null) {
				s.append(mix.getName());
			}
			templateArguments = mix.getTemplateArguments();
			if (templateArguments.length > 0) {
				s.append("!(");
				appendElements(s, templateArguments);
				s.append(")");
			}
			return s.toString();
		case IImaginaryElements.IMPORTS:
			 return "import declarations";
		case IImaginaryElements.ELSE:
			return "else";
		}
		
		if (element instanceof IType) {
			s = new StringBuilder();
			appendType(s, (IType) element);
			return s.toString();
		}
		
		return super.getText(element);
	}
	
	

	@Override
	public Image getImage(Object element) {
		IElement e = (IElement) element;
		int m;
		
		switch(e.getNodeType0()) {
		case IElement.MODULE_DECLARATION:
			return moduleImage;
		case IElement.IMPORT_DECLARATION:
			return importImage;
		case IElement.AGGREGATE_DECLARATION:
			IAggregateDeclaration a = (IAggregateDeclaration) element;
			switch(a.getKind()) {
			case CLASS:
				return classImage;
			case INTERFACE:
				return interfaceImage;
			case STRUCT:
				return structImage;
			case UNION:
				return unionImage;
			}
		case IElement.FUNCTION_DECLARATION:
			IFunctionDeclaration func = (IFunctionDeclaration) element;
			m = func.getModifierFlags();
			switch(func.getKind()) {
			case FUNCTION:
			case NEW:
			case DELETE:
				if ((m & IModifier.PRIVATE) > 0) {
					return functionPrivateImage;
				} else if ((m & IModifier.PROTECTED) > 0) {
					return functionProtectedImage;
				} else if ((m & IModifier.PACKAGE) > 0) {
					return functionPackageImage;
				} else if ((m & IModifier.EXPORT) > 0) {
					// TODO
				}
				return functionPublicImage;
			case CONSTRUCTOR:
			case STATIC_CONSTRUCTOR:
				if ((m & IModifier.PRIVATE) > 0) {
					return ctorPrivateImage;
				} else if ((m & IModifier.PROTECTED) > 0) {
					return ctorProtectedImage;
				} else if ((m & IModifier.PACKAGE) > 0) {
					return ctorPackageImage;
				} else if ((m & IModifier.EXPORT) > 0) {
					// TODO
				}
				return ctorPublicImage;
			case DESTRUCTOR:
			case STATIC_DESTRUCTOR:
				if ((m & IModifier.PRIVATE) > 0) {
					return dtorPrivateImage;
				} else if ((m & IModifier.PROTECTED) > 0) {
					return dtorProtectedImage;
				} else if ((m & IModifier.PACKAGE) > 0) {
					return dtorPackageImage;
				} else if ((m & IModifier.EXPORT) > 0) {
					// TODO
				}
				return dtorPublicImage;
			}
		case IElement.ENUM_DECLARATION:
			return enumImage;
		case IElement.ENUM_MEMBER:
			return fieldPublicImage;
		case IElement.INVARIANT_DECLARATION:
			return invariantImage;
		case IElement.UNIT_TEST_DECLARATION:
			return unittestImage;
		case IElement.VARIABLE_DECLARATION:
			m = ((IVariableDeclaration) element).getModifierFlags();
			if ((m & IModifier.PRIVATE) > 0) {
				return fieldPrivateImage;
			} else if ((m & IModifier.PROTECTED) > 0) {
				return fieldProtectedImage;
			} else if ((m & IModifier.PACKAGE) > 0) {
				return fieldPackageImage;
			} else if ((m & IModifier.EXPORT) > 0) {
				// TODO:
				return fieldPublicImage;
			}
			return fieldPublicImage;
		case IElement.TYPEDEF_DECLARATION:
			return typedefImage;
		case IElement.ALIAS_DECLARATION:
			return aliasImage;
		case IElement.TEMPLATE_DECLARATION:
			return templateImage;
		case IElement.LINK_DECLARATION:
			return linkImage;
		case IElement.DEBUG_DECLARATION:
			return debugImage;
		case IElement.VERSION_DECLARATION:
			return versionImage;
		case IElement.CONDITION_ASSIGNMENT:
			return debugImage;
		case IElement.VERSION_ASSIGNMENT:
			return versionImage;
		case IImaginaryElements.IMPORTS:
			return importsImage;
		}
		return super.getImage(element);
	}
	
	private String getImportText(IImportDeclaration impDecl) {
		StringBuilder builder = new StringBuilder();
		for(IImport imp : impDecl.imports()) {
			if (imp.getAlias() == null) {
				builder.append(imp.getName());
			} else {
				builder.append(imp.getAlias());
				builder.append(" = ");
				builder.append(imp.getName());
			}
			if (imp.selectiveImports().size() > 0) {
				builder.append(": ");
				for(ISelectiveImport sel : imp.selectiveImports()) {
					if (sel.getAlias() == null) {
						builder.append(sel.getName());
					} else {
						builder.append(sel.getAlias());
						builder.append(" = ");
						builder.append(sel.getName());
					}
					builder.append(", ");
				}
			} else {
				builder.append(", ");
			}
		}
		
		if (builder.length() > 2) {
			builder.delete(builder.length() - 2, builder.length());
		}
		
		return builder.toString();
	}
	
	// TODO remove duplicated code
	private void appendTemplateParameters(StringBuilder s, ITemplateParameter[] templateParameters) {
s.append('(');
		
		int i = 0;
		for(ITemplateParameter p : templateParameters) {
			switch(p.getNodeType0()) {
			case ITemplateParameter.TYPE_TEMPLATE_PARAMETER:
				ITypeTemplateParameter ttp = (ITypeTemplateParameter) p;
				s.append(ttp.getName().toString());
				if (ttp.getSpecificType() != null) {
					s.append(" : ");
					appendType(s, ttp.getSpecificType());
				}
				if (ttp.getDefaultType() != null) {
					s.append(" = ");
					appendType(s, ttp.getDefaultType());
				}
				break;
			case ITemplateParameter.ALIAS_TEMPLATE_PARAMETER:
				IAliasTemplateParameter tap = (IAliasTemplateParameter) p;
				s.append("alias ");
				s.append(tap.getName().toString());
				if (tap.getSpecificType() != null) {
					s.append(" : ");
					appendType(s, tap.getSpecificType());
				}
				if (tap.getDefaultType() != null) {
					s.append(" = ");
					appendType(s, tap.getDefaultType());
				}
				break;
			}
			if (i != templateParameters.length - 1) {
				s.append(", ");
			}
			i++;
		}
			
		s.append(')');
	}
	
	private void appendTemplateParameters(StringBuilder s, List<TemplateParameter> templateParameters) {
		s.append('(');
		
		int i = 0;
		for(ITemplateParameter p : templateParameters) {
			switch(p.getNodeType0()) {
			case ITemplateParameter.TYPE_TEMPLATE_PARAMETER:
				ITypeTemplateParameter ttp = (ITypeTemplateParameter) p;
				s.append(ttp.getName().toString());
				if (ttp.getSpecificType() != null) {
					s.append(" : ");
					appendType(s, ttp.getSpecificType());
				}
				if (ttp.getDefaultType() != null) {
					s.append(" = ");
					appendType(s, ttp.getDefaultType());
				}
				break;
			case ITemplateParameter.ALIAS_TEMPLATE_PARAMETER:
				IAliasTemplateParameter tap = (IAliasTemplateParameter) p;
				s.append("alias ");
				s.append(tap.getName().toString());
				if (tap.getSpecificType() != null) {
					s.append(" : ");
					appendType(s, tap.getSpecificType());
				}
				if (tap.getDefaultType() != null) {
					s.append(" = ");
					appendType(s, tap.getDefaultType());
				}
				break;
			}
			if (i != templateParameters.size() - 1) {
				s.append(", ");
			}
			i++;
		}
			
		s.append(')');
	}
	
	private void appendElements(StringBuilder s, IElement[] templateArguments) {
		for(int i = 0; i < templateArguments.length; i++) {
			s.append(getText(templateArguments[i]));
			if (i != templateArguments.length - 1) {
				s.append(", ");
			}
		}
	}
	
	// TODO improve performance
	private void appendArguments(StringBuilder s, List<Argument> arguments) {
		s.append('(');
		for(int i = 0; i < arguments.size(); i++) {
			switch(arguments.get(i).getPassageMode()) {
			case OUT: s.append("out "); break;
			case INOUT: s.append("inout "); break;
			case LAZY: s.append("lazy "); break;
			}
			appendType(s, arguments.get(i).getType());
			if (i != arguments.size() - 1) {
				s.append(", ");
			}
		}
		s.append(')');
	}
	
	// TODO remove duplicated code
	private void appendArguments(StringBuilder s, IArgument[] arguments) {
		s.append('(');
		for(int i = 0; i < arguments.length; i++) {
			switch(arguments[i].getPassageMode()) {
			case OUT: s.append("out "); break;
			case INOUT: s.append("inout "); break;
			case LAZY: s.append("lazy "); break;
			}
			appendType(s, arguments[i].getType());
			if (i != arguments.length - 1) {
				s.append(", ");
			}
		}
		s.append(')');
	}
	
	private void appendType(StringBuilder s, IType type) {
		switch(type.getNodeType0()) {
		case IType.BASIC_TYPE:
			s.append(type);
			break;
		case IType.POINTER_TYPE:
			IPointerType pointer = (IPointerType) type;
			appendType(s, pointer.getInnerType());
			s.append('*');
			break;
		case IType.DYNAMIC_ARRAY_TYPE: {
			IDynamicArrayType array = (IDynamicArrayType) type;
			appendType(s, array.getInnerType());
			s.append("[]");
			break;
		}
		case IType.STATIC_ARRAY_TYPE: {
			IStaticArrayType array = (IStaticArrayType) type;
			appendType(s, array.getInnerType());
			s.append('[');
			s.append(array.getDimension().toString());
			s.append(']');
			break;
		}
		case IType.ASSOCIATIVE_ARRAY_TYPE:
			IAssociativeArrayType array = (IAssociativeArrayType) type;
			appendType(s, array.getInnerType());
			s.append('[');
			appendType(s, array.getKeyType());
			s.append(']');
			break;
		case IType.TEMPLATE_INSTANCE_TYPE:
			ITemplateInstanceType ti = (ITemplateInstanceType) type;
			s.append(ti.getShortName());
			s.append("!(");
			appendElements(s, ti.getTemplateArguments());
			s.append(")");
			break;
		case IType.IDENTIFIER_TYPE:
			IIdentifierType it = (IIdentifierType) type;
			s.append(it.getShortName());
			break;
		case IType.DELEGATE_TYPE:
		case IType.POINTER_TO_FUNCTION_TYPE:
			IDelegateType dt = (IDelegateType) type;
			appendType(s, dt.getReturnType());
			s.append(' ');
			if (type.getNodeType0() == IType.DELEGATE_TYPE) {
				s.append("delegate");
			} else {
				s.append("function");
			}
			appendArguments(s, dt.getArguments());
			break;
		case IType.TYPEOF_TYPE:
			ITypeofType tt = (ITypeofType) type;
			s.append("typeof(");
			s.append(tt.getExpression());
			s.append(")");
			break;
		default:
			s.append(type.toString());
			break;
		}
	}
	
	@Override
	public void dispose() {
		moduleImage.dispose();
		importsImage.dispose();
		importImage.dispose();		
		classImage.dispose();
		interfaceImage.dispose();
		enumImage.dispose();
		structImage.dispose();
		unionImage.dispose();
		templateImage.dispose();
		invariantImage.dispose();
		unittestImage.dispose();
		typedefImage.dispose();
		aliasImage.dispose();
		linkImage.dispose();
		versionImage.dispose();
		debugImage.dispose();
		functionPublicImage.dispose();
		functionPackageImage.dispose();
		functionProtectedImage.dispose();
		functionPrivateImage.dispose();
		ctorPublicImage.dispose();
		ctorPackageImage.dispose();
		ctorProtectedImage.dispose();
		ctorPrivateImage.dispose();
		dtorPublicImage.dispose();
		dtorPackageImage.dispose();
		dtorProtectedImage.dispose();
		dtorPrivateImage.dispose();
		
		moduleImage = null;
		importsImage = null;
		importImage = null;		
		classImage = null;
		interfaceImage = null;
		enumImage = null;
		structImage = null;
		unionImage = null;
		templateImage = null;
		invariantImage = null;
		unittestImage = null;
		typedefImage = null;
		aliasImage = null;
		linkImage = null;
		versionImage = null;
		debugImage = null;
		functionPublicImage = null;
		functionPackageImage = null;
		functionProtectedImage = null;
		functionPrivateImage = null;
		ctorPublicImage = null;
		ctorPackageImage = null;
		ctorProtectedImage = null;
		ctorPrivateImage = null;
		dtorPublicImage = null;
		dtorPackageImage = null;
		dtorProtectedImage = null;
		dtorPrivateImage = null;
	}

}