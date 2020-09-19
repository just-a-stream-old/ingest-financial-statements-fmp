package finance.modelling.data.ingestfinancialfundamentalsfmp.client.dto;

import lombok.Data;

@Data
public class FmpBalanceSheetDTO {
    private String date;
    private String symbol;
    private String fillingDate;
    private String acceptedDate;
    private String period;
    private Long cashAndCashEquivalents;
    private Long shortTermInvestments;
    private Long cashAndShortTermInvestments;
    private Long netReceivables;
    private Long inventory;
    private Long otherCurrentAssets;
    private Long totalCurrentAssets;
    private Long propertyPlantEquipmentNet;
    private Double goodwill;
    private Double intangibleAssets;
    private Double goodwillAndIntangibleAssets;
    private Long longTermInvestments;
    private Double taxAssets;
    private Long otherNonCurrentAssets;
    private Long totalNonCurrentAssets;
    private Long otherAssets;
    private Long totalAssets;
    private Long accountPayables;
    private Long shortTermDebt;
    private Double taxPayables;
    private Long deferredRevenue;
    private Long otherCurrentLiabilities;
    private Long totalCurrentLiabilities;
    private Long longTermDebt;
    private Double deferredRevenueNonCurrent;
    private Double deferredTaxLiabilitiesNonCurrent;
    private Long otherNonCurrentLiabilities;
    private Long totalNonCurrentLiabilities;
    private Long otherLiabilities;
    private Long totalLiabilities;
    private Long commonStock;
    private Long retainedEarnings;
    private Long accumulatedOtherComprehensiveIncomeLoss;
    private Long othertotalStockholdersEquity;
    private Long totalStockholdersEquity;
    private Long totalLiabilitiesAndStockholdersEquity;
    private Long totalInvestments;
    private Long totalDebt;
    private Long netDebt;
    private String link;
    private String finalLink;
}
