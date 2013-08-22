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

package com.foundationdb.server.aggregation.std;

import com.foundationdb.junit.OnlyIfNot;
import com.foundationdb.server.types.NullValueSource;
import java.util.EnumSet;
import com.foundationdb.junit.Parameterization;
import java.util.Collection;
import com.foundationdb.junit.ParameterizationBuilder;
import org.junit.runner.RunWith;
import com.foundationdb.junit.NamedParameterizedRunner;
import java.math.BigInteger;
import java.math.BigDecimal;
import com.foundationdb.server.aggregation.Aggregator;
import com.foundationdb.server.types.AkType;
import com.foundationdb.server.types.util.ValueHolder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@RunWith(NamedParameterizedRunner.class)
public class BitAggregatorsTest
{
    protected static enum Types
    {
        DOUBLE
        {
            @Override
            public BigInteger aggregate_test1(Aggregator aggregator, ValueHolder holder)
            {
                holder.clear();
                for (int n = 0; n < 20; ++n)
                {
                    holder.putDouble(n);
                    aggregator.input(holder);
                }
                holder.putUBigInt(BigInteger.ONE); // just to change the type of holder to u_bigint
                aggregator.output(holder);
                return holder.getUBigInt();
            }

            @Override
            public BigInteger aggregate_test2(Aggregator aggregator, ValueHolder holder)
            {
                holder.clear();
                for (int n = 0; n < 5; ++n)
                {
                    holder.putDouble(5);
                    aggregator.input(holder);
                }
                holder.putUBigInt(BigInteger.ONE); 
                aggregator.output(holder);
                return holder.getUBigInt();
            }
        },

        FLOAT
        {
            public BigInteger aggregate_test1(Aggregator aggregator, ValueHolder holder)
            {
                holder.clear();
                for (int n = 0; n < 20; ++n)
                {
                    holder.putFloat(n);
                    aggregator.input(holder);
                }
                holder.putUBigInt(BigInteger.ONE);
                aggregator.output(holder);
                return holder.getUBigInt();
            }

            public BigInteger aggregate_test2(Aggregator aggregator, ValueHolder holder)
            {
                holder.clear();
                for (int n = 0; n < 5; ++n)
                {
                    holder.putFloat(5);
                    aggregator.input(holder);
                }
                holder.putUBigInt(BigInteger.ONE);
                aggregator.output(holder);
                return holder.getUBigInt();
            }
        },

        LONG
        {
            public BigInteger aggregate_test1(Aggregator aggregator, ValueHolder holder)
            {
                holder.clear();
                for (int n = 0; n < 20; ++n)
                {
                    holder.putLong(n);
                    aggregator.input(holder);
                }
                holder.putUBigInt(BigInteger.ONE);
                aggregator.output(holder);
                return holder.getUBigInt();
            }

            public BigInteger aggregate_test2(Aggregator aggregator, ValueHolder holder)
            {
                holder.clear();
                for (int n = 0; n < 5; ++n)
                {
                    holder.putLong(5);
                    aggregator.input(holder);
                }
                holder.putUBigInt(BigInteger.ONE);
                aggregator.output(holder);
                return holder.getUBigInt();
            }
        },

        INT
        {
            public BigInteger aggregate_test1(Aggregator aggregator, ValueHolder holder)
            {
                holder.clear();
                for (int n = 0; n < 20; ++n)
                {
                    holder.putInt(n);
                    aggregator.input(holder);
                }
                holder.putUBigInt(BigInteger.ONE);
                aggregator.output(holder);
                return holder.getUBigInt();
            }

            public BigInteger aggregate_test2(Aggregator aggregator, ValueHolder holder)
            {
                holder.clear();
                for (int n = 0; n < 5; ++n)
                {
                    holder.putInt(5);
                    aggregator.input(holder);
                }
                holder.putUBigInt(BigInteger.ONE);
                aggregator.output(holder);
                return holder.getUBigInt();
            }
        },

        DECIMAL
        {
            public BigInteger aggregate_test1(Aggregator aggregator, ValueHolder holder)
            {
                holder.clear();
                for (int n = 0; n < 20; ++n)
                {
                    holder.putDecimal(BigDecimal.valueOf(n));
                    aggregator.input(holder);
                }
                holder.putUBigInt(BigInteger.ONE);
                aggregator.output(holder);
                return holder.getUBigInt();
            }

            public BigInteger aggregate_test2(Aggregator aggregator, ValueHolder holder)
            {
                holder.clear();
                for (int n = 0; n < 5; ++n)
                {
                    holder.putDecimal(BigDecimal.valueOf(5));
                    aggregator.input(holder);
                }
                holder.putUBigInt(BigInteger.ONE);
                aggregator.output(holder);
                return holder.getUBigInt();
            }
        },

