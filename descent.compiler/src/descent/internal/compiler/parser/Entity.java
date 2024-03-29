package descent.internal.compiler.parser;

import descent.core.IProblemRequestor;
import descent.core.compiler.IProblem;


public class Entity {
	
	public static int HtmlNamedEntity(char[] input, int p, int length, int lineNumber, IProblemRequestor requestor) {
		String s = new String(input, p, length);

		int tableIndex = Character.toLowerCase(input[p]) - 'a';
		if (tableIndex >= 0 && tableIndex < 26) {
			Object[][] names = namesTable[tableIndex];
			int i;

			for (i = 0; names[i][0] != null; i++) {
				if (names[i][0].equals(s)) {
					return (Integer) names[i][1];
				}
			}
		}
		requestor.acceptProblem(Problem.newSyntaxError(
				IProblem.UnrecognizedCharacterEntity, lineNumber, p - 1, length + 2));
		return -1;
	}
	
	static final Object[][] namesA={
		{ "Aacgr", 	0x0386, },
		{ "aacgr", 	0x03AC, },
		{ "Aacute",	0x00C1, },
		{ "aacute",	0x00E1, },
		{ "Abreve",	0x0102, },
		{ "abreve",	0x0103, },
		{ "Acirc", 	0x00C2, },
		{ "acirc", 	0x00E2, },
		{ "acute", 	0x00B4, },
		{ "Acy",   	0x0410, },
		{ "acy",   	0x0430, },
		{ "AElig", 	0x00C6, },
		{ "aelig", 	0x00E6, },
		{ "Agr",   	0x0391, },
		{ "agr",   	0x03B1, },
		{ "Agrave",	0x00C0, },
		{ "agrave",	0x00E0, },
		{ "aleph", 	0x2135, },
		{ "alefsym",  8501, },
		{ "alpha", 	0x03B1, },
		{ "Amacr", 	0x0100, },
		{ "amacr", 	0x0101, },
		{ "amalg", 	0x2210, },
		{ "amp",   	0x0026, },
		{ "and",   	0x2227, },
		{ "ang",   	0x2220, },
		{ "ang90", 	0x221F, },
		{ "angmsd",	0x2221, },
		{ "angsph",	0x2222, },
		{ "angst", 	0x212B, },
		{ "Aogon", 	0x0104, },
		{ "aogon", 	0x0105, },
		{ "ap",    	0x2248, },
		{ "ape",   	0x224A, },
		{ "apos",  	0x0027, },
		{ "Aring", 	0x00C5, },
		{ "aring", 	0x00E5, },
		{ "ast",   	0x002A, },
		{ "asymp", 	0x224D, },
		{ "Atilde",	0x00C3, },
		{ "atilde",	0x00E3, },
		{ "Auml",  	0x00C4, },
		{ "auml",  	0x00E4, },
		{ "Alpha",	   913, },
		{ null,		0 },
	};

	static final Object[][] namesB ={
		{ "barwed",	0x22BC, },
		{ "Barwed",	0x2306, },
		{ "bcong", 	0x224C, },
		{ "Bcy",   	0x0411, },
		{ "bcy",   	0x0431, },
		{ "becaus",	0x2235, },
		{ "bepsi", 	0x220D, },
		{ "bernou",	0x212C, },
		{ "beta",  	0x03B2, },
		{ "beth",  	0x2136, },
		{ "Bgr",   	0x0392, },
		{ "bgr",   	0x03B2, },
		{ "blank", 	0x2423, },
		{ "blk12", 	0x2592, },
		{ "blk14", 	0x2591, },
		{ "blk34", 	0x2593, },
		{ "block", 	0x2588, },
		{ "bottom",	0x22A5, },
		{ "bowtie",	0x22C8, },
		{ "boxdl", 	0x2510, },
		{ "boxDL", 	0x2555, },
		{ "boxdL", 	0x2556, },
		{ "boxDl", 	0x2557, },
		{ "boxdr", 	0x250C, },
		{ "boxDR", 	0x2552, },
		{ "boxDr", 	0x2553, },
		{ "boxdR", 	0x2554, },
		{ "boxh",  	0x2500, },
		{ "boxH",  	0x2550, },
		{ "boxhd", 	0x252C, },
		{ "boxhD", 	0x2564, },
		{ "boxHD", 	0x2565, },
		{ "boxHd", 	0x2566, },
		{ "boxhu", 	0x2534, },
		{ "boxhU", 	0x2567, },
		{ "boxHU", 	0x2568, },
		{ "boxHu", 	0x2569, },
		{ "boxul", 	0x2518, },
		{ "boxUL", 	0x255B, },
		{ "boxUl", 	0x255C, },
		{ "boxuL", 	0x255D, },
		{ "boxur", 	0x2514, },
		{ "boxUR", 	0x2558, },
		{ "boxuR", 	0x2559, },
		{ "boxUr", 	0x255A, },
		{ "boxv",  	0x2502, },
		{ "boxV",  	0x2551, },
		{ "boxvh", 	0x253C, },
		{ "boxvH", 	0x256A, },
		{ "boxVH", 	0x256B, },
		{ "boxVh", 	0x256C, },
		{ "boxvl", 	0x2524, },
		{ "boxvL", 	0x2561, },
		{ "boxVL", 	0x2562, },
		{ "boxVl", 	0x2563, },
		{ "boxvr", 	0x251C, },
		{ "boxvR", 	0x255E, },
		{ "boxVR", 	0x255F, },
		{ "boxVr", 	0x2560, },
		{ "bprime",	0x2035, },
		{ "breve", 	0x02D8, },
		{ "brvbar",	0x00A6, },
		{ "bsim",  	0x223D, },
		{ "bsime", 	0x22CD, },
		{ "bsol",  	0x005C, },
		{ "bull",  	0x2022, },
		{ "bump",  	0x224E, },
		{ "bumpe", 	0x224F, },
		{ "bdquo",	  8222, },
		{ "Beta",	   914, },
		{ null,		0 },
	};

