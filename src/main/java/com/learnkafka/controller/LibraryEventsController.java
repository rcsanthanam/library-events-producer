package com.learnkafka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.producer.LibraryEventProducer;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class LibraryEventsController {

    @Autowired
    LibraryEventProducer libraryEventProducer;

    //Asynch approach 
    @PostMapping("/v1/libraryevent")
    public ResponseEntity<LibraryEvent> postLibraryEvents(@RequestBody LibraryEvent libraryEvent)
	    throws JsonProcessingException {
	log.info("Asynch : Library post event..");
	libraryEventProducer.sendLibraryEvents(libraryEvent);
	log.info("Message posted successfully...");
	return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }
    
    //Asynch approach 
    @PostMapping("/v1/libraryevent/synch")
    public ResponseEntity<LibraryEvent> postLibraryEventsSynch(@RequestBody LibraryEvent libraryEvent)
	    throws Exception {
	log.info("Synch : Library post event..");
	libraryEventProducer.sendLibraryEventsSynch(libraryEvent);
	log.info("Message posted successfully...");
	return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }
    
    //Asynch approach 
    @PostMapping("/v1/libraryevent/topics")
    public ResponseEntity<LibraryEvent> postLibraryEventsWithTopics(@RequestBody LibraryEvent libraryEvent)
	    throws Exception {
	log.info("Synch : Library post event..");
	libraryEventProducer.sendLibraryEventsWithTopics(libraryEvent);
	log.info("Message posted successfully...");
	return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

}
