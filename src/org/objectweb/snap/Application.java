package org.objectweb.snap;

import damon.core.DamonCore;
import damon.reflection.MetaData;
import damon.registry.Registry;
import damon.registry.RegistryException;
import easypastry.dht.DHTException;
import easypastry.dht.DHTHandler;
//import org.apache.axis.client.AdminClient;

import java.io.*;
import java.net.UnknownHostException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jdom.JDOMException;
import org.objectweb.snap.deployer.Deployer;
import org.objectweb.snap.exception.ApplicationDeploymentException;
import org.objectweb.snap.exception.ApplicationException;
import org.objectweb.snap.exception.ApplicationMetadataIncompleteException;
import org.objectweb.snap.exception.VerificationException;
import org.objectweb.snap.util.Requests;

import rice.p2p.commonapi.NodeHandle;

import java.security.PublicKey;
import java.security.Signature;

public class Application {
	
  private static Hashtable<String,Application> apps = new Hashtable<String,Application>();
	
  private static String serverName = null;
  private static int serverPort;  
  private Properties warConfig = null;  
  private DHTHandler dht = null;  
  
  private Requests reqs = new Requests();
   
  public Application (String serverName, Properties warConfig) throws Exception {
   	 	  	    
	    Application.serverName = serverName;
	    this.warConfig = warConfig;	    
	    
	    String appContext = warConfig.getProperty (Context.SNAP_APPCONTEXT);     
	      
	      dht = DamonCore.getDHTHandler(appContext);        
	    
	      String groupName = warConfig.getProperty (Context.SNAP_APPURL);     
	    
	      System.out.println (">>> SnapApplication :: " + appContext + " initialized.");
	    	
	      deployDistributedAspects(groupName);
	      //new DamonTaskThread(groupName,Thread.currentThread().getName()).start();

	      apps.put(appContext, this);
	    
	      //deploySnapWebServices("http://" + serverName + ":" + serverPort + "/" + appProperties.getProperty (Context.SNAP_APPCONTEXT),appProperties);	   	    
  
  }

  public static Application getApp(String context) {
	  return apps.get(context);
  }
  
  public static Application getSnapApp() {
	  return apps.get(Context.SNAP_CONTEXT);
  }

  public DHTHandler getStorageContext() {
    return dht;
  }
  
  public static String getServerName()  {
	    return serverName;
	  }

	  public static int getServerPort() {
	    return serverPort;
	  }

	  public static void setServerPort (int port) {
		System.out.println("Snap Server Port : "+port);
	    serverPort = port;
	  }

	  public String getP2PUrl() {
	    return warConfig.getProperty (Context.SNAP_APPURL);
	  }

	  public String getName() {
	    return warConfig.getProperty (Context.SNAP_APPNAME);
	  }

	  public String getContext() {
	    return warConfig.getProperty (Context.SNAP_APPCONTEXT);
	  }
	  
	  public String getWorkDir() {
		  return warConfig.getProperty (Context.SNAP_CODEBASE);
	  }

	  public String getWarName() {
	    return warConfig.getProperty (Context.SNAP_APPCONTEXT) + ".war";
	  }

	  public String getAppHttpUrl () {
	    return ("http://" + serverName + ":" + serverPort + "/" + getContext());
	  }
	  
	  public String getBootstrapHost() {
		  if (StartupServlet.bootstrap!=null && StartupServlet.bootstrap.length()>0) return StartupServlet.bootstrap; 
		  else return warConfig.getProperty(easypastry.util.Context.HOST);
	  }

	  public NodeHandle getAppLocalNodeHandle() {
	    return DamonCore.getReflection().getNodeHandle();
	  }
	  
	  public Requests getRequests() {
	  	return reqs;
	  }
	  
