package descent.internal.ui.javadocexport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
import descent.core.IJavaElement;
import descent.core.IJavaProject;
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

public class JavadocWizard extends Wizard implements IExportWizard {

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
	
	private static final String ID_JAVADOC_PROCESS_TYPE= "descent.ui.javadocProcess"; //$NON-NLS-1$

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

	private boolean executeJavadocGeneration() {
		try {
			// 1. Generate frameset in index.html
			generateIndex();
			
			// 2. Generate stylesheet
			generateStylesheet();
			
			// 3. Generate packages list
			generatePackagesList();
			
			// 4. Generate all symbols frame
			generateAllSymbolsFrame();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;

	}

	private void generateIndex() throws IOException {
		Writer out = writerFor("index.html");
		out.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\" \"http://www.w3.org/TR/html4/frameset.dtd\">\r\n" + 
				"<!--NewPage-->\r\n" + 
				"<HTML>\r\n" + 
				"<HEAD>\r\n" + 
				"<meta name=\"collection\" content=\"exclude\">\r\n" + 
				"<TITLE>\r\n");
		out.write(fStore.getTitle());
		out.write("</TITLE>\r\n" + 
				"<SCRIPT type=\"text/javascript\">\r\n" + 
				"   targetPage = \"\" + window.location.search;\r\n" + 
				"   if (targetPage != \"\" && targetPage != \"undefined\")\r\n" + 
				"      targetPage = targetPage.substring(1);\r\n" + 
				"   if (targetPage.indexOf(\":\") != -1)\r\n" + 
				"      targetPage = \"undefined\";\r\n" + 
				"   function loadFrames() {\r\n" + 
				"      if (targetPage != \"\" && targetPage != \"undefined\")\r\n" + 
				"	  top.classFrame.location = top.targetPage;\r\n" + 
				"     }\r\n" + 
				"</SCRIPT>\r\n" + 
				"<NOSCRIPT>\r\n" + 
				"</NOSCRIPT>\r\n" + 
				"</HEAD>\r\n" + 
				"<FRAMESET cols=\"20%,80%\" title=\"\" onLoad=\"top.loadFrames()\">\r\n" + 
				"<FRAMESET rows=\"30%,70%\" title=\"\" onLoad=\"top.loadFrames()\">\r\n" + 
				"<FRAME src=\"overview-frame.html\" name=\"packageListFrame\" title=\"All Packages\">\r\n" + 
				"\r\n" + 
				"<FRAME src=\"allsymbols-frame.html\" name=\"packageFrame\" title=\"All symbols\">\r\n" + 
				"</FRAMESET>\r\n" + 
				"<FRAME src=\"overview-summary.html\" name=\"classFrame\" title=\"Module descriptions\" scrolling=\"yes\">\r\n" + 
				"<NOFRAMES>\r\n" + 
				"<H2>\r\n" + 
				"Frame Alert</H2>\r\n" + 
				"\r\n" + 
				"<P>\r\n" + 
				"This document is designed to be viewed using the frames feature. If you see this message, you are using a non-frame-capable web client.\r\n" + 
				"<BR>\r\n" + 
				"Link to<A HREF=\"overview-summary.html\">Non-frame version.</A>\r\n" + 
				"</NOFRAMES>\r\n" + 
				"</FRAMESET>\r\n" + 
				"</HTML>");
		out.close();
	}
	
