package org.omg.PortableServer;


/**
* org/omg/PortableServer/ForwardRequest.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from /scratch/mesos/slaves/9190d864-6621-4810-ba08-d8d8c75ba674-S34/frameworks/1735e8a2-a1db-478c-8104-60c8b0af87dd-0196/executors/f87b4890-2412-429e-bdfd-83788a246cfb/runs/8f4f187f-165d-4027-8162-c5cc52d7c3d8/workspace/open/src/java.corba/share/classes/org/omg/PortableServer/poa.idl
* Thursday, June 28, 2018 at 12:47:40 AM Greenwich Mean Time
*/

public final class ForwardRequest extends org.omg.CORBA.UserException
{
  public org.omg.CORBA.Object forward_reference = null;

  public ForwardRequest ()
  {
    super(ForwardRequestHelper.id());
  } // ctor

  public ForwardRequest (org.omg.CORBA.Object _forward_reference)
  {
    super(ForwardRequestHelper.id());
    forward_reference = _forward_reference;
  } // ctor


  public ForwardRequest (String $reason, org.omg.CORBA.Object _forward_reference)
  {
    super(ForwardRequestHelper.id() + "  " + $reason);
    forward_reference = _forward_reference;
  } // ctor

} // class ForwardRequest
