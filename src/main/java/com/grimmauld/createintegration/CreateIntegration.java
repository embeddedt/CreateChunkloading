package com.grimmauld.createintegration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.grimmauld.createintegration.blocks.Dynamo;
import com.grimmauld.createintegration.blocks.DynamoTile;
import com.grimmauld.createintegration.blocks.ModBlocks;
import com.grimmauld.createintegration.setup.ClientProxy;
import com.grimmauld.createintegration.setup.IProxy;
import com.grimmauld.createintegration.setup.ModSetup;
import com.grimmauld.createintegration.setup.ServerProxy;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod("createintegration")
public class CreateIntegration {
	
	public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
	
	public static ModSetup setup = new ModSetup();
	
	public static CreateIntegration instance;
	public static final String modid = "createintegration";
	private static final Logger logger = LogManager.getLogger(modid);
	
	
	public CreateIntegration() {
		instance = this;
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
		
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientRegistries);
		
		Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve("createintegration-client.toml"));
		Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("createintegration-common.toml"));
		
		MinecraftForge.EVENT_BUS.register(this);
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
			Item.Properties properties = new Item.Properties().group(setup.itemGroup);
			
			event.getRegistry().register(new BlockItem(ModBlocks.DYNAMO, properties).setRegistryName("dynamo"));
		}
		
		@SubscribeEvent
		public static void registerBlocks(final RegistryEvent.Register<Block> event) {
			logger.info("blocks registering");
			event.getRegistry().register(new Dynamo());
			
		}
		
		@SubscribeEvent
		public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
			event.getRegistry().register(TileEntityType.Builder.create(DynamoTile::new, ModBlocks.DYNAMO).build(null).setRegistryName("dynamo"));
		}
		
	}
}
