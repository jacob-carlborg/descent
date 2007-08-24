package descent.internal.compiler.parser;

import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;

public class Problem implements IProblem {
	
	public final static Object[] NO_OBJECTS = new Object[0];
	
	private boolean isError;
	private int categoryId;
	private int id;
	private int sourceStart;
	private int sourceEnd;
	private int sourceLineNumber;
	private String[] arguments;
	
	private Problem() { }
	
	public static Problem newSyntaxError(int id, int line, int sourceStart, int length, String[] arguments) {
		Problem p = new Problem();
		p.isError = true;
		p.id = id;
		p.categoryId = CAT_SYNTAX;
		p.sourceLineNumber = line;
		p.sourceStart = sourceStart;
		p.sourceEnd = sourceStart + length - 1;
		p.arguments = arguments;
		return p;
	}
	
	public static Problem newSyntaxError(int id, int line, int sourceStart, int length) {
		return newSyntaxError(id, line, sourceStart, length, null);
	}
	
	public static Problem newSemanticMemberError(int id, int line, int sourceStart, int length, String[] arguments) {
		Problem p = new Problem();
		p.isError = true;
		p.id = id;
		p.categoryId = CAT_MEMBER;
		p.sourceLineNumber = line;
		p.sourceStart = sourceStart;
		p.sourceEnd = sourceStart + length - 1;
		p.arguments = arguments;
		return p;
	}
	
	public static Problem newSemanticMemberError(int id, int line, int sourceStart, int length) {
		return newSemanticMemberError(id, line, sourceStart, length, null);
	}
	
	public static Problem newSemanticTypeError(int id, int line, int sourceStart, int length, String[] arguments) {
		Problem p = new Problem();
		p.isError = true;
		p.id = id;
		p.categoryId = CAT_TYPE;
		p.sourceLineNumber = line;
		p.sourceStart = sourceStart;
		p.sourceEnd = sourceStart + length - 1;
		p.arguments = arguments;
		return p;
	}
	
	public static Problem newSemanticTypeError(int id, int line, int sourceStart, int length) {
		return newSemanticTypeError(id, line, sourceStart, length, null);
	}
	
	public static Problem newTask(String message, int line, int sourceStart, int length) {
		Problem p = new Problem();
		p.arguments = new String[] { message };
		p.isError = false;
		p.id = IProblem.Task;
		p.categoryId = CAT_UNSPECIFIED;
		p.sourceLineNumber = line;
		p.sourceStart = sourceStart;
		p.sourceEnd = sourceStart + length - 1;
		return p;
	}
	
	public int getID() {
		return id;
	}

