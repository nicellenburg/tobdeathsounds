package com.tobdeathsounds;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("tobdeathsound")
public interface ToBDeathSoundConfig extends Config
{
    @ConfigItem(
        keyName = "enabled",
        name = "Enable Plugin",
        description = "Enable or disable the ToB Death Sound plugin"
    )
    default boolean enabled()
    {
        return true;
    }

    @Range(
        min = 0,
        max = 100
    )
    @ConfigItem(
        keyName = "volume",
        name = "Sound Volume",
        description = "Adjust the sound volume",
        position = 1
    )
    default int volume()
    {
        return 100;
    }

    @ConfigItem(
        keyName = "soundChoice",
        name = "Sound Effect",
        description = "Choose the sound effect to play",
        position = 2
    )
    default String soundChoice()
    {
        return "sports.wav";
    }
}

