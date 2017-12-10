package me.hipoplar.flow.sample;

public class Application {
	private String id;
	private String name;
	private String mobile;
	private Boolean applied;
	private Boolean verified;
	private Boolean paid;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public Boolean getApplied() {
		return applied;
	}
	public void setApplied(Boolean applied) {
		this.applied = applied;
	}
	public Boolean getVerified() {
		return verified;
	}
	public void setVerified(Boolean verified) {
		this.verified = verified;
	}
	public Boolean getPaid() {
		return paid;
	}
	public void setPaid(Boolean paid) {
		this.paid = paid;
	}
}
