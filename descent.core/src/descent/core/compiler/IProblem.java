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
	int NonVirtualFunctionsCannotBeAbstract = 120;
	int CannotBeBothAbstractAndFinal = 121;
	int ModifierCannotBeAppliedToVariables = 122;
	int StructsCannotBeAbstract = 123;
	int UnionsCannotBeAbstract = 124;
	int AliasCannotBeConst = 125;
	int OneArgumentOfTypeExpected = 126;
	int IllegalMainParameters = 127;
	int MustReturnIntOrVoidFromMainFunction = 128;
	int AtLeastOneArgumentOfTypeExpected = 129;
	int FirstArgumentMustBeOfType = 130;
	int StringExpectedForPragmaMsg = 131;
	int LibPragmaMustRecieveASingleArgumentOfTypeString = 132;
	int StringExpectedForPragmaLib = 133;
	int CannotHaveOutOrInoutParameterOfTypeStaticArray = 134;
	int CannotHaveParameterOfTypeVoid = 135;
	int FunctionsCannotReturnStaticArrays = 136;
	int UnrecongnizedTrait = 137;
	int CanOnlyConcatenateArrays = 138;
	int ArrayIndexOutOfBounds = 139;
	int ArrayIndexOutOfBounds2 = 140;
	int AssertionFailed = 141;
	int AssertionFailedNoMessage = 142;
	int ExpressionIsNotEvaluatableAtCompileTime = 143;
	int UndefinedProperty = 144;
	int DeprecatedProperty = 145;
	int FileNameMustBeString = 146;
	int FileImportsMustBeSpecified = 147;
	int FileNotFound = 148;
	int ErrorReadingFile = 149;
	int ExpressionHasNoEffect = 150;
	int ConstantIsNotAnLValue = 151;
	int VersionIdentifierReserved = 152;
	int CannotPutCatchStatementInsideFinallyBlock = 153;
	int ExpressionDoesNotGiveABooleanResult = 154;
	int BreakIsNotInsideALoopOrSwitch = 155;
	int CaseIsNotInSwitch = 156;
	int VersionDeclarationMustBeAtModuleLevel = 157;
	int DebugDeclarationMustBeAtModuleLevel = 158;
	int GotoCaseNotInSwitch = 159;
	int GotoDefaultNotInSwitch = 160;
	int LazyVariablesCannotBeLvalues = 161;
	int DivisionByZero = 162;
	int DefaultNotInSwitch = 163;
	int SwitchAlreadyHasDefault = 164;
	int ContinueNotInLoop = 165;
	int ForeachIndexCannotBeRef = 166;
	int ParametersDoesNotMatchParameterTypes = 167;
	int IncompatibleParameterStorageClass = 168;
	int OutCannotBeConst = 169;
	int OutCannotBeInvariant = 170;
	int ScopeCannotBeRefOrOut = 171;
	int IncompatibleTypesForOperator = 172;
	int IncompatibleTypesForMinus = 173;
	int SymbolNotDefined = 174;
	int SymbolNotATemplate = 175;
	int CannotDeleteType = 176;
	int NotAnLvalue = 177;
	int CannotAliasAnExpression = 178;
	int CannotAssignToStaticArray = 179;
	int CannotChangeReferenceToStaticArray = 180;
	int CannotModifyParameterInContract = 181;
	int BothOverloadsMuchArgumentList = 182;
	int ExpressionHasNoType = 183;
	int SymbolNotAnExpression = 184;
	int SymbolHasNoValue = 185;
	int TooManyInitializers = 186;
	int SymbolNotAStaticAndCannotHaveStaticInitializer = 187;
	int SymbolNotAType = 188;
	int IncompleteMixinDeclaration = 189;
	int SymbolNotATemplateItIs = 190;
	int SymbolCannotBeDeclaredToBeAFunction = 191;
	int CannotHaveArrayOfType = 192;
	int SymbolDoesNotMatchAnyTemplateDeclaration = 193;
	int IndexOverflowForStaticArray = 194;
	int UnknownSize = 195;
	int NoSizeYetForForwardReference = 196;
	int SymbolMatchesMoreThanOneTemplateDeclaration = 197;
	int ExpressionLeadsToStackOverflowAtCompileTime = 198;
	int StringIndexOutOfBounds = 199;
	int CannotCreateInstanceOfAbstractClass = 200;
	int CannotCreateInstanceOfInterface = 201;
	int WithExpressionsMustBeClassObject = 202;
	int DeclarationIsAlreadyDefined = 203;
	int DeclarationIsAlreadyDefinedInAnotherScope = 204;
	int VersionDefinedAfterUse = 205;
	int DebugDefinedAfterUse = 206;
	int NotEnoughArguments = 207;
	int CanOnlySynchronizeOnClassObjects = 208;
	int CannotDeduceTemplateFunctionFromArgumentTypes = 209;
	int ArrayDimensionExceedsMax = 210;
	int AStructIsNotAValidInitializerFor = 211;
	int CannotUseArrayToInitialize = 212;
	int CircularReferenceTo = 213;
	int ParameterIsAlreadyDefined = 214;
	int MemberIsNotAccessible = 215;
	int SymbolIsNotAccessible = 216;
	int ThisForSymbolNeedsToBeType = 217;
	int SymbolHasForwardReferences = 218;
	int CannotHaveAssociativeArrayOfKey = 219;
	int CannotHaveAssociativeArrayOf = 220;
	int CannotHaveArrayOfAuto = 221;
	int EnclosingLabelForBreakNotFound = 222;
	int EnclosingLabelForContinueNotFound = 223;
	int CannotAppendTypeToType = 224;
	int CannotAppendToStaticArray = 225;
	int ExpressionIsVoidAndHasNoValue = 226;
	int NeedMemberFunctionOpCmpForSymbolToCompare = 227;
	int CompareNotDefinedForComplexOperands = 228;
	int NeedThisForAddressOfSymbol = 229;
	int RecursiveMixinInstantiation = 230;
	int SymbolIsNotOfIntegralType = 231;
	int DeleteAAKeyDeprecated = 232;
	int SymbolIsDeprecated = 233;
	int ShadowingDeclarationIsDeprecated = 234;
	int ReturnStatementsCannotBeInFinallyScopeExitOrScopeSuccessBodies = 235;
	int CannotReturnExpressionFromConstructor = 236;
	int CaseNotFound = 237;
	int CircularInheritanceOfInterface = 238;
	int ArgumentToMixinMustBeString = 239;
	int CannotAccessFrameOfFunction = 240;
	int OperationNotAllowedOnBool = 241;
	int SymbolIsNotAScalar = 242;
	int ImportCannotBeResolved = 243;
	int SymbolIsNotAVariable = 244;
	int CatchHidesCatch = 245;
	int ArithmeticOrStringTypeExpectedForValueParameter = 246;
	int FunctionsCannotReturnAFunction = 247;
	int FunctionsCannotReturnATuple = 248;
	int FunctionsCannotReturnAuto = 249;
	int RecursiveType = 250;
	int VariadicFunctionsWithNonDLinkageMustHaveAtLeastOneParameter = 251;
	int SymbolMustBeAFunction = 252;
	int FunctionExpectedBeforeCall = 253;
	int FunctionExpectedBeforeCallNotSymbolOfType = 254;
	int CircularReferenceOfTypedef = 255;
	int StringSliceIsOutOfBounds = 256;
	int ErrorInstantiating = 257;
	int CaseMustBeAnIntegralOrStringConstant = 258;
	int DuplicateCaseInSwitchStatement = 259;
	int SpecialMemberFunctionsNotAllowedForSymbol = 260;
	int SpecialFunctionsNotAllowedInInterface = 261;
	int FunctionBodyIsNotAbstractInInterface = 262;
	int SuperClassConstructorCallMustBeInAConstructor = 263;
	int ClassConstructorCallMustBeInAConstructor = 264;
	int NoSuperClassConstructor = 265;
	int ConstructorCallsNotAllowedInLoopsOrAfterLabels = 266;
	int MultipleConstructorCalls = 267;
	int ExpressionIsNotConstantOrDoesNotEvaluateToABool = 268;
	int StaticIfConditionalCannotBeAtGlobalScope = 269;
	int CannotBreakOutOfFinallyBlock = 270;
	int LabelHasNoBreak = 271;
	int LabelHasNoContinue = 272;
	int CannotGotoInOrOutOfFinallyBlock = 273;
	int CalledWithArgumentTypesMatchesBoth = 274;
	int SymbolIsNotAnArithmeticType = 275;
	int SymbolIsNotAnArithmeticTypeItIs = 276;
	int CannotPerformModuloComplexArithmetic = 277;
	int OperatorNotAllowedOnBoolExpression = 278;
	int ForeachKeyTypeMustBeIntOrUint = 279;
	int ForeachKeyCannotBeOutOrRef = 280;
	int NoReverseIterationOnAssociativeArrays = 281;
	int OnlyOneOrTwoArgumentsForAssociativeArrayForeach = 282;
	int OnlyOneOrTwoArgumentsForArrayForeach = 283;
	int ForeachTargetIsNotAnArrayOf = 284;
	int ForeachKeyCannotBeInout = 285;
	int ForeachValueOfUTFConversionCannotBeInout = 286;
	int CannotInferTypeForSymbol = 287;
	int CannotInferTypeFromInitializer = 288;
	int NoStorageClassForSymbol = 289;
	int OnlyOneValueOrTwoKeyValueArgumentsForTupleForeach = 290;
	int CannotUniquelyInferForeachArgumentTypes = 291;
	int InvalidForeachAggregate = 292;
	int NotAnAssociativeArrayInitializer = 293;
	int ArrayInitializersAsExpressionsNotAllowed = 294;
	int IftypeConditionCannotBeAtGlobalScope = 295;
	int SymbolIsNotAFieldOfSymbol = 296;
	int RecursiveTemplateExpansion = 297;
	int RecursiveTemplateExpansionForTemplateArgument = 298;
	int IndexIsNotATypeOrExpression = 299;
	int CannotHavePointerToSymbol = 300;
	int SizeOfTypeIsNotKnown = 301;
	int CanOnlySliceTupleTypes = 302;
	int NoPropertyForTuple = 303;
	int CannotResolveDotProperty = 304;
	int CannotTakeAddressOfBitInArray = 305;
	int OnlyOneIndexAllowedToIndex = 306;
	int NoOpIndexOperatorOverloadForType = 307;
	int ArrayDimensionOverflow = 308;
	int OperatorAssignmentOverloadWithOpIndexIllegal = 309;
	int CannotHaveOutOrInoutArgumentOfBitInArray = 310;
	int SymbolIsAliasedToAFunction = 311;
	int LinkageDoesNotMatchInterfaceFunction = 312;
	int InterfaceFunctionIsNotImplemented = 313;
	int ExpectedKeyAsArgumentToRemove = 314;
	int CyclicConstructorCall = 315;
	int MissingOrCurruptObjectDotD = 316;
	int CannotContinueOutOfFinallyBlock = 317;
	int ForwardDeclaration = 318;
	int CannotFormDelegateDueToCovariantReturnType = 319;
	int ForeachRangeKeyCannotHaveStorageClass = 320;
	int MultipleOverridesOfSameFunction = 321;
	int IdentityAssignmentOperatorOverloadIsIllegal = 322;
	int LiteralsCannotBeClassMembers = 323;
	int NoMatchForImplicitSuperCallInConstructor = 324;
	int NoReturnAtEndOfFunction = 325;
	int CanOnlyDeclareTypeAliasesWithinStaticIfConditionals = 326;
	int PackageAndModuleHaveTheSameName = 327;
	int StringLiteralsAreImmutable = 328;
	int ExpressionDotNewIsOnlyForAllocatingNestedClasses = 329;
	int TooManyArgumentsForArray = 330;
	int ReturnExpressionExpected = 331;
	int ReturnWithoutCallingConstructor = 332;
	int ModuleIsInMultiplePackages = 333;
	int ModuleIsInMultipleDefined = 334;
	int NeedUpperAndLowerBoundToSlicePointer = 335;
	int NeedUpperAndLowerBoundToSliceTuple = 336;
	int CannotConvertStringLiteralToVoidPointer = 337;
	int SymbolIsNotAPreInstanceInitializableField = 338;
	int NoCaseStatementFollowingGoto = 339;
	int SwitchStatementHasNoDefault = 340;
	int SymbolIsNotAFunctionTemplate = 341;
	int TupleIsNotAValidTemplateValueArgument = 342;
	int IncompatibleArgumentsForTemplateInstantiation = 343;
	int ThrowStatementsCannotBeInContracts = 344;
	int CanOnlyThrowClassObjects = 345;
	int StringExpectedAsSecondArgument = 346;
	int WrongNumberOfArguments = 347;
	int StringMustBeChars = 348;
	int InvalidFirstArgument = 349;
	int FirstArgumentIsNotAClass = 350;
	int ArgumentHasNoMembers = 351;
	int SymbolHasNoMembers = 352;
	int KindSymbolHasNoMembers = 353;
	int DotOffsetDeprecated = 354;
	int NoClassInfoForComInterfaceObjects = 355;
	int CannotMakeReferenceToABit = 356;
	int CannotFormTupleOfTuples = 357;
	int MissingInitializerInStaticConstructorForConstVariable = 358;
	int GlobalsStaticsFieldsRefAndAutoParametersCannotBeAuto = 359;
	int ReferenceToScopeClassMustBeScope = 360;
	int NumberOfKeysMustMatchNumberOfValues = 361;
	int ExpectedNumberArguments = 362;
	int ArraySliceIfOutOfBounds = 363;
	int InvalidUCS32Char = 364;
	int TupleIndexExceedsBounds = 365;
	int SliceIsOutOfRange = 366;
	int CannotTakeAddressOf = 367;
	int VariableIsUsedBeforeInitialization = 368;
	int EscapingReferenceToLocal = 369;
	int EscapingReferenceToAutoLocal = 370;
	int EscapingReferenceToLocalVariable = 371;
	int EscapingReferenceToVariadicParameter = 372;
	int CanOnlyCatchClassObjects = 373;
	int BaseClassIsForwardReferenced = 374;
	int BaseIsForwardReferenced = 375;
	int CannotInheritFromFinalClass = 376;
	int StaticClassCannotInheritFromNestedClass = 377;
	int SuperClassIsNestedWithin = 378;
	int ArrayComparisonTypeMismatch = 379;
	int ConditionalExpressionIsNotAModifiableLvalue = 380;
	int CannotCastSymbolToSymbol = 381;
	int CannotDeleteInstanceOfComInterface = 382;
	int TemplateIsNotAMemberOf = 383;
	int TemplateIdentifierIsNotAMemberOf = 384;
	int CanOnlyInitiailizeConstMemberInsideConstructor = 385;
	int SymbolIsNotAMember = 386;
	int SymbolIsNotATemplate = 387;
	int DSymbolHasNoSize = 388;
	int ExpressionOfTypeDoesNotHaveABooleanValue = 389;
	int ImplicitConversionCanCauseLossOfData = 390;
	int ForwardReferenceToType = 391;
	int FloatingPointConstantExpressionExpected = 392;
	int ExpressionIsNotAValidTemplateValueArgument = 393;
	int InvalidRangeLowerBound = 394;
	int InvalidRangeUpperBound = 395;
	int SymbolIsNotAScalarType = 396;
	int ForeachIndexMustBeType = 397;
	int ForeachValueMustBeType = 398;
	int OpApplyFunctionMustReturnAnInt = 399;
	int FunctionOfTypeOverridesButIsNotCovariant = 400;
	int CannotOverrideFinalFunction = 401;
	int IncompatibleCovariantTypes = 402;
	int CannotUseTemplateToAddVirtualFunctionToClass = 403;
	int OutResultIsAlreadyDefined = 404;
	int MissingInitializerForConstField = 405;
	int ImportNotFound = 406;
	int SymbolMustBeAnArrayOfPointerType = 407;
	int RvalueOfInExpressionMustBeAnAssociativeArray = 408;
	int InterfaceInheritsFromDuplicateInterface = 409;
	int LabelIsAlreadyDefined = 410;
	int CannotSubtractPointerFromSymbol = 411;
	int ThisForNestedClassMustBeAClassType = 412;
	int CanOnlyDereferenceAPointer = 413;
	int OuterClassThisNeededToNewNestedClass = 414;
	int ThisForNestedClassMustBeOfType = 415;
	int NoConstructorForSymbol = 416;
	int NoAllocatorForSymbol = 417;
	int NegativeArrayIndex = 418;
	int NewCanOnlyCreateStructsDynamicArraysAndClassObjects = 419;
	int MismatchedFunctionReturnTypeInference = 420;
	int ShiftLeftExceeds = 421;
	int SymbolCannotBeSlicedWithBrackets = 422;
	int SliceExpressionIsNotAModifiableLvalue = 423;
	int SymbolIsNotAMemberOf = 424;
	int MoreInitiailizersThanFields = 425;
	int OverlappingInitiailization = 426;
	int CannotMakeExpressionOutOfInitializer = 427;
	int NoDefaultOrCaseInSwitchStatement = 428;
	int SymbolIsNotASymbol = 429;
	int ForwardReferenceToTemplate = 430;
	int ForwardReferenceToTemplateDeclaration = 431;
	int SpecializationNotAllowedForDeducedParameter = 432;
	int CannotDeclareTemplateAtFunctionScope = 433;
	int TemplateHasNoValue = 434;
	int CannotUseLocalAsTemplateParameter = 435;
	int NoSizeForType = 436;
	int SymbolDotSymbolIsNotADeclaration = 437;
	int ThisIsRequiredButIsNotABaseClassOf = 438;
	int ForwardReferenceToSymbol = 439;
	int IdentifierOfSymbolIsNotDefined = 440;
	int StructIsForwardReferenced = 441;
	int CannotUseTemplateToAddFieldToAggregate = 442;
	int CannotModifyFinalVariable = 443;
	int InvalidUtf8Sequence2 = 444;
	int Utf16HighValuePastEndOfString = 445;
	int Utf16LowValueOutOfRange = 446;
	int UnpairedUtf16Value = 447;
	int IllegalUtf16Value = 448;
	int StaticConstructorCanOnlyBePartOfStructClassModule = 449;
	int ShiftAssignIsOutsideTheRange = 450;
	int TemplateTupleParameterMustBeLastOne = 451;
	int SymbolIsNestedInBoth = 452;
	int FunctionIsAbstract = 453;
	int KindSymbolDoesNotOverload = 454;
	int MismatchedTupleLengths = 455;
	int DoNotUseNullWhenComparingClassTypes = 456;
	int UseTokenInsteadOfTokenWhenComparingWithNull = 457;
	int VoidDoesNotHaveAnInitializer = 458;
	int FunctionNameExpectedForStartAddress = 459;
	int TypeofReturnMustBeInsideFunction = 460;
	int PostBlitsAreOnlyForStructUnionDefinitions = 461;

}
