package me.hipoplar.flow;

import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import me.hipoplar.h2.H2Engine;

public class FlowEngine {
	private H2Engine h2Engine;

	private FlowEngine() {
		init();
	}

	public static FlowEngine createEngine() {
		return new FlowEngine();
	}

	private void init() {
		h2Engine = new H2Engine();
		Connection connection = h2Engine.getConnection();
		try {
			PreparedStatement createTable = connection.prepareStatement(
					// FLOW
					"DROP TABLE IF EXISTS FLOW; "
					+ "CREATE TABLE FLOW(name VARCHAR(255) PRIMARY KEY, flowxml CLOB, businessId VARCHAR(255), businessName VARCHAR(255), status TINYINT );"
					// ACTIVITY
					+ "DROP TABLE IF EXISTS ACTIVITY; "
					+ "CREATE TABLE ACTIVITY(id VARCHAR(255) PRIMARY KEY, flow VARCHAR(255), node VARCHAR(255), operatorId VARCHAR(255), operatorName VARCHAR(255), operatorGroup VARCHAR(255), complete BOOLEAN, createTime DATETIME, updateTime DATETIME, businessId VARCHAR(255), businessName VARCHAR(255));");
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

	public Flow createFLow(Flow flow) {
		if (flow.getName() == null || flow.getName().trim().length() == 0) {
			throw new FlowException("Flow name not specified.");
		}
		Connection connection = h2Engine.getConnection();
		try {
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO FLOW(name, flowxml, businessId, businessName, status) VALUES(?, ?, ?, ?, ?)");
			stmt.setString(1, flow.getName());
			stmt.setString(2, toXml(flow));
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

	public Flow getFlow(String name) {
		if (name == null || name.trim().length() == 0) {
			throw new FlowException("Flow name not specified.");
		}
		Flow flow = null;
		Connection connection = h2Engine.getConnection();
		try {
			PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Flow WHERE name = ?");
			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				flow = toJavaObject(rs.getString("flowxml"));
				break;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		return flow;
	}

	public void start(String flowName, FlowContext<?> context) {
		Flow flow = getFlow(flowName);
		if (flow == null) {
			throw new FlowException("Flow name not specified.");
		}
		Node start = flow.search(Node.NODE_TYPE_START).get(0);
		String[] nextNodes = start.route(context);
		if(nextNodes == null || nextNodes.length == 0) {
			return;
		}
		for (String nodeKey : nextNodes) {
			if(nodeKey.trim().equals(start.getKey().trim())) {
				return;
			}
		}
		for (String nodeKey : nextNodes) {
			createActivity(flow, nodeKey);
		}
	}

	public void process(String activityId, FlowContext<?> context) {
		Activity activity = getActivity(activityId);
		if(activity == null) {
			throw new FlowException("Activity not found.");
		}
		if(activity.getComplete() != null && activity.getComplete()) {
			throw new FlowException("Activity completed.");
		}
		Flow flow = getFlow(activity.getFlow());
		Node node = flow.search(activity.getNode());
		String[] nextNodes = node.route(context);
		if(nextNodes == null || nextNodes.length == 0) {
			return;
		}
		for (String nodeKey : nextNodes) {
			if(nodeKey.trim().equals(node.getKey().trim())) {
				return;
			}
		}
		if(!completeActivity(activityId, context.getOperator().getOperatorId(), context.getOperator().getOperatorName())) {
			throw new FlowException("Complete activity error.");
		}
		for (String nodeKey : nextNodes) {
			createActivity(flow, nodeKey);
		}
	}
	
	public List<Activity> createActivity(Flow flow, String nodeKey) {
		Node node = flow.search(nodeKey);
		if(node.getType() == Node.NODE_TYPE_END) {
			return null;
		} else if(node.getOperators() == null){
			return null;
		} else {
			List<Activity> activities = new ArrayList<>();
			for (Operator operator : node.getOperators()) {
				Activity activity = new Activity();
				activity.setId(UUID.randomUUID().toString());
				activity.setBusinessId(flow.getBusinessId());
				activity.setBusinessName(flow.getBusinessName());
				activity.setComplete(false);
				activity.setCreateTime(new Date());
				activity.setFlow(flow.getName());
				activity.setNode(nodeKey);
				activity.setOperatorGorup(operator.getGroup());
				activity.setOperatorId(operator.getOperatorId());
				activity.setOperatorName(operator.getOperatorName());
				Connection connection = h2Engine.getConnection();
				try {
					PreparedStatement stmt = connection.prepareStatement("INSERT INTO ACTIVITY(id, flow, node, operatorId, operatorGroup, operatorName, complete, createTime, updateTime, businessId, businessName)"
							+ "VALUES(?, ?, ?, ?, ?, ?, 0, CURRENT_TIME(), CURRENT_TIME(), ?, ?)");
					stmt.setString(1, activity.getId());
					stmt.setString(2, activity.getFlow());
					stmt.setString(3, activity.getNode());
					stmt.setString(4, activity.getOperatorId());
					stmt.setString(5, activity.getOperatorGorup());
					stmt.setString(6, activity.getOperatorName());
					stmt.setString(7, activity.getBusinessId());
					stmt.setString(8, activity.getBusinessName());
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
		}
	}
	
	private boolean completeActivity(String activityId, String operatorId, String operatorName) {
		Connection connection = h2Engine.getConnection();
		try {
			PreparedStatement stmt = connection.prepareStatement("UPDATE ACTIVITY SET complete = 1, updateTime = CURRENT_TIME() WHERE id = ?");
			stmt.setString(1, activityId);
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
	
	public Activity getActivity(String activityId) {
		if (activityId == null || activityId.trim().length() == 0) {
			throw new FlowException("Node key not specified.");
		}
		Connection connection = h2Engine.getConnection();
		try {
			PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ACTIVITY WHERE id = ?");
			stmt.setString(1, activityId);
			ResultSet rs = stmt.executeQuery();
			Activity activity = null;
			while (rs.next()) {
				activity = new Activity();
				activity.setBusinessId(rs.getString("businessId"));
				activity.setBusinessName(rs.getString("businessName"));
				activity.setComplete(rs.getBoolean("complete"));
				activity.setCreateTime(rs.getDate("createTime"));
				activity.setFlow(rs.getString("flow"));
				activity.setNode(rs.getString("node"));
				activity.setOperatorId(rs.getString("operatorId"));
				activity.setOperatorName(rs.getString("operatorName"));
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
	
	public List<Activity> getNodeActivities(String nodeKey, String operatorId) {
		if (nodeKey == null || nodeKey.trim().length() == 0) {
			throw new FlowException("Node key not specified.");
		}
		if (operatorId == null || operatorId.trim().length() == 0) {
			throw new FlowException("Operator not specified.");
		}
		Connection connection = h2Engine.getConnection();
		try {
			PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ACTIVITY WHERE complete = 0 AND node = ? AND operatorId = ?");
			stmt.setString(1, nodeKey);
			stmt.setString(2, operatorId);
			ResultSet rs = stmt.executeQuery();
			List<Activity> activities = new ArrayList<>();
			while (rs.next()) {
				Activity activity = new Activity();
				activity.setBusinessId(rs.getString("businessId"));
				activity.setBusinessName(rs.getString("businessName"));
				activity.setComplete(rs.getBoolean("complete"));
				activity.setCreateTime(rs.getDate("createTime"));
				activity.setFlow(rs.getString("flow"));
				activity.setNode(rs.getString("node"));
				activity.setOperatorId(rs.getString("operatorId"));
				activity.setOperatorName(rs.getString("operatorName"));
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
	
	public List<Activity> getFlowActivities(String flow, String operatorId) {
		if (flow == null || flow.trim().length() == 0) {
			throw new FlowException("Flow name not specified.");
		}
		if (operatorId == null || operatorId.trim().length() == 0) {
			throw new FlowException("Operator not specified.");
		}
		Connection connection = h2Engine.getConnection();
		try {
			PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ACTIVITY WHERE complete = 0 AND flow = ? AND operatorId = ?");
			stmt.setString(1, flow);
			stmt.setString(2, operatorId);
			ResultSet rs = stmt.executeQuery();
			List<Activity> activities = new ArrayList<>();
			while (rs.next()) {
				Activity activity = new Activity();
				activity.setId(rs.getString("id"));
				activity.setBusinessId(rs.getString("businessId"));
				activity.setBusinessName(rs.getString("businessName"));
				activity.setComplete(rs.getBoolean("complete"));
				activity.setCreateTime(rs.getDate("createTime"));
				activity.setFlow(rs.getString("flow"));
				activity.setNode(rs.getString("node"));
				activity.setOperatorId(rs.getString("operatorId"));
				activity.setOperatorName(rs.getString("operatorName"));
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

	private String toXml(Flow flow) {
		StringWriter sw = new StringWriter();
		try {
			JAXBContext context = JAXBContext.newInstance(flow.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(flow, sw);
			return sw.toString();
		} catch (JAXBException e) {
			throw new FlowException(e);
		}
	}

	private Flow toJavaObject(String flowXml) {
		if (flowXml == null || flowXml.trim().length() == 0) {
			return null;
		}
		try {
			JAXBContext context = JAXBContext.newInstance(Flow.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			StringReader sr = new StringReader(flowXml);
			return (Flow) unmarshaller.unmarshal(sr);
		} catch (JAXBException e) {
			throw new FlowException(e);
		}
	}
}
