package org.dev.cash_accounts_manager_backend.services;

import org.dev.cash_accounts_manager_backend.dtos.*;
import org.dev.cash_accounts_manager_backend.enums.RoleEnum;
import org.dev.cash_accounts_manager_backend.models.Role;
import org.dev.cash_accounts_manager_backend.models.User;
import org.dev.cash_accounts_manager_backend.repositories.RoleRepository;
import org.dev.cash_accounts_manager_backend.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private Role superAdminRole;
    private Role ordinaryUserRole;
    private Role adminRole;
    private User superAdmin;
    private User admin;
    private User ordinaryUser;

    @BeforeEach
    void setUp() {
        superAdminRole = new Role(1, RoleEnum.SUPER_ADMIN, "Super admin user", Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now()));
        adminRole = new Role(2, RoleEnum.ADMIN, "Admin user", Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now()));
        ordinaryUserRole = new Role(3, RoleEnum.USER, "Ordinary user", Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now()));
        superAdmin =  new User(1, "Conny Jose", "ConJo", "gr3@a21", Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now()), superAdminRole);
        admin =  new User(2, "Bob Tobby", "Bobson", "bo2a@231", Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now()), adminRole);
        ordinaryUser =  new User(3, "John Sonny", "JohnySon", "123fea4", Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now()), ordinaryUserRole);
    }

    @AfterEach
    void tearDown() {
        superAdmin = null;
        admin = null;
        ordinaryUser = null;
        superAdminRole = null;
        adminRole = null;
        ordinaryUserRole = null;
    }

    @Test
    void givenRoles_whenGetAllRoles_thenGetThreeRoles() {
        int rolesCount = 3;
        when(roleRepository.findAll()).thenReturn(List.of(superAdminRole, adminRole, ordinaryUserRole));

        List<RoleDto> roles = userService.getAllRoles();

        assertEquals(rolesCount, roles.size(), "Should get three roles");
        assertTrue(roles.stream().anyMatch(role -> role.code() == RoleEnum.SUPER_ADMIN));
        assertTrue(roles.stream().anyMatch(role -> role.code() == RoleEnum.ADMIN));
        assertTrue(roles.stream().anyMatch(role -> role.code() == RoleEnum.USER));
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void givenRoles_whenFindRole_thenGetUserRole() {
        when(roleRepository.findByCode(RoleEnum.USER)).thenReturn(Optional.of(ordinaryUserRole));

        RoleDto role = userService.findRole(RoleEnum.USER);

        assertNotNull(role, "Role should not be null");
        assertEquals(RoleEnum.USER, role.code(), "Found role should have code " + RoleEnum.USER);
        verify(roleRepository, times(1)).findByCode(RoleEnum.USER);
    }

    @Test
    void givenUsers_whenFindUserById_thenGetUser() {
        int userId = 3;
        when(userRepository.findById(userId)).thenReturn(Optional.of(ordinaryUser));

        UserDto user = userService.findUser(userId);

        assertNotNull(user, "User should not be null");
        assertEquals(userId, user.id(), "Found user should have id " + userId);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void givenUsers_whenFindUserByUsername_thenGetUser() {
        String username = "JohnySon";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(ordinaryUser));

        UserDto user = userService.findUser(username);

        assertNotNull(user, "User should not be null");
        assertEquals(username, user.username(), "Found user should have username " + username);
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void givenUsers_whenGetAllUsers_thenGetUsers() {
        int usersCount = 2;
        when(userRepository.findAllActive()).thenReturn(List.of(admin, ordinaryUser));

        List<UserDto> users = userService.allUsers();

        assertEquals(usersCount, users.size(), "Should get two users");
        assertTrue(users.stream().anyMatch(user -> user.id().equals(admin.getId())));
        assertTrue(users.stream().anyMatch(user -> user.id().equals(ordinaryUser.getId())));
        verify(userRepository, times(1)).findAllActive();
    }

    @Test
    void givenUsers_whenGetAllUsersPaged_thenGetPageableWithUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(superAdmin, admin, ordinaryUser), pageable, 2);

        when(userRepository.findAllActive(pageable)).thenReturn(page);
        when(userRepository.count()).thenReturn(2L);

        PagedResponse<UserDto> foundUsersPage = userService.allUsers(pageable);
        List<UserDto> foundUsers = foundUsersPage.content();

        assertEquals(1, foundUsersPage.pagesCount(), "Should contain one page");
        assertEquals(2, foundUsersPage.totalElementsCount(), "Should contain two users");
        assertTrue(foundUsers.stream().anyMatch(user -> user.id().equals(admin.getId())));
        assertTrue(foundUsers.stream().anyMatch(user -> user.id().equals(ordinaryUser.getId())));
        verify(userRepository, times(1)).findAllActive(pageable);
        verify(userRepository, times(1)).count();
    }

    @Test
    void givenNewUserInput_whenCreate_thenGetCreatedUser() {
        String username = ordinaryUser.getUsername();
        String password = ordinaryUser.getPassword();
        String fullName = ordinaryUser.getFullName();
        RoleEnum roleEnum =  RoleEnum.USER;

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(roleRepository.findByCode(roleEnum)).thenReturn(Optional.of(ordinaryUserRole));
        when(passwordEncoder.encode(password)).thenReturn(password);
        when(userRepository.save(any(User.class))).thenReturn(ordinaryUser);

        RegisterUserDto registerUserDto = new RegisterUserDto(username, password, fullName);
        UserDto createdUser = userService.create(registerUserDto,  roleEnum);

        assertNotNull(createdUser, "Created user should not be null");
        assertEquals(username, createdUser.username(), "Created user should have username " + username);
        assertEquals(password, createdUser.password(), "Created user should have password " + password);
        assertEquals(fullName, createdUser.fullName(), "Created user should have fullName " + fullName);
        verify(userRepository, times(1)).findByUsername(username);
        verify(roleRepository, times(1)).findByCode(roleEnum);
        verify(passwordEncoder, times(1)).encode(password);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void givenUser_whenDeactivate_thenUserIsDeactivated() {
        String username = "JohnySon";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(ordinaryUser));

        userService.deactivate(username);

        assertTrue(ordinaryUser.isDisabled(), "User should be disabled");
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void givenUser_whenUpdate_thenUserIsUpdated() {
        int userId = 3;
        String newUsername = "JohnyS";
        String newPassword = "123@abc";
        String newFullName = "Tom Wilson";

        when(userRepository.findById(userId)).thenReturn(Optional.of(ordinaryUser));
        when(passwordEncoder.encode(newPassword)).thenReturn(newPassword);

        UpdateUserDto updateUserDto = new UpdateUserDto(newUsername, newPassword, newFullName);
        UserDto updatedUser = userService.update(userId, updateUserDto);

        assertNotNull(updatedUser, "Updated user should not be null");
        assertEquals(newUsername, updatedUser.username(), "Updated user should have username " + newUsername);
        assertEquals(newPassword, updatedUser.password(), "Updated user should have password " + newPassword);
        assertEquals(newFullName, updatedUser.fullName(), "Updated user should have full name " + newFullName);
        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).encode(newPassword);
    }

    @Test
    void givenUsername_whenUpdatePassword_thenUserPasswordIsUpdated() {
        String username = ordinaryUser.getUsername();
        String newPassword = "123@abc";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(ordinaryUser));
        when(passwordEncoder.encode(newPassword)).thenReturn(newPassword);

        userService.updatePassword(username, newPassword);

        assertEquals(newPassword, ordinaryUser.getPassword(), "Updated user should have password " + newPassword);
        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).encode(newPassword);
    }

    @Test
    void givenUsername_whenChangeRole_thenUserRoleIsChanged() {
        String username = ordinaryUser.getUsername();
        RoleEnum newRoleCode = RoleEnum.ADMIN;

        when(roleRepository.findByCode(newRoleCode)).thenReturn(Optional.of(adminRole));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(ordinaryUser));

        userService.changeRole(username, newRoleCode);

        assertEquals(newRoleCode, ordinaryUser.getRole().getCode(), "Updated user should have role with code " + newRoleCode);
        verify(roleRepository, times(1)).findByCode(newRoleCode);
        verify(userRepository, times(1)).findByUsername(username);
    }
}
