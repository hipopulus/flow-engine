package me.hipoplar.flow.api;

import me.hipoplar.flow.model.Flow;
import me.hipoplar.flow.model.FlowDef;
@SPI
public interface FlowService {
	FlowDef createFLow(Flow flow, String flowxml);
	FlowDef getFlow(String key);
}
