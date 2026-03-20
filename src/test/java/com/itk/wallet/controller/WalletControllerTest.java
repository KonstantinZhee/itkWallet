package com.itk.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itk.wallet.dto.WalletRequest;
import com.itk.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class WalletControllerTest {

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UUID testWalletId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();
        objectMapper = new ObjectMapper();
        testWalletId = UUID.randomUUID();
    }

    @Test
    void testDeposit() throws Exception {
        WalletRequest request = new WalletRequest();
        request.setWalletId(testWalletId);
        request.setOperationType(WalletRequest.OperationType.DEPOSIT);
        request.setAmount(BigDecimal.valueOf(1000));
        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        verify(walletService, times(1)).processTransaction(any(WalletRequest.class));
    }

    @Test
    void testGetBalance() throws Exception {
        when(walletService.getBalance(testWalletId)).thenReturn(BigDecimal.valueOf(5000));
        mockMvc.perform(get("/api/v1/wallets/{walletId}", testWalletId))
                .andExpect(status().isOk())
                .andExpect(content().string("5000"));
        verify(walletService, times(1)).getBalance(testWalletId);
    }

    @Test
    void testCreateWallet() throws Exception {
        UUID newWalletId = UUID.randomUUID();
        when(walletService.createWallet(null)).thenReturn(newWalletId);
        mockMvc.perform(post("/api/v1/wallets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(newWalletId.toString()));
        verify(walletService, times(1)).createWallet(null);
    }

    @Test
    void testCreateWalletWithBalance() throws Exception {
        UUID newWalletId = UUID.randomUUID();
        when(walletService.createWallet(BigDecimal.valueOf(1000))).thenReturn(newWalletId);
        mockMvc.perform(post("/api/v1/wallets")
                        .param("initialBalance", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(newWalletId.toString()));
        verify(walletService, times(1)).createWallet(BigDecimal.valueOf(1000));
    }

    @Test
    void testEmptyRequest() throws Exception {
        WalletRequest emptyRequest = new WalletRequest();
        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest());
        verify(walletService, never()).processTransaction(any());
    }

    @Test
    void testWithdraw() throws Exception {
        WalletRequest request = new WalletRequest();
        request.setWalletId(testWalletId);
        request.setOperationType(WalletRequest.OperationType.WITHDRAW);
        request.setAmount(BigDecimal.valueOf(500));
        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        verify(walletService, times(1)).processTransaction(any(WalletRequest.class));
    }

    @Test
    void testInvalidUuid() throws Exception {
        mockMvc.perform(get("/api/v1/wallets/123"))
                .andExpect(status().isBadRequest());
    }
}