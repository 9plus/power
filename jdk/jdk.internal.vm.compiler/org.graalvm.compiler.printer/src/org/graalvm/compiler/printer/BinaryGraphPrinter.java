/*
 * Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package org.graalvm.compiler.printer;

import static org.graalvm.compiler.graph.Edges.Type.Inputs;
import static org.graalvm.compiler.graph.Edges.Type.Successors;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.graalvm.compiler.api.replacements.SnippetReflectionProvider;
import org.graalvm.compiler.bytecode.Bytecode;
import org.graalvm.compiler.core.common.cfg.BlockMap;
import org.graalvm.compiler.debug.DebugContext;
import org.graalvm.compiler.debug.DebugOptions;
import org.graalvm.compiler.graph.CachedGraph;
import org.graalvm.compiler.graph.Edges;
import org.graalvm.compiler.graph.Graph;
import org.graalvm.compiler.graph.InputEdges;
import org.graalvm.compiler.graph.Node;
import org.graalvm.compiler.graph.NodeClass;
import org.graalvm.compiler.graph.NodeMap;
import org.graalvm.compiler.graph.NodeSourcePosition;
import org.graalvm.compiler.nodes.AbstractBeginNode;
import org.graalvm.compiler.nodes.AbstractEndNode;
import org.graalvm.compiler.nodes.AbstractMergeNode;
import org.graalvm.compiler.nodes.ConstantNode;
import org.graalvm.compiler.nodes.ControlSinkNode;
import org.graalvm.compiler.nodes.ControlSplitNode;
import org.graalvm.compiler.nodes.FixedNode;
import org.graalvm.compiler.nodes.PhiNode;
import org.graalvm.compiler.nodes.ProxyNode;
import org.graalvm.compiler.nodes.StructuredGraph;
import org.graalvm.compiler.nodes.VirtualState;
import org.graalvm.compiler.nodes.cfg.Block;
import org.graalvm.compiler.nodes.cfg.ControlFlowGraph;
import org.graalvm.compiler.nodes.util.JavaConstantFormattable;
import org.graalvm.compiler.phases.schedule.SchedulePhase;
import org.graalvm.graphio.GraphBlocks;
import org.graalvm.graphio.GraphElements;
import org.graalvm.graphio.GraphOutput;
import org.graalvm.graphio.GraphStructure;
import org.graalvm.graphio.GraphTypes;

import jdk.vm.ci.meta.ResolvedJavaField;
import jdk.vm.ci.meta.ResolvedJavaMethod;
import jdk.vm.ci.meta.ResolvedJavaType;
import jdk.vm.ci.meta.Signature;

public class BinaryGraphPrinter implements
                GraphStructure<BinaryGraphPrinter.GraphInfo, Node, NodeClass<?>, Edges>,
                GraphBlocks<BinaryGraphPrinter.GraphInfo, Block, Node>,
                GraphElements<ResolvedJavaMethod, ResolvedJavaField, Signature, NodeSourcePosition>,
                GraphTypes, GraphPrinter {
    private final SnippetReflectionProvider snippetReflection;
    private final GraphOutput<BinaryGraphPrinter.GraphInfo, ResolvedJavaMethod> output;

    public BinaryGraphPrinter(DebugContext ctx, SnippetReflectionProvider snippetReflection) throws IOException {
        this.output = ctx.buildOutput(GraphOutput.newBuilder(this).protocolVersion(5, 0).blocks(this).elements(this).types(this));
        this.snippetReflection = snippetReflection;
    }

    @Override
    public SnippetReflectionProvider getSnippetReflectionProvider() {
        return snippetReflection;
    }

    @Override
    public void beginGroup(DebugContext debug, String name, String shortName, ResolvedJavaMethod method, int bci, Map<Object, Object> properties) throws IOException {
        output.beginGroup(new GraphInfo(debug, null), name, shortName, method, bci, DebugContext.addVersionProperties(properties));
    }

    @Override
    public void endGroup() throws IOException {
        output.endGroup();
    }

    @Override
    public void close() {
        output.close();
    }

    @Override
    public ResolvedJavaMethod method(Object object) {
        if (object instanceof Bytecode) {
            return ((Bytecode) object).getMethod();
        } else if (object instanceof ResolvedJavaMethod) {
            return ((ResolvedJavaMethod) object);
        } else {
            return null;
        }
    }

    @Override
    public Node node(Object obj) {
        return obj instanceof Node ? (Node) obj : null;
    }

    @Override
    public NodeClass<?> nodeClass(Object obj) {
        if (obj instanceof NodeClass<?>) {
            return (NodeClass<?>) obj;
        }
        return null;
    }

    @Override
    public NodeClass<?> classForNode(Node node) {
        return node.getNodeClass();
    }

    @Override
    public Object nodeClassType(NodeClass<?> node) {
        return node.getJavaClass();
    }

    @Override
    public String nameTemplate(NodeClass<?> nodeClass) {
        return nodeClass.getNameTemplate();
    }

    @Override
    public final GraphInfo graph(GraphInfo currrent, Object obj) {
        if (obj instanceof Graph) {
            return new GraphInfo(currrent.debug, (Graph) obj);
        } else if (obj instanceof CachedGraph) {
            return new GraphInfo(currrent.debug, ((CachedGraph<?>) obj).getReadonlyCopy());
        } else {
            return null;
        }
    }

    @Override
    public int nodeId(Node n) {
        return getNodeId(n);
    }

    @Override
    public Edges portInputs(NodeClass<?> nodeClass) {
        return nodeClass.getEdges(Inputs);
    }

    @Override
    public Edges portOutputs(NodeClass<?> nodeClass) {
        return nodeClass.getEdges(Successors);
    }

    @SuppressWarnings("deprecation")
    private static int getNodeId(Node node) {
        return node == null ? -1 : node.getId();
    }

    @Override
    public List<Node> blockNodes(GraphInfo info, Block block) {
        List<Node> nodes = info.blockToNodes.get(block);
        if (nodes == null) {
            return null;
        }
        List<Node> extraNodes = new LinkedList<>();
        for (Node node : nodes) {
            findExtraNodes(node, extraNodes);
        }
        extraNodes.removeAll(nodes);
        extraNodes.addAll(0, nodes);
        return extraNodes;
    }

    @Override
    public int blockId(Block sux) {
        return sux.getId();
    }

    @Override
    public List<Block> blockSuccessors(Block block) {
        return Arrays.asList(block.getSuccessors());
    }

    @Override
    public Iterable<Node> nodes(GraphInfo info) {
        return info.graph.getNodes();
    }

    @Override
    public int nodesCount(GraphInfo info) {
        return info.graph.getNodeCount();
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void nodeProperties(GraphInfo info, Node node, Map<String, Object> props) {
        node.getDebugProperties((Map) props);
        Graph graph = info.graph;
        ControlFlowGraph cfg = info.cfg;
        NodeMap<Block> nodeToBlocks = info.nodeToBlocks;
        if (cfg != null && DebugOptions.PrintGraphProbabilities.getValue(graph.getOptions()) && node instanceof FixedNode) {
            try {
                props.put("probability", cfg.blockFor(node).probability());
            } catch (Throwable t) {
                props.put("probability", 0.0);
                props.put("probability-exception", t);
            }
        }

        try {
            props.put("NodeCost-Size", node.estimatedNodeSize());
            props.put("NodeCost-Cycles", node.estimatedNodeCycles());
        } catch (Throwable t) {
            props.put("node-cost-exception", t.getMessage());
        }

        if (nodeToBlocks != null) {
            Object block = getBlockForNode(node, nodeToBlocks);
            if (block != null) {
                props.put("node-to-block", block);
            }
        }

        if (node instanceof ControlSinkNode) {
            props.put("category", "controlSink");
        } else if (node instanceof ControlSplitNode) {
            props.put("category", "controlSplit");
        } else if (node instanceof AbstractMergeNode) {
            props.put("category", "merge");
        } else if (node instanceof AbstractBeginNode) {
            props.put("category", "begin");
        } else if (node instanceof AbstractEndNode) {
            props.put("category", "end");
        } else if (node instanceof FixedNode) {
            props.put("category", "fixed");
        } else if (node instanceof VirtualState) {
            props.put("category", "state");
        } else if (node instanceof PhiNode) {
            props.put("category", "phi");
        } else if (node instanceof ProxyNode) {
            props.put("category", "proxy");
        } else {
            if (node instanceof ConstantNode) {
                ConstantNode cn = (ConstantNode) node;
                updateStringPropertiesForConstant((Map) props, cn);
            }
            props.put("category", "floating");
        }
        if (getSnippetReflectionProvider() != null) {
            for (Map.Entry<String, Object> prop : props.entrySet()) {
                if (prop.getValue() instanceof JavaConstantFormattable) {
                    props.put(prop.getKey(), ((JavaConstantFormattable) prop.getValue()).format(this));
                }
            }
        }
    }

    private Object getBlockForNode(Node node, NodeMap<Block> nodeToBlocks) {
        if (nodeToBlocks.isNew(node)) {
            return "NEW (not in schedule)";
        } else {
            Block block = nodeToBlocks.get(node);
            if (block != null) {
                return block.getId();
            } else if (node instanceof PhiNode) {
                return getBlockForNode(((PhiNode) node).merge(), nodeToBlocks);
            }
        }
        return null;
    }

    private static void findExtraNodes(Node node, Collection<? super Node> extraNodes) {
        if (node instanceof AbstractMergeNode) {
            AbstractMergeNode merge = (AbstractMergeNode) node;
            for (PhiNode phi : merge.phis()) {
                extraNodes.add(phi);
            }
        }
    }

    @Override
    public boolean nodeHasPredecessor(Node node) {
        return node.predecessor() != null;
    }

    @Override
    public List<Block> blocks(GraphInfo graph) {
        return graph.blocks;
    }

    @Override
    public void print(DebugContext debug, Graph graph, Map<Object, Object> properties, int id, String format, Object... args) throws IOException {
        output.print(new GraphInfo(debug, graph), properties, id, format, args);
    }

    @Override
    public int portSize(Edges port) {
        return port.getCount();
    }

    @Override
    public boolean edgeDirect(Edges port, int index) {
        return index < port.getDirectCount();
    }

    @Override
    public String edgeName(Edges port, int index) {
        return port.getName(index);
    }

    @Override
    public Object edgeType(Edges port, int index) {
        return ((InputEdges) port).getInputType(index);
    }

    @Override
    public Collection<? extends Node> edgeNodes(GraphInfo graph, Node node, Edges port, int i) {
        if (i < port.getDirectCount()) {
            Node single = Edges.getNode(node, port.getOffsets(), i);
            return Collections.singletonList(single);
        } else {
            return Edges.getNodeList(node, port.getOffsets(), i);
        }
    }

    @Override
    public Object enumClass(Object enumValue) {
        if (enumValue instanceof Enum) {
            return enumValue.getClass();
        }
        return null;
    }

    @Override
    public int enumOrdinal(Object obj) {
        if (obj instanceof Enum<?>) {
            return ((Enum<?>) obj).ordinal();
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String[] enumTypeValues(Object clazz) {
        if (clazz instanceof Class<?>) {
            Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) clazz;
            Enum<?>[] constants = enumClass.getEnumConstants();
            if (constants != null) {
                String[] names = new String[constants.length];
                for (int i = 0; i < constants.length; i++) {
                    names[i] = constants[i].name();
                }
                return names;
            }
        }
        return null;
    }

    @Override
    public String typeName(Object obj) {
        if (obj instanceof Class<?>) {
            return ((Class<?>) obj).getName();
        }
        if (obj instanceof ResolvedJavaType) {
            return ((ResolvedJavaType) obj).toJavaName();
        }
        return null;
    }

    @Override
    public byte[] methodCode(ResolvedJavaMethod method) {
        return method.getCode();
    }

    @Override
    public int methodModifiers(ResolvedJavaMethod method) {
        return method.getModifiers();
    }

    @Override
    public Signature methodSignature(ResolvedJavaMethod method) {
        return method.getSignature();
    }

    @Override
    public String methodName(ResolvedJavaMethod method) {
        return method.getName();
    }

    @Override
    public Object methodDeclaringClass(ResolvedJavaMethod method) {
        return method.getDeclaringClass();
    }

    @Override
    public int fieldModifiers(ResolvedJavaField field) {
        return field.getModifiers();
    }

    @Override
    public String fieldTypeName(ResolvedJavaField field) {
        return field.getType().toJavaName();
    }

    @Override
    public String fieldName(ResolvedJavaField field) {
        return field.getName();
    }

    @Override
    public Object fieldDeclaringClass(ResolvedJavaField field) {
        return field.getDeclaringClass();
    }

    @Override
    public ResolvedJavaField field(Object object) {
        if (object instanceof ResolvedJavaField) {
            return (ResolvedJavaField) object;
        }
        return null;
    }

    @Override
    public Signature signature(Object object) {
        if (object instanceof Signature) {
            return (Signature) object;
        }
        return null;
    }

    @Override
    public int signatureParameterCount(Signature signature) {
        return signature.getParameterCount(false);
    }

    @Override
    public String signatureParameterTypeName(Signature signature, int index) {
        return signature.getParameterType(index, null).getName();
    }

    @Override
    public String signatureReturnTypeName(Signature signature) {
        return signature.getReturnType(null).getName();
    }

    @Override
    public NodeSourcePosition nodeSourcePosition(Object object) {
        if (object instanceof NodeSourcePosition) {
            return (NodeSourcePosition) object;
        }
        return null;
    }

    @Override
    public ResolvedJavaMethod nodeSourcePositionMethod(NodeSourcePosition pos) {
        return pos.getMethod();
    }

    @Override
    public NodeSourcePosition nodeSourcePositionCaller(NodeSourcePosition pos) {
        return pos.getCaller();
    }

    @Override
    public int nodeSourcePositionBCI(NodeSourcePosition pos) {
        return pos.getBCI();
    }

    @Override
    public StackTraceElement methodStackTraceElement(ResolvedJavaMethod method, int bci, NodeSourcePosition pos) {
        return method.asStackTraceElement(bci);
    }

    static final class GraphInfo {
        final DebugContext debug;
        final Graph graph;
        final ControlFlowGraph cfg;
        final BlockMap<List<Node>> blockToNodes;
        final NodeMap<Block> nodeToBlocks;
        final List<Block> blocks;

        private GraphInfo(DebugContext debug, Graph graph) {
            this.debug = debug;
            this.graph = graph;
            StructuredGraph.ScheduleResult scheduleResult = null;
            if (graph instanceof StructuredGraph) {

                StructuredGraph structuredGraph = (StructuredGraph) graph;
                scheduleResult = structuredGraph.getLastSchedule();
                if (scheduleResult == null) {

                    // Also provide a schedule when an error occurs
                    if (DebugOptions.PrintGraphWithSchedule.getValue(graph.getOptions()) || debug.contextLookup(Throwable.class) != null) {
                        try {
                            SchedulePhase schedule = new SchedulePhase(graph.getOptions());
                            schedule.apply(structuredGraph);
                            scheduleResult = structuredGraph.getLastSchedule();
                        } catch (Throwable t) {
                        }
                    }

                }
            }
            cfg = scheduleResult == null ? debug.contextLookup(ControlFlowGraph.class) : scheduleResult.getCFG();
            blockToNodes = scheduleResult == null ? null : scheduleResult.getBlockToNodesMap();
            nodeToBlocks = scheduleResult == null ? null : scheduleResult.getNodeToBlockMap();
            blocks = cfg == null ? null : Arrays.asList(cfg.getBlocks());
        }
    }

}
