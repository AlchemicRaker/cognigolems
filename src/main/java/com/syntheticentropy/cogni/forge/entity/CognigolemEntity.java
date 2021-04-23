package com.syntheticentropy.cogni.forge.entity;

import com.syntheticentropy.cogni.cognilog.*;
import com.syntheticentropy.cogni.forge.action.MoveToCoordAction;
import com.syntheticentropy.cogni.forge.action.SampleAction;
import com.syntheticentropy.cogni.forge.entity.goal.MoveToGoal;
import com.syntheticentropy.cogni.forge.rule.ConstantBlockTypeRule;
import com.syntheticentropy.cogni.forge.rule.ConstantCoordinateListRule;
import com.syntheticentropy.cogni.forge.rule.ConstantCoordinateRule;
import com.syntheticentropy.cogni.forge.rule.NearbyBlockRule;
import com.syntheticentropy.cogni.forge.solution.SampleSolution;
import com.syntheticentropy.cogni.forge.solution.Solution;
import com.syntheticentropy.cogni.forge.symbol.BaseValue;
import com.syntheticentropy.cogni.forge.symbol.BlockTypeValue;
import com.syntheticentropy.cogni.forge.symbol.CoordinateValue;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class CognigolemEntity extends GolemEntity implements IInventory, ICogniEntity {
    private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
    private ItemStack heldItem = ItemStack.EMPTY;
    @Nullable
    private PlayerEntity interactingPlayer;

    protected CognigolemEntity(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 100.0D).add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.KNOCKBACK_RESISTANCE, 1.0D).add(Attributes.ATTACK_DAMAGE, 15.0D);
    }

    @Override
    public void load(CompoundNBT compoundNBT) {
        super.load(compoundNBT);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compoundNBT, this.items);
        if(compoundNBT.contains("Solutions")) {
            ListNBT solutionsListNBT = compoundNBT.getList("Solutions", 10);
            solutions = solutionsListNBT.stream().map(s->(CompoundNBT)s).map(Solution::fromNBT).collect(Collectors.toList());
        }
    }

    @Override
    public boolean save(CompoundNBT compoundNBT) {
        boolean b = super.save(compoundNBT);
        ItemStackHelper.saveAllItems(compoundNBT, this.items);
        List<CompoundNBT> solutionsList = solutions.stream().map(Solution::toNBT).collect(Collectors.toList());
        ListNBT solutionsListNBT = new ListNBT();
        solutionsListNBT.addAll(solutionsList);
        compoundNBT.put("Solutions", solutionsListNBT);
        return b;
    }

    public int getContainerSize() {
        return 27;
    }

    @Override
    public boolean isEmpty() {
        return this.getItems().stream().allMatch(ItemStack::isEmpty);
    }

    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    protected void setItems(NonNullList<ItemStack> p_199721_1_) {
        this.items = p_199721_1_;
    }

    @Override
    public ItemStack getItem(int p_70301_1_) {
        return this.getItems().get(p_70301_1_);
    }

    @Override
    public ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
        ItemStack itemstack = ItemStackHelper.removeItem(this.getItems(), p_70298_1_, p_70298_2_);
        if (!itemstack.isEmpty()) {
            this.setChanged();
        }

        return itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int p_70304_1_) {
        return ItemStackHelper.takeItem(this.getItems(), p_70304_1_);
    }

    @Override
    public void setItem(int p_70299_1_, ItemStack p_70299_2_) {
        this.getItems().set(p_70299_1_, p_70299_2_);
        if (p_70299_2_.getCount() > this.getMaxStackSize()) {
            p_70299_2_.setCount(this.getMaxStackSize());
        }

        this.setChanged();
    }

    @Override
    public void setChanged() {
        // not useful for mob entities?
    }

    @Override
    public boolean stillValid(PlayerEntity playerEntity) {
        if (!this.isAlive()) {
            return false;
        } else {
            return !(playerEntity.distanceToSqr((double)this.getX(), (double)this.getY(), (double)this.getZ()) > 64.0D);
        }
    }


    public ActionResultType mobInteract(PlayerEntity playerEntity, Hand hand) {
//        ItemStack itemstack = playerEntity.getItemInHand(hand);
        if (this.isAlive() /*&& !this.isTrading()*/ && !this.isBaby()) {


            if (!this.level.isClientSide) {
                this.setInteractingPlayer(playerEntity);
                this.openContainer(playerEntity, this.getDisplayName(), 1);
            }

            return ActionResultType.sidedSuccess(this.level.isClientSide);

        } else {
            return super.mobInteract(playerEntity, hand);
        }
    }

    public void setInteractingPlayer(@Nullable PlayerEntity playerEntity) {
        this.interactingPlayer = playerEntity;
    }


    void openContainer(PlayerEntity playerEntity, ITextComponent iTextComponent, int p_213707_3_) {
        OptionalInt optionalint = playerEntity.openMenu(new SimpleNamedContainerProvider((num, playerInventory, playerEntity1) -> {
            return ChestContainer.threeRows(num, playerInventory, this);
        }, iTextComponent));
    }

    @Override
    public void clearContent() {
        this.getItems().clear();
    }


    protected void dropCustomDeathLoot(DamageSource p_213333_1_, int p_213333_2_, boolean p_213333_3_) {
        super.dropCustomDeathLoot(p_213333_1_, p_213333_2_, p_213333_3_);
        InventoryHelper.dropContents(this.level, this, this);
    }


    // Golems have a cognilog Core that recompiles any time the program changes
    // and runs as long as the golem is not paused (when the inventory is open or when no core is present)
    // TODO: all of the "code inventory" of the golem should be contained in an extractable "core" object
    // TODO: when no core is present, the golem should not idle, it should appear entirely inert

    private Core<Solution> core;
    private List<Solution> solutions;

    protected void initSampleCore() {
        if(this.level.isClientSide) return;
        CoordinateValue startingCoordinate = new CoordinateValue(this.getX(), this.getY(), this.getZ());
//        CoordinateValue startingCoordinate = new CoordinateValue(this.getX()+5, this.getY(), this.getZ());
        List<CoordinateValue> coordinateList = Arrays.asList(
                new CoordinateValue(this.getX(), this.getY(), this.getZ()),
                new CoordinateValue(this.getX()+5, this.getY(), this.getZ()),
                new CoordinateValue(this.getX(), this.getY(), this.getZ()+5),
                new CoordinateValue(this.getX()-5, this.getY(), this.getZ()),
                new CoordinateValue(this.getX(), this.getY(), this.getZ()-5)
        );
        int blockTypeSymbol = 1;
        int coordinateSymbol = 2;

        List<Line<Solution>> lines = Arrays.asList(
                new MoveToCoordAction(coordinateSymbol),
                new NearbyBlockRule(this,
                        Arrays.asList(Optional.of(blockTypeSymbol), Optional.of(coordinateSymbol)),
                        Arrays.asList(BaseValue.Type.BlockType.ordinal(), BaseValue.Type.Coordinate.ordinal())),
                new ConstantBlockTypeRule(blockTypeSymbol, new BlockTypeValue(Blocks.CRAFTING_TABLE))
        );

        Program<Solution> program = Program.compileProgram(lines);
        this.core = new Core<>(program);
        if(this.solutions == null) {
            this.solutions = new ArrayList<>();
        }
    }

    boolean firstTick = true;
    int solutionCooldown = 0;

    @Override
    public void tick() {
        super.tick();
        if(!this.isAlive()) return;

        if(firstTick) {
            firstTick = false;
            if(core == null) {
                initSampleCore();
            }
        }

        if(core == null || !core.isRunnable()) return;

        if(solutionCooldown > 0) {
            solutionCooldown--;
            return;
        }

        // TODO: if there's queued solutions, execute those first
        if(this.solutions.size() > 0) {
            Solution topSolution = this.solutions.get(0);
            boolean doneWithSolution = topSolution.tick(this);
            if(doneWithSolution) {
                this.solutions.remove(0);
                solutionCooldown = 60;
            }
            return;
        }

        Core.SolutionResult<Solution> result = core.findNextSolution();
        Optional<List<Solution>> maybeSolutions = result.getSolution();
        if(!maybeSolutions.isPresent()) return;
        List<Solution> solutions = maybeSolutions.get();

        this.solutions.addAll(solutions);
    }

    private MoveToGoal moveToGoal;

    @Override
    public Optional<MoveToGoal> getMoveToGoal() {
        return Optional.ofNullable(this.moveToGoal);
    }

    @Override
    public Optional<CreatureEntity> getCreatureEntity() {
        return Optional.of(this);
    }

    protected void registerGoals() {
        // TODO: have no target selectors; all targets and goals will be controlled by Action solutions
        this.moveToGoal = new MoveToGoal(this);
        this.goalSelector.addGoal(1, this.moveToGoal);

        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
    }

    public Optional<ItemStack> getHeldItem() {
        return Optional.of(heldItem);
    }

    public boolean setHeldItem(ItemStack heldItem) {
        this.heldItem = heldItem;
        return true;
    }
}
