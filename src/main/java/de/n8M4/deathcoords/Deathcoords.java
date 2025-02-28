package de.n8M4.deathcoords;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;


import static net.minecraft.commands.Commands.literal;

@Mod(Deathcoords.MODID)
public class Deathcoords {
    public static final String MODID = "deathcoords";

    private static String deathMessage = "";
    private static boolean displayMessage = false;

    public Deathcoords() {
        MinecraftForge.EVENT_BUS.register(new HudOverlayRenderer());
        MinecraftForge.EVENT_BUS.register(new DeathEventHandler());
        MinecraftForge.EVENT_BUS.addListener(this::onClientTick);
    }

    private static boolean wasDead = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player != null) {
            boolean isDead = player.getHealth() <= 0;

            if (isDead && !wasDead) {
                player.displayClientMessage(Component.literal("§aYou died at " + DeathEventHandler.getStringFromCoords(player)), false);
            }

            wasDead = isDead;
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class HudOverlayRenderer {
        @SubscribeEvent
        @OnlyIn(Dist.CLIENT)
        public void onRenderGui(RenderGuiEvent.Post event) {
            if(!displayMessage) return;
            PoseStack poseStack = event.getGuiGraphics().pose();
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            int screenWidth = mc.getWindow().getGuiScaledWidth();
            int screenHeight = mc.getWindow().getGuiScaledHeight();
            int x = screenWidth / 2 - mc.font.width(deathMessage) / 2;
            int y = screenHeight - 55;

            MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
            poseStack.pushPose();
            poseStack.translate(0, 0, 0);
            Matrix4f matrix = poseStack.last().pose();
            mc.font.drawInBatch(
                    deathMessage, x, y, 0xFFFFFF, true, matrix,
                    bufferSource, Font.DisplayMode.NORMAL, 0, 15728880
            );
            bufferSource.endBatch();
            poseStack.popPose();
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class DeathEventHandler {

        @SubscribeEvent
        public void onPlayerDeath(LivingDeathEvent event) {
            System.out.println("deathysdfsydfsy");
            if(event.getEntity() instanceof Player player) player.sendSystemMessage(Component.literal("§aYou died at " + getStringFromCoords(player)));
        }

        public static String getStringFromCoords(Player player) {
            deathMessage = "§2[§6x: "
                    + (int)player.getX()
                    + "§2]§r, §2[§6y: " + (int)player.getY()
                    + "§2]§r, §2[§6z: " + (int)player.getZ()
                    + "§2]§a, in §6" + player.level().dimension().location();
            return deathMessage;
        }

        @SubscribeEvent
        public void registerCommands(RegisterClientCommandsEvent event){
            register(event.getDispatcher());
        }
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(literal("display_death")
                .executes(context -> {
                    displayMessage = !displayMessage;
                    return 1;
                })
        );
    }

}
