package com.library.service;

import com.library.dto.AuthorDto;
import com.library.entity.Author;
import com.library.exception.BadRequestException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    public List<AuthorDto> getAllAuthors() {
        return authorRepository.findAll().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public AuthorDto getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
        return convertToDto(author);
    }

    public List<AuthorDto> searchAuthors(String name) {
        return authorRepository.findByNameContaining(name).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public AuthorDto createAuthor(AuthorDto authorDto) {
        Author author = new Author();
        author.setFirstName(authorDto.getFirstName());
        author.setLastName(authorDto.getLastName());
        author.setBiography(authorDto.getBiography());
        author.setDateOfBirth(authorDto.getDateOfBirth());
        author.setNationality(authorDto.getNationality());
        
        Author savedAuthor = authorRepository.save(author);
        return convertToDto(savedAuthor);
    }

    public AuthorDto updateAuthor(Long id, AuthorDto authorDto) {
        Author author = authorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
        
        author.setFirstName(authorDto.getFirstName());
        author.setLastName(authorDto.getLastName());
        author.setBiography(authorDto.getBiography());
        author.setDateOfBirth(authorDto.getDateOfBirth());
        author.setNationality(authorDto.getNationality());
        
        Author updatedAuthor = authorRepository.save(author);
        return convertToDto(updatedAuthor);
    }

    public void deleteAuthor(Long id) {
        Author author = authorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
        
        if (!author.getBooks().isEmpty()) {
            throw new BadRequestException("Cannot delete author with associated books");
        }
        
        authorRepository.delete(author);
    }

    private AuthorDto convertToDto(Author author) {
        AuthorDto dto = new AuthorDto();
        dto.setId(author.getId());
        dto.setFirstName(author.getFirstName());
        dto.setLastName(author.getLastName());
        dto.setBiography(author.getBiography());
        dto.setDateOfBirth(author.getDateOfBirth());
        dto.setNationality(author.getNationality());
        return dto;
    }
}

