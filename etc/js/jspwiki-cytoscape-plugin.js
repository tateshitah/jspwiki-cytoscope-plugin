const layout_param_cose = {
		name : 'cose',
		idealEdgeLength : 100,
		nodeOverlap : 20,
		refresh : 20,
		fit : true,
		padding : 30,
		randomize : false,
		componentSpacing : 100,
		nodeRepulsion : 400000,
		edgeElasticity : 100,
		nestingFactor : 5,
		gravity : 80,
		numIter : 1000,
		initialTemp : 200,
		coolingFactor : 0.95,
		minTemp : 1.0
};
const layout_param_cola = {
	name : 'cola',
	maxSimulationTime: 600000,
	padding: 10
};
let cy = cytoscape({
	container : document.getElementById('cy'),
});
cy.style([ {
		selector : 'node',
		style : {
			shape : 'roundrectangle',
			width : 20,
			height : 20,
			opacity : 0.3,
			'background-color' : 'blue',
		}
	}, {
		selector : 'node[group="L0"]',
		style : {
			shape : 'roundrectangle',
			'background-color' : 'blue',
			'background-image' : 'data(pic)',
			'background-fit' : 'contain',
			width : 100,
			height : 100,
			opacity : 0.8,
			'font-size' : 30,
			label : 'data(id)'
		}
	}, {
		selector : 'node[group="L0WOP"]',
		style : {
			shape : 'roundrectangle',
			'background-color' : 'blue',
			width : 100,
			height : 100,
			opacity : 0.8,
			'font-size' : 30,
			label : 'data(id)'
		}
	}, {
		selector : 'node[group="L1"]',
		style : {
			shape : 'roundrectangle',
			'background-color' : 'blue',
			'background-image' : 'data(pic)',
			'background-fit' : 'contain',
			width : 60,
			height : 60,
			opacity : 0.7,
			'font-size' : 20,
			label : 'data(id)'
		}
	}, {
		selector : 'node[group="L1WOP"]',
		style : {
			shape : 'roundrectangle',
			'background-color' : 'blue',
			width : 60,
			height : 60,
			opacity : 0.7,
			'font-size' : 20,
			label : 'data(id)'
		}
	}, {
		selector : 'node[group="L2"]',
		style : {
			shape : 'roundrectangle',
			'background-color' : 'blue',
			width : 40,
			height : 40,
			opacity : 0.6,
			'font-size' : 10,
			label : 'data(id)'
		}
	}, {
		selector : 'edge',
		style : {
			'width' : 1,
			'line-color' : '#c0c',
			'target-arrow-shape' : 'triangle',
			'target-arrow-color' : '#c0c',
			'curve-style' : 'segments'
		}
	} ]
);
cy.on('tap', 'node', function(evt) {
	data_array = [];
	for (let i = 0; i < node_array.length; i++) {
		node_array[i].data['group'] = null;
	}
	cy.remove('node');
	cy
			.add(readNodeAndEdge(data_array, node_array, edge_array, this.id(),
					2, 2));
	var layout = cy.layout(layout_param);
	layout.run();
});
cy.on('cxttap', 'node', function(evt) {
	window.location.href = 'Wiki.jsp?page=' + this.id();
});
function readNodeAndEdge(data_array, node_array, edge_array, target, depth,
		depth_origin) {
	for (let i = 0; i < node_array.length; i++) {
		if (node_array[i].data.id == target) {
			if (node_array[i].data['group'] == null) {
				if (depth_origin - depth < 2) {
					if (node_array[i].data.pic == null) {
						node_array[i].data['group'] = 'L'
								+ (depth_origin - depth) + 'WOP';
					} else {
						node_array[i].data['group'] = 'L'
								+ (depth_origin - depth) + 'WOP';
					}
				} else {
					node_array[i].data['group'] = 'L' + (depth_origin - depth);
				}
			}
			data_array.push({
				group : 'nodes',
				data : node_array[i].data
			});
			if (depth > 0) {
				for (let j = 0; j < edge_array.length; j++) {
					if (edge_array[j].data.source == target) {
						readNodeAndEdge(data_array, node_array, edge_array,
								edge_array[j].data.target, depth - 1,
								depth_origin);
						data_array.push({
							group : 'edges',
							data : edge_array[j].data
						});
					}
				}
				break;
			}
		}
	}
	return data_array;
}