	public String getMessage() {
		switch(id) {
		case UnterminatedBlockComment:
			return "Unterminated block comment";
		case UnterminatedPlusBlockComment:
			return "Unterminated plus block comment";
		case IncorrectNumberOfHexDigitsInEscapeSequence:
			return "Escape hex sequence has " + arguments[0] + " digits instead of " + arguments[1];
		case UndefinedEscapeHexSequence:
			return "Undefined escape hex sequence";
		case UnterminatedStringConstant:
			return "Unterminated string constant";
		case OddNumberOfCharactersInHexString:
			return "Odd number (" + arguments[0] + ") of hex characters in hex string";
		case NonHexCharacter:
			return "Non-hex character: " + arguments[0];
		case UnterminatedCharacterConstant:
			return "Unterminated character constant";
		case BinaryDigitExpected:
			return "Binary digit expected";
		case OctalDigitExpected:
			return "Octal digit expected";
		case HexDigitExpected:
			return "Hex digit expected";
		case UnsupportedCharacter:
			return "Unsupported char: " + arguments[0];
		case InvalidUtfCharacter:
			return "Invalid UTF character: \\U" + arguments[0];
		case ThreeEqualsIsNoLongerLegal:			
			return "'===' is no longer legal, use 'is' instead";
		case NotTwoEqualsIsNoLongerLegal:
			return "'!==' is no longer legal, use 'is' instead";
		case LSuffixDeprecated:
			return "'l' suffix is deprecated, use 'L' instead";
		case InvalidPragmaSyntax:
			return "#line integer [\"filespec\"]\\n expected";
		case UnrecognizedCharacterEntity:
			return "Unrecognized character entity";
		case UnterminatedNamedEntity:
			return "Unterminated named entity";
		case UndefinedEscapeSequence:
			return "Undefined escape sequence";
		case InvalidUtf8Sequence:
			return "Invalid input sequence";
		case IntegerOverflow:
			return "Integer overflow";
		case SignedIntegerOverflow:
			return "Signed integer overflow";
		case UnrecognizedToken:
			return "Unrecognized token";
		case BinaryExponentPartRequired:
			return "Binary-exponent-part required";
		case ExponentExpected:
			return "Exponent expected";
		case ISuffixDeprecated:
			return "'I' suffix is deprecated, use 'i' instead";
		case ParsingErrorInsertTokenAfter:
			return "Syntax error on token \"" + arguments[0] + "\", " + arguments[1] + " expected after this token";
		case ParsingErrorDeleteToken:
			return "Syntax error on token \"" + arguments[0] + "\", delete this token";
		case ParsingErrorInsertToComplete:
			return "Syntax error, insert \"" + arguments[0] + "\" to complete " + arguments[1];
		case EnumDeclarationIsInvalid:
			return "Enum declaration is invalid";
		case MismatchedStringLiteralPostfixes:
			return "Mismatched string literal postfixes '" + arguments[0] + "' and '" + arguments[1] + "'";
		case NoIdentifierForDeclarator:
			return "No identifier for declarator";
		case AliasCannotHaveInitializer:
			return "Alias cannot have initializer";
		case CStyleCastIllegal:
			return "C style cast illegal, use cast(...)";
		case InvalidLinkageIdentifier:
			return "Valid linkage identifiers are D, C, C++, Pascal, Windows";
		case VariadicArgumentCannotBeOutInoutOrRef:
			return "Variadic argument cannot be out, inout or ref";
		case VariadicNotAllowedInDelete:
			return "... not allowed in delete function parameter list";
		case NoIdentifierForTemplateValueParameter:
			return "No identifier for template value parameter";
		case UnexpectedIdentifierInDeclarator:
			return "Unexpected identifier in declarator";
		case RedundantStorageClass:
			return "Redundant storage class";
		case UseBracesForAnEmptyStatement:
			return "Use '{ }' for an empty statement, not a ';'";
		case MultipleDeclarationsMustHaveTheSameType:
			return"Multiple declarations must have the same type";
		case RedundantInStatement:
			return "Redundant 'in' statement";
		case RedundantOutStatement:
			return "Redundant 'out' statement";
		case StatementExpectedToBeCurlies:
			return "Statement expected to be { }";
		case InvalidScopeIdentifier:
			return "Valid scope identifiers are exit, failure, or success";
		case OnScopeDeprecated:
			return arguments[0] + " is deprecated, use scope";
		case DollarInvalidOutsideBrackets:
			return "'$' is valid only inside [] of index or slice";
		case IftypeDeprecated:
			return "iftype(condition) is deprecated, use static if (is(condition))";
		case IfAutoDeprecated:
			return "if (v; e) is deprecated, use if (auto v = e)";
		case VariadicTemplateParameterMustBeTheLastOne:
			return "Variadic template parameter must be last one";
		case NeedSizeOfRightmostArray:
			return "Need size of rightmost array";
		case DuplicatedSymbol:
			return "Duplicated symbol " + arguments[0];
		case PropertyCanNotBeRedefined:
			return "Property " + arguments[0] + " cannot be redefined";
		case CircularDefinition:
			return "Circular definition";
		case EnumValueOverflow:
			return "Overflow of enum value";
		case EnumMustHaveAtLeastOneMember:
			return "Enum must have at least one member";
		case EnumBaseTypeMustBeOfIntegralType:
			return "Base type must be of integral type";
		case ForwardReference:
			return "Forward reference of " + arguments[0];
		case ForwardReferenceWhenLookingFor:
			return arguments[0] + " is forward reference when looking for " + arguments[1];
		case BaseEnumIsForwardReference:
			return "Base enum is forward reference";
		case CannotResolveForwardReference:
			return "Cannot resolve forward reference";
		case EnumIsForwardReference:
			return "Enum is forward reference";
		case IntegerConstantExpressionExpected:
			return "Integer constant expression expected";
		case ThisNotInClassOrStruct:
			return "Not in a struct or class";
		case ThisOnlyAllowedInNonStaticMemberFunctions:
			return "'this' is only allowed in non-static member functions";
		case SuperOnlyAllowedInNonStaticMemberFunctions:
			return "'super' is only allowed in non-static member functions";
		case SuperNotInClass:
			return "Not in a class";
		case ClassHasNoSuper:
			return "Class " + arguments[0] + " has no 'super'";
		case BaseTypeMustBeInterface:
			return "Base type must be interface";
		case MemberIsPrivate:
			return arguments[0] + " is private";
		case UsedAsAType:
			return arguments[0] + " cannot be resolved to a type";
		case ExternSymbolsCannotHaveInitializers:
			return "Extern symbols cannot have initializers";
		case VoidsHaveNoValue:
			return "Voids have no value";
		case CannotInferType:
			return "Cannot infer type from this array initializer";
		case NoDefinition:
			return "No definition of struct " + arguments[0];
		case DuplicatedInterfaceInheritance:
			return "Duplicated interface " + arguments[0] + " for the type " + arguments[1];
		case BaseTypeMustBeClassOrInterface:
			return "Base type must be class or interface";
		case FieldsNotAllowedInInterfaces:
			return "Fields are not allowed in interfaces";
		case UndefinedIdentifier:
			return arguments[0] + " cannot be resolved";
		case NotAMember:
			return "Template identifier " + arguments[0] + " is not a member of this module";
		case NewAllocatorsOnlyForClassOrStruct:
			return "New allocators only are for class or struct definitions";
		case DeleteDeallocatorsOnlyForClassOrStruct:
			return "Delete deallocators only are for class or struct definitions";
		case IllegalParameters:
			return arguments[0];
		case ConstructorsOnlyForClass:
			return "Constructors only are for class definitions";
		case DestructorsOnlyForClass:
			return "Destructors only are for class definitions";
		case InvariantsOnlyForClassStructUnion:
			return "Invariants only are for struct/union/class definitions";
		case FunctionDoesNotOverrideAny:
			return "The function " + arguments[0] + " of type " + arguments[1] + " must override a superclass method";
		case CannotOverrideFinalFunctions:
			return "Cannot override the final function " + arguments[0] + " from " + arguments[1];
		case OverrideOnlyForClassMemberFunctions:
			return "Override only applies to class member functions";
		case IllegalReturnType:
			return arguments[0];
		case MoreThanOneInvariant:
			return "More than one invariant for " + arguments[0];
		case DuplicatedParameter:
			return "Duplicate parameter " + arguments[0];
		case SymbolNotFound:
			return "Symbol " + arguments[0] + " not found";
		case StaticAssertIsFalse:
			return "The expression statically evaluates to false";
		case VoidFunctionsHaveNoResult:
			return "Void functions have no result";
		case ReturnStatementsCannotBeInContracts:
			return "Return statements cannot be in contracts";
		case NotAnAggregateType:
			return arguments[0] + " is not an aggregate type";
		case UnrecognizedPragma:
			return "Unrecognized pragma";
		case AnonCanOnlyBePartOfAnAggregate:
			return "Anonymous unions can only be part of an aggregate";
		case PragmaIsMissingClosingSemicolon:
			return "pragma is missing closing ';'";
		case CannotImplicitlyConvert:
			return "Type mismatch: cannot implicitly convert from " + arguments[0] + " to " + arguments[1];
		case ForbiddenReference:
			return "Forbidden reference";
		case DiscouragedReference:
			return "Discouraged reference";
		case Task:
			return arguments[0];
		case UndefinedType:
			return "TODO: remove";
		case IsClassPathCorrect:
			return "TODO: remove";
		case FunctionsCannotBeConstOrAuto:
			return "Functions cannot be const or auto";
		case NonVirtualFunctionsCannotBeAbstract:
			return "Non-virtual functions cannot be abstract";
		case ModifierCannotBeAppliedToVariables:
			return arguments[0] + " cannot be applied to variables";
		case StructsCannotBeAbstract:
			return "structs cannot be abstract";
		case UnionsCannotBeAbstract:
			return "unions cannot be abstract";
		case AliasCannotBeConst:
			return "alias cannot be const";
		case OneArgumentOfTypeExpected:
			return "one argument of type " + arguments[0] + " expected";
		default:
			return "";
		}
	}
	
