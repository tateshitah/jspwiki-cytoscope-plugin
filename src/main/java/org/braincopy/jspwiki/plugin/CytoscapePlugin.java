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
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.plugin.WikiPlugin;
import org.apache.wiki.attachment.Attachment;
import org.apache.wiki.attachment.AttachmentManager;
import org.braincopy.IllegalFileNameException;
import org.braincopy.Link;
import org.braincopy.Picture;

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

		String pagename_url_param = null;
		pagename_url_param = context.getHttpParameter("target_node");
		if (pagename_url_param != null) {
			pagename = pagename_url_param;
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
		TreeSet<PageInformation> nodeSet = new TreeSet<PageInformation>();
		TreeSet<Link> edgeSet = new TreeSet<Link>();

		WikiEngine engine = context.getEngine();

		try {
			result += readNodeAndEdge(engine, pagename, nodeSet, edgeSet, depth, depth);

			result += "hello " + pagename + "<br>\n";
			result += "total page number: " + nodeSet.size() + "<br/>\n";
			result += "<style>\n";
			result += "\t#cy {\n";
			result += "\t\twidth: 95%;\n";
			result += "\t\theight: 100%;\n";
			result += "\t\tposition: absolute;\n";
			result += "\t\ttop: 30px;\n";
			result += "\t\tleft: 100px;\n";
			result += "\t}\n";
			result += "</style>\n";
			result += "<div id='cy'></div>\n";

			// result += "<br /> <br /> <br /> <br /> <br /> <br /> <br /> <br /> \n";

			// result += "<script src='/personal/scripts/cytoscape.js'></script>\n";
			// result += "<script src='/personal/scripts/cola.js'></script>\n";
			// result += "<script src='/personal/scripts/cytoscape-cola.js'></script>\n";
			result += "<script src='https://braincopy.org/WebContent/js/cytoscape.js'></script>\n";
			result += "<script src='https://braincopy.org/WebContent/js/cola.js'></script>\n";
			result += "<script src='https://braincopy.org/WebContent/js/cytoscape-cola.js'></script>\n";
			result += "<script>\n";
			result += "\tvar cy = cytoscape({\n";
			result += "\t\tcontainer: document.getElementById('cy'),\n";
			result += "\t\telements: [\n";

			Iterator<PageInformation> localNodesIte = nodeSet.iterator();
			Iterator<Link> localLinksIte = edgeSet.iterator();
			PageInformation nodePageInfo = null;
			while (localNodesIte.hasNext()) {
				nodePageInfo = localNodesIte.next();
				result += "\t\t\t{ data: { id: '" + nodePageInfo.getPageName() + "',\n";
				result += "\t\t\t\t group: 'L";
				if (nodePageInfo.getStep() <= 1) {
					result += nodePageInfo.getStep();
					if (nodePageInfo.getPicture() != null) {
						result += "',\n";
						result += "\t\t\t\t pic: 'attach/" + nodePageInfo.getName() + "/"
								+ nodePageInfo.getPicture().getFileName() + "'";
					} else {
						result += "WOP'";
					}
				} else {
					result += nodePageInfo.getStep() + "'";
				}
				result += "} },\n";
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
			result += "\t\t\tmaxSimulationTime: 600000,\n";
			result += "\t\t\tpadding: 10},\n";

			result += "\t\tstyle: [{\n";
			result += "\t\t\tselector: 'node',\n";
			result += "\t\t\tstyle: {\n";
			result += "\t\t\t\tshape: 'roundrectangle',\n";
			result += "\t\t\t\twidth: 20,\n";
			result += "\t\t\t\theight: 20,\n";
			result += "\t\t\t\topacity:0.3,\n";
			result += "\t\t\t\t'background-color': 'blue',\n";
			result += "\t\t\t}\n";
			result += "\t\t},\n";
			result += "\t\t{\n";
			result += "\t\t\tselector: 'node[group=\"L0\"]',\n";
			result += "\t\t\tstyle: {\n";
			result += "\t\t\t\tshape: 'roundrectangle',\n";
			result += "\t\t\t\t'background-color': 'blue',\n";
			result += "\t\t\t\t'background-image': 'data(pic)',\n";
			result += "\t\t\t\t'background-fit': 'contain',\n";
			result += "\t\t\t\twidth: 100,\n";
			result += "\t\t\t\theight: 100,\n";
			result += "\t\t\t\topacity:0.8,\n";
			result += "\t\t\t\t'font-size': 30,\n";
			result += "\t\t\t\tlabel: 'data(id)'\n";
			result += "\t\t\t}\n";
			result += "\t\t},\n";
			result += "\t\t{\n";
			result += "\t\t\tselector: 'node[group=\"L0WOP\"]',\n";
			result += "\t\t\tstyle: {\n";
			result += "\t\t\t\tshape: 'roundrectangle',\n";
			result += "\t\t\t\t'background-color': 'blue',\n";
			result += "\t\t\t\twidth: 100,\n";
			result += "\t\t\t\theight: 100,\n";
			result += "\t\t\t\topacity:0.8,\n";
			result += "\t\t\t\t'font-size': 30,\n";
			result += "\t\t\t\tlabel: 'data(id)'\n";
			result += "\t\t\t}\n";
			result += "\t\t},\n";
			result += "\t\t{\n";
			result += "\t\t\tselector: 'node[group=\"L1\"]',\n";
			result += "\t\t\tstyle: {\n";
			result += "\t\t\t\tshape: 'roundrectangle',\n";
			result += "\t\t\t\t'background-color': 'blue',\n";
			result += "\t\t\t\t'background-image': 'data(pic)',\n";
			result += "\t\t\t\t'background-fit': 'contain',\n";
			result += "\t\t\t\twidth: 60,\n";
			result += "\t\t\t\theight: 60,\n";
			result += "\t\t\t\topacity:0.7,\n";
			result += "\t\t\t\t'font-size': 20,\n";
			result += "\t\t\t\tlabel: 'data(id)'\n";
			result += "\t\t\t}\n";
			result += "\t\t},\n";
			result += "\t\t{\n";
			result += "\t\t\tselector: 'node[group=\"L1WOP\"]',\n";
			result += "\t\t\tstyle: {\n";
			result += "\t\t\t\tshape: 'roundrectangle',\n";
			result += "\t\t\t\t'background-color': 'blue',\n";
			result += "\t\t\t\twidth: 60,\n";
			result += "\t\t\t\theight: 60,\n";
			result += "\t\t\t\topacity:0.7,\n";
			result += "\t\t\t\t'font-size': 20,\n";
			result += "\t\t\t\tlabel: 'data(id)'\n";
			result += "\t\t\t}\n";
			result += "\t\t},\n";
			result += "\t\t{\n";
			result += "\t\t\tselector: 'node[group=\"L2\"]',\n";
			result += "\t\t\tstyle: {\n";
			result += "\t\t\t\tshape: 'roundrectangle',\n";
			result += "\t\t\t\t'background-color': 'blue',\n";
			result += "\t\t\t\twidth: 40,\n";
			result += "\t\t\t\theight: 40,\n";
			result += "\t\t\t\topacity:0.6,\n";
			result += "\t\t\t\t'font-size': 10,\n";
			result += "\t\t\t\tlabel: 'data(id)'\n";
			result += "\t\t\t}\n";
			result += "\t\t},\n";
			result += "\t\t{\n";
			result += "\t\t\tselector: 'edge',\n";
			result += "\t\t\tstyle: {\n";
			result += "\t\t\t\t'width':1,\n";
			result += "\t\t\t\t'line-color': '#c0c',\n";
			result += "\t\t\t\t'target-arrow-shape': 'triangle',\n";
			result += "\t\t\t\t'target-arrow-color': '#c0c',\n";
			result += "\t\t\t\t'curve-style': 'segments'\n";
			result += "\t\t\t}\n";
			result += "\t\t}]\n";
			result += "\t});\n";
			result += "\tcy.on('click', 'node', function(evt) {\n";
			result += "\t\tvar arg = new Object;\n";
			result += "\t\tvar pair=location.search.substring(1).split('&');\n";
			result += "\t\tfor(var i=0;pair[i];i++) {\n";
			result += "\t\t\tvar kv = pair[i].split('=');\n";
			result += "\t\t\targ[kv[0]]=kv[1];\n";
			result += "\t\t}\n";
			result += "\t\twindow.location.href='Wiki.jsp?page='+arg.page+'&target_node='+this.id();\n";
			result += "\t});\n";
			result += "</script>\n";

		} catch (ProviderException e) {
			result += "something happens related to Attachment Issues.";
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * @param engine
	 * @param pagename
	 * @param nodeSet
	 * @param edgeSet
	 * @param depth
	 * @return
	 * @throws ProviderException
	 */
	protected String readNodeAndEdge(WikiEngine engine, String pagename, TreeSet<PageInformation> nodeSet,
			TreeSet<Link> edgeSet, int depth, int max_depth) throws ProviderException {
		String result = "";
		AttachmentManager attachmentManager = engine.getAttachmentManager();
		PageInformation tmpPageInfo = new PageInformation(pagename, max_depth - depth);
		if (depth > 0) {
			checkAndAdd(nodeSet, tmpPageInfo);
			WikiPage page = engine.getPage(pagename);
			if (page != null) {
				String pagedata = engine.getPureText(page);
				WikiContext targetContext = new WikiContext(engine, page);

				@SuppressWarnings("rawtypes")
				Collection list = attachmentManager.listAttachments(page);
				if (list.size() > 0) {
					Object tempObj = list.iterator().next();
					if (tempObj instanceof Attachment) {
						String fileName = ((Attachment) tempObj).getFileName();
						if (Picture.isPictureFileName(fileName)) {
							try {
								tmpPageInfo.setPicture(new Picture(fileName));
							} catch (IllegalFileNameException e) {
								// do nothing
							}
						}
					}
				}

				LinkCollector localCollector = new LinkCollector();
				LinkCollector extCollector = new LinkCollector();
				LinkCollector attCollector = new LinkCollector();

				targetContext.setVariable(WikiEngine.PROP_REFSTYLE, "absolute");

				engine.textToHTML(targetContext, pagedata, localCollector, extCollector, attCollector);
				Collection<String> distNameSet = localCollector.getLinks();
				for (String distName : distNameSet) {
					edgeSet.add(new Link(pagename + "2" + distName + distName.hashCode(), pagename, distName));
					result += readNodeAndEdge(engine, distName, nodeSet, edgeSet, depth - 1, max_depth);
				}
			} else {
				result += pagename + " seem not exist.<br>\n";
			}
			// checkAndAdd(nodeSet, tmpPageInfo);
		}
		return result;
	}

	/**
	 * If this method return false, you should add the pageInformation object to
	 * nodeSet.
	 * 
	 * @param nodeSet
	 * @param checkingPageInfo
	 * @return
	 */
	protected boolean checkAndAdd(TreeSet<PageInformation> nodeSet, PageInformation checkingPageInfo) {
		boolean result = false;
		for (PageInformation pageInfo : nodeSet) {
			if (pageInfo.getPageName().equals(checkingPageInfo.getPageName())) {
				if (pageInfo.getStep() <= checkingPageInfo.getStep()) {
					result = true;
				} else {
					nodeSet.remove(pageInfo);
				}
				break;
			}
		}
		if (!result)
			nodeSet.add(checkingPageInfo);
		return result;
	}
}
