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

package org.ini4j.addon;

import java.io.*;
import java.util.*;
import junit.framework.*;
import java.util.prefs.*;

import org.ini4j.*;

///CLOVER:OFF

/**
 * JUnit test of PreferencesWrapper class.
 */
public class PreferencesWrapperTest extends AbstractTestBase
{
    public static final String DOC = "doc";
    public static final String HAPPY = "happy";
    
    public static final String OPTION = "height";
    
    /**
     * Instantiate test.
     *
     * @param testName name of the test
     */
    public PreferencesWrapperTest(String testName)
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
        return new TestSuite(PreferencesWrapperTest.class);
    }
    
    static class Listener implements PreferenceChangeListener, NodeChangeListener
    {
	PreferenceChangeEvent pce;
	NodeChangeEvent nce;
	
	public void childAdded(NodeChangeEvent event)
	{
	    nce = event;
	}
	
	public void childRemoved(NodeChangeEvent event)
	{
	    nce = event;
	}
	
	public void preferenceChange(PreferenceChangeEvent event)
	{
	    pce = event;
	}
    }
    
    /**
     * Test of all methods.
     *
     * @throws Exception on error
     */
    public void testCalls() throws Exception
    {
	String value;
	
        Preferences root = new IniPreferences(loadDwarfs());
	Preferences toor = new PreferencesWrapper(root);
	
	Preferences peer = root.node(DOC);
	Preferences pref = new PreferencesWrapper(peer);
	
	value = root.node(HAPPY).get(OPTION, null);
	assertNotNull(value);
	
	// keys
	assertTrue(Arrays.equals(pref.keys(), peer.keys()));
	
	// put-get
	pref.put(OPTION, value);
	assertEquals(value, pref.get(OPTION, null));
	assertEquals(value, peer.get(OPTION, null));
	
	// remove
	pref.remove(OPTION);
	assertNull(pref.get(OPTION, null));
	pref.put(OPTION, value);
	
	// clear
	pref.clear();
	assertEquals(0, pref.keys().length);
	pref.put(OPTION, value);
	
	// putInt-getInt
	peer.remove(OPTION);
	pref.putInt(OPTION, 1);
	assertEquals(1, pref.getInt(OPTION, 0));
	assertEquals(1, peer.getInt(OPTION, 0));
	
	// putLong-getLong
	peer.remove(OPTION);
	pref.putLong(OPTION, 1L);
	assertEquals(1L, pref.getLong(OPTION, 0L));
	assertEquals(1L, peer.getLong(OPTION, 0L));
        
	// putFloat-getFloat
	peer.remove(OPTION);
	pref.putFloat(OPTION, 1.0f);
	assertEquals(1.0f, pref.getFloat(OPTION, 1.0f));
	assertEquals(1.0f, peer.getFloat(OPTION, 1.0f));
	
	// putDouble-getDouble
	peer.remove(OPTION);
	pref.putDouble(OPTION, 1.0);
	assertEquals(1.0, pref.getDouble(OPTION, 1.0));
	assertEquals(1.0, peer.getDouble(OPTION, 1.0));

	// putBoolean-getBoolean
	peer.remove(OPTION);
	pref.putBoolean(OPTION, true);
	assertTrue(pref.getBoolean(OPTION, false));
	assertTrue(peer.getBoolean(OPTION, false));

	// putByteArray-getByteArray
	peer.remove(OPTION);
	pref.putByteArray(OPTION, OPTION.getBytes());
	assertTrue(Arrays.equals(OPTION.getBytes(), pref.getByteArray(OPTION, null)));
	assertTrue(Arrays.equals(OPTION.getBytes(), peer.getByteArray(OPTION, null)));
	
	// childrenNames
	assertTrue(Arrays.equals(root.childrenNames(), toor.childrenNames()));
	
	// parent
	assertEquals(peer.parent(), pref.parent());
	
	// node
	assertEquals(root.node(DOC), toor.node(DOC));
	
	assertTrue(toor.nodeExists(DOC));
	
	assertEquals(DOC, pref.name());
	
	assertEquals(peer.absolutePath(), pref.absolutePath());
	
	assertEquals(peer.isUserNode(), pref.isUserNode());
	
	assertEquals(peer.toString(), pref.toString());
	
	pref.exportNode(System.out);
	pref.exportSubtree(System.out);
	
	toor.flush();
	toor.sync();
	
	Listener listener = new Listener();
	
	toor.addNodeChangeListener(listener);
	pref.addPreferenceChangeListener(listener);
	
	pref.clear();
	pref.putInt(OPTION, 1);
	
	// XXX in some Java implementation (ie: Sun JDK 1.4) Preferences events 
	// generated on other thread, and should wait a bit for that thread.
	// Sorry, it makes this test very crazy.....
	Thread.currentThread().sleep(1000);
	
	pref.removePreferenceChangeListener(listener);
	pref.putInt(OPTION+OPTION, 1);
	
	pref.removeNode();
	assertFalse(root.nodeExists(DOC));
	pref = toor.node(DOC);
	toor.removeNodeChangeListener(listener);
	toor.node(DOC+DOC);

	assertEquals(OPTION, listener.pce.getKey());
	assertEquals(DOC, listener.nce.getChild().name());
    }
    
}
