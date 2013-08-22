/**
 * Copyright (C) 2009-2013 FoundationDB, LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.foundationdb.server.expression.std;

import com.foundationdb.qp.operator.QueryBindings;
import com.foundationdb.qp.operator.QueryContext;
import com.foundationdb.qp.row.Row;
import com.foundationdb.server.expression.ExpressionEvaluation;
import com.foundationdb.server.types.util.ValueHolder;

public abstract class AbstractNoArgExpressionEvaluation extends ExpressionEvaluation.Base {
    @Override
    public void of(Row row) {
    }

    @Override
    public void of(QueryContext context) {
    }

    @Override
    public void of(QueryBindings bindings) {
    }

    @Override
    public void acquire() {
    }

    @Override
    public boolean isShared() {
        return false;
    }

    @Override
    public void release() {
    }

    // for use by subclasses

    protected AbstractNoArgExpressionEvaluation() {
    }

    protected ValueHolder valueHolder () {
        return valueHolder == null ? valueHolder = new ValueHolder() : valueHolder;
    }

    private ValueHolder valueHolder;
}
