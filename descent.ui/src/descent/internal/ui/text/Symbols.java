/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.internal.ui.text;

/**
 * Symbols for the heuristic java scanner.
 *
 * @since 3.0
 */
public interface Symbols {
	int TokenEOF= -1;
	int TokenLBRACE= 1;
	int TokenRBRACE= 2;
	int TokenLBRACKET= 3;
	int TokenRBRACKET= 4;
	int TokenLPAREN= 5;
	int TokenRPAREN= 6;
	int TokenSEMICOLON= 7;
	int TokenOTHER= 8;
	int TokenCOLON= 9;
	int TokenQUESTIONMARK= 10;
	int TokenCOMMA= 11;
	int TokenEQUAL= 12;
	int TokenLESSTHAN= 13;
	int TokenGREATERTHAN= 14;
	int TokenIF= 109;
	int TokenDO= 1010;
	int TokenFOR= 1011;
	int TokenTRY= 1012;
	int TokenCASE= 1013;
	int TokenELSE= 1014;
	int TokenBREAK= 1015;
	int TokenCATCH= 1016;
	int TokenWHILE= 1017;
	int TokenRETURN= 1018;
	int TokenSWITCH= 1020;
	int TokenFINALLY= 1021;
	int TokenGOTO= 1023;
	int TokenDEFAULT= 1024;
	int TokenNEW= 1025;
	int TokenCLASS= 1026;
	int TokenINTERFACE= 1027;
	int TokenENUM= 1028;
	int TokenIDENT= 2000;
	int TokenPRIVATE = 3001; 
	int TokenPACKAGE = 3002;
	int TokenPROTECTED = 3003;
	int TokenPUBLIC = 3004;
	int TokenEXPORT = 3005;
	int TokenSTATIC= 1019;
	int TokenFINAL = 3006;
	int TokenABSTRACT = 3007;
	int TokenOVERRIDE = 3008;
	int TokenAUTO = 3009;
	int TokenSYNCHRONIZED= 1022;
	int TokenDEPRECATED = 3011;
	int TokenEXTERN = 3012;
	int TokenCONST = 3013;
	int TokenSCOPE = 3014;
	int TokenINVARIANT = 3015;
}
