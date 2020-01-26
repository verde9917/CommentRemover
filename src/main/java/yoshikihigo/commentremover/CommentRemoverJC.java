package yoshikihigo.commentremover;

import java.util.Stack;

public class CommentRemoverJC extends CommentRemover {

    enum STATE {
        CODE, BLOCKCOMMENT, LINECOMMENT, DOUBLEQUOTELITERAL, SINGLEQUOTELITERAL;
    }

    enum BRACKET_STATE {
        BEFORE_BRACKET, AFTER_OPEN_BRACKET, AFTER_CLOSE_BRACKET, USUAL_LINE;
    }

    public CommentRemoverJC(final CRConfig config) {
        super(config);
    }

    @Override
    public String perform(final String src) {

        String dest = src;
        if (CRConfig.OPERATION.REMOVE == this.config.getLINECOMMENT()) {
            dest = deleteLineComment(dest);
        }
        if (CRConfig.OPERATION.REMOVE == this.config.getBLOCKCOMMENT()) {
            dest = deleteBlockComment(dest);
        }
        if (CRConfig.OPERATION.REMOVE == this.config.getINDENT()) {
            dest = deleteIndent(dest);
        }
        if (CRConfig.OPERATION.REMOVE == this.config.getBRACKETLINE()) {
            dest = deleteBracketLine(dest);
        }
        if (CRConfig.OPERATION.REMOVE == this.config.getBLANKLINE()) {
            dest = deleteBlankLine(dest);
        }
        return dest;
    }

    @Override
    public String deleteLineComment(final String src) {

        final StringBuilder dest = new StringBuilder();
        final Stack<STATE> states = new Stack<>();
        states.push(STATE.CODE);
        boolean escape = false;
        int comlength = 0;

        for (int index = 0; index < src.length(); index++) {
            final char c1 = src.charAt(index);
            final char c2 = (index + 1) < src.length() ? src.charAt(index + 1) : '0';

            if (STATE.BLOCKCOMMENT == states.peek()) {
                dest.append(c1);

                if ((c1 == '*') && (c2 == '/')) {
                    dest.append(c2);
                    states.pop();
                    index++;
                }
            } else if (STATE.LINECOMMENT == states.peek()) {
                comlength += 1;
                if ((c1 == '\n') || (('\r' == c1) && ('\n' == c2))) {
                    boolean hasOtherState = false;
                    int commentHeadIndex = index - comlength + 1;
                    int lineHeadIndex = 0;
                    int count = 30;

                    for (int i = count; index - comlength - i >= 0 && i >= 0; i--) {
                        char c = src.charAt(index - comlength - i);
                        if (c == '\n') {
                            hasOtherState = false;
                            lineHeadIndex = index - comlength - i + 1;
                        } else if (!Character.isWhitespace(c)) {
                            hasOtherState = true;
                        }
                    }
                    if (lineHeadIndex == 0 || (lineHeadIndex != 0 && hasOtherState)) {
//            System.out.println("afters other sentence");
                        dest.append("\n");
                    } else if (lineHeadIndex == commentHeadIndex) {
//            System.out.println("start with comment");
                    } else if (lineHeadIndex != 0 && !hasOtherState) {
//            System.out.println("start with comment and tabbeds");
                        if (dest.length() >= (commentHeadIndex - lineHeadIndex)) {
                            dest.setLength(dest.length() - (commentHeadIndex - lineHeadIndex));
                        }
                    }

                    states.pop();
                }
            } else if (STATE.DOUBLEQUOTELITERAL == states.peek()) {

                dest.append(c1);

                if (!escape && ('\"' == c1)) {
                    states.pop();
                } else if ('\\' == c1) {
                    escape = !escape;
                } else {
                    escape = false;
                }
            } else if (STATE.SINGLEQUOTELITERAL == states.peek()) {

                dest.append(c1);

                if (!escape && (c1 == '\'')) {
                    states.pop();
                } else if ('\\' == c1) {
                    escape = !escape;
                } else {
                    escape = false;
                }
            } else if (STATE.CODE == states.peek()) {

                assert !escape : "illegal states.";
                if ((c1 == '/') && (c2 == '*')) {

                    states.push(STATE.BLOCKCOMMENT);
                    dest.append(c1);
                    dest.append(c2);
                    index++;
                } else if ((c1 == '/') && (c2 == '/')) {
                    states.push(STATE.LINECOMMENT);
                    comlength = 2;
                    index++;
                } else if (c1 == '\"') {
                    states.push(STATE.DOUBLEQUOTELITERAL);
                    dest.append(c1);
                } else if (c1 == '\'') {
                    states.push(STATE.SINGLEQUOTELITERAL);
                    dest.append(c1);
                } else {
                    dest.append(c1);
                }
            }
        }

        return dest.toString();
    }

