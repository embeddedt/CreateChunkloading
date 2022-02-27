package org.embeddedt.createchunkloading.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.embeddedt.createchunkloading.CreateChunkloading;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CreateChunkloading.MOD_ID)
public class CreateChunkloadingForge {
    public CreateChunkloadingForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(CreateChunkloading.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CreateChunkloadingForge::clientInit);
        CreateChunkloading.init();
    }

    public static void clientInit(FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(CreateChunkloading.CHUNK_LOADER.get(), RenderType.cutout());
    }
}
