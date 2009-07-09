package descent.internal.ui.javadocexport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import descent.core.ICompilationUnit;
import descent.core.IDocumented;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IMethod;
import descent.core.IParent;
import descent.core.JavaModelException;
import descent.core.dom.AST;
import descent.core.dom.ASTNode;
import descent.core.dom.AggregateDeclaration;
import descent.core.dom.AliasDeclaration;
import descent.core.dom.AliasDeclarationFragment;
import descent.core.dom.CompilationUnit;
import descent.core.dom.Declaration;
import descent.core.dom.EnumDeclaration;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.IBinding;
import descent.core.dom.IMethodBinding;
import descent.core.dom.ITypeBinding;
import descent.core.dom.IVariableBinding;
import descent.core.dom.TemplateDeclaration;
import descent.core.dom.TypedefDeclaration;
import descent.core.dom.TypedefDeclarationFragment;
import descent.core.dom.VariableDeclaration;
import descent.core.dom.VariableDeclarationFragment;
import descent.internal.corext.util.Messages;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.JavaPluginImages;
import descent.internal.ui.actions.OpenBrowserUtil;
import descent.internal.ui.dialogs.OptionalMessageDialog;
import descent.internal.ui.javaeditor.EditorUtility;
import descent.internal.ui.refactoring.RefactoringSaveHelper;
import descent.internal.ui.util.ExceptionHandler;
import descent.internal.ui.util.PixelConverter;
import descent.ui.JavaUI;
import descent.ui.JavadocContentAccess;

public class JavadocWizard extends Wizard implements IExportWizard {
	
	private static Comparator<ASTNode> astNodesComparator = new Comparator<ASTNode>() {
		public int compare(ASTNode o1, ASTNode o2) {
			return getName(o1).compareToIgnoreCase(getName(o2));
		}
	};
	
	private static Comparator<Module> modulesComparator = new Comparator<Module>() {
		public int compare(Module o1, Module o2) {
			return o1.unit.getFullyQualifiedName().compareToIgnoreCase(o2.unit.getFullyQualifiedName());
		}
	};
	
	private static Comparator<IBinding> bindingsComparator = new Comparator<IBinding>() {
		public int compare(IBinding o1, IBinding o2) {
			return o1.getName().compareToIgnoreCase(o2.getName());
		}
	};

	private JavadocTreeWizardPage fJTWPage;
	private JavadocSpecificsWizardPage fJSWPage;
	private JavadocStandardWizardPage fJSpWPage;

	private IPath fDestination;

	private boolean fWriteCustom;
	private boolean fOpenInBrowser;

	private final String TREE_PAGE_DESC= "JavadocTreePage"; //$NON-NLS-1$
	private final String SPECIFICS_PAGE_DESC= "JavadocSpecificsPage"; //$NON-NLS-1$
	private final String STANDARD_PAGE_DESC= "JavadocStandardPage"; //$NON-NLS-1$

	private final int YES= 0;
	private final int YES_TO_ALL= 1;
	private final int NO= 2;
	private final int NO_TO_ALL= 3;
	private final String JAVADOC_ANT_INFORMATION_DIALOG= "javadocAntInformationDialog";//$NON-NLS-1$


	private JavadocOptionsManager fStore;
	private IWorkspaceRoot fRoot;

	private IFile fXmlJavadocFile;
	
//	private static final String ID_JAVADOC_PROCESS_TYPE= "descent.ui.javadocProcess"; //$NON-NLS-1$

	public static void openJavadocWizard(JavadocWizard wizard, Shell shell, IStructuredSelection selection ) {
		wizard.init(PlatformUI.getWorkbench(), selection);
		
		PixelConverter converter= new PixelConverter(shell);
		
		WizardDialog dialog= new WizardDialog(shell, wizard);
		dialog.setMinimumPageSize(converter.convertWidthInCharsToPixels(100), converter.convertHeightInCharsToPixels(20));
		dialog.open();
	}
	
	
	public JavadocWizard() {
		this(null);
	}

	public JavadocWizard(IFile xmlJavadocFile) {
		super();
		setDefaultPageImageDescriptor(JavaPluginImages.DESC_WIZBAN_EXPORT_JAVADOC);
		setWindowTitle(JavadocExportMessages.JavadocWizard_javadocwizard_title); 

		setDialogSettings(JavaPlugin.getDefault().getDialogSettings());

		fRoot= ResourcesPlugin.getWorkspace().getRoot();
		fXmlJavadocFile= xmlJavadocFile;

		fWriteCustom= false;
	}
		
