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

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

class InventoriesConfigurations {

    private LapisInventories plugin;
    private YamlConfiguration messages;

    InventoriesConfigurations(LapisInventories p) {
        plugin = p;
        loadMessages();
        checkConfigVersion();
    }

    private void loadMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            try (InputStream is = plugin.getResource("messages.yml");
                 OutputStream os = new FileOutputStream(messagesFile)) {
                int readBytes;
                byte[] buffer = new byte[4096];
                while ((readBytes = is.read(buffer)) > 0) {
                    os.write(buffer, 0, readBytes);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    private void checkConfigVersion() {
        if (plugin.getConfig().getInt("ConfigVersion") != 2) {
            File oldConfig = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "config_old.yml");
            File newConfig = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "config.yml");
            if (!newConfig.renameTo(oldConfig)) {
                plugin.getLogger().info(plugin.getName() + " failed to update the config.yml");
            }
            plugin.saveDefaultConfig();

            loadMessages();
            File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
            File oldMessages = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "Messages_old.yml");
            if (!messagesFile.renameTo(oldMessages)) {
                plugin.getLogger().info(plugin.getName() + " failed to update the Messages.yml");
            }
            loadMessages();
            plugin.getLogger().info("New Configuration Generated for " + plugin.getName() + "," +
                    " Please Transfer Values From config_old.yml & Messages_old.yml");
        }
    }

    String getMessage(String key) {
        return ChatColor.stripColor(getColoredMessage(key));
    }

    String getColoredMessage(String key) {
        loadMessages();
        return ChatColor.translateAlternateColorCodes('&', messages.getString(key));
    }

}
