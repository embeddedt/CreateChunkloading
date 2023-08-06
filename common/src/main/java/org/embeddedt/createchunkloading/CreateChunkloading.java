package org.embeddedt.createchunkloading;

import com.simibubi.create.AllMovementBehaviours;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.embeddedt.createchunkloading.blocks.ChunkLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class CreateChunkloading {
    public static final String MOD_ID = "createchunkloading";
    public static final Logger logger = LogManager.getLogger(MOD_ID);

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> BLOCKS_TAB = TABS.register("blocks_tab", () ->
            CreativeTabRegistry.create(Component.translatable("itemGroup.createchunkloading.blocks_tab"), () ->
                new ItemStack(CreateChunkloading.CHUNK_LOADER.get())
            )
    );

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(MOD_ID, Registries.BLOCK);
    public static final RegistrySupplier<Block> CHUNK_LOADER = BLOCKS.register("chunk_loader", ChunkLoader::new);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registries.ITEM);
    public static final RegistrySupplier<Item> CHUNK_LOADER_ITEM = ITEMS.register("chunk_loader", () -> new BlockItem(CHUNK_LOADER.get(), new Item.Properties().arch$tab(BLOCKS_TAB)));
    
    public static void init() {
        BLOCKS.register();
        ITEMS.register();
        TABS.register();
        CHUNK_LOADER.listen(block -> ExampleExpectPlatform.registerMovementBehavior());
    }
}
