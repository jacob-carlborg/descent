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
	   60 is the first semantic error in the list. */
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
	int ValueIsLargerThanAByte = 55;
	int UnterminatedTokenStringConstant = 56;
	int HeredocRestOfLineShouldBeBlank = 57;
	int IdentifierExpectedForHeredoc = 58;
	int DelimitedStringMustEndInValue = 59;
	int SymbolConflictsWithSymbolAtLocation = 60;
	int SymbolAtLocationConflictsWithSymbolAtLocation = 61;
	int PropertyCanNotBeRedefined = 62;
	int CircularDefinition = 63;
	int EnumValueOverflow = 64;
	int EnumMustHaveAtLeastOneMember = 65;
	int EnumBaseTypeMustBeOfIntegralType = 66;
	int ForwardReferenceOfSymbol = 67;
	int ForwardReferenceOfSymbolDotSymbol = 68;
	int ForwardReferenceWhenLookingFor = 69;
	int BaseEnumIsForwardReference = 70;
	int CannotResolveForwardReference = 71;
	int EnumIsForwardReference = 72;
	int IntegerConstantExpressionExpected = 73;
	int ThisNotInClassOrStruct = 74;
	int ThisOnlyAllowedInNonStaticMemberFunctions = 75;
	int SuperOnlyAllowedInNonStaticMemberFunctions = 76;
	int SuperNotInClass = 77;
	int ClassHasNoSuper = 78;
	int BaseTypeMustBeInterface = 79;
	int MemberIsPrivate = 80;
	int UsedAsAType = 81;
	int ExternSymbolsCannotHaveInitializers = 82;
	int VoidsHaveNoValue = 83;
	int CannotInferTypeFromThisArrayInitializer = 84;
	int NoDefinition = 85;
	int DuplicatedInterfaceInheritance = 86;
	int BaseTypeMustBeClassOrInterface = 87;
	int FieldsNotAllowedInInterfaces = 88;
	int UndefinedIdentifier = 89;
	int NotAMember = 90;
	int NewAllocatorsOnlyForClassOrStruct = 91;
	int DeleteDeallocatorsOnlyForClassOrStruct = 92;
	int ConstructorsOnlyForClass = 93;
	int DestructorsOnlyForClass = 94;
	int InvariantsOnlyForClassStructUnion = 95;
	int FunctionDoesNotOverrideAny = 96;
	int CannotOverrideFinalFunctions = 97;
	int OverrideOnlyForClassMemberFunctions = 98;
	int FunctionMustReturnAResultOfType = 99;
	int MoreThanOneInvariant = 100;
	int ParameterMultiplyDefined = 101;
	int SymbolNotFound = 102;
	int StatementIsNotReachable = 103;
	int VoidFunctionsHaveNoResult = 104;
	int ReturnStatementsCannotBeInContracts = 105;
	int NotAnAggregateType = 106;
	int UnrecognizedPragma = 107;
	int AnonCanOnlyBePartOfAnAggregate = 108;
	int PragmaIsMissingClosingSemicolon = 109;
	int CannotImplicitlyConvert = 110;
	int ForbiddenReference = 111;
	int DiscouragedReference = 112;
	int Task = 113;
	int UndefinedType = 114;
	int IsClassPathCorrect = 115;
	int FunctionsCannotBeConstOrAuto = 116;
	int NonVirtualFunctionsCannotBeAbstract = 117;
	int ModifierCannotBeAppliedToVariables = 118;
	int StructsCannotBeAbstract = 119;
	int UnionsCannotBeAbstract = 120;
	int AliasCannotBeConst = 121;
	int OneArgumentOfTypeExpected = 122;
	int IllegalMainParameters = 123;
	int MustReturnIntOrVoidFromMainFunction = 124;
	int AtLeastOneArgumentOfTypeExpected = 125;
	int FirstArgumentMustBeOfType = 126;
	int StringExpectedForPragmaMsg = 127;
	int LibPragmaMustRecieveASingleArgumentOfTypeString = 128;
	int StringExpectedForPragmaLib = 129;
	int CannotHaveOutOrInoutParameterOfTypeStaticArray = 130;
	int CannotHaveParameterOfTypeVoid = 131;
	int FunctionsCannotReturnStaticArrays = 132;
	int UnrecongnizedTrait = 133;
	int CanOnlyConcatenateArrays = 134;
	int ArrayIndexOutOfBounds = 135;
	int ArrayIndexOutOfBounds2 = 136;
	int AssertionFailed = 137;
	int AssertionFailedNoMessage = 138;
	int ExpressionIsNotEvaluatableAtCompileTime = 139;
	int UndefinedProperty = 140;
	int DeprecatedProperty = 141;
	int FileNameMustBeString = 142;
	int FileImportsMustBeSpecified = 143;
	int FileNotFound = 144;
	int ErrorReadingFile = 145;
	int ExpressionHasNoEffect = 146;
	int ConstantIsNotAnLValue = 147;
	int VersionIdentifierReserved = 148;
	int CannotPutCatchStatementInsideFinallyBlock = 149;
	int ExpressionDoesNotGiveABooleanResult = 150;
	int BreakIsNotInsideALoopOrSwitch = 151;
	int CaseIsNotInSwitch = 152;
	int VersionDeclarationMustBeAtModuleLevel = 153;
	int DebugDeclarationMustBeAtModuleLevel = 154;
	int GotoCaseNotInSwitch = 155;
	int GotoDefaultNotInSwitch = 156;
	int LazyVariablesCannotBeLvalues = 157;
	int DivisionByZero = 158;
	int DefaultNotInSwitch = 159;
	int SwitchAlreadyHasDefault = 160;
	int ContinueNotInLoop = 161;
	int ForeachIndexCannotBeRef = 162;
	int ParametersDoesNotMatchParameterTypes = 163;
	int IncompatibleParameterStorageClass = 164;
	int OutCannotBeFinal = 165;
	int ScopeCannotBeRefOrOut = 166;
	int IncompatibleTypesForOperator = 167;
	int IncompatibleTypesForMinus = 168;
	int SymbolNotDefined = 169;
	int SymbolNotATemplate = 170;
	int CannotDeleteType = 171;
	int NotAnLvalue = 172;
	int CannotAliasAnExpression = 173;
	int CannotAssignToStaticArray = 174;
	int CannotChangeReferenceToStaticArray = 175;
	int CannotModifyParameterInContract = 176;
	int BothOverloadsMuchArgumentList = 177;
	int ExpressionHasNoType = 178;
	int SymbolNotAnExpression = 179;
	int SymbolHasNoValue = 180;
	int TooManyInitializers = 181;
	int SymbolNotAStaticAndCannotHaveStaticInitializer = 182;
	int SymbolNotAType = 183;
	int IncompleteMixinDeclaration = 184;
	int SymbolNotATemplateItIs = 185;
	int SymbolCannotBeDeclaredToBeAFunction = 186;
	int CannotHaveArrayOfType = 187;
	int SymbolDoesNotMatchAnyTemplateDeclaration = 188;
	int IndexOverflowForStaticArray = 189;
	int UnknownSize = 190;
	int NoSizeYetForForwardReference = 191;
	int SymbolMatchesMoreThanOneTemplateDeclaration = 192;
	int ExpressionLeadsToStackOverflowAtCompileTime = 193;
	int StringIndexOutOfBounds = 194;
	int CannotCreateInstanceOfAbstractClass = 195;
	int CannotCreateInstanceOfInterface = 196;
	int WithExpressionsMustBeClassObject = 197;
	int DeclarationIsAlreadyDefined = 198;
	int DeclarationIsAlreadyDefinedInAnotherScope = 199;
	int VersionDefinedAfterUse = 200;
	int DebugDefinedAfterUse = 201;
	int NotEnoughArguments = 202;
	int CanOnlySynchronizeOnClassObjects = 203;
	int CannotDeduceTemplateFunctionFromArgumentTypes = 204;
	int ArrayDimensionExceedsMax = 205;
	int AStructIsNotAValidInitializerFor = 206;
	int CannotUseArrayToInitialize = 207;
	int CircularReferenceTo = 208;
	int ParameterIsAlreadyDefined = 209;
	int MemberIsNotAccessible = 210;
	int SymbolIsNotAccessible = 211;
	int ThisForSymbolNeedsToBeType = 212;
	int SymbolHasForwardReferences = 213;
	int CannotHaveAssociativeArrayOfKey = 214;
	int CannotHaveAssociativeArrayOf = 215;
	int CannotHaveArrayOfAuto = 216;
	int EnclosingLabelForBreakNotFound = 217;
	int EnclosingLabelForContinueNotFound = 218;
	int CannotAppendTypeToType = 219;
	int CannotAppendToStaticArray = 220;
	int ExpressionIsVoidAndHasNoValue = 221;
	int NeedMemberFunctionOpCmpForSymbolToCompare = 222;
	int CompareNotDefinedForComplexOperands = 223;
	int NeedThisForAddressOfSymbol = 224;
	int RecursiveMixinInstantiation = 225;
	int SymbolIsNotOfIntegralType = 226;
	int DeleteAAKeyDeprecated = 227;
	int SymbolIsDeprecated = 228;
	int ShadowingDeclarationIsDeprecated = 229;
	int ReturnStatementsCannotBeInFinallyScopeExitOrScopeSuccessBodies = 230;
	int CannotReturnExpressionFromConstructor = 231;
	int CaseNotFound = 232;
	int CircularInheritanceOfInterface = 233;
	int ArgumentToMixinMustBeString = 234;
	int CannotAccessFrameOfFunction = 235;
	int OperationNotAllowedOnBool = 236;
	int SymbolIsNotAScalar = 237;
	int ImportCannotBeResolved = 238;
	int SymbolIsNotAVariable = 239;
	int CatchHidesCatch = 240;
	int ArithmeticOrStringTypeExpectedForValueParameter = 241;
	int FunctionsCannotReturnAFunction = 242;
	int FunctionsCannotReturnATuple = 243;
	int FunctionsCannotReturnAuto = 244;
	int RecursiveType = 245;
	int VariadicFunctionsWithNonDLinkageMustHaveAtLeastOneParameter = 246;
	int SymbolMustBeAFunction = 247;
	int FunctionExpectedBeforeCall = 248;
	int FunctionExpectedBeforeCallNotSymbolOfType = 249;
	int CircularReferenceOfTypedef = 250;
	int StringSliceIsOutOfBounds = 251;
	int ErrorInstantiating = 252;
	int CaseMustBeAnIntegralOrStringConstant = 253;
	int DuplicateCaseInSwitchStatement = 254;
	int SpecialMemberFunctionsNotAllowedForSymbol = 255;
	int SpecialFunctionsNotAllowedInInterface = 256;
	int FunctionBodyIsNotAbstractInInterface = 257;
	int SuperClassConstructorCallMustBeInAConstructor = 258;
	int ClassConstructorCallMustBeInAConstructor = 259;
	int NoSuperClassConstructor = 260;
	int ConstructorCallsNotAllowedInLoopsOrAfterLabels = 261;
	int MultipleConstructorCalls = 262;
	int ExpressionIsNotConstantOrDoesNotEvaluateToABool = 263;
	int StaticIfConditionalCannotBeAtGlobalScope = 264;
	int CannotBreakOutOfFinallyBlock = 265;
	int LabelHasNoBreak = 266;
	int LabelHasNoContinue = 267;
	int CannotGotoInOrOutOfFinallyBlock = 268;
	int CalledWithArgumentTypesMatchesBoth = 269;
	int SymbolIsNotAnArithmeticType = 270;
	int CannotPerformModuloComplexArithmetic = 271;
	int OperatorNotAllowedOnBoolExpression = 272;
	int ForeachKeyTypeMustBeIntOrUint = 273;
	int ForeachKeyCannotBeOutOrRef = 274;
	int NoReverseIterationOnAssociativeArrays = 275;
	int OnlyOneOrTwoArgumentsForAssociativeArrayForeach = 276;
	int OnlyOneOrTwoArgumentsForArrayForeach = 277;
	int ForeachTargetIsNotAnArrayOf = 278;
	int ForeachValueCannotBeOutAndTypeBit = 279;
	int ForeachKeyCannotBeInout = 280;
	int ForeachValueOfUTFConversionCannotBeInout = 281;
	int CannotInferTypeForSymbol = 282;
	int CannotInferTypeFromInitializer = 283;
	int NoStorageClassForSymbol = 284;
	int OnlyOneValueOrTwoKeyValueArgumentsForTupleForeach = 285;
	int CannotUniquelyInferForeachArgumentTypes = 286;
	int InvalidForeachAggregate = 287;
	int NotAnAssociativeArrayInitializer = 288;
	int ArrayInitializersAsExpressionsNotAllowed = 289;
	int IftypeConditionCannotBeAtGlobalScope = 290;
	int SymbolIsNotAFieldOfSymbol = 291;
	int RecursiveTemplateExpansion = 292;
	int RecursiveTemplateExpansionForTemplateArgument = 293;
	int IndexIsNotATypeOrExpression = 294;
	int CannotHavePointerToSymbol = 295;
	int SizeOfTypeIsNotKnown = 296;
	int CanOnlySliceTupleTypes = 297;
	int NoPropertyForTuple = 298;
	int CannotResolveDotProperty = 299;
	int CannotTakeAddressOfBitInArray = 300;
	int OnlyOneIndexAllowedToIndex = 301;
	int NoOpIndexOperatorOverloadForType = 302;
	int ArrayDimensionOverflow = 303;
	int OperatorAssignmentOverloadWithOpIndexIllegal = 304;
	int CannotHaveOutOrInoutArgumentOfBitInArray = 305;
	int SymbolIsAliasedToAFunction = 306;
	int LinkageDoesNotMatchInterfaceFunction = 307;
	int InterfaceFunctionIsNotImplemented = 308;
	int ExpectedKeyAsArgumentToRemove = 309;
	int CyclicConstructorCall = 310;
	int MissingOrCurruptObjectDotD = 311;
	int CannotContinueOutOfFinallyBlock = 312;
	int ForwardDeclaration = 313;
	int CannotFormDelegateDueToCovariantReturnType = 314;
	int ForeachRangeKeyCannotHaveStorageClass = 315;
	int MultipleOverridesOfSameFunction = 316;
	int IdentityAssignmentOperatorOverloadIsIllegal = 317;
	int LiteralsCannotBeClassMembers = 318;
	int NoMatchForImplicitSuperCallInConstructor = 319;
	int NoReturnAtEndOfFunction = 320;
	int CanOnlyDeclareTypeAliasesWithinStaticIfConditionals = 321;
	int PackageAndModuleHaveTheSameName = 322;
	int StringLiteralsAreImmutable = 323;
	int ExpressionDotNewIsOnlyForAllocatingNestedClasses = 324;
	int TooManyArgumentsForArray = 325;
	int ReturnExpressionExpected = 326;
	int ReturnWithoutCallingConstructor = 327;
	int ModuleIsInMultiplePackages = 328;
	int ModuleIsInMultipleDefined = 329;
	int NeedUpperAndLowerBoundToSlicePointer = 330;
	int NeedUpperAndLowerBoundToSliceTuple = 331;
	int CannotConvertStringLiteralToVoidPointer = 332;
	int SymbolIsNotAPreInstanceInitializableField = 333;
	int NoCaseStatementFollowingGoto = 334;
	int SwitchStatementHasNoDefault = 335;
	int SymbolIsNotAFunctionTemplate = 336;
	int TupleIsNotAValidTemplateValueArgument = 337;
	int IncompatibleArgumentsForTemplateInstantiation = 338;
	int ThrowStatementsCannotBeInContracts = 339;
	int CanOnlyThrowClassObjects = 340;
	int StringExpectedAsSecondArgument = 341;
	int WrongNumberOfArguments = 342;
	int StringMustBeChars = 343;
	int InvalidFirstArgument = 344;
	int FirstArgumentIsNotAClass = 345;
	int ArgumentHasNoMembers = 346;
	int SymbolHasNoMembers = 347;
	int KindSymbolHasNoMembers = 348;
	int DotOffsetDeprecated = 349;
	int NoClassInfoForComInterfaceObjects = 350;
	int CannotMakeReferenceToABit = 351;
	int CannotFormTupleOfTuples = 352;
	int MissingInitializerInStaticConstructorForConstVariable = 353;
	int GlobalsStaticsFieldsRefAndAutoParametersCannotBeAuto = 354;
	int ReferenceToScopeClassMustBeScope = 355;
	int NumberOfKeysMustMatchNumberOfValues = 356;
	int ExpectedNumberArguments = 357;
	int ArraySliceIfOutOfBounds = 358;
	int InvalidUCS32Char = 359;
	int TupleIndexExceedsBounds = 360;
	int SliceIsOutOfRange = 361;
	int CannotTakeAddressOf = 362;
	int VariableIsUsedBeforeInitialization = 363;
	int EscapingReferenceToLocal = 364;
	int EscapingReferenceToAutoLocal = 365;
	int EscapingReferenceToLocalVariable = 366;
	int EscapingReferenceToVariadicParameter = 367;
	int CanOnlyCatchClassObjects = 368;
	int BaseClassIsForwardReferenced = 369;
	int BaseIsForwardReferenced = 370;
	int CannotInheritFromFinalClass = 371;
	int StaticClassCannotInheritFromNestedClass = 372;
	int SuperClassIsNestedWithin = 373;
	int ArrayComparisonTypeMismatch = 374;
	int ConditionalExpressionIsNotAModifiableLvalue = 375;
	int CannotCastSymbolToSymbol = 376;
	int CannotDeleteInstanceOfComInterface = 377;
	int TemplateIsNotAMemberOf = 378;
	int TemplateIdentifierIsNotAMemberOf = 379;
	int CanOnlyInitiailizeConstMemberInsideConstructor = 380;
	int SymbolIsNotAMember = 381;
	int SymbolIsNotATemplate = 382;
	int DSymbolHasNoSize = 383;
	int ExpressionOfTypeDoesNotHaveABooleanValue = 384;
	int ImplicitConversionCanCauseLossOfData = 385;
	int ForwardReferenceToType = 386;
	int FloatingPointConstantExpressionExpected = 387;
	int ExpressionIsNotAValidTemplateValueArgument = 388;
	int InvalidRangeLowerBound = 389;
	int InvalidRangeUpperBound = 390;
	int SymbolIsNotAScalarType = 391;
	int ForeachIndexMustBeType = 392;
	int ForeachValueMustBeType = 393;
	int OpApplyFunctionMustReturnAnInt = 394;
	int FunctionOfTypeOverridesButIsNotCovariant = 395;
	int CannotOverrideFinalFunction = 396;
	int IncompatibleCovariantTypes = 397;
	int CannotUseTemplateToAddVirtualFunctionToClass = 398;
	int OutResultIsAlreadyDefined = 399;
	int MissingInitializerForConstField = 400;
	int ImportNotFound = 401;
	int SymbolMustBeAnArrayOfPointerType = 402;
	int RvalueOfInExpressionMustBeAnAssociativeArray = 403;
	int InterfaceInheritsFromDuplicateInterface = 404;
	int LabelIsAlreadyDefined = 405;
	int CannotSubtractPointerFromSymbol = 406;
	int ThisForNestedClassMustBeAClassType = 407;
	int CanOnlyDereferenceAPointer = 408;
	int OuterClassThisNeededToNewNestedClass = 409;
	int ThisForNestedClassMustBeOfType = 410;
	int NoConstructorForSymbol = 411;
	int NoAllocatorForSymbol = 412;
	int NegativeArrayIndex = 413;
	int NewCanOnlyCreateStructsDynamicArraysAndClassObjects = 414;
	int MismatchedFunctionReturnTypeInference = 415;
	int ShiftLeftExceeds = 416;
	int SymbolCannotBeSlicedWithBrackets = 417;
	int SliceExpressionIsNotAModifiableLvalue = 418;
	int SymbolIsNotAMemberOf = 419;
	int MoreInitiailizersThanFields = 420;
	int OverlappingInitiailization = 421;
	int CannotMakeExpressionOutOfInitializer = 422;
	int NoDefaultOrCaseInSwitchStatement = 423;
	int SymbolIsNotASymbol = 424;
	int ForwardReferenceToTemplate = 425;
	int ForwardReferenceToTemplateDeclaration = 426;
	int SpecializationNotAllowedForDeducedParameter = 427;
	int CannotDeclareTemplateAtFunctionScope = 428;
	int TemplateHasNoValue = 429;
	int InconsistentNestingLevels = 430;
	int CannotUseLocalAsTemplateParameter = 431;
	int NoSizeForType = 432;
	int SymbolDotSymbolIsNotADeclaration = 433;
	int ThisIsRequiredButIsNotABaseClassOf = 434;
	int ForwardReferenceToSymbol = 435;
	int IdentifierOfSymbolIsNotDefined = 436;
	int StructIsForwardReferenced = 437;
	int CannotUseTemplateToAddFieldToAggregate = 438;
	int CannotModifyFinalVariable = 439;
	int InvalidUtf8Sequence2 = 440;
	int Utf16HighValuePastEndOfString = 441;
	int Utf16LowValueOutOfRange = 442;
	int UnpairedUtf16Value = 443;
	int IllegalUtf16Value = 444;

}
