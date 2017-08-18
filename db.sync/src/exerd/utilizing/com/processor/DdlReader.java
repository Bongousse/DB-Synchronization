package exerd.utilizing.com.processor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DdlReader {

	public String readDdl(String filePath) {
		String ddl = null;
		ddl = readContentFrom(filePath);

		return ddl;
	}

	public static String readContentFrom(String textFileName) {
		BufferedReader bufferedTextFileReader = null;
		StringBuilder contentReceiver = new StringBuilder();
		try {
			bufferedTextFileReader = new BufferedReader(new FileReader(textFileName));
			
			String line = null;
			while ((line = bufferedTextFileReader.readLine()) != null) {
				contentReceiver.append(line + "\n");
			} 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedTextFileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return contentReceiver.toString();
	}
}
