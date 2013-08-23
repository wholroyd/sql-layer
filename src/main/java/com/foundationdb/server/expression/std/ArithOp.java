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

import com.foundationdb.server.expression.ExpressionType;
import java.math.BigDecimal;
import java.math.BigInteger;


public interface ArithOp 
{  
    // long 
    long evaluate (long one, long two, ExpressionType exp);
    
    // double
    double evaluate (double one, double two, ExpressionType exp);
    
    // BigDecimal
    BigDecimal evaluate (BigDecimal one, BigDecimal two, ExpressionType exp);
    
    // BigInteger
    BigInteger evaluate (BigInteger one, BigInteger two, ExpressionType exp);

    abstract char opName ();
    
    boolean isInfix();
    
    boolean isAssociative();
}