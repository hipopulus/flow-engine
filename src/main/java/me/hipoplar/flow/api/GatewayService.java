package me.hipoplar.flow.api;

import me.hipoplar.flow.model.Node;
@SPI
public interface GatewayService {
	boolean isJoined(Node gateway);
	void join(Node gateway, Node joinedNode);
}