	static final Object[][] namesC={
		{ "Cacute",	0x0106, },
		{ "cacute",	0x0107, },
		{ "cap",   	0x2229, },
		{ "Cap",   	0x22D2, },
		{ "caret", 	0x2041, },
		{ "caron", 	0x02C7, },
		{ "Ccaron",	0x010C, },
		{ "ccaron",	0x010D, },
		{ "Ccedil",	0x00C7, },
		{ "ccedil",	0x00E7, },
		{ "Ccirc", 	0x0108, },
		{ "ccirc", 	0x0109, },
		{ "Cdot",  	0x010A, },
		{ "cdot",  	0x010B, },
		{ "cedil", 	0x00B8, },
		{ "cent",  	0x00A2, },
		{ "CHcy",  	0x0427, },
		{ "chcy",  	0x0447, },
		{ "check", 	0x2713, },
		{ "chi",   	0x03C7, },
		{ "cir",   	0x25CB, },
		{ "circ",  	0x005E, },
		{ "cire",  	0x2257, },
		{ "clubs", 	0x2663, },
		{ "colon", 	0x003A, },
		{ "colone",	0x2254, },
		{ "comma", 	0x002C, },
		{ "commat",	0x0040, },
		{ "comp",  	0x2201, },
		{ "compfn",	0x2218, },
		{ "cong",  	0x2245, },
		{ "conint",	0x222E, },
		{ "coprod",	0x2210, },
		{ "copy",  	0x00A9, },
		{ "copysr",	0x2117, },
		{ "cross", 	0x2717, },
		{ "cuepr", 	0x22DE, },
		{ "cuesc", 	0x22DF, },
		{ "cularr",	0x21B6, },
		{ "cup",   	0x222A, },
		{ "Cup",   	0x22D3, },
		{ "cupre", 	0x227C, },
		{ "curarr",	0x21B7, },
		{ "curren",	0x00A4, },
		{ "cuvee", 	0x22CE, },
		{ "cuwed", 	0x22CF, },
		{ "crarr",	  8629, },
		{ "Chi",	   935, },
		{ null,		0 },
	};

	static final Object[][] namesD={
		{ "dagger",	0x2020, },
		{ "Dagger",	0x2021, },
		{ "daleth",	0x2138, },
		{ "darr",  	0x2193, },
		{ "dArr",  	0x21D3, },
		{ "darr2", 	0x21CA, },
		{ "dash",  	0x2010, },
		{ "dashv", 	0x22A3, },
		{ "dblac", 	0x02DD, },
		{ "Dcaron",	0x010E, },
		{ "dcaron",	0x010F, },
		{ "Dcy",   	0x0414, },
		{ "dcy",   	0x0434, },
		{ "deg",   	0x00B0, },
		{ "Delta", 	0x0394, },
		{ "delta", 	0x03B4, },
		{ "Dgr",   	0x0394, },
		{ "dgr",   	0x03B4, },
		{ "dharl", 	0x21C3, },
		{ "dharr", 	0x21C2, },
		{ "diam",  	0x22C4, },
		{ "diams", 	0x2666, },
		{ "die",   	0x00A8, },
		{ "divide",	0x00F7, },
		{ "divonx",	0x22C7, },
		{ "DJcy",  	0x0402, },
		{ "djcy",  	0x0452, },
		{ "dlarr", 	0x2199, },
		{ "dlcorn",	0x231E, },
		{ "dlcrop",	0x230D, },
		{ "dollar",	0x0024, },
		{ "Dot",   	0x00A8, },
		{ "dot",   	0x02D9, },
		{ "DotDot",	0x20DC, },
		{ "drarr", 	0x2198, },
		{ "drcorn",	0x231F, },
		{ "drcrop",	0x230C, },
		{ "DScy",  	0x0405, },
		{ "dscy",  	0x0455, },
		{ "Dstrok",	0x0110, },
		{ "dstrok",	0x0111, },
		{ "dtri",  	0x25BF, },
		{ "dtrif", 	0x25BE, },
		{ "DZcy",  	0x040F, },
		{ "dzcy",  	0x045F, },
		{ null,		0 },
	};

