package com.learnkafka.producer;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.domain.LibraryEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LibraryEventProducer {

    @Autowired
    KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;
    
    private String topic = "library-events";

    public void sendLibraryEvents(LibraryEvent libraryEvent) throws JsonProcessingException {
	Integer key = libraryEvent.getLibraryEventId();
	String value = objectMapper.writeValueAsString(libraryEvent);
	ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate
		.sendDefault(key, value);
	listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {

	    @Override
	    public void onSuccess(SendResult<Integer, String> result) {
		handleSuccess(key, value, result);

	    }

	    @Override
	    public void onFailure(Throwable ex) {
		handleFailure(key, value, ex);

	    }
	});
    }
    
    public ListenableFuture<SendResult<Integer, String>> sendLibraryEventsProducerRecordApproach(
	    LibraryEvent libraryEvent) throws JsonProcessingException {
	Integer key = libraryEvent.getLibraryEventId();
	String value = objectMapper.writeValueAsString(libraryEvent);

	ProducerRecord<Integer, String> producerRecord = buildProducerRecord(topic, key, value);

	ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate
		.send(producerRecord);
	listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {

	    @Override
	    public void onSuccess(SendResult<Integer, String> result) {
		handleSuccess(key, value, result);

	    }

	    @Override
	    public void onFailure(Throwable ex) {
		handleFailure(key, value, ex);

	    }
	});

	return listenableFuture;
    }
    
    private ProducerRecord<Integer, String> buildProducerRecord(String topic, Integer key,
	    String value) {
	//Add meta data - we can use Headers to add about data/origin of data/other details
	List<Header> headers = List.of(new RecordHeader("event-source", "scanner".getBytes()));
	return new ProducerRecord<>(topic,null,key,value,headers);
    }

    public SendResult<Integer, String> sendLibraryEventsSynch(LibraryEvent libraryEvent) throws Exception {
	Integer key = libraryEvent.getLibraryEventId();
	String value = objectMapper.writeValueAsString(libraryEvent);
	 SendResult<Integer, String> sendResult = null;
	try {
	    sendResult = kafkaTemplate
	    	.sendDefault(key, value).get(1,TimeUnit.SECONDS);
	} catch (InterruptedException | ExecutionException e) {
	    log.error("Error sending the message key {} and value - {} and the exception is {}", key,
			value, e.getMessage());
	    throw e;
	}catch (Exception e) {
	    log.error("Error sending the message key : {} and value : and the exception is {}", key,
			value, e.getMessage());
	    throw e;
	}	
	log.info("Message sent: Send result : {}",sendResult.toString());
	return sendResult;
    }

    protected void handleFailure(Integer key, String value, Throwable ex) {
	log.error("Error sending the message key : {} and value : {} and the exception is {}", key,
		value, ex.getMessage());
	try {
	    throw ex;
	} catch (Throwable e) {
	    log.error("Error in onFailure {} ", e.getMessage());
	}
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
	log.info("Message sent successfully for key : {} and value : {} , partition : {} ", key,
		value, result.getRecordMetadata().partition());

    }
}
