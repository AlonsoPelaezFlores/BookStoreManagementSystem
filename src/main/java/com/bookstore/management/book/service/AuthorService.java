package com.bookstore.management.book.service;

import com.bookstore.management.book.dto.AuthorResponseDTO;
import com.bookstore.management.book.dto.AuthorSummaryDTO;
import com.bookstore.management.book.dto.CreateAuthorDTO;
import com.bookstore.management.book.mapper.AuthorMapper;
import com.bookstore.management.book.model.Author;
import com.bookstore.management.book.repository.AuthorRepository;
import com.bookstore.management.shared.exception.custom.ResourceNotFoundException;
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

    public List<AuthorSummaryDTO> findAll(){

        return authorMapper.toSummaryDTOList(authorRepository.findAll());
    }

    public AuthorResponseDTO findById(long id){
        return authorMapper.toResponseDTO(authorRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Author","Id",id)));
    }

    @Transactional
    public AuthorResponseDTO createAuthor(CreateAuthorDTO createAuthorDto){

        Author author =  authorMapper.toEntity(createAuthorDto);

        return authorMapper.toResponseDTO(authorRepository.save(author));
    }
    @Transactional
    public void deleteAuthorById(Long id){

        if (findById(id)==null){
            throw new ResourceNotFoundException("Author", "Id", id);
        }
        authorRepository.deleteById(id);
    }

    @Transactional
    public AuthorResponseDTO updateAuthor(CreateAuthorDTO createAuthorDto, Long id){
        Author existingAuthor = authorRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Author","Id",id));

        authorMapper.updateEntityFromDto(createAuthorDto, existingAuthor);

        return authorMapper.toResponseDTO(authorRepository.save(existingAuthor));
    }

}
