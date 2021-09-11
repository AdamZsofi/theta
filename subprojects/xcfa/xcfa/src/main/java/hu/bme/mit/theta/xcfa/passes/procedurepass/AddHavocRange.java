/*
 * Copyright 2021 Budapest University of Technology and Economics
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

package hu.bme.mit.theta.xcfa.passes.procedurepass;

import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.stmt.HavocStmt;
import hu.bme.mit.theta.core.stmt.Stmt;
import hu.bme.mit.theta.frontend.FrontendMetadata;
import hu.bme.mit.theta.frontend.transformation.model.types.complex.CComplexType;
import hu.bme.mit.theta.xcfa.model.XcfaEdge;
import hu.bme.mit.theta.xcfa.model.XcfaProcedure;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AddHavocRange extends ProcedurePass {

	@Override
	public XcfaProcedure.Builder run(XcfaProcedure.Builder builder) {
		Set<HavocStmt<?>> alreadyAssumed = new LinkedHashSet<>();
		boolean found = true;
		while(found) {
			found = false;
			for (XcfaEdge edge : new ArrayList<>(builder.getEdges())) {
				Optional<Stmt> e = edge.getLabels().stream().filter(stmt -> stmt instanceof HavocStmt && !alreadyAssumed.contains(stmt)).findAny();
				if (e.isPresent()) {
					List<Stmt> collect = new ArrayList<>();
					for (Stmt stmt : edge.getLabels()) {
						if (stmt == e.get()) {
							VarDecl<?> var = ((HavocStmt) e.get()).getVarDecl();
							collect.add(stmt);
							Stmt wraparoundAssumption = CComplexType.getType(var.getRef()).limit(var.getRef());
							collect.add(wraparoundAssumption);
						} else collect.add(stmt);
					}
					alreadyAssumed.add((HavocStmt<?>) e.get());
					XcfaEdge xcfaEdge;
					xcfaEdge = new XcfaEdge(edge.getSource(), edge.getTarget(), collect);
					builder.removeEdge(edge);
					builder.addEdge(xcfaEdge);
					found = true;
					FrontendMetadata.lookupMetadata(edge).forEach((s, o) -> {
						FrontendMetadata.create(xcfaEdge, s, o);
					});
				}
			}
		}

		return builder;
	}

	@Override
	public boolean isPostInlining() {
		return true;
	}
}
