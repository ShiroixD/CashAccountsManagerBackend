package org.dev.cash_accounts_manager_backend.services;

import org.dev.cash_accounts_manager_backend.dtos.*;
import org.dev.cash_accounts_manager_backend.dtos.requests.AddressRequest;
import org.dev.cash_accounts_manager_backend.dtos.requests.PersonalInfoRequest;
import org.dev.cash_accounts_manager_backend.enums.RoleEnum;
import org.dev.cash_accounts_manager_backend.models.Role;
import org.dev.cash_accounts_manager_backend.models.User;
import org.dev.cash_accounts_manager_backend.models.person.Address;
import org.dev.cash_accounts_manager_backend.models.person.PersonalInfo;
import org.dev.cash_accounts_manager_backend.repositories.AddressRepository;
import org.dev.cash_accounts_manager_backend.repositories.PersonalInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonalDataServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private PersonalInfoRepository personalInfoRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PersonalDataService personalDataService;

    private Role ordinaryUserRole;
    private User ordinaryUser;
    private Address address;
    private PersonalInfo personalInfo;

    @BeforeEach
    void setUp() {
        ordinaryUserRole = new Role(3, RoleEnum.USER, "Ordinary user", Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now()));
        ordinaryUser =  new User(3, "John Sonny", "JohnySon", "123fea4", Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now()), ordinaryUserRole);
        address = new Address(1, "Sunny St.", "12c", "45L", "Nova city", "Green state", "98-19", "Poland");
        personalInfo = new PersonalInfo(3, ordinaryUser, "Jonathan", "Sonny", "jon.son@gmail.com", "+48538231330", address, "12340991342");
    }

    @AfterEach
    void tearDown() {
        personalInfo = null;
        address = null;
        ordinaryUser = null;
        ordinaryUserRole = null;
    }

    @Test
    void givenPersonalInfo_whenGetPersonalInfo_thenGetPersonalInfo() {
        int personalInfoId = personalInfo.getId();

        when(personalInfoRepository.findById(personalInfoId)).thenReturn(Optional.of(personalInfo));

        PersonalInfoDto personalInfoDto =  personalDataService.getPersonalInfo(personalInfoId);
        assertNotNull(personalInfoDto, "Personal info should not be null");
        assertEquals(personalInfoId, personalInfoDto.id(), "Personal info id should match " + personalInfoId);

        verify(personalInfoRepository, times(1)).findById(personalInfoId);
    }

    @Test
    void givenUser_whenGetUserPersonalInfo_thenGetPersonalInfo() {
        int userId = ordinaryUser.getId();

        when(personalInfoRepository.findByOwner(userId)).thenReturn(Optional.of(personalInfo));

        PersonalInfoDto personalInfoDto =  personalDataService.getUserPersonalInfo(userId);
        assertNotNull(personalInfoDto, "Personal info should not be null");
        assertEquals(userId, personalInfoDto.owner().id(), "Personal info owner id should match " + userId);

        verify(personalInfoRepository, times(1)).findByOwner(userId);
    }

    @Test
    void givenUserNewPersonalInfo_whenAddPersonalInfo_thenAddPersonalInfo() {
        int userId = ordinaryUser.getId();
        String firstName = personalInfo.getFirstName();
        String lastName = personalInfo.getLastName();
        String email = personalInfo.getEmail();
        String phoneNumber = personalInfo.getPhoneNumber();
        String street = address.getStreet();
        String houseNumber = address.getHouseNumber();
        String apartmentNumber = address.getApartmentNumber();
        String city = address.getCity();
        String state = address.getState();
        String zipCode = address.getZipCode();
        String country = address.getCountry();
        String personalCode = personalInfo.getPersonalCode();

        when(personalInfoRepository.countByUserId(userId)).thenReturn(0);
        when(personalInfoRepository.existsByPersonalCode(personalCode)).thenReturn(false);
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        when(personalInfoRepository.save(any(PersonalInfo.class))).thenReturn(personalInfo);

        AddressRequest addressRequest = new AddressRequest(street, houseNumber, apartmentNumber, city, state, zipCode, country);
        PersonalInfoRequest personalInfoRequest = new PersonalInfoRequest(userId, firstName, lastName, email, phoneNumber, addressRequest, personalCode);

        PersonalInfoDto addedPersonalInfo = personalDataService.addPersonalInfo(userId,  personalInfoRequest);

        assertNotNull(addedPersonalInfo, "Added personal info should not be null");
        assertEquals(userId, addedPersonalInfo.id(), "Personal info id should match " + userId);
        assertEquals(firstName, addedPersonalInfo.firstName(), "First name should match " + firstName);
        assertEquals(lastName, addedPersonalInfo.lastName(), "Last name should match " + lastName);
        assertEquals(email, addedPersonalInfo.email(), "Email should match " + email);
        assertEquals(phoneNumber, addedPersonalInfo.phoneNumber(), "PhoneNumber should match " + phoneNumber);
        assertEquals(street, addedPersonalInfo.address().street(), "Address street should match " + street);
        assertEquals(houseNumber, addedPersonalInfo.address().houseNumber(), "Address house number should match " + houseNumber);
        assertEquals(apartmentNumber, addedPersonalInfo.address().apartmentNumber(), "Address apartment number should match " + apartmentNumber);
        assertEquals(city, addedPersonalInfo.address().city(), "Address city should match " + city);
        assertEquals(state, addedPersonalInfo.address().state(), "Address state should match " + state);
        assertEquals(zipCode, addedPersonalInfo.address().zipCode(), "Address zip code should match " + zipCode);
        assertEquals(country, addedPersonalInfo.address().country(), "Address country should match " + country);
        assertEquals(personalCode, addedPersonalInfo.personalCode(), "Personal code should match " + personalCode);
        verify(personalInfoRepository, times(1)).countByUserId(userId);
        verify(personalInfoRepository, times(1)).existsByPersonalCode(personalCode);
        verify(addressRepository, times(1)).save(any(Address.class));
        verify(personalInfoRepository, times(1)).save(any(PersonalInfo.class));
    }

    @Test
    void givenPersonalInfo_whenUpdatePersonalInfo_thenPersonalInfoIsUpdated() {
        int userId = ordinaryUser.getId();
        int addressId = address.getId();
        String firstName = "Kenny";
        String lastName = "Tomson";
        String email = "ken@ton.com";
        String phoneNumber = "+48238638333";
        String street = "Downy";
        String houseNumber = "77";
        String apartmentNumber = "3";
        String city = "Bottomse";
        String state = "Blank state";
        String zipCode = "12-00";
        String country = "Germany";
        String personalCode = "62140792341";

        when(personalInfoRepository.findByOwner(userId)).thenReturn(Optional.of(personalInfo));
        when(personalInfoRepository.existsByPersonalCode(personalCode)).thenReturn(false);
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        when(personalInfoRepository.save(any(PersonalInfo.class))).thenReturn(personalInfo);

        AddressRequest addressRequest = new AddressRequest(street, houseNumber, apartmentNumber, city, state, zipCode, country);
        PersonalInfoRequest personalInfoRequest = new PersonalInfoRequest(userId, firstName, lastName, email, phoneNumber, addressRequest, personalCode);

        PersonalInfoDto updatedPersonalInfo = personalDataService.updatePersonalInfo(userId,  personalInfoRequest);

        assertNotNull(updatedPersonalInfo, "Updated personal info should not be null");
        assertEquals(firstName, updatedPersonalInfo.firstName(), "First name should match " + firstName);
        assertEquals(lastName, updatedPersonalInfo.lastName(), "Last name should match " + lastName);
        assertEquals(email, updatedPersonalInfo.email(), "Email should match " + email);
        assertEquals(phoneNumber, updatedPersonalInfo.phoneNumber(), "PhoneNumber should match " + phoneNumber);
        assertEquals(street, updatedPersonalInfo.address().street(), "Address street should match " + street);
        assertEquals(houseNumber, updatedPersonalInfo.address().houseNumber(), "Address house number should match " + houseNumber);
        assertEquals(apartmentNumber, updatedPersonalInfo.address().apartmentNumber(), "Address apartment number should match " + apartmentNumber);
        assertEquals(city, updatedPersonalInfo.address().city(), "Address city should match " + city);
        assertEquals(state, updatedPersonalInfo.address().state(), "Address state should match " + state);
        assertEquals(zipCode, updatedPersonalInfo.address().zipCode(), "Address zip code should match " + zipCode);
        assertEquals(country, updatedPersonalInfo.address().country(), "Address country should match " + country);
        assertEquals(personalCode, updatedPersonalInfo.personalCode(), "Personal code should match " + personalCode);

        verify(personalInfoRepository, times(1)).findByOwner(userId);
        verify(personalInfoRepository, times(1)).existsByPersonalCode(personalCode);
        verify(addressRepository, times(1)).findById(addressId);
        verify(addressRepository, times(1)).save(any(Address.class));
        verify(personalInfoRepository, times(1)).save(any(PersonalInfo.class));
    }

    @Test
    void givenUser_whenRemovePersonalInfo_thenPersonalInfoIsRemoved() {
        int userId = ordinaryUser.getId();
        int addressId = address.getId();
        int personalInfoId = personalInfo.getId();

        when(personalInfoRepository.findByOwner(userId)).thenReturn(Optional.of(personalInfo));
        when(addressRepository.existsById(addressId)).thenReturn(true);
        doAnswer(invocation -> {
            address = null;
            return null;
        }).when(addressRepository).deleteById(addressId);
        doAnswer(invocation -> {
            personalInfo = null;
            return null;
        }).when(personalInfoRepository).deleteById(personalInfoId);

        personalDataService.removePersonalInfo(userId);

        assertNull(address, "Address should be removed");
        assertNull(personalInfo, "Personal info should be removed");
        verify(personalInfoRepository, times(1)).findByOwner(userId);
        verify(addressRepository, times(1)).deleteById(addressId);
        verify(personalInfoRepository, times(1)).deleteById(personalInfoId);

    }
}