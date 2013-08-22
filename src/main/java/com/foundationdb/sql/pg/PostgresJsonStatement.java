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

package com.foundationdb.sql.pg;

import com.foundationdb.sql.optimizer.plan.CostEstimate;
import static com.foundationdb.sql.pg.PostgresJsonCompiler.JsonResultColumn;

import com.foundationdb.qp.operator.Operator;
import com.foundationdb.qp.row.Row;
import com.foundationdb.qp.rowtype.RowType;
import com.foundationdb.server.types.AkType;
import com.foundationdb.server.types3.mcompat.mtypes.MString;

import java.util.*;

public class PostgresJsonStatement extends PostgresOperatorStatement
{
    private List<JsonResultColumn> resultColumns;

    public PostgresJsonStatement(PostgresOperatorCompiler compiler) {
        super(compiler);
    }

    public void init(Operator resultOperator, RowType resultRowType,
                     List<JsonResultColumn> resultColumns,
                     PostgresType[] parameterTypes,
                     CostEstimate costEstimate,
                     boolean usePValues) {
        super.init(resultOperator, resultRowType,
                   // Looks like just one unlimited VARCHAR to the client.
                   jsonColumnNames(), jsonColumnTypes(),
                   parameterTypes, costEstimate, usePValues);
        this.resultColumns = resultColumns;
    }

    public static List<String> jsonColumnNames() {
        return Collections.singletonList("JSON");
    }

    public static List<PostgresType> jsonColumnTypes() {
        return Collections.singletonList(new PostgresType(PostgresType.TypeOid.VARCHAR_TYPE_OID,
                                                          (short)-1, -1, AkType.VARCHAR,
                                                          MString.VARCHAR.instance(false)));
    }

    @Override
    protected PostgresOutputter<Row> getRowOutputter(PostgresQueryContext context) {
        return new PostgresJsonOutputter(context, this, 
                                         resultColumns, getColumnTypes().get(0));
    }
    
}
