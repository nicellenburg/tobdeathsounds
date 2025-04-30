package com.tobdeathsounds;

import net.runelite.client.externalplugins.ExternalPluginManager;
import org.junit.Test;

public class ToBDeathSoundPluginTest
{
    @Test
    public void testPluginStartup()
    {
        ExternalPluginManager.loadBuiltin(ToBDeathSoundPlugin.class);
    }
}
