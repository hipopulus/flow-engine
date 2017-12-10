package me.hipoplar.flow.simple;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import me.hipoplar.flow.ActivityService;
import me.hipoplar.flow.DatabaseEngine;
import me.hipoplar.flow.FlowDefService;
import me.hipoplar.flow.FlowEngine;
import me.hipoplar.flow.GatewayService;

public class SimpleFlowEngine extends FlowEngine {
	
	private FlowDefService flowDefService;
	private ActivityService activityService;
	private GatewayService gatewayService;

	public SimpleFlowEngine(DatabaseEngine databaseEngine) {
		super();
		this.flowDefService = new SimpleFlowDefService(databaseEngine);
		this.activityService = new SimpleActivityService(databaseEngine);
		this.gatewayService = new SimpleGatewayService(databaseEngine);
	}


	protected void init(DatabaseEngine databaseEngine) {
		Connection connection = databaseEngine.getConnection();
		try {
			PreparedStatement createTable = connection.prepareStatement(
					// FLOW
					"DROP TABLE IF EXISTS FLOW; "
					+ "CREATE TABLE FLOW(name VARCHAR(255) PRIMARY KEY, flowxml CLOB, businessId VARCHAR(255), businessName VARCHAR(255), status TINYINT);"
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
	public FlowDefService getFlowDefService() {
		return flowDefService;
	}

	@Override
	public ActivityService getActivityService() {
		return activityService;
	}

	@Override
	public GatewayService getGatewayService() {
		return gatewayService;
	}
}
