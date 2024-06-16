package org.samo_lego.fabrictailor.testmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.samo_lego.fabrictailor.client.screen.tabs.CapeTab;
import org.samo_lego.fabrictailor.client.screen.tabs.UrlSkinTab;
import org.samo_lego.fabrictailor.command.SkinCommand;
import org.samo_lego.fabrictailor.util.SkinFetcher;


public class TailorTest implements ModInitializer, ClientModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            this.serversideSkinCmd(dispatcher);
        });
    }


    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, environment) -> {
            this.hdSkinCmd(dispatcher);
            this.capeCmd(dispatcher);
        });
    }

    private void hdSkinCmd(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("hdskin").executes(this::hdSkinCmd));
    }

    private int hdSkinCmd(CommandContext<FabricClientCommandSource> context) {
        new UrlSkinTab().getSkinChangePacket(context.getSource().getPlayer(), "https://raw.githubusercontent.com/ClassicFaithful/32x-Jappa/1.20.4/assets/minecraft/textures/entity/player/wide/steve.png", false)
                .ifPresent(ClientPlayNetworking::send);
        return 0;
    }

    private void capeCmd(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("cape").executes(this::capeCmd));
    }

    private int capeCmd(CommandContext<FabricClientCommandSource> context) {
        LocalPlayer player = context.getSource().getPlayer();

        new CapeTab().getSkinChangePacket(player, "https://static.wikia.nocookie.net/minecraft_gamepedia/images/6/65/Millionth_Customer_Cape_%28texture%29.png", false)
                .ifPresent(ClientPlayNetworking::send);
        return 0;
    }

    private void serversideSkinCmd(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("serverside_skin")
                .then(Commands.literal("player").executes(ctx -> {
                    SkinCommand.setSkin(ctx.getSource().getPlayer(), () -> SkinFetcher.fetchSkinByName("Notch"));
                    return 1;
                }))
                .then(Commands.literal("url").executes(ctx -> {
                    SkinCommand.setSkin(ctx.getSource().getPlayer(), () -> SkinFetcher.fetchSkinByUrl("https://textures.minecraft.net/texture/2736a21f4f7ccbc791ca44527445b2bea3d5bedd11cff16bf1d4d08044d51fc6", false));
                    return 1;
                })));

    }
}
