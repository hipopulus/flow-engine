package me.hipoplar.flow.simple;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import me.hipoplar.flow.api.DatabaseEngine;

public class SimpleDataBaseEngine implements DatabaseEngine {
	private H2Engine h2Engine;
	
	public SimpleDataBaseEngine() {
		h2Engine = new H2Engine();
		Connection connection = getConnection();
		try {
			PreparedStatement createTable = connection.prepareStatement(
					// FLOW
					"DROP TABLE IF EXISTS FLOW; "
					+ "CREATE TABLE FLOW(key VARCHAR(255) PRIMARY KEY, name VARCHAR(255), flowxml CLOB, businessId VARCHAR(255), businessName VARCHAR(255), status TINYINT, lastNodeIndex INT);"
					// JOINED_NODE
					+ "DROP TABLE IF EXISTS JOINED_NODE; "
					+ "CREATE TABLE JOINED_NODE(gateway VARCHAR(255), joinedNode VARCHAR(255));"
					// ACTIVITY
					+ "DROP TABLE IF EXISTS ACTIVITY; "
					+ "CREATE TABLE ACTIVITY(id VARCHAR(255) PRIMARY KEY, name VARCHAR(255), flow VARCHAR(255), node VARCHAR(255), operatorId VARCHAR(255), operatorName VARCHAR(255), operatorGroup VARCHAR(255), complete BOOLEAN, createTime DATETIME, updateTime DATETIME, businessId VARCHAR(255), businessName VARCHAR(255));");
			createTable.execute();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public Connection getConnection() {
		return h2Engine.getConnection();
	}

}
