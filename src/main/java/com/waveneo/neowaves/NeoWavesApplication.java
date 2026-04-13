package com.waveneo.neowaves;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NeoWavesApplication {

    public static void main(String[] args) {
        SpringApplication.run(NeoWavesApplication.class, args);
    }
    
}

//для запуска десктопа зайти в терминал и ввести: npm start
//чтобы порты не занимал: taskkill /F /IM java.exe