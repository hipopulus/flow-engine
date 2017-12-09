package me.hipoplar.flow;

public class Path {
	private Integer from;
	private Integer to;

	public Path() {
		super();
	}

	public Path(Integer from, Integer to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public String toString() {
		return "Path - from: " + from + ", to: " + to;
	}

	public Integer getFrom() {
		return from;
	}

	public void setFrom(Integer from) {
		this.from = from;
	}

	public Integer getTo() {
		return to;
	}

	public void setTo(Integer to) {
		this.to = to;
	}
}
