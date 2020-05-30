package com.grimmauld.createintegration;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.grimmauld.createintegration.blocks.BrassPressurePlate;
import com.grimmauld.createintegration.blocks.ChunkLoader;
import com.grimmauld.createintegration.blocks.ChunkLoaderTile;
import com.grimmauld.createintegration.blocks.CopperPressurePlate;
import com.grimmauld.createintegration.blocks.Dynamo;
import com.grimmauld.createintegration.blocks.DynamoTile;
import com.grimmauld.createintegration.blocks.EnderChest;
import com.grimmauld.createintegration.blocks.EnderChestTile;
import com.grimmauld.createintegration.blocks.EnderContainer;
import com.grimmauld.createintegration.blocks.ModBlocks;
import com.grimmauld.createintegration.blocks.Motor;
import com.grimmauld.createintegration.blocks.MotorTile;
import com.grimmauld.createintegration.blocks.RollingMachine;
import com.grimmauld.createintegration.blocks.RollingMachineTile;
import com.grimmauld.createintegration.blocks.RollingMachineTileEntityRenderer;
import com.grimmauld.createintegration.blocks.ZincPressurePlate;
import com.grimmauld.createintegration.misc.ChunkLoaderList;
import com.grimmauld.createintegration.misc.EnderList;
import com.grimmauld.createintegration.misc.IChunkLoaderList;
import com.grimmauld.createintegration.recipes.RecipeTypeRolling;
import com.grimmauld.createintegration.recipes.RollingRecipe;
import com.grimmauld.createintegration.setup.ClientProxy;
import com.grimmauld.createintegration.setup.IProxy;
import com.grimmauld.createintegration.setup.ModSetup;
import com.grimmauld.createintegration.setup.ServerProxy;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
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
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(CreateIntegration.modid)
public class CreateIntegration {
	
	public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
	
	public static ModSetup setup = new ModSetup();
	
	public static CreateIntegration instance;
	public static final String modid = "createintegration";
	public static final Logger logger = LogManager.getLogger(modid);
	
	public static final IRecipeType<RollingRecipe> ROLLING_RECIPE = new RecipeTypeRolling();
	
	@CapabilityInject(IChunkLoaderList.class)
    public static Capability<IChunkLoaderList> CHUNK_LOADING_CAPABILITY = null;