	static final Object[][] namesE={
		{ "Eacgr", 	0x0388, },
		{ "eacgr", 	0x03AD, },
		{ "Eacute",	0x00C9, },
		{ "eacute",	0x00E9, },
		{ "Ecaron",	0x011A, },
		{ "ecaron",	0x011B, },
		{ "ecir",  	0x2256, },
		{ "Ecirc", 	0x00CA, },
		{ "ecirc", 	0x00EA, },
		{ "ecolon",	0x2255, },
		{ "Ecy",   	0x042D, },
		{ "ecy",   	0x044D, },
		{ "Edot",  	0x0116, },
		{ "edot",  	0x0117, },
		{ "eDot",  	0x2251, },
		{ "EEacgr",	0x0389, },
		{ "eeacgr",	0x03AE, },
		{ "EEgr",  	0x0397, },
		{ "eegr",  	0x03B7, },
		{ "efDot", 	0x2252, },
		{ "Egr",   	0x0395, },
		{ "egr",   	0x03B5, },
		{ "Egrave",	0x00C8, },
		{ "egrave",	0x00E8, },
		{ "egs",   	0x22DD, },
		{ "ell",   	0x2113, },
		{ "els",   	0x22DC, },
		{ "Emacr", 	0x0112, },
		{ "emacr", 	0x0113, },
		{ "empty", 	0x2205, },
		{ "emsp",  	0x2003, },
		{ "emsp13",	0x2004, },
		{ "emsp14",	0x2005, },
		{ "ENG",   	0x014A, },
		{ "eng",   	0x014B, },
		{ "ensp",  	0x2002, },
		{ "Eogon", 	0x0118, },
		{ "eogon", 	0x0119, },
		{ "epsi",  	0x220A, },
		{ "epsis", 	0x220A, },
		{ "epsiv", 	0x03B5, },
		{ "equals",	0x003D, },
		{ "equiv", 	0x2261, },
		{ "epsilon",   949, },
		{ "erDot", 	0x2253, },
		{ "esdot", 	0x2250, },
		{ "eta",   	0x03B7, },
		{ "Epsilon",   917, },
		{ "ETH",   	0x00D0, },
		{ "eth",   	0x00F0, },
		{ "Euml",  	0x00CB, },
		{ "euml",  	0x00EB, },
		{ "excl",  	0x0021, },
		{ "exist", 	0x2203, },
		{ "euro",	  8364, },
		{ "Eta",	   919, },
		{ null,		0 },
	};

	static final Object[][] namesF={
		{ "Fcy",   	0x0424, },
		{ "fcy",   	0x0444, },
		{ "female",	0x2640, },
		{ "ffilig",	0xFB03, },
		{ "fflig", 	0xFB00, },
		{ "ffllig",	0xFB04, },
		{ "filig", 	0xFB01, },
		{ "flat",  	0x266D, },
		{ "fllig", 	0xFB02, },
		{ "fnof",  	0x0192, },
		{ "forall",	0x2200, },
		{ "fork",  	0x22D4, },
		{ "frac12",	0x00BD, },
		{ "frac13",	0x2153, },
		{ "frac14",	0x00BC, },
		{ "frac15",	0x2155, },
		{ "frac16",	0x2159, },
		{ "frac18",	0x215B, },
		{ "frac23",	0x2154, },
		{ "frac25",	0x2156, },
		{ "frac34",	0x00BE, },
		{ "frac35",	0x2157, },
		{ "frac38",	0x215C, },
		{ "frac45",	0x2158, },
		{ "frac56",	0x215A, },
		{ "frac58",	0x215D, },
		{ "frac78",	0x215E, },
		{ "frown", 	0x2322, },
		{ "frasl",	8260, },
		{ null,		0 },
	};

	static final Object[][] namesG={
		{ "gacute",	0x01F5, },
		{ "Gamma", 	0x0393, },
		{ "gamma", 	0x03B3, },
		{ "gammad",	0x03DC, },
		{ "gap",   	0x2273, },
		{ "Gbreve",	0x011E, },
		{ "gbreve",	0x011F, },
		{ "Gcedil",	0x0122, },
		{ "Gcirc", 	0x011C, },
		{ "gcirc", 	0x011D, },
		{ "Gcy",   	0x0413, },
		{ "gcy",   	0x0433, },
		{ "Gdot",  	0x0120, },
		{ "gdot",  	0x0121, },
		{ "ge",    	0x2265, },
		{ "gE",    	0x2267, },
		{ "gel",   	0x22DB, },
		{ "gEl",   	0x22DB, },
		{ "ges",   	0x2265, },
		{ "Gg",    	0x22D9, },
		{ "Ggr",   	0x0393, },
		{ "ggr",   	0x03B3, },
		{ "gimel", 	0x2137, },
		{ "GJcy",  	0x0403, },
		{ "gjcy",  	0x0453, },
		{ "gl",    	0x2277, },
		{ "gnap",  	0xE411, },
		{ "gne",   	0x2269, },
		{ "gnE",   	0x2269, },
		{ "gnsim", 	0x22E7, },
		{ "grave", 	0x0060, },
		{ "gsdot", 	0x22D7, },
		{ "gsim",  	0x2273, },
		{ "gt",    	0x003E, },
		{ "Gt",    	0x226B, },
		{ "gvnE",  	0x2269, },
		{ null,		0 },
	};

	static final Object[][] namesH={
		{ "hairsp",	0x200A, },
		{ "half",  	0x00BD, },
		{ "hamilt",	0x210B, },
		{ "HARDcy",	0x042A, },
		{ "hardcy",	0x044A, },
		{ "harr",  	0x2194, },
		{ "hArr",  	0x21D4, },
		{ "harrw", 	0x21AD, },
		{ "Hcirc", 	0x0124, },
		{ "hcirc", 	0x0125, },
		{ "hearts",	0x2665, },
		{ "hellip",	0x2026, },
		{ "horbar",	0x2015, },
		{ "Hstrok",	0x0126, },
		{ "hstrok",	0x0127, },
		{ "hybull",	0x2043, },
		{ "hyphen",	0x002D, },
		{ null,		0 },
	};

