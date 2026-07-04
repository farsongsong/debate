package com.example.portfolio.domain.tag.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "tags") @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) @AllArgsConstructor @Builder
public class Tag {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(unique = true, nullable = false, length = 50) private String name;
    public static Tag of(String name) { return Tag.builder().name(name.toLowerCase().trim()).build(); }
}
