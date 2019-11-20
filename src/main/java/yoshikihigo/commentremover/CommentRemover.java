package yoshikihigo.commentremover;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

abstract public class CommentRemover {

  public static void main(final String[] args) {

    CRConfig config = CRConfig.initialize(args);

    if (!config.hasINPUT()) {
      System.err.println("option \"-input\" is require to specify your target source files.");
      System.exit(0);
    }
    final Path inputPath = Paths.get(config.getINPUT());

    if (!config.hasOUTPUT()) {
      System.err.println("option \"-output\" is require to specify this tool's output directory.");
      System.exit(0);
    }
    final Path outputPath = Paths.get(config.getOUTPUT());

    if (Files.isRegularFile(inputPath) && Files.isReadable(inputPath)) {

      CommentRemover remover = null;
      if (LANGUAGE.C.isTargetFile(inputPath)) {
        remover = new CommentRemoverJC(config);
      } else if (LANGUAGE.CPP.isTargetFile(inputPath)) {
        remover = new CommentRemoverJC(config);
      } else if (LANGUAGE.JAVA.isTargetFile(inputPath)) {
        remover = new CommentRemoverJC(config);
      } else if (LANGUAGE.JAVASCRIPT.isTargetFile(inputPath)) {
        remover = new CommentRemoverJS(config);
      } else if (LANGUAGE.PHP.isTargetFile(inputPath)) {
        remover = new CommentRemoverPHP(config);
      } else if (LANGUAGE.PYTHON.isTargetFile(inputPath)) {
        remover = new CommentRemoverPY(config);
      }

      if (null != remover) {
        final String text = config.hasENCODING() ? Utils.readFile(inputPath, config.getENCODING())
            : Utils.readFile(inputPath);
        final String result = remover.perform(text);
        Utils.writeFile(result, outputPath);
      }
    }

    else if (Files.isDirectory(inputPath)) {

      int index = 0;
      final LANGUAGE language = config.getLANGUAGE();
      final List<Path> targetPaths = Utils.list(inputPath, language);

      for (final Path targetPath : targetPaths) {

        if (config.isVERBOSE()) {
          final StringBuilder text = new StringBuilder();
          text.append(" [");
          text.append(Integer.toString(index++ + 1));
          text.append("/");
          text.append(Integer.toString(targetPaths.size()));
          text.append("] ");
          text.append(targetPath.toString());
          System.err.println(text.toString());
        }

        final Path targetAbsolutePath = targetPath.toAbsolutePath();
        final String outputAbsolutePath = targetAbsolutePath.toString()
            .replace(config.getINPUT(), config.getOUTPUT());

        final String[] newArgs = new String[args.length];
        for (int i = 0; i < args.length; i++) {
          if (args[i].equals(config.getINPUT())) {
            newArgs[i] = targetAbsolutePath.toString();
          } else if (args[i].equals(config.getOUTPUT())) {
            newArgs[i] = outputAbsolutePath;
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
      final char c2 = (index + 1) < text.length() ? text.charAt(index + 1) : '0';

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
