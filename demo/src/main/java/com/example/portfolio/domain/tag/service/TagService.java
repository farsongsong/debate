package com.example.portfolio.domain.tag.service;

import com.example.portfolio.domain.tag.entity.Tag;
import com.example.portfolio.domain.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    @Transactional
    public Tag findOrCreate(String name) {
        return tagRepository.findByName(name.toLowerCase().trim())
                .orElseGet(() -> tagRepository.save(Tag.of(name)));
    }
}
