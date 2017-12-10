package me.hipoplar.flow.simple;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2Engine {

	private String url = "~/H2Data/test";
	private String username = "sa";
	private String password = "";

	public H2Engine() {
		super();
	}

	public H2Engine(String url, String username, String password) {
		if (url != null)
			this.url = url;
		if (username != null)
			this.username = username;
		if (password != null)
			this.password = password;
	}

	public Connection getConnection() {
		return getConnection(url, username, password);
	}

	private Connection getConnection(String url, String username, String password) {
		try {
			return DriverManager.getConnection("jdbc:h2:" + url, username, password);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
