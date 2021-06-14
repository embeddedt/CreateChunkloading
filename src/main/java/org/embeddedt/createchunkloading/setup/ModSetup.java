package org.embeddedt.createchunkloading.setup;

import org.embeddedt.createchunkloading.blocks.ModBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModSetup {
    public static ItemGroup itemGroup = new ItemGroup("createchunkloading") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModBlocks.CHUNK_LOADER);
        }
    };

    public void init() {

    }
}
