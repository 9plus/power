/*
 * Copyright (c) 2014, 2017, Oracle and/or its affiliates. All rights reserved.
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
 *
 *
 */

/**
 * Defines the Java Management Extensions (JMX) API.
 * <P>
 * The JMX API consists of interfaces for monitoring and management of the
 * JVM and other components in the Java runtime.
 *
 * @uses javax.management.remote.JMXConnectorProvider
 * @uses javax.management.remote.JMXConnectorServerProvider
 *
 * @moduleGraph
 * @since 9
 */
module java.management {
    // source file: file:///scratch/mesos/slaves/9190d864-6621-4810-ba08-d8d8c75ba674-S34/frameworks/1735e8a2-a1db-478c-8104-60c8b0af87dd-0196/executors/f87b4890-2412-429e-bdfd-83788a246cfb/runs/8f4f187f-165d-4027-8162-c5cc52d7c3d8/workspace/open/src/java.management/share/classes/module-info.java
    //              file:///scratch/mesos/slaves/9190d864-6621-4810-ba08-d8d8c75ba674-S34/frameworks/1735e8a2-a1db-478c-8104-60c8b0af87dd-0196/executors/f87b4890-2412-429e-bdfd-83788a246cfb/runs/8f4f187f-165d-4027-8162-c5cc52d7c3d8/workspace/closed/src/java.management/share/classes/module-info.java.extra
    exports java.lang.management;
    exports javax.management;
    exports javax.management.loading;
    exports javax.management.modelmbean;
    exports javax.management.monitor;
    exports javax.management.openmbean;
    exports javax.management.relation;
    exports javax.management.remote;
    exports javax.management.timer;
    exports com.sun.jmx.remote.internal to
        java.management.rmi,
        jdk.management.agent;
    exports com.sun.jmx.remote.security to
        java.management.rmi,
        jdk.management.agent;
    exports com.sun.jmx.remote.util to java.management.rmi;
    exports sun.management to
        jdk.jconsole,
        jdk.management,
        jdk.management.agent;
    exports sun.management.counter to jdk.management.agent;
    exports sun.management.counter.perf to jdk.management.agent;
    exports sun.management.spi to
        jdk.internal.vm.compiler.management,
        jdk.management,
        jdk.management.cmm,
        jdk.management.jfr;

    uses javax.management.remote.JMXConnectorProvider;
    uses javax.management.remote.JMXConnectorServerProvider;
    uses sun.management.spi.PlatformMBeanProvider;
    provides javax.security.auth.spi.LoginModule with com.sun.jmx.remote.security.FileLoginModule;
}
