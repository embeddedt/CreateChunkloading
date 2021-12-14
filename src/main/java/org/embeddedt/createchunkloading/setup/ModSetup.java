package org.embeddedt.createchunkloading.setup;

import org.embeddedt.createchunkloading.blocks.ModBlocks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModSetup {
    public static CreativeModeTab itemGroup = new CreativeModeTab("createchunkloading") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModBlocks.CHUNK_LOADER);
        }
    };

    public void init() {

    }
}
