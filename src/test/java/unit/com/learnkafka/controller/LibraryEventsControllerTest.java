package com.learnkafka.controller;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.domain.Book;
import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.producer.LibraryEventProducer;

@WebMvcTest(controllers = LibraryEventsController.class)
@AutoConfigureMockMvc
class LibraryEventsControllerTest {

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    LibraryEventProducer libraryEventProducer;

    @Test
    void postLibrary() throws Exception {
	
	// given
	Book book = Book.builder()
		        .bookId(123)
		        .bookName("Kafka using Spring Boot")
		        .bookAuthor("Ravi")
		        .build();
	
	LibraryEvent libraryEvent = LibraryEvent.builder()
		                                 .libraryEventId(null)
		                                 .book(book)
		                                 .build();
	String json = objectMapper.writeValueAsString(libraryEvent);
	//when
	when(libraryEventProducer.sendLibraryEventsProducerRecordApproach(isA(LibraryEvent.class))).thenReturn(null);
	
	//then
	mockMvc.perform(post("/v1/libraryevent")
	       .contentType(MediaType.APPLICATION_JSON_VALUE)
	       .content(json))
	       .andExpect(status().isCreated());
    }
    
    @Test
    void postLibrary_4xx() throws Exception {
	
	// given
	Book book = Book.builder()
		        .bookId(null)
		        .bookName("Kafka using Spring Boot")
		        .bookAuthor("Ravi")
		        .build();
	
	LibraryEvent libraryEvent = LibraryEvent.builder()
		                                 .libraryEventId(null)
		                                 .book(book)
		                                 .build();
	String json = objectMapper.writeValueAsString(libraryEvent);
	//when
	when(libraryEventProducer.sendLibraryEventsProducerRecordApproach(isA(LibraryEvent.class))).thenReturn(null);
	
	//expected 
	String expectedErrorMessage = "book.bookId - must not be null";
	mockMvc.perform(post("/v1/libraryevent")
	       .contentType(MediaType.APPLICATION_JSON_VALUE)
	       .content(json))
	       .andExpect(status().is4xxClientError())
	       .andExpect(content().string(expectedErrorMessage));
    }
    
    @Test 
    void updateLibraryEvent() throws Exception{
	// given
	Book book = Book.builder()
	        .bookId(123)
	        .bookName("Kafka using Spring Boot")
	        .bookAuthor("Ravi")
	        .build();	
		
		LibraryEvent libraryEvent = LibraryEvent.builder()
			                                 .libraryEventId(123)
			                                 .book(book)
			                                 .build();
		String json = objectMapper.writeValueAsString(libraryEvent);
		//when
		when(libraryEventProducer.sendLibraryEventsProducerRecordApproach(isA(LibraryEvent.class))).thenReturn(null);
		
		//then
		mockMvc.perform(put("/v1/libraryevent")
		       .contentType(MediaType.APPLICATION_JSON_VALUE)
		       .content(json))
		       .andExpect(status().isOk());
    }
    
    @Test
    void updateLibraryEvent_withNullLibraryEventId() throws Exception{
	// given
		Book book = Book.builder()
			        .bookId(123)
			        .bookName("Kafka using Spring Boot")
			        .bookAuthor("Ravi")
			        .build();
		
		LibraryEvent libraryEvent = LibraryEvent.builder()
			                                 .libraryEventId(null)
			                                 .book(book)
			                                 .build();
		String json = objectMapper.writeValueAsString(libraryEvent);
		//when
		when(libraryEventProducer.sendLibraryEventsProducerRecordApproach(isA(LibraryEvent.class))).thenReturn(null);
		
		//expected 
		String expectedErrorMessage = "Please enter valid liberary event ID";
		mockMvc.perform(put("/v1/libraryevent")
		       .contentType(MediaType.APPLICATION_JSON_VALUE)
		       .content(json))
		       .andExpect(status().is4xxClientError())
		       .andExpect(content().string(expectedErrorMessage));
    }

}