	static final Object[][] namesI={
		{ "Iacgr", 	0x038A, },
		{ "iacgr", 	0x03AF, },
		{ "Iacute",	0x00CD, },
		{ "iacute",	0x00ED, },
		{ "Icirc", 	0x00CE, },
		{ "icirc", 	0x00EE, },
		{ "Icy",   	0x0418, },
		{ "icy",   	0x0438, },
		{ "idiagr",	0x0390, },
		{ "Idigr", 	0x03AA, },
		{ "idigr", 	0x03CA, },
		{ "Idot",  	0x0130, },
		{ "IEcy",  	0x0415, },
		{ "iecy",  	0x0435, },
		{ "iexcl", 	0x00A1, },
		{ "iff",   	0x21D4, },
		{ "Igr",   	0x0399, },
		{ "igr",   	0x03B9, },
		{ "Igrave",	0x00CC, },
		{ "igrave",	0x00EC, },
		{ "IJlig", 	0x0132, },
		{ "ijlig", 	0x0133, },
		{ "Imacr", 	0x012A, },
		{ "imacr", 	0x012B, },
		{ "image", 	0x2111, },
		{ "incare",	0x2105, },
		{ "infin", 	0x221E, },
		{ "inodot",	0x0131, },
		{ "int",   	0x222B, },
		{ "intcal",	0x22BA, },
		{ "IOcy",  	0x0401, },
		{ "iocy",  	0x0451, },
		{ "Iogon", 	0x012E, },
		{ "iogon", 	0x012F, },
		{ "iota",  	0x03B9, },
		{ "iquest",	0x00BF, },
		{ "isin",  	0x220A, },
		{ "Itilde",	0x0128, },
		{ "itilde",	0x0129, },
		{ "Iukcy", 	0x0406, },
		{ "iukcy", 	0x0456, },
		{ "Iuml",  	0x00CF, },
		{ "iuml",  	0x00EF, },
		{ "Iota",	   921, },
		{ null,		0 },
	};

	static final Object[][] namesJ={
		{ "Jcirc", 	0x0134, },
		{ "jcirc", 	0x0135, },
		{ "Jcy",   	0x0419, },
		{ "jcy",   	0x0439, },
		{ "Jsercy",	0x0408, },
		{ "jsercy",	0x0458, },
		{ "Jukcy", 	0x0404, },
		{ "jukcy", 	0x0454, },
		{ null,		0 },
	};

	static final Object[][] namesK={
		{ "kappa", 	0x03BA, },
		{ "kappav",	0x03F0, },
		{ "Kcedil",	0x0136, },
		{ "kcedil",	0x0137, },
		{ "Kcy",   	0x041A, },
		{ "kcy",   	0x043A, },
		{ "Kgr",   	0x039A, },
		{ "kgr",   	0x03BA, },
		{ "kgreen",	0x0138, },
		{ "KHcy",  	0x0425, },
		{ "khcy",  	0x0445, },
		{ "KHgr",  	0x03A7, },
		{ "khgr",  	0x03C7, },
		{ "KJcy",  	0x040C, },
		{ "kjcy",  	0x045C, },
		{ "Kappa",	   922, },
		{ null,		0 },
	};

	static final Object[][] namesL={
		{ "lAarr", 	0x21DA, },
		{ "Lacute",	0x0139, },
		{ "lacute",	0x013A, },
		{ "lagran",	0x2112, },
		{ "Lambda",	0x039B, },
		{ "lambda",	0x03BB, },
		{ "lang",  	0x3008, },
		{ "lap",   	0x2272, },
		{ "laquo", 	0x00AB, },
		{ "larr",  	0x2190, },
		{ "Larr",  	0x219E, },
		{ "lArr",  	0x21D0, },
		{ "larr2", 	0x21C7, },
		{ "larrhk",	0x21A9, },
		{ "larrlp",	0x21AB, },
		{ "larrtl",	0x21A2, },
		{ "Lcaron",	0x013D, },
		{ "lcaron",	0x013E, },
		{ "Lcedil",	0x013B, },
		{ "lcedil",	0x013C, },
		{ "lceil", 	0x2308, },
		{ "lcub",  	0x007B, },
		{ "Lcy",   	0x041B, },
		{ "lcy",   	0x043B, },
		{ "ldot",  	0x22D6, },
		{ "ldquo", 	0x201C, },
		{ "ldquor",	0x201E, },
		{ "le",    	0x2264, },
		{ "lE",    	0x2266, },
		{ "leg",   	0x22DA, },
		{ "lEg",   	0x22DA, },
		{ "les",   	0x2264, },
		{ "lfloor",	0x230A, },
		{ "lg",    	0x2276, },
		{ "Lgr",   	0x039B, },
		{ "lgr",   	0x03BB, },
		{ "lhard", 	0x21BD, },
		{ "lharu", 	0x21BC, },
		{ "lhblk", 	0x2584, },
		{ "LJcy",  	0x0409, },
		{ "ljcy",  	0x0459, },
		{ "Ll",    	0x22D8, },
		{ "Lmidot",	0x013F, },
		{ "lmidot",	0x0140, },
		{ "lnap",  	0xE2A2, },
		{ "lne",   	0x2268, },
		{ "lnE",   	0x2268, },
		{ "lnsim", 	0x22E6, },
		{ "lowast",	0x2217, },
		{ "lowbar",	0x005F, },
		{ "loz",   	0x25CA, },
		{ "lozf",  	0x2726, },
		{ "lpar",  	0x0028, },
		{ "lrarr2",	0x21C6, },
		{ "lrhar2",	0x21CB, },
		{ "lsh",   	0x21B0, },
		{ "lsim",  	0x2272, },
		{ "lsqb",  	0x005B, },
		{ "lsquo", 	0x2018, },
		{ "lsquor",	0x201A, },
		{ "Lstrok",	0x0141, },
		{ "lstrok",	0x0142, },
		{ "lt",    	0x003C, },
		{ "Lt",    	0x226A, },
		{ "lthree",	0x22CB, },
		{ "ltimes",	0x22C9, },
		{ "ltri",  	0x25C3, },
		{ "ltrie", 	0x22B4, },
		{ "ltrif", 	0x25C2, },
		{ "lvnE",  	0x2268, },
		{ "lsaquo",	  8249, },
		{ "lrm",	  8206, },
		{ null,		0 },
	};

