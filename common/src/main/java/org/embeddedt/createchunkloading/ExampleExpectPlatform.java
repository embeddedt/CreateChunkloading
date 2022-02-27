package org.embeddedt.createchunkloading;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.platform.Platform;
import net.minecraft.server.level.ServerLevel;

import java.nio.file.Path;

public class ExampleExpectPlatform {
    /**
     * We can use {@link Platform#getConfigFolder()} but this is just an example of {@link ExpectPlatform}.
     * <p>
     * This must be a <b>public static</b> method. The platform-implemented solution must be placed under a
     * platform sub-package, with its class suffixed with {@code Impl}.
     * <p>
     * Example:
     * Expect: net.createchunkloading.ExampleExpectPlatform#getConfigDirectory()
     * Actual Fabric: net.createchunkloading.fabric.ExampleExpectPlatformImpl#getConfigDirectory()
     * Actual Forge: net.createchunkloading.forge.ExampleExpectPlatformImpl#getConfigDirectory()
     * <p>
     * <a href="https://plugins.jetbrains.com/plugin/16210-architectury">You should also get the IntelliJ plugin to help with @ExpectPlatform.</a>
     */
    @ExpectPlatform
    public static Path getConfigDirectory() {
        // Just throw an error, the content should get replaced at runtime.
        throw new AssertionError();
    }
    @ExpectPlatform
    public static void forceLoadChunk(ServerLevel world, int chunkX, int chunkZ, boolean state, Object entityUUID, boolean shouldLoadSurroundingAsWell) {
        throw new AssertionError();
    }
}
