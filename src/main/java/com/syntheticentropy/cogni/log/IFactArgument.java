package com.syntheticentropy.cogni.log;

import java.util.Optional;

public interface IFactArgument {
    String getName();
    Optional<IVariable> getVariable();
}
