/*
 * Copyright 2020 Benjamin Martin
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

package net.lapsimc.lapisinventories.creativeblocks;

import net.lapismc.lapiscore.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.UUID;

/**
 * A class to store data about blocks placed in creative mode
 */
public class CreativeBlock {

    private final Location loc;
    private final Material mat;
    private final UUID uuid;

    public CreativeBlock(Location loc, Material mat, UUID uuid) {
        this.loc = loc;
        this.mat = mat;
        this.uuid = uuid;
    }

    public CreativeBlock(String s) {
        String[] arr = s.split(":");
        loc = new LocationUtils().parseStringToLocation(arr[0]);
        mat = Material.getMaterial(arr[1]);
        uuid = UUID.fromString(arr[2]);
    }

    public Location getLocation() {
        return loc;
    }

    public Material getMaterial() {
        return mat;
    }

    public UUID getUniqueID() {
        return uuid;
    }

    @Override
    public String toString() {
        return new LocationUtils().parseLocationToString(loc) + ":" + mat.name() + ":" + uuid.toString();
    }
}
