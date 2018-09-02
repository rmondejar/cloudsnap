package org.objectweb.snap;

import java.io.File;


public class Context {
  
  public static final String SNAP_URL = "p2p://";
  public static final String SNAP_CONTEXT = "snap.objectweb.org";
  public static final String SNAP_URL_BASE = SNAP_URL + SNAP_CONTEXT;
  
  public static final String SNAP_APPNAME = "appName";
  public static final String SNAP_APPCONTEXT = "appContext";
  public static final String SNAP_APPURL = "appP2PUrl";
  public static final String SNAP_WARFILE = "warFile";
  public static final String SNAP_WARFILE_PARTS_NUM = "warFile_parts_num";
  public static final String SNAP_DISTRIBUTED_ASPECT = "snapDistributedAspect_";
  public static final String SNAP_P2PWS = "p2pWebService_";
  
  public static final String SNAP_CODEBASE = "codebase";
  public static final String SNAP_APPSIGNATURE = "appSignature";
  public static final String SNAP_ADMIN_PK = "p2p://public_key.admin.snap.objectweb.org";
  
  public static final String BUNSHIN_PROPS = "bunshin_props";
  public static final String WEBAPP_DIR = "./webzone"+File.separator+"webapps";
  public static final String CONTEXT_DIR = "./webzone"+File.separator+"contexts";
  public static final String METADATA_SUFFIX = "_METADATA";
  public static final String WARFILE_SUFFIX = "_WARFILE_";
  
  public static final String SNAP_MAIN_APP = "SNAPSkel";
  public static final String SNAP_APP = "snapApp";

  public static final int WARFILE_PART_MAX_SIZE = 750000;
  
}
