/*
 * Copyright 2018 Benjamin Martin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.lapsimc.lapisinventories;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.UUID;

public class InventoriesBlockLogger {

    private MySQLManager MySQL;
    public UUID importedUUID = UUID.fromString("ImportedData");

    InventoriesBlockLogger(LapisInventories p) {
        if (p.getConfig().getBoolean("CreativeBlockTracking")) {
            MySQL = new MySQLManager(p.getConfig());
        }
    }

    public void addBlock(Block block, UUID uuid) {
        if (checkBlock(block)) {
            MySQL.setData(concatenateLocation(block), "Location", uuid.toString());
        } else {
            MySQL.addData(concatenateLocation(block), uuid.toString());
        }
    }

    void removeBlock(Block block) {
        if (checkBlock(block)) {
            MySQL.dropRow(concatenateLocation(block));
        }
    }

    boolean checkBlock(Block block) {
        String location = concatenateLocation(block);
        return MySQL.getString(location, "Location") != null;
    }

    UUID checkPlacer(Block block) {
        String location = concatenateLocation(block);
        String uuid = MySQL.getString(location, "UUID");
        if (uuid != null) {
            return UUID.fromString(uuid);
        }
        return null;
    }

    private String concatenateLocation(Block b) {
        return b.getX() + "," + b.getY() + "," + b.getZ();
    }

}

class MySQLManager {

    private String url;
    private String username;
    private String password;
    private String DBName;
    private Connection conn;

    MySQLManager(FileConfiguration config) {
        url = "jdbc:mysql://%URL%/%DBName%?verifyServerCertificate=false&useSSL=true";
        this.username = config.getString("Database.username");
        this.password = config.getString("Database.password");
        this.DBName = config.getString("Database.dbName");
        url = url.replace("%URL%", config.getString("Database.location")).replace("%DBName%", DBName);
        if (isConnected()) {
            setupDatabase();
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("LapisInventories"),
                connectionCleaning(), 30 * 20, 30 * 20);
    }

    private Runnable connectionCleaning() {
        return () -> {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };
    }

    void addData(String location, String uuid) {
        try {
            conn = getConnection();
            String sql = "INSERT INTO CreativeBlocks(Location,UUID) VALUES(?,?)";
            PreparedStatement preStatement = conn.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            preStatement.setString(1, location);
            preStatement.setString(2, uuid);
            preStatement.execute();
            preStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void setData(String location, String path, String uuid) {
        try {
            conn = getConnection();
            String sqlUpdate = "UPDATE CreativeBlocks SET " + path + " = ? WHERE UUID = ?";
            PreparedStatement preStatement = conn.prepareStatement(sqlUpdate);
            preStatement.setString(1, location);
            preStatement.setString(2, uuid);
            preStatement.execute();
            preStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    String getString(String location, String key) {
        try {
            ResultSet rs = getResults(location, key);
            if (!rs.isBeforeFirst()) {
                return null;
            }
            rs.next();
            return rs.getString(key);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ResultSet getResults(String location, String item) {
        try {
            conn = getConnection();
            String sql = "SELECT " + item + " FROM CreativeBlocks WHERE Location = ?";
            PreparedStatement preStatement = conn.prepareStatement(sql);
            preStatement.setString(1, location);
            return preStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    void dropRow(String location) {
        try {
            conn = getConnection();
            String sqlUpdate = "DELETE FROM CreativeBlocks WHERE Location = ?";
            PreparedStatement preStatement = conn.prepareStatement(sqlUpdate);
            preStatement.setString(1, location);
            preStatement.execute();
            preStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getAllRows() {
        try {
            conn = getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM CreativeBlocks";
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getRows() {
        try {
            conn = getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM CreativeBlocks";
            ResultSet rs = stmt.executeQuery(sql);
            rs.last();
            return rs.getRow();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private boolean isConnected() {
        try {
            DriverManager.getConnection(url, username, password).close();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    private void setupDatabase() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = getConnection();
            Statement stmt = conn.createStatement();
            String sql = "CREATE DATABASE IF NOT EXISTS " + DBName;
            stmt.execute(sql);
            stmt = conn.createStatement();
            sql = "CREATE TABLE IF NOT EXISTS CreativeBlocks (" +
                    "Location VARCHAR(23) NOT NULL," +
                    "UUID VARCHAR(36));";
            stmt.execute(sql);
        } catch (SQLException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                return DriverManager.getConnection(url, username, password);
            } else {
                return conn;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}