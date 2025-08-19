package com.github.navelogic.conversor_one.Controller;

import com.github.navelogic.conversor_one.Service.CurrencyService;
import com.github.navelogic.conversor_one.Util.ConsoleColors;
import com.github.navelogic.conversor_one.Util.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class TerminalInterface implements CommandLineRunner {

    private final CurrencyService currencyService;

    private final Scanner scanner = new Scanner(System.in);
    private final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    @Override
    public void run(String... args) {
        showWelcome();

        while (true) {
            showMainMenu();

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1 -> performConversion();
                    case 2 -> showExchangeRates();
                    case 3 -> showCurrencyInfo();
                    case 4 -> showAbout();
                    case 5 -> {
                        showGoodbye();
                        return;
                    }
                    default -> System.out.println(ConsoleColors.RED_BOLD +
                            "❌ Opção inválida! Tente novamente." +
                            ConsoleColors.RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(ConsoleColors.RED_BOLD +
                        "❌ Por favor, digite um número válido!" +
                        ConsoleColors.RESET);
            }

            System.out.println("\n" + ConsoleColors.YELLOW +
                    "Pressione Enter para continuar..." +
                    ConsoleColors.RESET);
            scanner.nextLine();
        }
    }

    private void showWelcome() {
        clearScreen();
        System.out.println(ConsoleColors.CYAN_BOLD +
                "╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                ║");
        System.out.println("║                   💱  CONVERSOR DE MOEDAS  💱                  ║");
        System.out.println("║                                                                ║");
        System.out.println("║                    Desenvolvido em Java 21                     ║");
        System.out.println("║                      com Spring Boot                           ║");
        System.out.println("║                                                                ║");
        System.out.println("║                    © 2025 - Por Navelogic                      ║");
        System.out.println("║                                                                ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝"
                + ConsoleColors.RESET);
        System.out.println();
    }

    private void showMainMenu() {
        System.out.println(ConsoleColors.BLUE_BOLD +
                "┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│                        MENU PRINCIPAL                      │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ 1. 💸 Converter Moedas                                     │");
        System.out.println("│ 2. 📊 Ver Taxas de Câmbio                                  │");
        System.out.println("│ 3. 📖 Informações sobre Moedas                             │");
        System.out.println("│ 4. ℹ️  Sobre o Sistema                                      │");
        System.out.println("│ 5. 👋 Sair                                                 │");
        System.out.println("└─────────────────────────────────────────────────────────────┘" +
                ConsoleColors.RESET);
        System.out.print(ConsoleColors.WHITE_BOLD + "Digite sua opção: " + ConsoleColors.RESET);
    }

    private void performConversion() {
        clearScreen();
        System.out.println(ConsoleColors.GREEN_BOLD +
                "💸 CONVERSÃO DE MOEDAS" + ConsoleColors.RESET);
        System.out.println("═".repeat(50));

        Currency fromCurrency = selectCurrency("Moeda de origem");
        if (fromCurrency == null) return;

        Currency toCurrency = selectCurrency("Moeda de destino");
        if (toCurrency == null) return;

        System.out.print(ConsoleColors.WHITE_BOLD +
                "Digite o valor a converter: " + ConsoleColors.RESET);

        try {
            double amount = Double.parseDouble(scanner.nextLine());

            if (amount <= 0) {
                System.out.println(ConsoleColors.RED_BOLD +
                        "❌ O valor deve ser positivo!" + ConsoleColors.RESET);
                return;
            }

            double result = currencyService.convertCurrency(fromCurrency, toCurrency, amount);

            System.out.println();
            System.out.println(ConsoleColors.GREEN_BACKGROUND + ConsoleColors.BLACK_BOLD +
                    "                RESULTADO DA CONVERSÃO                " +
                    ConsoleColors.RESET);
            System.out.println();
            System.out.println(ConsoleColors.YELLOW_BOLD +
                    fromCurrency.getSymbol() + " " + decimalFormat.format(amount) +
                    " " + fromCurrency.getName() + ConsoleColors.RESET);
            System.out.println(ConsoleColors.CYAN_BOLD + "                    ↓" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.GREEN_BOLD +
                    toCurrency.getSymbol() + " " + decimalFormat.format(result) +
                    " " + toCurrency.getName() + ConsoleColors.RESET);

        } catch (NumberFormatException e) {
            System.out.println(ConsoleColors.RED_BOLD +
                    "❌ Valor inválido! Digite um número." + ConsoleColors.RESET);
        }
    }

    private Currency selectCurrency(String prompt) {
        System.out.println();
        System.out.println(ConsoleColors.PURPLE_BOLD + prompt + ":" + ConsoleColors.RESET);
        System.out.println("─".repeat(30));

        Currency[] currencies = Currency.values();
        for (int i = 0; i < currencies.length; i++) {
            System.out.printf("%s%2d. %s %s - %s%s%n",
                    ConsoleColors.CYAN,
                    i + 1,
                    currencies[i].getSymbol(),
                    currencies[i].name(),
                    currencies[i].getName(),
                    ConsoleColors.RESET);
        }

        System.out.print(ConsoleColors.WHITE_BOLD + "Digite o número da moeda: " + ConsoleColors.RESET);

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice >= 1 && choice <= currencies.length) {
                return currencies[choice - 1];
            } else {
                System.out.println(ConsoleColors.RED_BOLD +
                        "❌ Opção inválida!" + ConsoleColors.RESET);
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println(ConsoleColors.RED_BOLD +
                    "❌ Digite um número válido!" + ConsoleColors.RESET);
            return null;
        }
    }

    private void showExchangeRates() {
        clearScreen();
        System.out.println(ConsoleColors.BLUE_BOLD +
                "📊 TAXAS DE CÂMBIO" + ConsoleColors.RESET);
        System.out.println("═".repeat(50));
        boolean apiAvailable = currencyService.isApiAvailable();
        String updateTime = currencyService.getLastUpdateTime();

        if (apiAvailable) {
            System.out.println(ConsoleColors.GREEN_BOLD +
                    "✅ Conectado à API - Taxas em tempo real" + ConsoleColors.RESET);
        } else {
            System.out.println(ConsoleColors.YELLOW_BOLD +
                    "⚠️  Usando taxas de backup (API indisponível)" + ConsoleColors.RESET);
        }

        System.out.println(ConsoleColors.CYAN +
                "Última atualização: " + updateTime + ConsoleColors.RESET);
        System.out.println();

        System.out.println(ConsoleColors.YELLOW_BOLD +
                "Taxas baseadas em 1 USD:" + ConsoleColors.RESET);
        System.out.println();

        Currency[] currencies = {Currency.ARS, Currency.BOB, Currency.BRL,
                Currency.CLP, Currency.COP};

        for (Currency currency : currencies) {
            double rate = currencyService.convertCurrency(Currency.USD, currency, 1.0);
            System.out.printf("%s%s %s = %s %s%s%n",
                    ConsoleColors.CYAN,
                    Currency.USD.getSymbol() + " 1.00",
                    Currency.USD.name(),
                    currency.getSymbol() + " " + decimalFormat.format(rate),
                    currency.name(),
                    ConsoleColors.RESET);
        }
    }

    private void showCurrencyInfo() {
        clearScreen();
        System.out.println(ConsoleColors.PURPLE_BOLD +
                "📖 INFORMAÇÕES SOBRE MOEDAS" + ConsoleColors.RESET);
        System.out.println("═".repeat(50));

        System.out.println(ConsoleColors.YELLOW_BOLD +
                "Moedas latino-americanas disponíveis:" + ConsoleColors.RESET);
        System.out.println();

        String[] descriptions = {
                "Argentina - Peso Argentino",
                "Bolívia - Boliviano",
                "Brasil - Real Brasileiro",
                "Chile - Peso Chileno",
                "Colômbia - Peso Colombiano",
                "Estados Unidos - Dólar Americano"
        };

        int i = 0;
        for (Currency currency : Currency.values()) {
            System.out.printf("%s%-4s %s %-3s %s - %s%s%n",
                    ConsoleColors.CYAN_BOLD,
                    currency.name(),
                    ConsoleColors.GREEN_BOLD,
                    currency.getSymbol(),
                    ConsoleColors.WHITE,
                    descriptions[i++],
                    ConsoleColors.RESET);
        }
    }

    private void showAbout() {
        clearScreen();
        System.out.println(ConsoleColors.CYAN_BOLD +
                "ℹ️  SOBRE O SISTEMA" + ConsoleColors.RESET);
        System.out.println("═".repeat(60));

        System.out.println(ConsoleColors.WHITE_BOLD + "Sistema de Conversão de Moedas" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW + "Versão: 1.0.0" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW + "Desenvolvido em: Java 21" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW + "Framework: Spring Boot" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW + "Programa: Oracle Next Education (ONE)" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW + "Autor: Navelogic" + ConsoleColors.RESET);
        System.out.println();

        System.out.println(ConsoleColors.GREEN + "Características:" + ConsoleColors.RESET);
        System.out.println("• Interface colorida no terminal");
        System.out.println("• Conversão entre 6 moedas latino-americanas");
        System.out.println("• Integração com ExchangeRate-API");
        System.out.println("• Sistema de cache para melhor performance");
        System.out.println("• Taxas de backup em caso de falha na API");
        System.out.println("• Formatação de números localizada");
        System.out.println("• Interface intuitiva e amigável");
        System.out.println();

        System.out.println(ConsoleColors.YELLOW + "Nota:" + ConsoleColors.RESET +
                " Para usar a API em produção, crie um arquivo '.env' na raiz do projeto");
        System.out.println("      e adicione sua chave de API no formato:");
        System.out.println(ConsoleColors.GREEN + "      API_KEY=suachaveaqui" + ConsoleColors.RESET);
        System.out.println();

        System.out.println(ConsoleColors.CYAN + "🔗 Me siga no TikTok: " +
                ConsoleColors.GREEN_BOLD + "https://www.tiktok.com/@navelogic" + ConsoleColors.RESET);
    }


    private void showGoodbye() {
        clearScreen();
        System.out.println(ConsoleColors.GREEN_BOLD +
                "╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                              ║");
        System.out.println("║                 👋 OBRIGADO POR USAR O SISTEMA! 👋          ║");
        System.out.println("║                                                              ║");
        System.out.println("║                    Até a próxima! 😊                        ║");
        System.out.println("║                                                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝" +
                ConsoleColors.RESET);
    }

    private void clearScreen() {
        System.out.print("\033[2J\033[H");
        System.out.flush();
    }
}