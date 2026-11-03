package top.prefersmin.mirrordim.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import top.prefersmin.mirrordim.block.ChordPortalBlock;
import top.prefersmin.mirrordim.registry.PMBlocks;
import top.prefersmin.mirrordim.registry.PMDimensions;


@Mixin(EggItem.class)
public class EggItemMixin extends Item {

    public EggItemMixin(Properties pProperties) {
        super(pProperties);
    }

    // 使用鸡蛋右键幽匿块框架即可点亮传送门
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();

        if (player == null
                || (level.dimension() != PMDimensions.CHORD_LEVEL && level.dimension() != Level.OVERWORLD)) {
            return super.useOn(context);
        }

        // 先试点击面，再试上/下，兼顾从侧面点击框架的情况
        Direction[] directions = {context.getClickedFace(), Direction.UP, Direction.DOWN};
        for (Direction direction : directions) {
            BlockPos framePos = context.getClickedPos().relative(direction);
            if (((ChordPortalBlock) PMBlocks.CHORD_PORTAL.get()).trySpawnPortal(context, framePos)) {
                level.playSound(player, framePos, SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }

        return super.useOn(context);
    }
}
