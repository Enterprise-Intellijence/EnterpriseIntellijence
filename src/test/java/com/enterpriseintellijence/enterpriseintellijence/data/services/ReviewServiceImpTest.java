package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Review;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ReviewRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.ReviewDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserFullDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Provider;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ReviewServiceImpTest {
    private ReviewServiceImp reviewServiceImp;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserService userService;

    public ModelMapper modelMapper;

    private ReviewDTO defaultReviewDTO;
    private Review defaultReview;

    private UserFullDTO defaultUserFullDTO;
    private User defaultUserEntity;


    @BeforeEach
    public void setUp() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE).setMatchingStrategy(MatchingStrategies.STRICT).setAmbiguityIgnored(true);

        defaultUserFullDTO = UserFullDTO.builder()
                .id("1")
                .username("username")
                .password("password")
                .email("email@gmail.com")
                .role(UserRole.USER)
                .provider(Provider.LOCAL)
                .build();

        defaultUserEntity = modelMapper.map(defaultUserFullDTO, User.class);

        defaultReview = Review.builder()
                .id("1")
                .title("title")
                .description("descr")
                .vote(5)
                .reviewer(defaultUserEntity)
                .build();

        defaultReviewDTO = modelMapper.map(defaultReview, ReviewDTO.class);

        reviewServiceImp = new ReviewServiceImp(reviewRepository, modelMapper, userService);
    }

    @Test
    void whenMappingReviewEntityAndReviewDTO_thenCorrect() {

        ReviewDTO reviewDTO = ReviewDTO.builder()
                .title("title")
                .description("descr")
                .vote(5)
                .reviewer(defaultUserFullDTO)
                .build();

        Review review = mapToEntity(reviewDTO);

        Review expectedReview = Review.builder()
                .title("title")
                .description("descr")
                .vote(5)
                .reviewer(defaultUserEntity)
                .build();

        assertThat(review).usingRecursiveComparison().isEqualTo(expectedReview);
    }


    @Test
    void whenSavingReviewDTO_thenSaveReview() {

        var ReviewToSaveEntity = Review.builder()
                .id("1")
                .title("title")
                .description("descr")
                .vote(5)
                .reviewer(defaultUserEntity)
                .build();

        when(userService.findUserFromContext()).thenReturn(Optional.of(defaultUserFullDTO));
        when(reviewRepository.save(ReviewToSaveEntity)).thenReturn(defaultReview);

        ReviewDTO savedReview = reviewServiceImp.createReview(defaultReviewDTO);

        assertThat(savedReview).usingRecursiveComparison().isEqualTo(mapToDTO(defaultReview));
    }


    @Test
    void whenReplacingReviewDTO_throwOnIdMismatch() {
        ReviewDTO newReview = ReviewDTO.builder()
                .id("NOT 1")
                .title("title")
                .description("descr")
                .vote(5)
                .reviewer(defaultUserFullDTO)
                .build();

//        when(reviewRepository.findById("1")).thenReturn(Optional.of(defaultReview));

        Assertions.assertThrows(IdMismatchException.class, () -> {
            reviewServiceImp.replaceReview("1", newReview);
        });
    }

    @Test
    void whenReplacingReviewDTO_throwOnReviewNotFound() {
        ReviewDTO reviewToReplace = ReviewDTO.builder()
                .id("1")
                .vote(3)
                .reviewer(defaultUserFullDTO)
                .build();

        when(reviewRepository.findById("1")).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            reviewServiceImp.replaceReview("1", reviewToReplace);
        });
    }


    @Test
    void whenRequestingUserDifferentFromNewReviewUser_thenThrow() {
        UserFullDTO differentUserFullDTO = UserFullDTO.builder()
                .id("2")
                .username("anotherUsername")
                .password("password")
                .email("another@email.com")
                .role(UserRole.USER)
                .provider(Provider.LOCAL)
                .build();

        ReviewDTO newReview = ReviewDTO.builder()
                .id("1")
                .reviewer(differentUserFullDTO)
                .build();

        when(reviewRepository.findById("1")).thenReturn(Optional.of(defaultReview));
        when(userService.findUserFromContext()).thenReturn(Optional.of(defaultUserFullDTO));

        Assertions.assertThrows(IllegalAccessException.class, () -> {
            reviewServiceImp.replaceReview("1", newReview);
        });
    }

    @Test
    void whenRequestingUserDifferentFromOldReviewUser_thenThrow() {
        UserFullDTO anotherUserFullDTO = UserFullDTO.builder()
                .id("2")
                .username("anotherUsername")
                .password("password")
                .email("another@email.com")
                .role(UserRole.USER)
                .provider(Provider.LOCAL)
                .build();

        when(userService.findUserFromContext()).thenReturn(Optional.of(anotherUserFullDTO));
        when(reviewRepository.findById("1")).thenReturn(Optional.of(defaultReview));

        Assertions.assertThrows(IllegalAccessException.class, () -> {
            reviewServiceImp.replaceReview("1", defaultReviewDTO);
        });
    }


    @Test
    void whenReplacingReviewDTO_thenReplaceReview() throws IllegalAccessException {
        ReviewDTO reviewToReplace = ReviewDTO.builder()
                .id("1")
                .title("title")
                .description("descr")
                .vote(5)
                .reviewer(defaultUserFullDTO)
                .build();

        when(reviewRepository.findById("1")).thenReturn(Optional.of(defaultReview));
        when(reviewRepository.save(defaultReview)).thenReturn(defaultReview);
        when(userService.findUserFromContext()).thenReturn(Optional.of(defaultUserFullDTO));

        ReviewDTO replacedReview = reviewServiceImp.replaceReview("1", reviewToReplace);
    }

    @Test
    void whenGetReviewById_thenCorrect() {
        when(reviewRepository.findById("1")).thenReturn(Optional.of(defaultReview));
        ReviewDTO foundReview = reviewServiceImp.reviewById("1");
        assertThat(foundReview).usingRecursiveComparison().isEqualTo(defaultReviewDTO);
    }


    // Ho provato a testare findAll, ma non funziona
    /* @Test
    void whenGetAllReviews_thenCorrect() {
        Review review2 = Review.builder()
                .id("2")
                .title("title2")
                .description("descr2")
                .vote(1)
                .reviewer(defaultUserEntity)
                .build();

        ReviewDTO reviewDTO2 = ReviewDTO.builder()
                .id("2")
                .title("title2")
                .description("descr2")
                .vote(1)
                .reviewer(defaultUserDTO)
                .build();

        Review [] reviewArray = {defaultReview, review2};
        Iterable<Review> reviews = Arrays.asList(reviewArray);
        Iterable<ReviewDTO> dtosMapped = mapToDTO(reviews);

        ReviewDTO [] reviewDTOArray = {defaultReviewDTO, reviewDTO2};
        Iterable<ReviewDTO> reviewDTOs = Lists.newArrayList(reviewDTOArray);

        doReturn(reviewDTOs).when(reviewRepository.findAll());
        Iterable<ReviewDTO> dtosFound = reviewServiceImp.findAll();

        assertThat(dtosMapped).usingRecursiveComparison().isEqualTo(dtosFound);
    }*/


    public Review mapToEntity(ReviewDTO ReviewDTO) {
        return modelMapper.map(ReviewDTO, Review.class);
    }

    public ReviewDTO mapToDTO(Review Review) {
        return modelMapper.map(Review, ReviewDTO.class);
    }

    private List<ReviewDTO> mapToDTO(Iterable<Review> reviews) {
        List<ReviewDTO> reviewDTOs = new ArrayList<>();
        modelMapper.map(reviews,reviewDTOs);
        return reviewDTOs;
    }
}
