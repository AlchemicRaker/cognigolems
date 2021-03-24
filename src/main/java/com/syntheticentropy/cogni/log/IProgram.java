package com.syntheticentropy.cogni.log;

import com.mojang.datafixers.util.Either;

import java.util.List;

public interface IProgram {
    List<IFact> getFacts();
    Either<IProblem, ICompiledProgram> compile();
    boolean isValid();
}
