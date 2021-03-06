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

package com.foundationdb.http;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class HttpConductorImplTest {

    @Test
    public void standard() {
        check("/foo/*", "foo");
    }

    @Test
    public void unending() {
        check("/foo-bar", "foo-bar");
    }

    @Test(expected = IllegalPathRequest.class)
    public void badStart() {
        HttpConductorImpl.getContextPathPrefix("no-dash");
    }

    @Test(expected = IllegalPathRequest.class)
    public void empty() {
        HttpConductorImpl.getContextPathPrefix("");
    }

    @Test(expected = IllegalPathRequest.class)
    public void slashAll() {
        HttpConductorImpl.getContextPathPrefix("/*");
    }

    private void check(String full, String prefix) {
        assertEquals(full, prefix, HttpConductorImpl.getContextPathPrefix(full));
    }
}
