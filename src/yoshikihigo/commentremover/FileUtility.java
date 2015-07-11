package yoshikihigo.commentremover;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.SortedSet;
import java.util.TreeSet;

import com.sun.istack.internal.NotNull;

public class FileUtility {

	public static String readFile(final File file, final String encoding) {

		final StringBuilder text = new StringBuilder();

		try (final InputStreamReader reader = new InputStreamReader(
				new FileInputStream(file), null != encoding ? encoding
						: "JISAutoDetect")) {

			while (reader.ready()) {
				final int c = reader.read();
				text.append((char) c);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return text.toString();
	}

	public static void writeFile(final String text, final String path) {

		final File file = new File(path);
		file.getParentFile().mkdirs();

		try (final OutputStream out = new FileOutputStream(path)) {

			for (int i = 0; i < text.length(); i++) {
				out.write(text.charAt(i));
			}

		} catch (final IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	@NotNull
	public static SortedSet<File> getFiles(final File file,
			final LANGUAGE language) {

		final SortedSet<File> files = new TreeSet<>();

		if (file.isDirectory()) {
			final File[] children = file.listFiles();
			for (final File child : children) {
				files.addAll(getFiles(child, language));
			}
		}

		else if (file.isFile()) {

			if (language.isTargetFile(file.getName())) {
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
}
