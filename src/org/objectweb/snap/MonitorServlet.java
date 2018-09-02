package org.objectweb.snap;

import javax.servlet.*;
import javax.servlet.http.*;

import java.util.*;
import java.lang.management.*;

import org.jdom.*;
import org.jdom.output.*;

public class MonitorServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1681779722791479212L;

public void init() throws ServletException {  
    //ServletContext ctx = getServletContext();
  }

  private Element getMemoryUsageElement( String name, MemoryUsage mu )  {
    Element e = new Element( name );
    if( mu != null )
    {
      e.setAttribute( "init", Long.toString( mu.getInit() ) );
      e.setAttribute( "used", Long.toString( mu.getUsed() ) );
      e.setAttribute( "committed", Long.toString( mu.getCommitted() ) );
      e.setAttribute( "max", Long.toString( mu.getMax() ) );
    }
    return e;
  }

  public void service( HttpServletRequest req, HttpServletResponse res ) throws ServletException
  {
    try
    {
      System.out.println( "MXBeanServlet invoked..." );
      Element root = new Element( "management-info" );

      // Get our configuration information
      boolean configMode = true;
      boolean runtimeMode = true;
      boolean verboseMode = true;
      String mode = req.getParameter( "mode" );
      if( mode != null )
      {
        if( mode.equalsIgnoreCase( "config" ) )
        {
          runtimeMode = false;
        }
        else if( mode.equalsIgnoreCase( "runtime" ) )
        {
          configMode = false;
        }
      }
      String verboseStr = req.getParameter( "verbose" );
      if( verboseStr != null && verboseStr.equalsIgnoreCase( "false" ) )
      {
        verboseMode = false;
      }


      // Get runtime information
      RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();

      // Display environmental information
      if( configMode )
      {
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        Element envElement = new Element( "environment" );
        Element osElement = new Element( "operating-system" );
        osElement.setAttribute( "architecture", os.getArch() );
        osElement.setAttribute( "name", os.getName() );
        osElement.setAttribute( "version", os.getVersion() );
        osElement.setAttribute( "available-processors", 
                    Integer.toString( os.getAvailableProcessors() ) );
        envElement.addContent( osElement );
        Element rtSystem = new Element( "runtime-system" );
        rtSystem.setAttribute( "name", runtime.getName() );
        rtSystem.setAttribute( "spec-name", runtime.getSpecName() );
        rtSystem.setAttribute( "spec-vendor", runtime.getSpecVendor() );
        rtSystem.setAttribute( "spec-version", runtime.getSpecVersion() );
        rtSystem.setAttribute( "vm-name", runtime.getVmName() );
        rtSystem.setAttribute( "vm-vendor", runtime.getVmVendor() );
        rtSystem.setAttribute( "vm-version", runtime.getVmVersion() );
        rtSystem.setAttribute( "management-spec-version", runtime.getManagementSpecVersion() );
        rtSystem.setAttribute( "class-path", runtime.getClassPath() );
        rtSystem.setAttribute( "boot-class-path", runtime.getBootClassPath() );
        rtSystem.setAttribute( "library-path", runtime.getLibraryPath() );
        envElement.addContent( rtSystem );
        root.addContent( envElement );
      }

      // Display memory info
      MemoryMXBean memorymbean = ManagementFactory.getMemoryMXBean();
      Element memoryElement = new Element( "memory" );
      memoryElement.addContent( 
          getMemoryUsageElement( "heap-usage", memorymbean.getHeapMemoryUsage() ) );
      memoryElement.addContent( 
          getMemoryUsageElement( "non-heap-usage", memorymbean.getNonHeapMemoryUsage() ) );

      // Read Garbage Collection information
      Element gcsElement = new Element( "garbage-collectors" );
      List<GarbageCollectorMXBean> gcmbeans = ManagementFactory.getGarbageCollectorMXBeans();
      for( GarbageCollectorMXBean gcmbean : gcmbeans )
      {
        Element gcElement = new Element( "garbage-collector" );
        gcElement.setAttribute( "name", gcmbean.getName() );
        gcElement.setAttribute( "collection-count", Long.toString( gcmbean.getCollectionCount() ) );
        gcElement.setAttribute( "collection-time", Long.toString( gcmbean.getCollectionTime() ) );
        System.out.println( "Memory Pools: " );
        String[] memoryPoolNames = gcmbean.getMemoryPoolNames();
        for( int i=0; i<memoryPoolNames.length; i++ )
        {
          if( configMode )
          {
            Element memoryPoolElement = new Element( "memory-pool" );
            memoryPoolElement.setAttribute( "name", memoryPoolNames[ i ] );
            gcElement.addContent( memoryPoolElement );
          }
        }
        gcsElement.addContent( gcElement );
      }
      memoryElement.addContent( gcsElement );

      // Read Memory Pool Information
      Element memoryPoolsElement = new Element( "memory-pools" );
      List<MemoryPoolMXBean> mempoolsmbeans = ManagementFactory.getMemoryPoolMXBeans();
      for( MemoryPoolMXBean mempoolmbean : mempoolsmbeans )
      {
        Element memoryPoolElement = new Element( "memory-pool" );
        memoryPoolElement.setAttribute( "name", mempoolmbean.getName() );
        memoryPoolElement.setAttribute( "type", mempoolmbean.getType().toString() );
        memoryPoolElement.addContent( getMemoryUsageElement( "usage", mempoolmbean.getUsage() ) );
        memoryPoolElement.addContent( 
          getMemoryUsageElement( "collection-usage", mempoolmbean.getCollectionUsage() ) );
        memoryPoolElement.addContent( 
          getMemoryUsageElement( "peak-usage", mempoolmbean.getPeakUsage() ) );
        String[] memManagerNames = mempoolmbean.getMemoryManagerNames();
        for( int j=0; j<memManagerNames.length; j++ )
        {
          if( configMode )
          {
            Element memManagerElement = new Element( "memory-manager" );
            memManagerElement.setAttribute( "name", memManagerNames[ j ] );
            memoryPoolElement.addContent( memManagerElement );
          }
        }
        memoryPoolsElement.addContent( memoryPoolElement );
      }
      memoryElement.addContent( memoryPoolsElement );
      root.addContent( memoryElement );


      // Display thread info
      ThreadMXBean threads = ManagementFactory.getThreadMXBean();
      Element threadsElement = new Element( "threads" );
      threadsElement.setAttribute( "thread-count", Long.toString( threads.getThreadCount() ) );
      threadsElement.setAttribute( "total-started-thread-count", 
         Long.toString( threads.getTotalStartedThreadCount() ) );
      threadsElement.setAttribute( "daemon-thread-count", 
         Long.toString( threads.getDaemonThreadCount() ) );
      threadsElement.setAttribute( "peak-thread-count", Long.toString( threads.getPeakThreadCount() ) );
      long totalCpuTime = 0l;
      long totalUserTime = 0l;

      // Parse each thread
      ThreadInfo[] threadInfos = threads.getThreadInfo( threads.getAllThreadIds() );
      for( int i=0; i<threadInfos.length; i++ )
      {
        if( verboseMode )
        {
          Element threadElement = new Element( "thread" );
          threadElement.setAttribute( "id", Long.toString( threadInfos[ i ].getThreadId() ) );
          threadElement.setAttribute( "name", threadInfos[ i ].getThreadName() );
          threadElement.setAttribute( "cpu-time-nano", 
           Long.toString( threads.getThreadCpuTime( threadInfos[ i ].getThreadId() ) ) );
          threadElement.setAttribute( "cpu-time-ms", 
           Long.toString( threads.getThreadCpuTime( threadInfos[ i ].getThreadId() ) / 1000000l ) );
          threadElement.setAttribute( "user-time-nano", 
           Long.toString( threads.getThreadUserTime( threadInfos[ i ].getThreadId() ) ) );
          threadElement.setAttribute( "user-time-ms", 
          Long.toString( threads.getThreadUserTime( threadInfos[ i ].getThreadId() ) / 1000000l ) );
          threadElement.setAttribute( "blocked-count", 
           Long.toString( threadInfos[ i ].getBlockedCount() ) );
          threadElement.setAttribute( "blocked-time", 
           Long.toString( threadInfos[ i ].getBlockedTime() ) );
          threadElement.setAttribute( "waited-count", 
           Long.toString( threadInfos[ i ].getWaitedCount() ) );
          threadElement.setAttribute( "waited-time", 
           Long.toString( threadInfos[ i ].getWaitedTime() ) );
          threadsElement.addContent( threadElement );
        }

        // Update our aggregate values
        totalCpuTime += threads.getThreadCpuTime( threadInfos[ i ].getThreadId() );
        totalUserTime += threads.getThreadUserTime( threadInfos[ i ].getThreadId() );
      }
      long totalCpuTimeMs = totalCpuTime / 1000000l;
      long totalUserTimeMs = totalUserTime / 1000000l;
      threadsElement.setAttribute( "total-cpu-time-nano", Long.toString( totalCpuTime ) );
      threadsElement.setAttribute( "total-user-time-nano", Long.toString( totalUserTime ) );
      threadsElement.setAttribute( "total-cpu-time-ms", Long.toString( totalCpuTimeMs ) );
      threadsElement.setAttribute( "total-user-time-ms", Long.toString( totalUserTimeMs ) );

      // Compute thread percentages
      long uptime = runtime.getUptime();
      threadsElement.setAttribute( "uptime", Long.toString( uptime ) );
      double cpuPercentage = ( ( double )totalCpuTimeMs / ( double )uptime ) * 100.0;
      double userPercentage = ( ( double )totalUserTimeMs / ( double )uptime ) * 100.0;
      threadsElement.setAttribute( "total-cpu-percent", Double.toString( cpuPercentage ) );
      threadsElement.setAttribute( "total-user-percent", Double.toString( userPercentage ) );
      root.addContent( threadsElement );

      
      // Output the XML to the caller
      XMLOutputter outputter = new XMLOutputter( );
      outputter.output( root, res.getOutputStream() );
    }
    catch( Exception e )
    {
      e.printStackTrace();
      throw new ServletException( e );
    }

    
  }
}
