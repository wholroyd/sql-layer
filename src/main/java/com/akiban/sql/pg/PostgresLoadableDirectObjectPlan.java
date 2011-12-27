/**
 * Copyright (C) 2011 Akiban Technologies Inc.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 */

package com.akiban.sql.pg;

import com.akiban.qp.loadableplan.LoadableDirectObjectPlan;
import com.akiban.qp.loadableplan.DirectObjectPlan;
import com.akiban.qp.loadableplan.DirectObjectCursor;
import com.akiban.qp.operator.Bindings;
import com.akiban.util.Tap;

import java.util.List;
import java.io.IOException;

public class PostgresLoadableDirectObjectPlan extends PostgresBaseStatement
{
    private static final Tap.InOutTap EXECUTE_TAP = Tap.createTimer("PostgresLoadableDirectObjectPlan: execute shared");
    private static final Tap.InOutTap ACQUIRE_LOCK_TAP = Tap.createTimer("PostgresLoadableDirectObjectPlan: acquire shared lock");

    private DirectObjectPlan plan;
    private Object[] args;

    protected PostgresLoadableDirectObjectPlan(LoadableDirectObjectPlan loadablePlan,
                                               Object[] args)
    {
        super(loadablePlan.columnNames(),
              loadablePlan.columnTypes(),
              null, 
              null);
        this.plan = loadablePlan.plan();
        this.args = args;
    }
    
    @Override
    protected Tap.InOutTap executeTap()
    {
        return EXECUTE_TAP;
    }

    @Override
    protected Tap.InOutTap acquireLockTap()
    {
        return ACQUIRE_LOCK_TAP;
    }

    @Override
    public Bindings getBindings() {
        return PostgresLoadablePlan.getBindings(args);
    }

    @Override
    public PostgresStatement getBoundStatement(String[] parameters,
                                               boolean[] columnBinary, 
                                               boolean defaultColumnBinary) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TransactionMode getTransactionMode() {
        return TransactionMode.NONE;
    }
    
    @Override
    public int execute(PostgresServerSession server, int maxrows) throws IOException {
        PostgresMessenger messenger = server.getMessenger();
        Bindings bindings = getBindings();
        int nrows = 0;
        DirectObjectCursor cursor = null;
        try {
            cursor = plan.cursor();
            cursor.open(bindings);
            PostgresDirectObjectOutputter outputter = new PostgresDirectObjectOutputter(messenger, this);
            List<?> row;
            while ((row = cursor.next()) != null) {
                if (row.isEmpty()) {
                    messenger.flush();
                }
                else {
                    outputter.output(row);
                    nrows++;
                }
                if ((maxrows > 0) && (nrows >= maxrows))
                    break;
            }
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        {        
            messenger.beginMessage(PostgresMessages.COMMAND_COMPLETE_TYPE.code());
            messenger.writeString("CALL " + nrows);
            messenger.sendMessage();
        }
        return nrows;
    }

}
