package descent.internal.codeassist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import descent.core.Flags;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.JavaModelException;
import descent.core.WorkingCopyOwner;
import descent.core.compiler.CharOperation;
import descent.core.dom.ASTNode;
import descent.core.dom.CompilationUnitResolver;
import descent.internal.compiler.env.AccessRestriction;
import descent.internal.compiler.env.ICompilationUnit;
import descent.internal.compiler.impl.CompilerOptions;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.AliasDeclaration;
import descent.internal.compiler.parser.AlignDeclaration;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.Chars;
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.ConditionalDeclaration;
import descent.internal.compiler.parser.CtorDeclaration;
import descent.internal.compiler.parser.Declaration;
import descent.internal.compiler.parser.DotVarExp;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.EnumDeclaration;
import descent.internal.compiler.parser.EnumMember;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Import;
import descent.internal.compiler.parser.InterfaceDeclaration;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.NewExp;
import descent.internal.compiler.parser.ProtDeclaration;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.StorageClassDeclaration;
import descent.internal.compiler.parser.StructDeclaration;
import descent.internal.compiler.parser.TemplateDeclaration;
import descent.internal.compiler.parser.Token;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeExp;
import descent.internal.compiler.parser.TypedefDeclaration;
import descent.internal.compiler.parser.UnionDeclaration;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.compiler.parser.VarExp;
import descent.internal.compiler.parser.ast.AstVisitorAdapter;
import descent.internal.core.InternalSignature;
import descent.internal.core.JavaElement;
import descent.internal.core.JavaProject;
import descent.internal.core.LocalVariable;
import descent.internal.core.SearchableEnvironment;
import descent.internal.core.util.Util;

/*
 * For now, don't take the JDT approach: let's parse, visit and see where
 * if the node falls between the given ranges.
 * 
 * TODO: don't use visiting strategy, improve the custom selection parser to improve
 * performance.
 */
public class SelectionEngine extends AstVisitorAdapter {

	private final static IJavaElement[] NO_ELEMENTS = new IJavaElement[0];

	IJavaProject javaProject;
	WorkingCopyOwner owner;
	Map settings;
	CompilerOptions compilerOptions;

	int offset;
	int length;
	ICompilationUnit unit;
	Module module;
	SemanticContext context;
	ASTNodeEncoder encoder;
	List<IJavaElement> selectedElements;
	InternalSignature internalSignature;

	public SelectionEngine(Map settings, IJavaProject javaProject,
			WorkingCopyOwner owner) {
		this.javaProject = javaProject;
		this.owner = owner;
		this.settings = settings;
		this.compilerOptions = new CompilerOptions(settings);
		this.internalSignature = new InternalSignature(javaProject);
	}

