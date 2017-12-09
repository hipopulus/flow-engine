package me.hipoplar.flow;

public class Expression {

	private StringBuilder expression;

	public Expression() {
		this.expression = new StringBuilder();
	}
	
	public Expression(String value) {
		this.expression = new StringBuilder().append("'" + value + "';");
	}
	
	public Expression iF(String statement) {
		expression.append("if(").append(statement).append(")");
		return this;
	}
	
	public Expression elseIf(String statement) {
		expression.append("else if(").append(statement).append(")");
		return this;
	}
	
	public Expression then(String value) {
		expression.append("{'").append(value).append("';}");
		return this;
	}
	
	public Expression elseThen(String value) {
		expression.append(" else {'").append(value).append("';}");
		return this;
	}
	
	public String build() {
		return expression.toString();
	}
 }
