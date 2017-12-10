package me.hipoplar.flow.api;

import java.sql.Connection;
@SPI
public interface DatabaseEngine {
	Connection getConnection();
}