	static final Object[][] namesM={
		{ "macr",  	0x00AF, },
		{ "male",  	0x2642, },
		{ "malt",  	0x2720, },
		{ "map",   	0x21A6, },
		{ "marker",	0x25AE, },
		{ "Mcy",   	0x041C, },
		{ "mcy",   	0x043C, },
		{ "mdash", 	0x2014, },
		{ "Mgr",   	0x039C, },
		{ "mgr",   	0x03BC, },
		{ "micro", 	0x00B5, },
		{ "mid",   	0x2223, },
		{ "middot",	0x00B7, },
		{ "minus", 	0x2212, },
		{ "minusb",	0x229F, },
		{ "mldr",  	0x2026, },
		{ "mnplus",	0x2213, },
		{ "models",	0x22A7, },
		{ "mu",    	0x03BC, },
		{ "mumap", 	0x22B8, },
		{ "Mu",	       924, },
		{ null,		0 },
	};

	static final Object[][] namesN={
		{ "nabla", 	0x2207, },
		{ "Nacute",	0x0143, },
		{ "nacute",	0x0144, },
		{ "nap",   	0x2249, },
		{ "napos", 	0x0149, },
		{ "natur", 	0x266E, },
//			"nbsp",  	0x00A0,
		{ "nbsp",  	32, },   // make non-breaking space appear as space
		{ "Ncaron",	0x0147, },
		{ "ncaron",	0x0148, },
		{ "Ncedil",	0x0145, },
		{ "ncedil",	0x0146, },
		{ "ncong", 	0x2247, },
		{ "Ncy",   	0x041D, },
		{ "ncy",   	0x043D, },
		{ "ndash", 	0x2013, },
		{ "ne",    	0x2260, },
		{ "nearr", 	0x2197, },
		{ "nequiv",	0x2262, },
		{ "nexist",	0x2204, },
		{ "nge",   	0x2271, },
		{ "ngE",   	0x2271, },
		{ "nges",  	0x2271, },
		{ "Ngr",   	0x039D, },
		{ "ngr",   	0x03BD, },
		{ "ngt",   	0x226F, },
		{ "nharr", 	0x21AE, },
		{ "nhArr", 	0x21CE, },
		{ "ni",    	0x220D, },
		{ "NJcy",  	0x040A, },
		{ "njcy",  	0x045A, },
		{ "nlarr", 	0x219A, },
		{ "nlArr", 	0x21CD, },
		{ "nldr",  	0x2025, },
		{ "nle",   	0x2270, },
		{ "nlE",   	0x2270, },
		{ "nles",  	0x2270, },
		{ "nlt",   	0x226E, },
		{ "nltri", 	0x22EA, },
		{ "nltrie",	0x22EC, },
		{ "nmid",  	0x2224, },
		{ "not",   	0x00AC, },
		{ "notin", 	0x2209, },
		{ "npar",  	0x2226, },
		{ "npr",   	0x2280, },
		{ "npre",  	0x22E0, },
		{ "nrarr", 	0x219B, },
		{ "nrArr", 	0x21CF, },
		{ "nrtri", 	0x22EB, },
		{ "nrtrie",	0x22ED, },
		{ "nsc",   	0x2281, },
		{ "nsce",  	0x22E1, },
		{ "nsim",  	0x2241, },
		{ "nsime", 	0x2244, },
		{ "nsmid", 	0xE2AA, },
		{ "nspar", 	0x2226, },
		{ "nsub",  	0x2284, },
		{ "nsube", 	0x2288, },
		{ "nsubE", 	0x2288, },
		{ "nsup",  	0x2285, },
		{ "nsupe", 	0x2289, },
		{ "nsupE", 	0x2289, },
		{ "Ntilde",	0x00D1, },
		{ "ntilde",	0x00F1, },
		{ "nu",    	0x03BD, },
		{ "num",   	0x0023, },
		{ "numero",	0x2116, },
		{ "numsp", 	0x2007, },
		{ "nvdash",	0x22AC, },
		{ "nvDash",	0x22AD, },
		{ "nVdash",	0x22AE, },
		{ "nVDash",	0x22AF, },
		{ "nwarr", 	0x2196, },
		{ "Nu",	       925, },
		{ null,		0 },
	};

	static final Object[][] namesO={
		{ "Oacgr", 	0x038C, },
		{ "oacgr", 	0x03CC, },
		{ "Oacute",	0x00D3, },
		{ "oacute",	0x00F3, },
		{ "oast",  	0x229B, },
		{ "ocir",  	0x229A, },
		{ "Ocirc", 	0x00D4, },
		{ "ocirc", 	0x00F4, },
		{ "Ocy",   	0x041E, },
		{ "ocy",   	0x043E, },
		{ "odash", 	0x229D, },
		{ "Odblac",	0x0150, },
		{ "odblac",	0x0151, },
		{ "odot",  	0x2299, },
		{ "OElig", 	0x0152, },
		{ "oelig", 	0x0153, },
		{ "ogon",  	0x02DB, },
		{ "Ogr",   	0x039F, },
		{ "ogr",   	0x03BF, },
		{ "Ograve",	0x00D2, },
		{ "ograve",	0x00F2, },
		{ "OHacgr",	0x038F, },
		{ "ohacgr",	0x03CE, },
		{ "OHgr",  	0x03A9, },
		{ "ohgr",  	0x03C9, },
		{ "ohm",   	0x2126, },
		{ "olarr", 	0x21BA, },
		{ "Omacr", 	0x014C, },
		{ "omacr", 	0x014D, },
		{ "Omega", 	0x03A9, },
		{ "omega", 	0x03C9, },
		{ "ominus",	0x2296, },
		{ "oplus", 	0x2295, },
		{ "or",    	0x2228, },
		{ "orarr", 	0x21BB, },
		{ "order", 	0x2134, },
		{ "ordf",  	0x00AA, },
		{ "ordm",  	0x00BA, },
		{ "oS",    	0x24C8, },
		{ "Oslash",	0x00D8, },
		{ "oslash",	0x00F8, },
		{ "osol",  	0x2298, },
		{ "Otilde",	0x00D5, },
		{ "otilde",	0x00F5, },
		{ "otimes",	0x2297, },
		{ "Ouml",  	0x00D6, },
		{ "ouml",  	0x00F6, },
		{ "Omicron",   927, },
		{ "omicron",   959, },
		{ "oline",	  8254, },
		{ null,		0		},
	};

