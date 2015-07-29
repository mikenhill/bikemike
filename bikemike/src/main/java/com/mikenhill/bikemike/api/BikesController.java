package com.mikenhill.bikemike.api;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mikenhill.bikemike.persistence.repositories.BikeRepository;

/**
 * @author Michael J. Simons, 2014-02-19
 */
@RestController
public class BikesController {

    private final BikeRepository bikeRepository;

    @Autowired
    public BikesController(final BikeRepository bikeRepository) {
	this.bikeRepository = bikeRepository;
    }

    @RequestMapping(value = "/api/bikes", method = GET)
    public List<Bike> getBikes(final @RequestParam(required = false, defaultValue = "false") boolean all) {
	List<Bike> rv;
	if(all)
	    rv = bikeRepository.findAll(new Sort(Sort.Direction.ASC, "boughtOn", "decommissionedOn", "name"));
	else
	    rv = bikeRepository.findByDecommissionedOnIsNull(new Sort(Sort.Direction.ASC, "name"));
	return rv;
    }
    
    @RequestMapping(value = "/api/bikes/{id:\\d+}/milages", method = POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Milage> createMilage(final @PathVariable Integer id, final @RequestBody @Valid NewMilageCmd cmd, final BindingResult bindingResult) {	
	if(bindingResult.hasErrors())
	    throw new IllegalArgumentException("Invalid arguments.");
	
	final Bike bike = bikeRepository.findOne(id);
	
	ResponseEntity<Milage> rv;	
	if(bike == null)
	    rv = new ResponseEntity<>(HttpStatus.NOT_FOUND);
	else {
	    final Milage milage = bike.addMilage(cmd.recordedOnAsLocalDate(), cmd.getAmount());
	    this.bikeRepository.save(bike);

	    rv = new ResponseEntity<>(milage, HttpStatus.OK);
	}
	
	return rv;
    }
    
    @RequestMapping(value = "/api/bikes", method = POST) 
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Bike> createBike(final @RequestBody @Valid NewBikeCmd newBikeCmd, final BindingResult bindingResult) {
	if(bindingResult.hasErrors())
	    throw new IllegalArgumentException("Invalid arguments.");
	
	ResponseEntity<Bike> rv;
	
	final Bike bike = new Bike(newBikeCmd.getName(), newBikeCmd.boughtOnAsLocalDate());
	bike.setColor(newBikeCmd.getColor());
	bike.addMilage(newBikeCmd.boughtOnAsLocalDate().withDayOfMonth(1), 0);
	
	try {
	    this.bikeRepository.save(bike);
	    rv = new ResponseEntity<>(bike, HttpStatus.OK);
	} catch(DataIntegrityViolationException e) {	    
	    rv = new ResponseEntity<>(HttpStatus.CONFLICT);
	}
	
	return rv;
    }
    
    @ExceptionHandler(IllegalArgumentException.class)    
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) throws Exception {	
	return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}