package com.grimmauld.createintegration;

import java.util.Map;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.grimmauld.createintegration.blocks.Dynamo;
import com.grimmauld.createintegration.blocks.DynamoTile;
import com.grimmauld.createintegration.blocks.ModBlocks;
import com.grimmauld.createintegration.blocks.Motor;
import com.grimmauld.createintegration.blocks.MotorTile;
import com.grimmauld.createintegration.blocks.RollingMachine;
import com.grimmauld.createintegration.blocks.RollingMachineTile;
import com.grimmauld.createintegration.recipes.RecipeTypeRolling;
import com.grimmauld.createintegration.recipes.RollingRecipe;
import com.grimmauld.createintegration.setup.ClientProxy;
import com.grimmauld.createintegration.setup.IProxy;
import com.grimmauld.createintegration.setup.ModSetup;
import com.grimmauld.createintegration.setup.ServerProxy;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

@Mod("createintegration")
public class CreateIntegration {
	
	public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
	
	public static ModSetup setup = new ModSetup();
	
	public static CreateIntegration instance;
	public static final String modid = "createintegration";
	private static final Logger logger = LogManager.getLogger(modid);
	
	public static final IRecipeType<RollingRecipe> ROLLING_RECIPE = new RecipeTypeRolling();
	
	
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
    
    
	private void setup(final FMLCommonSetupEvent event) {
		setup.init();
		proxy.init();
		logger.info("Setup method registered.");
	}
	
	private void clientRegistries(final FMLClientSetupEvent event) {
		logger.info("Client method registered.");
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
			logger.info("finished items registering");
		}
		
		@SubscribeEvent
		public static void registerBlocks(final RegistryEvent.Register<Block> event) {
			logger.info("blocks registering");
			event.getRegistry().register(new Dynamo());
			event.getRegistry().register(new Motor());
			event.getRegistry().register(new RollingMachine());
			logger.info("finished blocks registering");
			
		}
		
		@SubscribeEvent
		public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
			logger.info("TEs registering");
			event.getRegistry().register(TileEntityType.Builder.create(DynamoTile::new, ModBlocks.DYNAMO).build(null).setRegistryName("dynamo"));
			event.getRegistry().register(TileEntityType.Builder.create(MotorTile::new, ModBlocks.MOTOR).build(null).setRegistryName("motor"));
			event.getRegistry().register(TileEntityType.Builder.create(RollingMachineTile::new, ModBlocks.ROLLING_MACHINE).build(null).setRegistryName("rolling_machine"));
			logger.info("finished TEs registering");
		}
		
	}
}
