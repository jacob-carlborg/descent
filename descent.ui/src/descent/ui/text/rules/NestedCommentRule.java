package descent.ui.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.Token;

public class NestedCommentRule extends PatternRule {
	
	private char[] anotherStart;

    public NestedCommentRule(String start, String anotherStart, String end, IToken token, char escapeCharacter, boolean breaksOnEOF) {
        super(start, end, token, escapeCharacter, false, breaksOnEOF);
        this.anotherStart = anotherStart.toCharArray();
    }

    // brackets sum value (opening is positive, closing is negative)
    private int fValue = 0;

    // matching brackets count
    private int fCount = 0;

    protected IToken doEvaluate(ICharacterScanner scanner, boolean resume) {
        fValue = fCount = 0;
        int c = scanner.read();
        if (c == fStartSequence[0]) {
            if (sequenceDetected(scanner, fStartSequence, false)) {
                ++fValue;
                ++fCount;
                if (matchingSequenceDetected(scanner))
                    return fToken;
            }
        }
        scanner.unread();
        return Token.UNDEFINED;
    }

    protected boolean matchingSequenceDetected(ICharacterScanner scanner) {
        int c;
        while ((c = scanner.read()) != ICharacterScanner.EOF) {
            if (c == fEscapeCharacter) {
                scanner.read();
            } else if (fStartSequence.length > 0 && c == anotherStart[0]) {
                if (sequenceDetected(scanner, anotherStart, true)) {
                    ++fValue;
                    ++fCount;
                }
            } else if (fEndSequence.length > 0 && c == fEndSequence[0]) {
                if (sequenceDetected(scanner, fEndSequence, true)) {
                    if (--fValue == 0)
                        return true;
                }
            }
        }
        if (fBreaksOnEOF)
            return (fCount > 0);
        scanner.unread();
        return false;
    }
}
