# jspwiki-cytoscope-plugin

## Introduction#
Do you know cytoscope.js? It is a great javascript library of network diagram drawing. This plugin for JSPWiki can provide network diagram of Wiki site.


##Installation#
Download ... and put into your JSPWiki's WEB-INF/lib directory.
restart tomcat
##Usage#
Place the following line anywhere in a JSPWiki page.

[{CytoscapePlugin ...}]
##Parameters#
page
...default is KM_TOP
depth
...default is 3
Heres an example of how the above image was created:

[{CytoscapePlugin}]
##usage example: Cytoscape-Plugin Test
##You can check source code on GitHub site: https://github.com/tateshitah/jspwiki-cytoscope-plugin

##Solved Issues#
Issue 1 during cytoscape plugin Dev
##Future works and Known Issues#
- Jump to the page with Double click the node.
- change root node with Single click
...
