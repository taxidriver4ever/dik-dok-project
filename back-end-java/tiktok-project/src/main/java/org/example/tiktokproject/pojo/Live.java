package org.example.tiktokproject.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Live implements Serializable {
    private long id;
    private String author;
    private String url;
    private String title;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
