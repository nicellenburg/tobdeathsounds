package com.tobdeathsounds;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.audio.AudioPlayer;

import java.io.InputStream;

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

	@Override
	protected void startUp()
	{
		hasPlayed = false;
	}

	@Override
	protected void shutDown()
	{
		hasPlayed = false;
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		hasPlayed = false;
	}

	@Subscribe
	public void onActorDeath(ActorDeath event)
	{
		if (!config.enabled() || hasPlayed || !isInTheatreOfBlood())
			return;

		if (event.getActor() instanceof Player)
		{
			playSound(config.soundChoice());
			hasPlayed = true;
		}
	}

	private boolean isInTheatreOfBlood()
	{
		int region = client.getLocalPlayer().getWorldLocation().getRegionID();
		return region == 14642 || region == 14643 || region == 14644 ||
			   region == 13122 || region == 13123 || region == 13124 || region == 13125;
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






