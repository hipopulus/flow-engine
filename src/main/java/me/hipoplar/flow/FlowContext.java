package me.hipoplar.flow;

public class FlowContext<T> {
	private T data;
	private Operator operator;

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}
}
