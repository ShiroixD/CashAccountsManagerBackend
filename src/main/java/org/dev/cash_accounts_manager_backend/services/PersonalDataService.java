package org.dev.cash_accounts_manager_backend.services;

import org.dev.cash_accounts_manager_backend.dtos.PersonalInfoDto;
import org.dev.cash_accounts_manager_backend.dtos.UserDto;
import org.dev.cash_accounts_manager_backend.dtos.requests.AddressRequest;
import org.dev.cash_accounts_manager_backend.dtos.requests.PersonalInfoRequest;
import org.dev.cash_accounts_manager_backend.exceptions.DataAlreadyExistsException;
import org.dev.cash_accounts_manager_backend.exceptions.NotFoundException;
import org.dev.cash_accounts_manager_backend.exceptions.ValidationError;
import org.dev.cash_accounts_manager_backend.models.person.Address;
import org.dev.cash_accounts_manager_backend.models.person.PersonalInfo;
import org.dev.cash_accounts_manager_backend.repositories.AddressRepository;
import org.dev.cash_accounts_manager_backend.repositories.PersonalInfoRepository;
import org.dev.cash_accounts_manager_backend.utils.Extensions;
import org.dev.cash_accounts_manager_backend.utils.Logger;
import org.dev.cash_accounts_manager_backend.utils.Validators;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service logic implementation for personal info<br>
 * It provides business logic of actions with personal info, addresses
 *
 * @author Fabian Frontczak
 */
@Service
public class PersonalDataService {
    private final AddressRepository addressRepository;
    private final PersonalInfoRepository personalInfoRepository;
    private final UserService userService;

    /**
     * Class constructor injecting dependencies and initializing necessary data
     */
    public PersonalDataService(AddressRepository addressRepository, PersonalInfoRepository personalInfoRepository, UserService userService) {
        this.addressRepository = addressRepository;
        this.personalInfoRepository = personalInfoRepository;
        this.userService = userService;
    }

    /**
     *  Personal info data validation method
     *  @throws ValidationError in case of validation failure with description
     */
    private void checkPersonalInfoData(PersonalInfoRequest personalInfoRequest) throws ValidationError {
        String validationResult = Validators.validate(personalInfoRequest);

        if (!validationResult.isBlank()) {
            Logger.log("Validation error: \n" + validationResult);
            throw new ValidationError(validationResult);
        }
    }

    private void checkAddressData(AddressRequest addressRequest) throws ValidationError {
        String validationResult = Validators.validate(addressRequest);

        if (!validationResult.isBlank()) {
            Logger.log("Validation error: \n" + validationResult);
            throw new ValidationError(validationResult);
        }
    }

    /**
     *  Method for getting chosen personal info data
     *  @param id personal info id
     *  @return personal infos transformed to DTO
     */
    public PersonalInfoDto getPersonalInfo(Integer id) {
        Optional<PersonalInfo> personalInfo = personalInfoRepository.findById(id);

        if (personalInfo.isEmpty()) {
            String message = "User with id " + id + " does not exist. Personal info not found";
            throw new NotFoundException(message);
        }

        return personalInfo.map(Extensions::asDto).orElse(null);
    }

    /**
     *  Method for getting all existing personal info data
     *  @param userId user id
     *  @return existing personal info transformed to DTO
     */
    public PersonalInfoDto getUserPersonalInfo(Integer userId) {
        Optional<PersonalInfo> personalInfo = personalInfoRepository.findByOwner(userId);

        if (personalInfo.isEmpty()) {
            String message = "User with id " + userId + " does not exist. Personal info not found";
            throw new NotFoundException(message);
        }

        return personalInfo.map(Extensions::asDto).orElse(null);
    }

    /**
     *  Method for adding new personal info data
     *  @param userId user owner id
     *  @param request data to create bank account
     *  @throws ValidationError in case of validation errors occurrence
     *  @throws DataAlreadyExistsException in case of adding duplicated data
     *  @return created personal info DTO
     */
    public PersonalInfoDto addPersonalInfo(Integer userId, PersonalInfoRequest request) throws ValidationError, DataAlreadyExistsException {
        checkPersonalInfoData(request);
        String personalCode = request.personalCode();

        if (personalInfoRepository.countByUserId(userId)) {
            String message = "Personal info for user with id " + userId + " already exists";
            throw new DataAlreadyExistsException(message);
        }

        if (personalInfoRepository.existsByPersonalCode(personalCode)) {
            String message = "Personal info with code " + personalCode + " already exists with same data";
            throw new DataAlreadyExistsException(message);
        }

        UserDto userDto = userService.user(userId);

        AddressRequest addressToCreate = request.address();
        Address address = new Address(
                addressToCreate.street(),
                addressToCreate.houseNumber(),
                addressToCreate.apartmentNumber(),
                addressToCreate.city(),
                addressToCreate.state(),
                addressToCreate.zipCode(),
                addressToCreate.country()
        );

        address = addressRepository.save(address);

        PersonalInfo personalInfo = new PersonalInfo(
                Extensions.asUser(userDto),
                request.firstName(),
                request.lastName(),
                request.email(),
                request.phoneNumber(),
                address,
                personalCode
        );

        personalInfo = personalInfoRepository.save(personalInfo);

        return Extensions.asDto(personalInfo);
    }

