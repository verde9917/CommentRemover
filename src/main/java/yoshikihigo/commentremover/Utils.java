package yoshikihigo.commentremover;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

  public static String readFile(final Path path) {
    return readFile(path, null);
  }

  public static String readFile(final Path path, final String encoding) {
    final Charset charset = null == encoding ? StandardCharsets.UTF_8 : Charset.forName(encoding);
    try {
      return new String(Files.readAllBytes(path), charset);
    } catch (final IOException e) {
      e.printStackTrace();
      return "";
    }
  }

  public static void writeFile(final String text, final Path path) {
    try {
      final Path parentDir = path.getParent();
      if (!Files.exists(parentDir)) {
        Files.createDirectories(parentDir);
      }
      List <String> texts = new ArrayList<>();
      texts.add(text);
      Files.write(path, texts);
      //      Files.writeString(path, text);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  public static List<Path> list(final Path path, final LANGUAGE language) {
    try {
      return Files.find(path, Integer.MAX_VALUE, (p, a) -> language.isTargetFile(p))
          .sorted()
          .collect(Collectors.toList());
    } catch (final IOException e) {
      e.printStackTrace();
      return Collections.emptyList();
    }
  }
}
