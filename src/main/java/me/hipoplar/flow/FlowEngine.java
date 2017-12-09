package me.hipoplar.flow;

import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
					"DROP TABLE IF EXISTS FLOW; CREATE TABLE FLOW(name VARCHAR(255) PRIMARY KEY, flowxml VARCHAR(2000))");
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
			PreparedStatement stmt = connection
					.prepareStatement("INSERT INTO FLOW(name, flowxml) VALUES(?, ?)");
			stmt.setString(1, flow.getName());
			stmt.setString(2, toXml(flow));
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
		if(flowXml == null || flowXml.trim().length() == 0) {
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
