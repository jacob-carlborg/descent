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
	int SymbolDoesNotMatchTemplateDeclaration = 197;
	int SymbolDoesNotMatchAnyFunctionTemplateDeclaration = 198;
	int IndexOverflowForStaticArray = 199;
	int UnknownSize = 200;
	int NoSizeYetForForwardReference = 201;
	int SymbolMatchesMoreThanOneTemplateDeclaration = 202;
	int ExpressionLeadsToStackOverflowAtCompileTime = 203;
	int StringIndexOutOfBounds = 204;
	int CannotCreateInstanceOfAbstractClass = 205;
	int CannotCreateInstanceOfInterface = 206;
	int WithExpressionsMustBeClassObject = 207;
	int DeclarationIsAlreadyDefined = 208;
	int DeclarationIsAlreadyDefinedInAnotherScope = 209;
	int VersionDefinedAfterUse = 210;
	int DebugDefinedAfterUse = 211;
	int NotEnoughArguments = 212;
	int CanOnlySynchronizeOnClassObjects = 213;
	int CannotDeduceTemplateFunctionFromArgumentTypes = 214;
	int CannotDeduceTemplateFunctionFromArgumentTypes2 = 215;
	int ArrayDimensionExceedsMax = 216;
	int AStructIsNotAValidInitializerFor = 217;
	int CannotUseArrayToInitialize = 218;
	int CircularReferenceTo = 219;
	int ParameterIsAlreadyDefined = 220;
	int MemberIsNotAccessible = 221;
	int SymbolIsNotAccessible = 222;
	int ThisForSymbolNeedsToBeType = 223;
	int SymbolHasForwardReferences = 224;
	int CannotHaveAssociativeArrayOfKey = 225;
	int CannotHaveAssociativeArrayOf = 226;
	int CannotHaveArrayOfAuto = 227;
	int EnclosingLabelForBreakNotFound = 228;
	int EnclosingLabelForContinueNotFound = 229;
	int CannotAppendTypeToType = 230;
	int CannotAppendToStaticArray = 231;
	int ExpressionIsVoidAndHasNoValue = 232;
	int NeedMemberFunctionOpCmpForSymbolToCompare = 233;
	int CompareNotDefinedForComplexOperands = 234;
	int NeedThisForAddressOfSymbol = 235;
	int RecursiveMixinInstantiation = 236;
	int SymbolIsNotOfIntegralType = 237;
	int DeleteAAKeyDeprecated = 238;
	int SymbolIsDeprecated = 239;
	int ShadowingDeclarationIsDeprecated = 240;
	int ReturnStatementsCannotBeInFinallyScopeExitOrScopeSuccessBodies = 241;
	int CannotReturnExpressionFromConstructor = 242;
	int CaseNotFound = 243;
	int CircularInheritanceOfInterface = 244;
	int ArgumentToMixinMustBeString = 245;
	int CannotAccessFrameOfFunction = 246;
	int OperationNotAllowedOnBool = 247;
	int SymbolIsNotAScalar = 248;
	int ImportCannotBeResolved = 249;
	int SymbolIsNotAVariable = 250;
	int CatchHidesCatch = 251;
	int ArithmeticOrStringTypeExpectedForValueParameter = 252;
	int FunctionsCannotReturnAFunction = 253;
	int FunctionsCannotReturnATuple = 254;
	int FunctionsCannotReturnAuto = 255;
	int RecursiveType = 256;
	int VariadicFunctionsWithNonDLinkageMustHaveAtLeastOneParameter = 257;
	int SymbolMustBeAFunction = 258;
	int FunctionExpectedBeforeCall = 259;
	int FunctionExpectedBeforeCallNotSymbolOfType = 260;
	int CircularReferenceOfTypedef = 261;
	int StringSliceIsOutOfBounds = 262;
	int ErrorInstantiating = 263;
	int CaseMustBeAnIntegralOrStringConstant = 264;
	int DuplicateCaseInSwitchStatement = 265;
	int SpecialMemberFunctionsNotAllowedForSymbol = 266;
	int SpecialFunctionsNotAllowedInInterface = 267;
	int FunctionBodyIsNotAbstractInInterface = 268;
	int SuperClassConstructorCallMustBeInAConstructor = 269;
	int ClassConstructorCallMustBeInAConstructor = 270;
	int NoSuperClassConstructor = 271;
	int ConstructorCallsNotAllowedInLoopsOrAfterLabels = 272;
	int MultipleConstructorCalls = 273;
	int ExpressionIsNotConstantOrDoesNotEvaluateToABool = 274;
	int StaticIfConditionalCannotBeAtGlobalScope = 275;
	int CannotBreakOutOfFinallyBlock = 276;
	int LabelHasNoBreak = 277;
	int LabelHasNoContinue = 278;
	int CannotGotoInOrOutOfFinallyBlock = 279;
	int CalledWithArgumentTypesMatchesBoth = 280;
	int SymbolIsNotAnArithmeticType = 281;
	int SymbolIsNotAnArithmeticTypeItIs = 282;
	int CannotPerformModuloComplexArithmetic = 283;
	int OperatorNotAllowedOnBoolExpression = 284;
	int ForeachKeyTypeMustBeIntOrUint = 285;
	int ForeachKeyTypeMustBeIntOrUintLongOrUlong = 286;
	int ForeachKeyCannotBeOutOrRef = 287;
	int NoReverseIterationOnAssociativeArrays = 288;
	int OnlyOneOrTwoArgumentsForAssociativeArrayForeach = 289;
	int OnlyOneOrTwoArgumentsForArrayForeach = 290;
	int ForeachTargetIsNotAnArrayOf = 291;
	int ForeachKeyCannotBeInout = 292;
	int ForeachValueOfUTFConversionCannotBeInout = 293;
	int CannotInferTypeForSymbol = 294;
	int CannotInferTypeFromInitializer = 295;
	int NoStorageClassForSymbol = 296;
	int OnlyOneValueOrTwoKeyValueArgumentsForTupleForeach = 297;
	int CannotUniquelyInferForeachArgumentTypes = 298;
	int InvalidForeachAggregate = 299;
	int NotAnAssociativeArrayInitializer = 300;
	int ArrayInitializersAsExpressionsNotAllowed = 301;
	int IftypeConditionCannotBeAtGlobalScope = 302;
	int SymbolIsNotAFieldOfSymbol = 303;
	int RecursiveTemplateExpansion = 304;
	int RecursiveTemplateExpansionForTemplateArgument = 305;
	int IndexIsNotATypeOrExpression = 306;
	int CannotHavePointerToSymbol = 307;
	int SizeOfTypeIsNotKnown = 308;
	int CanOnlySliceTupleTypes = 309;
	int NoPropertyForTuple = 310;
	int CannotResolveDotProperty = 311;
	int CannotTakeAddressOfBitInArray = 312;
	int OnlyOneIndexAllowedToIndex = 313;
	int NoOpIndexOperatorOverloadForType = 314;
	int ArrayDimensionOverflow = 315;
	int OperatorAssignmentOverloadWithOpIndexIllegal = 316;
	int CannotHaveOutOrInoutArgumentOfBitInArray = 317;
	int SymbolIsAliasedToAFunction = 318;
	int LinkageDoesNotMatchInterfaceFunction = 319;
	int InterfaceFunctionIsNotImplemented = 320;
	int ExpectedKeyAsArgumentToRemove = 321;
	int CyclicConstructorCall = 322;
	int MissingOrCurruptObjectDotD = 323;
	int CannotContinueOutOfFinallyBlock = 324;
	int ForwardDeclaration = 325;
	int CannotFormDelegateDueToCovariantReturnType = 326;
	int ForeachRangeKeyCannotHaveStorageClass = 327;
	int MultipleOverridesOfSameFunction = 328;
	int IdentityAssignmentOperatorOverloadIsIllegal = 329;
	int LiteralsCannotBeClassMembers = 330;
	int NoMatchForImplicitSuperCallInConstructor = 331;
	int NoReturnAtEndOfFunction = 332;
	int CanOnlyDeclareTypeAliasesWithinStaticIfConditionals = 333;
	int PackageAndModuleHaveTheSameName = 334;
	int StringLiteralsAreImmutable = 335;
	int ExpressionDotNewIsOnlyForAllocatingNestedClasses = 336;
	int TooManyArgumentsForArray = 337;
	int ReturnExpressionExpected = 338;
	int ReturnWithoutCallingConstructor = 339;
	int ModuleIsInMultiplePackages = 340;
	int ModuleIsInMultipleDefined = 341;
	int NeedUpperAndLowerBoundToSlicePointer = 342;
	int NeedUpperAndLowerBoundToSliceTuple = 343;
	int CannotConvertStringLiteralToVoidPointer = 344;
	int SymbolIsNotAPreInstanceInitializableField = 345;
	int NoCaseStatementFollowingGoto = 346;
	int SwitchStatementHasNoDefault = 347;
	int SymbolIsNotAFunctionTemplate = 348;
	int TupleIsNotAValidTemplateValueArgument = 349;
	int IncompatibleArgumentsForTemplateInstantiation = 350;
	int ThrowStatementsCannotBeInContracts = 351;
	int CanOnlyThrowClassObjects = 352;
	int StringExpectedAsSecondArgument = 353;
	int WrongNumberOfArguments = 354;
	int StringMustBeChars = 355;
	int InvalidFirstArgument = 356;
	int FirstArgumentIsNotAClass = 357;
	int ArgumentHasNoMembers = 358;
	int SymbolHasNoMembers = 359;
	int KindSymbolHasNoMembers = 360;
	int DotOffsetDeprecated = 361;
	int NoClassInfoForComInterfaceObjects = 362;
	int CannotMakeReferenceToABit = 363;
	int CannotFormTupleOfTuples = 364;
	int MissingInitializerInStaticConstructorForConstVariable = 365;
	int GlobalsStaticsFieldsRefAndAutoParametersCannotBeAuto = 366;
	int GlobalsStaticsFieldsManifestConstantsRefAndAutoParametersCannotBeScope = 367;
	int ReferenceToScopeClassMustBeScope = 368;
	int NumberOfKeysMustMatchNumberOfValues = 369;
	int ExpectedNumberArguments = 370;
	int ArraySliceIfOutOfBounds = 371;
	int InvalidUCS32Char = 372;
	int TupleIndexExceedsBounds = 373;
	int SliceIsOutOfRange = 374;
	int CannotTakeAddressOf = 375;
	int VariableIsUsedBeforeInitialization = 376;
	int EscapingReferenceToLocal = 377;
	int EscapingReferenceToAutoLocal = 378;
	int EscapingReferenceToLocalVariable = 379;
	int EscapingReferenceToVariadicParameter = 380;
	int CanOnlyCatchClassObjects = 381;
	int BaseClassIsForwardReferenced = 382;
	int BaseIsForwardReferenced = 383;
	int CannotInheritFromFinalClass = 384;
	int StaticClassCannotInheritFromNestedClass = 385;
	int SuperClassIsNestedWithin = 386;
	int SuperClassIsNotNestedWithin = 387;
	int ArrayComparisonTypeMismatch = 388;
	int ConditionalExpressionIsNotAModifiableLvalue = 389;
	int CannotCastSymbolToSymbol = 390;
	int CannotDeleteInstanceOfComInterface = 391;
	int TemplateIsNotAMemberOf = 392;
	int TemplateIdentifierIsNotAMemberOf = 393;
	int TemplateIdentifierIsNotAMemberOfUndefined = 394;
	int CanOnlyInitiailizeConstMemberInsideConstructor = 395;
	int SymbolIsNotAMember = 396;
	int SymbolIsNotATemplate = 397;
	int DSymbolHasNoSize = 398;
	int ExpressionOfTypeDoesNotHaveABooleanValue = 399;
	int ImplicitConversionCanCauseLossOfData = 400;
	int ForwardReferenceToType = 401;
	int FloatingPointConstantExpressionExpected = 402;
	int ExpressionIsNotAValidTemplateValueArgument = 403;
	int InvalidRangeLowerBound = 404;
	int InvalidRangeUpperBound = 405;
	int SymbolIsNotAScalarType = 406;
	int ForeachIndexMustBeType = 407;
	int ForeachValueMustBeType = 408;
	int OpApplyFunctionMustReturnAnInt = 409;
	int FunctionOfTypeOverridesButIsNotCovariant = 410;
	int CannotOverrideFinalFunction = 411;
	int IncompatibleCovariantTypes = 412;
	int CannotUseTemplateToAddVirtualFunctionToClass = 413;
	int OutResultIsAlreadyDefined = 414;
	int MissingInitializerForConstField = 415;
	int MissingInitializerForFinalField = 416;
	int ImportNotFound = 417;
	int SymbolMustBeAnArrayOfPointerType = 418;
	int RvalueOfInExpressionMustBeAnAssociativeArray = 419;
	int InterfaceInheritsFromDuplicateInterface = 420;
	int LabelIsAlreadyDefined = 421;
	int CannotSubtractPointerFromSymbol = 422;
	int ThisForNestedClassMustBeAClassType = 423;
	int CanOnlyDereferenceAPointer = 424;
	int OuterClassThisNeededToNewNestedClass = 425;
	int ThisForNestedClassMustBeOfType = 426;
	int NoConstructorForSymbol = 427;
	int NoAllocatorForSymbol = 428;
	int NegativeArrayIndex = 429;
	int NewCanOnlyCreateStructsDynamicArraysAndClassObjects = 430;
	int MismatchedFunctionReturnTypeInference = 431;
	int ShiftLeftExceeds = 432;
	int SymbolCannotBeSlicedWithBrackets = 433;
	int SliceExpressionIsNotAModifiableLvalue = 434;
	int SymbolIsNotAMemberOf = 435;
	int MoreInitiailizersThanFields = 436;
	int OverlappingInitiailization = 437;
	int CannotMakeExpressionOutOfInitializer = 438;
	int NoDefaultOrCaseInSwitchStatement = 439;
	int SymbolIsNotASymbol = 440;
	int ForwardReferenceToTemplate = 441;
	int ForwardReferenceToTemplateDeclaration = 442;
	int SpecializationNotAllowedForDeducedParameter = 443;
	int CannotDeclareTemplateAtFunctionScope = 444;
	int TemplateHasNoValue = 445;
	int CannotUseLocalAsTemplateParameter = 446;
	int NoSizeForType = 447;
	int SymbolDotSymbolIsNotADeclaration = 448;
	int ThisIsRequiredButIsNotABaseClassOf = 449;
	int ForwardReferenceToSymbol = 450;
	int IdentifierOfSymbolIsNotDefined = 451;
	int StructIsForwardReferenced = 452;
	int CannotUseTemplateToAddFieldToAggregate = 453;
	int CannotModifyFinalVariable = 454;
	int InvalidUtf8Sequence2 = 455;
	int Utf16HighValuePastEndOfString = 456;
	int Utf16LowValueOutOfRange = 457;
	int UnpairedUtf16Value = 458;
	int IllegalUtf16Value = 459;
	int StaticConstructorCanOnlyBePartOfStructClassModule = 460;
	int ShiftAssignIsOutsideTheRange = 461;
	int TemplateTupleParameterMustBeLastOne = 462;
	int SymbolIsNestedInBoth = 463;
	int FunctionIsAbstract = 464;
	int KindSymbolDoesNotOverload = 465;
	int MismatchedTupleLengths = 466;
	int DoNotUseNullWhenComparingClassTypes = 467;
	int UseTokenInsteadOfTokenWhenComparingWithNull = 468;
	int VoidDoesNotHaveAnInitializer = 469;
	int FunctionNameExpectedForStartAddress = 470;
	int TypeofReturnMustBeInsideFunction = 471;
	int PostBlitsAreOnlyForStructUnionDefinitions = 472;
	int CannotHaveEDotTuple = 473;
	int CannotCreateCppClasses = 474;
	int SwitchAndCaseAreInDifferentFinallyBlocks = 475;
	int SwitchAndDefaultAreInDifferentFinallyBlocks = 476;
	int CannotHaveFieldWithSameStructType = 477;
	int WithoutThisCannotBeConstInvariant = 478;
	int CannotModifySymbol = 479;
	int CannotCallPublicExportFunctionFromInvariant = 480;
	int TemplateMemberFunctionNotAllowedInInterface = 481;
	int ArgumentToTypeofIsNotAnExpression = 482;
	int CannotInferTypeFromOverloadedFunctionSymbol = 483;
	int SymbolIsNotMutable = 484;
	int SymbolForSymbolNeedsToBeType = 485;
	int CannotModifyConstInvariant = 486;
	int SymbolCanOnlyBeCalledOnAnInvariantObject = 487;
	int SymbolCanOnlyBeCalledOnAMutableObject = 488;
	int CannotCallMutableMethodOnFinalStruct = 489;
	int ForwardReferenceOfImport = 490;
	int TemplateDoesNotHaveProperty = 491;
	int ExpressionDoesNotHaveProperty = 492;
	int RecursiveOpCmpExpansion = 493;
	int CtorIsReservedForConstructors = 494;
	int FunctionOverridesBaseClassFunctionButIsNotMarkedWithOverride = 495;
	int ForwardReferenceOfTypeDotMangleof = 496;
	int ArrayLengthHidesOtherLengthNameInOuterScope = 497;
	int OnePathSkipsConstructor = 498;
	int PragmaLibNotAllowedAsStatement = 499;
	int ThereCanBeOnlyOneAliasThis = 500;
	int AliasThisCanOnlyAppearInStructOrClassDeclaration = 501;
	int FunctionIsOverloaded = 502;
	int PureFunctionCannotCallImpureFunction = 503;
	int PureFunctionCannotAccessMutableStaticData = 504;
	int ConstraintIsNotConstantOrDoesNotEvaluateToABool = 505;
	int EscapeStringLiteralDeprecated = 506;
	int DelimiterCannotBeWhitespace = 507;
	int ArrayEqualityComparisonTypeMismatch = 508;
	int GsharedNotAllowedInSafeMode = 509;
	int InnerStructCannotBeAField = 510;
	int OnlyParametersOfForeachDeclarationsCanBeRef = 511;
	int ManifestConstantsMustHaveInitializers = 512;

}
