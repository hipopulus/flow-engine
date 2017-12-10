package me.hipoplar.flow.simple;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import me.hipoplar.flow.FlowException;
import me.hipoplar.flow.api.ActivityService;
import me.hipoplar.flow.api.DatabaseEngine;
import me.hipoplar.flow.model.Activity;
import me.hipoplar.flow.model.Flow;
import me.hipoplar.flow.model.Node;
import me.hipoplar.flow.model.Operator;

public class SimpleActivityService implements ActivityService {

	private DatabaseEngine databaseEngine;
	
	public SimpleActivityService(DatabaseEngine databaseEngine) {
		super();
		this.databaseEngine = databaseEngine;
	}

	public Activity getActivity(String activityId) {
		if (activityId == null || activityId.trim().length() == 0) {
			throw new FlowException("Node key not specified.");
		}
		Connection connection = databaseEngine.getConnection();
		try {
			PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ACTIVITY WHERE id = ?");
			stmt.setString(1, activityId);
			ResultSet rs = stmt.executeQuery();
			Activity activity = null;
			while (rs.next()) {
				activity = new Activity();
				activity.setId(rs.getString("id"));
				activity.setName(rs.getString("name"));
				activity.setBusinessId(rs.getString("businessId"));
				activity.setBusinessName(rs.getString("businessName"));
				activity.setComplete(rs.getBoolean("complete"));
				activity.setCreateTime(rs.getDate("createTime"));
				activity.setFlow(rs.getString("flow"));
				activity.setNode(rs.getString("node"));
				activity.setOperatorId(rs.getString("operatorId"));
				activity.setOperatorName(rs.getString("operatorName"));
				activity.setOperatorGroup(rs.getString("operatorGroup"));
				activity.setUpdateTime(rs.getDate("updateTime"));
			}
			return activity;
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
	
	public List<Activity> getFlowActivities(String flow, String operatorId) {
		if (flow == null || flow.trim().length() == 0) {
			throw new FlowException("Flow name not specified.");
		}
		if (operatorId == null || operatorId.trim().length() == 0) {
			throw new FlowException("Operator not specified.");
		}
		Connection connection = databaseEngine.getConnection();
		try {
			PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ACTIVITY WHERE complete = 0 AND flow = ? AND operatorId = ?");
			stmt.setString(1, flow);
			stmt.setString(2, operatorId);
			ResultSet rs = stmt.executeQuery();
			List<Activity> activities = new ArrayList<>();
			while (rs.next()) {
				Activity activity = new Activity();
				activity.setId(rs.getString("id"));
				activity.setName(rs.getString("name"));
				activity.setBusinessId(rs.getString("businessId"));
				activity.setBusinessName(rs.getString("businessName"));
				activity.setComplete(rs.getBoolean("complete"));
				activity.setCreateTime(rs.getDate("createTime"));
				activity.setFlow(rs.getString("flow"));
				activity.setNode(rs.getString("node"));
				activity.setOperatorId(rs.getString("operatorId"));
				activity.setOperatorName(rs.getString("operatorName"));
				activity.setOperatorGroup(rs.getString("operatorGroup"));
				activity.setUpdateTime(rs.getDate("updateTime"));
				activities.add(activity);
			}
			return activities;
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
	
	public List<Activity> createNodeActivity(Flow flow, Node node) {
		if(node.getType() == Node.NODE_TYPE_TASK) {
			List<Activity> activities = new ArrayList<>();
			for (Operator operator : node.getOperators()) {
				Activity activity = new Activity();
				activity.setId(UUID.randomUUID().toString());
				activity.setBusinessId(flow.getBusinessId());
				activity.setBusinessName(flow.getBusinessName());
				activity.setComplete(false);
				activity.setCreateTime(new Date());
				activity.setFlow(flow.getName());
				activity.setNode(node.getKey());
				activity.setOperatorGroup(operator.getGroup());
				activity.setOperatorId(operator.getOperatorId());
				activity.setOperatorName(operator.getOperatorName());
				Connection connection = databaseEngine.getConnection();
				try {
					PreparedStatement stmt = connection.prepareStatement("INSERT INTO ACTIVITY(id, name, flow, node, operatorId, operatorGroup, operatorName, complete, createTime, updateTime, businessId, businessName)"
							+ "VALUES(?, ?, ?, ?, ?, ?, ?, 0, CURRENT_TIME(), CURRENT_TIME(), ?, ?)");
					stmt.setString(1, activity.getId());
					stmt.setString(2, node.getName());
					stmt.setString(3, activity.getFlow());
					stmt.setString(4, activity.getNode());
					stmt.setString(5, activity.getOperatorId());
					stmt.setString(6, activity.getOperatorGroup());
					stmt.setString(7, activity.getOperatorName());
					stmt.setString(8, activity.getBusinessId());
					stmt.setString(9, activity.getBusinessName());
					if(stmt.executeUpdate() == 0) {
						throw new FlowException("Create activity error.");
					}
					activities.add(activity);
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
			return activities;
		} else {
			return null;
		}
	}
	
	public boolean completeActivity(String nodeKey, String operatorId, String operatorName) {
		Connection connection = databaseEngine.getConnection();
		try {
			PreparedStatement stmt = connection.prepareStatement("UPDATE ACTIVITY SET complete = 1, updateTime = CURRENT_TIME() WHERE node = ?");
			stmt.setString(1, nodeKey);
			return stmt.executeUpdate() > 0;
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
