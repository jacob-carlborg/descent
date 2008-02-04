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
	   55 is the first semantic error in the list. */
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
	int ArrayIndexOutOfBounds2 = 131;
	int AssertionFailed = 132;
	int AssertionFailedNoMessage = 133;
	int ExpressionIsNotEvaluatableAtCompileTime = 134;
	int UndefinedProperty = 135;
	int DeprecatedProperty = 136;
	int FileNameMustBeString = 137;
	int FileImportsMustBeSpecified = 138;
	int FileNotFound = 139;
	int ErrorReadingFile = 140;
	int ExpressionHasNoEffect = 141;
	int ConstantIsNotAnLValue = 142;
	int VersionIdentifierReserved = 143;
	int CannotPutCatchStatementInsideFinallyBlock = 144;
	int ExpressionDoesNotGiveABooleanResult = 145;
	int BreakIsNotInsideALoopOrSwitch = 146;
	int CaseIsNotInSwitch = 147;
	int VersionDeclarationMustBeAtModuleLevel = 148;
	int DebugDeclarationMustBeAtModuleLevel = 149;
	int GotoCaseNotInSwitch = 150;
	int GotoDefaultNotInSwitch = 151;
	int LazyVariablesCannotBeLvalues = 152;
	int DivisionByZero = 153;
	int DefaultNotInSwitch = 154;
	int SwitchAlreadyHasDefault = 155;
	int ContinueNotInLoop = 156;
	int ForeachIndexCannotBeRef = 157;
	int ParametersDoesNotMatchParameterTypes = 158;
	int IncompatibleParameterStorageClass = 159;
	int OutCannotBeFinal = 160;
	int ScopeCannotBeRefOrOut = 161;
	int IncompatibleTypesForOperator = 162;
	int IncompatibleTypesForMinus = 163;
	int SymbolNotDefined = 164;
	int SymbolNotATemplate = 165;
	int CannotDeleteType = 166;
	int NotAnLvalue = 167;
	int CannotAliasAnExpression = 168;
	int CannotAssignToStaticArray = 169;
	int CannotChangeReferenceToStaticArray = 170;
	int CannotModifyParameterInContract = 171;
	int BothOverloadsMuchArgumentList = 172;
	int ExpressionHasNoType = 173;
	int SymbolNotAnExpression = 174;
	int SymbolHasNoValue = 175;
	int TooManyInitializers = 176;
	int SymbolNotAStaticAndCannotHaveStaticInitializer = 177;
	int SymbolNotAType = 178;
	int IncompleteMixinDeclaration = 179;
	int SymbolNotATemplateItIs = 180;
	int SymbolCannotBeDeclaredToBeAFunction = 181;
	int CannotHaveArrayOfType = 182;
	int SymbolDoesNotMatchAnyTemplateDeclaration = 183;
	int IndexOverflowForStaticArray = 184;
	int UnknownSize = 185;
	int NoSizeYetForForwardReference = 186;
	int SymbolMatchesMoreThanOneTemplateDeclaration = 187;
	int ExpressionLeadsToStackOverflowAtCompileTime = 188;
	int StringIndexOutOfBounds = 189;
	int CannotCreateInstanceOfAbstractClass = 190;
	int CannotCreateInstanceOfInterface = 191;
	int WithExpressionsMustBeClassObject = 192;
	int DeclarationIsAlreadyDefined = 193;
	int DeclarationIsAlreadyDefinedInAnotherScope = 194;
	int VersionDefinedAfterUse = 195;
	int DebugDefinedAfterUse = 196;
	int NotEnoughArguments = 197;
	int CanOnlySynchronizeOnClassObjects = 198;
	int CannotDeduceTemplateFunctionFromArgumentTypes = 199;
	int ArrayDimensionExceedsMax = 200;
	int AStructIsNotAValidInitializerFor = 201;
	int CannotUseArrayToInitialize = 202;
	int CircularReferenceTo = 203;
	int ParameterIsAlreadyDefined = 204;
	int MemberIsNotAccessible = 205;
	int SymbolIsNotAccessible = 206;
	int ThisForSymbolNeedsToBeType = 207;
	int SymbolHasForwardReferences = 208;
	int CannotHaveAssociativeArrayOfKey = 209;
	int CannotHaveAssociativeArrayOf = 210;
	int CannotHaveArrayOfAuto = 211;
	int EnclosingLabelForBreakNotFound = 212;
	int EnclosingLabelForContinueNotFound = 213;
	int CannotAppendTypeToType = 214;
	int CannotAppendToStaticArray = 215;
	int ExpressionIsVoidAndHasNoValue = 216;
	int NeedMemberFunctionOpCmpForSymbolToCompare = 217;
	int CompareNotDefinedForComplexOperands = 218;
	int NeedThisForAddressOfSymbol = 219;
	int RecursiveMixinInstantiation = 220;
	int SymbolIsNotOfIntegralType = 221;
	int DeleteAAKeyDeprecated = 222;
	int SymbolIsDeprecated = 223;
	int ShadowingDeclarationIsDeprecated = 224;
	int ReturnStatementsCannotBeInFinallyScopeExitOrScopeSuccessBodies = 225;
	int CannotReturnExpressionFromConstructor = 226;
	int CaseNotFound = 227;
	int CircularInheritanceOfInterface = 228;
	int ArgumentToMixinMustBeString = 229;
	int CannotAccessFrameOfFunction = 230;
	int OperationNotAllowedOnBool = 231;
	int SymbolIsNotAScalar = 232;
	int ImportCannotBeResolved = 233;
	int SymbolIsNotAVariable = 234;
	int CatchHidesCatch = 235;
	int ArithmeticOrStringTypeExpectedForValueParameter = 236;
	int FunctionsCannotReturnAFunction = 237;
	int FunctionsCannotReturnATuple = 238;
	int FunctionsCannotReturnAuto = 239;
	int RecursiveType = 240;
	int VariadicFunctionsWithNonDLinkageMustHaveAtLeastOneParameter = 241;
	int SymbolMustBeAFunction = 242;
	int FunctionExpectedBeforeCall = 243;
	int FunctionExpectedBeforeCallNotSymbolOfType = 244;
	int CircularReferenceOfTypedef = 245;
	int StringSliceIsOutOfBounds = 246;
	int ErrorInstantiating = 247;
	int CaseMustBeAnIntegralOrStringConstant = 248;
	int DuplicateCaseInSwitchStatement = 249;
	int SpecialMemberFunctionsNotAllowedForSymbol = 250;
	int SpecialFunctionsNotAllowedInInterface = 251;
	int FunctionBodyIsNotAbstractInInterface = 252;
	int SuperClassConstructorCallMustBeInAConstructor = 253;
	int ClassConstructorCallMustBeInAConstructor = 254;
	int NoSuperClassConstructor = 255;
	int ConstructorCallsNotAllowedInLoopsOrAfterLabels = 256;
	int MultipleConstructorCalls = 257;
	int ExpressionIsNotConstantOrDoesNotEvaluateToABool = 258;
	int StaticIfConditionalCannotBeAtGlobalScope = 259;
	int CannotBreakOutOfFinallyBlock = 260;
	int LabelHasNoBreak = 261;
	int LabelHasNoContinue = 262;
	int CannotGotoInOrOutOfFinallyBlock = 263;
	int CalledWithArgumentTypesMatchesBoth = 264;
	int SymbolIsNotAnArithmeticType = 265;
	int CannotPerformModuloComplexArithmetic = 266;
	int OperatorNotAllowedOnBoolExpression = 267;
	int ForeachKeyTypeMustBeIntOrUint = 268;
	int ForeachKeyCannotBeOutOrRef = 269;
	int NoReverseIterationOnAssociativeArrays = 270;
	int OnlyOneOrTwoArgumentsForAssociativeArrayForeach = 271;
	int OnlyOneOrTwoArgumentsForArrayForeach = 272;
	int ForeachTargetIsNotAnArrayOf = 273;
	int ForeachValueCannotBeOutAndTypeBit = 274;
	int ForeachKeyCannotBeInout = 275;
	int ForeachValueOfUTFConversionCannotBeInout = 276;
	int CannotInferTypeForSymbol = 277;
	int CannotInferTypeFromInitializer = 278;
	int NoStorageClassForSymbol = 279;
	int OnlyOneValueOrTwoKeyValueArgumentsForTupleForeach = 280;
	int CannotUniquelyInferForeachArgumentTypes = 281;
	int InvalidForeachAggregate = 282;
	int NotAnAssociativeArrayInitializer = 283;
	int ArrayInitializersAsExpressionsNotAllowed = 284;
	int IftypeConditionCannotBeAtGlobalScope = 285;
	int SymbolIsNotAFieldOfSymbol = 286;
	int RecursiveTemplateExpansion = 287;
	int RecursiveTemplateExpansionForTemplateArgument = 288;
	int IndexIsNotATypeOrExpression = 289;
	int CannotHavePointerToSymbol = 290;
	int SizeOfTypeIsNotKnown = 291;
	int CanOnlySliceTupleTypes = 292;
	int NoPropertyForTuple = 293;
	int CannotResolveDotProperty = 294;
	int CannotTakeAddressOfBitInArray = 295;
	int OnlyOneIndexAllowedToIndex = 296;
	int NoOpIndexOperatorOverloadForType = 297;
	int ArrayDimensionOverflow = 298;
	int OperatorAssignmentOverloadWithOpIndexIllegal = 299;
	int CannotHaveOutOrInoutArgumentOfBitInArray = 300;
	int SymbolIsAliasedToAFunction = 301;
	int LinkageDoesNotMatchInterfaceFunction = 302;
	int InterfaceFunctionIsNotImplemented = 303;
	int ExpectedKeyAsArgumentToRemove = 304;
	int CyclicConstructorCall = 305;
	int MissingOrCurruptObjectDotD = 306;
	int CannotContinueOutOfFinallyBlock = 307;
	int ForwardDeclaration = 308;
	int CannotFormDelegateDueToCovariantReturnType = 309;
	int ForeachRangeKeyCannotHaveStorageClass = 310;
	int MultipleOverridesOfSameFunction = 311;
	int IdentityAssignmentOperatorOverloadIsIllegal = 312;
	int LiteralsCannotBeClassMembers = 313;
	int NoMatchForImplicitSuperCallInConstructor = 314;
	int NoReturnAtEndOfFunction = 315;
	int CanOnlyDeclareTypeAliasesWithinStaticIfConditionals = 316;
	int PackageAndModuleHaveTheSameName = 317;
	int StringLiteralsAreImmutable = 318;
	int ExpressionDotNewIsOnlyForAllocatingNestedClasses = 319;
	int TooManyArgumentsForArray = 320;
	int ReturnExpressionExpected = 321;
	int ReturnWithoutCallingConstructor = 322;
	int ModuleIsInMultiplePackages = 323;
	int ModuleIsInMultipleDefined = 324;
	int NeedUpperAndLowerBoundToSlicePointer = 325;
	int NeedUpperAndLowerBoundToSliceTuple = 326;
	int CannotConvertStringLiteralToVoidPointer = 327;
	int SymbolIsNotAPreInstanceInitializableField = 328;
	int NoCaseStatementFollowingGoto = 329;
	int SwitchStatementHasNoDefault = 330;
	int SymbolIsNotAFunctionTemplate = 331;
	int TupleIsNotAValidTemplateValueArgument = 332;
	int IncompatibleArgumentsForTemplateInstantiation = 333;
	int ThrowStatementsCannotBeInContracts = 334;
	int CanOnlyThrowClassObjects = 335;
	int StringExpectedAsSecondArgument = 336;
	int WrongNumberOfArguments = 337;
	int StringMustBeChars = 338;
	int InvalidFirstArgument = 339;
	int FirstArgumentIsNotAClass = 340;
	int ArgumentHasNoMembers = 341;
	int SymbolHasNoMembers = 342;
	int KindSymbolHasNoMembers = 343;
	int DotOffsetDeprecated = 344;
	int NoClassInfoForComInterfaceObjects = 345;
	int CannotMakeReferenceToABit = 346;
	int CannotFormTupleOfTuples = 347;
	int MissingInitializerInStaticConstructorForConstVariable = 348;
	int GlobalsStaticsFieldsRefAndAutoParametersCannotBeAuto = 349;
	int ReferenceToScopeClassMustBeScope = 350;
	int NumberOfKeysMustMatchNumberOfValues = 351;
	int ExpectedNumberArguments = 352;
	int ArraySliceIfOutOfBounds = 353;
	int InvalidUCS32Char = 354;
	int TupleIndexExceedsBounds = 355;
	int SliceIsOutOfRange = 356;
	int CannotTakeAddressOf = 357;
	int VariableIsUsedBeforeInitialization = 358;
	int EscapingReferenceToLocal = 359;
	int EscapingReferenceToAutoLocal = 360;
	int EscapingReferenceToLocalVariable = 361;
	int EscapingReferenceToVariadicParameter = 362;
	int CanOnlyCatchClassObjects = 363;
	int BaseClassIsForwardReferenced = 364;
	int BaseIsForwardReferenced = 365;
	int CannotInheritFromFinalClass = 366;
	int StaticClassCannotInheritFromNestedClass = 367;
	int SuperClassIsNestedWithin = 368;
	int ArrayComparisonTypeMismatch = 369;
	int ConditionalExpressionIsNotAModifiableLvalue = 370;
	int CannotCastSymbolToSymbol = 371;
	int CannotDeleteInstanceOfComInterface = 372;
	int TemplateIsNotAMemberOf = 373;
	int TemplateIdentifierIsNotAMemberOf = 374;
	int CanOnlyInitiailizeConstMemberInsideConstructor = 375;
	int SymbolIsNotAMember = 376;
	int SymbolIsNotATemplate = 377;
	int DSymbolHasNoSize = 378;
	int ExpressionOfTypeDoesNotHaveABooleanValue = 379;
	int ImplicitConversionCanCauseLossOfData = 380;
	int ForwardReferenceToType = 381;
	int FloatingPointConstantExpressionExpected = 382;
	int ExpressionIsNotAValidTemplateValueArgument = 383;
	int InvalidRangeLowerBound = 384;
	int InvalidRangeUpperBound = 385;
	int SymbolIsNotAScalarType = 386;
	int ForeachIndexMustBeType = 387;
	int ForeachValueMustBeType = 388;
	int OpApplyFunctionMustReturnAnInt = 389;
	int FunctionOfTypeOverridesButIsNotCovariant = 390;
	int CannotOverrideFinalFunction = 391;
	int IncompatibleCovariantTypes = 392;
	int CannotUseTemplateToAddVirtualFunctionToClass = 393;
	int OutResultIsAlreadyDefined = 394;
	int MissingInitializerForConstField = 395;
	int ImportNotFound = 396;
	int SymbolMustBeAnArrayOfPointerType = 397;
	int RvalueOfInExpressionMustBeAnAssociativeArray = 398;
	int InterfaceInheritsFromDuplicateInterface = 399;
	int LabelIsAlreadyDefined = 400;
	int CannotSubtractPointerFromSymbol = 401;
	int ThisForNestedClassMustBeAClassType = 402;
	int CanOnlyDereferenceAPointer = 403;
	int OuterClassThisNeededToNewNestedClass = 404;
	int ThisForNestedClassMustBeOfType = 405;
	int NoConstructorForSymbol = 406;
	int NoAllocatorForSymbol = 407;
	int NegativeArrayIndex = 408;
	int NewCanOnlyCreateStructsDynamicArraysAndClassObjects = 409;
	int MismatchedFunctionReturnTypeInference = 410;
	int ShiftLeftExceeds = 411;
	int SymbolCannotBeSlicedWithBrackets = 412;
	int SliceExpressionIsNotAModifiableLvalue = 413;
	int SymbolIsNotAMemberOf = 414;
	int MoreInitiailizersThanFields = 415;
	int OverlappingInitiailization = 416;
	int CannotMakeExpressionOutOfInitializer = 417;
	int NoDefaultOrCaseInSwitchStatement = 418;
	int SymbolIsNotASymbol = 419;
	int ForwardReferenceToTemplate = 420;
	int ForwardReferenceToTemplateDeclaration = 421;
	int SpecializationNotAllowedForDeducedParameter = 422;
	int CannotDeclareTemplateAtFunctionScope = 423;
	int TemplateHasNoValue = 424;
	int InconsistentNestingLevels = 425;
	int CannotUseLocalAsTemplateParameter = 426;
	int NoSizeForType = 427;
	int SymbolDotSymbolIsNotADeclaration = 428;
	int ThisIsRequiredButIsNotABaseClassOf = 429;
	int ForwardReferenceToSymbol = 430;
	int IdentifierOfSymbolIsNotDefined = 431;
	int StructIsForwardReferenced = 432;
	int CannotUseTemplateToAddFieldToAggregate = 433;
	int CannotModifyFinalVariable = 434;
	int InvalidUtf8Sequence2 = 435;
	int Utf16HighValuePastEndOfString = 436;
	int Utf16LowValueOutOfRange = 437;
	int UnpairedUtf16Value = 438;
	int IllegalUtf16Value = 439;

}
