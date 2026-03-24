package by.kostya.skorik.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VkUser {
    private Long id;
    private String site;
    private String first_name;
    private String last_name;
    private Boolean can_access_closed;
    private Boolean is_closed;
}