package me.hipoplar.flow;

public interface GatewayService {
	boolean isJoined(Node gateway);
	void join(Node gateway, Node joinedNode);
}
