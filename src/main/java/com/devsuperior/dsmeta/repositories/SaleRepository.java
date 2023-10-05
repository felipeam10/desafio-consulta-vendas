package com.devsuperior.dsmeta.repositories;

import com.devsuperior.dsmeta.dto.SaleMinDTO;
import com.devsuperior.dsmeta.dto.SellerSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.devsuperior.dsmeta.entities.Sale;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {


    @Query(value = "SELECT new com.devsuperior.dsmeta.dto.SaleMinDTO(obj.id, obj.amount, obj.date, obj.seller.name) "
            + "FROM Sale obj "
            + "WHERE (:startDate IS NULL OR obj.date >= :startDate) AND "
            + "(:endDate IS NULL OR obj.date <= :endDate) AND "
            + "(:name IS NULL OR UPPER(obj.seller.name) LIKE UPPER(CONCAT('%', :name, '%')))")
    Page<SaleMinDTO> getReport(@Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate,
                               @Param("name") String name,
                               Pageable pageable);

    @Query(value = "SELECT new com.devsuperior.dsmeta.dto.SellerSummaryDTO(obj.seller.name, SUM(obj.amount)) "
            + "FROM Sale obj WHERE obj.date BETWEEN :minDate AND :maxDate AND "
            + "(COALESCE(:name, '') = '' or obj.seller.name = :name) "
            + "GROUP BY obj.seller.name")
    List<SellerSummaryDTO> getSummary(@Param("minDate") LocalDate minDate,
                                      @Param("maxDate") LocalDate maxDate,
                                      @Param("name") String name);

}
