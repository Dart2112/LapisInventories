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

class InventoriesBlockLogger {

    private MySQLManager MySQL;

    InventoriesBlockLogger(LapisInventories p) {
        if (p.getConfig().getBoolean("CreativeBlockTracking")) {
            MySQL = new MySQLManager(p.getConfig());
        }
    }

    void addBlock(Block block, UUID uuid) {
        //check if the block is already being stored, this shouldn't happen unless a non player object edits a block
        if (checkBlock(block)) {
            //if the block is already stored simply update the location and UUID
            MySQL.setData(concatenateLocation(block), "Location", uuid.toString());
        } else {
            //if the block doesn't exist we add it
            MySQL.addData(concatenateLocation(block), uuid.toString());
        }
    }

    void removeBlock(Block block) {
        //check to make sure we are storing the block
        if (checkBlock(block)) {
            //if so just drop the whole database row
            MySQL.dropRow(concatenateLocation(block));
        }
    }

    boolean checkBlock(Block block) {
        //get the string location then check if it stored in the database
        String location = concatenateLocation(block);
        return MySQL.getString(location, "Location") != null;
    }

    UUID checkPlacer(Block block) {
        //get the location of the block and retrieve the string version of the UUID that placed it
        String location = concatenateLocation(block);
        String uuid = MySQL.getString(location, "UUID");
        //since the UUID can be null if the block isn't stored we check that it isn't before getting the UUID object
        if (uuid != null) {
            return UUID.fromString(uuid);
        }
        //if the UUID was null we probably aren't storing the block or the database was externally edited
        //either way we cant return a UUID so just return null
        return null;
    }

    private String concatenateLocation(Block b) {
        //converts our location to a string that is more database friendly
        return b.getWorld() + "," + b.getX() + "," + b.getY() + "," + b.getZ();
    }

}

class MySQLManager {

    private String url;
    private String username;
    private String password;
    private String DBName;
    private Connection conn;

    MySQLManager(FileConfiguration config) {
        //verifyServerCertificate=false is required to stop console warnings on every connection
        url = "jdbc:mysql://%URL%/%DBName%?verifyServerCertificate=false&useSSL=true";
        this.username = config.getString("Database.username");
        this.password = config.getString("Database.password");
        this.DBName = config.getString("Database.dbName");
        //place the database URL and name into the URL used by JDBC
        url = url.replace("%URL%", config.getString("Database.location")).replace("%DBName%", DBName);
        //make sure we have a connection before we try to setup the database
        if (isConnected()) {
            setupDatabase();
        }
        //connection cleaning is used to make sure that connections don't stay open
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("LapisInventories"),
                this::connectionCleaning, 30 * 20, 30 * 20);
        //remove any rows that have old location formatting
        //TODO: remove this after a while as it wont be needed anymore
        Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("LapisInventories"), () -> {
            ResultSet rs = getAllRows();
            try {
                while (rs.next()) {
                    String loc = rs.getString("Location");
                    if (loc.split(",").length == 3) {
                        dropRow(loc);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void connectionCleaning() {
        try {
            //if we are still connected to the database then close the connection
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    @SuppressWarnings("SameParameterValue")
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

    private ResultSet getAllRows() {
        try {
            conn = getConnection();
            String sql = "SELECT * FROM CreativeBlocks";
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(sql);
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
                    "Location VARCHAR(40) NOT NULL," +
                    "UUID VARCHAR(36));";
            stmt.execute(sql);
            //make sure that we have the extended number of chars to store world information
            sql = "ALTER TABLE CreativeBlocks MODIFY COLUMN Location VARCHAR(40) NOT NULL;";
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