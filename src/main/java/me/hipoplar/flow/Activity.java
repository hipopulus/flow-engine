package me.hipoplar.flow;

import java.util.Date;

public class Activity {
	private String id;
	private String flow;
	private String node;
	private String operatorId;
	private String operatorGorup;
	private String operatorName;
	private Boolean complete;
	private Date createTime;
	private Date updateTime;
	private String businessId;
	private String businessName;
	
	@Override
	public String toString() {
		return "Activity - id: " + id + ", node: " + node + ", complete: " + complete;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFlow() {
		return flow;
	}
	public void setFlow(String flow) {
		this.flow = flow;
	}
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
	public Boolean getComplete() {
		return complete;
	}
	public void setComplete(Boolean complete) {
		this.complete = complete;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
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
	public String getOperatorGorup() {
		return operatorGorup;
	}
	public void setOperatorGorup(String operatorGorup) {
		this.operatorGorup = operatorGorup;
	}
}