	public IJavaElement[] select(ICompilationUnit sourceUnit, final int offset,
			final int length) {
		this.offset = offset;
		this.length = length;
		this.unit = sourceUnit;
		this.selectedElements = new ArrayList<IJavaElement>();

		try {
			char[] contents = sourceUnit.getContents();

			SelectionParser parser = new SelectionParser(
					javaProject.getApiLevel(),
					contents, 
					sourceUnit.getFileName());
			parser.selectionOffset = this.offset;
			parser.selectionLength = this.length;
			
			encoder = parser.encoder;
			
			parser.nextToken();

			module = parser.parseModuleObj();

			if (parser.commentToken != null) {
				final char[] tok = extractToken(parser.commentToken, offset);
				if (tok == null) {
					return NO_ELEMENTS;
				}

				SearchableEnvironment environment = ((JavaProject) javaProject)
						.newSearchableNameEnvironment(owner);
				environment.findDeclarations(tok, new ISearchRequestor() {
					public void acceptCompilationUnit(char[] fullyQualifiedName) {
					}

					public void acceptField(char[] packageName, char[] name,
							char[] typeName, char[][] enclosingTypeNames,
							long modifiers, int declarationStart, AccessRestriction accessRestriction) {
						addBinarySearch(packageName, name, declarationStart);
					}

					public void acceptMethod(char[] packageName, char[] name,
							char[][] enclosingTypeNames, char[] signature,
							char[] templateParametersSignature,
							long modifiers, int declarationStart, AccessRestriction accessRestriction) {
						
						addBinarySearch(packageName, name, declarationStart);
					}

					public void acceptPackage(char[] packageName) {
					}

					public void acceptType(char[] packageName, char[] typeName,
							char[] templateParametersSignature,
							char[][] enclosingTypeNames, long modifiers,
							int declarationStart,
							AccessRestriction accessRestriction) {
						
						addBinarySearch(packageName, typeName, declarationStart);
					}
					
					private void addBinarySearch(char[] packageName, char[] name, int declarationStart) {
						if (!CharOperation.equals(tok, name)) {
							return;
						}
						
						try {
							descent.core.ICompilationUnit unit = internalSignature.getCompilationUnit(new String(packageName));
							if (unit != null) {
								IJavaElement element = internalSignature.binarySearch(unit, declarationStart, declarationStart);
								if (element != null) {
									addJavaElement(element);
								}
							}
						} catch (JavaModelException e) {
							Util.log(e);
						}
					}
					
				});
			} else {
				module.moduleName = sourceUnit.getFullyQualifiedName();
				module.accept(this);
			}
			return selectedElements.toArray(new IJavaElement[selectedElements
					.size()]);
		} catch (JavaModelException e) {
			Util.log(e);
			return NO_ELEMENTS;
		}
	}
	
	private void doSemantic() {
		if (context == null) {
			try {
				context = CompilationUnitResolver.resolve(module, javaProject,
						owner, encoder);
			} catch (JavaModelException e) {
				Util.log(e);
			}
		}
	}

	private char[] extractToken(Token token, int offset) {
		char[] sourceString = token.sourceString;

		int start = offset - token.ptr;
		while (0 <= start && start < sourceString.length && Chars.isidchar(sourceString[start])) {
			start--;
		}
		start++;
		if (start < sourceString.length
				&& !Chars.isidstart(sourceString[start])) {
			return null;
		}

		int end = offset - token.ptr;
		while (end < sourceString.length && Chars.isidchar(sourceString[end])) {
			end++;
		}

		return CharOperation.subarray(sourceString, start, end);
	}

	// <<< Speedups

	@Override
	public boolean visit(AlignDeclaration node) {
		return isInRange(node);
	}

	@Override
	public boolean visit(ProtDeclaration node) {
		return isInRange(node);
	}

	@Override
	public boolean visit(StorageClassDeclaration node) {
		return isInRange(node);
	}

	@Override
	public boolean visit(ConditionalDeclaration node) {
		return isInRange(node);
	}

	// >>> Speedups

	@Override
	public boolean visit(Module node) {
		// Don't visit template instances in the module scope
		int length = node.members.size();
		for (int i = 0; i < length; i++) {
			Dsymbol dsymbol = node.members.get(i);
			if (null == dsymbol.isTemplateInstance()) {
				dsymbol.accept(this);
			}
		}
		return false;
	}

	@Override
	public boolean visit(ClassDeclaration node) {
		return visitType(node, node.ident);
	}

	@Override
	public boolean visit(StructDeclaration node) {
		return visitType(node, node.ident);
	}

	@Override
	public boolean visit(InterfaceDeclaration node) {
		return visitType(node, node.ident);
	}

	@Override
	public boolean visit(UnionDeclaration node) {
		return visitType(node, node.ident);
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		return visitType(node, node.ident);
	}

	@Override
	public boolean visit(TemplateDeclaration node) {
		if (isInRange(node.ident)) {
			addBinarySearch(node);
			return false;
		}
		return true;
	}

	@Override
	public boolean visit(VarDeclaration node) {
		if (isInRange(node.ident)) {
			add(node);
			return false;
		}
		return true;
	}

