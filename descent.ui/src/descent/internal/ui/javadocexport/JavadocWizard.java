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
import java.util.Arrays;
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

import descent.core.Flags;
import descent.core.ICompilationUnit;
import descent.core.IDocumented;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IMethod;
import descent.core.IPackageDeclaration;
import descent.core.IParent;
import descent.core.JavaModelException;
import descent.core.dom.AST;
import descent.core.dom.ASTNode;
import descent.core.dom.AggregateDeclaration;
import descent.core.dom.CompilationUnit;
import descent.core.dom.IBinding;
import descent.core.dom.ICompilationUnitBinding;
import descent.core.dom.IMethodBinding;
import descent.core.dom.ITemplateParameterBinding;
import descent.core.dom.ITypeBinding;
import descent.core.dom.IVariableBinding;
import descent.core.dom.AggregateDeclaration.Kind;
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
	
	private static Comparator<Module> modulesComparator = new Comparator<Module>() {
		public int compare(Module o1, Module o2) {
			return o1.unit.getName().compareToIgnoreCase(o2.unit.getName());
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
		ICompilationUnitBinding unit;
		Symbols symbols = new Symbols();
	}
	
	private static class Symbols {
		Set<IVariableBinding> variables = new TreeSet<IVariableBinding>(bindingsComparator);
		Set<ITypeBinding> aliases = new TreeSet<ITypeBinding>(bindingsComparator);
		Set<ITypeBinding> typedefs = new TreeSet<ITypeBinding>(bindingsComparator);
		Set<ITypeBinding> enums = new TreeSet<ITypeBinding>(bindingsComparator);
		Set<IMethodBinding> functions = new TreeSet<IMethodBinding>(bindingsComparator);
		Set<ITypeBinding> classes = new TreeSet<ITypeBinding>(bindingsComparator);
		Set<ITypeBinding> interfaces = new TreeSet<ITypeBinding>(bindingsComparator);
		Set<ITypeBinding> structs = new TreeSet<ITypeBinding>(bindingsComparator);
		Set<ITypeBinding> unions = new TreeSet<ITypeBinding>(bindingsComparator);
		Set<ITypeBinding> templates = new TreeSet<ITypeBinding>(bindingsComparator);
	}
	
	private boolean fAcceptsPrivate;
	private boolean fAcceptsProtected;

	private boolean executeJavadocGeneration() {
		try {
			fAcceptsPrivate = fStore.getAccess().equals(fStore.PRIVATE);
			fAcceptsProtected = fStore.getAccess().equals(fStore.PROTECTED);
			
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
				"<frame src=\"modules-frame.html\" name=\"module\" title=\"Module description\" scrolling=\"yes\">\r\n" +
				"</frameset>\r\n" + 
				"<noframes>\r\n" + 
				"<h2>\r\n" + 
				"Frame Alert</h2>\r\n" + 
				"\r\n" + 
				"<p>\r\n" + 
				"This document is designed to be viewed using the frames feature. If you see this message, you are using a non-frame-capable web client.\r\n" + 
				"<br/>\r\n" + 
				"Link to<a href=\"modules-frame.html\">Non-frame version.</a>\r\n" + 
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
	
	private static String getHref(ICompilationUnitBinding unit) {
		return unit.getName().replace('.', '_') + ".html";
	}
	
	private static String getHref(ICompilationUnit unit) {
		return unit.getFullyQualifiedName().replace('.', '_') + ".html";
	}

	private void generateModulesList(Module module, Writer out) throws IOException {
		out.write("<li><a href=\"");
		out.write(getHref(module.unit));
		out.write("\" target=\"module\">");
		out.write(module.unit.getName());
		out.write("</li>\r\n");
	}
	
	private void generateModuleDescriptionsFrame(Set<Module> modules, Module module) throws IOException, JavaModelException {
		Writer out = writerFor(getHref(module.unit));
		out.write(
			"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\r\n" + 
			"<html>\r\n" + 
			"<head>\r\n" + 
			"<title>\r\n" + 
			"Module ");
		out.write(module.unit.getName());
		out.write(
			"</title>\r\n" + 
			"<link rel=\"stylesheet\" type=\"text/css\" href=\"stylesheet.css\" title=\"Style\">\r\n" + 
			"</head>\r\n" + 
			"<body>\r\n" + 
			"<div class=\"module\">");
		out.write(module.unit.getName());
		out.write(
			"</div>\r\n");
		
		writeDdoc(out, module.unit.getJavaElement());
		
		out.write("<ul>");
		
		// Public imports
		ICompilationUnitBinding[] publicImports = module.unit.getPublicImports();
		if (publicImports.length > 0) {
			Arrays.sort(publicImports, bindingsComparator);
			
			out.write("<li><span class=\"first_header\">Public imports</span>");
			out.write("<ul>");
			for(ICompilationUnitBinding a : publicImports) {
				out.write("<li><a href=\"");
				out.write(getHref(a));
				out.write("\">");
				out.write(a.getName());
				out.write("</a></li>");
			}
			out.write("</ul>");
			out.write("</li>");
		}
		
		writeVariables("Variables", "first_header", module.symbols.variables, modules, out);
		writeAliases("first_header", module.symbols.aliases, modules, out);
		writeTypedefs("first_header", module.symbols.typedefs, modules, out);
		writeEnums("first_header", module.symbols.enums, modules, out);
		writeFunctions("Functions", "first_header", module.symbols.functions, modules, out);
		writeAggregates("Structs", "first_header", module.symbols.structs, modules, out);
		writeAggregates("Unions", "first_header", module.symbols.unions, modules, out);
		writeAggregates("Classes", "first_header", module.symbols.classes, modules, out);
		writeAggregates("Interfaces", "first_header", module.symbols.interfaces, modules, out);
		
		out.write(
			"</body>\r\n" +
			"</html>");
		out.close();
	}
	
	private void writeFunctions(String title, String css, Set<IMethodBinding> functions, Set<Module> modules, Writer out) throws IOException, JavaModelException {
		if (!functions.isEmpty()) {
			out.write("<li><span class=\"");
			out.write(css);
			out.write("\">");
			out.write(title);
			out.write("</span>");
			out.write("<ul>");
			for(IMethodBinding a : functions) {
				out.write("<li>");
				
				writeModifiers(a, out);
				
				if (a.isConstructor()) {
					out.write("<span class=\"keyword\">this</span>");
				} else {
					writeBinding(a.getReturnType(), out);
					out.write(" ");
					writeAnchor(out, a, ASTNode.FUNCTION_DECLARATION);
				}
				
				if (a.isParameterizedMethod()) {
					out.write("(");
					ITemplateParameterBinding[] typeParameters = a.getTypeParameters();
					for (int i = 0; i < typeParameters.length; i++) {
						if (i != 0)
							out.write(", ");
						out.write(typeParameters[i].getName());
					}
					out.write(")");
				}
				
				out.write("(");
				
				IMethod m = (IMethod) a.getJavaElement();
				String[] parameterNames = m.getParameterNames();
				
				IBinding[] params = a.getParameterTypes();
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
	}
	
	private void writeVariables(String title, String css, Set<IVariableBinding> variables, Set<Module> modules, Writer out) throws IOException, JavaModelException {
		if (!variables.isEmpty()) {
			out.write("<li><span class=\"");
			out.write(css);
			out.write("\">");
			out.write(title);
			out.write("</span>");
			out.write("<ul>");
			for(IVariableBinding a : variables) {
				out.write("<li>");
				
				writeModifiers(a, out);
				writeAnchor(out, a, ASTNode.VARIABLE_DECLARATION);
				
				ITypeBinding binding = (ITypeBinding) a.getType();
				out.write(": ");
				writeBinding(binding, out);
				
				IJavaElement element = a.getJavaElement();
				writeDdoc(out, element);
				
				out.write("</li>");
			}
			out.write("</ul>");
			out.write("</li>");
		}
	}
	
	private void writeAliases(String css, Set<ITypeBinding> aliases, Set<Module> modules, Writer out) throws IOException, JavaModelException {
		if (!aliases.isEmpty()) {
			out.write("<li><span class=\"");
			out.write(css);
			out.write("\">");
			out.write("Aliases</span>");
			out.write("<ul>");
			for(ITypeBinding a : aliases) {
				out.write("<li>");
				
				writeModifiers(a, out);
				
				writeAnchor(out, a, ASTNode.ALIAS_DECLARATION);
				
				out.write(": ");
				
				IBinding binding = a.getAliasedSymbol();
				if (binding == null) {
					binding = a.getAliasedType();
				}
				
				writeBinding(binding, out);
				
				IJavaElement element = a.getJavaElement();
				writeDdoc(out, element);
				
				out.write("</li>");
			}
			out.write("</ul>");
			out.write("</li>");
		}
	}
	
	private void writeTypedefs(String css, Set<ITypeBinding> typedefs, Set<Module> modules, Writer out) throws IOException, JavaModelException {
		if (!typedefs.isEmpty()) {
			out.write("<li><span class=\"");
			out.write(css);
			out.write("\">");
			out.write("Typedefs</span>");
			out.write("<ul>");
			for(ITypeBinding a : typedefs) {
				out.write("<li>");
				
				writeModifiers(a, out);
				
				writeAnchor(out, a, ASTNode.TYPEDEF_DECLARATION);
				
				ITypeBinding binding = a.getTypedefedType();
				out.write(": ");
				writeBinding(binding, out);
				
				IJavaElement element = a.getJavaElement();
				writeDdoc(out, element);
				
				out.write("</li>");
			}
			out.write("</ul>");
			out.write("</li>");
		}
	}
	
	private void writeEnums(String css, Set<ITypeBinding> enums, Set<Module> modules, Writer out) throws IOException, JavaModelException {
		if (!enums.isEmpty()) {
			out.write("<li><span class=\"");
			out.write(css);
			out.write("\">");
			out.write("Enums</span>");
			out.write("<ul>");
			for(ITypeBinding a : enums) {
				ITypeBinding binding = a.getSuperclass();
				if (a.getName() != null) {
					out.write("<li>");
					writeModifiers(a, out);					
					writeAnchor(out, a, ASTNode.ENUM_DECLARATION);
				} else {
					out.write("<li>");
					writeModifiers(a, out);
					out.write("(anynomous)");
				}
				out.write(": ");
				writeBinding(binding, out);
				
				IJavaElement element = a.getJavaElement();
				writeDdoc(out, element);
				
				IVariableBinding[] fields = a.getDeclaredFields();
				if (fields != null && fields.length > 0) {
					Arrays.sort(fields, bindingsComparator);
					out.write("<ul>");
					for(IVariableBinding var : fields) {
						out.write("<li><span class=\"symbol variable\">");
						out.write(var.getName());
						out.write("</span>");
						
						writeDdoc(out, var.getJavaElement());
						
						out.write("</li>");
					}
					out.write("</ul>");
				}
				
				out.write("</li>");
			}
			out.write("</ul>");
			out.write("</li>");
		}
	}
	
	private void writeAggregates(String title, String css, Set<ITypeBinding> aggs, Set<Module> modules, Writer out) throws IOException, JavaModelException {
		if (!aggs.isEmpty()) {
			out.write("<li><span class=\"");
			out.write(css);
			out.write("\">");
			out.write(title);
			out.write("</span>");
			out.write("<ul>");
			writeAggregate(modules, aggs, out);
			out.write("</ul>");
			out.write("</li>");
		}
	}

	private void writeAggregate(Set<Module> modules, Set<ITypeBinding> agg, Writer out) throws IOException, JavaModelException {
		for(ITypeBinding a : agg) {
			writeAggregate(modules, out, a);
		}
	}

	private void writeAggregate(Set<Module> modules, Writer out, ITypeBinding a) throws IOException, JavaModelException {
		out.write("<li>");
		
		writeModifiers(a, out);
		
		writeAnchor(out, a, ASTNode.AGGREGATE_DECLARATION, getKind(a));
		
		ITypeBinding superclass = a.getSuperclass();
		ITypeBinding[] interfaces = a.getInterfaces();
		
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
		
		IJavaElement element = a.getJavaElement();
		writeDdoc(out, element);
		
		out.write("<ul>");
		
		if (a.isClass()) {
			out.write("<li><span class=\"second_header\">Super hierarchy:</span> ");
			
			Stack<ITypeBinding> hierarchy = getSuperclassHierarchy(a);
			
			while(!hierarchy.isEmpty()) {
				writeBinding(hierarchy.pop(), out);
				out.write(" -> ");
			}
			
			out.write("<span class=\"symbol class\">");
			out.write(a.getName());
			out.write("</span>");
			
			out.write("</li>");
		}
		
		Set<ITypeBinding> implementedInterfaces = getImplementedInterfaces(modules, a);
		
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
		
		if (a.isClass()) {
			Set<ITypeBinding> subclasses = getSubclasses(modules, a);
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
		
		if (a.isInterface()) {
			Set<ITypeBinding> subinterfaces = getSubinterfaces(modules, a);
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
		
		if (a.isInterface()) {
			Set<ITypeBinding> implementing = getImplementors(modules, a);
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
		
		Set<IVariableBinding> fields = getDeclaredFields(a);
		writeVariables("Fields", "second_header", fields, modules, out);
		
		Set<IMethodBinding> constructors = getConstructors(a);
		writeFunctions("Constructors", "second_header", constructors, modules, out);

		Set<IMethodBinding> methods = getMethods(a);
		writeFunctions("Methods", "second_header", methods, modules, out);
		
		// Inherited methods
		if (a.getSuperclass() != null) {
			out.write("<li><span class=\"second_header\">Inherited methods</span>");
			out.write("<ul>");
			
			Stack<ITypeBinding> hierarchy = getSuperclassHierarchy(a);
			while(!hierarchy.isEmpty()) {
				writeInheritedMethods(hierarchy.pop(), out);
			}
			
			for(ITypeBinding t : implementedInterfaces) {
				writeInheritedMethods(t, out);
			}
			
			out.write("</ul>");
			out.write("</li>");
		}
		
		writeAliases("second_header", getSubAliases(a), modules, out);
		writeTypedefs("second_header", getSubTypedefs(a), modules, out);
		writeEnums("second_header", getSubEnums(a), modules, out);
		writeAggregates("Structs", "second_header", getSubStructs(a), modules, out);
		writeAggregates("Unions", "second_header", getSubUnions(a), modules, out);
		writeAggregates("Classes", "second_header", getSubClasses(a), modules, out);
		writeAggregates("Interfaces", "second_header", getSubInterfaces(a), modules, out);
		
		out.write("</ul>");
		out.write("</li>");
	}
	
	private void writeModifiers(IBinding a, Writer out) throws IOException {
		long modifiers = a.getModifiers();
		if ((modifiers & Flags.AccFinal) != 0) {
			out.write("<span class=\"keyword\">final</span> ");
		}
		if ((modifiers & Flags.AccAbstract) != 0) {
			out.write("<span class=\"keyword\">abstract</span> ");
		}
		if ((modifiers & Flags.AccStatic) != 0) {
			out.write("<span class=\"keyword\">static</span> ");
		}
		if ((modifiers & Flags.AccConst) != 0) {
			out.write("<span class=\"keyword\">const</span> ");
		}
	}


	private Set<ITypeBinding> getSubAliases(ITypeBinding a) {
		Set<ITypeBinding> result = new TreeSet<ITypeBinding>(bindingsComparator);
		for(ITypeBinding t : a.getDeclaredTypes()) {
			if (passesAccess(t) && t.isAlias())
				result.add(t);
		}
		return result;
	}

	private Set<ITypeBinding> getSubTypedefs(ITypeBinding a) {
		Set<ITypeBinding> result = new TreeSet<ITypeBinding>(bindingsComparator);
		for(ITypeBinding t : a.getDeclaredTypes()) {
			if (passesAccess(t) && t.isTypedef())
				result.add(t);
		}
		return result;
	}
	
	private Set<ITypeBinding> getSubEnums(ITypeBinding a) {
		Set<ITypeBinding> result = new TreeSet<ITypeBinding>(bindingsComparator);
		for(ITypeBinding t : a.getDeclaredTypes()) {
			if (passesAccess(t) && t.isEnum())
				result.add(t);
		}
		return result;
	}
	
	private Set<ITypeBinding> getSubStructs(ITypeBinding a) {
		Set<ITypeBinding> result = new TreeSet<ITypeBinding>(bindingsComparator);
		for(ITypeBinding t : a.getDeclaredTypes()) {
			if (passesAccess(t) && t.isStruct())
				result.add(t);
		}
		return result;
	}
	
	private Set<ITypeBinding> getSubUnions(ITypeBinding a) {
		Set<ITypeBinding> result = new TreeSet<ITypeBinding>(bindingsComparator);
		for(ITypeBinding t : a.getDeclaredTypes()) {
			if (passesAccess(t) && t.isUnion())
				result.add(t);
		}
		return result;
	}
	
	private Set<ITypeBinding> getSubClasses(ITypeBinding a) {
		Set<ITypeBinding> result = new TreeSet<ITypeBinding>(bindingsComparator);
		for(ITypeBinding t : a.getDeclaredTypes()) {
			if (passesAccess(t) && t.isClass())
				result.add(t);
		}
		return result;
	}
	
	private Set<ITypeBinding> getSubInterfaces(ITypeBinding a) {
		Set<ITypeBinding> result = new TreeSet<ITypeBinding>(bindingsComparator);
		for(ITypeBinding t : a.getDeclaredTypes()) {
			if (passesAccess(t) && t.isInterface())
				result.add(t);
		}
		return result;
	}

	private Set<IVariableBinding> getDeclaredFields(ITypeBinding a) {
		Set<IVariableBinding> vars = new TreeSet<IVariableBinding>(bindingsComparator);
		for(IVariableBinding var : a.getDeclaredFields()) {
			if (!passesAccess(var))
				continue;
			vars.add(var);
		}
		return vars;
	}

	private Set<IMethodBinding> getMethods(ITypeBinding a) {
		Set<IMethodBinding> methods = new TreeSet<IMethodBinding>(bindingsComparator);
		for(IMethodBinding method : a.getDeclaredMethods()) {
			if (!passesAccess(method))
				continue;
			
			if (!method.isConstructor()) {
				methods.add(method);
			}
		}
		return methods;
	}

	private Set<IMethodBinding> getConstructors(ITypeBinding a) {
		Set<IMethodBinding> methods = new TreeSet<IMethodBinding>(bindingsComparator);
		for(IMethodBinding method : a.getDeclaredMethods()) {
			if (!passesAccess(method))
				continue;
			
			if (method.isConstructor()) {
				methods.add(method);
			}
		}
		return methods;
	}

	private void writeInheritedMethods(ITypeBinding t, Writer out) throws IOException {
		Set<IMethodBinding> ms = getMethods(t);
		if (ms.isEmpty())
			return;
		
		out.write("<li>from ");
		writeBinding(t, out);
		out.write(": ");
		
		boolean writtenComma = false;
		for(IMethodBinding m : ms) {
			if (!passesAccess(m))
				continue;
			
			if (writtenComma)
				out.write(", ");
			
			out.write("<a href=\"");
			writeHref(m, out);
			out.write("\" class=\"symbol function\">");
			out.write(m.getName());
			out.write("</a>");
			
			writtenComma = true;
		}
		out.write("</li>");
	}

	private Stack<ITypeBinding> getSuperclassHierarchy(ITypeBinding a) {
		Stack<ITypeBinding> hierarchy = new Stack<ITypeBinding>();
		ITypeBinding current = a.getSuperclass();
		while(current != null) {
			hierarchy.push(current);
			current = current.getSuperclass();
		}
		return hierarchy;
	}

	private Kind getKind(ITypeBinding a) {
		if (a.isClass())
			return AggregateDeclaration.Kind.CLASS;
		else if (a.isInterface())
			return AggregateDeclaration.Kind.INTERFACE;
		else if (a.isStruct())
			return AggregateDeclaration.Kind.STRUCT;
		else if (a.isUnion())
			return AggregateDeclaration.Kind.UNION;
		throw new IllegalStateException();
	}


	private Set<ITypeBinding> getImplementedInterfaces(Set<Module> modules, ITypeBinding binding) {
		Set<ITypeBinding> result = new TreeSet<ITypeBinding>(bindingsComparator);
		fillImplementedInterfaces(binding.getSuperclass(), result);
		fillImplementedInterfaces(binding.getInterfaces(), result);
		return result;
	}
	
	private Set<ITypeBinding> getSubclasses(Set<Module> modules, ITypeBinding target) {
		Set<ITypeBinding> result = new TreeSet<ITypeBinding>(bindingsComparator);
		for(Module module : modules) {
			for(ITypeBinding binding : module.symbols.classes) {
				IBinding superclass = binding.getSuperclass();
				if (superclass != null && superclass.isEqualTo(target)) {
					result.add(binding);
				}
			}
		}
		return result;
	}

	private Set<ITypeBinding> getSubinterfaces(Set<Module> modules, ITypeBinding target) {
		Set<ITypeBinding> result = new TreeSet<ITypeBinding>(bindingsComparator);
		for(Module module : modules) {
			for(ITypeBinding binding : module.symbols.interfaces) {
				ITypeBinding[] interfaces = binding.getInterfaces();
				if (interfaces != null) {
					for(ITypeBinding inter : interfaces) {
						if (inter.isEqualTo(target)) {
							result.add(binding);
							break;
						}
					}
				}
			}
		}
		return result;
	}

	private Set<ITypeBinding> getImplementors(Set<Module> modules, ITypeBinding target) {
		Set<ITypeBinding> result = new TreeSet<ITypeBinding>(bindingsComparator);
		for(Module module : modules) {
			for(ITypeBinding binding : module.symbols.classes) {
				if (binding != null) {
					if (getImplementedInterfaces(modules, binding).contains(target)) {
						result.add(binding);
					}
				}
			}
		}
		return result;
	}

	private void fillImplementedInterfaces(ITypeBinding[] interfaces, Set<ITypeBinding> implementedInterfaces) {
		if (interfaces == null)
			return;
		
		for(ITypeBinding inter : interfaces) {
			implementedInterfaces.add(inter);
			fillImplementedInterfaces(inter.getInterfaces(), implementedInterfaces);
		}
	}
	
	private void fillImplementedInterfaces(ITypeBinding type, Set<ITypeBinding> implementedInterfaces) {
		if (type == null) {
			return;
		}
		
		if (type.isInterface()) {
			implementedInterfaces.add(type);
		}
		
		IBinding superclass = type.getSuperclass();
		if (superclass instanceof ITypeBinding) {
			fillImplementedInterfaces((ITypeBinding) superclass, implementedInterfaces);
		}
		fillImplementedInterfaces(type.getInterfaces(), implementedInterfaces);
	}


	private void writeAnchor(Writer out, IBinding binding, int nodeType) throws IOException {
		writeAnchor(out, binding, nodeType, null);
	}

	private void writeAnchor(Writer out, IBinding binding, int nodeType, AggregateDeclaration.Kind kind) throws IOException {
		out.write("<a name=\"");
		out.write(binding.getKey());
		out.write("\"");
		out.write(" class=\"symbol ");
		out.write(classFor(nodeType, kind));
		out.write("\"");
		out.write(">");
		out.write(binding.getName());
		out.write("</a>");
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
		if (element instanceof ICompilationUnit) {
			ICompilationUnit unit = (ICompilationUnit) element;
			IPackageDeclaration[] packageDeclarations = unit.getPackageDeclarations();
			if (packageDeclarations.length > 0) {
				writeDdoc(out, packageDeclarations[0]);
			}
			return;
		}
		
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
		if (binding == null) {
			System.out.println(123456);
			return;
		}
		
		if (binding.isPrimitive()) {
			out.write("<span class=\"keyword\">");
			out.write(binding.getName());
			out.write("</span>");
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
			out.write(" <span class=\"keyword\">delegate</span>(");
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
			out.write(" <span class=\"keyword\">function</span>(");
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
		} else if (binding.isTemplateParameter()) {
			out.write(binding.getName());
		} else {
			writeLink(binding, out);
		}
	}

	private void writeLink(ITypeBinding binding, Writer out) throws IOException {
		// Might happen with references to private symbols
		if (!passesAccess(binding)) {
			out.write(binding.getName());
			return;
		}
		
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
			out.write(" class=\"symbol enum\"");
		} else if (binding.isTemplate()) {
			out.write(" class=\"symbol template\"");
		} else if (binding.isAlias()) {
			out.write(" class=\"symbol alias\"");
		} else if (binding.isTypedef()) {
			out.write(" class=\"symbol typedef\"");
		}
		
		out.write(" target=\"module\">");
		out.write(binding.getName());
		out.write("</a>");
	}

	private void writeHref(IBinding binding, Writer out) throws IOException {
		IJavaElement element = binding.getJavaElement();
		if (element == null) {
			System.out.println(123456);
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
		
		out.write(getHref(unit));
		out.write("#");
		out.write(binding.getKey());
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
			Module module = new Module();
			collect(unit, module);
			modules.add(module);
		} else if (element instanceof IParent) {
			collect(((IParent) element).getChildren(), modules);
		}
	}
	
	private void collect(ICompilationUnit unit, Module module) throws JavaModelException {
		CompilationUnit ast = unit.getResolvedAtCompileTime(AST.D1);
		ICompilationUnitBinding unitBinding = ast.resolveBinding();
		module.unit = unitBinding;
		
		for(IMethodBinding a : unitBinding.getDeclaredFunctions()) {
			if (!passesAccess(a))
				continue;
			
			module.symbols.functions.add(a);
		}
		
		for(IVariableBinding a : unitBinding.getDeclaredVariables()) {
			if (!passesAccess(a))
				continue;
			
			module.symbols.variables.add(a);	
		}
		
		for(ITypeBinding a : unitBinding.getDeclaredTypes()) {
			if (!passesAccess(a))
				continue;
			
			if (a.isAlias()) {
				module.symbols.aliases.add(a);
			} else if (a.isTypedef()) {
				module.symbols.typedefs.add(a);
			} else if (a.isEnum()) {
				module.symbols.enums.add(a);
			} else if (a.isClass()) {
				module.symbols.classes.add(a);
			} else if (a.isInterface()) {
				module.symbols.interfaces.add(a);
			} else if (a.isStruct()) {
				module.symbols.structs.add(a);
			} else if (a.isUnion()) {
				module.symbols.unions.add(a);
			} else if (a.isTemplate()) {
				module.symbols.templates.add(a);
			}
		}
	}
	
	private boolean passesAccess(IBinding a) {
		long modifiers = a.getModifiers();
		if ((modifiers & Flags.AccPrivate) == 0 
			&& (modifiers & Flags.AccProtected) == 0
			&& (modifiers & Flags.AccPackage) == 0) {
			return true;
		}
		
		if ((modifiers & Flags.AccPublic) != 0)
			return true;
			
		if ((modifiers & Flags.AccProtected) != 0) {
			return fAcceptsProtected;
		}
		
		return fAcceptsPrivate;
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
				".variable { color: #0000B0; }\r\n" +
				".alias { color: #008080; }\r\n" +
				".typedef { color: #800000; }\r\n" +
				".function { color: #804040; }\r\n" +
				".enum { color: #644632; }\r\n" +
				".class { bold; color: #005032;  }\r\n" +
				".struct { color: #003250;  }\r\n" +
				".union { color: #325000;  }\r\n" +
				".interface { color: #323F70;  }\r\n" +
				".keyword { color: #320020; }");
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
