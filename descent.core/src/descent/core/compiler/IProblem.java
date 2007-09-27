/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package descent.core.compiler;

/**
 * Richer description of a D problem, as detected by the compiler or some of the underlying
 * technology reusing the compiler.
 * 
 * A problem provides access to:
 * <ul>
 * <li> its location (originating source file name, source position, line number), </li>
 * <li> its message description and a predicate to check its severity (warning or error). </li>
 * <li> its ID : a number identifying the very nature of this problem. All possible IDs for standard Java 
 * problems are listed as constants on <code>IProblem</code>, </li>
 * <li> its marker type : a string identfying the problem creator. It corresponds to the marker type
 * chosen if this problem was to be persisted. Standard Java problems are associated to marker
 * type "descent.core.problem"), </li>
 * <li> its category ID : a number identifying the category this problem belongs to. All possible IDs for 
 * standard Java problem categories are listed in this class. </li>
 * </ul>
 * 
 * Note: the compiler produces IProblems internally, which are turned into markers by the DBuilder
 * so as to persist problem descriptions. This explains why there is no API allowing to reach IProblem detected
 * when compiling. However, the D problem markers carry equivalent information to IProblem, in particular
 * their ID (attribute "id") is set to one of the IDs defined on this interface.
 */
public interface IProblem {
	
	/**
	 * Answer back the original arguments recorded into the problem.
	 * @return the original arguments recorded into the problem
	 */
	String[] getArguments();
	
	/**
	 * Returns the problem id
	 * 
	 * @return the problem id
	 */
	int getID();
	
	/**
	 * Answer a localized, human-readable message string which describes the problem.
	 * 
	 * @return a localized, human-readable message string which describes the problem
	 */
	String getMessage();
	
	/**
	 * Answer the file name in which the problem was found.
	 * 
	 * @return the file name in which the problem was found
	 */
	char[] getOriginatingFileName();
	
	/**
	 * Answer the end position of the problem (inclusive), or -1 if unknown.
	 * 
	 * @return the end position of the problem (inclusive), or -1 if unknown
	 */
	int getSourceEnd();

	/**
	 * Answer the line number in source where the problem begins.
	 * 
	 * @return the line number in source where the problem begins
	 */
	int getSourceLineNumber();

	/**
	 * Answer the start position of the problem (inclusive), or -1 if unknown.
	 * 
	 * @return the start position of the problem (inclusive), or -1 if unknown
	 */
	int getSourceStart();
	
	/**
	 * Checks the severity to see if the Error bit is set.
	 * 
	 * @return true if the Error bit is set for the severity, false otherwise
	 */
	boolean isError();

	/**
	 * Checks the severity to see if the Error bit is not set.
	 * 
	 * @return true if the Error bit is not set for the severity, false otherwise
	 */
	boolean isWarning();
	
	/**
	 * Set the end position of the problem (inclusive), or -1 if unknown.
	 * Used for shifting problem positions.
	 * 
	 * @param sourceEnd the given end position
	 */
	void setSourceEnd(int sourceEnd);

	/**
	 * Set the line number in source where the problem begins.
	 * 
	 * @param lineNumber the given line number
	 */
	void setSourceLineNumber(int lineNumber);

	/**
	 * Set the start position of the problem (inclusive), or -1 if unknown.
	 * Used for shifting problem positions.
	 * 
	 * @param sourceStart the given start position
	 */
	void setSourceStart(int sourceStart);
	
	/** 
	 * Returns an integer identifying the category of this problem. Categories, like problem IDs are
	 * defined in the context of some marker type. Custom implementations of <code>IProblem</code>
	 * may choose arbitrary values for problem/category IDs, as long as they are associated with a different
	 * marker type.
	 * Standard D problem markers (i.e. marker type is "descent.core.core.problem") carry an
	 * attribute "categoryId" persisting the originating problem category ID as defined by this method.
	 * @return id - an integer identifying the category of this problem
	 */
	public abstract int getCategoryID();