	@Override
	public boolean visit(AliasDeclaration node) {
		if (isInRange(node.ident)) {
			add(node);
			return false;
		}
		return true;
	}

	@Override
	public boolean visit(TypedefDeclaration node) {
		if (isInRange(node.ident)) {
			add(node);
			return false;
		}
		return true;
	}

	@Override
	public boolean visit(FuncDeclaration node) {
		if (isInRange(node.ident)) {
			addBinarySearch(node);
			return false;
		}
		return isInRange(node);
	}

	@Override
	public boolean visit(IdentifierExp node) {
		if (!isInRange(node)) {
			return false;
		}
		
		doSemantic();

		if (node.resolvedSymbol != null) {
			Dsymbol sym = node.resolvedSymbol;
			
			// See if this symbols was created at compile-time
			while (sym.creator != null) {
				sym = sym.creator;
			}
			
			if (sym.getJavaElement() != null) {
				addJavaElement(sym.getJavaElement());
			} else {
				if (isLocal(sym)) {
					addLocal((Declaration) sym, sym.getFlags());
				} else if (!(sym instanceof descent.internal.compiler.parser.Package)
						&& !(sym instanceof descent.internal.compiler.parser.Import)) {
					addBinarySearch(sym);
				}
			}
			return false;
		}

		return addResolvedExpression(node);
	}

	private boolean addResolvedExpression(Expression node) {
		Expression resolved = node.resolvedExpression;
		if (resolved == null) {
			return false;
		}

		return addExpression(resolved);
	}

	private boolean addExpression(Expression exp) {
		switch (exp.getNodeType()) {
		case ASTDmdNode.VAR_EXP:
			add((VarExp) exp);
			break;
		case ASTDmdNode.DOT_VAR_EXP:
			add((DotVarExp) exp);
			break;
		case ASTDmdNode.TYPE_EXP:
			add(((TypeExp) exp).type);
			break;
		}

		return false;
	}

