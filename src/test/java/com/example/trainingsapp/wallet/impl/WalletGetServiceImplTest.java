package com.example.trainingsapp.wallet.impl;

import com.example.trainingsapp.user.api.model.User;
import com.example.trainingsapp.wallet.api.WalletRepository;
import com.example.trainingsapp.wallet.api.dto.WalletDTO;
import com.example.trainingsapp.wallet.api.model.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class WalletGetServiceImplTest {
    @InjectMocks
    private WalletServiceImpl walletService;
    @Mock
    private WalletRepository walletRepository;
    User user;
    Wallet wallet;
    Wallet wallet2;
    AutoCloseable autoCloseable;


    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        user = User.builder()
                .id(1L)
                .build();

        wallet = Wallet.builder()
                .user(user)
                .name("slodycze")
                .creationDate(Instant.now())
                .build();

        wallet2 = Wallet.builder()
                .user(user)
                .name("zakupy")
                .creationDate(Instant.now())
                .build();
    }

    @Test
    void testGetWallets(){
        // given
        when(walletRepository.findAllByUserIdOrderByNameAsc(1L)).thenReturn(Arrays.asList(wallet,wallet2));

        // when
        List<WalletDTO> result = walletService.getWallets(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(walletService.getWallets(1L).get(0).getName()).isEqualTo(wallet.getName());
        assertThat(walletService.getWallets(1L).get(1).getName()).isEqualTo(wallet2.getName());
    }

}
