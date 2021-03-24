package com.syntheticentropy.cogni.forge.block;

import com.syntheticentropy.cogni.forge.item.Items;
import com.syntheticentropy.cogni.forge.tileentity.HivemindTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class HivemindBlock extends Block {
    public static final DirectionProperty FACING = HorizontalBlock.FACING;

    public HivemindBlock() {
        super(Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD));
        this.setRegistryName("hivemind");
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
//        return null;
        return new HivemindTileEntity();
    }

    @Override
    public Item asItem() {
        return Items.HIVEMIND;
    }

    public BlockState getStateForPlacement(BlockItemUseContext blockItemUseContext) {
        return this.defaultBlockState().setValue(FACING, blockItemUseContext.getHorizontalDirection().getOpposite());
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> stateContainerBuilder) {
        stateContainerBuilder.add(FACING);
    }

}
