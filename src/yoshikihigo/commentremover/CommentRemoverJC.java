package yoshikihigo.commentremover;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class CommentRemoverJC extends CommentRemover {

	enum STATE {
		CODE, BLOCKCOMMENT, LINECOMMENT, STRING, CHAR;
	}

	private static final String LINE_SEPARATOR = System
			.getProperty("line.separator");

	public String result;

	@Override
	public void perform(final Config config) {

		if(!config.hasINPUT()){
			System.err.println("input file is not specified.");
			System.exit(0);
		}
		final String optionI = config.getINPUT();
		final File inputFile = new File(optionI);

		// if a file path is specified ...
		if (inputFile.isFile()) {

			final String text = readFile(inputFile,
					config.hasENCODING() ? config.getENCODING() : null);

				final String[] newArgs = new String[args.length];
				for (int i = 0; i < args.length; i++) {
					if (args[i].equals(optionI)) {
						newArgs[i] = text;
					} else {
						newArgs[i] = args[i];
					}
				}

				main(newArgs);
			}

			// if a directory path is specified ...
			else if (inputFile.isDirectory()) {

				if (null == optionO) {
					System.out
							.println("specify an output file with option \"-o\" when you specify a directory as an input.");
					System.exit(0);
				}

				if (null == optionL) {
					System.out
							.println("specify a programming language with option \"-l\" when you specify a directory as an input");
					System.exit(0);
				}

				int index = 0;
				final Set<File> files = getFiles(new File(optionI), optionL);
				for (final File file : files) {

					if (cmd.hasOption("v")) {
						System.out.print(" [");
						System.out.print(index++ + 1);
						System.out.print("/");
						System.out.print(files.size());
						System.out.print("] ");
					}

					final String outputPath = file.getAbsolutePath().replace(
							optionI, optionO);
					if (cmd.hasOption("v")) {
						System.out.println(outputPath);
					}

					final String[] newArgs = new String[args.length];
					for (int i = 0; i < args.length; i++) {
						if (args[i].equals(optionI)) {
							newArgs[i] = file.getAbsolutePath();
						} else if (args[i].equals(optionO)) {
							newArgs[i] = outputPath;
						} else {
							newArgs[i] = args[i];
						}
					}

					main(newArgs);
				}

			}

			// if a file content is specified ...
			else {

				String text = optionI;
				if (!cmd.hasOption("c")) {
					text = deleteLineComment(text);
				}
				if (!cmd.hasOption("b")) {
					text = deleteBlockComment(text);
				}
				if (!cmd.hasOption("a")) {
					text = deleteBlankLine(text);
				}
				if (!cmd.hasOption("d")) {
					text = deleteBracketLine(text);
				}
				if (!cmd.hasOption("e")) {
					text = deleteIndent(text);
				}

				if (!cmd.hasOption("q")) {
					writeFile(text, optionO);
				}
				this.result = text;
			}

		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(0);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			System.exit(0);
		}
	}

	public static void main(String[] args) throws IOException {
		final CommentRemoverJC remover = new CommentRemoverJC();
		remover.perform(args);
	}

	public static Set<File> getFiles(final File file, final String language) {

		final Set<File> files = new HashSet<File>();

		if (file.isDirectory()) {
			final File[] subfiles = file.listFiles();
			for (int i = 0; i < subfiles.length; i++) {
				files.addAll(getFiles(subfiles[i], language));
			}
		}

		else if (file.isFile()) {

			final String path = file.getAbsolutePath();
			if (language.equals("java")) {
				if (path.endsWith(".java")) {
					files.add(file);
				}
			} else if (language.equals("csharp")) {
				if (path.endsWith(".cs")) {
					files.add(file);
				}
			} else if (language.equals("c")) {
				if (path.endsWith(".c") || path.endsWith("cpp")
						|| path.endsWith("cxx") || path.endsWith(".h")
						|| path.endsWith(".hpp") || path.endsWith(".hxx")) {
					files.add(file);
				}
			}
		}

		else {
			System.err.println("\"" + file.getAbsolutePath()
					+ "\" is not a vaild file!");
			System.exit(0);
		}

		return files;
	}

	public static String readFile(final File file, final String encoding) {

		try {

			final StringBuilder text = new StringBuilder();
			final InputStreamReader reader = new InputStreamReader(
					new FileInputStream(file), null != encoding ? encoding
							: "JISAutoDetect");
			while (reader.ready()) {
				final int c = reader.read();
				text.append((char) c);
			}
			reader.close();

			return text.toString();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static String deleteLineComment(final String src) {

		StringBuilder buf = new StringBuilder();

		STATE currentSTATE = STATE.CODE;

		for (int i = 0; i < src.length(); i++) {
			final char prev = 0 < i ? src.charAt(i - 1) : '0';
			final char ch = src.charAt(i);
			final char next = (i < src.length() - 1) ? src.charAt(i + 1) : '0';

			if (STATE.BLOCKCOMMENT == currentSTATE) {

				buf.append(ch);

				if (prev == '*' && ch == '/') {
					currentSTATE = STATE.CODE;
				}
			}

			else if (STATE.LINECOMMENT == currentSTATE) {
				// if (ch == LINE_SEPARATOR.charAt(0)) {
				if (ch == '\n' || ((ch != '\n') && (prev == '\r'))) {
					currentSTATE = STATE.CODE;
					buf.append(LINE_SEPARATOR);
				}
			}

			else if (STATE.STRING == currentSTATE) {
				buf.append(ch);

				if (ch == '\\') {
					buf.append(src.charAt(i++));
				}

				else if (ch == '\"') {
					currentSTATE = STATE.CODE;
				}
			}

			else if (STATE.CHAR == currentSTATE) {
				buf.append(ch);

				if (ch == '\\') {
					buf.append(src.charAt(i++));
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

	private static String deleteBlockComment(final String src) {

		StringBuilder buf = new StringBuilder();

		STATE currentSTATE = STATE.CODE;

		for (int i = 0; i < src.length(); i++) {
			final char prev = 0 < i ? src.charAt(i - 1) : '0';
			final char ch = src.charAt(i);
			final char next = (i < src.length() - 1) ? src.charAt(i + 1) : '0';

			if (STATE.BLOCKCOMMENT == currentSTATE) {
				if (prev == '*' && ch == '/') {
					currentSTATE = STATE.CODE;
					buf.append(" ");
				}

				else if (ch == '\n' || ((ch != '\n') && (prev == '\r'))) {
					buf.append(LINE_SEPARATOR);
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
					buf.append(src.charAt(i++));
				}

				else if (ch == '\"') {
					currentSTATE = STATE.CODE;
				}
			}

			else if (STATE.CHAR == currentSTATE) {
				buf.append(ch);

				if (ch == '\\') {
					buf.append(src.charAt(i++));
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

	private static String deleteBlankLine(final String src) throws IOException {

		StringBuilder buf = new StringBuilder();
		BufferedReader reader = new BufferedReader(new StringReader(src));

		String inLine;
		while ((inLine = reader.readLine()) != null) {
			if (!inLine.matches("^\\s*$")) {
				buf.append(inLine);
				buf.append(LINE_SEPARATOR);
			}
		}

		return buf.toString().replaceFirst("\\s*$", "");
	}

	private static String deleteBracketLine(final String src) {

		final StringBuilder text = new StringBuilder();
		for (final StringTokenizer tokenizer = new StringTokenizer(src,
				LINE_SEPARATOR); tokenizer.hasMoreTokens();) {

			final String line = tokenizer.nextToken();
			if (line.matches("[ \t]*[{][ \t]*")) {
				text.append("{");
			}

			else if (line.matches("[ \t]*[}][ \t]*")) {
				text.append("}");
			}

			else {

				if (0 < text.length()) {
					text.append(LINE_SEPARATOR);
				}
				text.append(line);
			}
		}

		return text.toString();
	}

	private static String deleteIndent(final String src) {
		return src.replaceAll(LINE_SEPARATOR + "[ \t]+", LINE_SEPARATOR);
	}

	public static void writeFile(final String text, final String path) {

		try {

			if (null != path) {
				final File file = new File(path);
				file.getParentFile().mkdirs();
			}

			final OutputStream out = null != path ? new FileOutputStream(path)
					: System.out;
			for (int i = 0; i < text.length(); i++) {
				out.write(text.charAt(i));
			}
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
