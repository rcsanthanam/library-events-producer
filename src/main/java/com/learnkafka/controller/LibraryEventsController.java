package com.learnkafka.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PostMapping("/v1/libraryevent")
    public ResponseEntity<LibraryEvent> postLibraryEvents(@RequestBody @Valid LibraryEvent libraryEvent)
	    throws Exception {
	log.info("Synch : Library post event..");
	libraryEvent.setLibraryEventType(LibraryEventType.NEW);
	libraryEventProducer.sendLibraryEventsProducerRecordApproach(libraryEvent);
	log.info("Message posted successfully...");
	return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }
    
    @PutMapping("/v1/libraryevent")
    public ResponseEntity<?> putLibraryEvents(@RequestBody @Valid LibraryEvent libraryEvent)
	    throws Exception {
	log.info("Synch : Library put event..");
	if(StringUtils.isEmpty(libraryEvent.getLibraryEventId())) {
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please enter valid liberary event ID");
	}
	libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);
	libraryEventProducer.sendLibraryEventsProducerRecordApproach(libraryEvent);
	log.info("Message updated successfully...");
	return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
    }

}
