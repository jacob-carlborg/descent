package dtool.ast.definitions;

import static melnorme.miscutil.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.ArrayUtil;
import melnorme.miscutil.StringUtil;
import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.dltk.core.ISourceModule;

import descent.internal.compiler.parser.Comment;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.ModuleDeclaration;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.declarations.Declaration;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

/**
 * D Module
 * XXX: What to do when the module name is not defined? Infer from module unit?
 */
public class Module extends DefUnit implements IScopeNode {

	public static class ModuleDefSymbol extends DefSymbol {
		
		protected Module module;

		public ModuleDefSymbol(IdentifierExp id) {
			super(id, null);
		}
		
		public ModuleDefSymbol(String id) {
			super(id);
		}
		
		@Override
		public DefUnit getDefUnit() {
			return module;
		}
	}

	public static class DeclarationModule extends ASTNeoNode {

		public String[] packages; // non-structural element
		public Symbol moduleName; 
		
		public DeclarationModule(DefSymbol moduleName, ModuleDeclaration md) {
			setSourceRange(md);
			this.moduleName = moduleName; 
		}

		@Override
		public void accept0(IASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				//TreeVisitor.acceptChildren(visitor, packages);
				TreeVisitor.acceptChildren(visitor, moduleName);
			}
			visitor.endVisit(this);
		}
		
		@Override
		public String toStringAsElement() {
			//String str = ASTPrinter.toStringAsElements(packages, "."); 
			String str = StringUtil.collToString(packages, ".");
			if(str.length() == 0)
				return moduleName.toStringAsElement();
			else
				return str + "." + moduleName.toStringAsElement();
		}
	}

	private Object moduleUnit; // The compilation unit/Model Element

	public final DeclarationModule md;
	public final ASTNeoNode[] members;

	
	public static Module createModule(descent.internal.compiler.parser.Module elem) {
		ModuleDefSymbol defname;
		DeclarationModule md;
		Comment[] preComments = null;

		ASTNeoNode[] members = Declaration.convertMany(elem.members);
		
		if(elem.md == null) {
			defname = new ModuleDefSymbol("<unnamed>");
			md = null;
		} else  {
			defname = new ModuleDefSymbol(elem.md.id);
			md = new DeclarationModule(defname, elem.md);
			
			md.packages = ArrayUtil.newSameSize(elem.md.packages, String.class);

			for (int i = 0; i < md.packages.length; i++) {
				md.packages[i] = new String(elem.md.packages.get(i).ident);
			}

			preComments = elem.md.preDdocs.toArray(new Comment[elem.md.preDdocs.size()]);
		}
		return new Module(defname, preComments, md, members, elem);
	}
	
	public Module(ModuleDefSymbol defname, Comment[] preComments, DeclarationModule md,
			ASTNeoNode[] members, IASTNode sourceRange) {
		super(defname);
		defname.module = this;
		setSourceRange(sourceRange);
		this.preComments = preComments;
		this.md = md;
		this.members = members;
	}


	@Override
	public EArcheType getArcheType() {
		return EArcheType.Module;
	}
	
	public void setModuleUnit(ISourceModule modUnit) {
		assertTrue(modUnit.exists());
		if(this.moduleUnit != null)
			assertTrue(this.moduleUnit.equals(modUnit));
		this.moduleUnit = modUnit;
	}
	public ISourceModule getModuleUnit() {
		return (ISourceModule) moduleUnit;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, md);
			TreeVisitor.acceptChildren(visitor, members);
		}
		visitor.endVisit(this);
	}

	@Override
	public IScopeNode getMembersScope() {
		return this;
	}
	
	public List<IScope> getSuperScopes() {
		return null;
	}

	public Iterator<? extends ASTNeoNode> getMembersIterator() {
		return Arrays.asList(members).iterator();
	}

	@Override
	public String toStringAsElement() {
		if(md == null)
			return "<undefined>";
		return md.toStringAsElement();
	}
	
	@Override
	public String toStringForHoverSignature() {
		return toStringAsElement();
	}
	
	@Override
	public String toStringForCodeCompletion() {
		return getName();
	}

}