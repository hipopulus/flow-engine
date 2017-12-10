package me.hipoplar.flow.simple;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.hipoplar.flow.DatabaseEngine;
import me.hipoplar.flow.Flow;
import me.hipoplar.flow.FlowDef;
import me.hipoplar.flow.FlowDefService;
import me.hipoplar.flow.FlowException;

public class SimpleFlowDefService implements FlowDefService {
	private DatabaseEngine databaseEngine;

	public SimpleFlowDefService(DatabaseEngine databaseEngine) {
		super();
		this.databaseEngine = databaseEngine;
	}

	@Override
	public FlowDef createFLow(FlowDef flow) {
		if (flow.getName() == null || flow.getName().trim().length() == 0) {
			throw new FlowException("Flow name not specified.");
		}
		Connection connection = databaseEngine.getConnection();
		try {
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO FLOW(name, flowxml, businessId, businessName, status) VALUES(?, ?, ?, ?, ?)");
			stmt.setString(1, flow.getName());
			stmt.setString(2, flow.getFlowxml());
			stmt.setString(3, flow.getBusinessId());
			stmt.setString(4, flow.getBusinessName());
			stmt.setInt(5, Flow.FLOW_SATUS_INIT);
			stmt.execute();
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

	@Override
	public FlowDef getFlow(String name) {
		if (name == null || name.trim().length() == 0) {
			throw new FlowException("Flow name not specified.");
		}
		FlowDef flow = null;
		Connection connection = databaseEngine.getConnection();
		try {
			PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Flow WHERE name = ?");
			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				flow = new FlowDef();
				flow.setName(rs.getString("name"));
				flow.setBusinessId(rs.getString("businessId"));
				flow.setBusinessName(rs.getString("businessName"));
				flow.setStatus(rs.getInt("status"));
				flow.setFlowxml(rs.getString("flowxml"));
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