      public void deployDistributedAspects(String groupName) throws Exception {
     	System.out.println("App "+groupName+" : "+Thread.currentThread().getContextClassLoader());
		DamonCore.registerGroup(groupName);		
		int proxyPort = serverPort+333;
				 
		if (groupName.equals(Context.SNAP_URL_BASE)) {			
		 // System.out.println("!!! "+groupName+" STARTING DAMON PUBLISHER : "+proxyPort);	
		  DamonCore.getContainer().allowPublisher(proxyPort);
		}
		else {
		  //System.out.println("!!! "+groupName+" STARTING DAMON PROXY : "+proxyPort);
		  ClassLoader cl = Thread.currentThread().getContextClassLoader();	
		  
		  DamonCore.setClassLoader(cl);
		  DamonCore.getContainer().allowProxy(cl.toString(),groupName,proxyPort);
		  
		}  
	   
      
		int i = 1;
		String aspectName = warConfig.getProperty(Context.SNAP_DISTRIBUTED_ASPECT+i);
		while(aspectName!=null) {	
						
		  Class aspectClass = Thread.currentThread().getContextClassLoader().loadClass(aspectName);
		  
  	      //DamonCore.getStorage().deploy(aspectName,aspectClass);
  	        	      
  	      MetaData md;
  	      //if (is!=null) 
  	    	  md = DamonCore.getControl().getMetaData(aspectName,Thread.currentThread().getContextClassLoader());
  	      //else md = DamonCore.getControl().getMetaData(aspectName);
  	      
  	      //System.out.println("Metadata ("+aspectName+") : "+md);
  	      md.setScope(groupName);  	    
  	      md.setClassLoader(Thread.currentThread().getContextClassLoader());
          DamonCore.getControl().activateMetaData(md);
          
          i++;
          aspectName = warConfig.getProperty(Context.SNAP_DISTRIBUTED_ASPECT+i);	
		}  
  }
      
      public void undeployDistributedAspects(String groupName) throws UnknownHostException, IOException, DHTException, JDOMException {
       	//System.out.println("--> New damon group : "+groupName);
  		
  		ClassLoader cl = Thread.currentThread().getContextClassLoader();  		
  		 
  		if (groupName.equals(Context.SNAP_URL_BASE)) {  		  	
  		  //DamonCore.getContainer().closePublisher();
  		}
  		else {  		  
  		  DamonCore.getContainer().closeProxies(cl.toString());
  		}    	   
		DamonCore.getControl().passivateLocallyAll(groupName); 
     }
    
  public static Properties getSnapAppConfig (String appRoot) throws ApplicationMetadataIncompleteException {
    Properties snap_war = new Properties();
    //Properties config = null;

    try {
      snap_war.loadFromXML (new FileInputStream (appRoot + File.separator + "META-INF" + File.separator + "snap-war.xml"));      
      //config = loadProps (appRoot + File.separator + "WEB-INF" + File.separator + "damon-config.xml");
    } catch (Exception e) {
      e.printStackTrace();
      throw new ApplicationMetadataIncompleteException ("Unable to get SNAP application (" + appRoot + ") configuration.");
    }
    //config.putAll (snap_war);    
    snap_war.setProperty (Context.BUNSHIN_PROPS, appRoot + File.separator + "WEB-INF" + File.separator + "bunshin.properties");
    snap_war.setProperty (Context.SNAP_CODEBASE, appRoot);

    return snap_war;
  }
  
  private static Properties loadProps(String path) throws IOException {
		FileInputStream fis = new FileInputStream(path);
		String ext = path.substring(path.lastIndexOf('.')+1);
		Properties prop = new Properties();
		if (ext.equals("xml")) {
			prop.loadFromXML(fis);
		}
		else prop.load(fis);
		fis.close();
		return prop;
	}
  

