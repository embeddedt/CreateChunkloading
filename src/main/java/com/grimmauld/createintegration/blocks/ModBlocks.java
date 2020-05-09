package com.grimmauld.createintegration.blocks;

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
}
