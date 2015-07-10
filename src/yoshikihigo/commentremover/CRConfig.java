package yoshikihigo.commentremover;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class CRConfig {

	static public CRConfig initialize(final String[] args) {

		final Options options = new Options();

		{
			final Option i = new Option("input", "input", true,
					"input directory");
			i.setArgName("input");
			i.setArgs(1);
			i.setRequired(false);
			options.addOption(i);
		}

		{
			final Option o = new Option("output", "output", true,
					"output directory");
			o.setArgName("output");
			o.setArgs(1);
			o.setRequired(false);
			options.addOption(o);
		}

		{
			final Option l = new Option("lang", "language", true, "language");
			l.setArgName("language");
			l.setArgs(1);
			l.setRequired(false);
			options.addOption(l);
		}

		{
			final Option x = new Option("encoding", "encoding", true,
					"encoding");
			x.setArgName("encoding");
			x.setArgs(1);
			x.setRequired(false);
			options.addOption(x);
		}

		{
			final Option a = new Option("blankline", "blankline", false,
					"blankline");
			a.setArgName("blankline");
			a.setRequired(false);
			options.addOption(a);
		}

		{
			final Option b = new Option("blockcomment", "blockcomment", false,
					"block comment");
			b.setArgName("blockcomment");
			b.setRequired(false);
			options.addOption(b);
		}

		{
			final Option c = new Option("linecomment", "linecomment", false,
					"line comment");
			c.setArgName("linecomment");
			c.setRequired(false);
			options.addOption(c);
		}

		{
			final Option d = new Option("bracketline", "bracketline", false,
					"bracket line");
			d.setArgName("bracketline");
			d.setRequired(false);
			options.addOption(d);
		}

		{
			final Option e = new Option("indent", "indent", false, "indent");
			e.setArgName("indent");
			e.setRequired(false);
			options.addOption(e);
		}

		{
			final Option v = new Option("v", "verbose", false, "verbose output");
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

	public boolean hasBLANKLINE() {
		return this.commandLine.hasOption("blankline");
	}

	public boolean hasBLOCKCOMMENT() {
		return this.commandLine.hasOption("blockcomment");
	}

	public boolean hasLINECOMMENT() {
		return this.commandLine.hasOption("linecomment");
	}

	public boolean hasBLACKETLINE() {
		return this.commandLine.hasOption("bracketline");
	}

	public boolean hasINDENT() {
		return this.commandLine.hasOption("indent");
	}

	public boolean isVERBOSE() {
		return this.commandLine.hasOption("v");
	}

	public boolean isQUIET() {
		return this.commandLine.hasOption("q");
	}
}
