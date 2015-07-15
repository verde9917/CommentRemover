package yoshikihigo.commentremover;

import java.util.Stack;

public class CommentRemoverPY extends CommentRemover {

	enum STATE {
		CODE, DOUBLEQUOTEBLOCKCOMMENT, SINGLEQUOTEBLOCKCOMMENT, LINECOMMENT, DOUBLEQUOTELITERAL, SINGLEQUOTELITERAL;
	}

	public CommentRemoverPY(final CRConfig config) {
		super(config);
	}

	public String perform(final String text) {

		String result = text;
		if (!this.config.hasLINECOMMENT()) {
			result = deleteLineComment(result);
		}
		if (!this.config.hasBLOCKCOMMENT()) {
			result = deleteBlockComment(result);
		}
		if (!this.config.hasBLANKLINE()) {
			result = deleteBlankLine(result);
		}
		return result;
	}

	@Override
	public String deleteLineComment(final String src) {

		StringBuilder buf = new StringBuilder();

		final Stack<STATE> states = new Stack<>();
		states.push(STATE.CODE);

		int index = 0;
		for (; index < src.length(); index++) {
			final char c1 = src.charAt(index);
			final char c2 = ((index + 1) < src.length()) ? src
					.charAt(index + 1) : ' ';
			final char c3 = ((index + 2) < src.length()) ? src
					.charAt(index + 2) : ' ';

			if (STATE.DOUBLEQUOTEBLOCKCOMMENT == states.peek()) {

				buf.append(c1);

				if ('\"' == c1 && '\"' == c2 && '\"' == c3) {
					states.pop();
					buf.append(c2);
					buf.append(c3);
					index += 2;
				}

			} else if (STATE.SINGLEQUOTEBLOCKCOMMENT == states.peek()) {

				buf.append(c1);

				if ('\'' == c1 && '\'' == c2 && '\'' == c3) {
					states.pop();
					buf.append(c2);
					buf.append(c3);
					index += 2;
				}

			} else if (STATE.LINECOMMENT == states.peek()) {

				if (('\n' == c1) || ((c1 == '\r') && ('\n' != c2))) {
					states.pop();
					buf.append(this.lineSeparator);
				}
			}

			else if (STATE.DOUBLEQUOTELITERAL == states.peek()) {

				buf.append(c1);

				if ('\"' == c1) {
					states.pop();
				}

				else if (('\\' == c1) && ('\"' == c2)) {
					buf.append(c2);
					index++;
				}

				else if ((('\\') == c1) && ('\\' == c2)) {
					buf.append(c2);
					index++;
				}
			}

			else if (STATE.SINGLEQUOTELITERAL == states.peek()) {

				buf.append(c1);

				if ('\'' == c1) {
					states.pop();
				}

				else if (('\\' == c1) && ('\'' == c2)) {
					buf.append(c2);
					index++;
				}

				else if ((('\\') == c1) && ('\\' == c2)) {
					buf.append(c2);
					index++;
				}
			}

			else if (STATE.CODE == states.peek()) {

				if ('\"' == c1 && '\"' == c2 && '\"' == c3) {
					states.push(STATE.DOUBLEQUOTEBLOCKCOMMENT);
					buf.append(c1);
					buf.append(c2);
					buf.append(c3);
					index += 2;
				}

				else if ('\'' == c1 && '\'' == c2 && '\'' == c3) {
					states.push(STATE.SINGLEQUOTEBLOCKCOMMENT);
					buf.append(c1);
					buf.append(c2);
					buf.append(c3);
					index += 2;
				}

				else if ('#' == c1) {
					states.push(STATE.LINECOMMENT);
				}

				else if ('\"' == c1) {
					states.push(STATE.DOUBLEQUOTELITERAL);
					buf.append(c1);
				}

				else if ('\'' == c1) {
					states.push(STATE.SINGLEQUOTELITERAL);
					buf.append(c1);
				}

				else {
					buf.append(c1);
				}
			}
		}

		return buf.toString();
	}

	@Override
	public String deleteBlockComment(final String src) {

		StringBuilder buf = new StringBuilder();

		final Stack<STATE> states = new Stack<>();
		states.push(STATE.CODE);

		int index = 0;
		for (; index < src.length(); index++) {
			final char c1 = src.charAt(index);
			final char c2 = ((index + 1) < src.length()) ? src
					.charAt(index + 1) : ' ';
			final char c3 = ((index + 2) < src.length()) ? src
					.charAt(index + 2) : ' ';

			if (STATE.SINGLEQUOTEBLOCKCOMMENT == states.peek()) {

				if (('\'' == c1) && ('\'' == c2) && ('\'' == c3)) {
					states.pop();
					index += 2;
				}

				else if (('\"' == c1) && ('\"' == c2) && ('\"' == c3)) {
					states.push(STATE.DOUBLEQUOTEBLOCKCOMMENT);
					index += 2;
				}

				else if ('\n' == c1 || (('\r' == c1) && ('\n' != c2))) {
					buf.append(this.lineSeparator);
				}
			}

			else if (STATE.DOUBLEQUOTEBLOCKCOMMENT == states.peek()) {

				if (('\'' == c1) && ('\'' == c2) && ('\'' == c3)) {
					states.push(STATE.SINGLEQUOTEBLOCKCOMMENT);
					index += 2;
				}

				else if (('\"' == c1) && ('\"' == c2) && ('\"' == c3)) {
					states.pop();
					index += 2;
				}

				else if ('\n' == c1 || (('\r' == c1) && ('\n' != c2))) {
					buf.append(this.lineSeparator);
				}
			}

			else if (STATE.SINGLEQUOTELITERAL == states.peek()) {

				buf.append(c1);

				if ('\'' == c1) {
					states.pop();
				}

				else if (('\\' == c1) && ('\'' == c2)) {
					buf.append(c2);
					index++;
				}

				else if ((('\\') == c1) && ('\\' == c2)) {
					buf.append(c2);
					index++;
				}
			}

			else if (STATE.DOUBLEQUOTELITERAL == states.peek()) {

				buf.append(c1);

				if ('\"' == c1) {
					states.pop();
				}

				else if (('\\' == c1) && ('\"' == c2)) {
					buf.append(c2);
					index++;
				}

				else if ((('\\') == c1) && ('\\' == c2)) {
					buf.append(c2);
					index++;
				}
			}

			else if (STATE.CODE == states.peek()) {

				if (('\'' == c1) && ('\'' == c2) && ('\'' == c3)) {
					states.push(STATE.SINGLEQUOTEBLOCKCOMMENT);
					index += 2;
				}

				else if (('\"' == c1) && ('\"' == c2) && ('\"' == c3)) {
					states.push(STATE.DOUBLEQUOTEBLOCKCOMMENT);
					index += 2;
				}

				else if ('\'' == c1) {
					buf.append(c1);
					states.push(STATE.SINGLEQUOTELITERAL);
				}

				else if ('\"' == c1) {
					buf.append(c1);
					states.push(STATE.DOUBLEQUOTELITERAL);
				}

				else {
					buf.append(c1);
				}
			}
		}

		return buf.toString();
	}
}
