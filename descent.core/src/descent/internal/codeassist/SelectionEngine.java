package descent.internal.codeassist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import descent.core.Flags;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IPackageFragment;
import descent.core.IPackageFragmentRoot;
import descent.core.IParent;
import descent.core.ISourceRange;
import descent.core.ISourceReference;
import descent.core.JavaModelException;
import descent.core.WorkingCopyOwner;
import descent.core.compiler.CharOperation;
import descent.core.dom.CompilationUnitResolver;
import descent.internal.compiler.env.AccessRestriction;
import descent.internal.compiler.env.ICompilationUnit;
import descent.internal.compiler.impl.CompilerOptions;
import descent.internal.compiler.parser.ASTDmdNode;
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
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.ProtDeclaration;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.StorageClassDeclaration;
import descent.internal.compiler.parser.StructDeclaration;
import descent.internal.compiler.parser.TOK;
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
import static descent.internal.compiler.parser.TOK.TOKblockcomment;
import static descent.internal.compiler.parser.TOK.TOKdocblockcomment;
import static descent.internal.compiler.parser.TOK.TOKdoclinecomment;
import static descent.internal.compiler.parser.TOK.TOKdocpluscomment;
import static descent.internal.compiler.parser.TOK.TOKlinecomment;
import static descent.internal.compiler.parser.TOK.TOKpluscomment;

