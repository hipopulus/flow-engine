package me.hipoplar.flow.api;

import me.hipoplar.flow.model.FlowDef;
@SPI
public interface FlowDefService {
	FlowDef createFLow(FlowDef flow);
	FlowDef getFlow(String name);
}
