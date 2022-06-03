package com.willr27.blocklings;

import com.willr27.blocklings.entity.BlocklingsEntityTypes;
import com.willr27.blocklings.entity.renderers.blockling.BlocklingRenderer;
import com.willr27.blocklings.item.items.BlocklingItem;
import com.willr27.blocklings.item.items.BlocklingsItems;
import com.willr27.blocklings.network.NetworkHandler;
import com.willr27.blocklings.sound.BlocklingsSounds;
import com.willr27.blocklings.util.Version;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Blocklings.MODID)
public class Blocklings
{
    /**
     * The mod's ID, which is also the mod's namespace.
     */
    @Nonnull
    public static final String MODID = "blocklings";

    /**
     * The mod's version.
     */
    @Nonnull
    public static final Version VERSION = new Version("7.0.0.3");

    /**
     * The mod's logger.
     */
    public static final Logger LOGGER = LogManager.getLogger();

    /**
     * The mod's constructor.
     */
    public Blocklings()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BlocklingsEntityTypes.register(modEventBus);
        BlocklingsItems.register(modEventBus);
        BlocklingsSounds.register(modEventBus);

        modEventBus.addListener(this::setupCommon);
        modEventBus.addListener(this::setupClient);

        MinecraftForge.EVENT_BUS.register(this);

        BlocklingsConfig.init();
    }

    /**
     * Setup shared between client and server.
     */
    private void setupCommon(final FMLCommonSetupEvent event)
    {
        NetworkHandler.init();
    }

    /**
     * Setup only on the client.
     */
    private void setupClient(final FMLClientSetupEvent event)
    {
        RenderingRegistry.registerEntityRenderingHandler(BlocklingsEntityTypes.BLOCKLING_ENTITY.get(), BlocklingRenderer::new);

        BlocklingItem.registerItemModelsProperties();
    }
}
