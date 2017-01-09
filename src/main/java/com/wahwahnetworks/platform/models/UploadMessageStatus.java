package com.wahwahnetworks.platform.models;

import com.wahwahnetworks.platform.data.entities.enums.UploadStatus;

/**
 * Created by Justin on 6/7/2014.
 */
public class UploadMessageStatus
{
	private String fileName;
	private UploadStatus status;

	public UploadMessageStatus(String fileName, UploadStatus status)
	{
		this.fileName = fileName;
		this.status = status;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public UploadStatus getStatus()
	{
		return status;
	}

	public void setStatus(UploadStatus status)
	{
		this.status = status;
	}
}
