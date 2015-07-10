package yoshikihigo.commentremover;

import java.util.StringTokenizer;

public class CommentRemoverJC extends CommentRemover {

	enum STATE {
		CODE, BLOCKCOMMENT, LINECOMMENT, STRING, CHAR;
	}

	public CommentRemoverJC(final Config config) {
		super(config);
	}

	@Override
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
		if (!this.config.hasBLACKETLINE()) {
			result = deleteBracketLine(result);
		}
		if (!this.config.hasINDENT()) {
			result = deleteIndent(result);
		}
		return result;
	}

	@Override
	public String deleteLineComment(final String text) {

		StringBuilder buf = new StringBuilder();

		STATE currentSTATE = STATE.CODE;

		for (int i = 0; i < text.length(); i++) {
			final char prev = 0 < i ? text.charAt(i - 1) : '0';
			final char ch = text.charAt(i);
			final char next = (i < text.length() - 1) ? text.charAt(i + 1)
					: '0';

			if (STATE.BLOCKCOMMENT == currentSTATE) {

				buf.append(ch);

				if (prev == '*' && ch == '/') {
					currentSTATE = STATE.CODE;
				}
			}

			else if (STATE.LINECOMMENT == currentSTATE) {
				if (ch == '\n' || ((ch != '\n') && (prev == '\r'))) {
					currentSTATE = STATE.CODE;
					buf.append(this.lineSeparator);
				}
			}

			else if (STATE.STRING == currentSTATE) {
				buf.append(ch);

				if (ch == '\\') {
					buf.append(text.charAt(i++));
				}

				else if (ch == '\"') {
					currentSTATE = STATE.CODE;
				}
			}

			else if (STATE.CHAR == currentSTATE) {
				buf.append(ch);

				if (ch == '\\') {
					buf.append(text.charAt(i++));
				}

				else if (ch == '\'') {
					currentSTATE = STATE.CODE;
				}
			}

			else if (ch == '/' && next == '*') {
				currentSTATE = STATE.BLOCKCOMMENT;
				buf.append("/*");
				i++;
			}

			else if (ch == '/' && next == '/') {
				currentSTATE = STATE.LINECOMMENT;
			}

			else if (ch == '\"') {
				currentSTATE = STATE.STRING;
				buf.append(ch);
			}

			else if (ch == '\'') {
				currentSTATE = STATE.CHAR;
				buf.append(ch);
			}

			else {
				buf.append(ch);
			}
		}

		return buf.toString();
	}

	@Override
	public String deleteBlockComment(final String text) {

		StringBuilder buf = new StringBuilder();

		STATE currentSTATE = STATE.CODE;

		for (int i = 0; i < text.length(); i++) {
			final char prev = 0 < i ? text.charAt(i - 1) : '0';
			final char ch = text.charAt(i);
			final char next = (i < text.length() - 1) ? text.charAt(i + 1)
					: '0';

			if (STATE.BLOCKCOMMENT == currentSTATE) {
				if (prev == '*' && ch == '/') {
					currentSTATE = STATE.CODE;
					buf.append(" ");
				}

				else if (ch == '\n' || ((ch != '\n') && (prev == '\r'))) {
					buf.append(this.lineSeparator);
				}
			}

			else if (STATE.LINECOMMENT == currentSTATE) {
				buf.append(ch);

				if (ch == '\n' || ((ch != '\n') && (prev == '\r'))) {
					currentSTATE = STATE.CODE;
				}
			}

			else if (STATE.STRING == currentSTATE) {
				buf.append(ch);

				if (ch == '\\') {
					buf.append(text.charAt(i++));
				}

				else if (ch == '\"') {
					currentSTATE = STATE.CODE;
				}
			}

			else if (STATE.CHAR == currentSTATE) {
				buf.append(ch);

				if (ch == '\\') {
					buf.append(text.charAt(i++));
				}

				else if (ch == '\'') {
					currentSTATE = STATE.CODE;
				}
			}

			else if (ch == '/' && next == '*') {
				currentSTATE = STATE.BLOCKCOMMENT;
				i++;
			}

			else if (ch == '\"') {
				currentSTATE = STATE.STRING;
				buf.append(ch);
			}

			else if (ch == '\'') {
				currentSTATE = STATE.CHAR;
				buf.append(ch);
			}

			else {
				buf.append(ch);
			}
		}

		return buf.toString();
	}

	public String deleteBracketLine(final String src) {

		final StringBuilder text = new StringBuilder();
		for (final StringTokenizer tokenizer = new StringTokenizer(src,
				this.lineSeparator); tokenizer.hasMoreTokens();) {

			final String line = tokenizer.nextToken();
			if (line.matches("[ \t]*[{][ \t]*")) {
				text.append("{");
			}

			else if (line.matches("[ \t]*[}][ \t]*")) {
				text.append("}");
			}

			else {

				if (0 < text.length()) {
					text.append(this.lineSeparator);
				}
				text.append(line);
			}
		}

		return text.toString();
	}

	private String deleteIndent(final String text) {
		return text.replaceAll(this.lineSeparator + "[ \t]+",
				this.lineSeparator);
	}
}
