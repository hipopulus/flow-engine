package me.hipoplar.flow;

import java.sql.Connection;

public interface DatabaseEngine {
	Connection getConnection();
}
