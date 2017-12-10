package me.hipoplar.flow;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Node {
	public final static int NODE_TYPE_START = 0;
	public final static int NODE_TYPE_TASK = 1;
	public final static int NODE_TYPE_GATEWAY_EXCLUSIVE = 3;
	public final static int NODE_TYPE_GATEWAY_PARALLEL = 4;
	public final static int NODE_TYPE_GATEWAY_JOIN = 5;
	public final static int NODE_TYPE_END = 6;
	
	private String key;
	private String name;
	private String expression;
	private Integer type;
	private List<Operator> operators;

	public Node() {
		super();
	}

	public Node(Integer type, String key, String name, String expression) {
		this.type = type;
		this.key = key;
		this.name = name;
		this.expression = expression;
	}

	@Override
	public String toString() {
		return "Node - key: " + key + ", type: " + type + ", name: " + name + ", expression: " + expression;
	}
	
	public void addOperator(String operatorId, String operatorName, String operatorGroup) {
		Operator operator = new Operator();
		operator.setOperatorId(operatorId);
		operator.setOperatorName(operatorName);
		operator.setGroup(operatorGroup);
		operator.setNode(key);
		if(operators == null) operators = new ArrayList<>();
		operators.add(operator);
	}

	public String[] route(FlowContext<?> context) {
		switch (type) {
		case NODE_TYPE_GATEWAY_EXCLUSIVE:
			if(expression != null) {
				ScriptEngineManager manager = new ScriptEngineManager();
				ScriptEngine engine = manager.getEngineByName("js");
				engine.put("context", context.getData());
				engine.put("operators", operators);
				engine.put("operator", context.getOperator());
				try {
					Object result = engine.eval(expression);
					if(result != null) {
						return ((String) result).split(",");
					}
				} catch (ScriptException e) {
					throw new FlowException(e);
				}
			} else {
				return new String[] { key };
			}
		case NODE_TYPE_START:
		case NODE_TYPE_TASK:
			return new String[] { expression };
		case NODE_TYPE_GATEWAY_PARALLEL:
			return expression.split(",");
		default:
			return new String[] { key };
		}
	}

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

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public List<Operator> getOperators() {
		return operators;
	}

	public void setOperators(List<Operator> operators) {
		this.operators = operators;
	}
}
