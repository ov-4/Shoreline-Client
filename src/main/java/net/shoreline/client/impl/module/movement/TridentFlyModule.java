package net.shoreline.client.impl.module.movement;

import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.EventStage;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.event.item.TridentPullbackEvent;
import net.shoreline.client.impl.event.item.TridentWaterEvent;
import net.shoreline.client.init.Managers;

/**
 * @author Shoreline
 * @since 1.0
 */
public class TridentFlyModule extends ToggleModule {

    Config<Boolean> allowNoWaterConfig = new BooleanConfig("AllowNoWater", "Allows you to fly using tridents even without water", true);
    Config<Boolean> flyConfig = new BooleanConfig("AutoFly", "Automatically uses riptide", false);
    Config<Integer> ticksConfig = new NumberConfig<>("Ticks", "The ticks between riptide boost", 0, 3, 20, () -> flyConfig.getValue());
    Config<Boolean> instantConfig = new BooleanConfig("Instant", "Removes the pullback of the trident", true);

    public TridentFlyModule() {
        super("TridentFly", "Allows you to fly using tridents", ModuleCategory.MOVEMENT);
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (event.getStage() == EventStage.PRE || !flyConfig.getValue()) {
            return;
        }
        if (mc.player.getMainHandStack().getItem() == Items.TRIDENT && mc.player.getItemUseTime() >= ticksConfig.getValue()) {
            Managers.NETWORK.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
            mc.player.stopUsingItem();
        }
    }

    @EventListener
    public void onTridentPullback(TridentPullbackEvent event) {
        if (instantConfig.getValue()) {
            event.cancel();
        }
    }

    @EventListener
    public void onTridentWaterCheck(TridentWaterEvent event) {
        if (allowNoWaterConfig.getValue()) {
            event.cancel();
        }
    }
}
