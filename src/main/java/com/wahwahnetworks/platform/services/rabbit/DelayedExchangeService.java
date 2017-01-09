package com.wahwahnetworks.platform.services.rabbit;

import com.wahwahnetworks.platform.data.entities.MessageFailureLogEntry;
import com.wahwahnetworks.platform.data.repos.MessageFailureLogRepository;
import com.wahwahnetworks.platform.lib.JsonSerializer;
import com.wahwahnetworks.platform.lib.NameUtils;
import com.wahwahnetworks.platform.models.rabbitmq.RMQDelayBase;
import org.apache.log4j.Logger;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Created by Brian.Bober on 4/26/2016.
 */
@Service
public class DelayedExchangeService
{

	private static final Logger log = Logger.getLogger(DelayedExchangeService.class);

	private static final Integer SECONDS = 1000;
	private static final Integer INITIAL_DELAY = 1 * SECONDS;
	private static final Integer MAX_DELAY = 1800 * SECONDS;

	@Autowired
	private AmqpTemplate amqpTemplate;

	@Autowired
	private MessageFailureLogRepository messageFailureLogRepository;

	public static Integer getInitialDelay()
	{
		return checkConstraints(INITIAL_DELAY);
	}

	// The following are static b/c they are used by more than just services and controllers (e.g. constructors)

	private static final Integer LINEAR_DELAY = 750;
	public static void incrementDelayLinear(RMQDelayBase message) // Must define variable "delay"
	{
		message.setBackoffDelay(checkConstraints(message.getBackoffDelay() + LINEAR_DELAY));
	}

	private static final Integer QUADRATIC_MULTIPLIER = 3;
	public static void incrementDelayQuadratic(RMQDelayBase message) // Must define variable "delay"
	{
		message.setBackoffDelay( checkConstraints(message.getBackoffDelay() * QUADRATIC_MULTIPLIER));
	}

	// Checks new delay against constraint constants
	private static Integer checkConstraints(Integer newDelay)
	{
		if (newDelay < MAX_DELAY)
			return newDelay;
		else
			return MAX_DELAY;
	}


	public static String generateMessageUUID()
	{
		return UUID.randomUUID().toString();
	}


	// Start non-static service methods

