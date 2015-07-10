package yoshikihigo.commentremover;

import java.util.Stack;

public class CommentRemoverPY extends CommentRemover {

	enum STATE {
		CODE, BLOCKCOMMENT, LINECOMMENT, INDOUBLEQUOTE, INSINGLEQUOTE;
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

		for (int index = 0; index < src.length(); index++) {
			final char c = src.charAt(index);
			final char next = ((index + 1) < src.length()) ? src
					.charAt(index + 1) : '0';

			if (STATE.LINECOMMENT == states.peek()) {
				if ('\n' == c || ((c == '\r') && ('\n' != next))) {
					states.pop();
					buf.append(this.lineSeparator);
				}
			}

			else if (STATE.INDOUBLEQUOTE == states.peek()) {

				buf.append(c);

				if ('\"' == c) {
					states.pop();
				}

				else if (('\\' == c) && ('\"' == next)) {
					buf.append(next);
					index++;
				}

			}

			else if (STATE.INSINGLEQUOTE == states.peek()) {

				buf.append(c);

				if ('\'' == c) {
					states.pop();
				}

				else if (('\\' == c) && ('\'' == next)) {
					buf.append(next);
					index++;
				}
			}

			else if (STATE.CODE == states.peek()) {

				if ('#' == c) {
					states.push(STATE.LINECOMMENT);
				}

				else if ('\"' == c) {
					states.push(STATE.INDOUBLEQUOTE);
					buf.append(c);
				}

				else if ('\'' == c) {
					states.push(STATE.INSINGLEQUOTE);
					buf.append(c);
				}

				else {
					buf.append(c);
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
		for (; (index + 2) < src.length(); index++) {
			final char c1 = src.charAt(index);
			final char c2 = src.charAt(index + 1);
			final char c3 = src.charAt(index + 2);

			if (STATE.INSINGLEQUOTE == states.peek()) {

				if (('\'' == c1) && ('\'' == c2) && ('\'' == c3)) {
					states.pop();
					index += 2;
				}

				else if (('\"' == c1) && ('\"' == c2) && ('\"' == c3)) {
					states.push(STATE.INDOUBLEQUOTE);
					index += 2;
				}

				else if ('\n' == c1 || (('\r' == c1) && ('\n' != c2))) {
					buf.append(this.lineSeparator);
				}
			}

			else if (STATE.INDOUBLEQUOTE == states.peek()) {

				if (('\'' == c1) && ('\'' == c2) && ('\'' == c3)) {
					states.push(STATE.INSINGLEQUOTE);
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

			else if (STATE.CODE == states.peek()) {

				if (('\'' == c1) && ('\'' == c2) && ('\'' == c3)) {
					states.push(STATE.INSINGLEQUOTE);
					index += 2;
				}

				else if (('\"' == c1) && ('\"' == c2) && ('\"' == c3)) {
					states.push(STATE.INDOUBLEQUOTE);
					index += 2;
				}

				else {
					buf.append(c1);
				}
			}
		}

		for (; index < src.length(); index++) {
			char c = src.charAt(index);
			buf.append(c);
		}

		return buf.toString();
	}
}
