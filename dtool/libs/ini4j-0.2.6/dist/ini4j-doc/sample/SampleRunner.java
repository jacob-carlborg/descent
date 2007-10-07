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

import junit.framework.*;
import java.util.prefs.Preferences;
import java.io.*;

public class SampleRunner extends TestCase
{
    static
    {
        System.setProperty("java.util.prefs.PreferencesFactory", "org.ini4j.IniPreferencesFactory");
    }
    
    private static final String[] ARGS = {System.getProperty("basedir") + "/src/doc/sample/dwarfs.ini"};
    private static final String[] SAMPLES =
    {
        "ReadStringSample", "ReadPrimitiveSample", "WriteSample", "IniSample", "StreamSample", "DumpSample",
                "BeanSample","NoImportSample","ListenerSample"
    };
    private static final String SEPARATOR = "************";
    
    public SampleRunner(String testName)
    {
        super(testName);
    }
    
    public static Test suite()
    {
        return  new TestSuite(SampleRunner.class);
    }
    
    public void testSamples() throws Exception
    {
        for(String name : SAMPLES)
        {
            System.out.println(SEPARATOR + " " +  name + " " + SEPARATOR);
            Class.forName(name).getDeclaredMethod("main", String[].class).invoke((Object)null,(Object)ARGS);
        }
    }
}