	/**
	 * Returns the marker type associated to this problem, if it gets persisted into a marker by the DBuilder
	 * Standard D problems are associated to marker type "descent.core.problem").
	 * Note: problem markers are expected to extend "org.eclipse.core.resources.problemmarker" marker type.
	 * @return the type of the marker which would be associated to the problem
	 */
	public abstract String getMarkerType();
	
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
	public String[] getExtraMarkerAttributeNames();

	/**
	 * Returns the respective values for the extra marker attributes associated to this problem when persisted into 
	 * a marker by the JavaBuilder. Each value must correspond to a matching attribute name, as defined by
	 * {@link #getExtraMarkerAttributeNames()}. 
	 * The values must be eligible for marker creation, as defined by <code>IMarker#setAttributes(String[], Object[])</code>.
	 * @return the values of the corresponding extra marker attributes
	 */
	public Object[] getExtraMarkerAttributeValues();
	
	/**
	 * List of standard category IDs used by Java problems, more categories will be added 
	 * in the future.
	 */
	int CAT_UNSPECIFIED = 0;
	/** Category for problems related to buildpath */
	int CAT_BUILDPATH = 10;
	/** Category for fatal problems related to syntax */
	int CAT_SYNTAX = 20;
	/** Category for fatal problems in import statements */
	int CAT_IMPORT = 30;
	/** Category for fatal problems related to types, could be addressed by some type change */
	int CAT_TYPE = 40;
	/** Category for fatal problems related to type members, could be addressed by some field or method change */
	int CAT_MEMBER = 50;
	/** Category for fatal problems which could not be addressed by external changes, but require an edit to be addressed */
	int CAT_INTERNAL = 60;	
	/** Category for optional problems in DDoc */
	int CAT_JAVADOC = 70;
	/** Category for optional problems related to coding style practices */
	int CAT_CODE_STYLE = 80;
	/** Category for optional problems related to potential programming flaws */
	int CAT_POTENTIAL_PROGRAMMING_PROBLEM = 90;
	/** Category for optional problems related to naming conflicts */
	int CAT_NAME_SHADOWING_CONFLICT = 100;
	/** Category for optional problems related to deprecation */
	int CAT_DEPRECATION = 110;
	/** Category for optional problems related to unnecessary code */
	int CAT_UNNECESSARY_CODE = 120;
	/** Category for optional problems related to type safety in generics */
	int CAT_UNCHECKED_RAW = 130;
	/** Category for optional problems related to internationalization of String literals */
	int CAT_NLS = 140;
	/** Category for optional problems related to access restrictions */
	int CAT_RESTRICTION = 150;
	
