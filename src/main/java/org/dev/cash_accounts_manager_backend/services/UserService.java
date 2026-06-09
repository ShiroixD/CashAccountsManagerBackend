package org.dev.cash_accounts_manager_backend.services;

import org.dev.cash_accounts_manager_backend.dtos.*;
import org.dev.cash_accounts_manager_backend.enums.RoleEnum;
import org.dev.cash_accounts_manager_backend.exceptions.UserAlreadyExistAuthenticationException;
import org.dev.cash_accounts_manager_backend.exceptions.UserNotFound;
import org.dev.cash_accounts_manager_backend.exceptions.UserRoleNotExist;
import org.dev.cash_accounts_manager_backend.models.Role;
import org.dev.cash_accounts_manager_backend.models.User;
import org.dev.cash_accounts_manager_backend.repositories.RoleRepository;
import org.dev.cash_accounts_manager_backend.repositories.UserRepository;
import org.dev.cash_accounts_manager_backend.utils.Extensions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<RoleDto> allRoles() {
        List<Role> roles = new ArrayList<>();

        roleRepository.findAll().forEach(roles::add);

        return roles.stream().map(Extensions::asDto).collect(Collectors.toList());
    }

    public RoleDto findRole(RoleEnum roleEnum) {
        Optional<Role> role = roleRepository.findByCode(roleEnum);

        return role.map(Extensions::asDto).orElse(null);
    }

    public UserDto user(Integer id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new UserNotFound("User with id " + id + " not found");
        }

        return Extensions.asDto(user.get());
    }

    public UserDto user(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }

        return Extensions.asDto(user.get());
    }

    public List<UserDto> allUsers() {
        List<User> users = userRepository.findAllActive();

        List<UserDto> userDtos = users.stream()
                .map(Extensions::asDto)
                .filter(x -> !x.role().code().equals(RoleEnum.SUPER_ADMIN) && !x.disabled())
                .collect(Collectors.toList());

        return userDtos;
    }

    public PagedResponse<UserDto> allUsers(Pageable pageable, boolean includeSuperAdmin) {
        Pageable adjustedPageable = null;
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();

        if (includeSuperAdmin) {
            adjustedPageable = PageRequest.of(pageable.getPageNumber(), pageSize, pageable.getSort());
        } else {
            adjustedPageable = PageRequest.of(pageable.getPageNumber(), pageSize + 1, pageable.getSort());
        }

        Page<User> page = userRepository.findAllActive(adjustedPageable);

        List<UserDto> pageUsers = page.getContent().stream()
                .map(Extensions::asDto)
                .filter(x -> !x.role().code().equals(RoleEnum.SUPER_ADMIN) && !x.disabled())
                .collect(Collectors.toList());

        int currentPageElementsCount = pageUsers.size();
        int totalElementsCount = Math.abs(Math.toIntExact(userRepository.count()) - 1);
        int totalPagesCount = (totalElementsCount / pageSize) + (totalElementsCount % pageSize > 0 ? 1 : 0);

        return new PagedResponse<UserDto>(pageUsers, pageNumber, pageSize, totalPagesCount, currentPageElementsCount, totalElementsCount);
    }

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

    public void deactivate(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }

        User user = optionalUser.get();
        user.setDisabled(true);

        userRepository.save(user);
    }

    public UserDto update(Integer id, UpdateUserDto updateUserDto) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            throw new UserNotFound("Account with id " + id + " not found");
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

        return Extensions.asDto(userRepository.save(user));
    }

    public void updatePassword(String username, String password) {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }

        User user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
    }

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
