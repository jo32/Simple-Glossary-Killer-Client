package info.jo32.SGKClient.Util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class PropertyLoader {

	private String path = null;
	Properties props = new Properties();

	public PropertyLoader(String path) throws IOException {
		this.path = path;
		InputStream in = new BufferedInputStream(new FileInputStream(path));
		this.props.load(in);
	}

	public String getProperty(String key) {
		String value = this.props.getProperty(key);
		return value;
	}

	public void setProperty(String key, String value) {
		this.props.setProperty(key, value);
	}

	public void makePersistent() throws IOException {
		OutputStream fos = new FileOutputStream(this.path);
		this.props.store(fos, "");
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
	}

}
