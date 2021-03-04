package ca.yorku.sis.mvj;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import jdk.internal.org.jline.reader.impl.DefaultParser;


public class Main {

	
	public static void main(String[] args) throws IOException{
				
		Options cliOptions = new Options();
		cliOptions.addRequiredOption("f", "from", true, "Directory to move files [f]rom");
		cliOptions.addRequiredOption("t", "to", true, "Directory to move files [t]o");
		cliOptions.addRequiredOption("p", "prefix", true, "File name [p]refix to search");
		cliOptions.addRequiredOption("m", "mtime", true, "Number of [m]inutes in the past and older to search");
		
		CommandLineParser cliParser = (CommandLineParser) new org.apache.commons.cli.DefaultParser();
		CommandLine cmd;
		String srcDir = "";
		String destDir = "";
		String filePrefix = "";
		int mmin = 1;
		
	
		try {
		
			cmd = cliParser.parse( cliOptions, args);
			srcDir = cmd.getOptionValue("f");
			destDir = cmd.getOptionValue("t");
			filePrefix = cmd.getOptionValue("p");
			mmin = Integer.parseInt(cmd.getOptionValue("m"));
		
		} catch (Exception e) {
			
			System.err.println( "Failed to parse cli arguments " + e.getMessage() );
			System.exit(1);
			
		}		
		
		System.out.println("*** start ***");
		
		Date aMinuteAgo = Date.from(Instant.now().minus(mmin, ChronoUnit.MINUTES));
		
		// gettting a list of files at srcDir
		File srcDirFile = new File(srcDir);
		List<File> files = (List<File>) FileUtils.listFiles(
				                                            
															// looking inside srcDir
															new File(srcDir), 
				                                             
				                                             // file created at least a minute ago
				                                             // and file has a prefix TQP
				                                             FileFilterUtils.and(
				                                            		 	FileFilterUtils.ageFileFilter(aMinuteAgo, true),
				                                            		 	FileFilterUtils.prefixFileFilter(filePrefix)
				                                            		 )
				                                             , 
				                                             
				                                             // not searching sub-directories
				                                             null);
		
		// showing you the files at srcDir
		if (files != null && files.size() > 0) {
			
			System.out.println(files.size() + " file(s) like " + filePrefix + "* in the last " + mmin + " minute(s) in " + srcDir );
			
			for (File file : files) {
				System.out.println("Moving file " + file.getCanonicalPath() + " to directory " + destDir);
				FileUtils.moveToDirectory( file, 
						                   new File(destDir), 
						                   
						                   // do not create the destination directory if it doesn't exist
						                   false
						                   );
			}
			
		} else {
			System.out.println("No file like " + filePrefix + "* in the last " + mmin + " minute(s) in " + srcDir);
		}
			
		
		
		
		System.out.println("=== end ===");
		
	}
	 
	
}
