package me.hipoplar.flow.simple;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.hipoplar.flow.FlowException;
import me.hipoplar.flow.api.DatabaseEngine;
import me.hipoplar.flow.api.FlowService;
import me.hipoplar.flow.model.Flow;
import me.hipoplar.flow.model.FlowDef;

public class SimpleFlowService implements FlowService {
	private DatabaseEngine databaseEngine;

	public SimpleFlowService(DatabaseEngine databaseEngine) {
		super();
		this.databaseEngine = databaseEngine;
	}

	@Override
	public FlowDef createFLow(Flow flow, String flowxml) {
		Connection connection = databaseEngine.getConnection();
		try {
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO FLOW(key, name, flowxml, businessId, businessName, status, instantial) VALUES(?, ?, ?, ?, ?, ?, ?)");
			FlowDef flowDef = new FlowDef();
			flowDef.setKey(flow.getKey());
			flowDef.setName(flow.getName());
			flowDef.setBusinessId(flow.getBusinessId());
			flowDef.setBusinessName(flow.getBusinessName());
			flowDef.setStatus(flow.getStatus());
			flowDef.setFlowxml(flowxml);
			flowDef.setInstantial(flow.getInstantial());
			
			stmt.setString(1, flowDef.getKey());
			stmt.setString(2, flowDef.getName());
			stmt.setString(3, flowxml);
			stmt.setString(4, flowDef.getBusinessId());
			stmt.setString(5, flowDef.getBusinessName());
			stmt.setInt(6, Flow.FLOW_SATUS_INIT);
			stmt.setBoolean(7, flowDef.getInstantial());
			stmt.execute();
			return flowDef;
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
	public FlowDef getFlow(String key) {
		if (key == null || key.trim().length() == 0) {
			throw new FlowException("Flow key not specified.");
		}
		FlowDef flow = null;
		Connection connection = databaseEngine.getConnection();
		try {
			PreparedStatement stmt = connection.prepareStatement("SELECT * FROM FLOW WHERE key = ?");
			stmt.setString(1, key);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				flow = new FlowDef();
				flow.setKey(rs.getString("key"));
				flow.setName(rs.getString("name"));
				flow.setBusinessId(rs.getString("businessId"));
				flow.setBusinessName(rs.getString("businessName"));
				flow.setStatus(rs.getInt("status"));
				flow.setFlowxml(rs.getString("flowxml"));
				flow.setInstantial(rs.getBoolean("instantial"));
				break;
			}
			return flow;
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

}
