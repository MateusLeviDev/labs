//package br.com.icecube.customer.domain.service.impl;
//
//import br.com.icecube.customer.api.dto.CustomerDTO;
//import br.com.icecube.customer.api.mapper.AddressMapper;
//import br.com.icecube.customer.api.mapper.CustomerMapper;
//import br.com.icecube.customer.domain.model.Customer;
//import br.com.icecube.customer.domain.repository.CustomerRepository;
//import br.com.icecube.customer.messaging.event.CustomerEvent;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import reactor.core.publisher.Sinks;
//
//@ExtendWith(MockitoExtension.class)
//class CustomerServiceImplTest {
//
//    @Mock
//    private CustomerRepository customerRepository;
//    @Mock
//    private CustomerMapper customerMapper;
//    @Mock
//    private AddressMapper addressMapper;
//    @InjectMocks
//    private CustomerServiceImpl customerService;
//    @Mock
//    private Sinks.Many<CustomerEvent> customerProducer;
//
//    CustomerDTO customerDTO;
//    Customer customerToBeSaved;
//    Customer updatedCustomer;
//    AddressDTO updatedAddressDTO;
//
//    @BeforeEach
//    void setUp() {
//        customerDTO = TestDataFactory.createCustomerDTO();
//        customerToBeSaved = TestDataFactory.createCustomerToBeSaved();
//        updatedCustomer = TestDataFactory.createUpdatedCustomer();
//        updatedAddressDTO = TestDataFactory.createUpdatedAddressDTO();
//    }
//
//    @Test
//    void shouldCreateNewCustomerWhenDocumentNotPresent() throws BadRequestException {
//        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
//        when(customerRepository.existsByDocument_Value(CUSTOMER_DOCUMENT)).thenReturn(false);
//        when(customerMapper.toModel(customerDTO)).thenReturn(customerToBeSaved);
//        when(customerRepository.save(customerToBeSaved)).thenReturn(customerToBeSaved);
//
//        customerService.save(customerDTO);
//
//        verify(customerRepository).save(customerCaptor.capture());
//        final var capture = customerCaptor.getValue();
//        assertNotNull(capture);
//        assertEquals(CUSTOMER_LEGAL_NAME, capture.getLegalName().getValue());
//        assertEquals(CUSTOMER_DOCUMENT, capture.getDocument().getValue());
//        verify(customerRepository).existsByDocument_Value(CUSTOMER_DOCUMENT);
//        verify(customerMapper).toModel(customerDTO);
//        verify(customerProducer).tryEmitNext(any(CustomerEvent.CustomerCreated.class));
//    }
//
//    @Test
//    void shouldThrowBadRequestExceptionWhenDocumentAlreadyExist() {
//        when(customerRepository.existsByDocument_Value(customerDTO.document())).thenReturn(true);
//
//        BadRequestException bre = assertThrows(BadRequestException.class, () -> {
//            customerService.save(customerDTO);
//        });
//
//        assertNotNull(bre);
//        verify(customerRepository).existsByDocument_Value(CUSTOMER_DOCUMENT);
//        verifyNoInteractions(addressMapper);
//    }
//
//    @Test
//    void shouldUpdateCustomerAddressSuccessfully() {
//        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
//        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customerToBeSaved));
//        when(addressMapper.toModel(any(AddressDTO.class))).thenReturn(updatedCustomer.getAddress().getFirst());
//        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);
//
//        customerService.updateCustomerAddress(CUSTOMER_ID, ADDRESS_ID, updatedAddressDTO);
//
//        verify(customerRepository).save(customerCaptor.capture());
//        final var address = customerCaptor.getValue().getAddress().getFirst();
//
//        assertNotNull(address);
//        assertEquals("Rua C", address.getStreet());
//        assertEquals("Cidade Z", address.getCity());
//        assertEquals("789", address.getNumber());
//        assertEquals("22222-222", address.getZipcode());
//        verify(customerRepository).findById(CUSTOMER_ID);
//        verify(addressMapper).toModel(updatedAddressDTO);
//    }
//
//    @Test
//    void shouldThrowEntityNotFoundExceptionWhenCustomerNotExist() {
//        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> {
//            customerService.updateCustomerAddress(CUSTOMER_ID, ADDRESS_ID, updatedAddressDTO);
//        });
//
//        verify(customerRepository).findById(CUSTOMER_ID);
//        verify(customerRepository, never()).save(any(Customer.class));
//        verifyNoInteractions(addressMapper);
//    }
//
//    @Test
//    void shouldThrowEntityNotFoundExceptionWhenAddressNotExist() {
//        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customerToBeSaved));
//
//        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () ->
//                customerService.updateCustomerAddress(CUSTOMER_ID, CUSTOMER_NON_EXISTENT_ID, updatedAddressDTO)
//        );
//
//        assertEquals(
//                ADDRESS_NOT_FOUND.formatted(CUSTOMER_NON_EXISTENT_ID), entityNotFoundException.getMessage());
//        verify(customerRepository).findById(CUSTOMER_ID);
//        verify(customerRepository, never()).save(any(Customer.class));
//    }
//
//}