package info.jo32.SGKClient.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ObjectURLLinker<T> {

	private String urlString;
	private String[] paramName;
	private String[] paramValue;
	private String jsonString;

	public ObjectURLLinker(String url, String[] paramName, String[] paramValue) throws UnsupportedEncodingException {
		this.urlString = url;
		this.paramName = paramName;
		this.paramValue = paramValue;
	}

	public void readURL() throws IOException {
		String data = URLEncoder.encode(paramName[0], "utf-8") + "=" + URLEncoder.encode(paramValue[0], "utf-8");
		for (int i = 1; i < paramName.length; i++) {
			data = data + "&" + URLEncoder.encode(paramName[i], "utf-8") + "=" + URLEncoder.encode(paramValue[i], "utf-8");
		}
		URL url = new URL(this.urlString);
		URLConnection conn = url.openConnection();
		conn.setConnectTimeout(60 * 1000);
		conn.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(data);
		wr.flush();

		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		String json = "";
		while ((line = rd.readLine()) != null) {
			json += line;
		}
		wr.close();
		rd.close();
		this.jsonString = json;
	}

	public T getObjectByURL(Class<T> cls) throws IOException {
		this.readURL();
		Gson g = new Gson();
		Type type = TypeToken.get(cls).getType();
		T t = g.fromJson(this.jsonString, type);
		return t;
	}
	
	public T getObjectByURL(Type cls) throws IOException {
		this.readURL();
		Gson g = new Gson();
		T t = g.fromJson(this.jsonString, cls);
		return t;
	}

}
