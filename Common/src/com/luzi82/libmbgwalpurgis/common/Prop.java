package com.luzi82.libmbgwalpurgis.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Prop {

	public static Properties getAuthProperties() throws IOException {
		File f = null;
		if (f == null) {
			f = new File("auth.properties");
			if (!f.exists()) {
				f = null;
			}
		}
		if (f == null) {
			f = new File("../_config/auth.properties");
			if (!f.exists()) {
				f = null;
			}
		}
		if (f == null) {
			throw new FileNotFoundException();
		}
		Properties props = new Properties();
		FileInputStream authFileIn = new FileInputStream(f);
		props.load(authFileIn);
		authFileIn.close();
		return props;
	}

}