	static final Object[][] namesP={
		{ "par",   	0x2225, },
		{ "para",  	0x00B6, },
		{ "part",  	0x2202, },
		{ "Pcy",   	0x041F, },
		{ "pcy",   	0x043F, },
		{ "percnt",	0x0025, },
		{ "period",	0x002E, },
		{ "permil",	0x2030, },
		{ "perp",  	0x22A5, },
		{ "Pgr",   	0x03A0, },
		{ "pgr",   	0x03C0, },
		{ "PHgr",  	0x03A6, },
		{ "phgr",  	0x03C6, },
		{ "Phi",   	0x03A6, },
		{ "phis",  	0x03C6, },
		{ "phiv",  	0x03D5, },
		{ "phmmat",	0x2133, },
		{ "phone", 	0x260E, },
		{ "Pi",    	0x03A0, },
		{ "pi",    	0x03C0, },
		{ "piv",   	0x03D6, },
		{ "planck",	0x210F, },
		{ "plus",  	0x002B, },
		{ "plusb", 	0x229E, },
		{ "plusdo",	0x2214, },
		{ "plusmn",	0x00B1, },
		{ "pound", 	0x00A3, },
		{ "pr",    	0x227A, },
		{ "prap",  	0x227E, },
		{ "pre",   	0x227C, },
		{ "prime", 	0x2032, },
		{ "Prime", 	0x2033, },
		{ "prnap", 	0x22E8, },
		{ "prnE",  	0xE2B3, },
		{ "prnsim",	0x22E8, },
		{ "prod",  	0x220F, },
		{ "prop",  	0x221D, },
		{ "prsim", 	0x227E, },
		{ "PSgr",  	0x03A8, },
		{ "psgr",  	0x03C8, },
		{ "Psi",   	0x03A8, },
		{ "psi",   	0x03C8, },
		{ "puncsp",	0x2008, },
		{ "phi",	   966, },
		{ null,		0 },
	};

	static final Object[][] namesQ={
		{ "quest", 	0x003F, },
		{ "quot",  	0x0022, },
		{ null,		0 },
	};

	static final Object[][] namesR={
		{ "rAarr", 	0x21DB, },
		{ "Racute",	0x0154, },
		{ "racute",	0x0155, },
		{ "radic", 	0x221A, },
		{ "rang",  	0x3009, },
		{ "raquo", 	0x00BB, },
		{ "rarr",  	0x2192, },
		{ "Rarr",  	0x21A0, },
		{ "rArr",  	0x21D2, },
		{ "rarr2", 	0x21C9, },
		{ "rarrhk",	0x21AA, },
		{ "rarrlp",	0x21AC, },
		{ "rarrtl",	0x21A3, },
		{ "rarrw", 	0x219D, },
		{ "Rcaron",	0x0158, },
		{ "rcaron",	0x0159, },
		{ "Rcedil",	0x0156, },
		{ "rcedil",	0x0157, },
		{ "rceil", 	0x2309, },
		{ "rcub",  	0x007D, },
		{ "Rcy",   	0x0420, },
		{ "rcy",   	0x0440, },
		{ "rdquo", 	0x201D, },
		{ "rdquor",	0x201C, },
		{ "real",  	0x211C, },
		{ "rect",  	0x25AD, },
		{ "reg",   	0x00AE, },
		{ "rfloor",	0x230B, },
		{ "Rgr",   	0x03A1, },
		{ "rgr",   	0x03C1, },
		{ "rhard", 	0x21C1, },
		{ "rharu", 	0x21C0, },
		{ "rho",   	0x03C1, },
		{ "rhov",  	0x03F1, },
		{ "ring",  	0x02DA, },
		{ "rlarr2",	0x21C4, },
		{ "rlhar2",	0x21CC, },
		{ "rpar",  	0x0029, },
		{ "rpargt",	0xE291, },
		{ "rsh",   	0x21B1, },
		{ "rsqb",  	0x005D, },
		{ "rsquo", 	0x2019, },
		{ "rsquor",	0x2018, },
		{ "rthree",	0x22CC, },
		{ "rtimes",	0x22CA, },
		{ "rtri",  	0x25B9, },
		{ "rtrie", 	0x22B5, },
		{ "rtrif", 	0x25B8, },
		{ "rx",    	0x211E, },
		{ "rsaquo",	  8250, },
		{ "Rho",	   929, },
		{ "rlm",	  8207, },
		{ null,		0 },
	};

