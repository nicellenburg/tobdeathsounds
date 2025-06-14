package com.tobdeathsounds;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.audio.AudioPlayer;

import java.io.InputStream;
import java.util.Objects;
import java.util.Set;

@Slf4j
@PluginDescriptor(
		name = "ToB Death Sound",
		description = "Plays a sound when a player dies in ToB",
		tags = {"tob", "death", "sound"}
)
public class ToBDeathSoundPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ToBDeathSoundConfig config;

	private boolean hasPlayed = false;
	private int tickCooldown = 0;

	private static final Set<String> TOB_NPCS = Set.of(
			"The Maiden of Sugadinti",
			"Pestilent Bloat",
			"Nylocas Vasilias",
			"Sotetseg",
			"Xarpus",
			"Verzik Vitur",
			"Nylocas Ischyros",
			"Nylocas Toxobolos",
			"Nylocas Hagios",
			"Nylocas Ischyros (hard mode)",
			"Nylocas Toxobolos (hard mode)",
			"Nylocas Hagios (hard mode)"
	);

	@Override
	protected void startUp()
	{
		hasPlayed = false;
		tickCooldown = 0;
	}

	@Override
	protected void shutDown()
	{
		hasPlayed = false;
		tickCooldown = 0;
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (tickCooldown > 0)
		{
			tickCooldown--;
		}
		else
		{
			hasPlayed = false;
		}
	}

	@Subscribe
	public void onActorDeath(ActorDeath event)
	{
		if (!config.enabled() || hasPlayed || tickCooldown > 0)
		{
			return;
		}

		if (event.getActor() instanceof Player && isInTheatreOfBloodRoom())
		{
			playSound(config.soundChoice().getFilename());
			hasPlayed = true;
			tickCooldown = 2;
		}
	}

	private boolean isInTheatreOfBloodRoom()
	{
		return client.getNpcs().stream()
				.map(NPC::getName)
				.filter(Objects::nonNull)
				.anyMatch(TOB_NPCS::contains);
	}

	private void playSound(String fileName)
	{
		try (InputStream soundStream = getClass().getResourceAsStream("/" + fileName))
		{
			if (soundStream == null)
			{
				log.warn("Sound file not found: {}", fileName);
				return;
			}
			AudioPlayer audioPlayer = new AudioPlayer();
			audioPlayer.play(soundStream, config.volume() / 100f);
		}
		catch (Exception e)
		{
			log.warn("Failed to play sound file: {}", fileName, e);
		}
	}

	@Provides
	ToBDeathSoundConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ToBDeathSoundConfig.class);
	}
}







