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
	int ForwardReferenceOfSymbolDotSymbol = 73;
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
	int ForeachKeyCannotBeOutOrRef = 284;
	int NoReverseIterationOnAssociativeArrays = 285;
	int OnlyOneOrTwoArgumentsForAssociativeArrayForeach = 286;
	int OnlyOneOrTwoArgumentsForArrayForeach = 287;
	int ForeachTargetIsNotAnArrayOf = 288;
	int ForeachKeyCannotBeInout = 289;
	int ForeachValueOfUTFConversionCannotBeInout = 290;
	int CannotInferTypeForSymbol = 291;
	int CannotInferTypeFromInitializer = 292;
	int NoStorageClassForSymbol = 293;
	int OnlyOneValueOrTwoKeyValueArgumentsForTupleForeach = 294;
	int CannotUniquelyInferForeachArgumentTypes = 295;
	int InvalidForeachAggregate = 296;
	int NotAnAssociativeArrayInitializer = 297;
	int ArrayInitializersAsExpressionsNotAllowed = 298;
	int IftypeConditionCannotBeAtGlobalScope = 299;
	int SymbolIsNotAFieldOfSymbol = 300;
	int RecursiveTemplateExpansion = 301;
	int RecursiveTemplateExpansionForTemplateArgument = 302;
	int IndexIsNotATypeOrExpression = 303;
	int CannotHavePointerToSymbol = 304;
	int SizeOfTypeIsNotKnown = 305;
	int CanOnlySliceTupleTypes = 306;
	int NoPropertyForTuple = 307;
	int CannotResolveDotProperty = 308;
	int CannotTakeAddressOfBitInArray = 309;
	int OnlyOneIndexAllowedToIndex = 310;
	int NoOpIndexOperatorOverloadForType = 311;
	int ArrayDimensionOverflow = 312;
	int OperatorAssignmentOverloadWithOpIndexIllegal = 313;
	int CannotHaveOutOrInoutArgumentOfBitInArray = 314;
	int SymbolIsAliasedToAFunction = 315;
	int LinkageDoesNotMatchInterfaceFunction = 316;
	int InterfaceFunctionIsNotImplemented = 317;
	int ExpectedKeyAsArgumentToRemove = 318;
	int CyclicConstructorCall = 319;
	int MissingOrCurruptObjectDotD = 320;
	int CannotContinueOutOfFinallyBlock = 321;
	int ForwardDeclaration = 322;
	int CannotFormDelegateDueToCovariantReturnType = 323;
	int ForeachRangeKeyCannotHaveStorageClass = 324;
	int MultipleOverridesOfSameFunction = 325;
	int IdentityAssignmentOperatorOverloadIsIllegal = 326;
	int LiteralsCannotBeClassMembers = 327;
	int NoMatchForImplicitSuperCallInConstructor = 328;
	int NoReturnAtEndOfFunction = 329;
	int CanOnlyDeclareTypeAliasesWithinStaticIfConditionals = 330;
	int PackageAndModuleHaveTheSameName = 331;
	int StringLiteralsAreImmutable = 332;
	int ExpressionDotNewIsOnlyForAllocatingNestedClasses = 333;
	int TooManyArgumentsForArray = 334;
	int ReturnExpressionExpected = 335;
	int ReturnWithoutCallingConstructor = 336;
	int ModuleIsInMultiplePackages = 337;
	int ModuleIsInMultipleDefined = 338;
	int NeedUpperAndLowerBoundToSlicePointer = 339;
	int NeedUpperAndLowerBoundToSliceTuple = 340;
	int CannotConvertStringLiteralToVoidPointer = 341;
	int SymbolIsNotAPreInstanceInitializableField = 342;
	int NoCaseStatementFollowingGoto = 343;
	int SwitchStatementHasNoDefault = 344;
	int SymbolIsNotAFunctionTemplate = 345;
	int TupleIsNotAValidTemplateValueArgument = 346;
	int IncompatibleArgumentsForTemplateInstantiation = 347;
	int ThrowStatementsCannotBeInContracts = 348;
	int CanOnlyThrowClassObjects = 349;
	int StringExpectedAsSecondArgument = 350;
	int WrongNumberOfArguments = 351;
	int StringMustBeChars = 352;
	int InvalidFirstArgument = 353;
	int FirstArgumentIsNotAClass = 354;
	int ArgumentHasNoMembers = 355;
	int SymbolHasNoMembers = 356;
	int KindSymbolHasNoMembers = 357;
	int DotOffsetDeprecated = 358;
	int NoClassInfoForComInterfaceObjects = 359;
	int CannotMakeReferenceToABit = 360;
	int CannotFormTupleOfTuples = 361;
	int MissingInitializerInStaticConstructorForConstVariable = 362;
	int GlobalsStaticsFieldsRefAndAutoParametersCannotBeAuto = 363;
	int ReferenceToScopeClassMustBeScope = 364;
	int NumberOfKeysMustMatchNumberOfValues = 365;
	int ExpectedNumberArguments = 366;
	int ArraySliceIfOutOfBounds = 367;
	int InvalidUCS32Char = 368;
	int TupleIndexExceedsBounds = 369;
	int SliceIsOutOfRange = 370;
	int CannotTakeAddressOf = 371;
	int VariableIsUsedBeforeInitialization = 372;
	int EscapingReferenceToLocal = 373;
	int EscapingReferenceToAutoLocal = 374;
	int EscapingReferenceToLocalVariable = 375;
	int EscapingReferenceToVariadicParameter = 376;
	int CanOnlyCatchClassObjects = 377;
	int BaseClassIsForwardReferenced = 378;
	int BaseIsForwardReferenced = 379;
	int CannotInheritFromFinalClass = 380;
	int StaticClassCannotInheritFromNestedClass = 381;
	int SuperClassIsNestedWithin = 382;
	int SuperClassIsNotNestedWithin = 383;
	int ArrayComparisonTypeMismatch = 384;
	int ConditionalExpressionIsNotAModifiableLvalue = 385;
	int CannotCastSymbolToSymbol = 386;
	int CannotDeleteInstanceOfComInterface = 387;
	int TemplateIsNotAMemberOf = 388;
	int TemplateIdentifierIsNotAMemberOf = 389;
	int CanOnlyInitiailizeConstMemberInsideConstructor = 390;
	int SymbolIsNotAMember = 391;
	int SymbolIsNotATemplate = 392;
	int DSymbolHasNoSize = 393;
	int ExpressionOfTypeDoesNotHaveABooleanValue = 394;
	int ImplicitConversionCanCauseLossOfData = 395;
	int ForwardReferenceToType = 396;
	int FloatingPointConstantExpressionExpected = 397;
	int ExpressionIsNotAValidTemplateValueArgument = 398;
	int InvalidRangeLowerBound = 399;
	int InvalidRangeUpperBound = 400;
	int SymbolIsNotAScalarType = 401;
	int ForeachIndexMustBeType = 402;
	int ForeachValueMustBeType = 403;
	int OpApplyFunctionMustReturnAnInt = 404;
	int FunctionOfTypeOverridesButIsNotCovariant = 405;
	int CannotOverrideFinalFunction = 406;
	int IncompatibleCovariantTypes = 407;
	int CannotUseTemplateToAddVirtualFunctionToClass = 408;
	int OutResultIsAlreadyDefined = 409;
	int MissingInitializerForConstField = 410;
	int MissingInitializerForFinalField = 411;
	int ImportNotFound = 412;
	int SymbolMustBeAnArrayOfPointerType = 413;
	int RvalueOfInExpressionMustBeAnAssociativeArray = 414;
	int InterfaceInheritsFromDuplicateInterface = 415;
	int LabelIsAlreadyDefined = 416;
	int CannotSubtractPointerFromSymbol = 417;
	int ThisForNestedClassMustBeAClassType = 418;
	int CanOnlyDereferenceAPointer = 419;
	int OuterClassThisNeededToNewNestedClass = 420;
	int ThisForNestedClassMustBeOfType = 421;
	int NoConstructorForSymbol = 422;
	int NoAllocatorForSymbol = 423;
	int NegativeArrayIndex = 424;
	int NewCanOnlyCreateStructsDynamicArraysAndClassObjects = 425;
	int MismatchedFunctionReturnTypeInference = 426;
	int ShiftLeftExceeds = 427;
	int SymbolCannotBeSlicedWithBrackets = 428;
	int SliceExpressionIsNotAModifiableLvalue = 429;
	int SymbolIsNotAMemberOf = 430;
	int MoreInitiailizersThanFields = 431;
	int OverlappingInitiailization = 432;
	int CannotMakeExpressionOutOfInitializer = 433;
	int NoDefaultOrCaseInSwitchStatement = 434;
	int SymbolIsNotASymbol = 435;
	int ForwardReferenceToTemplate = 436;
	int ForwardReferenceToTemplateDeclaration = 437;
	int SpecializationNotAllowedForDeducedParameter = 438;
	int CannotDeclareTemplateAtFunctionScope = 439;
	int TemplateHasNoValue = 440;
	int CannotUseLocalAsTemplateParameter = 441;
	int NoSizeForType = 442;
	int SymbolDotSymbolIsNotADeclaration = 443;
	int ThisIsRequiredButIsNotABaseClassOf = 444;
	int ForwardReferenceToSymbol = 445;
	int IdentifierOfSymbolIsNotDefined = 446;
	int StructIsForwardReferenced = 447;
	int CannotUseTemplateToAddFieldToAggregate = 448;
	int CannotModifyFinalVariable = 449;
	int InvalidUtf8Sequence2 = 450;
	int Utf16HighValuePastEndOfString = 451;
	int Utf16LowValueOutOfRange = 452;
	int UnpairedUtf16Value = 453;
	int IllegalUtf16Value = 454;
	int StaticConstructorCanOnlyBePartOfStructClassModule = 455;
	int ShiftAssignIsOutsideTheRange = 456;
	int TemplateTupleParameterMustBeLastOne = 457;
	int SymbolIsNestedInBoth = 458;
	int FunctionIsAbstract = 459;
	int KindSymbolDoesNotOverload = 460;
	int MismatchedTupleLengths = 461;
	int DoNotUseNullWhenComparingClassTypes = 462;
	int UseTokenInsteadOfTokenWhenComparingWithNull = 463;
	int VoidDoesNotHaveAnInitializer = 464;
	int FunctionNameExpectedForStartAddress = 465;
	int TypeofReturnMustBeInsideFunction = 466;
	int PostBlitsAreOnlyForStructUnionDefinitions = 467;
	int CannotHaveEDotTuple = 468;
	int CannotCreateCppClasses = 469;
	int SwitchAndCaseAreInDifferentFinallyBlocks = 470;
	int SwitchAndDefaultAreInDifferentFinallyBlocks = 471;
	int CannotHaveFieldWithSameStructType = 472;
	int WithoutThisCannotBeConstInvariant = 473;
	int CannotModifySymbol = 474;
	int CannotCallPublicExportFunctionFromInvariant = 475;
	int TemplateMemberFunctionNotAllowedInInterface = 476;
	int ArgumentToTypeofIsNotAnExpression = 477;
	int CannotInferTypeFromOverloadedFunctionSymbol = 478;
	int SymbolIsNotMutable = 479;
	int SymbolForSymbolNeedsToBeType = 480;
	int CannotModifyConstInvariant = 481;
	int SymbolCanOnlyBeCalledOnAnInvariantObject = 482;
	int SymbolCanOnlyBeCalledOnAMutableObject = 483;
	int CannotCallMutableMethodOnFinalStruct = 484;

}
