package top.prefersmin.mirrordim.event;

import net.minecraft.server.level.ServerLevel;

//? if forge {
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
//?}
//? if neoforge {
/*import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
*///?}

import top.prefersmin.mirrordim.block.ChordPortalBlock;
import top.prefersmin.mirrordim.registry.PMDimensions;

public final class PMEvents {

    private PMEvents() {
    }

    /**
     * 弦世界内只允许生成弦世界传送门，阻止下界门等其他传送门被激活。
     */
    @SubscribeEvent
    public static void onPortalSpawn(BlockEvent.PortalSpawnEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)
                || level.dimension() != PMDimensions.CHORD_LEVEL) {
            return;
        }

        // 自己的传送门使用 PMPortalShape，需要放行
        if (!(event.getPortalSize() instanceof ChordPortalBlock.PMPortalShape)) {
            event.setCanceled(true);
        }
    }

    /**
     * 弦世界可以睡觉设置重生点，但不允许通过睡觉跳过黑夜。
     */
    @SubscribeEvent
    public static void onSleepFinished(SleepFinishedTimeEvent event) {
        if (event.getLevel() instanceof ServerLevel level
                && level.dimension() == PMDimensions.CHORD_LEVEL) {
            // 把“醒来时间”设置为当前时间，等于不推进时间
            event.setTimeAddition(level.getDayTime());
        }
    }
}
