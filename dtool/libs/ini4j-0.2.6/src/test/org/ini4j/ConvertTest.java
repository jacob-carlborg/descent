/*
 * Copyright 2005 [ini4j] Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ini4j;

import java.util.*;
import junit.framework.*;


///CLOVER:OFF

/**
 * JUnit test of Convert class.
 */
public class ConvertTest extends AbstractTestBase
{
    /**
     * Instantiate test.
     *
     * @param testName name of the test
     */
    public ConvertTest(String testName)
    {
        super(testName);
    }
    
    /**
     * Create test suite.
     *
     * @return new test suite
     */
    public static Test suite()
    {
        return new TestSuite(ConvertTest.class);
    }
    
    /**
     * Test of escape method.
     *
     * @throws Exception on error
     */
    public void testEscape() throws Exception
    {
        Map<String,String> data = new HashMap<String,String>();
        
        data.put("simple","simple");
        data.put("Iv\ufffdn","Iv\\ufffdn");
        data.put("1\t2\n3\f","1\\t2\\n3\\f");
        
        for(String from : data.keySet())
        {
            assertEquals(data.get(from), Convert.escape(from));
        }
    }
    
    /**
     * Test of unescape method.
     *
     * @throws Exception on error
     */
    public void testUnescape() throws Exception
    {
        Map<String,String> data = new HashMap<String,String>();
        
        data.put("simple","simple");
        data.put("Iv\\ufffdn","Iv\ufffdn");
        data.put("1\\t2\\n3\\f","1\t2\n3\f");
        data.put("\\=", "=");
        
        for(String from : data.keySet())
        {
            assertEquals(data.get(from), Convert.unescape(from));
        }
        
        // invalid unicode escape mean IllegalArgumentException
        try
        {
            Convert.unescape("\\u98x");
            fail();
        }
        catch (IllegalArgumentException x)
        {
            ;
        }
    }
}
