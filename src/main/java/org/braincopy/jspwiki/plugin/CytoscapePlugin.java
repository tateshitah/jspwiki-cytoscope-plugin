/*

Copyright (c) 2017 Hiroaki Tateshita

Permission is hereby granted, free of charge, to any person obtaining a copy 
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
copies of the Software, and to permit persons to whom the Software is furnished
to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all 
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT 
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION 
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE 
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 */
package org.braincopy.jspwiki.plugin;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import org.apache.wiki.LinkCollector;
import org.apache.wiki.WikiContext;
import org.apache.wiki.WikiEngine;
import org.apache.wiki.WikiPage;
import org.apache.wiki.api.exceptions.PluginException;
import org.apache.wiki.api.plugin.WikiPlugin;
import org.braincopy.Link;

/**
 * @author Hiroaki Tateshita
 *
 */
public class CytoscapePlugin implements WikiPlugin {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.wiki.api.plugin.WikiPlugin#execute(org.apache.wiki.WikiContext,
	 * java.util.Map)
	 */
	public String execute(WikiContext context, Map<String, String> params) throws PluginException {
		String result = "";

		String pagename = "KM_TOP";
		if (params.get("page") != null) {
			pagename = params.get("page");
		}

		int depth = 3;
		if (params.get("depth") != null) {
			try {
				depth = Integer.parseInt(params.get("depth"));
			} catch (NumberFormatException e) {
				result += "depth seems not be number format. It was set 3 as default.\n";
				depth = 3;
			}
		}
		TreeSet<String> nodeSet = new TreeSet<String>();
		TreeSet<Link> edgeSet = new TreeSet<Link>();

		WikiEngine engine = context.getEngine();
		readNodeAndEdge(engine, pagename, nodeSet, edgeSet, depth);

		// result += "hello " + pagename + "<br>\n";
		result += "<style>\n";
		result += "\t#cy {\n";
		result += "\t\twidth: 75%;\n";
		result += "\t\theight: 100%;\n";
		result += "\t\tposition: absolute;\n";
		result += "\t\ttop: 130px;\n";
		result += "\t\tright: 30px;\n";
		result += "\t}\n";
		result += "</style>\n";
		result += "<div id='cy'></div>\n";

		result += "<br /> <br /> <br /> <br /> <br /> <br /> <br />";
		result += "<br /> <br /> <br /> <br /> <br /> <br /> <br /> <br /> <br /> <br />";
		result += "<br /> <br /> <br /> <br /> <br /> <br /> <br /> <br /> <br /> <br />";
		result += "<br /> <br /> <br /> <br /> <br /> <br /> <br /> <br /> \n";

		result += "<script src='https://braincopy.org/WebContent/js/cytoscape.js'></script>\n";
		result += "<script src='https://braincopy.org/WebContent/js/cola.js'></script>\n";
		result += "<script src='https://braincopy.org/WebContent/js/cytoscape-cola.js'></script>\n";
		result += "<script>\n";
		result += "\tvar cy = cytoscape({\n";
		result += "\t\tcontainer: document.getElementById('cy'),\n";
		result += "\t\telements: [\n";

		Iterator<String> localNodesIte = nodeSet.iterator();
		Iterator<Link> localLinksIte = edgeSet.iterator();
		String nodeStr = "";
		while (localNodesIte.hasNext()) {
			nodeStr = localNodesIte.next();
			result += "\t\t\t{ data: { id: '" + nodeStr + "' } },\n";
		}
		Link link = null;
		while (localLinksIte.hasNext()) {
			link = localLinksIte.next();
			result += "\t\t\t{ data: {\n";
			result += "\t\t\t\tid: '_" + link.getName() + "',\n";
			result += "\t\t\t\tsource: '" + link.getSource() + "',\n";
			result += "\t\t\t\ttarget: '" + link.getTarget() + "'}},\n";
		}
		result += "\t\t\t{ data: { id: '" + pagename + "' } }\n";

		result += "\t\t],\n";

		result += "\t\tlayout : {\n";
		result += "\t\t\tname : 'cola',\n";
		result += "padding: 10},\n";

		result += "\t\tstyle: [{\n";
		result += "\t\t\tselector: 'node',\n";
		result += "\t\t\tstyle: {\n";
		result += "\t\t\t\tshape: 'roundrectangle',\n";
		result += "\t\t\t\t'background-color': 'red',\n";
		result += "\t\t\t\tlabel: 'data(id)'\n";
		result += "\t\t\t}\n";
		result += "\t\t}]\n";
		result += "\t});\n";
		result += "</script>\n";

		return result;
	}

	protected String readNodeAndEdge(WikiEngine engine, String pagename, TreeSet<String> nodeSet, TreeSet<Link> edgeSet,
			int depth) {
		String result = "";
		if (!nodeSet.contains(pagename) && depth > 0) {
			nodeSet.add(pagename);
			WikiPage page = engine.getPage(pagename);
			if (page != null) {
				String pagedata = engine.getPureText(page);
				WikiContext targetContext = new WikiContext(engine, page);

				LinkCollector localCollector = new LinkCollector();
				LinkCollector extCollector = new LinkCollector();
				LinkCollector attCollector = new LinkCollector();

				targetContext.setVariable(WikiEngine.PROP_REFSTYLE, "absolute");

				engine.textToHTML(targetContext, pagedata, localCollector, extCollector, attCollector);
				Collection<String> distNameSet = localCollector.getLinks();
				for (String distName : distNameSet) {
					edgeSet.add(new Link(pagename + "2" + distName + distName.hashCode(), pagename, distName));
					result += readNodeAndEdge(engine, distName, nodeSet, edgeSet, depth - 1);
				}
			} else {
				result += pagename + " seem not exist.<br>\n";
			}
		}
		return result;
	}
}
