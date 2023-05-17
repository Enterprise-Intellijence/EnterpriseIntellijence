package com.enterpriseintellijence.enterpriseintellijence;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.*;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.Address;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.CustomMoney;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.PaymentMethodRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductImageRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.UserRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Currency;
import lombok.RequiredArgsConstructor;
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
    private String description = "The standard Lorem Ipsum passage, used since the 1500s\n" +
            "\"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\"\n" +
            "Section 1.10.32 of \"de Finibus Bonorum et Malorum\", written by Cicero in 45 BC\n" +
            "\"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. \n";
    private ArrayList<String> brand =new ArrayList<>();
    private ArrayList<byte[]> productImageArrayList = new ArrayList<>();

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PaymentMethodRepository paymentMethodRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;



    public void initialize() throws IOException {
        initializeProductImageList();
        initializeBrandList();
        createUser();
        for(int i=0;i<35;i++){
            createProduct(userArrays.get(i));
        }
        //setLikeProduct();


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

    public void createUser() throws IOException {

        for (int i=1; i<101;i++){
            User user = new User();
            user.setUsername("username"+i);
            user.setPassword(passwordEncoder.encode("password"+i));
            user.setEmail("email"+i+"@gmail.com");
            BufferedImage bufferedImage = ImageIO.read(new File("src/main/resources/tempFileDemo/foto_profilo.png"));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage,"png",bos);
            user.setPhoto(bos.toByteArray());
            user.setProvider(Provider.LOCAL);
            user.setAddress(new Address("country"+i,"city"+i,"street"+i,"88070"));
            user.setRole(UserRole.USER);
            //user.setDefaultPaymentMethod(createPayment(user));
            user = userRepository.save(user);
            userArrays.add(user);
            createPayment(user);
/*            if(i<=35)
                createProduct(user);*/

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
                user.setDefaultPaymentMethod(paymentMethod);
            paymentMethodRepository.save(paymentMethod);

        }
    }

    private void createProduct(User user){
        productArrayList = new ArrayList<>();

        //for clothing enum
        List<ProductGender> genderList = List.of(ProductGender.class.getEnumConstants());
        int sizeGender = genderList.size();

        List<Colour> colourList = List.of(Colour.class.getEnumConstants());
        int sizeColour = colourList.size();

        List<ClothingType> clothingTypeList = List.of(ClothingType.class.getEnumConstants());
        int sizeClothTipe = clothingTypeList.size();


        List<ClothingSize> clothsSizeList = List.of(ClothingSize.CLOTHS_3XS,ClothingSize.CLOTHS_2XS,ClothingSize.CLOTHS_XS,ClothingSize.CLOTHS_S,ClothingSize.CLOTHS_M,ClothingSize.CLOTHS_L,ClothingSize.CLOTHS_XL,ClothingSize.CLOTHS_2XL,ClothingSize.CLOTHS_3L,ClothingSize.CLOTHS_4XL);
        int clothSize = clothsSizeList.size();

        List<ClothingSize> shoesSizeList = List.of(ClothingSize.SHOES_25,ClothingSize.SHOES_26,ClothingSize.SHOES_27,ClothingSize.SHOES_28,ClothingSize.SHOES_29,ClothingSize.SHOES_30,ClothingSize.SHOES_31,
                ClothingSize.SHOES_32,ClothingSize.SHOES_33,ClothingSize.SHOES_34,ClothingSize.SHOES_35,ClothingSize.SHOES_36,ClothingSize.SHOES_37,ClothingSize.SHOES_38,ClothingSize.SHOES_39,
                ClothingSize.SHOES_40,ClothingSize.SHOES_41,ClothingSize.SHOES_42,ClothingSize.SHOES_43,ClothingSize.SHOES_44,ClothingSize.SHOES_45,ClothingSize.SHOES_46,ClothingSize.SHOES_47,
                ClothingSize.SHOES_48,ClothingSize.SHOES_49,ClothingSize.SHOES_50);
        int shoesSize = shoesSizeList.size();

        List<ClothingSize> otherSizeList = List.of(ClothingSize.OTHER_SMALL,ClothingSize.OTHER_MEDIUM,ClothingSize.OTHER_BIG);
        int otherSize =otherSizeList.size();

        //for entertainment enum
        List<EntertainmentType> entertainmentTypeList = List.of(EntertainmentType.class.getEnumConstants());
        int sizeEnterType = entertainmentTypeList.size();

        //for home enum
        List<HomeType> homeTypeList = List.of(HomeType.class.getEnumConstants());
        int sizeHomeType = homeTypeList.size();

        Random random = new Random();

        int rand = ThreadLocalRandom.current().nextInt(5, 15);
        for (int i=1;i<=rand;i++){

            int rand2=ThreadLocalRandom.current().nextInt(1, 4);
            //setting clothing
            if (rand2==1){
                Clothing clothing = new Clothing();
                clothing.setProductGender(genderList.get(random.nextInt(sizeGender)));
                clothing.setColour(colourList.get(random.nextInt(sizeColour)));
                clothing.setClothingType(clothingTypeList.get(random.nextInt(sizeClothTipe)));
                ClothingSize clothingSize = null;
                if(clothing.getClothingType().name().startsWith("CLOTHS"))
                    clothingSize = clothsSizeList.get(random.nextInt(clothSize));
                else if(clothing.getClothingType().name().startsWith("SHOES"))
                    clothingSize = shoesSizeList.get(random.nextInt(shoesSize));
                else
                    clothingSize = otherSizeList.get(random.nextInt(otherSize));
                clothing.setSize(clothingSize);
                clothing.setProductCategory(ProductCategory.CLOTHING);
                productArrayList.add(clothing);
            }
            //setting entertainment
            else if(rand2==2){
                Entertainment entertainment = new Entertainment();
                entertainment.setEntertainmentType(entertainmentTypeList.get(random.nextInt(sizeEnterType)));
                entertainment.setProductCategory(ProductCategory.ENTERTAINMENT);
                productArrayList.add(entertainment);
            }
            //setting home
            else if(rand2==3){
                Home home = new Home();
                home.setHomeType(homeTypeList.get(random.nextInt(sizeHomeType)));
                home.setColour(colourList.get(random.nextInt(sizeColour)));
                home.setProductCategory(ProductCategory.HOME);
                productArrayList.add(home);
            }
            else{
                Product product=new Product();
                product.setProductCategory(ProductCategory.OTHER);
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

        for (Product product: productArrayList){

            int n = productArrayList.indexOf(product);
            product.setTitle("Title product "+n);
            product.setDescription(description);
            product.setBrand(brand.get(random.nextInt(size)));

            Double priceProduct = price+random.nextInt(1,1500);
            product.setCustomMoney(new CustomMoney(priceProduct, Currency.EUR ));
            product.setCondition(conditionList.get(random.nextInt(sizeCondition)));
            product.setAddress(user.getAddress());
            product.setProductSize(productSizeList.get(random.nextInt(sizeProductSize)));
            product.setViews(random.nextInt(0,120));
            LocalDateTime date = LocalDateTime.now();
            date = date.minusSeconds(ThreadLocalRandom.current().nextInt(1, 5184000));
            product.setUploadDate(date);
            product.setVisibility(Visibility.PUBLIC);
            product.setAvailability(Availability.AVAILABLE);
            product.setSeller(user);
            String tempID= productRepository.save(product).getId();
            product.setId(tempID);
            setImageToProduct(product);
        }

    }

    private void setImageToProduct(Product product){
        Random random = new Random();
        for (int i=0;i<5;i++){
            ProductImage productImage = new ProductImage();
            productImage.setPhoto(productImageArrayList.get(random.nextInt(productImageArrayList.size())));
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
