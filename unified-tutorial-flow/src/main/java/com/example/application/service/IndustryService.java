package com.example.application.service;

import com.example.application.domain.model.Industry;
import com.example.application.domain.model.IndustryRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IndustryService {

    private final IndustryRepository industryRepository;

    IndustryService(IndustryRepository industryRepository) {
        this.industryRepository = industryRepository;
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Industry> list() {
        return industryRepository.findAll(Sort.by(Industry.PROP_NAME));
    }
}
