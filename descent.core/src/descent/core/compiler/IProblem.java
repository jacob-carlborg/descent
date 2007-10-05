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
	int SymbolConflictsWithSymbolAtLocation = 55;
	int SymbolAtLocationConflictsWithSymbolAtLocation = 56;
	int PropertyCanNotBeRedefined = 57;
	int CircularDefinition = 58;
	int EnumValueOverflow = 59;
	int EnumMustHaveAtLeastOneMember = 60;
	int EnumBaseTypeMustBeOfIntegralType = 61;
	int ForwardReference = 62;
	int ForwardReferenceWhenLookingFor = 63;
	int BaseEnumIsForwardReference = 64;
	int CannotResolveForwardReference = 65;
	int EnumIsForwardReference = 66;
	int IntegerConstantExpressionExpected = 67;
	int ThisNotInClassOrStruct = 68;
	int ThisOnlyAllowedInNonStaticMemberFunctions = 69;
	int SuperOnlyAllowedInNonStaticMemberFunctions = 70;
	int SuperNotInClass = 71;
	int ClassHasNoSuper = 72;
	int BaseTypeMustBeInterface = 73;
	int MemberIsPrivate = 74;
	int UsedAsAType = 75;
	int ExternSymbolsCannotHaveInitializers = 76;
	int VoidsHaveNoValue = 77;
	int CannotInferType = 78;
	int NoDefinition = 79;
	int DuplicatedInterfaceInheritance = 80;
	int BaseTypeMustBeClassOrInterface = 81;
	int FieldsNotAllowedInInterfaces = 82;
	int UndefinedIdentifier = 83;
	int NotAMember = 84;
	int NewAllocatorsOnlyForClassOrStruct = 85;
	int DeleteDeallocatorsOnlyForClassOrStruct = 86;
	int ConstructorsOnlyForClass = 87;
	int DestructorsOnlyForClass = 88;
	int InvariantsOnlyForClassStructUnion = 89;
	int FunctionDoesNotOverrideAny = 90;
	int CannotOverrideFinalFunctions = 91;
	int OverrideOnlyForClassMemberFunctions = 92;
	int FunctionMustReturnAResultOfType = 93;
	int MoreThanOneInvariant = 94;
	int DuplicatedParameter = 95;
	int SymbolNotFound = 96;
	int StatementIsNotReachable = 97;
	int VoidFunctionsHaveNoResult = 98;
	int ReturnStatementsCannotBeInContracts = 99;
	int NotAnAggregateType = 100;
	int UnrecognizedPragma = 101;
	int AnonCanOnlyBePartOfAnAggregate = 102;
	int PragmaIsMissingClosingSemicolon = 103;
	int CannotImplicitlyConvert = 104;
	int ForbiddenReference = 105;
	int DiscouragedReference = 106;
	int Task = 107;
	int UndefinedType = 108;
	int IsClassPathCorrect = 109;
	int FunctionsCannotBeConstOrAuto = 110;
	int NonVirtualFunctionsCannotBeAbstract = 111;
	int ModifierCannotBeAppliedToVariables = 112;
	int StructsCannotBeAbstract = 113;
	int UnionsCannotBeAbstract = 114;
	int AliasCannotBeConst = 115;
	int OneArgumentOfTypeExpected = 116;
	int IllegalMainParameters = 117;
	int MustReturnIntOrVoidFromMainFunction = 118;
	int AtLeastOneArgumentOfTypeExpected = 119;
	int FirstArgumentMustBeOfType = 120;
	int StringExpectedForPragmaMsg = 121;
	int LibPragmaMustRecieveASingleArgumentOfTypeString = 122;
	int StringExpectedForPragmaLib = 123;
	int CannotHaveOutOrInoutParameterOfTypeStaticArray = 124;
	int CannotHaveParameterOfTypeVoid = 125;
	int FunctionsCannotReturnStaticArrays = 126;
	int UnrecongnizedTrait = 127;
	int CanOnlyConcatenateArrays = 128;
	int ArrayIndexOutOfBounds = 129;
	int AssertionFailed = 130;
	int AssertionFailedNoMessage = 131;
	int ExpressionIsNotEvaluatableAtCompileTime = 132;
	int UndefinedProperty = 133;
	int DeprecatedProperty = 134;
	int FileNameMustBeString = 135;
	int FileImportsMustBeSpecified = 136;
	int FileNotFound = 137;
	int ErrorReadingFile = 138;
	int ExpressionHasNoEffect = 139;
	int ConstantIsNotAnLValue = 140;
	int VersionIdentifierReserved = 141;
	int CannotPutCatchStatementInsideFinallyBlock = 142;
	int ExpressionDoesNotGiveABooleanResult = 143;
	int BreakIsNotInsideALoopOrSwitch = 144;
	int CaseIsNotInSwitch = 145;
	int VersionDeclarationMustBeAtModuleLevel = 146;
	int DebugDeclarationMustBeAtModuleLevel = 147;
	int GotoCaseNotInSwitch = 148;
	int GotoDefaultNotInSwitch = 149;
	int LazyVariablesCannotBeLvalues = 150;
	int DivisionByZero = 151;
	int DefaultNotInSwitch = 152;
	int SwitchAlreadyHasDefault = 153;
	int ContinueNotInLoop = 154;
	int ForeachIndexCannotBeRef = 155;
	int ParametersDoesNotMatchParameterTypes = 156;
	int IncompatibleParameterStorageClass = 157;
	int OutCannotBeFinal = 158;
	int ScopeCannotBeRefOrOut = 159;
	int IncompatibleTypeForOperator = 160;
	int SymbolNotDefined = 161;
	int SymbolNotATemplate = 162;
	int CannotDeleteType = 163;
	int NotAnLvalue = 164;
	int CannotAliasAnExpression = 165;
	int CannotAssignToStaticArray = 166;
	int CannotChangeReferenceToStaticArray = 167;
	int CannotModifyParameterInContract = 168;
	int BothOverloadsMuchArgumentList = 169;
	int ExpressionHasNoType = 170;
	int SymbolNotAnExpression = 171;
	int SymbolHasNoValue = 172;
	int TooManyInitializers = 173;
	int SymbolNotAStaticAndCannotHaveStaticInitializer = 174;
	int SymbolNotAType = 175;
	int IncompleteMixinDeclaration = 176;
	int SymbolNotATemplateItIs = 177;
	int SymbolCannotBeDeclaredToBeAFunction = 178;
	int CannotHaveArrayOfType = 179;
	int SymbolDoesNotMatchAnyTemplateDeclaration = 180;
	int IndexOverflowForStaticArray = 181;
	int UnknownSize = 182;
	int NoSizeYetForForwardReference = 183;
	int SymbolMatchesMoreThanOneTemplateDeclaration = 184;
	int ExpressionLeadsToStackOverflowAtCompileTime = 185;
	int StringIndexOutOfBounds = 186;
	int CannotCreateInstanceOfAbstractClass = 187;
	int CannotCreateInstanceOfInterface = 188;
	int WithExpressionsMustBeClassObject = 189;
	int DeclarationIsAlreadyDefined = 190;
	int VersionDefinedAfterUse = 191;
	int DebugDefinedAfterUse = 192;
	int NotEnoughArguments = 193;
	int CanOnlySynchronizeOnClassObjects = 194;
	int CannotDeduceTemplateFunctionFromArgumentTypes = 195;
	int ArrayDimensionExceedsMax = 196;
	int AStructIsNotAValidInitializerFor = 197;
	int CannotUseArrayToInitialize = 198;
	int MemberIsNotAccessible = 199;
	int SymbolIsNotAccessible = 200;
	int ThisForSymbolNeedsToBeType = 201;
	int SymbolHasForwardReferences = 202;

}
