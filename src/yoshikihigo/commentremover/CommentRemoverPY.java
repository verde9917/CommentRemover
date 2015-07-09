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
import java.util.Stack;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class CommentRemoverPY {

	enum STATE {
		CODE, BLOCKCOMMENT, LINECOMMENT, INDOUBLEQUOTE, INSINGLEQUOTE;
	}

	private static final String LINE_SEPARATOR = System
			.getProperty("line.separator");

	public String result;

	public CommentRemoverPY() {
		this.result = null;
	}

	public void perform(final String[] args) {

		try {

			final Options options = new Options();

			{
				final Option i = new Option("i", "input", true,
						"input directory");
				i.setArgName("input");
				i.setArgs(1);
				i.setRequired(true);
				options.addOption(i);
			}

			{
				final Option o = new Option("o", "output", true,
						"output directory");
				o.setArgName("output");
				o.setArgs(1);
				o.setRequired(false);
				options.addOption(o);
			}

			{
				final Option x = new Option("x", "encoding", true, "encoding");
				x.setArgName("encoding");
				x.setArgs(1);
				x.setRequired(false);
				options.addOption(x);
			}

			{
				final Option a = new Option("a", "blankline", false,
						"blank line");
				a.setArgName("blankline");
				a.setRequired(false);
				options.addOption(a);
			}

			{
				final Option b = new Option("b", "blockcomment", false,
						"block comment");
				b.setArgName("blockcomment");
				b.setRequired(false);
				options.addOption(b);
			}

			{
				final Option c = new Option("c", "linecomment", false,
						"line comment");
				c.setArgName("linecomment");
				c.setRequired(false);
				options.addOption(c);
			}

			{
				final Option d = new Option("d", "bracketline", false,
						"bracket line");
				d.setArgName("bracketline");
				d.setRequired(false);
				options.addOption(d);
			}

			{
				final Option v = new Option("v", "verbose", false,
						"verbose output");
				v.setArgName("verbose");
				v.setRequired(false);
				options.addOption(v);
			}

			{
				final Option q = new Option("q", "quiet", false, "quiet");
				q.setArgName("quiet");
				q.setRequired(false);
				options.addOption(q);
			}

			final CommandLineParser parser = new PosixParser();
			final CommandLine cmd = parser.parse(options, args);

			final String optionI = cmd.getOptionValue("i");
			final String optionO = cmd.hasOption("o") ? cmd.getOptionValue("o")
					: null;
			final String optionL = cmd.hasOption("l") ? cmd.getOptionValue("l")
					: null;
			final File inputFile = new File(optionI);

			// if a file path is specified ...
			if (inputFile.isFile()) {

				final String text = readFile(inputFile,
						cmd.hasOption("x") ? cmd.getOptionValue("x") : null);

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

				int index = 0;
				final Set<File> files = getFiles(new File(optionI));
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
				// if (!cmd.hasOption("a")) {
				// text = deleteBlankLine(text);
				// }

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
		final CommentRemoverPY remover = new CommentRemoverPY();
		remover.perform(args);
	}

	public static Set<File> getFiles(final File file) {

		final Set<File> files = new HashSet<File>();

		if (file.isDirectory()) {
			final File[] subfiles = file.listFiles();
			for (int i = 0; i < subfiles.length; i++) {
				files.addAll(getFiles(subfiles[i]));
			}
		}

		else if (file.isFile()) {

			final String path = file.getAbsolutePath();
			if (path.endsWith(".py")) {
				files.add(file);
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

		final Stack<STATE> states = new Stack<>();
		states.push(STATE.CODE);

		for (int index = 0; index < src.length(); index++) {
			final char c = src.charAt(index);
			final char next = ((index + 1) < src.length()) ? src
					.charAt(index + 1) : '0';

			if (STATE.LINECOMMENT == states.peek()) {
				if ('\n' == c || ((c == '\r') && ('\n' != next))) {
					states.pop();
					buf.append(LINE_SEPARATOR);
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

	private static String deleteBlockComment(final String src) {

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
					buf.append(LINE_SEPARATOR);
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
					buf.append(LINE_SEPARATOR);
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
