package me.hipoplar.flow.model;

public class FlowDef {

	private String key;
	private String name;
	private Integer status;
	private Integer lastNodeIndex;
	private String businessId;
	private String businessName;
	private String flowxml;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
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
	public String getFlowxml() {
		return flowxml;
	}
	public void setFlowxml(String flowxml) {
		this.flowxml = flowxml;
	}
	public Integer getLastNodeIndex() {
		return lastNodeIndex;
	}
	public void setLastNodeIndex(Integer lastNodeIndex) {
		this.lastNodeIndex = lastNodeIndex;
	}
}
