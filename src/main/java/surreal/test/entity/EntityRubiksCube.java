package surreal.test.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;


/**
 * A basic entity for rendering
 **/
public class EntityRubiksCube extends Entity {

    public EntityRubiksCube(World worldIn) {
        super(worldIn);
        // Change hitbox size to a cube
        this.setSize(1.0F, 1.0F);
    }

    @Override protected void entityInit() {}
    @Override protected void readEntityFromNBT(NBTTagCompound compound) {}
    @Override protected void writeEntityToNBT(NBTTagCompound compound) {}
}
