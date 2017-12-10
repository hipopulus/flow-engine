package me.hipoplar.flow.api;

import java.util.List;

import me.hipoplar.flow.model.Activity;
import me.hipoplar.flow.model.Flow;
import me.hipoplar.flow.model.Node;

@SPI
public interface ActivityService {
	Activity getActivity(String activityId);
	List<Activity> createNodeActivity(Flow flow, Node node);
	boolean completeActivity(String nodeKey, String operatorId, String operatorName);
	List<Activity> getFlowActivities(String flow, String operatorId);
}
