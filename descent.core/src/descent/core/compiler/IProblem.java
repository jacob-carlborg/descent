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
	int IndexOverflowForStaticArray = 197;
	int UnknownSize = 198;
	int NoSizeYetForForwardReference = 199;
	int SymbolMatchesMoreThanOneTemplateDeclaration = 200;
	int ExpressionLeadsToStackOverflowAtCompileTime = 201;
	int StringIndexOutOfBounds = 202;
	int CannotCreateInstanceOfAbstractClass = 203;
	int CannotCreateInstanceOfInterface = 204;
	int WithExpressionsMustBeClassObject = 205;
	int DeclarationIsAlreadyDefined = 206;
	int DeclarationIsAlreadyDefinedInAnotherScope = 207;
	int VersionDefinedAfterUse = 208;
	int DebugDefinedAfterUse = 209;
	int NotEnoughArguments = 210;
	int CanOnlySynchronizeOnClassObjects = 211;
	int CannotDeduceTemplateFunctionFromArgumentTypes = 212;
	int CannotDeduceTemplateFunctionFromArgumentTypes2 = 213;
	int ArrayDimensionExceedsMax = 214;
	int AStructIsNotAValidInitializerFor = 215;
	int CannotUseArrayToInitialize = 216;
	int CircularReferenceTo = 217;
	int ParameterIsAlreadyDefined = 218;
	int MemberIsNotAccessible = 219;
	int SymbolIsNotAccessible = 220;
	int ThisForSymbolNeedsToBeType = 221;
	int SymbolHasForwardReferences = 222;
	int CannotHaveAssociativeArrayOfKey = 223;
	int CannotHaveAssociativeArrayOf = 224;
	int CannotHaveArrayOfAuto = 225;
	int EnclosingLabelForBreakNotFound = 226;
	int EnclosingLabelForContinueNotFound = 227;
	int CannotAppendTypeToType = 228;
	int CannotAppendToStaticArray = 229;
	int ExpressionIsVoidAndHasNoValue = 230;
	int NeedMemberFunctionOpCmpForSymbolToCompare = 231;
	int CompareNotDefinedForComplexOperands = 232;
	int NeedThisForAddressOfSymbol = 233;
	int RecursiveMixinInstantiation = 234;
	int SymbolIsNotOfIntegralType = 235;
	int DeleteAAKeyDeprecated = 236;
	int SymbolIsDeprecated = 237;
	int ShadowingDeclarationIsDeprecated = 238;
	int ReturnStatementsCannotBeInFinallyScopeExitOrScopeSuccessBodies = 239;
	int CannotReturnExpressionFromConstructor = 240;
	int CaseNotFound = 241;
	int CircularInheritanceOfInterface = 242;
	int ArgumentToMixinMustBeString = 243;
	int CannotAccessFrameOfFunction = 244;
	int OperationNotAllowedOnBool = 245;
	int SymbolIsNotAScalar = 246;
	int ImportCannotBeResolved = 247;
	int SymbolIsNotAVariable = 248;
	int CatchHidesCatch = 249;
	int ArithmeticOrStringTypeExpectedForValueParameter = 250;
	int FunctionsCannotReturnAFunction = 251;
	int FunctionsCannotReturnATuple = 252;
	int FunctionsCannotReturnAuto = 253;
	int RecursiveType = 254;
	int VariadicFunctionsWithNonDLinkageMustHaveAtLeastOneParameter = 255;
	int SymbolMustBeAFunction = 256;
	int FunctionExpectedBeforeCall = 257;
	int FunctionExpectedBeforeCallNotSymbolOfType = 258;
	int CircularReferenceOfTypedef = 259;
	int StringSliceIsOutOfBounds = 260;
	int ErrorInstantiating = 261;
	int CaseMustBeAnIntegralOrStringConstant = 262;
	int DuplicateCaseInSwitchStatement = 263;
	int SpecialMemberFunctionsNotAllowedForSymbol = 264;
	int SpecialFunctionsNotAllowedInInterface = 265;
	int FunctionBodyIsNotAbstractInInterface = 266;
	int SuperClassConstructorCallMustBeInAConstructor = 267;
	int ClassConstructorCallMustBeInAConstructor = 268;
	int NoSuperClassConstructor = 269;
	int ConstructorCallsNotAllowedInLoopsOrAfterLabels = 270;
	int MultipleConstructorCalls = 271;
	int ExpressionIsNotConstantOrDoesNotEvaluateToABool = 272;
	int StaticIfConditionalCannotBeAtGlobalScope = 273;
	int CannotBreakOutOfFinallyBlock = 274;
	int LabelHasNoBreak = 275;
	int LabelHasNoContinue = 276;
	int CannotGotoInOrOutOfFinallyBlock = 277;
	int CalledWithArgumentTypesMatchesBoth = 278;
	int SymbolIsNotAnArithmeticType = 279;
	int SymbolIsNotAnArithmeticTypeItIs = 280;
	int CannotPerformModuloComplexArithmetic = 281;
	int OperatorNotAllowedOnBoolExpression = 282;
	int ForeachKeyTypeMustBeIntOrUint = 283;
	int ForeachKeyTypeMustBeIntOrUintLongOrUlong = 284;
	int ForeachKeyCannotBeOutOrRef = 285;
	int NoReverseIterationOnAssociativeArrays = 286;
	int OnlyOneOrTwoArgumentsForAssociativeArrayForeach = 287;
	int OnlyOneOrTwoArgumentsForArrayForeach = 288;
	int ForeachTargetIsNotAnArrayOf = 289;
	int ForeachKeyCannotBeInout = 290;
	int ForeachValueOfUTFConversionCannotBeInout = 291;
	int CannotInferTypeForSymbol = 292;
	int CannotInferTypeFromInitializer = 293;
	int NoStorageClassForSymbol = 294;
	int OnlyOneValueOrTwoKeyValueArgumentsForTupleForeach = 295;
	int CannotUniquelyInferForeachArgumentTypes = 296;
	int InvalidForeachAggregate = 297;
	int NotAnAssociativeArrayInitializer = 298;
	int ArrayInitializersAsExpressionsNotAllowed = 299;
	int IftypeConditionCannotBeAtGlobalScope = 300;
	int SymbolIsNotAFieldOfSymbol = 301;
	int RecursiveTemplateExpansion = 302;
	int RecursiveTemplateExpansionForTemplateArgument = 303;
	int IndexIsNotATypeOrExpression = 304;
	int CannotHavePointerToSymbol = 305;
	int SizeOfTypeIsNotKnown = 306;
	int CanOnlySliceTupleTypes = 307;
	int NoPropertyForTuple = 308;
	int CannotResolveDotProperty = 309;
	int CannotTakeAddressOfBitInArray = 310;
	int OnlyOneIndexAllowedToIndex = 311;
	int NoOpIndexOperatorOverloadForType = 312;
	int ArrayDimensionOverflow = 313;
	int OperatorAssignmentOverloadWithOpIndexIllegal = 314;
	int CannotHaveOutOrInoutArgumentOfBitInArray = 315;
	int SymbolIsAliasedToAFunction = 316;
	int LinkageDoesNotMatchInterfaceFunction = 317;
	int InterfaceFunctionIsNotImplemented = 318;
	int ExpectedKeyAsArgumentToRemove = 319;
	int CyclicConstructorCall = 320;
	int MissingOrCurruptObjectDotD = 321;
	int CannotContinueOutOfFinallyBlock = 322;
	int ForwardDeclaration = 323;
	int CannotFormDelegateDueToCovariantReturnType = 324;
	int ForeachRangeKeyCannotHaveStorageClass = 325;
	int MultipleOverridesOfSameFunction = 326;
	int IdentityAssignmentOperatorOverloadIsIllegal = 327;
	int LiteralsCannotBeClassMembers = 328;
	int NoMatchForImplicitSuperCallInConstructor = 329;
	int NoReturnAtEndOfFunction = 330;
	int CanOnlyDeclareTypeAliasesWithinStaticIfConditionals = 331;
	int PackageAndModuleHaveTheSameName = 332;
	int StringLiteralsAreImmutable = 333;
	int ExpressionDotNewIsOnlyForAllocatingNestedClasses = 334;
	int TooManyArgumentsForArray = 335;
	int ReturnExpressionExpected = 336;
	int ReturnWithoutCallingConstructor = 337;
	int ModuleIsInMultiplePackages = 338;
	int ModuleIsInMultipleDefined = 339;
	int NeedUpperAndLowerBoundToSlicePointer = 340;
	int NeedUpperAndLowerBoundToSliceTuple = 341;
	int CannotConvertStringLiteralToVoidPointer = 342;
	int SymbolIsNotAPreInstanceInitializableField = 343;
	int NoCaseStatementFollowingGoto = 344;
	int SwitchStatementHasNoDefault = 345;
	int SymbolIsNotAFunctionTemplate = 346;
	int TupleIsNotAValidTemplateValueArgument = 347;
	int IncompatibleArgumentsForTemplateInstantiation = 348;
	int ThrowStatementsCannotBeInContracts = 349;
	int CanOnlyThrowClassObjects = 350;
	int StringExpectedAsSecondArgument = 351;
	int WrongNumberOfArguments = 352;
	int StringMustBeChars = 353;
	int InvalidFirstArgument = 354;
	int FirstArgumentIsNotAClass = 355;
	int ArgumentHasNoMembers = 356;
	int SymbolHasNoMembers = 357;
	int KindSymbolHasNoMembers = 358;
	int DotOffsetDeprecated = 359;
	int NoClassInfoForComInterfaceObjects = 360;
	int CannotMakeReferenceToABit = 361;
	int CannotFormTupleOfTuples = 362;
	int MissingInitializerInStaticConstructorForConstVariable = 363;
	int GlobalsStaticsFieldsRefAndAutoParametersCannotBeAuto = 364;
	int ReferenceToScopeClassMustBeScope = 365;
	int NumberOfKeysMustMatchNumberOfValues = 366;
	int ExpectedNumberArguments = 367;
	int ArraySliceIfOutOfBounds = 368;
	int InvalidUCS32Char = 369;
	int TupleIndexExceedsBounds = 370;
	int SliceIsOutOfRange = 371;
	int CannotTakeAddressOf = 372;
	int VariableIsUsedBeforeInitialization = 373;
	int EscapingReferenceToLocal = 374;
	int EscapingReferenceToAutoLocal = 375;
	int EscapingReferenceToLocalVariable = 376;
	int EscapingReferenceToVariadicParameter = 377;
	int CanOnlyCatchClassObjects = 378;
	int BaseClassIsForwardReferenced = 379;
	int BaseIsForwardReferenced = 380;
	int CannotInheritFromFinalClass = 381;
	int StaticClassCannotInheritFromNestedClass = 382;
	int SuperClassIsNestedWithin = 383;
	int SuperClassIsNotNestedWithin = 384;
	int ArrayComparisonTypeMismatch = 385;
	int ConditionalExpressionIsNotAModifiableLvalue = 386;
	int CannotCastSymbolToSymbol = 387;
	int CannotDeleteInstanceOfComInterface = 388;
	int TemplateIsNotAMemberOf = 389;
	int TemplateIdentifierIsNotAMemberOf = 390;
	int TemplateIdentifierIsNotAMemberOfUndefined = 391;
	int CanOnlyInitiailizeConstMemberInsideConstructor = 392;
	int SymbolIsNotAMember = 393;
	int SymbolIsNotATemplate = 394;
	int DSymbolHasNoSize = 395;
	int ExpressionOfTypeDoesNotHaveABooleanValue = 396;
	int ImplicitConversionCanCauseLossOfData = 397;
	int ForwardReferenceToType = 398;
	int FloatingPointConstantExpressionExpected = 399;
	int ExpressionIsNotAValidTemplateValueArgument = 400;
	int InvalidRangeLowerBound = 401;
	int InvalidRangeUpperBound = 402;
	int SymbolIsNotAScalarType = 403;
	int ForeachIndexMustBeType = 404;
	int ForeachValueMustBeType = 405;
	int OpApplyFunctionMustReturnAnInt = 406;
	int FunctionOfTypeOverridesButIsNotCovariant = 407;
	int CannotOverrideFinalFunction = 408;
	int IncompatibleCovariantTypes = 409;
	int CannotUseTemplateToAddVirtualFunctionToClass = 410;
	int OutResultIsAlreadyDefined = 411;
	int MissingInitializerForConstField = 412;
	int MissingInitializerForFinalField = 413;
	int ImportNotFound = 414;
	int SymbolMustBeAnArrayOfPointerType = 415;
	int RvalueOfInExpressionMustBeAnAssociativeArray = 416;
	int InterfaceInheritsFromDuplicateInterface = 417;
	int LabelIsAlreadyDefined = 418;
	int CannotSubtractPointerFromSymbol = 419;
	int ThisForNestedClassMustBeAClassType = 420;
	int CanOnlyDereferenceAPointer = 421;
	int OuterClassThisNeededToNewNestedClass = 422;
	int ThisForNestedClassMustBeOfType = 423;
	int NoConstructorForSymbol = 424;
	int NoAllocatorForSymbol = 425;
	int NegativeArrayIndex = 426;
	int NewCanOnlyCreateStructsDynamicArraysAndClassObjects = 427;
	int MismatchedFunctionReturnTypeInference = 428;
	int ShiftLeftExceeds = 429;
	int SymbolCannotBeSlicedWithBrackets = 430;
	int SliceExpressionIsNotAModifiableLvalue = 431;
	int SymbolIsNotAMemberOf = 432;
	int MoreInitiailizersThanFields = 433;
	int OverlappingInitiailization = 434;
	int CannotMakeExpressionOutOfInitializer = 435;
	int NoDefaultOrCaseInSwitchStatement = 436;
	int SymbolIsNotASymbol = 437;
	int ForwardReferenceToTemplate = 438;
	int ForwardReferenceToTemplateDeclaration = 439;
	int SpecializationNotAllowedForDeducedParameter = 440;
	int CannotDeclareTemplateAtFunctionScope = 441;
	int TemplateHasNoValue = 442;
	int CannotUseLocalAsTemplateParameter = 443;
	int NoSizeForType = 444;
	int SymbolDotSymbolIsNotADeclaration = 445;
	int ThisIsRequiredButIsNotABaseClassOf = 446;
	int ForwardReferenceToSymbol = 447;
	int IdentifierOfSymbolIsNotDefined = 448;
	int StructIsForwardReferenced = 449;
	int CannotUseTemplateToAddFieldToAggregate = 450;
	int CannotModifyFinalVariable = 451;
	int InvalidUtf8Sequence2 = 452;
	int Utf16HighValuePastEndOfString = 453;
	int Utf16LowValueOutOfRange = 454;
	int UnpairedUtf16Value = 455;
	int IllegalUtf16Value = 456;
	int StaticConstructorCanOnlyBePartOfStructClassModule = 457;
	int ShiftAssignIsOutsideTheRange = 458;
	int TemplateTupleParameterMustBeLastOne = 459;
	int SymbolIsNestedInBoth = 460;
	int FunctionIsAbstract = 461;
	int KindSymbolDoesNotOverload = 462;
	int MismatchedTupleLengths = 463;
	int DoNotUseNullWhenComparingClassTypes = 464;
	int UseTokenInsteadOfTokenWhenComparingWithNull = 465;
	int VoidDoesNotHaveAnInitializer = 466;
	int FunctionNameExpectedForStartAddress = 467;
	int TypeofReturnMustBeInsideFunction = 468;
	int PostBlitsAreOnlyForStructUnionDefinitions = 469;
	int CannotHaveEDotTuple = 470;
	int CannotCreateCppClasses = 471;
	int SwitchAndCaseAreInDifferentFinallyBlocks = 472;
	int SwitchAndDefaultAreInDifferentFinallyBlocks = 473;
	int CannotHaveFieldWithSameStructType = 474;
	int WithoutThisCannotBeConstInvariant = 475;
	int CannotModifySymbol = 476;
	int CannotCallPublicExportFunctionFromInvariant = 477;
	int TemplateMemberFunctionNotAllowedInInterface = 478;
	int ArgumentToTypeofIsNotAnExpression = 479;
	int CannotInferTypeFromOverloadedFunctionSymbol = 480;
	int SymbolIsNotMutable = 481;
	int SymbolForSymbolNeedsToBeType = 482;
	int CannotModifyConstInvariant = 483;
	int SymbolCanOnlyBeCalledOnAnInvariantObject = 484;
	int SymbolCanOnlyBeCalledOnAMutableObject = 485;
	int CannotCallMutableMethodOnFinalStruct = 486;
	int ForwardReferenceOfImport = 487;
	int TemplateDoesNotHaveProperty = 488;
	int ExpressionDoesNotHaveProperty = 489;
	int RecursiveOpCmpExpansion = 490;
	int CtorIsReservedForConstructors = 491;
	int FunctionOverridesBaseClassFunctionButIsNotMarkedWithOverride = 492;
	int ForwardReferenceOfTypeDotMangleof = 493;
	int ArrayLengthHidesOtherLengthNameInOuterScope = 494;
	int OnePathSkipsConstructor = 495;
	int PragmaLibNotAllowedAsStatement = 496;

}
