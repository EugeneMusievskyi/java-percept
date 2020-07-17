package bsa.java.concurrency.fs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "server")
@Getter
@Setter
public class FileSystemConfigurationProperties {
    String port;
}
