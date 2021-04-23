package com.syntheticentropy.cogni.cognilog;
import com.mojang.datafixers.util.Either;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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


    @Test
    void canCreateCore() {
        Core<Object> core = new Core<Object>(ProgramTest.getValidDummyProgram());
    }

//    @Test
//    void canBeRunnable() {
//        Core<Object> core = new Core<Object>(ProgramTest.getValidDummyProgram());
//        assertTrue(core.isRunnable());
//    }

    @Test
    void canHaveAProgram() {
        Core<Object> core = new Core<Object>(ProgramTest.getValidDummyProgram());
        core.getProgram();
    }

    @Test
    void canFindEndOfSolutions() {
        Core<String> core = new Core<String>(ProgramTest.getComplexProgram(new ProgramTest.ActionLineString()));
        Core.SolutionResult<String> solutionResult;
        do {
            solutionResult = core.findNextSolution();
            solutionResult.getSolution().ifPresent(sr -> {
                System.out.println("Solution:" + String.join(",", sr));
            });
        } while(!solutionResult.isEndOfSearch());
        // An empty optional means all solutions have been found.
        // A list, empty or not, means solutions have been found
    }
}
