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

package com.foundationdb.server.types.conversion;

import com.foundationdb.server.types.AkType;
import com.foundationdb.server.types.ValueTarget;

abstract class ConverterForFloat extends FloatConverter {

    static final FloatConverter SIGNED = new ConverterForFloat() {
        @Override
        protected void putFloat(ValueTarget target, float value) {
            target.putFloat(value);
        }
    };

    static final FloatConverter UNSIGNED = new ConverterForFloat() {
        @Override
        protected void putFloat(ValueTarget target, float value) {
            target.putUFloat(value);
        }
    };

    // AbstractConverter interface

    @Override
    protected AkType targetConversionType() {
        return AkType.FLOAT;
    }

    private ConverterForFloat() {}
}
