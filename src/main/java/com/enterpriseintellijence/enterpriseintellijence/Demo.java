package com.enterpriseintellijence.enterpriseintellijence;

import com.enterpriseintellijence.enterpriseintellijence.controller.UserController;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.*;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.CustomMoney;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.*;
import com.enterpriseintellijence.enterpriseintellijence.data.services.UserService;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Currency;
import com.nimbusds.jose.JOSEException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.PrePersist;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
    private ArrayList<Product> productArrayList ;
    private String description = "The standard Lorem Ipsum passage, used since the 1500s\n" ;
    private ArrayList<String> brand =new ArrayList<>();
    private ArrayList<byte[]> productImageArrayList = new ArrayList<>();
    private ArrayList<String> userIdArrays=new ArrayList<>();

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PaymentMethodRepository paymentMethodRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final MessageRepository messageRepository;
    private final AddressRepository addressRepository;



    public void initialize() throws IOException {
        //initializeProductImageList();
        initializeBrandList();
        createUser();
        setAddress();
        try{
            for(User user:userArrays){
                createProduct(user);
                createPayment(user);
                setFollower(user);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        userArrays = (ArrayList<User>) userRepository.findAll();
        setMessage();
        //convertToString();
        //createFollowersAndFollowing();
        //setLikeProduct();


    }


    public void createUser() throws IOException {

        for (int i=1; i<50;i++){
            User user = new User();
            user.setUsername("username"+i);
            user.setPassword(passwordEncoder.encode("password"+i));
            user.setEmail("email"+i+"@gmail.com");
            BufferedImage bufferedImage = ImageIO.read(new File("src/main/resources/tempFileDemo/foto_profilo.png"));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage,"png",bos);
            //UserImage userImage = new UserImage();
            ///userImage.setPhoto(bos.toByteArray());
            //userImage.setDescription("No description avalaible");
            //userImage.setUser(user);
            //user.setPhotoProfile(userImage);
            user.setProvider(Provider.LOCAL);
            //user.setAddress(new Address("country"+i,"city"+i,"street"+i,"88070"));
            user.setRole(UserRole.USER);
            user.setStatus(UserStatus.ACTIVE);
            user.setEmailVerified(true);

            user.setFollowers_number(0);
            user.setFollowing_number(0);

            //user.setDefaultPaymentMethod(createPayment(user));
            user = userRepository.save(user);
            userArrays.add(user);
            createPayment(user);
/*            if(i<=35)
                createProduct(user);*/

        }

        convertToString();


    }

    private void convertToString() {
        for(User user: userArrays)
            userIdArrays.add(user.getId());
        //userArrays.clear();
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
        if(setDefault) {
            //address.setDefaultUser(user);
            user.setDefaultAddress(address);
        }
        address.setUser(user);
    }

    private void setMessage() {
        Random rand = new Random();
        List<Message> messages;
        for (User user : userArrays) {
            messages = new ArrayList<>();
            int n = rand.nextInt(1, 15);
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
            int n= random.nextInt(15);
            for (int i=0;i<n;i++){

                String id2 =userIdArrays.get(random.nextInt(userIdArrays.size())) ;
                User user2= userRepository.findById(id2).orElseThrow(EntityExistsException::new);
                if(!user.getId().equals(user2.getId())){
                        List<User> local = user.getFollowing();
                        local.add(user2);
                        user.getFollowing().addAll(local);
                        user.setFollowing_number(user.getFollowing_number()+1);
                        List<User> local2 = user2.getFollowers();
                        local2.add(user);
                        user2.getFollowers().addAll(local2);
                        user2.setFollowers_number(user2.getFollowers_number()+1);
                }
            }
            userRepository.save(user);
        }catch (Exception e){e.printStackTrace();}
    }








    private void setLikeProduct(){
/*        // TODO: 13/05/2023 perchè non va????
        List<Product> products = productRepository.findAll();

        for (User user: userArrays){
            Random random = new Random();
            int n= random.nextInt(3,50);
            List<Product> likes=new ArrayList<>();
            for (int i=0;i<n;i++){
                Product temp = products.get(random.nextInt(products.size()));
                if(!likes.contains(temp))
                    likes.add(temp);
            }
            user.setLikedProducts(likes);
            userRepository.save(user);
            user.setLikedProducts(null);
        }*/
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
                user.setDefaultPaymentMethod(paymentMethod);
            paymentMethodRepository.save(paymentMethod);

        }
    }

    private void createProduct(User user){
        productArrayList = new ArrayList<>();

        List<ProductCategoryChild> type = List.of(ProductCategoryChild.class.getEnumConstants());
        int sizeType = type.size();

        //for clothing enum
        List<ProductGender> genderList = List.of(ProductGender.class.getEnumConstants());
        int sizeGender = genderList.size();

        List<Colour> colourList = List.of(Colour.class.getEnumConstants());
        int sizeColour = colourList.size();

        List<ClothingSize> clothsSizeList = List.of(ClothingSize.class.getEnumConstants());
        int clothSize = clothsSizeList.size();


        //for entertainment enum
        List<EntertainmentLanguage> entertainmentLanguageList = List.of(EntertainmentLanguage.class.getEnumConstants());
        int sizeLanguage = entertainmentLanguageList.size();

        //for home enum
        List<HomeSize> homeSizeList = List.of(HomeSize.class.getEnumConstants());
        int sizeHomeSize = homeSizeList.size();

        List<HomeMaterial> homeMaterials = List.of(HomeMaterial.class.getEnumConstants());
        int sizeHomeMaterial = homeMaterials.size();


        Random random = new Random();

        int rand = ThreadLocalRandom.current().nextInt(1, 6);
        for (int i=1;i<=rand;i++){

            int rand2=ThreadLocalRandom.current().nextInt(0, 4);
            //setting clothing
            if (rand2==1){
                Clothing clothing = new Clothing();
                clothing.setProductGender(genderList.get(random.nextInt(sizeGender)));
                clothing.setColour(colourList.get(random.nextInt(sizeColour)));
                ProductCategoryChild productCategoryChild = type.get(random.nextInt(sizeType));
                while(!productCategoryChild.productCategoryParent.getProductCategory().equals(ProductCategory.CLOTHING)) {
                    productCategoryChild = type.get(random.nextInt(sizeType));
                }
                clothing.setProductCategoryChild(productCategoryChild);
                clothing.setProductCategoryParent(productCategoryChild.productCategoryParent);
                clothing.setProductCategory(clothing.getProductCategoryParent().productCategory);

                ClothingSize clothingSize = clothsSizeList.get(random.nextInt(clothSize));
                while(clothingSize.productCategoryParent.equals(clothing.getProductCategoryParent())) {
                    clothingSize = clothsSizeList.get(random.nextInt(clothSize));
                }

                clothing.setSize(clothingSize);
                //clothing.setProductCategory(ProductCategory.CLOTHING);
                productArrayList.add(clothing);
            }
            //setting entertainment
            else if(rand2==2){
                Entertainment entertainment = new Entertainment();
                ProductCategoryChild productCategoryChild = type.get(random.nextInt(sizeType));
                while(!productCategoryChild.productCategoryParent.getProductCategory().equals(ProductCategory.ENTERTAINMENT)) {
                    productCategoryChild = type.get(random.nextInt(sizeType));
                }
                entertainment.setProductCategoryChild(productCategoryChild);
                entertainment.setProductCategoryParent(productCategoryChild.productCategoryParent);
                entertainment.setProductCategory(entertainment.getProductCategoryParent().productCategory);
                entertainment.setEntertainmentLanguage(entertainmentLanguageList.get(random.nextInt(sizeLanguage)));
                //entertainment.setProductCategory(ProductCategory.ENTERTAINMENT);
                productArrayList.add(entertainment);
            }
            //setting home
            else if(rand2==3){
                Home home = new Home();
                home.setHomeSize(homeSizeList.get(random.nextInt(sizeHomeSize)));
                ProductCategoryChild productCategoryChild = type.get(random.nextInt(sizeType));
                while(!productCategoryChild.productCategoryParent.getProductCategory().equals(ProductCategory.HOME)) {
                    productCategoryChild = type.get(random.nextInt(sizeType));
                }
                home.setProductCategoryChild(productCategoryChild);
                home.setProductCategoryParent(productCategoryChild.productCategoryParent);
                home.setProductCategory(home.getProductCategoryParent().productCategory);
                home.setColour(colourList.get(random.nextInt(sizeColour)));
                home.setHomeMaterial(homeMaterials.get(random.nextInt(sizeHomeMaterial)));
                //home.setProductCategory(ProductCategory.HOME);
                productArrayList.add(home);
            }
            else{
                Product product=new Product();
                product.setProductCategory(ProductCategory.OTHER);
                product.setProductCategoryParent(ProductCategoryParent.OTHER);
                product.setProductCategoryChild(ProductCategoryChild.OTHER);
                //product.setProductCategory(ProductCategory.OTHER);
                productArrayList.add(product);
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

        for (Product product: productArrayList){

            int n = productArrayList.indexOf(product);
            product.setTitle("Title product "+n);
            product.setDescription(description);
            product.setBrand(brand.get(random.nextInt(size)));

            Double priceProduct = price+random.nextInt(1,1500);
            product.setProductCost(new CustomMoney(priceProduct, Currency.EUR ));
            product.setDeliveryCost(new CustomMoney(5.00,Currency.EUR));

            product.setLikesNumber(random.nextInt(150));

            product.setCondition(conditionList.get(random.nextInt(sizeCondition)));
            //product.setAddress(user.getAddress());
            product.setProductSize(productSizeList.get(random.nextInt(sizeProductSize)));
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
            String tempID= productRepository.save(product).getId();
            product.setId(tempID);
            //setImageToProduct(product);
        }
    }

    private void setImageToProduct(Product product){
        Random random = new Random();
        int n = random.nextInt(1,6);
        for (int i=1;i<=n;i++){
            ProductImage productImage = new ProductImage();
            productImage.setDescription("Random description bla bla");
            //productImage.setUrlPhoto(productImageArrayList.get(random.nextInt(productImageArrayList.size())).getClass().);
            //productImage.setPhoto();
            productImage.setProduct(product);
            productImageRepository.save(productImage);
        }
    }

    private void initializeProductImageList() throws IOException {

        for(int i=1;i<6;i++){
            BufferedImage bufferedImage = ImageIO.read(new File("src/main/resources/tempFileDemo/product/"+i+".jpeg"));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage,"jpg",bos);
            productImageArrayList.add(bos.toByteArray());
        }

    }

    private void initializeBrandList(){
        for (int i=1;i<=13; i++){
            brand.add("Brand "+i);
        }
    }




}
