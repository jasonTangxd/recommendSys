package com.xiaoxiaomo.tools;

import java.io.File;
import java.io.FilenameFilter;
import java.util.logging.Logger;

import org.apache.commons.io.filefilter.FileFilterUtils;

/**
 * Created by xiaoxiaomo on 2015/6/28.
 */
public class DelLastUpdated {

    private static PropertyHelper propHelper = new PropertyHelper("config");
    private static final String KEY_MAVEN_REPO = "maven.repo";
    private static final String MAVEN_REPO_PATH = propHelper
            .getValue(KEY_MAVEN_REPO);
//    private static final String FILE_SUFFIX = "lastUpdated";
    private static final String FILE_SUFFIX = "_remote.repositories";
    private static final Logger _log = Logger.getLogger("fsdfsfd");

    /**
     * @param args
     */
    public static void main(String[] args) {
        File mavenRep = new File(MAVEN_REPO_PATH);
        if (!mavenRep.exists()) {
//            _log.warn("Maven repos is not exist.");
            return;
        }
        File[] files = mavenRep.listFiles((FilenameFilter) FileFilterUtils
                .directoryFileFilter());
        delFileRecr(files,null);
        _log.info("Clean lastUpdated files finished.");
    }

    private static void delFileRecr(File[] dirs, File[] files) {
        if (dirs != null && dirs.length > 0) {
            for(File dir: dirs){
                File[] childDir = dir.listFiles((FilenameFilter) FileFilterUtils
                        .directoryFileFilter());
                File[] childFiles = dir.listFiles((FilenameFilter) FileFilterUtils
                        .suffixFileFilter(FILE_SUFFIX));
                delFileRecr(childDir,childFiles);
            }
        }
        if(files!=null&&files.length>0){
            for(File file: files){
                if(file.delete()){
                    _log.info("File: ["+file.getName()+"] has been deleted.");
                }
            }
        }
    }

}



