package org.dev.cash_accounts_manager_backend.services;

import org.dev.cash_accounts_manager_backend.dtos.*;
import org.dev.cash_accounts_manager_backend.enums.RoleEnum;
import org.dev.cash_accounts_manager_backend.exceptions.UserAlreadyExistAuthenticationException;
import org.dev.cash_accounts_manager_backend.exceptions.UserNotFoundException;
import org.dev.cash_accounts_manager_backend.exceptions.UserRoleNotExist;
import org.dev.cash_accounts_manager_backend.models.Role;
import org.dev.cash_accounts_manager_backend.models.User;
import org.dev.cash_accounts_manager_backend.repositories.RoleRepository;
import org.dev.cash_accounts_manager_backend.repositories.UserRepository;
import org.dev.cash_accounts_manager_backend.utils.Extensions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service logic implementation for users<br>
 * It provides business logic of actions with accounts of users
 *
 * @author Fabian Frontczak
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final LogService logService;

    /**
     * Class constructor injecting dependencies and initializing necessary data
     */
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, LogService logService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.logService = logService;
    }

    /**
     *  Method for getting all existing user roles
     *  @return list of all existing user roles
     */
    public List<RoleDto> getAllRoles() {
        List<Role> roles = new ArrayList<>();

        roleRepository.findAll().forEach(roles::add);

        return roles.stream().map(Extensions::asDto).collect(Collectors.toList());
    }

    /**
     *  Method for getting chosen role data
     *  @param roleEnum enum value representing role type
     *  @return role data transformed to DTO
     */
    public RoleDto findRole(RoleEnum roleEnum) {
        Optional<Role> role = roleRepository.findByCode(roleEnum);

        return role.map(Extensions::asDto).orElse(null);
    }

    /**
     *  Method for getting chosen user by id
     *  @param id user id
     *  @return user transformed to DTO
     */
    public UserDto findUser(Integer id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new UserNotFoundException("User with id " + id + " not found");
        }

        return Extensions.asDto(user.get());
    }

    /**
     *  Method for getting chosen user by username
     *  @param username username
     *  @return user transformed to DTO
     */
    public UserDto findUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }

        return Extensions.asDto(user.get());
    }

    /**
     *  Method for getting currently authenticated user
     *  @return user transformed to DTO
     */
    public UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = ((User) authentication.getPrincipal());

        return  Extensions.asDto(authenticatedUser);
    }

    /**
     *  Method for getting all active users except super admin
     *  @return list of all users transformed to DTO
     */
    public List<UserDto> allUsers() {
        List<User> users = userRepository.findAllActive();

        return users.stream().map(Extensions::asDto).collect(Collectors.toList());
    }

    /**
     *  Method for getting all active users except super admin selected as filtered page
     *  @param pageable page details
     *  @return paged list of all users transformed to DTO
     */
    public PagedResponse<UserDto> allUsers(Pageable pageable) {
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();

        Pageable adjustedPageable = PageRequest.of(pageNumber, pageSize, pageable.getSort());;
        Page<User> page = userRepository.findAllActive(adjustedPageable);
        List<UserDto> pageUsers = page.getContent().stream().map(Extensions::asDto).collect(Collectors.toList());

        long currentPageElementsCount = pageUsers.size();
        long totalElementsCount = userRepository.count();
        long totalPagesCount = (totalElementsCount / pageSize) + (totalElementsCount % pageSize > 0 ? 1 : 0);

        return new PagedResponse<>(pageUsers, pageNumber, pageSize, totalPagesCount, currentPageElementsCount, totalElementsCount);
    }

    /**
     *  Method for creating new user
     *  @param input basic user data
     *  @param roleEnum enum value representing role that should be assigned to user
     *  @return created user transformed to DTO
     */
    public UserDto create(RegisterUserDto input, RoleEnum roleEnum) {
        if (userRepository.findByUsername(input.username()).isPresent()) {
            throw new UserAlreadyExistAuthenticationException("Cannot signup because user already exists");
        }

        Optional<Role> optionalRole = roleRepository.findByCode(roleEnum);

        if (optionalRole.isEmpty()) {
            throw new UserRoleNotExist(roleEnum);
        }

        User user = new User(input.fullName(), input.username(), passwordEncoder.encode(input.password()), optionalRole.get());
        user = userRepository.save(user);

        return Extensions.asDto(user);
    }

    /**
     *  Method for deactivating user
     *  @param username username
     */
    public void deactivate(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }

        User user = optionalUser.get();

        if (!user.isDisabled()) {
            user.setDisabled(true);
        }
    }

    /**
     *  Method for updating existing user
     *  @param id user id
     *  @param updateUserDto user data to update
     *  @return updated user transformed to DTO
     */
    public UserDto update(Integer id, UpdateUserDto updateUserDto) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("Account with id " + id + " not found");
        }

        User user = optionalUser.get();

        if (updateUserDto.username() != null && !updateUserDto.username().isBlank()) {
            user.setUsername(updateUserDto.username());
        }

        if (updateUserDto.password() != null && !updateUserDto.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(updateUserDto.password()));
        }

        if (updateUserDto.fullName() != null && !updateUserDto.fullName().isBlank()) {
            user.setFullName(updateUserDto.fullName());
        }

        return Extensions.asDto(user);
    }

    /**
     *  Method for updating user password
     *  @param username username
     *  @param password password value
     */
    public void updatePassword(String username, String password) {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }

        User user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(password));
    }

    /**
     *  Method for assigning new role to user
     *  @param username username
     *  @param roleEnum enum value representing role that should be assigned to user
     *  @return updated user transformed to DTO
     */
    public UserDto changeRole(String username, RoleEnum roleEnum) {
        Optional<Role> optionalRole = roleRepository.findByCode(roleEnum);

        if (optionalRole.isEmpty()) {
            throw new UserRoleNotExist(roleEnum);
        }

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }

        User user = optionalUser.get();
        user.setRole(optionalRole.get());

        return Extensions.asDto(user);
    }
}
