/*
 *  Copyright 2023 Budapest University of Technology and Economics
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/**
 * Expressions that work with multiple types. Use {@link hu.bme.mit.theta.core.type.abstracttype.AbstractExprs}
 * to create them. Note that these are abstract classes, and the corresponding expression for a given type will
 * be created (e.g., integer addition).
 *
 * Arithmetic:
 * - {@link hu.bme.mit.theta.core.type.abstracttype.AddExpr}: addition
 * - {@link hu.bme.mit.theta.core.type.abstracttype.DivExpr}: division
 * - {@link hu.bme.mit.theta.core.type.abstracttype.MulExpr}: multiplication
 * - {@link hu.bme.mit.theta.core.type.abstracttype.SubExpr}: subtraction
 *
 * Comparison:
 * - {@link hu.bme.mit.theta.core.type.abstracttype.EqExpr}: equal
 * - {@link hu.bme.mit.theta.core.type.abstracttype.GeqExpr}: greater or equal
 * - {@link hu.bme.mit.theta.core.type.abstracttype.GtExpr}: greater
 * - {@link hu.bme.mit.theta.core.type.abstracttype.LeqExpr}: less or equal
 * - {@link hu.bme.mit.theta.core.type.abstracttype.LtExpr}: less
 * - {@link hu.bme.mit.theta.core.type.abstracttype.NeqExpr}: not equal
 *
 * Other
 * - {@link hu.bme.mit.theta.core.type.abstracttype.CastExpr}: cast
 */

package hu.bme.mit.theta.core.type.abstracttype;