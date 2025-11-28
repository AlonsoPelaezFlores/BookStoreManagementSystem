package com.bookstore.management.customer.mapper;

import com.bookstore.management.customer.dto.CustomerCreateDTO;
import com.bookstore.management.customer.dto.CustomerSummaryDTO;
import com.bookstore.management.customer.model.Customer;
import jdk.jfr.Name;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    @Mapping(target = "id",ignore = true)
    Customer toEntity(CustomerCreateDTO customerCreateDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",ignore = true)
    void updateEntityFromDto(CustomerCreateDTO customerCreateDTO, @MappingTarget Customer customer);

    @Mapping(target = "fullName",source = ".", qualifiedByName="getFullName" )
    CustomerSummaryDTO toSummaryResponseDTO(Customer customer);

    @Named("getFullName")
    default String getFullName(Customer customer) {
        return customer.getName() +" " + customer.getSurname();
    }
}