	@Override
	public boolean visit(Import node) {
		if (isInRange(node.id)) {
			doSemantic();
			
			Module mod = node.mod;
			if (mod != null) {
				if (mod.getJavaElement() != null) {
					addJavaElement(mod.getJavaElement());
				} else {
					descent.core.ICompilationUnit unit = internalSignature.getCompilationUnit(module.moduleName);
					if (unit != null) {
						addJavaElement(unit);
					}
				}
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean visit(EnumMember node) {
		if (isInRange(node)) {
			addBinarySearch(node);
		}
		return false;
	}

	@Override
	public boolean visit(Argument node) {
		if (isInRange(node.ident)) {
			doSemantic();
			if (node.var != null) {
				if (node.var instanceof VarDeclaration) {
					add((VarDeclaration) node.var);
				} else if (node.var instanceof AliasDeclaration) {
					add((AliasDeclaration) node.var);
				} else if (node.var instanceof TypedefDeclaration) {
					add((TypedefDeclaration) node.var);
				}
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean visit(NewExp node) {
		if (isInRange(node.sourceNewtype)) {
			doSemantic();
			
			if (node.member != null) {
				CtorDeclaration ctor = node.member;
				if (ctor.getJavaElement() != null) {
					addJavaElement(ctor.getJavaElement());
				} else {
					addBinarySearch(ctor);
				}
			}
			return false;
		}
		return true;
	}

	private boolean visitType(ASTDmdNode node, IdentifierExp ident) {
		if (isInRange(ident)) {
			addBinarySearch(node);
		}
		return isInRange(node);
	}

	private void add(VarExp varExp) {
		Declaration var = varExp.var;
		if (var instanceof FuncDeclaration) {
			addBinarySearch(var);
		} else if (var instanceof VarDeclaration) {
			add((VarDeclaration) var);
		} else if (var.getJavaElement() != null) {
			addJavaElement(var.getJavaElement());
		} else {
			add(var);
		}
	}

	private void add(DotVarExp dotVarExp) {
		Declaration decl = dotVarExp.var;
		if (decl.getJavaElement() != null) {
			addJavaElement(decl.getJavaElement());
		} else {
			add(decl);
		}
	}

	private void add(Dsymbol sym) {
		if (sym instanceof TemplateDeclaration) {
			TemplateDeclaration decl = (TemplateDeclaration) sym;
			if (decl.wrapper) {
				add(decl.members.get(0));
			} else {
				addBinarySearch(sym);
			}
		} else {
			addBinarySearch(sym);
		}
	}

	private boolean isLocal(descent.internal.compiler.parser.Dsymbol node) {
		return node instanceof Declaration
				&& isLocal((descent.internal.compiler.parser.Declaration) node);
	}

	private boolean isLocal(descent.internal.compiler.parser.Declaration node) {
		return node.effectiveParent() instanceof FuncDeclaration;
	}

	private void add(VarDeclaration node) {
		doSemantic();
		
		if (node.getJavaElement() != null) {
			addJavaElement(node.getJavaElement());
		} else if (isLocal(node)) {
			addLocalVar(node);
		} else {
			addBinarySearch(node);
		}
	}

	private void add(AliasDeclaration node) {
		doSemantic();
		
		if (node.getJavaElement() != null) {
			addJavaElement(node.getJavaElement());
		} else if (isLocal(node)) {
			addLocalAlias(node);
		} else {
			addBinarySearch(node);
		}
	}

	private void add(TypedefDeclaration node) {
		doSemantic();
		
		if (node.getJavaElement() != null) {
			addJavaElement(node.getJavaElement());
		} else if (isLocal(node)) {
			addLocalTypedef(node);
		} else {
			addBinarySearch(node);
		}
	}

	private boolean isInRange(IdentifierExp ident) {
		if (ident == null) {
			return false;
		}
		if (ident.ident.length == 0) {
			return false;
		}

		return ident.start <= offset
				&& offset + length <= ident.start + ident.length;
	}

	private boolean isInRange(ASTDmdNode node) {
		return node != null && node.start <= offset
				&& offset + length <= node.start + node.length;
	}

	private void addLocalVar(VarDeclaration node) {
		addLocal(node, Flags.AccDefault);
	}

	private void addLocalAlias(AliasDeclaration node) {
		addLocal(node, Flags.AccAlias);
	}

	private void addLocalTypedef(TypedefDeclaration node) {
		addLocal(node, Flags.AccTypedef);
	}

	private void addLocal(Declaration node, long modifiers) {
		FuncDeclaration parent = (FuncDeclaration) node.effectiveParent();
		boolean found = addBinarySearch(parent);
		if (!found) {
			return;
		}

		JavaElement func = (JavaElement) selectedElements
				.remove(selectedElements.size() - 1);
		addJavaElement(new LocalVariable(func, node.ident.toString(),
				node.start, node.start + node.length - 1, node.ident.start,
				node.ident.start + node.ident.length - 1, node.type
						.getSignature(), modifiers));
	}

	private void add(Type type) {
		if (type == null) {
			return;
		}

		if (type.getJavaElement() != null) {
			addJavaElement(type.getJavaElement());
			return;
		}

		//		IJavaElement result = finder.find(type.getSignature());
		//		if (result != null) {
		//			addJavaElement(result);
		//		}
	}

	private void addJavaElement(IJavaElement element) {
		if (!selectedElements.contains(element)) {
			selectedElements.add(element);
		}
	}

	private boolean addBinarySearch(ASTDmdNode node) {
		try {
			descent.core.ICompilationUnit unit = internalSignature.getCompilationUnit(module.moduleName);
			if (unit != null) {
				IJavaElement element = internalSignature.binarySearch(unit, node.start, node.start
						+ node.length);
				if (element != null) {
					addJavaElement(element);
					return true;
				} else {
					return false;
				}
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
		return false;
	}

}
