package top.prefersmin.mirrordim.world;

import com.mojang.logging.LogUtils;
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import com.simibubi.create.foundation.utility.BlockFace;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.ITeleporter;
import org.slf4j.Logger;
import top.prefersmin.mirrordim.registry.PMDimensions;
import top.prefersmin.mirrordim.util.MinecraftCompat;

public class PortalTracksSupport {

    private static final Logger LOGGER = LogUtils.getLogger();

    @FunctionalInterface
    public interface PortalForcerFactory {
        ITeleporter create(ServerLevel level, BlockState state, BlockPos pos);
    }

    public static Pair<ServerLevel, BlockFace> mirrordim(Pair<ServerLevel, BlockFace> inbound) {

        return standardPortalProvider(inbound, Level.OVERWORLD, PMDimensions.CHORD_LEVEL, (level, state, pos) -> {
            try {
                return new ChordPortalForcer(level, state, pos);
            } catch (Exception e) {
                LOGGER.error("创建弦世界传送门 forcer 失败", e);
            }
            return level.getPortalForcer();
        });

    }

    public static Pair<ServerLevel, BlockFace> standardPortalProvider(Pair<ServerLevel, BlockFace> inbound,
                                                                      ResourceKey<Level> firstDimension, ResourceKey<Level> secondDimension,
                                                                      PortalForcerFactory customPortalForcer) {
        ServerLevel level = inbound.getFirst();
        ResourceKey<Level> resourcekey = level.dimension() == secondDimension ? firstDimension : secondDimension;
        MinecraftServer minecraftserver = level.getServer();
        ServerLevel otherLevel = minecraftserver.getLevel(resourcekey);

        if (otherLevel == null)
            return null;

        BlockFace inboundTrack = inbound.getSecond();
        BlockPos portalPos = inboundTrack.getConnectedPos();
        BlockState portalState = level.getBlockState(portalPos);
        ITeleporter teleporter = customPortalForcer.create(otherLevel, portalState, portalPos);

        SuperGlueEntity probe = new SuperGlueEntity(level, new AABB(portalPos));
        probe.setYRot(inboundTrack.getFace()
                .toYRot());
        // ChordPortalForcer 自行读取 sourceState/sourcePos，不需要默认的 findDimensionEntryPoint
        PortalInfo portalinfo = teleporter.getPortalInfo(probe, otherLevel, destination -> null);
        if (portalinfo == null)
            return null;

        BlockPos otherPortalPos = MinecraftCompat.blockPos(portalinfo.pos);
        BlockState otherPortalState = otherLevel.getBlockState(otherPortalPos);
        if (otherPortalState.getBlock() != portalState.getBlock())
            return null;

        Direction targetDirection = inboundTrack.getFace();
        if (targetDirection.getAxis() == otherPortalState.getValue(BlockStateProperties.HORIZONTAL_AXIS))
            targetDirection = targetDirection.getClockWise();
        BlockPos otherPos = otherPortalPos.relative(targetDirection);
        return Pair.of(otherLevel, new BlockFace(otherPos, targetDirection.getOpposite()));

    }

}