/*
 * For now, don't take the JDT approach: let's parse, visit and see where
 * if the node falls between the given ranges.
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
		long time = System.currentTimeMillis();
		
		this.offset = offset;
		this.length = length;
		this.unit = sourceUnit;
		this.selectedElements = new ArrayList<IJavaElement>();

		try {
			char[] contents = sourceUnit.getContents();

			final Token[] docToken = { null };

			// Custom parser to see if we are selecting a token in a comment
			Parser parser = new Parser(contents, 0, contents.length,
					true /* tokenize comments */, false, false, false,
					javaProject.getApiLevel(), null, null, false, sourceUnit
							.getFileName()) {
				@Override
				public TOK nextToken() {
					TOK tok = Lexer_nextToken();

					while ((tok == TOKlinecomment || tok == TOKdoclinecomment
							|| tok == TOKblockcomment
							|| tok == TOKdocblockcomment
							|| tok == TOKpluscomment || tok == TOKdocpluscomment)) {
						if (token.ptr <= offset
								&& offset <= token.ptr + token.sourceLen) {
							docToken[0] = new Token(token);
						}

						tok = Lexer_nextToken();
					}

					return tok;
				}
			};
			parser.nextToken();

			module = parser.parseModuleObj();

			if (docToken[0] != null) {
				char[] tok = extractToken(docToken[0], offset);
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
							long modifiers, AccessRestriction accessRestriction) {
						IJavaElement element = internalSignature.findField(
								packageName, name);
						if (element != null) {
							addJavaElement(element);
						}
					}

					public void acceptMethod(char[] packageName, char[] name,
							char[][] enclosingTypeNames, char[] signature,
							long modifiers, AccessRestriction accessRestriction) {
						IJavaElement element = internalSignature.findMethod(
								packageName, name, signature);
						if (element != null) {
							addJavaElement(element);
						}
					}

					public void acceptPackage(char[] packageName) {
					}

					public void acceptType(char[] packageName, char[] typeName,
							char[][] enclosingTypeNames, long modifiers,
							AccessRestriction accessRestriction) {
						IJavaElement element = internalSignature.findField(
								packageName, typeName);
						if (element != null) {
							addJavaElement(element);
						}
					}
				});
			} else {
				module.moduleName = sourceUnit.getFullyQualifiedName();

				context = CompilationUnitResolver.resolve(module, javaProject,
						owner);

				module.accept(this);
			}
			return selectedElements.toArray(new IJavaElement[selectedElements
					.size()]);
		} catch (JavaModelException e) {
			Util.log(e);
			return NO_ELEMENTS;
		} finally {
			time = System.currentTimeMillis() - time;
			System.out.println("Selection time: " + time);
		}
	}

	private char[] extractToken(Token token, int offset) {
		char[] sourceString = token.sourceString;

		int start = offset - token.ptr;
		while (start >= 0 && Chars.isidchar(sourceString[start])) {
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
		for (Dsymbol symbol : node.members) {
			Dsymbol dsymbol = symbol;
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

		if (node.resolvedSymbol != null) {
			Dsymbol sym = node.resolvedSymbol;
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
			Module mod = node.mod;
			if (mod != null) {
				if (mod.getJavaElement() != null) {
					addJavaElement(mod.getJavaElement());
				} else {
					descent.core.ICompilationUnit unit = getCompilationUnit();
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
		if (node.var != null && isInRange(node.ident)) {
			if (node.var instanceof VarDeclaration) {
				add((VarDeclaration) node.var);
			} else if (node.var instanceof AliasDeclaration) {
				add((AliasDeclaration) node.var);
			} else if (node.var instanceof TypedefDeclaration) {
				add((TypedefDeclaration) node.var);
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean visit(NewExp node) {
		if (isInRange(node.sourceNewtype) && node.member != null) {
			CtorDeclaration ctor = node.member;
			if (ctor.getJavaElement() != null) {
				addJavaElement(ctor.getJavaElement());
			} else {
				addBinarySearch(ctor);
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
		if (node.getJavaElement() != null) {
			addJavaElement(node.getJavaElement());
		} else if (isLocal(node)) {
			addLocalVar(node);
		} else {
			addBinarySearch(node);
		}
	}

	private void add(AliasDeclaration node) {
		if (node.getJavaElement() != null) {
			addJavaElement(node.getJavaElement());
		} else if (isLocal(node)) {
			addLocalAlias(node);
		} else {
			addBinarySearch(node);
		}
	}

	private void add(TypedefDeclaration node) {
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
			descent.core.ICompilationUnit unit = getCompilationUnit();
			if (unit != null) {
				return addBinarySearch(unit, node.start, node.start
						+ node.length);
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
		return false;
	}

	private descent.core.ICompilationUnit getCompilationUnit() {
		String fqn = unit.getFullyQualifiedName();
		String packages;
		String filename;

		int indexOfDot = fqn.lastIndexOf('.');
		if (indexOfDot == -1) {
			packages = "";
			filename = fqn + ".d";
		} else {
			packages = fqn.substring(0, indexOfDot);
			filename = fqn.substring(indexOfDot + 1) + ".d";
		}

		try {
			for (IPackageFragmentRoot root : javaProject
					.getPackageFragmentRoots()) {
				IPackageFragment pack = root.getPackageFragment(packages);
				if (pack.exists()) {
					// found it
					descent.core.ICompilationUnit unit = pack
							.getCompilationUnit(filename);
					if (!unit.exists()) {
						unit = pack.getClassFile(filename);
						if (!unit.exists())
							continue;
					}

					return unit;
				}
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}

		return null;
	}

	private boolean addBinarySearch(IJavaElement elem, int start, int end)
			throws JavaModelException {
		if (!(elem instanceof IParent)) {
			return false;
		}

		IJavaElement[] children = ((IParent) elem).getChildren();
		if (children.length == 0) {
			return false;
		}
		
		IJavaElement child = binarySearch(children, start, end);
		if (child == null) {
			return false;
		}
		
		if (child instanceof IParent) {
			IParent parent = (IParent) child;
			if (parent.hasChildren()) {
				boolean found = addBinarySearch(child, start, end);
				if (found) {
					return true;
				} else {
					addJavaElement(child);
					return true;
				}
			} else {
				addJavaElement(child);
				return true;
			}
		} else {
			addJavaElement(child);
			return true;
		}
	}

	private static IJavaElement binarySearch(IJavaElement[] a, int start, int end) throws JavaModelException {
		int low = 0;
		int high = a.length - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			ISourceReference midVal = (ISourceReference) a[mid];
			ISourceRange range = midVal.getSourceRange();

			if (range.getOffset() + range.getLength() < start) {
				low = mid + 1;
			} else if (range.getOffset() > end) {
				high = mid - 1;
			} else {
				if (range.getOffset() <= start
						&& end <= range.getOffset() + range.getLength()) {
					return a[mid]; // key found	
				} else {
					return null;
				}
			}
		}
		return null; // key not found.
	}

}
