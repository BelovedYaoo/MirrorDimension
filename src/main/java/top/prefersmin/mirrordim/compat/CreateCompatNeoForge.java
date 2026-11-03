package top.prefersmin.mirrordim.compat;

import com.mojang.logging.LogUtils;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.slf4j.Logger;
import top.prefersmin.mirrordim.MirrorDimension;
import top.prefersmin.mirrordim.registry.PMDimensions;
import top.prefersmin.mirrordim.util.MinecraftCompat;
import top.prefersmin.mirrordim.world.NeoForgeChordPortalForcer;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;

/**
 * Create 6.0（NeoForge）列车跨维度兼容。
 * 使用反射接入 Create 的 PortalTrackProvider，避免不同 Create 6.0.x
 * 之间 catnip BlockFace 包名变化导致编译期断裂。
 */
public final class CreateCompatNeoForge {

    private static final Logger LOGGER = LogUtils.getLogger();

    private CreateCompatNeoForge() {
    }

    public static void register() {
        try {
            Class<?> providerType = Class.forName("com.simibubi.create.api.contraption.train.PortalTrackProvider");
            Object provider = Proxy.newProxyInstance(
                    CreateCompatNeoForge.class.getClassLoader(),
                    new Class<?>[]{providerType},
                    (proxy, method, args) -> {
                        if (method.getName().equals("findExit") && args != null && args.length == 2) {
                            return findExit((ServerLevel) args[0], args[1]);
                        }
                        return null;
                    }
            );

            Class<?> allPortalTracks = Class.forName("com.simibubi.create.content.trains.track.AllPortalTracks");
            Method register = allPortalTracks.getMethod("tryRegisterIntegration", ResourceLocation.class, providerType);
            register.invoke(null, MinecraftCompat.id(MirrorDimension.MODID, "chord_portal"), provider);
        } catch (ReflectiveOperationException e) {
            LOGGER.warn("Create 6.0 传送门兼容注册失败", e);
        }
    }

    private static Object findExit(ServerLevel level, Object inboundTrack) throws ReflectiveOperationException {
        BlockPos portalPos = (BlockPos) inboundTrack.getClass().getMethod("getConnectedPos").invoke(inboundTrack);
        Direction inboundFace = (Direction) inboundTrack.getClass().getMethod("getFace").invoke(inboundTrack);

        ResourceKey<Level> targetDimension = level.dimension() == PMDimensions.CHORD_LEVEL
                ? Level.OVERWORLD : PMDimensions.CHORD_LEVEL;
        ServerLevel otherLevel = level.getServer().getLevel(targetDimension);
        if (otherLevel == null) {
            return null;
        }

        BlockState portalState = level.getBlockState(portalPos);
        Optional<BlockUtil.FoundRectangle> rectangle =
                new NeoForgeChordPortalForcer(otherLevel, portalState, portalPos).findOrCreateExit(portalPos);
        if (rectangle.isEmpty()) {
            return null;
        }

        BlockPos otherPortalPos = rectangle.get().minCorner;
        BlockState otherPortalState = otherLevel.getBlockState(otherPortalPos);
        if (otherPortalState.getBlock() != portalState.getBlock()) {
            return null;
        }

        Direction targetDirection = inboundFace;
        if (targetDirection.getAxis() == otherPortalState.getValue(BlockStateProperties.HORIZONTAL_AXIS)) {
            targetDirection = targetDirection.getClockWise();
        }
        BlockPos otherPos = otherPortalPos.relative(targetDirection);

        Class<?> blockFaceType = Class.forName("net.createmod.catnip.math.BlockFace");
        Object otherFace = blockFaceType.getConstructor(BlockPos.class, Direction.class)
                .newInstance(otherPos, targetDirection.getOpposite());
        Class<?> exitType = Class.forName("com.simibubi.create.api.contraption.train.PortalTrackProvider$Exit");
        return exitType.getConstructor(ServerLevel.class, blockFaceType).newInstance(otherLevel, otherFace);
    }
}
