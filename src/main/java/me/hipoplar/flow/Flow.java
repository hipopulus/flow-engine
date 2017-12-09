package me.hipoplar.flow;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "flow")
@XmlType(propOrder = { "name", "nodes", "paths" })
public class Flow {
	private String name;
	private List<Node> nodes;
	private List<Path> paths;
	@XmlTransient
	private FlowContext context;

	@Override
	public String toString() {
		return "Flow - name: " + name;
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

	public FlowContext getContext() {
		return context;
	}

	public void setContext(FlowContext context) {
		this.context = context;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer nextKey() {
		if (nodes == null || nodes.size() == 0) {
			return 0;
		}
		int max = -1;
		for (Node node : nodes) {
			if (node.getKey() > max) {
				max = node.getKey();
			}
		}
		return max + 1;
	}
}
