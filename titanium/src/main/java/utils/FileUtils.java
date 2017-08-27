package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {
	public static String readFile(File f, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
		return new String(encoded, encoding);
	}
	
	public static String readFile(File f, boolean createNewWhenDoesNotExists) throws IOException {
		if (!f.exists() && createNewWhenDoesNotExists)
			f.createNewFile();
		
		return readFile(f, Charset.defaultCharset());
	}
	
	public static void WriteFile(File f, String content) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(f);
		
		pw.print(content);
		
		pw.close();
	}
}
