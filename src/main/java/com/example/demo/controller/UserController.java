package com.example.demo.controller;
import com.example.demo.DTO.*;
import com.example.demo.enums.Auth;
import com.example.demo.mapper.*;
import com.example.demo.model.*;
import com.example.demo.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/user")
public class UserController {
    private UserService userService;
    private UserMapper userMapper;
    private ProductService productService;
    private CategoryService categoryService;
    private OrderService orderService;
    private PaymentService paymentService;
    private WishlistService wishlistService;
    private WishlistMapper wishlistMapper;
    private CartMapper cartMapper;
    private CartService cartService;
    private LogService logService;
    private LogMapper logMapper;

    @GetMapping()
    public List<UserDTO> findAllProd() {
        return userService.findAll();
    }

    public UserController(UserService userService,UserMapper userMapper, WishlistService wishlistService, WishlistMapper wishlistMapper,CartMapper cartMapper,CartService cartService,LogService logService,LogMapper logMapper,ProductService productService,CategoryService categoryService,OrderService orderService,PaymentService paymentService) {
        this.userService = userService;
        this.userMapper=userMapper;
        this.wishlistService=wishlistService;
        this.wishlistMapper=wishlistMapper;
        this.cartMapper=cartMapper;
        this.cartService=cartService;
        this.logService=logService;
        this.logMapper=logMapper;
        this.productService=productService;
        this.categoryService=categoryService;
        this.orderService=orderService;
        this.paymentService=paymentService;
    }

