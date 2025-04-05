package com.restaurant;

import com.restaurant.application.console.ConsoleMenu;
import com.restaurant.application.console.OrderConsoleAdapter;
import com.restaurant.application.console.ProductConsoleAdapter;
import com.restaurant.application.file.FileProductLoader;
import com.restaurant.domain.repository.CouponRepository;
import com.restaurant.domain.repository.OrderRepository;
import com.restaurant.domain.repository.ProductRepository;
import com.restaurant.domain.service.DiscountService;
import com.restaurant.infrastructure.InMemoryCouponRepository;
import com.restaurant.infrastructure.InMemoryOrderRepository;
import com.restaurant.infrastructure.InMemoryProductRepository;
import com.restaurant.usecase.OrderUseCase;
import com.restaurant.usecase.ProductUseCase;

public class Main {
    public static void main(String[] args) {
        // 1. Configurar repositorios en memoria
        ProductRepository productRepository = new InMemoryProductRepository();
        OrderRepository orderRepository = new InMemoryOrderRepository();
        CouponRepository couponRepository = new InMemoryCouponRepository();  // Nuevo repositorio de cupones

        // 2. Cargar productos desde el archivo de menú
        FileProductLoader productLoader = new FileProductLoader(productRepository);
        productLoader.loadProducts("menu.txt");

        // 3. Configurar servicios
        DiscountService discountService = new DiscountService(couponRepository);

        // 4. Configurar casos de uso
        ProductUseCase productUseCase = new ProductUseCase(productRepository);
        OrderUseCase orderUseCase = new OrderUseCase(orderRepository, couponRepository, discountService);

        // 5. Configurar adaptadores de consola
        ProductConsoleAdapter productAdapter = new ProductConsoleAdapter(productUseCase);
        OrderConsoleAdapter orderAdapter = new OrderConsoleAdapter(orderUseCase, productUseCase);

        // 6. Iniciar menú principal
        ConsoleMenu mainMenu = new ConsoleMenu(productAdapter, orderAdapter);
        mainMenu.showMainMenu();  // 🔥 Inicia la aplicación por consola
    }
}

//Ulitmos cambios para la branch