	private void generatePackagesList() throws IOException {
		Writer out = writerFor("overview-frame.html");
		out.write(
			"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\r\n" + 
			"<HTML>\r\n" + 
			"<HEAD>\r\n" + 
			"<meta name=\"collection\" content=\"exclude\">\r\n" + 
			"\r\n" + 
			"<TITLE>\r\n" + 
			"Overview (");
		out.write(fStore.getTitle());
		out.write("\r\n" + 
			"</TITLE>\r\n" + 
			"\r\n" + 
			"<META NAME=\"keywords\" CONTENT=\"Overview, Java<sup><font size=-2>TM</font></sup> 2 Platform Standard Edition 5.0<br>API Specification\">\r\n" + 
			"\r\n" + 
			"<LINK REL =\"stylesheet\" TYPE=\"text/css\" HREF=\"stylesheet.css\" TITLE=\"Style\">\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"</HEAD>\r\n" + 
			"\r\n" + 
			"<BODY BGCOLOR=\"white\">\r\n" + 
			"\r\n" + 
			"<TABLE BORDER=\"0\" WIDTH=\"100%\" SUMMARY=\"\">\r\n" + 
			"<TR>\r\n" + 
			"<TH ALIGN=\"left\" NOWRAP><FONT size=\"+1\" CLASS=\"FrameTitleFont\">\r\n" + 
			"<B>");
		out.write(fStore.getTitle());
		out.write("</B></FONT></TH>\r\n" + 
			"</TR>\r\n" + 
			"</TABLE>\r\n" + 
			"\r\n" + 
			"<TABLE BORDER=\"0\" WIDTH=\"100%\" SUMMARY=\"\">\r\n" + 
			"<TR>\r\n" + 
			"\r\n" + 
			"<TD NOWRAP><FONT CLASS=\"FrameItemFont\"><A HREF=\"allsymbols-frame.html\" target=\"packageFrame\">All Symbols</A></FONT>\r\n" + 
			"<P>\r\n" + 
			"<FONT size=\"+1\" CLASS=\"FrameHeadingFont\">\r\n" + 
			"Packages</FONT>\r\n" + 
 			"\r\n");
		
		IJavaElement[] elements = fStore.getSourceElements();
		
		// Sort packages by name
		Arrays.sort(elements, new Comparator<IJavaElement>() {
			public int compare(IJavaElement o1, IJavaElement o2) {
				return o1.getElementName().compareTo(o2.getElementName());
			}
		});
		
		generatePackagesList(elements, out);
		
		out.write( 
			"<BR>\r\n" + 
			"</TD>\r\n" + 
			"</TR>\r\n" + 
			"</TABLE>\r\n" + 
			"\r\n" + 
			"<P>\r\n" + 
			"&nbsp;\r\n" + 
			"<script language=\"JavaScript\" src=\"/js/omi/jsc/s_code_remote.js\"></script></BODY>\r\n" + 
			"</HTML>\r\n" + 
			""
		);
		out.close();
	}

	private void generatePackagesList(IJavaElement[] elements, Writer out) throws IOException {
		for(IJavaElement element : elements) {
			generatePackagesList(element, out);
		}
	}

	private void generatePackagesList(IJavaElement element, Writer out) throws IOException {
		if (element.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
			String[] ids = element.getElementName().split("\\.");
			
			out.write("<BR>\r\n"); 
			out.write("<FONT CLASS=\"FrameItemFont\"><A HREF=\"");
			for (int i = 0; i < ids.length; i++) {
				if (i != 0) {
					out.write(File.separatorChar);
				}
				out.write(ids[i]);
			}
			out.write("/package-frame.html");
			out.write("\" target=\"packageFrame\">");
			for (int i = 0; i < ids.length; i++) {
				if (i != 0) {
					out.write('.');
				}
				out.write(ids[i]);
			}
			out.write("</A></FONT>\r\n");
			return;
		}
	}
	
	private void generateAllSymbolsFrame() throws IOException, JavaModelException {
		Writer out = writerFor("allsymbols-frame.html");
		out.write(
			"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\r\n" + 
			"<HTML>\r\n" + 
			"<HEAD>\r\n" + 
			"<meta name=\"collection\" content=\"exclude\">\r\n" + 
			"\r\n" + 
			"<TITLE>\r\n" + 
			"All Symbols (");
		out.write(fStore.getTitle());
		out.write("</TITLE>\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"<LINK REL =\"stylesheet\" TYPE=\"text/css\" HREF=\"stylesheet.css\" TITLE=\"Style\">\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"</HEAD>\r\n" + 
			"\r\n" + 
			"<BODY BGCOLOR=\"white\">\r\n" + 
			"<FONT size=\"+1\" CLASS=\"FrameHeadingFont\">\r\n" + 
			"<B>All Symbols</B></FONT>\r\n" + 
			"<BR>\r\n" + 
			"\r\n" + 
			"<TABLE BORDER=\"0\" WIDTH=\"100%\" SUMMARY=\"\">\r\n" + 
			"<TR>\r\n" + 
			"<TD NOWRAP><FONT CLASS=\"FrameItemFont\">");
		
		generateAllSymbolsFrame(fStore.getSourceElements(), out);
		
		out.write(
			"<BR>\r\n" + 
			"</FONT></TD>\r\n" + 
			"</TR>\r\n" + 
			"</TABLE>\r\n" + 
			"\r\n" + 
			"</BODY>\r\n" + 
			"</HTML>\r\n" + 
			""
		);
		out.close();
	}
	
	private void generateAllSymbolsFrame(IJavaElement[] elements, Writer out) throws IOException, JavaModelException {
		List<ASTNode> declarations = new ArrayList<ASTNode>();
		collect(elements, declarations);
		
		Collections.sort(declarations, new Comparator<ASTNode>() {
			public int compare(ASTNode o1, ASTNode o2) {
				return getName(o1).compareTo(getName(o2));
			}
		});
		
		for(ASTNode node : declarations) {
			out.write("<BR>\r\n"); 
			out.write("<A HREF=\"javax/swing/AbstractButton.html\" title=\"class in javax.swing\" target=\"classFrame\">");
			out.write(getName(node));
			out.write("</A>\r\n");	
		}
	}
	
