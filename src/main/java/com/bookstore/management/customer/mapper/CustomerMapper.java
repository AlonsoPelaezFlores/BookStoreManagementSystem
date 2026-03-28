package com.bookstore.management.customer.mapper;

import com.bookstore.management.customer.dto.CustomerCreateDTO;
import com.bookstore.management.customer.dto.CustomerSummaryDTO;
import com.bookstore.management.customer.model.Customer;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    @Mapping(target = "id",ignore = true)
    Customer toEntity(CustomerCreateDTO customerCreateDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",ignore = true)
    void updateEntityFromDto(CustomerCreateDTO customerCreateDTO, @MappingTarget Customer customer);

    CustomerSummaryDTO toSummaryDTO(Customer customer);

    List<CustomerSummaryDTO> toSummaryDTOList(List<Customer> customers);

}
