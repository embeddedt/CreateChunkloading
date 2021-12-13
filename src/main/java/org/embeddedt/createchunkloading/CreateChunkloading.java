package org.embeddedt.createchunkloading;

import net.minecraftforge.common.world.ForgeChunkManager;
import org.embeddedt.createchunkloading.blocks.ChunkLoader;
import org.embeddedt.createchunkloading.misc.ChunkLoaderList;
import org.embeddedt.createchunkloading.misc.ChunkLoaderMovementBehaviour;
import org.embeddedt.createchunkloading.misc.IChunkLoaderList;
import org.embeddedt.createchunkloading.setup.ModSetup;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.embeddedt.createchunkloading.blocks.ChunkLoaderTile;
import org.embeddedt.createchunkloading.blocks.ModBlocks;

import static com.simibubi.create.AllMovementBehaviours.addMovementBehaviour;

import javax.annotation.Nonnull;
import java.util.Map;

@Mod(CreateChunkloading.modid)
public class CreateChunkloading {
    public static final String modid = "createchunkloading";
    @SuppressWarnings("unused")
    public static final String version = "0.2.0";
    public static final Logger logger = LogManager.getLogger(modid);
    public static ModSetup setup = new ModSetup();
    public static CreateChunkloading instance;
    @CapabilityInject(IChunkLoaderList.class)
    public static Capability<IChunkLoaderList> CHUNK_LOADING_CAPABILITY = null;

    public CreateChunkloading() {
        instance = this;

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientRegistries);

        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("createchunkloading-common.toml"));

        MinecraftForge.EVENT_BUS.register(this);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(CreateChunkloading::clientInit);
    }

    public static Map<ResourceLocation, IRecipe<?>> getRecipes(IRecipeType<?> recipeType, RecipeManager manager) {
        final Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> recipesMap = ObfuscationReflectionHelper.getPrivateValue(RecipeManager.class, manager, "recipes");
        assert recipesMap != null;
        return recipesMap.get(recipeType);
    }

    public static void clientInit(FMLClientSetupEvent event) {
        RenderTypeLookup.setRenderLayer(ModBlocks.CHUNK_LOADER, RenderType.translucent());
        registerRenderers();
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers() {
    }


    public static ResourceLocation generateResourceLocation(String resourceName) {
        return new ResourceLocation(modid, resourceName);
    }


    private void setup(final FMLCommonSetupEvent event) {
        setup.init();

        logger.info("Setup method registered.");
    }

    private void clientRegistries(final FMLClientSetupEvent event) {
        logger.info("Client method registered.");
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        @SuppressWarnings("unused")
        public static void registerItems(final RegistryEvent.Register<Item> event) {
            logger.info("items registering");
            Item.Properties properties = new Item.Properties().tab(ModSetup.itemGroup);

            event.getRegistry().register(new BlockItem(ModBlocks.CHUNK_LOADER, properties).setRegistryName("chunk_loader"));
            logger.info("finished items registering");
        }

        @SubscribeEvent
        @SuppressWarnings("unused")
        public static void registerBlocks(final RegistryEvent.Register<Block> event) {


            logger.info("blocks registering");
            Block b = new ChunkLoader();
            event.getRegistry().register(b);
            addMovementBehaviour(event.getRegistry().getKey(b), new ChunkLoaderMovementBehaviour());
            logger.info("finished blocks registering");
        }

/*
        @SubscribeEvent
        @SuppressWarnings("unused")
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
            logger.info("TEs registering");
            event.getRegistry().register(TileEntityType.Builder.create(ChunkLoaderTile::new, ModBlocks.CHUNK_LOADER).build(null).setRegistryName("chunk_loader"));
            logger.info("finished TEs registering");
        }
 */

    }
}
