import lombok.*;

import java.util.Map;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Data
public class FireFighter {
   private String _id;
   private String name;
   private String surname;
   private Map<String, Double> getTasks;
}
