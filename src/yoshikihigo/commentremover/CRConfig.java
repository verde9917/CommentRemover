package yoshikihigo.commentremover;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class CRConfig {

	public static enum OPERATION {
		REMOVE("remove"), RETAIN("retain");

		final private String value;

		private OPERATION(final String value) {
			this.value = value;
		}

		static public OPERATION getOPERATION(final String value) {
			if (value.equalsIgnoreCase("remove")) {
				return REMOVE;
			} else if (value.equalsIgnoreCase("retain")) {
				return RETAIN;
			} else {
				assert false : "illegal value: " + value;
				return null;
			}
		}
	}

	static public CRConfig initialize(final String[] args) {

		final Options options = new Options();

		{
			final Option input = new Option("input", "input", true,
					"input directory");
			input.setArgName("input");
			input.setArgs(1);
			input.setRequired(false);
			options.addOption(input);
		}

		{
			final Option output = new Option("output", "output", true,
					"output directory");
			output.setArgName("output");
			output.setArgs(1);
			output.setRequired(false);
			options.addOption(output);
		}

		{
			final Option language = new Option("lang", "language", true,
					"language");
			language.setArgName("language");
			language.setArgs(1);
			language.setRequired(false);
			options.addOption(language);
		}

		{
			final Option encoding = new Option("encoding", "encoding", true,
					"encoding");
			encoding.setArgName("encoding");
			encoding.setArgs(1);
			encoding.setRequired(false);
			options.addOption(encoding);
		}

		{
			final Option blankline = new Option("blankline", "blankline",
					false, "blankline");
			blankline.setArgName("blankline");
			blankline.setArgs(1);
			blankline.setRequired(false);
			options.addOption(blankline);
		}

		{
			final Option blockcomment = new Option("blockcomment",
					"blockcomment", false, "block comment");
			blockcomment.setArgName("blockcomment");
			blockcomment.setArgs(1);
			blockcomment.setRequired(false);
			options.addOption(blockcomment);
		}

		{
			final Option linecomment = new Option("linecomment", "linecomment",
					false, "line comment");
			linecomment.setArgName("linecomment");
			linecomment.setArgs(1);
			linecomment.setRequired(false);
			options.addOption(linecomment);
		}

		{
			final Option bracketline = new Option("bracketline", "bracketline",
					false, "bracket line");
			bracketline.setArgName("bracketline");
			bracketline.setArgs(1);
			bracketline.setRequired(false);
			options.addOption(bracketline);
		}

		{
			final Option indent = new Option("indent", "indent", false,
					"indent");
			indent.setArgName("indent");
			indent.setArgs(1);
			indent.setRequired(false);
			options.addOption(indent);
		}

		{
			final Option verbose = new Option("v", "verbose", false,
					"verbose output");
			verbose.setArgName("verbose");
			verbose.setRequired(false);
			options.addOption(verbose);
		}

		{
			final Option q = new Option("q", "quiet", false, "quiet");
			q.setArgName("quiet");
			q.setRequired(false);
			options.addOption(q);
		}

		try {
			final CommandLineParser parser = new PosixParser();
			final CommandLine commandLine = parser.parse(options, args);
			final CRConfig config = new CRConfig(commandLine);
			return config;
		} catch (final ParseException e) {
			e.printStackTrace();
			System.exit(0);
		}

		return null;
	}

	private final CommandLine commandLine;

	public CRConfig(final CommandLine commandLine) {
		this.commandLine = commandLine;
	}

	public boolean hasINPUT() {
		return this.commandLine.hasOption("input");
	}

	public String getINPUT() {
		return this.hasINPUT() ? this.commandLine.getOptionValue("input") : "";
	}

	public boolean hasOUTPUT() {
		return this.commandLine.hasOption("output");
	}

	public String getOUTPUT() {
		return this.hasOUTPUT() ? this.commandLine.getOptionValue("output")
				: "";
	}

	public boolean hasLANGUAGE() {
		return this.commandLine.hasOption("lang");
	}

	public LANGUAGE getLANGUAGE() {

		if (!this.hasLANGUAGE()) {
			return LANGUAGE.ALL;
		}

		final String language = this.commandLine.getOptionValue("lang");

		if (language.equalsIgnoreCase("C")) {

		} else if (language.equalsIgnoreCase("C")) {
			return LANGUAGE.C;
		} else if (language.equalsIgnoreCase("CPP")) {
			return LANGUAGE.CPP;
		} else if (language.equalsIgnoreCase("JAVA")) {
			return LANGUAGE.JAVA;
		} else if (language.equalsIgnoreCase("JAVASCRIPT")) {
			return LANGUAGE.JAVASCRIPT;
		} else if (language.equalsIgnoreCase("PYTHON")) {
			return LANGUAGE.PYTHON;
		} else {
			System.err
					.println("unknown value for option \"-lang\" is specified.");
			System.exit(0);
		}

		return null;
	}

	public boolean hasENCODING() {
		return this.commandLine.hasOption("encoding");
	}

	public String getENCODING() {
		return this.hasENCODING() ? this.commandLine.getOptionValue("encoding")
				: "";
	}

	public OPERATION getBLANKLINE() {
		return this.commandLine.hasOption("blankline") ? OPERATION
				.getOPERATION(this.commandLine.getOptionValue("blankline"))
				: OPERATION.RETAIN;
	}

	public OPERATION getBLOCKCOMMENT() {
		return this.commandLine.hasOption("blockcomment") ? OPERATION
				.getOPERATION(this.commandLine.getOptionValue("blockcomment"))
				: OPERATION.REMOVE;
	}

	public OPERATION getLINECOMMENT() {
		return this.commandLine.hasOption("linecomment") ? OPERATION
				.getOPERATION(this.commandLine.getOptionValue("linecomment"))
				: OPERATION.REMOVE;
	}

	public OPERATION getBRACKETLINE() {
		return this.commandLine.hasOption("bracketline") ? OPERATION
				.getOPERATION(this.commandLine.getOptionValue("bracketline"))
				: OPERATION.RETAIN;
	}

	public OPERATION getINDENT() {
		return this.commandLine.hasOption("indent") ? OPERATION
				.getOPERATION(this.commandLine.getOptionValue("indent"))
				: OPERATION.RETAIN;
	}

	public boolean isVERBOSE() {
		return this.commandLine.hasOption("v");
	}

	public boolean isQUIET() {
		return this.commandLine.hasOption("q");
	}
}