	/* Big WARNING --
	  just returning may not count as an Ack.

	  Need to throw AmqpRejectAndDontRequeueException
	 */

    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = AmqpRejectAndDontRequeueException.class)
	public void handleException( RMQDelayBase model, String msgName, Exception exception, AmqpTemplate template)
	{
		if(template == null){
			template = amqpTemplate; // Allow using a default template
		}

		try
		{

			String desc = "Error for message " + msgName;


			model.setSuccess(false);

			try
			{
				desc = desc + " for model: " + JsonSerializer.serialize(model);
			}
			catch (Exception e)
			{
			}

			log.error(desc,exception);

			DelayedExchangeService.incrementDelayQuadratic(model);

			log.info("Delaying " + (model.getBackoffDelay()/SECONDS) + " seconds");

			template.convertAndSend("Delayed", msgName, model, message -> {
				message.getMessageProperties().getHeaders().put("x-delay", model.getBackoffDelay());
				return message;
			});
			databaseRecordError(model, msgName, desc);
			throw new AmqpRejectAndDontRequeueException("Don't requeue. Force Ack. Intentional"); // Can't figure out a better way to Ack w/o using basicAck and accessing channel. Auto Ack isn't working b/c error is being handled.
		}
		catch(Exception e)
		{
			if(e instanceof AmqpRejectAndDontRequeueException){
				throw e;
			}

            log.error(e);
		}
	}

    @Transactional(propagation = Propagation.REQUIRES_NEW)
	public void startListener( RMQDelayBase model)
	{
		try
		{
			MessageFailureLogEntry entry;

			model.setSuccess(true); // May be set false in handleException

			try
			{
				entry = messageFailureLogRepository.findByUid(model.getUid());
			}
			catch (Exception e)
			{
				log.warn("Unknown database error in startListener.", e);
				throw new AmqpRejectAndDontRequeueException("AMQP failed in startListener. Don't requeue with no safe-guards");
			}

			if (entry == null) // Doesn't already exist.
				return;

			if (entry != null && (entry.getResolved() || model.getBackoffDelay() < entry.getLatestDelayInMillis()))
			{

				String modelDesc = model.getUid();
				try
				{
					modelDesc = JsonSerializer.serialize(model);
				}
				catch (Exception f)
				{
				}
				log.warn("AMQP requeued message when it shouldn't. " + modelDesc);
				throw new AmqpRejectAndDontRequeueException("AMQP requeued message when it shouldn't. " + model.getBackoffDelay() + " < " + entry.getLatestDelayInMillis()); // Can't figure out a better way to Ack w/o using basicAck and accessing channel. Auto Ack isn't working b/c error is being handled.
			}
		}
		catch(Exception e)
		{
			if(e instanceof AmqpRejectAndDontRequeueException){
				throw e;
			}

			log.error(e);
		}
	}

    @Transactional(propagation = Propagation.REQUIRES_NEW)
	public void endListener( RMQDelayBase model)
	{
		try
		{
			if (model.isSuccess())
			{
				databaseMarkErrorResolved(model);
			}
		}
		catch(Exception e)
		{
			if(e instanceof AmqpRejectAndDontRequeueException){
				throw e;
			}

			log.error(e);
		}
	}

	private void databaseRecordError(RMQDelayBase model, String msgName, String description)
	{

		MessageFailureLogEntry entry;

		try {
			entry = messageFailureLogRepository.findByUid(model.getUid());
			description = NameUtils.modifyNameWithPrependAndSuffix(description, 1000, "", "" );
			msgName = NameUtils.modifyNameWithPrependAndSuffix(msgName, 250, "", "" );


			final Instant now = Instant.now();

			if (entry == null)
			{
				entry = new MessageFailureLogEntry();
				entry.setUid(model.getUid());
				entry.setDescription(description);
				entry.setName(msgName);
				entry.setFailureCount(0);
				entry.setResolved(false);
				entry.setEntryTime(now);
			}
			else
			{
				entry.setFailureCount(entry.getFailureCount() + 1);
			}

			entry.setEntryTimeLastOccurrence(now);
			entry.setLatestDelayInMillis(model.getBackoffDelay());
			messageFailureLogRepository.save(entry);
		}
		catch(Exception e) // Shouldn't happen, but it does
		{
			String modelDesc = model.getUid();
			try
			{
				modelDesc =  JsonSerializer.serialize(model);
			}
			catch (Exception f) {}
			log.error("Gave up in databaseRecordError for model " + modelDesc,e);
			throw new AmqpRejectAndDontRequeueException("AMQP failed to record error. Possible timing issue."); // Can't figure out a better way to Ack w/o using basicAck and accessing channel. Auto Ack isn't working b/c error is being handled.
		}
	}

	private void databaseMarkErrorResolved(RMQDelayBase model)
	{
		MessageFailureLogEntry entry;

		try
		{
			log.info("Marking delay model error for message uid " + model.getUid() + " as resolved");

            entry = messageFailureLogRepository.findByUid(model.getUid());

			if (entry != null) // Should be 100%
			{
				entry.setResolved(true);
				entry.setEntryTimeResolved(Instant.now());
				messageFailureLogRepository.save(entry);
			}
		}
		catch(Exception e)
		{
			String modelDesc = model.getUid();

            try
			{
				modelDesc =  JsonSerializer.serialize(model);
			}
			catch (Exception f) {}

			log.error("Gave up in databaseMarkErrorResolved for model " + modelDesc,e);
			throw new AmqpRejectAndDontRequeueException("AMQP failed to record success. Possible timing issue."); // Can't figure out a better way to Ack w/o using basicAck and accessing channel. Auto Ack isn't working b/c error is being handled.

		}
	}

}
