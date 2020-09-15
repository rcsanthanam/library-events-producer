package com.learnkafka.domain;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Book {
    
    @NotNull
    private Integer bookId;
    @NotEmpty
    private String bookName;
    @NotEmpty
    private String bookAuthor;
    
}
