package com.bookstore.management.sale.mapper;

import com.bookstore.management.book.mapper.BookMapper;
import com.bookstore.management.sales.mapper.SalesDetailMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {BookMapper.class})
public class SaleDetailMapperTest {
    @Autowired
    private SalesDetailMapper salesDetailMapper;

}
