package com.learnkafka.producer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.domain.Book;
import com.learnkafka.domain.LibraryEvent;

@ExtendWith(MockitoExtension.class)
class LibraryEventProducerTest {

    @Mock
    KafkaTemplate<Integer, String> kafkaTemplate;
    
    @Spy
    ObjectMapper objectMapper = new ObjectMapper();
    
    @InjectMocks
    LibraryEventProducer libraryEventProducer;
    
    @Test
    void sendLibraryEvents_Failure() throws Exception{
	
	//given 
	Book book = Book.builder()
	        .bookId(123)
	        .bookName("Kafka using Spring Boot")
	        .bookAuthor("Ravi")
	        .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
	                                 .libraryEventId(null)
	                                 .book(book)
	                                 .build();
        
        SettableListenableFuture<SendResult<Integer, String>> future = new SettableListenableFuture<>();
        future.setException(new RuntimeException("Exception calling Kafka"));
	
        //when
         when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);
        
	//then
        assertThrows(Exception.class, () -> libraryEventProducer.sendLibraryEventsProducerRecordApproach(libraryEvent).get());
    }
    
    
    
    
}
