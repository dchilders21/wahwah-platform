package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.data.entities.enums.AuditActionTypeEnum;
import com.wahwahnetworks.platform.lib.EdgeCastRestTemplate;
import com.wahwahnetworks.platform.models.UserModel;
import com.wahwahnetworks.platform.models.edgecast.EdgeCastMediaType;
import com.wahwahnetworks.platform.models.edgecast.EdgeCastPurgeRequest;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

/**
 * Created by jhaygood on 2/9/15.
 */

@Service
public class FilePublisherService
{

	private static final Logger log = Logger.getLogger(FilePublisherService.class);

	private final String EDGECAST_USERNAME = "noreply+ftp@wahwahnetworks.com";
	private final String EDGECAST_PASSWORD = "w@hw@hpr0";
	private final String EDGECAST_APITOKEN = "88453356-e417-4c6e-98b0-bd769a73d56f";
	private final String EDGECAST_ACCOUNT = "BA6A";

	@Autowired
	private AuditService auditService;

	private EdgeCastRestTemplate restTemplate;

	public FilePublisherService()
	{
		restTemplate = new EdgeCastRestTemplate(EDGECAST_APITOKEN);
	}

	public FTPClient getEdgeCastFTPClient() throws Exception
	{

		log.info("[FTP] Creating Connection to EdgeCast FTP");

		int MAX_ATTEMPTS = 5;
		int currentAttempt = 0;

		boolean hasConnection = false;

		FTPClient ftpClient = null;

		do
		{
			try
			{
				currentAttempt++;

				ftpClient = new FTPClient();
				ftpClient.setBufferSize(1024 * 1024);
				ftpClient.connect("ftp.cpm.BA6A.edgecastcdn.net");
				ftpClient.login(EDGECAST_USERNAME, EDGECAST_PASSWORD);
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
				ftpClient.enterLocalPassiveMode();
				hasConnection = true;

			}
			catch (FTPConnectionClosedException exception)
			{

				if (exception.getMessage().contains("421") && currentAttempt < MAX_ATTEMPTS)
				{
					log.info("[FTP] EdgeCast reports 'To Many Connections'. Waiting 1 second and re-trying");

					// To Many Connections -- Sleep a While And Try Again
					Thread.sleep(1000);
				}
				else
				{
					log.error("[FTP] EdgeCast Connection Failed. Throwing exception", exception);
					throw exception;
				}
			}
		} while (!hasConnection);

		return ftpClient;
	}

	public void purgeFileOnEdgeCast(String path)
	{
		purgeFileOnEdgeCast(null, path);
	}

	public void purgeFileOnEdgeCast(UserModel userModel, String path)
	{

		String mediaPath = "http://wac." + EDGECAST_ACCOUNT + ".edgecastcdn.net/00" + EDGECAST_ACCOUNT + "/" + path;

		EdgeCastPurgeRequest purgeRequest = new EdgeCastPurgeRequest();
		purgeRequest.setMediaPath(mediaPath);
		purgeRequest.setMediaType(EdgeCastMediaType.HTTP_SMALL);

		restTemplate.put("https://api.edgecast.com/v2/mcc/customers/{accountNumber}/edge/purge", purgeRequest, EDGECAST_ACCOUNT);

		if (userModel != null)
		{
			auditService.addAuditEntry(userModel, AuditActionTypeEnum.PURGE_REQUEST, "EdgeCast HTTP Small Purge Request Issued", path);
		}
		else
		{
			auditService.addAuditEntry(AuditActionTypeEnum.PURGE_REQUEST, "EdgeCast HTTP Small Purge Request Issued", path);
		}
	}

	public void uploadFileToEdgeCast(FTPClient ftpClient, String path, byte[] fileContents) throws Exception
	{

		path = path.replace("\\", "/");
		String fileName = path;

		String workingDirectory = ftpClient.printWorkingDirectory();
		String pendingDirectory = "/" + path.substring(0, path.lastIndexOf("/"));

		log.info("[FTP] Working Directory: " + workingDirectory);
		log.info("[FTP] Pending Working Directory: " + pendingDirectory);

		log.info("[FTP] Uploading File: " + fileName + " with content length: " + fileContents.length);

		ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContents);
		BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

		if (!workingDirectory.equals(pendingDirectory))
		{

			log.info("[FTP] Change Working Directory: " + pendingDirectory);
			boolean didInitialChange = ftpClient.changeWorkingDirectory(pendingDirectory);
			log.info("[FTP] " + ftpClient.getReplyString());

			if (!didInitialChange)
			{
				log.info("[FTP] Change Working Directory: /");
				ftpClient.changeWorkingDirectory("/");
				log.info("[FTP] " + ftpClient.getReplyString());

				while (fileName.contains("/"))
				{

					String directoryName = fileName.substring(0, fileName.indexOf('/'));
					fileName = fileName.substring(fileName.indexOf('/') + 1);

					log.info("[FTP] Change Working Directory: " + directoryName);
					boolean didChange = ftpClient.changeWorkingDirectory(directoryName);
					log.info("[FTP] " + ftpClient.getReplyString());

					if (!didChange)
					{
						log.info("[FTP] Make Directory: " + directoryName);
						ftpClient.makeDirectory(directoryName);
						log.info("[FTP] " + ftpClient.getReplyString());

						log.info("[FTP] Make Directory: " + directoryName);
						ftpClient.changeWorkingDirectory(directoryName);
						log.info("[FTP] " + ftpClient.getReplyString());
					}
				}
			}
			else
			{
				fileName = path.substring(path.lastIndexOf("/") + 1);
			}
		}
		else
		{
			fileName = path.substring(path.lastIndexOf("/") + 1);
		}

		log.info("[FTP] Store File: " + fileName);
		ftpClient.storeFile(fileName, bufferedInputStream);
		log.info("[FTP] " + ftpClient.getReplyString());

		bufferedInputStream.close();
		inputStream.close();
	}
}
