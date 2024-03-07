package com.ars.authservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/home")
public class HomeController {

    @GetMapping(value = "/public")
    public ResponseEntity<Object> home() {
        return new ResponseEntity<>("PAGE_PUBLIC", HttpStatus.OK);
    }

    @GetMapping(value = "/admin")
    public ResponseEntity<Object> homeAdmin() {
        return new ResponseEntity<>("PAGE_ADMIN", HttpStatus.OK);
    }
}
