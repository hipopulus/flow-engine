package me.hipoplar.flow;

import java.util.ArrayList;
import java.util.List;

public class App {
	public static void main(String[] args) {
		Flow flow = new Flow();
		List<Node> nodes = new ArrayList<>();
		List<Path> paths = new ArrayList<>();
		
		flow.setName("Application Flow");
		flow.setNodes(nodes);
		flow.setPaths(paths);
		
		Node start = new Node(flow.nextKey(), "开始", null);
		nodes.add(start);
		
		Node application = new Node(flow.nextKey(), "申请", null);
		nodes.add(application);
		
		Node verification = new Node(flow.nextKey(), "审核", "if(result == 1) return 3; else return 1;");
		nodes.add(verification);
		
		Node end = new Node(flow.nextKey(), "结束", null);
		nodes.add(end);
		
		paths.add(new Path(start.getKey(), application.getKey()));
		paths.add(new Path(application.getKey(), verification.getKey()));
		paths.add(new Path(verification.getKey(), application.getKey()));
		paths.add(new Path(verification.getKey(), end.getKey()));
		
		FlowEngine flowEngine = FlowEngine.createEngine();
		flowEngine.createFLow(flow);
		Flow result = flowEngine.getFlow(flow.getName());
		System.out.println(result.getName());
		for (Node node : result.getNodes()) {
			System.out.println(node);
		}
		
		for (Path path : result.getPaths()) {
			System.out.println(path);
		}
	}
}