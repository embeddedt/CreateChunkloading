package com.grimmauld.createintegration.blocks;

import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class ModBlocks {

    @ObjectHolder("createintegration:dynamo")
    public static Dynamo DYNAMO;

    @ObjectHolder("createintegration:dynamo")
    public static TileEntityType<DynamoTile> DYNAMO_TILE;

    @ObjectHolder("createintegration:motor")
    public static Motor MOTOR;

    @ObjectHolder("createintegration:motor")
    public static TileEntityType<MotorTile> MOTOR_TILE;

    @ObjectHolder("createintegration:rolling_machine")
    public static RollingMachine ROLLING_MACHINE;

    @ObjectHolder("createintegration:rolling_machine")
    public static TileEntityType<RollingMachineTile> ROLLING_MACHINE_TILE;

    @ObjectHolder("createintegration:brass_pressure_plate")
    public static BrassPressurePlate BRASS_PRESSURE_PLATE;

    @ObjectHolder("createintegration:zinc_pressure_plate")
    public static ZincPressurePlate ZINC_PRESSURE_PLATE;

    @ObjectHolder("createintegration:copper_pressure_plate")
    public static CopperPressurePlate COPPER_PRESSURE_PLATE;

    @ObjectHolder("createintegration:chunk_loader")
    public static ChunkLoader CHUNK_LOADER;

    @ObjectHolder("createintegration:chunk_loader")
    public static TileEntityType<ChunkLoaderTile> CHUNK_LOADER_TILE;

    @ObjectHolder("createintegration:ender_chest")
    public static EnderChest ENDER_CHEST;

    @ObjectHolder("createintegration:ender_chest")
    public static TileEntityType<EnderChestTile> ENDER_CHEST_TILE;

    @ObjectHolder("createintegration:ender_chest")
    public static ContainerType<EnderContainer> ENDER_CONTAINER;
}
