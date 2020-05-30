package com.grimmauld.createintegration.setup;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ServerProxy implements IProxy{
	@Override
	public World getClientWorld() {
		throw new IllegalStateException("Only run this on the Client!");
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PlayerEntity getClientPlayer() {
		throw new IllegalStateException("Only run this on the Client!");
	}
}
