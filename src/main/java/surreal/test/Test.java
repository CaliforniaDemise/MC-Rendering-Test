package surreal.test;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import surreal.test.entity.EntityRubiksCube;
import surreal.test.entity.renderer.RenderRubiksCube;

@Mod(modid = Tags.MOD_ID, name = "Test", version = Tags.MOD_VERSION)
public class Test {

    /**
     * The location of the obj model.
     **/
    public static final ModelResourceLocation MODEL_RUBIKS_CUBE = new ModelResourceLocation(new ResourceLocation(Tags.MOD_ID, "rubiks_cube"), "inventory");

    @Mod.EventHandler
    public void construction(FMLConstructionEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (FMLLaunchHandler.side() == Side.CLIENT) {
            OBJLoader.INSTANCE.addDomain(Tags.MOD_ID); // Add mods models/ location to OBJLoader so the game tries to load from it
            RenderingRegistry.registerEntityRenderingHandler(EntityRubiksCube.class, RenderRubiksCube::new);
        }
    }

    /**
     * Registering and baking the model manually.
     * You don't need to define .mtl file, it defines it by changing models name from 'name.obj' from name.mtl
     * We're using inventory variant so we're using ITEM vertex format. I didn't play with it enough to say if it's important or not but better safe than sorry.
     **/
    @SubscribeEvent
    public void bakeModel(ModelBakeEvent event) {
        IRegistry<ModelResourceLocation, IBakedModel> registry = event.getModelRegistry();
        try {
            IModel model = OBJLoader.INSTANCE.loadModel(new ResourceLocation(Tags.MOD_ID, "models/entity/rubiks_cube.obj"));
            registry.putObject(MODEL_RUBIKS_CUBE, model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter()));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Registering the textures model will use to the stitcher.
     * Stitcher stitches this textures to {@link TextureMap#LOCATION_BLOCKS_TEXTURE}. You need to bind it before rendering the model.
     * The textures are defined in .mtl file with map_Kd key.
     **/
    @SubscribeEvent
    public void stitchTexture(TextureStitchEvent.Pre event) {
        TextureMap map = event.getMap();
        map.registerSprite(new ResourceLocation(Tags.MOD_ID, "entity/black"));
        map.registerSprite(new ResourceLocation(Tags.MOD_ID, "entity/white"));
        map.registerSprite(new ResourceLocation(Tags.MOD_ID, "entity/blue"));
        map.registerSprite(new ResourceLocation(Tags.MOD_ID, "entity/green"));
        map.registerSprite(new ResourceLocation(Tags.MOD_ID, "entity/orange"));
        map.registerSprite(new ResourceLocation(Tags.MOD_ID, "entity/red"));
        map.registerSprite(new ResourceLocation(Tags.MOD_ID, "entity/yellow"));
    }

    @SubscribeEvent
    public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        event.getRegistry().register(EntityEntryBuilder.create().entity(EntityRubiksCube.class).id(new ResourceLocation(Tags.MOD_ID, "rubiks_cube"), 0).name("rubiks_cube").factory(EntityRubiksCube::new).tracker(80, 1, true).build());
    }

    @SubscribeEvent
    public void registerItem(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new Item() {
            @Override
            public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
                if (!worldIn.isRemote) {
                    pos = pos.offset(facing);
                    EntityRubiksCube rubiksCube = new EntityRubiksCube(worldIn);
                    rubiksCube.setPosition(pos.getX(), pos.getY(), pos.getZ());
                    worldIn.spawnEntity(rubiksCube);
                }
                return EnumActionResult.SUCCESS;
            }
        }.setCreativeTab(CreativeTabs.MISC).setRegistryName(Tags.MOD_ID, "rubiks_cube"));
    }
}