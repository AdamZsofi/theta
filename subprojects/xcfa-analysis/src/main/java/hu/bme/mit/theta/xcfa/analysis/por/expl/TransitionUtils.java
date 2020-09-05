/*
 * Copyright 2019 Budapest University of Technology and Economics
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hu.bme.mit.theta.xcfa.analysis.por.expl;

import hu.bme.mit.theta.xcfa.XCFA;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Functions for gathering list of transitions from a given process, state, etc.
 * Every function is global as everything is safe to use.
 * Uses Java 8 Streams to optimize based on usage.
 * (I hope filter.filter.map.filter.blah.blah.findAny is optimized, for example)
 */
public final class TransitionUtils {

    private TransitionUtils() { }

    public static Collection<ProcessTransitions> getProcessTransitions(ExplState state) {
        return state.getProcessStates().getStates().entrySet().stream()
                .map(
                        process -> new ProcessTransitions(state, process.getKey(),
                                getTransitionsInternal(process.getKey(), process.getValue()).collect(Collectors.toUnmodifiableList())
                        )
                ).collect(Collectors.toUnmodifiableList());
    }

    public static Stream<Transition> getTransitions(ExplState state) {
        return state.getProcessStates().getStates().entrySet().stream()
                .flatMap(TransitionUtils::getTransitionsInternal);
    }

    public static Stream<Transition> getTransitions(ExplState readOnlyState, XCFA.Process process) {
        return getTransitionsInternal(process, readOnlyState.getProcess(process));
    }

    private static Stream<Transition> getTransitionsInternal(Map.Entry<XCFA.Process, ProcessState> ps) {
        return getTransitionsInternal(ps.getKey(), ps.getValue());
    }

    private static Stream<Transition> getTransitionsInternal(XCFA.Process process,
                                                             ProcessState processState) {
        return getTransitionsInternal(process, processState.getActiveCallState(),
                processState.isOutmostCall());
    }

    private static EdgeTransition getEdge(XCFA.Process process, XCFA.Process.Procedure.Edge edge) {
        // TODO cache results
        return new EdgeTransition(process, edge);
    }

    private static Stream<Transition> getTransitionsInternal(
            XCFA.Process process, CallState callState, boolean outmostCall) {
        // for every edge, create an edge transition
        return Stream.concat(
                callState.getLocation().getOutgoingEdges().stream().map( // edge transitions
                        edge->getEdge(callState.getProcess(), edge)
                ),
                leaveOnFinal(process, callState, outmostCall).stream() // also, on final location, add leave transition
        );
    }

    private static Optional<Transition> leaveOnFinal(XCFA.Process process, CallState callState,
                                                     boolean outmostCall) {
        if (!outmostCall && callState.isFinal())
            return Optional.of(new LeaveTransition(process, callState));
        return Optional.empty();
    }

    public static boolean equals(Transition a, Transition b) {
        while (a instanceof ExecutableTransitionBase) {
            a = ((ExecutableTransitionBase) a).getInternalTransition();
        }
        while (b instanceof ExecutableTransitionBase) {
            b = ((ExecutableTransitionBase) b).getInternalTransition();
        }
        return a.equals(b);
    }

    /**
     * Collects all transitions of all processes.
     */
    private static ProcessTransitions allTransitions(XCFA.Process process) {
        Collection<Transition> transitions =
                process.getProcedures().stream()
                        .flatMap(
                                p -> p.getEdges().stream()
                        ).map(
                                edge->getEdge(process, edge)
                        ).collect(Collectors.toUnmodifiableList());
        return new ProcessTransitions(null, process, transitions);
    }

    /**
     * Collects all transitions of all processes.
     */
    public static Collection<ProcessTransitions> getProcessTransitions(XCFA xcfa) {
        return xcfa.getProcesses().stream()
                .map(TransitionUtils::allTransitions)
                .collect(Collectors.toUnmodifiableList());
    }
}
