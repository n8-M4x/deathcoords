package de.n8M4.deathcoords;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(Deathcoords.MODID)
public class Deathcoords {
    public static final String MODID = "deathcoords";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Deathcoords() {
        MinecraftForge.EVENT_BUS.register(new DeathEventHandler1());
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class DeathEventHandler1 {
        
        @SubscribeEvent
        public static void onPlayerDeath(LivingDeathEvent event) {
            if(event.getEntity() instanceof ServerPlayer player) player.sendSystemMessage(Component.literal("§aYou died at " + getStringFromCoords(player)));
        }

        public static String getStringFromCoords(ServerPlayer player) {
            return "§2[§6x: "
                    + (int)player.getX()
                    + "§2]§r, §2[§6y: " + (int)player.getY()
                    + "§2]§r, §2[§6z: " + (int)player.getZ()
                    + "§2]§a, in §6" + player.level().dimension().location();
        }
    }
}
