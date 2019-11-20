package yoshikihigo.commentremover;

import java.nio.file.Path;

public enum LANGUAGE {

  C {

    @Override
    public boolean isTargetFile(final Path path) {
      return path.endsWith(".c") || path.endsWith(".h");
    }
  },

  CPP {

    @Override
    public boolean isTargetFile(final Path path) {
      return path.endsWith(".cpp") || path.endsWith(".cxx") || path.endsWith(".hpp")
          || path.endsWith(".hxx");
    }
  },

  JAVA {

    @Override
    public boolean isTargetFile(final Path path) {
      return path.endsWith(".java");
    }
  },

  JAVASCRIPT {

    @Override
    public boolean isTargetFile(final Path path) {
      return path.endsWith(".js");
    }
  },

  PHP {

    @Override
    public boolean isTargetFile(final Path path) {
      return path.endsWith(".php");
    }
  },

  PYTHON {

    @Override
    public boolean isTargetFile(final Path path) {
      return path.endsWith(".py");
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
