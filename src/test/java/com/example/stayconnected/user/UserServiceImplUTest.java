package com.example.stayconnected.user;


import com.example.stayconnected.event.SuccessfulRegistrationEvent;
import com.example.stayconnected.user.enums.UserRole;
import com.example.stayconnected.user.model.User;
import com.example.stayconnected.user.repository.UserRepository;
import com.example.stayconnected.user.service.impl.UserServiceImpl;
import com.example.stayconnected.utils.exception.EmailAlreadyExists;
import com.example.stayconnected.utils.exception.UserDoesNotExist;
import com.example.stayconnected.utils.exception.UsernameAlreadyExists;
import com.example.stayconnected.wallet.model.Wallet;
import com.example.stayconnected.wallet.service.WalletService;
import com.example.stayconnected.web.dto.user.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserServiceImplUTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserServiceImpl userServiceImpl;


    @Test
    void whenUserEditProfile_thenEditHisProfileData() {


        UUID id =  UUID.randomUUID();

        ProfileEditRequest dto = ProfileEditRequest.builder()
                .firstName("Jeko")
                .lastName("Gyulev")
                .email("test@abv.bg")
                .username("jeko777")
                .build();


        User retrievedUser = User.builder()
                .firstName("Joro")
                .lastName("Jorov")
                .email(null)
                .username("Joro777")
                        .build();



        userServiceImpl.updateProfile(retrievedUser, dto);


        assertEquals("Jeko", retrievedUser.getFirstName());
        assertEquals("Gyulev", retrievedUser.getLastName());
        assertNotNull(retrievedUser.getEmail());
        verify(userRepository).save(retrievedUser);
    }



    @Test
    void whenSwitchUserRole_AndUserNotFound_thenThrowException() {

        UUID userId = UUID.randomUUID();

        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(UserDoesNotExist.class, () -> userServiceImpl.switchRole(userId));
    }

    @Test
    void whenSwitchUserRole_AndUserIsFoundAndHisRoleIsUser_thenSwitchToAdmin() {
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .role(UserRole.USER)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));


        userServiceImpl.switchRole(userId);

        assertEquals(UserRole.ADMIN, user.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void whenSwitchUserRole_AndUserIsFoundAndHisRoleIsUser_thenSwitchToUser() {
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .role(UserRole.ADMIN)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));


        userServiceImpl.switchRole(userId);

        assertEquals(UserRole.USER, user.getRole());
        verify(userRepository).save(user);
    }


    @Test
    void whenChangeUserStatus_andUserIsNotFound_thenThrow() {
        UUID id =  UUID.randomUUID();

        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(UserDoesNotExist.class, () -> userServiceImpl.switchStatus(id));
    }

    @Test
    void whenChangeUserStatus_andUserIsActive_thenSwitchToInactive() {
        UUID id =  UUID.randomUUID();

        User user = User.builder()
                .id(id)
                .isActive(true).build();

        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        userServiceImpl.switchStatus(id);

        assertFalse(user.isActive());
        verify(userRepository).save(user);
    }

    @Test
    void whenTotalActiveUsersIsZero_thenPercentageActiveUsersZero() {

        when(userRepository.findAllByOrderByRegisteredAtDescUsernameAsc()).thenReturn(Collections.emptyList());

        when(userRepository.countAllByActiveIs(true)).thenReturn(0L);


        userServiceImpl.getAllUsersOrderedByDateAndUsername();
        userServiceImpl.getTotalActiveUsers();
        BigDecimal percentageActiveUsers = userServiceImpl.getPercentageActiveUsers();

        assertEquals(BigDecimal.ZERO, percentageActiveUsers);
    }

    @Test
    void whenTotalActiveUsersIsTwo_andOneUserIsActive_andSecondUserIsInactive_thenPercentageActiveUsers50() {

        User user = User.builder().isActive(true).build();
        User user2 = User.builder().isActive(false).build();

        List<User> users = List.of(user, user2);

        when(userRepository.findAllByOrderByRegisteredAtDescUsernameAsc()).thenReturn(users);

        when(userRepository.countAllByActiveIs(true)).thenReturn(1L);

        BigDecimal percentageActiveUsers = userServiceImpl.getPercentageActiveUsers();

        assertEquals(BigDecimal.valueOf(50).setScale(2), percentageActiveUsers);
    }

    @Test
    void changePassword_whenNewPasswordDoesNotMatchConfirmationPassword_thenThrow() {

        User user = new User();

        ChangePasswordRequest dto = ChangePasswordRequest.builder()
                .newPassword("newPassword")
                .confirmPassword("notTheSamePassword")
                .build();

        assertThrows(RuntimeException.class, () -> userServiceImpl.changePassword(user, dto));
    }

    @Test
    void changePassword_whenNewPasswordMatchConfirmationPassword_thenSave() {
        User user = User.builder()
                .password("oldPassword")
                .build();

        ChangePasswordRequest dto = ChangePasswordRequest.builder()
                .newPassword("newPassword")
                .confirmPassword("newPassword")
                .build();

        when(passwordEncoder.encode(dto.getNewPassword())).thenReturn("encodedNewPassword");

        userServiceImpl.changePassword(user, dto);

        assertEquals("encodedNewPassword", user.getPassword());
        verify(userRepository).save(user);
    }


    @Test
    void whenUserRegister_AndEnterUsernameThatAlreadyExists_ThenThrow() {

        RegisterRequest dto = RegisterRequest
                .builder()
                .username("Jeko777")
                .build();

        User userWithEmailAlreadyExists = User.builder()
                .username("Jeko777")
                .build();


        when(userRepository.findByUsername(dto.getUsername())).thenReturn(Optional.of(userWithEmailAlreadyExists));

        assertThrows(UsernameAlreadyExists.class, () -> userServiceImpl.register(dto));
    }

    @Test
    void whenUserRegister_AndEnterEmailThatAlreadyExists_ThenThrow() {
        RegisterRequest dto = RegisterRequest
                .builder()
                .email("zhekogyulev@gmail.com")
                .build();

        User userWithEmailAlreadyExists = User.builder()
                .email("zhekogyulev@gmail.com")
                .build();

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(userWithEmailAlreadyExists));


        assertThrows(EmailAlreadyExists.class, () -> userServiceImpl.register(dto));
    }

    @Test
    void whenUserRegister_AndEnterCorrectCredentials_thenSave() {

        RegisterRequest dto = RegisterRequest.builder()
                .firstName("Jeko")
                .lastName("Gyulev")
                .username("Jeko777")
                .password("password")
                .email("zhekogyulev@gmail.com")
                .build();

        when(userRepository.findByUsername(dto.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());


        Wallet wallet = new Wallet();
        when(walletService.createWallet(any(User.class))).thenReturn(wallet);


        User result = userServiceImpl.register(dto);


        assertEquals("Jeko777", result.getUsername());
        assertEquals("Jeko", result.getFirstName());
        assertEquals("Gyulev", result.getLastName());
        assertEquals("zhekogyulev@gmail.com", result.getEmail());
        assertEquals(wallet, result.getWallet());


        verify(userRepository).save(any(User.class));
        verify(walletService).createWallet(any(User.class));
        verify(eventPublisher).publishEvent(any(SuccessfulRegistrationEvent.class));
    }

    @Test
    void whenUserChangeHisProfilePicture_ThenSave() {

        UpdatePhotoRequest dto =
                new UpdatePhotoRequest("https://newPhoto");

        User user = User.builder()
                .profilePictureUrl("https://previousPhoto")
                .build();

        userServiceImpl.updatePhoto(user, dto);

        assertEquals("https://newPhoto", user.getProfilePictureUrl());
    }



    // TODO:


    @Test
    void whenUserFiltersOtherUsers_andThereAreNoFiltersApplied_thenShowAllUsers() {

        FilterUserRequest dto = new FilterUserRequest("ALL", "ALL");


        User firstUser = User.builder().isActive(true).role(UserRole.USER).build();
        User secondUser = User.builder().isActive(false).role(UserRole.ADMIN).build();

        List<User> users = List.of(firstUser, secondUser) ;


        when(userRepository.findAllByOrderByRegisteredAtDescUsernameAsc()).thenReturn(users);


        List<User> filteredUsers = userServiceImpl.getFilteredUsers(dto);

        assertArrayEquals(users.toArray(), filteredUsers.toArray());
    }

    @Test
    void whenUserFiltersOtherUsers_AndThereAreActiveAndUserRoleIsUserFilters_thenShow1Users() {
        FilterUserRequest dto = new FilterUserRequest("USER", "true");

        User firstUser = User.builder().isActive(true).role(UserRole.USER).build();
        User secondUser = User.builder().isActive(false).role(UserRole.ADMIN).build();

        List<User> users = List.of(firstUser, secondUser);

        when(userRepository.findAllByRoleAndIsActiveOrderByRegisteredAtDescUsernameAsc(
                UserRole.USER, true
        )).thenReturn(List.of(firstUser));

        List<User> filteredUsers = userServiceImpl.getFilteredUsers(dto);

        assertNotEquals(users.toArray().length, filteredUsers.toArray().length);
    }

    @Test
    void whenUserFiltersOtherUsers_AndThereAreActiveAndUserRoleIsAdminFilters_thenShow1Users() {
        FilterUserRequest dto = new FilterUserRequest("ADMIN", "true");

        User firstUser = User.builder().isActive(true).role(UserRole.USER).build();
        User secondUser = User.builder().isActive(false).role(UserRole.ADMIN).build();

        List<User> users = List.of(firstUser, secondUser);

        when(userRepository.findAllByRoleAndIsActiveOrderByRegisteredAtDescUsernameAsc(
                UserRole.ADMIN, true
        )).thenReturn(List.of(firstUser));

        List<User> filteredUsers = userServiceImpl.getFilteredUsers(dto);

        assertNotEquals(users.toArray().length, filteredUsers.toArray().length);
    }


    @Test
    void whenUserFiltersOtherUsers_AndThereIsAdminRoleOnlyFilters_thenShow2Users() {

        FilterUserRequest dto = new FilterUserRequest("ADMIN", "ALL");

        User firstUser = User.builder().isActive(true).role(UserRole.USER).build();
        User secondUser = User.builder().isActive(false).role(UserRole.ADMIN).build();
        User thirdUser = User.builder().isActive(true).role(UserRole.ADMIN).build();


        List<User> users = List.of(firstUser, secondUser, thirdUser);

        when(userRepository.findAllByRoleOrderByRegisteredAtDescUsernameAsc(UserRole.ADMIN))
                .thenReturn(List.of(secondUser, thirdUser));

        List<User> filteredUsers = userServiceImpl.getFilteredUsers(dto);

        assertEquals(2, filteredUsers.size());
    }

    @Test
    void whenUserFiltersOtherUsers_andThereIsUserRoleOnlyFilters_thenShow1User() {

        FilterUserRequest dto = new FilterUserRequest("USER", "ALL");

        User firstUser = User.builder().isActive(true).role(UserRole.USER).build();
        User secondUser = User.builder().isActive(false).role(UserRole.ADMIN).build();
        User thirdUser = User.builder().isActive(true).role(UserRole.ADMIN).build();


        List<User> users = List.of(firstUser, secondUser, thirdUser);

        when(userRepository.findAllByRoleOrderByRegisteredAtDescUsernameAsc(UserRole.USER))
                .thenReturn(List.of(firstUser));

        List<User> filteredUsers = userServiceImpl.getFilteredUsers(dto);

        assertEquals(1, filteredUsers.size());
    }


    @Test
    void whenUserFiltersOtherUsers_andThereIsStatusTrueFilter_thenShowUsers() {
        FilterUserRequest dto = new FilterUserRequest("ALL", "true");

        User firstUser = User.builder().isActive(true).role(UserRole.USER).build();
        User secondUser = User.builder().isActive(false).role(UserRole.ADMIN).build();
        User thirdUser = User.builder().isActive(true).role(UserRole.ADMIN).build();

        List<User> users = List.of(firstUser, secondUser, thirdUser);

        when(userRepository.findAllByIsActiveOrderByRegisteredAtDescUsernameAsc(true))
                .thenReturn(List.of(firstUser, thirdUser));


        List<User> filteredUsers = userServiceImpl.getFilteredUsers(dto);

        assertTrue(filteredUsers.stream().allMatch(User::isActive));
    }


    @Test
    void whenUserFiltersOtherUsers_andThereIsStatusFalseFilter_thenShowUsers() {
        FilterUserRequest dto = new FilterUserRequest("ALL", "false");

        User firstUser = User.builder().isActive(true).role(UserRole.USER).build();
        User secondUser = User.builder().isActive(false).role(UserRole.ADMIN).build();
        User thirdUser = User.builder().isActive(true).role(UserRole.ADMIN).build();

        List<User> users = List.of(firstUser, secondUser, thirdUser);

        when(userRepository.findAllByIsActiveOrderByRegisteredAtDescUsernameAsc(false))
                .thenReturn(List.of(secondUser));


        List<User> filteredUsers = userServiceImpl.getFilteredUsers(dto);

        assertEquals(1,filteredUsers.size());
    }


    @Test
    void loadUserByUsername_whenUsernameExists_thenReturnUserPrincipal() {

        String username = "Jeko777";

        User user = User.builder()
                .username("Jeko777")
                .password("password")
                .role(UserRole.USER)
                .isActive(true)
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserDetails userDetails = userServiceImpl.loadUserByUsername(username);

        assertEquals(username,  userDetails.getUsername());
        assertEquals(true, userDetails.isEnabled());
        assertEquals("ROLE_USER",  userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void loadUserByUsername_whenUsernameDoesNotExist_thenThrow() {
        String username = "Username";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userServiceImpl.loadUserByUsername(username));
    }


    @Test
    void whenMethodSaveIsCalled_ThenUserIsSaved() {

        UUID userId = UUID.randomUUID();

        User user = User.builder().id(userId).build();

        when(userRepository.save(user)).thenReturn(user);

        userServiceImpl.saveUser(user);

        assertEquals(userId, user.getId());
    }






}