    @RequestMapping(value="/delete")
    public User delete(@RequestBody User user) {
        System.out.println(user);
        return userService.deleteUser(user);
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    @RequestMapping(value="/adduser")
    public User addUser(@RequestBody User user) throws UnsupportedEncodingException {
        Wishlist wishlist = new Wishlist();
        Cart cart = new Cart();
        LoginLogout loginLogout = new LoginLogout();
        cart.setUser(user);
        wishlist.setUser(user);
        loginLogout.setUser(user);
        user.setCart(cart);
        user.setWishlist(wishlist);
        user.setLoginLogout(loginLogout);
        if(user.getName().equals("")){
            System.out.println("Nume invalid pentru user");
            return null;}
        if(user.getPassword().equals("")){
            System.out.println("Nu ai introdus o parola!");
            return null;}
        if(user.getAuth()==null){
            System.out.println("Nu ai introdus un rol!");
            return null;}
        if(!isValidEmailAddress(user.getMail())){
            System.out.println("Email invalid");
            return null;
        }
        byte[] encodedBytes2 = Base64.getEncoder().encode(user.getPassword().getBytes("UTF-8"));
        user.setPassword(new String(encodedBytes2));
        return userService.createUser(user);
    }

    @RequestMapping(value="/create",method= RequestMethod.POST)
    public void create(@RequestParam User user)
    {
        userService.createUser(user);
    }

    @RequestMapping(value="/update")
    public User update(@RequestBody User user) throws UnsupportedEncodingException {
        User u = userMapper.mapDtoToModel(userService.findById(user.getId()));
        WishlistDTO wishlistDTO = wishlistService.findByUserId(u.getId());
        wishlistDTO.setUser_id(u.getId());
        Wishlist wishlist = wishlistMapper.mapDtoToModel(wishlistDTO);
        wishlist.setUser(u);

        CartDTO cartDTO = cartService.findByUserId(u.getId());
        cartDTO.setUser_id(u.getId());
        Cart cart = cartMapper.mapDtoToModel(cartDTO);
        cart.setUser(u);

        LogDTO logDTO = logService.findByUserId(u.getId());
        logDTO.setUser_id(u.getId());
        LoginLogout loginLogout = logMapper.mapDtoToModel(logDTO);
        loginLogout.setUser(u);

        System.out.println(wishlistDTO.getUser_id());
        if(user.getCart()==null) user.setCart(cart);
        if(user.getWishlist()==null) user.setWishlist(wishlist);
        if(user.getName()==null) user.setName(u.getName());
        if(user.getMail()==null) user.setMail(u.getMail());
        if(user.getPassword()==null) user.setPassword(u.getPassword());
        if(user.getAuth()==null) user.setAuth(u.getAuth());

        byte[] encodedBytes2 = Base64.getEncoder().encode(user.getPassword().getBytes("UTF-8"));
        user.setPassword(new String(encodedBytes2));

        return userService.updateUser(user);
    }

    @RequestMapping(value="/all",method =RequestMethod.GET)
    public List<UserDTO> findAll()
    {
        return userService.findAll();
    }

    @RequestMapping(value="/findbyid",method =RequestMethod.GET)
    public UserDTO findById(@RequestParam int id)
    {
        return userService.findById(id);
    }

    @RequestMapping(value = "/login")
    public UserDTO login(@RequestBody User user) {
        if(user.getPassword().equals("")){
            System.out.println("Parola invalida");
            return null;
        }
        if(user.getName().equals("")){
            System.out.println("Username invalid");
            return null;
        }

        UserDTO user1 = userService.login(user.getName(), user.getPassword());
        if(user1!=null)
        return user1;
        else return null;
    }

    @RequestMapping(value = "/getId")
    public UserDTO getId(@RequestBody User user) {
        UserDTO user1 = userService.login(user.getName(), user.getPassword());
        if(user1!=null)
            return user1;
        else return null;
    }
    @RequestMapping(value = "/xml")
    public void xml() throws ParserConfigurationException, IOException, SAXException, TransformerException {
        System.out.println("aici");
        List<UserDTO> ulist = userService.findAll();
        List<ProductDTO> plist = productService.findAll();
        List<CategoryDTO> clist = categoryService.findAll();
        List<OrderDTO> olist = orderService.findAll();
        List<PaymentDTO> list = paymentService.findAll();

        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        // root element
        Element root = document.createElement("store");
        document.appendChild(root);

        // user element
        Element user = document.createElement("user");
        root.appendChild(user);

        for(UserDTO u: ulist) {
            // id element
            Element id = document.createElement("id");
            String x = String.valueOf(u.getId());
            id.appendChild(document.createTextNode(x));
            user.appendChild(id);
            // auth element
            Element auth = document.createElement("auth");
            String y = String.valueOf(u.getAuth());
            auth.appendChild(document.createTextNode(y));
            user.appendChild(auth);
            // firstname element
            Element firstName = document.createElement("name");
            firstName.appendChild(document.createTextNode(u.getName()));
            user.appendChild(firstName);
            // email element
            Element email = document.createElement("email");
            email.appendChild(document.createTextNode(u.getMail()));
            user.appendChild(email);
            // password elements
            Element password = document.createElement("password");
            password.appendChild(document.createTextNode(u.getPassword()));
            user.appendChild(password);
            // cart_id element
            Element cart = document.createElement("cart_id");
            String z = String.valueOf(u.getCart_id());
            cart.appendChild(document.createTextNode(z));
            user.appendChild(cart);
            // wishlist_id element
            Element wishlist = document.createElement("wishlist_id");
            String w = String.valueOf(u.getWishlist_id());
            wishlist.appendChild(document.createTextNode(w));
            user.appendChild(wishlist);
            // log_id element
            Element log = document.createElement("log_id");
            String l = String.valueOf(u.getLogin_logout_id());
            log.appendChild(document.createTextNode(l));
            user.appendChild(log);
        }

        Element product = document.createElement("product");
        root.appendChild(product);

        for(ProductDTO u: plist) {
            // id element
            Element id = document.createElement("id");
            String x = String.valueOf(u.getId());
            id.appendChild(document.createTextNode(x));
            product.appendChild(id);
            // category element
            Element categ = document.createElement("category_id");
            String y = String.valueOf(u.getCategory_id());
            categ.appendChild(document.createTextNode(y));
            product.appendChild(categ);
            // name element
            Element name = document.createElement("name");
            name.appendChild(document.createTextNode(u.getName()));
            product.appendChild(name);
            // description element
            Element description = document.createElement("description");
            description.appendChild(document.createTextNode(u.getDescription()));
            product.appendChild(description);
            // image elements
            Element image = document.createElement("image");
            image.appendChild(document.createTextNode(u.getImgUrl()));
            product.appendChild(image);
            // price element
            Element price = document.createElement("price");
            String z = String.valueOf(u.getPrice());
            price.appendChild(document.createTextNode(z));
            product.appendChild(price);
        }

        Element category = document.createElement("category");
        root.appendChild(category);

        for(CategoryDTO u: clist) {
            // id element
            Element id = document.createElement("id");
            String x = String.valueOf(u.getId());
            id.appendChild(document.createTextNode(x));
            category.appendChild(id);
            // name element
            Element name = document.createElement("name");
            name.appendChild(document.createTextNode(u.getName()));
            category.appendChild(name);
        }

        // order element
        Element order = document.createElement("order");
        root.appendChild(order);

        for(OrderDTO u: olist) {
            // id element
            Element id = document.createElement("id");
            String x = String.valueOf(u.getId());
            id.appendChild(document.createTextNode(x));
            order.appendChild(id);
            // cart element
            Element cart = document.createElement("cart_id");
            String y = String.valueOf(u.getCart_id());
            cart.appendChild(document.createTextNode(y));
            order.appendChild(cart);
            // firstname element
            Element name = document.createElement("name");
            name.appendChild(document.createTextNode(u.getName()));
            order.appendChild(name);
            // email element
            Element email = document.createElement("email");
            email.appendChild(document.createTextNode(u.getEmail()));
            order.appendChild(email);
            // address elements
            Element address = document.createElement("address");
            address.appendChild(document.createTextNode(u.getAddress()));
            order.appendChild(address);
            // payment_id element
            Element payment = document.createElement("payment_id");
            String z = String.valueOf(u.getPayment_id());
            payment.appendChild(document.createTextNode(z));
            order.appendChild(payment);
            // city element
            Element city = document.createElement("city");
            city.appendChild(document.createTextNode(u.getCity()));
            order.appendChild(city);
            // state element
            Element state = document.createElement("state");
            state.appendChild(document.createTextNode(u.getState()));
            order.appendChild(state);
            // zip element
            Element zip = document.createElement("zip");
            zip.appendChild(document.createTextNode(u.getZip()));
            order.appendChild(zip);
        }

        // payment element
        Element payment = document.createElement("payment");
        root.appendChild(payment);

        for(PaymentDTO u: list) {
            // id element
            Element id = document.createElement("id");
            String x = String.valueOf(u.getId());
            id.appendChild(document.createTextNode(x));
            payment.appendChild(id);
            // order element
            Element orders = document.createElement("order_id");
            String y = String.valueOf(u.getOrder_id());
            orders.appendChild(document.createTextNode(y));
            payment.appendChild(orders);
            // card element
            Element nc = document.createElement("card");
            nc.appendChild(document.createTextNode(u.getCard()));
            payment.appendChild(nc);
            // name element
            Element name = document.createElement("name");
            name.appendChild(document.createTextNode(u.getName()));
            payment.appendChild(name);
            // cvv elements
            Element cvv = document.createElement("cvv");
            cvv.appendChild(document.createTextNode(u.getCvv()));
            payment.appendChild(cvv);
            // month element
            Element month = document.createElement("month");
            month.appendChild(document.createTextNode(u.getMonth()));
            payment.appendChild(month);
            // year element
            Element year = document.createElement("year");
            year.appendChild(document.createTextNode(u.getYear()));
            payment.appendChild(year);
        }

        // create the xml file
        //transform the DOM Object to an XML File
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File("E:\\file.xml"));
        transformer.transform(domSource, streamResult);

    }
}
