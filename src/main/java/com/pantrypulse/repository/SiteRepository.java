package com.pantrypulse.repository;
import com.pantrypulse.model.Site;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SiteRepository extends JpaRepository<Site, Long> {
    
    Page<Site> findByCityContainingIgnoreCase(String city, Pageable pageable);
    
    Page<Site> findByStateContainingIgnoreCase(String state, Pageable pageable);
    
    @Query("SELECT s FROM Site s WHERE " +
           "(:city IS NULL OR LOWER(s.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
           "(:state IS NULL OR LOWER(s.state) LIKE LOWER(CONCAT('%', :state, '%')))")
    Page<Site> searchSites(@Param("city") String city, 
                           @Param("state") String state, 
                           Pageable pageable);
}

