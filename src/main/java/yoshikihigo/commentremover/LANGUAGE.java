package yoshikihigo.commentremover;

import java.nio.file.Files;
import java.nio.file.Path;

public enum LANGUAGE {

  C {

    @Override
    public boolean isTargetFile(final Path path) {
      if (!Files.isRegularFile(path)) {
        return false;
      }
      final String pathString = path.toString();
      return pathString.endsWith(".c") || pathString.endsWith(".h");
    }
  },

  CPP {

    @Override
    public boolean isTargetFile(final Path path) {
      if (!Files.isRegularFile(path)) {
        return false;
      }
      final String pathString = path.toString();
      return pathString.endsWith(".cpp") || pathString.endsWith(".cxx")
          || pathString.endsWith(".hpp") || pathString.endsWith(".hxx");
    }
  },

  JAVA {

    @Override
    public boolean isTargetFile(final Path path) {
      if (!Files.isRegularFile(path)) {
        return false;
      }
      final String pathString = path.toString();
      return pathString.endsWith(".java");
    }
  },

  JAVASCRIPT {

    @Override
    public boolean isTargetFile(final Path path) {
      if (!Files.isRegularFile(path)) {
        return false;
      }
      final String pathString = path.toString();
      return pathString.endsWith(".js");
    }
  },

  PHP {

    @Override
    public boolean isTargetFile(final Path path) {
      if (!Files.isRegularFile(path)) {
        return false;
      }
      final String pathString = path.toString();
      return pathString.endsWith(".php");
    }
  },

  PYTHON {

    @Override
    public boolean isTargetFile(final Path path) {
      if (!Files.isRegularFile(path)) {
        return false;
      }
      final String pathString = path.toString();
      return pathString.endsWith(".py");
    }
  },

  ALL {

    @Override
    public boolean isTargetFile(final Path path) {
      return C.isTargetFile(path) || CPP.isTargetFile(path) || JAVA.isTargetFile(path)
          || JAVASCRIPT.isTargetFile(path) || PHP.isTargetFile(path) || PYTHON.isTargetFile(path);
    }
  };

  abstract public boolean isTargetFile(final Path path);
}
