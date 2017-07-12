package memcache.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class CacheRequest {

    @NotEmpty
    private String ipAddress;
}