	private String getName(ASTNode decl) {
		switch(decl.getNodeType()) {
		case ASTNode.FUNCTION_DECLARATION:
			return ((FunctionDeclaration) decl).getName().getIdentifier();
		case ASTNode.AGGREGATE_DECLARATION:
			return ((AggregateDeclaration) decl).getName().getIdentifier();
		case ASTNode.ALIAS_DECLARATION_FRAGMENT:
			return ((AliasDeclarationFragment) decl).getName().getIdentifier();
		case ASTNode.ENUM_DECLARATION:
			return ((EnumDeclaration) decl).getName().getIdentifier();
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
	
	private void collect(IJavaElement[] elements, List<ASTNode> declarations) throws JavaModelException {
		for(IJavaElement element : elements) {
			collect(element, declarations);
		}
	}
	
	private void collect(IJavaElement element, List<ASTNode> declarations) throws JavaModelException{
		if (element.getElementType() == IJavaElement.COMPILATION_UNIT ||
			element.getElementType() == IJavaElement.CLASS_FILE) {
			collect((ICompilationUnit) element, declarations);
		} else if (element instanceof IParent) {
			collect(((IParent) element).getChildren(), declarations);
		}
	}
	
	private void collect(ICompilationUnit unit, List<ASTNode> declarations) throws JavaModelException {
		CompilationUnit ast = unit.getResolvedAtCompileTime(AST.D1);
		for(Declaration decl : ast.declarations()) {
			switch(decl.getNodeType()) {
			case ASTNode.ALIAS_DECLARATION:
				AliasDeclaration alias = (AliasDeclaration) decl;
				for(AliasDeclarationFragment fragment : alias.fragments()) {
					declarations.add(fragment);
				}
				break;
			case ASTNode.TYPEDEF_DECLARATION:
				TypedefDeclaration typedef = (TypedefDeclaration) decl;
				for(TypedefDeclarationFragment fragment : typedef.fragments()) {
					declarations.add(fragment);
				}
				break;
			case ASTNode.VARIABLE_DECLARATION:
				VariableDeclaration var = (VariableDeclaration) decl;
				for(VariableDeclarationFragment fragment : var.fragments()) {
					declarations.add(fragment);
				}
				break;
			case ASTNode.ENUM_DECLARATION:
			case ASTNode.TEMPLATE_DECLARATION:
			case ASTNode.FUNCTION_DECLARATION:
			case ASTNode.AGGREGATE_DECLARATION:
				declarations.add(decl);
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
				"/* Define colors, fonts and other style attributes here to override the defaults */\r\n" + 
				"\r\n" + 
				"/* Page background color */\r\n" + 
				"body { background-color: #FFFFFF }\r\n" + 
				"\r\n" + 
				"/* Headings */\r\n" + 
				"h1 { font-size: 145% }\r\n" + 
				"\r\n" + 
				"/* Table colors */\r\n" + 
				".TableHeadingColor     { background: #CCCCFF } /* Dark mauve */\r\n" + 
				".TableSubHeadingColor  { background: #EEEEFF } /* Light mauve */\r\n" + 
				".TableRowColor         { background: #FFFFFF } /* White */\r\n" + 
				"\r\n" + 
				"/* Font used in left-hand frame lists */\r\n" + 
				".FrameTitleFont   { font-size: 100%; font-family: Helvetica, Arial, sans-serif }\r\n" + 
				".FrameHeadingFont { font-size:  90%; font-family: Helvetica, Arial, sans-serif }\r\n" + 
				".FrameItemFont    { font-size:  90%; font-family: Helvetica, Arial, sans-serif }\r\n" + 
				"\r\n" + 
				"/* Navigation bar fonts and colors */\r\n" + 
				".NavBarCell1    { background-color:#EEEEFF;} /* Light mauve */\r\n" + 
				".NavBarCell1Rev { background-color:#00008B;} /* Dark Blue */\r\n" + 
				".NavBarFont1    { font-family: Arial, Helvetica, sans-serif; color:#000000;}\r\n" + 
				".NavBarFont1Rev { font-family: Arial, Helvetica, sans-serif; color:#FFFFFF;}\r\n" + 
				"\r\n" + 
				".NavBarCell2    { font-family: Arial, Helvetica, sans-serif; background-color:#FFFFFF;}\r\n" + 
				".NavBarCell3    { font-family: Arial, Helvetica, sans-serif; background-color:#FFFFFF;}\r\n" + 
				"");
		out.close();
	}
	
	private Writer writerFor(String file) throws IOException {
		return new BufferedWriter(new FileWriter(new File(fDestination.toOSString(), file)));
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