	@CapabilityInject(EnderList.class)
	public static Capability<EnderList> ENDER_CHEST_CAPABILITY = null;
	
	
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
		// MinecraftForge.EVENT_BUS.addListener(this::onTick);
		
		
		FMLJavaModLoadingContext.get().getModEventBus().addListener(CreateIntegration::clientInit);
	}
	
	
	
    private void registerRecipeSerializers (Register<IRecipeSerializer<?>> event) {
        
        // Vanilla has a registry for recipe types, but it does not actively use this registry.
        // While this makes registering your recipe type an optional step, I recommend
        // registering it anyway to allow other mods to discover your custom recipe types.
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(ROLLING_RECIPE.toString()), ROLLING_RECIPE);
        
        // Register the recipe serializer. This handles from json, from packet, and to packet.
        event.getRegistry().register(RollingRecipe.SERIALIZER);
    }
    

	/**
	 * This method lets you get all of the recipe data for a given recipe type. The existing
	 * methods for this require an IInventory, and this allows you to skip that overhead. This
	 * method uses reflection to get the recipes map, but an access transformer would also
	 * work.
	 * 
	 * @param recipeType The type of recipe to grab.
	 * @param manager The recipe manager. This is generally taken from a World.
	 * @return A map containing all recipes for the passed recipe type. This map is immutable
	 *         and can not be modified.
	 */
	public static Map<ResourceLocation, IRecipe<?>> getRecipes (IRecipeType<?> recipeType, RecipeManager manager) {
	    
	    final Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> recipesMap = ObfuscationReflectionHelper.getPrivateValue(RecipeManager.class, manager, "field_199522_d");
	    return recipesMap.get(recipeType);
	}
	
	
    @SubscribeEvent
    public void attachWorldCaps(AttachCapabilitiesEvent<World> event) {
        if (event.getObject().isRemote) return;
        final LazyOptional<IChunkLoaderList> inst = LazyOptional.of(() -> new ChunkLoaderList((ServerWorld)event.getObject()));
        final ICapabilitySerializable<INBT> loadingCapability = new ICapabilitySerializable<INBT>() {
            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
                return CHUNK_LOADING_CAPABILITY.orEmpty(cap, inst);
            }

            @Override
            public INBT serializeNBT() {
                return CHUNK_LOADING_CAPABILITY.writeNBT(inst.orElse(null), null);
            }

            @Override
            public void deserializeNBT(INBT nbt) {
                CHUNK_LOADING_CAPABILITY.readNBT(inst.orElse(null), null, nbt);
            }
        };

		final LazyOptional<EnderList> enderInst = LazyOptional.of(EnderList::new);
		final ICapabilitySerializable<INBT> enderCapability = new ICapabilitySerializable<INBT>() {
			@Override
			public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
				return ENDER_CHEST_CAPABILITY.orEmpty(cap, enderInst);
			}

			@Override
			public INBT serializeNBT() {
				return ENDER_CHEST_CAPABILITY.writeNBT(enderInst.orElse(null), null);
			}

			@Override
			public void deserializeNBT(INBT nbt) {
				ENDER_CHEST_CAPABILITY.readNBT(enderInst.orElse(null), null, nbt);
			}
		};

		event.addCapability(new ResourceLocation(modid, "create_integration_loader"), loadingCapability);
		event.addCapability(new ResourceLocation(modid, "create_integration_ender"), enderCapability);
        event.addListener(inst::invalidate);
        event.addListener(enderInst::invalidate);
    }
    
    
	private void setup(final FMLCommonSetupEvent event) {
		setup.init();
		proxy.init();
		
		CapabilityManager.INSTANCE.register(IChunkLoaderList.class, new ChunkLoaderList.Storage(), () -> new ChunkLoaderList(null));
		CapabilityManager.INSTANCE.register(EnderList.class, new EnderList.Storage(), EnderList::new);

		logger.info("Setup method registered.");
	}
	
	private void clientRegistries(final FMLClientSetupEvent event) {
		logger.info("Client method registered.");
	}
	
	public static void clientInit(FMLClientSetupEvent event) {
		registerRenderers();
	}
	
	@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents{
		@SubscribeEvent
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
			event.getRegistry().register(new BlockItem(ModBlocks.ENDER_CHEST, properties).setRegistryName("ender_chest"));
			logger.info("finished items registering");
		}
		
		@SubscribeEvent
		public static void registerBlocks(final RegistryEvent.Register<Block> event) {
			logger.info("blocks registering");
			event.getRegistry().register(new Dynamo());
			event.getRegistry().register(new Motor());
			event.getRegistry().register(new RollingMachine());
			event.getRegistry().register(new BrassPressurePlate());
			event.getRegistry().register(new CopperPressurePlate());
			event.getRegistry().register(new ZincPressurePlate());
			event.getRegistry().register(new ChunkLoader());
			event.getRegistry().register(new EnderChest());
			logger.info("finished blocks registering");
			
		}
		
		@SubscribeEvent
		public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
			logger.info("TEs registering");
			event.getRegistry().register(TileEntityType.Builder.create(DynamoTile::new, ModBlocks.DYNAMO).build(null).setRegistryName("dynamo"));
			event.getRegistry().register(TileEntityType.Builder.create(MotorTile::new, ModBlocks.MOTOR).build(null).setRegistryName("motor"));
			event.getRegistry().register(TileEntityType.Builder.create(RollingMachineTile::new, ModBlocks.ROLLING_MACHINE).build(null).setRegistryName("rolling_machine"));
			event.getRegistry().register(TileEntityType.Builder.create(ChunkLoaderTile::new, ModBlocks.CHUNK_LOADER).build(null).setRegistryName("chunk_loader"));
			event.getRegistry().register(TileEntityType.Builder.create(EnderChestTile::new, ModBlocks.ENDER_CHEST).build(null).setRegistryName("ender_chest"));
			logger.info("finished TEs registering");
		}
		
		
        @SubscribeEvent
        public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> event) {
            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new EnderContainer(windowId, CreateIntegration.proxy.getClientWorld(), pos, inv);
            }).setRegistryName("ender_chest"));
        }
		
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void registerRenderers() {
		bind(RollingMachineTile.class, new RollingMachineTileEntityRenderer());
	}
	
	@OnlyIn(Dist.CLIENT)
	private static <T extends TileEntity> void bind(Class<T> clazz, TileEntityRenderer<? super T> renderer) {
		ClientRegistry.bindTileEntitySpecialRenderer(clazz, renderer);
	}
	
	@SubscribeEvent
	public void onTick(TickEvent.WorldTickEvent event) {  // FIXME
		if(event.world != null && event.world.getGameTime() % 20 == 0) {
			event.world.getCapability(CreateIntegration.CHUNK_LOADING_CAPABILITY, null).ifPresent(cap -> cap.tickDown());
		}
	}

	public static ResourceLocation generateResourceLocation(String resourceName){
		return new ResourceLocation(modid, resourceName);
	}
	
	
}
