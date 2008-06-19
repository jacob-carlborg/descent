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
	   64 is the first semantic error in the list. */
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
	int SymbolConflictsWithSymbolAtLocation = 63;
	int SymbolAtLocationConflictsWithSymbolAtLocation = 64;
	int PropertyCanNotBeRedefined = 65;
	int CircularDefinition = 66;
	int EnumValueOverflow = 67;
	int EnumMustHaveAtLeastOneMember = 68;
	int EnumBaseTypeMustBeOfIntegralType = 69;
	int ForwardReferenceOfSymbol = 70;
	int ForwardReferenceOfSymbolDotSymbol = 71;
	int ForwardReferenceWhenLookingFor = 72;
	int BaseEnumIsForwardReference = 73;
	int CannotResolveForwardReference = 74;
	int EnumIsForwardReference = 75;
	int IntegerConstantExpressionExpected = 76;
	int ThisNotInClassOrStruct = 77;
	int ThisOnlyAllowedInNonStaticMemberFunctions = 78;
	int SuperOnlyAllowedInNonStaticMemberFunctions = 79;
	int SuperNotInClass = 80;
	int ClassHasNoSuper = 81;
	int BaseTypeMustBeInterface = 82;
	int MemberIsPrivate = 83;
	int UsedAsAType = 84;
	int ExternSymbolsCannotHaveInitializers = 85;
	int VoidsHaveNoValue = 86;
	int CannotInferTypeFromThisArrayInitializer = 87;
	int NoDefinition = 88;
	int DuplicatedInterfaceInheritance = 89;
	int BaseTypeMustBeClassOrInterface = 90;
	int FieldsNotAllowedInInterfaces = 91;
	int UndefinedIdentifier = 92;
	int NotAMember = 93;
	int NewAllocatorsOnlyForClassOrStruct = 94;
	int DeleteDeallocatorsOnlyForClassOrStruct = 95;
	int ConstructorsOnlyForClass = 96;
	int DestructorsOnlyForClass = 97;
	int InvariantsOnlyForClassStructUnion = 98;
	int FunctionDoesNotOverrideAny = 99;
	int CannotOverrideFinalFunctions = 100;
	int OverrideOnlyForClassMemberFunctions = 101;
	int FunctionMustReturnAResultOfType = 102;
	int MoreThanOneInvariant = 103;
	int ParameterMultiplyDefined = 104;
	int SymbolNotFound = 105;
	int StatementIsNotReachable = 106;
	int VoidFunctionsHaveNoResult = 107;
	int ReturnStatementsCannotBeInContracts = 108;
	int NotAnAggregateType = 109;
	int UnrecognizedPragma = 110;
	int AnonCanOnlyBePartOfAnAggregate = 111;
	int PragmaIsMissingClosingSemicolon = 112;
	int CannotImplicitlyConvert = 113;
	int ForbiddenReference = 114;
	int DiscouragedReference = 115;
	int Task = 116;
	int UndefinedType = 117;
	int IsClassPathCorrect = 118;
	int FunctionsCannotBeConstOrAuto = 119;
	int FunctionsCannotBeScopeOrAuto = 120;
	int NonVirtualFunctionsCannotBeAbstract = 121;
	int CannotBeBothAbstractAndFinal = 122;
	int ModifierCannotBeAppliedToVariables = 123;
	int StructsCannotBeAbstract = 124;
	int UnionsCannotBeAbstract = 125;
	int AliasCannotBeConst = 126;
	int OneArgumentOfTypeExpected = 127;
	int IllegalMainParameters = 128;
	int MustReturnIntOrVoidFromMainFunction = 129;
	int AtLeastOneArgumentOfTypeExpected = 130;
	int FirstArgumentMustBeOfType = 131;
	int StringExpectedForPragmaMsg = 132;
	int LibPragmaMustRecieveASingleArgumentOfTypeString = 133;
	int StringExpectedForPragmaLib = 134;
	int CannotHaveOutOrInoutParameterOfTypeStaticArray = 135;
	int CannotHaveParameterOfTypeVoid = 136;
	int FunctionsCannotReturnStaticArrays = 137;
	int UnrecongnizedTrait = 138;
	int CanOnlyConcatenateArrays = 139;
	int ArrayIndexOutOfBounds = 140;
	int ArrayIndexOutOfBounds2 = 141;
	int AssertionFailed = 142;
	int AssertionFailedNoMessage = 143;
	int ExpressionIsNotEvaluatableAtCompileTime = 144;
	int UndefinedProperty = 145;
	int DeprecatedProperty = 146;
	int FileNameMustBeString = 147;
	int FileImportsMustBeSpecified = 148;
	int FileNotFound = 149;
	int ErrorReadingFile = 150;
	int ExpressionHasNoEffect = 151;
	int ConstantIsNotAnLValue = 152;
	int VersionIdentifierReserved = 153;
	int CannotPutCatchStatementInsideFinallyBlock = 154;
	int ExpressionDoesNotGiveABooleanResult = 155;
	int BreakIsNotInsideALoopOrSwitch = 156;
	int CaseIsNotInSwitch = 157;
	int VersionDeclarationMustBeAtModuleLevel = 158;
	int DebugDeclarationMustBeAtModuleLevel = 159;
	int GotoCaseNotInSwitch = 160;
	int GotoDefaultNotInSwitch = 161;
	int LazyVariablesCannotBeLvalues = 162;
	int DivisionByZero = 163;
	int DefaultNotInSwitch = 164;
	int SwitchAlreadyHasDefault = 165;
	int ContinueNotInLoop = 166;
	int ForeachIndexCannotBeRef = 167;
	int ParametersDoesNotMatchParameterTypes = 168;
	int IncompatibleParameterStorageClass = 169;
	int OutCannotBeConst = 170;
	int OutCannotBeInvariant = 171;
	int ScopeCannotBeRefOrOut = 172;
	int IncompatibleTypesForOperator = 173;
	int IncompatibleTypesForMinus = 174;
	int SymbolNotDefined = 175;
	int SymbolNotATemplate = 176;
	int CannotDeleteType = 177;
	int NotAnLvalue = 178;
	int CannotAliasAnExpression = 179;
	int CannotAssignToStaticArray = 180;
	int CannotChangeReferenceToStaticArray = 181;
	int CannotModifyParameterInContract = 182;
	int BothOverloadsMuchArgumentList = 183;
	int ExpressionHasNoType = 184;
	int SymbolNotAnExpression = 185;
	int SymbolHasNoValue = 186;
	int TooManyInitializers = 187;
	int SymbolNotAStaticAndCannotHaveStaticInitializer = 188;
	int SymbolNotAType = 189;
	int IncompleteMixinDeclaration = 190;
	int SymbolNotATemplateItIs = 191;
	int SymbolCannotBeDeclaredToBeAFunction = 192;
	int CannotHaveArrayOfType = 193;
	int SymbolDoesNotMatchAnyTemplateDeclaration = 194;
	int IndexOverflowForStaticArray = 195;
	int UnknownSize = 196;
	int NoSizeYetForForwardReference = 197;
	int SymbolMatchesMoreThanOneTemplateDeclaration = 198;
	int ExpressionLeadsToStackOverflowAtCompileTime = 199;
	int StringIndexOutOfBounds = 200;
	int CannotCreateInstanceOfAbstractClass = 201;
	int CannotCreateInstanceOfInterface = 202;
	int WithExpressionsMustBeClassObject = 203;
	int DeclarationIsAlreadyDefined = 204;
	int DeclarationIsAlreadyDefinedInAnotherScope = 205;
	int VersionDefinedAfterUse = 206;
	int DebugDefinedAfterUse = 207;
	int NotEnoughArguments = 208;
	int CanOnlySynchronizeOnClassObjects = 209;
	int CannotDeduceTemplateFunctionFromArgumentTypes = 210;
	int CannotDeduceTemplateFunctionFromArgumentTypes2 = 211;
	int ArrayDimensionExceedsMax = 212;
	int AStructIsNotAValidInitializerFor = 213;
	int CannotUseArrayToInitialize = 214;
	int CircularReferenceTo = 215;
	int ParameterIsAlreadyDefined = 216;
	int MemberIsNotAccessible = 217;
	int SymbolIsNotAccessible = 218;
	int ThisForSymbolNeedsToBeType = 219;
	int SymbolHasForwardReferences = 220;
	int CannotHaveAssociativeArrayOfKey = 221;
	int CannotHaveAssociativeArrayOf = 222;
	int CannotHaveArrayOfAuto = 223;
	int EnclosingLabelForBreakNotFound = 224;
	int EnclosingLabelForContinueNotFound = 225;
	int CannotAppendTypeToType = 226;
	int CannotAppendToStaticArray = 227;
	int ExpressionIsVoidAndHasNoValue = 228;
	int NeedMemberFunctionOpCmpForSymbolToCompare = 229;
	int CompareNotDefinedForComplexOperands = 230;
	int NeedThisForAddressOfSymbol = 231;
	int RecursiveMixinInstantiation = 232;
	int SymbolIsNotOfIntegralType = 233;
	int DeleteAAKeyDeprecated = 234;
	int SymbolIsDeprecated = 235;
	int ShadowingDeclarationIsDeprecated = 236;
	int ReturnStatementsCannotBeInFinallyScopeExitOrScopeSuccessBodies = 237;
	int CannotReturnExpressionFromConstructor = 238;
	int CaseNotFound = 239;
	int CircularInheritanceOfInterface = 240;
	int ArgumentToMixinMustBeString = 241;
	int CannotAccessFrameOfFunction = 242;
	int OperationNotAllowedOnBool = 243;
	int SymbolIsNotAScalar = 244;
	int ImportCannotBeResolved = 245;
	int SymbolIsNotAVariable = 246;
	int CatchHidesCatch = 247;
	int ArithmeticOrStringTypeExpectedForValueParameter = 248;
	int FunctionsCannotReturnAFunction = 249;
	int FunctionsCannotReturnATuple = 250;
	int FunctionsCannotReturnAuto = 251;
	int RecursiveType = 252;
	int VariadicFunctionsWithNonDLinkageMustHaveAtLeastOneParameter = 253;
	int SymbolMustBeAFunction = 254;
	int FunctionExpectedBeforeCall = 255;
	int FunctionExpectedBeforeCallNotSymbolOfType = 256;
	int CircularReferenceOfTypedef = 257;
	int StringSliceIsOutOfBounds = 258;
	int ErrorInstantiating = 259;
	int CaseMustBeAnIntegralOrStringConstant = 260;
	int DuplicateCaseInSwitchStatement = 261;
	int SpecialMemberFunctionsNotAllowedForSymbol = 262;
	int SpecialFunctionsNotAllowedInInterface = 263;
	int FunctionBodyIsNotAbstractInInterface = 264;
	int SuperClassConstructorCallMustBeInAConstructor = 265;
	int ClassConstructorCallMustBeInAConstructor = 266;
	int NoSuperClassConstructor = 267;
	int ConstructorCallsNotAllowedInLoopsOrAfterLabels = 268;
	int MultipleConstructorCalls = 269;
	int ExpressionIsNotConstantOrDoesNotEvaluateToABool = 270;
	int StaticIfConditionalCannotBeAtGlobalScope = 271;
	int CannotBreakOutOfFinallyBlock = 272;
	int LabelHasNoBreak = 273;
	int LabelHasNoContinue = 274;
	int CannotGotoInOrOutOfFinallyBlock = 275;
	int CalledWithArgumentTypesMatchesBoth = 276;
	int SymbolIsNotAnArithmeticType = 277;
	int SymbolIsNotAnArithmeticTypeItIs = 278;
	int CannotPerformModuloComplexArithmetic = 279;
	int OperatorNotAllowedOnBoolExpression = 280;
	int ForeachKeyTypeMustBeIntOrUint = 281;
	int ForeachKeyCannotBeOutOrRef = 282;
	int NoReverseIterationOnAssociativeArrays = 283;
	int OnlyOneOrTwoArgumentsForAssociativeArrayForeach = 284;
	int OnlyOneOrTwoArgumentsForArrayForeach = 285;
	int ForeachTargetIsNotAnArrayOf = 286;
	int ForeachKeyCannotBeInout = 287;
	int ForeachValueOfUTFConversionCannotBeInout = 288;
	int CannotInferTypeForSymbol = 289;
	int CannotInferTypeFromInitializer = 290;
	int NoStorageClassForSymbol = 291;
	int OnlyOneValueOrTwoKeyValueArgumentsForTupleForeach = 292;
	int CannotUniquelyInferForeachArgumentTypes = 293;
	int InvalidForeachAggregate = 294;
	int NotAnAssociativeArrayInitializer = 295;
	int ArrayInitializersAsExpressionsNotAllowed = 296;
	int IftypeConditionCannotBeAtGlobalScope = 297;
	int SymbolIsNotAFieldOfSymbol = 298;
	int RecursiveTemplateExpansion = 299;
	int RecursiveTemplateExpansionForTemplateArgument = 300;
	int IndexIsNotATypeOrExpression = 301;
	int CannotHavePointerToSymbol = 302;
	int SizeOfTypeIsNotKnown = 303;
	int CanOnlySliceTupleTypes = 304;
	int NoPropertyForTuple = 305;
	int CannotResolveDotProperty = 306;
	int CannotTakeAddressOfBitInArray = 307;
	int OnlyOneIndexAllowedToIndex = 308;
	int NoOpIndexOperatorOverloadForType = 309;
	int ArrayDimensionOverflow = 310;
	int OperatorAssignmentOverloadWithOpIndexIllegal = 311;
	int CannotHaveOutOrInoutArgumentOfBitInArray = 312;
	int SymbolIsAliasedToAFunction = 313;
	int LinkageDoesNotMatchInterfaceFunction = 314;
	int InterfaceFunctionIsNotImplemented = 315;
	int ExpectedKeyAsArgumentToRemove = 316;
	int CyclicConstructorCall = 317;
	int MissingOrCurruptObjectDotD = 318;
	int CannotContinueOutOfFinallyBlock = 319;
	int ForwardDeclaration = 320;
	int CannotFormDelegateDueToCovariantReturnType = 321;
	int ForeachRangeKeyCannotHaveStorageClass = 322;
	int MultipleOverridesOfSameFunction = 323;
	int IdentityAssignmentOperatorOverloadIsIllegal = 324;
	int LiteralsCannotBeClassMembers = 325;
	int NoMatchForImplicitSuperCallInConstructor = 326;
	int NoReturnAtEndOfFunction = 327;
	int CanOnlyDeclareTypeAliasesWithinStaticIfConditionals = 328;
	int PackageAndModuleHaveTheSameName = 329;
	int StringLiteralsAreImmutable = 330;
	int ExpressionDotNewIsOnlyForAllocatingNestedClasses = 331;
	int TooManyArgumentsForArray = 332;
	int ReturnExpressionExpected = 333;
	int ReturnWithoutCallingConstructor = 334;
	int ModuleIsInMultiplePackages = 335;
	int ModuleIsInMultipleDefined = 336;
	int NeedUpperAndLowerBoundToSlicePointer = 337;
	int NeedUpperAndLowerBoundToSliceTuple = 338;
	int CannotConvertStringLiteralToVoidPointer = 339;
	int SymbolIsNotAPreInstanceInitializableField = 340;
	int NoCaseStatementFollowingGoto = 341;
	int SwitchStatementHasNoDefault = 342;
	int SymbolIsNotAFunctionTemplate = 343;
	int TupleIsNotAValidTemplateValueArgument = 344;
	int IncompatibleArgumentsForTemplateInstantiation = 345;
	int ThrowStatementsCannotBeInContracts = 346;
	int CanOnlyThrowClassObjects = 347;
	int StringExpectedAsSecondArgument = 348;
	int WrongNumberOfArguments = 349;
	int StringMustBeChars = 350;
	int InvalidFirstArgument = 351;
	int FirstArgumentIsNotAClass = 352;
	int ArgumentHasNoMembers = 353;
	int SymbolHasNoMembers = 354;
	int KindSymbolHasNoMembers = 355;
	int DotOffsetDeprecated = 356;
	int NoClassInfoForComInterfaceObjects = 357;
	int CannotMakeReferenceToABit = 358;
	int CannotFormTupleOfTuples = 359;
	int MissingInitializerInStaticConstructorForConstVariable = 360;
	int GlobalsStaticsFieldsRefAndAutoParametersCannotBeAuto = 361;
	int ReferenceToScopeClassMustBeScope = 362;
	int NumberOfKeysMustMatchNumberOfValues = 363;
	int ExpectedNumberArguments = 364;
	int ArraySliceIfOutOfBounds = 365;
	int InvalidUCS32Char = 366;
	int TupleIndexExceedsBounds = 367;
	int SliceIsOutOfRange = 368;
	int CannotTakeAddressOf = 369;
	int VariableIsUsedBeforeInitialization = 370;
	int EscapingReferenceToLocal = 371;
	int EscapingReferenceToAutoLocal = 372;
	int EscapingReferenceToLocalVariable = 373;
	int EscapingReferenceToVariadicParameter = 374;
	int CanOnlyCatchClassObjects = 375;
	int BaseClassIsForwardReferenced = 376;
	int BaseIsForwardReferenced = 377;
	int CannotInheritFromFinalClass = 378;
	int StaticClassCannotInheritFromNestedClass = 379;
	int SuperClassIsNestedWithin = 380;
	int ArrayComparisonTypeMismatch = 381;
	int ConditionalExpressionIsNotAModifiableLvalue = 382;
	int CannotCastSymbolToSymbol = 383;
	int CannotDeleteInstanceOfComInterface = 384;
	int TemplateIsNotAMemberOf = 385;
	int TemplateIdentifierIsNotAMemberOf = 386;
	int CanOnlyInitiailizeConstMemberInsideConstructor = 387;
	int SymbolIsNotAMember = 388;
	int SymbolIsNotATemplate = 389;
	int DSymbolHasNoSize = 390;
	int ExpressionOfTypeDoesNotHaveABooleanValue = 391;
	int ImplicitConversionCanCauseLossOfData = 392;
	int ForwardReferenceToType = 393;
	int FloatingPointConstantExpressionExpected = 394;
	int ExpressionIsNotAValidTemplateValueArgument = 395;
	int InvalidRangeLowerBound = 396;
	int InvalidRangeUpperBound = 397;
	int SymbolIsNotAScalarType = 398;
	int ForeachIndexMustBeType = 399;
	int ForeachValueMustBeType = 400;
	int OpApplyFunctionMustReturnAnInt = 401;
	int FunctionOfTypeOverridesButIsNotCovariant = 402;
	int CannotOverrideFinalFunction = 403;
	int IncompatibleCovariantTypes = 404;
	int CannotUseTemplateToAddVirtualFunctionToClass = 405;
	int OutResultIsAlreadyDefined = 406;
	int MissingInitializerForConstField = 407;
	int MissingInitializerForFinalField = 408;
	int ImportNotFound = 409;
	int SymbolMustBeAnArrayOfPointerType = 410;
	int RvalueOfInExpressionMustBeAnAssociativeArray = 411;
	int InterfaceInheritsFromDuplicateInterface = 412;
	int LabelIsAlreadyDefined = 413;
	int CannotSubtractPointerFromSymbol = 414;
	int ThisForNestedClassMustBeAClassType = 415;
	int CanOnlyDereferenceAPointer = 416;
	int OuterClassThisNeededToNewNestedClass = 417;
	int ThisForNestedClassMustBeOfType = 418;
	int NoConstructorForSymbol = 419;
	int NoAllocatorForSymbol = 420;
	int NegativeArrayIndex = 421;
	int NewCanOnlyCreateStructsDynamicArraysAndClassObjects = 422;
	int MismatchedFunctionReturnTypeInference = 423;
	int ShiftLeftExceeds = 424;
	int SymbolCannotBeSlicedWithBrackets = 425;
	int SliceExpressionIsNotAModifiableLvalue = 426;
	int SymbolIsNotAMemberOf = 427;
	int MoreInitiailizersThanFields = 428;
	int OverlappingInitiailization = 429;
	int CannotMakeExpressionOutOfInitializer = 430;
	int NoDefaultOrCaseInSwitchStatement = 431;
	int SymbolIsNotASymbol = 432;
	int ForwardReferenceToTemplate = 433;
	int ForwardReferenceToTemplateDeclaration = 434;
	int SpecializationNotAllowedForDeducedParameter = 435;
	int CannotDeclareTemplateAtFunctionScope = 436;
	int TemplateHasNoValue = 437;
	int CannotUseLocalAsTemplateParameter = 438;
	int NoSizeForType = 439;
	int SymbolDotSymbolIsNotADeclaration = 440;
	int ThisIsRequiredButIsNotABaseClassOf = 441;
	int ForwardReferenceToSymbol = 442;
	int IdentifierOfSymbolIsNotDefined = 443;
	int StructIsForwardReferenced = 444;
	int CannotUseTemplateToAddFieldToAggregate = 445;
	int CannotModifyFinalVariable = 446;
	int InvalidUtf8Sequence2 = 447;
	int Utf16HighValuePastEndOfString = 448;
	int Utf16LowValueOutOfRange = 449;
	int UnpairedUtf16Value = 450;
	int IllegalUtf16Value = 451;
	int StaticConstructorCanOnlyBePartOfStructClassModule = 452;
	int ShiftAssignIsOutsideTheRange = 453;
	int TemplateTupleParameterMustBeLastOne = 454;
	int SymbolIsNestedInBoth = 455;
	int FunctionIsAbstract = 456;
	int KindSymbolDoesNotOverload = 457;
	int MismatchedTupleLengths = 458;
	int DoNotUseNullWhenComparingClassTypes = 459;
	int UseTokenInsteadOfTokenWhenComparingWithNull = 460;
	int VoidDoesNotHaveAnInitializer = 461;
	int FunctionNameExpectedForStartAddress = 462;
	int TypeofReturnMustBeInsideFunction = 463;
	int PostBlitsAreOnlyForStructUnionDefinitions = 464;
	int CannotHaveEDotTuple = 465;
	int CannotCreateCppClasses = 466;
	int SwitchAndCaseAreInDifferentFinallyBlocks = 467;
	int SwitchAndDefaultAreInDifferentFinallyBlocks = 468;
	int CannotHaveFieldWithSameStructType = 469;
	int WithoutThisCannotBeConstInvariant = 470;
	int CannotModifySymbol = 471;

}
