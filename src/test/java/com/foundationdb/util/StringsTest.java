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

package com.foundationdb.util;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public final class StringsTest {
    @Test
    public void testStringToBytes() {
        ByteSource actual = Strings.parseHex("0xBEEFCAFE");
        int[] expectedInts = { 190, 239, 202, 254 };
        byte[] expected = new byte[expectedInts.length];
        for (int i=0; i < expected.length; ++i) {
            expected[i] = (byte)expectedInts[i];
        }

        assertEquals("bytes", new WrappingByteSource(expected), actual);
    }
    
    @Test
    public void withSpace() {
        ByteSource actual = Strings.parseHex("0x BE EFCA FE");
        int[] expectedInts = { 190, 239, 202, 254 };
        byte[] expected = new byte[expectedInts.length];
        for (int i=0; i < expected.length; ++i) {
            expected[i] = (byte)expectedInts[i];
        }

        assertEquals("bytes", new WrappingByteSource(expected), actual);
    }
    
    @Test
    public void formatMD5() {
        byte[] md5 = {0x01, 0x39, (byte) 0xef, (byte) 0xc1, (byte) 0xe9, (byte) 0x86, 0x22, 0x33, 0x74, 0x3a, 0x75, 0x77, (byte) 0x98, (byte) 0xdd, (byte) 0x9c};
        
        String expected = "0139efc1e9862233743a757798dd9c";
        
        String actual = Strings.formatMD5(md5, true);
        
        assertEquals ("bytes", expected, actual);
        
    }
}