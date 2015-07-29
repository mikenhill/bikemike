package com.mikenhill.bikemike.persistence.repositories;

import java.util.Calendar;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;

public interface BikeRepository {
	public List<Bike> findActive(final @Param("cutoffDate") Calendar cutoffDate);
    
    public Bike findByName(final String name);
 
    public List<Bike> findByDecommissionedOnIsNull(final Sort sort);
    
    public Calendar getDateOfFirstRecord();
}