    @Override
    public String deleteBlockComment(final String src) {

        final StringBuilder dest = new StringBuilder();
        final Stack<STATE> states = new Stack<>();
        states.push(STATE.CODE);

        for (int index = 0; index < src.length(); index++) {
            final char c1 = src.charAt(index);
            final char c2 = (index + 1) < src.length() ? src.charAt(index + 1) : '0';

            if (STATE.BLOCKCOMMENT == states.peek()) {
                if ('*' == c1 && '/' == c2) {
                    states.pop();
                    index++;
                    final char c3 = (index + 1) < src.length() ? src.charAt(index + 1) : '0';
                    final char c4 = (index + 2) < src.length() ? src.charAt(index + 2) : '0';
                    if ('\n' == c3 && '\n' == c4) {
                        index += 1;
                    }

                } else if ('\n' == c1 || (('\r' == c1) && ('\n' != c2))) {
//          dest.append(c1);
//          index++;
                }
            } else if (STATE.LINECOMMENT == states.peek()) {

                if ('\n' == c1 || (('\r' == c1) && ('\n' != c2))) {
                    states.pop();
                    dest.append(c1);
                } else {
                    dest.append(c1);
                }
            } else if (STATE.DOUBLEQUOTELITERAL == states.peek()) {

                dest.append(c1);

                if ('\"' == c1) {
                    states.pop();
                } else if (('\\' == c1) && ('\"' == c2)) {
                    dest.append(c2);
                    index++;
                } else if ((('\\') == c1) && ('\\' == c2)) {
                    dest.append(c2);
                    index++;
                }
            } else if (STATE.SINGLEQUOTELITERAL == states.peek()) {

                dest.append(c1);

                if ('\'' == c1) {
                    states.pop();
                } else if (('\\' == c1) && ('\'' == c2)) {
                    dest.append(c2);
                    index++;
                } else if ((('\\') == c1) && ('\\' == c2)) {
                    dest.append(c2);
                    index++;
                }
            } else if (STATE.CODE == states.peek()) {

                if ('/' == c1 && '*' == c2) {
                    states.push(STATE.BLOCKCOMMENT);
                    index++;
                } else if ('\"' == c1) {
                    states.push(STATE.DOUBLEQUOTELITERAL);
                    dest.append(c1);
                } else if ('\'' == c1) {
                    states.push(STATE.SINGLEQUOTELITERAL);
                    dest.append(c1);
                } else {
                    dest.append(c1);
                }
            }
        }

        return dest.toString();
    }

