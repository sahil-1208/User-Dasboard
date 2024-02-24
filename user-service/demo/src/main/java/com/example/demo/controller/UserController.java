package com.example.demo.controller;

import com.example.demo.excel.ReadDataFromExcel;
import com.example.demo.exception.UserResponseException;
import com.example.demo.model.UserRequest;
import com.example.demo.model.UserResponse;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/multipart")
    public ResponseEntity<?> uploadUserDetailsExcel(@RequestParam("file") MultipartFile file) {

        if(ReadDataFromExcel.checkExcelFormat(file)) {
            userService.save(file);
            return ResponseEntity.ok(Map.of("message","file is upload"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("uplod excel file");

    }

    @PostMapping
    public ResponseEntity<Optional<UserResponse>> createUser(@RequestBody UserRequest userRequest) {
        UserResponse userResponse = null;
        try{
            userResponse = this.userService.create(userRequest);
            System.out.println(userRequest);
            return ResponseEntity.ok(Optional.of(userResponse));
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @RequestMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse userResponse = userService.findUserById(id);
        if(userResponse == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.of(Optional.of(userResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUserById(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        try {
            UserResponse userResponse = userService.updateUserById(id, userRequest);
            return ResponseEntity.ok().body(userResponse);
        } catch (UserResponseException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {

        try {
            userService.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        List<UserResponse> list = userService.getAllUsers();
        if(list.size()<=0){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.of(Optional.of(list));
    }

}
