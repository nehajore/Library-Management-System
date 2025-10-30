package com.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    private Long id;
    private String title;
    private String isbn;
    private Long authorId;
    private String authorName;
    private String publisher;
    private Integer publicationYear;
    private Integer totalCopies;
    private Integer availableCopies;
    private String description;
    private String category;
    private String language;
    private Integer pages;
}