        U_BIGINT
        {
            public BigInteger aggregate_test1(Aggregator aggregator, ValueHolder holder)
            {

                holder.clear();
                for (int n = 0; n < 20; ++n)
                {
                    holder.putUBigInt(BigInteger.valueOf(n));
                    aggregator.input(holder);
                }
                holder.putUBigInt(BigInteger.ONE);
                aggregator.output(holder);
                return holder.getUBigInt();
            }

            public BigInteger aggregate_test2(Aggregator aggregator, ValueHolder holder)
            {

                holder.clear();
                for (int n = 0; n < 5; ++n)
                {
                    holder.putUBigInt(BigInteger.valueOf(5));
                    aggregator.input(holder);
                }
                holder.putUBigInt(BigInteger.ONE);
                aggregator.output(holder);
                return holder.getUBigInt();
            }
        };

        abstract BigInteger aggregate_test1 (Aggregator aggregator, ValueHolder holder);
        abstract BigInteger aggregate_test2 (Aggregator aggregator, ValueHolder holder);
    }

    protected static final EnumSet<AkType> SUPPORTED = EnumSet.of(AkType.LONG,
                                                                AkType.DOUBLE,
                                                                AkType.INT,
                                                                AkType.U_BIGINT,
                                                                AkType.DECIMAL,
                                                                AkType.FLOAT);

    private static ValueHolder holder = new ValueHolder();
    private static final BigInteger N64 = new BigInteger("FFFFFFFFFFFFFFFF", 16);
    private static boolean alreadyExc = false;

    private final Aggregator aggregator1, aggregator2;
    private Aggregator aggregator;
    private final BigInteger expected1, expected2;
    private BigInteger expected;
    private final Types type;

    public BitAggregatorsTest (Aggregator aggregator, BigInteger expected1, BigInteger expected2, Types type)
    {
        this.aggregator1 = aggregator;
        this.aggregator2 = aggregator;
        this.expected1 = expected1;
        this.expected2 = expected2;
        this.type = type;
    }

    @NamedParameterizedRunner.TestParameters
    public static Collection<Parameterization> params()
    {
        ParameterizationBuilder pb = new ParameterizationBuilder();
        BigInteger bigInt5 = BigInteger.valueOf(5);
        for (AkType t: SUPPORTED)
        {
            param(pb, Aggregators.bit_and("bit_and", t).get(), BigInteger.ZERO, bigInt5, Types.valueOf(t.name()));
            param(pb, Aggregators.bit_or("bit_or", t).get(), BigInteger.valueOf(31), bigInt5, Types.valueOf(t.name()));
            param(pb, Aggregators.bit_xor("bit_xor", t).get(), BigInteger.ZERO, bigInt5, Types.valueOf(t.name()));
        }

        return pb.asList();
    }

    private static void param(ParameterizationBuilder pb, Aggregator agg, BigInteger expected1,
            BigInteger expected2, Types type)
    {
        pb.add(agg.toString() + type, agg, expected1, expected2, type);
    }

    @Test
    public void testColumnOfDifferentValues ()
    {
        aggregator = aggregator1;
        expected = expected1;
        assertEquals(expected, type.aggregate_test1(aggregator, holder));
    }

    @Test
    public void testColumnOfSameValues ()
    {
        aggregator = aggregator2;
        expected = expected2;
        assertEquals(expected, type.aggregate_test2(aggregator, holder));
    }

    @OnlyIfNot("alreadyExc()")
    @Test
    public void testNullAnd ()
    {
        aggregator = Aggregators.bit_and("bit_and", AkType.U_BIGINT).get();
        expected = N64;
        testNull();
    }

    @OnlyIfNot("alreadyExc()")
    @Test
    public void testAndOrXor()
    {
        // test and
        aggregator = Aggregators.bit_and("bit_and", AkType.U_BIGINT).get();
        expected = BigInteger.valueOf(1);
        testNonParam();
        
        // test or
        aggregator = Aggregators.bit_or("bit_or", AkType.U_BIGINT).get();
        expected = BigInteger.valueOf(3);
        testNonParam();
        
        // test xor
        aggregator = Aggregators.bit_xor("bit_xor", AkType.U_BIGINT).get();
        expected = BigInteger.valueOf(2);
        testNonParam();
    }
    
    private void testNonParam ()
    {
        holder.clear();
        
        holder.putUBigInt(BigInteger.ONE);
        aggregator.input(holder);
        
        holder.putUBigInt(BigInteger.valueOf(3));
        aggregator.input(holder);
        
        holder.clear();
        holder.putUBigInt(N64);
        aggregator.output(holder);
        
        assertEquals(expected, holder.getUBigInt());
    }
    
    @OnlyIfNot("alreadyExc()")
    @Test
    public void testNullOr ()
    {
        aggregator = Aggregators.bit_or("bit_or", AkType.U_BIGINT).get();
        expected = BigInteger.ZERO;
        testNull();
    }

    @OnlyIfNot("alreadyExc()")
    @Test
    public void testNullXOr ()
    {
        aggregator = Aggregators.bit_xor("bit_xor", AkType.U_BIGINT).get();
        expected = BigInteger.ZERO;
        testNull();
        alreadyExc = true;
    }

    public boolean alreadyExc ()
    {
        return alreadyExc;
    }

    private void testNull ()
    {
        holder.clear();
        aggregator.input(NullValueSource.only());
        holder.expectType(AkType.U_BIGINT);
        aggregator.output(holder);

        assertEquals(expected, holder.getUBigInt());
    }
}