  public static void bindSnapRoot() throws ApplicationException {
    
	  try {
    Registry.lookup(Context.SNAP_URL_BASE);
	} catch (DHTException pe) {
		System.out.println ("Base Snap root not bound... Binding "+Context.SNAP_URL_BASE);
		try {
			Registry.bind(Context.SNAP_URL_BASE, Context.SNAP_URL_BASE);
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	} catch (RegistryException e) {
		throw new ApplicationException(e);
	}

  }
  
  private byte[] loadFile (String fileName) {
    try {
      File f = new File (fileName);

      // Load and check if file contains the administrator's signature untampered
      InputStream is = new BufferedInputStream (new FileInputStream (f));
      int length = (int) f.length ();
      byte[] existentFileBytes = new byte[length];

      if (length > Integer.MAX_VALUE) {
        // File is too large
        throw new ApplicationDeploymentException ("Application file to read (" + f.getAbsolutePath () + ") is too large!");
      }

      // Read in the bytes
      int offset = 0;
      int numRead = 0;
      while (offset < existentFileBytes.length &&
             (numRead = is.read (existentFileBytes, offset,
                                 existentFileBytes.length - offset)) >= 0) {
        offset += numRead;
      }

      // Ensure all the bytes have been read in
      if (offset < existentFileBytes.length) {
        throw new ApplicationDeploymentException ("Could not completely read file " + f.getAbsolutePath ());
      }

      // Close the input stream
      is.close ();
      return existentFileBytes;
    } catch (Exception e) {
    }
    return null;
  }
  
  public void reinsert() throws ApplicationDeploymentException, IOException, RegistryException, DHTException {
	  
	  String p2pUrl = this.getP2PUrl();
	  
	  //is in the network?
	  if (!Deployer.isDeployed(p2pUrl)) {
	  	
	    //retrieve war from webapps dir
	    File war = new File(Context.WEBAPP_DIR + File.separator + this.getWarName());
	    
	    Deployer.deployWarFile(war, p2pUrl, warConfig);
	  }  
	  
  }

  public String resolveP2PURL (String p2purl) throws ApplicationDeploymentException, ApplicationException {
    
	//deploy locally
	p2purl = p2purl.substring(6, p2purl.length());
	
	int endDomain = p2purl.indexOf("/");
	if (endDomain<0) endDomain = p2purl.length();
		
	String appName = p2purl.substring(0,endDomain);	
	
	String document = "";
    if (endDomain>0) {
      document = p2purl.substring(endDomain, p2purl.length());
      //p2purl  = Context.SNAP_URL_BASE + '/' + p2purl.substring(0, endDomain);
      p2purl  = Context.SNAP_URL + p2purl.substring(0, endDomain);
    }
    //else p2purl  = Context.SNAP_URL_BASE + '/' + p2purl;   
    else p2purl  = Context.SNAP_URL + p2purl;
    
    activateSnapApp (p2purl);    
    
    //deploy time
    try {      
      int timeout = 0;
      boolean found = false;
      while(!found && timeout<60) {    	
    	found = apps.containsKey(appName);    	
        timeout++;
        if (!found) {
        	if (timeout>30) System.out.print("*");
        	//System.out.println("> "+appName+" not in contexts : "+apps.keySet());
        }        
        Thread.sleep(2000);
        
      }  	   
      if (!found) System.out.println("Timeout : Application Context not Found");
    }  catch (InterruptedException e) {
	  throw new ApplicationException(e);
	}
    
    
    
    return "http://localhost" + ":" + serverPort + "/" + appName + "/" + document;
    
  }
    
  /**
   * Extract the war app file in the webapps directory
   * @param p2pUrl
   * @throws ApplicationDeploymentException
   * @throws ApplicationException
   */
  public void activateSnapApp (String p2pUrl) throws ApplicationDeploymentException, ApplicationException {

    Properties appMetadata = new Properties();   
	Properties paux;
	try {
		paux = Deployer.retrieveMetadata(p2pUrl);
	} catch (Exception e) {
		throw new ApplicationException(e);
	}
	//System.out.println("metadata restored : "+paux);
	
	appMetadata.putAll(paux);

    // Obtain application's signature
    ////byte[] sig = ((ByteWrapper) appMetadata.get (Context.SNAP_APPSIGNATURE)).getBytes();
        
    // Check whether file already exists
	//String warPath = Context.WEBAPP_DIR + File.separator + appMetadata.getProperty (Context.SNAP_WARFILE);
	String warPath = Context.WEBAPP_DIR + File.separator + appMetadata.getProperty (Context.SNAP_APPCONTEXT) + ".war";
    byte[] existentFileBytes = loadFile (warPath);

    try {
      if (existentFileBytes != null) {
        ////try {
        ////  if (!verifySignature (sig, existentFileBytes)) {
        ////    System.out.println ("Signature for existent WAR file does not match. Trying to obtain the Registry's version.");
        ////  }
        ////  else {
            return;
        ////  }
        ////}
        ////catch (Exception e) {
        ////  e.printStackTrace ();
        ////  System.out.println ("Unable to verify signature for already deployed WAR file. Proceeding to reload from registry.");
        ////}
      }
        
      // Application was not previously deployed anywhere, let's deploy it here ;-)
      System.out.println("WAR Lookup : "+p2pUrl);
      byte[] registryFileBytes = Deployer.retrieveFileBytes(p2pUrl, appMetadata);
      
      
      /* SECURITY OFF      

      try {
        if (!verifySignature (sig, registryFileBytes)) {
          throw new ApplicationDeploymentException (
              "Web application incorrectly signed. (" + registryFileBytes.length + " bytes)");
        }
      }
      catch (Exception e) {
        e.printStackTrace();
        throw new RemoteException ("Web application '" + p2pUrl +
                                   " has not been signed by the administrator. Cause " + e.getCause ());
      }
      */
      try {
    	
    	String xmlPath = Context.CONTEXT_DIR + File.separator + appMetadata.getProperty (Context.SNAP_APPCONTEXT) + ".xml";
    	
        File f = new File (warPath);
        f.delete();
        
        FileOutputStream fos = new FileOutputStream (warPath);
        fos.write (registryFileBytes);
        fos.close();
                
        hasXmlthisWar(warPath, xmlPath);
        Thread.sleep(1000);
        return;

      }
      catch (Exception e) {
        e.printStackTrace ();
        throw new ApplicationException ("Unable to create WAR file on webserver's web application directory...!");        
      } 
    
    } catch (Exception e) {
      e.printStackTrace();
      if (e instanceof ApplicationException) {
        throw (ApplicationException) e;
      }
      if (e instanceof ApplicationDeploymentException) {
        throw (ApplicationDeploymentException) e;
      }
      throw new ApplicationException (e);
    }
    
  }
  
  private boolean hasXmlthisWar (String warPath, String xmlPath) throws ApplicationMetadataIncompleteException, ApplicationDeploymentException {
	    
	    ZipInputStream bis = null;
	    FileOutputStream fos = null;
	    String xmlFileName = xmlPath.substring(xmlPath.lastIndexOf(File.separatorChar) + 1);
	    boolean found = false;
	    
	    try {
	      // Read war file
	      bis = new ZipInputStream (new BufferedInputStream (new FileInputStream (warPath)));
	      fos = new FileOutputStream (xmlPath);
	      ZipEntry z = bis.getNextEntry();

	      // Process all entries
	      while (z != null) {
	        String name = z.getName ();
	        name = name.replaceAll ("/", ".");
	        if (name.toLowerCase().endsWith (xmlFileName)) {
	        	found = true;
	            // Transfer bytes from the ZIP file to the output file
	            byte[] buf = new byte[1024];
	            int len;
	            while ((len = bis.read(buf)) > 0) {
	                fos.write(buf, 0, len);
	            }
	        }
	        z = bis.getNextEntry();
	      }
	      if (!found) throw new ApplicationMetadataIncompleteException ("Application metadata is incomplete: WAR lacks "+xmlFileName+" file.");
	    } catch (FileNotFoundException fnf) {
	      throw new ApplicationDeploymentException ("File not found while trying to extract WAR file.");
	    } catch (IOException ioe) {
	      throw new ApplicationDeploymentException ("I/O Exception while trying to extract WAR file.");
	    } finally {
	      try {
	        bis.close();
	      } catch (Exception e) {}
	    }
	    
	    return found;
	  }

  public boolean verifySignature (byte[] signature, byte[] fileBytes) throws VerificationException {
	    try {
	      // Check if the application's signature is validated against the administrator's public key
	      Signature rsa = Signature.getInstance ("SHA1withRSA");

	      // Initialize the object with the administrator's public key
	      PublicKey pubKey = (PublicKey) Registry.lookup (Context.SNAP_ADMIN_PK);
	      rsa.initVerify (pubKey);

	      // Update and verify the data
	      rsa.update (fileBytes);
	      return rsa.verify (signature);
	    } catch (java.security.NoSuchAlgorithmException ne) {
	      throw new VerificationException ("No such algorithm: SHA1withRSA!");
	    } catch (java.security.InvalidKeyException ik) {
	      throw new VerificationException ("Invalid administrator's public key!");
	    } catch (java.security.SignatureException se) {
	      throw new VerificationException ("Signature exception!");
	    } catch (RegistryException e) {
	    	throw new VerificationException ("Administrator's public key not found!");
		} catch (DHTException e) {
			throw new VerificationException ("Administrator's public key not found!");
		}
	  }

 /**
  * This method returns a listing of url of all available Snap web applications
  * which are already deployed.
  * @return Collection<String> List of Snap webapp identifiers.
 * @throws PersistenceException 
 * @throws RegistryException 
  */
 public static Collection<String> getApplications() throws RegistryException, DHTException {
   Collection<String> c = Registry.list (Context.SNAP_URL_BASE);
   return c;
 }
 

public void close() throws UnknownHostException, IOException, DHTException, JDOMException {
	undeployDistributedAspects(getP2PUrl());
	System.out.println (">>> SnapApplication :: " + getContext() + " closing.");
	
	
}




}