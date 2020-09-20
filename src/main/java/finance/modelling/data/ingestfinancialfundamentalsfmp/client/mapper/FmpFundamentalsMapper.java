package finance.modelling.data.ingestfinancialfundamentalsfmp.client.mapper;


import finance.modelling.fmcommons.data.schema.fmp.dto.*;

import java.util.List;

public class FmpFundamentalsMapper {

    public static FmpIncomeStatementsDTO mapIncomeStatementDTOListToIncomeStatementsDTO(
            List<FmpIncomeStatementDTO> fmpIncomeStatementDTOList
    ) {
        return FmpIncomeStatementsDTO
                .builder()
                .symbol(fmpIncomeStatementDTOList.get(0).getSymbol())
                .incomeStatements(fmpIncomeStatementDTOList)
                .build();
    }

    public static FmpBalanceSheetsDTO mapBalanceSheetDTOListToBalanceSheetsDTO(
            List<FmpBalanceSheetDTO> fmpBalanceSheetDTOList
    ) {
        return FmpBalanceSheetsDTO
                .builder()
                .symbol(fmpBalanceSheetDTOList.get(0).getSymbol())
                .balanceSheets(fmpBalanceSheetDTOList)
                .build();
    }

    public static FmpCashFlowsDTO mapCashFlowDTOListToCashFlowsDTO(
            List<FmpCashFlowDTO> fmpCashFlowDTOList
    ) {
        return FmpCashFlowsDTO
                .builder()
                .symbol(fmpCashFlowDTOList.get(0).getSymbol())
                .cashFlows(fmpCashFlowDTOList)
                .build();
    }
}
