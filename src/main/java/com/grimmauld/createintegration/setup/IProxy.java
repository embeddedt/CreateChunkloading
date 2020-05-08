package com.grimmauld.createintegration.setup;

import net.minecraft.world.World;

public interface IProxy {
	
	void init();
	
	World getClientWorld();
}
