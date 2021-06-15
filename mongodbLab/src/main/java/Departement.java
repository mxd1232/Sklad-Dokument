import lombok.*;

import java.util.ArrayList;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Data
public class Departement {

    private String _id;
    private String name;
    private String longName;
    private Double numberOfPeople;
    private ArrayList<String> fireFighters;

}
