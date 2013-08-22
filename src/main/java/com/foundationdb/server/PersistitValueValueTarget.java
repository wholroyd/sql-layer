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

package com.foundationdb.server;

import com.foundationdb.ais.model.Column;
import com.foundationdb.qp.operator.Cursor;
import com.foundationdb.server.types.AkType;
import com.foundationdb.server.types.ValueSourceHelper;
import com.foundationdb.server.types.ValueTarget;
import com.foundationdb.util.ByteSource;
import com.persistit.Value;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class PersistitValueValueTarget implements ValueTarget {

    // PersistitKeyValueTarget interface

    public void attach(Value value) {
        this.value = value;
    }

    public PersistitValueValueTarget expectingType(AkType type) {
        this.type = type;
        return this;
    }

    public PersistitValueValueTarget expectingType(Column column) {
        return expectingType(column.getType().akType());
    }
    
    // ValueTarget interface

    @Override
    public void putNull() {
        checkState(AkType.NULL);
        value.putNull();
        invalidate();
    }

    @Override
    public void putDate(long value) {
        checkState(AkType.DATE);
        this.value.put(value);
        invalidate();
    }

    @Override
    public void putDateTime(long value) {
        checkState(AkType.DATETIME);
        this.value.put(value);
        invalidate();
    }

    @Override
    public void putDecimal(BigDecimal value) {
        checkState(AkType.DECIMAL);
        this.value.put(value);
        invalidate();
    }

    @Override
    public void putDouble(double value) {
        checkState(AkType.DOUBLE);
        this.value.put(value);
        invalidate();
    }

    @Override
    public void putFloat(float value) {
        checkState(AkType.FLOAT);
        this.value.put(value);
        invalidate();
    }

    @Override
    public void putInt(long value) {
        checkState(AkType.INT);
        this.value.put(value);
        invalidate();
    }

    @Override
    public void putLong(long value) {
        checkState(AkType.LONG);
        this.value.put(value);
        invalidate();
    }

    @Override
    public void putString(String value) {
        checkState(AkType.VARCHAR);
        this.value.put(value);
        invalidate();
    }

    @Override
    public void putText(String value) {
        checkState(AkType.TEXT);
        this.value.put(value);
        invalidate();
    }

    @Override
    public void putTime(long value) {
        checkState(AkType.TIME);
        this.value.put(value);
        invalidate();
    }

    @Override
    public void putTimestamp(long value) {
        checkState(AkType.TIMESTAMP);
        this.value.put(value);
        invalidate();
    }
    
    @Override
    public void putInterval_Millis(long value) {
        throw new UnsupportedOperationException("interval not supported yet");
    }

    @Override
    public void putInterval_Month (long value) {
        throw new UnsupportedOperationException ("interval not supported yet");
    }
    
    @Override
    public void putUBigInt(BigInteger value) {
        checkState(AkType.U_BIGINT);
        this.value.put(value);
        invalidate();
    }

    @Override
    public void putUDouble(double value) {
        checkState(AkType.U_DOUBLE);
        this.value.put(value);
        invalidate();
    }

    @Override
    public void putUFloat(float value) {
        checkState(AkType.U_FLOAT);
        this.value.put(value);
        invalidate();
    }

    @Override
    public void putUInt(long value) {
        checkState(AkType.U_INT);
        this.value.put(value);
        invalidate();
    }

    @Override
    public void putVarBinary(ByteSource value) {
        checkState(AkType.VARBINARY);
        this.value.putByteArray(value.byteArray(), value.byteArrayOffset(), value.byteArrayLength());
        invalidate();
    }

    @Override
    public void putYear(long value) {
        checkState(AkType.YEAR);
        this.value.put(value);
        invalidate();
    }

    @Override
    public void putBool(boolean value) {
        checkState(AkType.BOOL);
        this.value.put(value);
        invalidate();
    }

    @Override
    public void putResultSet(Cursor value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AkType getConversionType() {
        return type;
    }

    // object interface

    @Override
    public String toString() {
        return value.toString();
    }

    // private methods

    private void checkState(AkType type) {
        ValueSourceHelper.checkType(this.type, type);
    }

    private void invalidate() {
        type = AkType.UNSUPPORTED;
    }

    // object state

    private Value value;
    private AkType type = AkType.UNSUPPORTED;
}
