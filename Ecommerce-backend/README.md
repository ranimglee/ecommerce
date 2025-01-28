## Remarks for Interns

### 1. Using `findAll` with Pagination
- **Issue**: Directly using the `findAll()` repository method without pagination can lead to performance issues, especially when dealing with large datasets.
- **Best Practice**: Always use the `findAll(Pageable pageable)` method when fetching a list of entities. This ensures the results are fetched in manageable chunks and reduces memory consumption.
- **Example**:
   ```java
   @Autowired
   private UserRepository userRepository;

   public Page<User> getAllUsers(Pageable pageable) {
       return userRepository.findAll(pageable);
   }

### 2. Handling the 2 warnings when launching the project
```java
   2025-01-28T20:05:19.970+01:00  WARN 2300 --- [Ecommerce] [main]
   r$InitializeUserDetailsManagerConfigurer : Global AuthenticationManager configured with an AuthenticationProvider bean.
   UserDetailsService beans will not be used by Spring Security for automatically configuring username/password login.
   Consider removing the AuthenticationProvider bean. Alternatively, consider using the UserDetailsService in a manually
   instantiated DaoAuthenticationProvider. If the current configuration is intentional, to turn off this warning,
   increase the logging level of
   'org.springframework.security.config.annotation.authentication.configuration.InitializeUserDetailsBeanManagerConfigurer' to ERROR.


   2025-01-28T20:05:20.288+01:00  WARN 2300 --- [Ecommerce]  main]
   ion$DefaultTemplateResolverConfiguration : Cannot find template location: classpath:/templates/ 
   (please add some templates, check your Thymeleaf configuration, or set spring.thymeleaf.check-template-location=false)
