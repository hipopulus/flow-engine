package me.hipoplar.flow.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import me.hipoplar.flow.IdGenerator;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "flow")
@XmlType(propOrder = { "key", "name", "status", "businessId", "businessName", "nodes", "paths" })
public class Flow {
	public final static int FLOW_SATUS_DEFINED = 0;
	public final static int FLOW_SATUS_INSTANTIAL = 1;
	public final static int FLOW_SATUS_PROCESSING = 2;
	public final static int FLOW_SATUS_COMPLETE = 3;
	
	@XmlAttribute(name = "id")
	private String key;
	private String name;
	private Integer status;
	private String businessId;
	private String businessName;
	@XmlElement(name = "node")
	private List<Node> nodes;
	@XmlElement(name = "path")
	private List<Path> paths;

	@Override
	public String toString() {
		return "Flow - key: " + key + ", name: " + name + ", business: " + businessId + " " + businessName;
	}
	
	public Flow() { super(); }
	
	public static Flow create(String flowName) {
		Flow flow = new Flow();
		flow.setName(flowName);
		flow.setStatus(Flow.FLOW_SATUS_DEFINED);
		return flow;
	}
	
	public Flow attachBusiness(String businessId, String businessName) {
		this.businessId = businessId;
		this.businessName = businessName;
		return this;
	}
	
	public Node addNode(String name, int type) {
		Node node = new Node(type, IdGenerator.instance().nextId(), name, null);
		if(nodes == null) {
			nodes = new ArrayList<>();
		}
		nodes.add(node);
		return node;
	}
	
	public Path direct(String from, String to) {
		Node n1 = search(from);
		Path path = new Path(from, to);
		if(n1.getType() != Node.NODE_TYPE_GATEWAY_EXCLUSIVE) {
			n1.setExpression(to);
			if(paths == null) {
				paths = new ArrayList<>();
			}
			paths.add(path);
		}
		return path;
	}
	
	public List<Path> exclude(String expression, String from, String... tos) {
		Node n1 = search(from);
		n1.setExpression(expression);
		List<Path> pathList = new ArrayList<>();
		for (String to : tos) {
			Path path = new Path(from, to);
			pathList.add(path);
		}
		if(paths == null) {
			paths = new ArrayList<>();
		}
		paths.addAll(pathList);
		return pathList;
	}
	
	public List<Path> parallel(String from, String...tos) {
		Node n1 = search(from);
		n1.setExpression(String.join(",", tos));
		List<Path> pathList = new ArrayList<>();
		for (String to : tos) {
			Path path = new Path(from, to);
			pathList.add(path);
		}
		if(paths == null) {
			paths = new ArrayList<>();
		}
		paths.addAll(pathList);
		return pathList;
	}
	
	public List<Path> join(String to, String... froms) {
		List<Path> pathList = new ArrayList<>();
		for (String from : froms) {
			Path path = direct(from, to);
			pathList.add(path);
		}
		if(paths == null) {
			paths = new ArrayList<>();
		}
		paths.addAll(pathList);
		return pathList;
	}
	
	public Node search(String key) {
		if (nodes == null || nodes.size() == 0 || key == null) {
			return null;
		}
		for (Node node : nodes) {
			if(key.equals(node.getKey())) {
				return node;
			}
		}
		return null;
	}
	
	public List<Node> search(int type) {
		if (nodes == null || nodes.size() == 0) {
			return null;
		}
		List<Node> result = new ArrayList<>();
		for (Node node : nodes) {
			if(type == node.getType()) {
				result.add(node);
			}
		}
		return result.isEmpty() ? null : result;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public List<Path> getPaths() {
		return paths;
	}

	public void setPaths(List<Path> paths) {
		this.paths = paths;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
