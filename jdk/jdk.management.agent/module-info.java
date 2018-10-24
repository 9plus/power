/*
 * Copyright (c) 2017, Oracle and/or its affiliates. All rights reserved.
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
 * Defines the JMX management agent.
 *
 * <p> This module allows a Java Virtual Machine to be monitored and managed
 * via JMX API.  See more information from the
 * {@extLink monitoring_and_management_using_jmx_technology
 * Monitoring and Management Using JMX} guide.
 *
 * @moduleGraph
 * @since 9
 */
module jdk.management.agent {
    // source file: file:///scratch/mesos/slaves/9190d864-6621-4810-ba08-d8d8c75ba674-S34/frameworks/1735e8a2-a1db-478c-8104-60c8b0af87dd-0196/executors/f87b4890-2412-429e-bdfd-83788a246cfb/runs/8f4f187f-165d-4027-8162-c5cc52d7c3d8/workspace/open/src/jdk.management.agent/share/classes/module-info.java
    //              file:///scratch/mesos/slaves/9190d864-6621-4810-ba08-d8d8c75ba674-S34/frameworks/1735e8a2-a1db-478c-8104-60c8b0af87dd-0196/executors/f87b4890-2412-429e-bdfd-83788a246cfb/runs/8f4f187f-165d-4027-8162-c5cc52d7c3d8/workspace/closed/src/jdk.management.agent/share/classes/module-info.java.extra
    requires java.management;
    requires java.management.rmi;
    exports jdk.internal.agent to jdk.jconsole;
    exports jdk.internal.agent.spi to jdk.snmp;

    uses jdk.internal.agent.spi.AgentProvider;
}
