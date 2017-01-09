package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.lib.CSSCompressor;
import com.wahwahnetworks.platform.lib.HTMLMinifier;
import com.wahwahnetworks.platform.lib.UglifyJS;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptException;
import java.io.IOException;

/**
 * Created by Justin on 6/10/2014.
 */

@Service
public class FileMinificationService
{


	private static final Logger log = Logger.getLogger(FileMinificationService.class);

	@Autowired
	private UglifyJS uglifyJS;

	@Autowired
	private HTMLMinifier htmlMinifier;

	@Autowired
	private FileWildcardService fileWildcardService;

	public byte[] compressFile(String fileName, byte[] fileContents) throws IOException
	{

		// Should we skip minification?
		if (fileWildcardService.doesFileNameMatchList(fileName, "skip_minification_list"))
		{
			return fileContents;
		}

		String extension = fileName.substring(fileName.lastIndexOf("."));

		if (extension.equals(".js"))
		{
			return compressJavaScript(fileContents);
		}

		if (extension.equals(".css"))
		{
			return compressCSS(fileContents);
		}

		if (extension.equals(".html"))
		{
			return compressHTML(fileContents);
		}

		return fileContents;
	}

	private byte[] compressJavaScript(byte[] fileContents)
	{
		try
		{
			String source = new String(fileContents);
			String minifiedSource = uglifyJS.compress(source);
			return minifiedSource.getBytes();
		}
		catch (ScriptException scriptException)
		{
			log.error("Error in compressJavaScript", scriptException);
			return fileContents;
		}
	}

	private byte[] compressCSS(byte[] fileContents)
	{
		String source = new String(fileContents);

		CSSCompressor compressor = new CSSCompressor(source);
		String minifiedSource = compressor.compress();

		return minifiedSource.getBytes();
	}

	private byte[] compressHTML(byte[] fileContents)
	{
		try
		{
			String source = new String(fileContents);

			String minifiedSource = htmlMinifier.minify(source);

			return minifiedSource.getBytes();
		}
		catch (ScriptException scriptException)
		{
			log.error("Error in compressHTML", scriptException);
			return fileContents;
		}
	}
}
