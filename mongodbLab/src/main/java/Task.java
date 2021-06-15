import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Data
public class Task {
    private String _id;
    private String name;
    private int timeTaken;
}
