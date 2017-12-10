package me.hipoplar.flow;

import java.util.List;
import java.util.UUID;

import junit.framework.TestCase;
import me.hipoplar.flow.model.Activity;
import me.hipoplar.flow.model.Flow;
import me.hipoplar.flow.model.Node;
import me.hipoplar.flow.model.Operator;
import me.hipoplar.flow.model.Path;
import me.hipoplar.flow.simple.SimpleDataBaseEngine;

public class PaymentTest extends TestCase {
	public void test() {
		/* =====================================================================================================*/
		System.out.println("Defining flow...");
		Flow flow = new Flow();
		flow.setName("Application Flow");
		String operatorId = "1001";
		String operatorName = "Vincent";
		String operatorGroup = "ANY";
		// Start node
		Node start = flow.addNode("Start", Node.NODE_TYPE_START);
		start.addOperator(operatorId, operatorName, operatorGroup);
		// Application node
		Node application = flow.addNode("Apply", Node.NODE_TYPE_TASK);
		application.addOperator(operatorId, operatorName, operatorGroup);
		// Payment node
		Node payment = flow.addNode("Pay", Node.NODE_TYPE_TASK);
		payment.addOperator(operatorId, operatorName, operatorGroup);
		// Verification node
		Node verification = flow.addNode("Verify", Node.NODE_TYPE_TASK);
		verification.addOperator(operatorId, operatorName, operatorGroup);
		// Parallel gateway
		Node parallel = flow.addNode("Parallel Gateway", Node.NODE_TYPE_GATEWAY_PARALLEL);
		parallel.addOperator(operatorId, operatorName, operatorGroup);
		// Join gateway
		Node join = flow.addNode("Join Gateway", Node.NODE_TYPE_GATEWAY_JOIN);
		join.addOperator(operatorId, operatorName, operatorGroup);
		// Verification gateway
		Node check = flow.addNode("Verification Gateway", Node.NODE_TYPE_GATEWAY_EXCLUSIVE);
		check.addOperator(operatorId, operatorName, operatorGroup);
		// End node
		Node end = flow.addNode("End", Node.NODE_TYPE_END);
		end.addOperator(operatorId, operatorName, operatorGroup);
		// From Start to Application
		flow.direct(start.getKey(), application.getKey());
		// From Application to Parallel
		flow.direct(application.getKey(), parallel.getKey());
		// From Parallel gateway to Payment and Verification
		flow.parallel(parallel.getKey(), payment.getKey(), verification.getKey());
		// From Verification to Check
		flow.direct(verification.getKey(), check.getKey());
		// From Check to Application or to Join
		Expression verifyExpression = new Expression().iF("context.verified").then(join.getKey()).elseThen(application.getKey());
		flow.exclude(verifyExpression.build(), check.getKey(), application.getKey(), join.getKey());
		// Join
		flow.join(join.getKey(), check.getKey(), payment.getKey());
		// From Join to End
		flow.direct(join.getKey(), end.getKey());
		/* =====================================================================================================*/
		
		// Create engine
		FlowEngine flowEngine = FlowEngine.createEngine(new SimpleDataBaseEngine());
		// Create flow
		Flow instance = flowEngine.instance(flow, "A Payment Sample");
		System.out.println(instance);
		for (Node node : instance.getNodes()) {
			System.out.println(node);
		}

		for (Path path : instance.getPaths()) {
			System.out.println(path);
		}
		System.out.println("====================================================================================");
		// Prepare application form
		System.out.println("Preparing application form...");
		FlowContext<Application> context = new FlowContext<>();
		Application sample = new Application();
		sample.setApplied(false);
		sample.setId(UUID.randomUUID().toString());
		sample.setMobile("18600000000");
		sample.setName("Lisa");
		sample.setVerified(false);
		sample.setPaid(false);
		context.setData(sample);
		Operator operator = new Operator();
		operator.setGroup(operatorGroup);
		operator.setOperatorId(operatorId);
		operator.setOperatorName(operatorName);
		context.setOperator(operator);
		context.setOperator(operator);
		// Start flow
		System.out.println("Starting flow...");
		flowEngine.start(flow.getKey(), context);
		// Submit
		System.out.println("Submiting...");
		List<Activity> activities = flowEngine.getFlowActivities(flow.getKey(), operatorId);
		for (Activity activity : activities) {
			sample.setApplied(true);
			flowEngine.process(activity.getId(), context);
		}
		// Reject and Pay
		activities = flowEngine.getFlowActivities(flow.getKey(), operatorId);
		for (Activity activity : activities) {
			if(activity.getName().equals("Verify")) {
				System.out.println("Rejecting...");
				sample.setVerified(false);
			} else if(activity.getName().equals("Pay")) {
				System.out.println("Paying...");
				sample.setPaid(true);
			}
			flowEngine.process(activity.getId(), context);
		}
		System.out.println("Opps! Your application was rejected! Please resubmit it again!");
		// Resubmit
		System.out.println("Resubmiting form...");
		activities = flowEngine.getFlowActivities(flow.getKey(), operatorId);
		for (Activity activity : activities) {
			sample.setApplied(true);
			sample.setName("Jobs");
			flowEngine.process(activity.getId(), context);
		}
		// Verify
		System.out.println("Verifying...");
		activities = flowEngine.getFlowActivities(flow.getKey(), operatorId);
		for (Activity activity : activities) {
			if(activity.getName().equals("Verify")) {
				System.out.println("Verifying...");
				sample.setVerified(true);
			} else if(activity.getName().equals("Pay")) {
				System.out.println("Paying...");
				sample.setPaid(true);
			}
			flowEngine.process(activity.getId(), context);
		}

		activities = flowEngine.getFlowActivities(flow.getKey(), operatorId);
		if (activities == null || activities.isEmpty()) {
			System.out.println("Welcome to join us, enjoy it!");
		}
	}
}
