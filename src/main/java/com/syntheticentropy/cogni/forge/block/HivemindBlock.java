package com.syntheticentropy.cogni.forge.block;

import com.syntheticentropy.cogni.forge.item.Items;
import com.syntheticentropy.cogni.forge.tileentity.HivemindTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

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
    
    public void onRemove(BlockState blockState, World world, BlockPos blockPos, BlockState blockState1, boolean p_196243_5_) {
        if (!blockState.is(blockState1.getBlock())) {
            TileEntity tileentity = world.getBlockEntity(blockPos);
            if (tileentity instanceof IInventory) {
                InventoryHelper.dropContents(world, blockPos, (IInventory)tileentity);
                world.updateNeighbourForOutputSignal(blockPos, this);
            }

            super.onRemove(blockState, world, blockPos, blockState1, p_196243_5_);
        }
    }

    public ActionResultType use(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult blockRayTraceResult) {
        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        } else {
            this.openContainer(world, blockPos, playerEntity);
            return ActionResultType.CONSUME;
        }
    }


    protected void openContainer(World world, BlockPos blockPos, PlayerEntity playerEntity) {
        TileEntity tileentity = world.getBlockEntity(blockPos);
        if (tileentity instanceof HivemindTileEntity) {
            playerEntity.openMenu((INamedContainerProvider)tileentity);
        }
    }
}
