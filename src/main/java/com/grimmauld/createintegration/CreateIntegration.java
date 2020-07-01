package com.grimmauld.createintegration;

import com.grimmauld.createintegration.blocks.*;
import com.grimmauld.createintegration.misc.ChunkLoaderList;
import com.grimmauld.createintegration.misc.EnderList;
import com.grimmauld.createintegration.misc.IChunkLoaderList;
import com.grimmauld.createintegration.recipes.RecipeTypeRolling;
import com.grimmauld.createintegration.recipes.RollingRecipe;
import com.grimmauld.createintegration.recipes.TagToTagProcessingRecipe;
import com.grimmauld.createintegration.setup.ModSetup;
import com.simibubi.create.content.contraptions.components.crusher.CrushingRecipe;
import com.simibubi.create.content.contraptions.components.millstone.MillingRecipe;
import com.simibubi.create.content.contraptions.components.press.PressingRecipe;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
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

import javax.annotation.Nonnull;
import java.util.Map;

@Mod(CreateIntegration.modid)
public class CreateIntegration {
    public static final String modid = "createintegration";
    @SuppressWarnings("unused")
    public static final String version = "0.1.8";
    public static final Logger logger = LogManager.getLogger(modid);
    public static final IRecipeType<RollingRecipe> ROLLING_RECIPE = new RecipeTypeRolling();
    public static final TagToTagProcessingRecipe<PressingRecipe> TAG_TO_TAG_PRESS_SERIALIZER = new TagToTagProcessingRecipe<>(PressingRecipe::new, "tagtotagpressing");
    public static final TagToTagProcessingRecipe<CrushingRecipe> TAG_TO_TAG_CRUSHING_SERIALIZER = new TagToTagProcessingRecipe<>(CrushingRecipe::new, "tagtotagcrushing");
    public static final TagToTagProcessingRecipe<MillingRecipe> TAG_TO_TAG_MILLING_SERIALIZER = new TagToTagProcessingRecipe<>(MillingRecipe::new, "tagtotagmilling");
    public static ModSetup setup = new ModSetup();
    public static CreateIntegration instance;
    @CapabilityInject(IChunkLoaderList.class)
    public static Capability<IChunkLoaderList> CHUNK_LOADING_CAPABILITY = null;

    @CapabilityInject(EnderList.class)
    public static Capability<EnderList> ENDER_CRATE_CAPABILITY = null;


