package top.prefersmin.mirrordim.mixin;

import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * 访问 Entity 中原本为 protected / private 的传送门相关成员。
 * 使用 Mixin accessor 代替 Access Transformer，避免 ModDevGradle 重新编译 Minecraft
 * 源码时因「父类方法被放宽、子类重写仍为 protected」而编译失败。
 */
@Mixin(Entity.class)
public interface EntityAccessor {

    @Accessor("portalEntrancePos")
    BlockPos getPortalEntrancePos();

    @Accessor("portalEntrancePos")
    void setPortalEntrancePos(BlockPos pos);

    @Invoker("getRelativePortalPosition")
    Vec3 callGetRelativePortalPosition(Direction.Axis axis, BlockUtil.FoundRectangle rectangle);

    @Invoker("findDimensionEntryPoint")
    PortalInfo callFindDimensionEntryPoint(ServerLevel destination);
}
