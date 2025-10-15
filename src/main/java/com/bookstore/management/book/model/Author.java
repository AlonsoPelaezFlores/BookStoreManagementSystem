package com.bookstore.management.book.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "authors")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column ( name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "nationality", nullable = false, length = 100)
    private String nationality;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(name = "biography", columnDefinition = "TEXT")
    private String biography;

    public Author(String name, String nationality, LocalDate dateOfBirth) {
        this.name = name;
        this.nationality = nationality;
        this.birthDate = dateOfBirth;
        this.gender = Gender.PREFER_NOT_TO_SAY;
    }

    public Author(String name, String nationality, LocalDate dateOfBirth, Gender gender) {
        this.name = name;
        this.nationality = nationality;
        this.birthDate = dateOfBirth;
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