	/*
	 * @see IWizard#performFinish()
	 */
	public boolean performFinish() {

		IJavaProject[] checkedProjects= fJTWPage.getCheckedProjects();
		updateStore(checkedProjects);
		
		fStore.updateDialogSettings(getDialogSettings(), checkedProjects);

		// Wizard should not run with dirty editors
		if (!new RefactoringSaveHelper(false).saveEditors(getShell())) {
			return false;
		}

		fDestination= Path.fromOSString(fStore.getDestination());
		fDestination.toFile().mkdirs();

		fOpenInBrowser= fStore.doOpenInBrowser();

		//Ask if you wish to set the javadoc location for the projects (all) to 
		//the location of the newly generated javadoc 
		if (fStore.isFromStandard()) {
			try {

				URL newURL= fDestination.toFile().toURL();
				List projs= new ArrayList();
				//get javadoc locations for all projects
				for (int i= 0; i < checkedProjects.length; i++) {
					IJavaProject curr= checkedProjects[i];
					URL currURL= JavaUI.getProjectJavadocLocation(curr);
					if (!newURL.equals(currURL)) { // currURL can be null
						//if not all projects have the same javadoc location ask if you want to change
						//them to have the same javadoc location
						projs.add(curr);
					}
				}
				if (!projs.isEmpty()) {
					setAllJavadocLocations((IJavaProject[]) projs.toArray(new IJavaProject[projs.size()]), newURL);
				}
			} catch (MalformedURLException e) {
				JavaPlugin.log(e);
			}
		}

		if (fJSWPage.generateAnt()) {
			//@Improve: make a better message
			OptionalMessageDialog.open(JAVADOC_ANT_INFORMATION_DIALOG, getShell(), JavadocExportMessages.JavadocWizard_antInformationDialog_title, null, JavadocExportMessages.JavadocWizard_antInformationDialog_message, MessageDialog.INFORMATION, new String[] { IDialogConstants.OK_LABEL }, 0); 
			try {
				File file= fStore.createXML(checkedProjects);
				if (file != null) {
					IFile[] files= fRoot.findFilesForLocation(Path.fromOSString(file.getPath()));
					if (files != null) {
						for (int i= 0; i < files.length; i++) {
							files[i].refreshLocal(IResource.DEPTH_ONE, null);
						}
					}
				}
				
			} catch (CoreException e) {
				ExceptionHandler.handle(e, getShell(),JavadocExportMessages.JavadocWizard_error_writeANT_title, JavadocExportMessages.JavadocWizard_error_writeANT_message); 
			}
		}

		if (!executeJavadocGeneration())
			return false;
		
		if (fOpenInBrowser) {
			refresh(fDestination); //If destination of javadoc is in workspace then refresh workspace
			spawnInBrowser(getShell().getDisplay());
		}

		return true;
	}
	