    /**
     *  Method for updating personal info
     *  @param addressId owner id
     *  @param updateAddressRequest data to update
     *  @throws NotFoundException in case of personal info not found
     */
    private Address updateAddress(Integer addressId, AddressRequest updateAddressRequest) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new NotFoundException("Address with id " + addressId + " not found"));

        String street = updateAddressRequest.street() != null ? updateAddressRequest.street() : address.getStreet();
        String houseNumber = updateAddressRequest.houseNumber() != null ? updateAddressRequest.houseNumber() : address.getHouseNumber();
        String apartmentNumber = updateAddressRequest.apartmentNumber() != null ? updateAddressRequest.apartmentNumber() : address.getApartmentNumber();
        String city = updateAddressRequest.city() != null ? updateAddressRequest.city() : address.getCity();
        String state = updateAddressRequest.state() != null ? updateAddressRequest.state() : address.getState();
        String zipCode = updateAddressRequest.zipCode() != null ? updateAddressRequest.zipCode() : address.getZipCode();
        String country = updateAddressRequest.country() != null ? updateAddressRequest.country() : address.getCountry();

        AddressRequest addressUpdatedRequest = new AddressRequest(street, houseNumber, apartmentNumber, city, state, zipCode, country);
        checkAddressData(addressUpdatedRequest);

        address.setStreet(street);
        address.setHouseNumber(houseNumber);
        address.setApartmentNumber(apartmentNumber);
        address.setCity(city);
        address.setState(state);
        address.setZipCode(zipCode);
        address.setCountry(country);

        return addressRepository.save(address);
    }

    /**
     *  Method for updating personal info
     *  @param userId owner id
     *  @param updatePersonalInfoRequest data to update
     *  @throws NotFoundException in case of personal info not found
     *  @throws DataAlreadyExistsException in case of adding duplicated data
     */
    public PersonalInfoDto updatePersonalInfo(Integer userId, PersonalInfoRequest updatePersonalInfoRequest) {
        PersonalInfo personalInfo = personalInfoRepository.findByOwner(userId).orElseThrow(() -> new NotFoundException("Personal info for user with id " + userId + " not found"));

        String firstName = updatePersonalInfoRequest.firstName() != null ? updatePersonalInfoRequest.firstName() : personalInfo.getFirstName();
        String lastName = updatePersonalInfoRequest.lastName() != null ? updatePersonalInfoRequest.lastName() : personalInfo.getLastName();
        String email = updatePersonalInfoRequest.email() != null ? updatePersonalInfoRequest.email() : personalInfo.getEmail();
        String phoneNumber = updatePersonalInfoRequest.phoneNumber() != null ? updatePersonalInfoRequest.phoneNumber() : personalInfo.getPhoneNumber();
        String personalCode = updatePersonalInfoRequest.personalCode();

        if (personalInfoRepository.existsByPersonalCode(personalCode)) {
            String message = "Personal info with code " + personalCode + " already exists with same data";
            throw new DataAlreadyExistsException(message);
        }

        Address address = updateAddress(personalInfo.getAddress().getId(), updatePersonalInfoRequest.address());
        personalInfo.setAddress(address);

        PersonalInfoRequest personalInfoUpdatedRequest = new PersonalInfoRequest(
                userId, firstName, lastName, email, phoneNumber, null, personalCode
        );

        checkPersonalInfoData(personalInfoUpdatedRequest);

        personalInfo.setFirstName(firstName);
        personalInfo.setLastName(lastName);
        personalInfo.setEmail(email);
        personalInfo.setPhoneNumber(phoneNumber);
        personalInfo.setPersonalCode(personalCode);

        return Extensions.asDto(personalInfoRepository.save(personalInfo));
    }

    /**
     *  Method for removing address
     *  @param addressId address id
     *  @throws NotFoundException in case of address not found
     */
    private void removeAddress(Integer addressId) {
        if (!addressRepository.existsById(addressId)) {
            throw new NotFoundException("Address with id " + addressId + " not found");
        }

        addressRepository.deleteById(addressId);
    }

    /**
     *  Method for updating personal info
     *  @param userId owner id
     *  @throws NotFoundException in case of personal info not found
     */
    public void removePersonalInfo(Integer userId) {
        PersonalInfo personalInfo = personalInfoRepository.findByOwner(userId).orElseThrow(() -> new NotFoundException("Personal info with id " + userId + " not found"));
        Address address = personalInfo.getAddress();

        if (address != null) {
            removeAddress(address.getId());
        }

        personalInfoRepository.deleteById(personalInfo.getId());
    }
}
