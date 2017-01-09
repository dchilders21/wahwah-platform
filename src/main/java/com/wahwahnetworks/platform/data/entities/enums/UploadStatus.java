package com.wahwahnetworks.platform.data.entities.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wahwahnetworks.platform.data.entities.User;

import javax.persistence.*;


/**
 * Created by Brian.Bober on 8/25/2015.
 */

// Used by UploadMessageStatus
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum UploadStatus
{
	UPLOAD, DONE, ERROR
}