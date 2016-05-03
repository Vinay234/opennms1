/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2016 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2016 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.features.topology.plugins.topo.atlas.info;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.opennms.features.topology.api.GraphContainer;
import org.opennms.features.topology.api.info.InfoPanelItem;
import org.opennms.features.topology.api.topo.EdgeRef;
import org.opennms.features.topology.api.topo.VertexRef;
import org.opennms.features.topology.plugins.topo.atlas.DefaultAtlasEdge;
import org.opennms.features.topology.plugins.topo.atlas.DefaultAtlasVertex;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.interpret.RenderResult;
import com.hubspot.jinjava.interpret.TemplateError;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;

public class GenericInfoPanelItem implements InfoPanelItem {

//    private final MeasurementsWrapper measurementWrapper;
//
//    public static class MeasurementsWrapper {
//
//        private final MyMeasurementsService measurementsService;
//
//        public MeasurementsWrapper(MyMeasurementsService measurementsService) throws IllegalAccessException, InstantiationException {
//            this.measurementsService = measurementsService;
//        }
//
////        public void query() {
////            org.opennms.netmgt.jasper.helper.MeasurementsHelper.getNodeOrNodeSourceDescriptor(String.valueOf($P{nodeid}), $P{foreignsource}, $P{foreignid})]]
////        }
//
//        public void query(String resource, String attribute) {
//            long now = System.currentTimeMillis();
//            long start = now - (30 * 60 * 60 * 1000); // 30 Minutes
//
//            QueryRequest request = new QueryRequest();
//            request.setRelaxed(true);
//            request.setStart(start);
//            request.setEnd(now);
//
//            Source source = new Source();
//            source.setAggregation("AVERAGE");
//            source.setTransient(false);
//            source.setAttribute(attribute);
//            source.setResourceId(resource);
//            source.setLabel(attribute);
//
//            request.getSources().add(source);
//
//            try {
//                QueryResponse query = measurementsService.query(request);
//
//
//            } catch (Exception ex) {
//                throw Throwables.propagate(ex);
//            }
//        }
//    }

    private final Jinjava jinjava;

    public GenericInfoPanelItem() throws InstantiationException, IllegalAccessException {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

            this.jinjava = new Jinjava();
        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
    }

    @Override
    public Component getComponent(GraphContainer container) {
        try {
            Map<String, Object> context = createContext(container);
            RenderResult result = render(getTemplate(), context);
            if (result.hasErrors()) {
                for (TemplateError error : result.getErrors()) { // TODO MVR
                    System.out.println(error.getSeverity() + " " + error.getMessage() + " : " + error.getLineno());
                }
            } else {
                    String renderedTemplate = result.getOutput();
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(renderedTemplate.getBytes());
                    Embedded embedded = new Embedded(null, new StreamResource(new StreamResource.StreamSource() {
                        @Override
                        public InputStream getStream() {
                            ByteArrayInputStream input = new ByteArrayInputStream(renderedTemplate.getBytes());
                            return input;
                        }
                    }, "dummy.html"));
                    embedded.setMimeType("text/html");
                    return embedded;
            }
        } catch (IOException e) {
            e.printStackTrace(); // TODO MVR
        }
        return null;
    }

    @Override
    public boolean contributesTo(GraphContainer container) {
        try {
            RenderResult result = render(getTemplate(), createContext(container));
            String visible = (String) result.getContext().getOrDefault("visible", "false");
            return Boolean.valueOf(visible);
        } catch (IOException e) {
            throw Throwables.propagate(e); // TODO MVR
        }
    }

    @Override
    public String getTitle(GraphContainer container) {
        try {
            RenderResult result = render(getTemplate(), createContext(container));
            return (String) result.getContext().get("title", "No Title defined");
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public int getOrder() {
        return 0; // TODO make configurable...
    }

    private Map<String, Object> createContext(GraphContainer container) {
        Map<String, Object> context = Maps.newHashMap();

        List<EdgeRef> selectedEdgeRefs = container.getSelectionManager().getSelectedEdgeRefs().stream()
                .filter(e -> e instanceof DefaultAtlasEdge)
                .collect(Collectors.toList());
        if (selectedEdgeRefs.size() == 1) {
            DefaultAtlasEdge edge = (DefaultAtlasEdge) selectedEdgeRefs.get(0);
            // TODO generalize
            HashMap<String, Object> edgeProperties = edge.getProperties();
            edgeProperties.put("LOGICAL_SITE_A", "CH33XC065-MW");
            edgeProperties.put("PROTECTION_SCHEME", "2+0");
            edgeProperties.put("LOGICAL_SITE_Z", "CH73Xc109-MW");
            edgeProperties.put("A_ESTIMATED_RSL_DBM", "TODO");
            edgeProperties.put("TOTAL_LINK_CAPACITY", "214");
            edgeProperties.put("RADIO_MODEL_A", "HP Quantum ODU Radio");
            edgeProperties.put("CLEARVISION_LINK_ID", null);
            edgeProperties.put("MICROWAVE_PATH_NAME", 101869801);
            edgeProperties.put("PATH_LENGTH_MILES", 6);
            edgeProperties.put("TRANSMIT_FREQ_A", "10995 1st MW ch  TODO 2nd MW ch");
            edgeProperties.put("TRANSMIT_FREQ_Z", "11485 1st MW ch  TODO 2nd MW ch");
            edgeProperties.put("DESIGNED_TX_MOD_TYPE", "32 QAM");

            context.put("edge", edgeProperties);
        }
        List<VertexRef> selectedVertexRefs = container.getSelectionManager().getSelectedVertexRefs().stream()
                .filter(v -> v instanceof DefaultAtlasVertex)
                .collect(Collectors.toList());
        if (selectedVertexRefs.size() == 1) {
            DefaultAtlasVertex vertex = (DefaultAtlasVertex) selectedVertexRefs.get(0);
            context.put("vertex", vertex.getProperties());
        }

//        context.put("measurement", measurementWrapper);

        return context;
    }

    private RenderResult render(String template, Map<String, Object> context) {
        RenderResult renderResult = jinjava.renderForResult(template, context);
        return renderResult;
    }

    private String getTemplate() throws IOException {
        String template = Resources.toString(Resources.getResource("/test1.html"), Charsets.UTF_8);
        return template;
    }
}
