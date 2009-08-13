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
	int DestructorsOnlyForClass = 100;
	int InvariantsOnlyForClassStructUnion = 101;
	int FunctionDoesNotOverrideAny = 102;
	int CannotOverrideFinalFunctions = 103;
	int OverrideOnlyForClassMemberFunctions = 104;
	int FunctionMustReturnAResultOfType = 105;
	int MoreThanOneInvariant = 106;
	int ParameterMultiplyDefined = 107;
	int SymbolNotFound = 108;
	int StatementIsNotReachable = 109;
	int VoidFunctionsHaveNoResult = 110;
	int ReturnStatementsCannotBeInContracts = 111;
	int NotAnAggregateType = 112;
	int UnrecognizedPragma = 113;
	int AnonCanOnlyBePartOfAnAggregate = 114;
	int PragmaIsMissingClosingSemicolon = 115;
	int CannotImplicitlyConvert = 116;
	int ForbiddenReference = 117;
	int DiscouragedReference = 118;
	int Task = 119;
	int UndefinedType = 120;
	int IsClassPathCorrect = 121;
	int FunctionsCannotBeConstOrAuto = 122;
	int FunctionsCannotBeScopeOrAuto = 123;
	int NonVirtualFunctionsCannotBeAbstract = 124;
	int CannotBeBothAbstractAndFinal = 125;
	int ModifierCannotBeAppliedToVariables = 126;
	int StructsCannotBeAbstract = 127;
	int UnionsCannotBeAbstract = 128;
	int AliasCannotBeConst = 129;
	int OneArgumentOfTypeExpected = 130;
	int IllegalMainParameters = 131;
	int MustReturnIntOrVoidFromMainFunction = 132;
	int AtLeastOneArgumentOfTypeExpected = 133;
	int FirstArgumentMustBeOfType = 134;
	int StringExpectedForPragmaMsg = 135;
	int LibPragmaMustRecieveASingleArgumentOfTypeString = 136;
	int StringExpectedForPragmaLib = 137;
	int CannotHaveOutOrInoutParameterOfTypeStaticArray = 138;
	int CannotHaveParameterOfTypeVoid = 139;
	int FunctionsCannotReturnStaticArrays = 140;
	int UnrecongnizedTrait = 141;
	int CanOnlyConcatenateArrays = 142;
	int ArrayIndexOutOfBounds = 143;
	int ArrayIndexOutOfBounds2 = 144;
	int AssertionFailed = 145;
	int AssertionFailedNoMessage = 146;
	int ExpressionIsNotEvaluatableAtCompileTime = 147;
	int UndefinedProperty = 148;
	int DeprecatedProperty = 149;
	int FileNameMustBeString = 150;
	int FileImportsMustBeSpecified = 151;
	int FileNotFound = 152;
	int ErrorReadingFile = 153;
	int ExpressionHasNoEffect = 154;
	int ConstantIsNotAnLValue = 155;
	int VersionIdentifierReserved = 156;
	int CannotPutCatchStatementInsideFinallyBlock = 157;
	int ExpressionDoesNotGiveABooleanResult = 158;
	int BreakIsNotInsideALoopOrSwitch = 159;
	int CaseIsNotInSwitch = 160;
	int VersionDeclarationMustBeAtModuleLevel = 161;
	int DebugDeclarationMustBeAtModuleLevel = 162;
	int GotoCaseNotInSwitch = 163;
	int GotoDefaultNotInSwitch = 164;
	int LazyVariablesCannotBeLvalues = 165;
	int DivisionByZero = 166;
	int DefaultNotInSwitch = 167;
	int SwitchAlreadyHasDefault = 168;
	int ContinueNotInLoop = 169;
	int ForeachIndexCannotBeRef = 170;
	int ParametersDoesNotMatchParameterTypes = 171;
	int IncompatibleParameterStorageClass = 172;
	int OutCannotBeConst = 173;
	int OutCannotBeInvariant = 174;
	int ScopeCannotBeRefOrOut = 175;
	int IncompatibleTypesForOperator = 176;
	int IncompatibleTypesForMinus = 177;
	int SymbolNotDefined = 178;
	int SymbolNotATemplate = 179;
	int CannotDeleteType = 180;
	int NotAnLvalue = 181;
	int CannotAliasAnExpression = 182;
	int CannotAssignToStaticArray = 183;
	int CannotChangeReferenceToStaticArray = 184;
	int CannotModifyParameterInContract = 185;
	int BothOverloadsMuchArgumentList = 186;
	int ExpressionHasNoType = 187;
	int SymbolNotAnExpression = 188;
	int SymbolHasNoValue = 189;
	int TooManyInitializers = 190;
	int SymbolNotAStaticAndCannotHaveStaticInitializer = 191;
	int SymbolNotAType = 192;
	int IncompleteMixinDeclaration = 193;
	int SymbolNotATemplateItIs = 194;
	int SymbolCannotBeDeclaredToBeAFunction = 195;
	int CannotHaveArrayOfType = 196;
	int SymbolDoesNotMatchAnyTemplateDeclaration = 197;
	int SymbolDoesNotMatchTemplateDeclaration = 198;
	int SymbolDoesNotMatchAnyFunctionTemplateDeclaration = 199;
	int IndexOverflowForStaticArray = 200;
	int UnknownSize = 201;
	int NoSizeYetForForwardReference = 202;
	int SymbolMatchesMoreThanOneTemplateDeclaration = 203;
	int ExpressionLeadsToStackOverflowAtCompileTime = 204;
	int StringIndexOutOfBounds = 205;
	int CannotCreateInstanceOfAbstractClass = 206;
	int CannotCreateInstanceOfInterface = 207;
	int WithExpressionsMustBeClassObject = 208;
	int DeclarationIsAlreadyDefined = 209;
	int DeclarationIsAlreadyDefinedInAnotherScope = 210;
	int VersionDefinedAfterUse = 211;
	int DebugDefinedAfterUse = 212;
	int NotEnoughArguments = 213;
	int CanOnlySynchronizeOnClassObjects = 214;
	int CannotDeduceTemplateFunctionFromArgumentTypes = 215;
	int CannotDeduceTemplateFunctionFromArgumentTypes2 = 216;
	int ArrayDimensionExceedsMax = 217;
	int AStructIsNotAValidInitializerFor = 218;
	int CannotUseArrayToInitialize = 219;
	int CircularReferenceTo = 220;
	int ParameterIsAlreadyDefined = 221;
	int MemberIsNotAccessible = 222;
	int SymbolIsNotAccessible = 223;
	int ThisForSymbolNeedsToBeType = 224;
	int SymbolHasForwardReferences = 225;
	int CannotHaveAssociativeArrayOfKey = 226;
	int CannotHaveAssociativeArrayOf = 227;
	int CannotHaveArrayOfAuto = 228;
	int EnclosingLabelForBreakNotFound = 229;
	int EnclosingLabelForContinueNotFound = 230;
	int CannotAppendTypeToType = 231;
	int CannotAppendToStaticArray = 232;
	int ExpressionIsVoidAndHasNoValue = 233;
	int NeedMemberFunctionOpCmpForSymbolToCompare = 234;
	int CompareNotDefinedForComplexOperands = 235;
	int NeedThisForAddressOfSymbol = 236;
	int RecursiveMixinInstantiation = 237;
	int SymbolIsNotOfIntegralType = 238;
	int DeleteAAKeyDeprecated = 239;
	int SymbolIsDeprecated = 240;
	int ShadowingDeclarationIsDeprecated = 241;
	int ReturnStatementsCannotBeInFinallyScopeExitOrScopeSuccessBodies = 242;
	int CannotReturnExpressionFromConstructor = 243;
	int CaseNotFound = 244;
	int CircularInheritanceOfInterface = 245;
	int ArgumentToMixinMustBeString = 246;
	int CannotAccessFrameOfFunction = 247;
	int OperationNotAllowedOnBool = 248;
	int SymbolIsNotAScalar = 249;
	int ImportCannotBeResolved = 250;
	int SymbolIsNotAVariable = 251;
	int CatchHidesCatch = 252;
	int ArithmeticOrStringTypeExpectedForValueParameter = 253;
	int FunctionsCannotReturnAFunction = 254;
	int FunctionsCannotReturnATuple = 255;
	int FunctionsCannotReturnAuto = 256;
	int RecursiveType = 257;
	int VariadicFunctionsWithNonDLinkageMustHaveAtLeastOneParameter = 258;
	int SymbolMustBeAFunction = 259;
	int FunctionExpectedBeforeCall = 260;
	int FunctionExpectedBeforeCallNotSymbolOfType = 261;
	int CircularReferenceOfTypedef = 262;
	int StringSliceIsOutOfBounds = 263;
	int ErrorInstantiating = 264;
	int CaseMustBeAnIntegralOrStringConstant = 265;
	int DuplicateCaseInSwitchStatement = 266;
	int SpecialMemberFunctionsNotAllowedForSymbol = 267;
	int SpecialFunctionsNotAllowedInInterface = 268;
	int FunctionBodyIsNotAbstractInInterface = 269;
	int SuperClassConstructorCallMustBeInAConstructor = 270;
	int ClassConstructorCallMustBeInAConstructor = 271;
	int NoSuperClassConstructor = 272;
	int ConstructorCallsNotAllowedInLoopsOrAfterLabels = 273;
	int MultipleConstructorCalls = 274;
	int ExpressionIsNotConstantOrDoesNotEvaluateToABool = 275;
	int StaticIfConditionalCannotBeAtGlobalScope = 276;
	int CannotBreakOutOfFinallyBlock = 277;
	int LabelHasNoBreak = 278;
	int LabelHasNoContinue = 279;
	int CannotGotoInOrOutOfFinallyBlock = 280;
	int CalledWithArgumentTypesMatchesBoth = 281;
	int SymbolIsNotAnArithmeticType = 282;
	int SymbolIsNotAnArithmeticTypeItIs = 283;
	int CannotPerformModuloComplexArithmetic = 284;
	int OperatorNotAllowedOnBoolExpression = 285;
	int ForeachKeyTypeMustBeIntOrUint = 286;
	int ForeachKeyTypeMustBeIntOrUintLongOrUlong = 287;
	int ForeachKeyCannotBeOutOrRef = 288;
	int NoReverseIterationOnAssociativeArrays = 289;
	int OnlyOneOrTwoArgumentsForAssociativeArrayForeach = 290;
	int OnlyOneOrTwoArgumentsForArrayForeach = 291;
	int ForeachTargetIsNotAnArrayOf = 292;
	int ForeachKeyCannotBeInout = 293;
	int ForeachValueOfUTFConversionCannotBeInout = 294;
	int CannotInferTypeForSymbol = 295;
	int CannotInferTypeFromInitializer = 296;
	int NoStorageClassForSymbol = 297;
	int OnlyOneValueOrTwoKeyValueArgumentsForTupleForeach = 298;
	int CannotUniquelyInferForeachArgumentTypes = 299;
	int InvalidForeachAggregate = 300;
	int NotAnAssociativeArrayInitializer = 301;
	int ArrayInitializersAsExpressionsNotAllowed = 302;
	int IftypeConditionCannotBeAtGlobalScope = 303;
	int SymbolIsNotAFieldOfSymbol = 304;
	int RecursiveTemplateExpansion = 305;
	int RecursiveTemplateExpansionForTemplateArgument = 306;
	int IndexIsNotATypeOrExpression = 307;
	int CannotHavePointerToSymbol = 308;
	int SizeOfTypeIsNotKnown = 309;
	int CanOnlySliceTupleTypes = 310;
	int NoPropertyForTuple = 311;
	int CannotResolveDotProperty = 312;
	int CannotTakeAddressOfBitInArray = 313;
	int OnlyOneIndexAllowedToIndex = 314;
	int NoOpIndexOperatorOverloadForType = 315;
	int ArrayDimensionOverflow = 316;
	int OperatorAssignmentOverloadWithOpIndexIllegal = 317;
	int CannotHaveOutOrInoutArgumentOfBitInArray = 318;
	int SymbolIsAliasedToAFunction = 319;
	int LinkageDoesNotMatchInterfaceFunction = 320;
	int InterfaceFunctionIsNotImplemented = 321;
	int ExpectedKeyAsArgumentToRemove = 322;
	int CyclicConstructorCall = 323;
	int MissingOrCurruptObjectDotD = 324;
	int CannotContinueOutOfFinallyBlock = 325;
	int ForwardDeclaration = 326;
	int CannotFormDelegateDueToCovariantReturnType = 327;
	int ForeachRangeKeyCannotHaveStorageClass = 328;
	int MultipleOverridesOfSameFunction = 329;
	int IdentityAssignmentOperatorOverloadIsIllegal = 330;
	int LiteralsCannotBeClassMembers = 331;
	int NoMatchForImplicitSuperCallInConstructor = 332;
	int NoReturnAtEndOfFunction = 333;
	int CanOnlyDeclareTypeAliasesWithinStaticIfConditionals = 334;
	int PackageAndModuleHaveTheSameName = 335;
	int StringLiteralsAreImmutable = 336;
	int ExpressionDotNewIsOnlyForAllocatingNestedClasses = 337;
	int TooManyArgumentsForArray = 338;
	int ReturnExpressionExpected = 339;
	int ReturnWithoutCallingConstructor = 340;
	int ModuleIsInMultiplePackages = 341;
	int ModuleIsInMultipleDefined = 342;
	int NeedUpperAndLowerBoundToSlicePointer = 343;
	int NeedUpperAndLowerBoundToSliceTuple = 344;
	int CannotConvertStringLiteralToVoidPointer = 345;
	int SymbolIsNotAPreInstanceInitializableField = 346;
	int NoCaseStatementFollowingGoto = 347;
	int SwitchStatementHasNoDefault = 348;
	int SymbolIsNotAFunctionTemplate = 349;
	int TupleIsNotAValidTemplateValueArgument = 350;
	int IncompatibleArgumentsForTemplateInstantiation = 351;
	int ThrowStatementsCannotBeInContracts = 352;
	int CanOnlyThrowClassObjects = 353;
	int StringExpectedAsSecondArgument = 354;
	int WrongNumberOfArguments = 355;
	int StringMustBeChars = 356;
	int InvalidFirstArgument = 357;
	int FirstArgumentIsNotAClass = 358;
	int ArgumentHasNoMembers = 359;
	int SymbolHasNoMembers = 360;
	int KindSymbolHasNoMembers = 361;
	int DotOffsetDeprecated = 362;
	int NoClassInfoForComInterfaceObjects = 363;
	int CannotMakeReferenceToABit = 364;
	int CannotFormTupleOfTuples = 365;
	int MissingInitializerInStaticConstructorForConstVariable = 366;
	int GlobalsStaticsFieldsRefAndAutoParametersCannotBeAuto = 367;
	int GlobalsStaticsFieldsManifestConstantsRefAndAutoParametersCannotBeScope = 368;
	int ReferenceToScopeClassMustBeScope = 369;
	int NumberOfKeysMustMatchNumberOfValues = 370;
	int ExpectedNumberArguments = 371;
	int ArraySliceIfOutOfBounds = 372;
	int InvalidUCS32Char = 373;
	int TupleIndexExceedsBounds = 374;
	int SliceIsOutOfRange = 375;
	int CannotTakeAddressOf = 376;
	int VariableIsUsedBeforeInitialization = 377;
	int EscapingReferenceToLocal = 378;
	int EscapingReferenceToAutoLocal = 379;
	int EscapingReferenceToScopeLocal = 380;
	int EscapingReferenceToLocalVariable = 381;
	int EscapingReferenceToVariadicParameter = 382;
	int CanOnlyCatchClassObjects = 383;
	int BaseClassIsForwardReferenced = 384;
	int BaseIsForwardReferenced = 385;
	int CannotInheritFromFinalClass = 386;
	int StaticClassCannotInheritFromNestedClass = 387;
	int SuperClassIsNestedWithin = 388;
	int SuperClassIsNotNestedWithin = 389;
	int ArrayComparisonTypeMismatch = 390;
	int ConditionalExpressionIsNotAModifiableLvalue = 391;
	int CannotCastSymbolToSymbol = 392;
	int CannotDeleteInstanceOfComInterface = 393;
	int TemplateIsNotAMemberOf = 394;
	int TemplateIdentifierIsNotAMemberOf = 395;
	int TemplateIdentifierIsNotAMemberOfUndefined = 396;
	int CanOnlyInitiailizeConstMemberInsideConstructor = 397;
	int SymbolIsNotAMember = 398;
	int SymbolIsNotATemplate = 399;
	int DSymbolHasNoSize = 400;
	int ExpressionOfTypeDoesNotHaveABooleanValue = 401;
	int ImplicitConversionCanCauseLossOfData = 402;
	int ForwardReferenceToType = 403;
	int FloatingPointConstantExpressionExpected = 404;
	int ExpressionIsNotAValidTemplateValueArgument = 405;
	int InvalidRangeLowerBound = 406;
	int InvalidRangeUpperBound = 407;
	int SymbolIsNotAScalarType = 408;
	int ForeachIndexMustBeType = 409;
	int ForeachValueMustBeType = 410;
	int OpApplyFunctionMustReturnAnInt = 411;
	int FunctionOfTypeOverridesButIsNotCovariant = 412;
	int CannotOverrideFinalFunction = 413;
	int IncompatibleCovariantTypes = 414;
	int CannotUseTemplateToAddVirtualFunctionToClass = 415;
	int OutResultIsAlreadyDefined = 416;
	int MissingInitializerForConstField = 417;
	int MissingInitializerForFinalField = 418;
	int ImportNotFound = 419;
	int SymbolMustBeAnArrayOfPointerType = 420;
	int RvalueOfInExpressionMustBeAnAssociativeArray = 421;
	int InterfaceInheritsFromDuplicateInterface = 422;
	int LabelIsAlreadyDefined = 423;
	int CannotSubtractPointerFromSymbol = 424;
	int ThisForNestedClassMustBeAClassType = 425;
	int CanOnlyDereferenceAPointer = 426;
	int OuterClassThisNeededToNewNestedClass = 427;
	int ThisForNestedClassMustBeOfType = 428;
	int NoConstructorForSymbol = 429;
	int NoAllocatorForSymbol = 430;
	int NegativeArrayIndex = 431;
	int NewCanOnlyCreateStructsDynamicArraysAndClassObjects = 432;
	int MismatchedFunctionReturnTypeInference = 433;
	int ShiftLeftExceeds = 434;
	int SymbolCannotBeSlicedWithBrackets = 435;
	int SliceExpressionIsNotAModifiableLvalue = 436;
	int SymbolIsNotAMemberOf = 437;
	int MoreInitiailizersThanFields = 438;
	int OverlappingInitiailization = 439;
	int CannotMakeExpressionOutOfInitializer = 440;
	int NoDefaultOrCaseInSwitchStatement = 441;
	int SymbolIsNotASymbol = 442;
	int ForwardReferenceToTemplate = 443;
	int ForwardReferenceToTemplateDeclaration = 444;
	int SpecializationNotAllowedForDeducedParameter = 445;
	int CannotDeclareTemplateAtFunctionScope = 446;
	int TemplateHasNoValue = 447;
	int CannotUseLocalAsTemplateParameter = 448;
	int NoSizeForType = 449;
	int SymbolDotSymbolIsNotADeclaration = 450;
	int ThisIsRequiredButIsNotABaseClassOf = 451;
	int ForwardReferenceToSymbol = 452;
	int IdentifierOfSymbolIsNotDefined = 453;
	int StructIsForwardReferenced = 454;
	int CannotUseTemplateToAddFieldToAggregate = 455;
	int CannotModifyFinalVariable = 456;
	int InvalidUtf8Sequence2 = 457;
	int Utf16HighValuePastEndOfString = 458;
	int Utf16LowValueOutOfRange = 459;
	int UnpairedUtf16Value = 460;
	int IllegalUtf16Value = 461;
	int StaticConstructorCanOnlyBePartOfStructClassModule = 462;
	int ShiftAssignIsOutsideTheRange = 463;
	int TemplateTupleParameterMustBeLastOne = 464;
	int SymbolIsNestedInBoth = 465;
	int FunctionIsAbstract = 466;
	int KindSymbolDoesNotOverload = 467;
	int MismatchedTupleLengths = 468;
	int DoNotUseNullWhenComparingClassTypes = 469;
	int UseTokenInsteadOfTokenWhenComparingWithNull = 470;
	int VoidDoesNotHaveAnInitializer = 471;
	int FunctionNameExpectedForStartAddress = 472;
	int TypeofReturnMustBeInsideFunction = 473;
	int PostBlitsAreOnlyForStructUnionDefinitions = 474;
	int CannotHaveEDotTuple = 475;
	int CannotCreateCppClasses = 476;
	int SwitchAndCaseAreInDifferentFinallyBlocks = 477;
	int SwitchAndDefaultAreInDifferentFinallyBlocks = 478;
	int CannotHaveFieldWithSameStructType = 479;
	int WithoutThisCannotBeConstInvariant = 480;
	int CannotModifySymbol = 481;
	int CannotCallPublicExportFunctionFromImmutable = 482;
	int TemplateMemberFunctionNotAllowedInInterface = 483;
	int ArgumentToTypeofIsNotAnExpression = 484;
	int CannotInferTypeFromOverloadedFunctionSymbol = 485;
	int SymbolIsNotMutable = 486;
	int SymbolForSymbolNeedsToBeType = 487;
	int CannotModifyConstImmutable = 488;
	int SymbolCanOnlyBeCalledOnAnInvariantObject = 489;
	int SymbolCanOnlyBeCalledOnAMutableObject = 490;
	int CannotCallMutableMethodOnFinalStruct = 491;
	int ForwardReferenceOfImport = 492;
	int TemplateDoesNotHaveProperty = 493;
	int ExpressionDoesNotHaveProperty = 494;
	int RecursiveOpCmpExpansion = 495;
	int CtorIsReservedForConstructors = 496;
	int FunctionOverridesBaseClassFunctionButIsNotMarkedWithOverride = 497;
	int ForwardReferenceOfTypeDotMangleof = 498;
	int ArrayLengthHidesOtherLengthNameInOuterScope = 499;
	int OnePathSkipsConstructor = 500;
	int PragmaLibNotAllowedAsStatement = 501;
	int ThereCanBeOnlyOneAliasThis = 502;
	int AliasThisCanOnlyAppearInStructOrClassDeclaration = 503;
	int FunctionIsOverloaded = 504;
	int PureFunctionCannotCallImpure = 505;
	int PureFunctionCannotAccessMutableStaticData = 506;
	int PureNestedFunctionCannotAccessMutableData = 507;
	int ConstraintIsNotConstantOrDoesNotEvaluateToABool = 508;
	int EscapeStringLiteralDeprecated = 509;
	int DelimiterCannotBeWhitespace = 510;
	int ArrayEqualityComparisonTypeMismatch = 511;
	int GsharedNotAllowedInSafeMode = 512;
	int InnerStructCannotBeAField = 513;
	int OnlyParametersOfForeachDeclarationsCanBeRef = 514;
	int ManifestConstantsMustHaveInitializers = 515;
	int ForwardReferenceToInferredReturnTypeOfFunctionCall = 516;
	int CaseRangesNotAllowedInFinalSwitch = 517;
	int MoreThan256CasesInCaseRange = 518;

}
