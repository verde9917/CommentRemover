package yoshikihigo.commentremover;

import java.util.Stack;

public class CommentRemoverPY extends CommentRemover {

	enum STATE {
		CODE, DOUBLEQUOTEBLOCKCOMMENT, SINGLEQUOTEBLOCKCOMMENT, LINECOMMENT, DOUBLEQUOTELITERAL, SINGLEQUOTELITERAL;
	}

	public CommentRemoverPY(final CRConfig config) {
		super(config);
	}

	public String perform(final String src) {

		String dest = src;
		if (CRConfig.OPERATION.REMOVE == this.config.getLINECOMMENT()) {
			dest = deleteLineComment(dest);
		}
		if (CRConfig.OPERATION.REMOVE == this.config.getBLOCKCOMMENT()) {
			dest = deleteBlockComment(dest);
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

		int index = 0;
		for (; index < src.length(); index++) {
			final char c1 = src.charAt(index);
			final char c2 = ((index + 1) < src.length()) ? src
					.charAt(index + 1) : ' ';
			final char c3 = ((index + 2) < src.length()) ? src
					.charAt(index + 2) : ' ';

			if (STATE.DOUBLEQUOTEBLOCKCOMMENT == states.peek()) {

				dest.append(c1);

				if ('\"' == c1 && '\"' == c2 && '\"' == c3) {
					states.pop();
					dest.append(c2);
					dest.append(c3);
					index += 2;
				}

			} else if (STATE.SINGLEQUOTEBLOCKCOMMENT == states.peek()) {

				dest.append(c1);

				if ('\'' == c1 && '\'' == c2 && '\'' == c3) {
					states.pop();
					dest.append(c2);
					dest.append(c3);
					index += 2;
				}

			} else if (STATE.LINECOMMENT == states.peek()) {

				if (('\n' == c1) || ((c1 == '\r') && ('\n' != c2))) {
					states.pop();
					dest.append(c1);
				}
			}

			else if (STATE.DOUBLEQUOTELITERAL == states.peek()) {

				dest.append(c1);

				if ('\"' == c1) {
					states.pop();
				}

				else if (('\\' == c1) && ('\"' == c2)) {
					dest.append(c2);
					index++;
				}

				else if ((('\\') == c1) && ('\\' == c2)) {
					dest.append(c2);
					index++;
				}
			}

			else if (STATE.SINGLEQUOTELITERAL == states.peek()) {

				dest.append(c1);

				if ('\'' == c1) {
					states.pop();
				}

				else if (('\\' == c1) && ('\'' == c2)) {
					dest.append(c2);
					index++;
				}

				else if ((('\\') == c1) && ('\\' == c2)) {
					dest.append(c2);
					index++;
				}
			}

			else if (STATE.CODE == states.peek()) {

				if ('\"' == c1 && '\"' == c2 && '\"' == c3) {
					states.push(STATE.DOUBLEQUOTEBLOCKCOMMENT);
					dest.append(c1);
					dest.append(c2);
					dest.append(c3);
					index += 2;
				}

				else if ('\'' == c1 && '\'' == c2 && '\'' == c3) {
					states.push(STATE.SINGLEQUOTEBLOCKCOMMENT);
					dest.append(c1);
					dest.append(c2);
					dest.append(c3);
					index += 2;
				}

				else if ('#' == c1) {
					states.push(STATE.LINECOMMENT);
				}

				else if ('\"' == c1) {
					states.push(STATE.DOUBLEQUOTELITERAL);
					dest.append(c1);
				}

				else if ('\'' == c1) {
					states.push(STATE.SINGLEQUOTELITERAL);
					dest.append(c1);
				}

				else {
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
					dest.append(c1);
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
					dest.append(c1);
				}
			}

			else if (STATE.SINGLEQUOTELITERAL == states.peek()) {

				dest.append(c1);

				if ('\'' == c1) {
					states.pop();
				}

				else if (('\\' == c1) && ('\'' == c2)) {
					dest.append(c2);
					index++;
				}

				else if ((('\\') == c1) && ('\\' == c2)) {
					dest.append(c2);
					index++;
				}
			}

			else if (STATE.DOUBLEQUOTELITERAL == states.peek()) {

				dest.append(c1);

				if ('\"' == c1) {
					states.pop();
				}

				else if (('\\' == c1) && ('\"' == c2)) {
					dest.append(c2);
					index++;
				}

				else if ((('\\') == c1) && ('\\' == c2)) {
					dest.append(c2);
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
					dest.append(c1);
					states.push(STATE.SINGLEQUOTELITERAL);
				}

				else if ('\"' == c1) {
					dest.append(c1);
					states.push(STATE.DOUBLEQUOTELITERAL);
				}

				else {
					dest.append(c1);
				}
			}
		}

		return dest.toString();
	}
}
