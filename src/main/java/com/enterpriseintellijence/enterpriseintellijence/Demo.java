//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.enterpriseintellijence.enterpriseintellijence;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Clothing;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Entertainment;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Home;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.PaymentMethod;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.ProductImage;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.Address;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.MyMoney;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.PaymentMethodRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductImageRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.UserRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Availability;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ClothingSize;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ClothingType;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Colour;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Condition;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Currency;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.EntertainmentType;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.HomeType;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ProductCategory;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ProductGender;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ProductSize;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Provider;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Visibility;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import javax.imageio.ImageIO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class Demo {
    private ArrayList<User> userArrays = new ArrayList();
    private ArrayList<Product> productArrayList;
    private String description = "The standard Lorem Ipsum passage, used since the 1500s\n\"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\"\nSection 1.10.32 of \"de Finibus Bonorum et Malorum\", written by Cicero in 45 BC\n\"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. \n";
    private ArrayList<String> brand = new ArrayList();
    private ArrayList<byte[]> productImageArrayList = new ArrayList();
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PaymentMethodRepository paymentMethodRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    public void initialize() throws IOException {
        this.initializeProductImageList();
        this.initializeBrandList();
        this.createUser();

        for(int i = 0; i < 35; ++i) {
            this.createProduct((User)this.userArrays.get(i));
        }

    }

    private void setLikeProduct() {
        List<Product> products = this.productRepository.findAll();
        Iterator var2 = this.userArrays.iterator();

        while(var2.hasNext()) {
            User user = (User)var2.next();
            Random random = new Random();
            int n = random.nextInt(3, 50);
            List<Product> likes = new ArrayList();

            for(int i = 0; i < n; ++i) {
                Product temp = (Product)products.get(random.nextInt(products.size()));
                if (!likes.contains(temp)) {
                    likes.add(temp);
                }
            }

            user.setLikedProducts(likes);
            this.userRepository.save(user);
            user.setLikedProducts((List)null);
        }

    }

    public void createUser() throws IOException {
        for(int i = 1; i < 101; ++i) {
            User user = new User();
            user.setUsername("username" + i);
            user.setPassword(this.passwordEncoder.encode("password" + i));
            user.setEmail("email" + i + "@gmail.com");
            BufferedImage bufferedImage = ImageIO.read(new File("src/main/resources/tempFileDemo/foto_profilo.png"));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", bos);
            user.setPhoto(bos.toByteArray());
            user.setProvider(Provider.LOCAL);
            user.setAddress(new Address("country" + i, "city" + i, "street" + i, "88070"));
            user.setRole(UserRole.USER);
            user = (User)this.userRepository.save(user);
            this.userArrays.add(user);
            this.createPayment(user);
        }

    }

    private void createPayment(User user) {
        int rand = ThreadLocalRandom.current().nextInt(1, 4);

        for(int i = 1; i <= rand; ++i) {
            PaymentMethod paymentMethod = new PaymentMethod();
            paymentMethod.setCreditCard("4000 4000 4000 400" + i);
            paymentMethod.setExpiryDate("03/25");
            String var10001 = user.getUsername();
            paymentMethod.setOwner(var10001 + " " + user.getUsername());
            paymentMethod.setOwnerUser(user);
            if (i == 1) {
                user.setDefaultPaymentMethod(paymentMethod);
            }

            this.paymentMethodRepository.save(paymentMethod);
        }

    }

    private void createProduct(User user) {
        this.productArrayList = new ArrayList();
        List<ProductGender> genderList = List.of((ProductGender[])ProductGender.class.getEnumConstants());
        int sizeGender = genderList.size();
        List<Colour> colourList = List.of((Colour[])Colour.class.getEnumConstants());
        int sizeColour = colourList.size();
        List<ClothingType> clothingTypeList = List.of((ClothingType[])ClothingType.class.getEnumConstants());
        int sizeClothTipe = clothingTypeList.size();
        List<ClothingSize> clothsSizeList = List.of(ClothingSize.CLOTHS_3XS, ClothingSize.CLOTHS_2XS, ClothingSize.CLOTHS_XS, ClothingSize.CLOTHS_S, ClothingSize.CLOTHS_M, ClothingSize.CLOTHS_L, ClothingSize.CLOTHS_XL, ClothingSize.CLOTHS_2XL, ClothingSize.CLOTHS_3L, ClothingSize.CLOTHS_4XL);
        int clothSize = clothsSizeList.size();
        List<ClothingSize> shoesSizeList = List.of(ClothingSize.SHOES_25, ClothingSize.SHOES_26, ClothingSize.SHOES_27, ClothingSize.SHOES_28, ClothingSize.SHOES_29, ClothingSize.SHOES_30, ClothingSize.SHOES_31, ClothingSize.SHOES_32, ClothingSize.SHOES_33, ClothingSize.SHOES_34, ClothingSize.SHOES_35, ClothingSize.SHOES_36, ClothingSize.SHOES_37, ClothingSize.SHOES_38, ClothingSize.SHOES_39, ClothingSize.SHOES_40, ClothingSize.SHOES_41, ClothingSize.SHOES_42, ClothingSize.SHOES_43, ClothingSize.SHOES_44, ClothingSize.SHOES_45, ClothingSize.SHOES_46, ClothingSize.SHOES_47, ClothingSize.SHOES_48, ClothingSize.SHOES_49, ClothingSize.SHOES_50);
        int shoesSize = shoesSizeList.size();
        List<ClothingSize> otherSizeList = List.of(ClothingSize.OTHER_SMALL, ClothingSize.OTHER_MEDIUM, ClothingSize.OTHER_BIG);
        int otherSize = otherSizeList.size();
        List<EntertainmentType> entertainmentTypeList = List.of((EntertainmentType[])EntertainmentType.class.getEnumConstants());
        int sizeEnterType = entertainmentTypeList.size();
        List<HomeType> homeTypeList = List.of((HomeType[])HomeType.class.getEnumConstants());
        int sizeHomeType = homeTypeList.size();
        Random random = new Random();
        int rand = ThreadLocalRandom.current().nextInt(10, 31);

        for(int i = 1; i <= rand; ++i) {
            int rand2 = ThreadLocalRandom.current().nextInt(1, 4);
            if (rand2 == 1) {
                Clothing clothing = new Clothing();
                clothing.setProductGender((ProductGender)genderList.get(random.nextInt(sizeGender)));
                clothing.setColour((Colour)colourList.get(random.nextInt(sizeColour)));
                clothing.setClothingType((ClothingType)clothingTypeList.get(random.nextInt(sizeClothTipe)));
                ClothingSize clothingSize = null;
                if (clothing.getClothingType().name().startsWith("CLOTHS")) {
                    clothingSize = (ClothingSize)clothsSizeList.get(random.nextInt(clothSize));
                } else if (clothing.getClothingType().name().startsWith("SHOES")) {
                    clothingSize = (ClothingSize)shoesSizeList.get(random.nextInt(shoesSize));
                } else {
                    clothingSize = (ClothingSize)otherSizeList.get(random.nextInt(otherSize));
                }

                clothing.setSize(clothingSize);
                clothing.setProductCategory(ProductCategory.CLOTHING);
                this.productArrayList.add(clothing);
            } else if (rand2 == 2) {
                Entertainment entertainment = new Entertainment();
                entertainment.setEntertainmentType((EntertainmentType)entertainmentTypeList.get(random.nextInt(sizeEnterType)));
                entertainment.setProductCategory(ProductCategory.ENTERTAINMENT);
                this.productArrayList.add(entertainment);
            } else if (rand2 == 3) {
                Home home = new Home();
                home.setHomeType((HomeType)homeTypeList.get(random.nextInt(sizeHomeType)));
                home.setColour((Colour)colourList.get(random.nextInt(sizeColour)));
                home.setProductCategory(ProductCategory.HOME);
                this.productArrayList.add(home);
            } else {
                Product product = new Product();
                product.setProductCategory(ProductCategory.OTHER);
                this.productArrayList.add(product);
            }
        }

        this.setBasicProduct(user);
    }

    private void setBasicProduct(User user) {
        int size = this.brand.size();
        Double price = 1.9D;
        List<Condition> conditionList = List.of((Condition[])Condition.class.getEnumConstants());
        int sizeCondition = conditionList.size();
        List<ProductSize> productSizeList = List.of((ProductSize[])ProductSize.class.getEnumConstants());
        int sizeProductSize = productSizeList.size();
        Random random = new Random();
        Iterator var9 = this.productArrayList.iterator();

        while(var9.hasNext()) {
            Product product = (Product)var9.next();
            int n = this.productArrayList.indexOf(product);
            product.setTitle("Title product " + n);
            product.setDescription(this.description);
            product.setBrand((String)this.brand.get(random.nextInt(size)));
            Double priceProduct = price + (double)random.nextInt(1, 1500);
            product.setMyMoney(new MyMoney(priceProduct, Currency.EUR));
            product.setCondition((Condition)conditionList.get(random.nextInt(sizeCondition)));
            product.setAddress(user.getAddress());
            product.setProductSize((ProductSize)productSizeList.get(random.nextInt(sizeProductSize)));
            product.setViews(random.nextInt(0, 120));
            LocalDateTime date = LocalDateTime.now();
            date = date.minusSeconds((long)ThreadLocalRandom.current().nextInt(1, 5184000));
            product.setUploadDate(date);
            product.setVisibility(Visibility.PUBLIC);
            product.setAvailability(Availability.AVAILABLE);
            product.setSeller(user);
            String tempID = ((Product)this.productRepository.save(product)).getId();
            product.setId(tempID);
            this.setImageToProduct(product);
        }

    }

    private void setImageToProduct(Product product) {
        Random random = new Random();

        for(int i = 0; i < 5; ++i) {
            ProductImage productImage = new ProductImage();
            productImage.setPhoto((byte[])this.productImageArrayList.get(random.nextInt(this.productImageArrayList.size())));
            productImage.setProduct(product);
            this.productImageRepository.save(productImage);
        }

    }

    private void initializeProductImageList() throws IOException {
        for(int i = 1; i < 23; ++i) {
            BufferedImage bufferedImage = ImageIO.read(new File("src/main/resources/tempFileDemo/product/" + i + ".jpeg"));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", bos);
            this.productImageArrayList.add(bos.toByteArray());
        }

    }

    private void initializeBrandList() {
        for(int i = 1; i <= 13; ++i) {
            this.brand.add("Brand " + i);
        }

    }

    public Demo(final UserRepository userRepository, final PasswordEncoder passwordEncoder, final PaymentMethodRepository paymentMethodRepository, final ProductRepository productRepository, final ProductImageRepository productImageRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.paymentMethodRepository = paymentMethodRepository;
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }
}
