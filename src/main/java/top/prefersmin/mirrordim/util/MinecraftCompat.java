package top.prefersmin.mirrordim.util;

import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.Vec3;

/**
 * 1.19.2 / 1.20.1 之间少量 API 名称差异的兼容层。
 * Stonecutter 会在生成各变体源码时裁掉不属于该版本的分支。
 */
public final class MinecraftCompat {

    private MinecraftCompat() {
    }

    public static BlockBehaviour.Properties blockProperties() {
        //? if >=1.20 {
        return BlockBehaviour.Properties.of();
        //?} else {
        return BlockBehaviour.Properties.of(net.minecraft.world.level.material.Material.PORTAL);
        //?}
    }

    public static BlockBehaviour.Properties portalProperties() {
        BlockBehaviour.Properties properties = blockProperties()
                .strength(-1F)
                .noCollission()
                .lightLevel(state -> 10)
                .noLootTable();
        //? if >=1.20 {
        properties.pushReaction(PushReaction.BLOCK);
        //?}
        return properties;
    }

    public static Level level(Entity entity) {
        //? if >=1.20 {
        return entity.level();
        //?} else {
        return entity.getLevel();
        //?}
    }

    public static boolean isSolid(BlockState state) {
        //? if >=1.20 {
        return state.isSolid();
        //?} else {
        return state.getMaterial().isSolid();
        //?}
    }

    public static BlockPos blockPos(Vec3 pos) {
        return new BlockPos(Mth.floor(pos.x), Mth.floor(pos.y), Mth.floor(pos.z));
    }

    public static PortalInfo createPortalInfo(ServerLevel level, BlockUtil.FoundRectangle rectangle,
                                              Direction.Axis axis, Vec3 offset, Entity entity,
                                              Vec3 velocity, float yaw, float pitch) {
        //? if >=1.20 {
        return PortalShape.createPortalInfo(level, rectangle, axis, offset, entity, velocity, yaw, pitch);
        //?} else {
        return PortalShape.createPortalInfo(level, rectangle, axis, offset,
                entity.getDimensions(entity.getPose()), velocity, yaw, pitch);
        //?}
    }
}
