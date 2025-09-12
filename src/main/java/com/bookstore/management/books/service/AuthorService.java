package com.bookstore.management.books.service;

import com.bookstore.management.books.dto.AuthorDto;
import com.bookstore.management.books.model.Author;
import com.bookstore.management.books.repository.AuthorRepository;
import com.bookstore.management.shared.exception.custom.AuthorNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class AuthorService {
    @Autowired
    public AuthorRepository authorRepository;

    public List<Author> findAll(){
        return authorRepository.findAll();
    }

    public Author findById(long id){
        return authorRepository.findById(id)
                .orElseThrow(()-> new AuthorNotFoundException("Author","Id",id));
    }

    @Transactional
    public Author createAuthor(AuthorDto authorDto){

        Author author = Author.builder()
                .name(authorDto.getName())
                .nationality(authorDto.getNationality())
                .birthDate(authorDto.getBirthDate())
                .gender(authorDto.getGender())
                .biography(authorDto.getBiography())
                .build();

        log.info("Creating author with id: {}", author.getId());

        return authorRepository.save(author);
    }
    @Transactional
    public void deleteAuthorById(Long id){

        if (!authorRepository.existsById(id)){
            throw new AuthorNotFoundException("Author", "Id", id);
        }
        log.info("Deleting author with id: {}", id);
        authorRepository.deleteById(id);
    }

    @Transactional
    public Author updateAuthor(AuthorDto authorDto, Long id){
        Author author = authorRepository.findById(id)
                .orElseThrow(()-> new AuthorNotFoundException("Author","Id",id));

        author.setName(authorDto.getName());
        author.setNationality(authorDto.getNationality());
        author.setBirthDate(authorDto.getBirthDate());
        author.setGender(authorDto.getGender());
        author.setBiography(authorDto.getBiography());

        log.info("Updating author with id: {}", author.getId());

        return authorRepository.save(author);
    }

}
