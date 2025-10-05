package com.bookstore.management.book.service;

import com.bookstore.management.book.dto.AuthorDto;
import com.bookstore.management.book.mapper.AuthorMapper;
import com.bookstore.management.book.model.Author;
import com.bookstore.management.book.repository.AuthorRepository;
import com.bookstore.management.shared.exception.custom.AuthorNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    public List<Author> findAll(){
        return authorRepository.findAll();
    }

    public Author findById(long id){
        return authorRepository.findById(id)
                .orElseThrow(()-> new AuthorNotFoundException("Author","Id",id));
    }

    @Transactional
    public Author createAuthor(AuthorDto authorDto){

        Author author =  authorMapper.toEntity(authorDto);

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
        Author existingAuthor = authorRepository.findById(id)
                .orElseThrow(()-> new AuthorNotFoundException("Author","Id",id));

        authorMapper.updateEntityFromDto(authorDto, existingAuthor);

        log.info("Updating author with id: {}", existingAuthor.getId());

        return authorRepository.save(existingAuthor);
    }

}
