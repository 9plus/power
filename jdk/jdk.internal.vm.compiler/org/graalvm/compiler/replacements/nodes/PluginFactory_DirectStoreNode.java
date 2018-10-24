// CheckStyle: stop header check
// CheckStyle: stop line length check
// GENERATED CONTENT - DO NOT EDIT
// GENERATORS: org.graalvm.compiler.replacements.verifier.VerifierAnnotationProcessor, org.graalvm.compiler.replacements.verifier.PluginGenerator
package org.graalvm.compiler.replacements.nodes;

import jdk.vm.ci.meta.ResolvedJavaMethod;
import org.graalvm.compiler.serviceprovider.ServiceProvider;

import org.graalvm.compiler.nodes.ValueNode;
import org.graalvm.compiler.nodes.graphbuilderconf.GraphBuilderContext;
import org.graalvm.compiler.nodes.graphbuilderconf.GeneratedInvocationPlugin;
import org.graalvm.compiler.nodes.graphbuilderconf.InvocationPlugin;
import org.graalvm.compiler.nodes.graphbuilderconf.InvocationPlugins;
import org.graalvm.compiler.nodes.graphbuilderconf.NodeIntrinsicPluginFactory;

@ServiceProvider(NodeIntrinsicPluginFactory.class)
public class PluginFactory_DirectStoreNode implements NodeIntrinsicPluginFactory {

    //        class: org.graalvm.compiler.replacements.nodes.DirectStoreNode
    //       method: storeBoolean(long,boolean,jdk.vm.ci.meta.JavaKind)
    // generated-by: org.graalvm.compiler.replacements.verifier.GeneratedNodeIntrinsicPlugin$ConstructorPlugin
    private static final class DirectStoreNode_storeBoolean extends GeneratedInvocationPlugin {

        @Override
        public boolean execute(GraphBuilderContext b, ResolvedJavaMethod targetMethod, InvocationPlugin.Receiver receiver, ValueNode[] args) {
            ValueNode arg0 = args[0];
            ValueNode arg1 = args[1];
            jdk.vm.ci.meta.JavaKind arg2;
            if (args[2].isConstant()) {
                arg2 = snippetReflection.asObject(jdk.vm.ci.meta.JavaKind.class, args[2].asJavaConstant());
            } else {
                return false;
            }
            org.graalvm.compiler.replacements.nodes.DirectStoreNode node = new org.graalvm.compiler.replacements.nodes.DirectStoreNode(arg0, arg1, arg2);
            b.add(node);
            return true;
        }

        private final org.graalvm.compiler.api.replacements.SnippetReflectionProvider snippetReflection;

        private DirectStoreNode_storeBoolean(InjectionProvider injection) {
            this.snippetReflection = injection.getInjectedArgument(org.graalvm.compiler.api.replacements.SnippetReflectionProvider.class);
        }
    }

    @Override
    public void registerPlugins(InvocationPlugins plugins, InjectionProvider injection) {
        plugins.register(new DirectStoreNode_storeBoolean(injection), org.graalvm.compiler.replacements.nodes.DirectStoreNode.class, "storeBoolean", long.class, boolean.class, jdk.vm.ci.meta.JavaKind.class);
    }
}
