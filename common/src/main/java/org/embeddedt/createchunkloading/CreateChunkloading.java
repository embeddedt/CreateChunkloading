package org.embeddedt.createchunkloading;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.embeddedt.createchunkloading.blocks.ChunkLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import static com.simibubi.create.AllMovementBehaviours.addMovementBehaviour;

public class CreateChunkloading {
    public static final String MOD_ID = "createchunkloading";
    public static final Logger logger = LogManager.getLogger(MOD_ID);

    public static final CreativeModeTab BLOCKS_TAB = CreativeTabRegistry.create(new ResourceLocation(MOD_ID, "blocks_tab"), () ->
            new ItemStack(CreateChunkloading.CHUNK_LOADER.get()));

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(MOD_ID, Registry.BLOCK_REGISTRY);
    public static final RegistrySupplier<Block> CHUNK_LOADER = BLOCKS.register("chunk_loader", ChunkLoader::new);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registry.ITEM_REGISTRY);
    public static final RegistrySupplier<Item> CHUNK_LOADER_ITEM = ITEMS.register("chunk_loader", () -> new BlockItem(CHUNK_LOADER.get(), new Item.Properties().tab(BLOCKS_TAB)));
    
    public static void init() {
        BLOCKS.register();
        ITEMS.register();
        System.out.println(ExampleExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());
        addMovementBehaviour(CHUNK_LOADER.getId(), new ChunkLoaderMovementBehaviour());
    }
}
