//package com.eventhub.demo.model;
//
//import jakarta.persistence.*;
//import jakarta.validation.Valid;
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotEmpty;
//import lombok.Data;
//
//import java.util.List;
//
//@Entity
//@Data
//@Table(name = "users")
//public class User {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
////    @NotEmpty
////    @Valid
////    @OneToMany(mappedBy = "organizer")
////    private List<Event> events;
//
//    @NotBlank(message = "Username can't be blank")
//    private String userName;
//    @NotBlank(message = "Firstname can't be blank")
//    private String firstName;
//    @NotBlank(message = "Lastname can't be blank")
//    private String lastName;
//    @NotBlank(message = "Email can't be blank")
//    @Email(message = "Wrong email format")
//    private String email;
//    @NotBlank(message = "Provide correct phone number")
//    private String phone;
//    @NotBlank(message = "Address can't be blank")
//    private String address;
//}