	/* Problems during lexing, parsing, and semantic analysis, generated automatically */
	int UnterminatedBlockComment = 1;
	int UnterminatedPlusBlockComment = 2;
	int IncorrectNumberOfHexDigitsInEscapeSequence = 3;
	int UndefinedEscapeHexSequence = 4;
	int UnterminatedStringConstant = 5;
	int OddNumberOfCharactersInHexString = 6;
	int NonHexCharacter = 7;
	int UnterminatedCharacterConstant = 8;
	int BinaryDigitExpected = 9;
	int OctalDigitExpected = 10;
	int HexDigitExpected = 11;
	int UnsupportedCharacter = 12;
	int InvalidUtfCharacter = 13;
	int ThreeEqualsIsNoLongerLegal = 14;
	int NotTwoEqualsIsNoLongerLegal = 15;
	int LSuffixDeprecated = 16;
	int InvalidPragmaSyntax = 17;
	int UnrecognizedCharacterEntity = 18;
	int UnterminatedNamedEntity = 19;
	int UndefinedEscapeSequence = 20;
	int InvalidUtf8Sequence = 21;
	int IntegerOverflow = 22;
	int SignedIntegerOverflow = 23;
	int UnrecognizedToken = 24;
	int BinaryExponentPartRequired = 25;
	int ExponentExpected = 26;
	int ISuffixDeprecated = 27;
	int ParsingErrorInsertTokenAfter = 28;
	int ParsingErrorDeleteToken = 29;
	int ParsingErrorInsertToComplete = 30;
	int EnumDeclarationIsInvalid = 31;
	int MismatchedStringLiteralPostfixes = 32;
	int NoIdentifierForDeclarator = 33;
	int AliasCannotHaveInitializer = 34;
	int CStyleCastIllegal = 35;
	int InvalidLinkageIdentifier = 36;
	int VariadicArgumentCannotBeOutOrRef = 37;
	int VariadicNotAllowedInDelete = 38;
	int NoIdentifierForTemplateValueParameter = 39;
	int UnexpectedIdentifierInDeclarator = 40;
	int RedundantStorageClass = 41;
	int UseBracesForAnEmptyStatement = 42;
	int MultipleDeclarationsMustHaveTheSameType = 43;
	int RedundantInStatement = 44;
	int RedundantOutStatement = 45;
	int StatementExpectedToBeCurlies = 46;
	int InvalidScopeIdentifier = 47;
	int OnScopeDeprecated = 48;
	int DollarInvalidOutsideBrackets = 49;
	int IftypeDeprecated = 50;
	int IfAutoDeprecated = 51;
	int VariadicTemplateParameterMustBeTheLastOne = 52;
	int NeedSizeOfRightmostArray = 53;
	int ConflictingStorageClass = 54;
	int DuplicatedSymbol = 55;
	int PropertyCanNotBeRedefined = 56;
	int CircularDefinition = 57;
	int EnumValueOverflow = 58;
	int EnumMustHaveAtLeastOneMember = 59;
	int EnumBaseTypeMustBeOfIntegralType = 60;
	int ForwardReference = 61;
	int ForwardReferenceWhenLookingFor = 62;
	int BaseEnumIsForwardReference = 63;
	int CannotResolveForwardReference = 64;
	int EnumIsForwardReference = 65;
	int IntegerConstantExpressionExpected = 66;
	int ThisNotInClassOrStruct = 67;
	int ThisOnlyAllowedInNonStaticMemberFunctions = 68;
	int SuperOnlyAllowedInNonStaticMemberFunctions = 69;
	int SuperNotInClass = 70;
	int ClassHasNoSuper = 71;
	int BaseTypeMustBeInterface = 72;
	int MemberIsPrivate = 73;
	int UsedAsAType = 74;
	int ExternSymbolsCannotHaveInitializers = 75;
	int VoidsHaveNoValue = 76;
	int CannotInferType = 77;
	int NoDefinition = 78;
	int DuplicatedInterfaceInheritance = 79;
	int BaseTypeMustBeClassOrInterface = 80;
	int FieldsNotAllowedInInterfaces = 81;
	int UndefinedIdentifier = 82;
	int NotAMember = 83;
	int NewAllocatorsOnlyForClassOrStruct = 84;
	int DeleteDeallocatorsOnlyForClassOrStruct = 85;
	int ConstructorsOnlyForClass = 86;
	int DestructorsOnlyForClass = 87;
	int InvariantsOnlyForClassStructUnion = 88;
	int FunctionDoesNotOverrideAny = 89;
	int CannotOverrideFinalFunctions = 90;
	int OverrideOnlyForClassMemberFunctions = 91;
	int FunctionMustReturnAResultOfType = 92;
	int MoreThanOneInvariant = 93;
	int DuplicatedParameter = 94;
	int SymbolNotFound = 95;
	int StatementIsNotReachable = 96;
	int VoidFunctionsHaveNoResult = 97;
	int ReturnStatementsCannotBeInContracts = 98;
	int NotAnAggregateType = 99;
	int UnrecognizedPragma = 100;
	int AnonCanOnlyBePartOfAnAggregate = 101;
	int PragmaIsMissingClosingSemicolon = 102;
	int CannotImplicitlyConvert = 103;
	int ForbiddenReference = 104;
	int DiscouragedReference = 105;
	int Task = 106;
	int UndefinedType = 107;
	int IsClassPathCorrect = 108;
	int FunctionsCannotBeConstOrAuto = 109;
	int NonVirtualFunctionsCannotBeAbstract = 110;
	int ModifierCannotBeAppliedToVariables = 111;
	int StructsCannotBeAbstract = 112;
	int UnionsCannotBeAbstract = 113;
	int AliasCannotBeConst = 114;
	int OneArgumentOfTypeExpected = 115;
	int IllegalMainParameters = 116;
	int MustReturnIntOrVoidFromMainFunction = 117;
	int AtLeastOneArgumentOfTypeExpected = 118;
	int FirstArgumentMustBeOfType = 119;
	int StringExpectedForPragmaMsg = 120;
	int LibPragmaMustRecieveASingleArgumentOfTypeString = 121;
	int StringExpectedForPragmaLib = 122;
	int CannotHaveOutOrInoutParameterOfTypeStaticArray = 123;
	int CannotHaveParameterOfTypeVoid = 124;
	int FunctionsCannotReturnStaticArrays = 125;
	int UnrecongnizedTrait = 126;
	int CanOnlyConcatenateArrays = 127;
	int ArrayIndexOutOfBounds = 128;
	int AssertionFailed = 129;
	int AssertionFailedNoMessage = 130;
	int ExpressionIsNotEvaluatableAtCompileTime = 131;
	int UndefinedProperty = 132;
	int DeprecatedProperty = 133;
	int FileNameMustBeString = 134;
	int FileImportsMustBeSpecified = 135;
	int FileNotFound = 136;
	int ErrorReadingFile = 137;
	int ExpressionHasNoEffect = 138;
	int ConstantIsNotAnLValue = 139;
	int VersionIdentifierReserved = 140;
	int CannotPutCatchStatementInsideFinallyBlock = 141;
	int ExpressionDoesNotGiveABooleanResult = 142;
	int BreakIsNotInsideALoopOrSwitch = 143;
	int CaseIsNotInSwitch = 144;
	int VersionDeclarationMustBeAtModuleLevel = 145;
	int DebugDeclarationMustBeAtModuleLevel = 146;
	int GotoCaseNotInSwitch = 147;
	int GotoDefaultNotInSwitch = 148;
	int LazyVariablesCannotBeLvalues = 149;
	int DivisionByZero = 150;
	int DefaultNotInSwitch = 151;
	int SwitchAlreadyHasDefault = 152;
	int ContinueNotInLoop = 153;
	int ForeachIndexCannotBeRef = 154;
	int ParametersDoesNotMatchParameterTypes = 155;
	int IncompatibleParameterStorageClass = 156;
	int OutCannotBeFinal = 157;
	int ScopeCannotBeRefOrOut = 158;
	int IncompatibleTypeForOperator = 159;
	int SymbolNotDefined = 160;
	int SymbolNotATemplate = 161;
	int CannotDeleteType = 162;
	int NotAnLvalue = 163;
	int CannotAliasAnExpression = 164;
	int CannotAssignToStaticArray = 165;
	int CannotChangeReferenceToStaticArray = 166;
	int CannotModifyParameterInContract = 167;
	int BothOverloadsMuchArgumentList = 168;
	int ExpressionHasNoType = 169;
	int SymbolNotAnExpression = 170;
	int SymbolHasNoValue = 171;
	int TooManyInitializers = 172;
	int SymbolNotAStaticAndCannotHaveStaticInitializer = 173;
	int SymbolNotAType = 174;
	int IncompleteMixinDeclaration = 175;

}