	static final Object[][] namesS={
		{ "Sacute",	0x015A, },
		{ "sacute",	0x015B, },
		{ "samalg",	0x2210, },
		{ "sbsol", 	0xFE68, },
		{ "sc",    	0x227B, },
		{ "scap",  	0x227F, },
		{ "Scaron",	0x0160, },
		{ "scaron",	0x0161, },
		{ "sccue", 	0x227D, },
		{ "sce",   	0x227D, },
		{ "Scedil",	0x015E, },
		{ "scedil",	0x015F, },
		{ "Scirc", 	0x015C, },
		{ "scirc", 	0x015D, },
		{ "scnap", 	0x22E9, },
		{ "scnE",  	0xE2B5, },
		{ "scnsim",	0x22E9, },
		{ "scsim", 	0x227F, },
		{ "Scy",   	0x0421, },
		{ "scy",   	0x0441, },
		{ "sdot",  	0x22C5, },
		{ "sdotb", 	0x22A1, },
		{ "sect",  	0x00A7, },
		{ "semi",  	0x003B, },
		{ "setmn", 	0x2216, },
		{ "sext",  	0x2736, },
		{ "sfgr",  	0x03C2, },
		{ "sfrown",	0x2322, },
		{ "Sgr",   	0x03A3, },
		{ "sgr",   	0x03C3, },
		{ "sharp", 	0x266F, },
		{ "SHCHcy",	0x0429, },
		{ "shchcy",	0x0449, },
		{ "SHcy",  	0x0428, },
		{ "shcy",  	0x0448, },
		{ "shy",   	0x00AD, },
		{ "Sigma", 	0x03A3, },
		{ "sigma", 	0x03C3, },
		{ "sigmav",	0x03C2, },
		{ "sim",   	0x223C, },
		{ "sime",  	0x2243, },
		{ "smid",  	0xE301, },
		{ "smile", 	0x2323, },
		{ "SOFTcy",	0x042C, },
		{ "softcy",	0x044C, },
		{ "sol",   	0x002F, },
		{ "spades",	0x2660, },
		{ "spar",  	0x2225, },
		{ "sqcap", 	0x2293, },
		{ "sqcup", 	0x2294, },
		{ "sqsub", 	0x228F, },
		{ "sqsube",	0x2291, },
		{ "sqsup", 	0x2290, },
		{ "sqsupe",	0x2292, },
		{ "squ",   	0x25A1, },
		{ "square",	0x25A1, },
		{ "squf",  	0x25AA, },
		{ "ssetmn",	0x2216, },
		{ "ssmile",	0x2323, },
		{ "sstarf",	0x22C6, },
		{ "star",  	0x22C6, },
		{ "starf", 	0x2605, },
		{ "sub",   	0x2282, },
		{ "Sub",   	0x22D0, },
		{ "sube",  	0x2286, },
		{ "subE",  	0x2286, },
		{ "subne", 	0x228A, },
		{ "subnE", 	0x228A, },
		{ "sum",   	0x2211, },
		{ "sung",  	0x2669, },
		{ "sup",   	0x2283, },
		{ "Sup",   	0x22D1, },
		{ "sup1",  	0x00B9, },
		{ "sup2",  	0x00B2, },
		{ "sup3",  	0x00B3, },
		{ "supe",  	0x2287, },
		{ "supE",  	0x2287, },
		{ "supne", 	0x228B, },
		{ "supnE", 	0x228B, },
		{ "szlig", 	0x00DF, },
		{ "sigmaf",	   962, },
		{ "sbquo",	  8218, },
		{ null,		0 },
	};

	static final Object[][] namesT={
		{ "target",	0x2316, },
		{ "tau",   	0x03C4, },
		{ "Tcaron",	0x0164, },
		{ "tcaron",	0x0165, },
		{ "Tcedil",	0x0162, },
		{ "tcedil",	0x0163, },
		{ "Tcy",   	0x0422, },
		{ "tcy",   	0x0442, },
		{ "tdot",  	0x20DB, },
		{ "telrec",	0x2315, },
		{ "Tgr",   	0x03A4, },
		{ "tgr",   	0x03C4, },
		{ "there4",	0x2234, },
		{ "Theta", 	0x0398, },
		{ "thetas",	0x03B8, },
		{ "thetasym",  977, },
		{ "thetav",	0x03D1, },
		{ "THgr",  	0x0398, },
		{ "thgr",  	0x03B8, },
		{ "thinsp",	0x2009, },
		{ "thkap", 	0x2248, },
		{ "thksim",	0x223C, },
		{ "THORN", 	0x00DE, },
		{ "thorn", 	0x00FE, },
		{ "tilde", 	0x02DC, },
		{ "times", 	0x00D7, },
		{ "timesb",	0x22A0, },
		{ "top",   	0x22A4, },
		{ "tprime",	0x2034, },
		{ "trade", 	0x2122, },
		{ "trie",  	0x225C, },
		{ "TScy",  	0x0426, },
		{ "tscy",  	0x0446, },
		{ "TSHcy", 	0x040B, },
		{ "tshcy", 	0x045B, },
		{ "Tstrok",	0x0166, },
		{ "tstrok",	0x0167, },
		{ "twixt", 	0x226C, },
		{ "theta",	   952, },
		{ "Tau",	   932, },
		{ null,		0 },
	};

