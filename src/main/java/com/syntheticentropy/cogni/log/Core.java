package com.syntheticentropy.cogni.log;

import com.google.common.collect.Lists;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.INBTType;

import javax.naming.CompoundName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Core {

    public static IResolveResponse resolve(ICompiledProgram program, IResolutionState resolutionState) {
        int iterationLimit = 50; // after hitting this many failures, save state and return results
        List<IResolvedAction> resolvedActions = new ArrayList<>();
        List<IActionGoal> allActionGoals = program.getActionGoals();

        // load the stack from the resolutionState
        List<IStackEntry> stack = resolutionState.getStack();

        // if the stack is empty in the resolutionState, then we are starting fresh
        if(stack.size() == 0 && allActionGoals.size() > 0) {
            stack.add(StackEntry.nextAction(0)); // queue the first actionGoal
        }

        while(iterationLimit > 0) {
            // we run the top thing on the stack.

            // nothing left in the stack? exit early!
            if(stack.size() == 0) {
                break;
            }

            IStackEntry stackEntry = stack.get(stack.size() - 1);

            switch (stackEntry.getStackToken()) {
                case NEXT_ACTION:
                    int actionIndex = stackEntry.getActionIndex();
                    // start working this action index
                    resolutionState.setActionIndex(actionIndex);

                    // replace self with the next action
                    if(actionIndex < allActionGoals.size()-1) {
                        stack.set(stack.size()-1, StackEntry.nextAction(actionIndex+1));
                    } else {
                        stack.remove(stack.size()-1);
                    }

                    // push first fact on the stack
                    IActionGoal actionGoal = allActionGoals.get(actionIndex);
                    if(actionGoal.getFacts().size() > 0) {

                    }

                    break;
                case INIT_FACT:
                    break;
                case RESUME_FACT:
                    break;
            }
            // when we get a positive binding and there's no unbound variables left, we've made a ResolvedAction!
            // save it and continue in the exact same place, to find any more matches

            iterationLimit--;
        }

        return new ResolveResponse(resolutionState, resolvedActions);
    }

    public static class StackEntry extends CompoundNBT implements IStackEntry {
        public static IStackEntry nextAction(int actionIndex) {
            CompoundNBT compoundNBT = new CompoundNBT();
            compoundNBT.putString("token",StackToken.NEXT_ACTION.name());
            compoundNBT.putInt("actionIndex", actionIndex);
            return new StackEntry(compoundNBT);
        }

        private StackEntry(CompoundNBT state) {
            this.merge(state);
        }

        public StackToken getStackToken() {
            return StackToken.valueOf(getString("token"));
        }

        public int getActionIndex() {
            return contains("actionIndex") ? getInt("actionIndex") : 0;
        }
    }

    interface IResolutionState {
        Integer getActionIndex();
        void setActionIndex(Integer actionIndex);
        List<IStackEntry> getStack();
    }

    interface IStackEntry {
        StackToken getStackToken();
        int getActionIndex();
    }

    public enum StackToken {
        NEXT_ACTION,
        INIT_FACT,
        RESUME_FACT
    }

    public static class ResolutionState extends CompoundNBT implements IResolutionState {
        public ResolutionState() {
            putInt("actionIndex", 0);
        }
        public ResolutionState(CompoundNBT copy) {
            super();
            this.merge(copy);
        }
        @Override
        public Integer getActionIndex() {
            return getInt("actionIndex");
        }

        @Override
        public void setActionIndex(Integer actionIndex) {
            putInt("actionIndex", actionIndex);
        }

        @Override
        public List<IStackEntry> getStack() {
            return getList("stack", 10).stream().map(e -> new StackEntry((CompoundNBT) e)).collect(Collectors.toList());
        }

    }

    public interface IResolveResponse {
        IResolutionState getResolutionState();
        List<IResolvedAction> getResolvedActions();
    }

    public static class ResolveResponse implements IResolveResponse {
        private final IResolutionState resolutionState;
        private final List<IResolvedAction> resolvedActions;
        private ResolveResponse(IResolutionState resolutionState, List<IResolvedAction> resolvedActions) {
            this.resolutionState = resolutionState;
            this.resolvedActions = resolvedActions;
        }

        @Override
        public IResolutionState getResolutionState() {
            return resolutionState;
        }

        @Override
        public List<IResolvedAction> getResolvedActions() {
            return resolvedActions;
        }
    }
}
