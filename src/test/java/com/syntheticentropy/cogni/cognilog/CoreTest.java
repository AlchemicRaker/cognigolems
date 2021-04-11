package com.syntheticentropy.cogni.cognilog;
import com.mojang.datafixers.util.Either;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CoreTest {
    /*
    A Core:
        contains a program (not necessarily valid)
        may be runnable (if the program is valid)
        has state that is OKAY to lose (iterator states do not need to be preserved)
        will find and return solutions to the program, until all iterations have resolved
        can be restarted, which resets iterator states to their beginning (but may help preserve cached data)
     */

    private Program getValidDummyProgram() {
        return new Program(Arrays.asList(new ActionLine(Arrays.asList(),Arrays.asList())));
    }

    @Test
    void canCreateCore() {
        Core core = new Core(getValidDummyProgram());
    }

    @Test
    void canBeRunnable() {
        Core core = new Core(getValidDummyProgram());
        assertTrue(core.isRunnable());
    }

    @Test
    void canHaveAProgram() {
        Core core = new Core(getValidDummyProgram());
        core.getProgram();
    }

    @Test
    void canFindEndOfSolutions() {
        Core core = new Core(new Program(Arrays.asList()));
        Core.SolutionResult solutionResult;
        do {
            solutionResult = core.findNextSolution();
        } while(!solutionResult.isEndOfSearch());
        // An empty optional means all solutions have been found.
        // A list, empty or not, means solutions have been found
    }
}
