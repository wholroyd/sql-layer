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

import com.foundationdb.qp.operator.QueryContext;
import com.foundationdb.server.error.InvalidArgumentTypeException;
import com.foundationdb.server.error.WrongExpressionArityException;
import com.foundationdb.server.expression.*;
import com.foundationdb.server.service.functions.Scalar;
import com.foundationdb.server.types.AkType;
import com.foundationdb.server.types.NullValueSource;
import com.foundationdb.server.types.ValueSource;
import com.foundationdb.sql.StandardException;

public class AbsExpression extends AbstractUnaryExpression 
{    
    @Scalar ({"absolute", "abs"})
    public static final ExpressionComposer COMPOSER = new InternalComposer();
    
    private static class InternalComposer extends UnaryComposer
    {
        @Override
        protected Expression compose(Expression argument, ExpressionType argType, ExpressionType resultType)
        {
            return new AbsExpression(argument);
        }

        @Override
        public ExpressionType composeType(TypesList argumentTypes) throws StandardException
        {
            if (argumentTypes.size() != 1) 
                throw new WrongExpressionArityException(1, argumentTypes.size());
           
            ExpressionType argExpType = argumentTypes.get(0);
            AkType argAkType = argExpType.getType();
            
            // Cast both VARCHAR and UNSUPPORTED; UNSUPPORTED appearing on SQL params (ABS(?) query)
            if (argAkType == AkType.VARCHAR || argAkType == AkType.UNSUPPORTED)
            {
                argumentTypes.setType(0, AkType.DOUBLE);
            }
            
            return argumentTypes.get(0);
        }
    }
    
    private static class InnerEvaluation extends AbstractUnaryExpressionEvaluation
    {
        public InnerEvaluation(ExpressionEvaluation eval)
        {
            super(eval);
        }
        
        @Override
        public ValueSource eval()
        {
            if (operand().isNull())
                return NullValueSource.only();
            AkType operandType = operand().getConversionType();
            
            switch (operandType) {
                case VARCHAR:
                    valueHolder().putDouble( Math.abs(Double.parseDouble(operand().getString())));
                    break;
                case DOUBLE:
                    valueHolder().putDouble( Math.abs(operand().getDouble()) ); 
                    break;   
                case FLOAT:
                    valueHolder().putFloat( Math.abs(operand().getFloat()) ); 
                    break;
                case LONG:
                    valueHolder().putLong( Math.abs(operand().getLong()) ); 
                    break;
                case INT:
                    valueHolder().putInt( Math.abs(operand().getInt()) ); 
                    break;
                case DECIMAL:
                    valueHolder().putDecimal( operand().getDecimal().abs()); 
                    break;
                case U_DOUBLE: 
                case U_BIGINT: 
                case U_FLOAT: 
                case U_INT:
                    // Unsigned values remain the same
                    valueHolder().copyFrom(operand()); 
                    break;
                default:
                    QueryContext context = queryContext();
                    if (context != null)
                        context.warnClient(new InvalidArgumentTypeException("ABS: " + operandType.name()));
                    return NullValueSource.only();
            }
            
            return valueHolder();
        }  
        
    }
    
    protected AbsExpression(Expression operand)
    {
        // ctor sets type and value
        super(operand.valueType(), operand);
    }
    
    @Override
    public String name() 
    {
        return "ABS";
    }

    @Override
    public ExpressionEvaluation evaluation() 
    {
        return new InnerEvaluation(this.operandEvaluation());
    }
    
}
