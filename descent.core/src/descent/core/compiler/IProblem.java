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
	
	/* Problems during lexing, parsing, and semantic analysis, generated automatically,
	   65 is the first semantic error in the list. */
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
	int RedundantProtectionAttribute = 42;
	int UseBracesForAnEmptyStatement = 43;
	int MultipleDeclarationsMustHaveTheSameType = 44;
	int RedundantInStatement = 45;
	int RedundantOutStatement = 46;
	int StatementExpectedToBeCurlies = 47;
	int InvalidScopeIdentifier = 48;
	int OnScopeDeprecated = 49;
	int DollarInvalidOutsideBrackets = 50;
	int IftypeDeprecated = 51;
	int IfAutoDeprecated = 52;
	int VariadicTemplateParameterMustBeTheLastOne = 53;
	int NeedSizeOfRightmostArray = 54;
	int ConflictingStorageClass = 55;
	int ValueIsLargerThanAByte = 56;
	int UnterminatedTokenStringConstant = 57;
	int HeredocRestOfLineShouldBeBlank = 58;
	int IdentifierExpectedForHeredoc = 59;
	int DelimitedStringMustEndInValue = 60;
	int TypeOnlyAllowedIfAnonymousEnumAndNoEnumType = 61;
	int IfTypeThereMustBeAnInitializer = 62;
	int InvariantAsAttributeIsOnlySupportedInD2 = 63;
	int ConstAsAttributeIsOnlySupportedInD2 = 64;
	int OnlyOneCaseAllowedForStartOfCaseRange = 65;
	int SymbolConflictsWithSymbolAtLocation = 66;
	int SymbolAtLocationConflictsWithSymbolAtLocation = 67;
	int PropertyCanNotBeRedefined = 68;
	int CircularDefinition = 69;
	int EnumValueOverflow = 70;
	int EnumMustHaveAtLeastOneMember = 71;
	int EnumBaseTypeMustBeOfIntegralType = 72;
	int ForwardReferenceOfSymbol = 73;
	int ForwardReferenceOfEnumSymbolDotSymbol = 74;
	int ForwardReferenceWhenLookingFor = 75;
	int BaseEnumIsForwardReference = 76;
	int CannotResolveForwardReference = 77;
	int EnumIsForwardReference = 78;
	int IntegerConstantExpressionExpected = 79;
	int ThisNotInClassOrStruct = 80;
	int ThisOnlyAllowedInNonStaticMemberFunctions = 81;
	int SuperOnlyAllowedInNonStaticMemberFunctions = 82;
	int SuperNotInClass = 83;
	int ClassHasNoSuper = 84;
	int BaseTypeMustBeInterface = 85;
	int MemberIsPrivate = 86;
	int UsedAsAType = 87;
	int ExternSymbolsCannotHaveInitializers = 88;
	int VoidsHaveNoValue = 89;
	int CannotInferTypeFromThisArrayInitializer = 90;
	int NoDefinition = 91;
	int DuplicatedInterfaceInheritance = 92;
	int BaseTypeMustBeClassOrInterface = 93;
	int FieldsNotAllowedInInterfaces = 94;
	int UndefinedIdentifier = 95;
	int NotAMember = 96;
	int NewAllocatorsOnlyForClassOrStruct = 97;
	int DeleteDeallocatorsOnlyForClassOrStruct = 98;
	int ConstructorsOnlyForClass = 99;
	int ConstructorsOnlyForClassOrStruct = 100;
	int DestructorsOnlyForClass = 101;
	int InvariantsOnlyForClassStructUnion = 102;
	int FunctionDoesNotOverrideAny = 103;
	int CannotOverrideFinalFunctions = 104;
	int OverrideOnlyForClassMemberFunctions = 105;
	int FunctionMustReturnAResultOfType = 106;
	int MoreThanOneInvariant = 107;
	int ParameterMultiplyDefined = 108;
	int SymbolNotFound = 109;
	int StatementIsNotReachable = 110;
	int VoidFunctionsHaveNoResult = 111;
	int ReturnStatementsCannotBeInContracts = 112;
	int NotAnAggregateType = 113;
	int UnrecognizedPragma = 114;
	int AnonCanOnlyBePartOfAnAggregate = 115;
	int PragmaIsMissingClosingSemicolon = 116;
	int CannotImplicitlyConvert = 117;
	int ForbiddenReference = 118;
	int DiscouragedReference = 119;
	int Task = 120;
	int UndefinedType = 121;
	int IsClassPathCorrect = 122;
	int FunctionsCannotBeConstOrAuto = 123;
	int FunctionsCannotBeScopeOrAuto = 124;
	int NonVirtualFunctionsCannotBeAbstract = 125;
	int CannotBeBothAbstractAndFinal = 126;
	int ModifierCannotBeAppliedToVariables = 127;
	int StructsCannotBeAbstract = 128;
	int UnionsCannotBeAbstract = 129;
	int AliasCannotBeConst = 130;
	int OneArgumentOfTypeExpected = 131;
	int IllegalMainParameters = 132;
	int MustReturnIntOrVoidFromMainFunction = 133;
	int AtLeastOneArgumentOfTypeExpected = 134;
	int FirstArgumentMustBeOfType = 135;
	int StringExpectedForPragmaMsg = 136;
	int LibPragmaMustRecieveASingleArgumentOfTypeString = 137;
	int StringExpectedForPragmaLib = 138;
	int CannotHaveOutOrInoutParameterOfTypeStaticArray = 139;
	int CannotHaveParameterOfTypeVoid = 140;
	int FunctionsCannotReturnStaticArrays = 141;
	int UnrecongnizedTrait = 142;
	int CanOnlyConcatenateArrays = 143;
	int ArrayIndexOutOfBounds = 144;
	int ArrayIndexOutOfBounds2 = 145;
	int AssertionFailed = 146;
	int AssertionFailedNoMessage = 147;
	int ExpressionIsNotEvaluatableAtCompileTime = 148;
	int UndefinedProperty = 149;
	int DeprecatedProperty = 150;
	int FileNameMustBeString = 151;
	int FileImportsMustBeSpecified = 152;
	int FileNotFound = 153;
	int ErrorReadingFile = 154;
	int ExpressionHasNoEffect = 155;
	int ConstantIsNotAnLValue = 156;
	int VersionIdentifierReserved = 157;
	int CannotPutCatchStatementInsideFinallyBlock = 158;
	int ExpressionDoesNotGiveABooleanResult = 159;
	int BreakIsNotInsideALoopOrSwitch = 160;
	int CaseIsNotInSwitch = 161;
	int VersionDeclarationMustBeAtModuleLevel = 162;
	int DebugDeclarationMustBeAtModuleLevel = 163;
	int GotoCaseNotInSwitch = 164;
	int GotoDefaultNotInSwitch = 165;
	int LazyVariablesCannotBeLvalues = 166;
	int DivisionByZero = 167;
	int DefaultNotInSwitch = 168;
	int SwitchAlreadyHasDefault = 169;
	int ContinueNotInLoop = 170;
	int ForeachIndexCannotBeRef = 171;
	int ParametersDoesNotMatchParameterTypes = 172;
	int IncompatibleParameterStorageClass = 173;
	int OutCannotBeConst = 174;
	int OutCannotBeInvariant = 175;
	int ScopeCannotBeRefOrOut = 176;
	int IncompatibleTypesForOperator = 177;
	int IncompatibleTypesForMinus = 178;
	int SymbolNotDefined = 179;
	int SymbolNotATemplate = 180;
	int CannotDeleteType = 181;
	int NotAnLvalue = 182;
	int CannotAliasAnExpression = 183;
	int CannotAssignToStaticArray = 184;
	int CannotChangeReferenceToStaticArray = 185;
	int CannotModifyParameterInContract = 186;
	int BothOverloadsMuchArgumentList = 187;
	int ExpressionHasNoType = 188;
	int SymbolNotAnExpression = 189;
	int SymbolHasNoValue = 190;
	int TooManyInitializers = 191;
	int SymbolNotAStaticAndCannotHaveStaticInitializer = 192;
	int SymbolNotAType = 193;
	int IncompleteMixinDeclaration = 194;
	int SymbolNotATemplateItIs = 195;
	int SymbolCannotBeDeclaredToBeAFunction = 196;
	int CannotHaveArrayOfType = 197;
	int SymbolDoesNotMatchAnyTemplateDeclaration = 198;
	int SymbolDoesNotMatchTemplateDeclaration = 199;
	int SymbolDoesNotMatchAnyFunctionTemplateDeclaration = 200;
	int IndexOverflowForStaticArray = 201;
	int UnknownSize = 202;
	int NoSizeYetForForwardReference = 203;
	int SymbolMatchesMoreThanOneTemplateDeclaration = 204;
	int ExpressionLeadsToStackOverflowAtCompileTime = 205;
	int StringIndexOutOfBounds = 206;
	int CannotCreateInstanceOfAbstractClass = 207;
	int CannotCreateInstanceOfInterface = 208;
	int WithExpressionsMustBeClassObject = 209;
	int DeclarationIsAlreadyDefined = 210;
	int DeclarationIsAlreadyDefinedInAnotherScope = 211;
	int VersionDefinedAfterUse = 212;
	int DebugDefinedAfterUse = 213;
	int NotEnoughArguments = 214;
	int CanOnlySynchronizeOnClassObjects = 215;
	int CannotDeduceTemplateFunctionFromArgumentTypes = 216;
	int CannotDeduceTemplateFunctionFromArgumentTypes2 = 217;
	int ArrayDimensionExceedsMax = 218;
	int AStructIsNotAValidInitializerFor = 219;
	int CannotUseArrayToInitialize = 220;
	int CircularReferenceTo = 221;
	int ParameterIsAlreadyDefined = 222;
	int MemberIsNotAccessible = 223;
	int SymbolIsNotAccessible = 224;
	int ThisForSymbolNeedsToBeType = 225;
	int SymbolHasForwardReferences = 226;
	int CannotHaveAssociativeArrayOfKey = 227;
	int CannotHaveAssociativeArrayOf = 228;
	int CannotHaveArrayOfAuto = 229;
	int EnclosingLabelForBreakNotFound = 230;
	int EnclosingLabelForContinueNotFound = 231;
	int CannotAppendTypeToType = 232;
	int CannotAppendToStaticArray = 233;
	int ExpressionIsVoidAndHasNoValue = 234;
	int NeedMemberFunctionOpCmpForSymbolToCompare = 235;
	int CompareNotDefinedForComplexOperands = 236;
	int NeedThisForAddressOfSymbol = 237;
	int RecursiveMixinInstantiation = 238;
	int SymbolIsNotOfIntegralType = 239;
	int DeleteAAKeyDeprecated = 240;
	int SymbolIsDeprecated = 241;
	int ShadowingDeclarationIsDeprecated = 242;
	int ReturnStatementsCannotBeInFinallyScopeExitOrScopeSuccessBodies = 243;
	int CannotReturnExpressionFromConstructor = 244;
	int CaseNotFound = 245;
	int CircularInheritanceOfInterface = 246;
	int ArgumentToMixinMustBeString = 247;
	int CannotAccessFrameOfFunction = 248;
	int OperationNotAllowedOnBool = 249;
	int SymbolIsNotAScalar = 250;
	int ImportCannotBeResolved = 251;
	int SymbolIsNotAVariable = 252;
	int CatchHidesCatch = 253;
	int ArithmeticOrStringTypeExpectedForValueParameter = 254;
	int FunctionsCannotReturnAFunction = 255;
	int FunctionsCannotReturnATuple = 256;
	int FunctionsCannotReturnAuto = 257;
	int RecursiveType = 258;
	int VariadicFunctionsWithNonDLinkageMustHaveAtLeastOneParameter = 259;
	int SymbolMustBeAFunction = 260;
	int FunctionExpectedBeforeCall = 261;
	int FunctionExpectedBeforeCallNotSymbolOfType = 262;
	int CircularReferenceOfTypedef = 263;
	int StringSliceIsOutOfBounds = 264;
	int ErrorInstantiating = 265;
	int CaseMustBeAnIntegralOrStringConstant = 266;
	int DuplicateCaseInSwitchStatement = 267;
	int SpecialMemberFunctionsNotAllowedForSymbol = 268;
	int SpecialFunctionsNotAllowedInInterface = 269;
	int FunctionBodyIsNotAbstractInInterface = 270;
	int SuperClassConstructorCallMustBeInAConstructor = 271;
	int ClassConstructorCallMustBeInAConstructor = 272;
	int NoSuperClassConstructor = 273;
	int ConstructorCallsNotAllowedInLoopsOrAfterLabels = 274;
	int MultipleConstructorCalls = 275;
	int ExpressionIsNotConstantOrDoesNotEvaluateToABool = 276;
	int StaticIfConditionalCannotBeAtGlobalScope = 277;
	int CannotBreakOutOfFinallyBlock = 278;
	int LabelHasNoBreak = 279;
	int LabelHasNoContinue = 280;
	int CannotGotoInOrOutOfFinallyBlock = 281;
	int CalledWithArgumentTypesMatchesBoth = 282;
	int SymbolIsNotAnArithmeticType = 283;
	int SymbolIsNotAnArithmeticTypeItIs = 284;
	int CannotPerformModuloComplexArithmetic = 285;
	int OperatorNotAllowedOnBoolExpression = 286;
	int ForeachKeyTypeMustBeIntOrUint = 287;
	int ForeachKeyTypeMustBeIntOrUintLongOrUlong = 288;
	int ForeachKeyCannotBeOutOrRef = 289;
	int NoReverseIterationOnAssociativeArrays = 290;
	int OnlyOneOrTwoArgumentsForAssociativeArrayForeach = 291;
	int OnlyOneOrTwoArgumentsForArrayForeach = 292;
	int ForeachTargetIsNotAnArrayOf = 293;
	int ForeachKeyCannotBeInout = 294;
	int ForeachValueOfUTFConversionCannotBeInout = 295;
	int CannotInferTypeForSymbol = 296;
	int CannotInferTypeFromInitializer = 297;
	int NoStorageClassForSymbol = 298;
	int OnlyOneValueOrTwoKeyValueArgumentsForTupleForeach = 299;
	int CannotUniquelyInferForeachArgumentTypes = 300;
	int InvalidForeachAggregate = 301;
	int NotAnAssociativeArrayInitializer = 302;
	int ArrayInitializersAsExpressionsNotAllowed = 303;
	int IftypeConditionCannotBeAtGlobalScope = 304;
	int SymbolIsNotAFieldOfSymbol = 305;
	int RecursiveTemplateExpansion = 306;
	int RecursiveTemplateExpansionForTemplateArgument = 307;
	int IndexIsNotATypeOrExpression = 308;
	int CannotHavePointerToSymbol = 309;
	int SizeOfTypeIsNotKnown = 310;
	int CanOnlySliceTupleTypes = 311;
	int NoPropertyForTuple = 312;
	int CannotResolveDotProperty = 313;
	int CannotTakeAddressOfBitInArray = 314;
	int OnlyOneIndexAllowedToIndex = 315;
	int NoOpIndexOperatorOverloadForType = 316;
	int ArrayDimensionOverflow = 317;
	int OperatorAssignmentOverloadWithOpIndexIllegal = 318;
	int CannotHaveOutOrInoutArgumentOfBitInArray = 319;
	int SymbolIsAliasedToAFunction = 320;
	int LinkageDoesNotMatchInterfaceFunction = 321;
	int InterfaceFunctionIsNotImplemented = 322;
	int ExpectedKeyAsArgumentToRemove = 323;
	int CyclicConstructorCall = 324;
	int MissingOrCurruptObjectDotD = 325;
	int CannotContinueOutOfFinallyBlock = 326;
	int ForwardDeclaration = 327;
	int CannotFormDelegateDueToCovariantReturnType = 328;
	int ForeachRangeKeyCannotHaveStorageClass = 329;
	int MultipleOverridesOfSameFunction = 330;
	int IdentityAssignmentOperatorOverloadIsIllegal = 331;
	int LiteralsCannotBeClassMembers = 332;
	int NoMatchForImplicitSuperCallInConstructor = 333;
	int NoReturnAtEndOfFunction = 334;
	int CanOnlyDeclareTypeAliasesWithinStaticIfConditionals = 335;
	int PackageAndModuleHaveTheSameName = 336;
	int StringLiteralsAreImmutable = 337;
	int ExpressionDotNewIsOnlyForAllocatingNestedClasses = 338;
	int TooManyArgumentsForArray = 339;
	int ReturnExpressionExpected = 340;
	int ReturnWithoutCallingConstructor = 341;
	int ModuleIsInMultiplePackages = 342;
	int ModuleIsInMultipleDefined = 343;
	int NeedUpperAndLowerBoundToSlicePointer = 344;
	int NeedUpperAndLowerBoundToSliceTuple = 345;
	int CannotConvertStringLiteralToVoidPointer = 346;
	int SymbolIsNotAPreInstanceInitializableField = 347;
	int NoCaseStatementFollowingGoto = 348;
	int SwitchStatementHasNoDefault = 349;
	int SymbolIsNotAFunctionTemplate = 350;
	int TupleIsNotAValidTemplateValueArgument = 351;
	int IncompatibleArgumentsForTemplateInstantiation = 352;
	int ThrowStatementsCannotBeInContracts = 353;
	int CanOnlyThrowClassObjects = 354;
	int StringExpectedAsSecondArgument = 355;
	int WrongNumberOfArguments = 356;
	int StringMustBeChars = 357;
	int InvalidFirstArgument = 358;
	int FirstArgumentIsNotAClass = 359;
	int ArgumentHasNoMembers = 360;
	int SymbolHasNoMembers = 361;
	int KindSymbolHasNoMembers = 362;
	int DotOffsetDeprecated = 363;
	int NoClassInfoForComInterfaceObjects = 364;
	int CannotMakeReferenceToABit = 365;
	int CannotFormTupleOfTuples = 366;
	int MissingInitializerInStaticConstructorForConstVariable = 367;
	int GlobalsStaticsFieldsRefAndAutoParametersCannotBeAuto = 368;
	int GlobalsStaticsFieldsManifestConstantsRefAndAutoParametersCannotBeScope = 369;
	int ReferenceToScopeClassMustBeScope = 370;
	int NumberOfKeysMustMatchNumberOfValues = 371;
	int ExpectedNumberArguments = 372;
	int ArraySliceIfOutOfBounds = 373;
	int InvalidUCS32Char = 374;
	int TupleIndexExceedsBounds = 375;
	int SliceIsOutOfRange = 376;
	int CannotTakeAddressOf = 377;
	int VariableIsUsedBeforeInitialization = 378;
	int EscapingReferenceToLocal = 379;
	int EscapingReferenceToAutoLocal = 380;
	int EscapingReferenceToScopeLocal = 381;
	int EscapingReferenceToLocalVariable = 382;
	int EscapingReferenceToVariadicParameter = 383;
	int CanOnlyCatchClassObjects = 384;
	int BaseClassIsForwardReferenced = 385;
	int BaseIsForwardReferenced = 386;
	int CannotInheritFromFinalClass = 387;
	int StaticClassCannotInheritFromNestedClass = 388;
	int SuperClassIsNestedWithin = 389;
	int SuperClassIsNotNestedWithin = 390;
	int ArrayComparisonTypeMismatch = 391;
	int ConditionalExpressionIsNotAModifiableLvalue = 392;
	int CannotCastSymbolToSymbol = 393;
	int CannotDeleteInstanceOfComInterface = 394;
	int TemplateIsNotAMemberOf = 395;
	int TemplateIdentifierIsNotAMemberOf = 396;
	int TemplateIdentifierIsNotAMemberOfUndefined = 397;
	int CanOnlyInitiailizeConstMemberInsideConstructor = 398;
	int SymbolIsNotAMember = 399;
	int SymbolIsNotATemplate = 400;
	int DSymbolHasNoSize = 401;
	int ExpressionOfTypeDoesNotHaveABooleanValue = 402;
	int ImplicitConversionCanCauseLossOfData = 403;
	int ForwardReferenceToType = 404;
	int FloatingPointConstantExpressionExpected = 405;
	int ExpressionIsNotAValidTemplateValueArgument = 406;
	int InvalidRangeLowerBound = 407;
	int InvalidRangeUpperBound = 408;
	int SymbolIsNotAScalarType = 409;
	int ForeachIndexMustBeType = 410;
	int ForeachValueMustBeType = 411;
	int OpApplyFunctionMustReturnAnInt = 412;
	int FunctionOfTypeOverridesButIsNotCovariant = 413;
	int CannotOverrideFinalFunction = 414;
	int IncompatibleCovariantTypes = 415;
	int CannotUseTemplateToAddVirtualFunctionToClass = 416;
	int OutResultIsAlreadyDefined = 417;
	int MissingInitializerForConstField = 418;
	int MissingInitializerForFinalField = 419;
	int ImportNotFound = 420;
	int SymbolMustBeAnArrayOfPointerType = 421;
	int RvalueOfInExpressionMustBeAnAssociativeArray = 422;
	int InterfaceInheritsFromDuplicateInterface = 423;
	int LabelIsAlreadyDefined = 424;
	int CannotSubtractPointerFromSymbol = 425;
	int ThisForNestedClassMustBeAClassType = 426;
	int CanOnlyDereferenceAPointer = 427;
	int OuterClassThisNeededToNewNestedClass = 428;
	int ThisForNestedClassMustBeOfType = 429;
	int NoConstructorForSymbol = 430;
	int NoAllocatorForSymbol = 431;
	int NegativeArrayIndex = 432;
	int NewCanOnlyCreateStructsDynamicArraysAndClassObjects = 433;
	int MismatchedFunctionReturnTypeInference = 434;
	int ShiftLeftExceeds = 435;
	int SymbolCannotBeSlicedWithBrackets = 436;
	int SliceExpressionIsNotAModifiableLvalue = 437;
	int SymbolIsNotAMemberOf = 438;
	int MoreInitiailizersThanFields = 439;
	int OverlappingInitiailization = 440;
	int CannotMakeExpressionOutOfInitializer = 441;
	int NoDefaultOrCaseInSwitchStatement = 442;
	int SymbolIsNotASymbol = 443;
	int ForwardReferenceToTemplate = 444;
	int ForwardReferenceToTemplateDeclaration = 445;
	int SpecializationNotAllowedForDeducedParameter = 446;
	int CannotDeclareTemplateAtFunctionScope = 447;
	int TemplateHasNoValue = 448;
	int CannotUseLocalAsTemplateParameter = 449;
	int NoSizeForType = 450;
	int SymbolDotSymbolIsNotADeclaration = 451;
	int ThisIsRequiredButIsNotABaseClassOf = 452;
	int ForwardReferenceToSymbol = 453;
	int IdentifierOfSymbolIsNotDefined = 454;
	int StructIsForwardReferenced = 455;
	int CannotUseTemplateToAddFieldToAggregate = 456;
	int CannotModifyFinalVariable = 457;
	int InvalidUtf8Sequence2 = 458;
	int Utf16HighValuePastEndOfString = 459;
	int Utf16LowValueOutOfRange = 460;
	int UnpairedUtf16Value = 461;
	int IllegalUtf16Value = 462;
	int StaticConstructorCanOnlyBePartOfStructClassModule = 463;
	int ShiftAssignIsOutsideTheRange = 464;
	int TemplateTupleParameterMustBeLastOne = 465;
	int SymbolIsNestedInBoth = 466;
	int FunctionIsAbstract = 467;
	int KindSymbolDoesNotOverload = 468;
	int MismatchedTupleLengths = 469;
	int DoNotUseNullWhenComparingClassTypes = 470;
	int UseTokenInsteadOfTokenWhenComparingWithNull = 471;
	int VoidDoesNotHaveAnInitializer = 472;
	int FunctionNameExpectedForStartAddress = 473;
	int TypeofReturnMustBeInsideFunction = 474;
	int PostBlitsAreOnlyForStructUnionDefinitions = 475;
	int CannotHaveEDotTuple = 476;
	int CannotCreateCppClasses = 477;
	int SwitchAndCaseAreInDifferentFinallyBlocks = 478;
	int SwitchAndDefaultAreInDifferentFinallyBlocks = 479;
	int CannotHaveFieldWithSameStructType = 480;
	int WithoutThisCannotBeConstInvariant = 481;
	int CannotModifySymbol = 482;
	int CannotCallPublicExportFunctionFromImmutable = 483;
	int TemplateMemberFunctionNotAllowedInInterface = 484;
	int ArgumentToTypeofIsNotAnExpression = 485;
	int CannotInferTypeFromOverloadedFunctionSymbol = 486;
	int SymbolIsNotMutable = 487;
	int SymbolForSymbolNeedsToBeType = 488;
	int CannotModifyConstImmutable = 489;
	int SymbolCanOnlyBeCalledOnAnInvariantObject = 490;
	int SymbolCanOnlyBeCalledOnAMutableObject = 491;
	int CannotCallMutableMethodOnFinalStruct = 492;
	int ForwardReferenceOfImport = 493;
	int TemplateDoesNotHaveProperty = 494;
	int ExpressionDoesNotHaveProperty = 495;
	int RecursiveOpCmpExpansion = 496;
	int CtorIsReservedForConstructors = 497;
	int FunctionOverridesBaseClassFunctionButIsNotMarkedWithOverride = 498;
	int ForwardReferenceOfTypeDotMangleof = 499;
	int ArrayLengthHidesOtherLengthNameInOuterScope = 500;
	int OnePathSkipsConstructor = 501;
	int PragmaLibNotAllowedAsStatement = 502;
	int ThereCanBeOnlyOneAliasThis = 503;
	int AliasThisCanOnlyAppearInStructOrClassDeclaration = 504;
	int FunctionIsOverloaded = 505;
	int PureFunctionCannotCallImpure = 506;
	int PureFunctionCannotAccessMutableStaticData = 507;
	int PureNestedFunctionCannotAccessMutableData = 508;
	int ConstraintIsNotConstantOrDoesNotEvaluateToABool = 509;
	int EscapeStringLiteralDeprecated = 510;
	int DelimiterCannotBeWhitespace = 511;
	int ArrayEqualityComparisonTypeMismatch = 512;
	int GsharedNotAllowedInSafeMode = 513;
	int InnerStructCannotBeAField = 514;
	int OnlyParametersOfForeachDeclarationsCanBeRef = 515;
	int ManifestConstantsMustHaveInitializers = 516;
	int ForwardReferenceToInferredReturnTypeOfFunctionCall = 517;
	int CaseRangesNotAllowedInFinalSwitch = 518;
	int MoreThan256CasesInCaseRange = 519;
	int CannotCastTuple = 520;
	int CastNotAllowedInSafeMode = 521;
	int SliceIsNotMutable = 522;
	int SymbolIsNothrowYetMayThrow = 523;
	int DefaultConstructorNotAllowedForStructs = 524;
	int VoidDoesNotHaveADefaiñtInitializer = 525;
	int CannotImplicitlyConvertToImmutable = 526;

}
