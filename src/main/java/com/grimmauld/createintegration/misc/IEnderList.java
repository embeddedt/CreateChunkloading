package com.grimmauld.createintegration.misc;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public interface IEnderList {
    LazyOptional<IItemHandler> getOrCreate(int id);
}
