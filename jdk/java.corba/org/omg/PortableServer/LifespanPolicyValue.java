package org.omg.PortableServer;


/**
* org/omg/PortableServer/LifespanPolicyValue.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from /scratch/mesos/slaves/9190d864-6621-4810-ba08-d8d8c75ba674-S34/frameworks/1735e8a2-a1db-478c-8104-60c8b0af87dd-0196/executors/f87b4890-2412-429e-bdfd-83788a246cfb/runs/8f4f187f-165d-4027-8162-c5cc52d7c3d8/workspace/open/src/java.corba/share/classes/org/omg/PortableServer/poa.idl
* Thursday, June 28, 2018 at 12:47:40 AM Greenwich Mean Time
*/


/**
	 * The LifespanPolicyValue can have the following values.
	 * TRANSIENT - The objects implemented in the POA 
	 * cannot outlive the POA instance in which they are 
	 * first created. 
	 * PERSISTENT - The objects implemented in the POA can 
	 * outlive the process in which they are first created. 
	 */
public class LifespanPolicyValue implements org.omg.CORBA.portable.IDLEntity
{
  private        int __value;
  private static int __size = 2;
  private static org.omg.PortableServer.LifespanPolicyValue[] __array = new org.omg.PortableServer.LifespanPolicyValue [__size];

  public static final int _TRANSIENT = 0;
  public static final org.omg.PortableServer.LifespanPolicyValue TRANSIENT = new org.omg.PortableServer.LifespanPolicyValue(_TRANSIENT);
  public static final int _PERSISTENT = 1;
  public static final org.omg.PortableServer.LifespanPolicyValue PERSISTENT = new org.omg.PortableServer.LifespanPolicyValue(_PERSISTENT);

  public int value ()
  {
    return __value;
  }

  public static org.omg.PortableServer.LifespanPolicyValue from_int (int value)
  {
    if (value >= 0 && value < __size)
      return __array[value];
    else
      throw new org.omg.CORBA.BAD_PARAM ();
  }

  protected LifespanPolicyValue (int value)
  {
    __value = value;
    __array[__value] = this;
  }
} // class LifespanPolicyValue
