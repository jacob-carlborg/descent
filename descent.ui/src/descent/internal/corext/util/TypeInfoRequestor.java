package descent.internal.corext.util;

import java.util.Collection;

import org.eclipse.jface.util.Assert;

import descent.core.search.TypeNameRequestor;


public class TypeInfoRequestor extends TypeNameRequestor {
	
	private Collection fTypesFound;
	private TypeInfoFactory fFactory;
	
	/**
	 * Constructs the TypeRefRequestor
	 * @param typesFound Will collect all TypeRef's found
	 */
	public TypeInfoRequestor(Collection typesFound) {
		Assert.isNotNull(typesFound);
		fTypesFound= typesFound;
		fFactory= new TypeInfoFactory();
	}
	
	protected boolean inScope(char[] packageName, char[] typeName) {
		return !TypeFilter.isFiltered(packageName, typeName);
	}

	public void acceptType(long modifiers, int kind, char[] packageName, char[] typeName, char[][] enclosingTypeNames, String path) {
		if (inScope(packageName, typeName)) {
			fTypesFound.add(fFactory.create(packageName, typeName, enclosingTypeNames, modifiers, kind, path));
		}
	}
}
