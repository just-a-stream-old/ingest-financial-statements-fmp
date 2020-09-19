package finance.modelling.data.ingestfinancialfundamentalsfmp.client.dto;

import lombok.Data;

@Data
public class FmpCashFlowDTO {
    private String date;
    private String symbol;
    private String fillingDate;
    private String acceptedDate;
    private String period;
    private Long netIncome;
    private Long depreciationAndAmortization;
    private Long deferredIncomeTax;
    private Long stockBasedCompensation;
    private Long changeInWorkingCapital;
    private Long accountsReceivables;
    private Long inventory;
    private Long accountsPayables;
    private Long otherWorkingCapital;
    private Long otherNonCashItems;
    private Long netCashProvidedByOperatingActivities;
    private Long investmentsInPropertyPlantAndEquipment;
    private Long acquisitionsNet;
    private Long purchasesOfInvestments;
    private Long salesMaturitiesOfInvestments;
    private Long otherInvestingActivites;
    private Long netCashUsedForInvestingActivites;
    private Long debtRepayment;
    private Double commonStockIssued;
    private Long commonStockRepurchased;
    private Long dividendsPaid;
    private Long otherFinancingActivites;
    private Long netCashUsedProvidedByFinancingActivities;
    private Double effectOfForexChangesOnCash;
    private Long netChangeInCash;
    private Long cashAtEndOfPeriod;
    private Long cashAtBeginningOfPeriod;
    private Long operatingCashFlow;
    private Long capitalExpenditure;
    private Long freeCashFlow;
    private String link;
    private String finalLink;
}
