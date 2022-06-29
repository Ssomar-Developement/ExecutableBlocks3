package com.ssomar.executableblocks.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.ssomar.executableblocks.ExecutableBlocks;

/**
 *
 * @author sqlitetutorial.net
 */
public class Database {

	private static Database instance;

	private String fileName;
	
	public void load() {
		createNewDatabase("data.db");
	}
	
	public void createNewDatabase(String fileName) {

		this.fileName = fileName;

		String url = "jdbc:sqlite:"+ExecutableBlocks.getPluginSt().getDataFolder() +"/"+fileName;

		try (Connection conn = DriverManager.getConnection(url)) {
			if (conn != null) {
				ExecutableBlocks.plugin.getLogger().info(ExecutableBlocks.NAME_2+" "+"Connexion to the db...");
			}

		} catch (SQLException e) {
			ExecutableBlocks.plugin.getLogger().severe(e.getMessage());
		}
	}
	
	public Connection connect() {
		// SQLite connection string
		String url = "jdbc:sqlite:"+ExecutableBlocks.getPluginSt().getDataFolder() + "/"+fileName;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			ExecutableBlocks.plugin.getLogger().severe(ExecutableBlocks.NAME_2+" "+e.getMessage());
		}
		return conn;
	}

	public static Database getInstance() {
		if (instance == null) {
			instance = new Database(); 
		}
		return instance;
	}


}