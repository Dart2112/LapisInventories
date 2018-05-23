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

package net.lapsimc.lapisinventories.importers;

import net.lapsimc.lapisinventories.LapisInventories;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;

public class GameModeInventoriesHook {


    private static final Method ADD_URL_METHOD;

    static {
        Method addUrlMethod;
        try {
            addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addUrlMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
        ADD_URL_METHOD = addUrlMethod;
    }

    private LapisInventories plugin;

    public GameModeInventoriesHook(LapisInventories p) {
        plugin = p;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::injectCode);
    }

    private void injectCode() {
        File jar = new File(plugin.getDataFolder() + File.separator + "GameModeInventoriesRequestAPI.jar");
        if (!jar.exists()) {
            try {
                URL jarURL = new URL("https://github.com/LapisPlugins/LapisInventories/raw/master/RequestAPIs/GameModeInventoriesRequestAPI.jar");
                ReadableByteChannel jarByteChannel = Channels.newChannel(jarURL.openStream());
                if (!jar.exists()) {
                    if (!jar.createNewFile()) {
                        plugin.getLogger().info("Failed to download GameModeInventories code injection!");
                        return;
                    }
                }
                FileOutputStream jarOutputStream = new FileOutputStream(jar);
                jarOutputStream.getChannel().transferFrom(jarByteChannel, 0, Long.MAX_VALUE);
                jarByteChannel.close();
                jarOutputStream.flush();
                jarOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Class.forName("me.eccentric_nz.gamemodeinventories.GameModeInventoriesRequestAPI");
        } catch (ClassNotFoundException e) {
            ClassLoader classLoader = getClass().getClassLoader();
            if (classLoader instanceof URLClassLoader) {
                try {
                    ADD_URL_METHOD.invoke(classLoader, jar.toURI().toURL());
                } catch (IllegalAccessException | InvocationTargetException | MalformedURLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        retrieveData();
        jar.deleteOnExit();
    }

    private void retrieveData() {
        List<Location> locations = new me.eccentric_nz.gamemodeinventories.GameModeInventoriesRequestAPI().getBlocks();
        for (Location l : locations) {
            plugin.blockLogger.addBlock(l.getBlock(), plugin.blockLogger.importedUUID);
        }
        plugin.getLogger().info(locations.size() + " creative block Location records imported from GameModeInventories");
        plugin.getLogger().info("Unfortunately GameModeInventories doesn't support import of inventories!");
    }


}
