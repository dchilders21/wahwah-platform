package com.wahwahnetworks.platform.services;

import com.wahwahnetworks.platform.data.entities.enums.UploadStatus;
import com.wahwahnetworks.platform.models.BulkPublishModel;
import com.wahwahnetworks.platform.models.UploadMessageStatus;
import com.wahwahnetworks.platform.models.UserModel;
import com.wahwahnetworks.platform.models.web.ProductModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Brian.Bober on 8/25/2015.
 */
@Service
public class BulkPublishService
{

	private static final Logger logger = Logger.getLogger(ToolbarUploadService.class);


	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ProductService productService;

	@Transactional
	@Async
	public void updateProduct(UserModel userModel, BulkPublishModel bulkPublishModel) throws Exception
	{
		List<Integer> productIds = bulkPublishModel.getProductIds();
		String stompConnectionId = bulkPublishModel.getStompConnectionId();

		try
		{

			for (int i = 0; i < productIds.size(); i++)
			{
				int productId = productIds.get(i);
				ProductModel productModel = productService.getProductById(productId, userModel);

				SimpMessagingTemplate brokerMessagingTemplate = applicationContext.getBean(SimpMessagingTemplate.class);

				UploadMessageStatus messageStatus;

				String file = "" + productId; // A little white lie that we'll know in bulkupdate JS

				messageStatus = new UploadMessageStatus(file, UploadStatus.UPLOAD);
				brokerMessagingTemplate.convertAndSend("/topic/" + stompConnectionId, messageStatus);

				productService.requestPublishProduct(productModel.getId()); // Do not call async or this will not block and DONE will be misleading

				messageStatus = new UploadMessageStatus(file, UploadStatus.DONE);
				brokerMessagingTemplate.convertAndSend("/topic/" + stompConnectionId, messageStatus);
			}

		}
		catch (Exception ex)
		{
			logger.error("Error while uploading files", ex);
		}
	}

}