	public int getLength() {
		return sourceEnd;
	}

	public int getSourceStart() {
		return sourceStart;
	}
	
	public int getSourceEnd() {
		return sourceEnd;
	}
	
	public boolean isError() {
		return isError;
	}
	
	public boolean isWarning() {
		return !isError;
	}
	
	public int getSourceLineNumber() {
		return sourceLineNumber;
	}
	
	public int getCategoryID() {
		return categoryId;
	}
	
	public String getMarkerType() {
		return "descent.core.problem";
	}
	
	public String[] getArguments() {
		return CharOperation.NO_STRINGS;
	}
	
	public char[] getOriginatingFileName() {
		return CharOperation.NO_CHAR;
	}

	public void setSourceEnd(int sourceEnd) {
		this.sourceEnd = sourceEnd;
	}

	public void setSourceLineNumber(int lineNumber) {
		this.sourceLineNumber = lineNumber;
	}

	public void setSourceStart(int sourceStart) {
		this.sourceStart = sourceStart;
	}
	
	/**
	 * Returns the names of the extra marker attributes associated to this problem when persisted into a marker 
	 * by the JavaBuilder. Extra attributes are only optional, and are allowing client customization of generated
	 * markers. By default, no EXTRA attributes is persisted, and a categorized problem only persists the following attributes:
	 * <ul>
	 * <li>	<code>IMarker#MESSAGE</code> -&gt; {@link IProblem#getMessage()}</li>
	 * <li>	<code>IMarker#SEVERITY</code> -&gt; <code> IMarker#SEVERITY_ERROR</code> or 
	 *         <code>IMarker#SEVERITY_WARNING</code> depending on {@link IProblem#isError()} or {@link IProblem#isWarning()}</li>
	 * <li>	<code>IJavaModelMarker#ID</code> -&gt; {@link IProblem#getID()}</li>
	 * <li>	<code>IMarker#CHAR_START</code>  -&gt; {@link IProblem#getSourceStart()}</li>
	 * <li>	<code>IMarker#CHAR_END</code>  -&gt; {@link IProblem#getSourceEnd()}</li>
	 * <li>	<code>IMarker#LINE_NUMBER</code>  -&gt; {@link IProblem#getSourceLineNumber()}</li>
	 * <li>	<code>IJavaModelMarker#ARGUMENTS</code>  -&gt; some <code>String[]</code> used to compute quickfixes </li>
	 * <li>	<code>IJavaModelMarker#CATEGORY_ID</code> -&gt; {@link CategorizedProblem#getCategoryID()}</li>
	 * </ul>
	 * The names must be eligible for marker creation, as defined by <code>IMarker#setAttributes(String[], Object[])</code>, 
	 * and there must be as many names as values according to {@link #getExtraMarkerAttributeValues()}.
	 * Note that extra marker attributes will be inserted after default ones (as described in {@link CategorizedProblem#getMarkerType()},
	 * and thus could be used to override defaults.
	 * @return the names of the corresponding marker attributes
	 */
	public String[] getExtraMarkerAttributeNames() {
		return CharOperation.NO_STRINGS;
	}

	/**
	 * Returns the respective values for the extra marker attributes associated to this problem when persisted into 
	 * a marker by the JavaBuilder. Each value must correspond to a matching attribute name, as defined by
	 * {@link #getExtraMarkerAttributeNames()}. 
	 * The values must be eligible for marker creation, as defined by <code>IMarker#setAttributes(String[], Object[])</code>.
	 * @return the values of the corresponding extra marker attributes
	 */
	public Object[] getExtraMarkerAttributeValues() {
		return NO_OBJECTS;
	}
	
	@Override
	public String toString() {
		return getMessage();
	}

}
