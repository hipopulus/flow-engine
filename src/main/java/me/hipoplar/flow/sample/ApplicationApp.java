package me.hipoplar.flow.sample;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.hipoplar.flow.Activity;
import me.hipoplar.flow.Flow;
import me.hipoplar.flow.FlowContext;
import me.hipoplar.flow.FlowEngine;
import me.hipoplar.flow.Node;
import me.hipoplar.flow.Operator;
import me.hipoplar.flow.Path;

public class ApplicationApp {
	public static void main(String[] args) {
		Flow flow = new Flow();
		List<Node> nodes = new ArrayList<>();
		List<Path> paths = new ArrayList<>();
		
		flow.setName("Application Flow");
		flow.setNodes(nodes);
		flow.setPaths(paths);
		
		String operatorId = "1001";
		String operatorName = "Vincent";
		String operatorGroup = "ANY";
		
		Node start = new Node(Node.NODE_TYPE_START, flow.nextKey(), "开始", "'1'");
		start.addOperator(operatorId, operatorName, operatorGroup);
		nodes.add(start);
		
		Node application = new Node(Node.NODE_TYPE_TASK, flow.nextKey(), "申请", "if(context.applied) { '2'; } else { '1'; }");
		application.addOperator(operatorId, operatorName, operatorGroup);
		nodes.add(application);
		
		Node verification = new Node(Node.NODE_TYPE_GATEWAY, flow.nextKey(), "审核", "if(context.verified) { '3'; }  else { '1'; }") {} ;
		verification.addOperator(operatorId, operatorName, operatorGroup);
		nodes.add(verification);
		
		Node end = new Node(Node.NODE_TYPE_END, flow.nextKey(), "结束", "'4'");
		end.addOperator(operatorId, operatorName, operatorGroup);
		nodes.add(end);
		
		paths.add(new Path(start.getKey(), application.getKey()));
		paths.add(new Path(application.getKey(), verification.getKey()));
		paths.add(new Path(verification.getKey(), application.getKey()));
		paths.add(new Path(verification.getKey(), end.getKey()));
		
		FlowEngine flowEngine = FlowEngine.createEngine();
		flowEngine.createFLow(flow);
		Flow result = flowEngine.getFlow(flow.getName());
		System.out.println(result);
		for (Node node : result.getNodes()) {
			System.out.println(node);
		}
		
		for (Path path : result.getPaths()) {
			System.out.println(path);
		}
		
		FlowContext<Application> context = new FlowContext<>();
		Application sample = new Application();
		sample.setApplied(true);
		sample.setId(UUID.randomUUID().toString());
		sample.setMobile("18600000000");
		sample.setName("Lisa");
		sample.setVerified(false);
		context.setData(sample);
		Operator operator = new Operator();
		operator.setGroup(operatorGroup);
		operator.setOperatorId(operatorId);
		operator.setOperatorName(operatorName);
		context.setOperator(operator);
		context.setOperator(operator);
		flowEngine.start(flow.getName(), context);
		List<Activity> activities = flowEngine.getFlowActivities(flow.getName(), operatorId);
		for (Activity activity : activities) {
			System.out.println(activity);
			sample.setApplied(true);
			flowEngine.process(activity.getId(), context);
		}
		activities = flowEngine.getFlowActivities(flow.getName(), operatorId);
		for (Activity activity : activities) {
			System.out.println(activity);
			sample.setVerified(false);
			flowEngine.process(activity.getId(), context);
		}
		
		activities = flowEngine.getFlowActivities(flow.getName(), operatorId);
		for (Activity activity : activities) {
			System.out.println(activity);
			sample.setApplied(true);
			sample.setName("Jobs");
			flowEngine.process(activity.getId(), context);
		}
		
		activities = flowEngine.getFlowActivities(flow.getName(), operatorId);
		for (Activity activity : activities) {
			System.out.println(activity);
			System.out.println("Sorry, your application was rejected! Reapply it again, please!");
			sample.setVerified(true);
			flowEngine.process(activity.getId(), context);
		}
		
		activities = flowEngine.getFlowActivities(flow.getName(), operatorId);
		if(activities == null || activities.isEmpty()) {
			System.out.println("Welcome " + sample.getName() + "!");
		}
	}
}