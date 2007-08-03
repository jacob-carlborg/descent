package dtool.dom.definitions;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.StringUtil;
import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.Identifier;
import descent.internal.core.dom.ModuleDeclaration;
import descent.internal.core.dom.TOK;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.declarations.Declaration;
import dtool.dom.references.EntIdentifier;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IGenericCompilationUnit;

/**
 * D Module
 */
public class Module extends DefUnit implements IScopeNode {

	public static class DeclarationModule extends ASTNeoNode {

		public EntIdentifier[] packages;
		public Symbol moduleName;
		
		public DeclarationModule(ModuleDeclaration md) {
			setSourceRange(md);
			this.moduleName = new Symbol(md.ident); 
		}

		public void accept0(IASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChildren(visitor, packages);
				TreeVisitor.acceptChild(visitor, moduleName);
			}
			visitor.endVisit(this);
		}
		
		@Override
		public String toString() {
			String str = StringUtil.collToString(packages, ".");
			if(str.length() == 0)
				return moduleName.toString();
			else
				return str + "." + moduleName;
		}
	}

	private IGenericCompilationUnit cunit;

	public DeclarationModule md;
	public ASTNode[] members;
	

	public Module(descent.internal.core.dom.Module elem) {
		convertNode(elem); // elem not a full formed Dsymbol
		setSourceRange(elem);
		convertIdentifier(new Identifier(null, TOK.TOKidentifier));
		//newelem.name = (elem.ident != null) ? elem.ident.string : null; 
		if(elem.md != null){
			// If there is md there is this	elem.ident
			this.defname = new Symbol(elem.ident); 
			this.md = new DeclarationModule(elem.md);

			if(elem.md.packages != null) {
				this.md.packages = new EntIdentifier[elem.md.packages.size()];
				DescentASTConverter.convertMany(elem.md.packages.toArray(),this.md.packages);
			} else {
				this.md.packages = new EntIdentifier[0];
			}
		}
		this.members = Declaration.convertMany(elem.getDeclarationDefinitions());
	}

	public EArcheType getArcheType() {
		return EArcheType.Module;
	}
	
	@Override
	public String toString() {
		if(defname.toString() == null)
			return "<noname>";
		else 
			return md.toString();
	}
	
	@Override
	public String toStringFullSignature() {
		return toString();
	}
	
	
	public void setCUnit(IGenericCompilationUnit cunit) {
		this.cunit = cunit;
	}

	public IGenericCompilationUnit getCUnit() {
		return cunit;
	}

	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, md);
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

	public Iterator<? extends ASTNode> getMembersIterator() {
		return Arrays.asList(members).iterator();
	}

}
