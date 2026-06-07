package org.dev.cash_accounts_manager_backend.enums;

import lombok.Getter;

/**
 * Enum representing particular bank providing name and short description
 *
 * @author Fabian Frontczak
 */
public enum BankType {
    STAR_BANK("Star Bank", "The brightest star among banks. The best choice in the galaxy", 13),
    FUTURE_INSIGHT("Future Insight", "Choose the best plan for your future. Don't miss you chance!", 7),
    SAFE_TODAY("Safe Today", "Would you like to feel safe mentally and financially ? We could provice it together", 25),
    BRIGHT_TOMMORROW("Bright Tommorrow", "Make your day bright as much as possible. Don't wait any longer and trust us with yourself", 87),
    GBANK("GBANK", "G stands for global. We can help you wherever you are", 54),
    STRONG_TOGETHER_GROUP("Strong Together", "People stands strong together so come with us", 3),
    COMMON_SENSE_INVESTMENT("Common Sense Investment", "Make conscious decisions and invest your savings with us", 65);

    @Getter
    private String name;

    @Getter
    private String description;

    @Getter
    private int code;

    BankType(String name, String description, int code) {
        this.name = name;
        this.description = description;
        this.code = code;
    }
}
