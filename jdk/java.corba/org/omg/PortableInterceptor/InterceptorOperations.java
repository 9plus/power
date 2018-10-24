package org.omg.PortableInterceptor;


/**
* org/omg/PortableInterceptor/InterceptorOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from /scratch/mesos/slaves/9190d864-6621-4810-ba08-d8d8c75ba674-S34/frameworks/1735e8a2-a1db-478c-8104-60c8b0af87dd-0196/executors/f87b4890-2412-429e-bdfd-83788a246cfb/runs/8f4f187f-165d-4027-8162-c5cc52d7c3d8/workspace/open/src/java.corba/share/classes/org/omg/PortableInterceptor/Interceptors.idl
* Thursday, June 28, 2018 at 12:47:39 AM Greenwich Mean Time
*/


/**
   * All Portable Interceptors implement Interceptor.
   */
public interface InterceptorOperations 
{

  /**
       * Returns the name of the interceptor.
       * <p>
       * Each Interceptor may have a name that may be used administratively 
       * to order the lists of Interceptors. Only one Interceptor of a given 
       * name can be registered with the ORB for each Interceptor type. An 
       * Interceptor may be anonymous, i.e., have an empty string as the name 
       * attribute. Any number of anonymous Interceptors may be registered with 
       * the ORB.
       *
       * @return the name of the interceptor.
       */
  String name ();

  /**
       * Provides an opportunity to destroy this interceptor.
       * The destroy method is called during <code>ORB.destroy</code>. When an 
       * application calls <code>ORB.destroy</code>, the ORB:
       * <ol>
       *   <li>waits for all requests in progress to complete</li>
       *   <li>calls the <code>Interceptor.destroy</code> operation for each 
       *       interceptor</li>
       *   <li>completes destruction of the ORB</li>
       * </ol>
       * Method invocations from within <code>Interceptor.destroy</code> on 
       * object references for objects implemented on the ORB being destroyed 
       * result in undefined behavior. However, method invocations on objects 
       * implemented on an ORB other than the one being destroyed are 
       * permitted. (This means that the ORB being destroyed is still capable 
       * of acting as a client, but not as a server.) 
       */
  void destroy ();
} // interface InterceptorOperations