	private void updateStore(IJavaProject[] checkedProjects) {
		//writes the new settings to store
		fJTWPage.updateStore(checkedProjects);
		if (!fJTWPage.getCustom())
			fJSpWPage.updateStore();
		fJSWPage.updateStore();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#performCancel()
	 */
	public boolean performCancel() {
		
		IJavaProject[] checkedProjects= fJTWPage.getCheckedProjects();
		updateStore(checkedProjects);
		
		//If the wizard was not launched from an ant file store the settings 
		if (fXmlJavadocFile == null) {
			fStore.updateDialogSettings(getDialogSettings(), checkedProjects);
		}
		return super.performCancel();
	}

	private void setAllJavadocLocations(IJavaProject[] projects, URL newURL) {
		Shell shell= getShell();
		Image image= shell == null ? null : shell.getDisplay().getSystemImage(SWT.ICON_QUESTION);
		String[] buttonlabels= new String[] { IDialogConstants.YES_LABEL, IDialogConstants.YES_TO_ALL_LABEL, IDialogConstants.NO_LABEL, IDialogConstants.NO_TO_ALL_LABEL };

		for (int j= 0; j < projects.length; j++) {
			IJavaProject iJavaProject= projects[j];
			String message= Messages.format(JavadocExportMessages.JavadocWizard_updatejavadoclocation_message, new String[] { iJavaProject.getElementName(), fDestination.toOSString()}); 
			MessageDialog dialog= new MessageDialog(shell, JavadocExportMessages.JavadocWizard_updatejavadocdialog_label, image, message, 4, buttonlabels, 1);

			switch (dialog.open()) {
				case YES :
					JavaUI.setProjectJavadocLocation(iJavaProject, newURL);
					break;
				case YES_TO_ALL :
					for (int i= j; i < projects.length; i++) {
						iJavaProject= projects[i];
						JavaUI.setProjectJavadocLocation(iJavaProject, newURL);
						j++;
					}
					break;
				case NO_TO_ALL :
					j= projects.length;
					break;
				case NO :
				default :
					break;
			}
		}
	}
	
	private static class Module {
		ICompilationUnit unit;
		Symbols symbols;
		public Module(ICompilationUnit unit) {
			this.unit = unit;
			this.symbols = new Symbols();
		}
	}
	
	private static class Symbols {
		Set<VariableDeclarationFragment> variables = new TreeSet<VariableDeclarationFragment>(astNodesComparator);
		Set<AliasDeclarationFragment> aliases = new TreeSet<AliasDeclarationFragment>(astNodesComparator);
		Set<TypedefDeclarationFragment> typedefs = new TreeSet<TypedefDeclarationFragment>(astNodesComparator);
		Set<EnumDeclaration> enums = new TreeSet<EnumDeclaration>(astNodesComparator);
		Set<FunctionDeclaration> functions = new TreeSet<FunctionDeclaration>(astNodesComparator);
		Set<AggregateDeclaration> classes = new TreeSet<AggregateDeclaration>(astNodesComparator);
		Set<AggregateDeclaration> interfaces = new TreeSet<AggregateDeclaration>(astNodesComparator);
		Set<AggregateDeclaration> structs = new TreeSet<AggregateDeclaration>(astNodesComparator);
		Set<AggregateDeclaration> unions = new TreeSet<AggregateDeclaration>(astNodesComparator);
		Set<TemplateDeclaration> templates = new TreeSet<TemplateDeclaration>(astNodesComparator);
	}

	private boolean executeJavadocGeneration() {
		try {
			// Collect symbols of modules
			Set<Module> modules = collectModules();
			
			// Generate frameset in index.html
			generateIndex();
			
			// Generate stylesheet
			generateStylesheet();
			
			// Generate modules list
			generateModulesFrame(modules);
			
			for(Module module : modules) {
				generateModuleDescriptionsFrame(modules, module);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;

	}

	private Set<Module> collectModules() throws JavaModelException {
		Set<Module> modules = new TreeSet<Module>(modulesComparator);
		collect(fStore.getSourceElements(), modules);
		return modules;
	}

	private void generateIndex() throws IOException {
		Writer out = writerFor("index.html");
		out.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\" \"http://www.w3.org/TR/html4/frameset.dtd\">\r\n" + 
				"<html>\r\n" + 
				"<head>\r\n" + 
				"<title>");
		out.write(fStore.getTitle());
		out.write("</title>\r\n" + 
				"<script type=\"text/javascript\">\r\n" + 
				"   targetPage = \"\" + window.location.search;\r\n" + 
				"   if (targetPage != \"\" && targetPage != \"undefined\")\r\n" + 
				"      targetPage = targetPage.substring(1);\r\n" + 
				"   if (targetPage.indexOf(\":\") != -1)\r\n" + 
				"      targetPage = \"undefined\";\r\n" + 
				"   function loadFrames() {\r\n" + 
				"      if (targetPage != \"\" && targetPage != \"undefined\")\r\n" + 
				"	  top.classFrame.location = top.targetPage;\r\n" + 
				"     }\r\n" + 
				"</script>\r\n" + 
				"<noscript>\r\n" + 
				"</noscript>\r\n" + 
				"</head>\r\n" + 
				"<frameset cols=\"20%,80%\" title=\"\" onLoad=\"top.loadFrames()\">\r\n" + 
				"<frame src=\"modules-frame.html\" name=\"moduleList\" title=\"All Modules\">\r\n" + 
				"<frame src=\"overview-summary.html\" name=\"module\" title=\"Module description\" scrolling=\"yes\">\r\n" +
				"</frameset>\r\n" + 
				"<noframes>\r\n" + 
				"<h2>\r\n" + 
				"Frame Alert</h2>\r\n" + 
				"\r\n" + 
				"<p>\r\n" + 
				"This document is designed to be viewed using the frames feature. If you see this message, you are using a non-frame-capable web client.\r\n" + 
				"<br/>\r\n" + 
				"Link to<a href=\"overview-summary.html\">Non-frame version.</a>\r\n" + 
				"</noframes>\r\n" + 
				"</frameset>\r\n" + 
				"</html>");
		out.close();
	}
	
	private void generateModulesFrame(Set<Module> modules) throws IOException {
		Writer out = writerFor("modules-frame.html");
		out.write(
			"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\r\n" + 
			"<html>\r\n" + 
			"<head>\r\n" + 
			"<title>\r\n" + 
			"Overview (");
		out.write(fStore.getTitle());
		out.write(
			"</title>\r\n" + 
			"<link rel=\"stylesheet\" type=\"text/css\" href=\"stylesheet.css\" title=\"Style\">\r\n" + 
			"</head>\r\n" + 
			"<body>\r\n" + 
			"<div class=\"title\">");
		out.write(fStore.getTitle());
		out.write("</div>\r\n" + 
			"<div class=\"modulesTitle\">Modules</div>\r\n" +
			"<ul class=\"modules\">");
		
		generateModulesList(modules, out);
		
		out.write(
				"</ul>\r\n" +
				"</body>\r\n" +
				"</html>");
		out.close();
	}

	private void generateModulesList(Set<Module> modules, Writer out) throws IOException {
		for(Module module : modules) {
			generateModulesList(module, out);
		}
	}

	private void generateModulesList(Module module, Writer out) throws IOException {
		ICompilationUnit cu = module.unit;
		String fqn = cu.getFullyQualifiedName();
		
		out.write("<li><a href=\"");
		out.write(fqn);
		out.write(".html");
		out.write("\" target=\"module\">");
		out.write(fqn);
		out.write("</li>\r\n");
	}
	
	private void generateModuleDescriptionsFrame(Set<Module> modules, Module module) throws IOException, JavaModelException {
		String fqn = module.unit.getFullyQualifiedName();
		
		Writer out = writerFor(fqn + ".html");
		out.write(
			"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\r\n" + 
			"<html>\r\n" + 
			"<head>\r\n" + 
			"<title>\r\n" + 
			"Module ");
		out.write(
			fqn);
		out.write(
			"</title>\r\n" + 
			"<link rel=\"stylesheet\" type=\"text/css\" href=\"stylesheet.css\" title=\"Style\">\r\n" + 
			"</head>\r\n" + 
			"<body>\r\n" + 
			"<div class=\"module\">");
		out.write(
			fqn);
		out.write(
			"</div>\r\n");
		
		writeDdoc(out, module.unit);
		
		// Variables
		out.write("<ul>");
		if (!module.symbols.variables.isEmpty()) {
			out.write("<li><span class=\"first_header\">Variables</span>");
			out.write("<ul>");
			for(VariableDeclarationFragment a : module.symbols.variables) {
				out.write("<li>");
				writeAnchor(out, a.getName().getIdentifier(), ASTNode.VARIABLE_DECLARATION);
				
				IBinding binding = a.getName().resolveTypeBinding();
				out.write(": ");
				writeBinding(binding, out);
				
				IJavaElement element = a.resolveBinding().getJavaElement();
				writeDdoc(out, element);
				
				out.write("</li>");
			}
			out.write("</ul>");
			out.write("</li>");
		}
		
		// Aliases
		if (!module.symbols.aliases.isEmpty()) {
			out.write("<li><span class=\"first_header\">Aliases</span>");
			out.write("<ul>");
			for(AliasDeclarationFragment a : module.symbols.aliases) {
				out.write("<li>");
				writeAnchor(out, a.getName().getIdentifier(), ASTNode.ALIAS_DECLARATION);
				
				IBinding binding = a.getName().resolveTypeBinding();
				out.write(": ");
				writeBinding(binding, out);
				
				IJavaElement element = a.resolveBinding().getJavaElement();
				writeDdoc(out, element);
				
				out.write("</li>");
			}
			out.write("</ul>");
			out.write("</li>");
		}
		
		// Typedefs
		if (!module.symbols.typedefs.isEmpty()) {
			out.write("<li><span class=\"first_header\">Typedefs</span>");
			out.write("<ul>");
			for(TypedefDeclarationFragment a : module.symbols.typedefs) {
				out.write("<li>");
				writeAnchor(out, a.getName().getIdentifier(), ASTNode.TYPEDEF_DECLARATION);
				
				IBinding binding = a.getName().resolveTypeBinding();
				out.write(": ");
				writeBinding(binding, out);
				
				IJavaElement element = a.resolveBinding().getJavaElement();
				writeDdoc(out, element);
				
				out.write("</li>");
			}
			out.write("</ul>");
			out.write("</li>");
		}
		
		// Enums
		if (!module.symbols.enums.isEmpty()) {
			out.write("<li><span class=\"first_header\">Enums</span>");
			out.write("<ul>");
			for(EnumDeclaration a : module.symbols.enums) {
				IBinding binding = a.getBaseType().resolveBinding();
				if (a.getName() != null) {
					out.write("<li>");
					writeAnchor(out, a.getName().getIdentifier(), ASTNode.ENUM_DECLARATION);
				} else {
					out.write("<li>");
					out.write("(anynomous)");
				}
				out.write(": ");
				writeBinding(binding, out);
				
				binding = a.resolveBinding();
				if (binding != null) {
					IJavaElement element = binding.getJavaElement();
					writeDdoc(out, element);
				}
				
				out.write("</li>");
			}
			out.write("</ul>");
			out.write("</li>");
		}
		
		// Structs
		if (!module.symbols.structs.isEmpty()) {
			out.write("<li><span class=\"first_header\">Structs</span>");
			out.write("<ul>");
			writeAggregate(modules, module.symbols.structs, out);
			out.write("</ul>");
			out.write("</li>");
		}
		
		// Unions
		if (!module.symbols.unions.isEmpty()) {
			out.write("<li><span class=\"first_header\">Unions</span>");
			out.write("<ul>");
			writeAggregate(modules, module.symbols.unions, out);
			out.write("</ul>");
			out.write("</li>");
		}
		
		// Classes
		if (!module.symbols.classes.isEmpty()) {
			out.write("<li><span class=\"first_header\">Classes</span>");
			out.write("<ul>");
			writeAggregate(modules, module.symbols.classes, out);
			out.write("</ul>");
			out.write("</li>");
		}
		
		// Interfaces
		if (!module.symbols.interfaces.isEmpty()) {
			out.write("<li><span class=\"first_header\">Interfaces</span>");
			out.write("<ul>");
			writeAggregate(modules, module.symbols.interfaces, out);
			out.write("</ul>");
			out.write("</li>");
		}
		
		out.write(
			"</body>\r\n" +
			"</html>");
		out.close();
	}


	private void writeAggregate(Set<Module> modules, Set<AggregateDeclaration> agg, Writer out) throws IOException, JavaModelException {
		for(AggregateDeclaration a : agg) {
			out.write("<li>");
			writeAnchor(out, a.getName().getIdentifier(), a.getNodeType(), a.getKind());
			
			ITypeBinding binding = a.resolveBinding();
			IBinding superclass = binding.getSuperclass();
			ITypeBinding[] interfaces = binding.getInterfaces();
			
			if (superclass != null || (interfaces != null && interfaces.length > 0)) {
				out.write(" : ");
			}
				
			if (superclass != null) {
				writeBinding(superclass, out);
			}
			
			if (superclass != null && (interfaces != null && interfaces.length > 0)) {
				out.write(", ");
			}
			
			if (interfaces != null) {
				for (int i = 0; i < interfaces.length; i++) {
					if (i != 0) {
						out.write(", ");
					}
					writeBinding(interfaces[i], out);
				}
			}
			
			IJavaElement element = a.resolveBinding().getJavaElement();
			writeDdoc(out, element);
			
			out.write("<ul>");
			
			if (a.getKind() == AggregateDeclaration.Kind.CLASS) {
				out.write("<li><span class=\"second_header\">Super hierarchy:</span> ");
				
				Stack<IBinding> bindings = new Stack<IBinding>();
				IBinding current = a.resolveBinding().getSuperclass();
				while(current != null) {
					bindings.push(current);
					if (current instanceof ITypeBinding) {
						current = ((ITypeBinding) current).getSuperclass();
					}
				}
				
				while(!bindings.isEmpty()) {
					writeBinding(bindings.pop(), out);
					out.write(" -> ");
				}
				
				out.write("<span class=\"symbol class\">");
				out.write(a.getName().getIdentifier());
				out.write("</span>");
				
				out.write("</li>");
			}
			
			Set<IBinding> implementedInterfaces = getImplementedInterfaces(modules, binding);
			
			if (!implementedInterfaces.isEmpty()) {
				out.write("<li><span class=\"second_header\">All implemented interfaces:</span> ");
				
				int i = 0;
				for(IBinding inter : implementedInterfaces) {
					if (i != 0)
						out.write(", ");
					writeBinding(inter, out);
					i++;
				}
				out.write("</li>");
			}
			
			if (a.getKind() == AggregateDeclaration.Kind.CLASS) {
				Set<IBinding> subclasses = getSubclasses(modules, a);
				if (!subclasses.isEmpty()) {
					out.write("<li><span class=\"second_header\">Direct known subclasses:</span> ");
					
					int i = 0;
					for(IBinding sub : subclasses) {
						if (i != 0)
							out.write(", ");
						writeBinding(sub, out);
						i++;
					}
					out.write("</li>");
				}
			}
			
			if (a.getKind() == AggregateDeclaration.Kind.INTERFACE) {
				Set<IBinding> subinterfaces = getSubinterfaces(modules, a);
				if (!subinterfaces.isEmpty()) {
					out.write("<li><span class=\"second_header\">All known subinterfaces:</span> ");
					
					int i = 0;
					for(IBinding sub : subinterfaces) {
						if (i != 0)
							out.write(", ");
						writeBinding(sub, out);
						i++;
					}
					out.write("</li>");
				}
			}
			
			if (a.getKind() == AggregateDeclaration.Kind.INTERFACE) {
				Set<IBinding> implementing = getImplementors(modules, a);
				if (!implementing.isEmpty()) {
					out.write("<li><span class=\"second_header\">All known implementing classes:</span> ");
					
					int i = 0;
					for(IBinding sub : implementing) {
						if (i != 0)
							out.write(", ");
						writeBinding(sub, out);
						i++;
					}
					out.write("</li>");
				}
			}
			
			IVariableBinding[] fields = binding.getDeclaredFields();
			if (fields != null && fields.length > 0) {
				out.write("<li><span class=\"second_header\">Fields</span>");
				out.write("<ul>");
				
				for(IVariableBinding var : fields) {
					out.write("<li>");
					out.write("<span class=\"variable\">");
					out.write(var.getName());
					out.write("</span>");
					out.write(": ");
					writeBinding(var.getType(), out);
					
					writeDdoc(out, var.getJavaElement());
					
					out.write("</li>");
				}
				
				out.write("</ul>");
				out.write("</li>");
			}
			

			IMethodBinding[] methods = binding.getDeclaredMethods();
			if (methods != null && methods.length > 0) {
				out.write("<li><span class=\"second_header\">Methods</span>");
				out.write("<ul>");
				
				for(IMethodBinding method : methods) {
					out.write("<li>");
					
					writeBinding(method.getReturnType(), out);
					out.write(" ");
					out.write("<span class=\"function\">");
					out.write(method.getName());
					out.write("</span>");
					out.write("(");
					
					IMethod m = (IMethod) method.getJavaElement();
					String[] parameterNames = m.getParameterNames();
					
					IBinding[] params = method.getParameterTypes();
					for (int i = 0; i < params.length; i++) {
						if (i != 0)
							out.write(", ");
						writeBinding(params[i], out);
						out.write(" ");
						out.write(parameterNames[i]);
					}
					
					switch(m.getVarargs()) {
					case IMethod.VARARGS_NO:
						break;
					case IMethod.VARARGS_SAME_TYPES:
						out.write("...");
						break;
					case IMethod.VARARGS_UNDEFINED_TYPES:
						if (params.length > 0) {
							out.write(", ");
						}
						out.write("...");
					}
					
					out.write(")");
					
					writeDdoc(out, m);
					
					out.write("</li>");
				}
				
				out.write("</ul>");
				out.write("</li>");
			}
			
			out.write("</ul>");
			out.write("<br/></li>");
		}
	}
	
	private Set<IBinding> getImplementedInterfaces(Set<Module> modules, ITypeBinding binding) {
		Set<IBinding> result = new TreeSet<IBinding>(bindingsComparator);
		fillImplementedInterfaces(binding.getSuperclass(), result);
		fillImplementedInterfaces(binding.getInterfaces(), result);
		return result;
	}
	
	private Set<IBinding> getSubclasses(Set<Module> modules, AggregateDeclaration a) {
		ITypeBinding target = a.resolveBinding();
		
		Set<IBinding> result = new TreeSet<IBinding>(bindingsComparator);
		for(Module module : modules) {
			for(AggregateDeclaration other : module.symbols.classes) {
				ITypeBinding binding = other.resolveBinding();
				if (binding != null) {
					IBinding superclass = binding.getSuperclass();
					if (superclass != null && superclass.equals(target)) {
						result.add(binding);
					}
				}
			}
		}
		return result;
	}

	private Set<IBinding> getSubinterfaces(Set<Module> modules, AggregateDeclaration a) {
		ITypeBinding target = a.resolveBinding();
		
		Set<IBinding> result = new TreeSet<IBinding>(bindingsComparator);
		for(Module module : modules) {
			for(AggregateDeclaration other : module.symbols.interfaces) {
				ITypeBinding binding = other.resolveBinding();
				if (binding != null) {
					ITypeBinding[] interfaces = binding.getInterfaces();
					if (interfaces != null) {
						for(ITypeBinding inter : interfaces) {
							if (inter.equals(target)) {
								result.add(binding);
								break;
							}
						}
					}
				}
			}
		}
		return result;
	}

	private Set<IBinding> getImplementors(Set<Module> modules, AggregateDeclaration a) {
		ITypeBinding target = a.resolveBinding();
		
		Set<IBinding> result = new TreeSet<IBinding>(bindingsComparator);
		for(Module module : modules) {
			for(AggregateDeclaration other : module.symbols.classes) {
				ITypeBinding binding = other.resolveBinding();
				if (binding != null) {
					if (getImplementedInterfaces(modules, binding).contains(target)) {
						result.add(binding);
					}
				}
			}
		}
		return result;
	}

	private void fillImplementedInterfaces(ITypeBinding[] interfaces, Set<IBinding> implementedInterfaces) {
		if (interfaces == null)
			return;
		
		for(ITypeBinding inter : interfaces) {
			implementedInterfaces.add(inter);
			fillImplementedInterfaces(inter.getInterfaces(), implementedInterfaces);
		}
	}
	
	private void fillImplementedInterfaces(IBinding binding, Set<IBinding> implementedInterfaces) {
		if (!(binding instanceof ITypeBinding))
			return;
		
		ITypeBinding type = (ITypeBinding) binding;
		
		if (type.isInterface()) {
			implementedInterfaces.add(type);
		}
		
		IBinding superclass = type.getSuperclass();
		if (superclass instanceof ITypeBinding) {
			fillImplementedInterfaces((ITypeBinding) superclass, implementedInterfaces);
		}
		fillImplementedInterfaces(type.getInterfaces(), implementedInterfaces);
	}


	private void writeAnchor(Writer out, String name, int nodeType) throws IOException {
		writeAnchor(out, name, nodeType, null);
	}

	private void writeAnchor(Writer out, String name, int nodeType, AggregateDeclaration.Kind kind) throws IOException {
		out.write("<a name=\"");
		out.write(name);
		out.write("\"");
		out.write(" class=\"symbol ");
		out.write(classFor(nodeType, kind));
		out.write("\"");
		out.write(">");
		out.write(name);
		out.write("</a>");
	}

	private String classFor(int nodeType) {
		return classFor(nodeType, null);
	}

	private String classFor(int nodeType, AggregateDeclaration.Kind kind) {
		switch(nodeType) {
		case ASTNode.VARIABLE_DECLARATION:
		case ASTNode.VARIABLE_DECLARATION_FRAGMENT:
		case ASTNode.ENUM_MEMBER:
			return "variable";
		case ASTNode.TYPEDEF_DECLARATION:
		case ASTNode.TYPEDEF_DECLARATION_FRAGMENT:
			return "typedef";
		case ASTNode.ALIAS_DECLARATION:
		case ASTNode.ALIAS_DECLARATION_FRAGMENT:
			return "alias";
		case ASTNode.FUNCTION_DECLARATION:
			return "function";
		case ASTNode.TEMPLATE_DECLARATION:
			return "template";
		case ASTNode.AGGREGATE_DECLARATION:
			switch(kind) {
			case CLASS:
				return "class";
			case INTERFACE:
				return "interface";
			case STRUCT:
				return "struct";
			case UNION:
				return "union";
			}
		case ASTNode.ENUM_DECLARATION:
			return "enum";
		}
		throw new IllegalStateException();
	}

	private void writeDdoc(Writer out, IJavaElement element) throws JavaModelException, IOException {
		if (element instanceof IDocumented) {
			IDocumented documented = (IDocumented) element;
			Reader reader = JavadocContentAccess.getHTMLContentReader(documented, true, false);
			if (reader == null)
				return;
			
			out.write("<div class=\"ddoc\">");
			while(true) {
				int i = reader.read();
				if (i > 0) {
					out.write((char) i);
				} else {
					break;
				}
			}
			out.write("</div>");
		}
	}
	
	private void writeBinding(IBinding binding, Writer out) throws IOException {
		if (binding == null) {
			System.out.println(123456);
			return;
		}
		
		switch(binding.getKind()) {
		case IBinding.TYPE:
			writeBinding((ITypeBinding) binding, out);
			break;
		}
	}
	
	private void writeBinding(ITypeBinding binding, Writer out) throws IOException {
		if (binding.isPrimitive()) {
			out.write(binding.getName());
		} else if (binding.isPointer()) {
			writeBinding(binding.getComponentType(), out);
			
			if (binding.getComponentType().getKind() == IBinding.TYPE &&
					((ITypeBinding) binding.getComponentType()).isFunction()) {
				
			} else {
				out.write('*');
			}
		} else if (binding.isAssociativeArray()) {
			writeBinding(binding.getValueType(), out);
			out.write('[');
			writeBinding(binding.getKeyType(), out);
			out.write(']');
		} else if (binding.isDelegate()) {
			writeBinding(binding.getReturnType(), out);
			out.write(" delegate(");
			for (int i = 0; i < binding.getParametersTypes().length; i++) {
				if (i != 0)
					out.write(", ");
				writeBinding(binding.getParametersTypes()[i], out);
			}
			out.write(')');
		} else if (binding.isDynamicArray()) {
			writeBinding(binding.getComponentType(), out);
			out.write('[');
			out.write(']');
		} else if (binding.isFunction()) {
			writeBinding(binding.getReturnType(), out);
			out.write(" function(");
			for (int i = 0; i < binding.getParametersTypes().length; i++) {
				if (i != 0)
					out.write(", ");
				writeBinding(binding.getParametersTypes()[i], out);
			}
			out.write(')');
		} else if (binding.isSlice()) {
			writeBinding(binding.getComponentType(), out);
			out.write('[');
			out.write(String.valueOf(binding.getLowerBound()));
			out.write(" .. ");
			out.write(String.valueOf(binding.getUpperBound()));
			out.write(']');
		} else if (binding.isStaticArray()) {
			writeBinding(binding.getComponentType(), out);
			out.write('[');
			out.write(String.valueOf(binding.getDimension()));
			out.write(']');
		} else if (binding.isClass() || binding.isStruct() || binding.isInterface() || binding.isUnion() || binding.isEnum()) {
			writeLink(binding, out);
		} else if (binding.isTemplate()) {
			// TODO
			writeLink(binding, out);
		}
	}

	private void writeLink(ITypeBinding binding, Writer out) throws IOException {
		out.write("<a href=\"");
		writeHref(binding, out);
		out.write("\"");
		
		if (binding.isClass()) {
			out.write(" class=\"symbol class\"");
		} else if (binding.isStruct()) {
			out.write(" class=\"symbol struct\"");
		} else if (binding.isUnion()) {
			out.write(" class=\"symbol union\"");
		} else if (binding.isInterface()) {
			out.write(" class=\"symbol interface\"");
		} else if (binding.isFunction()) {
			out.write(" class=\"symbol function\"");
		} else if (binding.isEnum()) {
			out.write(" class=\"symbol function\"");
		} else if (binding.isTemplate()) {
			out.write(" class=\"symbol template\"");
		}
		
		out.write(" target=\"module\">");
		out.write(binding.getName());
		out.write("</a>");
	}

	private void writeHref(ITypeBinding binding, Writer out) throws IOException {
		writeHref(binding.getJavaElement(), binding.getName(), out);
	}

	private static String getName(ASTNode decl) {
		switch(decl.getNodeType()) {
		case ASTNode.FUNCTION_DECLARATION:
			return ((FunctionDeclaration) decl).getName().getIdentifier();
		case ASTNode.AGGREGATE_DECLARATION:
			return ((AggregateDeclaration) decl).getName().getIdentifier();
		case ASTNode.ALIAS_DECLARATION_FRAGMENT:
			return ((AliasDeclarationFragment) decl).getName().getIdentifier();
		case ASTNode.ENUM_DECLARATION:
			EnumDeclaration e = (EnumDeclaration) decl;
			if (e.getName() == null) {
				return "(unnamed)";
			}
			return e.getName().getIdentifier();
		case ASTNode.TEMPLATE_DECLARATION:
			return ((TemplateDeclaration) decl).getName().getIdentifier();
		case ASTNode.TYPEDEF_DECLARATION_FRAGMENT:
			return ((TypedefDeclarationFragment) decl).getName().getIdentifier();
		case ASTNode.VARIABLE_DECLARATION_FRAGMENT:
			return ((VariableDeclarationFragment) decl).getName().getIdentifier();
		default:
			throw new IllegalStateException();
		}
	}
	
	private static void writeHref(ASTNode decl, Writer out) throws IOException {
		CompilationUnit unit = (CompilationUnit) decl.getRoot();
		IJavaElement element = unit.getJavaElement();
		writeHref(element, getName(decl), out);
	}
	
	private static void writeHref(IJavaElement element, String name, Writer out) throws IOException {
		if (element == null) {
			// TODO
			return;
		}
		
		ICompilationUnit unit;
		if (element instanceof ICompilationUnit) {
			unit = (ICompilationUnit) element;
		} else {
			unit = (ICompilationUnit) element.getAncestor(IJavaElement.COMPILATION_UNIT);
			if (unit == null) {
				unit = (ICompilationUnit) element.getAncestor(IJavaElement.CLASS_FILE);
			}
		}
		
		out.write(unit.getFullyQualifiedName());
		out.write(".html");
		out.write("#");
		out.write(name);
	}
	
	private void collect(IJavaElement[] elements, Set<Module> modules) throws JavaModelException {
		for(IJavaElement element : elements) {
			collect(element, modules);
		}
	}
	
	private void collect(IJavaElement element, Set<Module> modules) throws JavaModelException{
		if (element.getElementType() == IJavaElement.COMPILATION_UNIT ||
			element.getElementType() == IJavaElement.CLASS_FILE) {
			
			ICompilationUnit unit = (ICompilationUnit) element;
			Module module = new Module(unit);
			collect(unit, module.symbols);
			modules.add(module);
		} else if (element instanceof IParent) {
			collect(((IParent) element).getChildren(), modules);
		}
	}
	
	private void collect(ICompilationUnit unit, Symbols symbols) throws JavaModelException {
		CompilationUnit ast = unit.getResolvedAtCompileTime(AST.D1);
		for(Declaration decl : ast.declarations()) {
			switch(decl.getNodeType()) {
			case ASTNode.ALIAS_DECLARATION:
				AliasDeclaration alias = (AliasDeclaration) decl;
				for(AliasDeclarationFragment fragment : alias.fragments()) {
					symbols.aliases.add(fragment);
				}
				break;
			case ASTNode.TYPEDEF_DECLARATION:
				TypedefDeclaration typedef = (TypedefDeclaration) decl;
				for(TypedefDeclarationFragment fragment : typedef.fragments()) {
					symbols.typedefs.add(fragment);
				}
				break;
			case ASTNode.VARIABLE_DECLARATION:
				VariableDeclaration var = (VariableDeclaration) decl;
				for(VariableDeclarationFragment fragment : var.fragments()) {
					symbols.variables.add(fragment);
				}
				break;
			case ASTNode.ENUM_DECLARATION:
				symbols.enums.add((EnumDeclaration) decl);
				break;
			case ASTNode.TEMPLATE_DECLARATION:
				symbols.templates.add((TemplateDeclaration) decl);
				break;
			case ASTNode.FUNCTION_DECLARATION:
				symbols.functions.add((FunctionDeclaration) decl);
				break;
			case ASTNode.AGGREGATE_DECLARATION:
				AggregateDeclaration a = (AggregateDeclaration) decl;
				switch(a.getKind()) {
				case CLASS:
					symbols.classes.add(a);
					break;
				case STRUCT:
					symbols.structs.add(a);
					break;
				case UNION:
					symbols.unions.add(a);
					break;
				case INTERFACE:
					symbols.interfaces.add(a);
					break;
				}
				break;
			case ASTNode.MODIFIER_DECLARATION:
				// TODO
				break;
			}
		}
	}
	
	private void generateStylesheet() throws IOException {
		Writer out = writerFor("stylesheet.css");
		out.write(
				"body { background-color: #FFFFFF; }\r\n" +
//				"a { text-decoration:none; }\r\n" +
//				"a:hover { text-decoration:underline}\r\n" +
				
				".code		 { font-family: monospace; background-color: #e7e7e8; border: 2px solid #cccccc; padding: 1ex; }\r\n" + 
				".java_keyword{color: #7f0055;font-weight: bold;}\r\n" + 
				".java_keyword_return{color: #7f0055;font-weight: bold;}\r\n" + 
				".java_special_token{color: #646464;font-style: italic;}\r\n" + 
				".java_operator{color: #000000;}\r\n" + 
				".java_default{color: #000000;}\r\n" + 
				".java_pragma{color: #646464;}\r\n" + 
				".java_string{color: #2a00ff;}\r\n" + 
				".java_single_line_comment{color: #3f7f5f;}\r\n" + 
				".java_single_line_doc_comment{color: #3f5fbf;}\r\n" + 
				".java_multi_line_comment{color: #3f7f5f;}\r\n" + 
				".java_multi_line_plus_comment{color: #3f7f5f;}\r\n" + 
				".java_multi_line_plus_doc_comment{color: #3f5fbf;}\r\n" + 
				".java_doc_default{color: #3f5fbf;}\r\n" +
				
				".module { font-weight: bold; font-size: 20px; }\r\n" +
				".first_header { font-weight: bold; font-size: 18px; }\r\n" +
				".second_header { font-weight: bold; font-size: 16px; }\r\n" +
				
				".symbol { }\r\n" +
				".variable { color: #000040;  }\r\n" +
				".alias { color: #008080; }\r\n" +
				".typedef { color: #800000;  }\r\n" +
				".function { color: #320050;  }\r\n" +
				".enum { color: #644632;  }\r\n" +
				".class { bold; color: #005032;  }\r\n" +
				".struct { color: #003250;  }\r\n" +
				".union { color: #325000;  }\r\n" +
				".interface { color: #323F70;  }\r\n");
		out.close();
	}
	
	private Writer writerFor(String filename) throws IOException {
		File file = new File(fDestination.toOSString(), filename);
		createDirFor(file.getParentFile());
		return new BufferedWriter(new FileWriter(file));
	}

	private void createDirFor(File file) throws IOException {
		if (!file.exists()) {
			createDirFor(file.getParentFile());
			file.mkdir();
		}
	}

//	private String checkForSpaces(String curr) {
//		if (curr.indexOf(' ') == -1) {
//			return curr;
//		}	
//		StringBuffer buf= new StringBuffer();
//		buf.append('\'');
//		for (int i= 0; i < curr.length(); i++) {
//			char ch= curr.charAt(i);
//			if (ch == '\\' || ch == '\'') {
//				buf.append('\\');				
//			}
//			buf.append(ch);
//		}
//		buf.append('\'');
//		return buf.toString();
//	}

	/*
	 * @see IWizard#addPages()
	 */
	public void addPages() {
		
		fJTWPage= new JavadocTreeWizardPage(TREE_PAGE_DESC, fStore);
		fJSWPage= new JavadocSpecificsWizardPage(SPECIFICS_PAGE_DESC, fJTWPage, fStore);
		fJSpWPage= new JavadocStandardWizardPage(STANDARD_PAGE_DESC, fJTWPage, fStore);

		super.addPage(fJTWPage);
		super.addPage(fJSpWPage);
		super.addPage(fJSWPage);

		fJTWPage.init();
		fJSpWPage.init();
		fJSWPage.init();

	}

	public void init(IWorkbench workbench, IStructuredSelection structuredSelection) {
		IWorkbenchWindow window= workbench.getActiveWorkbenchWindow();
		List selected= Collections.EMPTY_LIST;
		if (window != null) {
			ISelection selection= window.getSelectionService().getSelection();
			if (selection instanceof IStructuredSelection) {
				selected= ((IStructuredSelection) selection).toList();
			} else {
				IJavaElement element= EditorUtility.getActiveEditorJavaInput();
				if (element != null) {
					selected= new ArrayList();
					selected.add(element);
				}
			}
		}
		fStore= new JavadocOptionsManager(fXmlJavadocFile, getDialogSettings(), selected);
	}

	private void refresh(IPath path) {
		if (fRoot.findContainersForLocation(path).length > 0) {
			try {
				fRoot.refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (CoreException e) {
				JavaPlugin.log(e);
			}
		}
	}

	private void spawnInBrowser(Display display) {
		if (fOpenInBrowser) {
			try {
				IPath indexFile= fDestination.append("index.html"); //$NON-NLS-1$
				URL url= indexFile.toFile().toURL();
				OpenBrowserUtil.open(url, display, getWindowTitle());
			} catch (MalformedURLException e) {
				JavaPlugin.log(e);
			}
		}
	}

	private class JavadocDebugEventListener implements IDebugEventSetListener {
		private Display fDisplay;
		private File fFile;

		public JavadocDebugEventListener(Display display, File file) {
			fDisplay= display;
			fFile= file;
		}
		
		public void handleDebugEvents(DebugEvent[] events) {
			for (int i= 0; i < events.length; i++) {
				if (events[i].getKind() == DebugEvent.TERMINATE) {
					try {
						if (!fWriteCustom) {
							fFile.delete();
							refresh(fDestination); //If destination of javadoc is in workspace then refresh workspace
							spawnInBrowser(fDisplay);
						}
					} finally {
						DebugPlugin.getDefault().removeDebugEventListener(this);
					}
					return;
				}
			}
		}
	}

	public IWizardPage getNextPage(IWizardPage page) {
		if (page instanceof JavadocTreeWizardPage) {
			if (!fJTWPage.getCustom()) {
				return fJSpWPage;
			}
			return fJSWPage;
		} else if (page instanceof JavadocSpecificsWizardPage) {
			return null;
		} else if (page instanceof JavadocStandardWizardPage)
			return fJSWPage;
		else
			return null;
	}

	public IWizardPage getPreviousPage(IWizardPage page) {
		if (page instanceof JavadocSpecificsWizardPage) {
			if (!fJTWPage.getCustom()) {
				return fJSpWPage;
			}
			return fJSWPage;
		} else if (page instanceof JavadocTreeWizardPage) {
			return null;
		} else if (page instanceof JavadocStandardWizardPage)
			return fJTWPage;
		else
			return null;
	}
	
}
