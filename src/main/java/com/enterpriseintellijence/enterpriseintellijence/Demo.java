package com.enterpriseintellijence.enterpriseintellijence;

import com.enterpriseintellijence.enterpriseintellijence.core.services.NotificationSystem;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.*;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.CustomMoney;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Currency;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class Demo {
    private ArrayList<User> userArrays = new ArrayList<>();
    private ArrayList<Product> productArrayList =new ArrayList<>();;
    private String description = "The standard Lorem Ipsum passage, used since the 1500s\n" ;
    private ArrayList<String> brand =new ArrayList<>();
    private ArrayList<String> urlImageUserList = new ArrayList<>(Arrays.asList(
            "images/user_photos/examples/foto_profilo1.jpg",
            "images/user_photos/examples/foto_profilo2.jpg",
            "images/user_photos/examples/foto_profilo3.jpg",
            "images/user_photos/examples/foto_profilo4.jpg",
            "images/user_photos/examples/foto_profilo5.jpg"
    ));
    private ArrayList<String> urlImageList = new ArrayList<>(Arrays.asList("images/product_photos/examples/1.jpeg",
            "images/product_photos/examples/2.jpeg",
            "images/product_photos/examples/3.jpeg",
            "images/product_photos/examples/4.jpeg",
            "images/product_photos/examples/5.jpeg",
            "images/product_photos/examples/6.jpeg",
            "images/product_photos/examples/7.jpeg",
            "images/product_photos/examples/8.jpeg",
            "images/product_photos/examples/9.jpeg",
            "images/product_photos/examples/10.jpeg",
            "images/product_photos/examples/11.jpeg",
            "images/product_photos/examples/12.jpeg",
            "images/product_photos/examples/13.jpeg",
            "images/product_photos/examples/14.jpeg",
            "images/product_photos/examples/15.jpeg",
            "images/product_photos/examples/16.jpeg",
            "images/product_photos/examples/17.jpeg",
            "images/product_photos/examples/18.jpeg",
            "images/product_photos/examples/19.jpeg",
            "images/product_photos/examples/20.jpeg",
            "images/product_photos/examples/21.jpeg",
            "images/product_photos/examples/22.jpeg",
            "images/product_photos/examples/23.jpeg",
            "images/product_photos/examples/24.jpeg",
            "images/product_photos/examples/25.jpeg",
            "images/product_photos/examples/26.jpeg",
            "images/product_photos/examples/27.jpeg"));
    private ArrayList<Product> tempProduct;
    private ArrayList<ProductCategory> categories;
    private ArrayList<Size> sizes;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PaymentMethodRepository paymentMethodRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final MessageRepository messageRepository;
    private final AddressRepository addressRepository;
    private final FollowingRepository followingRepository;
    private final UserImageRepository userImageRepository;
    private final NotificationSystem notificationSystem;
    private final OfferRepository offerRepository;
    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;
    private final DeliveryRepository deliveryRepository;
    private final ReviewRepository reviewRepository;
    private final ProductCatRepository productCatRepository;
    private final SizeRepository sizeRepository;
    private final ReportRepository reportRepository;
    private int globalPurchased=0;
    private int limitPurchasing;



    public void initialize() throws IOException {
/*        initializeBrandList();
        createCategory();
        categories.clear();
        categories.addAll(productCatRepository.findAll());
        createSizes();
        sizes.clear();
        sizes.addAll(sizeRepository.findAll());
        createUser();
        setAddress();
        try{
            for(User user:userArrays){
                createProduct(user);
                setFollower(user);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        setMessage();
        for (Product product: productArrayList){
            setLikeProduct(product);
        }
        userArrays.clear();
        productArrayList.clear();
        userArrays.addAll(userRepository.findAll());
        productArrayList.addAll(productRepository.findAll());
        processSaleExampleData();*/
        //userArrays.clear();
        //userArrays.addAll(userRepository.findAll());
        reportSomeUser();
    }

    private void reportSomeUser() {
        userArrays.clear();
        userArrays.addAll(userRepository.findAll());
        Random random = new Random();
        for (User user: userArrays){
           int m= random.nextInt(100);
           int n= random.nextInt(1,5);

            if(m<12){
               for(int l=0;l<n;l++){
                   Report report = new Report();
                   report.setReporterUser(user);
                   report.setDescription("questo è un test per la descrizione");
                   User reported = userArrays.get(random.nextInt(userArrays.size()));
                   while(reported.equals(user)){
                       reported = userArrays.get(random.nextInt(userArrays.size()));
                   }
                   report.setReportedUser(reported);
                   report.setDate(LocalDateTime.now());
                   report.setLastUpdate(LocalDateTime.now());
                   report.setStatus(ReportStatus.PENDING);
                   int x = random.nextInt(10);
                   if(x%2==0){
                       if(reported.getSellingProducts()!=null)
                           report.setReportedProduct(reported.getSellingProducts().get(random.nextInt(reported.getSellingProducts().size())));
                   }
                   reportRepository.save(report);
               }

           }
        }
    }


    public void createUser() throws IOException {

        for (int i=0; i<80;i++){
            User user = new User();
            if(i==0) {
                user.setUsername("superadmin");
                user.setPassword(passwordEncoder.encode("superadmin"));
                user.setRole(UserRole.SUPER_ADMIN);

            }
            else{
                user.setUsername("username"+i);
                user.setPassword(passwordEncoder.encode("password"+i));
                user.setRole(UserRole.USER);
            }

            user.setEmail("email"+i+"@gmail.com");
            user.setProvider(Provider.LOCAL);
            user.setStatus(UserStatus.ACTIVE);
            user.setEmailVerified(true);
            user.setFollowersNumber(0);
            user.setFollowingNumber(0);
            user = userRepository.save(user);
            userArrays.add(user);
            createPayment(user);
            setUserImage(user);
        }
    }

    private void setUserImage(User user){
        Random random = new Random();
        UserImage userImage = new UserImage();
        userImage.setUser(user);
        userImage.setDescription("No description avalaible");
        userImage.setUrlPhoto(urlImageUserList.get(random.nextInt(urlImageUserList.size())));
        userImageRepository.save(userImage);

    }


    private void setAddress() {
        Random random = new Random();
        for (int i=0;i<userArrays.size();i++){
            Address address = new Address();
            setParameterToAddress(address, String.valueOf(i+1),userArrays.get(i),userArrays.get(i).getUsername(),true);
            addressRepository.save(address);
            int n= random.nextInt(0,3);
            for(int l=1;l<=n;l++){
                address = new Address();
                String val = (i+1) + " . " +l;
                setParameterToAddress(address,val,userArrays.get(i),userArrays.get(i).getUsername(),false);
                addressRepository.save(address);
            }
        }
    }

    private void setParameterToAddress(Address address, String i,User user,String username,boolean setDefault){
        address.setHeader(username+" "+username);
        address.setCountry("country"+i);
        address.setCity("city"+i);
        address.setStreet("street"+i);
        address.setZipCode("85269");
        address.setPhoneNumber("342 6989745");
        address.setDefault(setDefault);
        address.setUser(user);
    }

    private void setMessage() {
        Random rand = new Random();
        List<Message> messages;
        for (User user : userArrays) {
            messages = new ArrayList<>();
            int n = rand.nextInt(1, 10);
            for (int i = 0; i < n; i++) {
                Message message = new Message();
                User receiver = userArrays.get(rand.nextInt(userArrays.size()));
                while (receiver.equals(user))
                    receiver = userArrays.get(rand.nextInt(userArrays.size()));
                if (!receiver.equals(user)) {

                    message.setText("Random text from " + user.getUsername() + " to " + receiver.getUsername());
                    message.setMessageDate(LocalDateTime.now());
                    message.setMessageStatus(MessageStatus.UNREAD);
                    message.setOffer(null);
                    if (rand.nextInt(100) % 2 == 0) {
                        Product product;
                        List<Product> products = productRepository.findAllBySeller(receiver, PageRequest.of(0, 1000)).stream().toList();

                        if (products != null && !products.isEmpty()) {
                            product = products.get(rand.nextInt(products.size()));
                            message.setProduct(product);
                        }
                    }

                    message.setSendUser(user);
                    message.setReceivedUser(receiver);
                    String convID;
                    if (message.getConversationId() == null) {
                        convID = UUID.randomUUID().toString();
                        while(!messageRepository.canUseConversationId(convID))
                            convID = UUID.randomUUID().toString();

                        message.setConversationId(convID);

                        message = messageRepository.save(message);

                        Product productTemp = message.getProduct() != null ? message.getProduct() : null;
                        int y = rand.nextInt(1, 8);
                        //messages.add(message);
                        for (int l = 0; l < y; l++) {
                            message = new Message();
                            User user1;
                            User user2;
                            if (l % 2 != 0) {
                                user1 = receiver;
                                user2 = user;
                            } else {
                                user1 = user;
                                user2 = receiver;
                            }
                            message.setText("Random text from " + user1.getUsername() + " to " + user2.getUsername());
                            message.setMessageDate(LocalDateTime.now());
                            message.setConversationId(convID);
                            message.setMessageStatus(MessageStatus.UNREAD);
                            message.setOffer(null);
                            message.setProduct(productTemp);
                            message.setSendUser(user1);
                            message.setReceivedUser(user2);
                            messages.add(message);
                        }
                    }
                }
                messageRepository.saveAll(messages);
                messages.clear();
            }
        }
    }

    private void setFollower(User user) {
        try{
            Random random = new Random();
            int n= random.nextInt(20);
            for (int i=0;i<n;i++){

                User user2= userArrays.get(random.nextInt(userArrays.size())) ;
                if(!user.getId().equals(user2.getId())){
                    // TODO: 05/06/2023 valid date
                    followingRepository.save(Following.builder()
                            .followingFrom(LocalDateTime.now())
                            .follower(user)
                            .following(user2)
                            .build());
                        user.setFollowingNumber(user.getFollowingNumber()+1);
                        user2.setFollowersNumber(user2.getFollowersNumber()+1);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void createPayment(User user){
        int rand = ThreadLocalRandom.current().nextInt(1, 4);
        for (int i= 1; i<=rand;i++){
            PaymentMethod paymentMethod = new PaymentMethod();
            paymentMethod.setCreditCard("4000 4000 4000 400"+i);
            paymentMethod.setExpiryDate(LocalDate.parse("30-03-2025", DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            paymentMethod.setOwner(user.getUsername()+" "+user.getUsername());
            paymentMethod.setOwnerUser(user);
            if(i==1)
                paymentMethod.setDefault(true);
            else
                paymentMethod.setDefault(false);
            paymentMethodRepository.save(paymentMethod);

        }
    }

    private void createProduct(User user){
        tempProduct = new ArrayList<>();

        //for clothing enum
        List<ProductGender> genderList = List.of(ProductGender.class.getEnumConstants());
        int sizeGender = genderList.size();

        List<Colour> colourList = List.of(Colour.class.getEnumConstants());
        int sizeColour = colourList.size();

        //for entertainment enum
        List<EntertainmentLanguage> entertainmentLanguageList = List.of(EntertainmentLanguage.class.getEnumConstants());
        int sizeLanguage = entertainmentLanguageList.size();


        List<HomeMaterial> homeMaterials = List.of(HomeMaterial.class.getEnumConstants());
        int sizeHomeMaterial = homeMaterials.size();


        Random random = new Random();

        int rand = ThreadLocalRandom.current().nextInt(1, 30);
        for (int i=1;i<=rand;i++){

            int rand2=ThreadLocalRandom.current().nextInt(0, 4);
            //setting clothing
            if (rand2==1){
                Clothing clothing = new Clothing();
                clothing.setProductGender(genderList.get(random.nextInt(sizeGender)));
                clothing.setColour(colourList.get(random.nextInt(sizeColour)));
                ProductCategory productCategory = categories.get(random.nextInt(categories.size()));
                while(!productCategory.getPrimaryCat().equals("Clothing")) {
                    productCategory = categories.get(random.nextInt(categories.size()));
                }

                clothing.setProductCategory(productCategory);

                Size size = sizes.get(random.nextInt(sizes.size()));
                while(!size.getType().equals(clothing.getProductCategory().getSecondaryCat())) {
                    size = sizes.get(random.nextInt(sizes.size()));
                }

                clothing.setClothingSize(size);
                //clothing.setProductCategory(ProductCategoryOld.CLOTHING);
                tempProduct.add(clothing);
            }
            //setting entertainment
            else if(rand2==2){
                Entertainment entertainment = new Entertainment();
                ProductCategory productCategory = categories.get(random.nextInt(categories.size()));
                while(!productCategory.getPrimaryCat().equals("Entertainment")) {
                    productCategory = categories.get(random.nextInt(categories.size()));
                }

                entertainment.setProductCategory(productCategory);
                entertainment.setEntertainmentLanguage(entertainmentLanguageList.get(random.nextInt(sizeLanguage)));
                //entertainment.setProductCategory(ProductCategoryOld.ENTERTAINMENT);
                tempProduct.add(entertainment);
            }
            //setting home
            else if(rand2==3){
                Home home = new Home();

                ProductCategory productCategory = categories.get(random.nextInt(categories.size()));
                while(!productCategory.getPrimaryCat().equals("Home")) {
                    productCategory = categories.get(random.nextInt(categories.size()));
                }

                home.setProductCategory(productCategory);
                Size size = sizes.get(random.nextInt(sizes.size()));
                while(!size.getType().equals(productCategory.getPrimaryCat())) {
                    size = sizes.get(random.nextInt(sizes.size()));
                }
                home.setHomeSize(size);
                home.setColour(colourList.get(random.nextInt(sizeColour)));
                home.setHomeMaterial(homeMaterials.get(random.nextInt(sizeHomeMaterial)));
                //home.setProductCategory(ProductCategoryOld.HOME);
                tempProduct.add(home);
            }
            else{
                Product product=new Product();
                for(ProductCategory productCategory:categories)
                    if(productCategory.getPrimaryCat().equals("Other"))
                        product.setProductCategory(productCategory);
                //product.setProductCategory(ProductCategoryOld.OTHER);
                tempProduct.add(product);
            }

        }
        setBasicProduct(user);
    }

    private void setBasicProduct(User user){

        int size= brand.size();

        Double price = 1.90;


        List<Condition> conditionList = List.of(Condition.class.getEnumConstants());
        int sizeCondition = conditionList.size();

        List<ProductSize> productSizeList = List.of(ProductSize.class.getEnumConstants());
        int sizeProductSize = productSizeList.size();

        Random random = new Random();

        List<Visibility> visibilities = List.of(Visibility.class.getEnumConstants());

        for (Product product: tempProduct){

            int n = productArrayList.indexOf(product);
            product.setTitle("Title product "+n);
            product.setDescription(description);
            product.setBrand(brand.get(random.nextInt(size)));

            Double priceProduct = price+random.nextInt(1,1500);
            product.setProductCost(new CustomMoney(priceProduct, Currency.EUR ));
            product.setDeliveryCost(new CustomMoney(5.00,Currency.EUR));

            //product.setProductCategory(categories.get(random.nextInt(categories.size())));

            product.setCondition(conditionList.get(random.nextInt(sizeCondition)));
            //product.setAddress(user.getAddress());
            product.setProductSize(productSizeList.get(random.nextInt(sizeProductSize)));
            product.setLikesNumber(0);
            product.setViews(random.nextInt(0,120));
            LocalDateTime date = LocalDateTime.now();
            date = date.minusSeconds(ThreadLocalRandom.current().nextInt(1, 5184000));
            product.setUploadDate(date);
            product.setLastUpdateDate(date);
            if(random.nextInt(100)>=90)
                product.setVisibility(Visibility.PRIVATE);
            else
                product.setVisibility(Visibility.PUBLIC);
            product.setAvailability(Availability.AVAILABLE);
            product.setSeller(user);
            //user.getSellingProducts().add(product);
            productRepository.save(product);
            setImageToProduct(product);
            productArrayList.add(product);
        }
        tempProduct.clear();
    }

    private void setImageToProduct(Product product){
        Random random = new Random();
        int n = random.nextInt(1,6);
        for (int i=1;i<=n;i++){
            ProductImage productImage = new ProductImage();
            productImage.setDescription("Random description bla bla");
            productImage.setUrlPhoto(urlImageList.get(random.nextInt(urlImageList.size())));
            productImage.setProduct(product);
            productImageRepository.save(productImage);
        }
    }

    private void setLikeProduct(Product product){

        Random random = new Random();
        int cont=0;
        for (User user: userArrays){
            if(!product.getSeller().getId().equals(user.getId())){
                int n = random.nextInt(101);
                if(n<=12){
                    //user.getLikedProducts().add(product);/*
                    product.getUsersThatLiked().add(user);
                    cont++;
                }
            }
        }
        product.setLikesNumber(cont);
        productRepository.save(product);
    }
    private void initializeBrandList(){
        for (int i=1;i<=13; i++){
            brand.add("Brand "+i);
        }
    }

    private void processSaleExampleData(){

        for (User user: userArrays){
            madeAnOffer(user);
        }
        List<Offer> offers = offerRepository.findAll();
        List<Product> offerAccepted = new ArrayList<>();
        for(Offer offer: offers){
            rejectOrAcceptOffer(offer,offerAccepted);
        }
        Random random = new Random();


        offers.clear();
        offers = offerRepository.findAll();
        List<Product> products = productRepository.findAll();
        limitPurchasing= products.size()/100*25;
        for(Offer offer: offers){
            buyFromOffer(offer);
        }

        List<User> users = userRepository.findAll();


        for(Product product: products){
            User buyer =users.get(random.nextInt(users.size()));
            buyProduct(product,buyer);

        }

        List<Order> orders = orderRepository.findAll();
        int sizeOrders = orders.size();
        int pay= sizeOrders/100*75;
        int count=0;
        while(count<pay){
            payForProduct(orders.get(count));
            count++;
        }

        orders.clear();
        orders = orderRepository.findAll();
        for (Order order: orders){
            if(order.getState().equals(OrderState.PURCHASED)){
                boolean deliveryCreated = random.nextBoolean();
                if(deliveryCreated){
                    boolean isDelivered = random.nextBoolean();
                    Address senderAddress = addressRepository.findAllByUserEquals(order.getProduct().getSeller()).get(0);
                    DeliveryStatus deliveryStatus = DeliveryStatus.DELIVERED;
                    if(!isDelivered)
                        deliveryStatus = DeliveryStatus.SHIPPED;
                    Delivery delivery =Delivery.builder()
                            .order(order)
                            .sendTime(LocalDateTime.now())
                            .deliveredTime(LocalDateTime.now())
                            .deliveryCost(order.getProduct().getDeliveryCost())
                            .shipper("Bartolini spa")
                            .deliveryStatus(deliveryStatus)
                            .senderAddress(senderAddress)
                            .receiverAddress(order.getDeliveryAddress())
                            .build();
                    if(isDelivered)
                        order.setState(OrderState.DELIVERED);
                    else
                        order.setState(OrderState.SHIPPED);
                    deliveryRepository.save(delivery);
                    orderRepository.save(order);
                    if(order.getState().equals(OrderState.DELIVERED)){
                        order.setState(OrderState.COMPLETED);
                        orderRepository.save(order);
                        User buyer = order.getUser();
                        User seller = order.getProduct().getSeller();
                        int voto= random.nextInt(1,6);
                        Review review = Review.builder()
                                .date(LocalDateTime.now())
                                .title(buyer.getUsername() +" recensisce "+seller.getUsername())
                                .description("Questa è una recensione finta, e serve come placeholder")
                                .vote(voto)
                                .reviewed(seller)
                                .reviewer(buyer)
                                .build();

                        reviewRepository.save(review);
                        seller.setReviewsNumber(seller.getReviewsNumber()+1);
                        seller.setReviewsTotalSum(seller.getReviewsTotalSum()+voto);
                        userRepository.save(seller);
                    }

                }
            }
        }
    }

    private void payForProduct(Order order) {
        User user = order.getUser();
        List<PaymentMethod> paymentMethods = paymentMethodRepository.findByOwnerUserEquals(user);
        Double price = order.getProduct().getProductCost().getPrice();
        if(order.getOffer()!=null)
            price +=order.getOffer().getAmount().getPrice();
        price = Math.round(price * 100.00) / 100.00;
        Transaction transaction = Transaction.builder()
                .creationTime(LocalDateTime.now())
                .amount(new CustomMoney(price,order.getProduct().getProductCost().getCurrency()))
                .transactionState(TransactionState.COMPLETED)
                .paymentMethod(paymentMethods.get(0))
                .order(order)
                .build();
        order.setTransaction(transaction);
        order.getProduct().setAvailability(Availability.UNAVAILABLE);
        order.setState(OrderState.PURCHASED);
        transactionRepository.save(transaction);
        orderRepository.save(order);
    }

    private void buyFromOffer(Offer offer) {
        Product product = offer.getProduct();
        if(offer.getState().equals(OfferState.ACCEPTED) && product.getAvailability().equals(Availability.PENDING) && globalPurchased<limitPurchasing){
            User buyer = offer.getOfferer();
            List<Address> addresses = addressRepository.findAllByUserEquals(buyer);
            product.setAvailability(Availability.PENDING);


            Order order = Order.builder()
                    .orderDate(LocalDateTime.now())
                    .orderUpdateDate(LocalDateTime.now())
                    .state(OrderState.PENDING)
                    .product(product)
                    .user(buyer)
                    .deliveryAddress(addresses.get(0))
                    .offer(offer)
                    .build();

            orderRepository.save(order);
            product.setOrder(order);

            productRepository.save(product);
            globalPurchased++;

        }
    }

    private void buyProduct(Product product,User user) {

        Random random = new Random();
        int value = random.nextInt(100);
        if(value>=70 && product.getAvailability().equals(Availability.AVAILABLE) && globalPurchased<limitPurchasing){
            product.setAvailability(Availability.PENDING);
            List<Address> addresses = addressRepository.findAllByUserEquals(user);
            Order order = Order.builder()
                    .orderDate(LocalDateTime.now())
                    .orderUpdateDate(LocalDateTime.now())
                    .state(OrderState.PENDING)
                    .product(product)
                    .user(user)
                    .deliveryAddress(addresses.get(0))
                    .build();

            orderRepository.save(order);
            product.setOrder(order);
            productRepository.save(product);
            globalPurchased++;



        }
    }

    private void rejectOrAcceptOffer(Offer offer,List<Product> offerAccepted) {
        Random random = new Random();
        int prob = random.nextInt(101);
        boolean isAccepted=false;
        if(prob>75)
            isAccepted=true;
        if(isAccepted ){
            if(offerAccepted.isEmpty() || !offerAccepted.contains(offer.getProduct())){
                offer.setState(OfferState.ACCEPTED);
                offer.getProduct().setAvailability(Availability.PENDING);
                offer.getProduct().setLastUpdateDate(LocalDateTime.now());
                offerAccepted.add(offer.getProduct());
            }else
                offer.setState(OfferState.REJECTED);

        }
        else
            offer.setState(OfferState.REJECTED);
        //offerRepository.save(offer);
        String basic = "Your offer for product " +offer.getProduct().getTitle();
        if (isAccepted)
            basic = basic + " is accepted. Please check your profile and complete the order";
        else
            basic = basic + "was rejected. Sorry, try again.";


        Message message= Message.builder()
                .text(basic)
                .messageDate(LocalDateTime.now())
                .messageStatus(MessageStatus.UNREAD)
                .conversationId(offer.getMessage().getConversationId())
                .product(offer.getProduct())
                .sendUser(offer.getOfferer())
                .receivedUser(offer.getProduct().getSeller())
                .offer(offer)
                .build();

        offer.setMessage(message);
        messageRepository.save(message);


    }

    private void madeAnOffer(User user) {
        Random random = new Random();
        int n = random.nextInt(0,13);
        for(int i=0; i<n;i++){
            Product product = productArrayList.get(random.nextInt(1, productArrayList.size()));
            if(!product.getSeller().getId().equals(user.getId())){
                Double price = product.getProductCost().getPrice()/100*80;
                price = Math.round(price * 100.00) / 100.00;


                Offer offer = Offer.builder()
                        .amount(new CustomMoney(price,product.getProductCost().getCurrency()))
                        .creationTime(LocalDateTime.now())
                        .state(OfferState.PENDING)
                        .offerer(user)
                        .product(product)
                        .build();
                //offerRepository.save(offer);

                String convID =getConversationId(offer.getOfferer(), product.getSeller(), product);
                Message message = Message.builder()
                        .text("Hi, here is my offer for " + product.getTitle() + ".")
                        .messageDate(LocalDateTime.now())
                        .messageStatus(MessageStatus.UNREAD)
                        .product(product)
                        .conversationId(convID)
                        .sendUser(offer.getOfferer())
                        .receivedUser(product.getSeller())
                        .offer(offer)
                        .build();

                offer.setMessage(message);
                messageRepository.save(message);
            }

        }
    }

    private void createCategory() {
        categories = new ArrayList<>();
        categories.add(ProductCategory.builder().primaryCat("Clothing").secondaryCat("Cloths").tertiaryCat("Jeans").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Clothing").secondaryCat("Cloths").tertiaryCat("Dress").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Clothing").secondaryCat("Cloths").tertiaryCat("Skirt").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Clothing").secondaryCat("Cloths").tertiaryCat("T Shirt").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Clothing").secondaryCat("Cloths").tertiaryCat("Sweaters").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Clothing").secondaryCat("Cloths").tertiaryCat("Trousers").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Clothing").secondaryCat("Shoes").tertiaryCat("Boots").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Clothing").secondaryCat("Shoes").tertiaryCat("Heels").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Clothing").secondaryCat("Shoes").tertiaryCat("Sport").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Clothing").secondaryCat("Shoes").tertiaryCat("Trainers").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Clothing").secondaryCat("Shoes").tertiaryCat("Sandals").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Clothing").secondaryCat("Bags").tertiaryCat("Shoulder bag").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Clothing").secondaryCat("Bags").tertiaryCat("Handbags").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Clothing").secondaryCat("Bags").tertiaryCat("Luggage").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Clothing").secondaryCat("Bags").tertiaryCat("Backpacks").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Clothing").secondaryCat("Accessories").tertiaryCat("Watches").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Clothing").secondaryCat("Accessories").tertiaryCat("Sunglasses").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Clothing").secondaryCat("Accessories").tertiaryCat("Belts").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Clothing").secondaryCat("Accessories").tertiaryCat("Hats Caps").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Entertainment").secondaryCat("Gaming").tertiaryCat("XBox One").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Entertainment").secondaryCat("Gaming").tertiaryCat("PS Five").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Entertainment").secondaryCat("Gaming").tertiaryCat("PS Older").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Entertainment").secondaryCat("Gaming").tertiaryCat("PC").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Entertainment").secondaryCat("Media").tertiaryCat("Music").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Entertainment").secondaryCat("Media").tertiaryCat("Video").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Entertainment").secondaryCat("Books").tertiaryCat("Non Fiction").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Entertainment").secondaryCat("Books").tertiaryCat("Kids").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Entertainment").secondaryCat("Books").tertiaryCat("Literature").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Entertainment").secondaryCat("Books").tertiaryCat("Fiction").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Home").secondaryCat("Textiles").tertiaryCat("Blankets").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Home").secondaryCat("Textiles").tertiaryCat("Cushions").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Home").secondaryCat("Textiles").tertiaryCat("Table Linen").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Home").secondaryCat("Textiles").tertiaryCat("Towel").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Home").secondaryCat("Textiles").tertiaryCat("Bedding").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Home").secondaryCat("Home Accessories").tertiaryCat("Cooks").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Home").secondaryCat("Home Accessories").tertiaryCat("Picture").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Home").secondaryCat("Home Accessories").tertiaryCat("Photo Frames").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Home").secondaryCat("Home Accessories").tertiaryCat("Storage").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Home").secondaryCat("Home Accessories").tertiaryCat("Mirrors").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Home").secondaryCat("Home Accessories").tertiaryCat("Vases").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Home").secondaryCat("Tableware").tertiaryCat("Dinnerware").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Home").secondaryCat("Tableware").tertiaryCat("Cutlery").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Home").secondaryCat("Tableware").tertiaryCat("Drink ware").visibility(Visibility.PUBLIC).build());
        categories.add(ProductCategory.builder().primaryCat("Other").secondaryCat("Other#2").tertiaryCat("Other#3").visibility(Visibility.PUBLIC).build());
        productCatRepository.saveAll(categories);
    }

    private void createSizes() {
        sizes=new ArrayList<>();
        sizes.add(Size.builder().sizeName("3XS").type("Cloths").build());
        sizes.add(Size.builder().sizeName("2XS").type("Cloths").build());
        sizes.add(Size.builder().sizeName("XS").type("Cloths").build());
        sizes.add(Size.builder().sizeName("S").type("Cloths").build());
        sizes.add(Size.builder().sizeName("M").type("Cloths").build());
        sizes.add(Size.builder().sizeName("L").type("Cloths").build());
        sizes.add(Size.builder().sizeName("XL").type("Cloths").build());
        sizes.add(Size.builder().sizeName("2XL").type("Cloths").build());
        sizes.add(Size.builder().sizeName("3L").type("Cloths").build());
        sizes.add(Size.builder().sizeName("4XL").type("Cloths").build());
        sizes.add(Size.builder().sizeName("25").type("Shoes").build());
        sizes.add(Size.builder().sizeName("26").type("Shoes").build());
        sizes.add(Size.builder().sizeName("27").type("Shoes").build());
        sizes.add(Size.builder().sizeName("28").type("Shoes").build());
        sizes.add(Size.builder().sizeName("29").type("Shoes").build());
        sizes.add(Size.builder().sizeName("30").type("Shoes").build());
        sizes.add(Size.builder().sizeName("31").type("Shoes").build());
        sizes.add(Size.builder().sizeName("32").type("Shoes").build());
        sizes.add(Size.builder().sizeName("33").type("Shoes").build());
        sizes.add(Size.builder().sizeName("34").type("Shoes").build());
        sizes.add(Size.builder().sizeName("35").type("Shoes").build());
        sizes.add(Size.builder().sizeName("36").type("Shoes").build());
        sizes.add(Size.builder().sizeName("37").type("Shoes").build());
        sizes.add(Size.builder().sizeName("38").type("Shoes").build());
        sizes.add(Size.builder().sizeName("39").type("Shoes").build());
        sizes.add(Size.builder().sizeName("40").type("Shoes").build());
        sizes.add(Size.builder().sizeName("41").type("Shoes").build());
        sizes.add(Size.builder().sizeName("42").type("Shoes").build());
        sizes.add(Size.builder().sizeName("43").type("Shoes").build());
        sizes.add(Size.builder().sizeName("44").type("Shoes").build());
        sizes.add(Size.builder().sizeName("45").type("Shoes").build());
        sizes.add(Size.builder().sizeName("46").type("Shoes").build());
        sizes.add(Size.builder().sizeName("47").type("Shoes").build());
        sizes.add(Size.builder().sizeName("48").type("Shoes").build());
        sizes.add(Size.builder().sizeName("49").type("Shoes").build());
        sizes.add(Size.builder().sizeName("50").type("Shoes").build());
        sizes.add(Size.builder().sizeName("SMALL_BAG").type("Bags").build());
        sizes.add(Size.builder().sizeName("MEDIUM_BAG").type("Bags").build());
        sizes.add(Size.builder().sizeName("BIG_BAG").type("Bags").build());
        sizes.add(Size.builder().sizeName("SMALL_ACC").type("Accessories").build());
        sizes.add(Size.builder().sizeName("MEDIUM_ACC").type("Accessories").build());
        sizes.add(Size.builder().sizeName("BIG_ACC").type("Accessories").build());
        sizes.add(Size.builder().sizeName("30 x 50 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("35 x 50 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("35 x 40 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("40 x 40 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("40 x 60 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("40 x 75 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("40 x 80 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("40 x 90 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("45 x 45 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("50 x 50 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("50 x 60 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("50 x 70 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("50 x 90 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("60 x 60 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("65 x 65 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("80 x 80 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("70 x 90 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("80 x 100 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("100 x 150 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("100 x 200 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("110 x 150 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("110 x 170 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("120 x 160 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("120 x 200 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("130 x 200 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("125 x 150 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("130 x 170 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("150 x 200 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("180 x 200 cm").type("Home").build());
        sizes.add(Size.builder().sizeName("other").type("Home").build());

        sizeRepository.saveAll(sizes);

    }


    public String getConversationId(User user1, User user2, @Nullable Product product){
        List<Message> messages = messageRepository.findAllMyConversation(user1.getId());

        for(Message message : messages){
            if((message.getSendUser().equals(user1) && message.getReceivedUser().equals(user2)) ||
                    (message.getSendUser().equals(user2) && message.getReceivedUser().equals(user1)))
                if(message.getProduct()!=null && message.getProduct().equals(product) && message.getConversationId()!=null)
                    return message.getConversationId();
        }
        String convID = UUID.randomUUID().toString();
        while(!messageRepository.canUseConversationId(convID))
            convID = UUID.randomUUID().toString();
        return convID;
    }


}
