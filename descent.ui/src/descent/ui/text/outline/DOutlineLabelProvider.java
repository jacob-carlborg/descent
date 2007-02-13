package descent.ui.text.outline;

import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import descent.core.dom.ASTNode;
import descent.core.dom.AggregateDeclaration;
import descent.core.dom.AliasDeclaration;
import descent.core.dom.AliasDeclarationFragment;
import descent.core.dom.AliasTemplateParameter;
import descent.core.dom.Argument;
import descent.core.dom.AssociativeArrayType;
import descent.core.dom.ConstructorDeclaration;
import descent.core.dom.DebugAssignment;
import descent.core.dom.DebugDeclaration;
import descent.core.dom.DelegateType;
import descent.core.dom.DynamicArrayType;
import descent.core.dom.EnumDeclaration;
import descent.core.dom.EnumMember;
import descent.core.dom.ExternDeclaration;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.Import;
import descent.core.dom.ImportDeclaration;
import descent.core.dom.ModuleDeclaration;
import descent.core.dom.Name;
import descent.core.dom.PointerType;
import descent.core.dom.PragmaDeclaration;
import descent.core.dom.SelectiveImport;
import descent.core.dom.SimpleType;
import descent.core.dom.StaticArrayType;
import descent.core.dom.TemplateDeclaration;
import descent.core.dom.TemplateParameter;
import descent.core.dom.TemplateType;
import descent.core.dom.Type;
import descent.core.dom.TypeTemplateParameter;
import descent.core.dom.TypedefDeclaration;
import descent.core.dom.TypedefDeclarationFragment;
import descent.core.dom.TypeofType;
import descent.core.dom.VariableDeclaration;
import descent.core.dom.VariableDeclarationFragment;
import descent.core.dom.VersionAssignment;
import descent.core.dom.VersionDeclaration;
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
	//private Image fieldPackageImage;
	//private Image fieldProtectedImage;
	//private Image fieldPrivateImage;
	
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
		//this.fieldPackageImage = DescentUI.getImageDescriptor(IImages.FIELD_PACKAGE).createImage();
		//this.fieldProtectedImage = DescentUI.getImageDescriptor(IImages.FIELD_PROTECTED).createImage();
		//this.fieldPrivateImage = DescentUI.getImageDescriptor(IImages.FIELD_PRIVATE).createImage();
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
		ASTNode e = (ASTNode) element;
		Name name;
		StringBuilder s;
		//ASTNode[] templateArguments;
		
		switch(e.getNodeType()) {
		case ASTNode.MODULE_DECLARATION:
			return ((ModuleDeclaration) element).getName().getFullyQualifiedName();
		case ASTNode.IMPORT_DECLARATION:
			ImportDeclaration importDeclaration = (ImportDeclaration) element;
			return getImportText(importDeclaration);
		case ASTNode.AGGREGATE_DECLARATION:
			AggregateDeclaration aggregateDeclaration = (AggregateDeclaration) element;
			s = new StringBuilder();
			name = aggregateDeclaration.getName();
			if (name != null) s.append(name.getFullyQualifiedName());
			if (!aggregateDeclaration.templateParameters().isEmpty()) {
				appendTemplateParameters(s, aggregateDeclaration.templateParameters());
			}
			return s.toString();
		case ASTNode.FUNCTION_DECLARATION:
			FunctionDeclaration f = (FunctionDeclaration) e;
			s = new StringBuilder();
			s.append(f.getName() == null ? "" : f.getName().getFullyQualifiedName());
			if (!f.templateParameters().isEmpty()) {
				appendTemplateParameters(s, f.templateParameters());
			}
			appendArguments(s, f.arguments());
			return s.toString();
		case ASTNode.CONSTRUCTOR_DECLARATION:
			ConstructorDeclaration c = (ConstructorDeclaration) e;
			s = new StringBuilder();
			switch(c.getKind()) {
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
			appendArguments(s, c.arguments());
			return s.toString();
		case ASTNode.ENUM_DECLARATION:
			name = ((EnumDeclaration) element).getName();
			return name == null ? "" : name.getFullyQualifiedName();
		case ASTNode.ENUM_MEMBER:
			return ((EnumMember) element).getName().getFullyQualifiedName();
		case ASTNode.INVARIANT_DECLARATION:
			return "invariant";
		case ASTNode.UNIT_TEST_DECLARATION:
			return "unit test";
		case ASTNode.VARIABLE_DECLARATION_FRAGMENT:
			VariableDeclarationFragment var = (VariableDeclarationFragment) element;
			name = var.getName();
			s = new StringBuilder();
			if (name != null) s.append(name.getFullyQualifiedName());
			if (((VariableDeclaration) var.getParent()).getType() != null) {
				s.append(" : ");
				appendType(s, ((VariableDeclaration) var.getParent()).getType());
			}
			return s.toString();
		case ASTNode.TYPEDEF_DECLARATION_FRAGMENT:
			TypedefDeclarationFragment tl = (TypedefDeclarationFragment) element;
			name = tl.getName();
			s = new StringBuilder();
			if (name != null) s.append(name.getFullyQualifiedName());
			s.append(" : ");
			appendType(s, ((TypedefDeclaration) tl.getParent()).getType());
			return s.toString();
		case ASTNode.ALIAS_DECLARATION_FRAGMENT:
			AliasDeclarationFragment al = (AliasDeclarationFragment) element;
			name = al.getName();
			s = new StringBuilder();
			if (name != null) s.append(name.getFullyQualifiedName());
			s.append(" : ");
			appendType(s, ((AliasDeclaration) al.getParent()).getType());
			return s.toString();
		case ASTNode.TEMPLATE_DECLARATION:
			TemplateDeclaration t = (TemplateDeclaration) element;
			name = t.getName();
			s = new StringBuilder();
			if (name != null) s.append(name.getFullyQualifiedName());
			appendTemplateParameters(s, t.templateParameters());
			return s.toString();
		case ASTNode.EXTERN_DECLARATION:
			ExternDeclaration link = (ExternDeclaration) element;
			return link.getLinkage().toString();
		case ASTNode.VERSION_DECLARATION:
			VersionDeclaration v = (VersionDeclaration) element;
			return v.getVersion() == null ? "" : v.getVersion().toString();
		case ASTNode.DEBUG_DECLARATION:
			DebugDeclaration d = (DebugDeclaration) element;
			return d.getVersion() == null ? "" : d.getVersion().toString();
		case ASTNode.DEBUG_ASSIGNMENT:
			DebugAssignment da = (DebugAssignment) element;
			return "debug = " + da.getVersion().getValue();
		case ASTNode.VERSION_ASSIGNMENT:
			VersionAssignment va = (VersionAssignment) element;
			return "version = " + va.getVersion().getValue();
		case ASTNode.PRAGMA_DECLARATION:
			PragmaDeclaration pd = (PragmaDeclaration) element;
			return pd.getName() == null ? "" : pd.getName().getFullyQualifiedName();
		case ASTNode.MIXIN_DECLARATION:
			return "";
		case IImaginaryElements.IMPORTS:
			 return "import declarations";
		case IImaginaryElements.ELSE:
			return "else";
		}
		
		if (element instanceof Type) {
			s = new StringBuilder();
			appendType(s, (Type) element);
			return s.toString();
		}
		
		return super.getText(element);
	}
	
	

	@Override
	public Image getImage(Object element) {
		ASTNode e = (ASTNode) element;
		//Modifier m;
		
		switch(e.getNodeType()) {
		case ASTNode.MODULE_DECLARATION:
			return moduleImage;
		case ASTNode.IMPORT_DECLARATION:
			return importImage;
		case ASTNode.AGGREGATE_DECLARATION:
			AggregateDeclaration a = (AggregateDeclaration) element;
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
		case ASTNode.FUNCTION_DECLARATION:
			return functionPublicImage;
		case ASTNode.CONSTRUCTOR_DECLARATION:
			ConstructorDeclaration func = (ConstructorDeclaration) element;
			switch(func.getKind()) {
			case NEW:
			case DELETE:
				return functionPublicImage;
			case CONSTRUCTOR:
			case STATIC_CONSTRUCTOR:
				return ctorPublicImage;
			case DESTRUCTOR:
			case STATIC_DESTRUCTOR:
				return dtorPublicImage;
			}
		case ASTNode.ENUM_DECLARATION:
			return enumImage;
		case ASTNode.ENUM_MEMBER:
			return fieldPublicImage;
		case ASTNode.INVARIANT_DECLARATION:
			return invariantImage;
		case ASTNode.UNIT_TEST_DECLARATION:
			return unittestImage;
		case ASTNode.VARIABLE_DECLARATION:
			/*
			m = ((IVariableDeclaration) element).getModifier();
			if (m.isPrivate()) {
				return fieldPrivateImage;
			} else if (m.isProtected()) {
				return fieldProtectedImage;
			} else if (m.isPackage()) {
				return fieldPackageImage;
			}
			*/
			return fieldPublicImage;
		case ASTNode.TYPEDEF_DECLARATION:
			return typedefImage;
		case ASTNode.ALIAS_DECLARATION:
			return aliasImage;
		case ASTNode.TEMPLATE_DECLARATION:
			return templateImage;
		case ASTNode.EXTERN_DECLARATION:
			return linkImage;
		case ASTNode.DEBUG_DECLARATION:
			return debugImage;
		case ASTNode.VERSION_DECLARATION:
			return versionImage;
		case ASTNode.DEBUG_ASSIGNMENT:
			return debugImage;
		case ASTNode.VERSION_ASSIGNMENT:
			return versionImage;
		case IImaginaryElements.IMPORTS:
			return importsImage;
		}
		return super.getImage(element);
	}
	
	private String getImportText(ImportDeclaration impDecl) {
		StringBuilder builder = new StringBuilder();
		for(Import imp : impDecl.imports()) {
			if (imp.getAlias() == null) {
				builder.append(imp.getName().getFullyQualifiedName());
			} else {
				builder.append(imp.getAlias().getFullyQualifiedName());
				builder.append(" = ");
				builder.append(imp.getName().getFullyQualifiedName());
			}
			if (imp.selectiveImports().size() > 0) {
				builder.append(": ");
				for(SelectiveImport sel : imp.selectiveImports()) {
					if (sel.getAlias() == null) {
						builder.append(sel.getName().getFullyQualifiedName());
					} else {
						builder.append(sel.getAlias());
						builder.append(" = ");
						builder.append(sel.getName().getFullyQualifiedName());
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
	
	private void appendTemplateParameters(StringBuilder s, List<TemplateParameter> templateParameters) {
		s.append('(');
		
		int i = 0;
		for(TemplateParameter p : templateParameters) {
			switch(p.getNodeType()) {
			case TemplateParameter.TYPE_TEMPLATE_PARAMETER:
				TypeTemplateParameter ttp = (TypeTemplateParameter) p;
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
			case TemplateParameter.ALIAS_TEMPLATE_PARAMETER:
				AliasTemplateParameter tap = (AliasTemplateParameter) p;
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
	
	private void appendElements(StringBuilder s, List<ASTNode> templateArguments) {
		for(int i = 0; i < templateArguments.size(); i++) {
			s.append(getText(templateArguments.get(0)));
			if (i != templateArguments.size() - 1) {
				s.append(", ");
			}
		}
	}
	
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
	
	private void appendType(StringBuilder s, Type type) {
		switch(type.getNodeType()) {
		case Type.PRIMITIVE_TYPE:
			s.append(type);
			break;
		case Type.POINTER_TYPE:
			PointerType pointer = (PointerType) type;
			appendType(s, pointer.getComponentType());
			s.append('*');
			break;
		case Type.DYNAMIC_ARRAY_TYPE: {
			DynamicArrayType array = (DynamicArrayType) type;
			appendType(s, array.getComponentType());
			s.append("[]");
			break;
		}
		case Type.STATIC_ARRAY_TYPE: {
			StaticArrayType array = (StaticArrayType) type;
			appendType(s, array.getComponentType());
			s.append('[');
			s.append(array.getSize().toString());
			s.append(']');
			break;
		}
		case Type.ASSOCIATIVE_ARRAY_TYPE:
			AssociativeArrayType array = (AssociativeArrayType) type;
			appendType(s, array.getComponentType());
			s.append('[');
			appendType(s, array.getKeyType());
			s.append(']');
			break;
		case Type.TEMPLATE_TYPE:
			TemplateType ti = (TemplateType) type;
			s.append(ti.getName());
			s.append("!(");
			appendElements(s, ti.arguments());
			s.append(")");
			break;
		case Type.SIMPLE_TYPE:
			SimpleType it = (SimpleType) type;
			s.append(it.getName());
			break;
		case Type.DELEGATE_TYPE:
			DelegateType dt = (DelegateType) type;
			appendType(s, dt.getReturnType());
			s.append(' ');
			if (dt.isFunctionPointer()) {
				s.append("function");
			} else {
				s.append("delegate");
			}
			appendArguments(s, dt.arguments());
			break;
		case Type.TYPEOF_TYPE:
			TypeofType tt = (TypeofType) type;
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