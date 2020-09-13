package com.learnkafka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.domain.LibraryEventType;
import com.learnkafka.producer.LibraryEventProducer;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class LibraryEventsController {

    @Autowired
    LibraryEventProducer libraryEventProducer;

  
    @PostMapping("/v1/libraryevent/topics")
    public ResponseEntity<LibraryEvent> postLibraryEventsWithTopics(@RequestBody LibraryEvent libraryEvent)
	    throws Exception {
	log.info("Synch : Library post event..");
	libraryEvent.setLibraryEventType(LibraryEventType.NEW);
	libraryEventProducer.sendLibraryEventsProducerRecordApproach(libraryEvent);
	log.info("Message posted successfully...");
	return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

}