    public CreateIntegration() {
        instance = this;

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientRegistries);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(IRecipeSerializer.class, this::registerRecipeSerializers);


        Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve("createintegration-client.toml"));
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("createintegration-common.toml"));

        MinecraftForge.EVENT_BUS.register(this);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(CreateIntegration::clientInit);
    }

    public static Map<ResourceLocation, IRecipe<?>> getRecipes(IRecipeType<?> recipeType, RecipeManager manager) {
        final Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> recipesMap = ObfuscationReflectionHelper.getPrivateValue(RecipeManager.class, manager, "field_199522_d");
        assert recipesMap != null;
        return recipesMap.get(recipeType);
    }

    public static void clientInit(FMLClientSetupEvent event) {
        ScreenManager.registerFactory(ModBlocks.ENDER_CONTAINER, EnderGui::new);
        RenderTypeLookup.setRenderLayer(ModBlocks.CHUNK_LOADER, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.ROLLING_MACHINE, RenderType.getCutoutMipped());  // FIXME
        registerRenderers();
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers() {
        ClientRegistry.bindTileEntityRenderer(ModBlocks.ROLLING_MACHINE_TILE, RollingMachineTileEntityRenderer::new);
    }


    public static ResourceLocation generateResourceLocation(String resourceName) {
        return new ResourceLocation(modid, resourceName);
    }

    private void registerRecipeSerializers(Register<IRecipeSerializer<?>> event) {
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(ROLLING_RECIPE.toString()), ROLLING_RECIPE);
        event.getRegistry().register(RollingRecipe.SERIALIZER);
        event.getRegistry().register(TAG_TO_TAG_PRESS_SERIALIZER);
        event.getRegistry().register(TAG_TO_TAG_CRUSHING_SERIALIZER);
        event.getRegistry().register(TAG_TO_TAG_MILLING_SERIALIZER);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void attachWorldCaps(AttachCapabilitiesEvent<World> event) {
        if (event.getObject().isRemote) return;
        final LazyOptional<IChunkLoaderList> loaderInst = LazyOptional.of(() -> new ChunkLoaderList((ServerWorld) event.getObject()));
        final ICapabilitySerializable<INBT> loadingCapability = new ICapabilitySerializable<INBT>() {
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
                return CHUNK_LOADING_CAPABILITY.orEmpty(cap, loaderInst);
            }

            @Override
            public INBT serializeNBT() {
                return CHUNK_LOADING_CAPABILITY.writeNBT(loaderInst.orElse(null), null);
            }

            @Override
            public void deserializeNBT(INBT nbt) {
                loaderInst.ifPresent(h -> CHUNK_LOADING_CAPABILITY.readNBT(h, null, nbt));
            }
        };

        final LazyOptional<EnderList> enderInst = LazyOptional.of(EnderList::new);
        final ICapabilitySerializable<INBT> enderCapability = new ICapabilitySerializable<INBT>() {
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
                return ENDER_CRATE_CAPABILITY.orEmpty(cap, enderInst);
            }

            @Override
            public INBT serializeNBT() {
                return ENDER_CRATE_CAPABILITY.writeNBT(enderInst.orElse(null), null);
            }

            @Override
            public void deserializeNBT(INBT nbt) {
                enderInst.ifPresent(h -> ENDER_CRATE_CAPABILITY.readNBT(h, null, nbt));
            }
        };

        event.addCapability(new ResourceLocation(modid, "create_integration_loader"), loadingCapability);
        event.addCapability(new ResourceLocation(modid, "create_integration_ender"), enderCapability);
        event.addListener(loaderInst::invalidate);
        event.addListener(enderInst::invalidate);
    }

    private void setup(final FMLCommonSetupEvent event) {
        setup.init();

        CapabilityManager.INSTANCE.register(IChunkLoaderList.class, new ChunkLoaderList.Storage(), () -> new ChunkLoaderList(null));
        CapabilityManager.INSTANCE.register(EnderList.class, new EnderList.Storage(), EnderList::new);

        logger.info("Setup method registered.");
    }

    private void clientRegistries(final FMLClientSetupEvent event) {
        logger.info("Client method registered.");
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onTick(TickEvent.WorldTickEvent event) {
        if (event.world != null && event.world.getGameTime() % 20 == 0) {
            //event.world.getCapability(CreateIntegration.CHUNK_LOADING_CAPABILITY, null).ifPresent(IChunkLoaderList::tickDown);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onServerStarted(FMLServerStartedEvent event) {
        event.getServer().getWorlds().forEach(world -> world.getCapability(CreateIntegration.CHUNK_LOADING_CAPABILITY, null).ifPresent(IChunkLoaderList::start));

    }


    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        @SuppressWarnings("unused")
        public static void registerItems(final RegistryEvent.Register<Item> event) {
            logger.info("items registering");
            Item.Properties properties = new Item.Properties().group(ModSetup.itemGroup);

            event.getRegistry().register(new BlockItem(ModBlocks.DYNAMO, properties).setRegistryName("dynamo"));
            event.getRegistry().register(new BlockItem(ModBlocks.MOTOR, properties).setRegistryName("motor"));
            event.getRegistry().register(new BlockItem(ModBlocks.ROLLING_MACHINE, properties).setRegistryName("rolling_machine"));
            event.getRegistry().register(new BlockItem(ModBlocks.BRASS_PRESSURE_PLATE, properties).setRegistryName("brass_pressure_plate"));
            event.getRegistry().register(new BlockItem(ModBlocks.COPPER_PRESSURE_PLATE, properties).setRegistryName("copper_pressure_plate"));
            event.getRegistry().register(new BlockItem(ModBlocks.ZINC_PRESSURE_PLATE, properties).setRegistryName("zinc_pressure_plate"));
            event.getRegistry().register(new BlockItem(ModBlocks.CHUNK_LOADER, properties).setRegistryName("chunk_loader"));
            event.getRegistry().register(new BlockItem(ModBlocks.ENDER_CRATE, properties).setRegistryName("ender_crate"));
            logger.info("finished items registering");
        }

        @SubscribeEvent
        @SuppressWarnings("unused")
        public static void registerBlocks(final RegistryEvent.Register<Block> event) {


            logger.info("blocks registering");
            event.getRegistry().register(new Dynamo());
            event.getRegistry().register(new Motor());
            event.getRegistry().register(new RollingMachine());
            event.getRegistry().register(new BrassPressurePlate());
            event.getRegistry().register(new CopperPressurePlate());
            event.getRegistry().register(new ZincPressurePlate());
            event.getRegistry().register(new ChunkLoader());
            event.getRegistry().register(new EnderCrate());
            logger.info("finished blocks registering");

        }


        @SubscribeEvent
        @SuppressWarnings("unused")
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
            logger.info("TEs registering");
            event.getRegistry().register(TileEntityType.Builder.create(DynamoTile::new, ModBlocks.DYNAMO).build(null).setRegistryName("dynamo"));
            event.getRegistry().register(TileEntityType.Builder.create(MotorTile::new, ModBlocks.MOTOR).build(null).setRegistryName("motor"));
            event.getRegistry().register(TileEntityType.Builder.create(RollingMachineTile::new, ModBlocks.ROLLING_MACHINE).build(null).setRegistryName("rolling_machine"));
            event.getRegistry().register(TileEntityType.Builder.create(ChunkLoaderTile::new, ModBlocks.CHUNK_LOADER).build(null).setRegistryName("chunk_loader"));
            event.getRegistry().register(TileEntityType.Builder.create(EnderCrateTile::new, ModBlocks.ENDER_CRATE).build(null).setRegistryName("ender_crate"));
            logger.info("finished TEs registering");
        }


        @SubscribeEvent
        @SuppressWarnings("unused")
        @OnlyIn(Dist.CLIENT)
        public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> event) {
            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new EnderContainer(windowId, Minecraft.getInstance().world, pos, inv);
            }).setRegistryName("ender_crate"));
        }
    }
}
