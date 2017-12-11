package me.hipoplar.flow;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import me.hipoplar.flow.api.ActivityService;
import me.hipoplar.flow.api.FlowService;
import me.hipoplar.flow.api.GatewayService;
import me.hipoplar.flow.model.Activity;
import me.hipoplar.flow.model.Flow;
import me.hipoplar.flow.model.FlowDef;
import me.hipoplar.flow.model.Node;

public class FlowEngine {
	protected FlowService flowService;
	protected ActivityService activityService;
	protected GatewayService gatewayService;
	public FlowEngine(FlowService flowService, ActivityService activityService, GatewayService gatewayService) {
		super();
		this.flowService = flowService;
		this.activityService = activityService;
		this.gatewayService = gatewayService;
	}
	
	public final Flow instance(String definitionKey, String name) {
		if (name == null || name.trim().length() == 0) {
			throw new FlowException("Flow name not specified.");
		}
		Flow flow = getFlow(definitionKey);
		if(flow == null) {
			throw new FlowException("Flow definition not found.");
		}
		flow.setKey(IdGenerator.instance().nextId());
		flow.setInstantial(true);
		getFlowService().createFLow(flow, toXml(flow));
		return flow;
	}
	
	public final Flow instance(Flow flow, String name) {
		if (name == null || name.trim().length() == 0) {
			throw new FlowException("Flow name not specified.");
		}
		flow.setName(name);
		flow.setKey(IdGenerator.instance().nextId());
		flow.setInstantial(true);
		getFlowService().createFLow(flow, toXml(flow));
		return flow;
	}
	
	public final Flow define(Flow flow) {
		if (flow.getName() == null || flow.getName().trim().length() == 0) {
			throw new FlowException("Flow name not specified.");
		}
		flow.setKey(IdGenerator.instance().nextId());
		flow.setInstantial(false);
		getFlowService().createFLow(flow, toXml(flow));
		return flow;
	}

	public final Flow getFlow(String key) {
		if (key == null || key.trim().length() == 0) {
			throw new FlowException("Flow key not specified.");
		}
		FlowDef flowDef = getFlowService().getFlow(key);
		Flow flow = null;
		if(flowDef != null) {
			flow = toJavaObject(flowDef.getFlowxml());
		}
		return flow;
	}

	public final void start(String key, FlowContext<?> context) {
		Flow flow = getFlow(key);
		if (flow == null) {
			throw new FlowException("Flow key not specified.");
		}
		Node start = flow.search(Node.NODE_TYPE_START).get(0);
		String[] nextNodes = start.route(context);
		if(nextNodes == null || nextNodes.length == 0) {
			return;
		}
		for (String nodeKey : nextNodes) {
			if(nodeKey.trim().equals(start.getKey().trim())) {
				return;
			}
		}
		for (String nextNodeKey : nextNodes) {
			Node nextNode = flow.search(nextNodeKey);
			flowTo(flow, start, nextNode, context);
		}
	}
	
	public List<Activity> getFlowActivities(String flow, String operatorId) {
		return getActivityService().getFlowActivities(flow, operatorId);
	}

	public final void process(String activityId, FlowContext<?> context) {
		Activity activity = getActivityService().get(activityId);
		if(activity == null) {
			throw new FlowException("Activity not found.");
		}
		if(activity.getComplete() != null && activity.getComplete()) {
			throw new FlowException("Activity completed.");
		}
		Flow flow = getFlow(activity.getFlow());
		Node node = flow.search(activity.getNode());
		String[] nextNodes = node.route(context);
		if(nextNodes == null || nextNodes.length == 0) {
			return;
		}
		for (String nodeKey : nextNodes) {
			if(nodeKey.trim().equals(node.getKey().trim())) {
				return;
			}
		}
		if(!getActivityService().complete(node.getKey(), context.getOperator().getOperatorId(), context.getOperator().getOperatorName())) {
			throw new FlowException("Complete activity error.");
		}
		for (String nextNodeKey : nextNodes) {
			Node nextNode = flow.search(nextNodeKey);
			flowTo(flow, node, nextNode, context);
		}
	}
	
	protected final void flowTo(Flow flow, Node from, Node to, FlowContext<?> context) {
		if(to.getType() == Node.NODE_TYPE_GATEWAY_JOIN) {
			getGatewayService().join(to, from);
			if(getGatewayService().isJoined(to)) {
				return;
			}
		}
		String[] nextNodes = null;
		switch (to.getType()) {
		case Node.NODE_TYPE_START:
		case Node.NODE_TYPE_GATEWAY_EXCLUSIVE:
		case Node.NODE_TYPE_GATEWAY_PARALLEL:
		case Node.NODE_TYPE_GATEWAY_JOIN:
			nextNodes = to.route(context);
			if(nextNodes != null) {
				for (String nextNodeKey : nextNodes) {
					if(!nextNodeKey.trim().equals(to.getKey().trim())) {
						Node nextNode = flow.search(nextNodeKey); 
						flowTo(flow, to, nextNode, context);
					}
				}
			}
			break;
		case Node.NODE_TYPE_TASK:
			getActivityService().createNodeActivities(flow, to);
			break;
		default:
			break;
		}
	}
	
	protected final String toXml(Flow flow) {
		StringWriter sw = new StringWriter();
		try {
			JAXBContext context = JAXBContext.newInstance(flow.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(flow, sw);
			return sw.toString();
		} catch (JAXBException e) {
			throw new FlowException(e);
		}
	}

	protected final Flow toJavaObject(String flowXml) {
		if (flowXml == null || flowXml.trim().length() == 0) {
			return null;
		}
		try {
			JAXBContext context = JAXBContext.newInstance(Flow.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			StringReader sr = new StringReader(flowXml);
			return (Flow) unmarshaller.unmarshal(sr);
		} catch (JAXBException e) {
			throw new FlowException(e);
		}
	}
	

	protected FlowService getFlowService() {
		return flowService;
	}

	public ActivityService getActivityService() {
		return activityService;
	}

	public GatewayService getGatewayService() {
		return gatewayService;
	}

}
