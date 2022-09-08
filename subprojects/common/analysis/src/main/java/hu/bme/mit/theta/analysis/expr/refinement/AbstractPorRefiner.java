package hu.bme.mit.theta.analysis.expr.refinement;

import hu.bme.mit.theta.analysis.Prec;
import hu.bme.mit.theta.analysis.algorithm.ARG;
import hu.bme.mit.theta.analysis.algorithm.ArgNode;
import hu.bme.mit.theta.analysis.algorithm.cegar.Refiner;
import hu.bme.mit.theta.analysis.algorithm.cegar.RefinerResult;
import hu.bme.mit.theta.analysis.expr.ExprAction;
import hu.bme.mit.theta.analysis.expr.ExprState;
import hu.bme.mit.theta.core.decl.Decl;
import hu.bme.mit.theta.core.type.Type;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class AbstractPorRefiner<S extends ExprState, A extends ExprAction, P extends Prec> implements Refiner<S, A, P> {

	private final Refiner<S, A, P> refiner;

	private final PruneStrategy pruneStrategy;

	private final Map<Decl<? extends Type>, Set<S>> ignoredVariableRegistry;

	private AbstractPorRefiner(final Refiner<S, A, P> refiner,
							   final PruneStrategy pruneStrategy,
							   final Map<Decl<? extends Type>, Set<S>> ignoredVariableRegistry) {
		this.refiner = refiner;
		this.pruneStrategy = pruneStrategy;
		this.ignoredVariableRegistry = ignoredVariableRegistry;
	}

	public static <S extends ExprState, A extends ExprAction, P extends Prec, R extends Refutation> AbstractPorRefiner<S, A, P> create(
			final Refiner<S, A, P> refiner, final PruneStrategy pruneStrategy,
			final Map<Decl<? extends Type>, Set<S>> ignoredVariableRegistry) {
		return new AbstractPorRefiner<>(refiner, pruneStrategy, ignoredVariableRegistry);
	}

	@Override
	public RefinerResult<S, A, P> refine(final ARG<S, A> arg, final P prec) {
		final RefinerResult<S, A, P> result = refiner.refine(arg, prec);
		if (result.isUnsafe() || pruneStrategy != PruneStrategy.LAZY) return result;

		final P newPrec = result.asSpurious().getRefinedPrec();
		final Set<Decl<? extends Type>> newlyAddedVars = new HashSet<>(newPrec.getUsedVars());
		newlyAddedVars.removeAll(prec.getUsedVars());

		newlyAddedVars.forEach(newVar -> {
			if (ignoredVariableRegistry.containsKey(newVar)) {
				Set<ArgNode<S, A>> nodesToReExpand = ignoredVariableRegistry.get(newVar).stream().flatMap(stateToPrune ->
						arg.getNodes().filter(node -> node.getState().equals(stateToPrune)) // TODO one state can be in one ARG node?
				).collect(Collectors.toSet());
				nodesToReExpand.forEach(arg::markForReExpansion);
				ignoredVariableRegistry.remove(newVar);
			}
		});

		return result;
	}
}
