package com.enterpriseintellijence.enterpriseintellijence.data.services;


import com.enterpriseintellijence.enterpriseintellijence.data.entities.Transaction;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.TransactionRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.TransactionDTO;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;
import jakarta.persistence.EntityNotFoundException;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.math.RoundingMode;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class TransactionServiceImpTest {
    /*private TransactionServiceImp transactionServiceImp;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserService userService;
    public ModelMapper modelMapper;

    public Money defaultMoney;

    private TransactionDTO defaultTransactionDTO;
    private Transaction defaultTransaction;



    @BeforeEach
    public void setUp(){
        modelMapper = new ModelMapper();
        defaultMoney = Money.of(CurrencyUnit.EUR,10, RoundingMode.DOWN);
        modelMapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE).setMatchingStrategy(MatchingStrategies.STRICT).setAmbiguityIgnored(true);

        defaultTransaction = Transaction.builder()
                .id("1")
                .amount(defaultMoney)
                .paymentMethod("paypal")
                .build();

        defaultTransactionDTO = modelMapper.map(defaultTransaction,TransactionDTO.class);
        transactionServiceImp = new TransactionServiceImp(transactionRepository,modelMapper);

    }

    @Test
    void whenMappingTransactionEntityAndTransactionDTO_thenCorrect(){
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .id("1")
                .amount(defaultMoney)
                .paymentMethod("paypal")
                .build();
        Transaction transaction = mapToEntity(transactionDTO);
        Transaction expectedTransaction = Transaction.builder()
                .id("1")
                .amount(defaultMoney)
                .paymentMethod("paypal")
                .build();

        assertThat(transaction).usingRecursiveComparison().isEqualTo(expectedTransaction);
    }

    @Test
    void whenSavingTransactionDTO_thenSaveTransaction(){
        var transactionToSave = TransactionDTO.builder()
                .id("1")
                .amount(defaultMoney)
                .paymentMethod("paypal")
                .build();
        var transactionToSaveEntity = Transaction.builder()
                .id("1")
                .amount(defaultMoney)
                .paymentMethod("paypal")
                .build();

        when(transactionRepository.save(transactionToSaveEntity)).thenReturn(defaultTransaction);
        TransactionDTO savedTransaction = transactionServiceImp.createTransaction(transactionToSave);
        assertThat(savedTransaction).usingRecursiveComparison().isEqualTo(mapToDTO(defaultTransaction));
    }

    //todo: da fare test whenReplacinTransactionDTO_throwOnIdMismatch(), in quanto manca il metodo nel service


    //todo: questo metodo non parte per il motivo spiegato nel todo di prima
    @Test
    void whenReplacingTransactionDTO_throwOnTransactionNotFound(){
        TransactionDTO transactionToReplace = TransactionDTO.builder()
                .id("1")
                .amount(defaultMoney)
                .paymentMethod("paypal")
                .build();
        when(transactionRepository.findById("1")).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            transactionServiceImp.replaceTransaction("1",transactionToReplace);
        });
    }
    public TransactionDTO mapToDTO(Transaction transaction){
        return modelMapper.map(transaction,TransactionDTO.class);
    }
    public Transaction mapToEntity(TransactionDTO transactionDTO){
        return modelMapper.map(transactionDTO,Transaction.class);}*/
}

