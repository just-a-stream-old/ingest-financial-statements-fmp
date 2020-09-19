package finance.modelling.data.ingestfinancialfundamentalsfmp.client.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FmpBalanceSheetsDTO {
    private String symbol;
    private List<FmpBalanceSheetDTO> balanceSheets;
}
