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
	int ForwardReferenceOfSymbol = 62;
	int ForwardReferenceOfSymbolDotSymbol = 63;
	int ForwardReferenceWhenLookingFor = 64;
	int BaseEnumIsForwardReference = 65;
	int CannotResolveForwardReference = 66;
	int EnumIsForwardReference = 67;
	int IntegerConstantExpressionExpected = 68;
	int ThisNotInClassOrStruct = 69;
	int ThisOnlyAllowedInNonStaticMemberFunctions = 70;
	int SuperOnlyAllowedInNonStaticMemberFunctions = 71;
	int SuperNotInClass = 72;
	int ClassHasNoSuper = 73;
	int BaseTypeMustBeInterface = 74;
	int MemberIsPrivate = 75;
	int UsedAsAType = 76;
	int ExternSymbolsCannotHaveInitializers = 77;
	int VoidsHaveNoValue = 78;
	int CannotInferTypeFromThisArrayInitializer = 79;
	int NoDefinition = 80;
	int DuplicatedInterfaceInheritance = 81;
	int BaseTypeMustBeClassOrInterface = 82;
	int FieldsNotAllowedInInterfaces = 83;
	int UndefinedIdentifier = 84;
	int NotAMember = 85;
	int NewAllocatorsOnlyForClassOrStruct = 86;
	int DeleteDeallocatorsOnlyForClassOrStruct = 87;
	int ConstructorsOnlyForClass = 88;
	int DestructorsOnlyForClass = 89;
	int InvariantsOnlyForClassStructUnion = 90;
	int FunctionDoesNotOverrideAny = 91;
	int CannotOverrideFinalFunctions = 92;
	int OverrideOnlyForClassMemberFunctions = 93;
	int FunctionMustReturnAResultOfType = 94;
	int MoreThanOneInvariant = 95;
	int ParameterMultiplyDefined = 96;
	int SymbolNotFound = 97;
	int StatementIsNotReachable = 98;
	int VoidFunctionsHaveNoResult = 99;
	int ReturnStatementsCannotBeInContracts = 100;
	int NotAnAggregateType = 101;
	int UnrecognizedPragma = 102;
	int AnonCanOnlyBePartOfAnAggregate = 103;
	int PragmaIsMissingClosingSemicolon = 104;
	int CannotImplicitlyConvert = 105;
	int ForbiddenReference = 106;
	int DiscouragedReference = 107;
	int Task = 108;
	int UndefinedType = 109;
	int IsClassPathCorrect = 110;
	int FunctionsCannotBeConstOrAuto = 111;
	int NonVirtualFunctionsCannotBeAbstract = 112;
	int ModifierCannotBeAppliedToVariables = 113;
	int StructsCannotBeAbstract = 114;
	int UnionsCannotBeAbstract = 115;
	int AliasCannotBeConst = 116;
	int OneArgumentOfTypeExpected = 117;
	int IllegalMainParameters = 118;
	int MustReturnIntOrVoidFromMainFunction = 119;
	int AtLeastOneArgumentOfTypeExpected = 120;
	int FirstArgumentMustBeOfType = 121;
	int StringExpectedForPragmaMsg = 122;
	int LibPragmaMustRecieveASingleArgumentOfTypeString = 123;
	int StringExpectedForPragmaLib = 124;
	int CannotHaveOutOrInoutParameterOfTypeStaticArray = 125;
	int CannotHaveParameterOfTypeVoid = 126;
	int FunctionsCannotReturnStaticArrays = 127;
	int UnrecongnizedTrait = 128;
	int CanOnlyConcatenateArrays = 129;
	int ArrayIndexOutOfBounds = 130;
	int AssertionFailed = 131;
	int AssertionFailedNoMessage = 132;
	int ExpressionIsNotEvaluatableAtCompileTime = 133;
	int UndefinedProperty = 134;
	int DeprecatedProperty = 135;
	int FileNameMustBeString = 136;
	int FileImportsMustBeSpecified = 137;
	int FileNotFound = 138;
	int ErrorReadingFile = 139;
	int ExpressionHasNoEffect = 140;
	int ConstantIsNotAnLValue = 141;
	int VersionIdentifierReserved = 142;
	int CannotPutCatchStatementInsideFinallyBlock = 143;
	int ExpressionDoesNotGiveABooleanResult = 144;
	int BreakIsNotInsideALoopOrSwitch = 145;
	int CaseIsNotInSwitch = 146;
	int VersionDeclarationMustBeAtModuleLevel = 147;
	int DebugDeclarationMustBeAtModuleLevel = 148;
	int GotoCaseNotInSwitch = 149;
	int GotoDefaultNotInSwitch = 150;
	int LazyVariablesCannotBeLvalues = 151;
	int DivisionByZero = 152;
	int DefaultNotInSwitch = 153;
	int SwitchAlreadyHasDefault = 154;
	int ContinueNotInLoop = 155;
	int ForeachIndexCannotBeRef = 156;
	int ParametersDoesNotMatchParameterTypes = 157;
	int IncompatibleParameterStorageClass = 158;
	int OutCannotBeFinal = 159;
	int ScopeCannotBeRefOrOut = 160;
	int IncompatibleTypesForOperator = 161;
	int IncompatibleTypesForMinus = 162;
	int SymbolNotDefined = 163;
	int SymbolNotATemplate = 164;
	int CannotDeleteType = 165;
	int NotAnLvalue = 166;
	int CannotAliasAnExpression = 167;
	int CannotAssignToStaticArray = 168;
	int CannotChangeReferenceToStaticArray = 169;
	int CannotModifyParameterInContract = 170;
	int BothOverloadsMuchArgumentList = 171;
	int ExpressionHasNoType = 172;
	int SymbolNotAnExpression = 173;
	int SymbolHasNoValue = 174;
	int TooManyInitializers = 175;
	int SymbolNotAStaticAndCannotHaveStaticInitializer = 176;
	int SymbolNotAType = 177;
	int IncompleteMixinDeclaration = 178;
	int SymbolNotATemplateItIs = 179;
	int SymbolCannotBeDeclaredToBeAFunction = 180;
	int CannotHaveArrayOfType = 181;
	int SymbolDoesNotMatchAnyTemplateDeclaration = 182;
	int IndexOverflowForStaticArray = 183;
	int UnknownSize = 184;
	int NoSizeYetForForwardReference = 185;
	int SymbolMatchesMoreThanOneTemplateDeclaration = 186;
	int ExpressionLeadsToStackOverflowAtCompileTime = 187;
	int StringIndexOutOfBounds = 188;
	int CannotCreateInstanceOfAbstractClass = 189;
	int CannotCreateInstanceOfInterface = 190;
	int WithExpressionsMustBeClassObject = 191;
	int DeclarationIsAlreadyDefined = 192;
	int VersionDefinedAfterUse = 193;
	int DebugDefinedAfterUse = 194;
	int NotEnoughArguments = 195;
	int CanOnlySynchronizeOnClassObjects = 196;
	int CannotDeduceTemplateFunctionFromArgumentTypes = 197;
	int ArrayDimensionExceedsMax = 198;
	int AStructIsNotAValidInitializerFor = 199;
	int CannotUseArrayToInitialize = 200;
	int CircularReferenceTo = 201;
	int ParameterIsAlreadyDefined = 202;
	int MemberIsNotAccessible = 203;
	int SymbolIsNotAccessible = 204;
	int ThisForSymbolNeedsToBeType = 205;
	int SymbolHasForwardReferences = 206;
	int CannotHaveAssociativeArrayOfKey = 207;
	int CannotHaveAssociativeArrayOf = 208;
	int CannotHaveArrayOfAuto = 209;
	int EnclosingLabelForBreakNotFound = 210;
	int EnclosingLabelForContinueNotFound = 211;
	int CannotAppendTypeToType = 212;
	int CannotAppendToStaticArray = 213;
	int ExpressionIsVoidAndHasNoValue = 214;
	int NeedMemberFunctionOpCmpForSymbolToCompare = 215;
	int CompareNotDefinedForComplexOperands = 216;
	int NeedThisForAddressOfSymbol = 217;
	int RecursiveMixinInstantiation = 218;
	int SymbolIsNotOfIntegralType = 219;
	int DeleteAAKeyDeprecated = 220;
	int SymbolIsDeprecated = 221;
	int ShadowingDeclarationIsDeprecated = 222;
	int ReturnStatementsCannotBeInFinallyScopeExitOrScopeSuccessBodies = 223;
	int CannotReturnExpressionFromConstructor = 224;
	int CaseNotFound = 225;
	int CircularInheritanceOfInterface = 226;
	int ArgumentToMixinMustBeString = 227;
	int CannotAccessFrameOfFunction = 228;
	int OperationNotAllowedOnBool = 229;
	int SymbolIsNotAScalar = 230;
	int ImportCannotBeResolved = 231;
	int SymbolIsNotAVariable = 232;
	int CatchHidesCatch = 233;
	int ArithmeticOrStringTypeExpectedForValueParameter = 234;
	int FunctionsCannotReturnAFunction = 235;
	int FunctionsCannotReturnATuple = 236;
	int FunctionsCannotReturnAuto = 237;
	int RecursiveType = 238;
	int VariadicFunctionsWithNonDLinkageMustHaveAtLeastOneParameter = 239;
	int SymbolMustBeAFunction = 240;
	int FunctionExpectedBeforeCall = 241;
	int FunctionExpectedBeforeCallNotSymbolOfType = 242;
	int CircularReferenceOfTypedef = 243;
	int StringSliceIsOutOfBounds = 244;
	int ErrorInstantiating = 245;
	int CaseMustBeAnIntegralOrStringConstant = 246;
	int DuplicateCaseInSwitchStatement = 247;
	int SpecialMemberFunctionsNotAllowedForSymbol = 248;
	int SpecialFunctionsNotAllowedInInterface = 249;
	int FunctionBodyIsNotAbstractInInterface = 250;
	int SuperClassConstructorCallMustBeInAConstructor = 251;
	int ClassConstructorCallMustBeInAConstructor = 252;
	int NoSuperClassConstructor = 253;
	int ConstructorCallsNotAllowedInLoopsOrAfterLabels = 254;
	int MultipleConstructorCalls = 255;
	int ExpressionIsNotConstantOrDoesNotEvaluateToABool = 256;
	int StaticIfConditionalCannotBeAtGlobalScope = 257;
	int CannotBreakOutOfFinallyBlock = 258;
	int LabelHasNoBreak = 259;
	int LabelHasNoContinue = 260;
	int CannotGotoInOrOutOfFinallyBlock = 261;
	int CalledWithArgumentTypesMatchesBoth = 262;
	int SymbolIsNotAnArithmeticType = 263;
	int CannotPerformModuloComplexArithmetic = 264;
	int OperatorNotAllowedOnBoolExpression = 265;
	int ForeachKeyTypeMustBeIntOrUint = 266;
	int ForeachKeyCannotBeOutOrRef = 267;
	int NoReverseIterationOnAssociativeArrays = 268;
	int OnlyOneOrTwoArgumentsForAssociativeArrayForeach = 269;
	int OnlyOneOrTwoArgumentsForArrayForeach = 270;
	int ForeachTargetIsNotAnArrayOf = 271;
	int ForeachValueCannotBeOutAndTypeBit = 272;
	int ForeachKeyCannotBeInout = 273;
	int ForeachValueOfUTFConversionCannotBeInout = 274;
	int CannotInferTypeForSymbol = 275;
	int CannotInferTypeFromInitializer = 276;
	int NoStorageClassForSymbol = 277;
	int OnlyOneValueOrTwoKeyValueArgumentsForTupleForeach = 278;
	int CannotUniquelyInferForeachArgumentTypes = 279;
	int InvalidForeachAggregate = 280;
	int NotAnAssociativeArrayInitializer = 281;
	int ArrayInitializersAsExpressionsNotAllowed = 282;
	int IftypeConditionCannotBeAtGlobalScope = 283;
	int SymbolIsNotAFieldOfSymbol = 284;
	int RecursiveTemplateExpansion = 285;
	int IndexIsNotATypeOrExpression = 286;
	int CannotHavePointerToSymbol = 287;
	int SizeOfTypeIsNotKnown = 288;
	int CanOnlySliceTupleTypes = 289;
	int NoPropertyForTuple = 290;
	int CannotResolveDotProperty = 291;
	int CannotTakeAddressOfBitInArray = 292;
	int OnlyOneIndexAllowedToIndex = 293;
	int NoOpIndexOperatorOverloadForType = 294;
	int ArrayDimensionOverflow = 295;
	int OperatorAssignmentOverloadWithOpIndexIllegal = 296;
	int CannotHaveOutOrInoutArgumentOfBitInArray = 297;
	int SymbolIsAliasedToAFunction = 298;
	int LinkageDoesNotMatchInterfaceFunction = 299;
	int InterfaceFunctionIsNotImplemented = 300;
	int ExpectedKeyAsArgumentToRemove = 301;
	int CyclicConstructorCall = 302;
	int MissingOrCurruptObjectDotD = 303;
	int CannotContinueOutOfFinallyBlock = 304;
	int ForwardDeclaration = 305;
	int CannotFormDelegateDueToCovariantReturnType = 306;
	int ForeachRangeKeyCannotHaveStorageClass = 307;
	int MultipleOverridesOfSameFunction = 308;
	int IdentityAssignmentOperatorOverloadIsIllegal = 309;
	int LiteralsCannotBeClassMembers = 310;
	int NoMatchForImplicitSuperCallInConstructor = 311;
	int NoReturnAtEndOfFunction = 312;
	int CanOnlyDeclareTypeAliasesWithinStaticIfConditionals = 313;
	int PackageAndModuleHaveTheSameName = 314;
	int StringLiteralsAreImmutable = 315;
	int ExpressionDotNewIsOnlyForAllocatingNestedClasses = 316;
	int TooManyArgumentsForArray = 317;
	int ReturnExpressionExpected = 318;
	int ReturnWithoutCallingConstructor = 319;

}