	static final Object[][] namesU={
		{ "Uacgr", 	0x038E, },
		{ "uacgr", 	0x03CD, },
		{ "Uacute",	0x00DA, },
		{ "uacute",	0x00FA, },
		{ "uarr",  	0x2191, },
		{ "uArr",  	0x21D1, },
		{ "uarr2", 	0x21C8, },
		{ "Ubrcy", 	0x040E, },
		{ "ubrcy", 	0x045E, },
		{ "Ubreve",	0x016C, },
		{ "ubreve",	0x016D, },
		{ "Ucirc", 	0x00DB, },
		{ "ucirc", 	0x00FB, },
		{ "Ucy",   	0x0423, },
		{ "ucy",   	0x0443, },
		{ "Udblac",	0x0170, },
		{ "udblac",	0x0171, },
		{ "udiagr",	0x03B0, },
		{ "Udigr", 	0x03AB, },
		{ "udigr", 	0x03CB, },
		{ "Ugr",   	0x03A5, },
		{ "ugr",   	0x03C5, },
		{ "Ugrave",	0x00D9, },
		{ "ugrave",	0x00F9, },
		{ "uharl", 	0x21BF, },
		{ "uharr", 	0x21BE, },
		{ "uhblk", 	0x2580, },
		{ "ulcorn",	0x231C, },
		{ "ulcrop",	0x230F, },
		{ "Umacr", 	0x016A, },
		{ "umacr", 	0x016B, },
		{ "uml",   	0x00A8, },
		{ "Uogon", 	0x0172, },
		{ "uogon", 	0x0173, },
		{ "uplus", 	0x228E, },
		{ "upsi",  	0x03C5, },
		{ "Upsi",  	0x03D2, },
		{ "urcorn",	0x231D, },
		{ "urcrop",	0x230E, },
		{ "Uring", 	0x016E, },
		{ "uring", 	0x016F, },
		{ "Utilde",	0x0168, },
		{ "utilde",	0x0169, },
		{ "utri",  	0x25B5, },
		{ "utrif", 	0x25B4, },
		{ "Uuml",  	0x00DC, },
		{ "uuml",  	0x00FC, },
		{ "Upsilon",   933, },
		{ "upsilon",   965, },
		{ "upsih",	   978, },
		{ "uarr",	  8593, },
		{ null,		0 },
	};

	static final Object[][] namesV={
		{ "varr",  	0x2195, },
		{ "vArr",  	0x21D5, },
		{ "Vcy",   	0x0412, },
		{ "vcy",   	0x0432, },
		{ "vdash", 	0x22A2, },
		{ "vDash", 	0x22A8, },
		{ "Vdash", 	0x22A9, },
		{ "veebar",	0x22BB, },
		{ "vellip",	0x22EE, },
		{ "verbar",	0x007C, },
		{ "Verbar",	0x2016, },
		{ "vltri", 	0x22B2, },
		{ "vprime",	0x2032, },
		{ "vprop", 	0x221D, },
		{ "vrtri", 	0x22B3, },
		{ "vsubne",	0x228A, },
		{ "vsubnE",	0xE2B8, },
		{ "vsupne",	0x228B, },
		{ "vsupnE",	0x228B, },
		{ "Vvdash",	0x22AA, },
		{ null,		0 },
	};

	static final Object[][] namesW={
		{ "Wcirc", 	0x0174, },
		{ "wcirc", 	0x0175, },
		{ "wedgeq",	0x2259, },
		{ "weierp",	0x2118, },
		{ "wreath",	0x2240, },
		{ null,		0 },
	};

	static final Object[][] namesX={
		{ "xcirc", 	0x25CB, },
		{ "xdtri", 	0x25BD, },
		{ "Xgr",   	0x039E, },
		{ "xgr",   	0x03BE, },
		{ "xharr", 	0x2194, },
		{ "xhArr", 	0x2194, },
		{ "Xi",    	0x039E, },
		{ "xi",    	0x03BE, },
		{ "xlArr", 	0x21D0, },
		{ "xrArr", 	0x21D2, },
		{ "xutri", 	0x25B3, },
		{ null,		0 },
	};

	static final Object[][] namesY={
		{ "Yacute",	0x00DD, },
		{ "yacute",	0x00FD, },
		{ "YAcy",  	0x042F, },
		{ "yacy",  	0x044F, },
		{ "Ycirc", 	0x0176, },
		{ "ycirc", 	0x0177, },
		{ "Ycy",   	0x042B, },
		{ "ycy",   	0x044B, },
		{ "yen",   	0x00A5, },
		{ "YIcy",  	0x0407, },
		{ "yicy",  	0x0457, },
		{ "YUcy",  	0x042E, },
		{ "yucy",  	0x044E, },
		{ "yuml",  	0x00FF, },
		{ "Yuml",  	0x0178, },
		{ null,		0 },
	};

	static final Object[][] namesZ={
		{ "Zacute",	0x0179, },
		{ "zacute",	0x017A, },
		{ "Zcaron",	0x017D, },
		{ "zcaron",	0x017E, },
		{ "Zcy",   	0x0417, },
		{ "zcy",   	0x0437, },
		{ "Zdot",  	0x017B, },
		{ "zdot",  	0x017C, },
		{ "zeta",  	0x03B6, },
		{ "Zgr",   	0x0396, },
		{ "zgr",   	0x03B6, },
		{ "ZHcy",  	0x0416, },
		{ "zhcy",  	0x0436, },
		{ "Zeta",	   918, },
		{ "zwnj",	  8204, },
		{ "zwj",	  8205, },
		{ null, 0 },
	};
	
	static final Object[][][] namesTable = { 
		namesA, namesB, namesC, namesD, namesE, namesF, namesG, namesH, namesI,
		namesJ, namesK, namesL, namesM, namesN, namesO, namesP, namesQ, namesR,
		namesS, namesT, namesU, namesV, namesW, namesX, namesY, namesZ, null
	};

}
