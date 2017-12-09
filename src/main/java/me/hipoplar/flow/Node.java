package me.hipoplar.flow;

public class Node {
	private Integer key;
	private String name;
	private String expression;

	public Node() {
		super();
	}

	public Node(Integer key, String name, String expression) {
		this.key = key;
		this.name = name;
		this.expression = expression;
	}

	@Override
	public String toString() {
		return "Node - key: " + key + ", name: " + name + ", expression: " + expression;
	}

	public Integer route(FlowContext context) {
		return null;
	}

	public Integer getKey() {
		return key;
	}

	public void setKey(Integer key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
}
