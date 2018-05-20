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
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class LapisInventoriesAPI {

    private static List<JavaPlugin> hooks = new ArrayList<>();
    private static LapisInventories plugin;

    LapisInventoriesAPI(LapisInventories p) {
        plugin = p;
    }

    public LapisInventoriesAPI() {
    }

    public void addLoginHook(JavaPlugin p) {
        hooks.add(p);
        Bukkit.getLogger().info("[LapisInventories] New login plugin " + p.getName() + " added");
    }

    public void hideInventory(Player p, GameMode gm) {
        plugin.invManager.saveInventory(p, gm);
    }

    public void giveInventory(Player p, GameMode gm) {
        plugin.invManager.loadInventory(p, gm);
    }

    public void loginComplete(Player p, GameMode gm) {
        plugin.invManager.loadInventory(p, gm);
    }

    List<JavaPlugin> getHooks() {
        return hooks;
    }

}
