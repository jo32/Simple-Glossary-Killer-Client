package info.jo32.SGKClient.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileWordsConverter {

	String path = null;

	public FileWordsConverter(String path) {
		this.path = path;
	}

	public List<String> getWordsString() throws IOException {
		String s = "";
		List<String> words = new ArrayList<String>();
		File file = new File(this.path);
		if (file.exists()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String temp = br.readLine();
			int count = 1;
			while (temp != null) {
				s += temp;
				s += "|";
				temp = br.readLine();
				count++;
				if (count == 500) {
					words.add(s);
					count = 0;
				}
			}
		} else {
			throw new FileNotFoundException();
		}
		return words;
	}
}
