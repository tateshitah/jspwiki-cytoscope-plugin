package org.braincopy.jspwiki.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.TreeSet;

import org.braincopy.IllegalFileNameException;
import org.braincopy.Link;
import org.braincopy.Picture;
import org.junit.jupiter.api.Test;

//import junit.framework.TestCase;

public class CytoscapePluginTest  {

	/**
	 * 
	 */
	@Test
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

	@Test
	public void testGetNodeDataJson() throws IllegalFileNameException {
		CytoscapePlugin plugin = new CytoscapePlugin();
		TreeSet<PageInformation> nodeSet = new TreeSet<PageInformation>();
		PageInformation pageInfo1 = new PageInformation("a", 1);
		pageInfo1.setPicture(new Picture("test.png"));
		nodeSet.add(pageInfo1);
		PageInformation pageInfo2 = new PageInformation("b", 2);
		nodeSet.add(pageInfo2);
		try {
			String testStr = plugin.getNodeDataJson(nodeSet);
			assertTrue(testStr.startsWith("var"));
			assertTrue(testStr.endsWith("];\n"));
			System.out.print(testStr);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testGetEdgeDataJson() throws IllegalFileNameException {
		CytoscapePlugin plugin = new CytoscapePlugin();
		TreeSet<Link> edgeSet = new TreeSet<Link>();
		PageInformation pageInfo1 = new PageInformation("a", 1);
		pageInfo1.setPicture(new Picture("test.png"));
		PageInformation pageInfo2 = new PageInformation("b", 2);
		PageInformation pageInfo3 = new PageInformation("c", 2);
		Link link1 = new Link("a2b", pageInfo1, pageInfo2);
		edgeSet.add(link1);
		Link link2 = new Link("a2c", pageInfo1, pageInfo3);
		edgeSet.add(link2);
		String testStr = plugin.getEdgeDataJson(edgeSet);
		assertTrue(testStr.startsWith("var"));
		assertTrue(testStr.endsWith("];\n"));
		System.out.print(testStr);
	}

	@Test
	public void testGetLayoutParam() {
		CytoscapePlugin plugin = new CytoscapePlugin();
		String testStr = "";
		testStr = plugin.getLayoutParam("cola");
		System.out.println(testStr);
		testStr = plugin.getLayoutParam("cose");
		System.out.print(testStr);
	}

	@Test
	public void testGetRecursiveFunction() {
		CytoscapePlugin plugin = new CytoscapePlugin();
		String testStr = "";
		testStr = plugin.getRecursiveFunction();
		System.out.print(testStr);
	}
}
