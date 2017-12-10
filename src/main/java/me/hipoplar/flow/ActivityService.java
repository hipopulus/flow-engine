package me.hipoplar.flow;

import java.util.List;

public interface ActivityService {
	Activity getActivity(String activityId);
	List<Activity> createNodeActivity(Flow flow, Node node);
	boolean completeActivity(String nodeKey, String operatorId, String operatorName);
	List<Activity> getFlowActivities(String flow, String operatorId);
}
