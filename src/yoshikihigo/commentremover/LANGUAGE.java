package yoshikihigo.commentremover;

public enum LANGUAGE {

	C {

		@Override
		public boolean isTargetFile(final String path) {
			return path.endsWith(".c") || path.endsWith(".h");
		}
	},

	CPP {

		@Override
		public boolean isTargetFile(final String path) {
			return path.endsWith(".cpp") || path.endsWith(".cxx")
					|| path.endsWith(".hpp") || path.endsWith(".hxx");
		}
	},

	JAVA {

		@Override
		public boolean isTargetFile(final String path) {
			return path.endsWith(".java");
		}
	},

	PYTHON {

		@Override
		public boolean isTargetFile(final String path) {
			return path.endsWith("py");
		}
	},

	ALL {

		@Override
		public boolean isTargetFile(final String path) {
			return C.isTargetFile(path) || CPP.isTargetFile(path)
					|| JAVA.isTargetFile(path) || PYTHON.isTargetFile(path);
		}
	};

	abstract public boolean isTargetFile(final String path);
}
