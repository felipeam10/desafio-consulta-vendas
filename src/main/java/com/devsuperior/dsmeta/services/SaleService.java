package com.devsuperior.dsmeta.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import com.devsuperior.dsmeta.dto.SellerSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.devsuperior.dsmeta.dto.SaleMinDTO;
import com.devsuperior.dsmeta.entities.Sale;
import com.devsuperior.dsmeta.repositories.SaleRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaleService {

	@Autowired
	private SaleRepository repository;

	@Transactional(readOnly = true)
	public SaleMinDTO findById(Long id) {
		Optional<Sale> result = repository.findById(id);
		Sale entity = result.get();
		return new SaleMinDTO(entity);
	}

	@Transactional(readOnly = true)
	public Page<SaleMinDTO> getReport(String minDateStr, String maxDateStr, String name, Pageable pageable) {

		LocalDate minDate;
		LocalDate maxDate = LocalDate.ofInstant(Instant.now(), ZoneId.systemDefault());

		try {
			maxDate = (maxDateStr != null && !maxDateStr.isEmpty()) ? LocalDate.parse(maxDateStr) : LocalDate.now();
		} catch (DateTimeParseException e) {
			throw getIllegalArgumentException(e);
		}


		if (minDateStr != null && !minDateStr.isEmpty()) {
			try {
				minDate = LocalDate.parse(minDateStr);
			} catch (DateTimeParseException e) {
				throw getIllegalArgumentException(e);
			}
		} else {
			minDate = maxDate.minusYears(1L);
		}

		name = name != null ? name : "";

		return repository.getReport(minDate, maxDate, name, pageable);
	}


	@Transactional(readOnly = true)
	public List<SellerSummaryDTO> getSummary(String minDate, String maxDate, String name) {
		LocalDate end = (maxDate != null) ? LocalDate.parse(maxDate) : LocalDate.ofInstant(Instant.now(), ZoneId.systemDefault());
		LocalDate start = (minDate != null) ? LocalDate.parse(minDate) : end.minusYears(1L);
		return repository.getSummary(start, end, name);
	}

	private static IllegalArgumentException getIllegalArgumentException(DateTimeParseException e) {
		return new IllegalArgumentException("Data inicial com formato inválido. Favor usar o formato aaaa-MM-dd.", e);
	}

}
