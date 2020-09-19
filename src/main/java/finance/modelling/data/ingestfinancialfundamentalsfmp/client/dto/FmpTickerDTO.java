package finance.modelling.data.ingestfinancialfundamentalsfmp.client.dto;

import lombok.Data;

@Data
public class FmpTickerDTO {
    private String symbol;
    private String name;
    private String price;
    private String exchange;
}
