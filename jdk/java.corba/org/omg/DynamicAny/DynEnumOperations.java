package org.omg.DynamicAny;


/**
* org/omg/DynamicAny/DynEnumOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from /scratch/mesos/slaves/9190d864-6621-4810-ba08-d8d8c75ba674-S34/frameworks/1735e8a2-a1db-478c-8104-60c8b0af87dd-0196/executors/f87b4890-2412-429e-bdfd-83788a246cfb/runs/8f4f187f-165d-4027-8162-c5cc52d7c3d8/workspace/open/src/java.corba/share/classes/org/omg/DynamicAny/DynamicAny.idl
* Thursday, June 28, 2018 at 12:47:37 AM Greenwich Mean Time
*/


/**
    * DynEnum objects support the manipulation of IDL enumerated values.
    * The current position of a DynEnum is always -1.
    */
public interface DynEnumOperations  extends org.omg.DynamicAny.DynAnyOperations
{

  /**
          * Returns the value of the DynEnum as an IDL identifier.
          */
  String get_as_string ();

  /**
          * Sets the value of the DynEnum to the enumerated value whose IDL identifier is passed in the value parameter.
          *
          * @exception InvalidValue If value contains a string that is not a valid IDL identifier
          *            for the corresponding enumerated type
          */
  void set_as_string (String value) throws org.omg.DynamicAny.DynAnyPackage.InvalidValue;

  /**
          * Returns the value of the DynEnum as the enumerated value's ordinal value.
          * Enumerators have ordinal values 0 to n-1, as they appear from left to right
          * in the corresponding IDL definition.
          */
  int get_as_ulong ();

  /**
          * Sets the value of the DynEnum as the enumerated value's ordinal value.
          *
          * @exception InvalidValue If value contains a value that is outside the range of ordinal values
          *            for the corresponding enumerated type
          */
  void set_as_ulong (int value) throws org.omg.DynamicAny.DynAnyPackage.InvalidValue;
} // interface DynEnumOperations
