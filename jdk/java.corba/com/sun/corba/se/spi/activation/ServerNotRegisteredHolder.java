package com.sun.corba.se.spi.activation;

/**
* com/sun/corba/se/spi/activation/ServerNotRegisteredHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from /scratch/mesos/slaves/9190d864-6621-4810-ba08-d8d8c75ba674-S34/frameworks/1735e8a2-a1db-478c-8104-60c8b0af87dd-0196/executors/f87b4890-2412-429e-bdfd-83788a246cfb/runs/8f4f187f-165d-4027-8162-c5cc52d7c3d8/workspace/open/src/java.corba/share/classes/com/sun/corba/se/spi/activation/activation.idl
* Thursday, June 28, 2018 at 12:47:36 AM Greenwich Mean Time
*/

public final class ServerNotRegisteredHolder implements org.omg.CORBA.portable.Streamable
{
  public com.sun.corba.se.spi.activation.ServerNotRegistered value = null;

  public ServerNotRegisteredHolder ()
  {
  }

  public ServerNotRegisteredHolder (com.sun.corba.se.spi.activation.ServerNotRegistered initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = com.sun.corba.se.spi.activation.ServerNotRegisteredHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    com.sun.corba.se.spi.activation.ServerNotRegisteredHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return com.sun.corba.se.spi.activation.ServerNotRegisteredHelper.type ();
  }

}
