package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.ProductService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class ProductControllerTest {
    private static final String PRODUCT_ID = "id-1";
    private static final String PRODUCT_NAME = "Tea";
    private static final int PRODUCT_QUANTITY = 5;
    private static final String REDIRECT_PRODUCT_LIST = "/product/list";
    private static final String SUGAR = "Sugar";
    private static final String MISSING = "missing";

    private MockMvc mockMvc;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = Mockito.mock(ProductService.class);
        ProductController controller = new ProductController();
        ReflectionTestUtils.setField(controller, "service", productService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void createProductPageReturnsFormAndModelAttribute() throws Exception {
        mockMvc.perform(get("/product/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("createProduct"))
                .andExpect(model().attributeExists("product"));
    }

    @Test
    void createProductPostDelegatesToServiceAndRedirects() throws Exception {
        mockMvc.perform(post("/product/create")
                        .param("productId", PRODUCT_ID)
                        .param("productName", PRODUCT_NAME)
                        .param("productQuantity", String.valueOf(PRODUCT_QUANTITY)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(REDIRECT_PRODUCT_LIST));

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        Mockito.verify(productService).create(captor.capture());
        Product product = captor.getValue();
        assertEquals(PRODUCT_ID, product.getProductId());
        assertEquals(PRODUCT_NAME, product.getProductName());
        assertEquals(PRODUCT_QUANTITY, product.getProductQuantity());
    }

    @Test
    void createProductPostUsesDefaultBoundValuesWhenFieldsMissing() throws Exception {
        mockMvc.perform(post("/product/create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(REDIRECT_PRODUCT_LIST));

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        Mockito.verify(productService).create(captor.capture());
        Product product = captor.getValue();
        assertNull(product.getProductId());
        assertNull(product.getProductName());
        assertEquals(0, product.getProductQuantity());
    }

    @Test
    void productListPageReturnsListViewWithProducts() throws Exception {
        Product product1 = new Product();
        product1.setProductId(PRODUCT_ID);
        Product product2 = new Product();
        product2.setProductId("id-2");
        List<Product> products = List.of(product1, product2);
        Mockito.when(productService.findAll()).thenReturn(products);

        mockMvc.perform(get("/product/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("productList"))
                .andExpect(model().attribute("products", Matchers.sameInstance(products)));
    }

    @Test
    void productListPageReturnsListViewWhenNoProductsFound() throws Exception {
        List<Product> products = List.of();
        Mockito.when(productService.findAll()).thenReturn(products);

        mockMvc.perform(get("/product/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("productList"))
                .andExpect(model().attribute("products", Matchers.sameInstance(products)));
    }

    @Test
    void deleteProductDelegatesToServiceAndRedirects() throws Exception {
        mockMvc.perform(get("/product/delete/{id}", PRODUCT_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(REDIRECT_PRODUCT_LIST));

        Mockito.verify(productService).delete(PRODUCT_ID);
    }

    @Test
    void editProductPageReturnsEditViewWhenProductExists() throws Exception {
        Product product = new Product();
        product.setProductId(PRODUCT_ID);
        product.setProductName("Coffee");
        product.setProductQuantity(8);
        Mockito.when(productService.findById(PRODUCT_ID)).thenReturn(product);

        mockMvc.perform(get("/product/edit/{id}", PRODUCT_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("editProduct"))
                .andExpect(model().attribute("product", Matchers.sameInstance(product)));
    }

    @Test
    void editProductPageRedirectsWhenProductMissing() throws Exception {
        Mockito.when(productService.findById(MISSING)).thenReturn(null);

        mockMvc.perform(get("/product/edit/" + MISSING))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(REDIRECT_PRODUCT_LIST));

        Mockito.verify(productService).findById(MISSING);
    }

    @Test
    void editProductPostDelegatesToServiceAndRedirects() throws Exception {
        mockMvc.perform(post("/product/edit")
                        .param("productId", PRODUCT_ID)
                        .param("productName", SUGAR)
                        .param("productQuantity", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(REDIRECT_PRODUCT_LIST));

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        Mockito.verify(productService).update(captor.capture());
        Product product = captor.getValue();
        assertEquals(PRODUCT_ID, product.getProductId());
        assertEquals(SUGAR, product.getProductName());
        assertEquals(3, product.getProductQuantity());
    }

    @Test
    void editProductPostPassesNegativeQuantityToService() throws Exception {
        mockMvc.perform(post("/product/edit")
                        .param("productId", PRODUCT_ID)
                        .param("productName", SUGAR)
                        .param("productQuantity", "-1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(REDIRECT_PRODUCT_LIST));

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        Mockito.verify(productService).update(captor.capture());
        Product product = captor.getValue();
        assertEquals(PRODUCT_ID, product.getProductId());
        assertEquals(SUGAR, product.getProductName());
        assertEquals(-1, product.getProductQuantity());
    }
}
