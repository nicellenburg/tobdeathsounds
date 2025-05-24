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

	// All region IDs known from Entry, Normal, and Hard modes
	private static final Set<Integer> TOB_REGION_IDS = Set.of(

			13122, 13123, 13124, 13125,
			14642, 14643, 14644,
			45201, 55962, 55977,
			56016, 55263, 46062,
			56209, 55953, 44674, 44418, 46258, 46002,
			49107, 45291, 42789, 48207, 43635
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
		// Reset the play flag and cooldown after a few ticks
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
		if (!config.enabled() || hasPlayed || tickCooldown > 0 || !isInTheatreOfBlood())
		{
			return;
		}

		if (event.getActor() instanceof Player)
		{
			playSound(config.soundChoice().getFilename());
			hasPlayed = true;
			tickCooldown = 2; // Delay further sounds for 2 ticks (~1.2s)
		}
	}

	private boolean isInTheatreOfBlood()
	{
		int region = client.getLocalPlayer().getWorldLocation().getRegionID();
		return TOB_REGION_IDS.contains(region);
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







