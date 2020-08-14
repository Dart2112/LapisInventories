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

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreativeBlocksManager {

    HashMap<Location, CreativeBlock> creativeBlocks = new HashMap<>();

    public void loadBlocks() {
        //TODO: load this from file
        List<String> list = new ArrayList<>();
        for (String s : list) {
            CreativeBlock block = new CreativeBlock(s);
            creativeBlocks.put(block.getLocation(), block);
        }
    }

    public void saveBlocks() {
        List<String> list = new ArrayList<>();
        for (CreativeBlock blocks : creativeBlocks.values()) {
            list.add(blocks.toString());
        }
        //TODO: save the list to file
    }

}
