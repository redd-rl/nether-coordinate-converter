package net.reddrl.nethercoordinateconverter;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dev.xpple.clientarguments.arguments.CBlockPosArgumentType;

public class nethercoordinateconverter implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");
	KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.nethercoordinateconverter.translate",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_G,
			"category.nethercoordinateconverter.translate"
		));
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("World's worst mod initialized!");
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (keyBinding.wasPressed()) {
				RegistryKey<World> playerDim = client.player.getWorld().getRegistryKey();

				if (playerDim == World.NETHER) {
					Vec3d playerPos = client.player.getPos();
					int playerX = (int)Math.floor(playerPos.x);
				    int playerY = (int)Math.floor(playerPos.y);
				    int playerZ = (int)Math.floor(playerPos.z);
					int nPlayerX = playerX * 8;
					int nPlayerY = playerY;
					int nPlayerZ = playerZ * 8;
					String message = "Overworld location of " + String.valueOf(playerX) + ", " + String.valueOf(playerY) + ", " + String.valueOf(playerZ) + " is " +  String.valueOf(nPlayerX) + ", " + String.valueOf(nPlayerY) + ", " + String.valueOf(nPlayerZ);
					client.player.sendMessage(Text.literal(message), false);

				} else if (playerDim == World.OVERWORLD) {
					Vec3d playerPos = client.player.getPos();
					int playerX = (int)Math.floor((float)playerPos.x);
				    int playerY = (int)Math.floor((float)playerPos.y);
				    int playerZ = (int)Math.floor((float)playerPos.z);
					int nPlayerX = (int)Math.floor(playerX / 8);
					int nPlayerY = playerY;
					int nPlayerZ = (int)Math.floor(playerZ / 8);
					String message = "Nether location of " + String.valueOf(playerX) + ", " + String.valueOf(playerY) + ", " + String.valueOf(playerZ) + " is " +  String.valueOf(nPlayerX) + ", " + String.valueOf(nPlayerY) + ", " + String.valueOf(nPlayerZ);
					client.player.sendMessage(Text.literal(message), false);
				} else if (playerDim == World.END) {
					client.player.sendMessage(Text.literal("Can't build nether portals in this dimension dummy!"), false);
				} else {
					client.player.sendMessage(Text.literal("The dimension you are in is not supported!"), false);
				}
			}
		});
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(ClientCommandManager.literal("ncc")
		     .then(ClientCommandManager.argument("world", DimensionArgumentType.dimension())
			     	 .then(ClientCommandManager.argument("xyz", CBlockPosArgumentType.blockPos())
							.executes(context -> {
								// Get the world the player chose
								Identifier world = context.getArgument("world", Identifier.class);
								// Get the x y z arguments the player chose
								BlockPos xyz = CBlockPosArgumentType.getCBlockPos(context, "xyz");
								int x = xyz.getX();
								int y = xyz.getY();
								int z = xyz.getZ();
								if (world.equals(Identifier.of("minecraft", "the_nether"))) {
									int Ox = (int)Math.floor(x * 8);
									int Oy = y;
									int Oz = (int)Math.floor(z * 8);
									String message = "Overworld location of " + String.valueOf(x) + ", " + String.valueOf(y) + ", " + String.valueOf(z) + " is " +  String.valueOf(Ox) + ", " + String.valueOf(Oy) + ", " + String.valueOf(Oz);
								    context.getSource().sendFeedback(Text.literal(message));
								} else if (world.equals(Identifier.of("minecraft", "overworld"))) {
									int Ox = (int)Math.floor(x / 8);
									int Oy = y;
									int Oz = (int)Math.floor(z / 8);
									String message = "Nether location of " + String.valueOf(x) + ", " + String.valueOf(y) + ", " + String.valueOf(z) + " is " +  String.valueOf(Ox) + ", " + String.valueOf(Oy) + ", " + String.valueOf(Oz);
								    context.getSource().sendFeedback(Text.literal(message));
								} else {
									context.getSource().sendFeedback(Text.literal("Unsupported dimension specified in command."));
								}
								return z;	
						}))));
				});
	}
}
