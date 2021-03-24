package com.syntheticentropy.cogni.log;

import com.google.common.collect.Lists;
import net.minecraft.nbt.CompoundNBT;

import javax.naming.CompoundName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Core {
//    private final ICompiledProgram program;
//
//    public Core(ICompiledProgram program) {
//        this.program = program;
//        // Also provide state to allow:
//        // Iteration pause & resume
//        // Caching
//    }

    public static IResolveResponse resolve(ICompiledProgram program, IResolutionState resolutionState) {
        int iterationLimit = 50; // after hitting this many failures, save state and return results
        List<IResolvedAction> resolvedActions = new ArrayList<>();
        List<IActionGoal> actionGoals = program.getActionGoals();

        List<StackEntry> stack = new ArrayList<>();
        // prime the stack from the resolutionState

        // if the stack is empty in the resolutionState, then we are starting fresh
        if(stack.size() == 0 && actionGoals.size() > 0) {
            stack.add(new StackEntry(StackToken.INIT_ACTION, null)); // queue the first actionGoal
        }

        while(iterationLimit > 0) {
            // we run the top thing on the stack.

            // nothing left in the stack? exit early!
            if(stack.size() == 0) {
                break;
            }

            final StackEntry stackEntry = stack.get(stack.size() - 1);

            switch (stackEntry.getStackToken()) {
                case INIT_ACTION:
                    break;
                case NEXT_ACTION:
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

    public static class StackEntry {
        private final StackToken stackToken;
        private final CompoundNBT state;
        private StackEntry(StackToken stackToken, CompoundNBT state) {
            this.stackToken = stackToken;
            this.state = state;
        }

        public StackToken getStackToken() {
            return stackToken;
        }

        public CompoundNBT getState() {
            return state;
        }
    }

    public enum StackToken {
        INIT_ACTION,
        NEXT_ACTION,
        INIT_FACT,
        RESUME_FACT
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
