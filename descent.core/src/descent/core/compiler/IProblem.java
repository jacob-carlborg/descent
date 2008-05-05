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
	int CannotBeBothAbstractAndFinal = 118;
	int ModifierCannotBeAppliedToVariables = 119;
	int StructsCannotBeAbstract = 120;
	int UnionsCannotBeAbstract = 121;
	int AliasCannotBeConst = 122;
	int OneArgumentOfTypeExpected = 123;
	int IllegalMainParameters = 124;
	int MustReturnIntOrVoidFromMainFunction = 125;
	int AtLeastOneArgumentOfTypeExpected = 126;
	int FirstArgumentMustBeOfType = 127;
	int StringExpectedForPragmaMsg = 128;
	int LibPragmaMustRecieveASingleArgumentOfTypeString = 129;
	int StringExpectedForPragmaLib = 130;
	int CannotHaveOutOrInoutParameterOfTypeStaticArray = 131;
	int CannotHaveParameterOfTypeVoid = 132;
	int FunctionsCannotReturnStaticArrays = 133;
	int UnrecongnizedTrait = 134;
	int CanOnlyConcatenateArrays = 135;
	int ArrayIndexOutOfBounds = 136;
	int ArrayIndexOutOfBounds2 = 137;
	int AssertionFailed = 138;
	int AssertionFailedNoMessage = 139;
	int ExpressionIsNotEvaluatableAtCompileTime = 140;
	int UndefinedProperty = 141;
	int DeprecatedProperty = 142;
	int FileNameMustBeString = 143;
	int FileImportsMustBeSpecified = 144;
	int FileNotFound = 145;
	int ErrorReadingFile = 146;
	int ExpressionHasNoEffect = 147;
	int ConstantIsNotAnLValue = 148;
	int VersionIdentifierReserved = 149;
	int CannotPutCatchStatementInsideFinallyBlock = 150;
	int ExpressionDoesNotGiveABooleanResult = 151;
	int BreakIsNotInsideALoopOrSwitch = 152;
	int CaseIsNotInSwitch = 153;
	int VersionDeclarationMustBeAtModuleLevel = 154;
	int DebugDeclarationMustBeAtModuleLevel = 155;
	int GotoCaseNotInSwitch = 156;
	int GotoDefaultNotInSwitch = 157;
	int LazyVariablesCannotBeLvalues = 158;
	int DivisionByZero = 159;
	int DefaultNotInSwitch = 160;
	int SwitchAlreadyHasDefault = 161;
	int ContinueNotInLoop = 162;
	int ForeachIndexCannotBeRef = 163;
	int ParametersDoesNotMatchParameterTypes = 164;
	int IncompatibleParameterStorageClass = 165;
	int OutCannotBeFinal = 166;
	int ScopeCannotBeRefOrOut = 167;
	int IncompatibleTypesForOperator = 168;
	int IncompatibleTypesForMinus = 169;
	int SymbolNotDefined = 170;
	int SymbolNotATemplate = 171;
	int CannotDeleteType = 172;
	int NotAnLvalue = 173;
	int CannotAliasAnExpression = 174;
	int CannotAssignToStaticArray = 175;
	int CannotChangeReferenceToStaticArray = 176;
	int CannotModifyParameterInContract = 177;
	int BothOverloadsMuchArgumentList = 178;
	int ExpressionHasNoType = 179;
	int SymbolNotAnExpression = 180;
	int SymbolHasNoValue = 181;
	int TooManyInitializers = 182;
	int SymbolNotAStaticAndCannotHaveStaticInitializer = 183;
	int SymbolNotAType = 184;
	int IncompleteMixinDeclaration = 185;
	int SymbolNotATemplateItIs = 186;
	int SymbolCannotBeDeclaredToBeAFunction = 187;
	int CannotHaveArrayOfType = 188;
	int SymbolDoesNotMatchAnyTemplateDeclaration = 189;
	int IndexOverflowForStaticArray = 190;
	int UnknownSize = 191;
	int NoSizeYetForForwardReference = 192;
	int SymbolMatchesMoreThanOneTemplateDeclaration = 193;
	int ExpressionLeadsToStackOverflowAtCompileTime = 194;
	int StringIndexOutOfBounds = 195;
	int CannotCreateInstanceOfAbstractClass = 196;
	int CannotCreateInstanceOfInterface = 197;
	int WithExpressionsMustBeClassObject = 198;
	int DeclarationIsAlreadyDefined = 199;
	int DeclarationIsAlreadyDefinedInAnotherScope = 200;
	int VersionDefinedAfterUse = 201;
	int DebugDefinedAfterUse = 202;
	int NotEnoughArguments = 203;
	int CanOnlySynchronizeOnClassObjects = 204;
	int CannotDeduceTemplateFunctionFromArgumentTypes = 205;
	int ArrayDimensionExceedsMax = 206;
	int AStructIsNotAValidInitializerFor = 207;
	int CannotUseArrayToInitialize = 208;
	int CircularReferenceTo = 209;
	int ParameterIsAlreadyDefined = 210;
	int MemberIsNotAccessible = 211;
	int SymbolIsNotAccessible = 212;
	int ThisForSymbolNeedsToBeType = 213;
	int SymbolHasForwardReferences = 214;
	int CannotHaveAssociativeArrayOfKey = 215;
	int CannotHaveAssociativeArrayOf = 216;
	int CannotHaveArrayOfAuto = 217;
	int EnclosingLabelForBreakNotFound = 218;
	int EnclosingLabelForContinueNotFound = 219;
	int CannotAppendTypeToType = 220;
	int CannotAppendToStaticArray = 221;
	int ExpressionIsVoidAndHasNoValue = 222;
	int NeedMemberFunctionOpCmpForSymbolToCompare = 223;
	int CompareNotDefinedForComplexOperands = 224;
	int NeedThisForAddressOfSymbol = 225;
	int RecursiveMixinInstantiation = 226;
	int SymbolIsNotOfIntegralType = 227;
	int DeleteAAKeyDeprecated = 228;
	int SymbolIsDeprecated = 229;
	int ShadowingDeclarationIsDeprecated = 230;
	int ReturnStatementsCannotBeInFinallyScopeExitOrScopeSuccessBodies = 231;
	int CannotReturnExpressionFromConstructor = 232;
	int CaseNotFound = 233;
	int CircularInheritanceOfInterface = 234;
	int ArgumentToMixinMustBeString = 235;
	int CannotAccessFrameOfFunction = 236;
	int OperationNotAllowedOnBool = 237;
	int SymbolIsNotAScalar = 238;
	int ImportCannotBeResolved = 239;
	int SymbolIsNotAVariable = 240;
	int CatchHidesCatch = 241;
	int ArithmeticOrStringTypeExpectedForValueParameter = 242;
	int FunctionsCannotReturnAFunction = 243;
	int FunctionsCannotReturnATuple = 244;
	int FunctionsCannotReturnAuto = 245;
	int RecursiveType = 246;
	int VariadicFunctionsWithNonDLinkageMustHaveAtLeastOneParameter = 247;
	int SymbolMustBeAFunction = 248;
	int FunctionExpectedBeforeCall = 249;
	int FunctionExpectedBeforeCallNotSymbolOfType = 250;
	int CircularReferenceOfTypedef = 251;
	int StringSliceIsOutOfBounds = 252;
	int ErrorInstantiating = 253;
	int CaseMustBeAnIntegralOrStringConstant = 254;
	int DuplicateCaseInSwitchStatement = 255;
	int SpecialMemberFunctionsNotAllowedForSymbol = 256;
	int SpecialFunctionsNotAllowedInInterface = 257;
	int FunctionBodyIsNotAbstractInInterface = 258;
	int SuperClassConstructorCallMustBeInAConstructor = 259;
	int ClassConstructorCallMustBeInAConstructor = 260;
	int NoSuperClassConstructor = 261;
	int ConstructorCallsNotAllowedInLoopsOrAfterLabels = 262;
	int MultipleConstructorCalls = 263;
	int ExpressionIsNotConstantOrDoesNotEvaluateToABool = 264;
	int StaticIfConditionalCannotBeAtGlobalScope = 265;
	int CannotBreakOutOfFinallyBlock = 266;
	int LabelHasNoBreak = 267;
	int LabelHasNoContinue = 268;
	int CannotGotoInOrOutOfFinallyBlock = 269;
	int CalledWithArgumentTypesMatchesBoth = 270;
	int SymbolIsNotAnArithmeticType = 271;
	int CannotPerformModuloComplexArithmetic = 272;
	int OperatorNotAllowedOnBoolExpression = 273;
	int ForeachKeyTypeMustBeIntOrUint = 274;
	int ForeachKeyCannotBeOutOrRef = 275;
	int NoReverseIterationOnAssociativeArrays = 276;
	int OnlyOneOrTwoArgumentsForAssociativeArrayForeach = 277;
	int OnlyOneOrTwoArgumentsForArrayForeach = 278;
	int ForeachTargetIsNotAnArrayOf = 279;
	int ForeachValueCannotBeOutAndTypeBit = 280;
	int ForeachKeyCannotBeInout = 281;
	int ForeachValueOfUTFConversionCannotBeInout = 282;
	int CannotInferTypeForSymbol = 283;
	int CannotInferTypeFromInitializer = 284;
	int NoStorageClassForSymbol = 285;
	int OnlyOneValueOrTwoKeyValueArgumentsForTupleForeach = 286;
	int CannotUniquelyInferForeachArgumentTypes = 287;
	int InvalidForeachAggregate = 288;
	int NotAnAssociativeArrayInitializer = 289;
	int ArrayInitializersAsExpressionsNotAllowed = 290;
	int IftypeConditionCannotBeAtGlobalScope = 291;
	int SymbolIsNotAFieldOfSymbol = 292;
	int RecursiveTemplateExpansion = 293;
	int RecursiveTemplateExpansionForTemplateArgument = 294;
	int IndexIsNotATypeOrExpression = 295;
	int CannotHavePointerToSymbol = 296;
	int SizeOfTypeIsNotKnown = 297;
	int CanOnlySliceTupleTypes = 298;
	int NoPropertyForTuple = 299;
	int CannotResolveDotProperty = 300;
	int CannotTakeAddressOfBitInArray = 301;
	int OnlyOneIndexAllowedToIndex = 302;
	int NoOpIndexOperatorOverloadForType = 303;
	int ArrayDimensionOverflow = 304;
	int OperatorAssignmentOverloadWithOpIndexIllegal = 305;
	int CannotHaveOutOrInoutArgumentOfBitInArray = 306;
	int SymbolIsAliasedToAFunction = 307;
	int LinkageDoesNotMatchInterfaceFunction = 308;
	int InterfaceFunctionIsNotImplemented = 309;
	int ExpectedKeyAsArgumentToRemove = 310;
	int CyclicConstructorCall = 311;
	int MissingOrCurruptObjectDotD = 312;
	int CannotContinueOutOfFinallyBlock = 313;
	int ForwardDeclaration = 314;
	int CannotFormDelegateDueToCovariantReturnType = 315;
	int ForeachRangeKeyCannotHaveStorageClass = 316;
	int MultipleOverridesOfSameFunction = 317;
	int IdentityAssignmentOperatorOverloadIsIllegal = 318;
	int LiteralsCannotBeClassMembers = 319;
	int NoMatchForImplicitSuperCallInConstructor = 320;
	int NoReturnAtEndOfFunction = 321;
	int CanOnlyDeclareTypeAliasesWithinStaticIfConditionals = 322;
	int PackageAndModuleHaveTheSameName = 323;
	int StringLiteralsAreImmutable = 324;
	int ExpressionDotNewIsOnlyForAllocatingNestedClasses = 325;
	int TooManyArgumentsForArray = 326;
	int ReturnExpressionExpected = 327;
	int ReturnWithoutCallingConstructor = 328;
	int ModuleIsInMultiplePackages = 329;
	int ModuleIsInMultipleDefined = 330;
	int NeedUpperAndLowerBoundToSlicePointer = 331;
	int NeedUpperAndLowerBoundToSliceTuple = 332;
	int CannotConvertStringLiteralToVoidPointer = 333;
	int SymbolIsNotAPreInstanceInitializableField = 334;
	int NoCaseStatementFollowingGoto = 335;
	int SwitchStatementHasNoDefault = 336;
	int SymbolIsNotAFunctionTemplate = 337;
	int TupleIsNotAValidTemplateValueArgument = 338;
	int IncompatibleArgumentsForTemplateInstantiation = 339;
	int ThrowStatementsCannotBeInContracts = 340;
	int CanOnlyThrowClassObjects = 341;
	int StringExpectedAsSecondArgument = 342;
	int WrongNumberOfArguments = 343;
	int StringMustBeChars = 344;
	int InvalidFirstArgument = 345;
	int FirstArgumentIsNotAClass = 346;
	int ArgumentHasNoMembers = 347;
	int SymbolHasNoMembers = 348;
	int KindSymbolHasNoMembers = 349;
	int DotOffsetDeprecated = 350;
	int NoClassInfoForComInterfaceObjects = 351;
	int CannotMakeReferenceToABit = 352;
	int CannotFormTupleOfTuples = 353;
	int MissingInitializerInStaticConstructorForConstVariable = 354;
	int GlobalsStaticsFieldsRefAndAutoParametersCannotBeAuto = 355;
	int ReferenceToScopeClassMustBeScope = 356;
	int NumberOfKeysMustMatchNumberOfValues = 357;
	int ExpectedNumberArguments = 358;
	int ArraySliceIfOutOfBounds = 359;
	int InvalidUCS32Char = 360;
	int TupleIndexExceedsBounds = 361;
	int SliceIsOutOfRange = 362;
	int CannotTakeAddressOf = 363;
	int VariableIsUsedBeforeInitialization = 364;
	int EscapingReferenceToLocal = 365;
	int EscapingReferenceToAutoLocal = 366;
	int EscapingReferenceToLocalVariable = 367;
	int EscapingReferenceToVariadicParameter = 368;
	int CanOnlyCatchClassObjects = 369;
	int BaseClassIsForwardReferenced = 370;
	int BaseIsForwardReferenced = 371;
	int CannotInheritFromFinalClass = 372;
	int StaticClassCannotInheritFromNestedClass = 373;
	int SuperClassIsNestedWithin = 374;
	int ArrayComparisonTypeMismatch = 375;
	int ConditionalExpressionIsNotAModifiableLvalue = 376;
	int CannotCastSymbolToSymbol = 377;
	int CannotDeleteInstanceOfComInterface = 378;
	int TemplateIsNotAMemberOf = 379;
	int TemplateIdentifierIsNotAMemberOf = 380;
	int CanOnlyInitiailizeConstMemberInsideConstructor = 381;
	int SymbolIsNotAMember = 382;
	int SymbolIsNotATemplate = 383;
	int DSymbolHasNoSize = 384;
	int ExpressionOfTypeDoesNotHaveABooleanValue = 385;
	int ImplicitConversionCanCauseLossOfData = 386;
	int ForwardReferenceToType = 387;
	int FloatingPointConstantExpressionExpected = 388;
	int ExpressionIsNotAValidTemplateValueArgument = 389;
	int InvalidRangeLowerBound = 390;
	int InvalidRangeUpperBound = 391;
	int SymbolIsNotAScalarType = 392;
	int ForeachIndexMustBeType = 393;
	int ForeachValueMustBeType = 394;
	int OpApplyFunctionMustReturnAnInt = 395;
	int FunctionOfTypeOverridesButIsNotCovariant = 396;
	int CannotOverrideFinalFunction = 397;
	int IncompatibleCovariantTypes = 398;
	int CannotUseTemplateToAddVirtualFunctionToClass = 399;
	int OutResultIsAlreadyDefined = 400;
	int MissingInitializerForConstField = 401;
	int ImportNotFound = 402;
	int SymbolMustBeAnArrayOfPointerType = 403;
	int RvalueOfInExpressionMustBeAnAssociativeArray = 404;
	int InterfaceInheritsFromDuplicateInterface = 405;
	int LabelIsAlreadyDefined = 406;
	int CannotSubtractPointerFromSymbol = 407;
	int ThisForNestedClassMustBeAClassType = 408;
	int CanOnlyDereferenceAPointer = 409;
	int OuterClassThisNeededToNewNestedClass = 410;
	int ThisForNestedClassMustBeOfType = 411;
	int NoConstructorForSymbol = 412;
	int NoAllocatorForSymbol = 413;
	int NegativeArrayIndex = 414;
	int NewCanOnlyCreateStructsDynamicArraysAndClassObjects = 415;
	int MismatchedFunctionReturnTypeInference = 416;
	int ShiftLeftExceeds = 417;
	int SymbolCannotBeSlicedWithBrackets = 418;
	int SliceExpressionIsNotAModifiableLvalue = 419;
	int SymbolIsNotAMemberOf = 420;
	int MoreInitiailizersThanFields = 421;
	int OverlappingInitiailization = 422;
	int CannotMakeExpressionOutOfInitializer = 423;
	int NoDefaultOrCaseInSwitchStatement = 424;
	int SymbolIsNotASymbol = 425;
	int ForwardReferenceToTemplate = 426;
	int ForwardReferenceToTemplateDeclaration = 427;
	int SpecializationNotAllowedForDeducedParameter = 428;
	int CannotDeclareTemplateAtFunctionScope = 429;
	int TemplateHasNoValue = 430;
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
	int StaticConstructorCanOnlyBePartOfStructClassModule = 445;
	int ShiftAssignIsOutsideTheRange = 446;
	int TemplateTupleParameterMustBeLastOne = 447;
	int SymbolIsNestedInBoth = 448;

}
