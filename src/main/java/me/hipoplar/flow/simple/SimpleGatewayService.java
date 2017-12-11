package me.hipoplar.flow.simple;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import me.hipoplar.flow.api.DatabaseEngine;
import me.hipoplar.flow.api.GatewayService;
import me.hipoplar.flow.model.Node;

public class SimpleGatewayService implements GatewayService {
	
	private DatabaseEngine databaseEngine;

	public SimpleGatewayService(DatabaseEngine databaseEngine) {
		super();
		this.databaseEngine = databaseEngine;
	}

	@Override
	public void join(Node gateway, Node joinedNode) {
		Connection connection = databaseEngine.getConnection();
		try {
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO JOINED_NODE(gateway, joinedNode) VALUES(?, ?)");
			stmt.setString(1, gateway.getKey());
			stmt.setString(2, joinedNode.getKey());
			stmt.executeUpdate();
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
	public boolean isJoined(Node gateway) {
		String[] nodes = gateway.getExpression().split(",");
		if (nodes == null || nodes.length == 0) {
			return false;
		}
		Connection connection = databaseEngine.getConnection();
		Set<String> joinedNodes = new HashSet<>();
		try {
			PreparedStatement stmt = connection.prepareStatement("SELECT joinedNode FROM JOINED_NODE WHERE gateway = ?");
			stmt.setString(1, gateway.getKey());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				joinedNodes.add(rs.getString("joinedNode"));
			}
			return nodes.length == joinedNodes.size();
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
