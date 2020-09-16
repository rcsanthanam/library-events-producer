package com.learnkafka.producer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
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
    
    @Test
    void sendLibraryEvents_Success() throws Exception{
	
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
        Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);
       
        SettableListenableFuture<SendResult<Integer, String>> future = new SettableListenableFuture<>();
        ProducerRecord<Integer, String> producerRecord = new ProducerRecord<Integer, String>("library-events", key,value);
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("library-events", 1), 1, 1, 3245, 12345678L, 1, 2);
        SendResult<Integer, String> sendResult = new SendResult<>(producerRecord, recordMetadata);
        future.set(sendResult);
	
        //when
         when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);
        
	//then
         ListenableFuture<SendResult<Integer, String>> listenableFuture = libraryEventProducer.sendLibraryEventsProducerRecordApproach(libraryEvent);
         SendResult<Integer, String> result = listenableFuture.get();
         
         assertEquals(result.getRecordMetadata().partition(),1);
         assertEquals("library-events",result.getProducerRecord().topic());
    }    
}