    public String deleteBracketLine(final String src) {

        final StringBuilder dest = new StringBuilder();
        final StringBuilder line = new StringBuilder();
        final Stack<STATE> states = new Stack<>();
        states.push(STATE.CODE);
        BRACKET_STATE bracketState = BRACKET_STATE.BEFORE_BRACKET;
        boolean escape = false;

        for (int index = 0; index <= src.length(); index++) {
            final char c1 = index < src.length() ? src.charAt(index) : '\n';
            final char c2 = (index + 1) < src.length() ? src.charAt(index + 1) : '0';

            if (BRACKET_STATE.USUAL_LINE == bracketState) {

                line.append(c1);
                if ('\n' == c1 || (('\r' == c1) && ('\n' != c2))) {
                    bracketState = BRACKET_STATE.BEFORE_BRACKET;
                    dest.append(line.toString());
                    line.delete(0, line.length());
                }

            } else if (BRACKET_STATE.AFTER_OPEN_BRACKET == bracketState) {

                line.append(c1);

                if (('\n' == c1) || (('\r' == c1) && ('\n' != c2))) {
                    for (int last = dest.length() - 1; ' ' == dest.charAt(last) || '\t' == dest.charAt(last)
                            || '\n' == dest.charAt(last) || '\r' == dest.charAt(last); last--) {
                        dest.deleteCharAt(last);
                    }
                    dest.append("{");
                    dest.append(c1);
                    line.delete(0, line.length());
                    bracketState = BRACKET_STATE.BEFORE_BRACKET;
                } else if ((' ' == c1) || ('\t' == c1) || ('\r' == c1)) {
                    // do nothing
                } else {
                    bracketState = BRACKET_STATE.USUAL_LINE;
                }
            } else if (BRACKET_STATE.AFTER_CLOSE_BRACKET == bracketState) {

                line.append(c1);

                if ('\n' == c1 || (('\r' == c1) && ('\n' != c2))) {
                    for (int last = dest.length() - 1; ' ' == dest.charAt(last) || '\t' == dest.charAt(last)
                            || '\n' == dest.charAt(last) || '\r' == dest.charAt(last); last--) {
                        dest.deleteCharAt(last);
                    }
                    dest.append("}");
                    dest.append(c1);
                    line.delete(0, line.length());
                    bracketState = BRACKET_STATE.BEFORE_BRACKET;
                } else if ((' ' == c1) || ('\t' == c1) || ('\r' == c1)) {
                    // do nothing
                } else {
                    bracketState = BRACKET_STATE.USUAL_LINE;
                }
            } else if (BRACKET_STATE.BEFORE_BRACKET == bracketState) {

                line.append(c1);

                if ('\n' == c1 || (('\r' == c1) && ('\n' != c2))) {
                    bracketState = BRACKET_STATE.BEFORE_BRACKET;
                    dest.append(line.toString());
                    line.delete(0, line.length());
                } else if ((' ' == c1) || ('\t' == c1)) {
                    // do nothing
                } else if ('{' == c1) {
                    bracketState = BRACKET_STATE.AFTER_OPEN_BRACKET;
                } else if ('}' == c1) {
                    bracketState = BRACKET_STATE.AFTER_CLOSE_BRACKET;
                } else {
                    bracketState = BRACKET_STATE.USUAL_LINE;
                }
            }

            if (STATE.BLOCKCOMMENT == states.peek()) {
                if ('*' == c1 && '/' == c2) {
                    states.pop();
                } else if ('\n' == c1 || (('\r' == c1) && ('\n' != c2))) {
                }
            } else if (STATE.LINECOMMENT == states.peek()) {

                if ('\n' == c1 || (('\r' == c1) && ('\n' != c2))) {
                    states.pop();
                }
            } else if (STATE.DOUBLEQUOTELITERAL == states.peek()) {

                if (!escape && ('\"' == c1)) {
                    states.pop();
                } else if ('\\' == c1) {
                    escape = !escape;
                } else {
                    escape = false;
                }
            } else if (STATE.SINGLEQUOTELITERAL == states.peek()) {

                if (!escape && ('\'' == c1)) {
                    states.pop();
                } else if ('\\' == c1) {
                    escape = !escape;
                } else {
                    escape = false;
                }

            } else if (STATE.CODE == states.peek()) {

                assert !escape : "illegal state.";
                if (('/' == c1) && ('*' == c2)) {
                    states.push(STATE.BLOCKCOMMENT);
                }

                if (('/' == c1) && ('/' == c2)) {
                    states.push(STATE.LINECOMMENT);
                } else if ('\"' == c1) {
                    states.push(STATE.DOUBLEQUOTELITERAL);
                } else if ('\'' == c1) {
                    states.push(STATE.SINGLEQUOTELITERAL);
                }
            }
        }

        assert 0 == line.length() : "illegal state.";
        dest.deleteCharAt(dest.length() - 1);
        return dest.toString();
    }

    private String deleteIndent(final String src) {

        final StringBuilder dest = new StringBuilder();
        boolean indent = true;

        for (int index = 0; index < src.length(); index++) {
            final char c1 = src.charAt(index);
            final char c2 = (index + 1) < src.length() ? src.charAt(index + 1) : '0';

            if (' ' == c1 || '\t' == c1) {
                if (!indent) {
                    dest.append(c1);
                }
            } else if ('\n' == c1 || (('\r' == c1) && ('\n' != c2))) {
                dest.append(c1);
                indent = true;
            } else {
                indent = false;
                dest.append(c1);
            }
        }

        return dest.toString();
    }
}
