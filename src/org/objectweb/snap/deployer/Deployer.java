package org.objectweb.snap.deployer;

import com.oreilly.servlet.multipart.*;

import damon.registry.Registry;
import damon.registry.RegistryException;
import easypastry.dht.DHTException;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.*;

import org.objectweb.snap.Context;
import org.objectweb.snap.exception.ApplicationDeploymentException;
import org.objectweb.snap.exception.ApplicationMetadataIncompleteException;

public class Deployer {

	public static Properties extractWarInfo(String warFile)
			throws ApplicationMetadataIncompleteException,
			ApplicationDeploymentException {
		return extractWarInfo(warFile, "snap-war.xml");
	}

	private static Properties extractWarInfo(String warFile, String xmlFile)
			throws ApplicationMetadataIncompleteException,
			ApplicationDeploymentException {

		ZipInputStream bis = null;
		Properties appProperties = new Properties();

		try {
			// Read component's jar file
			bis = new ZipInputStream(new BufferedInputStream(
					new FileInputStream(warFile)));
			ZipEntry z = bis.getNextEntry();

			// Process all war file entries
			while (z != null) {
				String name = z.getName();
				name = name.replaceAll("/", ".");
				if (name.toLowerCase().endsWith(xmlFile)) {
					// Load application metadata
					appProperties.loadFromXML(bis);
					return appProperties;
				}
				z = bis.getNextEntry();
			}
			throw new ApplicationMetadataIncompleteException(
					"Application metadata is incomplete: WAR lacks 'snap-war.xml' file.");
		} catch (FileNotFoundException fnf) {
			throw new ApplicationDeploymentException(
					"File not found while trying to extract WAR file.");
		} catch (IOException ioe) {
			throw new ApplicationDeploymentException(
					"I/O Exception while trying to extract WAR file.");
		} finally {
			try {
				bis.close();
			} catch (Exception e) {
			}
		}
	}

	public static String uploadFile(HttpServletRequest request, String path)
			throws IOException {

		// Properties prop = System.getProperties();
		// String separator = (String)prop.getProperty("file.separator");
		
		String fileName = "";
		
		MultipartParser mp = new MultipartParser(request, 100 * 1024 * 1024);
		Part part;
		while ((part = mp.readNextPart()) != null) {

			// String name = part.getName();
			if (part.isFile()) {

				FilePart filePart = (FilePart) part;
				fileName = filePart.getFileName();
				if (fileName != null) {

					File f = new File(path + File.separator + fileName);
					filePart.writeTo(f);
				}
			}
		}

		return fileName;
	}

	public static void deployWarFile(File f, String p2pUrl, Properties metadata)
			throws ApplicationDeploymentException, IOException,
			RegistryException, DHTException {

		// WAR file to byte[]
		InputStream is = new BufferedInputStream(new FileInputStream(f));
		int length = (int) f.length();
		byte[] bytes = new byte[length];

		if (length > Integer.MAX_VALUE) {
			// File is too large
			throw new ApplicationDeploymentException(
					"Application file to deploy (" + f.getName()
							+ ") is too large!");
		}

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new ApplicationDeploymentException(
					"Could not completely read file " + f.getName());
		}

		// Close the input stream and return bytes
		is.close();
		
		insertApp(p2pUrl, metadata, bytes);
		
		
	}
	
	public static void insertApp(String p2pUrl, Properties metadata, byte[] bytes) throws RegistryException, DHTException {
		
		// split bytes in 1MB parts
		Vector<byte[]> parts = splitBytes(bytes);

		metadata.setProperty(Context.SNAP_WARFILE_PARTS_NUM, Integer.toString(parts.size()));
		Registry.bind(p2pUrl + Context.METADATA_SUFFIX, metadata);
		
		for (int i = 0; i < parts.size(); i++) {
		  Registry.bind(p2pUrl + Context.WARFILE_SUFFIX + i, parts.get(i));
		}
		
	}
	
	public static Vector<byte[]> splitBytes(byte[] bytes) {
		Vector<byte[]> parts = new Vector<byte[]>();
		final int MAX = Context.WARFILE_PART_MAX_SIZE;
		
		int partsNum = (bytes.length/MAX) + 1;
		//System.out.println("Deployer > Length : "+bytes.length);
		//System.out.println("Deployer > MAX : "+MAX);
		//System.out.println("Deployer > PartsNum : "+partsNum);
		if (partsNum>1) {
			int length = bytes.length;
			int counter = 0;
			while(counter<partsNum) {
			  	
			  if (((counter+1)*MAX)<length) {
				//System.out.println("Deployer > Part("+counter+") : "+MAX);
				byte[] part = new byte[MAX];
				System.arraycopy(bytes, counter*MAX, part, 0, MAX);		
				parts.add(part);
			  }
			  else { //last part
				int rest = length - (counter*MAX);
				//System.out.println("Deployer > Part("+counter+") : "+rest);
				byte[] part = new byte[rest];
				System.arraycopy(bytes, counter*MAX, part, 0, rest);
				parts.add(part);
			  }
			  counter++;
			}  		  	
		}
		else {
			parts.add(bytes);
		}
	
		return parts;
	}
	
	public static byte[] joinBytes(Vector<byte[]> parts) {
	
		int length = 0;
		for (byte[] part : parts) {
		  length += part.length;	
		}
		
		byte[] bytes = new byte[length];
		int counter = 0;
		for (byte[] part : parts) {
			System.arraycopy(part, 0, bytes, counter, part.length);
			counter += part.length;
		}		                   
		
		return bytes;
	}

	public static byte[] retrieveFileBytes(String p2pUrl, Properties metadata) throws RegistryException, DHTException {
				
		Vector<byte[]> parts = new Vector<byte[]>();
		int partsNum = Integer.parseInt(metadata.getProperty(Context.SNAP_WARFILE_PARTS_NUM));
		//System.out.println("Deployer > PartsNum : "+partsNum);
		for (int i = 0; i < partsNum; i++) {
		  byte[] part = (byte[]) Registry.lookup (p2pUrl + Context.WARFILE_SUFFIX + i);
		  //System.out.println("Deployer > Part("+i+") : "+part.length);
		  parts.add(part);
		}  
		
		byte[] bytes = joinBytes(parts);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		return bytes;
	}

	public static Properties retrieveMetadata(String p2pUrl) throws RegistryException, DHTException {
		return (Properties) Registry.lookup (p2pUrl + Context.METADATA_SUFFIX);		
	}

	public static boolean isDeployed(String p2pUrl) {
		
		boolean exists = false;
		
		try {
			Properties metadata = (Properties) Registry.lookup (p2pUrl + Context.METADATA_SUFFIX);
			
			if (metadata!=null) {
			  
			  //check war file
			  retrieveFileBytes(p2pUrl, metadata);	
			
			  exists = true;	
			}
			else exists = false;
			
			
		} catch (Exception e) {
			exists = false;
		}
		
		return exists;
	}

}
