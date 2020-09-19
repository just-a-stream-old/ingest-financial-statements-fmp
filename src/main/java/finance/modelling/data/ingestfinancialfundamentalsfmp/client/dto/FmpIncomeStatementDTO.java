package finance.modelling.data.ingestfinancialfundamentalsfmp.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FmpIncomeStatementDTO {
    private String date;
    private String symbol;
    private String fillingDate;
    private String acceptedDate;
    private String period;
    private Long revenue;
    private Long costOfRevenue;
    private Long grossProfit;
    private Double grossProfitRatio;
    private Long researchAndDevelopmentExpenses;
    private Long generalAndAdministrativeExpenses;
    private Double sellingAndMarketingExpenses;
    private Long otherExpenses;
    private Long operatingExpenses;
    private Long costAndExpenses;
    private Long interestExpense;
    private Long depreciationAndAmortization;
    @JsonProperty("ebitda") private Long earningsBeforeInterestTaxesDepreciationAmortisation;
    @JsonProperty("ebitdaratio") private Double ratioEarningsBeforeInterestTaxesDepreciationAmortisation;
    private Long operatingIncome;
    private Double operatingIncomeRatio;
    private Long totalOtherIncomeExpensesNet;
    private Long incomeBeforeTax;
    private Double incomeBeforeTaxRatio;
    private Long incomeTaxExpense;
    private Long netIncome;
    private Double netIncomeRatio;
    private Double eps;
    @JsonProperty("epsdiluted") private Double EpsDiluted;
    private Long weightedAverageShsOut;
    private Long weightedAverageShsOutDil;
    private String link;
    private String finalLink;
}
