package com.bookstore.management.books.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column ( name = "name", nullable = false)
    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @Column(name = "nationality", nullable = false)
    @NotBlank
    @Size(min = 2,max = 50,message = "The nationality must be between 2 and 50 characters")
    private String nationality;

    @Column(name = "date_of_birth", nullable = false)
    @NotNull
    @Past
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private Gender gender;

    @Column(name = "biography", columnDefinition = "TEXT")
    @Size(max = 2000, message = "The biography cannot exceed 2000 characters")
    private String biography;

    public Author(String name, String nationality, LocalDate dateOfBirth) {
        this.name = name;
        this.nationality = nationality;
        this.dateOfBirth = dateOfBirth;
        this.gender = Gender.PREFER_NOT_TO_SAY;
    }

    public Author(String name, String nationality, LocalDate dateOfBirth, Gender gender) {
        this.name = name;
        this.nationality = nationality;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    @Getter
    public enum Gender{
        MALE("Male"),
        FEMALE("Female"),
        OTHER("Other"),
        PREFER_NOT_TO_SAY("Prefer not to say");
        private final String displayName;

        Gender(String displayName) {
            this.displayName = displayName;
        }

    }
}

