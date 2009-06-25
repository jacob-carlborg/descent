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
	int SymbolConflictsWithSymbolAtLocation = 65;
	int SymbolAtLocationConflictsWithSymbolAtLocation = 66;
	int PropertyCanNotBeRedefined = 67;
	int CircularDefinition = 68;
	int EnumValueOverflow = 69;
	int EnumMustHaveAtLeastOneMember = 70;
	int EnumBaseTypeMustBeOfIntegralType = 71;
	int ForwardReferenceOfSymbol = 72;
	int ForwardReferenceOfEnumSymbolDotSymbol = 73;
	int ForwardReferenceWhenLookingFor = 74;
	int BaseEnumIsForwardReference = 75;
	int CannotResolveForwardReference = 76;
	int EnumIsForwardReference = 77;
	int IntegerConstantExpressionExpected = 78;
	int ThisNotInClassOrStruct = 79;
	int ThisOnlyAllowedInNonStaticMemberFunctions = 80;
	int SuperOnlyAllowedInNonStaticMemberFunctions = 81;
	int SuperNotInClass = 82;
	int ClassHasNoSuper = 83;
	int BaseTypeMustBeInterface = 84;
	int MemberIsPrivate = 85;
	int UsedAsAType = 86;
	int ExternSymbolsCannotHaveInitializers = 87;
	int VoidsHaveNoValue = 88;
	int CannotInferTypeFromThisArrayInitializer = 89;
	int NoDefinition = 90;
	int DuplicatedInterfaceInheritance = 91;
	int BaseTypeMustBeClassOrInterface = 92;
	int FieldsNotAllowedInInterfaces = 93;
	int UndefinedIdentifier = 94;
	int NotAMember = 95;
	int NewAllocatorsOnlyForClassOrStruct = 96;
	int DeleteDeallocatorsOnlyForClassOrStruct = 97;
	int ConstructorsOnlyForClass = 98;
	int DestructorsOnlyForClass = 99;
	int InvariantsOnlyForClassStructUnion = 100;
	int FunctionDoesNotOverrideAny = 101;
	int CannotOverrideFinalFunctions = 102;
	int OverrideOnlyForClassMemberFunctions = 103;
	int FunctionMustReturnAResultOfType = 104;
	int MoreThanOneInvariant = 105;
	int ParameterMultiplyDefined = 106;
	int SymbolNotFound = 107;
	int StatementIsNotReachable = 108;
	int VoidFunctionsHaveNoResult = 109;
	int ReturnStatementsCannotBeInContracts = 110;
	int NotAnAggregateType = 111;
	int UnrecognizedPragma = 112;
	int AnonCanOnlyBePartOfAnAggregate = 113;
	int PragmaIsMissingClosingSemicolon = 114;
	int CannotImplicitlyConvert = 115;
	int ForbiddenReference = 116;
	int DiscouragedReference = 117;
	int Task = 118;
	int UndefinedType = 119;
	int IsClassPathCorrect = 120;
	int FunctionsCannotBeConstOrAuto = 121;
	int FunctionsCannotBeScopeOrAuto = 122;
	int NonVirtualFunctionsCannotBeAbstract = 123;
	int CannotBeBothAbstractAndFinal = 124;
	int ModifierCannotBeAppliedToVariables = 125;
	int StructsCannotBeAbstract = 126;
	int UnionsCannotBeAbstract = 127;
	int AliasCannotBeConst = 128;
	int OneArgumentOfTypeExpected = 129;
	int IllegalMainParameters = 130;
	int MustReturnIntOrVoidFromMainFunction = 131;
	int AtLeastOneArgumentOfTypeExpected = 132;
	int FirstArgumentMustBeOfType = 133;
	int StringExpectedForPragmaMsg = 134;
	int LibPragmaMustRecieveASingleArgumentOfTypeString = 135;
	int StringExpectedForPragmaLib = 136;
	int CannotHaveOutOrInoutParameterOfTypeStaticArray = 137;
	int CannotHaveParameterOfTypeVoid = 138;
	int FunctionsCannotReturnStaticArrays = 139;
	int UnrecongnizedTrait = 140;
	int CanOnlyConcatenateArrays = 141;
	int ArrayIndexOutOfBounds = 142;
	int ArrayIndexOutOfBounds2 = 143;
	int AssertionFailed = 144;
	int AssertionFailedNoMessage = 145;
	int ExpressionIsNotEvaluatableAtCompileTime = 146;
	int UndefinedProperty = 147;
	int DeprecatedProperty = 148;
	int FileNameMustBeString = 149;
	int FileImportsMustBeSpecified = 150;
	int FileNotFound = 151;
	int ErrorReadingFile = 152;
	int ExpressionHasNoEffect = 153;
	int ConstantIsNotAnLValue = 154;
	int VersionIdentifierReserved = 155;
	int CannotPutCatchStatementInsideFinallyBlock = 156;
	int ExpressionDoesNotGiveABooleanResult = 157;
	int BreakIsNotInsideALoopOrSwitch = 158;
	int CaseIsNotInSwitch = 159;
	int VersionDeclarationMustBeAtModuleLevel = 160;
	int DebugDeclarationMustBeAtModuleLevel = 161;
	int GotoCaseNotInSwitch = 162;
	int GotoDefaultNotInSwitch = 163;
	int LazyVariablesCannotBeLvalues = 164;
	int DivisionByZero = 165;
	int DefaultNotInSwitch = 166;
	int SwitchAlreadyHasDefault = 167;
	int ContinueNotInLoop = 168;
	int ForeachIndexCannotBeRef = 169;
	int ParametersDoesNotMatchParameterTypes = 170;
	int IncompatibleParameterStorageClass = 171;
	int OutCannotBeConst = 172;
	int OutCannotBeInvariant = 173;
	int ScopeCannotBeRefOrOut = 174;
	int IncompatibleTypesForOperator = 175;
	int IncompatibleTypesForMinus = 176;
	int SymbolNotDefined = 177;
	int SymbolNotATemplate = 178;
	int CannotDeleteType = 179;
	int NotAnLvalue = 180;
	int CannotAliasAnExpression = 181;
	int CannotAssignToStaticArray = 182;
	int CannotChangeReferenceToStaticArray = 183;
	int CannotModifyParameterInContract = 184;
	int BothOverloadsMuchArgumentList = 185;
	int ExpressionHasNoType = 186;
	int SymbolNotAnExpression = 187;
	int SymbolHasNoValue = 188;
	int TooManyInitializers = 189;
	int SymbolNotAStaticAndCannotHaveStaticInitializer = 190;
	int SymbolNotAType = 191;
	int IncompleteMixinDeclaration = 192;
	int SymbolNotATemplateItIs = 193;
	int SymbolCannotBeDeclaredToBeAFunction = 194;
	int CannotHaveArrayOfType = 195;
	int SymbolDoesNotMatchAnyTemplateDeclaration = 196;
	int SymbolDoesNotMatchAnyFunctionTemplateDeclaration = 197;
	int IndexOverflowForStaticArray = 198;
	int UnknownSize = 199;
	int NoSizeYetForForwardReference = 200;
	int SymbolMatchesMoreThanOneTemplateDeclaration = 201;
	int ExpressionLeadsToStackOverflowAtCompileTime = 202;
	int StringIndexOutOfBounds = 203;
	int CannotCreateInstanceOfAbstractClass = 204;
	int CannotCreateInstanceOfInterface = 205;
	int WithExpressionsMustBeClassObject = 206;
	int DeclarationIsAlreadyDefined = 207;
	int DeclarationIsAlreadyDefinedInAnotherScope = 208;
	int VersionDefinedAfterUse = 209;
	int DebugDefinedAfterUse = 210;
	int NotEnoughArguments = 211;
	int CanOnlySynchronizeOnClassObjects = 212;
	int CannotDeduceTemplateFunctionFromArgumentTypes = 213;
	int CannotDeduceTemplateFunctionFromArgumentTypes2 = 214;
	int ArrayDimensionExceedsMax = 215;
	int AStructIsNotAValidInitializerFor = 216;
	int CannotUseArrayToInitialize = 217;
	int CircularReferenceTo = 218;
	int ParameterIsAlreadyDefined = 219;
	int MemberIsNotAccessible = 220;
	int SymbolIsNotAccessible = 221;
	int ThisForSymbolNeedsToBeType = 222;
	int SymbolHasForwardReferences = 223;
	int CannotHaveAssociativeArrayOfKey = 224;
	int CannotHaveAssociativeArrayOf = 225;
	int CannotHaveArrayOfAuto = 226;
	int EnclosingLabelForBreakNotFound = 227;
	int EnclosingLabelForContinueNotFound = 228;
	int CannotAppendTypeToType = 229;
	int CannotAppendToStaticArray = 230;
	int ExpressionIsVoidAndHasNoValue = 231;
	int NeedMemberFunctionOpCmpForSymbolToCompare = 232;
	int CompareNotDefinedForComplexOperands = 233;
	int NeedThisForAddressOfSymbol = 234;
	int RecursiveMixinInstantiation = 235;
	int SymbolIsNotOfIntegralType = 236;
	int DeleteAAKeyDeprecated = 237;
	int SymbolIsDeprecated = 238;
	int ShadowingDeclarationIsDeprecated = 239;
	int ReturnStatementsCannotBeInFinallyScopeExitOrScopeSuccessBodies = 240;
	int CannotReturnExpressionFromConstructor = 241;
	int CaseNotFound = 242;
	int CircularInheritanceOfInterface = 243;
	int ArgumentToMixinMustBeString = 244;
	int CannotAccessFrameOfFunction = 245;
	int OperationNotAllowedOnBool = 246;
	int SymbolIsNotAScalar = 247;
	int ImportCannotBeResolved = 248;
	int SymbolIsNotAVariable = 249;
	int CatchHidesCatch = 250;
	int ArithmeticOrStringTypeExpectedForValueParameter = 251;
	int FunctionsCannotReturnAFunction = 252;
	int FunctionsCannotReturnATuple = 253;
	int FunctionsCannotReturnAuto = 254;
	int RecursiveType = 255;
	int VariadicFunctionsWithNonDLinkageMustHaveAtLeastOneParameter = 256;
	int SymbolMustBeAFunction = 257;
	int FunctionExpectedBeforeCall = 258;
	int FunctionExpectedBeforeCallNotSymbolOfType = 259;
	int CircularReferenceOfTypedef = 260;
	int StringSliceIsOutOfBounds = 261;
	int ErrorInstantiating = 262;
	int CaseMustBeAnIntegralOrStringConstant = 263;
	int DuplicateCaseInSwitchStatement = 264;
	int SpecialMemberFunctionsNotAllowedForSymbol = 265;
	int SpecialFunctionsNotAllowedInInterface = 266;
	int FunctionBodyIsNotAbstractInInterface = 267;
	int SuperClassConstructorCallMustBeInAConstructor = 268;
	int ClassConstructorCallMustBeInAConstructor = 269;
	int NoSuperClassConstructor = 270;
	int ConstructorCallsNotAllowedInLoopsOrAfterLabels = 271;
	int MultipleConstructorCalls = 272;
	int ExpressionIsNotConstantOrDoesNotEvaluateToABool = 273;
	int StaticIfConditionalCannotBeAtGlobalScope = 274;
	int CannotBreakOutOfFinallyBlock = 275;
	int LabelHasNoBreak = 276;
	int LabelHasNoContinue = 277;
	int CannotGotoInOrOutOfFinallyBlock = 278;
	int CalledWithArgumentTypesMatchesBoth = 279;
	int SymbolIsNotAnArithmeticType = 280;
	int SymbolIsNotAnArithmeticTypeItIs = 281;
	int CannotPerformModuloComplexArithmetic = 282;
	int OperatorNotAllowedOnBoolExpression = 283;
	int ForeachKeyTypeMustBeIntOrUint = 284;
	int ForeachKeyTypeMustBeIntOrUintLongOrUlong = 285;
	int ForeachKeyCannotBeOutOrRef = 286;
	int NoReverseIterationOnAssociativeArrays = 287;
	int OnlyOneOrTwoArgumentsForAssociativeArrayForeach = 288;
	int OnlyOneOrTwoArgumentsForArrayForeach = 289;
	int ForeachTargetIsNotAnArrayOf = 290;
	int ForeachKeyCannotBeInout = 291;
	int ForeachValueOfUTFConversionCannotBeInout = 292;
	int CannotInferTypeForSymbol = 293;
	int CannotInferTypeFromInitializer = 294;
	int NoStorageClassForSymbol = 295;
	int OnlyOneValueOrTwoKeyValueArgumentsForTupleForeach = 296;
	int CannotUniquelyInferForeachArgumentTypes = 297;
	int InvalidForeachAggregate = 298;
	int NotAnAssociativeArrayInitializer = 299;
	int ArrayInitializersAsExpressionsNotAllowed = 300;
	int IftypeConditionCannotBeAtGlobalScope = 301;
	int SymbolIsNotAFieldOfSymbol = 302;
	int RecursiveTemplateExpansion = 303;
	int RecursiveTemplateExpansionForTemplateArgument = 304;
	int IndexIsNotATypeOrExpression = 305;
	int CannotHavePointerToSymbol = 306;
	int SizeOfTypeIsNotKnown = 307;
	int CanOnlySliceTupleTypes = 308;
	int NoPropertyForTuple = 309;
	int CannotResolveDotProperty = 310;
	int CannotTakeAddressOfBitInArray = 311;
	int OnlyOneIndexAllowedToIndex = 312;
	int NoOpIndexOperatorOverloadForType = 313;
	int ArrayDimensionOverflow = 314;
	int OperatorAssignmentOverloadWithOpIndexIllegal = 315;
	int CannotHaveOutOrInoutArgumentOfBitInArray = 316;
	int SymbolIsAliasedToAFunction = 317;
	int LinkageDoesNotMatchInterfaceFunction = 318;
	int InterfaceFunctionIsNotImplemented = 319;
	int ExpectedKeyAsArgumentToRemove = 320;
	int CyclicConstructorCall = 321;
	int MissingOrCurruptObjectDotD = 322;
	int CannotContinueOutOfFinallyBlock = 323;
	int ForwardDeclaration = 324;
	int CannotFormDelegateDueToCovariantReturnType = 325;
	int ForeachRangeKeyCannotHaveStorageClass = 326;
	int MultipleOverridesOfSameFunction = 327;
	int IdentityAssignmentOperatorOverloadIsIllegal = 328;
	int LiteralsCannotBeClassMembers = 329;
	int NoMatchForImplicitSuperCallInConstructor = 330;
	int NoReturnAtEndOfFunction = 331;
	int CanOnlyDeclareTypeAliasesWithinStaticIfConditionals = 332;
	int PackageAndModuleHaveTheSameName = 333;
	int StringLiteralsAreImmutable = 334;
	int ExpressionDotNewIsOnlyForAllocatingNestedClasses = 335;
	int TooManyArgumentsForArray = 336;
	int ReturnExpressionExpected = 337;
	int ReturnWithoutCallingConstructor = 338;
	int ModuleIsInMultiplePackages = 339;
	int ModuleIsInMultipleDefined = 340;
	int NeedUpperAndLowerBoundToSlicePointer = 341;
	int NeedUpperAndLowerBoundToSliceTuple = 342;
	int CannotConvertStringLiteralToVoidPointer = 343;
	int SymbolIsNotAPreInstanceInitializableField = 344;
	int NoCaseStatementFollowingGoto = 345;
	int SwitchStatementHasNoDefault = 346;
	int SymbolIsNotAFunctionTemplate = 347;
	int TupleIsNotAValidTemplateValueArgument = 348;
	int IncompatibleArgumentsForTemplateInstantiation = 349;
	int ThrowStatementsCannotBeInContracts = 350;
	int CanOnlyThrowClassObjects = 351;
	int StringExpectedAsSecondArgument = 352;
	int WrongNumberOfArguments = 353;
	int StringMustBeChars = 354;
	int InvalidFirstArgument = 355;
	int FirstArgumentIsNotAClass = 356;
	int ArgumentHasNoMembers = 357;
	int SymbolHasNoMembers = 358;
	int KindSymbolHasNoMembers = 359;
	int DotOffsetDeprecated = 360;
	int NoClassInfoForComInterfaceObjects = 361;
	int CannotMakeReferenceToABit = 362;
	int CannotFormTupleOfTuples = 363;
	int MissingInitializerInStaticConstructorForConstVariable = 364;
	int GlobalsStaticsFieldsRefAndAutoParametersCannotBeAuto = 365;
	int ReferenceToScopeClassMustBeScope = 366;
	int NumberOfKeysMustMatchNumberOfValues = 367;
	int ExpectedNumberArguments = 368;
	int ArraySliceIfOutOfBounds = 369;
	int InvalidUCS32Char = 370;
	int TupleIndexExceedsBounds = 371;
	int SliceIsOutOfRange = 372;
	int CannotTakeAddressOf = 373;
	int VariableIsUsedBeforeInitialization = 374;
	int EscapingReferenceToLocal = 375;
	int EscapingReferenceToAutoLocal = 376;
	int EscapingReferenceToLocalVariable = 377;
	int EscapingReferenceToVariadicParameter = 378;
	int CanOnlyCatchClassObjects = 379;
	int BaseClassIsForwardReferenced = 380;
	int BaseIsForwardReferenced = 381;
	int CannotInheritFromFinalClass = 382;
	int StaticClassCannotInheritFromNestedClass = 383;
	int SuperClassIsNestedWithin = 384;
	int SuperClassIsNotNestedWithin = 385;
	int ArrayComparisonTypeMismatch = 386;
	int ConditionalExpressionIsNotAModifiableLvalue = 387;
	int CannotCastSymbolToSymbol = 388;
	int CannotDeleteInstanceOfComInterface = 389;
	int TemplateIsNotAMemberOf = 390;
	int TemplateIdentifierIsNotAMemberOf = 391;
	int TemplateIdentifierIsNotAMemberOfUndefined = 392;
	int CanOnlyInitiailizeConstMemberInsideConstructor = 393;
	int SymbolIsNotAMember = 394;
	int SymbolIsNotATemplate = 395;
	int DSymbolHasNoSize = 396;
	int ExpressionOfTypeDoesNotHaveABooleanValue = 397;
	int ImplicitConversionCanCauseLossOfData = 398;
	int ForwardReferenceToType = 399;
	int FloatingPointConstantExpressionExpected = 400;
	int ExpressionIsNotAValidTemplateValueArgument = 401;
	int InvalidRangeLowerBound = 402;
	int InvalidRangeUpperBound = 403;
	int SymbolIsNotAScalarType = 404;
	int ForeachIndexMustBeType = 405;
	int ForeachValueMustBeType = 406;
	int OpApplyFunctionMustReturnAnInt = 407;
	int FunctionOfTypeOverridesButIsNotCovariant = 408;
	int CannotOverrideFinalFunction = 409;
	int IncompatibleCovariantTypes = 410;
	int CannotUseTemplateToAddVirtualFunctionToClass = 411;
	int OutResultIsAlreadyDefined = 412;
	int MissingInitializerForConstField = 413;
	int MissingInitializerForFinalField = 414;
	int ImportNotFound = 415;
	int SymbolMustBeAnArrayOfPointerType = 416;
	int RvalueOfInExpressionMustBeAnAssociativeArray = 417;
	int InterfaceInheritsFromDuplicateInterface = 418;
	int LabelIsAlreadyDefined = 419;
	int CannotSubtractPointerFromSymbol = 420;
	int ThisForNestedClassMustBeAClassType = 421;
	int CanOnlyDereferenceAPointer = 422;
	int OuterClassThisNeededToNewNestedClass = 423;
	int ThisForNestedClassMustBeOfType = 424;
	int NoConstructorForSymbol = 425;
	int NoAllocatorForSymbol = 426;
	int NegativeArrayIndex = 427;
	int NewCanOnlyCreateStructsDynamicArraysAndClassObjects = 428;
	int MismatchedFunctionReturnTypeInference = 429;
	int ShiftLeftExceeds = 430;
	int SymbolCannotBeSlicedWithBrackets = 431;
	int SliceExpressionIsNotAModifiableLvalue = 432;
	int SymbolIsNotAMemberOf = 433;
	int MoreInitiailizersThanFields = 434;
	int OverlappingInitiailization = 435;
	int CannotMakeExpressionOutOfInitializer = 436;
	int NoDefaultOrCaseInSwitchStatement = 437;
	int SymbolIsNotASymbol = 438;
	int ForwardReferenceToTemplate = 439;
	int ForwardReferenceToTemplateDeclaration = 440;
	int SpecializationNotAllowedForDeducedParameter = 441;
	int CannotDeclareTemplateAtFunctionScope = 442;
	int TemplateHasNoValue = 443;
	int CannotUseLocalAsTemplateParameter = 444;
	int NoSizeForType = 445;
	int SymbolDotSymbolIsNotADeclaration = 446;
	int ThisIsRequiredButIsNotABaseClassOf = 447;
	int ForwardReferenceToSymbol = 448;
	int IdentifierOfSymbolIsNotDefined = 449;
	int StructIsForwardReferenced = 450;
	int CannotUseTemplateToAddFieldToAggregate = 451;
	int CannotModifyFinalVariable = 452;
	int InvalidUtf8Sequence2 = 453;
	int Utf16HighValuePastEndOfString = 454;
	int Utf16LowValueOutOfRange = 455;
	int UnpairedUtf16Value = 456;
	int IllegalUtf16Value = 457;
	int StaticConstructorCanOnlyBePartOfStructClassModule = 458;
	int ShiftAssignIsOutsideTheRange = 459;
	int TemplateTupleParameterMustBeLastOne = 460;
	int SymbolIsNestedInBoth = 461;
	int FunctionIsAbstract = 462;
	int KindSymbolDoesNotOverload = 463;
	int MismatchedTupleLengths = 464;
	int DoNotUseNullWhenComparingClassTypes = 465;
	int UseTokenInsteadOfTokenWhenComparingWithNull = 466;
	int VoidDoesNotHaveAnInitializer = 467;
	int FunctionNameExpectedForStartAddress = 468;
	int TypeofReturnMustBeInsideFunction = 469;
	int PostBlitsAreOnlyForStructUnionDefinitions = 470;
	int CannotHaveEDotTuple = 471;
	int CannotCreateCppClasses = 472;
	int SwitchAndCaseAreInDifferentFinallyBlocks = 473;
	int SwitchAndDefaultAreInDifferentFinallyBlocks = 474;
	int CannotHaveFieldWithSameStructType = 475;
	int WithoutThisCannotBeConstInvariant = 476;
	int CannotModifySymbol = 477;
	int CannotCallPublicExportFunctionFromInvariant = 478;
	int TemplateMemberFunctionNotAllowedInInterface = 479;
	int ArgumentToTypeofIsNotAnExpression = 480;
	int CannotInferTypeFromOverloadedFunctionSymbol = 481;
	int SymbolIsNotMutable = 482;
	int SymbolForSymbolNeedsToBeType = 483;
	int CannotModifyConstInvariant = 484;
	int SymbolCanOnlyBeCalledOnAnInvariantObject = 485;
	int SymbolCanOnlyBeCalledOnAMutableObject = 486;
	int CannotCallMutableMethodOnFinalStruct = 487;
	int ForwardReferenceOfImport = 488;
	int TemplateDoesNotHaveProperty = 489;
	int ExpressionDoesNotHaveProperty = 490;
	int RecursiveOpCmpExpansion = 491;
	int CtorIsReservedForConstructors = 492;
	int FunctionOverridesBaseClassFunctionButIsNotMarkedWithOverride = 493;
	int ForwardReferenceOfTypeDotMangleof = 494;
	int ArrayLengthHidesOtherLengthNameInOuterScope = 495;
	int OnePathSkipsConstructor = 496;
	int PragmaLibNotAllowedAsStatement = 497;

}
