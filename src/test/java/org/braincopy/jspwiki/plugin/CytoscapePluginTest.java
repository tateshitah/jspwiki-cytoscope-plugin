package org.braincopy.jspwiki.plugin;

import java.util.TreeSet;

import junit.framework.TestCase;

public class CytoscapePluginTest extends TestCase {

	/**
	 * 
	 */
	public void testCloserPageExist() {

		TreeSet<PageInformation> nodeSet = new TreeSet<PageInformation>();
		PageInformation pageInfo1 = new PageInformation("a", 1);
		nodeSet.add(pageInfo1);
		PageInformation pageInfo2 = new PageInformation("b", 2);
		nodeSet.add(pageInfo2);

		CytoscapePlugin plugin = new CytoscapePlugin();
		PageInformation tmpPageInfo = new PageInformation("a", 2);
		assertEquals(true, plugin.checkAndAdd(nodeSet, tmpPageInfo));

		tmpPageInfo = new PageInformation("a", 1);
		assertEquals(true, plugin.checkAndAdd(nodeSet, tmpPageInfo));

		tmpPageInfo = new PageInformation("b", 2);
		assertEquals(true, plugin.checkAndAdd(nodeSet, tmpPageInfo));

		tmpPageInfo = new PageInformation("b", 1);
		assertEquals(false, plugin.checkAndAdd(nodeSet, tmpPageInfo));

		tmpPageInfo = new PageInformation("c", 2);
		assertEquals(false, plugin.checkAndAdd(nodeSet, tmpPageInfo));

	}

}
