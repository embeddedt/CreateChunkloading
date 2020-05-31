package com.grimmauld.createintegration.setup;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public interface IProxy {

    void init();

    World getClientWorld();

    @SuppressWarnings("unused")
    PlayerEntity getClientPlayer();
}
