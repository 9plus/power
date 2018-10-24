package org.omg.CosNaming.NamingContextPackage;


/**
* org/omg/CosNaming/NamingContextPackage/NotFoundReason.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from /scratch/mesos/slaves/9190d864-6621-4810-ba08-d8d8c75ba674-S34/frameworks/1735e8a2-a1db-478c-8104-60c8b0af87dd-0196/executors/f87b4890-2412-429e-bdfd-83788a246cfb/runs/8f4f187f-165d-4027-8162-c5cc52d7c3d8/workspace/open/src/java.corba/share/classes/org/omg/CosNaming/nameservice.idl
* Thursday, June 28, 2018 at 12:47:37 AM Greenwich Mean Time
*/


/**
         * Indicates the reason for not able to resolve.
         */
public class NotFoundReason implements org.omg.CORBA.portable.IDLEntity
{
  private        int __value;
  private static int __size = 3;
  private static org.omg.CosNaming.NamingContextPackage.NotFoundReason[] __array = new org.omg.CosNaming.NamingContextPackage.NotFoundReason [__size];

  public static final int _missing_node = 0;
  public static final org.omg.CosNaming.NamingContextPackage.NotFoundReason missing_node = new org.omg.CosNaming.NamingContextPackage.NotFoundReason(_missing_node);
  public static final int _not_context = 1;
  public static final org.omg.CosNaming.NamingContextPackage.NotFoundReason not_context = new org.omg.CosNaming.NamingContextPackage.NotFoundReason(_not_context);
  public static final int _not_object = 2;
  public static final org.omg.CosNaming.NamingContextPackage.NotFoundReason not_object = new org.omg.CosNaming.NamingContextPackage.NotFoundReason(_not_object);

  public int value ()
  {
    return __value;
  }

  public static org.omg.CosNaming.NamingContextPackage.NotFoundReason from_int (int value)
  {
    if (value >= 0 && value < __size)
      return __array[value];
    else
      throw new org.omg.CORBA.BAD_PARAM ();
  }

  protected NotFoundReason (int value)
  {
    __value = value;
    __array[__value] = this;
  }
} // class NotFoundReason
