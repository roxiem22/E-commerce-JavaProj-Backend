package com.example.demo.service;

import com.example.demo.DTO.CategoryDTO;
import com.example.demo.DTO.OrderDTO;
import com.example.demo.model.Cart;
import com.example.demo.model.Order;
import com.example.demo.model.Product;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface OrderService {
    public Order updateOrder(Order order);
    public Order deleteOrder(Order order);
    public Order createOrder(Order order);
    OrderDTO findbyId(int id);

    OrderDTO findbyCartId(int id);
    List<OrderDTO> findAll();
}
