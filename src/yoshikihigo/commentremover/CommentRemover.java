package yoshikihigo.commentremover;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.SortedSet;

abstract public class CommentRemover {

	public static void main(final String[] args) {

		Config config = Config.initialize(args);

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

	final protected Config config;
	final protected String lineSeparator;

	CommentRemover(final Config config) {
		this.config = config;
		this.lineSeparator = System.lineSeparator();
	}

	abstract public String deleteLineComment(final String text);

	abstract public String deleteBlockComment(final String text);

	final public String deleteBlankLine(final String text) {

		StringBuilder result = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new StringReader(text))) {

			while (true) {
				final String line = reader.readLine();
				if (null == line) {
					break;
				}
				if (!line.matches("^\\s*$")) {
					result.append(line);
					result.append(this.lineSeparator);
				}
			}
		}

		catch (final IOException e) {
			e.printStackTrace();
			System.exit(0);
		}

		return result.toString().replaceFirst("\\s*$", "");
	}

	abstract public String perform(final String text);
}
