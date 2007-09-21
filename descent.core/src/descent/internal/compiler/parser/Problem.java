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
	
	public static Problem newSyntaxError(int id, int line, int start, int length, String[] arguments) {
		Problem p = new Problem();
		p.isError = true;
		p.id = id;
		p.categoryId = CAT_SYNTAX;
		p.sourceLineNumber = line;
		p.sourceStart = start;
		p.sourceEnd = start + length - 1;
		p.arguments = arguments;
		return p;
	}
	
	public static Problem newSyntaxError(int id, int line, int start, int length) {
		return newSyntaxError(id, line, start, length, null);
	}
	
	public static Problem newSemanticMemberError(int id, int line, int start, int length, String[] arguments) {
		Problem p = new Problem();
		p.isError = true;
		p.id = id;
		p.categoryId = CAT_MEMBER;
		p.sourceLineNumber = line;
		p.sourceStart = start;
		p.sourceEnd = start + length - 1;
		p.arguments = arguments;
		return p;
	}
	
	public static Problem newSemanticMemberError(int id, int line, int start, int length) {
		return newSemanticMemberError(id, line, start, length, null);
	}
	
	private static Problem newSemanticTypeProblem(int id, int line, int start, int length, String[] arguments, boolean isError) {
		Problem p = new Problem();
		p.isError = isError;
		p.id = id;
		p.categoryId = CAT_TYPE;
		p.sourceLineNumber = line;
		p.sourceStart = start;
		p.sourceEnd = start + length - 1;
		p.arguments = arguments;
		return p;
	}
	
	public static Problem newSemanticTypeError(int id, int line, int start, int length, String[] arguments) {
		return newSemanticTypeProblem(id, line, start, length, arguments, true);
	}
	
	public static Problem newSemanticTypeError(int id, int line, int start, int length) {
		return newSemanticTypeError(id, line, start, length, null);
	}
	
	public static IProblem newSemanticTypeWarning(int id, int line, int start, int length, String[] arguments) {
		return newSemanticTypeProblem(id, line, start, length, arguments, false);
	}
	
	public static IProblem newSemanticTypeWarning(int id, int line, int start, int length) {
		return newSemanticTypeWarning(id, line, start, length, null);
	}
	
	public static Problem newTask(String message, int line, int start, int length) {
		Problem p = new Problem();
		p.arguments = new String[] { message };
		p.isError = false;
		p.id = IProblem.Task;
		p.categoryId = CAT_UNSPECIFIED;
		p.sourceLineNumber = line;
		p.sourceStart = start;
		p.sourceEnd = start + length - 1;
		return p;
	}
	
	public int getID() {
		return id;
	}

	public String getMessage() {
		switch(id) {
		case UnterminatedBlockComment:
			return String.format(ProblemMessages.UnterminatedBlockComment);
		case UnterminatedPlusBlockComment:
			return String.format(ProblemMessages.UnterminatedPlusBlockComment);
		case IncorrectNumberOfHexDigitsInEscapeSequence:
			return String.format(ProblemMessages.IncorrectNumberOfHexDigitsInEscapeSequence, arguments[0], arguments[1]);
		case UndefinedEscapeHexSequence:
			return String.format(ProblemMessages.UndefinedEscapeHexSequence);
		case UnterminatedStringConstant:
			return String.format(ProblemMessages.UnterminatedStringConstant);
		case OddNumberOfCharactersInHexString:
			return String.format(ProblemMessages.OddNumberOfCharactersInHexString, arguments[0]);
		case NonHexCharacter:
			return String.format(ProblemMessages.NonHexCharacter, arguments[0]);
		case UnterminatedCharacterConstant:
			return String.format(ProblemMessages.UnterminatedCharacterConstant);
		case BinaryDigitExpected:
			return String.format(ProblemMessages.BinaryDigitExpected);
		case OctalDigitExpected:
			return String.format(ProblemMessages.OctalDigitExpected);
		case HexDigitExpected:
			return String.format(ProblemMessages.HexDigitExpected);
		case UnsupportedCharacter:
			return String.format(ProblemMessages.UnsupportedCharacter, arguments[0]);
		case InvalidUtfCharacter:
			return String.format(ProblemMessages.InvalidUtfCharacter, arguments[0]);
		case ThreeEqualsIsNoLongerLegal:
			return String.format(ProblemMessages.ThreeEqualsIsNoLongerLegal);
		case NotTwoEqualsIsNoLongerLegal:
			return String.format(ProblemMessages.NotTwoEqualsIsNoLongerLegal);
		case LSuffixDeprecated:
			return String.format(ProblemMessages.LSuffixDeprecated);
		case InvalidPragmaSyntax:
			return String.format(ProblemMessages.InvalidPragmaSyntax);
		case UnrecognizedCharacterEntity:
			return String.format(ProblemMessages.UnrecognizedCharacterEntity);
		case UnterminatedNamedEntity:
			return String.format(ProblemMessages.UnterminatedNamedEntity);
		case UndefinedEscapeSequence:
			return String.format(ProblemMessages.UndefinedEscapeSequence);
		case InvalidUtf8Sequence:
			return String.format(ProblemMessages.InvalidUtf8Sequence);
		case IntegerOverflow:
			return String.format(ProblemMessages.IntegerOverflow);
		case SignedIntegerOverflow:
			return String.format(ProblemMessages.SignedIntegerOverflow);
		case UnrecognizedToken:
			return String.format(ProblemMessages.UnrecognizedToken);
		case BinaryExponentPartRequired:
			return String.format(ProblemMessages.BinaryExponentPartRequired);
		case ExponentExpected:
			return String.format(ProblemMessages.ExponentExpected);
		case ISuffixDeprecated:
			return String.format(ProblemMessages.ISuffixDeprecated);
		case ParsingErrorInsertTokenAfter:
			return String.format(ProblemMessages.ParsingErrorInsertTokenAfter, arguments[0], arguments[1]);
		case ParsingErrorDeleteToken:
			return String.format(ProblemMessages.ParsingErrorDeleteToken, arguments[0]);
		case ParsingErrorInsertToComplete:
			return String.format(ProblemMessages.ParsingErrorInsertToComplete, arguments[0], arguments[1]);
		case EnumDeclarationIsInvalid:
			return String.format(ProblemMessages.EnumDeclarationIsInvalid);
		case MismatchedStringLiteralPostfixes:
			return String.format(ProblemMessages.MismatchedStringLiteralPostfixes, arguments[0], arguments[1]);
		case NoIdentifierForDeclarator:
			return String.format(ProblemMessages.NoIdentifierForDeclarator);
		case AliasCannotHaveInitializer:
			return String.format(ProblemMessages.AliasCannotHaveInitializer);
		case CStyleCastIllegal:
			return String.format(ProblemMessages.CStyleCastIllegal);
		case InvalidLinkageIdentifier:
			return String.format(ProblemMessages.InvalidLinkageIdentifier);
		case VariadicArgumentCannotBeOutOrRef:
			return String.format(ProblemMessages.VariadicArgumentCannotBeOutOrRef);
		case VariadicNotAllowedInDelete:
			return String.format(ProblemMessages.VariadicNotAllowedInDelete);
		case NoIdentifierForTemplateValueParameter:
			return String.format(ProblemMessages.NoIdentifierForTemplateValueParameter);
		case UnexpectedIdentifierInDeclarator:
			return String.format(ProblemMessages.UnexpectedIdentifierInDeclarator);
		case RedundantStorageClass:
			return String.format(ProblemMessages.RedundantStorageClass);
		case UseBracesForAnEmptyStatement:
			return String.format(ProblemMessages.UseBracesForAnEmptyStatement);
		case MultipleDeclarationsMustHaveTheSameType:
			return String.format(ProblemMessages.MultipleDeclarationsMustHaveTheSameType);
		case RedundantInStatement:
			return String.format(ProblemMessages.RedundantInStatement);
		case RedundantOutStatement:
			return String.format(ProblemMessages.RedundantOutStatement);
		case StatementExpectedToBeCurlies:
			return String.format(ProblemMessages.StatementExpectedToBeCurlies);
		case InvalidScopeIdentifier:
			return String.format(ProblemMessages.InvalidScopeIdentifier);
		case OnScopeDeprecated:
			return String.format(ProblemMessages.OnScopeDeprecated, arguments[0]);
		case DollarInvalidOutsideBrackets:
			return String.format(ProblemMessages.DollarInvalidOutsideBrackets);
		case IftypeDeprecated:
			return String.format(ProblemMessages.IftypeDeprecated);
		case IfAutoDeprecated:
			return String.format(ProblemMessages.IfAutoDeprecated);
		case VariadicTemplateParameterMustBeTheLastOne:
			return String.format(ProblemMessages.VariadicTemplateParameterMustBeTheLastOne);
		case NeedSizeOfRightmostArray:
			return String.format(ProblemMessages.NeedSizeOfRightmostArray);
		case ConflictingStorageClass:
			return String.format(ProblemMessages.ConflictingStorageClass);
		case DuplicatedSymbol:
			return String.format(ProblemMessages.DuplicatedSymbol, arguments[0]);
		case PropertyCanNotBeRedefined:
			return String.format(ProblemMessages.PropertyCanNotBeRedefined, arguments[0]);
		case CircularDefinition:
			return String.format(ProblemMessages.CircularDefinition);
		case EnumValueOverflow:
			return String.format(ProblemMessages.EnumValueOverflow);
		case EnumMustHaveAtLeastOneMember:
			return String.format(ProblemMessages.EnumMustHaveAtLeastOneMember);
		case EnumBaseTypeMustBeOfIntegralType:
			return String.format(ProblemMessages.EnumBaseTypeMustBeOfIntegralType);
		case ForwardReference:
			return String.format(ProblemMessages.ForwardReference, arguments[0]);
		case ForwardReferenceWhenLookingFor:
			return String.format(ProblemMessages.ForwardReferenceWhenLookingFor, arguments[0], arguments[1]);
		case BaseEnumIsForwardReference:
			return String.format(ProblemMessages.BaseEnumIsForwardReference);
		case CannotResolveForwardReference:
			return String.format(ProblemMessages.CannotResolveForwardReference);
		case EnumIsForwardReference:
			return String.format(ProblemMessages.EnumIsForwardReference);
		case IntegerConstantExpressionExpected:
			return String.format(ProblemMessages.IntegerConstantExpressionExpected);
		case ThisNotInClassOrStruct:
			return String.format(ProblemMessages.ThisNotInClassOrStruct);
		case ThisOnlyAllowedInNonStaticMemberFunctions:
			return String.format(ProblemMessages.ThisOnlyAllowedInNonStaticMemberFunctions);
		case SuperOnlyAllowedInNonStaticMemberFunctions:
			return String.format(ProblemMessages.SuperOnlyAllowedInNonStaticMemberFunctions);
		case SuperNotInClass:
			return String.format(ProblemMessages.SuperNotInClass);
		case ClassHasNoSuper:
			return String.format(ProblemMessages.ClassHasNoSuper, arguments[0]);
		case BaseTypeMustBeInterface:
			return String.format(ProblemMessages.BaseTypeMustBeInterface);
		case MemberIsPrivate:
			return String.format(ProblemMessages.MemberIsPrivate, arguments[0]);
		case UsedAsAType:
			return String.format(ProblemMessages.UsedAsAType, arguments[0]);
		case ExternSymbolsCannotHaveInitializers:
			return String.format(ProblemMessages.ExternSymbolsCannotHaveInitializers);
		case VoidsHaveNoValue:
			return String.format(ProblemMessages.VoidsHaveNoValue);
		case CannotInferType:
			return String.format(ProblemMessages.CannotInferType);
		case NoDefinition:
			return String.format(ProblemMessages.NoDefinition, arguments[0]);
		case DuplicatedInterfaceInheritance:
			return String.format(ProblemMessages.DuplicatedInterfaceInheritance, arguments[0], arguments[1]);
		case BaseTypeMustBeClassOrInterface:
			return String.format(ProblemMessages.BaseTypeMustBeClassOrInterface);
		case FieldsNotAllowedInInterfaces:
			return String.format(ProblemMessages.FieldsNotAllowedInInterfaces);
		case UndefinedIdentifier:
			return String.format(ProblemMessages.UndefinedIdentifier, arguments[0]);
		case NotAMember:
			return String.format(ProblemMessages.NotAMember, arguments[0]);
		case NewAllocatorsOnlyForClassOrStruct:
			return String.format(ProblemMessages.NewAllocatorsOnlyForClassOrStruct);
		case DeleteDeallocatorsOnlyForClassOrStruct:
			return String.format(ProblemMessages.DeleteDeallocatorsOnlyForClassOrStruct);
		case ConstructorsOnlyForClass:
			return String.format(ProblemMessages.ConstructorsOnlyForClass);
		case DestructorsOnlyForClass:
			return String.format(ProblemMessages.DestructorsOnlyForClass);
		case InvariantsOnlyForClassStructUnion:
			return String.format(ProblemMessages.InvariantsOnlyForClassStructUnion);
		case FunctionDoesNotOverrideAny:
			return String.format(ProblemMessages.FunctionDoesNotOverrideAny, arguments[0], arguments[1]);
		case CannotOverrideFinalFunctions:
			return String.format(ProblemMessages.CannotOverrideFinalFunctions, arguments[0], arguments[1]);
		case OverrideOnlyForClassMemberFunctions:
			return String.format(ProblemMessages.OverrideOnlyForClassMemberFunctions);
		case FunctionMustReturnAResultOfType:
			return String.format(ProblemMessages.FunctionMustReturnAResultOfType, arguments[0]);
		case MoreThanOneInvariant:
			return String.format(ProblemMessages.MoreThanOneInvariant, arguments[0]);
		case DuplicatedParameter:
			return String.format(ProblemMessages.DuplicatedParameter, arguments[0]);
		case SymbolNotFound:
			return String.format(ProblemMessages.SymbolNotFound, arguments[0]);
		case StatementIsNotReachable:
			return String.format(ProblemMessages.StatementIsNotReachable);
		case VoidFunctionsHaveNoResult:
			return String.format(ProblemMessages.VoidFunctionsHaveNoResult);
		case ReturnStatementsCannotBeInContracts:
			return String.format(ProblemMessages.ReturnStatementsCannotBeInContracts);
		case NotAnAggregateType:
			return String.format(ProblemMessages.NotAnAggregateType, arguments[0]);
		case UnrecognizedPragma:
			return String.format(ProblemMessages.UnrecognizedPragma);
		case AnonCanOnlyBePartOfAnAggregate:
			return String.format(ProblemMessages.AnonCanOnlyBePartOfAnAggregate);
		case PragmaIsMissingClosingSemicolon:
			return String.format(ProblemMessages.PragmaIsMissingClosingSemicolon);
		case CannotImplicitlyConvert:
			return String.format(ProblemMessages.CannotImplicitlyConvert, arguments[0], arguments[1]);
		case ForbiddenReference:
			return String.format(ProblemMessages.ForbiddenReference);
		case DiscouragedReference:
			return String.format(ProblemMessages.DiscouragedReference);
		case Task:
			return String.format(ProblemMessages.Task, arguments[0]);
		case UndefinedType:
			return String.format(ProblemMessages.UndefinedType);
		case IsClassPathCorrect:
			return String.format(ProblemMessages.IsClassPathCorrect);
		case FunctionsCannotBeConstOrAuto:
			return String.format(ProblemMessages.FunctionsCannotBeConstOrAuto);
		case NonVirtualFunctionsCannotBeAbstract:
			return String.format(ProblemMessages.NonVirtualFunctionsCannotBeAbstract);
		case ModifierCannotBeAppliedToVariables:
			return String.format(ProblemMessages.ModifierCannotBeAppliedToVariables, arguments[0]);
		case StructsCannotBeAbstract:
			return String.format(ProblemMessages.StructsCannotBeAbstract);
		case UnionsCannotBeAbstract:
			return String.format(ProblemMessages.UnionsCannotBeAbstract);
		case AliasCannotBeConst:
			return String.format(ProblemMessages.AliasCannotBeConst);
		case OneArgumentOfTypeExpected:
			return String.format(ProblemMessages.OneArgumentOfTypeExpected, arguments[0]);
		case IllegalMainParameters:
			return String.format(ProblemMessages.IllegalMainParameters);
		case MustReturnIntOrVoidFromMainFunction:
			return String.format(ProblemMessages.MustReturnIntOrVoidFromMainFunction);
		case AtLeastOneArgumentOfTypeExpected:
			return String.format(ProblemMessages.AtLeastOneArgumentOfTypeExpected, arguments[0]);
		case FirstArgumentMustBeOfType:
			return String.format(ProblemMessages.FirstArgumentMustBeOfType, arguments[0]);
		case StringExpectedForPragmaMsg:
			return String.format(ProblemMessages.StringExpectedForPragmaMsg);
		case LibPragmaMustRecieveASingleArgumentOfTypeString:
			return String.format(ProblemMessages.LibPragmaMustRecieveASingleArgumentOfTypeString);
		case StringExpectedForPragmaLib:
			return String.format(ProblemMessages.StringExpectedForPragmaLib);
		case CannotHaveOutOrInoutParameterOfTypeStaticArray:
			return String.format(ProblemMessages.CannotHaveOutOrInoutParameterOfTypeStaticArray);
		case CannotHaveParameterOfTypeVoid:
			return String.format(ProblemMessages.CannotHaveParameterOfTypeVoid);
		case FunctionsCannotReturnStaticArrays:
			return String.format(ProblemMessages.FunctionsCannotReturnStaticArrays);
		case UnrecongnizedTrait:
			return String.format(ProblemMessages.UnrecongnizedTrait, arguments[0]);
		case CanOnlyConcatenateArrays:
			return String.format(ProblemMessages.CanOnlyConcatenateArrays, arguments[0], arguments[1]);
		case ArrayIndexOutOfBounds:
			return String.format(ProblemMessages.ArrayIndexOutOfBounds, arguments[0], arguments[1]);
		case AssertionFailed:
			return String.format(ProblemMessages.AssertionFailed, arguments[0]);
		case AssertionFailedNoMessage:
			return String.format(ProblemMessages.AssertionFailedNoMessage, arguments[0]);
		case ExpressionIsNotEvaluatableAtCompileTime:
			return String.format(ProblemMessages.ExpressionIsNotEvaluatableAtCompileTime, arguments[0]);
		case UndefinedProperty:
			return String.format(ProblemMessages.UndefinedProperty, arguments[0], arguments[1]);
		case DeprecatedProperty:
			return String.format(ProblemMessages.DeprecatedProperty, arguments[0], arguments[1]);
		case FileNameMustBeString:
			return String.format(ProblemMessages.FileNameMustBeString, arguments[0]);
		case FileImportsMustBeSpecified:
			return String.format(ProblemMessages.FileImportsMustBeSpecified, arguments[0]);
		case FileNotFound:
			return String.format(ProblemMessages.FileNotFound, arguments[0]);
		case ErrorReadingFile:
			return String.format(ProblemMessages.ErrorReadingFile, arguments[0]);
		case ExpressionHasNoEffect:
			return String.format(ProblemMessages.ExpressionHasNoEffect);
		case ConstantIsNotAnLValue:
			return String.format(ProblemMessages.ConstantIsNotAnLValue);
		case VersionIdentifierReserved:
			return String.format(ProblemMessages.VersionIdentifierReserved, arguments[0]);
		case CannotPutCatchStatementInsideFinallyBlock:
			return String.format(ProblemMessages.CannotPutCatchStatementInsideFinallyBlock);
		case ExpressionDoesNotGiveABooleanResult:
			return String.format(ProblemMessages.ExpressionDoesNotGiveABooleanResult);
		case BreakIsNotInsideALoopOrSwitch:
			return String.format(ProblemMessages.BreakIsNotInsideALoopOrSwitch);
		case CaseIsNotInSwitch:
			return String.format(ProblemMessages.CaseIsNotInSwitch);
		case VersionDeclarationMustBeAtModuleLevel:
			return String.format(ProblemMessages.VersionDeclarationMustBeAtModuleLevel);
		case DebugDeclarationMustBeAtModuleLevel:
			return String.format(ProblemMessages.DebugDeclarationMustBeAtModuleLevel);
		case GotoCaseNotInSwitch:
			return String.format(ProblemMessages.GotoCaseNotInSwitch);
		case GotoDefaultNotInSwitch:
			return String.format(ProblemMessages.GotoDefaultNotInSwitch);
		case LazyVariablesCannotBeLvalues:
			return String.format(ProblemMessages.LazyVariablesCannotBeLvalues);
		case DivisionByZero:
			return String.format(ProblemMessages.DivisionByZero);
		case DefaultNotInSwitch:
			return String.format(ProblemMessages.DefaultNotInSwitch);
		case SwitchAlreadyHasDefault:
			return String.format(ProblemMessages.SwitchAlreadyHasDefault);
		case ContinueNotInLoop:
			return String.format(ProblemMessages.ContinueNotInLoop);
		case ForeachIndexCannotBeRef:
			return String.format(ProblemMessages.ForeachIndexCannotBeRef);
		case ParametersDoesNotMatchParameterTypes:
			return String.format(ProblemMessages.ParametersDoesNotMatchParameterTypes, arguments[0], arguments[1]);
		case IncompatibleParameterStorageClass:
			return String.format(ProblemMessages.IncompatibleParameterStorageClass);
		case OutCannotBeFinal:
			return String.format(ProblemMessages.OutCannotBeFinal);
		case ScopeCannotBeRefOrOut:
			return String.format(ProblemMessages.ScopeCannotBeRefOrOut);
		case IncompatibleTypeForOperator:
			return String.format(ProblemMessages.IncompatibleTypeForOperator, arguments[0], arguments[1], arguments[2]);
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
