package yoshikihigo.commentremover;

import java.io.File;
import java.util.SortedSet;

abstract public class CommentRemover {

	public static void main(final String[] args) {

		CRConfig config = CRConfig.initialize(args);

		if (!config.hasINPUT()) {
			System.err
					.println("option \"-input\" is require to specify your target source files.");
			System.exit(0);
		}
		final String input = config.getINPUT();

		if (!config.hasOUTPUT()) {
			System.err
					.println("option \"-output\" is require to specify this tool's output directory.");
			System.exit(0);
		}
		final String output = config.getOUTPUT();

		final File inputFile = new File(input);
		if (inputFile.isFile()) {

			CommentRemover remover = null;
			if (LANGUAGE.C.isTargetFile(input)) {
				remover = new CommentRemoverJC(config);
			} else if (LANGUAGE.CPP.isTargetFile(input)) {
				remover = new CommentRemoverJC(config);
			} else if (LANGUAGE.JAVA.isTargetFile(input)) {
				remover = new CommentRemoverJC(config);
			} else if (LANGUAGE.JAVASCRIPT.isTargetFile(input)) {
				remover = new CommentRemoverJS(config);
			} else if (LANGUAGE.PYTHON.isTargetFile(input)) {
				remover = new CommentRemoverPY(config);
			}

			if (null != remover) {
				final String text = FileUtility.readFile(inputFile,
						config.hasENCODING() ? config.getENCODING() : null);
				final String result = remover.perform(text);
				FileUtility.writeFile(result, output);
			}
		}

		else if (inputFile.isDirectory()) {

			int index = 0;
			final LANGUAGE language = config.getLANGUAGE();
			final SortedSet<File> files = FileUtility.getFiles(inputFile,
					language);

			for (final File file : files) {

				if (config.isVERBOSE()) {
					final StringBuilder text = new StringBuilder();
					text.append(" [");
					text.append(Integer.toString(index++ + 1));
					text.append("/");
					text.append(Integer.toString(files.size()));
					text.append("] ");
					text.append(file.getAbsolutePath());
					System.err.println(text.toString());
				}

				final String outputPath = file.getAbsolutePath().replace(
						config.getINPUT(), config.getOUTPUT());

				final String[] newArgs = new String[args.length];
				for (int i = 0; i < args.length; i++) {
					if (args[i].equals(config.getINPUT())) {
						newArgs[i] = file.getAbsolutePath();
					} else if (args[i].equals(config.getOUTPUT())) {
						newArgs[i] = outputPath;
					} else {
						newArgs[i] = args[i];
					}
				}

				main(newArgs);
			}
		}
	}

	final protected CRConfig config;

	protected CommentRemover(final CRConfig config) {
		this.config = config;
	}

	abstract public String deleteLineComment(final String text);

	abstract public String deleteBlockComment(final String text);

	abstract public String perform(final String text);

	final public String deleteBlankLine(final String text) {

		final StringBuilder result = new StringBuilder();
		final StringBuilder line = new StringBuilder();
		boolean blankline = true;

		for (int index = 0; index < text.length(); index++) {
			final char c1 = text.charAt(index);
			final char c2 = (index + 1) < text.length() ? text
					.charAt(index + 1) : '0';

			if (('\n' == c1) || (('\r' == c1) && ('\n' != c2))) {
				if (!blankline) {
					line.append(c1);
					result.append(line.toString());
				}
				if (0 < line.length()) {
					line.delete(0, line.length());
				}
				blankline = true;
			}

			else if ((' ' == c1) || ('\t' == c1) || ('\r' == c1)) {
				line.append(c1);
			}

			else {
				line.append(c1);
				blankline = false;
			}
		}

		return result.toString();
	}
}
