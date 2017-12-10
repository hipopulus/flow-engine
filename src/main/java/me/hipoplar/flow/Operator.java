package me.hipoplar.flow;

public class Operator {
	public final static String GROUP_ANY = "Any";
	public final static String GROUP_PERSONAL = "PERSONAL";
	public final static String GROUP_CORPORATE = "CORPORATE";
	public final static String GROUP_DEPARTMENT = "DEPARTMENT";
	
	private String node;
	private String operatorId;
	private String operatorName;
	private String group;
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public String getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
}
