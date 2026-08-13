package com.hotel.packcheck.controller;

import com.hotel.packcheck.dto.TaskRequest;
import com.hotel.packcheck.entity.Hotel;
import com.hotel.packcheck.security.AdminUserDetails;
import com.hotel.packcheck.security.BellboyHeadUserDetails;
import com.hotel.packcheck.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/packcheck")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/request")
    public ResponseEntity<String> createTask(
            Authentication authentication,
            @RequestBody TaskRequest request) {

        Hotel hotel;

        if (authentication.getPrincipal()
                instanceof AdminUserDetails adminUserDetails) {

            hotel = adminUserDetails
                    .getAdmin()
                    .getHotel();

        } else if (authentication.getPrincipal()
                instanceof BellboyHeadUserDetails bellboyHeadUserDetails) {

            hotel = bellboyHeadUserDetails
                    .getBellboyHead()
                    .getHotel();

        } else {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Access denied.");
        }

        try {

            taskService.sendTaskRequest(
                    request,
                    hotel
            );

            return ResponseEntity.ok(
                    "Task request sent"
            );

        } catch (IllegalArgumentException exception) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(exception.getMessage());
        }
    }
}
//package com.hotel.packcheck.controller;
//
//import com.hotel.packcheck.dto.TaskRequest;
//import com.hotel.packcheck.service.TaskService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/packcheck")
//@RequiredArgsConstructor
//public class TaskController {
//
//    private final TaskService taskService;
//
//    @PostMapping("/request")
//    public ResponseEntity<String> createTask(
//            @RequestBody TaskRequest request) {
//
//        taskService.sendTaskRequest(request);
//
//        return ResponseEntity.ok("Task request sent");
//    }
//}